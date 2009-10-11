//package temp;
/*
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.TreeMap;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.parsers.SAXParser;

import software.common.InputConstants;
import software.common.MeetingReaderSax;
import software.common.Prow;
import software.common.Util;
import software.engine.MeetingTwo;
import software.engine.RowPermutation;
import software.engine.ioExample.MeetingTwoIn;
import software.engine.ioExample.Test;

public class InternetVoting {

	String pinsDirPrefix="Elections/Internet/PINs";
	String[] pinsDir=null;
	String pinsConstantPrefix="Pins";
	byte[][] pinsConstant=null;
	String mkPinsPrefix="PinsKey";
	byte[][] mk1Pins=null;
	byte[][] mk2Pins=null;
	
	String confirmationCodesDir="Elections/Internet/ValidationCodes/";
	String confirmationCodesConstant="ValidationCodes ";
	byte[] mk1CC="confirmationCod0".getBytes();
	byte[] mk2CC="confirmationCod1".getBytes();
	
	int[][] ccToPins=null;
	
	String constantForChoosingThePrintedConfirmationCodes="CCPrinted";
	byte[] mk1ForChoosingThePrintedConfirmationCodes="CCPrinted0".getBytes();
	byte[] mk2ForChoosingThePrintedConfirmationCodes="CCPrinted1".getBytes();
	
	int noVoters=InputConstants.NoBallots/10;
	
	int noElections=7;
	
	public InternetVoting() {
		pinsDir=new String[noElections];
		pinsConstant=new byte[noElections][];
		mk1Pins=new byte[noElections][];
		mk2Pins=new byte[noElections][];
		
		for (int i=0;i<noElections;i++) {
			pinsDir[i]=pinsConstantPrefix+i+"/";
			pinsConstant[i]=Util.AddleadingZeros(pinsConstantPrefix+i, 16).getBytes();
			mk1Pins[i]=Util.AddleadingZeros(mkPinsPrefix+"1 "+i, 16).getBytes();
			mk2Pins[i]=Util.AddleadingZeros(mkPinsPrefix+"2 "+i, 16).getBytes();
		}
		
		ccToPins=new int[noElections][];
	}
	
	
	public void go() throws Exception {
		
		Test test=new Test();
		
		//1.	EA runs meeting one on CC
		InputConstants.setTempDir(confirmationCodesDir);
		InputConstants.C=confirmationCodesConstant.getBytes();
		InputConstants.MK1=mk1CC;
		InputConstants.MK2=mk2CC;
		test.testMeetingOne();	
		
		
		//2.	EA runs meeting one on each PIN election.
		for (int i=0;i<noElections;i++) {
			InputConstants.setTempDir(pinsDir[i]);
			InputConstants.C=pinsConstant[i];
			InputConstants.MK1=mk1Pins[i];
			InputConstants.MK2=mk2Pins[i];
			test.testMeetingOne();
		}
		
		//3.	noElections mappings are produced, from confirmation numbers to PINs
		int[] confirmationCodesPerm=RowPermutation.permuteD1D5(
				new SecretKeySpec(mk1CC,"AES"),
				new SecretKeySpec(mk2CC,"AES"),
				confirmationCodesConstant.getBytes(),
				0,InputConstants.NoBallots-1,(byte)0);
		Util.print(confirmationCodesPerm);
		
		for (int i=0;i<noElections;i++) {
			SecretKeySpec mk1 = new SecretKeySpec(mk1Pins[i],"AES");
			SecretKeySpec mk2 = new SecretKeySpec(mk2Pins[i],"AES");
	
			int[] pinPerm = RowPermutation.permuteD1D5(mk1,mk2,pinsConstant[i],0,InputConstants.NoBallots-1,(byte)0);
			Util.print(pinPerm);
			int[] pinPermInv=Util.getInverse(pinPerm);
			
			ccToPins[i]=new int[pinPerm.length];
			for (int j=0;j<pinPerm.length;j++) {
				ccToPins[i][j]=pinPermInv[confirmationCodesPerm[j]];
			}
		}
		
		//4.	The confirmation numbers are shuffled and paired. 
		int[] pinPerm = RowPermutation.permuteD1D5(
				new SecretKeySpec(mk1ForChoosingThePrintedConfirmationCodes,"AES"),
				new SecretKeySpec(mk2ForChoosingThePrintedConfirmationCodes,"AES"),
				constantForChoosingThePrintedConfirmationCodes.getBytes(),
				0,InputConstants.NoBallots-1,(byte)0);

		//5.	For the 10% of the pairs (20% of the confirmation numbers) chosen at random, 
		//registration pages are created. Each registration page has part A and part B, 
		//each representing one valid registration ticket = one confirmation code and 
		//noElections PINs and transcodes.
		for (int i=0;i<2*noVoters;i++) {

			
			//i and i+1 are in the same pair
		}
		
		//5.	Print the tickets using the private mapping.
		InputConstants.setTempDir(pinsDir);
		InputConstants.C=pinsConstant;
		//just produce the printing, without any checking
		MeetingTwoIn.write(0,InputConstants.NoBallots,InputConstants.MeetingTwoIn);
		//run meeting two
		MeetingTwo m2 = new MeetingTwo(InputConstants.MeetingOneIn);
		m2.init(InputConstants.MK1,InputConstants.MK2);
		m2.go(InputConstants.MeetingTwoIn, InputConstants.MeetingTwoOut);
		m2.makePrints(InputConstants.tempDir+"MeetingOnePrints.xml",InputConstants.tempDir+"__temp.xml");
		
		
		//6.	Some voters are going to choose some tickets to open fully. This will check the printing of the tickets and the fact that the tables are well formed. Meeting two will be run on the PINs. Then meeting two will be run on the Confirmation Codes, after the results table for the Confirmation codes is mapped back to the Print table (to create the challenges)
		InputConstants.setTempDir(pinsDir);
		InputConstants.C=pinsConstant;
		test.testMeetingTwo();
		
		MeetingReaderSax mr = new MeetingReaderSax();
        try {
            SAXParser saxParser = Util.newSAXParser();
            saxParser.parse( new File(InputConstants.MeetingTwoIn), mr);
        } catch (Throwable t) {
            t.printStackTrace();
        }
        
        while (!mr.isDoneParsing()) {
        	Thread.sleep(100);
        }        
        TreeMap<Integer,Prow> pinsPrintingAudit = mr.getProws();
        
		InputConstants.setTempDir(confirmationCodesDir);
		InputConstants.C=conformationCodesConstant;
		OutputStream fos = new BufferedOutputStream(new FileOutputStream(InputConstants.MeetingTwoIn));
		fos.write("<xml>\n".getBytes());
		fos.write("\t<challenges>\n".getBytes());
		fos.write("\t\t<print>\n".getBytes());
				for (int pin:pinsPrintingAudit.keySet()) {
		fos.write(("\t\t\t<row id=\""+pinsToConfirmationCodes[pin]+"\"/>\n").getBytes());				
				}
		fos.write("\t\t</print>\n".getBytes());
		fos.write("\t</challenges>\n".getBytes());
		fos.write("</xml>\n".getBytes());			
		fos.close();
		String[] arg=new String[5];
		arg[0]=InputConstants.MeetingOneIn;
		arg[1]=InputConstants.MeetingTwoIn;
		arg[2]=InputConstants.MeetingTwoOut;
		arg[3]=InputConstants.MeetingTwoPrints;
		arg[4]=InputConstants.SerialMap;
		MeetingTwo.main(arg);

		//run the audits
		InputConstants.setTempDir(pinsDir);
		InputConstants.C=pinsConstant;
		test.testPreElectionAudit();
		
		InputConstants.setTempDir(confirmationCodesDir);
		InputConstants.C=conformationCodesConstant;
		test.testPreElectionAudit();		
		
		//7.	The Print table for the Confirmation Codes is filled in during the registration period. 
		InputConstants.setTempDir(confirmationCodesDir);
		InputConstants.C=conformationCodesConstant;
		test.testMeetingThree();
		
		//8.	Election day comes and the Print table for the PINs is filled in.
		InputConstants.PercentVoted=1-InputConstants.PercentCheck;
		InputConstants.setTempDir(pinsDir);
		InputConstants.C=pinsConstant;
		test.testMeetingThree();
		
		InputConstants.setTempDir(pinsDir);
		InputConstants.C=pinsConstant;
		test.testMeetingFour();
		
		//2.	Run meeting one on Confirmation Codes
		InputConstants.setTempDir(confirmationCodesDir);
		InputConstants.C=conformationCodesConstant;
		test.testMeetingFour();				
		
		//run the audits
		InputConstants.setTempDir(pinsDir);
		InputConstants.C=pinsConstant;
		test.testPostElectionAudit();
		
		InputConstants.setTempDir(confirmationCodesDir);
		InputConstants.C=conformationCodesConstant;
		test.testPostElectionAudit();		

	}
	
	public static void main(String[] args) throws Exception {
		InternetVoting iv=new InternetVoting();
		iv.go();
	}

}
*/