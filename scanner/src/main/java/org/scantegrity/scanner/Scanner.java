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

import java.awt.image.BufferedImage;
import java.beans.XMLDecoder;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.GregorianCalendar;

import org.scantegrity.common.Ballot;
import org.scantegrity.common.Logging;
import org.scantegrity.common.SysBeep;
import org.scantegrity.common.FindFile;
import org.apache.commons.cli.CommandLine;
import org.scantegrity.ostv.CommandLineParser;
import org.scantegrity.ostv.HelpFormatter;
import org.scantegrity.ostv.Option;
import org.scantegrity.ostv.Options;
import org.scantegrity.ostv.ParseException;
import org.scantegrity.ostv.PosixParser;
import org.scantegrity.scanner.ScannerConfig;
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
	
	/**
	 * Create options for this application. Currently there is only 1, and 
	 * that is if the user wants to include a contest information file.
	 */
	public static void setOptions()
	{
		c_opts = new Options();
		
		Option l_contestInfo = OptionBuilder.withArgName( "contestinfo" )
						.hasArg()
						.withDescription("Use a file that contains contest information.")
						.create("info");
		
		Option l_help = new Option("help", "Print help message.");
		Option l_verb = new Option("v", "Unimplemented verbosity setting, prints more info.");
		
		c_opts.addOption(l_contestInfo);	
		c_opts.addOption(l_help);	
		c_opts.addOption(l_verb);	
		
	}
	
	/**
	 * Prints the usage information for the application. 
	 */
	public static void printUsage()
	{
		try {			
			HelpFormatter l_form  = new HelpFormatter();
			l_form.printHelp(80, "mt2ostv [OPTIONS] INFILE [OUTFILE]", 
					"mt2ostv converts results files in Scantegrity to the BLT format " +
					"used by the OpenSTV counting program. It uses the Scantegrity" +
					" results INFILE, usually named MeetingThreeOut.xml to produce" +
					" a BLT compatible file to stdout or OUTFILE.\n\nOPTIONS:", c_opts, "", false);
		} 
		catch (Exception l_e)
		{
			l_e.printStackTrace();
			System.exit(-1);
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
				System.err.println("Could not open file ");
			}
		}
		catch(NullPointerException e_npe)
		{
			c_loc = new FindFile(ScannerConstants.DEFAULT_CONFIG_NAME).find();
			System.err.println("Could not open file ");
		}
		
		//TODO: make sure the file is found and is readable
		if(c_loc == null)
		{
			//TODO: Log 
			Thread l_th = new Thread(new SysBeep(10, 500));
			l_th.start();
			
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				//e1.printStackTrace();
			} 
			
			//TODO: Shutdown, but for now just exit
			System.exit(-1);
		}
		
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
	 * This method sets up the logging for the scanner
	 */
	public static Logging initializeLogger(org.scantegrity.scanner.ScannerConfig p_config)
	{
		String l_logName = p_config.getLogName(); 
		
		if(l_logName == null)
		{
			//TODO: add the date to the log file name
			l_logName = "ScantegrityScannerLog-" + p_config.getScannerID() + ".xml";
		}
		
		Logging l_log = new Logging(p_config.getLogName(), p_config.getLogLevel());
		
		return l_log;
	}
	
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
		setOptions();
		
		String l_args[] = null;
		CommandLine l_cmdLine = null;
		try {
			CommandLineParser l_parser = new PosixParser();
		    l_cmdLine = l_parser.parse(c_opts, args);
		    l_args = l_cmdLine.getArgs();
		}
		catch( ParseException l_e ) {
			l_e.printStackTrace();
		    return;
		}
		
	    if (l_cmdLine == null || l_cmdLine.hasOption("help") || l_args == null 
	    		|| l_args.length < 1 || l_args.length > 2)
	    {
	    	printUsage();
	    	return;
	    }
	    
		//Get the config file
		ScannerConfig l_config = getConfiguration(null);
		
		//register logging handlers if any
		Logging l_log = initializeLogger(l_config); 
		
		//check hardware devices
		
		//grab all mounts points, log uuid, and setup scantegrity file struct
		
		//init ballot storage
		
		//authentication ??????????
		
		
		//register devices if any
		ScannerInterface l_scanner = new ScannerInterface(l_log); 
		
		//start the election
		BallotHandler l_ballotHandlerRef = new BallotHandler(l_log, c_errDir, l_config);
		
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
