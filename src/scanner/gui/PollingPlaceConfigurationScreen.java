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

package software.scanner.gui;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import javax.swing.WindowConstants;

import org.gwu.voting.standardFormat.electionSpecification.ElectionSpecification;

import software.common.BallotGeometry;
import software.scanner.ScannedBallotInterface;

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
public class PollingPlaceConfigurationScreen extends javax.swing.JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel jPanelFrame;
	private JLabel jLabelRePassword;
	private JPasswordField jPasswordFieldPassword;
	private JLabel jLabelPassword;
	private JTextField jTextFieldNoPollWorkers;
	private JLabel jLabelNoPollWorkers;
	private JTextField jTextFieldPollingPlaceName;
	private JLabel jLabelPollingPlaceName;
	private JPanel jPanelOkCancel;
	private JCheckBox jCheckBoxOverlays;
	private JCheckBox jCheckBoxWithReports;
	private JPanel jPanelMailInReportsOverlays;
	private JButton jButtonCancel;
	private JCheckBox jCheckBoxMailIn;
	private JButton jButtonOk;
	private JButton jButtonElectionSpec;
	private JTextField jTextFieldElectionSpec;
	private JLabel jLabelElectionSpec;
	private JButton jButtonGeometry;
	private JTextField jTextFieldgeometry;
	private JLabel jLabelBallotMap;
	private JButton jButtonFolder2;
	private JTextField jTextFieldForlder2;
	private JButton jButtonFolder1;
	private JTextField jTextFieldFolder1;
	private JLabel jLabelOutFolder;
	private JCheckBox jCheckBoxImageFolder;
	private JButton jButtonImageFOlder;
	private JTextField jTextFieldImageForder;
	private JLabel jLabelImageFordel;
	private JPasswordField jPasswordFieldRePassword;
	private JPanel jPanelBallotMapES;
	private JPanel jPanelFolders;
	private JPanel jPanelNameAndPass;

	JFileChooser fc=new JFileChooser();
	/**
	* Auto-generated main method to display this JFrame
	*/
	public static void main(String[] args) {
		PollingPlaceConfigurationScreen inst = new PollingPlaceConfigurationScreen();
		inst.setVisible(true);
	}
	
	public PollingPlaceConfigurationScreen() {
		super();
		initGUI();
	}
	
	private void initGUI() {
		try {
			setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			{
				jPanelFrame = new JPanel();
				BoxLayout jPanelFrameLayout = new BoxLayout(
					jPanelFrame,
					javax.swing.BoxLayout.Y_AXIS);
				jPanelFrame.setLayout(jPanelFrameLayout);
				getContentPane().add(jPanelFrame, BorderLayout.CENTER);
				{
					jPanelNameAndPass = new JPanel();
					jPanelFrame.add(jPanelNameAndPass);
					{
						jLabelPollingPlaceName = new JLabel();
						jPanelNameAndPass.add(jLabelPollingPlaceName);
						jLabelPollingPlaceName.setText("Precinct Name");
					}
					{
						jTextFieldPollingPlaceName = new JTextField();
						jPanelNameAndPass.add(jTextFieldPollingPlaceName);
						jTextFieldPollingPlaceName.setText("Precinct 4711");
						jTextFieldPollingPlaceName.setPreferredSize(new java.awt.Dimension(141, 20));
					}
					{
						jLabelNoPollWorkers = new JLabel();
						jPanelNameAndPass.add(jLabelNoPollWorkers);
						jLabelNoPollWorkers.setText("Number of members");
					}
					{
						jTextFieldNoPollWorkers = new JTextField();
						jPanelNameAndPass.add(jTextFieldNoPollWorkers);
						jTextFieldNoPollWorkers.setText("2");
						jTextFieldNoPollWorkers.setPreferredSize(new java.awt.Dimension(19, 20));
					}
					{
						jLabelPassword = new JLabel();
						jPanelNameAndPass.add(jLabelPassword);
						jLabelPassword.setText("Password");
					}
					{
						jPasswordFieldPassword = new JPasswordField();
						jPanelNameAndPass.add(jPasswordFieldPassword);
						jPasswordFieldPassword.setPreferredSize(new java.awt.Dimension(87, 20));
					}
					{
						jLabelRePassword = new JLabel();
						jPanelNameAndPass.add(jLabelRePassword);
						jLabelRePassword.setText("Retype password");
					}
					{
						jPasswordFieldRePassword = new JPasswordField();
						jPanelNameAndPass.add(jPasswordFieldRePassword);
						jPasswordFieldRePassword.setPreferredSize(new java.awt.Dimension(87, 20));
					}
				}
				{
					jPanelFolders = new JPanel();
					jPanelFrame.add(jPanelFolders);
					jPanelFolders.setPreferredSize(new java.awt.Dimension(392, 96));
					{
						jLabelImageFordel = new JLabel();
						jPanelFolders.add(jLabelImageFordel);
						jLabelImageFordel
							.setText("The images from the scanner will appear in this folder");
					}
					{
						jTextFieldImageForder = new JTextField();
						jPanelFolders.add(jTextFieldImageForder);
						jTextFieldImageForder.setPreferredSize(new java.awt.Dimension(144, 20));
						jTextFieldImageForder.setText("images");
					}
					{
						jButtonImageFOlder = new JButton();
						jPanelFolders.add(jButtonImageFOlder);
						jButtonImageFOlder.setText("Browse");
						jButtonImageFOlder
							.addActionListener(new ActionListener() {
								public void actionPerformed(ActionEvent evt) {
									setFieldFromFileChooser(
										JFileChooser.DIRECTORIES_ONLY,
										jTextFieldImageForder);
								}
							});
					}
					{
						jCheckBoxImageFolder = new JCheckBox();
						jPanelFolders.add(jCheckBoxImageFolder);
						jCheckBoxImageFolder.setText("Scan existing images");
					}
					{
						jLabelOutFolder = new JLabel();
						jPanelFolders.add(jLabelOutFolder);
						jLabelOutFolder
							.setText("The scanned ballots will be outputed to the folowing folders");
					}
					{
						jTextFieldFolder1 = new JTextField();
						jPanelFolders.add(jTextFieldFolder1);
						jTextFieldFolder1.setPreferredSize(new java.awt.Dimension(155, 20));
						jTextFieldFolder1.setText("ballots");
					}
					{
						jButtonFolder1 = new JButton();
						jPanelFolders.add(jButtonFolder1);
						jButtonFolder1.setText("Browse");
						jButtonFolder1.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent evt) {
								setFieldFromFileChooser(JFileChooser.DIRECTORIES_ONLY, jTextFieldFolder1);
							}
						});
					}
					{
						jTextFieldForlder2 = new JTextField();
						jPanelFolders.add(jTextFieldForlder2);
						jTextFieldForlder2.setPreferredSize(new java.awt.Dimension(155, 20));
						jTextFieldForlder2.setText("ballotsBackup");
					}
					{
						jButtonFolder2 = new JButton();
						jPanelFolders.add(jButtonFolder2);
						jButtonFolder2.setText("Browse");
						jButtonFolder2.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent evt) {
								setFieldFromFileChooser(JFileChooser.DIRECTORIES_ONLY, jTextFieldForlder2);
							}
						});
					}
				}
				{
					jPanelBallotMapES = new JPanel();
					jPanelFrame.add(jPanelBallotMapES);
					{
						jLabelBallotMap = new JLabel();
						jPanelBallotMapES.add(jLabelBallotMap);
						jLabelBallotMap.setText("Geometry");
						jLabelBallotMap.setPreferredSize(new java.awt.Dimension(100, 14));
					}
					{
						jTextFieldgeometry = new JTextField();
						jPanelBallotMapES.add(jTextFieldgeometry);
						jTextFieldgeometry.setText("geometry.xml");
						jTextFieldgeometry.setPreferredSize(new java.awt.Dimension(155, 20));
					}
					{
						jButtonGeometry = new JButton();
						jPanelBallotMapES.add(jButtonGeometry);
						jButtonGeometry.setText("Browse");
						jButtonGeometry.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent evt) {
								setFieldFromFileChooser(JFileChooser.FILES_ONLY, jTextFieldgeometry);
							}
						});
					}
					{
						jLabelElectionSpec = new JLabel();
						jPanelBallotMapES.add(jLabelElectionSpec);
						jLabelElectionSpec.setText("Election Specification");
					}
					{
						jTextFieldElectionSpec = new JTextField();
						jPanelBallotMapES.add(jTextFieldElectionSpec);
						jTextFieldElectionSpec.setText("ElectionSpec.xml");
						jTextFieldElectionSpec.setPreferredSize(new java.awt.Dimension(155, 20));
					}
					{
						jButtonElectionSpec = new JButton();
						jPanelBallotMapES.add(jButtonElectionSpec);
						jButtonElectionSpec.setText("Browse");
						jButtonElectionSpec
							.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent evt) {
								setFieldFromFileChooser(JFileChooser.FILES_ONLY, jTextFieldElectionSpec);
							}
							});
					}
				}
				{
					jPanelOkCancel = new JPanel();
					jPanelFrame.add(jPanelOkCancel);
					jPanelOkCancel.setPreferredSize(new java.awt.Dimension(392, 45));
					{
						jPanelMailInReportsOverlays = new JPanel();
						jPanelOkCancel.add(jPanelMailInReportsOverlays);
						jPanelMailInReportsOverlays.setPreferredSize(new java.awt.Dimension(388, 28));
						{
							jCheckBoxMailIn = new JCheckBox("Mail in");
							jPanelMailInReportsOverlays.add(jCheckBoxMailIn);
						}
						{
							jCheckBoxWithReports = new JCheckBox("With Start and End Reports");
							jPanelMailInReportsOverlays
								.add(jCheckBoxWithReports);
							jCheckBoxWithReports.setSelected(true);
						}
						{
							jCheckBoxOverlays = new JCheckBox("With Overlays");
							jPanelMailInReportsOverlays.add(jCheckBoxOverlays);
							jCheckBoxOverlays.setSelected(true);
						}
					}
					{
						jButtonOk = new JButton();
						jPanelOkCancel.add(jButtonOk);
						jButtonOk.setText("OK");
						jButtonOk.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent evt) {
								try {
									BallotGeometry geom=new BallotGeometry(jTextFieldgeometry.getText());
									ElectionSpecification es=new ElectionSpecification(jTextFieldElectionSpec.getText());
									ScannedBallotInterface sb=null;
									
									if (geom.getBottomNode("0", "0", "0")==null)
										sb=new software.scanner.scantegrity.ScannedBallot(geom,es);
									else
										sb=new software.scanner.ScannedBallot(geom,es);
									
									sb.setMailIn(jCheckBoxMailIn.isSelected());
									VoterLocalScreen2 vls=new VoterLocalScreen2(sb,new String(jPasswordFieldPassword.getPassword()),jTextFieldFolder1.getText(),jTextFieldImageForder.getText(),!jCheckBoxImageFolder.isSelected());
									vls.setFolderWhereToWriteVotedBallotsBACKUP(jTextFieldForlder2.getText());
									vls.setElectionID(es.getId());
									vls.setPollingPlaceName(jTextFieldPollingPlaceName.getText());
									vls.setNoPollWorkers(Integer.parseInt(jTextFieldNoPollWorkers.getText()));
									vls.setWithOverlays(jCheckBoxOverlays.isSelected());
									vls.setWithReports(jCheckBoxWithReports.isSelected());
									vls.setPerfectWidth(geom.getWidth());
									vls.go();
									dispose();
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						});
					}
					{
						jButtonCancel = new JButton();
						jPanelOkCancel.add(jButtonCancel);
						jButtonCancel.setText("Cancel");
						jButtonCancel.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent evt) {
								dispose();
							}
						});
					}
				}
			}
			pack();
			this.setSize(400, 400);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void setFieldFromFileChooser(int mode,JTextField jTextFieldImageForder) {
		fc.setFileSelectionMode(mode);
	    fc.setVisible(true);
	    int retVal = fc.showOpenDialog(this);
	    
	    if (retVal == JFileChooser.APPROVE_OPTION)
	    {
	        try {
				jTextFieldImageForder.setText(fc.getSelectedFile().getCanonicalPath());
			} catch (IOException e) {
				e.printStackTrace();
			}                                         
	    }
	    else
	    {
	        return;
	    }
	}

}
