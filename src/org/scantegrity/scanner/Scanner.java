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

import org.scantegrity.common.DirectoryWatcher;
import org.scantegrity.common.ImageLoader;
import org.scantegrity.common.gui.Dialogs;
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
	
	public Scanner(PollingPlaceGUI p_guiRef)
	{
		c_guiRef = p_guiRef; 
	}
	
	public void startElection()
	{
		String c_srcDir = "/mnt/scantegritytmpfs/images";
		String c_destDir = "/mnt/scantegritytmpfs/backup";
		
		//Create Ballot Handler
		BallotImageHandler l_bih = new BallotImageHandler(c_guiRef);
		
		//initialize the ImageLoader
		ImageLoader l_il = new ImageLoader(l_bih);
		
		//initialize and start the directory watcher thread
		Runnable l_dirWatch = new DirectoryWatcher(c_srcDir, c_destDir, l_il);
		Thread l_dirWatchThread = new Thread(l_dirWatch);
		l_dirWatchThread.start();
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		//Get the config file
		//Things here will come from config
		
		Runnable l_gui = new PollingPlaceGUI();
		
		try
		{
			l_gui = new PollingPlaceGUI();
		}
		catch(HeadlessException headEx)
		{
			//eventually send this to dialogs?
			Dialogs.displayErrorDialog("Headless Exception in Scanneer when creating PollingPlaceGUI.");
		}
		
		
		//start gui thread
		Thread l_guiThread = new Thread(l_gui); 
		l_guiThread.start();

	}
}
