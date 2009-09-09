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

import org.scantegrity.common.Logging;
import org.scantegrity.common.SysBeep;

import uk.org.jsane.JSane_Net.JSane_Net_Connection;
import uk.org.jsane.JSane_Base.JSane_Base_Device;
import uk.org.jsane.JSane_Base.JSane_Base_Frame;
import uk.org.jsane.JSane_Exceptions.JSane_Exception;

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
		
		try
		{
			c_saneConnection = new JSane_Net_Connection(ScannerConstants.LOCAL_IP, ScannerConstants.SANE_CONNECT_PORT);
		}
		catch (UnknownHostException e)
		{
			c_log.log(Level.FINER, e.getMessage());
		}
		catch (IOException e)
		{
			c_log.log(Level.FINER, e.getMessage());
		}
	}
	
	/**
	 * This constructor takes a port and initializes the 
	 * scanner connection with local host on the given port. 
	 * @param p_port
	 */
	public ScannerInterface(Logging p_log, int p_port)
	{
		c_log = p_log;
		
		try
		{
			c_saneConnection = new JSane_Net_Connection(ScannerConstants.LOCAL_IP, p_port);
		}
		catch (UnknownHostException e)
		{
			c_log.log(Level.FINER, e.getMessage());
		}
		catch (IOException e)
		{
			c_log.log(Level.FINER, e.getMessage());
		} 
	}
	
	/**
	 * Initializes a sane connection with given hostname on the given port
	 * @param p_hostname
	 * @param p_port
	 */
	public ScannerInterface(Logging p_log, String p_hostname, int p_port)
	{
		c_log = p_log;
		
		try
		{
			c_saneConnection = new JSane_Net_Connection(p_hostname, p_port);
		}
		catch (UnknownHostException e)
		{
			c_log.log(Level.FINER, e.getMessage());
		}
		catch (IOException e)
		{
			c_log.log(Level.FINER, e.getMessage());
		} 
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
		
		//grab the hostname and port of the scanner
		
	}
	
	/** 
	 * This method will keep polling the scanner until an image is returned. 
	 * @return
	 * @throws JSane_Exception
	 * @throws IOException
	 */
	public BufferedImage getImageFromScanner() throws JSane_Exception, IOException
	{
		c_scannerDevice = c_saneConnection.getDevice(0);
		c_scannerDevice.open(); 
		
		JSane_Base_Frame l_frame;
		BufferedImage l_image = null; 
		try
		{
			l_frame = c_scannerDevice.getFrame();
			l_image = l_frame.getImage(false);
		}
		catch (JSane_Exception e1)
		{
			c_log.log(Level.FINE, "JSane Exception thrown. Cannot get image from the scanner.");
			Thread l_th = new Thread(new SysBeep(3, 100));
			l_th.start();
		} 
		
		c_scannerDevice.close();
		
		return l_image; 
	}
	
	public void printDevices() throws IOException, JSane_Exception
	{
		c_scannerDevice = c_saneConnection.getDevice(0);
		System.out.println(c_scannerDevice.getName()); 
	}
}
