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

import java.util.Date;

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
	private Date c_date = new Date(); //Date and time are stored here.
	private String c_chiefJudge = "Unknown Chief Judge";
	private BallotReader c_reader = null;
	private Contest[] c_contests = null;
	protected BallotStyle[] c_styles = null;
	
	
	
	public ScannerConfig() {
		//TODO
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
	public Date getDate()
	{
		return c_date;
	}


	/**
	 * @param p_date the date to set
	 */
	public void setDate(Date p_date)
	{
		c_date = p_date;
	}


	/**
	 * @return the chiefJudge
	 */
	public String getChiefJudge()
	{
		return c_chiefJudge;
	}


	/**
	 * @param p_chiefJudge the chiefJudge to set
	 */
	public void setChiefJudge(String p_chiefJudge)
	{
		c_chiefJudge = p_chiefJudge;
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
	 * @return the contests
	 */
	public Contest[] getContests()
	{
		return c_contests;
	}


	/**
	 * @param p_contests the contests to set
	 */
	public void setContests(Contest[] p_contests)
	{
		c_contests = p_contests;
	}
	
	/**
	 * @return the styles
	 */
	public BallotStyle[] getStyles()
	{
		return c_styles;
	}


	/**
	 * @param p_styles the styles to set
	 */
	public void setStyles(BallotStyle[] p_styles)
	{
		c_styles = p_styles;
	}
	
}

