package temp;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.StringTokenizer;

import org.gwu.voting.standardFormat.Constants;
import org.gwu.voting.standardFormat.basic.Question;
import org.gwu.voting.standardFormat.electionSpecification.ElectionSpecification;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import software.common.Prow;
import software.common.Util;

public class OldPrintsFileToNewPrintsFile {

	Question[] qs=null;
	int noa=0;
	
	public OldPrintsFileToNewPrintsFile(String electionStec) throws Exception {
		ElectionSpecification es=new ElectionSpecification(electionStec);
		qs=es.getOrderedQuestions();
		for (int i=0;i<qs.length;i++) {
			noa+=qs[i].getAnswers().size();
		}
	}
	
	public byte[] OlDPermToNewPerm(String oldPerm) {
		byte[] ret=new byte[noa];
		int retPos=0;
		StringTokenizer st=new StringTokenizer(oldPerm);
		while (st.hasMoreTokens()) {
			for (int i=0;i<qs.length;i++) {
				if (qs[i].getTypeOfAnswer().equals(Constants.ONE_ANSWER)) {
					//it is a shift question, I need to chenge it to a permutation
					int shift=Integer.parseInt(st.nextToken());
					for (int a=0;a<qs[i].getAnswers().size();a++) {
						ret[retPos++]=(byte)((a+shift) % qs[i].getAnswers().size());
					}
				} else {
					for (int a=0;a<qs[i].getAnswers().size();a++) {
						ret[retPos++]=Byte.parseByte(st.nextToken());
					}
				}
			}
		}
		return ret;
	}
	
	/**
	 * @param args
	 */
	public void transform(String oldPrintsFile,String newPrintsFile) throws Exception {
		OutputStream pos = new BufferedOutputStream(new FileOutputStream(newPrintsFile));
		pos.write("<xml>\n".getBytes());
		pos.write("\t<print>\n".getBytes());

		
		Document doc = Util.DomParse(oldPrintsFile);

		Prow.setOperation(Prow.Operation.OPEN_COMMITMENTS);
		NodeList rows=doc.getElementsByTagName("row");
		for (int i=0;i<rows.getLength();i++) {
			Node row=rows.item(i);
			String id=row.getAttributes().getNamedItem("id").getNodeValue();
			
			Prow prow=new Prow();
			prow.setId(Integer.parseInt(id));
			for (Node node=row.getFirstChild();node!=null;node=node.getNextSibling()) {
				if (node.getNodeName().compareTo("top")==0) {
					prow.setPage1(OlDPermToNewPerm(node.getFirstChild().getNodeValue()));
				} else {
					if (node.getNodeName().compareTo("bottom")==0) {
						prow.setPage2(OlDPermToNewPerm(node.getFirstChild().getNodeValue()));
					}
				}
			}
			//prow.setChosenPage(Prow.ChosenPage.BOTH);
			pos.write(prow.toString().getBytes());
		}
		pos.write("\t</print>\n".getBytes());			
		pos.write("</xml>".getBytes());
		pos.close();
	}

	public static void main(String args[]) throws Exception {

		OldPrintsFileToNewPrintsFile lp=new OldPrintsFileToNewPrintsFile("D:\\PunchScan2.0\\PunchScan2.0\\Elections\\CPSR 2006 mock\\ElectionSpec.xml");
		lp.transform("D:\\PunchScan2.0\\PunchScan2.0\\Elections\\CPSR 2006 mock\\public\\Prints.xml", "D:\\PunchScan2.0\\PunchScan2.0\\Elections\\CPSR 2006 mock\\public\\MeetingTwoPrints.xml");
	}
}
