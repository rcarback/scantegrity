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

import org.scantegrity.scanner.CircleAlignmentMarkReader;
import org.scantegrity.scanner.QRCodeReader;
import org.scantegrity.scanner.ScantegrityBallotReader;

/**
 * Tests the scantegrityBallotReader object methods.
 * @author Richard Carback
 *
 */
public class ScantegrityBallotReaderTest
{

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException
	{
		// TODO Auto-generated method stub
		//Alignment marks
		/*private Point[] c_alignment;
		private Integer c_radius;
		private Double c_tolerance;
		//Dimensions of the ballot
		private Rectangle c_dimensions;
		private SerialNumberReader c_serial = null;
		private BallotStyle[] c_styles = null;
		*/
		
		ScantegrityBallotReader l_reader = new ScantegrityBallotReader();
		Dimension l_d = new Dimension(2550, 3300);
		Point[] l_marks = new Point[2];
		l_marks[0] = new Point(2341, 478);
		l_marks[1] = new Point(2390, 1609);
		
		//Test 2
		/*l_marks[0] = new Point(2248, 484);
		l_marks[1] = new Point(2292, 1622);
		//Test 3
		l_marks[0] = new Point(2248, 384);
		l_marks[1] = new Point(2292, 1522);
		//Test 4
		l_marks[0] = new Point(2348, 384);
		l_marks[1] = new Point(2392, 1522);
		//Test 5
		l_marks[0] = new Point(2048, 184);
		l_marks[1] = new Point(2092, 1322);
		//Test 6
		l_marks[0] = new Point(2148, 584);
		l_marks[1] = new Point(2492, 1322);
		//Test 7 -- False Positives?
		l_marks[0] = new Point(1148, 584);
		l_marks[1] = new Point(1492, 1322);*/
		QRCodeReader l_code = new QRCodeReader();
		l_reader.setSerial(l_code);
		l_reader.setAlignment(l_marks);
		l_reader.setDimension(l_d);
		l_reader.setAlignmentMark(new CircleAlignmentMarkReader(36, .05));
		PlanarImage pi = JAI.create("fileload", "testing/scanner/sample-images/test1-inv-rotr.tiff");
		BufferedImage img = pi.getAsBufferedImage();
		long l_start = System.currentTimeMillis();
		l_reader.scanBallot(null, img);
		System.out.println("Time (sans Loading): " + (System.currentTimeMillis() - l_start) + "ms");
	}

}
