package org.scantegrity.lib;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.ArrayList;
import javax.imageio.ImageIO;

import org.scantegrity.lib.exceptions.FontNotFoundException;
import org.scantegrity.lib.InvisibleInkFactory;

public class ImageGen {
	
	static float[] yellow  = {0, 0, 1, 0};
	static float[] magenta = {0, 1, 0, 0};
	static float[] cyan    = {1, 0, 0, 0};
	
	// Driver Method for testing
	public static void main(String[] args) throws FontNotFoundException {
		File out = new File("img.png");
		InvisibleInkFactory x = new InvisibleInkFactory();
		try {
			ImageIO.write(x.getBufferedImage("CP"), "png", out);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	/**
	 * Same as other makeImage(...) except that this 
	 * function will not produce a file as output.
	 * 
	 * @param text
	 * @param font
	 * @param fontsize
	 * @param textColor
	 * @param bgColor
	 * @param width
	 * @param height
	 * @param threshold
	 * @return
	 * @throws FontNotFoundException
	 */
	public static BufferedImage makeImage(String text, 
										  String font,
										  int fontsize, 
										  Color textColor, 
										  Color bgColor, 
										  int width,
										  int height, 
										  int cyanThreshold,
										  float brightThreshold
										) throws FontNotFoundException {
		return makeImage(text,
						 font,
						 fontsize,
						 textColor,
						 bgColor,
						 width,
						 height,
						 cyanThreshold,
						 brightThreshold,
						 null);
	}

	/**
	 * This function generates an  image with the given 
	 * text and attributes, then randomized the brightness 
	 * of each pixel. The result will be returned as a 
	 * BufferedImage and will also be stored in <outfile>.
	 * 
	 * @param text - text to write into the image
	 * @param font
	 * @param fontsize
	 * @param textColor
	 * @param bgColor
	 * @param width - width of resultant image (px)
	 * @param height - height of resultat image (px)
	 * @param threshold - variance in brightness (0 - 255)
	 * @param outfile - file to store the resultant image in
	 * @return
	 * @throws FontNotFoundException
	 */
	public static BufferedImage makeImage(String text, 
			  String font,
			  int fontsize, 
			  Color textColor, 
			  Color bgColor, 
			  int width,
			  int height, 
			  int cyanThreshold,
			  float brightnessThreshold,
			  File outfile
			) throws FontNotFoundException {
		
		// Check to see if the font exists on the system
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		String[] fontFamilies = ge.getAvailableFontFamilyNames();
		ArrayList<String> fonts = new ArrayList<String>();
		for (String s : fontFamilies) {
			fonts.add(s);
		}
		if (fonts.lastIndexOf(font) == -1)
			throw new FontNotFoundException();

		// Create the image
		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		// Get the image's graphics pallette
		Graphics2D g2d = img.createGraphics();

		// Set background color & fill image
		g2d.setColor(bgColor);
		g2d.fillRect(0, 0, width, height);

		// Create font object & set it as the active font
		Font fontObj = new Font(font, Font.BOLD, fontsize);
		g2d.setFont(fontObj);

		// Get the width of the string & determine offset to center (horiz.)
		FontRenderContext frc = g2d.getFontRenderContext();
		double xOffset = (img.getWidth() - fontObj.getStringBounds(text, frc)
				.getWidth()) / 2f;

		// Get the height of the string & determine offset to center (vert.)
		LineMetrics lm = fontObj.getLineMetrics(text, frc);
		double yOffset = (img.getHeight() / 2f) + lm.getDescent();

		// Set the font color
		g2d.setColor(textColor);

		// Draw the string into the image
		g2d.drawString(text, (int) xOffset, (int) yOffset);

		// Add Random Cyan and Randomize the Brightness
		RandomizeBrightness(img, brightnessThreshold);
		AddRandomCyan(img, cyanThreshold);
		

		// Output the image
		if (outfile != null) {
			try {
				ImageIO.write(img, "jpg", outfile);
			} catch (IOException e) {
				// HANDLE THIS
			}
		}

		return img;
	}

	private static void AddRandomCyan(BufferedImage img, int threshold) {
		// Handle the trivial case.
		if(threshold == 0) return;
		System.out.println("RC");
		SecureRandom srand = new SecureRandom();
		
		// For each pixel...
		for (int x = 0; x < img.getWidth(); x++) {
			for (int y = 0; y < img.getHeight(); y++) {
				int v = 500; // Obviously way above threshold 

				Color c = new Color(img.getRGB(x, y));
				int r = c.getRed();
				int g = c.getGreen();
				int b = c.getBlue();

				// Cyan translates to Green + Blue in RGB, so
				// by reducing the remaining color (red), we
				// cause a certain amount of CYAN to be
				// introduced (since CMYK is subtractive, 
				// taking away some red actually adds more of
				// the other colors).
				while(v > r) v = Math.abs(srand.nextInt() & threshold);
				r -= v;

				// Set the new color to the pixel
				Color newColor = new Color(r, g, b);
				img.setRGB(x, y, newColor.getRGB());
			}
		}
	}
	
	private static void RandomizeBrightness(BufferedImage img, float threshold){
		// Handle the trivial case.
		if(threshold == 0) return;
		System.out.println("RB");
		SecureRandom srand = new SecureRandom();
		
		// For each pixel...
		for (int x = 0; x < img.getWidth(); x++){
			for (int y = 0; y < img.getHeight(); y++){
				float v = 10; // Obviously greater than the threshold
				while(v > threshold) v = Math.abs(v - srand.nextFloat());
				
				// Invert v to _take away_ up to "threshold" amount of
				// color, not leave only that much.
				v = 1 - v;
				
				Color c = new Color(img.getRGB(x, y));
				int r = c.getRed();
				int g = c.getGreen();
				int b = c.getBlue();

				// Muliplying each color by the same value < 1 will reduce
				// the brightness of the image.
				r = (int) Math.floor(r*v);
				g = (int) Math.floor(g*v);
				b = (int) Math.floor(b*v);

				
				// Set the new color to the pixel
				Color newColor = new Color(r, g, b);
				img.setRGB(x, y, newColor.getRGB());
			}
		}
	}
}
