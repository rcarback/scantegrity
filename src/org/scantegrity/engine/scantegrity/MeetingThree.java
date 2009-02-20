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

package org.scantegrity.engine.scantegrity;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Hashtable;

import org.gwu.voting.standardFormat.basic.Question;
import org.scantegrity.common.Prow;
import org.scantegrity.common.Util;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class MeetingThree extends org.scantegrity.engine.MeetingThree {

	protected Hashtable<String,String> serialToPid=null;
	
	static {
		org.scantegrity.common.Meeting.MeetingOneInSchema="../"+org.scantegrity.common.Meeting.MeetingOneInSchema;
	}
	
	public MeetingThree(String confFile) throws Exception {
		super(confFile);
	}

	public MeetingThree(Document doc) throws Exception {
		super(doc);		
	}
	
	public MeetingThree() throws Exception {
		super();		
	}
	
	/**
	 * This method used the two master keys, therefore has to be in this class
	 * 
	 * @param folderWithClearMarks
	 * @param outFile
	 * @param serialMap
	 * @throws Exception
	 */
	public void clearVotesToCodedVotes(String folderWithClearMarks,String outFile, String serialMap) throws Exception {
		//read the pid to serial mapping
		setSerialMap(serialMap);		
		
		OutputStream out= new BufferedOutputStream(new FileOutputStream(outFile));
		out.write("<xml>\n".getBytes());
		out.write("\t<print>\n".getBytes());

		clearVotesToCodedVotes(folderWithClearMarks, out);
		
		out.write("\t</print>\n".getBytes());
		out.write("</xml>".getBytes());		
		out.close();		
	}
	
	private void setSerialMap(String serialMap) throws SAXException, IOException {
		serialToPid=Util.SerialToPid(serialMap);		
	}
	
	private void clearVotesToCodedVotes(String folderWithClearMarks,OutputStream out) throws Exception {
		//read one file at a time from the source folder
		File f=new File(folderWithClearMarks);
		if (f.isFile()) {
		} else {
			if (f.isDirectory()) {
				File[] allFiles=f.listFiles();
				for (int i=0;i<allFiles.length;i++) {
					clearVotesToCodedVotes(allFiles[i].getAbsolutePath(), out);
				}
				return;
			}
		}
		
		//read the first line from each file and treat it like a condensed file
		if (f.getAbsolutePath().endsWith(".cer"))
			return;
		Document doc=Util.DomParse(f.getAbsolutePath());
		NodeList ballotNodes=doc.getElementsByTagName("row");		
		for (int i=0;i<ballotNodes.getLength();i++) {
			Node ballotNode=ballotNodes.item(i);
			Prow prow=new Prow(ballotNode);
			prow.setId(Integer.parseInt(serialToPid.get(prow.getId()+"")));			
			prow.setVote(possitionsToLetters(prow.getId(), prow.getVote()));
			out.write(prow.toString().getBytes());
		}
	}
	
	private byte[] possitionsToLetters(int serial,byte[] allPossitions) throws Exception {
//		int serial=Integer.parseInt(possitions.substring(0,possitions.indexOf(" ")));
		
		Prow prow=new Prow();
		prow.setId(serial);
		prow.setChosenPage(Prow.ChosenPage.BOTH);
		computeP(prow);
		
		return positionsToSymbols(prow.getPage1(),prow.getPage2(), allPossitions);
	}

	private byte[] positionsToSymbols(byte[] p1, byte[] p2, byte[] allPossitions) {
		byte[] ret=new byte[allPossitions.length];
		
		int numberOfAnswers = 0;	
		int maxNumberOfAnswersUntillCurrentQuestion = 0;
		int ppos = 0;
		Question[] qs = es.getOrderedQuestions();
		for (int i=0;i<qs.length;i++) {
			numberOfAnswers = qs[i].getAnswers().size();
			byte[] permTop = new byte[numberOfAnswers];
			System.arraycopy(p1,ppos,permTop,0,numberOfAnswers);
			byte[] permBottom = new byte[numberOfAnswers];
			System.arraycopy(p2,ppos,permBottom,0,numberOfAnswers);
			byte[] permBottomInverse=Util.getInverse(permBottom);
			for (int j=0;j<qs[i].getMax();j++) {
				if (allPossitions[maxNumberOfAnswersUntillCurrentQuestion+j]==-1)
					ret[maxNumberOfAnswersUntillCurrentQuestion+j]=-1;
				else
					ret[maxNumberOfAnswersUntillCurrentQuestion+j]=permBottomInverse[permTop[allPossitions[maxNumberOfAnswersUntillCurrentQuestion+j]]];
			}
			maxNumberOfAnswersUntillCurrentQuestion+=qs[i].getMax();
			ppos+=numberOfAnswers;
		}
		return ret;		
	}	
	
	//This method can be eliminated if one page is opened
	protected void computeP() throws Exception {
		//go through the prows in order				
		for (int i:prows.keySet()) {
			Prow prow=prows.get(i);
			computeP(prow);
			
			//free some memory
			prow.setC1(null);
			prow.setC2(null);
			prow.setS1(null);
			prow.setS2(null);			
		}
	}
}
