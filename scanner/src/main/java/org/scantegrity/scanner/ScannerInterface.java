/*
 * @(#)ScannerInterface.java.java
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
import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.logging.Level;

import javax.imageio.ImageIO;

import org.scantegrity.common.DrunkDriver;
import org.scantegrity.common.Logging;
import org.scantegrity.common.SysBeep;

import uk.org.jsane.JSane_Net.JSane_Net_Connection;
import uk.org.jsane.JSane_Base.JSane_Base_Device;
import uk.org.jsane.JSane_Base.JSane_Base_Frame;
import uk.org.jsane.JSane_Exceptions.JSane_Exception;
import uk.org.jsane.JSane_Exceptions.JSane_Exception_IoError;
import uk.org.jsane.JSane_Exceptions.JSane_Exception_NoDocs;

/**
 * @author John Conway
 * 
 * The Scanner Interface holds methods for 
 * accessing the scanner hardware. 
 * This class is essentially a wrapper
 * for the JSane library. 
 */
public class ScannerInterface
{
	/* Sane Variables */
	private JSane_Base_Device c_scannerDevice;
	private JSane_Net_Connection c_saneConnection; 
	private String c_hostname; 
	private int c_port; 
	
	/* Scantegrity References */
	private ScannerConfig c_scannerConfigRef; 
	private Logging c_log; 
	
	/**
	 * Default Constructor
	 * 
	 * Initializes the connection to local host with default port of 6567
	 */
	public ScannerInterface(Logging p_log)
	{
		c_log = p_log; 
		c_log.log(Level.INFO, "Setting up JSane Connection to Scanner using default settings.");
		
		c_hostname = ScannerConstants.LOCAL_IP; 
		c_port = ScannerConstants.SANE_CONNECT_PORT; 
		
		connect(c_hostname, c_port);
	}
	
	/**
	 * This constructor takes a port and initializes the 
	 * scanner connection with local host on the given port. 
	 * @param p_port
	 */
	public ScannerInterface(Logging p_log, int p_port)
	{
		c_log = p_log;
		c_log.log(Level.INFO, "Setting up JSane Connection to Scanner using localhost and port "
								+ p_port + ".");
		c_hostname = ScannerConstants.LOCAL_IP; 
		c_port = p_port; 
		
		connect(c_hostname, c_port);
	}
	
	/**
	 * Initializes a sane connection with given hostname on the given port
	 * @param p_hostname
	 * @param p_port
	 */
	public ScannerInterface(Logging p_log, String p_hostname, int p_port)
	{
		c_log = p_log;
		c_log.log(Level.INFO, "Setting up JSane Connection to Scanner using hostname "
								+ p_hostname + " and port "
								+ p_port + ".");
		
		c_hostname = p_hostname;
		c_port = p_port; 
		
		connect(c_hostname, c_port);
	}
	
	/**
	 * This constructor gets the hostname and port from the 
	 * config file. 
	 * 
	 * @param p_scannerConfigRef
	 */
	public ScannerInterface(Logging p_log, ScannerConfig p_scannerConfigRef)
	{
		c_log = p_log;
		
		c_scannerConfigRef = p_scannerConfigRef; 
		
		//TODO: grab the hostname and port of the scanner

	}
	
	public void connect(String p_hostname, int p_port)
	{
		try 
		{
			c_saneConnection = new JSane_Net_Connection(p_hostname, p_port);
			c_scannerDevice = c_saneConnection.getDevice(0);
			c_scannerDevice.open();
			c_scannerDevice.getOption("resolution").setValue("150");
		} 
		catch (UnknownHostException e_uh) 
		{
			c_log.log(Level.SEVERE, e_uh.getMessage() + " Cannot reconnect.");
		}
		catch (JSane_Exception e_gen) 
		{
			c_log.log(Level.SEVERE, e_gen.getMessage() + " Cannot reconnect.");
		}
		catch (IOException e_io) 
		{
			c_log.log(Level.SEVERE, e_io.getMessage() + " Cannot reconnect.");
		}
	}
	
	public void reconnect()
	{
		//destroy old connection
		try 
		{
			c_log.log(Level.INFO, "Closing Old JSane Connection");
			c_scannerDevice.close();
			c_saneConnection.exit();
		} 
		catch (IOException e_io) 
		{
			c_log.log(Level.SEVERE, "Could not close the old JSane Connection");
		}
		catch (JSane_Exception e) 
		{
			c_log.log(Level.SEVERE, "Could not close the old JSane Connection");
		} 
		
		//reconnect
		connect(c_hostname, c_port); 
	}
	
	/** 
	 * This method will keep polling the scanner until an image is returned. 
	 * @return
	 * @throws JSane_Exception
	 * @throws IOException
	 */
	public BufferedImage getImageFromScanner() throws JSane_Exception, IOException
	{	
		BufferedImage l_image = null; 
		
		c_log.log(Level.INFO, "Scanning Image");
		
		while(l_image == null)
		{	
			try
			{
				JSane_Base_Frame l_frame = c_scannerDevice.getFrame();
				l_image = l_frame.getImage();
			}
			catch (JSane_Exception_NoDocs e_noDoc)
			{
				continue; 
			}
			catch (JSane_Exception_IoError e_io)
			{
				c_log.log(Level.SEVERE, "JSane Exception thrown : " + e_io.getMessage() + ". Reconnecting");
				Thread l_th = new Thread(new SysBeep(3, 100));
				l_th.start();
				
				throw new JSane_Exception_IoError(); 
			}
			catch (JSane_Exception e_gen)
			{
				c_log.log(Level.SEVERE, "JSane Exception thrown : " + e_gen.getMessage() + ". Cannot get image from the scanner.");
				Thread l_th = new Thread(new SysBeep(3, 100));
				l_th.start();
				
				return null;
			}
		}
		
		//drunkDriver
		//Give me your keys
		if(DrunkDriver.isDrunk(l_image, 10))
			return null;
		
		return l_image; 
	}
	
	public void printDevices() throws IOException, JSane_Exception
	{
		c_scannerDevice = c_saneConnection.getDevice(0);
		System.out.println(c_scannerDevice.getName()); 
	}
}
