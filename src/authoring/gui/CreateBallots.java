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

package software.authoring.gui;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;

import javax.swing.WindowConstants;

import org.gwu.voting.standardFormat.electionSpecification.ElectionSpecification;

import software.authoring.BmpTogeometryInterface;
import software.authoring.ContestSymbols;
import software.authoring.Drills;
import software.authoring.FillInPdfForm;
import software.authoring.FormMaker;
import software.authoring.invisibleink.PrintableBallotMaker;
import software.common.BallotGeometry;
import software.common.Cluster;
import software.common.ImagePanel;
import software.common.Util;


/**
* This code was edited or generated using CloudGarden's Jigloo
* SWT/Swing GUI Builder, which is free for non-commercial
* use. If Jigloo is being used commercially (ie, by a corporation,
* company or business for any purpose whatever) then you
* should purchase a license for each developer using Jigloo.
* Please visit www.cloudgarden.com for details.
* Use of Jigloo implies acceptance of these licensing terms.
* A COMMERCIAL LICENSE HAS NOT BEEN PURCHASED FOR
* THIS MACHINE, SO JIGLOO OR THIS CODE CANNOT BE USED
* LEGALLY FOR ANY CORPORATE OR COMMERCIAL PURPOSE.
*/

/**
 * A simple window that allows to create the geometry and the election Specification
 * for an election given an image with colored bullets and donuts.
 * 
 * Is also able to produce interactive pdf ballots
 */
public class CreateBallots extends javax.swing.JFrame {
	private static final long serialVersionUID = -3110431710177821281L;
	private JMenuBar jMenuBar;
	private JMenu jMenuFIle;
	private JPanel jPanelImage;
	private JSeparator jSeparatorFile1;
	private JMenuItem jMenuItemLoadBitmap;
	private JMenuItem jMenuMakeForm;
	private JMenu jMenuToold;
	private JMenuItem jMenuItemLoadprints;
	private JMenuItem jMenuItemLoadGeometry;

	final JFileChooser fc = new JFileChooser(".");
	ImagePanel imagePanel=null;
	private JMenuItem jMenuItemRandomPSVotes;
	private JSeparator jSeparator1;

	String outputFolder="";
	
	BallotGeometry geometry=null;
	private JMenuItem jMenuItemMakePrintableBallots;
	private JMenuItem jMenuItemPdfFromDrillFiles;
	private JMenuItem jMenuItemMakeDrillFiles;
	ElectionSpecification electionSpec=null;
	String pdfForm="javaCreatedForm.pdf";	
	
	private JMenuItem jMenuItemLoadPdfForm;
	private JSeparator jSeparatorFile2;
	private JMenuItem jMenuItemMakeVirtualBallots;	
	String printsFilePath="MeetingTwoPrints.xml";

	String background=null;
	
	Util.BallotType ballotType=Util.BallotType.NONE;
	
	public static void main(String[] args) {
		CreateBallots inst = new CreateBallots();
		inst.setVisible(true);
	}
	
	public CreateBallots() {
		super();
		initGUI();
	}
	
