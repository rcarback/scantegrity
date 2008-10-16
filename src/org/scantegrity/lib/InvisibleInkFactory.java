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
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.ArrayList;
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
	static final short MUNGE = 2; 
	static final short THREE = 4; 
	static final short FOUR = 8; 
	static final byte FIVE = 16; 
	static final short SIX = 32; 
	static final short SEVEN = 64; 
	static final short EIGHT = 128; 
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
	private int[] c_hGridSpace = { 2 };
	private int[] c_vGridSpace = { 2 };
	private int[] c_hGridSize = { 20 };
	private int[] c_vGridSize = { 20 };
	private Color c_gridColor = Color.WHITE;
	private Vector<Integer> c_xGridCoords = null;
	private Vector<Integer> c_yGridCoords = null;
	/**
	 * Default constructor for the class, which uses a 18pt Serif
	 * Font, 10 pixel padding, and block mode without any csprng stuff.   
	 */
	public InvisibleInkFactory () {
		this ("SansSerif", 256, 20, (short)1, null);
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
		
	    l_g2d.setColor(Color.GREEN);
	    /*
	    l_g2d.drawLine(0, c_padding, (int)l_width, c_padding);
	    l_g2d.drawLine(0, (int)l_height-c_padding, (int)l_width, 
	    					(int)l_height-c_padding);
	    l_g2d.drawLine(0, (int)(l_height-2*c_padding-c_font.getLineMetrics(p_txt, l_frc).getAscent()), (int)l_width, (int)(l_height-2*c_padding-c_font.getLineMetrics(p_txt, l_frc).getAscent()));
	    */
		//Process flags
		if ((c_flags & BLOCK) == BLOCK) {
			//Block Operations
			l_ret = GenGrid(l_ret);
			if((c_flags & MUNGE) == MUNGE) {
			}
		} else {
			//Pixel Operations
		}
		
		return l_ret;
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
	private BufferedImage GenGrid(BufferedImage p_img) {
		Graphics2D l_g2d = p_img.createGraphics();
		
		c_xGridCoords = new Vector<Integer>();
		c_yGridCoords = new Vector<Integer>();
		int l_index = 0;
		int l_width = p_img.getWidth();
		int l_height = p_img.getHeight();
		int l_h = c_hGridSize[0];
		int l_v = c_vGridSize[0];
		do {
			l_g2d.setColor(c_gridColor);
			//Add vertical line
			if (l_h < l_width - c_hGridSpace[l_index%c_hGridSpace.length]) {
				l_g2d.fillRect(l_h, 0, 
								c_hGridSpace[l_index%c_hGridSpace.length], 
								l_height);
				l_h += c_hGridSize[l_index%c_hGridSize.length] 
			                     + c_hGridSpace[l_index%c_hGridSpace.length];

				c_xGridCoords.add(l_h-c_hGridSize[l_index%c_hGridSize.length]);
				
			}
			
			//Add horizontal line
			if (l_v < l_height - c_vGridSpace[l_index%c_vGridSpace.length]) {
				l_g2d.fillRect(0, l_v, l_width, 
								c_vGridSpace[l_index%c_vGridSpace.length]);
				l_v += c_vGridSize[l_index%c_vGridSize.length] 
		                     + c_vGridSpace[l_index%c_vGridSpace.length];

				c_yGridCoords.add(l_v-c_vGridSize[l_index%c_vGridSize.length]);
			} 
 
			
			//Prevent index OOB
			if (l_h >= l_width) l_h = l_width-1;
			if (l_v >= l_height) l_v = l_height-1;
			
			l_index++;
			//Current Block
			int l_xCoord = c_xGridCoords.lastElement();
			int l_yCoord = c_yGridCoords.lastElement();
			Color l_c = new Color(p_img.getRGB(l_h - (l_h-l_xCoord)/2, 
											   l_v - (l_v-l_yCoord)/2));
			l_g2d.setColor(l_c);
			l_g2d.fillRect(l_xCoord, l_yCoord, (l_h-l_xCoord), 
						   (l_v-l_yCoord));
			//Blocks above or below current block.
			for (int l_i = l_index-1; l_i >= 0; l_i--) {
				int l_size = 0;
				
				//Horizontal blocks
				l_size = c_hGridSize[l_i%c_hGridSize.length];
				if (l_i < c_xGridCoords.size() &&
						(l_width-c_xGridCoords.elementAt(l_i) > l_size)) {
					l_c = new Color(p_img.getRGB(
									l_size/2 + c_xGridCoords.elementAt(l_i), 
									l_v - (l_v-l_yCoord)/2));
					l_g2d.setColor(l_c);
					l_g2d.fillRect(c_xGridCoords.elementAt(l_i), 
								   l_yCoord, 
								   l_size, 
								   (l_v-l_yCoord));
				}
				
				//Vertical Blocks
				l_size = c_vGridSize[l_i%c_vGridSize.length];
				if (l_i < c_yGridCoords.size() && 
						(l_height - c_yGridCoords.elementAt(l_i) > l_size )) {
					l_c = new Color(p_img.getRGB(l_h - (l_h-l_xCoord)/2, 
									l_size/2 + c_yGridCoords.elementAt(l_i)));
					l_g2d.setColor(l_c);
					l_g2d.fillRect(l_xCoord, 
								   c_yGridCoords.elementAt(l_i), 
								   (l_h-l_xCoord), 
								   l_size);

				}
			}	
		} while (l_h < l_width - c_hGridSpace[l_index%c_hGridSpace.length] ||
				 l_v < l_height - c_vGridSpace[l_index%c_vGridSpace.length]);

		
		
		return p_img;
	}	
}
 