/*
 * $Id: FormTextField.java 1742 2005-05-09 11:52:51Z blowagie $
 * $Name$
 *
 * This code is part of the 'iText Tutorial'.
 * You can find the complete tutorial at the following address:
 * http://itextdocs.lowagie.com/tutorial/
 *
 * This code is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 * itext-questions@lists.sourceforge.net
 */

package temp;


import java.awt.Color;

import java.io.FileOutputStream;


import com.lowagie.text.Document;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.GrayColor;
import com.lowagie.text.pdf.PdfAnnotation;
import com.lowagie.text.pdf.PdfAppearance;
import com.lowagie.text.pdf.PdfBorderDictionary;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfFormField;
import com.lowagie.text.pdf.PdfImportedPage;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfWriter;

/**
 * Generates an Acroform with a TextField
 * @author blowagie
 */
public class FormTextField {
    /**
     * Generates an Acroform with a TextField
     * @param args no arguments needed here
     */
    public static void main(String[] args) throws Exception {
        
        System.out.println("Textfield");
        
        // step 2:

        // step 1: creation of a document-object
        String background="Elections/POLK COUNTY, FLORIDA NOVEMBER 7, 2000/page1/xxx.pdf";
        PdfReader backgroundReader = new PdfReader(background);
		Rectangle pdfPageSize = backgroundReader.getPageSize(1);
		//double scalling = pdfPageSize.getWidth() / ballotMap.getW();
		
		Document document = new Document(pdfPageSize);
        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream("textfield.pdf"));

        // step 4:
        BaseFont helv = BaseFont.createFont("Helvetica", BaseFont.CP1252, BaseFont.EMBEDDED);

                
		PdfImportedPage page1 = writer.getImportedPage(backgroundReader, 1);
		document = new Document(pdfPageSize);
		
        PdfContentByte cb = writer.getDirectContent();
        document.open();

        cb.addTemplate(page1,0,0);
    	document.setMargins(0,0,0,0);        	

        
            cb.moveTo(0, 0);
            String text = "Some start text";
            float fontSize = 12;
            Color textColor = new GrayColor(0f);
            PdfFormField field = PdfFormField.createTextField(writer, false, false, 0);
            field.setWidget(new Rectangle(171, 750, 342, 769), PdfAnnotation.HIGHLIGHT_INVERT);
            field.setFlags(PdfAnnotation.FLAGS_PRINT);
            field.setFieldName("ATextField");
            field.setValueAsString(text);
            field.setDefaultValueAsString(text);
            field.setBorderStyle(new PdfBorderDictionary(2, PdfBorderDictionary.STYLE_SOLID));
            field.setPage();
            
            PdfAppearance tp = cb.createAppearance(171, 19);
            PdfAppearance da = (PdfAppearance)tp.getDuplicate();
            da.setFontAndSize(helv, fontSize);
            da.setColorFill(textColor);
            field.setDefaultAppearanceString(da);
            tp.beginVariableText();
            tp.saveState();
            tp.rectangle(2, 2, 167, 15);
            tp.clip();
            tp.newPath();
            tp.beginText();
            tp.setFontAndSize(helv, fontSize);
            tp.setColorFill(textColor);
            tp.setTextMatrix(4, 5);
            tp.showText(text);
            tp.endText();
            tp.restoreState();
            tp.endVariableText();
            field.setAppearance(PdfAnnotation.APPEARANCE_NORMAL, tp);
            writer.addAnnotation(field);
        
        // step 5: we close the document
        document.close();
    }
}