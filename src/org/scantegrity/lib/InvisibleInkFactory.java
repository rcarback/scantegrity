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

	private byte l_flags;
	private Font l_font;
	private int l_fontSize;
	private int l_padding;
	private SecureRandom l_csprng;
	
	/**
	 * Default constructor for the class, which uses a 12pt Times New Roman 
	 * Font, 10 pixel padding, and block mode without any csprng stuff.
	 *   
	 */
	public InvisibleInkFactory ()
	{
		//Defaults
		InvisibleInkFactory ("Times New Roman", 12, 10, 0);
	}
	
	
	/**
	 * Standard constructor that allows user to set font, fontsize, padding,
	 * flags for the class, and optionally, a CSPRNG.
	 * 
	 * @param p_fontName - Name of the font, e.g. "Times New Roman".
	 * @param p_
	 */
	public InvisibleInkFactory (String p_fontName, int p_fontSize, int p_pad,
			byte p_flags, SecureRandom p_csprng = null) 
			throws FontNotFoundException {

		l_flags = p_flags;
		l_font = new Font(p_fontName, Font.BOLD, p_fontsize);
		
	}
	
}
 