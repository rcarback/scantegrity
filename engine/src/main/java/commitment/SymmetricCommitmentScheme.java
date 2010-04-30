package commitment;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class SymmetricCommitmentScheme implements CommitmentScheme {

	Cipher c_cipher;
	SecureRandom c_rand;
	
	public SymmetricCommitmentScheme()
	{
		c_rand = new SecureRandom();
		try {
			c_cipher = Cipher.getInstance("AES");
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void setup( String p_cryptAlg, String p_randAlg )
	{
		try {
			c_rand = SecureRandom.getInstance(p_randAlg);
			c_cipher = Cipher.getInstance(p_cryptAlg);
		} catch (NoSuchAlgorithmException e) {
			System.err.println("Could not load alg: " + p_cryptAlg + " or " + p_randAlg);
			System.exit(-1);
		} catch (NoSuchPaddingException e) {
			System.err.println("Could not load cipher padding: " + p_cryptAlg);
			System.exit(-1);
		}
	}
	
	@Override
	public Commitment commit(byte[] data) throws Exception{
		//Generate random key
		KeyGenerator l_kgen = KeyGenerator.getInstance(c_cipher.getAlgorithm());
	    l_kgen.init(128);
	    
	    SecretKey l_skey = l_kgen.generateKey();
		
		//Encrypt data
		c_cipher.init(Cipher.ENCRYPT_MODE, l_skey);
		byte[] l_commitData = c_cipher.doFinal(data);
		//Return commitment
		return new Commitment(l_commitData, l_skey.getEncoded());
	}

	@Override
	public boolean decommit(byte[] data, Commitment commit) {
		try {
			c_cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(commit.c_randSeed, c_cipher.getAlgorithm()));
			byte[] l_confirmation = c_cipher.doFinal(commit.c_commitment);
			return Arrays.equals(l_confirmation, data);
		} catch (Exception e)
		{
			return false;
		}
	}

}
