/*
 * @(#)ContestResult.java.java
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

import java.util.TreeMap;

/**
 * ContestResults represents the results of a Contest. This is, at the basic
 * level, simply a mapping of candidates to their final rankings. Extending 
 * classes store 
 * 
 * @author Rick Carback
 * @param <TreeMap>
 *
 */
public class ContestResult
{
	protected TreeMap<Integer, TreeMap<Integer, String>> c_ranking;

	/**
	 * @return the ranking
	 */
	public TreeMap<Integer, TreeMap<Integer, String>> getRanking()
	{
		return c_ranking;
	}

	/**
	 * @param p_ranking the ranking to set
	 */
	public void setRanking(TreeMap<Integer, TreeMap<Integer, String>> p_ranking)
	{
		c_ranking = p_ranking;
	}
	
	
}
