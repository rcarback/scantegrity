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
package org.scantegrity.scanner;

import java.awt.Rectangle;
import java.util.List;

public class BallotStyle {
	private int c_id;
	//An ordered list of contests on this ballot style
	private List<Integer> c_contests;
	//A list of the x,y locations and sizes of contests on the ballot image
	private List<Rectangle> c_contestRects;
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
	 * @param p_contestRects
	 * @param p_counted
	 */
	public BallotStyle(int p_id, List<Integer> p_contests,
			List<Rectangle> p_contestRects, boolean p_counted)
	{
		super();
		c_id = p_id;
		c_contests = p_contests;
		c_contestRects = p_contestRects;
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
	public void setContests(List<Integer> contests)
	{
		c_contests = contests;
	}
	/**
	 * @return the contests
	 */
	public List<Integer> getContests()
	{
		return c_contests;
	}
	
	/**
	 * @param contestRects the contestRects to set
	 */
	public void setContestRects(List<Rectangle> contestRects)
	{
		c_contestRects = contestRects;
	}
	/**
	 * @return the contestRects
	 */
	public List<Rectangle> getContestRects()
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

	
}