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
import java.beans.XMLEncoder;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Set;
import java.util.TreeMap;
import java.util.Vector;

import javax.imageio.ImageIO;

import org.apache.derby.iapi.services.io.NewByteArrayInputStream;
import org.scantegrity.common.*;
import org.scantegrity.common.gui.Dialogs;
import org.scantegrity.lib.Ballot;
import org.scantegrity.lib.BallotStyle;
import org.scantegrity.lib.Contest;
import org.scantegrity.lib.methods.ContestResult;
import org.scantegrity.lib.methods.TallyMethod;
import org.scantegrity.scanner.gui.PollingPlaceGUI;
import org.scantegrity.util.DrunkDriver;

/**
 * Implements ImageHandler as the callback for the directory watcher
 * As ballots are scanned, handleimage is called to process ballots. 
 * 
 * This class also handles the BallotStore class. 
 * 
 * @author John Conway
 *
 */
public class BallotHandler implements ImageHandler
{
	/* *************************************************
	 * Class Variable
	 **************************************************/
	
	//References
	private BallotReader c_reader;
	private TallyMethod c_tm; 
	private PollingPlaceGUI c_guiRef; 
	private Vector<BallotStyle> c_styles = null;
	private String c_errDir;
	private ScannerConfig c_config;
	
	//The ballot Store
	private BallotStore c_ballotStore;
	
	//the results string to send to the gui
	private String c_results;
	
	//counter for error files
	private int i = 0; 
	
	/* *************************************************
	 * Constructor
	 **************************************************/
	
