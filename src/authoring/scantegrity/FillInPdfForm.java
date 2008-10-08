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

package software.authoring.scantegrity;

import java.awt.Point;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.TreeMap;
import java.util.Vector;

import org.gwu.voting.standardFormat.Constants;
import org.gwu.voting.standardFormat.basic.Question;
import org.gwu.voting.standardFormat.electionSpecification.ElectionSpecification;
import org.gwu.voting.standardFormat.electionSpecification.exceptions.ESException;

import software.authoring.scantegrity.ContestSymbols;
import software.common.Util;
import software.scanner.ScannedBallot;

import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.AcroFields;
import com.lowagie.text.pdf.PdfFormField;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;

public class FillInPdfForm {

	/**
	 * @param range
	 * @param outDir
	 * @param chars
	 * @param charReset
	 * @throws Exception
	 */
	public static void fillIn(ElectionSpecification es,String form,String pathToPrintsFile,Vector<Point> range,String[] chars, boolean charReset,String outDir) throws Exception {
		ContestSymbols cs=new ContestSymbols(pathToPrintsFile,es,chars,charReset);
		for (int i=0;i<range.size();i++) {
			for (int j=range.get(i).x;j<=range.get(i).y;j++) {
				fillIn(es,form,cs.getSerialNumber(j),cs.getAllSymbols(j,0),outDir+cs.getSerialNumber(j)+".pdf");
			}
		}
	}
	
	public static void fillIn(ElectionSpecification es,String form,String serial,String[] allSymbols,String output) throws FileNotFoundException, DocumentException, IOException {
		PdfReader reader = new PdfReader(form);
		
		PdfStamper stamp1 = new PdfStamper(reader, new FileOutputStream(output));
		
        AcroFields form1 = stamp1.getAcroFields();
        
        int ii=0;
        while (form1.getField("serialTop_"+ii)!=null) {
        	ii++;
        }
        char bullet=162;
        serial=Util.AddleadingZeros(serial, ii);
        for (int i=0;i<serial.length();i++) {
        	form1.setField("serialTop_"+i,serial.charAt(i)+"");
        	form1.setField("serialBulleted_"+i+"_"+serial.charAt(i),Character.toString(bullet));
        }
        
        //set the serial in barcode
        form1.setField("serialBarcode", serial);
        
        Question[] qs=es.getOrderedQuestions();
        int count=0;
        for (int i=0;i<qs.length;i++) {
        	for (int j=0;j<qs[i].getAnswers().size();j++) {
    			form1.setField("bottomSymbol_"+i+"_"+0+"_"+j,allSymbols[count]);        			
    			form1.setFieldProperty("bottomSymbol_"+i+"_"+0+"_"+j,"setfflags",PdfFormField.Q_CENTER, null);
        		count++;
        	}
        }        
        stamp1.close();
	}
		
	public static void randomVote(FormMaker fm,String form) throws Exception {
		String javaScriptFromForm=fm.getJsInitValues()+fm.getJsFunctions();
		
		File f=new File(form);
		if (f.isFile()) {		
			PdfReader reader = new PdfReader(form);
			String output=form.substring(0,form.lastIndexOf(Util.fileSeparator)+1)+"Voted"+form.substring(form.lastIndexOf(Util.fileSeparator)+1);
			PdfStamper stamp1 = new PdfStamper(reader, new FileOutputStream(output));
			StringBuffer sb=new StringBuffer("");
			
			Question[] qs=fm.getQs();
			int a;
			int r;
			for (int q=0;q<qs.length;q++) {
				r=0;
				for (int j=0;j<qs[q].getMax();j++) {
					if (Math.random()<0.9) {
						a =(int)(Math.random()*qs[q].getAnswers().size());
						if (qs[q].getTypeOfAnswer().compareTo(Constants.ONE_ANSWER)==0)
							sb.append("voteOne("+q+","+a+");");
						else
							if (qs[q].getTypeOfAnswer().compareTo(Constants.MULTIPLE_ANSWERS)==0)
								sb.append("voteMany("+q+","+a+");");
							else
								if (qs[q].getTypeOfAnswer().compareTo(Constants.RANK)==0)
									sb.append("voteRank("+q+","+r+","+a+");");
					}
					r++;
				}
			}
			
		    sb.append("finishSelection();");
		        
			stamp1.addJavaScript(javaScriptFromForm+sb.toString());
			stamp1.close();
		} else {
			File[] allFIles=f.listFiles();
			for (int i=0;i<allFIles.length;i++) {
				randomVote(fm, allFIles[i].getAbsolutePath());
			}
		}
	}

