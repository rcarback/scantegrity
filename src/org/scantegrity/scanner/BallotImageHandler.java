/*
 * @(#)BallotImageHandler.java.java
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

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.Map;
import java.util.Vector;

import org.scantegrity.common.*;
import org.scantegrity.common.gui.Dialogs;
import org.scantegrity.scanner.gui.PollingPlaceGUI;

/**
 * @author John Conway
 *
 */
public class BallotImageHandler implements ImageHandler
{
	private ScantegrityBallotReader c_reader;
	private PollingPlaceGUI c_guiRef; 
	
	private String c_results; 
	
	public BallotImageHandler(PollingPlaceGUI p_guiRef)
	{
		c_guiRef = p_guiRef;
		
		c_reader = new ScantegrityBallotReader();
		Dimension l_d = new Dimension(2550, 3300);
		Point[] l_marks = new Point[2];
		l_marks[0] = new Point(2299, 209);
		l_marks[1] = new Point(2288, 2964);
		QRCodeReader l_code = new QRCodeReader();
		l_code.setSerialBoundingBox(new Rectangle(154, 60, 240, 240));
		c_reader.setSerial(l_code);
		c_reader.setAlignment(l_marks);
		c_reader.setDimension(l_d);
		c_reader.setAlignmentMark(new CircleAlignmentMarkReader(36, .05));
		c_reader.setTolerance(.4);
		
		Vector<Integer> l_contests = new Vector<Integer>();
		l_contests.add(0);
		l_contests.add(1);
		l_contests.add(2);

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
		
		
		c_reader.setStyles(l_styles);
	}
	
	/* (non-Javadoc)
	 * @see org.scantegrity.common.ImageHandler#handleImage(java.awt.image.BufferedImage)
	 */
	public void handleImage(BufferedImage p_image)
	{
		
		try {
			Ballot l_b = c_reader.scanBallot(null, p_image);
			
			//couldn't find alignment marks 
			if(l_b == null)
			{
				//c_guiRef.setToWaiting();
				c_results = "Unable to Scan Ballot. Please Try Again";
				Dialogs.displayInfoDialog(c_results, "Unable to Read Ballot!");
				//TODO: Remove
				//c_guiRef.displayScanResults(c_results);
				return;
			}
			//c_guiRef.setToScanningBallot();
			
			c_results = "<html><p>Serial #: " + l_b.getId() + "<br/>";
			c_results += "Results:";
			Map<Integer, Integer[][]> l_res = l_b.getBallotData();
			Integer l_it[] = new Integer[l_res.size()];
			l_res.keySet().toArray(l_it);
			for (int l_key : l_it)
			{
				c_results += "Contest #" + l_key + "<br/>";
				for (Integer[] l_cres: l_res.get(l_key))
				{
					c_results += java.util.Arrays.toString(l_cres) + "<br/>";
				}
			}
		} catch (Exception l_e) {
			l_e.printStackTrace();
			return;
		}
		
		c_results +="</html>";
		c_guiRef.displayScanResults(c_results);
	}

}
