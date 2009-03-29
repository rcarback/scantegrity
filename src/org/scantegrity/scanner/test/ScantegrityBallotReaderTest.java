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
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;

import org.scantegrity.scanner.Ballot;
import org.scantegrity.scanner.BallotStyle;
import org.scantegrity.scanner.CircleAlignmentMarkReader;
import org.scantegrity.scanner.QRCodeReader;
import org.scantegrity.scanner.ScantegrityBallotReader;

import com.lowagie.text.pdf.hyphenation.TernaryTree.Iterator;

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
		
		Vector<Integer> l_contests = new Vector<Integer>();
		l_contests.add(0);
		l_contests.add(1);

		Vector<Vector<Vector<Rectangle>>> l_rects = new Vector<Vector<Vector<Rectangle>>>();
		//Contest 0
		l_rects.add(new Vector<Vector<Rectangle>>());
		l_rects.elementAt(0).add(new Vector<Rectangle>());
		l_rects.elementAt(0).elementAt(0).add(new Rectangle(1520, 795, 100, 40));
		l_rects.elementAt(0).elementAt(0).add(new Rectangle(1655, 795, 100, 40));
		l_rects.elementAt(0).add(new Vector<Rectangle>());
		l_rects.elementAt(0).elementAt(1).add(new Rectangle(1521, 920, 100, 40));
		l_rects.elementAt(0).elementAt(1).add(new Rectangle(1655, 920, 100, 40));

		l_rects.add(new Vector<Vector<Rectangle>>());
		l_rects.elementAt(1).add(new Vector<Rectangle>());
		l_rects.elementAt(1).elementAt(0).add(new Rectangle(1525, 1200, 100, 40));
		l_rects.elementAt(1).elementAt(0).add(new Rectangle(1660, 1200, 100, 40));
		l_rects.elementAt(1).elementAt(0).add(new Rectangle(1800, 1200, 100, 40));
		l_rects.elementAt(1).add(new Vector<Rectangle>());
		l_rects.elementAt(1).elementAt(1).add(new Rectangle(1525, 1300, 100, 40));
		l_rects.elementAt(1).elementAt(1).add(new Rectangle(1660, 1300, 100, 40));
		l_rects.elementAt(1).elementAt(1).add(new Rectangle(1800, 1300, 100, 40));
		l_rects.elementAt(1).add(new Vector<Rectangle>());
		l_rects.elementAt(1).elementAt(2).add(new Rectangle(1525, 1445, 100, 40));
		l_rects.elementAt(1).elementAt(2).add(new Rectangle(1660, 1445, 100, 40));
		l_rects.elementAt(1).elementAt(2).add(new Rectangle(1800, 1445, 100, 40));
		
		
		BallotStyle l_style = new BallotStyle(0, l_contests, l_rects, true);
		BallotStyle l_styles[] = new BallotStyle[1];
		l_styles[0] = l_style;
		
		
		l_reader.setStyles(l_styles);
		
		for (String test: tests)
		{
			try {
				System.out.println("Test Case: " + test);
				long l_start = System.currentTimeMillis();
				PlanarImage pi = JAI.create("fileload", basedir+test);
				BufferedImage img = pi.getAsBufferedImage();
				long l_load = System.currentTimeMillis();
				Ballot l_b = l_reader.scanBallot(null, img);
				long l_last = System.currentTimeMillis();
				System.out.println("Serial #: " + l_b.getId());
				System.out.println("\tResults:");
				Map<Integer, Integer[][]> l_res = l_b.getBallotData();
				Integer l_it[] = new Integer[l_res.size()];
				l_res.keySet().toArray(l_it);
				for (int l_key : l_it)
				{
					System.out.println("\t\tContest #" + l_key);
					for (Integer[] l_cres: l_res.get(l_key))
					{
						System.out.println("\t\t\t" + java.util.Arrays.toString(l_cres));
					}
				}
				System.out.print("Load: " + (l_load-l_start) + "ms ");
				System.out.print("Scan: " + (l_last-l_load) + "ms ");
				System.out.println("Total: " + (l_last-l_start) + "ms ");
			} catch (Exception l_e) {
				l_e.printStackTrace();
				return;
			}
		}
	}
}
