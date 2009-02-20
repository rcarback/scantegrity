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

package org.scantegrity.authoring.scantegrity;

import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.gwu.voting.standardFormat.electionSpecification.ElectionSpecification;
import org.scantegrity.common.BallotGeometry;
import org.scantegrity.common.InputConstants;
import org.scantegrity.common.Util;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfAnnotation;
import com.lowagie.text.pdf.PdfAppearance;
import com.lowagie.text.pdf.PdfBorderDictionary;
import com.lowagie.text.pdf.PdfFormField;
import com.lowagie.text.pdf.PdfImportedPage;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfWriter;

public class PrintableBallotMaker extends FormMaker 
{
	
	private PdfReader backgroundReader = null;
	ElectionSpecification es=null;
	ContestSymbols cs=null;
	
	public PrintableBallotMaker(ElectionSpecification es,BallotGeometry geom) throws Exception {
		super(es,geom);
		this.es=es;
		symbolColor=new Color(127,127,255);//Color.BLUE;
	}

	protected void addJavaScript() {
		//do not add any javascript
	}
	
	public void init(String background,String[] chars,boolean charReset,String pathToPrintsFile) throws Exception {
		try {
			backgroundReader = new PdfReader(background);
		} catch (Exception e) {
			createBlankBackgroundPage(geom.getWidth()*72,geom.getHeight()*72);
		}
		cs=new ContestSymbols(pathToPrintsFile,es,chars,charReset);		
	}
	
	public void makePrintableBallots(String outputDir,int start,int stop) throws Exception {

		Rectangle pdfPageSize = backgroundReader.getPageSize(1);
		//double scalling = pdfPageSize.width() / ballotMap.getW();
		
		Document document = new Document(pdfPageSize);
		
		File f= new File(outputDir+"/"+start+"-"+stop+".pdf");
		writer = PdfWriter.getInstance(document,new FileOutputStream(f));
		writer.setPdfVersion(PdfWriter.VERSION_1_5);
		writer.setViewerPreferences(PdfWriter.PageModeUseOC);

		document.open();
        cb = writer.getDirectContent();
                
		PdfImportedPage page1 = writer.getImportedPage(backgroundReader, 1);
		document.setMargins(0,0,0,0);
		cb.addTemplate(page1,0,0);
		
		for (int i=start;i<=stop;i++) {
			String serial=cs.getSerialNumber(i);
			String[] allSymbols=cs.getAllSymbols(i,0);
			addAlignment(i+"");
			addSerialNumber(Util.AddleadingZeros(serial,geom.getNoDigitsInSerial()),i+"");
			addContests(allSymbols,allSymbols,i+"");
			
			if (i!=stop) {
				document.newPage();
				cb.addTemplate(page1,0,0);
			}
		}
		
		document.close();		
	}
	protected void addContests(String[] allSymbolsTop,String[] allSymbolsBottom,String prefixForFieldName) {
    	symbolTopFontSize=getFontSize(geom.getTop("0","0","0"),helv);
    	
        String sufix;
    	
    	int allSymbolsPos=0;
        //step3 - for each race, add the placeholders for symbols
        for (int qno=0;qno<qs.length;qno++) {        	
        	//step 3.1 add the top symbols
        	//for each candidate
        	for (int c=0;c<qs[qno].getAnswers().size();c++) {
        		sufix = "_"+qno+"_"+c;                
            	//step 3.2 for each row (rank), add the bottom symbols and the orange (top, bottom and both)
                //for (int rank=0;rank<noRanks;rank++) 
                int rank=0;
                {
                	sufix = "_"+qno+"_"+rank+"_"+c;
                	rect = geom.getTop(qno+"",rank+"",c+"");

                	drawWhiteRectangle(rect);
                	/*
                	pdfFormField = makeButtonTopHoles(
                			rect, 
                			prefixForFieldName+"topHole"+sufix);
                    writer.addAnnotation(pdfFormField);                	
                	*/
                    //put the symbols to the left of the oval. if you put them
                    //in the oval, and the symbol has lots of black, it may get
                    //detected as marked. Plus the voters sees the letter after marking
                	pdfFormField = makeText(
                			translate(rect, 0-rect.getWidth(),0), 
                    		prefixForFieldName+"bottomSymbol"+sufix,
                    		helv,
                    		symbolTopFontSize,
                    		allSymbolsTop[allSymbolsPos]);
                    writer.addAnnotation(pdfFormField);
                }
                allSymbolsPos++;
        	}
        }
    }
	
/*	
	public void drawWhiteRectangle(Rectangle r) {
	}
*/
	
