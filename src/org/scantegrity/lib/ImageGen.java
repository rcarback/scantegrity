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

public class ImageGen {
	
	// Driver Method for testing
	public static void main(String[] args) throws FontNotFoundException {
		File out = new File("img.jpg");
		makeImage("AE", "Courier New", 32, Color.YELLOW, Color.MAGENTA, 50, 50, 100,	out);
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
										  int threshold
										) throws FontNotFoundException {
		return makeImage(text,
						 font,
						 fontsize,
						 textColor,
						 bgColor,
						 width,
						 height,
						 threshold,
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
			  int threshold,
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

		// Randomize the Brightness
		RandomizeBrightness(img, threshold);

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

	private static void RandomizeBrightness(BufferedImage img, int threshold) {
		SecureRandom srand = new SecureRandom();
		
		// For each pixel...
		for (int x = 0; x < img.getWidth(); x++) {
			for (int y = 0; y < img.getHeight(); y++) {
				int v = Math.abs(srand.nextInt() % threshold);

				Color c = new Color(img.getRGB(x, y));
				int r = c.getRed();
				int g = c.getGreen();
				int b = c.getBlue();

				// If the color is 0, don't mess with its brightness,
				// otherwise, subtract v from it (all start at 255)
				if (r != 0)
					r -= v;
				if (g != 0)
					g -= v;
				if (b != 0)
					b -= v;

				// Set the new color to the pixel
				Color newColor = new Color(r, g, b);
				img.setRGB(x, y, newColor.getRGB());
			}
		}
	}
}
