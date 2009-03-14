/*
 * @(#)MarkRules.java.java
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

/**
 * MarkRules encapsulates the marking rules for a contest. This class is used
 * by the scanner and by the tally method class to determine correctness of the 
 * ballot marks. 
 *  
 * @author Richard Carback
 */
public interface MarkRules
{
	
	/**
	 * validateMarks simply takes mark data and runs a validation routine. 
	 *
	 * States could be:
	 * 	- No vote, unable to find an indication of preference.
	 *  - Over vote, more candidates are selected than are permitted.
	 *  - Partial vote, at least one of the candidates has been voted for but not
	 *  all. Error codes refer to error messages that can be printed out by
	 *  the Scanner GUI or Tally Method reading the system. 
	 *  
	 * @param p_marks the ballot marks read by the scanner
	 * @return A list of error codes, if any.
	 */
	public Integer[] validateMarks(Integer[][] p_marks);
	
	/**
	 * The tally method class uses it to get a selection, but the semantic meaning 
	 * of this function changes based on the type. In plurality, this is the
  	 * chosen candidate, and in Approval each selection indicates an individual 
 	 * candidate and if he is selected (so the tally must differentiate between 
 	 * them). Similarly, in IRV the selection index is the rank, and in range, 
 	 * the selection index is each candidates score. 
 	 * 
	 * @param p_index the index of the desired selection.
	 * @return
	 */
	public Integer[] getSelections(Integer[][] p_marks);
}
