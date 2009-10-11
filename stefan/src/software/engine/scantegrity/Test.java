package software.engine.scantegrity;

import org.gwu.voting.standardFormat.electionSpecification.ElectionSpecification;

import software.common.InputConstants;
import software.engine.scantegrity.MeetingThree;

public class Test  extends software.engine.ioExample.Test {
	
	public void testMeetingThree() throws Exception {
		long start=0;

		//TODO check that the votes in are the votes out !!!
		//meeting three
		//create input
		MeetingThreeInScantegrity m3in=new MeetingThreeInScantegrity(InputConstants.MeetingOneIn);
		m3in.init(InputConstants.MK1,InputConstants.MK2);
		
		m3in.write(new ElectionSpecification(InputConstants.ElectionSpec),InputConstants.BallotsDir,/*InputConstants.MeetingTwoPrints,*/InputConstants.SerialMap);
		
		//run meeting three
		start = System.currentTimeMillis();
		MeetingThree m3=new MeetingThree(InputConstants.MeetingOneIn);

		m3.init(InputConstants.MK1,InputConstants.MK2);
		
		m3.clearVotesToCodedVotes(InputConstants.BallotsDir,InputConstants.MeetingThreeIn, InputConstants.SerialMap);
		m3.go(InputConstants.MeetingThreeIn,InputConstants.MeetingThreeOut);		

		System.out.println("meting three took "+(System.currentTimeMillis()-start)/1000+" seconds");				
	}
	
	public static void main(String[] args) throws Exception {
		Test test=new Test();
		test.testAll();
	}


}
