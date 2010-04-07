package commitment;

public interface CommitmentScheme {

	public Commitment commit(byte[] data) throws Exception;
	public boolean decommit(byte[] data, Commitment commit);
}
