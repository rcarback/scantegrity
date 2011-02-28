package org.scantegrity.authoring.invisibleink;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.SecureRandom;
import java.util.Hashtable;

import javax.imageio.ImageIO;

import org.scantegrity.common.ballotstandards.electionSpecification.ElectionSpecification;

import com.lowagie.text.DocumentException;
import com.lowagie.text.Image;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfContentByte;

import org.scantegrity.common.BallotGeometry;
import org.scantegrity.common.BallotRow;
import org.scantegrity.common.InputConstants;
import org.scantegrity.common.Util;
import org.scantegrity.common.InvisibleInkFactory;

public class PrintableBallotMakerWithBarcodes extends PrintableBallotMaker {
	
	static InvisibleInkFactory iif=null;
	Hashtable<String, Image> textToImage=new Hashtable<String, Image>();
	
	public PrintableBallotMakerWithBarcodes(ElectionSpecification es, BallotGeometry geom) throws Exception {
		super(es, geom);
		
		try {
			SecureRandom c_csprng = SecureRandom.getInstance("SHA1PRNG");
			iif=new InvisibleInkFactory("Tenacity HR192", 144, 10, c_csprng);
/*Rick's settings			
			float[] minMaskColor={0.0f, 0.0f, 0.0f, 0.0f};
			iif.setMinMaskColor(minMaskColor);
			
			float[] maxMaskColor={0.0f, 0.0f, 0.0f, 0.0f};
			iif.setMaxMaskColor(maxMaskColor);
			
			float[] minFontColor={0.4f, 0.0f, 0.65f, 0.0f};
			iif.setMinFontColor(minFontColor);
			
			float[] maxFontColor={0.65f, 0.0f, 0.85f, 0.0f};
			iif.setMaxFontColor(maxFontColor);
			
			float[] minBgColor={0.0f, 0.6f, 0.65f, 0.0f};
			iif.setMinBgColor(minBgColor);
			
			float[] maxBgColor={0.0f, 0.7f, 0.85f, 0.0f};
			iif.setMaxBgColor(maxBgColor);
*/
			//David's Settings UMCP
			float[] minFontColor={0.9f, 0.0f, 0.0f, 0.0f};
			iif.setMinFontColor(minFontColor);
			
			float[] maxFontColor={1.0f, 0.0f, 0.0f, 0.0f};
			iif.setMaxFontColor(maxFontColor);
			
			float[] minBgColor={0.0f, 0.9f, 0.0f, 0.0f};
			iif.setMinBgColor(minBgColor);
			
			float[] maxBgColor={0.0f, 1.0f, 0.0f, 0.0f};
			iif.setMaxBgColor(maxBgColor);

			float[] minMaskColor={0.0f, 0.0f, 0.2f, 0.0f};
			iif.setMinMaskColor(minMaskColor);
			
			float[] maxMaskColor={0.0f, 0.0f, 0.6f, 0.0f};
			iif.setMaxMaskColor(maxMaskColor);
			
			
			Integer[] p_vGridSize=new Integer[1];
			p_vGridSize[0]=11;

			Integer[] p_vGridSpace=new Integer[1];
			p_vGridSpace[0]=1;

			Integer[] p_hGridSize=new Integer[1];
			p_hGridSize[0]=11;
			
			Integer[] p_hGridSpace=new Integer[1];
			p_hGridSpace[0]=1;
			
			iif.setGrid(p_vGridSize, p_vGridSpace, p_hGridSize, p_hGridSpace);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	protected void addSerialNumber(String serial,String prefixForFieldName) {
    	serialFontSize=getFontSize(geom.getSerialTop("0"), helv);//serialFont);
    	
        //step3 - add the serial number in latin
    	Rectangle r=null;
        //for (int i=0;i<geom.getNoDigitsInSerial();i++) {
    	
    		BallotRow ballotRow=ballotRows.get(Integer.parseInt(serial));
    	if (true) {
//    		if ( ! mailIn) {
	        	
    		
        	r=geom.getSerialTop("0");
        	drawWhiteRectangle(r);
        	
//System.out.println("Serial="+serial+" ballotRow="+ballotRow);
        	
        	String webSerial=ballotRow.getWebSerial();
        	
        	drawRegularText(webSerial, r, this.helv, 12);
//    		}
			//float width=r.getWidth();

	        	r=geom.getSerialTop("1");
	        	drawWhiteRectangle(r);
	        	String printingKey1=ballotRow.getStubSerial();
	        	
	        	drawRegularText(printingKey1, r, this.helv, 12);
	        	
	        	//addTextCentered(cb, r, this.helv, 8, Color.BLACK,printingKey1);
	
	        	/*
	        	r=geom.getSerialTop("2");
	        	drawWhiteRectangle(r);
	        	String printingKey2=Util.AddleadingZeros(ballotRow.getPassword2(),6);	
	        	addTextCentered(cb, r, serialFont, 8, symbolColor,printingKey2);
	        	*/
    	}
/*        	
        	String printingKey2=Util.AddleadingZeros(ballotRow.getPassword2(),6);
        	r.setLeft(r.getLeft()+width);
        	r.setRight(r.getRight()+width);
        	addTextCentered(cb, r, serialFont, 8, symbolColor,printingKey2);
*/        //}
        

        //add the 2D barcode
        Image barcode=null;
        try {
        	BufferedImage bi=ImageIO.read(new URL("http://chart.apis.google.com/chart?cht=qr&chs=120x120&chl="+ballotRow.getBarcodeSerial()));
  
        	//BufferedImage bi=ImageIO.read(new URL("http://chart.apis.google.com/chart?cht=qr&chs=120x120&chl=123456"));
        	
        	
			barcode=Util.CMYKBufferedImageToCMYKImage(
					Util.RGBBufferedImageToCMYKBufferedImage(bi));
			
			//barcode=Image.getInstance(new URL("http://chart.apis.google.com/chart?cht=qr&chs=120x120&chl="+ballotRow.getBarcodeSerial()));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
		
		Rectangle rect=geom.makeRectangle(geom.getSerialBulletedNode());
		barcode.setAbsolutePosition(rect.getLeft(),rect.getBottom());
		float w=rect.getWidth()*1.05f; 
		float h=rect.getHeight()*1.05f;
		
		//float w=barcode.getWidth(); 
		//float h=barcode.getHeight();
		
		
		try {
			cb.addImage(barcode, Math.max(w,h), 0, 0, Math.max(w,h), rect.getLeft(),rect.getBottom()-0.5f);
		} catch (DocumentException e) {
			e.printStackTrace();
		}
        
    }	
	
	
	public void addTextCentered(PdfContentByte cb, Rectangle possition, BaseFont font, int XXXfontSize,Color textColor,String text) {
		//draw a white background		
		cb.saveState();
		cb.setColorFill(Color.WHITE);
		cb.setColorStroke(Color.WHITE);
		cb.rectangle(possition.getLeft(), possition.getBottom(), possition.getWidth(), possition.getHeight());
		cb.fillStroke();
		cb.restoreState();
		
		if (mailIn) return;
		
		//TODO this may eat too much memory
		Image l_img=textToImage.get(text);
		if (l_img==null) {
			l_img=Util.CMYKBufferedImageToCMYKImage(iif.getBufferedImage("  "+text+"  "));
			textToImage.put(text, l_img);
		}
		
		//System.out.println("Adding image at "+ possition.getLeft()+" "+ possition.getBottom());
		try {
			//cb.addImage(l_img, img.getWidth(), 0, 0, img.getHeight(), possition.getLeft(), possition.getBottom());
			cb.addImage(l_img, possition.getWidth(), 0, 0, possition.getHeight(), possition.getLeft(), possition.getBottom());
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		
	}
	
	private void drawRegularText(String text, Rectangle possition,BaseFont font,int fontSize) {
		cb.beginText();
		cb.setFontAndSize(font,fontSize);
		font.correctArabicAdvance();		
		cb.showTextAligned(PdfContentByte.ALIGN_LEFT, text, possition.getLeft(), possition.getBottom(), 0);
		cb.endText();
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
    	InputConstants.setPublicFolder(InputConstants.publicFolder+"/ward1/");
    	
		ElectionSpecification es=new ElectionSpecification(InputConstants.ElectionSpec);
		BallotGeometry geom=new BallotGeometry(InputConstants.Geometry);

		String background=InputConstants.PdfTemplate;
		String pathToPrintsFile=InputConstants.MeetingTwoPrints;
		
    	PrintableBallotMakerWithBarcodes pbm=new PrintableBallotMakerWithBarcodes(es,geom);
    	//pbm.init(background,pathToPrintsFile);
    	    	
    	int batchSize=10;
    	for (int i=0;i<200;i+=batchSize) {
    		pbm.makePrintableBallots(InputConstants.privateFolder+"", i,i+batchSize-1);
		}
//    	pbm.makePrintableBallots(InputConstants.privateFolder+"", 51,52);
//    	pbm.makePrintableBallots(InputConstants.privateFolder+"", 109,111);
//    	pbm.makePrintableBallots(InputConstants.privateFolder+"", 370,371);
//    	pbm.makePrintableBallots(InputConstants.privateFolder+"", 444,445);
	}

}
