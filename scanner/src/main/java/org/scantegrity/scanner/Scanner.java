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
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Vector;
import java.util.logging.Level;

import javax.imageio.ImageIO;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.io.FileUtils;
import org.scantegrity.common.Ballot;
import org.scantegrity.common.BallotStyle;
import org.scantegrity.common.DrunkDriver;
import org.scantegrity.common.FindFile;
import org.scantegrity.common.Logging;
import org.scantegrity.common.RandomBallotStore;
import org.scantegrity.common.SysBeep;

import com.google.zxing.ReaderException;

import uk.org.jsane.JSane_Exceptions.JSane_Exception;
import uk.org.jsane.JSane_Exceptions.JSane_Exception_IoError;

/**
 * @author John Conway
 *
 * This class is the main wrapper for the scanner. Running
 * Scanners main will load all necessary classes to run the 
 * scanner on election day. 
 */
public class Scanner
{	
	private static String c_errDir = "error/"; 
	private static Options c_opts;
	private static ScannerConfig c_config; 
	private static Logging c_log; 
	private static ScannerController c_scanner; 
	private static RandomBallotStore[] c_store;
	private static Vector<Integer> c_ballotIds; 
	private static Vector<String> c_outDirs;
	private static int c_numErrorFiles = 0; 
	private static int c_myId = -1;
	private static int c_count = 0;
	private static MessageDigest c_hash;
	private static SecureRandom c_csprng; 

	/**
	 * Main Logic loop.
	 * 
	 * @param args CLI flags
	 */
	public static void main(String[] args)
	{		
		processCmdLine(args);		
		
		//Create the locations we should output files too
		createOutputDirectories();
		
		//register logging handlers if any
		c_myId = c_config.getPollID();
		c_log = new Logging(c_outDirs, c_myId, Level.FINEST); 
		c_log.log(Level.INFO, "Logging Intialized");
    	c_log.log(Level.INFO, "Scanner ID Number: " + c_myId);		
    	
		c_log.log(Level.INFO, "Initializing Cryptographic hash and rng.");
		try {
			c_hash = MessageDigest.getInstance("SHA1");
		} catch (NoSuchAlgorithmException l_e) {
			l_e.printStackTrace();
			c_hash = null;
			c_log.log(Level.SEVERE, "Unable to initialize hash. Reason: " + l_e.getMessage());
		}
		try {
			c_csprng = SecureRandom.getInstance("SHA1PRNG");
		} catch (NoSuchAlgorithmException l_e) {
			l_e.printStackTrace();
			c_csprng = null;
			c_log.log(Level.SEVERE, "Unable to initialize RNG. Reason: " + l_e.getMessage());
		}		

		//init ballot storage
		c_ballotIds = new Vector<Integer>();
		//TODO: Change this size to be variable...
		c_store = initializeBallotStore(c_outDirs, 10*1024*1024); 
		
		//start the election
		c_log.log(Level.SEVERE, "Election Started");

		//main loop
		//TODO: terminating condition, button, or special ballot???
		while(true)
		{
			BufferedImage l_ballotImg[] = null;
			Ballot l_ballot = null;
			
			//process image into ballot
			l_ballotImg = getBallotImages();
			
			
			if(l_ballotImg == null 
					|| (l_ballotImg[0] == null && l_ballotImg[1] == null))
				continue;
			
			for (int l_c = 0; l_c < l_ballotImg.length; l_c++)
			{
				//Ignore empties
				if (l_ballotImg[l_c] == null)
				{
					c_log.log(Level.WARNING, "Only 1 ballot object returned." 
									+ " Make sure the scanner supports duplex");
					continue;
				}
				//Ignore blank pages
				if (DrunkDriver.isDrunk(l_ballotImg[l_c], 10))
					continue;
			
				l_ballot = getBallot(l_ballotImg[l_c]); 
				
				if(l_ballot == null)
					continue;
	
				c_count++;
				l_ballot.setScannerId(c_myId);
				
				if(isDuplicate(l_ballot))
				{
					c_log.log(Level.WARNING, "Duplicate Ballot detected. ID : " 
									+ l_ballot.getId());
					l_ballot.setCounted(false); 
					l_ballot.addNote("Duplicate Ballot");	
				}
				
				//check if the ballot is a "starting ballot"
				
				//check if the ballot is a "closing ballot"
				
				//else
				saveBallot(l_ballot);
			}
			//resume scanning
		}
		
		//end election (ballot handler)
		
		//turn off storage
		
		//disconnect devices 
		
		//turn off log 
		
		//quit
		//endElection(); 
	}

