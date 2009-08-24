/*
 * @(#)Scanner.java.java
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

import java.awt.HeadlessException;
import java.awt.image.BufferedImage;
import java.beans.XMLDecoder;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;


import org.scantegrity.common.DirectoryWatcher;
import org.scantegrity.common.ImageLoader;
import org.scantegrity.common.gui.Dialogs;
import org.scantegrity.common.FindFile;
import org.scantegrity.scanner.ScannerConfig;
import org.scantegrity.scanner.gui.PollingPlaceGUI;

import uk.org.jsane.JSane_Exceptions.JSane_Exception;

/**
 * @author John Conway
 *
 * This class is the main wrapper for the scanner. Running
 * Scanners main will load all necessary classes to run the 
 * scanner on election day. 
 */
public class Scanner
{	
	private static final String c_errDir = "~/error/"; 
	public static void endElection()
	{
		try
		{
			Runtime.getRuntime().exec("shutdown -h now").waitFor();
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static ScannerConfig getConfiguration(String p_configPath)
	{
		ScannerConfig l_config = new ScannerConfig();
	
		File c_loc = null; 
		
		try
		{
			c_loc = new File(p_configPath);
			
			if(!c_loc.isFile())
			{
				c_loc = new FindFile(ScannerConstants.DEFAULT_CONFIG_NAME).find();
			}
		}
		catch(NullPointerException e_npe)
		{
			c_loc = new FindFile(ScannerConstants.DEFAULT_CONFIG_NAME).find();
			//TODO: This should be logged and printed to std.err
		}
		
		//TODO: make sure the file is found and is readable
		
		XMLDecoder e;
		try
		{
			e = new XMLDecoder(new BufferedInputStream(new FileInputStream(c_loc)));
			l_config = (ScannerConfig)e.readObject();
			e.close();	
		}
		catch(FileNotFoundException e_fnf)
		{
			System.err.println("Could not open Configuration File. File not Found!");
		}
		
		return l_config;
	}
	
	/**
	 * This is the main to start the entire Scanner. Main sets up the GUI, grabs the config
	 * and runs the GUI. 
	 * 
	 * Flags: 
	 * 	
	 * 
	 * @param args CLI flags
	 */
	public static void main(String[] args)
	{
		//process command line arguments
		
		//Get the config file
		ScannerConfig l_config = getConfiguration(null);
		
		//init audit log
		
		//check hardware devices
		//init ballot storage
		
		//authentication ??????????
		
		//register logging handlers if any
		
		//register devices if any
		ScannerInterface l_scanner = new ScannerInterface(); 
		
		//start the election
		BallotHandler l_ballotHandlerRef = new BallotHandler(c_errDir, l_config);
		
		//main loop
		//TODO: terminating condition, button, or special ballot???
		while(true)
		{
			BufferedImage l_ballotImg = null;
			Ballot l_ballot = null;
			
			//get a ballot image
			try
			{
				l_ballotImg = l_scanner.getImageFromScanner();
			}
			catch (JSane_Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}  
			
			//process
			l_ballot = l_ballotHandlerRef.handleImage(l_ballotImg);
			
			//cast or reject
			l_ballotHandlerRef.castBallot(l_ballot);
			
			//resume scanning
		}
		
		//end election (ballot handler)
		
		//turn off storage
		
		//disconnect devices 
		
		//turn off audit log 
		
		//quit
	}
}
