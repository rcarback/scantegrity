/*
 * @(#)InvisibleInkFactory.java
 *  
 * Copyright (C) 2008 Scantegrity Project
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
 
package org.scantegrity.lib;
 
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.image.BufferedImage;
import java.security.SecureRandom;
import java.util.Vector;
 
/**
 * Generates images to print text in reactive (yellow) and dummy (magenta) ink.
 * The images produced by this module are embedded into ballot files that are 
 * printed on an invisible ink-capable printer. It is straightforward to 
 * generate the images, but the module has a number of extra functions to 
 * make the printed ink harder to read under various lights and types of 
 * paper. 
 * 
 * @author Richard Carback
 * @version 0.0.1 
 * @date 08/10/09
 */
public class InvisibleInkFactory {

	static final short BLOCK = 1; 
	static final short MASK = 2; 
	static final short MUNGE = 4; 
	static final String DEFAULT_SYMBOLS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";	
	
	private short c_flags; 
	private Font c_font;
	private int c_padding;
	private SecureRandom c_csprng;

	//The True ascent of the current font.
	private int c_txtAscent;
	
	/* Grid Options
	 * If these arrays are sized > 1, then it creates a pattern as it 
	 * pastes itself across the image.
	 */
	private Integer[] c_hGridSpace = { 1 };
	private Integer[] c_vGridSpace = { 1 };
	private Integer[] c_hGridSize = { 5 };
	private Integer[] c_vGridSize = { 5 };
	private Color c_gridColor = Color.WHITE;
	private Vector<Integer> c_xGridCoords = null;
	private Vector<Integer> c_yGridCoords = null;
	/**
	 * Default constructor for the class, which uses a 18pt Serif
	 * Font, 10 pixel padding, and block mode without any csprng stuff.   
	 */
	public InvisibleInkFactory () {
		this ("SanSerif", 96, 10, (short)7, null);
	}
	
	/**
	 * Overloaded constructor for the standard constructor. Defaults csprng to 
	 * null.
	 */	
	public InvisibleInkFactory (String p_fontName, int p_fontSize, int p_pad,
			short p_flags) {
		this (p_fontName, p_fontSize, p_pad, p_flags, null);
	}

	/**
	 * Standard constructor that allows user to set font, fontsize, padding,
	 * flags for the class, and optionally, a CSPRNG.
	 * 
	 * @param p_fontName - Name of the font, e.g. "Times New Roman".
	 * @param p_fontSize - Font Size
	 * @param p_pad - Padding around the font
	 * @param p_csprng - A cryptographically secure pseudo-random number 
	 * 					 generator
	 */
	public InvisibleInkFactory (String p_fontName, int p_fontSize, int p_pad,
			short p_flags, SecureRandom p_csprng) {

		c_flags = p_flags;
		c_font = new Font(p_fontName, Font.BOLD, p_fontSize);
		c_padding = p_pad;
		c_csprng = p_csprng;
		SetTrueAscent(DEFAULT_SYMBOLS);
	}
	
	
	/**
	 * Generate an image using the current settings and specified text.
	 * @param p_txt
	 * @return A BufferedImage object with the specified text and padding.
	 */
	public BufferedImage getBufferedImage(String p_txt) {
		float l_height, l_width;
		BufferedImage l_ret;
		Graphics2D l_g2d;
		
		//Get the size of the string
		FontRenderContext l_frc = new FontRenderContext(null, false, true);
		l_height = c_txtAscent;
		l_width = (int)c_font.getStringBounds(p_txt, l_frc).getWidth();
		
		//Add padding
		l_height += c_padding*2;
		l_width += c_padding*2;
		
		//Generate image and set colors
		l_ret = new BufferedImage((int)l_width, (int)l_height, 
									BufferedImage.TYPE_INT_RGB);
		l_g2d = l_ret.createGraphics();
		
		
		//Draw Background
		l_g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, 
								RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
		l_g2d.setColor(Color.YELLOW);
		l_g2d.fillRect(0, 0, (int)l_width, (int)l_height);
		
		//Draw Text
		l_g2d.setFont(c_font);
		l_g2d.setColor(Color.MAGENTA);				
		l_g2d.drawString(p_txt, c_padding, c_txtAscent+c_padding); 
		
		//Process Block flag, or set grid to pixel resolution.
		if ((c_flags & BLOCK) == BLOCK) l_ret = GenBlockGrid(l_ret);
		else GenGrid(l_ret);
		
		if ((c_flags & MASK) == MASK) l_ret = AddRandomCyan(l_ret);
		if ((c_flags & MUNGE) == MUNGE) l_ret = RandomizeBrightness(l_ret);
		
		return l_ret;
	}
	
