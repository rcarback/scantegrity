/**
 * @author John L. Conway IV 
 */

package punchscan;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;
import java.util.Random;

public class PunchscanEngine {
	static final String DEFAULT_NUM_BALLOTS = "100"; 
	static final String DEFAULT_MAX_CANDIDATES = "100"; 
	static final String DEFAULT_SEED = "13"; 
	
	SimplePermutation sperm; 
	int numBallots; 
	int maxCandidates; 
	Random rand; 
	
	/**
	 * The constructor for the Punchscan Engine. This will look at the 
	 * .properties file and determine the number of candidates, initialize 
	 * the random number generator, and handle all other setup information. 
	 */
	public PunchscanEngine(long seed) {
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
		
		numBallots = Integer.parseInt(l_prop.getProperty("NumBallots", DEFAULT_NUM_BALLOTS));
		maxCandidates = Integer.parseInt(l_prop.getProperty("MaxCandidates", DEFAULT_MAX_CANDIDATES)); 
		seed = Integer.parseInt(l_prop.getProperty("Seed", DEFAULT_SEED)); 
		
		//set up the permutation engine
		sperm = new SimplePermutation(maxCandidates);
		
		//create the random num generator 
		rand = new Random(seed);
	}
	
	/**
	 * This method generates the entire backend table.
	 */
	public void generate(int candidatePermutations[][][])
	{
		//for each ballot, generate each row
		for(int i = 0; i < candidatePermutations.length; i++)
		{
			int permA[][] = new int[candidatePermutations[i].length][maxCandidates];
			
			//for each contest, grab a permutation
			for(int j = 0; j < candidatePermutations[i].length; j++)
			{
				permA[j] = sperm.getPerm();
			}
			
			//ok now create the inverse perm
			int permB[][] = new int[candidatePermutations[i].length][maxCandidates];
			
			//TODO: find the inverse
			
		}
	}
	
	/**
	 * This method will generate one row of the table given 
	 * an initial Ballot ID and Vector of Candidate Orders
	 */
	public void generateRow(int ballotID, int candidatePermutations[][])
	{
		int perm[] = sperm.getPerm(ballotID);
		
		printArray(perm);
	}
	
	public void Test()
	{
		int numContests = 1;
		
		//create the initial candidate list
		int initial[][] = new int[numContests][maxCandidates];
		for(int i = 0; i < numContests; i++)
		{
			for(int j = 0; j < maxCandidates; j++)
			{
				initial[i][j] = j;
			}
		}
		
		//test just the first row
		for(int i = 0; i < numBallots; i++)
		{
			generateRow(i, initial);
		}
		
		generateRow(4,initial);
		generateRow(2,initial);
		generateRow(6,initial); 
	}
	
	/*
	 * Test until I figure out how to do the junit tests or whatever
	 */
	public static void main(String args[])
	{	
		//create the engine
		PunchscanEngine engine = new PunchscanEngine(0);
		engine.Test(); 
	}
	
	private void printArray(int array[])
	{
		System.out.println("Dumping array:");
		for(int i = 0; i < array.length; i++)
		{
			System.out.print(array[i]); 
		}
		System.out.println(); 
	}
	
	private void printArray(int array[][])
	{
		System.out.println("Dumping array:");
		for(int i = 0; i < array.length; i++)
		{
			for(int j = 0; j < array[i].length; j++)
			{
				System.out.print(array[i][j]);	
			} 
			System.out.println(); 
		}
		System.out.println(); 
	}
}
