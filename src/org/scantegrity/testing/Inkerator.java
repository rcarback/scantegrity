/*
 * @(#)Inkerator.java
 *  
 * Copyright (C) 2008 Scantegrity Project
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
package org.scantegrity.testing;

import java.awt.event.KeyEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.awt.Event;
import java.awt.BorderLayout;

import javax.imageio.ImageIO;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.KeyStroke;
import java.awt.Point;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JMenuItem;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JFrame;
import javax.swing.JDialog;
import javax.swing.JSplitPane;
import javax.swing.JButton;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Image;

import javax.swing.JTextField;
import javax.swing.JComboBox;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.security.SecureRandom;
import java.util.Vector;

import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;

/**
 * Inkerator is a testing application for invisible ink printers. It utilizes
 * the "InvisibleInkFactory" library to generate the images and allows a user
 * to save them to file.
 * 
 * 
 * @author Richard Carback
 * @version 0.1.0 
 * @date 12/11/09
 */
public class Inkerator {

	private JFrame jFrame = null;  //  @jve:decl-index=0:visual-constraint="2,7"
	private JPanel jContentPane = null;
	private JMenuBar jJMenuBar = null;
	private JMenu fileMenu = null;
	private JMenu editMenu = null;
	private JMenu helpMenu = null;
	private JMenuItem exitMenuItem = null;
	private JMenuItem aboutMenuItem = null;
	private JMenuItem cutMenuItem = null;
	private JMenuItem copyMenuItem = null;
	private JMenuItem pasteMenuItem = null;
	private JMenuItem saveMenuItem = null;
	private JDialog aboutDialog = null;
	private JPanel aboutContentPane = null;
	private JLabel aboutVersionLabel = null;
	private JSplitPane jSplitPane = null;
	private JPanel jPanel = null;
	private JButton Update = null;
	private JButton Save = null;
	private JTextField ImageText = null;
	private JLabel ImageTextLabel = null;
	private JTextField vGridSizeString = null;
	private JLabel vGridSizeLabel = null;
	private JLabel hGridLabel = null;
	private JTextField vGridSpaces = null;
	private JTextField hGridSizes = null;
	private JLabel vGridSpaceLabel = null;
	private JTextField hGridSpaces = null;
	private JLabel hGridSpacesLabel = null;
	private JComboBox FontChooser = null;
	private JLabel fontChooserLabel = null;
	private InvisibleInkFactory imgFactory = null;  //  @jve:decl-index=0:
	private SecureRandom c_csprng = null;  //  @jve:decl-index=0:
	private File c_file = null;  //  @jve:decl-index=0:
	private PrintStream c_stream = null;
	private BufferedImage c_img = null;
	private JLabel ImageLabel = null;
	private JPanel jPanel1 = null;
	private JScrollPane jScrollPane = null;
	private JLabel ZoomLabel = null;
	private JLabel FontSizeLabel = null;
	private int c_c = 0;
	private JSpinner ZoomSpinner = null;
	private JSpinner FontSpinner = null;
	private JLabel SeedLabel = null;
	private JSpinner SeedSpinner = null;
	private JLabel MungeLevelLabel = null;
	private JTextField MungeLevelTextField = null;
	private JTextField MaskLevelTextField = null;
	private JLabel MaskLevelLabel = null;
	/**
	 * This method initializes jFrame
	 * 
	 * @return javax.swing.JFrame
	 */
	private JFrame getJFrame() {
		if (jFrame == null) {
			jFrame = new JFrame();
			jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			jFrame.setJMenuBar(getJJMenuBar());
			jFrame.setSize(757, 449);
			jFrame.setContentPane(getJContentPane());
			jFrame.setTitle("Application");
		}
		return jFrame;
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.setLayout(new BorderLayout());
			jContentPane.add(getJSplitPane(), BorderLayout.CENTER);
		}
		return jContentPane;
	}

	/**
	 * This method initializes jJMenuBar	
	 * 	
	 * @return javax.swing.JMenuBar	
	 */
	private JMenuBar getJJMenuBar() {
		if (jJMenuBar == null) {
			jJMenuBar = new JMenuBar();
			jJMenuBar.add(getFileMenu());
			jJMenuBar.add(getEditMenu());
			jJMenuBar.add(getHelpMenu());
		}
		return jJMenuBar;
	}

	/**
	 * This method initializes jMenu	
	 * 	
	 * @return javax.swing.JMenu	
	 */
	private JMenu getFileMenu() {
		if (fileMenu == null) {
			fileMenu = new JMenu();
			fileMenu.setText("File");
			fileMenu.add(getSaveMenuItem());
			fileMenu.add(getExitMenuItem());
		}
		return fileMenu;
	}

	/**
	 * This method initializes jMenu	
	 * 	
	 * @return javax.swing.JMenu	
	 */
	private JMenu getEditMenu() {
		if (editMenu == null) {
			editMenu = new JMenu();
			editMenu.setText("Edit");
			editMenu.add(getCutMenuItem());
			editMenu.add(getCopyMenuItem());
			editMenu.add(getPasteMenuItem());
		}
		return editMenu;
	}

	/**
	 * This method initializes jMenu	
	 * 	
	 * @return javax.swing.JMenu	
	 */
	private JMenu getHelpMenu() {
		if (helpMenu == null) {
			helpMenu = new JMenu();
			helpMenu.setText("Help");
			helpMenu.add(getAboutMenuItem());
		}
		return helpMenu;
	}

	/**
	 * This method initializes jMenuItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getExitMenuItem() {
		if (exitMenuItem == null) {
			exitMenuItem = new JMenuItem();
			exitMenuItem.setText("Exit");
			exitMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					System.exit(0);
				}
			});
		}
		return exitMenuItem;
	}

	/**
	 * This method initializes jMenuItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getAboutMenuItem() {
		if (aboutMenuItem == null) {
			aboutMenuItem = new JMenuItem();
			aboutMenuItem.setText("About");
			aboutMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					JDialog aboutDialog = getAboutDialog();
					aboutDialog.pack();
					Point loc = getJFrame().getLocation();
					loc.translate(20, 20);
					aboutDialog.setLocation(loc);
					aboutDialog.setVisible(true);
				}
			});
		}
		return aboutMenuItem;
	}

	/**
	 * This method initializes aboutDialog	
	 * 	
	 * @return javax.swing.JDialog
	 */
	private JDialog getAboutDialog() {
		if (aboutDialog == null) {
			aboutDialog = new JDialog(getJFrame(), true);
			aboutDialog.setTitle("About");
			aboutDialog.setContentPane(getAboutContentPane());
		}
		return aboutDialog;
	}

	/**
	 * This method initializes aboutContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getAboutContentPane() {
		if (aboutContentPane == null) {
			aboutContentPane = new JPanel();
			aboutContentPane.setLayout(new BorderLayout());
			aboutContentPane.add(getAboutVersionLabel(), BorderLayout.CENTER);
		}
		return aboutContentPane;
	}

	/**
	 * This method initializes aboutVersionLabel	
	 * 	
	 * @return javax.swing.JLabel	
	 */
	private JLabel getAboutVersionLabel() {
		if (aboutVersionLabel == null) {
			aboutVersionLabel = new JLabel();
			aboutVersionLabel.setText("Version 0.1.0");
			aboutVersionLabel.setHorizontalAlignment(SwingConstants.CENTER);
		}
		return aboutVersionLabel;
	}

	/**
	 * This method initializes jMenuItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getCutMenuItem() {
		if (cutMenuItem == null) {
			cutMenuItem = new JMenuItem();
			cutMenuItem.setText("Cut");
			cutMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X,
					Event.CTRL_MASK, true));
		}
		return cutMenuItem;
	}

	/**
	 * This method initializes jMenuItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getCopyMenuItem() {
		if (copyMenuItem == null) {
			copyMenuItem = new JMenuItem();
			copyMenuItem.setText("Copy");
			copyMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C,
					Event.CTRL_MASK, true));
		}
		return copyMenuItem;
	}

	/**
	 * This method initializes jMenuItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getPasteMenuItem() {
		if (pasteMenuItem == null) {
			pasteMenuItem = new JMenuItem();
			pasteMenuItem.setText("Paste");
			pasteMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V,
					Event.CTRL_MASK, true));
		}
		return pasteMenuItem;
	}

	/**
	 * This method initializes jMenuItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getSaveMenuItem() {
		if (saveMenuItem == null) {
			saveMenuItem = new JMenuItem();
			saveMenuItem.setText("Save");
			saveMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
					Event.CTRL_MASK, true));
			saveMenuItem.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					Save();				
				}
			});
		}
		return saveMenuItem;
	}

	/**
	 * This method initializes jSplitPane	
	 * 	
	 * @return javax.swing.JSplitPane	
	 */
	private JSplitPane getJSplitPane() {
		if (jSplitPane == null) {
			jSplitPane = new JSplitPane();
			jSplitPane.setLeftComponent(getJPanel());
			jSplitPane.setRightComponent(getJScrollPane());
		}
		return jSplitPane;
	}

	/**
	 * This method initializes jPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanel() {
		if (jPanel == null) {
			GridBagConstraints gridBagConstraints61 = new GridBagConstraints();
			gridBagConstraints61.gridx = 0;
			gridBagConstraints61.anchor = GridBagConstraints.WEST;
			gridBagConstraints61.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints61.gridy = 15;
			MaskLevelLabel = new JLabel();
			MaskLevelLabel.setText("Mask Level");
			GridBagConstraints gridBagConstraints52 = new GridBagConstraints();
			gridBagConstraints52.fill = GridBagConstraints.BOTH;
			gridBagConstraints52.gridy = 15;
			gridBagConstraints52.weightx = 1.0;
			gridBagConstraints52.anchor = GridBagConstraints.WEST;
			gridBagConstraints52.gridx = 1;
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.fill = GridBagConstraints.BOTH;
			gridBagConstraints3.gridy = 14;
			gridBagConstraints3.weightx = 1.0;
			gridBagConstraints3.anchor = GridBagConstraints.WEST;
			gridBagConstraints3.gridx = 1;
			GridBagConstraints gridBagConstraints22 = new GridBagConstraints();
			gridBagConstraints22.gridx = 0;
			gridBagConstraints22.anchor = GridBagConstraints.WEST;
			gridBagConstraints22.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints22.gridy = 14;
			MungeLevelLabel = new JLabel();
			MungeLevelLabel.setText("Munge Level");
			GridBagConstraints gridBagConstraints51 = new GridBagConstraints();
			gridBagConstraints51.gridx = 1;
			gridBagConstraints51.fill = GridBagConstraints.BOTH;
			gridBagConstraints51.anchor = GridBagConstraints.WEST;
			gridBagConstraints51.gridy = 9;
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.gridx = 0;
			gridBagConstraints2.anchor = GridBagConstraints.WEST;
			gridBagConstraints2.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints2.gridy = 9;
			SeedLabel = new JLabel();
			SeedLabel.setText("Random Seed:");
			GridBagConstraints gridBagConstraints17 = new GridBagConstraints();
			gridBagConstraints17.gridx = 1;
			gridBagConstraints17.fill = GridBagConstraints.BOTH;
			gridBagConstraints17.gridwidth = 2;
			gridBagConstraints17.gridy = 12;
			GridBagConstraints gridBagConstraints16 = new GridBagConstraints();
			gridBagConstraints16.gridx = 1;
			gridBagConstraints16.fill = GridBagConstraints.BOTH;
			gridBagConstraints16.gridy = 13;
			GridBagConstraints gridBagConstraints18 = new GridBagConstraints();
			gridBagConstraints18.gridx = 0;
			gridBagConstraints18.anchor = GridBagConstraints.WEST;
			gridBagConstraints18.gridy = 12;
			FontSizeLabel = new JLabel();
			FontSizeLabel.setText("Font Size:");
			GridBagConstraints gridBagConstraints15 = new GridBagConstraints();
			gridBagConstraints15.gridx = 0;
			gridBagConstraints15.anchor = GridBagConstraints.WEST;
			gridBagConstraints15.gridy = 13;
			ZoomLabel = new JLabel();
			ZoomLabel.setText("Zoom:");
			GridBagConstraints gridBagConstraints14 = new GridBagConstraints();
			gridBagConstraints14.gridx = 0;
			gridBagConstraints14.anchor = GridBagConstraints.WEST;
			gridBagConstraints14.gridy = 10;
			fontChooserLabel = new JLabel();
			fontChooserLabel.setText("Font:");
			GridBagConstraints gridBagConstraints13 = new GridBagConstraints();
			gridBagConstraints13.fill = GridBagConstraints.BOTH;
			gridBagConstraints13.gridy = 10;
			gridBagConstraints13.weightx = 1.0;
			gridBagConstraints13.anchor = GridBagConstraints.WEST;
			gridBagConstraints13.gridx = 1;
			GridBagConstraints gridBagConstraints12 = new GridBagConstraints();
			gridBagConstraints12.gridx = 0;
			gridBagConstraints12.anchor = GridBagConstraints.WEST;
			gridBagConstraints12.gridy = 7;
			hGridSpacesLabel = new JLabel();
			hGridSpacesLabel.setText("Horizonal Grid Spaces:");
			GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
			gridBagConstraints11.fill = GridBagConstraints.BOTH;
			gridBagConstraints11.gridy = 7;
			gridBagConstraints11.weightx = 1.0;
			gridBagConstraints11.anchor = GridBagConstraints.WEST;
			gridBagConstraints11.gridx = 1;
			GridBagConstraints gridBagConstraints10 = new GridBagConstraints();
			gridBagConstraints10.gridx = 0;
			gridBagConstraints10.anchor = GridBagConstraints.WEST;
			gridBagConstraints10.gridy = 6;
			vGridSpaceLabel = new JLabel();
			vGridSpaceLabel.setText("Vertical Grid Spaces:");
			GridBagConstraints gridBagConstraints9 = new GridBagConstraints();
			gridBagConstraints9.fill = GridBagConstraints.BOTH;
			gridBagConstraints9.gridy = 5;
			gridBagConstraints9.weightx = 1.0;
			gridBagConstraints9.anchor = GridBagConstraints.WEST;
			gridBagConstraints9.gridx = 1;
			GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
			gridBagConstraints7.fill = GridBagConstraints.BOTH;
			gridBagConstraints7.gridy = 6;
			gridBagConstraints7.weightx = 1.0;
			gridBagConstraints7.anchor = GridBagConstraints.WEST;
			gridBagConstraints7.gridx = 1;
			GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
			gridBagConstraints6.gridx = 0;
			gridBagConstraints6.anchor = GridBagConstraints.WEST;
			gridBagConstraints6.gridy = 5;
			hGridLabel = new JLabel();
			hGridLabel.setText("Horizontal Grid Sizes:");
			GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
			gridBagConstraints5.gridx = 0;
			gridBagConstraints5.anchor = GridBagConstraints.WEST;
			gridBagConstraints5.gridy = 3;
			vGridSizeLabel = new JLabel();
			vGridSizeLabel.setText("Vertical Grid Sizes:");
			GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
			gridBagConstraints4.fill = GridBagConstraints.BOTH;
			gridBagConstraints4.gridy = 3;
			gridBagConstraints4.weightx = 1.0;
			gridBagConstraints4.anchor = GridBagConstraints.WEST;
			gridBagConstraints4.gridx = 1;
			GridBagConstraints gridBagConstraints31 = new GridBagConstraints();
			gridBagConstraints31.gridx = 0;
			gridBagConstraints31.anchor = GridBagConstraints.WEST;
			gridBagConstraints31.gridy = 1;
			ImageTextLabel = new JLabel();
			ImageTextLabel.setText("Image Text:");
			GridBagConstraints gridBagConstraints21 = new GridBagConstraints();
			gridBagConstraints21.fill = GridBagConstraints.BOTH;
			gridBagConstraints21.gridy = 1;
			gridBagConstraints21.weightx = 1.0;
			gridBagConstraints21.gridwidth = 1;
			gridBagConstraints21.anchor = GridBagConstraints.WEST;
			gridBagConstraints21.gridx = 1;
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.gridx = 1;
			gridBagConstraints1.gridy = 16;
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 16;
			jPanel = new JPanel();
			jPanel.setLayout(new GridBagLayout());
			jPanel.add(getUpdate(), gridBagConstraints);
			jPanel.add(getSave(), gridBagConstraints1);
			jPanel.add(getImageText(), gridBagConstraints21);
			jPanel.add(ImageTextLabel, gridBagConstraints31);
			jPanel.add(getVGridSizeString(), gridBagConstraints4);
			jPanel.add(vGridSizeLabel, gridBagConstraints5);
			jPanel.add(hGridLabel, gridBagConstraints6);
			jPanel.add(getVGridSpaces(), gridBagConstraints7);
			jPanel.add(getHGridSizes(), gridBagConstraints9);
			jPanel.add(vGridSpaceLabel, gridBagConstraints10);
			jPanel.add(getHGridSpaces(), gridBagConstraints11);
			jPanel.add(hGridSpacesLabel, gridBagConstraints12);
			jPanel.add(getFontChooser(), gridBagConstraints13);
			jPanel.add(fontChooserLabel, gridBagConstraints14);
			jPanel.add(ZoomLabel, gridBagConstraints15);
			jPanel.add(FontSizeLabel, gridBagConstraints18);
			jPanel.add(getZoomSpinner(), gridBagConstraints16);
			jPanel.add(getFontSpinner(), gridBagConstraints17);
			jPanel.add(SeedLabel, gridBagConstraints2);
			jPanel.add(getSeedSpinner(), gridBagConstraints51);
			jPanel.add(MungeLevelLabel, gridBagConstraints22);
			jPanel.add(getMungeLevelTextField(), gridBagConstraints3);
			jPanel.add(getMaskLevelTextField(), gridBagConstraints52);
			jPanel.add(MaskLevelLabel, gridBagConstraints61);
		}
		return jPanel;
	}

	/**
	 * This method initializes Update	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getUpdate() {
		if (Update == null) {
			Update = new JButton();
			Update.setText("Update");
			Update.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					UpdateImage();
				}
			});
		}
		return Update;
	}

	/**
	 * This method initializes Save	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getSave() {
		if (Save == null) {
			Save = new JButton();
			Save.setText("Save");
			Save.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					Save();
				}
			});
		}
		return Save;
	}

	/**
	 * This method initializes ImageText	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getImageText() {
		if (ImageText == null) {
			ImageText = new JTextField();
			ImageText.setText("ABCD");

			ImageText.addKeyListener(new java.awt.event.KeyAdapter() {
				public void keyReleased(java.awt.event.KeyEvent e) {
					UpdateImage();
				}
			});
		}
		return ImageText;
	}

	/**
	 * This method initializes vGridSizeString	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getVGridSizeString() {
		if (vGridSizeString == null) {
			vGridSizeString = new JTextField();
			vGridSizeString.setText("5");
		}
		return vGridSizeString;
	}

	/**
	 * This method initializes vGridSpaces	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getVGridSpaces() {
		if (vGridSpaces == null) {
			vGridSpaces = new JTextField();
			vGridSpaces.setText("1");
		}
		return vGridSpaces;
	}

	/**
	 * This method initializes hGridSizes	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getHGridSizes() {
		if (hGridSizes == null) {
			hGridSizes = new JTextField();
			hGridSizes.setText("5");
		}
		return hGridSizes;
	}

	/**
	 * This method initializes hGridSpaces	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getHGridSpaces() {
		if (hGridSpaces == null) {
			hGridSpaces = new JTextField();
			hGridSpaces.setText("1");
		}
		return hGridSpaces;
	}

	/**
	 * This method initializes FontChooser	
	 * 	
	 * @return javax.swing.JComboBox	
	 */
	private JComboBox getFontChooser() {
		if (FontChooser == null) {
			FontChooser = new JComboBox();
			// Check to see if the font exists on the system
			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			String[] fontFamilies = ge.getAvailableFontFamilyNames();
			int l_index = 0;
			int l_c = 0;
			for (String s : fontFamilies) {
				FontChooser.addItem(s);
				if (s == "SansSerif") l_index = l_c;
				l_c++;
			}
			try {
				FontChooser.setSelectedIndex(l_index);
				FontChooser.addItemListener(new java.awt.event.ItemListener() {
					public void itemStateChanged(java.awt.event.ItemEvent e) {
						UpdateImage();
					}
				});
			} catch (Exception e) {
				//nothing.
			}
			
		}
		return FontChooser;
	}
	
	private void UpdateImage() {
		if (imgFactory == null) imgFactory = new InvisibleInkFactory();
		if (c_file == null) {
			c_stream = new PrintStream(System.out);
			c_file = new File("inkerator.log");
			if (c_file.canWrite()) {
				try {
					c_stream = new PrintStream(c_file);
				} catch (FileNotFoundException e) {
					System.out.println("Error: Cannot write logfile, will use console!");
					c_stream.println(e.getStackTrace().toString());					
				}
			} else {
				System.out.println("Error: Cannot write logfile, will use console!");
			}
		}
		try {
			c_csprng = SecureRandom.getInstance("SHA1PRNG");
			long seed = System.currentTimeMillis();
			c_stream.println("Seed: " + seed);
			c_csprng.setSeed(Integer.parseInt(SeedSpinner.getValue().toString()));
			imgFactory.setCSPRNG(c_csprng);
		} catch (Exception e) {
			c_stream.println(e.getStackTrace().toString());					
			c_csprng = null;
		}					
		JLabel l_imgLabel = getImageLabel();
		//Get Settings                 
		String l_imgText = getImageText().getText();
		double l_zoom = (double)Integer.parseInt(ZoomSpinner.getValue().toString())/10;
		
		float l_mask = (float)Float.parseFloat(MaskLevelTextField.getText());
		if (l_mask < 0 || l_mask > 1) {
			l_mask = 0;
			MaskLevelTextField.setText("0");
		}		
		imgFactory.setMaskLevel(l_mask);
		
		float l_munge = (float)Float.parseFloat(MungeLevelTextField.getText());
		if (l_munge < 0 || l_munge > 1) {
			l_munge = 0;
			MungeLevelTextField.setText("0");
		}		
		imgFactory.setMungeLevel(l_munge);		
		
		imgFactory.setGrid(GetList(getVGridSizeString(), 5), 
						   GetList(getVGridSpaces(), 1),
						   GetList(getHGridSizes(), 5), 
						   GetList(getHGridSpaces(), 1));
		
		Font l_font = new Font((String)getFontChooser().getSelectedItem(), 
				   Font.BOLD, Integer.parseInt(FontSpinner.getValue().toString()));
		imgFactory.setFont(l_font);
		
		long l_time = System.currentTimeMillis();
		c_img = imgFactory.getBufferedImage(l_imgText);
		l_time = System.currentTimeMillis() - l_time;
		
		Image l_result = c_img.getScaledInstance((int)(c_img.getWidth()*l_zoom), 
											  (int)(c_img.getHeight()*l_zoom), 
												BufferedImage.SCALE_FAST);	
		
		ImageIcon l_icon = new ImageIcon(l_result);
		l_imgLabel.setIcon(l_icon);
		l_imgLabel.repaint();
		
		c_stream.println("==Image Generated==");
		c_stream.print(", Text=" + l_imgText);
		c_stream.print(", vGridSizes= { ");
		c_stream.print(" }, vGridSpaces= { ");
		c_stream.print(" }, hGridSizes= { ");
		c_stream.print(" }, hGridSpaces= { ");	
		c_stream.println(" }, zoom=" + l_zoom);
		c_stream.print("Font Name=" + l_font.getName());
		c_stream.println(", Size=" + l_font.getSize());
		c_stream.println("Generated in: " + l_time + "ms");
	}
	
	private Integer[] GetList(JTextField l_txtField, int l_default) {
		String l_str = l_txtField.getText();
		String[] l_list = l_str.split(",", l_str.length());
		Vector<Integer> l_ret = new Vector<Integer>();
		int l_tmp = -1;
		for (int l_i = 0; l_i < l_list.length; l_i++) {
			try {
				l_tmp = Integer.parseInt(l_list[l_i]);
			} catch (Exception e) {
				l_tmp = -1;
			}
			if (l_tmp >= 0) {
				l_ret.add(l_tmp);
				l_tmp = -1;
			}			
		}
		if (l_ret.size() == 0) l_ret.add(l_default);
		Integer[] l_int = new Integer[l_ret.size()];
		l_ret.toArray(l_int);
		return l_int;
	}
	
	/**
	 * This method initializes ImageLabel	
	 * 	
	 * @return javax.swing.JLabel	
	 */
	private JLabel getImageLabel() {
		if (ImageLabel == null) {
			ImageLabel = new JLabel();
			ImageLabel.setText("");
		}
		return ImageLabel;
	}
	
	private void Save() {
		File out = new File("inkeratorout" + c_c + ".png");
		try {
			ImageIO.write(c_img, "png", out);
			c_c++;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}

	/**
	 * This method initializes jPanel1	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanel1() {
		if (jPanel1 == null) {
			GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
			gridBagConstraints8.gridx = 0;
			gridBagConstraints8.gridy = 0;
			jPanel1 = new JPanel();
			jPanel1.setLayout(new GridBagLayout());
			jPanel1.add(getImageLabel(), gridBagConstraints8);
		}
		return jPanel1;
	}

	/**
	 * This method initializes jScrollPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getJScrollPane() {
		if (jScrollPane == null) {
			jScrollPane = new JScrollPane();
			jScrollPane.setViewportView(getJPanel1());
		}
		return jScrollPane;
	}

	/**
	 * This method initializes ZoomSpinner	
	 * 	
	 * @return javax.swing.JSpinner	
	 */
	private JSpinner getZoomSpinner() {
		if (ZoomSpinner == null) {
			ZoomSpinner = new JSpinner();
			ZoomSpinner.setValue(10);

			ZoomSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
				public void stateChanged(javax.swing.event.ChangeEvent e) {
					UpdateImage();
				}
			});
		}
		return ZoomSpinner;
	}

	/**
	 * This method initializes FontSpinner	
	 * 	
	 * @return javax.swing.JSpinner	
	 */
	private JSpinner getFontSpinner() {
		if (FontSpinner == null) {
			FontSpinner = new JSpinner();
			FontSpinner.setValue(96);
			FontSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
				public void stateChanged(javax.swing.event.ChangeEvent e) {
					UpdateImage();
				}
			});
		}
		return FontSpinner;
	}

	/**
	 * This method initializes SeedSpinner	
	 * 	
	 * @return javax.swing.JSpinner	
	 */
	private JSpinner getSeedSpinner() {
		if (SeedSpinner == null) {
			SeedSpinner = new JSpinner();
			SeedSpinner.setValue(1024);
			SeedSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
				public void stateChanged(javax.swing.event.ChangeEvent e) {
					UpdateImage();
				}
			});
		}
		return SeedSpinner;
	}

	/**
	 * This method initializes MungeLevelTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getMungeLevelTextField() {
		if (MungeLevelTextField == null) {
			MungeLevelTextField = new JTextField();
			MungeLevelTextField.setText(".5");
		}
		return MungeLevelTextField;
	}

	/**
	 * This method initializes MaskLevelTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getMaskLevelTextField() {
		if (MaskLevelTextField == null) {
			MaskLevelTextField = new JTextField();
			MaskLevelTextField.setText(".5");
		}
		return MaskLevelTextField;
	}

	/**
	 * Launches this application
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {				
				Inkerator application = new Inkerator();
				application.getJFrame().setVisible(true);
				application.UpdateImage();
			}
		});
	}

}