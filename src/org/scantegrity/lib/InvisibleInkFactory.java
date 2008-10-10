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
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.ArrayList;
 
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
	static final short TWO = 2; 
	static final short THREE = 4; 
	static final short FOUR = 8; 
	static final byte FIVE = 16; 
	static final short SIX = 32; 
	static final short SEVEN = 64; 
	static final short EIGHT = 128; 
	
	
	private short c_flags;
	private Font c_font;
	private int c_padding;
	private SecureRandom c_csprng;
	
	/**
	 * Default constructor for the class, which uses a 12pt Serif
	 * Font, 10 pixel padding, and block mode without any csprng stuff.   
	 */
	public InvisibleInkFactory () {
		this ("Serif", 18, 10, (short)0, null);
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
	 * @param p_
	 */
	public InvisibleInkFactory (String p_fontName, int p_fontSize, int p_pad,
			short p_flags, SecureRandom p_csprng) {

		c_flags = p_flags;
		c_font = new Font(p_fontName, Font.BOLD, p_fontSize);
		c_padding = p_pad;
		c_csprng = p_csprng;		
	}
	
	
	
	public BufferedImage getBufferedImage(String p_txt) {
		float l_height, l_width;
		BufferedImage l_ret;
		Graphics2D l_g2d;
		
		//Get the size of the string
		FontRenderContext l_frc = new FontRenderContext(null, false, true);
		l_height = (int)c_font.getStringBounds(p_txt, l_frc).getHeight();
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
		l_g2d.setColor(Color.MAGENTA);
		l_g2d.fillRect(0, 0, (int)l_width, (int)l_height);
		
		//Draw Text
		l_g2d.setFont(c_font);
		l_g2d.setColor(Color.CYAN);
		
		float l_fontOff = (l_height-2*c_padding-c_font.getLineMetrics(p_txt, l_frc).getAscent())/2;
		
		l_g2d.drawString(p_txt, c_padding, l_height-c_padding-l_fontOff); 
						 ///(int)(l_height-c_padding-(c_font.getStringBounds(p_txt, l_frc).getHeight()-c_font.getLineMetrics(p_txt, l_frc).getAscent())));
		
	    l_g2d.setColor(Color.GREEN);
	    l_g2d.drawLine(0, c_padding, (int)l_width, c_padding);
	    l_g2d.drawLine(0, (int)l_height-c_padding, (int)l_width, (int)l_height-c_padding);
	    l_g2d.drawLine(0, (int)(l_height-2*c_padding-c_font.getLineMetrics(p_txt, l_frc).getAscent()), (int)l_width, (int)(l_height-2*c_padding-c_font.getLineMetrics(p_txt, l_frc).getAscent()));
		//Process flags
		if ((c_flags & BLOCK) == 0) {
			//Block Operations
		} else {
			//Pixel Operations
		}
		
		return l_ret;
	}
}
 