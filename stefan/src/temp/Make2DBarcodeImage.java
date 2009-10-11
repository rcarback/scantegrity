package temp;

import software.common.InputConstants;

import com.google.zxing.client.j2se.CommandLineRunner;

public class Make2DBarcodeImage {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		String[] argsclr={"--try_harder",InputConstants.publicFolder+"scannes/1.jpg"};
		CommandLineRunner.main(argsclr);
	}

}
