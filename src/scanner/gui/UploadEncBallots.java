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
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.NoSuchPaddingException;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import javax.swing.WindowConstants;

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
public class UploadEncBallots extends javax.swing.JFrame {
	private static final long serialVersionUID = -1227952349478036806L;
	final JFileChooser fc = new JFileChooser(".");
	private JPanel jPanel1;
	private JLabel jLabelStatus;
	private JButton jButtonUpload;
	private JTextField jTextFieldURL;
	private JLabel jLabel3;
	private JButton jButton1;
	private JTextField jTextFieldEncBalots;
	private JLabel jLabel2;
	private JPasswordField jPasswordField1;
	private JLabel jLabel1;

	/**
	* Auto-generated main method to display this JFrame
	*/
	public static void main(String[] args) {
		UploadEncBallots inst = new UploadEncBallots();
		inst.setVisible(true);
	}
	
	public UploadEncBallots() {
		super();
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);		
		initGUI();
	}
	
	private void initGUI() {
		try {
			setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
			addWindowListener( new WindowAdapter() {
				   public void windowClosing( WindowEvent e ){
				           setVisible(false);
				           dispose();
					   }
				   }
			); 			
			{
				jPanel1 = new JPanel();
				getContentPane().add(jPanel1, BorderLayout.CENTER);
				jPanel1.setPreferredSize(new java.awt.Dimension(392, 114));
				{
					jLabel1 = new JLabel();
					jPanel1.add(jLabel1);
					jLabel1.setText("Password");
					jLabel1.setPreferredSize(new java.awt.Dimension(72, 14));
				}
				{
					jPasswordField1 = new JPasswordField();
					jPanel1.add(jPasswordField1);
					jPasswordField1.setPreferredSize(new java.awt.Dimension(183, 20));
				}
				{
					jLabel2 = new JLabel();
					jPanel1.add(jLabel2);
					jLabel2.setText("Folder with encrypted ballots");
					jLabel2.setPreferredSize(new java.awt.Dimension(146, 14));
				}
				{
					jTextFieldEncBalots = new JTextField();
					jPanel1.add(jTextFieldEncBalots);
					jTextFieldEncBalots.setText("tempBallots/");
					jTextFieldEncBalots.setPreferredSize(new java.awt.Dimension(178, 20));
				}
				{
					jButton1 = new JButton();
					jPanel1.add(jButton1);
					jButton1.setText("...");
					jButton1.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							int rv = fc
							.showOpenDialog(new JFrame(
								"Choose a folder"));
						if (rv == JFileChooser.APPROVE_OPTION) {
							jTextFieldEncBalots
								.setText(fc
									.getSelectedFile()
									.getPath());
						}
						}
					});
				}
				{
					jLabel3 = new JLabel();
					jPanel1.add(jLabel3);
					jLabel3.setText("URL");
				}
				{
					jTextFieldURL = new JTextField();
					jPanel1.add(jTextFieldURL);
					jTextFieldURL.setText("http://hangingchad.cs.umbc.edu/~rick/punchscan/control/upload_ballot.php?data=");
				}
				{
					jButtonUpload = new JButton();
					jPanel1.add(jButtonUpload);
					jButtonUpload.setText("Upload");
					jButtonUpload.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							jLabelStatus.setText("Status: Uploading....please wait");
							software.scanner.UploadEncBallots u = null;
							try {
								u = new software.scanner.UploadEncBallots(new String(jPasswordField1.getPassword()),jTextFieldURL.getText());
							} catch (InvalidKeyException e1) {
								jLabelStatus.setText("Status: Uploading....Exception:"+e1.getMessage());
								e1.printStackTrace();
								return;
							} catch (NoSuchAlgorithmException e1) {
								jLabelStatus.setText("Status: Uploading....Exception:"+e1.getMessage());								
								e1.printStackTrace();
								return;
							} catch (NoSuchPaddingException e1) {
								jLabelStatus.setText("Status: Uploading....Exception:"+e1.getMessage());								
								e1.printStackTrace();
								return;
							}
							try {
								u.upload(jTextFieldEncBalots.getText());
							} catch (Exception e) {
								jLabelStatus.setText("Status: Uploading....Exception:"+e.getMessage());								
								e.printStackTrace();
								return;
							}
							jLabelStatus.setText("Status: Uploading....DONE");							
						}
					});
				}
				{
					jLabelStatus = new JLabel();
					jPanel1.add(jLabelStatus);
					jLabelStatus.setText("Status:");
					jLabelStatus.setPreferredSize(new java.awt.Dimension(361, 14));
				}
			}
			pack();
			this.setSize(400, 158);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