	/**
	 * Process command line arguments, filling in any configuration settings 
	 * provided by the calling process.
	 * @param args - CLI arguments.
	 */
	private static void processCmdLine(String args[])
	{	
		setOptions();

		String l_args[] = null;
		CommandLine l_cmdLine = null;
		try {
			CommandLineParser l_parser = new PosixParser();
		    l_cmdLine = l_parser.parse(c_opts, args);
		    l_args = l_cmdLine.getArgs();
		}
		catch( ParseException l_e ) 
		{
			l_e.printStackTrace();
			System.exit(-1);
		}
		
		//Invalid command line options.
	    if (l_cmdLine == null || l_cmdLine.hasOption("help") || l_args == null)
	    {
	    	printUsage();
	    	System.exit(0);
	    }		

	    //Custom configuration file.
	    if (l_cmdLine.hasOption("config"))
	    {
		    c_config = getConfiguration(l_cmdLine.getOptionValue("config"));
	    }
	    else c_config = getConfiguration("ScannerConfig.xml");
	    
	    
	    //ScannerController input/output locations.
	    String l_bin, l_in, l_out;
	    if (l_cmdLine.hasOption("in")) l_in = l_cmdLine.getOptionValue("in");
	    else l_in = null;
	    if (l_cmdLine.hasOption("out")) l_out = l_cmdLine.getOptionValue("out");
	    else l_out = null;
	    if(l_cmdLine.hasOption("bin")) l_bin = l_cmdLine.getOptionValue("bin");
	    else l_bin = null;
	    //The default error directory should be a subdirectory of the output
	    //directory.
	    if (l_out != null)
	    {
	    	c_errDir = l_out + File.separator + c_errDir;
	    }
		c_scanner = new ScannerController(c_log, l_bin, l_in, l_out, true);
		
		//Create error directory if it does not exist.
		try
		{
			File l_e = new File(c_errDir);
			if (!l_e.exists())
			{
				FileUtils.forceMkdir(l_e);
			}
			else if (!l_e.isDirectory())
			{
				FileUtils.forceMkdir(l_e);
			}
		}
		catch (Exception l_e)
		{
			//Try to save errors to local directory.
			c_errDir = "";
		}
	}
	
	/**
	 * Create options for this application. Currently there is only 1, and 
	 * that is if the user wants to include a contest information file.
	 */
	private static void setOptions()
	{
		c_opts = new Options();
		
		Option l_help = new Option("help", "Print help message.");
		Option l_verb = new Option("v", "Unimplemented verbosity setting, prints more info.");
		Option l_in = new Option("in", "Input directory. This should be a "
									+ " ramdisk mountpoint.");
		Option l_out = new Option("out", "Output directory. Output images will "
									+ "be stored here. NOTE: This is not an "
									+ "override option for the scanner config."
									+ " This option backs up ballot images, "
									+ "which is typically not necessary.");
		Option l_bin = new Option("bin", "Binaries directory. Where the " 
									+ "scanner scripts are stored.");
		Option l_config = new Option("config", "Configuration file path and name.");
		
		c_opts.addOption(l_help);	
		c_opts.addOption(l_verb);	
		c_opts.addOption(l_in);	
		c_opts.addOption(l_out);	
		c_opts.addOption(l_bin);
		c_opts.addOption(l_config);	
	}
	
	/**
	 * Prints the usage information for the application. 
	 */
	private static void printUsage()
	{
		try {			
			HelpFormatter l_form  = new HelpFormatter();
			l_form.printHelp(80, "scanner <OPTIONS>", 
					"This is the scanner daemon for the scantegrity voting" +
					"system. If you do not provide a configuration, the system" +
					"will attempt to find one.\n"
					+ "\nOPTIONS:", c_opts, "", false);
		} 
		catch (Exception l_e)
		{
			l_e.printStackTrace();
			System.exit(-1);
		}
	}	
	
	/**
	 * Get and set up the configuration file data.
	 * 
	 * This file configures the election.
	 * 
	 * @param p_configPath - The path. 
	 * @return
	 */
	private static ScannerConfig getConfiguration(String p_configPath)
	{
		ScannerConfig l_config = new ScannerConfig();
	
		File c_loc = null; 
		
		try
		{
			if(p_configPath == null)
			{
				c_loc = new FindFile(ScannerConstants.DEFAULT_CONFIG_NAME).find();
			}
			else
			{
				c_loc = new File(p_configPath);
			}
			
			if(!c_loc.isFile())
			{
				c_loc = new FindFile(ScannerConstants.DEFAULT_CONFIG_NAME).find();
				System.err.println("Could not open file.");
			}
		}
		catch(NullPointerException e_npe)
		{
			System.err.println("Could not open file. File does not exist.");
			e_npe.printStackTrace();
			criticalExit(5);
		}
		
		//TODO: make sure the file is found and is readable
		if(c_loc == null)
		{
			System.err.println("Critical Error: Could not open configuration "
								+ "file. System Exiting.");
			criticalExit(10);
		}
		
		XMLDecoder e;
		try
		{
			e = new XMLDecoder(new BufferedInputStream(new FileInputStream(c_loc)));
			l_config = (ScannerConfig)e.readObject();
			e.close();	
		}
		catch(Exception e_e)
		{
			System.err.println("Could not parse Configuration File!");
			e_e.printStackTrace();
			criticalExit(20);
		}
		
		return l_config;
	}	
	