	public PdfFormField makeText(Rectangle possition, String name, BaseFont font, int fontSize,String text) {
		float h = possition.getHeight();
		float w = possition.getWidth();

        cb.moveTo(0, 0);
        PdfFormField field = PdfFormField.createTextField(writer, false, false, 0);
        field.setWidget(possition, PdfAnnotation.HIGHLIGHT_INVERT);
        field.setFlags(PdfAnnotation.FLAGS_PRINT);
        field.setFieldFlags(PdfFormField.FF_READ_ONLY);
        field.setQuadding(PdfFormField.Q_CENTER);
        field.setFieldName(name);
        field.setValueAsString(text);
        field.setDefaultValueAsString(text);
        field.setBorderStyle(new PdfBorderDictionary(2, PdfBorderDictionary.STYLE_SOLID));
        field.setPage();
        
        PdfAppearance tp = cb.createAppearance(w,h);
        PdfAppearance da = (PdfAppearance)tp.getDuplicate();
        da.setFontAndSize(font, fontSize);
        da.setColorFill(Color.BLACK);
        field.setDefaultAppearanceString(da);
        tp.beginVariableText();
        tp.saveState();
        tp.rectangle(0, 0, w,h);
        tp.clip();
        tp.newPath();
        tp.beginText();
        tp.setFontAndSize(font, fontSize);
        tp.setColorFill(Color.BLACK);
        tp.setTextMatrix(0,0);
        tp.showText(text);
        tp.endText();
        tp.restoreState();
        tp.endVariableText();
        field.setAppearance(PdfAnnotation.APPEARANCE_NORMAL, tp);
        return field;
	}

	private void createBlankBackgroundPage(float w,float h) {
		com.lowagie.text.Document document = new com.lowagie.text.Document(new Rectangle(w,h));
		try {
			PdfWriter.getInstance(document,
					new FileOutputStream("__BlankPdf.pdf"));
			/* do this in test ballots
				HeaderFooter header = new HeaderFooter(new Phrase("Test Ballot"), false);
				HeaderFooter footer = new HeaderFooter(new Phrase("This ballot whould not be used in a real election"), new Phrase("."));
				footer.setAlignment(Element.ALIGN_CENTER);
				document.setHeader(header); 
				document.setFooter(footer);
			*/ 			
			document.open();
			document.add(new Paragraph(" "));
		} catch (DocumentException de) {
			System.err.println(de.getMessage());
		} catch (IOException ioe) {
			System.err.println(ioe.getMessage());
		}
		document.close();
		try {
			backgroundReader = new PdfReader("__BlankPdf.pdf");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

    
    public static void main(String[] args) throws Exception {
		String dir=InputConstants.tempDir;
		ElectionSpecification es=new ElectionSpecification(dir+"ElectionSpec.xml");
		BallotGeometry geom=new BallotGeometry(dir+"geometry.xml");

		String background=dir+"dagstuhl background.pdf";
		String pathToPrintsFile=dir+"private/Prints.xml";
		//Vector<Point> range=new Vector<Point>();
		//range.add(new Point(0,9));
    	
    	PrintableBallotMaker pbm=new PrintableBallotMaker(es,geom);
    	pbm.init(background, ContestSymbols.alphabet, true, pathToPrintsFile);
    	pbm.makePrintableBallots(dir, 0,4);
    	
    	
    	
/*    	
		dir="Elections/POLK COUNTY, FLORIDA NOVEMBER 7, 2000/version4Letter/bottom/";
		es=new ElectionSpecification(dir+"ElectionSpec.xml");
		geom=new BallotGeometry(dir+"geometry.xml");

		background=dir+"polk county back sheet watermark2.pdf";
		pathToPrintsFile=dir+"MeetingTwoPrints.xml";
		//Vector<Point> range=new Vector<Point>();
		//range.add(new Point(0,9));
    	
    	pbm=new PrintableBallotMaker(es,geom);
    	pbm.init(background, Util.alphabet.toCharArray(), false, pathToPrintsFile);
    	pbm.makePrintableBallots(dir, 0,99);
*/
    }
}
