/**
 * @(#)ScannerController.java 
 *  
 * Created By: carback1
 * Date: Oct 7, 2009
 * Copyright (C) 2008 Scantegrity Project
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
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.media.jai.JAI;
import javax.media.jai.RenderedOp;

import org.scantegrity.common.Logging;

/**
 * Command line Unix interface to the scanner. 
 * 
 * @author carback1
 *
 */
public class ScannerController {

	private String c_scanimgcmd = "/usr/local/bin/scanscript";
	private String c_testopts = "-L";
	private String c_scanopts = "scan-%d.tiff";
	private String c_scanfmt = "scan-%s.tiff";
	private String c_sigcmd = "kill -0 %s";
	private String c_killcmd = "kill -9 %s";
	private String c_pgrep = "pgrep %s";
	private Logger c_log = null;
	private long c_timeout = 3000;
	private long c_hangup = 12000;
	
	/**
	 * Default Constructor. 
	 * 
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public ScannerController()
	{
		this(null, null, null, null);
	}
	
	/**
	 * Convenience Constructor. Uses defaults with the exception of a log obj.
	 * @param p_log
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public ScannerController(Logger p_log)
	{
		this(p_log, null, null, null);
	}
	
	
	/**
	 * Full Constructor. Most of the common options can be set.
	 * 
	 * @param p_log
	 * @param p_scanimgcmd
	 * @param p_testopts
	 * @param p_scanopts
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public ScannerController(Logger p_log, String p_scanimgcmd, 
								String p_testopts, String p_scanopts)
	{
		if (p_scanimgcmd != null) c_scanimgcmd = p_scanimgcmd;
		if (p_testopts != null) c_testopts = p_testopts;
		if (p_scanopts != null) c_scanopts = p_scanopts;
		
		c_log = p_log;		
		if (c_log == null)
		{
			c_log = new Logging("log1.txt", Level.OFF);
		}
		try
		{
			//Test to make sure the command works.
			Process l_p = Runtime.getRuntime().exec(c_scanimgcmd + " " + c_testopts);
			synchronized(this)
			{
				l_p.waitFor();
				if (l_p.exitValue() != 0)
				{
					String l_err = "Unable to open scanning program. Error: ";
					l_err += getErrorMsg(l_p);
					c_log.log(Level.SEVERE, l_err);
				}
				else
				{
					c_log.log(Level.INFO, "Scanner control initialized and working.");				
				}
			}
		}
		catch (Exception l_e)
		{
			c_log.log(Level.SEVERE, "Unable to start scanning program!" 
						+ l_e.getMessage());
		}
	}
	
	/**
	 * Return a ballot image from the scanner. The scan is in duplex, so this
	 * will always return an array of size 2. The elements inside *may* be null,
	 * however.
	 * 
	 * @return
	 * @throws IOException
	 */
	public BufferedImage[] getImagesFromScanner() throws IOException
	{
		Process l_p;
		int l_pid = 0; 
		String l_cmd = c_scanimgcmd + " " + c_scanopts;
		//Execute the scan command
		l_p = Runtime.getRuntime().exec(l_cmd);
		l_pid = getPid(c_scanimgcmd);
		if (l_pid == 0) 
		{
			try {
				l_p.exitValue();
			}
			catch (IllegalThreadStateException l_e)
			{
				c_log.log(Level.WARNING, c_scanimgcmd + " failed to exec." + 
						getErrorMsg(l_p));
			}
			l_pid = -1;
		}
		synchronized (this) {
			//Wait for it to end.
			int l_hang = 0;
			do
			{
				for (int l_i = 0; l_i < c_timeout; l_i+=200)
				{
					if (l_pid != getPid(c_scanimgcmd)) break;
					try {
						wait(200);
					} catch (InterruptedException e) {
						//do nothing.
					}
				}
				// If it's still alive after 3s send a signal to it
				// to see if it's still responding.
				if (l_pid == getPid(c_scanimgcmd))
				{
					// If it doesn't respond and it's still running kill the 
					// process and throw an error (which should cause us to 
					// try again)
					if (!isAlive(l_pid))
					{
						c_log.log(Level.WARNING, c_scanimgcmd + " did not die.");
						killPid(l_pid);
					}
					else
					{
						//Countdown, if it's still going after 12s, do something
						if (l_hang >= c_hangup) 
						{
							killPid(l_pid);
							break;
						}
						else 
						{
							l_hang += c_timeout;
						}
					}
				}
			} while (l_pid == getPid(c_scanimgcmd));
		}
		
		//try to read in the images
		BufferedImage l_imgs[] = new BufferedImage[2];
		for (int l_i = 0; l_i < 2; l_i++)
		{
			try
			{
				File l_imgFile = new File(String.format(c_scanfmt, l_i+1));
				if (l_imgFile.exists())
				{
					try
					{
						RenderedOp l_op = JAI.create("FileLoad", l_imgFile.getName());
						l_imgs[l_i] = l_op.createInstance().getAsBufferedImage();
					}
					catch (Exception l_e)
					{
						l_imgs[l_i] = null;
						c_log.log(Level.WARNING, "Read Error: " + l_imgFile);
						//TODO: Handle the error image by moving it
					}
					//TODO: Delete or move the scanned image after reading.

				
				}
				else
				{
					l_imgs[l_i] = null;
					c_log.log(Level.FINE, "Could not read " + l_imgFile.getName());
					//IT does not exist, so we can't move it.
				}
			}
			catch (Exception l_e)
			{
				l_imgs[l_i] = null;
				c_log.log(Level.WARNING, "Error: " + l_e.getMessage());
			}
		}
		
		//If we failed, check the return value. Log it.
		if (l_imgs[0] == null && l_imgs[1] == null)
		{
			int l_err = l_p.exitValue();
			switch (l_err)
			{
				case 0:
				case 7: //This is the "out of documents" error.
					break;
				default:
					c_log.log(Level.WARNING, "Scanner exited with error code "
							+ l_err + "\n Message: " + getErrorMsg(l_p));
					break;
			}
		}
		else
		{
			return l_imgs;
		}
		return l_imgs;
	}

