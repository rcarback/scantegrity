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

package org.scantegrity.common;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.InvalidKeyException;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.parsers.SAXParser;

import org.gwu.voting.standardFormat.Constants;
import org.gwu.voting.standardFormat.basic.Question;
import org.gwu.voting.standardFormat.electionSpecification.ElectionSpecification;
import org.scantegrity.engine.Commitments;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import org.bouncycastle.util.encoders.Base64;

public class InvisibleInkCodes2 {

	int noCodesPerBallot=0;
		
	protected TreeMap<Integer,Prow> prows=null;
	protected Question[] qs=null;
	
	public static int NumberOfCharectersInACode=4;
	public static String[] CodesAlphabet={
		"O","1","2","3","4",
		"5","6","7","8","9",
		"A","C","E","F","H",
		"J","K","L","+","N",
		"P","R","S","T","U",
		"X","W","Y","=",">",
		"#","-"
	};//1,000,000 4 digit symbols

	public static int NumberOfPossibleCodes=(int)Math.pow(CodesAlphabet.length,NumberOfCharectersInACode);
	public static int noBits=(int)Math.round((Math.log(NumberOfPossibleCodes)/Math.log(2)));
	
	//TODO list unvalid conbinations in a file
	public static String[] forbidenCodes= {
		"FUCK","SH1T","-REP","-DEM"};
	
	int[] noCodesBeforeQuestion=null;

	protected SecretKeySpec mk1 = null;
	protected SecretKeySpec mk2 = null;
	protected byte[] c = null;
	
	public void init(byte[] mk1,byte[] mk2,byte[] c) {
		this.mk1 = new SecretKeySpec(mk1,"AES");
		this.mk2 = new SecretKeySpec(mk2,"AES");
		this.c=c;
	}
	
	public static void WriteCodes(String outputFile) throws IOException {
		OutputStream pos = new BufferedOutputStream(new FileOutputStream(outputFile));
		pos.write("<xml>\n".getBytes());
		pos.write(("\t<codes numberOfLettersPerCode=\""+InvisibleInkCodes.NumberOfCharectersInACode+"\">\n").getBytes());
		
		for (int i=0;i<InvisibleInkCodes.CodesAlphabet.length;i++) {
			pos.write(("\t\t<code id=\""+i+"\" value=\"").getBytes());
			pos.write(Base64.encode(InvisibleInkCodes.CodesAlphabet[i].getBytes()));
			pos.write("\"/>\n".getBytes());
		}
		
		pos.write("\t</codes>\n".getBytes());			
		pos.write("</xml>".getBytes());
		pos.close();		
	}

	public static void ReadCodes(String inFile) throws IOException, SAXException {
		Document doc = Util.DomParse(inFile);
		Node codesNode=doc.getElementsByTagName("codes").item(0);
		InvisibleInkCodes.NumberOfCharectersInACode=Integer.parseInt(codesNode.getAttributes().getNamedItem("numberOfLettersPerCode").getNodeValue());
		
		TreeMap<Integer, String> codes=new TreeMap<Integer, String>();
		NodeList codeNodeList=doc.getElementsByTagName("code");
		for (int i=0;i<codeNodeList.getLength();i++) {
			NamedNodeMap codeNode=codeNodeList.item(i).getAttributes();
			codes.put(Integer.parseInt(codeNode.getNamedItem("id").getNodeValue()), new String(Base64.decode(codeNode.getNamedItem("value").getNodeValue())));
		}
		
		InvisibleInkCodes.CodesAlphabet=new String[codes.size()];
		int index=0;
		for (int key:codes.keySet()) {
			InvisibleInkCodes.CodesAlphabet[index++]=codes.get(key);
		}
	}

	public InvisibleInkCodes2(ElectionSpecification es) {
		qs=es.getOrderedQuestions();
		noCodesPerBallot=0;
		noCodesBeforeQuestion=new int[qs.length+1];
		for (int q=0;q<qs.length;q++) {
				noCodesBeforeQuestion[q]=noCodesPerBallot;
				noCodesPerBallot+=getNoCodesPerQuestion(qs[q]);
		}
		noCodesBeforeQuestion[qs.length]=noCodesPerBallot;
	}
	
	public int getNoCodesBeforeQuestion(int q) {
		return noCodesBeforeQuestion[q];
	}
	
	public int getNoCodesPerQuestion(Question q) {
		if (q.getTypeOfAnswer().compareTo(Constants.RANK)==0) {
			return q.getAnswers().size()*q.getMax();
		}
		return 	q.getAnswers().size();
	}
	
	public InvisibleInkCodes2(String pathToPrintsFile, ElectionSpecification es) throws Exception {
		this(es);
		
		if (pathToPrintsFile!=null) {
			MeetingReaderSax mr = new MeetingReaderSax();
	        try {
	            SAXParser saxParser = Util.newSAXParser();
	            saxParser.parse( new File(pathToPrintsFile), mr);
	        } catch (Throwable t) {
	            t.printStackTrace();
	        }        
	        while (!mr.isDoneParsing()) {
	        	Thread.sleep(100);
	        }        
			prows=mr.getProws();
		}
	}
	
