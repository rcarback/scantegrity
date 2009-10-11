package software.engine.scantegrity;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Vector;

import org.gwu.voting.standardFormat.GenerateDummyBallots;
import org.gwu.voting.standardFormat.Tally;
import org.gwu.voting.standardFormat.basic.Question;
import org.gwu.voting.standardFormat.basic.Section;
import org.gwu.voting.standardFormat.electionSpecification.ElectionSpecification;
import org.gwu.voting.standardFormat.filledInBallot.FilledInBallot;
import org.gwu.voting.standardFormat.results.Results;
import org.w3c.dom.Document;

import software.common.InputConstants;
import software.common.Prow;
import software.common.Util;
import software.engine.RowPermutation;

public class MeetingThreeInScantegrity extends software.engine.ioExample.MeetingThreeIn {
	
	public MeetingThreeInScantegrity() {
		super();
	}
	
	public MeetingThreeInScantegrity(String confFile) throws Exception {
		super(confFile);
	}

	public MeetingThreeInScantegrity(Document doc) throws Exception {
		super(doc);
	}

	public Results write(ElectionSpecification es, String m2in, String m3in) throws Exception {		
		OutputStream fos = new BufferedOutputStream(new FileOutputStream(m3in));
		fos.write("<xml>\n".getBytes());
		fos.write("\t<print>\n".getBytes());	
		
		//transform from standard ballots to inputs to meeting three

        Prow.setOperation(Prow.Operation.NONE);

		GenerateDummyBallots gdb = new GenerateDummyBallots(es);

		//I need a hashtable that maps section Ids to ordered questions
		Hashtable<String, Question[]> sidToQs = new Hashtable<String, Question[]>();
		
		Tally tally = new Tally(es);
		Section[] ss = es.getOrderedSections();
		for (int i=0;i<ss.length;i++) {
			sidToQs.put(ss[i].getId(),ss[i].getOrderedQuestions());
		}

		Question[] qs = es.getOrderedQuestions();
		int maxNoAnswers = 0;
		for (int qno = 0; qno < qs.length; qno++) {			
			maxNoAnswers+=qs[qno].getMax();
		}
		
		
		String serialPrefix=Util.getWardFromElectionConstant(new String(c));
		
		//generate the barcode serial number
		String[] barCodeSerials=RowPermutation.generateBarcodeSerialNumbers(mk1, mk2, c, serialPrefix, 100000, 999999, numberOfBallots);

		prows=Util.readPRows(m2in);
		
		Vector<String> printedBallots=new Vector<String>();
		for (int i=0,noBallots=0;i<numberOfBallots && noBallots<numberOfBallots * InputConstants.PercentVoted;i++) {
			if (prows.get(i)==null) {
				printedBallots.add(barCodeSerials[i]);
				noBallots++;
			}
		}
		
		Collections.shuffle(printedBallots);

		byte[] vote=new byte[maxNoAnswers];		
		for (String barcodeSerial:printedBallots) {
						
			//read a filledInBallot and go through the questions
			FilledInBallot fib = gdb.generateOneBallot();
			
			
            //FileOutputStream fosfib = new FileOutputStream(i+".xml");
			//fosfib.write(fib.toString().getBytes());
			//fosfib.close();			
			
			
			tally.tally(fib);
			//go through each question
			int topPos = 0;
			int votePos=0;
			for (int s=0;s<ss.length;s++) {
				Question[] qsSpec = sidToQs.get(ss[s].getId());
				Section fibSection = (Section)fib.getSections().get(ss[s].getId());				
				for (int qpos = 0;qpos < qsSpec.length;qpos++) {
					Question fibQuestion = (Question)fibSection.getQuestions().get(qsSpec[qpos].getId());
					
					byte[] as = getAnswerPositions(fibQuestion.getAnswers());

					int previousVotePos=votePos;
					for (int apos = 0;apos<qsSpec[qpos].getMax();apos++) {
						try {
							vote[votePos++]=as[apos];
						} catch (ArrayIndexOutOfBoundsException e) {
							vote[--votePos]=-1;
						}
					}
					
					//permute the order of the answers a little
					int newPos=-1;
					byte temp=0;
					for (int j=previousVotePos;j<votePos;j++) {
						newPos=(int)(Math.random()*qsSpec[qpos].getMax());
						temp=vote[j];
						vote[j]=vote[previousVotePos+newPos];
						vote[previousVotePos+newPos]=temp;
					}
					
					topPos+=qsSpec[qpos].getAnswers().size();
				}
			}
			Prow prow=new Prow();
			prow.setId(Integer.parseInt(barcodeSerial.substring(barcodeSerial.lastIndexOf("-")+1)));
			prow.setChosenPage(Prow.ChosenPage.NONE);
			prow.setVote(vote);
			fos.write(("\t\t"+prow.toString()).getBytes());			
		}
		fos.write("\t</print>\n".getBytes());
		fos.write("</xml>".getBytes());
		fos.close();
		return tally.getResults();
	}
}
