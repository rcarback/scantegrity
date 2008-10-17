package org.scantegrity.lib;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;


import javax.imageio.ImageIO;

import org.scantegrity.lib.exceptions.FontNotFoundException;
import org.scantegrity.lib.InvisibleInkFactory;

public class ImageGen {
	
	static float[] yellow  = {0, 0, 1, 0};
	static float[] magenta = {0, 1, 0, 0};
	static float[] cyan    = {1, 0, 0, 0};
	
	// Driver Method for testing
	public static void main(String[] args) throws FontNotFoundException {
		long start = System.currentTimeMillis();
		File out = new File("img.png");
		BufferedImage img;
		try {
			InvisibleInkFactory x = new InvisibleInkFactory();
			SecureRandom csprng = SecureRandom.getInstance("SHA1PRNG");
			csprng.setSeed(System.currentTimeMillis());
			x.setCSPRNG(csprng);
			img = x.getBufferedImage("AC");
			System.out.println(System.currentTimeMillis()-start);
			ImageIO.write(img, "png", out);			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