	/**
	 * Reads all the output directories in the configuration, creates a list
	 * of every directory, and sets up any output directories if needed.
	 * 
	 * @return
	 */
	private static void createOutputDirectories() {
		Vector<String> l_locs = c_config.getOutputDirNames();
		c_outDirs = new Vector<String>();
		
		//WE want to avoid failures at all costs in this function.
		if (l_locs == null)
		{
			System.err.println("Invalid output directory option! Using" +
					" default /media.");
			l_locs = new Vector<String>();
			l_locs.add("/media");
		}
		//Go through each directory name.
		for (String l_loc : l_locs)
		{
			if (l_loc == null) continue;
			File l_d = new File(l_loc);
			int l_c = 0;
			try 
			{				
				if (l_d.exists() && 
						l_d.canExecute() && l_d.canRead() && l_d.isDirectory())
				{
					//Get a directory listing.
					File[] l_subds = l_d.listFiles();
					for (File l_subd : l_subds)
					{
						try
						{
							if (l_subd.canExecute() && l_subd.canRead() 
									&& l_subd.canWrite() && l_subd.isDirectory())
							{
								if (!(new File(l_subd.getAbsolutePath() + File.separator
										+ "scantegrity-scanner").exists()))
								{
									FileUtils.forceMkdir(new File(l_subd.getAbsolutePath() + File.separator 
											+ "scantegrity-scanner"));									
								}
								c_outDirs.add(l_subd.getAbsolutePath() + File.separator 
												+ "scantegrity-scanner");
								l_c++;
							}
						}
						catch (Exception l_e)
						{
							l_e.printStackTrace();
							//ignore.
						}
					}
					if (l_c == 0 && l_d.canWrite())
					{
						if (!(new File(l_d.getAbsolutePath() + File.separator
								+ "scantegrity-scanner/").exists()))
						{
							FileUtils.forceMkdir(new File(l_d.getAbsolutePath() + File.separator
									+ "scantegrity-scanner"));							
						}
						c_outDirs.add(l_d.getAbsolutePath() + File.separator
								+ "scantegrity-scanner");
					}
					else if (l_c == 0)
					{
						System.err.println("Permissions error: could not use " 
								+ l_d.getAbsolutePath());	
					}
				}
			}
			catch (Exception l_e)
			{
				System.err.println("Could not read " 
							+ l_d.getAbsolutePath() + "\n Reason:" 
							+ l_e.getMessage());
			}
		}
		//If all else fails..
		if (c_outDirs.size() <= 0)
		{
			System.err.println("Unable to use an output directory!");
			try 
			{
				if (!(new File("scantegrity-scanner").exists()))
				{
					FileUtils.forceMkdir(new File("scantegrity-scanner"));
				}
				c_outDirs.add("scantegrity-scanner");
			}
			catch (Exception l_e)
			{
				System.err.println("Exiting.. could not create an output directory!");
				criticalExit(15);
			}
		}		
	}
	
