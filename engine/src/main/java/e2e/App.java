package e2e;
import java.io.FileInputStream;
import java.security.Provider;
import java.security.SecureRandom;
import java.security.SecureRandomSpi;
import java.security.Security;
import java.util.Properties;
import java.util.Set;

import commitment.Commitment;
import commitment.HashCommitmentScheme;
import commitment.PRNGCommitmentScheme;
import commitment.SymmetricCommitmentScheme;

/**
 * Hello world!
 *
 */
public class App 
{
	static final String DEFAULT_RAND_PROVIDER = "SUN";
	static final String DEFAULT_RAND_ALG = "SHA1PRNG";
	
    public static void main( String[] args )
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
		
		SymmetricCommitmentScheme l_commitScheme = new SymmetricCommitmentScheme();
		l_commitScheme.setup("AES","SHA1PRNG");
		//HashCommitmentScheme l_commitScheme = new HashCommitmentScheme();
		//l_commitScheme.setup("SHA-512", 256, "SHA1PRNG");
		//PRNGCommitmentScheme l_commitScheme = new PRNGCommitmentScheme();
		System.out.println("Generating test data...");
		
		byte[][] l_test = new byte[2][4096];
		for( int x = 0; x < l_test.length; x++ )
		{
			l_rand.nextBytes(l_test[x]);
		}
		
		long l_start = System.currentTimeMillis();
		System.out.print("Testing commit sanity...");
		for( int x = 0; x < l_test.length; x++ )
		{
			try {
				Commitment l_testCommit = l_commitScheme.commit(l_test[x]);
				if( !l_commitScheme.decommit(l_test[x], l_testCommit) )
					System.out.println("FAILED");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println("DONE");
		
		System.out.println(System.currentTimeMillis() - l_start);
    }
}
