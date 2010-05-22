/**
 * This is the results table
 */
package punchscan;

import java.util.Random;

/**
 * @author jay12701
 *
 */
public class RTable {
	RRow c_rTable[];
	Random c_rand; 
	
	public RTable(int p_numBallots, Random p_rand)
	{
		c_rTable = new RRow[p_numBallots];
		c_rand = p_rand; 
	}
	
	public void addResult(int p_ballotID, RRow p_result)
	{
		c_rTable[p_ballotID] = p_result; 
	}
	
	public void shuffle()
	{
		 // i is the number of items remaining to be shuffled.
	    for (int i = c_rTable.length; i > 1; i--) {
	        // Pick a random element to swap with the i-th element.
	        int j = c_rand.nextInt(i);  // 0 <= j <= i-1 (0-based array)
	        // Swap array elements.
	        RRow tmp = c_rTable[j];
	        c_rTable[j] = c_rTable[i-1];
	        c_rTable[i-1] = tmp;
	    }
	}
}
