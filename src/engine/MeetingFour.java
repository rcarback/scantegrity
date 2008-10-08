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

package software.engine;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.TreeMap;

import javax.xml.parsers.SAXParser;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;


import org.w3c.dom.Document;

import software.common.Drow;
import software.common.InputConstants;
import software.common.Meeting;
import software.common.MeetingReaderSax;
import software.common.Prow;
import software.common.Util;

/**
 * For each row in the D table that has been filed in with a partial
 * decrypted vote in Meeting three, the auditor chose left or right to open.
 * Meeting four replies to the auditor by opening the frequired rows in the D tables.
 * @author stefan
 *
 */
public class MeetingFour extends Meeting {	
	
	public static String MeetingFourInSchema="MeetingFourIn.xsd";
	public static String MeetingFourOutSchema="MeetingFourOut.xsd";
	
	
	TreeMap<Byte, TreeMap<Byte, TreeMap<Integer, Drow>>> allDrows=null; 
	
	public MeetingFour() throws Exception {
		super();
	}
	
	public MeetingFour(String confFile) throws Exception {
		super(confFile);
	}

	public MeetingFour(Document doc) throws Exception {
		super(doc);
	}
		
	/** 
	 * @param inFile - the challenges from the auditor. 
	 * it does not check that the challenges are 
	 * consistent with meeting three out
	 * @param outputFile - where the opened commitments are written
	 * @throws Exception - the the meeting has not been initialized with the two master keys
	 */
	public void go(String inFile,String outputFile) throws Exception {
		if (mk1==null || mk2==null)
			throw new Exception("Meeting was not initialized with master keys.");

		if (Meeting.CheckagainsSchema) {
			Document doc = Util.DomParse(inFile);			
		    Source schemaSource = new StreamSource(getClass().getResourceAsStream(MeetingFour.MeetingFourInSchema));
		    Schema schema = Meeting.schemaFactory.newSchema(schemaSource);
		    schema.newValidator().validate(new DOMSource(doc));         						
		}	    
		
		
		MeetingReaderSax mr = new MeetingReaderSax();
        try {
            SAXParser saxParser = Util.newSAXParser();
            saxParser.parse( new File(inFile), mr);
        } catch (Throwable t) {
            t.printStackTrace();
        }
        
        while (!mr.isDoneParsing()) {
        	Thread.sleep(100);
        }        
        
        allDrows=mr.getDrows();
        
        fos = new BufferedOutputStream(new FileOutputStream(outputFile));
        fos.write("<xml>\n".getBytes());
        fos.write("\t<database>\n".getBytes());		
        
        Drow.setOperation(Drow.Operation.OPEN_COMMITMENTS);
        computeD();
        
	    fos.write("\t</database>\n".getBytes());
	    fos.write("</xml>".getBytes());
	    fos.close();
	    
		if (Meeting.CheckagainsSchema) {
			Document doc = Util.DomParse(outputFile);			
		    Source schemaSource = new StreamSource(getClass().getResourceAsStream(MeetingFour.MeetingFourOutSchema));
		    Schema schema = Meeting.schemaFactory.newSchema(schemaSource);
		    schema.newValidator().validate(new DOMSource(doc));         						
		}
		
		if (withSignatures)
			sign(outputFile);
	}
	
	protected void computeD() throws Exception {
		int[] masterPerm;
		//for (byte partitionId=0;partitionId<partitions.size();partitionId++) 
		for (byte partitionId:allDrows.keySet())
		{
			fos.write(("\t\t<partition id=\""+partitionId+"\">\n").getBytes());	
			fos.write("\t\t\t<decrypt>\n".getBytes());
			//compute the D1 permutation
			//this is the permutation that maps D1 to D5
			masterPerm = RowPermutation.permuteD1D5(mk1,mk2,c,0,numberOfBallots-1,partitionId);
			//for (byte dNo=0;dNo<numberOfDs;dNo++)
			for (byte dNo:allDrows.get(partitionId).keySet())
			{
				//for one instance of the D table, compute the flips and the commitments.
				fos.write(("\t\t\t\t<instance id=\""+dNo+"\">\n").getBytes());
				drows=allDrows.get(partitionId).get(dNo);

				computeD(masterPerm,dNo,partitionId);
				fos.write("\t\t\t\t</instance>\n".getBytes());			
			}
			fos.write("\t\t\t</decrypt>\n".getBytes());
			
			if (Drow.getOperation().equals(Drow.Operation.PUBLISH_RESULTS))
				computeR();
			fos.write(("\t\t</partition>\n").getBytes());
		}
	}
	
	/**
	 * Openes all the ballots that were not used during the elections and were not audit in the prelection audit
	 * @param allM3InsAndM2In - an array of paths two files pointing to m3in and m2in
	 * @param out - where the opened commitments are written
	 * @throws Exception - the the meeting has not been initialized with the two master keys
	 */
	public void revealUnvoted(String[] allM3InsAndM2In,String meetingOneIn, String out) throws Exception {		
		if (mk1==null || mk2==null)
			throw new Exception("Meeting was not initialized with master keys.");

		//eliminate the ones in allM3InsAndM2In
		
		MeetingReaderSax mr = new MeetingReaderSax();
        try {
            SAXParser saxParser = Util.newSAXParser();
            for (int i=0;i<allM3InsAndM2In.length;i++)
            	saxParser.parse( new File(allM3InsAndM2In[i]), mr);
        } catch (Throwable t) {
            t.printStackTrace();
        }
        
        while (!mr.isDoneParsing()) {
        	Thread.sleep(100);
        }
        TreeMap<Integer, Prow> eliminated=mr.getProws();
        
		prows=new TreeMap<Integer, Prow>();
		for (int i=0;i<numberOfBallots;i++) {
			if (eliminated.get(i)==null) {
				Prow prow=new Prow();
				prow.setId(i);
				prows.put(i, prow);
			}
		}
		Prow.setOperation(Prow.Operation.OPEN_COMMITMENTS);
		
		MeetingTwo m2=new MeetingTwo(meetingOneIn);
		m2.init(mk1, mk2);
		m2.setProws(prows);		
		m2.go(out);
	}
	
	public static void main(String[] args) throws Exception {
		if (args.length < 2) {
			System.out.println("MeetingOne MeetingOneIn.xml MeetingFourIn.xml");
			return;
		}
		MeetingFour m4 = new MeetingFour(args[0]);
		m4.init(InputConstants.MK1,InputConstants.MK2);		
		m4.go(args[1],args[2]);
	}
}
