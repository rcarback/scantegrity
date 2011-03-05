/*
 * @(#)Commitment.java
 *  
 * Copyright (C) 2008 Scantegrity Project
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

package org.scantegrity.crypto;


/**
 * Commitment is a container class for holding the cryptographic data generated 
 * by a commitment scheme.
 * 
 * @author Travis Mayberry
 * @version 0.0.1
 * @date June 5th, 2010
 */
public class Commitment {

	public byte[] c_commitment;
	public byte[] c_randSeed;
	
	public Commitment(byte[] p_commitment, byte[] p_randSeed)
	{
		c_commitment = p_commitment;
		c_randSeed = p_randSeed;
	}
	
}
