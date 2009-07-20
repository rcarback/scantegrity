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
	 */
	public static void main(String[] args) throws IOException
	{
		RandomBallotStore l_store = new RandomBallotStore("testballots.sbr");
		l_store.create(4096, 64);
		l_store.close();
		
		l_store.open();
		l_store.close();
	}

}
