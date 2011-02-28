/*
 * @(#)SymbolFactory.java
 *  
 * Copyright (C) Scantegrity Project
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

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Vector;

/**
 * SymbolFactory generates confirmation numbers for Scantegrity ballots.  
 * SymbolFactory should only have 1 instance, and is implemented as a singleton. 
 * 
 * Users of this class should never reseed or reuse the RNG they send to this 
 * class while they are using it, as it is important to reproduce symbols in the 
 * exact order during subsequent operations in the voting protocol. 
 * 
 * 	TODO: Set a limit on the size of the pre-generated code list, and fall back to a 
 * slower generation algorithm if necessary. Current length min is 1 and max is 10,
 * so unless the symbol list gets really big this should not be a problem.
 * 
 * TODO: Add minimum entropy requirement on this class. If any combination of
 * settings causes us to dip below, say 10, possible elements, then throw an 
 * exception.
 * 
 * TODO: Remove error msgs to constants at the top of the class.
 * 
 * @author Richard Carback
 * @version 0.0.1
 * @date 2/27/11
 */

public enum SymbolFactory {
	INSTANCE;
	
	private String c_symbols = "0123456789";
	private Integer c_codeLen = 3;
	private String[] c_forbidden = { "" };
	private String[] c_codeList = null;
		
	/**
	 * Use the given SPRNG to generate p_numCodes unique code words. 
	 * 
	 * You must use the SPRNG consistently with the same seed if you want to 
	 * reproduce the same code words. This class assumes you have properly 
	 * seeded the generator.
	 * 
	 * @param p_sprng a pre-seeded secure pseudo-random number generator.
	 * @param p_num the number of unique codes to produce.
	 * @throws Exception 
	 */ 
	public String[] getCodes(SecureRandom p_sprng, int p_num) throws Exception
	{
		//Could potentially make this unsafe and not use these check conditions 
		//(save overhead if necessary)
		if (c_codeList == null) genCodeList();		
		if (p_num > c_codeList.length) throw new Exception("SymbolGenerator: Cannot produce " + p_num + " unique codes with current settings!");
		
		// Note: this gets called a lot, it needs to be fast, and it can't introduce bias!
		//Current implementation is probably not as fast as it could be...
		
		//TODO: Not sure if 2*p_num is the best idea.
		HashMap<Integer, Integer> l_shuf = new HashMap<Integer, Integer>(2*p_num);
		String[] l_codes = new String[p_num];
		int l_t = 0;
		for (int i = 0; i < p_num; i++)
		{
			//"nextInt" claims to be uniformly distributed...
			l_t = p_sprng.nextInt(c_codeList.length-i);
			
			//If it exists in the hashmap, take the index in the hasmap, this is
			// an old "max" value.
			if (l_shuf.get(l_t) != null)
			{
				l_codes[i] = c_codeList[l_shuf.get(l_t)];
			}
			else
			{
				l_codes[i] = c_codeList[l_t];
			}
			//Set this position to the current max, if the alg already selected max this
			//doesn't do anything since max won't come up again.
			if (l_shuf.get(c_codeList.length-i-1) == null) 
			{
				l_shuf.put(l_t, c_codeList.length-i-1);
			}
			else
			{
				l_shuf.put(l_t, l_shuf.get(c_codeList.length-i-1));
			}
		}
		
		return l_codes;
	}

	/**
	 * Generate each possible code word, and remove all the forbidden code 
	 * words. This can potentially cause memory issues, but since we usually
	 * limit code lengths, the numbers are generally small. 
	 * @throws Exception 
	 */
	private void genCodeList() throws Exception
	{
		Vector<String> l_tmp = new Vector<String>();
		
		char[] l_cur = new char[c_codeLen];
		char[] l_last = new char[c_codeLen];
		int[] l_ctrs = new int[c_codeLen];
		
		for (int i = 0; i  < c_codeLen; i++)
		{
			l_cur[i] = c_symbols.charAt(0);
			l_last[i] = c_symbols.charAt(c_symbols.length()-1);
			l_ctrs[i] = 0;
		}
		
		//This particular loop produces each permutation in inverse of the
		//way you might do it on paper (easier to start at 0 vice len-1).
		while (!java.util.Arrays.equals(l_cur, l_last))
		{
			l_tmp.add(new String(l_cur));
			
			boolean l_cascade = true;
			int i = 0; 
			while (l_cascade == true && i < c_codeLen)
			{
				l_ctrs[i]++;
				if (l_ctrs[i] < c_symbols.length())
				{
					l_cascade = false; //This breaks the loop.
				}
				else
				{
					l_ctrs[i] = 0;
				}
				l_cur[i] = c_symbols.charAt(l_ctrs[i]);
				i++;
			}
		}
		l_tmp.add(new String(l_cur));
		
		//Remove forbidden
		for (int i = 0; i < c_forbidden.length; i++)
		{
			l_tmp.remove(c_forbidden[i]);			
		}
		
		if (l_tmp.size() <= 0) throw new Exception("SymbolGenerator: cannot produce any code words.");
		
		c_codeList = l_tmp.toArray(new String[l_tmp.size()]);
	}
	
	
	/**
	 * Set the symbols produced by this class. Length of the string must be > 0 
	 * and duplicate characters will be removed from the string.
	 * 
	 * @param p_symbols the symbols used to generate the codes.
	 * @throws Exception 
	 */	
	public void setSymbols(String p_symbols) throws Exception {
		if (p_symbols.length() > 0)
		{
			//Remove duplicates in the String
			char[] l_sorted = p_symbols.toCharArray();
			java.util.Arrays.sort(l_sorted);
			c_symbols = "" + l_sorted[0];
			for (int i = 1; i < l_sorted.length; i++)
			{
				while(l_sorted[i-1] == l_sorted[i]) i++;
				if (i < l_sorted.length)
				{
					c_symbols += l_sorted[i];
				}
			}
		}
		else
		{
			throw new Exception("SymbolGenerator: Cannot use a zero-length string.");
		}
	}
	
	/**
	 * Get the current symbol list.
	 * 
	 * @return the symbols used to generate codes.
	 */
	public String getSymbols() {
		return c_symbols;
	}
	
	/**
	 * Forbidden strings that should never be produced by the generator. 
	 * 
	 * @param p_forbidden an array of forbidden codes
	 */
	public void setForbidden(String[] p_forbidden) {
		c_forbidden = p_forbidden;
	}
	
	/**
	 * Get the current list of forbidden code words.
	 * 
	 * @return the forbidden code words.
	 */
	public String[] getForbidden() {
		return c_forbidden;
	}
	
	/**
	 * Set the length of each code word. Min 1 and Max 10. 
	 * 
	 * @param codeLen the codeLen to set
	 */
	public void setCodeLen(Integer p_codeLen) {
		if (p_codeLen > 10) p_codeLen = 10;
		if (p_codeLen < 1) p_codeLen = 1;
		c_codeLen = p_codeLen;
	}

	/**
	 * Get the length of each code word.
	 * 
	 * @return the codeLen
	 */
	public Integer getCodeLen() {
		return c_codeLen;
	}
}
