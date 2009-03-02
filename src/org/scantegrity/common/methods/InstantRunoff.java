import java.util.logging.Logger;

/*
 * @(#)InstantRunoff.java
 *  
 * Copyright (C) 2009 Scantegrity Project
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
 * InstantRunoff accepts and publishes election results using Instant Runoff
 * rules. The current ruleset and reporting is specific to Takoma Park, who's 
 * relevant law I will cite here:
 * 
 * (c) An instant runoff voting system shall be used in order to elect the Mayor
 * and Councilmembers with a majority of votes by allowing voters to rank 
 * candidates in order of choice. Instructions on instant runoff voting 
 * provided to voters shall conform substantially to the following 
 * specifications, although subject to modification based on ballot design and 
 * voting system: “Vote for candidates by indicating your first-choice 
 * candidate, your second-choice candidate, and so on. Indicate your first 
 * choice by marking the number “1" beside a candidate’s name, your second 
 * choice by marking the number “2" beside that candidate’s name, your third 
 * choice by marking the number “3", and so on, for as many choices as you wish.
 * You are free to rank only one candidate, but ranking additional candidates 
 * cannot help defeat your first-choice candidate. Do not mark the same number 
 * beside more than one candidate. Do not skip numbers.”
 * (d) The first choice marked on each ballot shall be counted initially by the 
 * judges as one vote. If any candidate receives a majority of the first 
 * choices, that candidate shall be declared elected.
 * (e) If no candidate receives a majority of first choices, the judges of 
 * election shall conduct an instant runoff consisting of additional rounds of 
 * ballot counting. In every round of counting, each ballot is counted as one 
 * vote for that ballot’s highest ranked advancing candidate. “Advancing 
 * candidate” means a candidate for that office who has not been eliminated. A 
 * candidate receiving a majority of valid votes in a round shall be declared 
 * elected.
 * If no candidate receives a majority of valid votes in a round, the candidate 
 * with the fewest votes shall be eliminated, and all ballots shall be 
 * recounted. This process of eliminating the candidate with the fewest votes 
 * and recounting all ballots shall continue until one candidate receives a 
 * majority of the valid votes in a round. 
 * (f) To facilitate ballot counting in any round, the judges of election may 
 * eliminate all candidates with no mathematical chance of winning. A candidate 
 * has no mathematical chance of winning if the sum total of all votes credited 
 * to that candidate and all candidates with fewer votes is less than the number
 * of votes credited to the candidate with the next greatest number of votes.
 * (g) If a ballot has no more available choices ranked on it, that ballot 
 * shall be declared “exhausted” and not counted in that round or any 
 * subsequent round. Ballots skipping one number shall be counted for that 
 * voter’s next clearly indicated choice, but ballots skipping more than one 
 * number shall be declared exhausted when this skipping of numbers is reached. 
 * Ballots with two or more f the same number shall be declared exhaused when 
 * such duplicate rankings are reached unless only one of the candidates with 
 * the duplicate ranking is an advancing candidate.
 * (h) In the event of a tie that effects the outcome of the election, the 
 * tie shall be broken by comparing the votes of the tied candidates in the 
 * previous rounds of counting, starting with the count immediately preceding 
 * the round in which the tie occurs. If one of the tied candidates had more 
 * votes than the remaining tied candidates in the preceding round or an 
 * earlier round of counting, then that candidate shall advance and the others 
 * shall be eliminated. If the candidates were tied in each preceding round, 
 * then the tie shall be resolved by lot. In the event that this tie occurs 
 * between or among all remaining candidates, then a runoff election between or 
 * among the tied candidates shall be held within forty-five (45) days after 
 * the date of the election.
 * 
 * @author Richard Carback
 * @version 0.0.1 
 * @date 01/03/09
 */

public class InstantRunoff implements VotingMethod {

	public void calculateResults(Integer[][][] p_ballots) {
		// TODO Auto-generated method stub
		
	}

	public String[] getDecisionLog() {
		// TODO Auto-generated method stub
		return null;
	}

	public CandidateResult[] getRankings() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getResults() {
		// TODO Auto-generated method stub
		return null;
	}

	public CandidateResult[] getWinners() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setLogger(Logger p_logger) {
		// TODO Auto-generated method stub
		
	}
	
}