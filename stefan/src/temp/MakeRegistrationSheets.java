package temp;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.TreeMap;

import javax.xml.parsers.SAXParser;

import software.common.BallotGeometry;
import software.common.MeetingReaderSax;
import software.common.Prow;
import software.common.Util;

import com.lowagie.text.DocumentException;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfImportedPage;
import com.lowagie.text.pdf.PdfLayer;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfWriter;

public class MakeRegistrationSheets {

	BallotGeometry geometry;
	private PdfContentByte cb = null;
	private PdfReader backgroundReader = null;
	Rectangle pdfPageSize = null;
	
	protected BaseFont helv = null;

	float pdfPageheight=0;
	
	String fileNamePrefix="Registration_";
	
	int symbolFontSize=-1;
	
	int batchSize=125;
	
	protected TreeMap<Integer,Prow> prows=null;
	protected int[] pinsToConfirmationCodes=null;
	
	public MakeRegistrationSheets(String meetingOnePrints,int[] pinsToConfirmationCodes) {
		MeetingReaderSax mr = new MeetingReaderSax();
        try {
            SAXParser saxParser = Util.newSAXParser();
            saxParser.parse( new File(meetingOnePrints), mr);
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
        prows = mr.getProws();
        this.pinsToConfirmationCodes=pinsToConfirmationCodes;
	}
	
	public void make(int start,int stop, String getPathToWatermark, boolean coloredWatermark, String outputDir) throws Exception {				
		try {
			backgroundReader = new PdfReader(getPathToWatermark);
			pdfPageSize = backgroundReader.getPageSize(1);
			pdfPageheight=pdfPageSize.getHeight();
			if (coloredWatermark) {
				//backgroundReader = new PdfReader(coverAllColor(getPathToWatermark));
				//TODO modify the background to cover all the colors
			}
		} catch (Exception e) {
			createBlankBackgroundPage(8.5f*72f,11f*72f);
		}
		
		File f= new File(outputDir);
		if (!f.exists())
			f.mkdirs();		
		make(outputDir,start,stop);		
	}
	
	private void createBlankBackgroundPage(float w,float h) {
		com.lowagie.text.Document document = new com.lowagie.text.Document(new Rectangle(w,h));
		pdfPageheight=h;
		try {
			PdfWriter.getInstance(document,new FileOutputStream("__BlankPdf.pdf"));
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
	
	private void make(String outputDir,int start,int stop) throws Exception {       
		com.lowagie.text.Document document = new com.lowagie.text.Document(pdfPageSize);
		pdfPageheight=pdfPageSize.getHeight();
		File f= new File(outputDir+"/"+fileNamePrefix+"_"+start+".pdf");
		PdfWriter writer = PdfWriter.getInstance(document,new FileOutputStream(f));
		writer.setPdfVersion(PdfWriter.VERSION_1_5);
		writer.setViewerPreferences(PdfWriter.PageModeUseOC);
		
		document.open();
        cb = writer.getDirectContent();
        
		PdfLayer l1 = new PdfLayer(""+start, writer);
		cb.beginLayer(l1);
        
		PdfImportedPage page1 = writer.getImportedPage(backgroundReader, 1);

    	symbolFontSize=14;
		
        int batchNumber = 0;
        int numberOfPrintedBallotsInThisBatch=0;
		for (int i=start;i<=stop;i++) {
        	if (numberOfPrintedBallotsInThisBatch >= batchSize) {
        		document.close();
                batchNumber++;
                numberOfPrintedBallotsInThisBatch=0;
        		document = new com.lowagie.text.Document(pdfPageSize);
        		f= new File(outputDir+"/"+fileNamePrefix+"_"+(start+batchNumber*batchSize)+".pdf");
        		writer = PdfWriter.getInstance(document, new FileOutputStream(f));
        		document.open();      
                cb = writer.getDirectContent();

				page1 = writer.getImportedPage(backgroundReader, 1);
        	}
            cb.addTemplate(page1,0,0);        	
        	document.setMargins(0,0,0,0);        	
			
			addPINs(i);
			
			numberOfPrintedBallotsInThisBatch++;			
			if (numberOfPrintedBallotsInThisBatch < batchSize && i< stop) {
	        	document.newPage();
			}			
        }
		cb.endLayer();
		document.close();
	}
	
    protected void addPINs(int i) {
        for (int electionNo=0;electionNo<1;electionNo++) {
        	Prow prow=prows.get(i);
			drawTextCenter(prow.getId()+"", geometry.getTopCluster(electionNo+"",0+"",0+"").getCenterOfMass(),helv, symbolFontSize);
    		drawTextCenter(Util.toString(prow.getPage1()), geometry.getBottomCluster(electionNo+"",0+"",0+"").getCenterOfMass(),helv, symbolFontSize);
        }
    }

    protected void addConfirmationNumber(int i) {
        for (int electionNo=0;electionNo<1;electionNo++) {
        	Prow prow=prows.get(i);
			drawTextCenter(prow.getId()+"", geometry.getTopCluster(electionNo+"",0+"",0+"").getCenterOfMass(),helv, symbolFontSize);
    		drawTextCenter(Util.toString(prow.getPage1()), geometry.getBottomCluster(electionNo+"",0+"",0+"").getCenterOfMass(),helv, symbolFontSize);
        }
    }
    
	private void drawAlignmentMarks(Rectangle rect) {
			cb.setColorStroke(Color.BLACK);
			cb.setColorFill(Color.BLACK);
			
			float hh = rect.getHeight();
			float w = rect.getWidth();
	        float l=w>hh?hh:w;
	        cb.circle(rect.getLeft()+w/2,rect.getBottom()+hh/2,l/2);
			cb.fill();
			//cb.circle((float)(p.getX()*72),(float)(h-p.getY()*72),geometry.getHoleDiameter()*72/2);
			cb.fillStroke();
	}

	private void drawTextCenter(String text,Point2D p,BaseFont font,int fontSize) {
		cb.beginText();
		cb.setFontAndSize(font,fontSize);
		float yoffset = (font.getAscentPoint(text,fontSize)+font.getDescentPoint(text,fontSize)) / 2;
		font.correctArabicAdvance();
		cb.showTextAligned(PdfContentByte.ALIGN_CENTER, text, (float)(p.getX()*72),(float)(pdfPageheight-p.getY()*72-yoffset),0);
		cb.endText();
	}
	
	public static void main(String[] args) {

	}

}
