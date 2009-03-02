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
package org.scantegrity.scanner.gui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

/**
 * @author John Conway
 *
 */
public class ScannerInfoPanels implements ActionListener
{
	/*
	 * Class Variables
	 */
	private JPanel c_mainPanel; 
	private JButton c_startElectionButton;

	/*
	 * Constructor
	 */
	public ScannerInfoPanels() 
	{
		initializeComponents();
		initializeDimensions();
		buildPanel(); 
	}
	
	/* ******************************************
	 * GUI Build Methods
	 *******************************************/
	
	/**
	 * Initializes the GUI Components
	 */
	private void initializeComponents()
	{
		c_mainPanel = new JPanel(); 
		
		c_startElectionButton = new JButton();
		c_startElectionButton.setText("Start Election");
		c_startElectionButton.addActionListener(this);
		c_startElectionButton.setFocusable(false); 
	}
	
	/**
	 * Initializes the Dimensions of Components
	 */
	private void initializeDimensions()
	{
		//Create Dimensions
		Dimension l_maxButtonSize = new Dimension(400,300);
		Dimension l_minButtonSize = new Dimension(300,100); 
		Dimension l_preferredButtonSize = new Dimension(300,200);
		
		Dimension l_maxPanelSize = new Dimension(800,600);
		Dimension l_minPanelSize = new Dimension(800,600); 
		Dimension l_preferredPanelSize = new Dimension(800,600);
		
		//set button dimensions
		c_startElectionButton.setMaximumSize(l_maxButtonSize);
		c_startElectionButton.setMinimumSize(l_minButtonSize);
		c_startElectionButton.setPreferredSize(l_preferredButtonSize);
		
		//set Panel dimensions
		c_mainPanel.setMaximumSize(l_maxPanelSize);
		c_mainPanel.setMinimumSize(l_minPanelSize);
		c_mainPanel.setPreferredSize(l_preferredPanelSize);
	}
	
	/**
	 * Builds the Panel
	 */
	private void buildPanel()
	{
		c_mainPanel.add(c_startElectionButton);
	}
	
	/* ******************************************
	 * Getters and Setters
	 *******************************************/
	
	/**
	 * @return JPanel - The Start Election Panel
	 */
	public JPanel getStartElectionPanel()
	{
		return c_mainPanel;
	}

	/* ******************************************
	 * Action Listener Methods
	 *******************************************/
	
	public void actionPerformed(ActionEvent e)
	{
		//TODO:Figure out how to determine the button cleanly
		if(e.getActionCommand().equals("Start Election"))
		{
			//Will start Election
			//TODO: How to start the election?
		}
		
	}
}
