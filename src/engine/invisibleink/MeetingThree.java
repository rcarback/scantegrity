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

package software.engine.invisibleink;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Hashtable;
import java.util.TreeMap;

import javax.xml.parsers.SAXParser;

import org.bouncycastle.util.encoders.Base64;
import org.gwu.voting.standardFormat.Constants;
import org.gwu.voting.standardFormat.basic.Question;
import org.w3c.dom.Document;

import software.authoring.scantegrity.ContestSymbols;
import software.common.CodesReaderSax;
import software.common.InputConstants;
import software.common.InvisibleInkCodes;
import software.common.Prow;
import software.common.SymbolRow;
import software.common.Util;
import software.engine.Commitments;

public class MeetingThree extends software.engine.scantegrity.MeetingThree {

	InvisibleInkCodes invisibleInkCodes=null;
	
	
	public MeetingThree(String confFile) throws Exception {
		super(confFile);
	}

	public MeetingThree(Document doc) throws Exception {
		super(doc);		
	}
	
	public MeetingThree() throws Exception {
		super();		
	}

	public void revealMarkedSymbols(String m3in,String pathToAlphabetFile,String outputFile) throws Exception {
		readPRows(m3in);
		
		InvisibleInkCodes.ReadCodes(pathToAlphabetFile);
		invisibleInkCodes=new InvisibleInkCodes(null,es);
		invisibleInkCodes.init(mk1, mk2, c);

		ContestSymbols cs=new ContestSymbols(null,es,ContestSymbols.alphabet,true);
		
		//create a pid to serial mapping
		Hashtable<String,String> pidToSerial=new Hashtable<String, String>();
		for (String s:serialToPid.keySet()) {
			pidToSerial.put(serialToPid.get(s),s);
		}
		
		fos = new BufferedOutputStream(new FileOutputStream(outputFile));
		fos.write("<xml>\n".getBytes());
		fos.write("\t<database>\n".getBytes());
		fos.write("\t\t<printCommitments>\n".getBytes());
		
		for (int pid:prows.keySet()) {
			int printedSerial=Integer.parseInt(pidToSerial.get(pid+""));
			fos.write(("\t\t\t<ballot printedSerial=\""+printedSerial+"\">\n").getBytes());
			
			//all the Codes for this ballot
			String[] nonPermutedCodes=invisibleInkCodes.getNonPermutedSymbols(printedSerial);

			Prow p=new Prow();
			p.setId(pid);
			p.setChosenPage(Prow.ChosenPage.BOTH);
			computeP(p);

			//String[] permutedCodes=cs.getAllSymbols(p, -1, nonPermutedCodes);

			//all the selections the voter made
			byte[] codedVotes=prows.get(pid).getVote();
			
			//project allSymbols on prowWithCodedVotes
			int noCodedVotesUntilcurrentQuestion=0;
			int nonPermutedCodesPos=0;
			Question[] qs=es.getOrderedQuestions();
			for (int q=0;q<qs.length;q++) {
				fos.write(("\t\t\t\t<question id=\""+q+"\">\n").getBytes());
				//Arrays.sort(codedVotes,noCodedVotesUntilcurrentQuestion,qs[q].getMax());
				for (int a=0;a<qs[q].getMax();a++) {
					byte currentCodedVote=codedVotes[noCodedVotesUntilcurrentQuestion+a];
					if (currentCodedVote!=-1) {
						String code=nonPermutedCodes[nonPermutedCodesPos+currentCodedVote];
						fos.write(("\t\t\t\t\t<symbol id=\""+currentCodedVote+"\"" +
								" code=\""+code+"\""+
								" salt=\"").getBytes());
						byte[] salt=Commitments.saltCode(mk1,mk2,this.c,(printedSerial+" "+q+" "+currentCodedVote).getBytes()).getEncoded(); 
						fos.write(Base64.encode(salt));
						fos.write("\"/>\n".getBytes());
					}
					//careful with the rank questions
					if (qs[q].getTypeOfAnswer().compareTo(Constants.RANK)==0) {
						nonPermutedCodesPos+=qs[q].getAnswers().size();
					}
				}
				if (qs[q].getTypeOfAnswer().compareTo(Constants.RANK)!=0) {
					nonPermutedCodesPos+=qs[q].getAnswers().size();
				}				
				noCodedVotesUntilcurrentQuestion+=qs[q].getMax();
				fos.write(("\t\t\t\t</question>\n").getBytes());
			}
			fos.write(("\t\t\t</ballot>\n").getBytes());
		}

		fos.write("\t\t</printCommitments>\n".getBytes());
		fos.write("\t</database>\n".getBytes());
		fos.write("</xml>\n".getBytes());
		fos.close();	

		if (withSignatures)
			sign(outputFile);
		
	}

