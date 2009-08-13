/*
 * Scantegrity - Successor to Punchscan, a High Integrity Voting System
 * Copyright (C) 2006  Richard Carback, David Chaum, Jeremy Clark, 
 * Aleks Essex, Stefan Popoveniuc, and Jeremy Robin
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package org.scantegrity.engine.ioExample;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import org.gwu.voting.standardFormat.GenerateDummyBallots;
import org.gwu.voting.standardFormat.Tally;
import org.gwu.voting.standardFormat.basic.Answer;
import org.gwu.voting.standardFormat.basic.Question;
import org.gwu.voting.standardFormat.basic.Section;
import org.gwu.voting.standardFormat.electionSpecification.ElectionSpecification;
import org.gwu.voting.standardFormat.filledInBallot.FilledInBallot;
import org.gwu.voting.standardFormat.results.Results;
import org.scantegrity.common.InputConstants;
import org.scantegrity.common.Meeting;
import org.scantegrity.common.Prow;
import org.scantegrity.common.Util;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class MeetingThreeIn extends Meeting { 
	
	public MeetingThreeIn() {
		super();
	}
	
	public MeetingThreeIn(String confFile) throws Exception {
		super(confFile);
	}

	public MeetingThreeIn(Document doc) throws Exception {
		super(doc);
	}
	
	public Hashtable<String,String> serialToPid=null; 
	
	public void ScannerOutputToMeetingThreeIn(String folderWithBallots,String m3in,String serialMap) throws Exception {
		setSerialMap(serialMap);
				
		OutputStream fos = new BufferedOutputStream(new FileOutputStream(m3in));
		fos.write("<xml>\n".getBytes());
		fos.write("\t<print>\n".getBytes());	
		
		ScannerOutputToMeetingThreeIn(folderWithBallots,fos);
		
		fos.write("\t</print>\n".getBytes());
		fos.write("</xml>".getBytes());
		fos.close();				
	}
	
	protected void setSerialMap(String serialMap) throws SAXException, IOException {
		serialToPid=Util.SerialToPid(serialMap);		
	}
	
	public void ScannerOutputToMeetingThreeIn(String folderWithBallots,OutputStream fos) throws Exception {
		File f=new File(folderWithBallots);
		if (f.isFile() && f.getName().endsWith(".enc")) {
			Document doc=Util.DomParse(f.getAbsolutePath());
			NodeList ballotNodes=doc.getElementsByTagName("row");
			
			for (int i=0;i<ballotNodes.getLength();i++) {
				Node ballotNode=ballotNodes.item(i);
				Prow prow=new Prow(ballotNode);			
				prow.setId(Integer.parseInt(serialToPid.get(prow.getId()+"")));			
				fos.write(prow.toString().getBytes());
			}			
		} else {
			if (f.isDirectory()) {
				File[] allFiles=f.listFiles();
				for (int i=0;i<allFiles.length;i++) {
					ScannerOutputToMeetingThreeIn(allFiles[i].getAbsolutePath(), fos);
				}
				return;
			}
		}

	}
	public Results write(ElectionSpecification es,String m3in,String m2Prints,String serialMap) throws Exception {
		
		//read the pid to serial mapping
		setSerialMap(serialMap);
		
		OutputStream fos = new BufferedOutputStream(new FileOutputStream(m3in));
		fos.write("<xml>\n".getBytes());
		fos.write("\t<print>\n".getBytes());	
		
		//transform from standard ballots to inputs to meeting three
		//I need the file with the prints

        Prow.setOperation(Prow.Operation.NONE);
		
		GenerateDummyBallots gdb = new GenerateDummyBallots(es);

		//I need a hashtable that maps section Ids to ordered questions
		Hashtable<String, Question[]> sidToQs = new Hashtable<String, Question[]>();
		
		Tally tally = new Tally(es);
		Section[] ss = es.getOrderedSections();
		for (int i=0;i<ss.length;i++) {
			sidToQs.put(ss[i].getId(),ss[i].getOrderedQuestions());
		}

		Question[] qs = es.getOrderedQuestions();
		int maxNoAnswers = 0;
		for (int qno = 0; qno < qs.length; qno++) {			
			maxNoAnswers+=qs[qno].getMax();
		}
		
		Vector<Integer> printedBallots=new Vector<Integer>();
		if (serialToPid!=null)
			for (String serial:serialToPid.keySet()) {
				printedBallots.add(Integer.parseInt(serial));
			}
		else {
			for (int i=0;i<InputConstants.NoBallots*InputConstants.PercentVoted-1;i++) {
				printedBallots.add(i);
			}
		}
		Collections.shuffle(printedBallots);
		
		byte[] vote=new byte[maxNoAnswers];		
		for (int i=0;i<InputConstants.NoBallots*InputConstants.PercentVoted-1;i++) {
			
			int serial=printedBallots.get(i);
			Prow prow=new Prow();
			prow.setId(printedBallots.get(i));
			prow.setChosenPage(Prow.ChosenPage.BOTH);
			computeP(prow);			
			
			setPage(prow);
						
			//read a filledInBallot and go through the questions
			FilledInBallot fib = gdb.generateOneBallot();
			
			/*
            FileOutputStream fosfib = new FileOutputStream("temp/before/"+i+".xml");
			fosfib.write(fib.toString().getBytes());
			fosfib.close();			
			*/
			
			tally.tally(fib);
			//go through each question
			int topPos = 0;
			int votePos=0;
			for (int s=0;s<ss.length;s++) {
				Question[] qsSpec = sidToQs.get(ss[s].getId());
				Section fibSection = (Section)fib.getSections().get(ss[s].getId());				
				for (int qpos = 0;qpos < qsSpec.length;qpos++) {
					Question fibQuestion = (Question)fibSection.getQuestions().get(qsSpec[qpos].getId());
					
					byte[] as = getAnswerPositions(fibQuestion.getAnswers());

					byte[] topInv = Util.getInverse(prow.getPage1(),topPos,qsSpec[qpos].getAnswers().size());
					byte[] bottomInv = Util.getInverse(prow.getPage2(),topPos,qsSpec[qpos].getAnswers().size());

					for (int apos = 0;apos<qsSpec[qpos].getMax();apos++) {
						try {
							vote[votePos++]=ClearVoteToPunchScanVote(as[apos], topInv, bottomInv);
						} catch (ArrayIndexOutOfBoundsException e) {
							vote[--votePos]=-1;
						}
					}
					
					topPos+=qsSpec[qpos].getAnswers().size();
				}
			}
			prow.setVote(vote);
			prow.setPage1(null);
			prow.setPage2(null);
			fos.write(("\t\t"+prow.toString()).getBytes());			
		}
		fos.write("\t</print>\n".getBytes());
		fos.write("</xml>".getBytes());
		fos.close();
		return tally.getResults();
	}

