/**
 * This class gives permutations based on a Fisher-Yates Shuffle. 
 */

package punchscan;

import java.util.Random;

public class SimplePermutation implements Permutation {
	Random rand; 
	int initial[];
	int numObjs; 
	int seed = 14; 
	
	/**
	 * The constructor. Initializes the PRNG 
	 * @param numObjs The number of objects in the permutation
	 */
	public SimplePermutation(int numObjs) {
		//set up the PRNG
		//this should eventually grab the seed from the .properties file
		rand = new Random(seed);
		initial = new int[numObjs];
		
		for(int i = 0; i < numObjs; i++)
		{
			initial[i] = i;
		}
		
		this.numObjs = numObjs; 
	}

	public int[] getPerm() {
		//create a new perm of the initial 
		int perm[] = new int[numObjs];	
		for(int i = 0; i < initial.length; i++)
		{
			perm[i] = initial[i]; 
		}
		
		//shuffle
		shuffle(perm);
		
		//return the new perm
		return perm;
	}

	public int[] getPerm(int location) {
		Random r = rand; //save the current random 
		rand = new Random(seed); //create a new random 
		
		//set up the random number generator at the correct place
		for(int i = 0; i < location; i++)
		{
			for(int j = 0; j < numObjs; j++)
			{
				rand.nextInt();
			}
		}
		
		//now continue with getting the next perm 
		int perm[] = getPerm(); 

		rand = r; //reset the prng back to where it was
		
		return perm;
	}
	
	/**
	 * from Wikipedia
	 * This is the Durstenfeld's FY shuffle algorithm
	 * @param array
	 */
	public void shuffle(int[] array) {
	    // i is the number of items remaining to be shuffled.
	    for (int i = array.length; i > 1; i--) {
	        // Pick a random element to swap with the i-th element.
	        int j = rand.nextInt(i);  // 0 <= j <= i-1 (0-based array)
	        // Swap array elements.
	        int tmp = array[j];
	        array[j] = array[i-1];
	        array[i-1] = tmp;
	    }
	}
}
