/*
 * @(#)CandidateResult.java
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
 * CandidateResult represents a candidate's rank, id, name, and status after the
 * results of the election have been calculated. Rank/ID/Name are self-
 * explanatory. Status is a generic string that could stand for anything (% of
 * votes obtained by the candidate, for example). 
 * 
 * @author Richard Carback
 * @version 0.0.1 
 * @date 01/03/09
 */
package org.scantegrity.lib.methods;

public class CandidateResult {
	private Integer c_rank = -1;
	private Integer c_id = -1;
	private String c_name = "";
	private String c_status = "";
	
	
	/**
	 * Constructor - Sets invalid values.
	 */
	public CandidateResult() {
		this(-1, -1, "", "");	
	}

	/**
	 * Constructor - Convenience method for methods that don't need a
	 * status, which should be most methods.
	 *  
	 * @param p_rank the rank of the candidate
	 * @param p_id the id number of the candidate.
	 * @param p_name the name of the candidate.
	 */	
	public CandidateResult(Integer p_rank, Integer p_id, String p_name) {
		this(p_rank, p_id, p_name, "");	
	}
	
	/**
	 * Constructor - Expected method for creating a CandidateResult object.
	 *  
	 * @param p_rank the rank of the candidate
	 * @param p_id the id number of the candidate.
	 * @param p_name the name of the candidate.
	 * @param p_status the status of the candidate.
	 */	
	public CandidateResult(Integer p_rank, Integer p_id, String p_name, 
							String p_status) {
		/* Recall that String and Integer are immutable, so we don't need "new"
		 * calls here. */
		setRank(p_rank);
		setId(p_id);
		setName(p_name);
		setStatus(p_status);
	}

	/**
	 * setRank - Sets the rank of this candidate object.
	 * 
	 * @param p_rank the rank to set
	 */
	public void setRank(Integer p_rank) {
		c_rank = p_rank;
	}

	/**
	 * getRank - Return the current rank of this candidate.
	 * 
	 * @return the rank
	 */
	public Integer getRank() {
		return c_rank;
	}

	/**
	 * setId - Sets the id of this candidate object.
	 * 
	 * @param p_id the id to set
	 */
	public void setId(Integer p_id) {
		c_id = p_id;
	}

	/**
	 * getId - return the current candidates Id.
	 * 
	 * @return the id
	 */
	public Integer getId() {
		return c_id;
	}

	/**
	 * setName - Sets the name of this candidate object.
	 * 
	 * @param p_name the name to set
	 */
	public void setName(String p_name) {
		c_name = p_name;
	}

	/**
	 * getName - return the name of this candidate.
	 * 
	 * @return the name
	 */
	public String getName() {
		return c_name;
	}

	/**
	 * setStatus - Set the status of this candidate.
	 * 
	 * @param p_status the status to set
	 */
	public void setStatus(String p_status) {
		c_status = p_status;
	}

	/**
	 * getStatus - Return the status of this candidate.
	 *  
	 * @return the status
	 */
	public String getStatus() {
		return c_status;
	}
	
}