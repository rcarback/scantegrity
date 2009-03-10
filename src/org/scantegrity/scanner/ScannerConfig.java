/*
 * @(#)ScannerConfig.java
 *  
 * Copyright (C) 2008-2009 Scantegrity Project
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

/**
 * ScannerConfig represents the configuration needed for the scanner in an 
 * election. This information is read from an XML file and this class is used
 * to retrieve objects needed by the Scanner application on demand. 
 * 
 * In particular, ScannerConfig tracks the polling place information, and 
 * also contains the class configurations necessary to decode each contest based 
 * on ballot style and report results at the end of the day.
 * 
 * Future work for this class: 
 * 	-methods for accessing scanner information stored in Crypto Hardware 
 *  -multiple polling place information to "detect" location based on chief 
 *   judge?
 *  
 *  
 * @author Richard Carback
 * @version 0.0.1 
 * @date 07/03/09
 */
package org.scantegrity.scanner;

import java.util.Date;

public class ScannerConfig {
	
	/**
	 Currently undefined attributes:
	  
	 private Authentication c_ChiefJudgeAuth;
	 private Pin[] c_JudgePins;
	  
	 // Object to parse serial number data 
	 private SerialParser c_serialParser;
	 
	 // Object to parse different types of contests, and a mapper to map
	 // each contest to a particular Parser.
	 private ContestParser[] c_contestParsers;
	 private Vector<Integer> c_contestParseMap;
	 
	 // Object to generate results for each contest, and a mapper.
	 private VotingMethod[] c_contestMethods;
	 private Vector<Integer> c_contestMethodMap;
	 
	 // (Highly optional, and probably won't make it into final version).
	 private ScannerInterface c_scanInterface;
	 */
	
	private int c_pollID = -1;
	private int c_scannerID = -1;
	private String c_name = "Unknown Name";
	private String c_location = "Unknown Location";
	private Date c_date = new Date(); //Date and time are stored here.
	private String c_chiefJudge = "Unknown Chief Judge";
	
	
	public ScannerConfig() {
		// Load the configuration file
		
		// Chief Judge Authentication
		
		// Judge pins
		
		// Polling place info.
		
		// Determine what serial number parser the ballot needs (and how many
		// ballot styles there are).
		
		// Determine what contest parsers are needed (and which ballot styles
		// they belong to).
		
		// Determine what Method calculators are needed (and which ballot 
		// styles they belong to). 
		
		// Determine the scanner image getter. 
	}
}