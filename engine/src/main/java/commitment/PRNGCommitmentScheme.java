package commitment;

import java.security.SecureRandom;


public class PRNGCommitmentScheme implements CommitmentScheme {

	byte[] c_challenge;
	SecureRandom c_rand;
	
	public PRNGCommitmentScheme(byte[] p_challenge)
	{
		c_challenge = p_challenge.clone();
		c_rand = new SecureRandom();
	}
	
	
	@Override
	public Commitment commit(byte[] data) throws Exception {
		if( data.length > c_challenge.length )
			throw new Exception("Message is longer than challenge input");
		
		//Generate the random seed for the PRNG and store it for use with decommit
		byte[] l_randSeed = new byte[c_challenge.length];
		c_rand.nextBytes(l_randSeed);
		
		//Generate the random bits to XOR with the data
		SecureRandom l_rand = new SecureRandom(l_randSeed);
		byte[] l_randBytes = new byte[c_challenge.length * 4];
		
		//Multiply the data by the challenge
		return new Commitment(l_randSeed, data);
		
	}

	@Override
	public boolean decommit(byte[] data, Commitment comm) {
		// TODO Auto-generated method stub
		return false;
	}


}
