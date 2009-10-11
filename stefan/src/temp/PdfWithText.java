
package temp;


import java.awt.Color;
import java.io.FileOutputStream;
import java.io.IOException;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.PageSize;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfWriter;

/**
 * Generates an Acroform with a PushButton
 * @author blowagie
 */
public class PdfWithText {
    /**
     * Generates an Acroform with a PushButton
     * @param args no arguments needed here
     */
	
    public static void main(String[] args) {
       
    	Color cyan=new Color(0,255,255);
    	Color magenta=new Color(255,255,0);
    	Color yellow=new Color(255,0,255);
    	
        //Document.compress = false;
        // step 1: creation of a document-object
        Document document = new Document(PageSize.A4);
        
        try {
            
            // step 2:
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream("PureCYMK.pdf"));
            // step 3: we open the document
            document.open();
            //document.add(new Paragraph("RGB "+colorprofile));
            
            // step 4:
            PdfContentByte cb = writer.getDirectContent();
                        
            cb.saveState();
    		cb.setColorFill(cyan);//.setCMYKColorFill(0, 0xFF, 0, 0);//
    		cb.setColorStroke(cyan);//setCMYKColorStroke(0, 0xFF, 0, 0);//
    		cb.rectangle(100,100,30,30);
    		cb.fillStroke();
    		cb.restoreState();
    		
            cb.saveState();
    		cb.setColorFill(magenta);//setCMYKColorFill(0, 0, 0xFF, 0);
    		cb.setColorStroke(magenta);//.setCMYKColorStroke(0, 0, 0xFF, 0);
    		cb.rectangle(100,200,30,30);
    		cb.fillStroke();
    		cb.restoreState();

            cb.saveState();
    		cb.setColorFill(yellow);//setCMYKColorFill(0, 0, 0xFF, 0);
    		cb.setColorStroke(yellow);//.setCMYKColorStroke(0, 0, 0xFF, 0);
    		cb.rectangle(100,300,30,30);
    		cb.fillStroke();
    		cb.restoreState();

        }
        catch(DocumentException de) {
            System.err.println(de.getMessage());
        }
        catch(IOException ioe) {            System.err.println(ioe.getMessage());
        }
        
        // step 5: we close the document
        document.close();
    }
}