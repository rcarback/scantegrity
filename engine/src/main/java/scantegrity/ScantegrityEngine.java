package scantegrity;

import java.security.SecureRandom;


public class ScantegrityEngine {	
	int c_numBallots; 
	int c_maxCandidates; 
	SecureRandom c_rand;
	RTable c_tableR;
	String[][] c_tableQ;	
	
	public ScantegrityEngine(SecureRandom p_rand)
	{
		c_rand = p_rand;
	}
	
	public void generate(String[][] p_confCodes)
	{
		generateQ(p_confCodes);
	}
	
	public void generateQ(String[][] p_confCodes)
	{
		int l_ballots = p_confCodes.length;
		int l_columns = p_confCodes[0].length;
		
		c_tableR = new RTable(l_ballots, l_columns, c_rand);
		c_tableQ = new String[l_ballots][l_columns];
		
		for( int x = 0; x < p_confCodes.length; x++ )
		{
			//Initialize with a shuffled version of the confirmation codes
			for( int y = 0;  y < p_confCodes[x].length; y++ )
			{
				c_tableQ[x][y] = p_confCodes[x][y];
			}

			for( int y = c_tableQ[x].length - 1; y > 1; y-- )
			{
				int index = c_rand.nextInt(y+1);
				
				String temp = c_tableQ[x][index];
				c_tableQ[x][index] = c_tableQ[x][y];
				c_tableQ[x][y] = temp;
				
				c_tableR.SwitchQ(y * l_columns + x, index * l_columns + x);
			}
		}
		
		c_tableR.Shuffle();
		//c_tableR.Test(c_tableQ);
	}
	
}
