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

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.Border;

import org.scantegrity.common.gui.ScantegrityJFrame;

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
public class PollingPlaceGUI extends JFrame 
			implements Runnable,ActionListener
{
	
	/* 
	 * Serial Version UID
	 */ 
	//TODO: Does this go here?
	private static final long serialVersionUID = -1875275649149039514L;

	/* ***********************************************
	 * Class Variables 
	 ************************************************/
	private ScantegrityJFrame c_frame; 
	private CardLayout c_scannerInfoCardLayout; 
	private CardLayout c_electionInfoCardLayout;
	
	//main panels
	private JPanel c_topPanel; 
	private JPanel c_electionInfoPanel; 
	private JPanel c_scannerInfoPanel;
	private JPanel c_infoBarPanel; 
	
	//card panels
	private JPanel c_chiefJudgeLoginPanel;
	private JPanel c_startElectionPanel;
	private JPanel c_scanningBallotsPanel;
	private JPanel c_waitingForBallotsPanel;
	private JPanel c_ballotResultsPanel;
	private JPanel c_electionInfoCardPanel;
	
	//Buttons
	private JButton c_adminButton;
	private JButton c_chiefLoginButton;
	private JButton c_startElectionButton;
	private JButton c_castBallotButton;
	private JButton c_rejectBallotButton; 
	
	//Fields
	private JTextField c_castField; 
	private JTextField c_spoiledField;

	//password fields
	private JPasswordField c_chiefPasswordField;
	
	/* ***********************************************
	 * Constructors  
	 ************************************************/
	
	/**
	 * TODO: What to do with this constructor (PPGUI, NOTHING)
	 * @throws HeadlessException
	 */
	public PollingPlaceGUI()
	{
		//TODO: get configuration info
		
		guiInit(); 
	}

	/* ***********************************************
	 * Runnable Methods  
	 ************************************************/
	/**
	 * Runs the GUI thread
	 */
	public void run()
	{
		changeCard(ScannerUIConstants.LOGIN_CARD);
		//display the frame
		c_frame.display(true);
	}
	
	/* ***********************************************
	 * Public Methods to change status
	 ************************************************/
	
	public void setToScanningBallot()
	{
		changeCard(ScannerUIConstants.SCANNING_BALLOT_CARD);
	}
	
	public void displayScanResults(boolean p_badBallot)
	{
		if(!p_badBallot)
		{
			changeCard(ScannerUIConstants.BALLOT_INFO_CARD);
		}
		else
		{
			changeCard(ScannerUIConstants.BALLOT_INFO_WITH_REJECTED_CARD);
		}
	}
	
	/* ***********************************************
	 * GUI Build Methods
	 ************************************************/
	/**
	 * Initializes the frame and panels, then calls buildLayout()
	 */
	private void guiInit()
	{
		c_frame = new ScantegrityJFrame("Polling Place");
		c_frame.setFullScreen(); 
		c_frame.setUndecorated(true);
		c_frame.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		//c_frame.setAlwaysOnTop(true);
		
		buildLayout(); 
	}
	
	/**
	 * Builds the panels that will be used and 
	 * sets their layouts. 
	 */
	private void buildLayout()
	{
		//set up frame Layout 
		c_frame.setLayout(new BorderLayout()); 
		
		//set up internal panels
		buildTopPanel();
		buildElectionInfoPanel(); 
		buildScannerPanel(); 
		buildInfoBar(); 
		
		//put panels into the JFrame
		JPanel l_tempPanel = new JPanel(); 
		l_tempPanel.add(c_electionInfoCardPanel, BorderLayout.NORTH);
		l_tempPanel.add(c_scannerInfoPanel, BorderLayout.CENTER);
		
		c_frame.add(c_topPanel, BorderLayout.NORTH);
		c_frame.add(l_tempPanel, BorderLayout.CENTER);
		c_frame.add(c_infoBarPanel, BorderLayout.SOUTH);
		
	}
	
	/* *************************************************
	 * Panel Build Methods
	 * TODO: Reorganize the normal and compact election info panels
	 **************************************************/
	
	/**
	 * This method builds the top panel 
	 * that holds the admin buttons
	 */
	private void buildTopPanel()
	{
		c_topPanel = new JPanel(new FlowLayout());
		
		//create admin button
		c_adminButton = new JButton(); 
		c_adminButton.setText("Administration");
		c_adminButton.setFocusable(false);
		c_adminButton.setBorderPainted(false);
		c_adminButton.setContentAreaFilled(false);
		c_adminButton.addActionListener(this);
		
		//border 
		Border l_border = BorderFactory.createLoweredBevelBorder(); 
		c_topPanel.setBorder(l_border);
		
		JPanel l_tempPanel = new JPanel(); 
		l_tempPanel.setPreferredSize(new Dimension(c_frame.getWidth() - c_adminButton.getWidth(), c_adminButton.getHeight()));
		
		c_topPanel.add(c_adminButton);
		c_topPanel.add(l_tempPanel);
	}
	
	/**
	 * This builds the Election info panel that will 
	 * display the election information to the user. 
	 * until the election is started.  
	 */
	private void buildElectionInfoPanel()
	{
		c_electionInfoCardPanel = new JPanel();
		
		//set layout manager
		c_electionInfoCardLayout = new CardLayout();
		
		c_electionInfoCardPanel.setLayout(c_electionInfoCardLayout);
		
		//build the card panels
		buildExtendedElectionInfoPanel();
		buildCompactElectionInfoPanel();
		
		//set default card 
		c_electionInfoCardLayout.show(c_electionInfoCardPanel,
				ScannerUIConstants.EXTENDED_ELECTION_INFO_CARD);
	}
		
	private void buildExtendedElectionInfoPanel()
	{
		JPanel l_electionInfoPanel = new JPanel(); 
		l_electionInfoPanel.setLayout(new GridBagLayout());
		
		//TODO: Find a way around this font size
		int l_fs = (int)(Toolkit.getDefaultToolkit().getScreenSize().getWidth() / 100); 
		
		JLabel l_ppIdLabel = new JLabel("<html><font size=\"" + l_fs + "\">Polling Place ID #: </font></html>");
		JLabel l_ppNameLabel = new JLabel("<html><font size=\"" + l_fs + "\">Polling Place Name: </font></html>");
		JLabel l_ppLocLabel = new JLabel("<html><font size=\"" + l_fs + "\">Polling Place Location: </font></html>");
		JLabel l_elecDateLabel = new JLabel("<html><font size=\"" + l_fs + "\">Date of Election: </font></html>");
		JLabel l_elecStartTimeLabel = new JLabel("<html><font size=\"" + l_fs + "\">Start Time of Election: </font></html>");
		JLabel l_chiefJudgeLabel = new JLabel("<html><font size=\"" + l_fs + "\">Chief Judge: </font></html>");
		
		//TODO: These will be filled in by the configuration interface
		JLabel l_ppId = new JLabel("<html><font size=\"" + l_fs + "\">0001</font></html>");
		JLabel l_ppName = new JLabel("<html><font size=\"" + l_fs + "\">Polling Place</font></html>");
		JLabel l_ppLoc = new JLabel("<html><font size=\"" + l_fs + "\">Catonsville, MD</font></html>");
		JLabel l_elecDate = new JLabel("<html><font size=\"" + l_fs + "\">01/01/2009</font></html>");
		JLabel l_elecStartTime = new JLabel("<html><font size=\"" + l_fs + "\">07:00</font></html>");
		JLabel l_chiefJudge = new JLabel("<html><font size=\"" + l_fs + "\">John Smith</font></html>");

		//Insets 
		Insets l_insets = new Insets(0, 0, 10, 50);
		
		//add to layout
		GridBagConstraints l_c;
		
		//Row 1
		 l_c = new GridBagConstraints();
		 l_c.gridx = 0; 
		 l_c.gridy = 2; 
		 l_c.anchor = GridBagConstraints.LINE_START;
		 l_c.insets = l_insets; 
		 l_electionInfoPanel.add(l_ppIdLabel, l_c);
		 
		 l_c.gridx = 1;
		 l_c.gridy = 2; 
		 l_c.anchor = GridBagConstraints.LINE_START;
		 l_c.insets = l_insets; 
		 l_electionInfoPanel.add(l_ppId, l_c);
		
		 //Row 2
		 l_c.gridx = 0;
		 l_c.gridy = 3; 
		 l_c.anchor = GridBagConstraints.LINE_START;
		 l_c.insets = l_insets; 
		 l_electionInfoPanel.add(l_ppNameLabel, l_c);
		 
		 l_c.gridx = 1;
		 l_c.gridy = 3;
		 l_c.anchor = GridBagConstraints.LINE_START;
		 l_c.insets = l_insets; 
		 l_electionInfoPanel.add(l_ppName, l_c);
		 
		 //Row 3
		 l_c = new GridBagConstraints();
		 l_c.gridx = 0; 
		 l_c.gridy = 4; 
		 l_c.anchor = GridBagConstraints.LINE_START;
		 l_c.insets = l_insets; 
		 l_electionInfoPanel.add(l_ppLocLabel, l_c);
		 
		 l_c.gridx = 1;
		 l_c.gridy = 4; 
		 l_c.anchor = GridBagConstraints.LINE_START;
		 l_c.insets = l_insets; 
		 l_electionInfoPanel.add(l_ppLoc, l_c);
		
		 //Row 4
		 l_c = new GridBagConstraints();
		 l_c.gridx = 0; 
		 l_c.gridy = 5; 
		 l_c.anchor = GridBagConstraints.LINE_START;
		 l_c.insets = l_insets; 
		 l_electionInfoPanel.add(l_elecDateLabel, l_c);
		 
		 l_c.gridx = 1;
		 l_c.gridy = 5;
		 l_c.anchor = GridBagConstraints.LINE_START;
		 l_c.insets = l_insets; 
		 l_electionInfoPanel.add(l_elecDate, l_c);
		
		 //Row 5
		 l_c.gridx = 0;
		 l_c.gridy = 6; 
		 l_c.anchor = GridBagConstraints.LINE_START;
		 l_c.insets = l_insets; 
		 l_electionInfoPanel.add(l_elecStartTimeLabel, l_c);
		 
		 l_c.gridx = 1;
		 l_c.gridy = 6; 
		 l_c.anchor = GridBagConstraints.LINE_START;
		 l_c.insets = l_insets; 
		 l_electionInfoPanel.add(l_elecStartTime, l_c);
		 
		 //Row 6
		 l_c = new GridBagConstraints();
		 l_c.gridx = 0; 
		 l_c.gridy = 7; 
		 l_c.anchor = GridBagConstraints.LINE_START;
		 l_c.insets = l_insets; 
		 l_electionInfoPanel.add(l_chiefJudgeLabel, l_c);
		 
		 l_c.gridx = 1;
		 l_c.gridy = 7; 
		 l_c.anchor = GridBagConstraints.LINE_START;
		 l_c.insets = l_insets;
		 l_electionInfoPanel.add(l_chiefJudge, l_c);
		 
		//add the Border
		Border l_border = BorderFactory.createLoweredBevelBorder(); 
		l_electionInfoPanel.setBorder(l_border);
		 
		 c_electionInfoCardPanel.add(l_electionInfoPanel, ScannerUIConstants.EXTENDED_ELECTION_INFO_CARD);
	}
	
	/**
	 * This builds the compacted election information 
	 * that will be displayed after the election has started. 
	 */
	private void buildCompactElectionInfoPanel()
	{
		JPanel l_electionInfoPanel = new JPanel(); 
		l_electionInfoPanel.setLayout(new GridBagLayout());
		
		//TODO: Find a way around this font size
		int l_fs = (int)(Toolkit.getDefaultToolkit().getScreenSize().getWidth() / 250 );  
		
		JLabel l_ppIdLabel = new JLabel("<html><font size=\"" + l_fs + "\">Polling Place ID #: </font></html>");
		JLabel l_ppNameLabel = new JLabel("<html><font size=\"" + l_fs + "\">Polling Place Name: </font></html>");
		JLabel l_ppLocLabel = new JLabel("<html><font size=\"" + l_fs + "\">Polling Place Location: </font></html>");
		JLabel l_elecDateLabel = new JLabel("<html><font size=\"" + l_fs + "\">Date of Election: </font></html>");
		JLabel l_elecStartTimeLabel = new JLabel("<html><font size=\"" + l_fs + "\">Start Time of Election: </font></html>");
		JLabel l_chiefJudgeLabel = new JLabel("<html><font size=\"" + l_fs + "\">Chief Judge: </font></html>");
		
		//TODO: These will be filled in by the configuration interface
		JLabel l_ppId = new JLabel("<html><font size=\"" + l_fs + "\">0001</font></html>");
		JLabel l_ppName = new JLabel("<html><font size=\"" + l_fs + "\">Polling Place</font></html>");
		JLabel l_ppLoc = new JLabel("<html><font size=\"" + l_fs + "\">Catonsville, MD</font></html>");
		JLabel l_elecDate = new JLabel("<html><font size=\"" + l_fs + "\">01/01/2009</font></html>");
		JLabel l_elecStartTime = new JLabel("<html><font size=\"" + l_fs + "\">07:00</font></html>");
		JLabel l_chiefJudge = new JLabel("<html><font size=\"" + l_fs + "\">John Smith</font></html>");
		
		//Insets 
		Insets l_insets = new Insets(0, 0, 10, 30);
		
		//add to layout
		GridBagConstraints l_c;
		
		//Row 1
		 l_c = new GridBagConstraints();
		 l_c.gridx = 0; 
		 l_c.gridy = 0; 
		 l_c.anchor = GridBagConstraints.LINE_START;
		 l_c.insets = l_insets; 
		 l_electionInfoPanel.add(l_ppIdLabel, l_c);
		 
		 l_c.gridx = 1;
		 l_c.gridy = 0; 
		 l_c.anchor = GridBagConstraints.LINE_START;
		 l_c.insets = l_insets; 
		 l_electionInfoPanel.add(l_ppId, l_c);

		 l_c.gridx = 2;
		 l_c.gridy = 0; 
		 l_c.anchor = GridBagConstraints.LINE_START;
		 l_c.insets = l_insets; 
		 l_electionInfoPanel.add(l_ppNameLabel, l_c);
		 
		 l_c.gridx = 3;
		 l_c.gridy = 0;
		 l_c.anchor = GridBagConstraints.LINE_START;
		 l_c.insets = l_insets; 
		 l_electionInfoPanel.add(l_ppName, l_c);
		 
		 l_c = new GridBagConstraints();
		 l_c.gridx = 4; 
		 l_c.gridy = 0; 
		 l_c.anchor = GridBagConstraints.LINE_START;
		 l_c.insets = l_insets; 
		 l_electionInfoPanel.add(l_ppLocLabel, l_c);
		 
		 l_c.gridx = 5;
		 l_c.gridy = 0; 
		 l_c.anchor = GridBagConstraints.LINE_START;
		 l_c.insets = l_insets; 
		 l_electionInfoPanel.add(l_ppLoc, l_c);
		
		 //Row 2
		 l_c = new GridBagConstraints();
		 l_c.gridx = 0; 
		 l_c.gridy = 1; 
		 l_c.anchor = GridBagConstraints.LINE_START;
		 l_c.insets = l_insets; 
		 l_electionInfoPanel.add(l_elecDateLabel, l_c);
		 
		 l_c.gridx = 1;
		 l_c.gridy = 1;
		 l_c.anchor = GridBagConstraints.LINE_START;
		 l_c.insets = l_insets; 
		 l_electionInfoPanel.add(l_elecDate, l_c);
		
		 l_c.gridx = 2;
		 l_c.gridy = 1; 
		 l_c.anchor = GridBagConstraints.LINE_START;
		 l_c.insets = l_insets; 
		 l_electionInfoPanel.add(l_elecStartTimeLabel, l_c);
		 
		 l_c.gridx = 3;
		 l_c.gridy = 1; 
		 l_c.anchor = GridBagConstraints.LINE_START;
		 l_c.insets = l_insets; 
		 l_electionInfoPanel.add(l_elecStartTime, l_c);

		 l_c.gridx = 4; 
		 l_c.gridy = 1; 
		 l_c.anchor = GridBagConstraints.LINE_START;
		 l_c.insets = l_insets; 
		 l_electionInfoPanel.add(l_chiefJudgeLabel, l_c);
		 
		 l_c.gridx = 5;
		 l_c.gridy = 1; 
		 l_c.anchor = GridBagConstraints.LINE_START;
		 l_c.insets = l_insets;
		 l_electionInfoPanel.add(l_chiefJudge, l_c);
		 
		//add the Border
		Border l_border = BorderFactory.createLoweredBevelBorder(); 
		l_electionInfoPanel.setBorder(l_border);
		
		l_electionInfoPanel.setPreferredSize(new Dimension((int)Toolkit.getDefaultToolkit().getScreenSize().getWidth(),100));
		 
		 c_electionInfoCardPanel.add(l_electionInfoPanel, ScannerUIConstants.COMPACT_ELECTION_INFO_CARD);
	}
	
	private void buildScannerPanel()
	{
		//initialize main panel
		c_scannerInfoPanel = new JPanel(); 
		
		//build the panels
		buildLoginCardPanel();
		buildStartCardPanel();
		buildScanningCardPanel();
		buildWaitingCardPanel();
		buildBallotResultsCards();
		
		c_scannerInfoCardLayout = new CardLayout();
		c_scannerInfoPanel.setLayout(c_scannerInfoCardLayout);
		
		c_scannerInfoPanel.add(c_chiefJudgeLoginPanel, ScannerUIConstants.LOGIN_CARD);
		c_scannerInfoPanel.add(c_startElectionPanel, ScannerUIConstants.START_ELECTION_CARD);
		c_scannerInfoPanel.add(c_scanningBallotsPanel, ScannerUIConstants.SCANNING_BALLOT_CARD);
		c_scannerInfoPanel.add(c_waitingForBallotsPanel, ScannerUIConstants.WAITING_FOR_BALLOT_CARD);
		//c_scannerInfoPanel.add(c_ballotResultsPanel, ScannerUIConstants.BALLOT_INFO_CARD);
		
		//add border
		//set borders 
		Border l_border = BorderFactory.createLoweredBevelBorder();
		c_scannerInfoPanel.setBorder(l_border);
	}
	
	/**
	 * This method builds the info bar that displays the 
	 * ballots cast and spoiled. This bar is always 
	 * displayed to the user. 
	 */
	private void buildInfoBar()
	{
		c_infoBarPanel = new JPanel(); 
		c_infoBarPanel.setLayout(new FlowLayout(FlowLayout.LEADING)); 
		
		JLabel l_castLabel = new JLabel("Cast Ballots: ");
		l_castLabel.setFocusable(false);
		
		JLabel l_spoiledLabel = new JLabel("Spoiled Ballots: ");
		l_spoiledLabel.setFocusable(false);
		
		JSeparator l_verticalSeparator = new JSeparator(SwingConstants.VERTICAL);
		
		//border
		Border l_bf = BorderFactory.createLoweredBevelBorder(); 
		c_infoBarPanel.setBorder(l_bf);
		
		c_castField = new JTextField("0");
		c_castField.setEditable(false);
		c_castField.setFocusable(false);
		
		c_spoiledField = new JTextField("0"); 
		c_spoiledField.setEditable(false);
		c_spoiledField.setFocusable(false);
		
		c_infoBarPanel.add(l_castLabel);
		c_infoBarPanel.add(c_castField);
		c_infoBarPanel.add(l_verticalSeparator);
		c_infoBarPanel.add(l_spoiledLabel);
		c_infoBarPanel.add(c_spoiledField);
		
		c_infoBarPanel.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
	}

	/* ***********************************************
	 * Card Panel Build Methods
	 ************************************************/
	
	private void buildLoginCardPanel()
	{
		c_chiefJudgeLoginPanel = new JPanel(); 
		JPanel l_passwordPanel = new JPanel(); 
		
		//set the layouts
		c_chiefJudgeLoginPanel.setLayout(new BoxLayout(c_chiefJudgeLoginPanel, BoxLayout.Y_AXIS));
		l_passwordPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		
		//build components 
		JLabel l_passwordLabel = new JLabel("Chief Judge Password: ");
		c_chiefPasswordField = new JPasswordField(
				ScannerUIConstants.NUM_PASSWORD_COLUMNS);
		
		c_chiefLoginButton = new JButton(); 
		c_chiefLoginButton.setText("Login");
		c_chiefLoginButton.setFocusable(false);
		c_chiefLoginButton.addActionListener(this);
		
		//put components into layout 
		l_passwordPanel.add(l_passwordLabel);
		l_passwordPanel.add(c_chiefPasswordField);
		
		c_chiefJudgeLoginPanel.add(l_passwordPanel);
		c_chiefJudgeLoginPanel.add(c_chiefLoginButton);
	}
	
	private void buildStartCardPanel()
	{
		c_startElectionPanel = new JPanel(); 
		
		c_startElectionButton = new JButton(); 
		c_startElectionButton.setText("Start Election");
		c_startElectionButton.setFocusable(false);
		c_startElectionButton.addActionListener(this);
		
		c_startElectionPanel.add(c_startElectionButton);
	}
	
	private void buildScanningCardPanel()
	{
		c_scanningBallotsPanel = new JPanel(); 
		
		JLabel l_label = new JLabel("<html><font size=\"10\">Scanning Ballot...</font></html>");
		
		c_scanningBallotsPanel.add(l_label);
	}
	
	private void buildWaitingCardPanel()
	{
		c_waitingForBallotsPanel = new JPanel(); 
		
		JLabel l_label = new JLabel("<html><font size=\"10\">Waiting for Ballot...</font></html>");
		
		c_waitingForBallotsPanel.add(l_label);
	}
	
	private void buildBallotResultsCards()
	{
		
	}
	
	/* ***********************************************
	 * Dialog Boxes
	 ************************************************/
	
	private int showAdminDialogBox()
	{
		//make a dialog box 
		Object[] l_options = {"Return", "End Election"};
		String l_title = "Administration";
		
		int l_choice = JOptionPane.showOptionDialog(c_frame, 
											l_title,
											l_title,
											JOptionPane.YES_NO_OPTION, 
											JOptionPane.QUESTION_MESSAGE, 
											null,
											l_options, 
											l_options[0]);
		
		return l_choice;
	}
	
	/* ***********************************************
	 * Card Layout Methods
	 ************************************************/
	
	private void changeCard(String p_cardName)
	{
		c_scannerInfoCardLayout.show(c_scannerInfoPanel, p_cardName);
	}
	
	/* ***********************************************
	 * Action Listener Methods
	 ************************************************/
	
	/**
	 * Actions to be taken when a button is activated. 
	 */
	public void actionPerformed(ActionEvent e)
	{
		if(e.getActionCommand().equals(c_adminButton.getText()))
		{
			int l_choice = showAdminDialogBox();
			
			if(l_choice == 1)
			{
				c_frame.dispose();
			}
		}		
		else if(e.getActionCommand().equals(c_startElectionButton.getText()))
		{
			//build compacted election info 
			c_electionInfoCardLayout.show(c_electionInfoCardPanel, 
					ScannerUIConstants.COMPACT_ELECTION_INFO_CARD);
			c_electionInfoCardPanel.doLayout();
	
			c_frame.validate();
			
			changeCard(ScannerUIConstants.WAITING_FOR_BALLOT_CARD);
		}
		else if(e.getActionCommand().equals(c_chiefLoginButton.getText()))
		{
			//check for login based on boolean 
			boolean l_validated = true; 
			
			if(l_validated)
				changeCard(ScannerUIConstants.START_ELECTION_CARD);
			else
				changeCard(ScannerUIConstants.LOGIN_CARD);
		}
	}
}
