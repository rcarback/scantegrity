/*
 * @(#)CMYKColorSpace.java
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

import java.awt.color.ColorSpace;
import java.io.Serializable;

/**
 * A simple CMYK color space. ICC provides a CMYK.pf file you can load into
 * the ICC ColorSpace. This is a convenience class to avoid doing that.
 * 
 * @author carback1
 * @version 0.0.1 
 * @date 18/11/09
 * @see java.awt.color.ColorSpace, java.awt.color.ICC_ColorSpace
 */
public class CMYKColorSpace extends ColorSpace implements Serializable {

	private static final long serialVersionUID = -5982040365555064012L;
	
	/**
	 * 
	 */
	public CMYKColorSpace() {
		super(ColorSpace.TYPE_CMYK, 4);
	}

	/* (non-Javadoc)
	 * @see java.awt.color.ColorSpace#fromCIEXYZ(float[])
	 */
	@Override
	public float[] fromCIEXYZ(float[] p_colorvalue) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Converts a given RGB to CMYK. RGB doesn't really use black, so K will 
	 * always be 0. On printers, the black should actually look dark brown.
	 * RGB (an additive space) is simply the backwards from CMY (a subtractive 
	 * space), so all we do is:
	 * 
	 * 		C = 1-R
	 * 		M = 1-G
	 * 		Y = 1-B
	 * 
	 * @param p_rgbvalue - The color to translate 
	 * @return a float[4] of the CMYK values.
	 * @see java.awt.color.ColorSpace#fromRGB(float[])
	 */
	@Override
	public float[] fromRGB(float[] p_rgbvalue) {
		/* TODO: Maybe we should do a better job to determine when black should 
		 * be used and pulled out? -- At this time, it's not necessary for our
		 * (Scantegrity's) uses.
		 */
		float[] l_res = {0,0,0,0};
		if (p_rgbvalue.length >= 3) {
			l_res[0] = (float)1.0 - p_rgbvalue[0];
			l_res[1] = (float)1.0 - p_rgbvalue[1];
			l_res[2] = (float)1.0 - p_rgbvalue[2];			
		}
		return normalize(l_res);
	}

	/* (non-Javadoc)
	 * @see java.awt.color.ColorSpace#toCIEXYZ(float[])
	 */
	@Override
	public float[] toCIEXYZ(float[] p_colorvalue) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Converts CMYK colors to RGB. Note that converting back will be lossy. The
	 * formula for this is:
	 * 
	 * K = 1 - K (go to additive)
	 * R = K * (1 - C)
	 * G = K * (1 - M)
	 * B = K * (1 - Y)
	 * 
	 * @param p_colorvalue The color in CMYK.
	 * @see java.awt.color.ColorSpace#toRGB(float[])
	 */
	@Override
	public float[] toRGB(float[] p_colorvalue) {
		float[] l_res = {0,0,0};
		if (p_colorvalue.length >= 4)
		{
			float l_black = (float)1.0 - p_colorvalue[3]; 
			l_res[0] = l_black * ((float)1.0 - p_colorvalue[0]);
			l_res[1] = l_black * ((float)1.0 - p_colorvalue[1]);
			l_res[2] = l_black * ((float)1.0 - p_colorvalue[2]);			
		}
		return normalize(l_res);
	}
	
	/**
	 * Normalize ensures all color values returned are between 0 and 1.
	 * 
	 * @param p_colors
	 * @return p_colors, with any values greater than 1 set to 1, and less than
	 * 0 set to 0.
	 */
	private float[] normalize(float[] p_colors) {
		for (int l_i = 0; l_i < p_colors.length; l_i++) {
			if (p_colors[l_i] > (float)1.0) p_colors[l_i] = (float)1.0;
			else if (p_colors[l_i] < (float)0.0) p_colors[l_i] = (float)0.0;
		}		
		return p_colors;
	}
}
