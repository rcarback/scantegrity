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

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;
//import javax.media.jai.RenderableGraphics;

import org.scantegrity.common.*;
import org.scantegrity.common.gui.Dialogs;
import org.scantegrity.lib.Ballot;
import org.scantegrity.lib.BallotStyle;
import org.scantegrity.scanner.gui.PollingPlaceGUI;
import org.scantegrity.util.DrunkDriver;

/**
 * @author John Conway
 *
 */
public class BallotImageHandler implements ImageHandler
{
	private BallotReader c_reader;
	private PollingPlaceGUI c_guiRef; 
	private Vector<BallotStyle> c_styles = null;
	private int i = 0; 
	private String c_errDir;
	private ScannerConfig c_config;
	
	private String c_results; 
	
	public BallotImageHandler(PollingPlaceGUI p_guiRef, String p_errDir, ScannerConfig p_config)
	{
		c_guiRef = p_guiRef; 
		c_errDir = p_errDir;
		
		c_config = p_config;
		
		c_reader = c_config.getReader();
		c_styles = c_config.getStyles();
		
		/*OLD* /
		c_reader = new ScantegrityBallotReader();
		Dimension l_d = new Dimension(2550, 4200);
		Point[] l_marks = new Point[2];
		l_marks[0] = new Point(2352, 232);
		l_marks[1] = new Point(2328, 3152);
		QRCodeReader l_code = new QRCodeReader();
		l_code.setSerialBoundingBox(new Rectangle(125, 20, 310, 310));
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
		l_rects.elementAt(0).elementAt(0).add(new Rectangle(1204, 725, 160, 80));
		l_rects.elementAt(0).elementAt(0).add(new Rectangle(1367, 725, 160, 80));
		l_rects.elementAt(0).add(new Vector<Rectangle>());
		l_rects.elementAt(0).elementAt(1).add(new Rectangle(1204, 866, 160, 80));
		l_rects.elementAt(0).elementAt(1).add(new Rectangle(1367, 866, 160, 80));
		l_rects.elementAt(0).add(new Vector<Rectangle>());
		l_rects.elementAt(0).elementAt(2).add(new Rectangle(1204, 1016, 160, 80));
		l_rects.elementAt(0).elementAt(2).add(new Rectangle(1367, 1016, 160, 80));

		l_rects.add(new Vector<Vector<Rectangle>>());
		l_rects.elementAt(1).add(new Vector<Rectangle>());
		l_rects.elementAt(1).elementAt(0).add(new Rectangle(1204, 1655, 160, 80));
		l_rects.elementAt(1).elementAt(0).add(new Rectangle(1367, 1655, 160, 80));
		l_rects.elementAt(1).elementAt(0).add(new Rectangle(1527, 1655, 160, 80));
		l_rects.elementAt(1).add(new Vector<Rectangle>());
		l_rects.elementAt(1).elementAt(1).add(new Rectangle(1204, 1805, 160, 80));
		l_rects.elementAt(1).elementAt(1).add(new Rectangle(1367, 1805, 160, 80));
		l_rects.elementAt(1).elementAt(1).add(new Rectangle(1527, 1805, 160, 80));
		l_rects.elementAt(1).add(new Vector<Rectangle>());
		l_rects.elementAt(1).elementAt(2).add(new Rectangle(1204, 1948, 160, 80));
		l_rects.elementAt(1).elementAt(2).add(new Rectangle(1367, 1948, 160, 80));
		l_rects.elementAt(1).elementAt(2).add(new Rectangle(1527, 1948, 160, 80));
		
		l_rects.add(new Vector<Vector<Rectangle>>());
		l_rects.elementAt(2).add(new Vector<Rectangle>());
		l_rects.elementAt(2).elementAt(0).add(new Rectangle(2073, 725, 160, 80));
		l_rects.elementAt(2).elementAt(0).add(new Rectangle(2233, 725, 160, 80));
		
		
		BallotStyle l_style = new BallotStyle(0, l_contests, l_rects, true);
		
		c_styles = new BallotStyle[1];
		c_styles[0] = l_style;
		
		//c_reader.setStyles(l_styles);
		/*END OLD*/
	}
	
	/* (non-Javadoc)
	 * @see org.scantegrity.common.ImageHandler#handleImage(java.awt.image.BufferedImage)
	 */
	public void handleImage(BufferedImage p_image)
	{
		
		//long l_start = System.currentTimeMillis(); 
		try {
			//Give me your keys
			if(DrunkDriver.isDrunk(p_image, 10))
				return;
		
			Ballot l_b = c_reader.scanBallot(c_styles, p_image);
			
			//System.out.println(System.currentTimeMillis() - l_start);
			//couldn't find alignment marks 
			if(l_b == null || l_b.getId() == null)
			{
				//c_guiRef.setToWaiting();
				c_results = "Unable to Scan Ballot. Please Try Again";
				Dialogs.displayInfoDialog(c_results, "Unable to Read Ballot!");
				
				//Move file to destination directory
				ImageIO.write(p_image, "tiff", new File(c_errDir + "/error" + i + ".tiff"));
				i++;
				
				//TODO: Remove
				//c_guiRef.displayScanResults(c_results);
				return;
			}
			//c_guiRef.setToScanningBallot();
			
			c_results = "Serial #: " + l_b.getId() + "\n";
			c_results += "Results:";
			Map<Integer, Integer[][]> l_res = l_b.getBallotData();
			Integer l_it[] = new Integer[l_res.size()];
			l_res.keySet().toArray(l_it);
			for (int l_key : l_it)
			{
				c_results += "\n\tContest #" + l_key + "\n";
				for (Integer[] l_cres: l_res.get(l_key))
				{
					c_results += "\t\t" + java.util.Arrays.toString(l_cres) + "\n";
				}
			}
		} catch (Exception l_e) {
			c_results = "Unable to Scan Ballot. Please Try Again";
			Dialogs.displayInfoDialog(c_results, "Unable to Read Ballot!");
			//ImageIO.write(p_image, "tiff", new File(c_errDir + "/error" + i + ".tiff"));
			i++;
			l_e.printStackTrace();
			return;
		}
		
		//Send the results to the gui
		c_guiRef.addBallotResults(c_results);
	}

}
