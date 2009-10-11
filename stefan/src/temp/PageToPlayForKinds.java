package temp;

import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.SecureRandom;

import software.authoring.invisibleink.InvisibleInkFactory;
import software.common.Util;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Image;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfGState;
import com.lowagie.text.pdf.PdfWriter;

public class PageToPlayForKinds {

	BaseFont barcodeFont=null;
	
	public PageToPlayForKinds() {
		try {
			barcodeFont=BaseFont.createFont("D:\\PunchScan2.0\\PunchScan2.0\\src\\temp\\TENHB192.TTF", BaseFont.CP1252, BaseFont.EMBEDDED);
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
	
	public static String[] CodesAlphabet={
		"3","4","9",
		"C","H","I","K","L",
		"M","N","T","W"
	};
	
	public static String getRandomCode() {
		String ret="";
		int numberOfLetters=2;
		
		for (int i=0;i<numberOfLetters;i++) {
			ret+=CodesAlphabet[(int)(Math.random()*CodesAlphabet.length)];
		}
		
		return ret;
	}
	
	public static void addOval(PdfContentByte cb, Rectangle possition) {
		if (possition.getHeight()>possition.getWidth()) {
			System.err.println("Cannot draw jelly bean; the height is greater then the widtd for "+possition);
			return;
		}
		float width=1f;
		//float width=0.9f;
		
		//draw a thicker white line
		//float widthWhite=possition.getHeight()*0.22f;
		float widthWhite=possition.getHeight()*0.32f;
		cb.saveState();
		{
			PdfGState state=new PdfGState();
			state.setStrokeOpacity(1f);
			cb.setGState(state);
		}		
		cb.setCMYKColorStrokeF(0,0,0,0);
		cb.setLineWidth(widthWhite);
		cb.roundRectangle(possition.getLeft()-widthWhite/2, possition.getBottom()-widthWhite/2, possition.getWidth()+widthWhite, possition.getHeight()+widthWhite, possition.getHeight()/2+widthWhite/2);
		cb.stroke();
		cb.restoreState();

		//draw the jelly bean itself
		cb.saveState();
		{
			PdfGState state=new PdfGState();
			state.setStrokeOpacity(1f);
			cb.setGState(state);
		}		
		cb.setCMYKColorStrokeF(0,0,0,0);
		cb.setLineWidth(width);
		cb.roundRectangle(possition.getLeft(), possition.getBottom(), possition.getWidth(), possition.getHeight(), possition.getHeight()/2);
		cb.stroke();
		cb.restoreState();

		cb.saveState();
		{/*
			PdfGState state=new PdfGState();
			state.setStrokeOpacity(0.6f);
			cb.setGState(state);
			*/
		}
		cb.setCMYKColorStrokeF(0,0,0,0.5f);
		cb.roundRectangle(possition.getLeft(), possition.getBottom(), possition.getWidth(), possition.getHeight(), possition.getHeight()/2);
		cb.stroke();		
		cb.restoreState();		
	}

	
	public static void addTextCentered(PdfContentByte cb, Rectangle possition, String text,InvisibleInkFactory iif) {
		//draw a white background		
		cb.saveState();
		cb.setColorFill(Color.WHITE);
		cb.setColorStroke(Color.WHITE);
		cb.rectangle(possition.getLeft(), possition.getBottom(), possition.getWidth(), possition.getHeight());
		cb.fillStroke();
		cb.restoreState();
		
		//if (true) return;
		Image l_img=Util.CMYKBufferedImageToCMYKImage(iif.getBufferedImage("  "+text+"  "));
		
		//System.out.println("Adding image at "+ possition.getLeft()+" "+ possition.getBottom());
		try {
			//cb.addImage(l_img, img.getWidth(), 0, 0, img.getHeight(), possition.getLeft(), possition.getBottom());
			cb.addImage(l_img, possition.getWidth(), 0, 0, possition.getHeight(), possition.getLeft(), possition.getBottom());
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		
	}		
	
	public static void main(String[] args) throws Exception {
		
		
		SecureRandom c_csprng = SecureRandom.getInstance("SHA1PRNG");
		InvisibleInkFactory iif=new InvisibleInkFactory("Tenacity HR192", 144, 10, c_csprng);
		
		float[] minMaskColor={0.0f, 0.0f, 0.2f, 0.0f};
		iif.setMinMaskColor(minMaskColor);
		
		float[] maxMaskColor={0.0f, 0.0f, 0.6f, 0.0f};
		iif.setMaxMaskColor(maxMaskColor);
		
		float[] minFontColor={0.9f, 0.0f, 0.0f, 0.0f};
		iif.setMinFontColor(minFontColor);
		
		float[] maxFontColor={1.0f, 0.0f, 0.0f, 0.0f};
		iif.setMaxFontColor(maxFontColor);
		
		float[] minBgColor={0.0f, 0.9f, 0.0f, 0.0f};
		iif.setMinBgColor(minBgColor);
		
		float[] maxBgColor={0.0f, 1.0f, 0.0f, 0.0f};
		iif.setMaxBgColor(maxBgColor);

		
		Integer[] p_vGridSize=new Integer[1];
		p_vGridSize[0]=11;

		Integer[] p_vGridSpace=new Integer[1];
		p_vGridSpace[0]=1;

		Integer[] p_hGridSize=new Integer[1];
		p_hGridSize[0]=11;
		
		Integer[] p_hGridSpace=new Integer[1];
		p_hGridSpace[0]=1;
		
		iif.setGrid(p_vGridSize, p_vGridSpace, p_hGridSize, p_hGridSpace);

		
		
		
		
		
		Rectangle pdfPageSize=new Rectangle(0,0,11*72,8.5f*72);
		Document document = new Document(pdfPageSize);
		File f = new File("TestPageForKids.pdf");
		PdfWriter writer = PdfWriter.getInstance(document,new FileOutputStream(f));
		//writer.setPdfVersion(PdfWriter.VERSION_1_4);
		//writer.setPDFXConformance(PdfWriter.PDFX32002);
		
		document.open();
        document.newPage();
        PdfContentByte cb = writer.getDirectContent();
        
        BaseFont helvetica = BaseFont.createFont("Helvetica", BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
        cb.setFontAndSize(helvetica, 36);

		cb.beginText();
		cb.showTextAligned(Element.ALIGN_CENTER, "Voter Pen Test Pad", 400,(int)(8.5*72-50), 0);
		cb.endText();
		
             
		int noRows=20;
		int noColumns=10;

		int xstart=80;
		int ystart=50;

		int width=(int)(0.4f*72);
		int heigth=(int)(0.17f*72);
		
		int xpace=(int)(2.5f*width);
		int ypace=2*heigth;
		
		PageToPlayForKinds pg=new PageToPlayForKinds();
        cb.setFontAndSize(pg.barcodeFont, 14);
		
		for (int i=0;i<noRows;i++) {
			for (int j=0;j<noColumns;j++) {
				Rectangle possition=new Rectangle(xstart+j*xpace,ystart+i*ypace,
						xstart+j*xpace+width,ystart+i*ypace+heigth);
				String randomCode=getRandomCode();
				System.out.println(randomCode);
				addTextCentered(cb, possition, randomCode, iif);
				addOval(cb, possition);
				
				cb.beginText();
				cb.showTextAligned(Element.ALIGN_CENTER, randomCode, xstart+j*xpace-15,ystart+i*ypace+2, 0);
				cb.endText();
			}
		}
		
		
		document.add(new Paragraph());
		
        document.close();

	}

}
