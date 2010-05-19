package scantegrity;

import java.io.File;
import java.util.Random;

import commitment.CommitmentScheme;

public class ScantegrityFrontEnd {

	int c_ballots;
	Random c_rand;
	int c_candidates;
	File c_directory;
	ScantegrityEngine c_engine;
	CommitmentScheme c_cs;
	static final int CODE_LENGTH = 4;
	
	public ScantegrityFrontEnd( Random p_rand, int p_numBallots, int p_numCandidates, File p_directory, CommitmentScheme p_cs )
	{
		c_rand = p_rand;
		c_ballots = p_numBallots;
		c_candidates = p_numCandidates;
		c_directory = p_directory;
		c_cs = p_cs;
		
		c_engine = new ScantegrityEngine(c_rand, c_directory, c_cs); 
	}
	
	//Does all the pre-election generation and returns a grid of confirmation codes
	public String[][] preElection() throws Exception
	{
		String[][] l_codes = new String[c_ballots][c_candidates];
		
		for( int x = 0; x < c_ballots; x++ )
		{
			for( int y = 0; y < c_candidates; y++ )
			{
				l_codes[x][y] = generateCode();
			}
		}
		
		c_engine.preElection(l_codes);
		return l_codes;
	}
	
	private String generateCode()
	{
		String l_ret = "";
		for( int x = 0; x < CODE_LENGTH; x++ )
		{
			l_ret += (char)(c_rand.nextInt(26) + 65);
		}
		return l_ret;
	}
	
}
