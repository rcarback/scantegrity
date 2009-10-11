package temp;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import org.gwu.voting.standardFormat.Tally;
import org.gwu.voting.standardFormat.basic.Answer;
import org.gwu.voting.standardFormat.basic.Question;
import org.gwu.voting.standardFormat.electionSpecification.ElectionSpecification;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import engine.CPProProperties;
import engine.Engine;

import software.common.InputConstants;
import software.common.Prow;
import software.common.Util;

public class ScantegrityToChoicePlusPro {
	
	public static void ScantegrityInputToChoicePlusProInput(ElectionSpecification es,String folderWithClearMarks,String dir) throws Exception {
		Question[] qs = es.getOrderedQuestions();
		
		BufferedOutputStream[] bos=new BufferedOutputStream[qs.length];
		
		for (int i=0;i<qs.length;i++) {
			File file=new File(dir+"Contest_"+i);
			file.mkdirs();
			
			bos[i]=new BufferedOutputStream(new FileOutputStream(file.getAbsolutePath()+"/CPPRO_Contest_"+i+".in"));
			bos[i].write((".TITLE "+es.getId()).getBytes());
			bos[i].write(("\n").getBytes());
			bos[i].write(("\n").getBytes());
			
			bos[i].write((".ELECT 1").getBytes());
			bos[i].write(("\n").getBytes());
			bos[i].write(("\n").getBytes());

			Answer[] answers=qs[i].getOrderedAnswers();
			for (int j=0;j<answers.length;j++) {
				bos[i].write((".CANDIDATE "+j+",\""+answers[j].getId()+"\"").getBytes());
				bos[i].write(("\n").getBytes());
			}
			bos[i].write(("\n").getBytes());
		}
		
		ScantegrityInputToChoicePlusProInput(bos, qs, folderWithClearMarks, dir);
		
		for (int i=0;i<qs.length;i++) {
			bos[i].close();
		}
	}
	
	
	private static void ScantegrityInputToChoicePlusProInput(BufferedOutputStream[] bos,Question[] qs,String folderWithClearMarks,String dir) throws Exception {
		//read one file at a time from the source folder
		File f=new File(folderWithClearMarks);
		if (f.isFile()) {

		} else {
			if (f.isDirectory()) {
				File[] allFiles=f.listFiles();
				for (int i=0;i<allFiles.length;i++) {
					ScantegrityInputToChoicePlusProInput(bos, qs, allFiles[i].getAbsolutePath(), dir);
				}
				return;
			}
		}
		
		//read the first line from each file and treat it like a condensed file
		if (f.getAbsolutePath().endsWith(".cer") || f.getAbsolutePath().endsWith(".log"))
			return;
		Document doc=Util.DomParse(f.getAbsolutePath());
		NodeList ballotNodes=doc.getElementsByTagName("row");
				
		for (int b=0;b<ballotNodes.getLength();b++) {
			Node ballotNode=ballotNodes.item(b);
			Prow prow=new Prow(ballotNode);
			byte[] allPossitions=prow.getVote();
			
			int numberOfAnswers = 0;	
			int maxNumberOfAnswersUntillCurrentQuestion = 0;
			int ppos = 0;
			for (int i=0;i<qs.length;i++) {
				bos[i].write((prow.getId()+") ").getBytes());
				numberOfAnswers = qs[i].getAnswers().size();
				for (int j=0;j<qs[i].getMax();j++) {
					if (allPossitions[maxNumberOfAnswersUntillCurrentQuestion+j]==-1)
						;//do nothing
					else
						bos[i].write((allPossitions[maxNumberOfAnswersUntillCurrentQuestion+j]+"").getBytes());
					bos[i].write((",").getBytes());
				}
				bos[i].write(("\n").getBytes());
				maxNumberOfAnswersUntillCurrentQuestion+=qs[i].getMax();
				ppos+=numberOfAnswers;
			}
		}
	}

	public static void TallyUsingChoicePlusPro(String folderWithClearMarks) {
        CPProProperties.setHomeFileDir("C:\\Program Files\\VSoln\\CPPro\\CPPSource2_3_1");
        CPProProperties.loadProps("ChoicePlus.properties");
		
		Engine engine=new Engine();
		TallyUsingChoicePlusPro(engine, folderWithClearMarks);
	}
	
	private static void TallyUsingChoicePlusPro(Engine engine, String folderWithClearMarks) {	
		File f=new File(folderWithClearMarks);
		if (f.isFile()) {
			
			engine.start(f.getAbsolutePath());
		} else {
			if (f.isDirectory()) {
				File[] allFiles=f.listFiles();
				for (int i=0;i<allFiles.length;i++) {
					if (allFiles[i].isDirectory()&&allFiles[i].getName().startsWith("Contest_")) {
						File[] allFilesInProperFolders=allFiles[i].listFiles();
						TallyUsingChoicePlusPro(allFilesInProperFolders[0].getAbsolutePath());
					}
				}
			}
		}
	}
	
	public static void OpenHTMLReportsInbrowser(String folderWithClearMarks) {
		File f=new File(folderWithClearMarks);
		if (f.isDirectory()) {
			File[] allFiles=f.listFiles();
			for (int i=0;i<allFiles.length;i++) {
				if (allFiles[i].isDirectory()&&allFiles[i].getName().startsWith("Contest_")) {
					String htmlFile=allFiles[i].getAbsoluteFile()+"/Round.htm";
					Util.openURL("file://"+htmlFile);
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}		
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		//String dir=InputConstants.privateFolder;
		//ScantegrityInputToChoicePlusProInput(new ElectionSpecification(InputConstants.ElectionSpec), InputConstants.BallotsDir, dir);
		//TallyUsingChoicePlusPro(dir);
		OpenHTMLReportsInbrowser("D:\\PunchScan2.0\\PunchScan2.0\\Elections\\TakomaWithBarcode");
	}

}
