/*
 * Scantegrity - Successor to Punchscan, a High Integrity Voting System
 * Copyright (C) 2006  Richard Carback, David Chaum, Jeremy Clark, 
 * Aleks Essex, Stefan Popoveniuc, and Jeremy Robin
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package org.scantegrity.scanner;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.Vector;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.xml.parsers.ParserConfigurationException;

import org.scantegrity.common.Util;

/**
 * Given a folder with scanned ballots, it upload them to a webserver
 * @author stefan
 *
 */
public class UploadEncBallots {

	String host;
	Cipher cipher = null;

	SecretKeySpec key = null;
	
	/**
	 * 
	 * @param password - can be null or have a lenght of zero, in which case the ballots are considered unencrypted
	 * @param host - the host where the ballots should uploaded
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchPaddingException
	 * @throws InvalidKeyException
	 * @throws ParserConfigurationException
	 */
	public UploadEncBallots(String password,String host) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException{
		
		if (password==null || password.length()==0) {
			;
		} else {
			if (password.length() > 16)
				password = password.substring(0,16);
			while (password.length() !=16)
				password = password +" ";			
			key = new SecretKeySpec(password.getBytes(),"AES");
			cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE,key);
		}
		
		if (password.length() > 16)
			password = password.substring(0,16);
		while (password.length() !=16)
			password = password +" ";		
		this.host = host;
		
		TrustManager[] trustAllCerts = new TrustManager[]{
		        new X509TrustManager() {
		            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
		                return null;
		            }
		            public void checkClientTrusted(
		                java.security.cert.X509Certificate[] certs, String authType) {
		            }
		            public void checkServerTrusted(
		                java.security.cert.X509Certificate[] certs, String authType) {
		            }
		        }
		    };
		    
		    // Install the all-trusting trust manager
		    try {
		        SSLContext sc = SSLContext.getInstance("SSL");
		        sc.init(null, trustAllCerts, new java.security.SecureRandom());
		        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
		    } catch (Exception e) {
		    }
	}
		
	/**
	 * Uploads a file or all the files in a folder (recursevly).
	 * One connection is oppened for each ballot. The content of the ballot is
	 * appended to the web link (HTTP GET style)
	 * @param folder
	 * @throws Exception
	 */
	public void upload(String folder) throws Exception {
		
		File f = new File(folder);
		if (f.isFile()) {
			if (f.getName().endsWith(".enc")) {
				InputStream fis = new BufferedInputStream(new FileInputStream(f));
				byte[] encFile = new byte[fis.available()];
				fis.read(encFile);
				
				byte[] decFile = encFile;
				
				//TODO check the decreyptin result against an XML schema
				if (key!=null) {
					try {
						decFile = cipher.doFinal(encFile);
						ByteArrayInputStream bit=new ByteArrayInputStream(decFile);
						Util.GetDocumentBuilder().parse(bit);
					} catch (Exception e) {
						System.out.println("Incorrect password for "+f.getAbsolutePath());
						return;
					}
				}
				
				String xmlBallot = new String(decFile);
				System.out.println(xmlBallot);
				 // Create a URLConnection object for a URL
		        URL url = new URL(host+prepareXMLforUpload(xmlBallot));
		        System.out.println(url.toString());
		        HttpURLConnection  conn = (HttpURLConnection)url.openConnection();
		        conn.setRequestMethod("GET");
		        conn.connect();
		        int responseCode = conn.getResponseCode();
		        if (responseCode < 200 || responseCode > 299)
		        	throw new Exception("Ballot upload failed. Server response "+conn.getResponseCode()+" "+conn.getResponseMessage());
		        System.out.println("Succesfully uploaded "+f.getAbsolutePath());
			}
		} else {
			File[] files = f.listFiles();

			Vector<File> v = new Vector<File>();
			for (int i=0;i<files.length;i++) {
				v.add(files[i]);
			}
			
			Collections.shuffle(v);
			
			for (File ff:v) {
				upload(ff.getAbsolutePath());
			}
		}
	}
	
	/**
	 * Replaces the characters from an xml that are invalid to appear
	 * in an web address with escape characters.
	 * 
	 * @param xml
	 * @return A prepresentattion of the given parameter string, safe to use in a web link
	 */
	public static String prepareXMLforUpload(String xml) {
		String ret = xml;
		ret = ret.replace(new StringBuffer("+"),new StringBuffer("%2B"));
		ret = ret.replaceAll("<","%3C");
		ret = ret.replace(new StringBuffer("?"),new StringBuffer("%3F"));
		ret = ret.replaceAll(" ","+");
		ret = ret.replaceAll("=","%3D");
		ret = ret.replaceAll("\"","%22");
		ret = ret.replaceAll(">","%3E");
		ret=ret.replaceAll("/", "%2F");
		ret = ret.replace(new StringBuffer("\t"),new StringBuffer("%09"));
		ret = ret.replaceAll("\r","%0D");
		ret = ret.replaceAll("\n","%0A");
		ret=ret.replaceAll("#", "%23");
		ret=ret.replaceAll(":", "%3A");
		return ret;
	}	
	
	/**
	 * debug method
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		if (args.length<3) {
			System.out.println("Error. Run it like this ");
			System.out.println("   UploadEncBallots password sourceFolder hostURL");
			return;
		}
		UploadEncBallots u = new UploadEncBallots(args[0],args[2]);
		u.upload(args[1]);
	}

}
