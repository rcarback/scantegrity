/*
 * @(#)Contest.java.java
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
package org.scantegrity.lib;

import java.util.Vector;

import org.scantegrity.lib.methods.TallyMethod;
/**
 * Contest describes a race, question, or other contest that appears in an 
 * election. This description includes a list of options or names in the contest,
 * an ID number, the rules for this contest, and instructions on how to tally 
 * this contest.
 * 
 * @author Richard Carback
 *
 */
public class Contest
{
	private String c_contestName;
	private Integer c_id;
	private Vector<Contestant> c_contestants;
	//private MarkRules c_rules;
	private TallyMethod c_method;
	
	/**
	 * @return the contestName
	 */
	public String getContestName()
	{
		return c_contestName;
	}
	/**
	 * @param p_contestName the contestName to set
	 */
	public void setContestName(String p_contestName)
	{
		c_contestName = p_contestName;
	}
	/**
	 * @return the id
	 */
	public Integer getId()
	{
		return c_id;
	}
	/**
	 * @param p_id the id to set
	 */
	public void setId(Integer p_id)
	{
		c_id = p_id;
	}
	/**
	 * @return the options
	 */
	public Vector<Contestant> getContestants()
	{
		return c_contestants;
	}
	/**
	 * @param p_options the options to set
	 */
	public void setContestants(Vector<Contestant> p_options)
	{
		c_contestants = p_options;
	}
	/**
	 * @return the rules
	 *//*
	public MarkRules getRules()
	{
		return c_rules;
	}
	/**
	 * @param p_rules the rules to set
	 *//*
	public void setRules(MarkRules p_rules)
	{
		c_rules = p_rules;
	}
	/**
	 * @return the method
	 */
	public TallyMethod getMethod()
	{
		return c_method;
	}
	/**
	 * @param p_method the method to set
	 */
	public void setMethod(TallyMethod p_method)
	{
		c_method = p_method;
	}
}