	private static RandomBallotStore[] initializeBallotStore(Vector<String> p_storeLocs, int p_size)
	{
		RandomBallotStore[] l_store = null;
		
		try
		{	
			c_log.log(Level.INFO, "Initializing the Random Ballot Stores");
			l_store = new RandomBallotStore[p_storeLocs.size()];
			int l_ret = -1;
			for(int i = 0; i < p_storeLocs.size(); i++)
			{
				c_log.log(Level.INFO, "Creating Random Ballot Store : " + p_storeLocs.get(i));
				l_store[i] = new RandomBallotStore(c_myId, 
													p_size, 
													512,
													p_storeLocs.get(i) + File.separator + "ballots.sbr", 
													c_hash, 
													c_csprng);
				l_ret = l_store[i].initializeStore();
				c_count = Math.max(l_ret, c_count);				
				
				if(l_ret < 0)
				{
					c_log.log(Level.SEVERE, "Failed to open random ballot store " + p_storeLocs.get(i));
					System.err.println("Failed to open random ballot store " + p_storeLocs.get(i));
					System.err.println("This error may prevent you from storing ballots!");
				}
				else
				{
					c_log.log(Level.INFO, "Random Ballot Store Created.");
					if (i == 0)
					{
						c_ballotIds = l_store[0].getBallotIds();
						if (c_ballotIds.size() > 0)
						{
							c_log.log(Level.WARNING, "There are " + c_ballotIds.size()
										+ " ballots in the store!");
						}
					}

				}
			}
		}
		catch(Exception e_e)
		{
			//Security Failed, log and quit
			c_log.log(Level.SEVERE, "Critical Failure: Could initialize random number generator. System Exiting. ");
			e_e.printStackTrace(); 
			criticalExit(10);
		}
		
		if(l_store == null || l_store.length < 1)
		{
			//Security Failed, log and quit
			c_log.log(Level.SEVERE, "Critical Failure: Could initialize random number generator. System Exiting.");
			criticalExit(10);
		}
		
		return l_store; 
	}	

	
	private static BufferedImage[] getBallotImages()
	{
		BufferedImage l_ballotImgs[] = null; 
		
		//get a ballot image
		try
		{
			c_log.log(Level.FINE, "Getting ballot image from scanner");
			l_ballotImgs = c_scanner.getImagesFromScanner();
			
			if(l_ballotImgs == null)
			{
				c_log.log(Level.FINE, "Invalid image object returned.");  
			}
			
			return l_ballotImgs;
		}
		catch (Exception l_e)
		{
			c_log.log(Level.SEVERE, "Possibly Lost Ballot:" + l_e.getMessage());			
		}

		return null;
	}
	
	private static Ballot getBallot(BufferedImage p_ballotImg) 
	{	
		c_log.log(Level.INFO, "Converting Image to Ballot.");
		
		Ballot l_b = null;
		BallotReader l_reader = c_config.getReader();
		Vector<BallotStyle> l_styles = c_config.getStyles();
		String l_err = "Unknown";
		try 
		{
			//scan the ballot
			l_b = l_reader.scanBallot(l_styles, p_ballotImg);
			
			//couldn't find alignment marks or couldn't read serial number 
			if(l_b != null && l_b.getId() != null)
			{	
				return l_b;
			}
		}
		catch (Exception l_e) 
		{
			l_err = l_e.getMessage();
		}
		
		c_log.log(Level.WARNING, "Could not read (possible) ballot image."
				+ "Reason: " + l_err);
		saveErrorImage(p_ballotImg);
		return null; 
	}
	
	private static Boolean isDuplicate(Ballot p_ballot) 
	{
		if(!c_ballotIds.contains(p_ballot.getId()))
		{
			c_ballotIds.add(p_ballot.getId());
			return false; 
		}
		
		return true; 
	}
	
	private static void saveErrorImage(BufferedImage p_ballotImg)
	{
		c_log.log(Level.SEVERE, "Bad Ballot. Saving to Error Directory.");
		
		//Copy the bad image to the error directory
		try 
		{
			ImageIO.write(p_ballotImg, "tiff", new File(c_errDir + File.separator 
														+ "scanerror" 
														+ c_numErrorFiles 
														+ ".tiff"));
			
			//increment bad image count
			c_numErrorFiles++;
			
			return; 
		} 
		catch (Exception e)
		{
			//do nothing...
		}
		
		c_log.log(Level.WARNING, "Could not save error ballot.");
	}
	
	private static void saveBallot(Ballot p_ballot)
	{
		//do some logging
		c_log.log(Level.INFO, "Saving Ballot to Random Ballot Store");
	
		for(RandomBallotStore l_store : c_store)
		{
			try 
			{
				c_log.log(Level.INFO, "Saving to ballot " + c_count + " store: " + l_store.getLocation());
				l_store.addBallot(p_ballot);
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
				c_log.log(Level.SEVERE, "I/O Error. Unable to save ballot to " + l_store.getLocation());
			}
		}
	}
	
	private static void endElection()
	{
		//add date
		c_log.log(Level.SEVERE, "Ending Election ");
		
		//get shutdown type
		
		//shutdown logging. 
		c_log = null; 
		
		try
		{
			//shutdown system
			System.exit(0);
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Beeps p_numBeeps times and then exits the system
	 * 
	 * @param p_numBeeps the number of beeps
	 */
	private static void criticalExit(int p_numBeeps)
	{ 
		Thread l_th = new Thread(new SysBeep(p_numBeeps, 500));
		l_th.start();
		
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e1) {
			//e1.printStackTrace();
		} 
		
		//TODO: Shutdown, but for now just exit
		System.exit(-1);
	}
	

}
