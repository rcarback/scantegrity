/*
 * @(#)ScantegrityBallotReaderTest.java.java
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
package org.scantegrity.scanner.test;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;

import org.scantegrity.scanner.Ballot;
import org.scantegrity.scanner.CircleAlignmentMarkReader;
import org.scantegrity.scanner.QRCodeReader;
import org.scantegrity.scanner.ScantegrityBallotReader;

/**
 * Tests the scantegrityBallotReader object methods.
 * 
 * Significantly updated on 3/28 to make it a more proper testing class.
 * 
 * @author Richard Carback
 *
 */
public class ScantegrityBallotReaderTest
{
	private static String basedir = "testing/scanner/sample-images/Basic/";
	private static String tests[] = { /**/"basic.tiff", 
										"crop.tiff", 
									 	"shiftd.tiff", 
									 	"shiftl.tiff", 
									 	"shiftlu.tiff", 
									 	"shiftld.tiff", 
									 	"shiftru.tiff",
									 	"shiftr.tiff",
									 	"shiftrd.tiff", 
									 	"rotr.tiff", 
									 	"rotl.tiff", 
									 	"inv.tiff", 
										"inv-shiftdr.tiff", 
									 	"inv-shiftd.tiff", 
									 	"inv-shiftdl.tiff", 
									 	"inv-shiftul.tiff", 
									 	"inv-shiftur.tiff", 
									 	"inv-shiftu.tiff", 
									 	"inv-rotl.tiff", 
									 	"inv-rotr.tiff", 
									 	"wacky1.tiff", 
									 	"wacky2.tiff",
									 	"wacky3.tiff"/**/
									};
	
	
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException
	{
		ScantegrityBallotReader l_reader = new ScantegrityBallotReader();
		Dimension l_d = new Dimension(2550, 3300);
		Point[] l_marks = new Point[2];
		l_marks[0] = new Point(2341, 478);
		l_marks[1] = new Point(2390, 1609);
		QRCodeReader l_code = new QRCodeReader();
		l_reader.setSerial(l_code);
		l_reader.setAlignment(l_marks);
		l_reader.setDimension(l_d);
		l_reader.setAlignmentMark(new CircleAlignmentMarkReader(36, .05));
		l_reader.setTolerance(.4);

		for (String test: tests)
		{
			try {
				System.out.println("Test Case: " + test);
				long l_start = System.currentTimeMillis();
				PlanarImage pi = JAI.create("fileload", basedir+test);
				BufferedImage img = pi.getAsBufferedImage();
				long l_load = System.currentTimeMillis();
				System.out.print("Load: " + (l_load-l_start) + "ms ");
				Ballot l_b = l_reader.scanBallot(null, img);
				long l_last = System.currentTimeMillis();
				System.out.print("Scan: " + (l_last-l_load) + "ms ");
				System.out.println("Serial #: " + l_b.getId());
				System.out.println("Total: " + (l_last-l_start) + "ms ");
			} catch (Exception l_e) {
				l_e.printStackTrace();
				return;
			}
		}
	}
}
