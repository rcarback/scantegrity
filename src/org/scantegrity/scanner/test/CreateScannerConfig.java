/*
 * @(#)CreateScannerConfig.java.java
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
import java.beans.XMLEncoder;
import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.Vector;

import org.scantegrity.lib.methods.Contestant;
import org.scantegrity.lib.methods.InstantRunoffTally;
import org.scantegrity.scanner.BallotStyle;
import org.scantegrity.scanner.CircleAlignmentMarkReader;
import org.scantegrity.scanner.Contest;
import org.scantegrity.scanner.QRCodeReader;
import org.scantegrity.scanner.ScannerConfig;
import org.scantegrity.scanner.ScantegrityBallotReader;

/**
 * Creates a scanner configuration xml file called "ScannerConfig.xml" in the 
 * testing directory.
 * 
 * @author Richard Carback
 *
 */
public class CreateScannerConfig
{
	private static String c_loc = "testing/scanner/";
	private static String c_name = "ScannerConfig.xml";

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		ScannerConfig l_config = new ScannerConfig();
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
		
		Vector<Vector<Integer>> l_contestantIds = new Vector<Vector<Integer>>();
		l_contestantIds.add(new Vector<Integer>());
		l_contestantIds.get(0).add(0);
		l_contestantIds.get(0).add(1);
		l_contestantIds.add(new Vector<Integer>());
		l_contestantIds.get(1).add(0);
		l_contestantIds.get(1).add(1);
		l_contestantIds.get(1).add(2);
		
		
		BallotStyle l_style = new BallotStyle(0, l_contests, l_rects, true);
		l_style.setContestantIds(l_contestantIds);
		BallotStyle l_styles[] = new BallotStyle[1];
		l_styles[0] = l_style;
		
		Vector<Contest> l_c = new Vector<Contest>();
		Contest l_x = new Contest();
		Vector<Contestant> l_can = new Vector<Contestant>();
		l_can.add(new Contestant(0, "Bruce R. Williams"));
		l_can.add(new Contestant(1, "Write-In Candidate"));
		l_x.setId(0);
		l_x.setContestName("Mayor");
		l_x.setContestants(l_can);
		l_x.setMethod(new InstantRunoffTally());
		l_c.add(l_x);

		l_x = new Contest();
		l_can = new Vector<Contestant>();
		l_can.add(new Contestant(0, "Bridget Bowers"));
		l_can.add(new Contestant(1, "Dan Robinson"));
		l_can.add(new Contestant(2, "Write-In Candidate"));
		l_x.setId(1);
		l_x.setContestants(l_can);
		l_x.setContestName("Council Member Ward 3");
		l_x.setMethod(new InstantRunoffTally());
		l_c.add(l_x);		
		
		Vector<String> l_locs = new Vector<String>();
		l_locs.add("/home/");
		l_locs.add("/media/*/");
		
		Vector<BallotStyle> l_s = new Vector<BallotStyle>();
		l_s.add(l_style);
		
		Vector<String> l_j = new Vector<String>();
		l_j.add("Richard Carback");
		l_config.setChiefJudges(l_j);
		l_config.setLocation("1000 Hilltop Circle, Baltimore MD 21250");
		l_config.setName("ScannerConfig XML Test");
		l_config.setOutputLocs(l_locs);
		l_config.setPollID(0);
		l_config.setReader(l_reader);
		l_config.setContests(l_c);
		l_config.setDate(new Date());
		l_config.setScannerID(0);
		l_config.setStyles(l_s);
		
		XMLEncoder e;
		try
		{
			e = new XMLEncoder(new BufferedOutputStream(new FileOutputStream(c_loc + c_name)));
			e.writeObject(l_config);
			e.close();		
		}
		catch (FileNotFoundException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
	}

}
