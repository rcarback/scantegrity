/*
 * Scantegrity - Successor to Punchscan, a High Integrity Voting System
 * Copyright (C) 2006  Richard Carback, David Chaum, Jeremy Clark, 
 * Aleks Essex, Stefan Popoveniuc, and Jeremy Robin
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
package org.scantegrity.common.gui;

import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.awt.Window;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * @author John Conway 
 * 
 * This class handles the main set up of the Scanner GUI. The flow of the 
 * gui is also controlled by this class.
 * 
 *  If the graphics configuration is not given to the JFrame, the class
 *  will attempt to set up a graphics configuration. If it cannot be set up, 
 *  the JFrame will be set to maximized, always on top, and undecorated. 
 *  
 *  The JFrame is set up with Card Layout to give the JFrame control 
 *  over the flow of the program. 
 */
public class ScantegrityJFrame extends JFrame
{
	/* 
	 * Serial Version UID
	 */ 
	//TODO: Does this go here?
	private static final long serialVersionUID = -1545144663742123090L;
	
	/* ***********************************************
	 * Class Variables 
	 ************************************************/
	private Dimension c_screenSize;
	
	private GraphicsDevice c_gd; 
	private GraphicsConfiguration c_gc;
	
	/* ***********************************************
	 * Constructors  
	 ************************************************/
	
	/**
	 * TODO: What to do with this constructor (PPGUI, NOTHING)
	 * @throws HeadlessException
	 */
	public ScantegrityJFrame() throws HeadlessException 
	{
		initializeDimensions();
	}

	/**
	 * TODO: What to do with this constructor (PPGUI, GC)
	 * @param gc
	 */
	public ScantegrityJFrame(GraphicsConfiguration p_gc) 
	{
		super(p_gc);
		c_gc = p_gc;
		initializeDimensions();
	}

	/**
	 * @param title
	 * @throws HeadlessException
	 */
	public ScantegrityJFrame(String p_title) throws HeadlessException 
	{
		super("Scantegrity: " + p_title);
		initializeDimensions();
	}

	/**
	 * TODO: What to do with this constructor (PPGUI, TITLE, GC)
	 * @param title
	 * @param gc
	 */
	public ScantegrityJFrame(String p_title, GraphicsConfiguration p_gc) 
	{
		super("Scantegrity: " + p_title, p_gc);
		c_gc = p_gc;
		initializeDimensions();
	}

	/* ***********************************************
	 * JFrame Methods
	 ************************************************/
	
	/**
	 * Sets the frame as visible
	 */
	public void display(boolean p_visible)
	{
		useSystemLookAndFeel();
		pack(); 
		setVisible(p_visible);
	}
	
	public void setFullScreen()
	{
		if(c_gd.isFullScreenSupported() && c_gd.isDisplayChangeSupported())
		{
			try
			{
				c_gd.setFullScreenWindow(this);
			}
			finally
			{
				c_gd.setFullScreenWindow(null);
			}
		}
		else
		{
			c_screenSize = new Dimension((int)c_gc.getBounds().getWidth(), (int)c_gc.getBounds().getHeight());
			this.setUndecorated(true);
			setPreferredSize(c_screenSize);
			this.setState(JFrame.MAXIMIZED_BOTH);
			this.setExtendedState(JFrame.MAXIMIZED_BOTH);
		}
			
	}
	
	private void initializeDimensions()
	{
		GraphicsEnvironment l_ge = null;
		
		if(c_gc == null)
		{
			l_ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			c_gd = l_ge.getDefaultScreenDevice();
			c_gc = c_gd.getDefaultConfiguration();
		}
		
		c_screenSize = new Dimension(Toolkit.getDefaultToolkit().getScreenSize());
	}
	
	private void useSystemLookAndFeel()
	{
		try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch (ClassNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (InstantiationException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IllegalAccessException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (UnsupportedLookAndFeelException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		SwingUtilities.updateComponentTreeUI(this);
		this.pack(); 
	}
}
