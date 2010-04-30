package commitment;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;

public class HashCommitmentScheme implements CommitmentScheme {

	MessageDigest c_digest;
	int c_saltLength;
	SecureRandom c_rand;
	
	public HashCommitmentScheme()
	{
		try {
			c_digest = MessageDigest.getInstance("SHA-512");
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		c_saltLength = 256;
		c_rand = new SecureRandom();
	}
	
	public void setup(String p_hashAlg, int p_saltLength, String p_randAlg)
	{
		try {
			c_digest = MessageDigest.getInstance(p_hashAlg);
			c_rand = SecureRandom.getInstance(p_randAlg);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		c_saltLength = p_saltLength;
	}
	
	@Override
	public Commitment commit(byte[] data) throws Exception {
		c_digest.reset();
		c_digest.update(data);
		byte[] l_salt = new byte[c_saltLength];
		c_rand.nextBytes(l_salt);
		c_digest.update(l_salt);
		byte[] l_hash = c_digest.digest();
		
		Commitment l_commit = new Commitment(l_hash, l_salt);
		return l_commit;
	}

	@Override
	public boolean decommit(byte[] data, Commitment commit) {
		c_digest.reset();
		c_digest.update(data);
		c_digest.update(commit.c_randSeed);
		byte[] l_confirmation = c_digest.digest();
		
		return Arrays.equals(l_confirmation, commit.c_commitment);
	}

}
