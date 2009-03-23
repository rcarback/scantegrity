/*
 * @(#)ScantegrityBallotReader.java.java
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

/**
 * Reads scantegrity ballots.
 * 
 * @author Richard Carback
 *
 */
public class ScantegrityBallotReader extends BallotReader
{

	/**
	 * 
	 */
	public ScantegrityBallotReader()
	{
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see org.scantegrity.scanner.BallotReader#scanBallot(org.scantegrity.scanner.SerialNumberReader, org.scantegrity.scanner.BallotStyle[], java.awt.image.BufferedImage)
	 */
	@Override
	public Ballot scanBallot(BallotStyle[] p_styles, 
								BufferedImage p_img)
	{
		super.normalizeImage(p_img);
		//Normalize the Ballot
		
		//Read in the Serial Number
		
		//Read in the Ballot Style
		
		//Select the right Ballot Style, which gives a list of contest data
		
		//Process each contest
		
		//Create a new Ballot object with the serial number, style, and contest
		//data, return that object.
		
		return new Ballot();
	}

}
