package temp;

import org.w3c.dom.Document;

import software.common.Util;

public class PrintTextFileInJar {

	public PrintTextFileInJar() throws Exception {
		Document doc = Util.GetDocumentBuilder().parse(getClass().getResourceAsStream("DigitMap.xml"));
		System.out.println(doc.getElementsByTagName("digit").getLength());
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		new PrintTextFileInJar();
	}

}
