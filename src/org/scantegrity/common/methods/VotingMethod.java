/*
 * @(#)VotingMethod.java
 *  
 * Copyright (C) 2008 Scantegrity Project
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
 * VotingMethod is an abstract class or Interface that defines the functions
 * a voting method needs to implement in order to work with the rest of the
 * scantegrity applications.
 * 
 * This should be generalized enough to support Plurality, Ranked Choice, Score
 * Voting, MultiWinner Elections, etc. Part of that support is the "log" and
 * "listing candidate rankings." The latter should include a rank that could be
 * a percentage (If Necessary), which is why it is represented as a String. 
 *
 * We chose this particular design to enable generic programs to be written to
 * display the data in as many formats as possible. Most applications will only
 * call one of these to get the data in the format they need.
 * 
 * @author Richard Carback
 * @version 0.0.1 
 * @date 24/02/09
 */

package org.scantegrity.common.methods;

public interface VotingMethod {
	/* TODO: This might be better as an abstract class. Not sure until we get
	 * some of the implementations done. */
	
	/**
	 * GetWinners - Return an array of the winners. The map should contain
	 * an order number (starting at 0), the Candidate's name, and a string 
	 * that represents the percentage of voters who chose the candidate or 
	 * some other reasonable data element (e.g. how many candidates can be 
	 * elected). The last piece could be an int or a double (or something else),
	 * which is why it is represented as a string.
	 * 
	 * @param p_config - Configuration for the election.
	 * @param p_cid - Contest ID.
	 * @param p_results - an array of results scanned by the system. Typically,
	 * this is represented as a matrix.
	 * @return an array of the winning candidates
	 */
	CandidateResult[] getWinners(ElectionConfiguration p_config, Integer p_cid,
								RawResults p_results);
	
	/**
	 * GetRankings - Return an array of the full ranking of candidates, and not 
	 * just the winners. The map should contain
	 * an order number (starting at 0), the Candidate's name, and a string 
	 * that represents the percentage of voters who chose the candidate or 
	 * some other reasonable data element (e.g. how many candidates can be 
	 * elected). The last piece could be an int or a double (or something else),
	 * which is why it is represented as a string.
	 * 
	 * @param p_config - Configuration for the election.
	 * @param p_cid - Contest ID.
	 * @param p_results - an array of results scanned by the system. Typically,
	 * this is represented as a matrix.
	 * @return an array of the full ranking of candidates, and not 
	 * just the winners.
	 */
	CandidateResult[] getRankings(ElectionConfiguration p_config, Integer p_cid,
								RawResults p_results);
	
	/**
	 * getDecisionLog - Report each decision made by the algorithm (including
	 * deciding of the final result). Each decision should be a separate entry 
	 * in the array.
	 * 
	 * @param p_config - Configuration for the election.
	 * @param p_cid - Contest ID.
	 * @param p_results - an array of results scanned by the system. Typically,
	 * this is represented as a matrix.
	 * @return An array of unformatted strings describing the decisions made.
	 */
	String[] getDecisionLog(ElectionConfiguration p_config, Integer p_cid,
								RawResults p_results);
	
	
	/**
	 * getResults - Return a fully formatted summary of the election results.
	 * 
	 * @param p_config - Configuration for the election.
	 * @param p_cid - Contest ID.
	 * @param p_results - an array of results scanned by the system. Typically,
	 * this is represented as a matrix.
	 * @return An formatted string describing the tally results. This could be
	 * a simple list, or it could be a string of decisions (as in IRV).
	 */
	String getResults(ElectionConfiguration p_config, Integer p_cid,
			RawResults p_results);
	
	
}