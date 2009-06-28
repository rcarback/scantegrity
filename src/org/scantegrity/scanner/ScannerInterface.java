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

import javax.imageio.ImageIO;

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
	
	/**
	 * Default Constructor
	 * 
	 * Initializes the connection to local host with default port of 6566
	 */
	public ScannerInterface()
	{
		try
		{
			c_saneConnection = new JSane_Net_Connection(ScannerConstants.LOCAL_IP, ScannerConstants.SANE_CONNECT_PORT);
		}
		catch (UnknownHostException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * This constructor takes a port and initializes the 
	 * scanner connection with local host on the given port. 
	 * @param p_port
	 */
	public ScannerInterface(int p_port)
	{
		try
		{
			c_saneConnection = new JSane_Net_Connection(ScannerConstants.LOCAL_IP, p_port);
		}
		catch (UnknownHostException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	
	/**
	 * Initializes a sane connection with given hostname on the given port
	 * @param p_hostname
	 * @param p_port
	 */
	public ScannerInterface(String p_hostname, int p_port)
	{
		try
		{
			c_saneConnection = new JSane_Net_Connection(p_hostname, p_port);
		}
		catch (UnknownHostException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	
	/**
	 * This constructor gets the hostname and port from the 
	 * config file. 
	 * 
	 * @param p_scannerConfigRef
	 */
	public ScannerInterface(ScannerConfig p_scannerConfigRef)
	{
		c_scannerConfigRef = p_scannerConfigRef; 
		
		//grab the hostname and port of the scanner
		
	}
	
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
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} 
		
		c_scannerDevice.close();
		
		return l_image; 
	}
	
	private void printDevices() throws IOException, JSane_Exception
	{
		c_scannerDevice = c_saneConnection.getDevice(0);
		System.out.println(c_scannerDevice.getName()); 
		c_scannerDevice.close(); 
	}
	
	public static void main(String[] args)
	{
		ScannerInterface l_si = new ScannerInterface();
		
		try
		{
			l_si.printDevices();
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
		
		//get an image from the scanner
		BufferedImage l_image = null;
		try
		{
			l_image = l_si.getImageFromScanner();
			ImageIO.write(l_image, "tiff", new File("test.tiff"));
		}
		catch (JSane_Exception e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
}
