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
import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.border.Border;

import org.scantegrity.common.DirectoryWatcher;
import org.scantegrity.common.ImageLoader;
import org.scantegrity.common.gui.Dialogs;
import org.scantegrity.common.gui.ScantegrityJFrame;
import org.scantegrity.scanner.BallotImageHandler;
import org.scantegrity.scanner.Scanner;

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
public class PollingPlaceGUI implements Runnable,ActionListener
{
	/* 
	 * Serial Version UID
	 */ 
	private static final long serialVersionUID = -1875275649149039514L;

	/* ***********************************************
	 * Class Variables 
	 ************************************************/
	private Scanner c_scannerRef; 
	
	private ScantegrityJFrame c_frame; 
	private CardLayout c_scannerInfoCardLayout;
	
	//main panels
	private JPanel c_electionInfoPanel; 
	private JPanel c_compactElectionInfoPanel;
	private JPanel c_scannerInfoPanel;
	private JPanel c_infoBarPanel; 
	
	//card panels
	private JPanel c_chiefJudgeLoginPanel;
	private JPanel c_startElectionPanel;
	private JPanel c_scanningBallotsPanel;
	private JPanel c_waitingForBallotsPanel;
	private JPanel c_thankYouPanel;
	private JPanel c_ballotResultsPanel;
	
	//Buttons
	private JButton c_chiefLoginButton;
	private JButton c_startElectionButton;
	private JButton c_castBallotButton;
	private JButton c_rejectBallotButton; 
	
	//Menu 
	private JMenuBar c_menuBar;
	private JMenu c_adminMenu; 
	private JMenuItem c_adminItem; 
	
	//Text Areas
	private JTextArea c_ballotInfoLabel; 
	
	//JScrollPanes 
	private JScrollPane c_ballotInfo;
	
	//Fields
	private JTextField c_castField; 
	private JTextField c_spoiledField;

	//password fields
	private JPasswordField c_chiefPasswordField;
	
	//Font Style
	private String c_fontStyle; 
	
	//TODO Temp INTs
	private Integer c_numCastBallots;
	private Integer c_numSpoiledBallots;
	
	/* ***********************************************
	 * Constructors  
	 ************************************************/
	
