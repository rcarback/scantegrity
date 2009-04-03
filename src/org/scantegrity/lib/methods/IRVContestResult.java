/*
 * @(#)IRVContestRest.java.java
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
package org.scantegrity.lib.methods;

import java.io.StringWriter;
import java.util.Vector;

/**
 * 
 * @author Richard Carback
 *
 */
public class IRVContestResult extends ContestResult
{	
	protected Integer c_contestId = -1;
	protected Vector<Contestant> c_contestants = null;
	protected Vector<Round> c_rounds = null;
	
	public IRVContestResult()
	{
		this (-1, null);
	}
	
	/**
	 * @param p_l_rounds
	 */
	public IRVContestResult(Integer p_contestId, 
							Vector<Contestant> p_contestants)
	{
		super();
		p_contestId = c_contestId;
		this.setCandidates(p_contestants);
		c_rounds = new Vector<Round>();		
	}
	
	public void addRound(Round p_round)
	{
		c_rounds.add(p_round);
	}

	/**
	 * @return the l_rounds
	 */
	public Vector<Round> getRounds()
	{
		return c_rounds;
	}

	/**
	 * @param p_l_rounds the l_rounds to set
	 */
	public void setRounds(Vector<Round> p_rounds)
	{
		c_rounds = p_rounds;
	}
	
	/**
	 * @return the candidates
	 */
	public Vector<Contestant> getContestants()
	{
		return c_contestants;
	}
	/**
	 * @param p_candidates the candidates to set
	 */
	public void setCandidates(Vector<Contestant> p_contestants)
	{
		c_contestants = new Vector<Contestant>(p_contestants);
	}
	
	/**
	 * @return the contestId
	 */
	public Integer getContestId()
	{
		return c_contestId;
	}

	/**
	 * @param p_contestId the contestId to set
	 */
	public void setContestId(Integer p_contestId)
	{
		c_contestId = p_contestId;
	}

	public class Round {
		protected int c_id;
		protected Vector<Integer> c_totals;
		protected Vector<Integer> c_delta;
		protected Vector<String> c_state;
		protected Vector<String> c_roundNotes;

		public Round()
		{
			this(-1, null, null, null, null);
		}
		
		/**
		 * Only used on the first instantiate. The rest should use the
		 * next instantiation.
		 * @param p_id
		 */
		public Round(int p_id)
		{
			c_id = p_id;
			c_totals = new Vector<Integer>();
			c_delta = new Vector<Integer>();
			c_state = new Vector<String>();
			c_roundNotes = new Vector<String>();
			
			c_totals.setSize(c_contestants.size());
			c_delta.setSize(c_contestants.size());
			c_state.setSize(c_contestants.size());
			for (int l_i = 0; l_i < c_state.size(); l_i++)
			{
				if (c_contestants.get(l_i).c_id >= 0)
					c_state.set(l_i, "CONTINUES");
				else
					c_state.set(l_i, "EXHAUST");
			}
		}

		public Round(Round p_prev)
		{
			c_id = p_prev.getId()+1;
			c_totals = new Vector<Integer>(p_prev.getTotals());
			c_delta = new Vector<Integer>();
			c_state = new Vector<String>(p_prev.getState());
			c_roundNotes = new Vector<String>();
			
			c_delta.setSize(c_contestants.size());
		}

		/**
		 * @param p_id
		 * @param p_totals
		 * @param p_delta
		 * @param p_state
		 * @param p_roundNotes
		 */
		public Round(int p_id, Vector<Integer> p_totals, 
				Vector<Integer> p_delta, Vector<String> p_state, 
				Vector<String> p_roundNotes)
		{
			c_id = p_id;
			c_totals = p_totals;
			c_delta = p_delta;
			c_state = p_state;
			c_roundNotes = p_roundNotes;
		}		
		/**
		 * @return the id
		 */
		public int getId()
		{
			return c_id;
		}
		/**
		 * @param p_id the id to set
		 */
		public void setId(int p_id)
		{
			c_id = p_id;
		}

		/**
		 * @return the totals
		 */
		public Vector<Integer> getTotals()
		{
			return c_totals;
		}
		/**
		 * @param p_totals the totals to set
		 */
		public void setTotals(Vector<Integer> p_totals)
		{
			c_totals = p_totals;
		}
		/**
		 * @return the lastTot
		 */
		public Vector<Integer> getDelta()
		{
			return c_delta;
		}
		/**
		 * @param p_lastTot the lastTot to set
		 */
		public void setDelta(Vector<Integer> p_delta)
		{
			c_delta = p_delta;
		}
		/**
		 * @return the state
		 */
		public Vector<String> getState()
		{
			return c_state;
		}
		/**
		 * @param p_state the state to set
		 */
		public void setState(Vector<String> p_state)
		{
			c_state = p_state;
		}
		/**
		 * @return the roundNotes
		 */
		public Vector<String> getRoundNotes()
		{
			return c_roundNotes;
		}
		/**
		 * @param p_roundNotes the roundNotes to set
		 */
		public void setRoundNotes(Vector<String> p_roundNotes)
		{
			c_roundNotes = p_roundNotes;
		}
		
		public void addNote(String p_note)
		{
			c_roundNotes.add(p_note);
		}
		
		public String toString()
		{
			StringWriter l_out = new StringWriter();
			int l_change = 0, l_tot = 0;
			//ROUND\t\tCONTESTANTS\t\tCHANGE\t\tTOTALS
			String l_fmt = "\t\t\t\t\t\t  RESULTS FOR ROUND %s \n\n";
			l_out.write(String.format(l_fmt, c_id));
			l_out.write("\t\tCONTESTANT\t\t      CHANGE\t\t\tTOTALS\t\t\t   STATE\n");
			l_fmt = "%26s";
			for(int l_i = 0; l_i < c_contestants.size(); l_i++)
			{
				l_out.write(String.format(l_fmt, c_contestants.get(l_i)));
				l_out.write(String.format("%+26d", c_delta.get(l_i)));
				l_out.write(String.format(l_fmt, c_totals.get(l_i)));
				l_out.write(String.format(l_fmt, c_state.get(l_i)));
				l_out.write("\n");
				
				l_change += c_delta.get(l_i);
				l_tot += c_totals.get(l_i);
			}
			l_out.write("\n\n");
			l_out.write(String.format(l_fmt, "TOTALS:"));
			l_out.write(String.format("%+26d", l_change));
			l_out.write(String.format(l_fmt, l_tot));
			l_out.write("\n\n");
			for (String note : c_roundNotes) l_out.write(note + "\n");
			
			return l_out.toString();
		}

	}

}