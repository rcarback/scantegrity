/**
 * This is a row in the results table
 */
package punchscan;

/**
 * @author jay12701
 *
 */
public class RRow {
	int c_results[]; 
	int c_ballotID; 
	
	public RRow(int p_ballotID)
	{
		c_ballotID = p_ballotID;
		c_results = null;
	}

	public void setResult(int[] p_results) {
		c_results = p_results;
	}

}
