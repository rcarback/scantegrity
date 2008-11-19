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
	 * @param p_rgbvalue - 
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
		l_res[0] = (float)1.0 - p_rgbvalue[0];
		l_res[1] = (float)1.0 - p_rgbvalue[1];
		l_res[2] = (float)1.0 - p_rgbvalue[2];
		return l_res;
	}

	/* (non-Javadoc)
	 * @see java.awt.color.ColorSpace#toCIEXYZ(float[])
	 */
	@Override
	public float[] toCIEXYZ(float[] p_colorvalue) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see java.awt.color.ColorSpace#toRGB(float[])
	 */
	@Override
	public float[] toRGB(float[] colorvalue) {
		// TODO Auto-generated method stub
		return null;
	}

}
