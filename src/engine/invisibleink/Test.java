/*
 * Scantegrity - Successor to Punchscan, a High Integrity Voting System
 * Copyright (C) 2006  Richard Carback, David Chaum, Jeremy Clark, 
 * Aleks Essex, Stefan Popoveniuc, and Jeremy Robin
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package software.engine.invisibleink;

import software.auditor.CheckBallotDecryption;
import software.auditor.CheckTableCorrectness;
import software.auditor.invisibleink.CheckCommitmentsToSymbols;
import software.common.InputConstants;
import software.engine.MeetingFour;
import software.engine.MeetingOne;
import software.engine.ioExample.MeetingOneIn;
import software.engine.ioExample.MeetingTwoIn;

/**
 * Runs a full election: 4 meetings and 2 auditors
 * @author stefan
 *
 */
public class Test {

	public boolean withAudit=true;
	
	public void testMeetingOne() throws Exception {
		String[] arg;
		long start=0;
		
		arg=new String[3];
		arg[0]=InputConstants.MeetingOneIn;		
		arg[1]=InputConstants.MeetingOneOut;
		
		//meeting one
		//create input
		MeetingOneIn.write(InputConstants.ElectionSpec,InputConstants.NoBallots,InputConstants.NoDs,InputConstants.C, InputConstants.MeetingOneIn);
		//run meeting one
		start = System.currentTimeMillis();
		MeetingOne.main(arg);
		System.out.println("meting one took "+(System.currentTimeMillis()-start)/1000+" seconds");				
	}
	
	public void testMeetingTwo() throws Exception {
		String[] arg;
		long start=0;
		
		//meeting two
		//Create Input
		MeetingTwoIn.write(InputConstants.PercentCheck,InputConstants.NoBallots,InputConstants.MeetingTwoIn);
		//run meeting two
		arg=new String[8];
		arg[0]=InputConstants.MeetingOneIn;
		arg[1]=InputConstants.MeetingTwoIn;
		arg[2]=InputConstants.MeetingTwoOut;
		arg[3]=InputConstants.MeetingTwoPrints;
		arg[5]=InputConstants.SerialMap;
		arg[6]=InputConstants.Codes;
		arg[7]=InputConstants.MeetingTwoCodesCommitments;
		start = System.currentTimeMillis();
		MeetingTwo.main(arg);
		System.out.println("meting two took "+(System.currentTimeMillis()-start)/1000+" seconds");
		
	}
	
	public void testMeetingThree() throws Exception {
		long start=0;

	//TODO check that the votes in are the votes out !!!
	//meeting three
	//create input
//	MeetingThreeInScantegrity m3in=new MeetingThreeInScantegrity(InputConstants.MeetingOneIn);
//	m3in.init(InputConstants.MK1,InputConstants.MK2);
//	m3in.write(new ElectionSpecification(InputConstants.ElectionSpec),InputConstants.ClearTextBallots,InputConstants.MeetingTwoPrints,InputConstants.SerialMap);
	
	//run meeting three
	start = System.currentTimeMillis();
	MeetingThree m3=new MeetingThree(InputConstants.MeetingOneIn);

	m3.init(InputConstants.MK1,InputConstants.MK2);
	
	m3.clearVotesToCodedVotes(InputConstants.ClearTextBallots,InputConstants.MeetingThreeIn, InputConstants.SerialMap);
	m3.revealMarkedSymbols(InputConstants.MeetingThreeIn, InputConstants.Codes, InputConstants.MeetingThreeOutCodes);
	m3.go(InputConstants.MeetingThreeIn,InputConstants.MeetingThreeOut);		

	System.out.println("meting three took "+(System.currentTimeMillis()-start)/1000+" seconds");				
				
	}
	
