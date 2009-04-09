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
import java.beans.XMLDecoder;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.scantegrity.common.DirectoryWatcher;
import org.scantegrity.common.ImageLoader;
import org.scantegrity.common.gui.Dialogs;
import org.scantegrity.lib.FindFile;
import org.scantegrity.scanner.ScannerConfig;
import org.scantegrity.scanner.gui.PollingPlaceGUI;

/**
 * @author John Conway
 *
 * This class is the main wrapper for the scanner. Running
 * Scanners main will load all necessary classes to run the 
 * scanner on election day. 
 */
public class Scanner
{	
	private PollingPlaceGUI c_guiRef;
	private static final String c_name = "ScannerConfig.xml";
	private static final String c_srcDir = "/mnt/scantegritytmpfs/images";
	private static final String c_destDir = "/mnt/scantegritytmpfs/backup";
	private static final String c_errDir = "/mnt/scantegritytmpfs/error";
	
	public Scanner(PollingPlaceGUI p_guiRef)
	{
		c_guiRef = p_guiRef; 
	}
	
	public void startElection(ScannerConfig p_config)
	{
		//Create Ballot Handler
		BallotHandler l_bih = new BallotHandler(c_guiRef, c_errDir, p_config);
		
		//initialize the ImageLoader
		ImageLoader l_il = new ImageLoader(l_bih);
		
		//initialize and start the directory watcher thread
		Runnable l_dirWatch = new DirectoryWatcher(c_srcDir, c_destDir, l_il);
		Thread l_dirWatchThread = new Thread(l_dirWatch);
		l_dirWatchThread.start();
	}
	
	public void endElection()
	{
		System.exit(0);
	}
	
	public static ScannerConfig getConfigurationFile()
	{
		ScannerConfig l_config = new ScannerConfig();
		
		File c_loc = new FindFile(c_name).find();
		
		XMLDecoder e;
		try
		{
			e = new XMLDecoder(new BufferedInputStream(new FileInputStream(c_loc)));
			l_config = (ScannerConfig)e.readObject();
			e.close();	
		}
		catch(FileNotFoundException e_fnf)
		{
			Dialogs.displayErrorDialog("Could not open Configuration File. File not Found!");
		}
		
		return l_config;
	}
	
	/**
	 * This is the main to start the entire Scanner. Main sets up the GUI, grabs the config
	 * and runs the GUI. 
	 * 
	 * Flags: 
	 * 	1. -f --fullscreen
	 * 		sets the gui to fullscreen mode
	 * 
	 * @param args CLI flags
	 */
	public static void main(String[] args)
	{
		boolean l_fullscreen = false;
		
		for(int i = 0; i < args.length; i++)
		{
			String l_temp = args[i]; 
			if(l_temp.equals("-f") || l_temp.equals("--fullscreen"))
				l_fullscreen = true;
		}
 
		//Get the config file
		ScannerConfig l_config = getConfigurationFile();
		
		//no configuration, quit
		//TODO: if the filefinder doesnt work, ask for it before quitting
		if(l_config == null)
			return;
		
		//set up the gui
		Runnable l_gui = null;
		
		try
		{
			l_gui = new PollingPlaceGUI(l_config, l_fullscreen);
		}
		catch(HeadlessException headEx)
		{
			Dialogs.displayErrorDialog("Sorry! Problem with starting the Scanner.");
		}
		
		
		//start gui thread
		Thread l_guiThread = new Thread(l_gui); 
		l_guiThread.start();

	}
}
