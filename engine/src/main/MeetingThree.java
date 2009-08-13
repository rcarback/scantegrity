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

package org.scantegrity.engine;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import javax.xml.parsers.SAXParser;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;

import org.scantegrity.common.Drow;
import org.scantegrity.common.InputConstants;
import org.scantegrity.common.Meeting;
import org.scantegrity.common.MeetingReaderSax;
import org.scantegrity.common.Prow;
import org.scantegrity.common.Util;
import org.w3c.dom.Document;

/**
 * Reveal the page chosen by the voters. 
 * Populate the intermediary colum in the D tables (D3) and
 * compute results
 * 
 * @author Stefan
 *
 */
public class MeetingThree extends Meeting{
	
	public static String MeetingThreeInSchema="MeetingThreeIn.xsd";
	public static String MeetingThreeOutSchema="MeetingThreeOut.xsd";
	
	public MeetingThree() throws Exception {
		super();
	}
	
	public MeetingThree(Document doc) throws Exception {
		super(doc);
	}
	
	
	public MeetingThree(String confFile) throws Exception {
		super(confFile);
	}
	
	/**
	 * Reveal the printing on all the receipts, compute results and
	 * populate with partial decrypted votes the D tables
	 * 
	 * @param inFilePath - file with all the reciepts, as downloaded from the web server
	 * @param outputFile - populated P D and R tables with receipts (P), results(R) and partially decrypted votes (D) 
	 * @throws Exception - if the meeting wasn't initialized with the two master keys
	 */	
	public void go(String inFilePath,String outputFile) throws Exception {
		if (Meeting.CheckagainsSchema) {
			Document doc = Util.DomParse(inFilePath);			
		    Source schemaSource = new StreamSource(getClass().getResourceAsStream(MeetingThree.MeetingThreeInSchema));
		    Schema schema = Meeting.schemaFactory.newSchema(schemaSource);
		    schema.newValidator().validate(new DOMSource(doc));         						
		}		
		
		if (prows==null) {
			readPRows(inFilePath);
		}
		go(outputFile);
	}

	protected void readPRows(String inFilePath) {
		MeetingReaderSax mr = new MeetingReaderSax();
        try {
            SAXParser saxParser = Util.newSAXParser();
            saxParser.parse( new File(inFilePath), mr);
        } catch (Throwable t) {
            t.printStackTrace();
        }        
        while (!mr.isDoneParsing()) {
        	try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
        }        
		prows=mr.getProws();		
	}
	
	private void go(String outputFile) throws Exception {
		if (mk1==null || mk2==null)
			throw new Exception("Meeting was not initialized with master keys.");
		
fos = new BufferedOutputStream(new FileOutputStream(outputFile));
fos.write("<xml>\n".getBytes());
fos.write("\t<database>\n".getBytes());						
		
		Prow.setOperation(Prow.Operation.OPEN_COMMITMENTS);
		computeP();

		Drow.setOperation(Drow.Operation.PUBLISH_RESULTS);
		computeD();
		
fos.write("\t</database>\n".getBytes());
fos.write("</xml>".getBytes());
fos.close();	

		if (Meeting.CheckagainsSchema) {
			Document doc = Util.DomParse(outputFile);			
		    Source schemaSource = new StreamSource(getClass().getResourceAsStream(MeetingThree.MeetingThreeOutSchema));
		    Schema schema = Meeting.schemaFactory.newSchema(schemaSource);
		    schema.newValidator().validate(new DOMSource(doc));         						
		}
		if (withSignatures)
			sign(outputFile);
	}			

	/**
	 * debug method
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		if (args.length < 2) {
			System.out.println("MeetingThree MeetingOneIn.xml MeetingThreeIn.xml");
			return;
		}
		MeetingThree m3 = new MeetingThree(args[0]);
		m3.init(InputConstants.MK1,InputConstants.MK2);		
		m3.go(args[1],args[2]);
	}
	
}