	private void initGUI() {
		try {
			setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
			addWindowListener( new WindowAdapter() {
				   public void windowClosing( WindowEvent e ){
					   cancel();
				   }
			} );
			
			{
				jPanelImage = new JPanel();
				getContentPane().add(jPanelImage, BorderLayout.CENTER);
				jPanelImage.setPreferredSize(new java.awt.Dimension(392, 248));
			}
			{
				jMenuBar = new JMenuBar();
				setJMenuBar(jMenuBar);
				{
					jMenuFIle = new JMenu();
					jMenuBar.add(jMenuFIle);
					jMenuFIle.setText("File");
					{
						jMenuItemLoadBitmap = new JMenuItem();
						jMenuFIle.add(jMenuItemLoadBitmap);
						jMenuItemLoadBitmap.setText("Load Fully Marked Ballot");
						jMenuItemLoadBitmap
							.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent evt) {
								loadBitmap();
							}
							});
					}
					{
						jSeparatorFile1 = new JSeparator();
						jMenuFIle.add(jSeparatorFile1);
					}
					{
						jMenuItemLoadGeometry = new JMenuItem();
						jMenuFIle.add(jMenuItemLoadGeometry);
						jMenuItemLoadGeometry.setText("Load Geometry");
						jMenuItemLoadGeometry
							.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent evt) {
								loadGeometry();
								loadElectionSpec();
							}
							});
					}
					{
						jSeparatorFile2 = new JSeparator();
						jMenuFIle.add(jSeparatorFile2);
					}
					{
						jMenuItemLoadPdfForm = new JMenuItem();
						jMenuFIle.add(jMenuItemLoadPdfForm);
						jMenuItemLoadPdfForm.setText("Load Pdf Form");
						jMenuItemLoadPdfForm
							.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent evt) {
								loadPdfForm();
							}
							});
					}
					{
						jMenuItemLoadprints = new JMenuItem();
						jMenuFIle.add(jMenuItemLoadprints);
						jMenuItemLoadprints.setText("Load Prints File");
						jMenuItemLoadprints
							.addActionListener(new ActionListener() {
								public void actionPerformed(ActionEvent evt) {
									loadPrintsFile();
								}
							});
					}
				}
				{
					jMenuToold = new JMenu();
					jMenuBar.add(jMenuToold);
					jMenuToold.setText("Tools");
					{
						jMenuMakeForm = new JMenuItem();
						jMenuToold.add(jMenuMakeForm);
						jMenuMakeForm.setText("Make Pdf Form");
						jMenuMakeForm
							.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent evt) {
								createPdfForm();
							}
							});
					}
					{
						jMenuItemMakeVirtualBallots = new JMenuItem();
						jMenuToold.add(jMenuItemMakeVirtualBallots);
						jMenuItemMakeVirtualBallots.setText("Make Virtual Ballots");
						jMenuItemMakeVirtualBallots
							.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent evt) {
								makeVirtualBallots();
							}
							});
					}
					{
						jMenuItemMakePrintableBallots = new JMenuItem();
						jMenuToold.add(jMenuItemMakePrintableBallots);
						jMenuItemMakePrintableBallots
							.setText("Make Printable Ballots");
						jMenuItemMakePrintableBallots
							.addActionListener(new ActionListener() {
								public void actionPerformed(ActionEvent evt) {
									makePrintableBallots();
								}
							});
					}
					{
						jMenuItemRandomPSVotes = new JMenuItem();
						jMenuToold.add(jMenuItemRandomPSVotes);
						jMenuItemRandomPSVotes.setText("Vote Randomly");
						jMenuItemRandomPSVotes
							.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent evt) {
								voteRandomly();
							}
							});
					}
					{
						jSeparator1 = new JSeparator();
						jMenuToold.add(jSeparator1);
					}
					{
						jMenuItemMakeDrillFiles = new JMenuItem();
						jMenuToold.add(jMenuItemMakeDrillFiles);
						jMenuItemMakeDrillFiles.setText("Make Drill Files");
						jMenuItemMakeDrillFiles
							.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent evt) {
								makeDrillFiles();
							}
							});
					}
					{
						jMenuItemPdfFromDrillFiles = new JMenuItem();
						jMenuToold.add(jMenuItemPdfFromDrillFiles);
						jMenuItemPdfFromDrillFiles
							.setText("Make Pdf From Drill Files");
						jMenuItemPdfFromDrillFiles
							.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent evt) {
								makePdfFromDrillFiles();
							}
							});
					}
				}
			}
			pack();
			setSize(400, 600);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void loadBitmap() {
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		int rv = fc.showDialog(this,"Load Ballot Image");
		if (rv == JFileChooser.CANCEL_OPTION)
			return;
		BufferedImage img=null;
		String imageFile = fc.getSelectedFile().getPath();
		outputFolder=fc.getSelectedFile().getParent()+Util.fileSeparator;
		
		try {
			img = ImageIO.read(new File(imageFile));
		} catch (IOException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null,
					"Error in reading the image \n"+e.getMessage(),
					"alert",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		if (imagePanel==null)
			imagePanel = new ImagePanel();
		imagePanel.setImage(img);
		//TODO Assuming letter size paper
		
		imagePanel.setDpi(img.getHeight() / 11);
		
		imagePanel.setPreferredSize(new Dimension((int)(getWidth()*0.9),(int)(getHeight()*0.9)));		
		imagePanel.setZoom(imagePanel.getPreferredSize().getWidth() / img.getWidth());
		imagePanel.removeBrowseButton();
		imagePanel.getFileNameLabel().setText("");
		jPanelImage.add(imagePanel);
		jPanelImage.repaint();
		jPanelImage.setSize(new Dimension(getWidth()+1,getHeight()+1));
		jPanelImage.setPreferredSize(new Dimension(getWidth()+1,getHeight()+1));
				
		ChoosePdfColors choosePdfColors = new ChoosePdfColors();
		
		imagePanel.addActionListner(choosePdfColors);		
	}
	
	void done(BmpTogeometryInterface btg) {
		int noColumns = 1;
		noColumns = Integer.parseInt(JOptionPane.showInputDialog("How many columns are there?"));
		
		try {
			String electionSpecPath=outputFolder+"ElectionSpec.xml";
			String geomPath=outputFolder+"geometry.xml";
			btg.createGeometry(imagePanel.getImage(), (float)imagePanel.getDpi(), noColumns, geomPath,electionSpecPath);
			electionSpec=new ElectionSpecification(electionSpecPath);
			geometry=new BallotGeometry(geomPath);
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null,
					"Error in processing the image \n"+e.getMessage(),
					"alert",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		JOptionPane.showMessageDialog(this, "Done");		
	}
	
	void loadGeometry() {
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fc.setSelectedFile(new File(outputFolder+"geometry.xml"));
		int rv = fc.showDialog(this,"Load the Geometry");
		if (rv == JFileChooser.CANCEL_OPTION)
			return;
		outputFolder=fc.getSelectedFile().getParent()+Util.fileSeparator;
		try {
			geometry=new BallotGeometry(fc.getSelectedFile().getCanonicalPath());
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, "Improper geometry.\n"+e.getMessage());
			return;
		}
	}
	void loadElectionSpec() {
		fc.setSelectedFile(new File(outputFolder+"ElectionSpec.xml"));
		int rv = fc.showDialog(this,"Load the ElectionSpec");
		if (rv == JFileChooser.CANCEL_OPTION)
			return;
		outputFolder=fc.getSelectedFile().getParent()+Util.fileSeparator;
		try {
			electionSpec=new ElectionSpecification(fc.getSelectedFile().getCanonicalPath());
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, "Improper election spec.\n"+e.getMessage());			
		}
	}
	
	private void loadPrintsFile() {
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fc.setSelectedFile(new File(outputFolder+printsFilePath));
		int rv = fc.showDialog(this,"Choose the Prints File");
		if (rv == JFileChooser.CANCEL_OPTION)
			return;
		printsFilePath = fc.getSelectedFile().getPath();
	}	

	private void loadPdfForm() {
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fc.setSelectedFile(new File(outputFolder+pdfForm));
		int rv = fc.showDialog(this,"Choose the Pdf Form");
		if (rv == JFileChooser.CANCEL_OPTION)
			return;
		pdfForm = fc.getSelectedFile().getPath();
		ballotType=Util.askWhatTypeOfBallot(this);
		
		if (electionSpec==null) {
			loadElectionSpec();
		}
		loadPrintsFile();
	}	
	
	private void askForBackground() {
		fc.setSelectedFile(new File(outputFolder+background));
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		int rv = fc.showDialog(this,"Choose background");
		if (rv == JFileChooser.CANCEL_OPTION)
			return;
		background = fc.getSelectedFile().getPath();		
	}
	
	private void createPdfForm() {
		try {
			//ask for background
			if (background==null)
				askForBackground();
			
			fc.setSelectedFile(new File(outputFolder));
			fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			int rv = fc.showDialog(this,"Choose Destination Folder");
			if (rv == JFileChooser.CANCEL_OPTION)
				return;
			outputFolder = fc.getSelectedFile().getPath()+Util.fileSeparator;
			
			if (ballotType.equals(Util.BallotType.NONE)) {
				//deternime the type of ballot from the geometry
				if (geometry.getBottomNode("0", "0", "0")!=null)
					ballotType=Util.BallotType.PUNCHSCAN;
				else
					ballotType=Util.BallotType.SCANTEGRITY;
			}
						
			if (ballotType.equals(Util.BallotType.SCANTEGRITY)) {
				//scantegrity
				software.authoring.scantegrity.FormMaker fm=new software.authoring.scantegrity.FormMaker(electionSpec,geometry);
				fm.make(background, outputFolder+pdfForm);
			}
			else {
				if (ballotType.equals(Util.BallotType.PUNCHSCAN)) {				
					software.authoring.FormMaker fm=new software.authoring.FormMaker(electionSpec,geometry);
					fm.make(background, outputFolder+pdfForm);
				} else {
					System.out.println(ballotType);					
					JOptionPane.showMessageDialog(this, "No ballot style selected");
				}
			}
			pdfForm=outputFolder+pdfForm;
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, "Error: "+e.getMessage());
			return;			
		}
		
		JOptionPane.showMessageDialog(this, "Done");
	}

	private Vector<Point> getBallotsToBePrinted() {
		String allSerialnumbersIntervals =JOptionPane.showInputDialog("Serial numbers");
		Vector<Point> range=new Vector<Point>();
		StringTokenizer st = new StringTokenizer(allSerialnumbersIntervals,",;");
		while (st.hasMoreTokens()) {
			String interval = st.nextToken();
			int delimpos = interval.indexOf("-");
			int start; 
			int stop;										
			if (delimpos>0) {
				start = Integer.parseInt(interval.substring(0,delimpos));
				stop = Integer.parseInt(interval.substring(interval.indexOf("-")+1));
			} else {
				start=stop=Integer.parseInt(interval);
			}
			range.add(new Point(start,stop));
		}
		return range;
	}

	private String getOutPutFolder() {
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fc.setSelectedFile(new File(outputFolder));
		int rv = fc.showDialog(this,"Choose the output folder");
		if (rv == JFileChooser.CANCEL_OPTION)
			return null;
		return fc.getSelectedFile().getPath()+Util.fileSeparator;
	}
	private void makeVirtualBallots() {
		Vector<Point> range=getBallotsToBePrinted();
		outputFolder = getOutPutFolder();
		if (outputFolder==null)
			return;
		try {
			if (ballotType.equals(Util.BallotType.PUNCHSCAN))
				software.authoring.FillInPdfForm.fillIn(electionSpec, pdfForm, printsFilePath,range, ContestSymbols.alphabet,false, outputFolder);
			else
				if (ballotType.equals(Util.BallotType.SCANTEGRITY))
					software.authoring.scantegrity.FillInPdfForm.fillIn(electionSpec, pdfForm, printsFilePath,range, ContestSymbols.alphabet,false, outputFolder);
		} catch (Exception e) {
			e.printStackTrace();			
			JOptionPane.showMessageDialog(this, "Error: "+e.getMessage());
			return;
		}
		JOptionPane.showMessageDialog(this, "Done");		
	}

	private void makeDrillFiles() {
		
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fc.setSelectedFile(new File(outputFolder));
		int rv = fc.showDialog(this,"Choose the output folder");
		if (rv == JFileChooser.CANCEL_OPTION)
			return;
		outputFolder = fc.getSelectedFile().getPath()+Util.fileSeparator;
		
		try {
			Drills.GeometryToDrillFile(geometry,outputFolder,0,0);
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this,"Error Creating Drill File\n"+e.getMessage());
			return;
		}
		JOptionPane.showMessageDialog(this, "Done");
	}
	
	private void makePdfFromDrillFiles() {
		//if (background==null)
			askForBackground();

		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fc.setSelectedFile(new File(outputFolder));
		int rv = fc.showDialog(this,"Choose the input folder with the two Drill files");
		if (rv == JFileChooser.CANCEL_OPTION)
			return;
		outputFolder = fc.getSelectedFile().getPath()+Util.fileSeparator;		
		
		try {
			Drills.DrillFilesToPdf(outputFolder, background, outputFolder+"PdfFromDrillFiles.pdf");
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null,
					"Error Creating the pdf file\n"+e.getMessage(),
					"alert",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		JOptionPane.showMessageDialog(this, "Done");
	}
	
	private void makePrintableBallots() {		
		if (geometry==null)
			loadGeometry();
		if (electionSpec==null)
			loadElectionSpec();
		if (background==null)
			askForBackground();
		
		loadPrintsFile();

		try {
	    	PrintableBallotMaker pbm=new PrintableBallotMaker(this.electionSpec,this.geometry);
	    	pbm.init(background,this.printsFilePath);
	
			Vector<Point> range=getBallotsToBePrinted();
	    	//Ask for directory
	    	outputFolder=getOutPutFolder();
	    	
	    	for (Point p:range)
	    		pbm.makePrintableBallots(outputFolder, p.x,p.y);
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this,"Error creating ballots \n"+e.getMessage());
			return;
		}

/*		
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fc.setSelectedFile(new File(outputFolder));
		int rv = fc.showDialog(this,"Choose the folder with the virtual Ballots");
		if (rv == JFileChooser.CANCEL_OPTION)
			return;
		outputFolder = fc.getSelectedFile().getPath()+Util.fileSeparator;			
		
		try {
			FillInPdfForm.topAndBottomSeparatly(new FormMaker(electionSpec,geometry), outputFolder);
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null,
					"Error Creating Printables Ballots\n"+e.getMessage(),
					"alert",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
*/		
		JOptionPane.showMessageDialog(this, "Done");
	}

	private void voteRandomly() {		
		if (geometry==null)
			loadGeometry();
		if (electionSpec==null)
			loadElectionSpec();

		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fc.setSelectedFile(new File(outputFolder));
		int rv = fc.showDialog(this,"Choose the folder with the virtual Ballots");
		if (rv == JFileChooser.CANCEL_OPTION)
			return;
		outputFolder = fc.getSelectedFile().getPath()+Util.fileSeparator;			
		
		ballotType=Util.askWhatTypeOfBallot(this);
		
		try {
			if (ballotType.equals(Util.BallotType.PUNCHSCAN))
				FillInPdfForm.randomVote(new FormMaker(electionSpec,geometry), outputFolder);
			else
				if (ballotType.equals(Util.BallotType.SCANTEGRITY))
					software.authoring.scantegrity.FillInPdfForm.randomVote(new software.authoring.scantegrity.FormMaker(electionSpec,geometry), outputFolder);
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null,
					"Error Creating Voted Ballots at Random\n"+e.getMessage(),
					"alert",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		JOptionPane.showMessageDialog(this, "Done");
	}
	
	
	private void cancel() {
		this.setVisible(false);
		dispose();
		System.exit(-1);
	}
	
	/**
	 * An always on top frame with one radio button and one colored label for each given color.
	 * When the radio button is selected and the user clicks on the image in the main frame,
	 * the coresponding label gets the clicked color
	 * @author stefan
	 *
	 */
	class ChoosePdfColors extends javax.swing.JFrame implements ActionListener {
		private static final long serialVersionUID = 8105801097268950275L;
		private JPanel jPanelFrame;
		private JPanel jPanelDone;
		private JPanel jPanelAllCollors;
		private JButton jButtonDone;
		
		private JRadioButton jRadioButton=null;
		private JLabel jLabel=null;
		
		private ButtonGroup buttonGroup=new ButtonGroup();
		JRadioButton[] allRadioButtons=null;
		JLabel[] allLabels=null;
		
		
		private BmpTogeometryInterface btg=null;
		private Vector<Cluster> colors;
		
		ChoosePdfColors() {
			super();
			initGUI();
		}		
		
		private void initGUI() {
			//ask if PunchScan or Scantegrity
			ballotType=Util.askWhatTypeOfBallot(this);
			
			if (ballotType.equals(Util.BallotType.PUNCHSCAN)) {
				btg=new software.authoring.BmpToGeometry();
			} else {
				if (ballotType.equals(Util.BallotType.SCANTEGRITY)) {
					btg=new software.authoring.scantegrity.BmpToGeometry();
				}
				else {
					JOptionPane.showMessageDialog(this, "Unspecified ballot type");
					return;
				}
			}
			
			colors=btg.getAllColors();
			
			allRadioButtons=new JRadioButton[colors.size()];
			allLabels=new JLabel[colors.size()];
			
			jPanelFrame = new JPanel();
			BorderLayout jPanel1Layout = new BorderLayout();
			jPanelFrame.setLayout(jPanel1Layout);
			getContentPane().add(jPanelFrame, BorderLayout.CENTER);
			jPanelDone = new JPanel();
			jPanelFrame.add(jPanelDone, BorderLayout.SOUTH);
			jButtonDone = new JButton();
			jPanelDone.add(jButtonDone);
			jButtonDone.setText("Done");
			jButtonDone.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					dispose();
					done(btg);
				}
			});
		
			jPanelAllCollors = new JPanel();
			GridLayout jPanel2Layout = new GridLayout(colors.size(), 2);
			jPanel2Layout.setHgap(5);
			jPanel2Layout.setVgap(5);
			jPanel2Layout.setColumns(2);
			jPanel2Layout.setRows(colors.size());
			jPanelAllCollors.setLayout(jPanel2Layout);
			jPanelFrame.add(jPanelAllCollors, BorderLayout.CENTER);
			
			for (int i=0;i<colors.size();i++) {
				Cluster c=colors.get(i);
				
				jRadioButton = new JRadioButton();
				jPanelAllCollors.add(jRadioButton);
				jRadioButton.setText(c.getName());
				jRadioButton.setSelected(false);
				buttonGroup.add(jRadioButton);
				jLabel = new JLabel();
				jPanelAllCollors.add(jLabel);
				jLabel.setBackground(c.getColor());
				jLabel.setOpaque(true);
				
				allRadioButtons[i]=jRadioButton;
				allLabels[i]=jLabel;
			}
			pack();
			this.setSize(270, 200);
			this.setVisible(true);
			this.setAlwaysOnTop(true);			
		}
		
		public void actionPerformed(ActionEvent evt) {
			for (int i=0;i<allRadioButtons.length;i++)
			if (allRadioButtons[i].isSelected()) {
				Color color=new Color(Integer.parseInt(evt.getActionCommand()));
				allLabels[i].setBackground(color);
				colors.get(i).setColor(color);
			}
			
		}
	}
}
