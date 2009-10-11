package temp;

import java.io.File;

import software.common.Util;

public class PrintAllFolder {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		File dir=new File("D:/PunchScan2.0/PunchScan2.0/Elections/VoComp/PunchScan/pdfBallots/");
		File[] allFiles=dir.listFiles();
		for (int i=0;i<allFiles.length;i++) {
			if (allFiles[i].getName().startsWith("Voted"))
				Util.printPdfSilently(allFiles[i].getAbsolutePath());
		}
	}

}
