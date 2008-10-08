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

package software.auditor;

import java.io.File;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;

import javax.xml.parsers.SAXParser;

import org.xml.sax.helpers.DefaultHandler;

import software.common.Drow;
import software.common.MarkPermutations;
import software.common.Meeting;
import software.common.MeetingReaderSax;
import software.common.Prow;
import software.common.Util;

/**
 * Checks:
 * - the reply contains what was asked for
 * - commitments to p1 and p2 is valid
 * - p1+p2=d2+d4
 * - commitment to d2 and d4 is valid
 * - check if all the pid in the D tables are among the requested ones
 * - check that all the rids in one partition are the same (no matter how many D instances)
 * 
 * Algorithm:
 * read in memory MeetingOneIn.xml
 * read in memory MeetingTwoIn.xml
 * read in memory MeetingTwoOut.xml
 *  - check that all the requested ballots have been opened (while reading prints)
 *  - TODO for each partition, check that the pid SET is the one that was requested
 *  - TODO for each partition, check that the rid SET is the same for all partitions
 *  - TODO check the files agains an xml schema
 *  - check that p1+p2=d2+d4 for all partitions, all instances  
 * go through each line of meeting one out (use sax)
 *  - if a row in P is found, check if the ballot was requested for audit
 *  	- check the commitments for p1 and p2
 *  - if a row in D is found
 *  	- check the commitment for d2 and d4
 */


public class CheckTableCorrectness {

	//the election constant
	byte[] c=null;
	MarkPermutations markPerm=null;
	
	TreeMap<Integer,Prow> prows;
	TreeMap<Byte,TreeMap<Byte,TreeMap<Integer,Drow>>> drows;
	
	/**
	 * parses the three parameters and puts them into memory.
	 * 
	 * They are smaller them m1out (m1out will not be kept in memory)
	 *  
	 * @param meetingOneIn
	 * @param meetingTwoIn
	 * @param meetingTwoOut
	 * @throws Exception
	 */
	public CheckTableCorrectness(String meetingOneIn,String meetingTwoIn, String meetingTwoOut) throws Exception {
		
		//read MeetingOneIn.xml
		Meeting m=new Meeting(meetingOneIn);
		c=m.getC();
		
		markPerm=new MarkPermutations(c,m.getEs(),m.getPartitions());

		//read MeetingTwoIn.xml
		MeetingReaderSax mr = new MeetingReaderSax();
        SAXParser saxParser = Util.newSAXParser();
        Set<Integer> pidsRequested=null;
        if (meetingTwoIn!=null && meetingTwoIn.length()!=0) {
	        saxParser.parse( new File(meetingTwoIn), mr);
	        while (!mr.isDoneParsing()) {
	        	Thread.sleep(100);
	        }
	        pidsRequested=mr.getProws().keySet();
        }		

		//read MeetingTwoOut.xml
		mr = new MeetingReaderSax();
        saxParser = Util.newSAXParser();
        
        saxParser.parse( new File(meetingTwoOut), mr);
        while (!mr.isDoneParsing()) {
        	Thread.sleep(100);
        }
		prows = mr.getProws();
		drows = mr.getDrows();
		
		//check if all the requested ballots have been opened
		if (pidsRequested!=null && !prows.keySet().containsAll(pidsRequested))
			throw new Exception("In P table: Not all requested ballots have been provided "+pidsRequested.removeAll(prows.keySet()));			
		
	}
	
	/**
	/**
	 * First checks the table correctness (p1+p2=d2+d4). Then
	 * it checks the commitments
	 * 
	 * All the challenges have to be checked. m1in is not read in memory
	 *  
	 * @param meetingOneout
	 * @throws Exception - if the auditor find an iregularity, or (null) 
	 * if one of the challenges is not opened
	 */
	public void check(String meetingOneout) throws Exception {
		checkP1PlusP2IsD2PlusD4();
		
		//check the commitments
        DefaultHandler handler = new ParseMeetingOneOutCTC(prows,drows,c);
        SAXParser saxParser = Util.newSAXParser();
        saxParser.parse( new File(meetingOneout), handler);	
	}
	
	private void checkP1PlusP2IsD2PlusD4() throws Exception {		
		for (byte partitionId:drows.keySet()) {
			TreeMap<Byte,TreeMap<Integer,Drow>> i=drows.get(partitionId);
			for (byte dNo:i.keySet()) {
				TreeMap<Integer,Drow> j=i.get(dNo);
				for (Iterator<Drow> k=j.values().iterator();k.hasNext();) {
					Drow drow=k.next();								
					byte[] d4 = markPerm.getD4(prows.get(drow.getPid()).getPage1(), prows.get(drow.getPid()).getPage2(), drow.getPage1(), partitionId);
					if (Util.compare(d4,drow.getPage2())!=0)
						throw new Exception("p1+p2!=d2+d4 for partition="+partitionId+" dInstance="+dNo+" did="+drow.getId());
				}
			}
		}
		
	}
	
	/**
	 * debug method
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		CheckTableCorrectness ctc=new CheckTableCorrectness(args[0],args[2],args[3]);
		ctc.check(args[1]);		
		
		/*
		String dir="Elections/VoComp/public/";
		CheckTableCorrectness ctc=new CheckTableCorrectness(dir+"MeetingOneIn.xml",dir+"MeetingTwoIn.xml",dir+"MeetingTwoOut.xml");
		ctc.check(dir+"MeetingOneOut.xml");
		*/
	}	
}
