package temp;

import java.io.IOException;

import com.lowagie.text.pdf.AcroFields;
import com.lowagie.text.pdf.PdfReader;

public class ReadImageFromPdf {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		PdfReader reader = new PdfReader("Elections/Ottawa/faxes/MR1179796353947.pdf");
		AcroFields fields=reader.getAcroFields();
		System.out.println(fields.getFields());
	}

}
