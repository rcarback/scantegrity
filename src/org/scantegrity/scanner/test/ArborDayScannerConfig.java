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
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Vector;

import org.scantegrity.lib.BallotStyle;
import org.scantegrity.lib.Contest;
import org.scantegrity.lib.Contestant;
import org.scantegrity.lib.methods.InstantRunoffTally;
import org.scantegrity.lib.methods.PluralityTally;
import org.scantegrity.scanner.CircleAlignmentMarkReader;
import org.scantegrity.scanner.QRCodeReader;
import org.scantegrity.scanner.ScannerConfig;
import org.scantegrity.scanner.ScantegrityBallotReader;

/**
 * Scanner config creator. Translated from Stefan's BallotGeom.xml file.
 * 
 * Unfortunately, don't have a translator module for that just yet.
 * 
 * @author Richard Carback
 *
 */
public class ArborDayScannerConfig
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
		
		Dimension l_d = new Dimension(2550, 4200);
		Point[] l_marks = new Point[2];
		l_marks[0] = new Point(2344, 187);
		l_marks[1] = new Point(2344, 3118);
		QRCodeReader l_code = new QRCodeReader();
		l_code.setSerialBoundingBox(new Rectangle(150, 150, 375, 375));
		l_reader.setSerial(l_code);
		l_reader.setAlignment(l_marks);
		l_reader.setDimension(l_d);
		l_reader.setAlignmentMark(new CircleAlignmentMarkReader(75/2, .05));
		l_reader.setTolerance(.4);
		
		Vector<Integer> l_contests = new Vector<Integer>();
		l_contests.add(0);
		l_contests.add(1);
		l_contests.add(2);
		l_contests.add(3);

		Vector<Vector<Vector<Rectangle>>> l_rects = new Vector<Vector<Vector<Rectangle>>>();
		//Contest 0
		l_rects.add(new Vector<Vector<Rectangle>>());
		l_rects.elementAt(0).add(new Vector<Rectangle>());
		l_rects.elementAt(0).elementAt(0).add(new Rectangle(1515, 645, 106, 36));
		l_rects.elementAt(0).elementAt(0).add(new Rectangle(1733, 645, 106, 36));
		l_rects.elementAt(0).elementAt(0).add(new Rectangle(1941, 645, 106, 36));
		l_rects.elementAt(0).elementAt(0).add(new Rectangle(2157, 645, 106, 36));
		l_rects.elementAt(0).add(new Vector<Rectangle>());
		l_rects.elementAt(0).elementAt(1).add(new Rectangle(1515, 745, 106, 36));
		l_rects.elementAt(0).elementAt(1).add(new Rectangle(1733, 745, 106, 36));
		l_rects.elementAt(0).elementAt(1).add(new Rectangle(1941, 745, 106, 36));
		l_rects.elementAt(0).elementAt(1).add(new Rectangle(2157, 745, 106, 36));
		l_rects.elementAt(0).add(new Vector<Rectangle>());
		l_rects.elementAt(0).elementAt(2).add(new Rectangle(1515, 845, 106, 36));
		l_rects.elementAt(0).elementAt(2).add(new Rectangle(1733, 845, 106, 36));
		l_rects.elementAt(0).elementAt(2).add(new Rectangle(1941, 845, 106, 36));
		l_rects.elementAt(0).elementAt(2).add(new Rectangle(2157, 845, 106, 36));
		l_rects.elementAt(0).add(new Vector<Rectangle>());
		l_rects.elementAt(0).elementAt(3).add(new Rectangle(1515, 947, 106, 36));
		l_rects.elementAt(0).elementAt(3).add(new Rectangle(1733, 947, 106, 36));
		l_rects.elementAt(0).elementAt(3).add(new Rectangle(1941, 947, 106, 36));
		l_rects.elementAt(0).elementAt(3).add(new Rectangle(2157, 947, 106, 36));
		l_rects.elementAt(0).add(new Vector<Rectangle>());
		l_rects.elementAt(0).elementAt(4).add(new Rectangle(1515, 1045, 106, 36));
		l_rects.elementAt(0).elementAt(4).add(new Rectangle(1733, 1045, 106, 36));
		l_rects.elementAt(0).elementAt(4).add(new Rectangle(1941, 1045, 106, 36));
		l_rects.elementAt(0).elementAt(4).add(new Rectangle(2157, 1045, 106, 36));
		//Contest 1
		l_rects.add(new Vector<Vector<Rectangle>>());
		l_rects.elementAt(1).add(new Vector<Rectangle>());
		l_rects.elementAt(1).elementAt(0).add(new Rectangle(1737, 1597, 106, 36));
		l_rects.elementAt(1).elementAt(0).add(new Rectangle(1945, 1597, 106, 36));
		l_rects.elementAt(1).elementAt(0).add(new Rectangle(2160, 1597, 106, 36));
		l_rects.elementAt(1).add(new Vector<Rectangle>());
		l_rects.elementAt(1).elementAt(1).add(new Rectangle(1737, 1697, 106, 36));
		l_rects.elementAt(1).elementAt(1).add(new Rectangle(1945, 1697, 106, 36));
		l_rects.elementAt(1).elementAt(1).add(new Rectangle(2160, 1697, 106, 36));
		l_rects.elementAt(1).add(new Vector<Rectangle>());
		l_rects.elementAt(1).elementAt(2).add(new Rectangle(1737, 1797, 106, 36));
		l_rects.elementAt(1).elementAt(2).add(new Rectangle(1945, 1797, 106, 36));
		l_rects.elementAt(1).elementAt(2).add(new Rectangle(2160, 1797, 106, 36));
		l_rects.elementAt(1).add(new Vector<Rectangle>());
		l_rects.elementAt(1).elementAt(3).add(new Rectangle(1737, 1897, 106, 36));
		l_rects.elementAt(1).elementAt(3).add(new Rectangle(1945, 1897, 106, 36));
		l_rects.elementAt(1).elementAt(3).add(new Rectangle(2160, 1897, 106, 36));
		//Contest 2
		l_rects.add(new Vector<Vector<Rectangle>>());
		l_rects.elementAt(2).add(new Vector<Rectangle>());
		l_rects.elementAt(2).elementAt(0).add(new Rectangle(1437, 2608, 106, 36));
		l_rects.elementAt(2).add(new Vector<Rectangle>());
		l_rects.elementAt(2).elementAt(1).add(new Rectangle(1437, 2708, 106, 36));
		l_rects.elementAt(2).add(new Vector<Rectangle>());
		l_rects.elementAt(2).elementAt(2).add(new Rectangle(1437, 2808, 106, 36));
		l_rects.elementAt(2).add(new Vector<Rectangle>());
		l_rects.elementAt(2).elementAt(3).add(new Rectangle(1437, 2908, 106, 36));
		l_rects.elementAt(2).add(new Vector<Rectangle>());
		l_rects.elementAt(2).elementAt(4).add(new Rectangle(1437, 3008, 106, 36));
		//Contest 3
		l_rects.add(new Vector<Vector<Rectangle>>());
		l_rects.elementAt(3).add(new Vector<Rectangle>());
		l_rects.elementAt(3).elementAt(0).add(new Rectangle(2170, 2810, 106, 36));
		l_rects.elementAt(3).add(new Vector<Rectangle>());
		l_rects.elementAt(3).elementAt(1).add(new Rectangle(2170, 2910, 106, 36));
		
		
		Vector<Vector<Integer>> l_contestantIds = new Vector<Vector<Integer>>();
		l_contestantIds.add(new Vector<Integer>());
		l_contestantIds.get(0).add(0);
		l_contestantIds.get(0).add(1);
		l_contestantIds.get(0).add(2);
		l_contestantIds.get(0).add(3);
		l_contestantIds.get(0).add(4);
		l_contestantIds.add(new Vector<Integer>());
		l_contestantIds.get(1).add(0);
		l_contestantIds.get(1).add(1);
		l_contestantIds.get(1).add(2);
		l_contestantIds.get(1).add(3);
		l_contestantIds.add(new Vector<Integer>());
		l_contestantIds.get(2).add(0);
		l_contestantIds.get(2).add(1);
		l_contestantIds.get(2).add(2);
		l_contestantIds.get(2).add(3);
		l_contestantIds.get(2).add(4);
		l_contestantIds.add(new Vector<Integer>());
		l_contestantIds.get(3).add(0);
		l_contestantIds.get(3).add(1);		
		
		BallotStyle l_style = new BallotStyle(0, l_contests, l_rects, true);
		l_style.setContestantIds(l_contestantIds);
		BallotStyle l_styles[] = new BallotStyle[1];
		l_styles[0] = l_style;
		
		Vector<Contest> l_c = new Vector<Contest>();
		Contest l_x = new Contest();
		l_x.setId(0);
		l_x.setContestName("Favorite Tree / �rbol Favorito");
		Vector<Contestant> l_can = new Vector<Contestant>();
		l_can.add(new Contestant(0, "Cherry / el cerezo"));
		l_can.add(new Contestant(1, "Elm / el olmo"));
		l_can.add(new Contestant(2, "Maple / el arce"));
		l_can.add(new Contestant(3, "Oak / el roble"));
		l_can.add(new Contestant(4, "Write-In / o por escrito"));
		l_x.setContestants(l_can);
		l_x.setMethod(new InstantRunoffTally());
		l_c.add(l_x);

		l_x = new Contest();
		l_x.setId(1);
		l_x.setContestName("Favorite Forest Animal / Animal Arbolado Favorito");
		l_can = new Vector<Contestant>();
		l_can.add(new Contestant(0, "Owl / B�ho"));
		l_can.add(new Contestant(1, "Rabbit / Conejo"));
		l_can.add(new Contestant(2, "Squirrel / Ardilla"));
		l_can.add(new Contestant(3, "Write-In / o por escrito"));
		l_x.setContestants(l_can);
		l_x.setMethod(new InstantRunoffTally());
		l_c.add(l_x);		

		l_x = new Contest();
		l_x.setId(2);
		l_x.setContestName("How many trees are on your property?" 
				+ " / �Cuanto �rboles estan en su propiedad?");
		l_can = new Vector<Contestant>();
		l_can.add(new Contestant(0, "0"));
		l_can.add(new Contestant(1, "1-2"));
		l_can.add(new Contestant(2, "2-5"));
		l_can.add(new Contestant(3, "More than 10 / M�s de 10"));
		l_x.setContestants(l_can);
		l_x.setMethod(new PluralityTally());
		l_c.add(l_x);		
		
		l_x = new Contest();
		l_x.setId(3);
		l_x.setContestName("Do you use less paper products than you did ten "
						+ "years ago? / �Utiliza menos producto de papel que" +
						" hace 10 a�os?");
		l_can = new Vector<Contestant>();
		l_can.add(new Contestant(0, "Yes / Si"));
		l_can.add(new Contestant(1, "No / No"));
		l_x.setContestants(l_can);
		l_x.setMethod(new PluralityTally());
		l_c.add(l_x);		
		
		Vector<String> l_locs = new Vector<String>();
		l_locs.add("/home/");
		l_locs.add("/media/*/");
		
		Vector<BallotStyle> l_s = new Vector<BallotStyle>();
		l_s.add(l_style);
		
		Vector<String> l_j = new Vector<String>();
		l_j.add("Jessie Carpenter");
		l_config.setChiefJudges(l_j);
		l_config.setLocation("Takoma Park Community Center Azalea Room");
		l_config.setName("Arbor Day Mock Election at Takoma Park");
		l_config.setOutputLocs(l_locs);
		l_config.setPollID(10);
		l_config.setReader(l_reader);
		l_config.setContests(l_c);
		
		l_config.setDate("April 11, 2009");
		l_config.setTime("10:00 am");
		
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
