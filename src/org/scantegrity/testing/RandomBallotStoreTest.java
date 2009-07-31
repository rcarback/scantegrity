/*
 * @(#)RandomBallotStoreTest.java.java
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
package org.scantegrity.testing;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Vector;

import org.scantegrity.lib.Ballot;
import org.scantegrity.lib.RandomBallotStore;

/**
 * @author Richard Carback
 *
 */
public class RandomBallotStoreTest
{

	/**
	 * @param args
	 * @throws IOException 
	 * @throws NoSuchAlgorithmException 
	 */
	public static void main(String[] args) throws IOException, NoSuchAlgorithmException
	{
		RandomBallotStore l_store = new RandomBallotStore("testballots2.sbr", null, null);
		l_store.create(10*1024*1024, 1024*4);
		RandomBallotCreator l_c = new RandomBallotCreator();
		Vector<Ballot> l_ballots = new Vector<Ballot>();
		Vector<Ballot> l_read = new Vector<Ballot>();
		
		
		for (int l_i = 0; l_i < 1; l_i++)
		{
			l_ballots.add(l_c.getBallot());
			l_store.addBallot(l_ballots.lastElement());
		}
		
		l_store.close();
		
		l_store.open();
		l_read = l_store.getBallots();
		
		for (int l_i = 0; l_i < l_ballots.size(); l_i++)
		{
			if (!l_ballots.contains(l_read.get(l_i))) {
				System.out.println("Ballot Missing: " + l_read.get(l_i).getId());
			}
			else
			{
				System.out.println("Ballot Found: " + l_read.get(l_i).getId());	
			}
		}		
		
		l_store.close();
		
		
	}

}
