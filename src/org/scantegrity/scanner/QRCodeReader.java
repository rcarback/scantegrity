/*
 * @(#)QRCodeReader.java.java
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

import java.awt.Point;
import java.awt.image.BufferedImage;

import com.google.zxing.MultiFormatReader;
import com.google.zxing.ReaderException;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageMonochromeBitmapSource;
import com.google.zxing.client.result.ParsedResult;
import com.google.zxing.client.result.ResultParser;

/**
 * @author Richard Carback
 *
 */
public class QRCodeReader implements SerialNumberReader
{
	private Point c_topleft = new Point(144, 112);
	private Point c_bottomright = new Point(278, 242);

	/* (non-Javadoc)
	 * @see org.scantegrity.scanner.SerialNumberReader#getBallotStyle(java.awt.image.BufferedImage)
	 */

	public Integer getBallotStyle(BufferedImage p_ballot) throws Exception
	{
		return null;
	}

	/* (non-Javadoc)
	 * @see org.scantegrity.scanner.SerialNumberReader#getSerialNumber(java.awt.image.BufferedImage)
	 */
	/**
	 * This method assumes that p_ballot is a properly rotated and bounded box.
	 * Since different barcode setups may require a whole ballot to process, it
	 * probably shouldn't do that.
	 */
	public Integer getSerialNumber(BufferedImage p_ballot) throws Exception
	{
		BufferedImageMonochromeBitmapSource l_serial;
		try {
			int l_start = (int) System.currentTimeMillis();
			System.out.println("Trying...");
			l_serial = new BufferedImageMonochromeBitmapSource(p_ballot.getSubimage(144, 112, 278-144, 242-112));
			Result result = new MultiFormatReader().decode(l_serial);
			System.out.println("The Result: " + result.getText());
			System.out.println("Time:" + (int)(System.currentTimeMillis()-l_start));
			return Integer.parseInt(result.getText());
		} catch (ReaderException e) {
			return null;			
		}
	}

}
