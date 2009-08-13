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

package org.scantegrity.auditor.invisibleink;

import java.io.File;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.TreeMap;

import javax.xml.parsers.SAXParser;

import org.gwu.voting.standardFormat.basic.Question;
import org.gwu.voting.standardFormat.electionSpecification.ElectionSpecification;
import org.scantegrity.common.CodesReaderSax;
import org.scantegrity.common.Meeting;
import org.scantegrity.common.MeetingReaderSax;
import org.scantegrity.common.Prow;
import org.scantegrity.common.SymbolRow;
import org.scantegrity.common.Util;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class CheckCommitmentsToSymbols {

	//printedSerial, question, symbol
	TreeMap<Integer,TreeMap<Byte,TreeMap<Byte,SymbolRow>>> rows=new TreeMap<Integer, TreeMap<Byte,TreeMap<Byte,SymbolRow>>>();	
	byte[] c;
	ElectionSpecification es=null;
	
	//read in memory m3outcodes
	public CheckCommitmentsToSymbols(String meetingThreeOutCodes,String m1in) throws Exception {

		Meeting m=new Meeting(m1in);
		c=m.getC();
		es=m.getEs();
		
		//read MeetingThreeOutCodes.xml
		CodesReaderSax cr = new CodesReaderSax();
        SAXParser saxParser = Util.newSAXParser();
        if (meetingThreeOutCodes!=null && meetingThreeOutCodes.length()!=0) {
	        saxParser.parse( new File(meetingThreeOutCodes), cr);
	        while (!cr.isDoneParsing()) {
	        	Thread.sleep(100);
	        }
        }
        rows=cr.getRows();        
	}
	
	public void check(String m2outCodesComm,String m3in,String serialMap) throws SAXException, IOException, InterruptedException {
        checkCommitments(m2outCodesComm);
        
        checkM3CodesisM3In(m3in, serialMap);
	}

	public void checkCommitments(String m2outCodesComm) throws SAXException, IOException {
        DefaultHandler handler = new ParseM2CodesCheckCommitments(rows,c);
        SAXParser saxParser = Util.newSAXParser();
        saxParser.parse( new File(m2outCodesComm), handler);		
	}
	
	public void checkM3CodesisM3In(String m3in,String serialMap) throws SAXException, IOException, InterruptedException {
		
		//read m3in in memory
		MeetingReaderSax mr = new MeetingReaderSax();
        SAXParser saxParser = Util.newSAXParser();
        saxParser.parse( new File(m3in), mr);
        
        while (!mr.isDoneParsing()) {
        	Thread.sleep(100);
        }        
		TreeMap<Integer,Prow> m3inProws=mr.getProws();

		//read the serialmap in memory
		Hashtable<String,String> pidToSerial=Util.PidToSerial(serialMap);
		
		Question[] qs=es.getOrderedQuestions();
		
		//go through all the ballots in m3in
		for (Iterator<Prow> it=m3inProws.values().iterator();it.hasNext();) {
			Prow prow=it.next();
			TreeMap<Byte,TreeMap<Byte,SymbolRow>> ballot=rows.get(Integer.parseInt(pidToSerial.get(prow.getId()+"")));
			if (ballot==null)
				throw new SAXException("Ballot with pid="+prow.getId()+" (printedSerial="+pidToSerial.get(prow.getId()+"")+") does not have the codes revealed. ");
			byte[] vote=prow.getVote();
			int votepos=0;
			for (Question q:qs) {
				TreeMap<Byte,SymbolRow> qm3Codes=ballot.get(Byte.parseByte(q.getId()));
				if (qm3Codes==null)
					throw new SAXException("For ballot with pid="+prow.getId()+" (printedSerial="+pidToSerial.get(prow.getId()+"")+") question="+q.getId()+" the codes are not revealed.");
				for (int a=0;a<q.getMax();a++) {
					if (vote[votepos]!=-1) {
						if (qm3Codes.get(vote[votepos])==null) {
							throw new SAXException("For ballot with pid="+prow.getId()+" (printedSerial="+pidToSerial.get(prow.getId()+"")+") question="+q.getId()+" codedVote="+vote[votepos]+" the code is not revealed.");
						}
					}
					votepos++;
				}
			}
		}
	}
	
	/*
	 * 0 - m1in
	 * 1 - m2commit
	 * 2 - m3codes
	 * 3 - m3in
	 * 4 - serialmap
	 */
	public static void main(String[] args) throws Exception {
		CheckCommitmentsToSymbols ccts=new CheckCommitmentsToSymbols(args[2],args[0]);
		if (args.length>3)
			ccts.check(args[1],args[3],args[4]);
		else
			ccts.checkCommitments(args[1]);
	}
}
