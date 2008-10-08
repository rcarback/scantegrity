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
		
		m3in.write(new ElectionSpecification(InputConstants.ElectionSpec),InputConstants.ClearTextBallots,InputConstants.MeetingTwoPrints,InputConstants.SerialMap);
		
		//run meeting three
		start = System.currentTimeMillis();
		MeetingThree m3=new MeetingThree(InputConstants.MeetingOneIn);

		m3.init(InputConstants.MK1,InputConstants.MK2);
		
		m3.clearVotesToCodedVotes(InputConstants.ClearTextBallots,InputConstants.MeetingThreeIn, InputConstants.SerialMap);
		m3.go(InputConstants.MeetingThreeIn,InputConstants.MeetingThreeOut);		

		System.out.println("meting three took "+(System.currentTimeMillis()-start)/1000+" seconds");				
	}
	
	public static void main(String[] args) throws Exception {
		Test test=new Test();
		test.testAll();
	}


}
