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


package org.scantegrity.scanner;

import java.util.Vector;

import org.scantegrity.lib.BallotStyle;
import org.scantegrity.lib.Contest;

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
public class ScannerConfig {
	/**
	 * TODO: Needs to add authentication for chief judge and possibly the 
	 * other judge pins. 
	 */
	private int c_pollID = -1;
	private int c_scannerID = -1;
	private String c_name = "Unknown Name";
	private String c_location = "Unknown Location";
	private String c_date = "Unknown Date"; //Date and time are stored here.
	private String c_time = "Unknown Time";
	private Vector<String> c_chiefJudges = null;
	private Vector<String> c_judgePassHash = null;
	private BallotReader c_reader = null;
	private Vector<Contest> c_contests = null;
	protected Vector<BallotStyle> c_styles = null;
	private Vector<String> c_outputFileNames = null;
	/**
	 * @return the styles
	 */
	public Vector<BallotStyle> getStyles()
	{
		return c_styles;
	}


	private Vector<String> c_outputLocs = null;
	
	
	
	public ScannerConfig() {
		c_pollID = -1;
		c_scannerID = -1;
		c_name = "Unknown Name";
		c_location = "Unknown Location";
		c_date = "Unknown Date"; //Date and time are stored here.
		c_time = "Unknown Time";
		c_chiefJudges = new Vector<String>();
		c_chiefJudges.add("Unknown Chief Judge");
		c_judgePassHash = new Vector<String>();
		c_judgePassHash.add("");
		c_reader = new ScantegrityBallotReader();
		c_contests = new Vector<Contest>();
		c_styles = new Vector<BallotStyle>();	
		c_outputLocs = new Vector<String>();
		c_outputFileNames = new Vector<String>();
	}


	/**
	 * @return the pollID
	 */
	public int getPollID()
	{
		return c_pollID;
	}


	/**
	 * @param p_pollID the pollID to set
	 */
	public void setPollID(int p_pollID)
	{
		c_pollID = p_pollID;
	}


	/**
	 * @return the scannerID
	 */
	public int getScannerID()
	{
		return c_scannerID;
	}


	/**
	 * @param p_scannerID the scannerID to set
	 */
	public void setScannerID(int p_scannerID)
	{
		c_scannerID = p_scannerID;
	}


	/**
	 * @return the name
	 */
	public String getName()
	{
		return c_name;
	}


	/**
	 * @param p_name the name to set
	 */
	public void setName(String p_name)
	{
		c_name = p_name;
	}


	/**
	 * @return the location
	 */
	public String getLocation()
	{
		return c_location;
	}


	/**
	 * @param p_location the location to set
	 */
	public void setLocation(String p_location)
	{
		c_location = p_location;
	}


	/**
	 * @return the date
	 */
	public String getDate()
	{
		return c_date;
	}
	

	/**
	 * @param p_date the date to set
	 */
	public void setDate(String p_date)
	{
		c_date = p_date;
	}
	
	/**
	 * @return the time
	 */
	public String getTime()
	{
		return c_time;
	}
	
	/**
	 * @param p_time
	 */
	public void setTime(String p_time)
	{
		c_time = p_time;
	}
	
	
	/**
	 * @return the chiefJudges
	 */
	public Vector<String> getChiefJudges()
	{
		return c_chiefJudges;
	}


	/**
	 * @param p_chiefJudges the chiefJudges to set
	 */
	public void setChiefJudges(Vector<String> p_chiefJudges)
	{
		c_chiefJudges = p_chiefJudges;
	}


	/**
	 * @return the judgePassHash
	 */
	public Vector<String> getJudgePassHash()
	{
		return c_judgePassHash;
	}


	/**
	 * @param p_judgePassHash the judgePassHash to set
	 */
	public void setJudgePassHash(Vector<String> p_judgePassHash)
	{
		c_judgePassHash = p_judgePassHash;
	}

	/**
	 * @return the reader
	 */
	public BallotReader getReader()
	{
		return c_reader;
	}


	/**
	 * @param p_reader the reader to set
	 */
	public void setReader(BallotReader p_reader)
	{
		c_reader = p_reader;
	}
	
	/**
	 * @return the outputLocs
	 */
	public Vector<String> getOutputLocs()
	{
		return c_outputLocs;
	}


	/**
	 * @param p_outputLocs the outputLocs to set
	 */
	public void setOutputLocs(Vector<String> p_outputLocs)
	{
		c_outputLocs = p_outputLocs;
	}
	
	/**
	 * @return the output Jar File 
	 */
	public Vector<String> getOutputFileNames()
	{
		return c_outputFileNames;
	}


	/**
	 * @param p_outputLocs the outputLocs to set
	 */
	public void setOutputFileNames(Vector<String> p_outputFileNames)
	{
		c_outputFileNames = p_outputFileNames;
	}


	/**
	 * @param p_contests the contests to set
	 */
	public void setContests(Vector<Contest> p_contests)
	{
		c_contests = p_contests;
	}


	/**
	 * @param p_styles the styles to set
	 */
	public void setStyles(Vector<BallotStyle> p_styles)
	{
		c_styles = p_styles;
	}


	/**
	 * @return the contests
	 */
	public Vector<Contest> getContests()
	{
		return c_contests;
	}

}