	//TODO a method that would construct a pdf similar with the scanned paper ballot
	public static void reconstructVote(FormMaker fm,String form, TreeMap<Integer, TreeMap<Integer, TreeMap<Integer, ScannedBallot.TypeOfVotes>>>  markedContests) throws Exception {
		String javaScriptFromForm=fm.getJsInitValues()+fm.getJsFunctions();
		
		File f=new File(form);
		if (f.isFile()) {		
			PdfReader reader = new PdfReader(form);
			String output=form.substring(0,form.lastIndexOf(Util.fileSeparator)+1)+"Voted"+form.substring(form.lastIndexOf(Util.fileSeparator)+1);
			PdfStamper stamp1 = new PdfStamper(reader, new FileOutputStream(output));
			StringBuffer sb=new StringBuffer("");
			
			Question[] qs=fm.getQs();
			int a;
			int r;
			for (int q=0;q<qs.length;q++) {
				r=0;
				for (int j=0;j<qs[q].getMax();j++) {
					if (Math.random()<0.9) {
						a =(int)(Math.random()*qs[q].getAnswers().size());
						if (qs[q].getTypeOfAnswer().compareTo(Constants.ONE_ANSWER)==0)
							sb.append("voteOne("+q+","+a+");");
						else
							if (qs[q].getTypeOfAnswer().compareTo(Constants.MULTIPLE_ANSWERS)==0)
								sb.append("voteMany("+q+","+a+");");
							else
								if (qs[q].getTypeOfAnswer().compareTo(Constants.RANK)==0)
									sb.append("voteRank("+q+","+r+","+a+");");
					}
					r++;
				}
			}
			
		    sb.append("finishSelection();");
		        
			stamp1.addJavaScript(javaScriptFromForm+sb.toString());
			stamp1.close();
		} else {
			File[] allFIles=f.listFiles();
			for (int i=0;i<allFIles.length;i++) {
				randomVote(fm, allFIles[i].getAbsolutePath());
			}
		}
	}
	
	
	/**
	 * 
	 * @param args ElectionSpec PdfForm PrintsFile From0-To0;From1-To1 OutDir Chars
	 * @throws ESException
	 * @throws IOException
	 * @throws DocumentException
	 */
	public static void main(String[] args) throws Exception {
/*		
		ElectionSpecification es=new ElectionSpecification(args[0]);
		Vector<Point> range=new Vector<Point>();;
		StringTokenizer st=new StringTokenizer(args[3],";");
		while (st.hasMoreTokens()) {
			String s=st.nextToken();
			if (s.indexOf('-')==-1)
				range.add(new Point(Integer.parseInt(s),Integer.parseInt(s)));				
			else
				range.add(new Point(Integer.parseInt(s.substring(0,s.indexOf('-'))),Integer.parseInt(s.substring(s.indexOf('-')+1))));
		}
		FillInPdfForm.fillIn(es,args[1],args[2], range, args[4], args[5].toCharArray(), false);
*/
		String dir="C:/Documents and Settings/stefan/Desktop/claimDemocracy/";
		ElectionSpecification es=new ElectionSpecification(dir+"ElectionSpec.xml");
		Vector<Point> range=new Vector<Point>();
		range.add(new Point(0,0));
		FillInPdfForm.fillIn(es, dir+"javaCreatedForm.pdf",dir+"MeetingTwoPrints.xml",range,software.authoring.ContestSymbols.alphabet,false,dir);
		
		//BallotGeometry geom=new BallotGeometry(dir+"ScanTegrity/geometry.xml");
		//FillInPdfForm.randomVote(new FormMaker(es,geom), dir+"Scantegrity/pdfBallots/");
	}

}
