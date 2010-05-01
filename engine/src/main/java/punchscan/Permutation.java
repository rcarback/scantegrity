/**
 * 
 */
package punchscan;

/**
 * @author jay12701
 *
 */
public interface Permutation {
	
	/** 
	 * Returns the next permutation in the sequence. 
	 * @return The next permutation
	 */
	int [] getPerm();
	
	/** 
	 * Returns the permutation at the given location. 
	 * @param location 
	 * @return The given permutation 
	 */
	int [] getPerm(int location);
}
