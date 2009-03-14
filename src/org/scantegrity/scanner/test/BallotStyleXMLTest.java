/*
 * @(#)BallotStyleXMLTest.java.java
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

import java.awt.Rectangle;
import java.beans.XMLEncoder;
import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Vector;
import java.util.List;

import org.scantegrity.scanner.BallotStyle;

import javax.swing.JButton;

/**
 * Tests XML Serialization of Ballot Style.
 * @author Rick Carback
 *
 */
public class BallotStyleXMLTest
{

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		try
		{
			XMLEncoder e = new XMLEncoder(new BufferedOutputStream(new FileOutputStream("testing/scanner/dummystyle.xml")));
			e.writeObject(new JButton("Hello, world"));
			e.close();
			
			e = new XMLEncoder(new BufferedOutputStream(new FileOutputStream("testing/scanner/singlestyle.xml")));
			Rectangle x = new Rectangle(10, 15, 100, 150);
			List<Rectangle> xlist = new Vector<Rectangle>(); 
			xlist.add(x);
			List<Integer> c = new Vector<Integer>();
			c.add(456);
			BallotStyle tmp = new BallotStyle(123, c, xlist, true); 
			e.writeObject(tmp);
			e.close();

			BallotStyle[] tmp2 = new BallotStyle[4];
			tmp2[0] = tmp;
			c.add(789);
			x = new Rectangle(11, 15, 101, 150);
			xlist.add(x);
			tmp2[1] = new BallotStyle(124, c, xlist, true);
			c.add(32);
			x = new Rectangle(11, 15, 101, 150);
			xlist.add(x);
			tmp2[2] = new BallotStyle(125, c, xlist, true);			
			c.add(42);
			x = new Rectangle(11, 15, 101, 150);
			xlist.add(x);
			tmp2[3] = new BallotStyle(124, c, xlist, true);
			e = new XMLEncoder(new BufferedOutputStream(new FileOutputStream("testing/scanner/multistyle.xml")));
			e.writeObject(tmp2);
			e.close();
		}
		catch (FileNotFoundException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	
	}

}
