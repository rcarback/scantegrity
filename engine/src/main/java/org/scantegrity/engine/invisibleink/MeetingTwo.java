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

package org.scantegrity.engine.invisibleink;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Set;

import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;

import org.bouncycastle.util.encoders.Base64;
import org.gwu.voting.standardFormat.basic.Question;
import org.scantegrity.authoring.ContestSymbols;
import org.scantegrity.common.InputConstants;
import org.scantegrity.common.InvisibleInkCodes;
import org.scantegrity.common.Meeting;
import org.scantegrity.common.Prow;
import org.scantegrity.common.Util;
import org.scantegrity.engine.Commitments;
import org.w3c.dom.Document;

public class MeetingTwo extends org.scantegrity.engine.MeetingTwo {

	public MeetingTwo() throws Exception {
		super();
	}
	
	public MeetingTwo(String confFile) throws Exception {
		super(confFile);
	}

	public MeetingTwo(Document doc) throws Exception {
		super(doc);
	}
	
	public static void writeCodesToFile(String outputFile) throws Exception {
		OutputStream pos = new BufferedOutputStream(new FileOutputStream(outputFile));
		pos.write("<xml>\n".getBytes());
		pos.write(("\t<codes numberOfLettersPerCode=\""+InvisibleInkCodes.NumberOfCharectersInACode+"\">\n").getBytes());
		
		for (int i=0;i<InvisibleInkCodes.CodesAlphabet.length;i++) {
			pos.write(("\t\t<code id=\""+i+"\" value=\"").getBytes());
			pos.write(InvisibleInkCodes.CodesAlphabet[i].getBytes());
			pos.write("\"/>\n".getBytes());
		}
		
		pos.write("\t</codes>\n".getBytes());			
		pos.write("</xml>".getBytes());
		pos.close();
	}

	public void makePrintsAndCommitmentsToCodes(String printOutputFile,String mapOutputFile,String codeCommitmentsOutputFile) throws Exception {
		//eliminate the spoiled ballots
		Set<Integer> challenges=prows.keySet();
		int[] prints=new int[numberOfBallots-challenges.size()];
		int printsPos=0;
		for (int i=0;i<numberOfBallots;i++) {
			if (!challenges.contains(i))
				prints[printsPos++]=i;
		}
		
		ContestSymbols cs=new ContestSymbols(null,es,ContestSymbols.alphabet,true);
		
		BufferedOutputStream pos = new BufferedOutputStream(new FileOutputStream(printOutputFile));
		pos.write("<xml>\n".getBytes());
		pos.write("\t<database>\n".getBytes());
		pos.write("\t\t<printCommitments>\n".getBytes());
	
		OutputStream mos = new BufferedOutputStream(new FileOutputStream(mapOutputFile));
		mos.write("<xml>\n".getBytes());
		mos.write("\t<print>\n".getBytes());
		
		BufferedOutputStream cos = new BufferedOutputStream(new FileOutputStream(codeCommitmentsOutputFile));
		cos.write("<xml>\n".getBytes());
		cos.write("\t<database>\n".getBytes());
		cos.write("\t\t<codes>\n".getBytes());

		InvisibleInkCodes invisibleInkCodes=new InvisibleInkCodes(es);
		invisibleInkCodes.init(mk1, mk2, c);
		Question[] qs=es.getOrderedQuestions();

		for (int printedSerialNumber=0;printedSerialNumber<numberOfBallots-prows.keySet().size();printedSerialNumber++) {
			mos.write(("\t\t<row id=\""+prints[printedSerialNumber]+"\" serial=\""+printedSerialNumber+"\"/>\n").getBytes());
			
			pos.write(("\t\t\t<ballot printedSerial=\""+printedSerialNumber+"\">\n").getBytes());
			cos.write(("\t\t\t<ballot printedSerial=\""+printedSerialNumber+"\">\n").getBytes());
			
			String[] nonPermutedCodes=invisibleInkCodes.getNonPermutedSymbols(printedSerialNumber);
			
			Prow p=new Prow();
			p.setId(prints[printedSerialNumber]);
			p.setChosenPage(Prow.ChosenPage.BOTH);
			computeP(p);
			
			String[] permutedCodes=cs.getAllSymbols(p, -1, nonPermutedCodes);
			
			int codeNumber=0;
			for (int q=0;q<qs.length;q++) {
				pos.write(("\t\t\t\t<question id=\""+q+"\">\n").getBytes());
				cos.write(("\t\t\t\t<question id=\""+q+"\">\n").getBytes());				
				for (int noCodesPerQuestion=0;noCodesPerQuestion<invisibleInkCodes.getNoCodesPerQuestion(qs[q]);codeNumber++,noCodesPerQuestion++) {
					String printCode=permutedCodes[codeNumber];
					String commitCode=nonPermutedCodes[codeNumber];
//					System.out.println("invisibleInk.MeetingOne code: "+code);				
					pos.write(("\t\t\t\t\t<symbol id=\""+noCodesPerQuestion+"\" code=\""+printCode+"\"/>\n").getBytes());
					cos.write(("\t\t\t\t\t<symbol id=\""+noCodesPerQuestion+"\" c=\"").getBytes());
					byte[] salt=Commitments.saltCode(mk1,mk2,this.c,(printedSerialNumber+" "+q+" "+noCodesPerQuestion).getBytes()).getEncoded();
					byte[] commitment=Commitments.commitCode(
							salt, 
							this.c,(printedSerialNumber+" "+q+" "+noCodesPerQuestion).getBytes(), commitCode.getBytes());
					cos.write(Base64.encode(commitment));
					cos.write("\"/>\n".getBytes());
				}
				pos.write(("\t\t\t\t</question>\n").getBytes());
				cos.write(("\t\t\t\t</question>\n").getBytes());
			}
			pos.write(("\t\t\t</ballot>\n").getBytes());
			cos.write(("\t\t\t</ballot>\n").getBytes());
		}
			
		cos.write("\t\t\t</codes>\n".getBytes());
		cos.write("\t</database>\n".getBytes());
		cos.write("</xml>".getBytes());
		cos.close();
		
		pos.write("\t\t\t</printCommitments>\n".getBytes());
		pos.write("\t</database>\n".getBytes());
		pos.write("</xml>".getBytes());
		pos.close();

		
mos.write("\t</print>\n".getBytes());			
mos.write("</xml>".getBytes());
mos.close();









		if (Meeting.CheckagainsSchema) {
		    Document doc = Util.DomParse(mapOutputFile);			
		    Source schemaSource = new StreamSource(getClass().getResourceAsStream(MeetingTwo.SerialMapSchema));
		    Schema schema = Meeting.schemaFactory.newSchema(schemaSource);
		    schema.newValidator().validate(new DOMSource(doc));
		}

		if (withSignatures) {
			sign(mapOutputFile);
		}
	}

	
	public static void main(String[] args) throws Exception {
		MeetingTwo m2 = new MeetingTwo(args[0]);
		m2.init(InputConstants.MK1,InputConstants.MK2);		
		m2.go(args[1],args[2]);
		m2.makePrintsAndCommitmentsToCodes(args[3], args[5], args[7]);
		MeetingTwo.writeCodesToFile(args[6]);
	}

}