	public void revealContestedSymbols(String contestedSymbols,String pathToAlphabetFile,String outputFileWithCodes,String outputFileWithBallotsToBeRetrieved) throws Exception {
		CodesReaderSax cr = new CodesReaderSax();
        SAXParser saxParser = Util.newSAXParser();
        saxParser.parse( new File(contestedSymbols), cr);
        while (!cr.isDoneParsing()) {
        	Thread.sleep(100);
        }
        
		InvisibleInkCodes.ReadCodes(pathToAlphabetFile);
		invisibleInkCodes=new InvisibleInkCodes(null,es);
		invisibleInkCodes.init(mk1, mk2, c);
    

		fos = new BufferedOutputStream(new FileOutputStream(outputFileWithCodes));
		fos.write("<xml>\n".getBytes());
		fos.write("\t<database>\n".getBytes());
		fos.write("\t\t<printCommitments>\n".getBytes());

		BufferedOutputStream fos2 = new BufferedOutputStream(new FileOutputStream(outputFileWithBallotsToBeRetrieved));
		fos2.write("<xml>\n".getBytes());
		fos2.write("\t<database>\n".getBytes());
		fos2.write("\t\t<printCommitments>\n".getBytes());

		
        //serialPrinted, question, symbolid(dummy in this case)
    	TreeMap<Integer,TreeMap<Byte,TreeMap<Byte,SymbolRow>>> rows=cr.getRows();
    	boolean getBallotFromTheWarehouse=false;
    	boolean ballotHeaderprinted=false;
    	for (int serialPrinted:rows.keySet()) {
    		String[] webcodes=invisibleInkCodes.getNonPermutedSymbols(serialPrinted);
    		getBallotFromTheWarehouse=false;
    		ballotHeaderprinted=false;
    		for (byte question:rows.get(serialPrinted).keySet()) {
    			
    			String[] voterClaimedCodes=new String[rows.get(serialPrinted).get(question).size()];
    			int cvvPos=0;
    			for (byte codeId:rows.get(serialPrinted).get(question).keySet()) {
    				voterClaimedCodes[cvvPos++]=rows.get(serialPrinted).get(question).get(codeId).getCode();
    			}
    			
    			if (mightTheVoterBeRight(question, voterClaimedCodes, webcodes)) {
    				if ( ! getBallotFromTheWarehouse) {
    					fos2.write(("\t\t\t<ballot printedSerial=\""+serialPrinted+"\"/>\n").getBytes());
    				}
    				getBallotFromTheWarehouse=true;
    			} else {
    				if ( ! ballotHeaderprinted) {
    					fos.write(("\t\t\t<ballot printedSerial=\""+serialPrinted+"\">\n").getBytes());
    					ballotHeaderprinted=true;
    				}
    				//show all the codes for this question
    				fos.write(("\t\t\t\t<question id=\""+question+"\">\n").getBytes());
    				for (int i=invisibleInkCodes.getNoCodesBeforeQuestion(question);i<invisibleInkCodes.getNoCodesBeforeQuestion(question+1);i++) {
    						String code=webcodes[i];
    						fos.write(("\t\t\t\t\t<symbol id=\""+(i-invisibleInkCodes.getNoCodesBeforeQuestion(question))+"\"" +
    								" code=\""+code+"\""+
    								" salt=\"").getBytes());
    						byte[] salt=Commitments.saltCode(mk1,mk2,this.c,(serialPrinted+" "+question+" "+(i-invisibleInkCodes.getNoCodesBeforeQuestion(question))).getBytes()).getEncoded(); 
    						fos.write(Base64.encode(salt));
    						fos.write("\"/>\n".getBytes());
    				}
    				fos.write(("\t\t\t\t</question>\n").getBytes());
    			}
    		}
    		if (ballotHeaderprinted) {
    			fos.write(("\t\t\t</ballot>\n").getBytes());
    		}
    	}

		fos2.write("\t\t</printCommitments>\n".getBytes());
		fos2.write("\t</database>\n".getBytes());
		fos2.write("</xml>\n".getBytes());
		fos2.close();	

		fos.write("\t\t</printCommitments>\n".getBytes());
		fos.write("\t</database>\n".getBytes());
		fos.write("</xml>\n".getBytes());
		fos.close();	

		if (withSignatures) {
			sign(outputFileWithCodes);
			sign(outputFileWithBallotsToBeRetrieved);
		}
	}

	//check if ONE of the claimed votes are in the correct codes for that question
	private boolean mightTheVoterBeRight(int question,String[] voterClaimedCodes,String[] webcodes) {
		for (String voterClaimedCode:voterClaimedCodes) {
			for (int i=invisibleInkCodes.getNoCodesBeforeQuestion(question);i<invisibleInkCodes.getNoCodesBeforeQuestion(question+1);i++) {
				if (voterClaimedCode.compareTo(webcodes[i])==0) {
					return true;
				}
			}
		}
		return false;
	}
	
	public static void main(String[] args) throws Exception {
		if (args.length < 2) {
			System.out.println("MeetingThree MeetingOneIn.xml MeetingThreeIn.xml");
			return;
		}
		MeetingThree m3 = new MeetingThree(args[0]);
		m3.init(InputConstants.MK1,InputConstants.MK2);

		m3.revealMarkedSymbols(args[1], args[3], args[4]);
		
		m3.go(args[1],args[2]);
	}
}
