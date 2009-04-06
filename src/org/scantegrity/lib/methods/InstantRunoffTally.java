package org.scantegrity.lib.methods;
import java.util.TreeMap;
import java.util.Vector;

import org.scantegrity.lib.Ballot;
import org.scantegrity.lib.Contest;
import org.scantegrity.lib.Contestant;


/*
 * @(#)InstantRunoffTally.java
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
 * InstantRunoffTally accepts and publishes election results using Instant Runoff
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

public class InstantRunoffTally implements TallyMethod {	
	/** TODO: This entire thing might benefit from a call back to ask 
	 * the user to make certain decisions.
	 */
	

	/* (non-Javadoc)
	 * @see org.scantegrity.lib.methods.TallyMethod#validateBallot(org.scantegrity.scanner.Contest, org.scantegrity.scanner.Ballot)
	 */
	@Override
	public TreeMap<String, String> validateContest(int p_contestId,
													Ballot p_ballot)
	{
		if (!p_ballot.hasContest(p_contestId)) return null;
		TreeMap<String, String> l_res = new TreeMap<String, String>();
		//This models some of the iterator functionality, but since counting
		//and validation are different beasts, we can't really use it.
		//if ()
		Integer l_bData[][] = p_ballot.getContestData(p_contestId);
		if (l_bData.length == 0 || l_bData[0].length == 0) return null;
		for (int l_i = 0; l_i < l_bData[0].length; l_i++)
		{
			int l_rankCnt = 0;
			for (int l_j = 0; l_j < l_bData.length; l_j++)
			{
				if (l_bData[l_j][l_i] == 1) l_rankCnt++;
			}
			String l_rankName = "Rank " + (l_i+1); 
			if (l_rankCnt == 1)	l_res.put(l_rankName, "Vote Recorded");
			else if (l_rankCnt == 0) l_res.put(l_rankName, "No Vote");
			else l_res.put(l_rankName, "Overvote");
		}
		
		return l_res;
	}	
	
	
	/* (non-Javadoc)
	 * @see org.scantegrity.lib.methods.TallyMethod#tally(int, java.util.Vector)
	 */
	@Override
	public ContestResult tally(Contest p_contest, Vector<Ballot> p_ballots)
	{
		int l_id = p_contest.getId();
		
		//The election is a series of rounds, which holds stacks for 
		//each contestant and keeps track of good/bad ballots.
		Vector<Contestant> l_contestants;		
		l_contestants = new Vector<Contestant>(p_contest.getContestants());
		//Add an "Exhausted" contestant.
		l_contestants.add(new Contestant(-2, "Exhausted Pile"));

		//Results Object
		IRVContestResult l_res = new IRVContestResult(l_id, l_contestants);		
		IRVContestResult.Round l_curRound = l_res.new Round(1);
		IRVContestResult.Round l_prevRound = null;

		
		//currently available stacks
		TreeMap<Contestant, Vector<BallotIterator>> l_stacks;
		l_stacks = new TreeMap<Contestant, Vector<BallotIterator>>();
		for (Contestant l_c: l_contestants)
		{
			l_stacks.put(l_c, new Vector<BallotIterator>());	
		}
		
		System.out.println("Started with: " + p_ballots.size());
		Vector<BallotIterator> l_countMe;
		l_countMe = getBallotsWithContest(l_id, p_ballots);

		//Stack's for resolving ties.
		Vector<TreeMap<Contestant, Vector<BallotIterator>>> l_tieStack;
		l_tieStack = new Vector<TreeMap<Contestant, Vector<BallotIterator>>>();
		Vector<IRVContestResult.Round> l_roundStack;
		l_roundStack = new Vector<IRVContestResult.Round>();
		Vector<Contestant> l_canStack = new Vector<Contestant>();
		
		
		//Process each round, create a round result for each iteration.
		int l_numBallots = l_countMe.size();
		TreeMap<Integer, Vector<Contestant>> l_curRank;
		do
		{
			doRound(l_stacks, l_countMe);
			
			//Record current totals
			Vector<BallotIterator> l_curStack;
			for (int l_i = 0; l_i < l_contestants.size(); l_i++)
			{
				//Pull stack for this contestant
				l_curStack = l_stacks.get(l_contestants.elementAt(l_i));
				int l_totals = 0;
				if (l_curStack != null) l_totals = l_curStack.size();
				int l_delta = l_totals;

				if (l_prevRound != null)
				{
					l_delta = l_totals - l_prevRound.c_totals.get(l_i);
				}

				l_curRound.c_totals.set(l_i, l_totals);
				l_curRound.c_delta.set(l_i, l_delta);
			}

			l_curRank = getRankOrder(l_stacks);			
			/**
			 * NOTE: Ties are OK, as long as they aren't on the bottom AND
			 * the bottom candidates are still mathematically viable.
			 */
			Vector<Contestant> l_defeated = getDefeated(l_curRank, l_stacks);
			
			if (l_defeated == null || l_defeated.size() == 0)
			{
				//A Tie!
				Vector<Contestant> l_tied = l_curRank.get(l_curRank.lastKey());
				String l_tieNote = "Candidates Tied: ";
				for (Contestant l_c : l_tied) {
					l_tieNote += l_c.toString() + ", ";
				}
				l_curRound.addNote(l_tieNote);
				
				//Try to break the tie
				Contestant l_tieCan;
				l_tieCan = breakTie(l_tied, l_curRound, l_res.c_rounds);
				if (l_tieCan == null)
				{
					//Tie could not be broken, is this the last possible round?
					if (l_stacks.size() == 3)
					{
						l_curRound.addNote("Tie between last 2 candidates " 
											+ "cannot be broken. No Winner!");
						
					}
					else
					{
						/**
						 * We now save the "state" which get's popped when a winner
						 * is detected. Pick one state, push the other possibilities
						 * onto the stack, then continue this chain.
						 */
						l_curRound.addNote("Tie is Unbreakable, computing all"
											+ " possibilities...");
						int l_i;
						for (l_i = 0; l_i < l_tied.size()-1; l_i++)
						{
							//System.out.println("Adding alternate exec: " + l_i);
							TreeMap<Contestant, Vector<BallotIterator>> l_tmpStack;
							IRVContestResult.Round l_tmpRnd;
							Contestant l_tmpCan; 
							l_tmpStack = copyStack(l_stacks);
							l_tmpRnd = l_curRound.clone();
							l_tmpCan = l_tied.get(l_i);
							
							l_tmpRnd.c_desc += ", Dropped " + l_tmpCan.toString()
												+ " in Round " + l_curRound.c_id;
							l_tmpRnd.addNote(l_tmpCan.getName() 
												+ " DEFEATED (TIE)");
							l_tmpRnd.c_state.set(l_contestants.indexOf(l_tmpCan), 
									"DEFEATED (TIE), Round " + l_curRound.getId());
							
							l_canStack.add(l_tmpCan);
							l_roundStack.add(l_tmpRnd);
							l_tieStack.add(l_tmpStack);
						}
						
						//Chosen state is always 0
						l_curRound.c_desc += ", Dropped " + l_tied.get(l_i).toString()
											+ " in Round " + l_curRound.c_id;
						l_curRound.addNote(l_tied.get(l_i).getName() 
											+ " DEFEATED (TIE)");
						l_curRound.c_state.set(l_contestants.indexOf(l_tied.get(l_i)), 
									"DEFEATED (TIE), Round " + l_curRound.getId());
						l_countMe = new Vector<BallotIterator>();
						l_countMe.addAll(l_stacks.get(l_tied.get(l_i)));
						l_stacks.remove(l_tied.get(l_i));
					}
					l_defeated = null;
				}
				else
				{
					l_defeated = new Vector<Contestant>();
					l_curRound.addNote(l_tieCan.toString() 
									+ " had fewer votes than " 
									+ "other candidates in a previous round.");
					l_defeated.add(l_tieCan);
				}	
			}
			
			if (l_defeated != null)
			{
				l_countMe = new Vector<BallotIterator>();
				for (Contestant l_c : l_defeated) 
				{
					l_countMe.addAll(l_stacks.get(l_c));
					l_stacks.remove(l_c);
					l_curRound.addNote(l_c.getName() + " DEFEATED");
					l_curRound.c_state.set(l_contestants.indexOf(l_c), 
									"DEFEATED, Round " + l_curRound.getId());
					//System.out.println(l_c.getName() + " DEFEATED");
				}
			}

			//Detect a winner
			//Is there a winner with 50%+1 vote majority? End now.
			Vector<Contestant> l_top = l_curRank.get(l_curRank.firstKey());
			if (l_top.size() == 1 
					&& l_numBallots/l_stacks.get(l_top.get(0)).size() < 2.0)
			{
				l_curRound.addNote("END, Majority winner is " + l_top.get(0));
				l_curRound.c_state.set(l_contestants.indexOf(l_top.get(0)), 
										"WINNER, Round " + l_curRound.getId());
				//Need something here to end counting.. but not a big problem..
			}
			else if (l_stacks.size() <= 2)
			{
				//Nothing left to count
				l_curRound.addNote("END, winner is " + l_stacks.lastKey());
				l_curRound.c_state.set(l_contestants.indexOf(l_stacks.lastKey()), 
										"WINNER, Round " + l_curRound.getId());
			}
			
			//Save round and report results
			l_res.addRound(l_curRound);
			System.out.println(l_curRound.toString());						
			
			//Continue execution if there are remaining ties that need to 
			//be computed.
			if ((l_stacks.size() <= 2 && l_tieStack.size() > 0) 
					|| (l_stacks.size() == 3 && l_tieStack.size() > 0 
							&& l_curRank.size() == 1 
							&& l_curRank.get(0).size() == 2))
			{
				int l_index = l_tieStack.size()-1;
				l_stacks = l_tieStack.get(l_index);
				l_curRound = l_roundStack.get(l_index);
				Contestant l_dropCan = l_canStack.get(l_index);
				l_countMe = new Vector<BallotIterator>();
				l_countMe.addAll(l_stacks.get(l_dropCan));
				l_stacks.remove(l_dropCan);
				l_tieStack.remove(l_index);
				l_roundStack.remove(l_index);
				l_canStack.remove(l_index);
			}

			//System.out.println("Alt Size: " + l_tieStack.size());
			//System.out.println("Stack Size: " + l_stacks.size());
			//Set up next round.
			l_prevRound = l_curRound;
			l_curRound = l_res.new Round(l_prevRound);
		} while (l_stacks.size() > 2 //End, or tied ending
					|| l_tieStack.size() > 0); 
		
		l_res.setRanking(l_curRank);
		
		return l_res;
	}

	/**
	 * This is assuming the dead candidates are *removed* from the stack!
	 * @param l_stacks
	 * @param l_ballots
	 */
	private void doRound(TreeMap<Contestant, Vector<BallotIterator>> l_stacks,
						Vector<BallotIterator> l_ballots)
	{
		for (BallotIterator l_ballot : l_ballots)
		{
			//Find the next contestant ID, recall that the ID's returned from
			//the getNext method should be "normalized" to the standard
			//ordering listed in the original config.
			Contestant l_contestant = null;
			while (l_contestant == null)
			{
				Vector<Integer> l_nextIDs = l_ballot.getNext();
				//If we reached the end..
				if (l_nextIDs == null )
				{
					l_nextIDs = new Vector<Integer>();
					l_nextIDs.add(-2);
				}
				
				//Find the Contestant in the stack, if we find more than 2, abort.
				for (int l_i = 0; l_i < l_nextIDs.size(); l_i++)
				{
					for (Object l_key: l_stacks.keySet())
					{
						Contestant l_c = (Contestant)l_key;
						if (l_c.getId() == l_nextIDs.elementAt(l_i))
						{
							if (l_contestant != null)
							{
								//Put in "bad" stack, report problem
								l_contestant = l_stacks.firstKey();
								System.out.println("Ballot " + 
										l_ballot.c_ballot.getId()
										+ " has multiple Choices for Rank " 
										+ l_ballot.c_curPos);
								l_ballot.c_ballot.addNote("Ballot " + 
										l_ballot.c_ballot.getId()
										+ " has multiple Choices for Rank " 
										+ l_ballot.c_curPos);
								l_i = l_nextIDs.size();
								break;
							}
							else
							{
								l_contestant = l_c;
							}
						}
					}
				}
			}
			
			//System.out.println("Adding " + l_ballot.c_ballot.getId() 
			//		+ " to " + l_contestant.toString());
			Vector<BallotIterator> l_b = l_stacks.get(l_contestant);
			l_b.add(l_ballot);
			//l_stacks.get(l_contestant).add(l_ballot);	
		}
	}
	
	private Integer getContestantTotals(Vector<Contestant> l_contestants,
						TreeMap<Contestant, Vector<BallotIterator>> l_stacks)
	{
		Integer l_tot = 0;
		for (Contestant l_c : l_contestants)
		{
			l_tot += l_stacks.get(l_c).size();
		}
		return l_tot;
	}
	
	private Vector<BallotIterator> getBallotsWithContest(int p_contestId, 
													Vector<Ballot> p_ballots)
	{
		//First pass, find ballots that have this contest, and then 
		//sum up the stacks for round 1.
		BallotIterator l_curIter;
		Ballot l_curBallot;
		Vector<BallotIterator> l_res = new Vector<BallotIterator>();
		for (int l_i = 0; l_i < p_ballots.size(); l_i++)
		{
			l_curBallot = p_ballots.elementAt(l_i);
			if (p_ballots.elementAt(l_i).hasContest(p_contestId))
			{
				//TODO: Maybe styles should be a map?
				//Find the correct ballot style for each ballot to create
				// a proper ballot iterator.
				l_curIter = new BallotIterator(l_curBallot, p_contestId);
				/*for (BallotStyle l_style : p_styles)
				{
					int l_sid = p_ballots.elementAt(l_i).getBallotStyleID();
					if (l_style.getId() == l_sid)
					{
						l_curIter = new BallotIterator(l_curBallot, l_style, 
														p_contestId);
						break;
					}
				}
				if (l_curIter == null) {
					l_curIter = new BallotIterator(l_curBallot, null,
													p_contestId);
					//TODO: Make a note that this ballot was invalid!
					System.out.println("Ballot " + l_curBallot.getId() + " has "
							+ "no style!");
				}*/
				l_res.add(l_curIter);
			}
		}		
		
		return l_res;
	}
	
	
	private TreeMap<Integer, Vector<Contestant>> getRankOrder(
							TreeMap<Contestant, Vector<BallotIterator>> p_stacks)
	{
		Object l_keys[] = p_stacks.keySet().toArray();
		TreeMap<Integer, Vector<Contestant>> l_tmp, l_final;
		l_tmp = new TreeMap<Integer, Vector<Contestant>>();		
		l_final = new TreeMap<Integer, Vector<Contestant>>();
		
		//Each key becomes the target, the new keys are the size, order is
		//automagically computed via treemap.
		for (Object l_k: l_keys) 
		{
			Contestant l_key = (Contestant)l_k;
			if (l_key.getId() == -2) continue; //Skip the exhausted pile
			//System.out.print(l_key.toString() + ":");
			//System.out.println(", Size: " + p_stacks.get(l_key).size());
			if (!l_tmp.containsKey(p_stacks.get(l_key).size()))
			{
				l_tmp.put(p_stacks.get(l_key).size(), new Vector<Contestant>());
			}
			l_tmp.get(p_stacks.get(l_key).size()).add(l_key);
		}
		
		//Change each key to equivalent rank.
		l_keys = l_tmp.keySet().toArray();
		for (int l_i = 0; l_i < l_keys.length; l_i++)
		{
			l_final.put(l_i, l_tmp.get(l_tmp.lastKey()));
			l_tmp.remove(l_tmp.lastKey());
		}
		
		return l_final;
	}
	
	private Vector<Contestant> getDefeated(
			TreeMap<Integer, Vector<Contestant>> p_rank, 
			TreeMap<Contestant, Vector<BallotIterator>> p_stacks)
	{
		/* TODO: Needs bounds checking! */
		Vector<Contestant> l_defeated = new Vector<Contestant>();
		Vector<Contestant> l_lowCans, l_upCan;
		Integer l_curKey = p_rank.lastKey();
		if (l_curKey == null || p_rank.lowerKey(l_curKey) == null)
		{
			return l_defeated;
		}
		l_lowCans = p_rank.get(l_curKey);
		l_upCan = p_rank.get(p_rank.lowerKey(l_curKey));
		//Only need 1 upcan
		l_upCan.setSize(1);
		Integer l_lowTot, l_upTot;
		l_lowTot = getContestantTotals(l_lowCans, p_stacks);
		l_upTot = getContestantTotals(l_upCan, p_stacks);
		//System.out.println(l_lowTot + ", " + l_upTot);
		while (l_lowTot < l_upTot)
		{
			l_defeated.addAll(l_lowCans);
			
			l_curKey = p_rank.lowerKey(l_curKey);
			if (l_curKey == null || p_rank.lowerKey(l_curKey) == null) break;
			l_lowCans = p_rank.get(l_curKey);
			l_upCan = p_rank.get(p_rank.lowerKey(l_curKey));
			//Only need 1 upcan
			l_upCan.setSize(1);

			l_lowTot += getContestantTotals(l_lowCans, p_stacks);
			l_upTot = getContestantTotals(l_upCan, p_stacks);
		}
		
		return l_defeated;
	}
	
	/**
	 * Travels up the results tree matching the current execution context. 
	 * Looks to see if at one point one of the candidates had fewer votes than 
	 * all the others during that round. If true, the ranks are ordered and 
	 * returned, if not, null is returned to signify an unbreakable tie.
	 * 
	 * This method assumes a single chain of execution, do not send multiple 
	 * chains to it or it will freak out.
	 * 
	 * @param p_tied
	 * @param p_rounds
	 * @return
	 */
	private Contestant breakTie(Vector<Contestant> p_tied,
								IRVContestResult.Round p_curRound,
								Vector<IRVContestResult.Round> p_rounds)
	{
		//Sanity check
		if (p_tied.size() <= 1)	return null;
		
		//Tree Travel!
		Contestant l_lowest, l_nextLowest;
		for (int l_i = p_rounds.size()-1; l_i >= 0; l_i--)
		{
			IRVContestResult.Round l_tieRound = p_rounds.get(l_i);
			if (p_curRound.getDesc().startsWith(l_tieRound.getDesc())
					&& p_curRound.getId() > l_tieRound.getId())
			{				
				Vector<Integer> l_curTotals = l_tieRound.getTotals(); 
				//Compare each candidate, see if there is a lowest.
				l_lowest = p_tied.firstElement();
				l_nextLowest = p_tied.get(1); 
				int l_lowSize = l_curTotals.get(l_lowest.getId());
				for (int l_j = 1; l_j < p_tied.size(); l_j++)
				{
					if (l_lowSize > l_curTotals.get(p_tied.get(l_j).getId()))
					{
						l_nextLowest = l_lowest;
						l_lowest = p_tied.get(l_j);
						l_lowSize = l_curTotals.get(p_tied.get(l_j).getId());
					}
				}
				//Did we actually find a lowest Candidate?
				if (l_curTotals.get(l_lowest.getId()) 
						< l_curTotals.get(l_nextLowest.getId()))
				{
					return l_lowest;
				}
				//Else try again.
			}	
		}
		
		return null;
	}
	
	private TreeMap<Contestant, Vector<BallotIterator>> copyStack(
					TreeMap<Contestant, Vector<BallotIterator>> l_stack)
	{
		TreeMap<Contestant, Vector<BallotIterator>> l_new;
		l_new = new TreeMap<Contestant, Vector<BallotIterator>>();
		Contestant l_c = l_stack.firstKey();
		do
		{
			Vector<BallotIterator> l_bi;
			l_bi = new Vector<BallotIterator>();
			for (BallotIterator l_b : l_stack.get(l_c))
			{
				l_bi.add(new BallotIterator(l_b));
			}
			l_new.put(l_c, l_bi);
			
		} while ((l_c = l_stack.higherKey(l_c)) != null);
		
		return l_new;
	}

	/**
	 * Encapsulates iteration over a ballot.
	 * NOTE: BallotIterator Handles the following rules:
	 * 		Check down one rank to see if 1 is marked. If not, put in 
	 *      exhausted pile. 
	 * It does this simply by running false to hasNext, in which case
	 * it gets put in the bad pile.
	 * @author Richard Carback
	 *
	 */
	private class BallotIterator {

		protected int c_curPos = -1;
		protected int c_contestId = -1;
		protected Ballot c_ballot;
		protected Vector<Integer> c_nextCan = null;
		
		/**
		 * @param p_ballot
		 */
		public BallotIterator(Ballot p_ballot, Integer p_contestId)
		{
			//super();
			c_curPos = -1;
			c_nextCan = null;
			c_ballot = p_ballot;
			c_contestId = p_contestId;
		}		
		
		/**
		 * @param p_b
		 */
		public BallotIterator(BallotIterator p_b)
		{
			this(p_b.c_ballot, p_b.c_contestId);
			c_curPos = p_b.c_curPos;
			c_nextCan = p_b.c_nextCan;			
		}

		/**
		 * @return true if there are more (valid) ranks to count.
		 */
		public boolean hasNext() 
		{
			Integer l_bdata[][] = c_ballot.getContestData(c_contestId);
			
			//This should never happen, something really bad happened!
			if (l_bdata == null) return false;
			//Already checked before
			if (c_nextCan != null) return true;

			int l_skip = 0;
			Vector<Integer> l_can = new Vector<Integer>();
			while (l_can.size() == 0 && l_skip < 1)
			{
				c_curPos++;

				//Bounds check
				if (c_curPos < 0 || c_curPos >= l_bdata.length) {
					return false;
				}

				//Are there candidates in this column?
				for (int l_j = 0; l_j < l_bdata.length; l_j++)
				{
					if (l_bdata[l_j][c_curPos] == 1) 
					{	
						l_can.add(l_j);
					}
				}
				
				//We may skip up to 1 position when counting.
				l_skip++;		
			} 
						
			if (l_can.size() > 0) {
				c_nextCan = l_can;
				return true;
			}

			return false;
		}
		
		/**
		 * @return the next Candidate ID.
		 */
		public Vector<Integer> getNext()
		{
			Vector<Integer> l_res = null;
			if (hasNext()) l_res = c_nextCan;
			c_nextCan = null;
			return l_res;
		}
	}
}