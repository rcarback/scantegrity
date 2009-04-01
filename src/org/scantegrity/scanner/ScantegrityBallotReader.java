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

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.Vector;

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
		//Nothing to do here...Scantegrity ballots aren't special!
	}

	/* (non-Javadoc)
	 * @see org.scantegrity.scanner.BallotReader#scanBallot(org.scantegrity.scanner.SerialNumberReader, org.scantegrity.scanner.BallotStyle[], java.awt.image.BufferedImage)
	 */
	@Override
	public Ballot scanBallot(BallotStyle[] p_styles, 
								BufferedImage p_img)
	{
		/*TODO: Need to return an "invalid" ballot instead of null, this ballot
		 * does it's best to scan everything but when it messes up sets the
		 * bad state on the ballot object so that the scanner can report it!
		 */
		
		Ballot l_res = new Ballot();
		AffineTransformOp l_alignmentOp = super.getAlignmentOp(p_img);
		
		//Read in the Serial Number
		try
		{
			l_res.setId(super.c_serial.getSerialNumber(p_img, l_alignmentOp));
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}		
		
		//Read in the Ballot Style, which is left most digit on 5 digit serial
		int l_styleID = l_res.getId()/10000;

		//Select the right Ballot Style, which gives a list of contest data		
		BallotStyle l_style = null;
		for (BallotStyle l_s : p_styles) {
			if (l_s.getId() == l_styleID) {
				l_style = l_s;
				break;
			}
		}
		
		if (l_style == null) return null;
		//Process each contest
		Vector<Vector<Vector<Rectangle>>> l_rects = l_style.getContestRects();
		Integer l_r[][][] = new Integer[l_rects.size()][][];
		BufferedImage l_tmp = null;
		for (int l_i = 0; l_i < l_rects.size(); l_i++) 
		{
			l_r[l_i] = new Integer[l_rects.elementAt(l_i).size()][];
			for (int l_j = 0; l_j < l_rects.elementAt(l_i).size(); l_j++) 
			{
				l_r[l_i][l_j] = new Integer[l_rects.elementAt(l_i).elementAt(l_j).size()];
				for (int l_k = 0; l_k < l_rects.elementAt(l_i).elementAt(l_j).size(); l_k++)
				{
					try
					{
						l_tmp = AffineCropper.crop(p_img, l_alignmentOp, 
								l_rects.elementAt(l_i).elementAt(l_j).elementAt(l_k));
						if (isMarked(l_tmp)) {
							l_r[l_i][l_j][l_k] = 1;
						} 
						else
						{
							l_r[l_i][l_j][l_k] = 0;
						}
							
					}
					catch (Exception e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
						l_r[l_i][l_j][l_k] = 0;
					}
				}
			}
		}
		
		//Create a new Ballot object with the serial number, style, and contest
		//data, return that object.
		TreeMap<Integer, Integer[][]> l_bData = new TreeMap<Integer, Integer[][]>();
		for (int l_i = 0; l_i < l_r.length; l_i++) {
			l_bData.put(l_style.getContests().get(l_i), l_r[l_i]);
		}
		l_res.setBallotData(l_bData);
		l_res.setCounted(l_style.isCounted());
		l_res.setBallotStyleID(l_style.getId());
		
		return l_res;
	}
	
	private boolean isMarked(BufferedImage p_img)
	{
		//If more than 30% black, return true
		int l_total = p_img.getHeight()*p_img.getWidth();
		int l_det = 0;
		for (int l_i = 0; l_i < p_img.getWidth(); l_i++)
		{
			for (int l_j = 0; l_j < p_img.getHeight(); l_j++) {
				if (DetectBlack.isBlack(l_i, l_j, p_img)) l_det++;
			}
		}
		
		double l_res = (double)l_det/(double)l_total;
		
		//System.out.println("% Black: " + l_res);
		
		if (l_res > .3) return true;
		return false;
	}

}
