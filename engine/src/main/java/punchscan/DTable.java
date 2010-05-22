/**
 * This is the DTable for the punchscan backend. 
 */
package punchscan;

import java.util.Random;

import commitment.Commitment;
import commitment.CommitmentScheme;

/**
 * @author jay12701
 *
 */
public class DTable {
	static final String DEFAULT_RAND_PROVIDER = "SUN";
	static final String DEFAULT_RAND_ALG = "SHA1PRNG";
	
	private DRow c_dTable[]; 
	private Random c_rand;
	
	public DTable(int p_numBallots, Random p_rand)
	{
		/*
		 * Each row in the table will be following: 
		 * 	Ballot ID, PermA, intermediate, PermB, Ptr to Results
		 */
		c_dTable = new DRow[p_numBallots];
		
		c_rand = p_rand;  
	}
	
	public void shuffle()
	{	
		 // i is the number of items remaining to be shuffled.
	    for (int i = c_dTable.length; i > 1; i--) {
	        // Pick a random element to swap with the i-th element.
	        int j = c_rand.nextInt(i);  // 0 <= j <= i-1 (0-based array)
	        // Swap array elements.
	        DRow tmp = c_dTable[j];
	        c_dTable[j] = c_dTable[i-1];
	        c_dTable[i-1] = tmp;
	    }	
	}

	public Byte[] colToByte(int p_col)
	{
		return null;
	}

	public void add(DRow p_row, int p_ballotID) 
	{
		c_dTable[p_ballotID] = p_row; 
	}
	
	public DRow getRow(int index)
	{
		return c_dTable[index];
	}
	
	public void commitRows(CommitmentScheme p_cs)
	{
		for(int i = 0; i < c_dTable.length; i++)
		{
			try {
				c_dTable[i].setCommit(p_cs.commit(c_dTable[i].toByte()));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}
	}
	
	public Commitment[] commitToRows(CommitmentScheme p_cs)
	{
		Commitment l_commits[] = new Commitment[c_dTable.length];
		
		for(int i = 0; i < c_dTable.length; i++)
		{
			try {
				l_commits[i] = p_cs.commit(c_dTable[i].toByte()); 
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}
		
		return l_commits; 
	}
	
	public Commitment[] commitCols(CommitmentScheme p_cs)
	{
		//we are commiting to two columns. 
		Commitment l_commits[] = new Commitment[4]; 
	
		String l_ballotID = "";
		String l_permA = ""; 
		String l_permB = ""; 
		String l_resPtr = ""; 
		
		for(int i = 0; i < c_dTable.length; i++)
		{
			DRow l_row = c_dTable[i]; 
			
			l_ballotID += String.valueOf(l_row.getBallotID());
			l_permA += l_row.permToString(l_row.getPermA()); 
			l_permB += l_row.permToString(l_row.getPermB());
			//TODO get resPTr String
		}
		
		//commit to all the columns
		try {
			l_commits[0] = p_cs.commit(l_ballotID.getBytes());
			l_commits[1] = p_cs.commit(l_permA.getBytes()); 
			l_commits[2] = p_cs.commit(l_permB.getBytes()); 
			l_commits[3] = p_cs.commit(l_resPtr.getBytes()); 
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return l_commits;
		
	}
}
