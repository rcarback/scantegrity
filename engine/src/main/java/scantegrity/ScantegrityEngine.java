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
		c_tableR = new RTable(p_confCodes.length, p_confCodes[0].length);
		c_tableQ = new String[p_confCodes.length][p_confCodes[0].length];
		
		for( int x = 0; x < p_confCodes.length; x++ )
		{
			//Initialize with a shuffled version of the confirmation codes
			c_tableQ[x][0] = p_confCodes[x][0];

			for( int y = 1; y < p_confCodes[x].length; y++ )
			{
				int index = c_rand.nextInt(y + 1);
				c_tableQ[x][y] = c_tableQ[x][index];
				c_tableQ[x][index] = p_confCodes[x][y];
			}
		}
	}
	
}
