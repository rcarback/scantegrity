package commitment;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class SymmetricCommitmentScheme implements CommitmentScheme {

	Cipher c_cipher;
	SecureRandom c_rand;
	
	public SymmetricCommitmentScheme(String p_cryptAlg, String p_randAlg, String p_prov)
	{
		try {
			c_rand = SecureRandom.getInstance(p_randAlg, p_prov);
			c_cipher = Cipher.getInstance(p_cryptAlg, p_prov);
		} catch (NoSuchAlgorithmException e) {
			System.err.println("Could not load alg: " + p_cryptAlg + " or " + p_randAlg);
			System.exit(-1);
		} catch (NoSuchPaddingException e) {
			System.err.println("Could not load cipher padding: " + p_cryptAlg);
			System.exit(-1);
		} catch (NoSuchProviderException e) {
			System.err.println("Could not load provider: " + p_prov);
			System.exit(-1);
		}
	}
	
	@Override
	public Commitment commit(byte[] data) throws Exception{
		//Generate random key
		byte[] l_rand = new byte[128];
		c_rand.nextBytes(l_rand);
		
		//Encrypt data
		c_cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(l_rand, "AES"));
		byte[] l_commitData = c_cipher.doFinal(data);
		//Return commitment
		return new Commitment(l_rand, l_commitData);
	}

	@Override
	public boolean decommit(byte[] data, Commitment commit) {
		try {
			c_cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(commit.c_randSeed, "AES"));
			byte[] l_confirmation = c_cipher.doFinal(commit.c_commitment);
			return Arrays.equals(l_confirmation, data);
		} catch (Exception e)
		{
			return false;
		}
	}

}