/*	
	public Results write(ElectionSpecification es,String m3in,String m2Prints,String serialMap) throws Exception {
		
		//read the pid to serial mapping
		setSerialMap(serialMap);
		
		OutputStream fos = new BufferedOutputStream(new FileOutputStream(m3in));
		fos.write("<xml>\n".getBytes());
		fos.write("\t<print>\n".getBytes());	
		
		//transform from standard ballots to inputs to meeting three
		//I need the file with the prints

		MeetingReaderSax mr = new MeetingReaderSax();
        try {
            SAXParser saxParser = Util.newSAXParser();
            saxParser.parse( new File(m2Prints), mr);
        } catch (Throwable t) {
            t.printStackTrace();
        }
        
        while (!mr.isDoneParsing()) {
        	Thread.sleep(100);
        }        

        Prow.setOperation(Prow.Operation.NONE);
		
		GenerateDummyBallots gdb = new GenerateDummyBallots(es);

		//I need a hashtable that maps section Ids to ordered questions
		Hashtable<String, Question[]> sidToQs = new Hashtable<String, Question[]>();
		
		Tally tally = new Tally(es);
		Section[] ss = es.getOrderedSections();
		for (int i=0;i<ss.length;i++) {
			sidToQs.put(ss[i].getId(),ss[i].getOrderedQuestions());
		}

		Question[] qs = es.getOrderedQuestions();
		int maxNoAnswers = 0;
		for (int qno = 0; qno < qs.length; qno++) {			
			maxNoAnswers+=qs[qno].getMax();
		}

		byte[] vote=new byte[maxNoAnswers];
		
		TreeMap<Integer, Prow> prows=mr.getProws();
		Vector<Integer> printedBallots=new Vector<Integer>();
		for (int serial:prows.keySet()) {
			printedBallots.add(serial);
		}
		Collections.shuffle(printedBallots);
		
		for (int i=0;i<InputConstants.NoBallots*InputConstants.PercentVoted-1;i++) {
			
			int serial=printedBallots.get(i);
			Prow prow=prows.get(serial);
			if (serialToPid!=null)
				prow.setId(Integer.parseInt(serialToPid.get(serial+"")));
			
			setPage(prow);
						
			//read a filledInBallot and go through the questions
			FilledInBallot fib = gdb.generateOneBallot();
			
			
            //FileOutputStream fosfib = new FileOutputStream("temp/before/"+i+".xml");
			//fosfib.write(fib.toString().getBytes());
			//fosfib.close();			
			
			
			tally.tally(fib);
			//go through each question
			int topPos = 0;
			int votePos=0;
			for (int s=0;s<ss.length;s++) {
				Question[] qsSpec = sidToQs.get(ss[s].getId());
				Section fibSection = (Section)fib.getSections().get(ss[s].getId());				
				for (int qpos = 0;qpos < qsSpec.length;qpos++) {
					Question fibQuestion = (Question)fibSection.getQuestions().get(qsSpec[qpos].getId());
					
					byte[] as = getAnswerPositions(fibQuestion.getAnswers());

					byte[] topInv = Util.getInverse(prow.getPage1(),topPos,qsSpec[qpos].getAnswers().size());
					byte[] bottomInv = Util.getInverse(prow.getPage2(),topPos,qsSpec[qpos].getAnswers().size());

					for (int apos = 0;apos<qsSpec[qpos].getMax();apos++) {
						try {
							vote[votePos++]=ClearVoteToPunchScanVote(as[apos], topInv, bottomInv);
						} catch (ArrayIndexOutOfBoundsException e) {
							vote[--votePos]=-1;
						}
					}
					
					topPos+=qsSpec[qpos].getAnswers().size();
				}
			}
			prow.setVote(vote);
			prow.setPage1(null);
			prow.setPage2(null);
			fos.write(("\t\t"+prow.toString()).getBytes());			
		}
		fos.write("\t</print>\n".getBytes());
		fos.write("</xml>".getBytes());
		fos.close();
		return tally.getResults();
	}
*/	
	private byte[] getAnswerPositions(Hashtable fibAnswers) {
		byte[] ret = new byte[fibAnswers.size()];
		int pos = 0;
		for (Iterator i = fibAnswers.values().iterator();i.hasNext();) {
			Answer a = (Answer)i.next();
			ret[pos]=(byte)(Byte.parseByte(a.getId()));//(byte)(Byte.parseByte(((Answer)esAnswers.get(a.getId())).getId())-1);
			pos++;
		}
		return ret;
	}

	public byte ClearVoteToPunchScanVote(byte vote, byte[] topInv, byte[] bottomInv) {
		return bottomInv[topInv[vote]];		
	}

	public void setPage(Prow prow) {
		if (Math.random()<0.5)
			prow.setChosenPage(Prow.ChosenPage.TOP);
		else
			prow.setChosenPage(Prow.ChosenPage.BOTTOM);		
	}
	
	
	public static void main(String args[]) throws Exception {
		String dir="Elections/VoComp/";
		MeetingThreeIn m3in=new MeetingThreeIn();
		m3in.ScannerOutputToMeetingThreeIn(dir+"PunchScan/ballots/", dir+"MeetingThreeIn.xml",dir+"SerialMap.xml");
	}
}
