package e2e;
import java.io.FileInputStream;
import java.security.Provider;
import java.security.SecureRandom;
import java.security.Security;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

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
    }
}
