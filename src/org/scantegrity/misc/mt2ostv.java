/*
 * @(#)BallotStore.java.java
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
package org.scantegrity.misc;

import java.beans.XMLDecoder;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Vector;

import org.apache.commons.cli.*;
import org.scantegrity.lib.Contest;
import org.scantegrity.scanner.ScannerConfig;

/**
 * Converts MeetingThreeOut.xml (results) files to the OSTV format. Requires
 * ContestInformation.xml in order to get names. 
 * 
 * @author Richard Carback
 *
 */
public class mt2ostv 
{
	private static Options c_opts = null;	
	
	public static void main(String args[])
	{
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
	    
	    //Looks like we have valid arguments, try to read M3
		
		
		//Get contest information, if possible.
	    Vector<Contest> l_c;
	    if (l_cmdLine.hasOption("contestinfo"))
	    {
	    	l_c = loadContest(l_cmdLine.getOptionValue("contestinfo"));
	    }
	    else
	    {
	    	//Load defaults
	    	
	    }
		
	    //Convert results to BLT format.
	    
		
	}
	
	/**
	 * Load a contest from file. 
	 * @param l_fname
	 */
	public static Vector<Contest> loadContest(String p_fname)
	{
		Vector<Contest> l_res;
		XMLDecoder e;
		try
		{
			e = new XMLDecoder(new BufferedInputStream(new FileInputStream(p_fname)));
			l_res = (Vector<Contest>)e.readObject();
			e.close();		
		}
		catch (FileNotFoundException e1)
		{
			e1.printStackTrace();
			l_res = null;
		}
		
		return l_res;
	}

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
		
		Option l_help = new Option("help", "Print this message");
		
		c_opts.addOption(l_contestInfo);	
		c_opts.addOption(l_help);	
		
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
	
}