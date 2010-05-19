package scantegrity;

import java.io.File;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Random;

import commitment.CommitmentScheme;

import table.FlatFileTable;


public class ScantegrityEngine {	
	int c_ballots; 
	int c_columns;
	int c_maxCandidates; 
	Random c_rand;
	RTable c_tableR;
	String[][] c_tableQ;	
	short[][] c_tableQPerms;
	boolean[][] c_tableS;
	File c_directory;
	CommitmentScheme c_cs;
	
	
	public ScantegrityEngine(Random p_rand, File p_directory, CommitmentScheme p_cs)
	{
		c_rand = p_rand;
		c_directory = p_directory;
		c_cs = p_cs;
	}
	
	public void preElection(String[][] p_confCodes) throws Exception
	{
		generate(p_confCodes);
		commit(c_directory, c_cs);
	}
	
	public void postElection(boolean[][] p_votes)
	{
		generateS(p_votes);
		printS(c_directory);
	}
	
	public void generate(String[][] p_confCodes)
	{
		generateQ(p_confCodes);
	}
	
	public void generateQ(String[][] p_confCodes)
	{
		int l_ballots = p_confCodes.length;
		int l_columns = p_confCodes[0].length;
		c_ballots = l_ballots;
		c_columns = l_columns;
		
		c_tableR = new RTable(l_ballots, l_columns, c_rand);
		c_tableQ = new String[l_ballots][l_columns];
		c_tableQPerms = new short[l_ballots][l_columns];
		
		for( int x = 0; x < p_confCodes.length; x++ )
		{
			//Initialize by copying codes
			for( int y = 0;  y < p_confCodes[x].length; y++ )
			{
				c_tableQ[x][y] = p_confCodes[x][y];
				//Keep track of the permutations so votes can be recorded correctly later
				c_tableQPerms[x][y] = (short)y;
			}

			for( int y = c_tableQ[x].length - 1; y > 1; y-- )
			{
				int index = c_rand.nextInt(y+1);
				
				String temp = c_tableQ[x][index];
				c_tableQ[x][index] = c_tableQ[x][y];
				c_tableQ[x][y] = temp;
				
				/*short pos1 = c_tableQPerms[x][index];
				short pos2 = c_tableQPerms[x][y];
				c_tableQPerms[x][pos1] = (short)y;
				c_tableQPerms[x][pos2] = (short)index;*/
				short tempi = c_tableQPerms[x][index];
				c_tableQPerms[x][index] = c_tableQPerms[x][y];
				c_tableQPerms[x][y] = tempi;
				
				c_tableR.switchQ(y * l_columns + x, index * l_columns + x);
			}
		}
		
		c_tableR.shuffle();
		//c_tableR.test(c_tableQ, c_tableQPerms);
	}
	
	public boolean commit(File p_directory, CommitmentScheme p_cs) throws Exception
	{
		if( p_directory.isDirectory() && p_directory.canWrite() )
		{
			commitQ(p_directory, p_cs);
			commitR(p_directory, p_cs);
			return true;
		}
		return false;
	}
	
	private void commitQ(File p_directory, CommitmentScheme p_cs) throws Exception
	{
		FlatFileTable l_table = new FlatFileTable();
		for( int x = 0; x < c_tableQ.length; x++ )
		{
			ArrayList<Object> l_row = new ArrayList<Object>();
			for( int y = 0; y < c_tableQ[x].length; y++ )
				l_row.add(p_cs.commit(c_tableQ[x][y].getBytes()).c_commitment);
			l_table.insertRow(l_row);
		}
		l_table.saveXmlFile(p_directory, "TableQ");
	}
	
	private void commitR(File p_directory, CommitmentScheme p_cs) throws Exception
	{
		c_tableR.commit(p_directory, p_cs);
	}
	
	private void generateS(boolean[][] p_votes)
	{
		c_tableS = c_tableR.tally(p_votes, c_tableQPerms);
	}
	
	public void printS(File p_directory)
	{
		FlatFileTable l_table = new FlatFileTable();
		
		for( int x = 0; x < c_tableS.length; x++ )
		{
			ArrayList<Object> l_row = new ArrayList<Object>();
			for( int y = 0; y < c_tableS[x].length; y++ )
			{
				l_row.add(c_tableS[x][y]);
			}
			l_table.insertRow(l_row);
		}
		l_table.saveXmlFile(p_directory, "TableS");
	}
	
	public void fullAudit( int[] p_ballotNums, String p_name )
	{
		c_tableR.fullAudit( p_ballotNums, c_directory, p_name );
	}
	
	public void randomAudit( String p_name )
	{
		c_tableR.randomAudit( c_rand, c_directory, p_name );
	}
	
	public void randomAudit( String p_name, Random p_rand )
	{
		c_tableR.randomAudit( p_rand, c_directory, p_name );
	}
	
}
