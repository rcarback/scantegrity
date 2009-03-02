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
 * The intended usage is for the application to get the configuration and use it
 * to determine the method type and (possibly) data format. Then it can feed
 * the data to the VotingMethod, which validates and calculates results. Thus,
 * we expect a config class to be able to return properly configured objects 
 * that implement the VotingMethod interface and the user of these methods
 * will simply send them (currently only) dark mark logic matrices of the 
 * results they parsed, scanned, or otherwise obtained.
 * 
 * @author Richard Carback
 * @version 0.0.1 
 * @date 24/02/09
 */

package org.scantegrity.common.methods;

import java.util.logging.Logger;

public interface VotingMethod {
	/* TODO: It might turn out that this is better done through abstract
	 * classes, but that's indeterminate at this time.
	 */
	
	/**
	 * setLogger - sets the logging context for the Voting method using the
	 * configuration passed to it.
	 * 
	 * @param p_logger the configured logger to use.
	 */
	void setLogger(Logger p_logger);
	
	/**
	 * calculateResults - tells the method to take the current data set and
	 * calculate results from it. This may throw exceptions if the format
	 * of a ballot is invalid. It's assumed at this point in time that the
	 * ballot data will be in darkmark logic (0 for unmarked, 1 for marked). 
	 * 
	 * @param p_ballots an array of 2 dimensional darkmark logic contest 
	 * results.  
	 */
	void calculateResults(Integer p_ballots[][][]);
	
	/**
	 * GetWinners - Return an array of the winners. The map should contain
	 * an order number (starting at 0), the Candidate's name, and a string 
	 * that represents the percentage of voters who chose the candidate or 
	 * some other reasonable data element (e.g. how many candidates can be 
	 * elected). The last piece could be an int or a double (or something else),
	 * which is why it is represented as a string.
	 * 
	 * @param bp_ballotsMatrix a darkmark logic representation of each ballot
	 * @return an array of the winning candidates
	 */
	CandidateResult[] getWinners();
	
	/**
	 * GetRankings - Return an array of the full ranking of candidates, and not 
	 * just the winners. The map should contain
	 * an order number (starting at 0), the Candidate's name, and a string 
	 * that represents the percentage of voters who chose the candidate or 
	 * some other reasonable data element (e.g. how many candidates can be 
	 * elected). The last piece could be an int or a double (or something else),
	 * which is why it is represented as a string.
	 * 
	 * @return an array of the full ranking of candidates, and not 
	 * just the winners.
	 */
	CandidateResult[] getRankings();
	
	/**
	 * getDecisionLog - Report each decision made by the algorithm (including
	 * deciding of the final result). Each decision should be a separate entry 
	 * in the array.
	 * 
	 * @return An array of unformatted strings describing the decisions made.
	 */
	String[] getDecisionLog();
	
	
	/**
	 * getResults - Return a formatted summary of the election results.
	 * 
	 * @return An formatted string describing the tally results. This could be
	 * a simple list, or it could be a string of decisions (as in IRV).
	 */
	String getResults();
	
	
}