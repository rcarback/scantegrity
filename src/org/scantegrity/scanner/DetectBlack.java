/*
 * @(#)DetectBlack.java.java
 *  
 * Copyright (C) 2008-2009 Scantegrity Project
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

import java.awt.Color;
import java.awt.image.BufferedImage;

/**
 * Method to detect if a particular point an in image is black or white.
 * @author Richard Carback
 *
 */
public final class DetectBlack
{
	private static int c_br = Color.black.getRed();
	private static int c_bg = Color.black.getGreen();
	private static int c_bb = Color.black.getBlue();
	
	/**
	 * Is the color at this location black?
	 * 
	 * TODO: The tolerance here might be exceptionally high!
	 * 
	 * @param p_x x location of the pixel
	 * @param p_y y location of the pixel
	 * @return if the color at the location of the pixel is black.
	 */
	public static boolean isBlack(int p_x, int p_y, BufferedImage p_img) {
		int l_cr, l_cg, l_cb;
		Color l_color = new Color(p_img.getRGB(p_x, p_y));
		l_cr = l_color.getRed();
		l_cg = l_color.getGreen();
		l_cb = l_color.getBlue();
		//System.out.println("Black is:" + c_br + ", " + c_bg + ", " + c_bb);
		//System.out.println("Color is:" + l_cr + ", " + l_cg + ", " + l_cb);
		//This tolerance might be too high! 
		//TODO: Might want to make it configurable.
		if (Math.abs(l_cr - c_br) <= 100 
				&& Math.abs(l_cg - c_bg) <= 100 
				&& Math.abs(l_cb - c_bb) <=100) 
		{
			return true;
		}
		return false;
	}	
}