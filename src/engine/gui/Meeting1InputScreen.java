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

package software.engine.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import javax.swing.WindowConstants;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

import org.gwu.voting.standardFormat.electionSpecification.ElectionSpecification;
import org.w3c.dom.Document;

import software.common.Util;
import software.engine.ioExample.MeetingOneIn;


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
 *	This is the first screen, for the selection of the environment variables: 
 * 	number of ballots, nr of Ds, etc.. 
 */
public class Meeting1InputScreen extends JDialog {
	private static final long serialVersionUID = -3555189551108961790L;
	private JPanel jPanel1;
	private JLabel jLabel4;
	private JLabel jLabelElectionSpecification;
	private JButton jbLoad;
	private JButton jsSave;
	private JButton jbES;
	private JTextField jtfES;
	private JTextField jtfNoDs;
	private JLabel jLabel5;
	private JTextField jtfNoB;
	private JTextField jtfC;
	private JLabel jLabel3;

	final JFileChooser fc = new JFileChooser();

	String c = null;
	int noB = 0;
	int noDs = 0;
	String es = null;

	String pathToM1In="";
	
	/**
	* Auto-generated main method to display this JFrame
	*/
	public static void main(String[] args) {
		Meeting1InputScreen inst = new Meeting1InputScreen("public");
		inst.setVisible(true);
	}
	
	public Meeting1InputScreen(String publicDir) {
		super();
		setModal(true);
		this.pathToM1In=publicDir;//+"/MeetingOneIn.xml";
		fc.setCurrentDirectory(new File(pathToM1In).getParentFile());
		initGUI();
		try {
			parse(new File(pathToM1In));
		} catch (Exception e) {
			//e.printStackTrace();
		}
	}
	
	private void initGUI() {
		try {
			setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			{
				jPanel1 = new JPanel();
				getContentPane().add(jPanel1, BorderLayout.CENTER);
				jPanel1.setPreferredSize(new java.awt.Dimension(392, 167));
				{
					jLabel3 = new JLabel();
					jPanel1.add(jLabel3);
					jLabel3.setText("Public Constant");
				}
				{
					jtfC = new JTextField();
					jPanel1.add(jtfC);
					jtfC.setPreferredSize(new java.awt.Dimension(262, 20));
				}
				{
					jLabel4 = new JLabel();
					jPanel1.add(jLabel4);
					jLabel4.setText("Number Of Ballots");
				}
				{
					jtfNoB = new JTextField();
					jPanel1.add(jtfNoB);
					jtfNoB.setPreferredSize(new java.awt.Dimension(60, 20));
				}
				{
					jLabel5 = new JLabel();
					jPanel1.add(jLabel5);
					jLabel5.setText("Number Of D Tables");
				}
				{
					jtfNoDs = new JTextField();
					jPanel1.add(jtfNoDs);
					jtfNoDs.setPreferredSize(new java.awt.Dimension(60, 20));
				}
				{
					jLabelElectionSpecification = new JLabel();
					jPanel1.add(jLabelElectionSpecification);
					jLabelElectionSpecification.setText("ElectionSpec");
					jLabelElectionSpecification.setPreferredSize(new java.awt.Dimension(79, 14));
				}
				{
					jtfES = new JTextField();
					jPanel1.add(jtfES);
					jtfES.setPreferredSize(new java.awt.Dimension(207, 20));
					jtfES.setEditable(true);
				}
				{
					jbES = new JButton();
					jPanel1.add(jbES);
					jbES.setText("Browse");
					jbES.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							int rv = fc.showOpenDialog(new JFrame(
							"Choose the ELection Specification"));
							if (rv == JFileChooser.APPROVE_OPTION) {
								jtfES.setText(fc.getSelectedFile().getAbsolutePath());
							}
						}
					});
				}
				{
					jsSave = new JButton();
					jPanel1.add(jsSave);
					jsSave.setText("Save&Exit");
					jsSave.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							try {
								save();
							} catch (Exception e) {
								JOptionPane.showMessageDialog(null,e.getMessage(), "alert", JOptionPane.ERROR_MESSAGE);
								return;
							}
							dispose();
						}
					});
				}
				{
					jbLoad = new JButton();
					jPanel1.add(jbLoad);
					jbLoad.setText("Load");
					jbLoad.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							load();
						}
					});
				}
			}
			pack();
			this.setSize(400, 192);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Generates the MeetingOneIn XML with the election settings.   
	 * @throws Exception
	 */
	private void write(String file) throws Exception {
		MeetingOneIn.write(es,noB,noDs,c.getBytes(), file);
	}
	
	/**
	 * Loads a preset MeetingOneIn XMLs 
	 * @throws Exception
	 */
	private void parse(File file) throws Exception {
		Document doc = Util.DomParse(file);
		jtfES.setText(doc.getElementsByTagName("electionSpec").item(0).getFirstChild().getNodeValue());
		jtfNoB.setText(doc.getElementsByTagName("noBallots").item(0).getFirstChild().getNodeValue());
		jtfNoDs.setText(doc.getElementsByTagName("noDs").item(0).getFirstChild().getNodeValue());
		jtfC.setText(new String(Base64.decode(doc.getElementsByTagName("constant").item(0).getFirstChild().getNodeValue())));
	}
	
	/**
	 * Loads MeetingOneIn from a file and fills in the coresponding fields
	 *
	 */
	private void load() {
		try {
			fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
			fc.setSelectedFile(new File(pathToM1In));
		    fc.setVisible(true);
		    int retVal = fc.showOpenDialog(this);
		    
		    if (retVal == JFileChooser.APPROVE_OPTION)
		    {
		    	File f=fc.getSelectedFile();
		    	pathToM1In=f.getAbsolutePath();
		    	parse(f);
		    }
		    else
		    {
		        return;
		    }			
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	private void save() throws Exception {
		c = jtfC.getText().trim();
		if (c.length() < 16) {
			while (c.length()<16)
				c=c+" ";
			//throw new Exception("The public constant has to have 16 chars. Is has "+c.length());								
		}
		if (c.length() > 16) {
			c=c.substring(0,16);
		}
		
		try {
			noB = Integer.parseInt(jtfNoB.getText());
			if (noB < 1 )
				throw new Exception("");
		} catch(Exception e) {
			throw new Exception("Numbe of ballots must be an integer between > 0");																
		}
		try {
			noDs = Integer.parseInt(jtfNoDs.getText());
			if (noDs < 1 || noDs >20)
				throw new Exception("");
		} catch(Exception e) {
			throw new Exception("Numbe of D tables must be an integer between 1 and 20");																
		}
		try {
			new ElectionSpecification(jtfES.getText());
			es=jtfES.getText();
		} catch (Exception e) {
			throw new Exception("Invalid ElectionSpecification file ");																			
		}
		
    	try {
			write(pathToM1In);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Error saving "+pathToM1In, "alert", JOptionPane.ERROR_MESSAGE);				
			e.printStackTrace();
		}
	}
}
