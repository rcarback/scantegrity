package commitment;

public class Commitment {

	public byte[] c_commitment;
	public byte[] c_randSeed;
	
	public Commitment(byte[] p_commitment, byte[] p_randSeed)
	{
		c_commitment = p_commitment;
		c_randSeed = p_randSeed;
	}
	
}
