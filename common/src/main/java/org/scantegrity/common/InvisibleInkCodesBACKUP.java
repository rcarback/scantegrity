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

/*
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.TreeMap;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.parsers.SAXParser;

import org.bouncycastle.util.encoders.Base64;
import org.gwu.voting.standardFormat.Constants;
import org.gwu.voting.standardFormat.basic.Question;
import org.gwu.voting.standardFormat.electionSpecification.ElectionSpecification;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class InvisibleInkCodes {

	int a=0,c=0,x=0,m=0;
	int noCodesPerBallot=0;
	
//	protected Hashtable<String,String> serialToPid=null;	
	protected int previousPrintedSerial=-1;
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

	//TODO list unvalid conbinations in a file
	public static String[] forbidenCodes= {
		"FUCK","SH1T","-REP","-DEM"};
	
	int[] noCodesBeforeQuestion=null;
	
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

	public InvisibleInkCodes(ElectionSpecification es) {
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
	
	public InvisibleInkCodes(String pathToPrintsFile, ElectionSpecification es) throws Exception {
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
	
	public String[] getNonPermutedSymbols(int printedSerial) throws Exception {
//System.out.println("pid="+pid+" currentpid="+currentpid);		
		if (printedSerial<=previousPrintedSerial)
			throw new Exception("For effiency reasons, please generate the code in ascending order of pids");

		String[] nonPermutedCodes=new String[noCodesPerBallot];
		
		for (int i=0;i<(printedSerial-previousPrintedSerial-1)*noCodesPerBallot;i++) {
			advance();
		}
		
		for (int i=0;i<noCodesPerBallot;i++) {
			nonPermutedCodes[i]=getNextCode();
		}
		previousPrintedSerial=printedSerial;
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

	
	//TODO - check if the codes are enogth for all ballots - otherwise resead the PRNG when looping
	public void init(int x0) {
		
		//not a good idea...the seed x0 can be guessed since x0<m and m is the closest power of two greater then the total number of symbols needed on all ballots
		//int m=1 << (int)(Math.log(numberOfBallots*noCodesPerBallot-1)/Math.log(2) + 1);
		//m is the closesc power of 2, smaller then (the number of valid letters)^(the length of the code). i.e. 28^4
		m=1 << (int)(Math.log(Math.pow(InvisibleInkCodes.CodesAlphabet.length,InvisibleInkCodes.NumberOfCharectersInACode))/Math.log(2));
		
		//choose the values such that the period is guaranteed
		//choose beter values for a and c
		a=17;
		c=3;

//System.out.println("InvisibleInkCodes a="+a);		
//System.out.println("InvisibleInkCodes c="+c);
//System.out.println("InvisibleInkCodes x0="+x0);
//System.out.println("InvisibleInkCodes m="+m);
		
		this.x=x0%m;
	}
	
	protected void advance() {
		//this is the "great" linear congruential random number generator
		x=(a*x+c)%m;	
	}
	
	protected String getNextCode() {
		advance();
		return intToString(x,CodesAlphabet);
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
*/