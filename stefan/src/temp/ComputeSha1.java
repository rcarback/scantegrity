package temp;

import java.security.MessageDigest;
import java.security.Security;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Base64;

public class ComputeSha1 {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		Security.addProvider(new BouncyCastleProvider());
		byte[] m="xu7s8h4@10fhs5*^aj1355amsdkkksai".getBytes();
		MessageDigest sha = MessageDigest.getInstance("SHA256","BC");
		sha.update(m,0,m.length);
		byte[] h1 = sha.digest();
		System.out.println(new String(Base64.encode(h1)));
	}

}
