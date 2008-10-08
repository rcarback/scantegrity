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

package software.auditor.scantegrity;

import java.util.Hashtable;

import software.authoring.scantegrity.ContestSymbols;
import software.common.InputConstants;
import software.common.Util;

//This class is for the Java servlet to serve to the voters on a web page
public class RecordedAsCast {

	Hashtable<String, String> serialToPid=null;
	ContestSymbols cs=null;
	
	static RecordedAsCast instance=null;
	
	public static RecordedAsCast getInstance(String serialMap,String meetingThreeIn, String es) throws Exception {
		if (instance==null) {
			instance=new RecordedAsCast(serialMap,meetingThreeIn,es);
		}
		return instance;	
	}
	
	private RecordedAsCast(String serialMap,String meetingThreeIn, String es) throws Exception {
		serialToPid=Util.SerialToPid(serialMap);
		cs=new ContestSymbols(meetingThreeIn,es, ContestSymbols.alphabet,false);
	}
	
	public String[] getLetters(String serial) throws Exception {
		String pid=serialToPid.get(serial);
		
		return cs.getSelectedSymbols(pid);
	}
	
	public static void main(String args[]) throws Exception {
		RecordedAsCast rac=new RecordedAsCast(InputConstants.SerialMap,InputConstants.MeetingThreeIn,InputConstants.ElectionSpec);
		String letters[]=rac.getLetters(15+"");
		for (int i=0;i<letters.length;i++)
			System.out.println(letters[i]+";");
	}
}
