/*
 * @(#)PluralityTally.java
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
 * PluralityTally accepts and publishes election results using the first past the
 * post or plurality election method.
 * 
 * This is currently a quickly written example. Don't take this to be any sort 
 * of best practice implementation.
 * 
 * @author Richard Carback
 * @version 0.0.1 
 * @date 01/03/09
 */
package org.scantegrity.lib.methods;

import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.logging.Logger;

public class PluralityTally implements TallyMethod {
	/**
	 * TODO: Should be tracking number of busted ballots, make better use of 
	 * logging facility, use more specific exceptions, preserve candidate ids. 
	 * Also could use localization of reporting strings, better decision log to 
	 * reflect bad ballots, and other things..
	 */
	private static Logger c_logger = Logger.getLogger("PluralityVotingMethod");
	private Integer c_numCandidates = 0;
	private String c_names[] = null;
	private Integer c_totals[] = null;
	
	/**
	 * Constructor - plurality has few options that need to be 
	 * set. The only is the list of candidate names. 
	 * 
	 * @param p_names
	 */
	public PluralityTally(String p_names[]) {
		c_names = p_names;
		c_totals = new Integer[p_names.length];
		
		for (int l_i = 0; l_i < p_names.length; l_i++) {
			if (p_names[l_i].isEmpty()) {
				p_names[l_i] = "Candidate" + (l_i+1);
			} 
			c_totals[l_i] = 0;
		}
		
		c_numCandidates = c_names.length;
	}
	
	public void tallyResults(Integer p_ballots[][][], MarkRules p_rules) {
		c_logger.finest("Entering calculateResults");
		
		//Sum up the ballots
		for (int l_i = 0; l_i < p_ballots.length; l_i++) {
			try 
			{
				validate(p_ballots[l_i]);
				for (int l_j = 0; l_j < p_ballots[l_i].length; l_j++) {
					c_totals[l_j] += p_ballots[l_i][l_j][0]; 
				}
			} catch (Exception e) {
				c_logger.warning("Ballot " + l_i + " is malformed. Not Counted.");
			}
		}
		
		//Reorder the candidates according to rank.
		TreeMap<Integer, String> l_map = new TreeMap<Integer, String>();
		for (int l_i = 0; l_i < c_names.length; l_i++) {
			l_map.put(c_totals[l_i], c_names[l_i]);
		}
		//Save in sorted order
		Entry<Integer, String> l_entry = l_map.lastEntry(); 
		for (int l_i = 0; l_i < c_names.length; l_i++) {
			c_names[l_i] = l_entry.getValue();
			c_totals[l_i] = l_entry.getKey();
			l_entry = l_map.lowerEntry(c_totals[l_i]);
		}
		
		c_logger.finest("Leaving calculateResults");
	}

	public String[] getDecisionLog() {
		String l_log[] = new String[1];
		l_log[0] = c_names[0] + " wins with " + c_totals[0] + "votes.";
		return l_log;
	}

	public CandidateResult[] getRankings() {
		CandidateResult l_res[] = new CandidateResult[c_names.length];
		for (int l_i = 0; l_i < c_names.length; l_i++) {
			l_res[l_i] = new CandidateResult(l_i+1, -1, c_names[l_i],
			                                 "" + c_totals[l_i]);
		}
		return l_res;
	}

	public String getResults() {
		String l_res = "";
		for (int l_i = 0; l_i < c_names.length; l_i++) {
			l_res += c_names[l_i] + ": " + c_totals[l_i];
		}
		return l_res;
	}

	public CandidateResult[] getWinners() {
		CandidateResult l_res[] = new CandidateResult[1];
		l_res[0] = new CandidateResult(1, -1, c_names[0],
			                                 "" + c_totals[0]);
		return l_res;
	}

	public void setLogger(Logger p_logger) {
		c_logger = p_logger;
	}

	private void validate(Integer p_ballot[][]) throws Exception {
		int l_sum = 0;
		if (p_ballot.length != c_numCandidates) {
			throw new Exception("Wrong number of Candidates!");
		}
		for (int l_i = 0; l_i < p_ballot.length; l_i++) {
			if (p_ballot[l_i].length != 1) {
				throw new Exception("Wrong number of positions Candidates!");
			} else if (p_ballot[l_i][0] != 0 || p_ballot[l_i][0] != 1) {
				throw new Exception("Bad ballot format!");	
			} else {
				l_sum += p_ballot[l_i][0];
			}
		}
	}
}