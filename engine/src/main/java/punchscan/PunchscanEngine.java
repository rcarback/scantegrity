/**
 * @author John L. Conway IV 
 */

package punchscan;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;

public class PunchscanEngine {
	static final String DEFAULT_NUM_BALLOTS = "100"; 
	static final String DEFAULT_MAX_CANDIDATES = "100";
	static final boolean DEBUG = true; 
	
	SimplePermutation c_sperm; 
	int c_numBallots; 
	int c_maxCandidates; 
	
	/**
	 * The constructor for the Punchscan Engine. This will look at the 
	 * .properties file and determine the number of candidates,
	 *  and handle all other setup information. 
	 */
	public PunchscanEngine() {
		//load the properties from the properties file
		FileInputStream l_propxml = null;
		try {
			l_propxml = new FileInputStream("Config.properties");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Properties l_prop = new Properties();
		
		try {
			l_prop.loadFromXML(l_propxml);
		} catch (InvalidPropertiesFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		c_numBallots = Integer.parseInt(l_prop.getProperty("NumBallots", DEFAULT_NUM_BALLOTS));
		c_maxCandidates = Integer.parseInt(l_prop.getProperty("MaxCandidates", DEFAULT_MAX_CANDIDATES)); 
		
		//set up the permutation engine
		c_sperm = new SimplePermutation(c_maxCandidates);
	}
	
	/**
	 * This method generates the entire backend table.
	 * 
	 * @param candidatePermutations[][][] - These are the initial permutations 
	 * 	from the frontend. The lowest level array is the array of indexes for the 
	 * 	actual permutation put into an array of contests for each ballot, put into the 
	 *  last array of ballots. 
	 * @return ArrayList<Rows> The representation of the row TODO
	 */
	public void generate(int p_candidatePermutations[][][])
	{
		//for each ballot, generate each row
		for(int i = 0; i < p_candidatePermutations.length; i++)
		{
			int l_inputPerm[][] = p_candidatePermutations[i];
			int l_permA[][] = new int[l_inputPerm.length][c_maxCandidates];
			
			//for each contest, grab a permutation
			for(int j = 0; j < l_inputPerm.length; j++)
			{
				l_permA[j] = c_sperm.getPerm();
			}
			
			//ok now create the inverse perm
			int l_permB[][] = new int[p_candidatePermutations[i].length][c_maxCandidates];
			
			//TODO: find the inverse
			//based on what I have seen, it appears the inverse is the same as the input.
			for(int j = 0; j < l_inputPerm.length; j++)
			{
				int I[] = l_inputPerm[j]; 
				
				for(int k = 0; k < I.length; k++)
				{
					l_permB[j][l_permA[j][I[k]]] = I[k]; ;
				}
			} 
		}
	}
	
	/**
	 * This method will generate one row of the table given 
	 * an initial Ballot ID and Vector of Candidate Orders
	 * 
	 * @param candidatePermutations[][]: This is an array of permutations per contest
	 * @return ArrayList<Rows> The representation of the row TODO
	 */
	public void generateRow(int p_ballotID, int p_candidatePermutations[][])
	{	
		for(int i = 0; i < p_candidatePermutations.length; i++)
		{
			int l_inputPerm[] = p_candidatePermutations[i];
			int l_permA[] = new int[c_maxCandidates];
			
			//for each contest, grab a permutation
			for(int j = 0; j < l_inputPerm.length; j++)
			{
				l_permA = c_sperm.getPerm(p_ballotID);
			}
			
			//ok now create the inverse perm
			int l_permB[] = new int[c_maxCandidates];
						
			for(int j = 0; j < l_inputPerm.length; j++)
			{
				l_permB[l_permA[l_inputPerm[j]]] = l_inputPerm[j];
			}
			
			if(DEBUG)
			{
				System.out.println("Printing Input, PermA and PermB");
				printArray(l_inputPerm);
				printArray(l_permA);
				printArray(l_permB);
			}
		}
	}
	
	public void preElectionAudit()
	{
		
	}
	
	public void postElectionAudit()
	{
		
	}
	
	public void results(int p_results[][])
	{
		
	}
	
	
	/* *******************************************************
	 * 
	 * All Methods Below For Testing only
	 * 
	 *********************************************************/
	public void Test()
	{	
		int l_numContests = 1;
		
		//create the initial candidate list
		int l_initial[][] = new int[l_numContests][c_maxCandidates];
		for(int i = 0; i < l_numContests; i++)
		{
			for(int j = 0; j < c_maxCandidates; j++)
			{
				l_initial[i][j] = j;
			}
		}
		
		//test just the first row
		for(int i = 0; i < c_numBallots; i++)
		{
			generateRow(i, l_initial);
		}
		
		generateRow(4,l_initial);
		generateRow(2,l_initial);
		generateRow(6,l_initial); 
	}
	
	/*
	 * Test until I figure out how to do the junit tests or whatever
	 */
	public static void main(String args[])
	{	
		//create the engine
		PunchscanEngine l_engine = new PunchscanEngine();
		l_engine.Test(); 
	}
	
	private void printArray(int p_array[])
	{
		System.out.println("Dumping array:");
		for(int i = 0; i < p_array.length; i++)
		{
			System.out.print(p_array[i]); 
		}
		System.out.println(); 
	}
	
	@SuppressWarnings("unused")
	private void printArray(int p_array[][])
	{
		System.out.println("Dumping array:");
		for(int i = 0; i < p_array.length; i++)
		{
			for(int j = 0; j < p_array[i].length; j++)
			{
				System.out.print(p_array[i][j]);	
			} 
			System.out.println(); 
		}
		System.out.println(); 
	}
}
