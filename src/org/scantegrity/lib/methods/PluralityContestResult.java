/*
 * @(#)PluralityContestResult.java.java
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

import java.util.Vector;

/**
 * @author John Conway
 *
 */
public class PluralityContestResult extends ContestResult
{
	Vector<Integer> c_totals;
	
	/**
	 *  Create a new plurality contest result
	 */
	public PluralityContestResult()
	{
		super();
		c_totals = null;
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
	
	public String getHtmlResults()
	{
		return toString();
	}

	public String toString()
	{
		String l_res = "";
		Integer l_key = super.c_ranking.firstKey();
		int l_i = 0;
		l_res = "<table style=\"width: 100%; text-align: left;\">";
		l_res += "<tr><th>Candidate</th><th>Votes</th>";
		while (l_key != null)
		{
			l_res += "<tr><td>" + super.c_ranking.get(l_key).toString() + "</td>";
			l_res += "<td>" + c_totals.get(l_i) + "</td></tr>";
			l_key = super.c_ranking.higherKey(l_key);
			l_i++;
		}
		l_res += "</table>";
		
		return l_res;
	}
	
	
}
