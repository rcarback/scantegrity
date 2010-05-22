/**
 * This class gives permutations based on a Fisher-Yates Shuffle. 
 */

package punchscan;

import java.io.FileInputStream;
import java.security.SecureRandom;
import java.util.Properties;

public class SimplePermutation implements Permutation { 
	private int c_initial[];
	private int  c_numObjs; 
	private SecureRandom c_rand;
	
	static final String DEFAULT_RAND_PROVIDER = "SUN";
	static final String DEFAULT_RAND_ALG = "SHA1PRNG";
	
	/**
	 * The constructor. Initializes the PRNG 
	 * @param numObjs The number of objects in the permutation
	 */
	public SimplePermutation(int p_numObjs) {
		//set up the PRNG
    	c_rand = getNewSecureRandom(); 
		
		
		c_initial = new int[p_numObjs];
		
		for(int i = 0; i < p_numObjs; i++)
		{
			c_initial[i] = i;
		}
		
		this.c_numObjs = p_numObjs; 
	}

	public int[] getPerm() {
		//create a new perm of the initial 
		int l_perm[] = new int[c_numObjs];	
		for(int i = 0; i < c_initial.length; i++)
		{
			l_perm[i] = c_initial[i]; 
		}
		
		//shuffle
		shuffle(l_perm);
		
		//return the new perm
		return l_perm;
	}

	public int[] getPerm(int p_location) {
		SecureRandom l_rand = c_rand; //save the current random 
		c_rand = getNewSecureRandom(); //create a new random 
		
		//set up the random number generator at the correct place
		for(int i = 0; i < p_location; i++)
		{
			for(int j = 0; j < c_numObjs; j++)
			{
				c_rand.nextInt();
			}
		}
		
		//now continue with getting the next perm 
		int l_perm[] = getPerm(); 

		c_rand = l_rand; //reset the prng back to where it was
		
		return l_perm;
	}
	
	/**
	 * from Wikipedia
	 * This is the Durstenfeld's FY shuffle algorithm
	 * @param array
	 */
	private void shuffle(int[] p_array) {
	    // i is the number of items remaining to be shuffled.
	    for (int i = p_array.length; i > 1; i--) {
	        // Pick a random element to swap with the i-th element.
	        int j = c_rand.nextInt(i);  // 0 <= j <= i-1 (0-based array)
	        // Swap array elements.
	        int tmp = p_array[j];
	        p_array[j] = p_array[i-1];
	        p_array[i-1] = tmp;
	    }
	}
	
	private SecureRandom getNewSecureRandom()
	{
		SecureRandom l_rand; 
		try {
			FileInputStream l_propxml = new FileInputStream("Config.properties");
			Properties l_prop = new Properties();
			l_prop.loadFromXML(l_propxml);
			
			String l_randAlgName = l_prop.getProperty("SecureRandomAlgorithm", DEFAULT_RAND_ALG);
			String l_randProviderName = l_prop.getProperty("SecureRandomProvider", DEFAULT_RAND_PROVIDER);
			l_rand = SecureRandom.getInstance(l_randAlgName, l_randProviderName);
			
		} catch (Exception e) {
			//If there is some exception loading the properties file or initializing the primitives, revert to system defaults
			l_rand = new SecureRandom();
		}
		
		return l_rand;
	}
}