	/**
	 * 
	 */
	public PollingPlaceGUI()
	{
		//initalize scanner ref
		c_scannerRef = new Scanner(this);
		
		//TODO: get configuration info
		c_fontStyle = ScannerUIConstants.FONT_STYLE_SERIF;
		
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
	
	public void setToWaiting()
	{
		changeCard(ScannerUIConstants.WAITING_FOR_BALLOT_CARD);
	}
	
	public void displayScanResults(String p_scanResults)
	{
		c_ballotInfoLabel.setText(p_scanResults);
		changeCard(ScannerUIConstants.BALLOT_INFO_CARD);
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
		//c_frame.setFullScreen(); 
		//c_frame.setUndecorated(true);
		c_frame.setDefaultCloseOperation(ScantegrityJFrame.DO_NOTHING_ON_CLOSE);
		//c_frame.setAlwaysOnTop(true);
		c_frame.setPreferredSize(new Dimension(800,600));
		
		//use the larger election info
		buildLayout(false); 
	}
	
	/**
	 * Builds the panels that will be used and 
	 * sets their layouts. 
	 */
	private void buildLayout(boolean useCompactElecInfo)
	{
		//set up frame Layout 
		c_frame.setLayout(new BorderLayout()); 
		
		//set up internal panels
		buildMenuBar();
		buildElectionInfoPanel(); 
		buildScannerPanel(); 
		buildInfoBar(); 
		
		if(useCompactElecInfo)
		{
			c_frame.add(c_compactElectionInfoPanel, BorderLayout.PAGE_START);
		}
		else
		{
			c_frame.add(c_electionInfoPanel, BorderLayout.PAGE_START);
		}
		
		c_frame.add(c_scannerInfoPanel, BorderLayout.CENTER);
		c_frame.add(c_infoBarPanel, BorderLayout.PAGE_END);
		
	}
	
	/* *************************************************
	 * Panel Build Methods
	 **************************************************/
	
	/**
	 * This method builds the top panel 
	 * that holds the admin buttons
	 */
	private void buildMenuBar()
	{
		//This will now be a menu bar
		c_menuBar = new JMenuBar(); 
		c_adminMenu = new JMenu("Administration");
		c_adminItem = new JMenuItem("Enter Administration");
		c_adminItem.addActionListener(this);
		
		c_adminMenu.add(c_adminItem);
		c_menuBar.add(c_adminMenu);
		
		c_frame.setJMenuBar(c_menuBar);
	}
	
	/**
	 * This builds the Election info panel that will 
	 * display the election information to the user. 
	 * until the election is started.  
	 */
	private void buildElectionInfoPanel()
	{
		//build the card panels
		buildExtendedElectionInfoPanel();
		buildCompactElectionInfoPanel();
	}
		
	private void buildExtendedElectionInfoPanel()
	{
		c_electionInfoPanel = new JPanel(); 
		c_electionInfoPanel.setLayout(new GridBagLayout());
		
		JLabel l_ppIdLabel = new JLabel("Polling Place ID #: ");
		JLabel l_ppNameLabel = new JLabel("Polling Place Name: ");
		JLabel l_ppLocLabel = new JLabel("Polling Place Location: ");
		JLabel l_elecDateLabel = new JLabel("Date of Election: ");
		JLabel l_elecStartTimeLabel = new JLabel("Start Time of Election: ");
		JLabel l_chiefJudgeLabel = new JLabel("Chief Judge: ");
		
		//TODO: These will be filled in by the configuration interface
		JLabel l_ppId = new JLabel("0001");
		JLabel l_ppName = new JLabel("Polling Place");
		JLabel l_ppLoc = new JLabel("Catonsville, MD");
		JLabel l_elecDate = new JLabel("01/01/2009");
		JLabel l_elecStartTime = new JLabel("07:00");
		JLabel l_chiefJudge = new JLabel("John Smith");
		
		//Font
		//TODO: Eventually I want to make the font size determined by the 
		//screen resolution
		Font l_font = new Font(c_fontStyle, Font.BOLD, 20);
		
		l_ppIdLabel.setFont(l_font);
		l_ppNameLabel.setFont(l_font);
		l_ppLocLabel.setFont(l_font);
		l_elecDateLabel.setFont(l_font);
		l_elecStartTimeLabel.setFont(l_font);
		l_chiefJudgeLabel.setFont(l_font);
		l_ppId.setFont(l_font);
		l_ppName.setFont(l_font);
		l_ppLoc.setFont(l_font);
		l_elecDate.setFont(l_font);
		l_elecStartTime.setFont(l_font);
		l_chiefJudge.setFont(l_font);
		

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
		 c_electionInfoPanel.add(l_ppIdLabel, l_c);
		 
		 l_c.gridx = 1;
		 l_c.gridy = 2; 
		 l_c.anchor = GridBagConstraints.LINE_START;
		 l_c.insets = l_insets; 
		 c_electionInfoPanel.add(l_ppId, l_c);
		
		 //Row 2
		 l_c.gridx = 0;
		 l_c.gridy = 3; 
		 l_c.anchor = GridBagConstraints.LINE_START;
		 l_c.insets = l_insets; 
		 c_electionInfoPanel.add(l_ppNameLabel, l_c);
		 
		 l_c.gridx = 1;
		 l_c.gridy = 3;
		 l_c.anchor = GridBagConstraints.LINE_START;
		 l_c.insets = l_insets; 
		 c_electionInfoPanel.add(l_ppName, l_c);
		 
		 //Row 3
		 l_c = new GridBagConstraints();
		 l_c.gridx = 0; 
		 l_c.gridy = 4; 
		 l_c.anchor = GridBagConstraints.LINE_START;
		 l_c.insets = l_insets; 
		 c_electionInfoPanel.add(l_ppLocLabel, l_c);
		 
		 l_c.gridx = 1;
		 l_c.gridy = 4; 
		 l_c.anchor = GridBagConstraints.LINE_START;
		 l_c.insets = l_insets; 
		 c_electionInfoPanel.add(l_ppLoc, l_c);
		
		 //Row 4
		 l_c = new GridBagConstraints();
		 l_c.gridx = 0; 
		 l_c.gridy = 5; 
		 l_c.anchor = GridBagConstraints.LINE_START;
		 l_c.insets = l_insets; 
		 c_electionInfoPanel.add(l_elecDateLabel, l_c);
		 
		 l_c.gridx = 1;
		 l_c.gridy = 5;
		 l_c.anchor = GridBagConstraints.LINE_START;
		 l_c.insets = l_insets; 
		 c_electionInfoPanel.add(l_elecDate, l_c);
		
		 //Row 5
		 l_c.gridx = 0;
		 l_c.gridy = 6; 
		 l_c.anchor = GridBagConstraints.LINE_START;
		 l_c.insets = l_insets; 
		 c_electionInfoPanel.add(l_elecStartTimeLabel, l_c);
		 
		 l_c.gridx = 1;
		 l_c.gridy = 6; 
		 l_c.anchor = GridBagConstraints.LINE_START;
		 l_c.insets = l_insets; 
		 c_electionInfoPanel.add(l_elecStartTime, l_c);
		 
		 //Row 6
		 l_c = new GridBagConstraints();
		 l_c.gridx = 0; 
		 l_c.gridy = 7; 
		 l_c.anchor = GridBagConstraints.LINE_START;
		 l_c.insets = l_insets; 
		 c_electionInfoPanel.add(l_chiefJudgeLabel, l_c);
		 
		 l_c.gridx = 1;
		 l_c.gridy = 7; 
		 l_c.anchor = GridBagConstraints.LINE_START;
		 l_c.insets = l_insets;
		 c_electionInfoPanel.add(l_chiefJudge, l_c);
		 
		 //add the Border
		 Border l_border = BorderFactory.createLoweredBevelBorder(); 
		 c_electionInfoPanel.setBorder(l_border);
	}
	
	/**
	 * This builds the compacted election information 
	 * that will be displayed after the election has started. 
	 */
	private void buildCompactElectionInfoPanel()
	{
		c_compactElectionInfoPanel = new JPanel(); 
		c_compactElectionInfoPanel.setLayout(new GridBagLayout());
		
		JLabel l_ppIdLabel = new JLabel("Polling Place ID #: ");
		JLabel l_ppNameLabel = new JLabel("Polling Place Name: ");
		JLabel l_ppLocLabel = new JLabel("Polling Place Location: ");
		JLabel l_elecDateLabel = new JLabel("Date of Election: ");
		JLabel l_elecStartTimeLabel = new JLabel("Start Time of Election: ");
		JLabel l_chiefJudgeLabel = new JLabel("Chief Judge: ");
		
		//TODO: These will be filled in by the configuration interface
		JLabel l_ppId = new JLabel("0001");
		JLabel l_ppName = new JLabel("Polling Place");
		JLabel l_ppLoc = new JLabel("Catonsville, MD");
		JLabel l_elecDate = new JLabel("01/01/2009");
		JLabel l_elecStartTime = new JLabel("07:00");
		JLabel l_chiefJudge = new JLabel("John Smith");
		
		
		//Set fonts
		//Font
		Font l_font = new Font(c_fontStyle, Font.BOLD, 12);
		
		l_ppIdLabel.setFont(l_font);
		l_ppNameLabel.setFont(l_font);
		l_ppLocLabel.setFont(l_font);
		l_elecDateLabel.setFont(l_font);
		l_elecStartTimeLabel.setFont(l_font);
		l_chiefJudgeLabel.setFont(l_font);
		l_ppId.setFont(l_font);
		l_ppName.setFont(l_font);
		l_ppLoc.setFont(l_font);
		l_elecDate.setFont(l_font);
		l_elecStartTime.setFont(l_font);
		l_chiefJudge.setFont(l_font);
		
		
		//Insets 
		//the first inset is for a larger horizontal gap in between 
		//labels that are not connected
		Insets l_insets = new Insets(0, 0, 10, 30);
		Insets l_closerInsets = new Insets(0, 0, 10, 10);
		
		//add to layout
		GridBagConstraints l_c;
		
		//Row 1
		 l_c = new GridBagConstraints();
		 l_c.gridx = 0; 
		 l_c.gridy = 0; 
		 l_c.anchor = GridBagConstraints.LINE_START;
		 l_c.insets = l_closerInsets; 
		 c_compactElectionInfoPanel.add(l_ppIdLabel, l_c);
		 
		 l_c.gridx = 1;
		 l_c.gridy = 0; 
		 l_c.anchor = GridBagConstraints.LINE_START;
		 l_c.insets = l_insets; 
		 c_compactElectionInfoPanel.add(l_ppId, l_c);

		 l_c.gridx = 2;
		 l_c.gridy = 0; 
		 l_c.anchor = GridBagConstraints.LINE_START;
		 l_c.insets = l_closerInsets; 
		 c_compactElectionInfoPanel.add(l_ppNameLabel, l_c);
		 
		 l_c.gridx = 3;
		 l_c.gridy = 0;
		 l_c.anchor = GridBagConstraints.LINE_START;
		 l_c.insets = l_insets; 
		 c_compactElectionInfoPanel.add(l_ppName, l_c);
		 
		 l_c = new GridBagConstraints();
		 l_c.gridx = 4; 
		 l_c.gridy = 0; 
		 l_c.anchor = GridBagConstraints.LINE_START;
		 l_c.insets = l_closerInsets; 
		 c_compactElectionInfoPanel.add(l_ppLocLabel, l_c);
		 
		 l_c.gridx = 5;
		 l_c.gridy = 0; 
		 l_c.anchor = GridBagConstraints.LINE_START;
		 l_c.insets = l_insets; 
		 c_compactElectionInfoPanel.add(l_ppLoc, l_c);
		
		 //Row 2
		 l_c = new GridBagConstraints();
		 l_c.gridx = 0; 
		 l_c.gridy = 1; 
		 l_c.anchor = GridBagConstraints.LINE_START;
		 l_c.insets = l_closerInsets; 
		 c_compactElectionInfoPanel.add(l_elecDateLabel, l_c);
		 
		 l_c.gridx = 1;
		 l_c.gridy = 1;
		 l_c.anchor = GridBagConstraints.LINE_START;
		 l_c.insets = l_insets; 
		 c_compactElectionInfoPanel.add(l_elecDate, l_c);
		
		 l_c.gridx = 2;
		 l_c.gridy = 1; 
		 l_c.anchor = GridBagConstraints.LINE_START;
		 l_c.insets = l_closerInsets; 
		 c_compactElectionInfoPanel.add(l_elecStartTimeLabel, l_c);
		 
		 l_c.gridx = 3;
		 l_c.gridy = 1; 
		 l_c.anchor = GridBagConstraints.LINE_START;
		 l_c.insets = l_insets; 
		 c_compactElectionInfoPanel.add(l_elecStartTime, l_c);

		 l_c.gridx = 4; 
		 l_c.gridy = 1; 
		 l_c.anchor = GridBagConstraints.LINE_START;
		 l_c.insets = l_closerInsets;
		 c_compactElectionInfoPanel.add(l_chiefJudgeLabel, l_c);
		 
		 l_c.gridx = 5;
		 l_c.gridy = 1; 
		 l_c.anchor = GridBagConstraints.LINE_START;
		 l_c.insets = l_insets;
		 c_compactElectionInfoPanel.add(l_chiefJudge, l_c);
		 
		//add the Border
		Border l_border = BorderFactory.createLoweredBevelBorder(); 
		c_compactElectionInfoPanel.setBorder(l_border);
		
		c_compactElectionInfoPanel.setForeground(Color.WHITE);
		c_compactElectionInfoPanel.setBackground(null);
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
		buildThankYouCardPanel();
		buildBallotResultsCards("<html><p align=\"center\">No Ballot Results to Display</p></html>");
		
		c_scannerInfoCardLayout = new CardLayout();
		c_scannerInfoPanel.setLayout(c_scannerInfoCardLayout);
		
		c_scannerInfoPanel.add(c_chiefJudgeLoginPanel, ScannerUIConstants.LOGIN_CARD);
		c_scannerInfoPanel.add(c_startElectionPanel, ScannerUIConstants.START_ELECTION_CARD);
		c_scannerInfoPanel.add(c_scanningBallotsPanel, ScannerUIConstants.SCANNING_BALLOT_CARD);
		c_scannerInfoPanel.add(c_waitingForBallotsPanel, ScannerUIConstants.WAITING_FOR_BALLOT_CARD);
		c_scannerInfoPanel.add(c_thankYouPanel, ScannerUIConstants.THANK_YOU_CARD);
		c_scannerInfoPanel.add(c_ballotResultsPanel, ScannerUIConstants.BALLOT_INFO_CARD);
		
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
		
		c_numCastBallots = new Integer(0); 
		c_numSpoiledBallots = new Integer(0);
		
		c_castField = new JTextField(c_numCastBallots.toString());
		c_castField.setEditable(false);
		c_castField.setFocusable(false);
		
		c_spoiledField = new JTextField(c_numSpoiledBallots.toString()); 
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
		l_passwordPanel.add(c_chiefLoginButton);
		
		c_chiefJudgeLoginPanel.add(l_passwordPanel);
		//c_chiefJudgeLoginPanel.add(c_chiefLoginButton);
	}
	
	private void buildStartCardPanel()
	{
		c_startElectionPanel = new JPanel(); 
		
		c_startElectionButton = new JButton(); 
		c_startElectionButton.setText("Start Election");
		c_startElectionButton.setFocusable(false);
		c_startElectionButton.addActionListener(this);
		c_startElectionButton.setFont(new Font(c_fontStyle, Font.BOLD, 24));	
		c_startElectionPanel.add(c_startElectionButton);
	}
	
	private void buildScanningCardPanel()
	{
		c_scanningBallotsPanel = new JPanel(); 
		
		JLabel l_label = new JLabel("Scanning Ballot...");
		l_label.setFont(new Font(c_fontStyle, Font.BOLD, 24));
		
		c_scanningBallotsPanel.add(l_label);
		
		//colors
		c_scanningBallotsPanel.setBackground(Color.WHITE);
		c_scanningBallotsPanel.setForeground(Color.BLACK);
	}
	
	private void buildWaitingCardPanel()
	{
		c_waitingForBallotsPanel = new JPanel(); 
		
		JLabel l_label = new JLabel("Waiting for Ballot...");
		l_label.setFont(new Font(c_fontStyle, Font.BOLD, 24));
		
		c_waitingForBallotsPanel.add(l_label);
		
		//colors
		c_waitingForBallotsPanel.setBackground(Color.WHITE);
		c_waitingForBallotsPanel.setForeground(Color.BLACK);
	}
	
	private void buildThankYouCardPanel()
	{
		c_thankYouPanel = new JPanel(); 
		
		JLabel l_label = new JLabel("Thank you for Voting.");
		l_label.setFont(new Font(c_fontStyle, Font.BOLD, 24));
		
		c_thankYouPanel.add(l_label);
		
		//colors
		c_thankYouPanel.setBackground(Color.WHITE);
		c_thankYouPanel.setForeground(Color.BLACK);
	}
	
	private void buildBallotResultsCards(String p_htmlBallotInfo)
	{
		c_ballotResultsPanel = new JPanel(new BorderLayout());
		c_ballotInfoLabel = new JTextArea(p_htmlBallotInfo);
		c_ballotInfoLabel.setFont(new Font(c_fontStyle, Font.BOLD, 16));
		c_ballotInfo = new JScrollPane(c_ballotInfoLabel);
		
		c_ballotResultsPanel.add(c_ballotInfoLabel, BorderLayout.CENTER);
		
		c_castBallotButton = new JButton();
		c_castBallotButton.setText("Cast Ballot");
		c_castBallotButton.setFocusable(false);
		c_castBallotButton.addActionListener(this);
		
		c_rejectBallotButton = new JButton(); 
		c_rejectBallotButton.setText("Reject Ballot");
		c_rejectBallotButton.setFocusable(false);
		c_rejectBallotButton.addActionListener(this); 
		
		JPanel l_tmpPanel = new JPanel();
		BoxLayout l_bl = new BoxLayout(l_tmpPanel, BoxLayout.X_AXIS);
		l_tmpPanel.setLayout(l_bl);
		
		l_tmpPanel.add(c_castBallotButton);
		l_tmpPanel.add(Box.createRigidArea(new Dimension(c_frame.getSize().width - (2 * c_castBallotButton.getSize().width) - 10,10)));
		l_tmpPanel.add(c_rejectBallotButton);
		
		c_ballotResultsPanel.add(l_tmpPanel, BorderLayout.PAGE_END);
		
		//colors
		c_ballotResultsPanel.setBackground(Color.WHITE);
		c_ballotResultsPanel.setForeground(Color.BLACK);
		
		c_ballotResultsPanel.revalidate();
		
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
	
	private char[] showJudgePasswordDialog()
	{
		JPanel l_panel = new JPanel();
		l_panel.setLayout(new BoxLayout(l_panel, BoxLayout.Y_AXIS));
	
		JLabel l_label = new JLabel("Please enter Judge Pin");
		l_panel.add(l_label);
		JPasswordField l_passField = new JPasswordField(ScannerUIConstants.NUM_PASSWORD_PIN_COLUMNS);
		l_panel.add(l_passField);
		
		int l_selected = JOptionPane.showConfirmDialog(c_frame, 
				                                       l_panel, 
				                                       "Enter Judge Pin", 
				                                       JOptionPane.OK_CANCEL_OPTION);
		
		if(l_selected != JOptionPane.OK_OPTION)
			return null;
		
		return l_passField.getPassword();
	}
	
	private char[] showChiefJudgePasswordDialog()
	{
		JPanel l_panel = new JPanel();
		l_panel.setLayout(new BoxLayout(l_panel, BoxLayout.Y_AXIS));
	
		JLabel l_label = new JLabel("Please enter Chief Judge Password");
		l_panel.add(l_label);
		JPasswordField l_passField = new JPasswordField(ScannerUIConstants.NUM_PASSWORD_COLUMNS);
		l_panel.add(l_passField);
		
		int l_selected = JOptionPane.showConfirmDialog(c_frame, 
													   l_panel, 
													   "Enter Chief Judge Password", 
													   JOptionPane.OK_CANCEL_OPTION);
		
		if(l_selected != JOptionPane.OK_OPTION)
			return null;
		
		return l_passField.getPassword();
	}
	
	private boolean getJudgeAuthorization()
	{
		char[] l_pass = showJudgePasswordDialog();
		
		if(l_pass == null)
			return false;
		
		boolean l_validated = false; 
		
		if((new String(l_pass)).equals("1234"))
			l_validated = true;
		
		if(l_validated)
		{
			return true;
		}
		else
		{
			Dialogs.displayWarningDialog("Incorrect Judge Pin");
			return getJudgeAuthorization();  
		}
	}
	
	private boolean getChiefJudgeAuthorization()
	{
		char[] l_pass = showChiefJudgePasswordDialog();
		
		if(l_pass == null)
			return false;
		
		boolean l_validated = false; 
		
		if((new String(l_pass)).equals("test"))
				l_validated = true;
		
		if(l_validated)
		{
			return true;
		}
		else
		{
			Dialogs.displayWarningDialog("Incorrect Password");
			return false; 
		}
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
		if(e.getActionCommand().equals(c_adminItem.getText()))
		{
			boolean l_isAuthorized = getChiefJudgeAuthorization();
			
			if(!l_isAuthorized)
				return; 
			
			int l_choice = showAdminDialogBox();
			
			if(l_choice == 1)
			{
				c_frame.dispose();
				System.exit(1);
			}
		}		
		else if(e.getActionCommand().equals(c_startElectionButton.getText()))
		{
			c_scannerRef.startElection();
			
			c_frame.setVisible(false);
			
			c_frame.remove(c_electionInfoPanel);
			c_frame.remove(c_scannerInfoPanel);
			c_frame.add(c_compactElectionInfoPanel, BorderLayout.PAGE_START);
			c_frame.add(c_scannerInfoPanel, BorderLayout.CENTER);
			
			c_frame.setVisible(true);
			
			changeCard(ScannerUIConstants.WAITING_FOR_BALLOT_CARD);

		}
		else if(e.getActionCommand().equals(c_chiefLoginButton.getText()))
		{
			//check for login based on boolean 
			boolean l_validated = true; 
			
			if(l_validated)
			{
				
				changeCard(ScannerUIConstants.START_ELECTION_CARD);
			}
			else
			{
				changeCard(ScannerUIConstants.LOGIN_CARD);
			
				Dialogs.displayWarningDialog("Incorrect Password. Try again.");
			}
		}
		else if(e.getActionCommand().equals(c_castBallotButton.getText()))
		{
			//cast the ballot
			c_numCastBallots++;
			c_castField.setText(c_numCastBallots.toString());
			
			//display thank you 
			
			//wait then display waiting for ballots
			changeCard(ScannerUIConstants.THANK_YOU_CARD);
		}
		else if(e.getActionCommand().equals(c_rejectBallotButton.getText()))
		{
			//reject the ballot
			//bring up judge authorization dialog
			boolean l_isValidated = getJudgeAuthorization();
			
			if(l_isValidated)
			{
				c_numSpoiledBallots++;
				c_spoiledField.setText(c_numSpoiledBallots.toString());
			}
			
			//wait then display waiting for ballots
			changeCard(ScannerUIConstants.WAITING_FOR_BALLOT_CARD);
		}
	}
}
