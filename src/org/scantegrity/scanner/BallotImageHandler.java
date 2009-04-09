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
import java.util.Set;
import java.util.TreeMap;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;
//import javax.media.jai.RenderableGraphics;

import org.scantegrity.common.*;
import org.scantegrity.common.gui.Dialogs;
import org.scantegrity.lib.Ballot;
import org.scantegrity.lib.BallotStyle;
import org.scantegrity.lib.Contest;
import org.scantegrity.lib.methods.TallyMethod;
import org.scantegrity.scanner.gui.PollingPlaceGUI;
import org.scantegrity.util.DrunkDriver;

import com.sun.xml.internal.bind.v2.runtime.reflect.ListIterator;

/**
 * @author John Conway
 *
 */
public class BallotImageHandler implements ImageHandler
{
	private BallotReader c_reader;
	private TallyMethod c_tm; 
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
	}
	
	/* (non-Javadoc)
	 * @see org.scantegrity.common.ImageHandler#handleImage(java.awt.image.BufferedImage)
	 */
	public void handleImage(BufferedImage p_image)
	{
		
		
		//Start running the tests to confirm the ballot is valid
		
		//long l_start = System.currentTimeMillis(); 
		try 
		{
			//Give me your keys
			if(DrunkDriver.isDrunk(p_image, 10))
				return;
		
			Ballot l_b = c_reader.scanBallot(c_styles, p_image);
			
			//couldn't find alignment marks or couldnt read serial number 
			if(l_b == null || l_b.getId() == null)
			{
				//c_guiRef.setToWaiting();
				c_results = "Unable to Scan Ballot. Please Try Again";
				Dialogs.displayInfoDialog(c_results, "Unable to Read Ballot! \n Please try scanning again.");
				
				//Move file to destination directory
				ImageIO.write(p_image, "tiff", new File(c_errDir + "/error" + i + ".tiff"));
				i++;
				
				return;
			}
			
			
			//Validate the Results
			
			Vector<Contest> l_cv = c_config.getContests();
			
			c_results = "";
			
			for (Contest l_c: l_cv)
			{ 				
				c_tm = l_c.getMethod();
				int l_id = l_c.getId();  
				
				c_results += "Contest: " + (l_id + 1) + "\n";
				
				TreeMap<String, String> l_tm = c_tm.validateContest(l_id, l_b);
				
				Set<String> l_set = l_tm.keySet();
				
				for(String s: l_set)
				{
					c_results += "\t" + s + " : " + l_tm.get(s) + "\n";
				}
				
				c_results += "\n";
			}
		} 
		catch (Exception l_e) 
		{
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