	/**
	 * setCSPRNG - Sets the CSPRNG (if not set by Constructor).
	 * @param p_csprng - the CSPRNG to use.
	 */
	public void setCSPRNG(SecureRandom p_csprng) {
		this.c_csprng = p_csprng;
	}
	
	/**
	 * setFlags - sets the current flag set on the factory.
	 * @param p_flags - the flags.
	 */
	public void setFlags(short p_flags) {
		c_flags = p_flags;
	}
	
	/**
	 * Sets the current font.
	 * @param p_font - font to tuse.
	 */
	public void setFont(Font p_font) {
		c_font = p_font;
		SetTrueAscent(DEFAULT_SYMBOLS);
	}
	
	public void setGrid(Integer[] p_vGridSize, Integer[] p_vGridSpace, 
						Integer[] p_hGridSize, Integer[] p_hGridSpace)
	{
		//check for 0's
		for (int l_i = 0; l_i < p_vGridSize.length; l_i++) {
			if (p_vGridSize[l_i] <= 0) p_vGridSize[l_i] = 1;
		}
		for (int l_i = 0; l_i < p_hGridSize.length; l_i++) {
			if (p_hGridSize[l_i] <= 0) p_hGridSize[l_i] = 1;
		}

		c_vGridSize = p_vGridSize;
		c_hGridSize = p_hGridSize;
		c_vGridSpace = p_vGridSpace;
		c_hGridSpace = p_hGridSpace;		
	}
		
	/**
	 * This function exists because Java doesn't produce a true Ascent value
	 * for any given font. Thus, we do a pixel search to see what the topmost
	 * pixel of the image in the font really is, and subtract that from the
	 * reported ascent. This is pre-computed at instantiation using a given
	 * symbol set.
	 * @param p_txt - The symbol set to be used in the Invisible Ink images.
	 */
	private void SetTrueAscent(String p_txt) {
		float l_height, l_width;
		BufferedImage l_img;
		Graphics2D l_g2d;
		
		//Get the size of the string
		FontRenderContext l_frc = new FontRenderContext(null, false, true);
		l_height = (int)c_font.getLineMetrics(p_txt, l_frc).getAscent();
		l_width = (int)c_font.getStringBounds(p_txt, l_frc).getWidth();
	
		try {
			//Generate image and set colors
			l_img = new BufferedImage((int)l_width, (int)l_height, 
										BufferedImage.TYPE_INT_RGB);
			l_g2d = l_img.createGraphics();
					
			//Draw Text
			l_g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, 
									RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
			l_g2d.setFont(c_font);
			l_g2d.setColor(Color.CYAN);	
			l_g2d.drawString(p_txt, 0, l_height); 		
			
			int l_x = 0;
			int l_y = 0;
			int tmp = l_img.getRGB(l_x, l_y);
			//Remember that the image only has 2 colors: background and font
			while (tmp == l_img.getRGB(l_x, l_y)) {
				l_x++;
				if (l_x == l_width) {
					l_y++;
					l_x = 0;
					//If we exceed all bounds, die with default 0.
					if (l_y >= l_height) {
						l_y = 0;
						break;
					}
				}		
			}
			
			c_txtAscent = (int) l_height - l_y;
		} catch(Exception l_e) {
			//If any errors occur, set it to default of "height".
			c_txtAscent = (int) l_height;
		}
	}
	