	public void testMeetingFour() throws Exception {
		String[] arg;
		long start=0;
		//meeting four
		//create input
		//MeetingFourIn.write(InputConstants.MeetingThreeOut,InputConstants.MeetingFourIn);
		////run meeting four
		arg=new String[3];
		arg[0]=InputConstants.MeetingOneIn;
		arg[1]=InputConstants.MeetingFourIn;
		arg[2]=InputConstants.MeetingFourOut;
		start = System.currentTimeMillis();
		MeetingFour.main(arg);
		System.out.println("meting four took "+(System.currentTimeMillis()-start)/1000+" seconds");		
	}
	
	public void testPreElectionAudit() throws Exception {
		String[] arg;
		long start=0;

		arg=new String[4];
		arg[0]=InputConstants.MeetingOneIn;
		arg[1]=InputConstants.MeetingOneOut;
		arg[2]=InputConstants.MeetingTwoIn;
		arg[3]=InputConstants.MeetingTwoOut;
		start = System.currentTimeMillis();
		CheckTableCorrectness.main(arg);
		System.out.println("audit one took "+(System.currentTimeMillis()-start)/1000+" seconds");		
	}
	
	public void testCodesCommitments(boolean testContestedCodes) throws Exception {
		String[] arg;
		long start=0;
		/*
		 * 0 - m1in
		 * 1 - m2commit
		 * 2 - m3codes
		 * 3 - m3in
		 * 4 - serialmap
		*/
		if (testContestedCodes)
			arg=new String[3];
		else
			arg=new String[5];
		arg[0]=InputConstants.MeetingOneIn;
		arg[1]=InputConstants.MeetingTwoCodesCommitments;
		if (testContestedCodes)
			arg[2]=InputConstants.ReplyToContestedCodes;
		else
			arg[2]=InputConstants.MeetingThreeOutCodes;
		if (!testContestedCodes) {
			arg[3]=InputConstants.MeetingThreeIn;
			arg[4]=InputConstants.SerialMap;
		}
		start = System.currentTimeMillis();
		CheckCommitmentsToSymbols.main(arg);
		System.out.println("audit code commitments took "+(System.currentTimeMillis()-start)/1000+" seconds");		
	}

	public void testChallengedCodes() throws Exception {
		//run meeting three
		long start = System.currentTimeMillis();
		MeetingThree m3=new MeetingThree(InputConstants.MeetingOneIn);

		m3.init(InputConstants.MK1,InputConstants.MK2);
		m3.revealContestedSymbols(InputConstants.ContestedCodes, InputConstants.Codes, InputConstants.ReplyToContestedCodes, InputConstants.BallotsToBeRetrievedFromTheWarehouse);

		System.out.println("meting three took "+(System.currentTimeMillis()-start)/1000+" seconds");				
	}


	public void testPostElectionAudit() throws Exception {
		String[] arg;
		long start=0;

		arg=new String[6];
		arg[0]=InputConstants.MeetingOneIn;
		arg[1]=InputConstants.MeetingOneOut;
		arg[2]=InputConstants.MeetingThreeIn;
		arg[3]=InputConstants.MeetingThreeOut;
		arg[4]=InputConstants.MeetingFourIn;
		arg[5]=InputConstants.MeetingFourOut;
		start = System.currentTimeMillis();
		CheckBallotDecryption.main(arg);
		System.out.println("audit two took "+(System.currentTimeMillis()-start)/1000+" seconds");
	}

	public void testAll() throws Exception {
		testMeetingOne();
		
		testMeetingTwo();
				
		//run the first audit
		if (withAudit) {
			testPreElectionAudit();
		}

		testMeetingThree();
		
		testCodesCommitments(false);
		
		testChallengedCodes();
		
		testCodesCommitments(true);
					
		testMeetingFour();
		
		//run the second audit
		if (withAudit) {
			testPostElectionAudit();
		}
				
	}
	
	public static void main(String[] args) throws Exception {
		Test test=new Test();
		test.testAll();
		//InputConstants.setTempDir("Elections/POLK COUNTY, FLORIDA NOVEMBER 7, 2000/version4/bottom/");
		//test.testAll();
	}

}
