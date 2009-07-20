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

import java.beans.XMLEncoder;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
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
	private static String FILESTART = "RAF0";
	private static String INVALID_FORMAT = "Unrecognized File Format";
	private static String INVALID_SIZE = "Invalid file size parameter.";
	private static String INVALID_BLKSIZE = "Invalid block size parameter."; 
	private static String FILE_EXISTS = "File already exists.";
	private static int MAXTRIES = 3;
	private String c_fname = "";
	private RandomAccessFile c_file = null;
	private long c_btab[] = null;
	private int c_blksize = 0;
	private long c_fsize = 0;
		
	/**
	 * Create a new RandomBallotStore Object.
	 * 
	 * @param p_fname
	 */
	public RandomBallotStore(String p_fname)
	{
		c_fname = p_fname;
	}
	
	/**
	 * Open and set up a pre-existing ballot file. If this function detects
	 * a problem it will throw an IOException with a string that explains the
	 * specific error.
	 * 
	 * @return the number of ballots found in the file.
	 * @throws IOException
	 */
	public int open() throws IOException
	{
		c_file = new RandomAccessFile(c_fname, "rwd");
		
		if (c_file.length() < 20) 
		{
			throw new FileNotFoundException();
		}
		
		//Verify File Type
		byte l_fstart[] = new byte[FILESTART.length()];
		c_file.read(l_fstart);
		String l_fstartstr = new String(l_fstart);
		if (!l_fstartstr.equals(FILESTART))
		{
			throw new IOException(INVALID_FORMAT);
		}
		//If valid, read the next long for the file size.
		c_fsize = c_file.readLong();
		if (c_fsize < 0)
		{
			throw new IOException(INVALID_SIZE);
		}
		//Read the blksize
		c_blksize = c_file.readInt();
		if (c_blksize % 2 != 0)
		{
			throw new IOException(INVALID_BLKSIZE);
		}
		
		c_btab = new long[getTabSize(c_fsize, c_blksize)];
		int l_numBallots = 0;
		for (int i = 0; i < c_btab.length; i++)
		{
			c_btab[i] = c_file.readLong();
			if (c_btab[i] > 0) {
				l_numBallots++;
			}
		}
		return l_numBallots;
	}
	
	
	/**
	 * Using the size and blksize, create a new ballot file.
	 * 
	 * @param p_size
	 * @param p_blksize
	 * @return
	 * @throws IOException 
	 */
	public int create(long p_size, int p_blksize) throws IOException
	{
		c_file = new RandomAccessFile(c_fname, "rwd");
		if (c_file.length() != 0)
		{
			throw new IOException(FILE_EXISTS);
		}
		
		//Calculate true file size
		int l_tabSize = getTabSize(p_size, p_blksize);
		long l_evenSize = (p_size - FILESTART.length() - 16)/p_blksize;
		l_evenSize += l_tabSize;
		l_evenSize *= p_blksize;
		l_evenSize += FILESTART.length() + 16;
		c_file.setLength(l_evenSize);
		
		c_file.writeChars(FILESTART);
		c_file.writeLong(l_evenSize);
		c_file.writeInt(p_blksize);
		c_file.getFD().sync();
		
		System.out.println(l_evenSize + " " + p_blksize);
		
		return 0;
	}
	
	public void addBallot(Ballot p_ballot)
	{
		//How many blocks does the ballot need?
		ByteArrayOutputStream l_ballot = new ByteArrayOutputStream();
		XMLEncoder l_enc = new XMLEncoder(l_ballot);
		l_enc.writeObject(p_ballot);
		l_enc.close();
		int l_numBlks = l_ballot.size()/c_blksize;
		
		//Break up the blks
		ByteArrayOutputStream l_blks[] = new ByteArrayOutputStream[l_numBlks];
		for (int l_i = 0; l_i < l_numBlks; l_i++)
		{
			System.arraycopy(l_ballot, l_i*c_blksize, 
								l_blks[l_i], 0, c_blksize);
		}

		//hash each block to find it's position.
		
		
		//While the position is filled, continue hashing until an appropriate
		//position is found. If MAXTRIES is reached, throw an IOException
		
		//If a position is found, check to make sure it's actually all 0's
		//throw an IOException if it's not.
		
		//Convert the ballot into a serialized or XML Serialized object
		
		//Write the table entry
		//Write the block to the file.
		//Do the same process for the next block.
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
	
	public void close() throws IOException
	{
		c_file.close();
	}
	
	
	/**
	 * Calculates the size of the table needed to address all of the blocks in 
	 * the file.
	 * 
	 * TBD: This approximation could be more exact. There's no reason to use
	 * a whole block when a specific size is possible. 
	 * 
	 * Note, int should be safe here. I don't think we'll be addressing 
	 * more than 2 gigs of entries. 
	 *  
	 * @param p_fsize the File size.
	 * @param p_blksize the Block size.
	 * @return
	 */
	private int getTabSize(long p_fsize, long p_blksize)
	{
		//How many blks are possible?
		long l_numBlks = (p_fsize - FILESTART.length() - 16)/p_blksize;
		//How many entries can be stored in a blk?
		long l_entriesPerBlk = p_blksize/8;
		
		long l_tBlks = 1 + l_numBlks/l_entriesPerBlk;
		return (int)(l_tBlks*p_blksize);
	}
	
	
}