	/**
	 * GenGrid - Generates a grid across a pre-generated image, and sets the 
	 * color inside each grid element to the middle-most pixel. 
	 * @param p_img
	 * @return
	 */
	private BufferedImage GenBlockGrid(BufferedImage p_img) {
		/* TODO: This function is probably too long */
		Graphics2D l_g2d = p_img.createGraphics();
		
		c_xGridCoords = new Vector<Integer>();
		c_yGridCoords = new Vector<Integer>();
		int l_index = 0;
		int l_width = p_img.getWidth();
		int l_height = p_img.getHeight();
		int l_h = c_hGridSize[0];
		int l_v = c_vGridSize[0];
		int l_hCurSpace = c_hGridSpace[0];
		int l_vCurSpace = c_vGridSpace[0];
		int l_hCurSize = l_h;
		int l_vCurSize = l_v;
		c_xGridCoords.add(0);
		c_yGridCoords.add(0);
		p_img = FillGridCell(p_img, 0, 0, l_h, l_v);
		while ((l_h + l_hCurSpace) < l_width || 
				  (l_v + l_vCurSpace) < l_height) {

			l_index++;
			l_g2d.setColor(c_gridColor);
			//Vertical Lines
			if (l_h + l_hCurSpace < l_width) {
				l_g2d.fillRect(l_h, 0, l_hCurSpace, l_height);
				l_hCurSize = c_hGridSize[l_index%c_hGridSize.length];
				l_h += l_hCurSpace + l_hCurSize;				
				l_hCurSpace = c_hGridSpace[l_index%c_hGridSpace.length];
				c_xGridCoords.add(Math.min(l_width-1, l_h - l_hCurSize));
			}

			//Horizontal Lines
			if (l_v + l_vCurSpace < l_height) {
				l_g2d.fillRect(0, l_v, l_width, l_vCurSpace);
				l_vCurSize = c_vGridSize[l_index%c_vGridSize.length];
				l_v += l_vCurSpace + l_vCurSize;				
				l_vCurSpace = c_vGridSpace[l_index%c_vGridSpace.length];
				c_yGridCoords.add(Math.min(l_height-1, l_v - l_vCurSize));
			}			
		
			if (c_xGridCoords.lastElement() + l_hCurSize > l_width) {
				l_hCurSize = l_width - c_xGridCoords.lastElement();
			}
			if (c_yGridCoords.lastElement() + l_vCurSize > l_height) {
				l_vCurSize = l_height - c_yGridCoords.lastElement();
			}
			
			
			//Color the Current (Next) Diagonal Block
			p_img = FillGridCell(p_img, c_xGridCoords.lastElement(), 
					 c_yGridCoords.lastElement(), 
					 l_hCurSize, l_vCurSize);
			//Blocks above or below current block.				
			for (int l_i = l_index-1; l_i >= 0; l_i--) {
				int l_tmpSize = 0;
				//Horizontal
				if (l_i < c_xGridCoords.size()) {
					l_tmpSize = Math.min(c_hGridSize[l_i%c_hGridSize.length],
										l_width-c_xGridCoords.elementAt(l_i));
					p_img = FillGridCell(p_img, c_xGridCoords.elementAt(l_i),
									 c_yGridCoords.lastElement(), 
									 l_tmpSize,
									 l_vCurSize);
				}
				//Vertical
				if (l_i < c_yGridCoords.size()) {
					l_tmpSize = Math.min(c_vGridSize[l_i%c_vGridSize.length],
							l_height-c_yGridCoords.elementAt(l_i));
					p_img = FillGridCell(p_img, c_xGridCoords.lastElement(),
						 c_yGridCoords.elementAt(l_i),
						 l_hCurSize,
						 l_tmpSize);
				}
			}
		}
		return p_img;
	}	
	
	/**
	 * GenGrid - Sets the class grid variabls to pixels for the given image.
	 * @param p_img - The image to be gridded.
	 */
	private void GenGrid(BufferedImage p_img) {
		Integer[] l_size = { 1 };
		Integer[] l_space = { 0 };
		c_xGridCoords = new Vector<Integer>();
		c_yGridCoords = new Vector<Integer>();
		for (int l_i = 0; l_i < p_img.getWidth(); l_i++) c_xGridCoords.add(l_i);
		for (int l_j = 0; l_j < p_img.getHeight(); l_j++) c_yGridCoords.add(l_j);
		c_vGridSize = l_size;
		c_hGridSize = l_size;
		c_vGridSpace = l_space;
		c_hGridSpace = l_space;
	}
	
