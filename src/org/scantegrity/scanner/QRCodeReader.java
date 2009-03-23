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
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

import com.google.zxing.MultiFormatReader;
import com.google.zxing.ReaderException;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageMonochromeBitmapSource;

/**
 * @author Richard Carback
 *
 */
public class QRCodeReader implements SerialNumberReader
{
	private Rectangle c_boundingBox = new Rectangle(144, 112, 278, 242);

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
	public Integer getSerialNumber(BufferedImage p_img, AffineTransformOp p_op) 
	throws Exception
	{
		BufferedImageMonochromeBitmapSource l_serial;
		try {
			int l_start = (int) System.currentTimeMillis();
			System.out.println("Trying...");
			Point2D l_tst = new Point(144, 112);
			p_op.getPoint2D(l_tst, l_tst);
			System.out.println(l_tst.getX() + ", " + l_tst.getY());
			Rectangle2D l_tmp = p_op.getBounds2D(p_img.getSubimage(144, 112, 278-144, 242-112));
			System.out.println("Bounding Box:" + l_tmp.getWidth() + ", " + l_tmp.getHeight() + " " + l_tmp.getX() + ", " + l_tmp.getY());
			BufferedImage l_img = new BufferedImage((int)l_tmp.getWidth(), (int)l_tmp.getHeight(), p_img.getType());
			p_op.filter(p_img.getSubimage((int)l_tmp.getX(), (int)l_tmp.getY(), (int)l_tmp.getWidth(), (int)l_tmp.getWidth()), l_img);
			l_serial = new BufferedImageMonochromeBitmapSource(l_img);
			Result result = new MultiFormatReader().decode(l_serial);
			System.out.println("The Result: " + result.getText());
			System.out.println("Time:" + (int)(System.currentTimeMillis()-l_start) + "ms");
			return Integer.parseInt(result.getText());
		} catch (ReaderException e) {
			e.printStackTrace();
			return null;			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
