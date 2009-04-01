/*
 * @(#)Ballot.java.java
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

import java.util.Map;
import java.util.Vector;

/**
 * Ballot is used for counting the final tally at the end of the day and for
 * storing results so the central system can use them. It must store the
 * ID read by the scanner, the contest IDs and the Ballot Style IDs (which lets
 * us know which contests are on each ballot). 
 * 
 * Notes are stored to indicate if there's a problem with the ballot and how the
 * system resolved that problem, e.g. "This ballot has 2 candidates in Rank 2 on
 * Contest 3, Tally method permits this, left alone," or "No vote for contest 
 * 1." 
 * 
 * @author Richard Carback
 *
 */
public class Ballot
{
	private Integer c_id;
	private Integer c_ballotStyleID;
	private Map<Integer, Integer[][]> c_ballotData;
	private boolean c_counted;
	private Vector<String> c_notes;
	
	/**
	 * Default Constructor, creates invalid ballot.
	 */
	public Ballot() 
	{
		this (-1, -1, null);
	}

	/**
	 * Creates a new ballot.
	 * @param p_id the ID of the ballot.
	 * @param p_styleID tye style type.
	 * @param p_contestData the actual data on the ballot.
	 */
	public Ballot(Integer p_id, Integer p_styleID, 
					Map<Integer, Integer[][]> p_contestData) 
	{
		c_id = p_id;
		c_ballotStyleID = p_styleID;
		c_ballotData = p_contestData;
		c_notes = new Vector<String>();
	}

	/**
	 * Does this ballot contain results for the given contest?
	 * @param p_contestID
	 * @return true if the contest exists in this ballot, false otherwise.
	 */
	public boolean hasContest(Integer p_contestID) 
	{
		return (c_ballotData.containsKey(p_contestID) && c_counted);
	}
	
	/**
	 * Get results for given contest.
	 * @param p_contestID
	 * @return true if the contest exists in this ballot, false otherwise.
	 */
	public Integer[][] getContestData(Integer p_contestID) 
	{
		if (c_counted) return c_ballotData.get(p_contestID);
		else return null;
	}
	
	/**
	 * @return the id
	 */
	public Integer getId()
	{
		return c_id;
	}

	/**
	 * @param p_id the id to set
	 */
	public void setId(Integer p_id)
	{
		c_id = p_id;
	}

	/**
	 * @return the ballotStyleID
	 */
	public Integer getBallotStyleID()
	{
		return c_ballotStyleID;
	}

	/**
	 * @param p_ballotStyleID the ballotStyleID to set
	 */
	public void setBallotStyleID(Integer p_ballotStyleID)
	{
		c_ballotStyleID = p_ballotStyleID;
	}

	/**
	 * @return the ballotData
	 */
	public Map<Integer, Integer[][]> getBallotData()
	{
		return c_ballotData;
	}

	/**
	 * @param p_ballotData the ballotData to set
	 */
	public void setBallotData(Map<Integer, Integer[][]> p_ballotData)
	{
		c_ballotData = p_ballotData;
	}

	/**
	 * @param counted the counted to set
	 */
	public void setCounted(boolean counted)
	{
		c_counted = counted;
	}

	/**
	 * @return the counted
	 */
	public boolean isCounted()
	{
		return c_counted;
	}

	/**
	 * @return the notes
	 */
	public Vector<String> getNotes()
	{
		return c_notes;
	}

	/**
	 * @param p_notes the notes to set
	 */
	public void setNotes(Vector<String> p_notes)
	{
		c_notes = p_notes;
	}

	/**
	 * Add note to list.
	 * @param p_note
	 */
	public void addNote(String p_note) {
		c_notes.add(p_note);
	}
	
}
