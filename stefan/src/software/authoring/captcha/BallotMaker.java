package software.authoring.captcha;

import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.TreeMap;

import org.gwu.voting.standardFormat.Constants;
import org.gwu.voting.standardFormat.electionSpecification.ElectionSpecification;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import software.common.BallotGeometry;
import software.common.InputConstants;
import software.common.Util;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Image;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfAction;
import com.lowagie.text.pdf.PdfBorderDictionary;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfFormField;
import com.lowagie.text.pdf.PdfImportedPage;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.TextField;

public class BallotMaker extends software.authoring.scantegrity.PrintableBallotMaker {
	
	private PdfReader backgroundReader = null;
	ElectionSpecification es=null;
	TreeMap<Integer,TreeMap<Integer,TreeMap<Integer,String>>> allCodes=null;
	
	static MakeCaptcha mc=null;
	static String captchaTempImageFile="temp.bmp";
	
	public BallotMaker(ElectionSpecification es,BallotGeometry geom) throws Exception {
		super(es,geom);
		this.es=es;
		
		//80%symbolColor=new Color(255,48,255);
		//symbolColor=new Color(255,153,255);//40%
		symbolColor=new Color(255,128,255);//50%
		//symbolColor=Color.BLACK;
		
		mc=new MakeCaptcha(MakeCaptcha.ConfigFile);
	}
	
	protected void loadJavaScript() throws IOException {
		//writer.addJavaScript("app.alert(\"The codes are \");");	
	}
	
	public void init(String background,String pathToPrintsFile) throws Exception {
		try {
			backgroundReader = new PdfReader(background);
		} catch (Exception e) {
			createBlankBackgroundPage(geom.getWidth()*72,geom.getHeight()*72);
		}
		
		org.w3c.dom.Document doc=Util.DomParse(pathToPrintsFile);
		allCodes=new TreeMap<Integer, TreeMap<Integer,TreeMap<Integer,String>>>();
		int printedSerial=-1;
		int qid=-1;
		int sid=-1;
		NodeList ballots=doc.getElementsByTagName("ballot");
		for (int b=0;b<ballots.getLength();b++) {
			Node ballot=ballots.item(b);
			printedSerial=Integer.parseInt(ballot.getAttributes().getNamedItem("printedSerial").getNodeValue());
			TreeMap<Integer,TreeMap<Integer,String>> ballotCodes=new TreeMap<Integer, TreeMap<Integer,String>>();
			for (Node question=ballot.getFirstChild();question!=null;question=question.getNextSibling()) {
				if (question.getNodeName().compareTo("question")==0) {
					qid=Integer.parseInt(question.getAttributes().getNamedItem("id").getNodeValue());
					TreeMap<Integer, String> questionCodes=new TreeMap<Integer, String>();
					for (Node symbol=question.getFirstChild();symbol!=null;symbol=symbol.getNextSibling()) {
						if (symbol.getNodeName().compareTo("symbol")==0) {
							sid=Integer.parseInt(symbol.getAttributes().getNamedItem("id").getNodeValue());
							questionCodes.put(sid, symbol.getAttributes().getNamedItem("code").getNodeValue());
						}
					}
					ballotCodes.put(qid, questionCodes);
				}
			}
			allCodes.put(printedSerial, ballotCodes);
		}
//System.out.println(allCodes);
	}
	
	public void makePrintableBallots(String outputDir,int start,int stop) throws Exception {

		Rectangle pdfPageSize = backgroundReader.getPageSize(1);
		//double scalling = pdfPageSize.getWidth() / ballotMap.getW();
		
		Document document = new Document(pdfPageSize);
		File f = new File(outputDir+"/"+start+"-"+stop+".pdf");
		writer = PdfWriter.getInstance(document,new FileOutputStream(f));
		writer.setPdfVersion(PdfWriter.VERSION_1_4);
		writer.setViewerPreferences(PdfWriter.PageLayoutSinglePage);
		//writer.setViewerPreferences(PdfWriter.PageModeUseOC);
		//writer.setPDFXConformance(PdfWriter.PDFX32002);
		
		document.open();
        cb = writer.getDirectContent();
        
		PdfImportedPage page1 = writer.getImportedPage(backgroundReader, 1);
		
		document.setMargins(0,0,0,0);
		cb.addTemplate(page1,0,0);
		
		for (int i=start;i<=stop;i++) {
			//addAlignment(i+"");
			addSerialNumber(Util.AddleadingZeros(i+"",geom.getNoDigitsInSerial()),i+"");			
			addContests(i);

			if (i!=stop) {
				document.newPage();
				cb.addTemplate(page1,0,0);
			}
		}
		
		addTextField(new Rectangle(300,300,450,350),"vote",helv,18,"Vasile");
		document.close();		
	}
	
	protected void addContests(int printedSerial) {    	
    	int allSymbolsPos=0;
    	int noRanks=0;
        //step3 - for each race, add the placeholders for symbols
        for (int qno=0;qno<qs.length;qno++) {        	
        	//step 3.1 add the top symbols
        	//for each candidate
        	allSymbolsPos=0;
        	for (int c=0;c<qs[qno].getAnswers().size();c++) {                
            	//step 3.2 for each row (rank), add the bottom symbols and the orange (top, bottom and both)
                noRanks=1;
                if (qs[qno].getTypeOfAnswer().compareTo(Constants.RANK)==0)
                	noRanks=qs[qno].getMax(); 
        		for (int rank=0;rank<noRanks;rank++) {
                	rect = geom.getTop(qno+"",rank+"",c+"");
                	
                	addCaptcha(cb,rect,serialFont,symbolTopFontSize,symbolColor,allCodes.get(printedSerial).get(qno).get(allSymbolsPos));

                	allSymbolsPos++;
                }
        	}
        }
    }