	public static int getCode(int printedSerial, int qno,int rank, int ano,SecretKeySpec key,int noBits, TreeSet<Integer> codesAlreadyGenerated) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		//TODO improve this into single bytes
		String m=printedSerial+" "+qno+" "+rank+" "+ano;
		SecurityUtil.cipherPkcs5Padding.init(Cipher.ENCRYPT_MODE, key);
		byte[] enc=m.getBytes();
		int ret=-1;
		do {
			enc=SecurityUtil.cipherPkcs5Padding.doFinal(enc);
			
			//from the last noBits bits, make a positive integer
			ret=enc[15];
			ret |=enc[14]<<8;
			ret |=enc[13]<<16;
			ret |=enc[12]<<24;
			//now ret has the last 32 bits
			
			//take only the last noBits
			ret ^= 1<<(noBits-1);
		}
		while (ret>=NumberOfPossibleCodes || codesAlreadyGenerated.contains(ret));
		
		return ret;
	}
	
	public String[] getNonPermutedSymbols(int printedSerial) throws Exception {
		String[] nonPermutedCodes=new String[noCodesPerBallot];
		int nonPermutedCodesPos=0;
		TreeSet<Integer> codesAlreadyGenerated=new TreeSet<Integer>();
		
		int noRanks=0;
        for (int qno=0;qno<qs.length;qno++) { 
        	
        	//generate the key for this question;
        	SecretKeySpec key=Commitments.KeyForCodeGeneration(mk1, mk2, c, (printedSerial+"").getBytes(), (qno+"").getBytes(), Commitments.KEY_CONSTANT);
        	
        	codesAlreadyGenerated.clear();
            noRanks=1;
            if (qs[qno].getTypeOfAnswer().compareTo(Constants.RANK)==0)
            	noRanks=qs[qno].getMax();
            for (int rank=0;rank<noRanks;rank++) {                	            		        	
	        	for (int ano=0;ano<qs[qno].getAnswers().size();ano++) {	        			        		
	        		nonPermutedCodes[nonPermutedCodesPos++]=
	        			intToString(getCode(printedSerial, qno, rank, ano, key, noBits,codesAlreadyGenerated),CodesAlphabet);
	        	}
            }
        }
				
		return nonPermutedCodes;
	}
	
	public String[] getAllSymbols(Prow prow) throws Exception {
		String[] nonPermutedCodes=getNonPermutedSymbols(prow.getId());
		String[] permutedCodes=new String[noCodesPerBallot];		
		
		int numberOfAnswers = 0;
		
		if (prow!=null) {
			int numberOfAnswersUntillCurrentQuestion = 0;			
			int numberOfCodesUntillCurrentQuestion = 0;
			for (int i=0;i<qs.length;i++) {
				numberOfAnswers = qs[i].getAnswers().size();
				byte[] perm = getPermutationForCurrentPage(prow.getPage1(), prow.getPage2(), numberOfAnswers, numberOfAnswersUntillCurrentQuestion);
				int noRanks=qs[i].getTypeOfAnswer().compareTo(Constants.RANK)==0?qs[i].getMax():1;
				for (int rank=0;rank<noRanks;rank++) {
					Util.permString(perm,nonPermutedCodes,numberOfCodesUntillCurrentQuestion,permutedCodes);	
					numberOfCodesUntillCurrentQuestion+=numberOfAnswers;
				}								
				numberOfAnswersUntillCurrentQuestion+=numberOfAnswers;				
			}
		}
		return permutedCodes;		
	}
	
	public String[] getAllSymbols(int printedSerial) throws Exception {		
		return getAllSymbols(prows.get(printedSerial));
	}

	protected byte[] getPermutationForCurrentPage(byte[] p1, byte[] p2,int numberOfAnswers,int numberOfAnswersUntillCurrentQuestion) {
				byte[] ret = new byte[numberOfAnswers];
				
				byte[] permTop = new byte[numberOfAnswers];
				System.arraycopy(p1,numberOfAnswersUntillCurrentQuestion,permTop,0,numberOfAnswers);
				byte[] permBottom = new byte[numberOfAnswers];
				System.arraycopy(p2,numberOfAnswersUntillCurrentQuestion,permBottom,0,numberOfAnswers);
				byte[] permBottomInverse=Util.getInverse(permBottom);
				for (int j=0;j<permTop.length;j++) {
					ret[j]=permBottomInverse[permTop[j]];				
				}
				return ret;
			}
	
	public static String intToString(int x,String[] radixChars) {
		int radix=radixChars.length;
		StringBuffer ret=new StringBuffer("");
		while (x>0) {
			ret.append(radixChars[x%radix]);
			x=x/radix;
		}
		while (ret.length()<NumberOfCharectersInACode) {
			ret.append(radixChars[0]);
		}
		return ret.reverse().toString();
	}
	
	public int getNoCodesPerBallot() {
		return noCodesPerBallot;
	}	
}