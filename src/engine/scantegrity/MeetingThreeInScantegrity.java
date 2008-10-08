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

package software.engine.scantegrity;

import org.w3c.dom.Document;

import software.common.Prow;

public class MeetingThreeInScantegrity extends software.engine.ioExample.MeetingThreeIn {
	
	public MeetingThreeInScantegrity() {
		super();
	}
	
	public MeetingThreeInScantegrity(String confFile) throws Exception {
		super(confFile);
	}

	public MeetingThreeInScantegrity(Document doc) throws Exception {
		super(doc);
	}

	
	public byte ClearVoteToPunchScanVote(byte vote, byte[] topInv, byte[] bottomInv) {
		return vote;
	}
	
	public void setPage(Prow prow) {
		prow.setChosenPage(Prow.ChosenPage.NONE);
	}
	
	protected void setSerialMap(String serialMap) {
		serialToPid=null;		
	}
	
/*	
	
	
	public static Results write(String esPath) throws Exception {
				
		//transform from standard ballots to inputs to meeting three
		//I need the file with the prints

		Document doc = Util.DomParse(InputConstants.MeetingTwoPrints);
		NodeList printsNodes = doc.getElementsByTagName("row");

		ElectionSpecification es = new ElectionSpecification(esPath);
		GenerateDummyBallots gdb = new GenerateDummyBallots(esPath);

		//I need a hashtable that maps section Ids to ordered questions
		Hashtable<String, Question[]> sidToQs = new Hashtable<String, Question[]>();
		
		Tally tally = new Tally(esPath);
		Section[] ss = es.getOrderedSections();
		for (int i=0;i<ss.length;i++) {
			sidToQs.put(ss[i].getId(),ss[i].getOrderedQuestions());
		}

		OutputStream fos = new BufferedOutputStream(new FileOutputStream(clearTextBallots));
		fos.write("<xml>\n".getBytes());
		fos.write("\t<clearTextBallots>\n".getBytes());			
		
		for (int i=0;i<printsNodes.getLength();i++) {
			Node row = printsNodes.item(i);
			int serial = Integer.parseInt(row.getAttributes().getNamedItem("id").getNodeValue());
			
			fos.write((serial+" ").getBytes());
			fos.write(" ".getBytes());
						
			//read a filledInBallot and go through the questions
			FilledInBallot fib = gdb.generateOneBallot();
			
			tally.tally(fib);
			//go through each question
			for (int s=0;s<ss.length;s++) {
				Question[] qsSpec = sidToQs.get(ss[s].getId());
				Section fibSection = (Section)fib.getSections().get(ss[s].getId());				
				for (int qpos = 0;qpos < qsSpec.length;qpos++) {
					Question fibQuestion = (Question)fibSection.getQuestions().get(qsSpec[qpos].getId());
					
					byte[] as = getAnswerPositions(fibQuestion.getAnswers());

					for (int apos = 0;apos<qsSpec[qpos].getMax();apos++) {
						try {
							fos.write((as[apos]+" ").getBytes());
						} catch (ArrayIndexOutOfBoundsException e) {
							fos.write("-1 ".getBytes());
						}
					}
				}
			}
			fos.write("\n".getBytes());			
		}
		fos.write("\t</clearTextBallots>\n".getBytes());
		fos.write("</xml>".getBytes());
		fos.close();		
		return tally.getResults();
	}
	
	private static byte[] getAnswerPositions(Hashtable fibAnswers) {
		byte[] ret = new byte[fibAnswers.size()];
		int pos = 0;
		for (Iterator i = fibAnswers.values().iterator();i.hasNext();) {
			Answer a = (Answer)i.next();
			ret[pos]=(byte)(Byte.parseByte(a.getId()));//(byte)(Byte.parseByte(((Answer)esAnswers.get(a.getId())).getId())-1);
			pos++;
		}
		return ret;
	}
*/
}