	public static void addCaptcha(PdfContentByte cb, Rectangle possition, BaseFont font, int XXXfontSize,Color textColor,String text) {
		//draw a white background
		int fontSize=getFontSize(possition,text,font);
//System.out.println(possition.getLeft());		
		cb.saveState();
		cb.setColorFill(Color.WHITE);
		cb.setColorStroke(Color.WHITE);
		cb.rectangle(possition.getLeft(), possition.getBottom(), possition.getWidth(), possition.getHeight());
		cb.fillStroke();
		cb.restoreState();
		
		float rectLeft=possition.getLeft();
		float rectBottom=possition.getBottom();
	
		try {
			mc.make(text, captchaTempImageFile);
            Image jpg = Image.getInstance(captchaTempImageFile);
            jpg.scaleAbsolute(possition.getWidth(), possition.getHeight());
            jpg.setAbsolutePosition(rectLeft,rectBottom);
            cb.addImage(jpg);
		} catch (Exception e) {
			e.printStackTrace();
		}
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

	protected void addSerialNumber(String serial,String prefixForFieldName) {
    	serialFontSize=getFontSize(geom.getSerialTop("0"), serialFont);
    	
        //step3 - add the serial number in latin
    	Rectangle r=null;
        for (int i=0;i<geom.getNoDigitsInSerial();i++) {
        	r=geom.getSerialTop(i+"");
        	
        	drawWhiteRectangle(r);
        	
        	pdfFormField = makeText(
        			r, 
        			prefixForFieldName+"serialTop_"+i,
            		serialFont,
            		serialFontSize,
            		Character.toString(serial.charAt(i)));
            writer.addAnnotation(pdfFormField);
        }
    }	

	public void addTextField(Rectangle possition, String name, BaseFont font, int fontSize,String defaultText) {
		
		cb.beginText();
		cb.setFontAndSize(font, 18);
		//cb.setColorFill(Color.BLACK);
		cb.showTextAligned(PdfContentByte.ALIGN_LEFT, "Enter Code:",possition.getLeft()-200,possition.getBottom(),0);
		cb.endText();
		
		TextField tf = new TextField(writer, new Rectangle(possition.getLeft(),possition.getBottom(),possition.getRight(),possition.getTop()), "vote");
        //tf.setBackgroundColor(Color.LIGHT_GRAY);
        tf.setBorderColor(Color.BLACK);
        tf.setBorderWidth(2);
        tf.setBorderStyle(PdfBorderDictionary.STYLE_BEVELED);
        tf.setText("");
        tf.setAlignment(Element.ALIGN_LEFT);
        //tf.setOptions(TextField.MULTILINE | TextField.REQUIRED);
        //tf.setRotation(90);
        PdfFormField field=null;
		try {
			field = tf.getTextField();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (DocumentException e) {
			e.printStackTrace();
		}
        writer.addAnnotation(field);
        
        //add the cast button
        possition.setBottom(possition.getBottom()-100);
        possition.setTop(possition.getTop()-100);
        cb.setFontAndSize(font, 40);
        pdfFormField=makeButtonFinishVoting(possition, "finishVoting", "Cast Ballot");
    	pdfFormField.setAction(PdfAction.javaScript(
    			"app.mailMsg(true, \"poste@gwu.edu\", \"\", \"\", \"PunchScan Ballot\", getField(\"vote\").value);",
    			writer));
        writer.addAnnotation(pdfFormField);              	                                                

/*        
        PdfFormField pushbutton = PdfFormField.createPushButton(writer);
        PdfAppearance normal = cb.createAppearance(100,100);
        
        
        normal.setColorFill(Color.RED);
        normal.rectangle(0,0,100,100);
        normal.fill();
        
        normal.setColorFill(Color.GRAY);
        
        normal.setFontAndSize(helv, 12);
        normal.moveTo(0,0);
        normal.beginText();
        normal.showTextAligned(PdfContentByte.ALIGN_LEFT, "Cast Ballot", 5, 10, 0);
        normal.endText();       
        pushbutton.setFieldName(name);
        pushbutton.setAppearance(PdfAnnotation.APPEARANCE_NORMAL, normal);
        possition.setBottom(possition.getBottom()-100);
        pushbutton.setWidget(possition, PdfAnnotation.HIGHLIGHT_PUSH);
        pushbutton.setAction(PdfAction.javaScript(
        		"app.mailMsg(true, \"poste@gwu.edu\", \"\", \"\", \"PunchScan Ballot\", getField(\"vote\").value);",
        		writer));
        writer.addAnnotation(pushbutton);              	                                                
*/
	}
	
    public static void main(String[] args) throws Exception {
    	String dir=InputConstants.publicFolder;
		ElectionSpecification es=new ElectionSpecification(dir+"ElectionSpec.xml");
		BallotGeometry geom=new BallotGeometry(dir+"geometry.xml");

		String background=dir+"PepsiCoke.pdf";
		String pathToPrintsFile=dir+"MeetingTwoPrints.xml";
		
    	BallotMaker pbm=new BallotMaker(es,geom);
    	pbm.init(background,pathToPrintsFile);
    	pbm.makePrintableBallots(dir+"", 0,0);    	
    }
}