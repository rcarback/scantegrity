/*
 * @(#)PRNGCommitmentScheme.java
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

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;


public class PRNGCommitmentScheme implements CommitmentScheme {

	byte[] c_challenge;
	SecureRandom c_rand;
	
	public PRNGCommitmentScheme()
	{
		c_rand = new SecureRandom();
	}
	
	public void setup(byte[] p_challenge)
	{
		c_challenge = p_challenge.clone();
	}
	
	//http://www.springerlink.com/index/N615G60560417356.pdf
	
	public Commitment commit(byte[] data) throws Exception {
		if( c_challenge == null )
		{
			c_challenge = new byte[4096];
			c_rand.nextBytes(c_challenge);
		}
		
		if( data.length > c_challenge.length )
			throw new Exception("Message is longer than challenge input");
		
		//Generate the random seed for the PRNG and store it for use with decommit
		byte[] l_randSeed = new byte[c_challenge.length];
		c_rand.nextBytes(l_randSeed);
		
		//Generate the random bits to XOR with the data
		SecureRandom l_rand = SecureRandom.getInstance("SHA1PRNG");
		l_rand.setSeed(l_randSeed);
		byte[] l_randBytes = new byte[c_challenge.length * 4];
		l_rand.nextBytes(l_randBytes);
		
		//Multiply the data by the challenge
		BigInteger l_challengeBig = new BigInteger(1, c_challenge);
		BigInteger l_dataBig = new BigInteger(1, data);
		byte[] l_res = l_challengeBig.multiply(l_dataBig).toByteArray();
		
		for(int x = 0; x < l_randBytes.length; x++ )
		{
			if( x < l_res.length )
				l_randBytes[x] = (byte) (l_randBytes[x] ^ l_res[x]);
		}
		
		return new Commitment(l_randBytes, l_randSeed);
		
	}

	public boolean decommit(byte[] data, Commitment comm) {
		//Generate the random bits to XOR with the data
		SecureRandom l_rand = null;
		try {
			l_rand = SecureRandom.getInstance("SHA1PRNG");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		l_rand.setSeed(comm.c_randSeed);
		byte[] l_randBytes = new byte[c_challenge.length * 4];
		l_rand.nextBytes(l_randBytes);
		
		//Multiply the data by the challenge
		BigInteger l_challengeBig = new BigInteger(1, c_challenge);
		BigInteger l_dataBig = new BigInteger(1, data);
		byte[] l_res = l_challengeBig.multiply(l_dataBig).toByteArray();
		
		for(int x = 0; x < l_randBytes.length; x++ )
		{
			if( x < l_res.length )
				l_randBytes[x] = (byte) (l_randBytes[x] ^ l_res[x]);
		}
		
		return Arrays.equals(l_randBytes, comm.c_commitment);
	}


}