	/**
	 * Samples and fills a cell with magenta or yellow depending on which has 
	 * the most instances of it.
	 * @param p_img - the image containing the cell
	 * @param p_x - the rightmost col of the cell
	 * @param p_y - the top row of the cell
	 * @param p_sizeX - width of cell
	 * @param p_sizeY - length of cell
	 * @return - a modified image
	 */
	private BufferedImage FillGridCell(BufferedImage p_img, int p_x, int p_y, 
									int p_sizeX, int p_sizeY) {
		int l_mCount, l_magenta;
		l_magenta = (Color.MAGENTA).getRGB();
		l_mCount = 0;	
		//For each pixel, count instance of magenta
		for (int l_i = 0; l_i < p_sizeX; l_i++) {
			for (int l_j = 0; l_j < p_sizeY; l_j++) {
				if (p_img.getRGB(p_x+l_i, p_y+l_j) == l_magenta) l_mCount++;
			}
		}
		//Draw the best.
		Graphics2D l_g2d = p_img.createGraphics();
		if (l_mCount < p_sizeX*p_sizeY/1.5) {
			l_g2d.setColor(Color.YELLOW);
		}
		else {
			l_g2d.setColor(Color.MAGENTA);
		}
		l_g2d.fillRect(p_x, p_y, p_sizeX, p_sizeY);		
		return p_img;
	}
	
	/**
	 * AddRandomCyan - Uses the preset CSPRNG to generate random additions of
	 * cyan to the image.
	 * @param p_img - image to modify.
	 * @return modified image.
	 */
	private BufferedImage AddRandomCyan(BufferedImage p_img) {
		/* It might be better to have one "addcolor" function, then in this
		 * just use them differently, instead of 2 that do specific things...
		 */
		if (c_csprng == null) {
			throw new NullPointerException("CSPRNG is not Set!");			
		}
		// For each pixel...
		for (int l_x = 0; l_x < c_xGridCoords.size(); l_x++) {
			for (int l_y = 0; l_y < c_yGridCoords.size(); l_y++) {
				Color l_c = new Color(p_img.getRGB(c_xGridCoords.elementAt(l_x),
												   c_yGridCoords.elementAt(l_y)));
				int l_r = l_c.getRed();
				int l_g = l_c.getGreen();
				int l_b = l_c.getBlue();

				// Cyan translates to Green + Blue in RGB, so
				// by reducing the remaining color (red), we
				// cause a certain amount of CYAN to be
				// introduced (since CMYK is subtractive, 
				// taking away some red actually adds more of
				// the other colors).
				l_r -= c_csprng.nextInt((int)((255 - (255-l_r))*.5));

				// Set the new color to the Grid Cell
				Graphics2D l_g2d = p_img.createGraphics();
				l_g2d.setColor(new Color(l_r, l_g, l_b));
				l_g2d.fillRect(c_xGridCoords.elementAt(l_x), 
							   c_yGridCoords.elementAt(l_y), 
							   c_hGridSize[l_x%c_hGridSize.length], 
							   c_vGridSize[l_y%c_vGridSize.length]);				
			}
		}
		return p_img;
	}
	
	private BufferedImage RandomizeBrightness(BufferedImage p_img) {
		if (c_csprng == null) {
			throw new NullPointerException("CSPRNG is not Set!");			
		}
		// For each pixel...
		for (int l_x = 0; l_x < c_xGridCoords.size(); l_x++) {
			for (int l_y = 0; l_y < c_yGridCoords.size(); l_y++) {
				Color l_c = new Color(p_img.getRGB(c_xGridCoords.elementAt(l_x),
												   c_yGridCoords.elementAt(l_y)));
				int l_r = l_c.getRed();
				int l_g = l_c.getGreen();
				int l_b = l_c.getBlue();
				
				// Invert l_rand to _take away_ amount of
				// color, not leave only that much.
				double l_rand = 1 - c_csprng.nextDouble()*.33;
				

				// Muliplying each color by the same value < 1 will reduce
				// the brightness of the image.
				l_r = (int) Math.floor(l_r*l_rand);
				l_g = (int) Math.floor(l_g*l_rand);
				l_b = (int) Math.floor(l_b*l_rand);
				
				// Set the new color to the Grid Cell
				Graphics2D l_g2d = p_img.createGraphics();
				l_g2d.setColor(new Color(l_r, l_g, l_b));
				l_g2d.fillRect(c_xGridCoords.elementAt(l_x), 
							   c_yGridCoords.elementAt(l_y), 
							   c_hGridSize[l_x%c_hGridSize.length], 
							   c_vGridSize[l_y%c_vGridSize.length]);				
			}
		}
		return p_img;
	}
}
 