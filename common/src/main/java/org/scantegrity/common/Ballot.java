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
package org.scantegrity.common;

import java.awt.image.BufferedImage;
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
 * This is raw ballot data, not result data. It should not be published 
 * because of pattern marking (italian) attacks. Instead, contest-level 
 * selections should be stored and published.
 * 
 * @author Richard Carback
 *
 */
public class Ballot
{
	/** TODO: Notes should be moved to the published object. */
	
	private Integer c_id;
	private Integer c_ballotStyleID;
	private Map<Integer, Integer[][]> c_ballotData;
	private boolean c_counted;
	private Vector<String> c_notes;
	
	/* Write-in Support */
	//Rectangle clippings of write-in ballot location.  Maps contest ID to map of write-in candidate IDs to images
	private Map<Integer, Map<Integer, BufferedImage>> c_writeInImgs = null;
	//Map of write-ins to CandidateIDs in corresponding Contest. Maps contest ID to map of original write-in candidate ID
	//to new candidate ID
	private Map<Integer, Map<Integer, Integer>> c_writeInMap = null;
	
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
		c_counted = true;
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
	
	/**
	 * This is really weak, that's on purpose!
	 * @param l_rhs
	 * @return
	 */
	public boolean equals(Object l_rhs)
	{
		if (l_rhs instanceof Ballot) 
		{
			if (((Ballot)l_rhs).getId().equals(c_id))
			{
				return true;
			}
		}
		return false;	
	}

	public void setWriteInImgs(Map<Integer, Map<Integer, BufferedImage>> writeInImgs) {
		c_writeInImgs = writeInImgs;
	}

	public Map<Integer, Map<Integer,BufferedImage>> getWriteInImgs() {
		return c_writeInImgs;
	}

	public void setWriteInMap(Map<Integer, Map<Integer, Integer>> writeInMap) {
		c_writeInMap = writeInMap;
	}

	public Map<Integer, Map<Integer, Integer>> getWriteInMap() {
		return c_writeInMap;
	}
	
}
