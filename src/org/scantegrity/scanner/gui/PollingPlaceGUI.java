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
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.print.PrinterException;
import java.io.IOException;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.Border;

import org.scantegrity.common.gui.Dialogs;
import org.scantegrity.common.gui.ScantegrityJFrame;
import org.scantegrity.lib.Ballot;
import org.scantegrity.scanner.BallotHandler;
import org.scantegrity.scanner.Scanner;
import org.scantegrity.scanner.ScannerConfig;


/**
 * @author John Conway 
 * 
 * This class builds the Scanner GUI. The main body of the gui that 
 * holds start election button, and the scan results is set using a card layout. 
 *  
 *  When a ballot is scanned, the BallotImageHandler calls addBallotResults to 
 *  add the String results to the ballot queue. If the queue contains only one 
 *  ballot it will display results, otherwise it will wait for the ballot currently
 *  being displayed to be cast or rejected. 
 *  
 *  If a ballot has been overvoted or rejected, a judge pin authorization is 
 *  required.
 *  
 *  To bring up the admin interface, press and hold the mouse (or finger) over 
 *  the election information at the top of the screen for 1 second then release. 
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
	
	//Class References
	private Scanner c_scannerRef; 
	private ScannerConfig c_config;
	private BallotHandler c_bhRef;
	
	//command line flags the gui uses
	private boolean c_fullscreen;
	
	//require pin flag
	private Vector<Boolean> c_reqPin;
	private Vector<Boolean> c_reqPinOnReject;
	
	//election started flag
	private boolean c_isElectionStarted = false; 
	
	//The thread that controls the Thank You card wait time
	private Thread c_thread;
	
	//Frames
	private ScantegrityJFrame c_frame; 
	
	//Layouts
	private CardLayout c_scannerInfoCardLayout;
	
	//main panels
	private JPanel c_electionInfoPanel; 
	private JPanel c_compactElectionInfoPanel;
	private JPanel c_scannerInfoPanel;
	private JPanel c_infoBarPanel; 
	
	//card panels
	private JPanel c_startElectionPanel;
	//private JPanel c_scanningBallotsPanel;
	//private JPanel c_waitingForBallotsPanel;
	//private JPanel c_thankYouPanel;
	//private JPanel c_rejectedPanel;
	private JPanel c_ballotResultsPanel;
	
	//Buttons
	private JButton c_startElectionButton;
	private JButton c_castBallotButton;
	private JButton c_rejectBallotButton; 
	private JButton c_closeButton;
	private JButton c_printButton;
	
	//Text Areas
	private JTextPane c_ballotResultsTextPane; 
	private JTextPane c_ballotSummaryTextPane;
	
	//JScrollPanes 
	private JScrollPane c_ballotResultsScrollPanel;
	
	//Fields
	private JTextField c_castField; 
	private JTextField c_spoiledField;
	
	//Font Style
	private String c_fontStyle; 
	
	//Scanned Ballots Queue
	private Vector<String> c_ballotResultsQueue;
	private Vector<Ballot> c_ballotQueue;
	
	//TODO Temp INTs
	private Integer c_numCastBallots;
	private Integer c_numSpoiledBallots;
	
	
	
	
	/* ***********************************************
	 * Constructors  
	 ************************************************/
	
	
	/**
	 * Constructor 
	 * @param p_config : Reference to the ScannerConfig Object. 
	 * @param p_fullscreen : boolean sets if the gui to be fullscreen
	 */
	public PollingPlaceGUI(ScannerConfig p_config, boolean p_fullscreen)
	{
		//is the gui going to be fullscreen
		c_fullscreen = p_fullscreen;
		
		//set config reference
		c_config = p_config;
		
		//initalize scanner ref
		c_scannerRef = new Scanner(this);
		
		//initialize scanned ballot queue
		c_ballotResultsQueue = new Vector<String>();
		c_ballotQueue = new Vector<Ballot>();
		c_reqPin = new Vector<Boolean>();
		c_reqPinOnReject = new Vector<Boolean>(); 
		
		//set the font style
		c_fontStyle = ScannerUIConstants.FONT_STYLE_SERIF;
		
		//initialize the gui
		guiInit();
	}

	/* ***********************************************
	 * Runnable Interface Methods 
	 ************************************************/
	
	
	/**
	 * Runs the GUI thread 
	 */
	public void run()
	{
		changeCard(ScannerUIConstants.START_ELECTION_CARD);
		
		//display the frame
		c_frame.display(true);
	}
	
	
	/* ***********************************************
	 * Public Methods to change status
	 ************************************************/
	
	/**
	 * Sets the ballot information panel to show "Scanning Ballot"
	 */
	public void setToScanningBallot()
	{
		changeCard(ScannerUIConstants.SCANNING_BALLOT_CARD);
	}
	
	
	/**
	 * Sets the ballot information panel to show "Waiting for Ballots"
	 */
	public void setToWaiting()
	{
		if(c_ballotResultsQueue.size() > 0)
			displayScanResults(c_ballotResultsQueue.get(0));
		else
			changeCard(ScannerUIConstants.WAITING_FOR_BALLOT_CARD);
	}
	
	
	/**
	 * Adds the ballot results string to the queue to display to the user
	 * 
	 * @param p_results The html formatted results string
	 * @param p_ballot the ballot object
	 */
	public void addBallotResults(String p_results, Ballot p_ballot, boolean p_reqPin)
	{
		c_reqPin.add(p_reqPin); 
		c_reqPinOnReject.add(true);
		
		c_ballotResultsQueue.add(p_results);
		c_ballotQueue.add(p_ballot);
		
		if(c_ballotQueue.size() == 1)
			displayScanResults(c_ballotResultsQueue.get(0));
	}
	
	public void displaySummaryInfo(String p_htmlSummary)
	{
		c_ballotSummaryTextPane.setText(p_htmlSummary);
		changeCard(ScannerUIConstants.ELECTION_SUMMARY_CARD);
	}
	
	/**
	 * Sets the ballot information panel to display the scan results
	 * was public but now isn't, cause I said so
	 * 
	 * @param p_scanResults The HTML formatted results string
	 */
	private void displayScanResults(String p_scanResults)
	{
		c_ballotResultsTextPane.setText(p_scanResults);
		changeCard(ScannerUIConstants.BALLOT_INFO_CARD);
	}

	public void setBallotHandlerRef(BallotHandler p_bhRef)
	{
		c_bhRef = p_bhRef;
	}
	
	
	/* ***********************************************
	 * GUI Build Methods
	 ************************************************/
	
	
	/**
	 * Initializes the frame and panels, then calls buildLayout()
	 */
	private void guiInit()
	{
		
		//create frame and set options
		c_frame = new ScantegrityJFrame("Polling Place");
		c_frame.setDefaultCloseOperation(ScantegrityJFrame.DO_NOTHING_ON_CLOSE);
		c_frame.setPreferredSize(new Dimension(1024,768));
		
		if(c_fullscreen)
		{
			c_frame.setFullScreen();
			//c_frame.setAlwaysOnTop(true);
		}
		
		//build the layout using the larger election info
		buildLayout(false); 
	}
	
	
	/**
	 * Builds the panels that will be used and 
	 * sets their layouts. 
	 * 
	 * @param p_useCompactElecInfo sets whether or not to use the smaller 
	 * 			election information panel 
	 */
	private void buildLayout(boolean p_useCompactElecInfo)
	{
		//set up frame Layout 
		c_frame.getContentPane().setLayout(new BorderLayout()); 
		
		//set up internal panels
		buildElectionInfoPanel(); 
		buildScannerPanel(); 
		buildInfoBar(); 
		
		
		//add the panels to the frame
		if(p_useCompactElecInfo)
		{
			c_frame.getContentPane().add(c_compactElectionInfoPanel, BorderLayout.PAGE_START);
		}
		else
		{
			c_frame.getContentPane().add(c_electionInfoPanel, BorderLayout.PAGE_START);
		}
		
		c_frame.getContentPane().add(c_scannerInfoPanel, BorderLayout.CENTER);
		c_frame.getContentPane().add(c_infoBarPanel, BorderLayout.PAGE_END);
		
	}
	
	/* *************************************************
	 * Panel Build Methods
	 * 
	 * The methods that will build the panels themselves
	 **************************************************/
	
	
	/**
	 * This builds the Election info panel that will 
	 * display the election information to the user. 
	 */
	private void buildElectionInfoPanel()
	{
		//build the card panels
		buildExtendedElectionInfoPanel();
		buildCompactElectionInfoPanel();
	}
		
	
	/**
	 * Builds the initial large election information panel 
	 * This panel displays until the start of the election
	 */
	private void buildExtendedElectionInfoPanel()
	{
		//set up the panel
		c_electionInfoPanel = new JPanel(); 
		c_electionInfoPanel.setLayout(new GridBagLayout());
		
		//this mouse listener is for bringing up the admin interface
		c_electionInfoPanel.addMouseListener(new mouseListener());
		
		//create the information labels
		JLabel l_ppIdLabel = new JLabel("Polling Place ID #: ");
		JLabel l_ppNameLabel = new JLabel("Polling Place Name: ");
		JLabel l_ppLocLabel = new JLabel("Polling Place Location: ");
		JLabel l_elecDateLabel = new JLabel("Date of Election: ");
		JLabel l_elecStartTimeLabel = new JLabel("Start Time of Election: ");
		JLabel l_chiefJudgeLabel = new JLabel("Chief Judge: ");
		
		//create the information labels with info from config files
		JLabel l_ppId = new JLabel(new Integer(c_config.getPollID()).toString());
		JLabel l_ppName = new JLabel(c_config.getName());
		JLabel l_ppLoc = new JLabel(c_config.getLocation());
		JLabel l_elecDate = new JLabel(c_config.getDate());
		JLabel l_elecStartTime = new JLabel(c_config.getTime());
		JLabel l_chiefJudge = new JLabel(c_config.getChiefJudges().firstElement());
		
		//set up font
		Font l_font = new Font(c_fontStyle, Font.BOLD, ScannerUIConstants.MEDIUM_TEXT_FONT_SIZE);
		
		//add the fonts to all the labels
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
		
		//Set up the gridbag constraints
		GridBagConstraints l_c;
		
		//configure the grid and add each label to the layout
		
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
		c_compactElectionInfoPanel.addMouseListener(new mouseListener());
		
		JLabel l_ppIdLabel = new JLabel("Polling Place ID #: ");
		JLabel l_ppNameLabel = new JLabel("Polling Place Name: ");
		JLabel l_ppLocLabel = new JLabel("Polling Place Location: ");
		JLabel l_elecDateLabel = new JLabel("Date of Election: ");
		JLabel l_elecStartTimeLabel = new JLabel("Start Time of Election: ");
		JLabel l_chiefJudgeLabel = new JLabel("Chief Judge: ");
		
		JLabel l_ppId = new JLabel(new Integer(c_config.getPollID()).toString());
		JLabel l_ppName = new JLabel(c_config.getName());
		JLabel l_ppLoc = new JLabel(c_config.getLocation());
		JLabel l_elecDate = new JLabel(c_config.getDate());
		JLabel l_elecStartTime = new JLabel(c_config.getTime());
		JLabel l_chiefJudge = new JLabel(c_config.getChiefJudges().firstElement());
		
		
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
		
		 //Row 2
		 l_c = new GridBagConstraints();
		 l_c.gridx = 0; 
		 l_c.gridy = 1; 
		 l_c.anchor = GridBagConstraints.LINE_START;
		 l_c.insets = l_closerInsets; 
		 c_compactElectionInfoPanel.add(l_ppLocLabel, l_c);
		 
		 l_c.gridx = 1;
		 l_c.gridy = 1; 
		 l_c.anchor = GridBagConstraints.LINE_START;
		 l_c.insets = l_insets; 
		 c_compactElectionInfoPanel.add(l_ppLoc, l_c);
		
		 l_c = new GridBagConstraints();
		 l_c.gridx = 2; 
		 l_c.gridy = 1; 
		 l_c.anchor = GridBagConstraints.LINE_START;
		 l_c.insets = l_closerInsets; 
		 c_compactElectionInfoPanel.add(l_elecDateLabel, l_c);
		 
		 l_c.gridx = 3;
		 l_c.gridy = 1;
		 l_c.anchor = GridBagConstraints.LINE_START;
		 l_c.insets = l_insets; 
		 c_compactElectionInfoPanel.add(l_elecDate, l_c);
		
		 //Row 3
		 l_c.gridx = 0;
		 l_c.gridy = 2; 
		 l_c.anchor = GridBagConstraints.LINE_START;
		 l_c.insets = l_closerInsets; 
		 c_compactElectionInfoPanel.add(l_elecStartTimeLabel, l_c);
		 
		 l_c.gridx = 1;
		 l_c.gridy = 2; 
		 l_c.anchor = GridBagConstraints.LINE_START;
		 l_c.insets = l_insets; 
		 c_compactElectionInfoPanel.add(l_elecStartTime, l_c);

		 l_c.gridx = 2; 
		 l_c.gridy = 2; 
		 l_c.anchor = GridBagConstraints.LINE_START;
		 l_c.insets = l_closerInsets;
		 c_compactElectionInfoPanel.add(l_chiefJudgeLabel, l_c);
		 
		 l_c.gridx = 3;
		 l_c.gridy = 2; 
		 l_c.anchor = GridBagConstraints.LINE_START;
		 l_c.insets = l_insets;
		 c_compactElectionInfoPanel.add(l_chiefJudge, l_c);
		 
		//add the Border
		Border l_border = BorderFactory.createLoweredBevelBorder(); 
		c_compactElectionInfoPanel.setBorder(l_border);
		
		c_compactElectionInfoPanel.setForeground(Color.WHITE);
		c_compactElectionInfoPanel.setBackground(null);
	}
	
	
	/**
	 * Builds the main panels for the card layout.
	 * The panels included are the following: 
	 * 1. Initial Chief Judge Login
	 * 2. Start Election Button Panel
	 * 2. Waiting for Ballots
	 * 3. Scanning Ballots
	 * 4. Thank you For Voting
	 * 5. The ballots Results
	 */
	private void buildScannerPanel()
	{
		//initialize main panel
		c_scannerInfoPanel = new JPanel(); 
		
		//build the panels
		buildStartCardPanel();
		buildBallotResultsCards("<html><p align=\"center\">No Ballot Results to Display</p></html>");
		
		//set up the card layout
		c_scannerInfoCardLayout = new CardLayout();
		c_scannerInfoPanel.setLayout(c_scannerInfoCardLayout);
		
		//add the panels to the card layout
		c_scannerInfoPanel.add(c_startElectionPanel, ScannerUIConstants.START_ELECTION_CARD);
		c_scannerInfoPanel.add(c_ballotResultsPanel, ScannerUIConstants.BALLOT_INFO_CARD);
		buildCardPanels(ScannerUIConstants.SCANNING_BALLOT_CARD);
		buildCardPanels(ScannerUIConstants.WAITING_FOR_BALLOT_CARD);
		buildCardPanels(ScannerUIConstants.THANK_YOU_CARD);
		buildCardPanels(ScannerUIConstants.REJECT_CARD);
		buildSummaryScreens();
		
		//Borders
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
	
	private void buildCardPanels(String p_cardName)
	{
		//create panel
		JPanel l_tmp = new JPanel(); 
		
		//create label and set font
		JLabel l_label = new JLabel(p_cardName);
		l_label.setFont(new Font(c_fontStyle, Font.BOLD, ScannerUIConstants.GIANT_TEXT_FONT_SIZE));
		l_label.setVerticalAlignment(JLabel.CENTER);
		
		//add to panel
		l_tmp.add(l_label);
		
		//set background and foreground colors
		l_tmp.setBackground(Color.WHITE);
		l_tmp.setForeground(Color.BLACK);
		
		c_scannerInfoPanel.add(l_tmp, p_cardName);
	}
	
	/**
	 * Builds the Start Election button panel
	 */
	private void buildStartCardPanel()
	{
		//Create Panel
		c_startElectionPanel = new JPanel(); 
		
		//Create Start Election Button
		c_startElectionButton = new JButton(); 
		c_startElectionButton.setText("Start Election");
		c_startElectionButton.setFocusable(false);
		c_startElectionButton.addActionListener(this);
		c_startElectionButton.setFont(new Font(c_fontStyle, Font.BOLD, 30));
		
		//Add button to panel
		c_startElectionPanel.add(c_startElectionButton);
	}
	
	
	/**
	 * Builds the ballot results panel and initializes the results string to 
	 * p_htmlBallotInfo
	 * 
	 * @param p_htmlBallotInfo
	 */
	private void buildBallotResultsCards(String p_htmlBallotInfo)
	{
		//create panel
		c_ballotResultsPanel = new JPanel(new BorderLayout());
		
		//create the JTextPane
		//This can take html formatted strings to display the results. Results
		//are preformatted before being set in the JTextPane
		c_ballotResultsTextPane = new JTextPane();
		c_ballotResultsTextPane.setEditable(false);
		c_ballotResultsTextPane.setFont(new Font(c_fontStyle, Font.PLAIN, ScannerUIConstants.MEDIUM_TEXT_FONT_SIZE));
		c_ballotResultsTextPane.setText(p_htmlBallotInfo);
		c_ballotResultsTextPane.setBackground(Color.WHITE);
		c_ballotResultsTextPane.setContentType("text/html; charset=EUC-JP"); //set to allow html
		
		//puts the results into a scrollpane 
		//only shows scrollbars if results are larger than panel size
		c_ballotResultsScrollPanel = new JScrollPane(c_ballotResultsTextPane);
		
		//add the panel
		c_ballotResultsPanel.add(c_ballotResultsScrollPanel, BorderLayout.CENTER);
		
		//create cast ballot button
		c_castBallotButton = new JButton();
		c_castBallotButton.setText("Cast Ballot");
		c_castBallotButton.setFocusable(false);
		c_castBallotButton.addActionListener(this);
		c_castBallotButton.setFont(new Font(c_fontStyle, Font.BOLD,  ScannerUIConstants.GIANT_TEXT_FONT_SIZE));
		
		//create reject ballot button
		c_rejectBallotButton = new JButton(); 
		c_rejectBallotButton.setText("Reject Ballot");
		c_rejectBallotButton.setFocusable(false);
		c_rejectBallotButton.addActionListener(this); 
		c_rejectBallotButton.setFont(new Font(c_fontStyle, Font.BOLD,  ScannerUIConstants.GIANT_TEXT_FONT_SIZE));
		
		//set up a temp panel to put buttons into 
		JPanel l_tmpPanel = new JPanel();
		l_tmpPanel.setLayout(new BorderLayout());
		
		//put buttons into temp panel
		l_tmpPanel.add(c_castBallotButton, BorderLayout.LINE_START);
		l_tmpPanel.add(c_rejectBallotButton, BorderLayout.LINE_END);
		
		//add the temp buttons panel the main panel
		c_ballotResultsPanel.add(l_tmpPanel, BorderLayout.PAGE_END);
	}
	
	/**
	 * Builds the Start and End Election Summary Screens
	 */
	private void buildSummaryScreens()
	{	
		c_ballotSummaryTextPane = new JTextPane();
		c_ballotSummaryTextPane.setEditable(false);
		c_ballotSummaryTextPane.setFont(new Font(c_fontStyle, Font.PLAIN, ScannerUIConstants.SMALL_TEXT_FONT_SIZE));
		c_ballotSummaryTextPane.setBackground(Color.WHITE);
		c_ballotSummaryTextPane.setContentType("text/html; charset=EUC-JP"); //set to allow html
		c_ballotSummaryTextPane.setText("");
		
		JScrollPane l_sp = new JScrollPane();
		l_sp.add(c_ballotSummaryTextPane);
		
		//create cast ballot button
		c_closeButton = new JButton();
		c_closeButton.setText("Exit");
		c_closeButton.setFocusable(false);
		c_closeButton.addActionListener(this);
		c_closeButton.setFont(new Font(c_fontStyle, Font.BOLD,  ScannerUIConstants.GIANT_TEXT_FONT_SIZE));
		
		//create reject ballot button
		c_printButton = new JButton(); 
		c_printButton.setText("Print Summary");
		c_printButton.setFocusable(false);
		c_printButton.addActionListener(this); 
		c_printButton.setFont(new Font(c_fontStyle, Font.BOLD,  ScannerUIConstants.GIANT_TEXT_FONT_SIZE));
		
		JPanel l_tmp = new JPanel(new BorderLayout());
		l_tmp.add(c_printButton, BorderLayout.LINE_START);
		l_tmp.add(c_closeButton, BorderLayout.LINE_END);
	
		JPanel l_mainPanel = new JPanel(new BorderLayout());
		l_mainPanel.add(l_sp, BorderLayout.CENTER);
		l_mainPanel.add(l_tmp, BorderLayout.PAGE_END);
		
		c_scannerInfoPanel.add(l_mainPanel, ScannerUIConstants.ELECTION_SUMMARY_CARD);
	}
	
	/* ***********************************************
	 * Dialog Boxes
	 * 
	 * These include the Judge login dialogs and admin 
	 * dialog
	 ************************************************/
	
	/**
	 * TODO: Should probably turn this into something other than JOptionPane
	 * 
	 * Displays the Dialog Box giving 2 administration 
	 * options: 
	 * 1. Return 
	 * 2. End Election
	 * 
	 * Return continues election as normal 
	 * End Election will end the election. 
	 * 
	 * @return int The choice selected
	 */
	private int showAdminDialogBox()
	{
		//make a dialog box 
		Object[] l_options = {"Return", "End Election"};
		String l_title = "Administration";
		
		//show dialog box
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
	
	/**
	 * Shows the on screen Keypad for a judge to enter her pin
	 *  
	 * @return String the pin entered
	 */
	private String showJudgePasswordDialog()
	{		
		//create the keypad
		OnScreenKeypad pad = new OnScreenKeypad(c_frame, "Judge Authorization", 480, 480, '*');

		//get the pin from the keypad
		String l_pass = pad.getBuffer();
		
		return l_pass;
	}
	
	/**
	 * Shows the on screen keyboard for the chief judge authorization.
	 * 
	 * @return String the chief judge password
	 */
	private String showChiefJudgePasswordDialog()
	{		
		//create the keyboard
		OnScreenKeyboard l_board = new OnScreenKeyboard(c_frame, "Chief Judge Authorization", true);

		//get the pin from the keypad
		String l_pass = l_board.getBuffer();
		
		return l_pass;
	}
	
	/**
	 * TODO: send to authorization class
	 * @return
	 */
	private boolean getJudgeAuthorization()
	{
		String l_pass = showJudgePasswordDialog();
		
		if(l_pass == null)
			return false;
		
		boolean l_validated = false; 
		
		if(l_pass.equals("1234"))
			l_validated = true;
		
		if(l_validated)
		{
			return true;
		}
		else
		{
			Dialogs.displayWarningDialog("Incorrect Judge Pin", c_frame);
			return false; 
		}
	}
	
	/**
	 * TODO: send to authorization class
	 * @return
	 */
	private boolean getChiefJudgeAuthorization()
	{
		String l_pass = showChiefJudgePasswordDialog();
		
		if(l_pass == null)
			return false;
		
		boolean l_validated = false; 
		
		if(l_pass.equals("test"))
				l_validated = true;
		
		if(l_validated)
		{
			return true;
		}
		else
		{
			Dialogs.displayWarningDialog("Incorrect Password", c_frame);
			return false; 
		}
	}
	
	
	/* ***********************************************
	 * Card Layout Methods
	 ************************************************/
	
	/**
	 * Changes the card to the card name given
	 * 
	 * @param p_cardName The card to change to
	 */
	private void changeCard(String p_cardName)
	{
		c_scannerInfoCardLayout.show(c_scannerInfoPanel, p_cardName);
	}
	
	
	/* ***********************************************
	 * Other Private Methods
	 ************************************************/
	
	/**
	 * Start Election 
	 * 
	 * This will reset the frame to use the compact election info
	 */
	private void startElection()
	{ 	
		if(getChiefJudgeAuthorization())
		{
			c_frame.setVisible(false);
			
			c_frame.remove(c_electionInfoPanel);
			c_frame.remove(c_scannerInfoPanel);
			c_frame.add(c_compactElectionInfoPanel, BorderLayout.PAGE_START);
			c_frame.add(c_scannerInfoPanel, BorderLayout.CENTER);
			
			c_frame.setVisible(true);
	
			changeCard(ScannerUIConstants.WAITING_FOR_BALLOT_CARD);
			
			//tell scanner to start all election threads
			//and run all necessary election startup procedures
			c_scannerRef.startElection(c_config);
			
			c_isElectionStarted = true;
		}
	}
	
	
	/**
	 * Ends the election
	 */
	private void endElection()
	{
		if(c_isElectionStarted)
		{
			try
			{
				c_bhRef.endElection();
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	
	/**
	 * Cast the Ballot
	 */
	private void castBallot()
	{
		boolean l_isValidated = true;
		
		if(c_reqPin.get(0))
			l_isValidated = getJudgeAuthorization();
		
		if(l_isValidated)
		{
			//update cast count
			c_numCastBallots++;
			c_castField.setText(c_numCastBallots.toString());
			
			//display thank you 
			changeCard(ScannerUIConstants.THANK_YOU_CARD);
			
			//cast the ballot
			c_bhRef.castBallot(c_ballotQueue.remove(0));
			
			//remove ballot results from queue
			c_ballotResultsQueue.remove(0);
			c_reqPin.remove(0);
			c_reqPinOnReject.remove(0);
			
			//create a thread to wait while thank you is displayed
			Runnable l_guiThread = new GUIThread(this); 
			c_thread = new Thread(l_guiThread);
			c_thread.start();
		}
	}
	
	
	/**
	 * Reject a ballot
	 */
	private void rejectBallot()
	{
		boolean l_isValidated = true;
		
		if(c_reqPinOnReject.get(0))
			l_isValidated = getJudgeAuthorization();
		
		if(l_isValidated)
		{
			//update spoil count
			c_numSpoiledBallots++;
			c_spoiledField.setText(c_numSpoiledBallots.toString());
		
			changeCard(ScannerUIConstants.REJECT_CARD);
			
			//cast the ballot
			c_bhRef.rejectBallot(c_ballotQueue.remove(0));
			
			//remove ballot results from queue
			c_ballotResultsQueue.remove(0);
			c_reqPin.remove(0);
			c_reqPinOnReject.remove(0);
			
			//create a thread to wait while thank you is displayed
			Runnable l_guiThread = new GUIThread(this); 
			c_thread = new Thread(l_guiThread);
			c_thread.start();
		}

		//else do nothing, validation unsucessful
	}
	
	
	/**
	 * Opens print dialog to print summary reports
	 */
	private void printSummary()
	{
		try
		{
			c_ballotSummaryTextPane.print();
		}
		catch (PrinterException e)
		{
			// TODO Auto-generated catch block
			//e.printStackTrace();
			Dialogs.displayErrorDialog("Could not print Summary. Try again.");
		}
	}
	
	
	/**
	 * Closes the GUI to end election
	 */
	private void closeScanner()
	{
		c_frame.dispose();
		c_scannerRef.endElection();
	}
	
	/* ***********************************************
	 * Action Listener Methods
	 * 
	 * The Action performed Method to handle all Action
	 * Events
	 ************************************************/
	
	/**
	 * Actions to be taken when an Action Even Occurs
	 */
	public void actionPerformed(ActionEvent e)
	{
		if(e.getActionCommand().equals(c_startElectionButton.getText()))
		{
			//Start Election Button Pressed
			startElection(); 
		}
		else if(e.getActionCommand().equals(c_castBallotButton.getText()))
		{
			//cast ballot button has been pressed
			castBallot();
		}
		else if(e.getActionCommand().equals(c_rejectBallotButton.getText()))
		{
			//reject ballot button pressed
			rejectBallot(); 
		}
		else if(e.getActionCommand().equals(c_printButton.getText()))
		{
			printSummary(); 
		}
		else if(e.getActionCommand().equals(c_closeButton.getText()))
		{
			closeScanner(); 
		}
	}
	
	/* ***********************************************
	 * Mouse Listener
	 * 
	 * The mouse listener to bring up the admin dialog
	 ************************************************/
	
	private class mouseListener implements MouseListener
	{
		//holds time to check for correct hold time
		private long c_firstClick = 0; 
		
		/* (non-Javadoc)
		 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
		 */
		@Override
		public void mouseClicked(MouseEvent p_e)
		{
			//no relevance to this use of mouse listener
		}

		/* (non-Javadoc)
		 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
		 */
		@Override
		public void mouseEntered(MouseEvent p_e)
		{
			// no relevance to this use of mouse listener
			
		}

		/* (non-Javadoc)
		 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
		 */
		@Override
		public void mouseExited(MouseEvent p_e)
		{
			// no relevance to this use of mouse listener
			
		}

		/* (non-Javadoc)
		 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
		 */
		@Override
		public void mousePressed(MouseEvent p_e)
		{
			//set the time when the mouse (or finger) was pressed
			c_firstClick = p_e.getWhen();
		}

		/* (non-Javadoc)
		 * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
		 */
		@Override
		public void mouseReleased(MouseEvent p_e)
		{
			//check that the mouse (or finger) was held down 
			//for long enough (normally 1 second)
			if(p_e.getWhen() - c_firstClick > 1000)
			{
				//show Admin
				boolean l_isAuthorized = getChiefJudgeAuthorization();
				
				if(!l_isAuthorized)
					return; 
				
				int l_choice = showAdminDialogBox();
				
				if(l_choice == 1)
				{
					endElection();
				}
			}
		}
	}
}