	/**
	 * Kill a process based on the PID.
	 * 
	 * @param p_pid
	 */
	private void killPid(int p_pid) {
		try
		{
			synchronized (this) 
			{
				Runtime.getRuntime().exec(String.format(c_killcmd, p_pid)).waitFor();
			}
		}
		catch(Exception l_e)
		{
			//Nothing.
		}
	}

	/**
	 * Determine if a process is alive based on the PID number.
	 * 
	 * @param p_pid
	 * @return
	 */
	private boolean isAlive(int p_pid) 
	{
		Process l_p;
		try
		{
			synchronized (this) 
			{
				l_p = Runtime.getRuntime().exec(String.format(c_sigcmd, p_pid));
				l_p.waitFor();
				if (l_p.exitValue() == 0)
				{
					return true;
				}
			}
		}
		catch(Exception l_e)
		{
			//Nothing.
		}
		return false;
	}

	/**
	 * Returns the PID for the given command.
	 * 
	 * @param p_cmd
	 * @return
	 */
	private int getPid(String p_cmd) 
	{
		int l_pid = 0;
		BufferedReader l_buf;
		Process l_p;
		try
		{
			l_p = Runtime.getRuntime().exec(String.format(c_pgrep, p_cmd));
			synchronized (this) {
				l_p.waitFor();	
			}
			l_buf = new BufferedReader(new InputStreamReader(l_p.getInputStream()));
			String l_str = l_buf.readLine();
			if (l_str != null && l_str.length() > 0)
			{
				l_pid = Integer.parseInt(l_str);				
			}
		} 
		catch (Exception e_p)
		{
			//Do nothing.
			l_pid = 0;
		}
		
		return l_pid;
	}

	/**
	 * @return the c_scanimgcmd
	 */
	public String getScanimgcmd() {
		return c_scanimgcmd;
	}

	/**
	 * @param cScanimgcmd the c_scanimgcmd to set
	 */
	public void setScanimgcmd(String p_scanimgcmd) {
		c_scanimgcmd = p_scanimgcmd;
	}

	/**
	 * @return the c_testopts
	 */
	public String getTestopts() {
		return c_testopts;
	}

	/**
	 * @param cTestopts the c_testopts to set
	 */
	public void setTestopts(String p_testopts) {
		c_testopts = p_testopts;
	}

	/**
	 * @return the c_scanopts
	 */
	public String getScanopts() {
		return c_scanopts;
	}

	/**
	 * @param cScanopts the c_scanopts to set
	 */
	public void setScanopts(String p_scanopts) {
		c_scanopts = p_scanopts;
	}

	/**
	 * @return the c_log
	 */
	public Logger getLog() {
		return c_log;
	}

	/**
	 * @param cLog the c_log to set
	 */
	public void setLog(Logger p_log) {
		c_log = p_log;
	}


	public void setScanfmt(String scanfmt) {
		c_scanfmt = scanfmt;
	}

	public String getScanfmt() {
		return c_scanfmt;
	}

	/**
	 * Using the given process, grab the error stream and report the result.
	 * 
	 * @param p_p
	 * @return
	 * @throws IOException
	 */
	private String getErrorMsg(Process p_p) throws IOException
	{
		String l_errmsg = "";
		String l_tmp = "";
		BufferedReader l_buf;
		l_buf = new BufferedReader(new InputStreamReader(p_p.getErrorStream()));
		//Read until empty/null
		l_tmp = l_buf.readLine();
		while (l_tmp != null)
		{
			l_errmsg += "\n" + l_tmp;
			l_tmp = l_buf.readLine();
		}
		return l_errmsg;
	}	
}
