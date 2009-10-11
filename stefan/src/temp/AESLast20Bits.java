package temp;

import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Security;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class AESLast20Bits {

	public static Cipher cipherPkcs5Padding = null;
	public static Cipher cipherNoPaddingNoKey = null;
	
	public static SecretKeySpec MK1=new SecretKeySpec("1234567890123456".getBytes(),"AES");
	public static SecretKeySpec MK2=new SecretKeySpec("ABCDEFGHIJKLMNOP".getBytes(),"AES");
	public static byte[] C = "A PublicConstant".getBytes();		
	
	static {
		try {
			Security.addProvider(new BouncyCastleProvider());			
			//cipherPkcs5Padding = Cipher.getInstance("AES/ECB/PKCS5Padding");
			cipherPkcs5Padding = Cipher.getInstance("AES/ECB/NoPadding");
			cipherNoPaddingNoKey = Cipher.getInstance("AES/ECB/NoPadding");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		}
	}

	public static void getCode(int printedSerial, int qno,int rank, int ano,SecretKeySpec key,int noBits) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		String m=printedSerial+" "+qno+" "+rank+" "+ano;
		cipherPkcs5Padding.init(Cipher.ENCRYPT_MODE, key);
		byte[] enc=new byte[16];
		System.arraycopy(m.getBytes(), 0, enc, 0, Math.min(16,m.getBytes().length));
			//m.getBytes();		
		int ret=0;
		enc=cipherPkcs5Padding.doFinal(enc);
//Util.print(enc);			
		//from the last four bits, make a positive integer
		ret=enc[15];
		ret |=enc[14]<<8;
		ret |=enc[13]<<16;
		ret |=enc[12]<<24;
		//now ret has the last 32 bits
//System.out.println();
//System.out.println(ret+" " +Integer.toBinaryString(ret));			
//System.out.println(Integer.toBinaryString((1<<noBits) -1));
		//take only the last noBits
		ret &= ((1<<noBits) -1);
System.out.println(ret+" "+Integer.toBinaryString(ret));			
	}
	
	public static SecretKeySpec makeKey(int printedSerial, int qno) throws Exception {
    	 return KeyForCodeGeneration(MK1, MK2, C, (printedSerial+"").getBytes(), (qno+"").getBytes(), "KEY".getBytes());		
	}
	
	public static SecretKeySpec KeyForCodeGeneration(SecretKeySpec mk1,SecretKeySpec mk2,byte[] c, byte[] serial,byte[] qno,byte[] constant) throws Exception {
		byte[] km=new byte[16];
		System.arraycopy(serial,0,km,0,serial.length);
		System.arraycopy(qno,0,km,serial.length+1,qno.length);
		System.arraycopy(constant,0,km,serial.length+1+qno.length+1,constant.length);
		return tripleAES(mk1,mk2,c,km);
	}
	
	public static SecretKeySpec tripleAES(SecretKeySpec mk1,SecretKeySpec mk2,byte[] c,byte[] message) throws Exception {
		cipherNoPaddingNoKey.init(Cipher.ENCRYPT_MODE,mk1);
		byte[] emk1 = cipherNoPaddingNoKey.doFinal(message);
		cipherNoPaddingNoKey.init(Cipher.ENCRYPT_MODE,mk2);
		byte[] emk2 = cipherNoPaddingNoKey.doFinal(xor(c,emk1));
		cipherNoPaddingNoKey.init(Cipher.DECRYPT_MODE,mk1);
		byte[] smkRaw = cipherNoPaddingNoKey.doFinal(xor(c,emk2));
		return new SecretKeySpec(smkRaw,"AES");
	}
	
	public static byte[] xor(byte[] a, byte[] b) throws Exception {
		byte[] retVal = null;
		if (a.length != b.length) {
			throw new Exception("byte arrays have to have the same size.a["+a.length+"]"+" b["+b.length+"]");
		}
		retVal = new byte[a.length];
		for (int i=0;i<a.length;i++) {
			retVal[i] = (byte)(a[i] ^ b[i]);
		}
		return retVal;	
	}

	
	public static void main(String[] args) throws Exception {
	int rank=0;
		for (int printedSerial=0;printedSerial<10;printedSerial++) {
			for (int qno=0;qno<10;qno++) {
				SecretKeySpec key=makeKey(printedSerial, qno);
				System.out.println("Key "+new BigInteger(key.getEncoded()).toString(16));									
				for (int ano=0;ano<10;ano++) {
					getCode(printedSerial, qno, rank, ano,key,20);
				}
			}
		}
	}

}
