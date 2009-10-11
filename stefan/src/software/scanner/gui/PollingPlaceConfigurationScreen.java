package software.scanner.gui;
import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Vector;

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
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import software.common.BallotGeometry;
import software.common.Util;
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
	private JTextField jTextFieldToSerial;
	private JLabel jLabelToSerial;
	private JTextField jTextFieldFromSerial;
	private JLabel jLabelFromSerial;
	private JCheckBox jCheckBoxInteractive;
	private JButton jButtonSaveConfigFile;
	private JButton jButtonLoadConfigFile;
	private JButton jButtonBrowseCOnfigFile;
	private JTextField jTextFieldConfigurationFile;
	private JPanel jPanelLoadConfigorationFile;
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
	private JTextField jTextFieldFolder2;
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
						jTextFieldFolder1.setPreferredSize(new java.awt.Dimension(193, 20));
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
						jTextFieldFolder2 = new JTextField();
						jPanelFolders.add(jTextFieldFolder2);
						jTextFieldFolder2.setPreferredSize(new java.awt.Dimension(193, 20));
						jTextFieldFolder2.setText("ballotsBackup");
					}
					{
						jButtonFolder2 = new JButton();
						jPanelFolders.add(jButtonFolder2);
						jButtonFolder2.setText("Browse");
						jButtonFolder2.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent evt) {
								setFieldFromFileChooser(JFileChooser.DIRECTORIES_ONLY, jTextFieldFolder2);
							}
						});
					}
					{
						jLabelFromSerial = new JLabel();
						jPanelFolders.add(jLabelFromSerial);
						jLabelFromSerial.setText("First Valid Serial No");
					}
					{
						jTextFieldFromSerial = new JTextField();
						jPanelFolders.add(jTextFieldFromSerial);
						jTextFieldFromSerial.setText("0");
						jTextFieldFromSerial.setPreferredSize(new java.awt.Dimension(46, 20));
					}
					{
						jLabelToSerial = new JLabel();
						jPanelFolders.add(jLabelToSerial);
						jLabelToSerial.setText("Last Valid Serial No");
					}
					{
						jTextFieldToSerial = new JTextField();
						jPanelFolders.add(jTextFieldToSerial);
						jTextFieldToSerial.setText("100");
						jTextFieldToSerial.setPreferredSize(new java.awt.Dimension(45, 20));
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
							jCheckBoxWithReports.setText("Reports");
						}
						{
							jCheckBoxOverlays = new JCheckBox("With Overlays");
							jPanelMailInReportsOverlays.add(jCheckBoxOverlays);
							jCheckBoxOverlays.setSelected(true);
							jCheckBoxOverlays.setText("Overlays");
						}
						{
							jCheckBoxInteractive = new JCheckBox();
							jPanelMailInReportsOverlays
								.add(jCheckBoxInteractive);
							jCheckBoxInteractive.setText("Interactive");
							jCheckBoxInteractive.setSelected(true);
						}
					}
					{
						jButtonOk = new JButton();
						jPanelOkCancel.add(jButtonOk);
						jButtonOk.setText("OK");
						jButtonOk.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent evt) {
								launchVotingScreen();								
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
				{
					jPanelLoadConfigorationFile = new JPanel();
					jPanelFrame.add(jPanelLoadConfigorationFile);
					jPanelLoadConfigorationFile.setBackground(new java.awt.Color(128,128,255));
					{
						jTextFieldConfigurationFile = new JTextField();
						jPanelLoadConfigorationFile
							.add(jTextFieldConfigurationFile);
						jTextFieldConfigurationFile.setText("scannerConfig.xml");
						jTextFieldConfigurationFile.setPreferredSize(new java.awt.Dimension(155, 20));
					}
					{
						jButtonBrowseCOnfigFile = new JButton();
						jPanelLoadConfigorationFile
							.add(jButtonBrowseCOnfigFile);
						jButtonBrowseCOnfigFile.setText("Browse");
						jButtonBrowseCOnfigFile
							.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent evt) {
								setFieldFromFileChooser(JFileChooser.FILES_ONLY, jTextFieldConfigurationFile);}
							});
					}
					{
						jButtonLoadConfigFile = new JButton();
						jPanelLoadConfigorationFile.add(jButtonLoadConfigFile);
						jButtonLoadConfigFile.setText("Load");
						jButtonLoadConfigFile
							.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent evt) {
								loadConfigFile();
							}
							});
					}
					{
						jButtonSaveConfigFile = new JButton();
						jPanelLoadConfigorationFile.add(jButtonSaveConfigFile);
						jButtonSaveConfigFile.setText("Save");
						jButtonSaveConfigFile
							.addActionListener(new ActionListener() {
								public void actionPerformed(ActionEvent evt) {
									saveConfigFile();
								}
							});
					}
				}
			}
			pack();
			this.setSize(400, 600);
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
	
	private void loadConfigFile() {
		Document doc=null;
		try {
			doc = Util.DomParse(jTextFieldConfigurationFile.getText());
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Node node=doc.getElementsByTagName("precinct").item(0);
		jTextFieldPollingPlaceName.setText(node.getAttributes().getNamedItem("name").getNodeValue());
		jTextFieldNoPollWorkers.setText(node.getAttributes().getNamedItem("noMembers").getNodeValue());
		
		node=doc.getElementsByTagName("input").item(0);
		jTextFieldImageForder.setText(node.getAttributes().getNamedItem("images").getNodeValue());
		if (node.getAttributes().getNamedItem("scanexisting").getNodeValue().compareToIgnoreCase("yes")==0)
			jCheckBoxImageFolder.setSelected(true);
		else
			jCheckBoxImageFolder.setSelected(false);
		
		node=doc.getElementsByTagName("output").item(0);
		jTextFieldFolder1.setText(node.getAttributes().getNamedItem("harddrive").getNodeValue());
		jTextFieldFolder2.setText(node.getAttributes().getNamedItem("memorystick").getNodeValue());
		
		node=doc.getElementsByTagName("config").item(0);
		jTextFieldgeometry.setText(node.getAttributes().getNamedItem("geometry").getNodeValue());
		jTextFieldElectionSpec.setText(node.getAttributes().getNamedItem("electionSpec").getNodeValue());
		
		node=doc.getElementsByTagName("validSerialNumbers").item(0);
		jTextFieldFromSerial.setText(node.getAttributes().getNamedItem("from").getNodeValue());
		jTextFieldToSerial.setText(node.getAttributes().getNamedItem("to").getNodeValue());
		
		if (doc.getElementsByTagName("mailin").item(0)!=null)
			jCheckBoxMailIn.setSelected(true);
		else
			jCheckBoxMailIn.setSelected(false);

		if (doc.getElementsByTagName("withStartAndEndReports").item(0)!=null)
			jCheckBoxOverlays.setSelected(true);
		else
			jCheckBoxOverlays.setSelected(false);

		if (doc.getElementsByTagName("withStartAndEndReports").item(0)!=null)
			jCheckBoxWithReports.setSelected(true);
		else
			jCheckBoxWithReports.setSelected(false);
		
		if (doc.getElementsByTagName("interactive").item(0)!=null)
			jCheckBoxInteractive.setSelected(true);
		else
			jCheckBoxInteractive.setSelected(false);

	}
	
	private void saveConfigFile() {
		BufferedOutputStream bos=null;
		try {
			bos=new BufferedOutputStream(new FileOutputStream(jTextFieldConfigurationFile.getText()));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		try {
			bos.write("<xml>\n".getBytes());
			bos.write(("\t<precinct name=\""+jTextFieldPollingPlaceName.getText()+"\" noMembers=\""+jTextFieldNoPollWorkers.getText()+"\"/>\n").getBytes());
			
			bos.write(("\t<input images=\""+jTextFieldImageForder.getText()+"\" scanexisting=\"").getBytes());
			if (jCheckBoxImageFolder.isSelected())
				bos.write(("yes").getBytes());
			else
				bos.write(("no").getBytes());
			bos.write(("\"/>\n").getBytes());
			
			bos.write(("\t<output harddrive=\""+jTextFieldFolder1.getText()+"\" memorystick=\""+jTextFieldFolder2.getText()+"\"/>\n").getBytes());
			bos.write(("\t<config geometry=\""+jTextFieldgeometry.getText()+"\" electionSpec=\""+jTextFieldElectionSpec.getText()+"\"/>\n").getBytes());
			
			bos.write(("\t<validSerialNumbers from=\""+jTextFieldFromSerial.getText()+"\" to=\""+jTextFieldToSerial.getText()+"\"/>\n").getBytes());
			
			bos.write(("\t<properties>\n").getBytes());
			if (jCheckBoxMailIn.isSelected())
				bos.write(("\t\t<mailin/>\n").getBytes());
			if (jCheckBoxWithReports.isSelected())
				bos.write(("\t\t<withStartAndEndReports/>\n").getBytes());
			if (jCheckBoxOverlays.isSelected())
				bos.write(("\t\t<withOverlays/>\n").getBytes());
			if (jCheckBoxInteractive.isSelected())
				bos.write(("\t\t<interactive/>\n").getBytes());
			bos.write(("\t</properties>\n").getBytes());
			bos.write("</xml>\n".getBytes());
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		try {
			bos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	void launchVotingScreen() {
		try {
			BallotGeometry geom=new BallotGeometry(jTextFieldgeometry.getText());
			ElectionSpecification es=new ElectionSpecification(jTextFieldElectionSpec.getText());
			ScannedBallotInterface sb=null;
			
			if (geom.getBottomNode("0", "0", "0")==null)
				sb = new software.scanner.scantegrity.ScannedBallotWithBarcodes(geom,es);
				//sb = new software.scanner.scantegrity.ScannedBallot(geom,es);
			else
				sb = new software.scanner.ScannedBallot(geom,es);
			
			Vector<Point> validSerials=new Vector<Point>();
			validSerials.add(new Point(Integer.parseInt(jTextFieldFromSerial.getText()),Integer.parseInt(jTextFieldToSerial.getText())));
			
			sb.setMailIn(jCheckBoxMailIn.isSelected());
			VoterLocalScreen2Private vls=new VoterLocalScreen2Private(sb,new String(jPasswordFieldPassword.getPassword()),jTextFieldFolder1.getText(),jTextFieldImageForder.getText(),!jCheckBoxImageFolder.isSelected());
			vls.setFolderWhereToWriteVotedBallotsBACKUP(jTextFieldFolder2.getText());
			vls.setElectionID(es.getId());
			vls.setPollingPlaceName(jTextFieldPollingPlaceName.getText());
			vls.setNoPollWorkers(Integer.parseInt(jTextFieldNoPollWorkers.getText()));
			vls.setWithOverlays(jCheckBoxOverlays.isSelected());
			vls.setWithReports(jCheckBoxWithReports.isSelected());
			vls.setInteractive(jCheckBoxInteractive.isSelected());
			vls.setPerfectWidth(geom.getWidth());
			//vls.setValidSerialNumbers(validSerials);
			vls.go();
			dispose();
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
}
