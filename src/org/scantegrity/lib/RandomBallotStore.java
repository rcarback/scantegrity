/*
 * @(#)RandomBallotStore.java.java
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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.security.SecureRandom;
import java.util.Vector;

/**
 * Stores ballot objects at a random location in a large file. The intent is to
 * provide some anonymity by destroying the order in which ballots have been
 * entered into the system. 
 * 
 * The approach is to randomly place ballots in the file with hashing.
 * When a collision occurs, it tries again. When a threshold is reached, it 
 * attempts to place the ballot ahead of or behind the most recent collision.
 * 
 * A table at the beginning of the file records where each entry is located.
 * Ballots also begin with the string BALLOT and the size of the file as a 64 
 * bit number. This redundancy is meant to be helpful in file recovery 
 * situations.
 * 
 * 
 * @author Richard Carback
 *
 */
public class RandomBallotStore
{
	private int MAXTRIES = 3;
	private RandomAccessFile c_file = null;
	private SecureRandom c_csprng = null;
	private long c_btab[] = null;
	
	
	public RandomBallotStore(String p_fname, long p_size, 
								SecureRandom p_csprng) throws IOException
	{
		c_file = new RandomAccessFile(p_fname, "rwd");
		c_csprng = p_csprng;
		
		//Calculate the size of a ballot object, find out how many ballot positions
		//we can have for this file based on the size.

		//Round out the size, and set it
		c_file.setLength(p_size);
		
		//Read the file table block and see if ballots exist.
		//Load current ballot locations array.
		
	}
	
	public void addBallot(Ballot p_ballot)
	{
		//hash the ballot to find it's position.
		//While the position is filled, continue hashing until an appropriate
		//position is found. If MAXTRIES is reached, throw an IOException
		
		//If a position is found, check to make sure it's actually all 0's
		//throw an IOException if it's not.
		
		//Convert the ballot into a serialized or XML Serialized object
		
		//Write the table entry
		//Write the ballot to the file.
		//Force a Sync of the file w/ the disk.
	}
	
	public Vector<Ballot> getBallots()
	{
		//Read the table entries for ballots
		//convert each serialized ballot into a ballot object
		//Make sure the ballot object "makes sense"
		//Add the ballot into the list
		
		return new Vector<Ballot>();
	}
	
}
