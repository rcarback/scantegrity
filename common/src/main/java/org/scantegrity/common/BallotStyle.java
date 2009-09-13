/*
 * @(#)BallotStyle.java
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
 * Every election has multiple ballot styles. The style indicates the contests a
 * voter is permitted to vote for and may also indicate other types of state. 
 * For example, there may be a race for president, but also one for the dog 
 * catcher for the local ward. If the polling place services multiple wards, 
 * then there must be a specific ballot style for that ward. Likewise, a voter
 * may not be registered to vote, and policy would dictate that they receive a 
 * provisional ballot. That provisional ballot indicates that the ballot should
 * be noted but not recorded in the final tally.
 *  
 *  TODO: This class needs to represent each *possible* marking position on 
 *  each contest. Maybe that's something that happens in the Contest object, 
 *  i'm not sure (probably not, but it might be nice to have a special helper class
 *  that does the mapping instead). 
 *  
 *  
 * @author Richard Carback
 * @version 0.0.1 
 * @date 11/03/09
 */
package org.scantegrity.common;

import java.awt.Rectangle;
import java.util.Vector;

public class BallotStyle {
	private int c_id;
	//An ordered list of contests on this ballot style
	private Vector<Integer> c_contests;
	//An array of contestandIds ordered as listed on the ballot.
	private Vector<Vector<Integer>> c_contestantIds;
	//An array of positions that are write-in positions.
	private Vector<Vector<Integer>> c_writeIns;
	//A list of the x,y locations and sizes of contests on the ballot image
	private Vector<Vector<Vector<Rectangle>>> c_contestRects;
	//A list of the x,y locations and sizes of write in locs on the ballot image
	private Vector<Vector<Vector<Rectangle>>> c_writeInRects;
	//Should this ballot be counted at the scanner?
	private boolean c_counted;
	
	/**
	 * Default constructor creates an invalid BallotStyle.
	 */
	public BallotStyle() {
		this(-1, false);
	}
	
	/**
	 * Creates a valid BallotStyle with null members.
	 * 
	 * @param p_id
	 * @param p_counted
	 */
	public BallotStyle(int p_id, boolean p_counted) {
		this(p_id, null, null, p_counted);
	}	
	/**
	 * Creates a Ballot Style.
	 * 
	 * @param p_id
	 * @param p_contests
	 * @param p_l_rects
	 * @param p_counted
	 */
	public BallotStyle(int p_id, Vector<Integer> p_contests,
			Vector<Vector<Vector<Rectangle>>> p_l_rects, boolean p_counted)
	{
		super();
		c_id = p_id;
		c_contests = p_contests;
		c_contestRects = p_l_rects;
		c_counted = p_counted;
	}
	
	/**
	 * @param id the id to set
	 */
	public void setId(int id)
	{
		c_id = id;
	}
	/**
	 * @return the id
	 */
	public int getId()
	{
		return c_id;
	}
	
	/**
	 * @param contests the contests to set
	 */
	public void setContests(Vector<Integer> contests)
	{
		c_contests = contests;
	}
	/**
	 * @return the contests
	 */
	public Vector<Integer> getContests()
	{
		return c_contests;
	}
	
	/**
	 * @return the contestantIds
	 */
	public Vector<Vector<Integer>> getContestantIds()
	{
		//The default is ordered.
		if (c_contestantIds == null && c_contestRects != null) 
		{
			c_contestantIds = new Vector<Vector<Integer>>();
			for (int l_i = 0; l_i < c_contestRects.size(); l_i++)
			{
				c_contestantIds.add(new Vector<Integer>());
				for (int l_j = 0; l_j < c_contestRects.elementAt(l_i).size(); l_j++)
				{
					c_contestantIds.elementAt(l_i).add(l_j);
				}
			}
		}
		return c_contestantIds;
	}

	/**
	 * @param p_contestantIds the contestantIds to set
	 */
	public void setContestantIds(Vector<Vector<Integer>> p_contestantIds)
	{
		c_contestantIds = p_contestantIds;
	}

	/**
	 * @param contestRects the contestRects to set
	 */
	public void setContestRects(Vector<Vector<Vector<Rectangle>>> contestRects)
	{
		c_contestRects = contestRects;
	}
	/**
	 * @return the contestRects
	 */
	public Vector<Vector<Vector<Rectangle>>> getContestRects()
	{
		return c_contestRects;
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
	 * @param writeIns the writeIns to set
	 */
	public void setWriteIns(Vector<Vector<Integer>> writeIns) {
		c_writeIns = writeIns;
	}

	/**
	 * @return the writeIns
	 */
	public Vector<Vector<Integer>> getWriteIns() {
		return c_writeIns;
	}

	/**
	 * @param writeInRects the writeInRects to set
	 */
	public void setWriteInRects(Vector<Vector<Vector<Rectangle>>> writeInRects) {
		c_writeInRects = writeInRects;
	}

	/**
	 * @return the writeInRects
	 */
	public Vector<Vector<Vector<Rectangle>>> getWriteInRects() {
		return c_writeInRects;
	}

	
}