	/**
	 * Constructor 
	 * Takes a reference to the gui for sending results, the error directory 
	 * to copy bad ballots, and a reference to configuration file
	 */
	public BallotHandler(PollingPlaceGUI p_guiRef, String p_errDir, ScannerConfig p_config)
	{
		//gui and config ref
		c_guiRef = p_guiRef;
		c_config = p_config;
		
		//send the gui a reference to this
		setRefInGUI();
		
		//get the reader and styles
		c_reader = c_config.getReader();
		c_styles = c_config.getStyles();
		
		//set up ballot store
		try
		{
			c_ballotStore = new BallotStore(c_config.getOutputFileNames());
		}
		catch (NoSuchAlgorithmException e)
		{
			//e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		//set error directory to copy bad images to
		c_errDir = p_errDir;
	}
	
	
	/* ***************************************************
	 * Ballot Methods
	 * 
	 * CastBallot, RejectBallot, and use the ballot store
	 ****************************************************/
	
	/**
	 * Casts a ballot
	 */
	public void castBallot(Ballot p_ballot)
	{
		p_ballot.setCounted(true);
		
		try
		{
			c_ballotStore.add(p_ballot);
		}
		catch (IOException e)
		{
			//e.printStackTrace();
		}
	}
	
	
	/**
	 * Rejects a Ballot
	 */
	public void rejectBallot(Ballot p_ballot)
	{
		//set notes
		Vector<String> l_v = new Vector<String>(); 
		l_v.add("Rejected by User");
		p_ballot.setNotes(l_v);
		
		p_ballot.setCounted(false);
		
		try
		{
			c_ballotStore.add(p_ballot);
		}
		catch (IOException e)
		{
			//e.printStackTrace();
		}
	}
	
	/**
	 * Runs end election info
	 * @throws IOException 
	 */
	public void endElection() throws IOException
	{
		Vector<Ballot> l_ballots = c_ballotStore.getBallots();
		Vector<String> l_outLoc = c_config.getOutputLocs();
		String l_results = "";
		
		//calculate the results
		Vector<ContestResult> l_res = calculateResults(l_ballots);
		
		for(String l_s: l_outLoc)
		{
			XMLEncoder l_xml = new XMLEncoder(new FileOutputStream(l_s + "Ballots.xml"));
			l_xml.writeObject(l_ballots);
			l_xml.close();
			
			FileWriter l_fw = new FileWriter(new File(l_s + "Results.txt"));
			
			for(ContestResult l_cr: l_res)
			{
				l_results += l_cr.toString();
				l_fw.write(l_cr.toString());
			}
			
			l_fw.close();
			//M3In Goes Here
		} 
		
		c_guiRef.displaySummaryInfo(l_results);
	}
	
	public Vector<ContestResult> calculateResults(Vector<Ballot> p_ballots)
	{
		Vector<ContestResult> l_res = new Vector<ContestResult>(); 
		Vector<Contest> l_cv = c_config.getContests(); 
		
		for(Contest l_c: l_cv)
		{
			TallyMethod l_tm = l_c.getMethod();
			l_res.add(l_tm.tally(l_c, p_ballots));
		}
		
		return l_res;
	}
	
	/* ***************************************************
	 * Other private Methods
	 ****************************************************/
	
	/**
	 * Sets a reference to this ballot handler in the gui
	 * so it can inform the the ballot handler of cast and 
	 * rejected ballots
	 */
	private void setRefInGUI()
	{
		c_guiRef.setBallotHandlerRef(this);
	}
	
	
	/* ***************************************************
	 * HandleImage Method
	 ****************************************************/
	
	/* (non-Javadoc)
	 * @see org.scantegrity.common.ImageHandler#handleImage(java.awt.image.BufferedImage)
	 */
	public void handleImage(BufferedImage p_image)
	{	
		//Start running the tests to confirm the ballot is valid
		Ballot l_b = null;
		boolean l_reqPin = false;
		
		try 
		{
			//Give me your keys
			if(DrunkDriver.isDrunk(p_image, 10))
				return;
		
			//scan the ballot
			l_b = c_reader.scanBallot(c_styles, p_image);
			
			//couldn't find alignment marks or couldn't read serial number 
			if(l_b == null || l_b.getId() == null)
			{
				c_results = "Unable to Scan Ballot. Please Try Again";
				Dialogs.displayInfoDialog(c_results, "Unable to Read Ballot! \n" 
						+ "Either I couldn't find the alignment marks, or I "
						+ "couldn't read the serial number.");
				
				//Copy the bad image to the error directory
				ImageIO.write(p_image, "tiff", new File(c_errDir + "/error" + i + ".tiff"));
				
				//increment bad image count
				i++;
				
				//TODO: log the bad image
				
				return;
			}
		}
		catch (Exception l_e) 
		{
			c_results = "Unable to Scan Ballot. Please Try Again";
			Dialogs.displayInfoDialog(c_results, "Sorry! Unable to Read the Ballot!");
			
			//
			try
			{
				ImageIO.write(p_image, "tiff", new File(c_errDir + "/error" + i + ".tiff"));
				i++;
			}
			catch (Exception e)
			{
				//TODO: log the error
				/*DEBUG* /
				e.printStackTrace();
				System.out.println("Couldn't copy bad ballot image!");
				 /*END DEBUG*/
				return;
			}
			
			//l_e.printStackTrace();
			
			return;
		}
		
		//Validate the Results
		Vector<Contest> l_cv = c_config.getContests();
		
		c_results = "<html><table align=\"center\" width=\"80%\">";
		
		if(c_ballotStore.isDuplicate(l_b.getId()))
		{
			c_results += "<tr><td align=\"center\" colspan=\"2\"><font color=\"red\"><b>" 
				+ "This Ballot is a Duplicate!</b></font></td></tr>";
			
			l_b.addNote("This Ballot is a Duplicate!");
			
			l_reqPin = true;
		}
		
		for (Contest l_c: l_cv)
		{ 				
			c_tm = l_c.getMethod();
			int l_id = l_c.getId();  
			
			c_results += "<tr bgcolor=\"DDDDDD\"><td colspan=\"2\" align=\"center\">" + l_c.getContestName() + "</td></tr>";
			
			TreeMap<String, String> l_tm = c_tm.validateContest(l_id, l_b);
			
			Set<String> l_set = l_tm.keySet();
			
			for(String s: l_set)
			{
				String l_res = l_tm.get(s); 
				
				c_results += "<tr>";
				
				if(s.equals(""))
				{
					if(l_res.equals("Overvote"))
					{
						c_results += "<td align=\"center\" colspan=\"2\"><font color=\"red\">" + l_res + "</font>";
						
						l_b.addNote("Overvote");
						
						l_reqPin = true;
					}
					else
					{
						c_results += "<td align=\"center\" colspan=\"2\">" + l_res;
					}
				}
				else
				{
					if(l_res.equals("Overvote"))
					{
						c_results += "<td align=\"center\">" + s + "</td><td align=\"center\"><font color=\"red\">" + l_res + "</font>";
						
						l_b.addNote("Overvote");
						
						l_reqPin = true;
					}
					else
					{
						c_results += "<td align=\"center\">" + s + "</td><td align=\"center\">" + l_res;
					}
				}
				
				c_results += "</td></tr>";
			}
		}
		
		c_results += "</table></html>";

		
		//Send the results to the gui
		c_guiRef.addBallotResults(c_results, l_b, l_reqPin);
	}

}
