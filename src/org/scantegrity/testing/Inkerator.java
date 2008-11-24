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

//Utility libs
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.security.SecureRandom;
import java.util.Vector;
//AWT graphical elements
import java.awt.event.KeyEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.awt.Color;
import java.awt.Event;
import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Image;
//Swing GUI elements
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.KeyStroke;
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
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.JComboBox;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfWriter;

import org.scantegrity.lib.CMYKColorSpace;
import org.scantegrity.lib.InvisibleInkFactory;
import java.awt.Dimension;
import javax.swing.JTextArea;

/**
 * Inkerator is a testing application for invisible ink printers. It utilizes
 * the "InvisibleInkFactory" library to generate the images and allows a user
 * to save them to file.
 * 
 * 
 * @author Richard Carback
 * @version 0.3.1
 * @date 22/11/09
 */
public class Inkerator {

	//Auto-Generated by Visual Eclipse Project
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
	private JDialog aboutDialog = null;  //  @jve:decl-index=0:visual-constraint="839,10"
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
	private JLabel ImageLabel = null;
	private JPanel jPanel1 = null;
	private JScrollPane jScrollPane = null;
	private JLabel ZoomLabel = null;
	private JLabel FontSizeLabel = null;
	private JSpinner FontSpinner = null;
	private JLabel SeedLabel = null;
	private JSpinner SeedSpinner = null;
	//Manually Added variables.
	private InvisibleInkFactory imgFactory = null;  //  @jve:decl-index=0:
	private SecureRandom c_csprng = null;  //  @jve:decl-index=0:
	private File c_file = null;  //  @jve:decl-index=0:
	private PrintStream c_stream = null;
	private BufferedImage c_img = null;
	private int c_c = 0;
	private JTextArea DirectionsTextArea = null;
	private JLabel FontColorRangeLabel = null;
	private JLabel BGColorRangeLabel = null;
	private JLabel MaskColorRangeLabel = null;
	private JTextField FontColorRange = null;
	private JTextField BGColorRange = null;
	private JTextField MaskColorRange = null;
	private JLabel ImgHeightLabel = null;
	private JLabel imgWidthLabel = null;
	private JTextField ImgHeight = null;
	private JTextField ImgWidth = null;
	private JTextField Zoom = null;
	private String c_imgDetails = null;
	private JLabel ColorOrderingLabel = null;
	private JTextField ColorOrdering = null;
	/**
	 * GetList - Grabs a list of comma separated integer values from a 
	 * JTextField element. 
	 *
	 * @param l_txtField - The textfield to parse.
	 * @param l_default - a default value to return if the parse is broken.
	 * @return A list of integers that were comma separated.
	 */
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
	 * GetFloatList - Grabs a list of comma separated float values from a 
	 * JTextField element. 
	 *
	 * @param p_txtField - The textfield to parse.
	 * @param p_default - a default value to return if the parse is broken.
	 * @param p_size - the limit of values to return, set to 0 for no limit.
	 * @return A list of floats that were comma separated.
	 */
	private float[] GetFloatList(JTextField p_txtField, float[] p_default,
								 int p_size) {
		String l_str = p_txtField.getText();
		String[] l_list = l_str.split(",", l_str.length());
		float l_tmp = -1;
		int l_size = l_list.length;
		if (p_size != 0) l_size = Math.min(l_size, p_size);

		float[] l_ret = new float[l_size];		
		for (int l_i = 0; l_i < l_size; l_i++) {
			if (l_i < p_default.length) l_ret[l_i] = p_default[l_i];
			else l_ret[l_i] = 0;
			try {
				l_tmp = Float.parseFloat(l_list[l_i]);
			} catch (Exception e) {
				l_tmp = -1;
			}
			if (l_tmp >= 0) {
				l_ret[l_i] = l_tmp;
				l_tmp = -1;
			}			
		}
		return l_ret;
	}	
	
	/**
	 * Save - Saves the current image as a PDF.
	 */
	private void Save() {
		try {
			Raster l_tmpRaster = c_img.getRaster();
			DataBuffer l_db = l_tmpRaster.getDataBuffer();
			byte[] l_bytes = new byte[l_db.getSize()];

			for (int l_i = 0; l_i < l_bytes.length; l_i++) {
				l_bytes[l_i] = (byte)Math.round(l_db.getElemFloat(l_i)*(float)255);
				System.out.print(l_bytes[l_i] + ", ");
				if (l_i%3 == 0) System.out.println(""); 
			}
			
			
			com.lowagie.text.Image l_img = com.lowagie.text.Image.getInstance(
														l_tmpRaster.getWidth(), 
														l_tmpRaster.getHeight(), 
														4, 8, l_bytes);
			l_img.setDpi(300, 300);

			Document l_doc = new Document(new Rectangle(0,0,l_img.getWidth(), 
															l_img.getHeight()));
			l_doc.setMargins(0,0,0,0);

			PdfWriter.getInstance(l_doc, 
						new FileOutputStream("inkeratorout" + c_c + ".pdf"));
			l_doc.open();
			l_doc.add(l_img);
			l_doc.close();		
			c_c++;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	/**
	 * UpdateImage - Updates the image display.
	 */
	private void UpdateImage() {
		CMYKColorSpace l_cs = new CMYKColorSpace();
		c_imgDetails = "--Image Details-- \n";
		
		if (imgFactory == null) imgFactory = new InvisibleInkFactory();
		
		try {
			c_csprng = SecureRandom.getInstance("SHA1PRNG");
			long l_seed = Integer.parseInt(SeedSpinner.getValue().toString());
			c_csprng.setSeed(l_seed);
			c_imgDetails += "Seed: " + l_seed + "\n";
			imgFactory.setCSPRNG(c_csprng);
		} catch (Exception e) {
			c_csprng = null;
		}
		
		float l_colors[][] = { {(float).5,0,0,0},
								{0,(float).5,0,0},
								{0,0,(float).5,0},
								{0,0,0,(float).5}
							 };
		
		//Font Color
		float[] l_rangeDefault = {0, 1};
		float[] l_BGColorRange = CMYKColorSpace.normalize(GetFloatList(
																BGColorRange, 
																l_rangeDefault, 
																2));
		float[] l_maskColorRange = CMYKColorSpace.normalize(GetFloatList(
																MaskColorRange, 
																l_rangeDefault, 
																2));
		
		float[] l_fontColorRange = CMYKColorSpace.normalize(GetFloatList(
				FontColorRange, 
				l_rangeDefault, 
				2));
		float l_mid = (l_fontColorRange[1] + l_fontColorRange[0])/2;
		float l_range = Math.abs(l_fontColorRange[1]-l_fontColorRange[0])/2;
		
		
		
		imgFactory.setMaskColor(new Color(l_cs, l_colors[0], 1));
		imgFactory.setForegroundColor(new Color(l_cs, l_colors[1], 1));
		imgFactory.setBackgroundColor(new Color(l_cs, l_colors[2], 1));
		imgFactory.setMungeColor(new Color(l_cs, l_colors[3], 1));
		
		//Get Settings                 
		String l_imgText = getImageText().getText();
		c_imgDetails += "Text: " + l_imgText + "\n";
		double l_zoom = Double.parseDouble((Zoom.getText()));
		c_imgDetails += "Zoom: " + l_zoom + "\n";
		
		Integer[][] l_grids = {GetList(getVGridSizeString(), 5), 
							 GetList(getVGridSpaces(), 1),
							 GetList(getHGridSizes(), 5), 
							 GetList(getHGridSpaces(), 1)};
		
		c_imgDetails += "vGridSize: " + l_grids[0].toString() + "\n";
		c_imgDetails += "vGridSpaceSize: " + l_grids[1].toString() + "\n";
		c_imgDetails += "hGridSize: " + l_grids[2].toString() + "\n";
		c_imgDetails += "hGridSpaceSize: " + l_grids[3].toString() + "\n";
		
		imgFactory.setGrid(l_grids[0], l_grids[1], l_grids[2], l_grids[3]); 
		
		
		
		Font l_font = new Font((String)getFontChooser().getSelectedItem(), 
				   Font.BOLD, Integer.parseInt(FontSpinner.getValue().toString()));
		imgFactory.setFont(l_font);
		c_imgDetails += "Font: " + l_font.getName() + "\n";
		c_imgDetails += "Font Size: " + l_font.getSize() + "\n";
		
		long l_time = System.currentTimeMillis();
		c_img = imgFactory.getBufferedImage(l_imgText);
		l_time = System.currentTimeMillis() - l_time;
		
		c_imgDetails += "Time to Generate: " + l_time + "\n";
		
		//Display image in the GUI.
		Image l_result = c_img.getScaledInstance((int)(c_img.getWidth()*l_zoom), 
											  (int)(c_img.getHeight()*l_zoom), 
												BufferedImage.SCALE_FAST);	
		ImageIcon l_icon = new ImageIcon(l_result);
		JLabel l_imgLabel = getImageLabel();
		l_imgLabel.setIcon(l_icon);
		l_imgLabel.repaint();
	}		
	
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
			jFrame.setSize(827, 522);
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
			aboutDialog.setSize(new Dimension(328, 111));
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
			BorderLayout borderLayout = new BorderLayout();
			borderLayout.setHgap(0);
			borderLayout.setVgap(0);
			aboutContentPane = new JPanel();
			aboutContentPane.setLayout(borderLayout);
			aboutContentPane.add(getAboutVersionLabel(), BorderLayout.SOUTH);
			aboutContentPane.add(getDirectionsTextArea(), BorderLayout.CENTER);
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
			aboutVersionLabel.setText("Version 0.2.0");
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
			GridBagConstraints gridBagConstraints24 = new GridBagConstraints();
			gridBagConstraints24.fill = GridBagConstraints.BOTH;
			gridBagConstraints24.gridy = 17;
			gridBagConstraints24.weightx = 1.0;
			gridBagConstraints24.anchor = GridBagConstraints.WEST;
			gridBagConstraints24.gridx = 1;
			GridBagConstraints gridBagConstraints23 = new GridBagConstraints();
			gridBagConstraints23.gridx = 0;
			gridBagConstraints23.anchor = GridBagConstraints.WEST;
			gridBagConstraints23.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints23.gridy = 17;
			ColorOrderingLabel = new JLabel();
			ColorOrderingLabel.setText("Color Ordering:");
			GridBagConstraints gridBagConstraints22 = new GridBagConstraints();
			gridBagConstraints22.fill = GridBagConstraints.BOTH;
			gridBagConstraints22.gridy = 16;
			gridBagConstraints22.weightx = 1.0;
			gridBagConstraints22.anchor = GridBagConstraints.WEST;
			gridBagConstraints22.gridx = 1;
			GridBagConstraints gridBagConstraints211 = new GridBagConstraints();
			gridBagConstraints211.fill = GridBagConstraints.BOTH;
			gridBagConstraints211.gridy = 22;
			gridBagConstraints211.weightx = 1.0;
			gridBagConstraints211.anchor = GridBagConstraints.WEST;
			gridBagConstraints211.gridx = 1;
			GridBagConstraints gridBagConstraints19 = new GridBagConstraints();
			gridBagConstraints19.fill = GridBagConstraints.BOTH;
			gridBagConstraints19.gridy = 21;
			gridBagConstraints19.weightx = 1.0;
			gridBagConstraints19.anchor = GridBagConstraints.WEST;
			gridBagConstraints19.gridx = 1;
			GridBagConstraints gridBagConstraints181 = new GridBagConstraints();
			gridBagConstraints181.gridx = 0;
			gridBagConstraints181.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints181.anchor = GridBagConstraints.WEST;
			gridBagConstraints181.gridy = 22;
			imgWidthLabel = new JLabel();
			imgWidthLabel.setText("Save Width:");
			GridBagConstraints gridBagConstraints171 = new GridBagConstraints();
			gridBagConstraints171.gridx = 0;
			gridBagConstraints171.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints171.anchor = GridBagConstraints.WEST;
			gridBagConstraints171.gridy = 21;
			ImgHeightLabel = new JLabel();
			ImgHeightLabel.setText("Save Height:");
			GridBagConstraints gridBagConstraints131 = new GridBagConstraints();
			gridBagConstraints131.fill = GridBagConstraints.BOTH;
			gridBagConstraints131.gridy = 20;
			gridBagConstraints131.weightx = 1.0;
			gridBagConstraints131.anchor = GridBagConstraints.WEST;
			gridBagConstraints131.gridx = 1;
			GridBagConstraints gridBagConstraints111 = new GridBagConstraints();
			gridBagConstraints111.fill = GridBagConstraints.BOTH;
			gridBagConstraints111.gridy = 19;
			gridBagConstraints111.weightx = 1.0;
			gridBagConstraints111.anchor = GridBagConstraints.WEST;
			gridBagConstraints111.gridx = 1;
			GridBagConstraints gridBagConstraints101 = new GridBagConstraints();
			gridBagConstraints101.fill = GridBagConstraints.BOTH;
			gridBagConstraints101.gridy = 18;
			gridBagConstraints101.weightx = 1.0;
			gridBagConstraints101.anchor = GridBagConstraints.WEST;
			gridBagConstraints101.gridx = 1;
			GridBagConstraints gridBagConstraints91 = new GridBagConstraints();
			gridBagConstraints91.gridx = 0;
			gridBagConstraints91.anchor = GridBagConstraints.WEST;
			gridBagConstraints91.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints91.gridy = 20;
			MaskColorRangeLabel = new JLabel();
			MaskColorRangeLabel.setText("Mask Color Range:");
			GridBagConstraints gridBagConstraints81 = new GridBagConstraints();
			gridBagConstraints81.gridx = 0;
			gridBagConstraints81.anchor = GridBagConstraints.WEST;
			gridBagConstraints81.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints81.gridy = 19;
			BGColorRangeLabel = new JLabel();
			BGColorRangeLabel.setText("BG Color Range:");
			GridBagConstraints gridBagConstraints71 = new GridBagConstraints();
			gridBagConstraints71.gridx = 0;
			gridBagConstraints71.anchor = GridBagConstraints.WEST;
			gridBagConstraints71.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints71.gridy = 18;
			FontColorRangeLabel = new JLabel();
			FontColorRangeLabel.setText("Font Color Range:");
			GridBagConstraints gridBagConstraints51 = new GridBagConstraints();
			gridBagConstraints51.gridx = 1;
			gridBagConstraints51.fill = GridBagConstraints.BOTH;
			gridBagConstraints51.anchor = GridBagConstraints.WEST;
			gridBagConstraints51.gridy = 12;
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.gridx = 0;
			gridBagConstraints2.anchor = GridBagConstraints.WEST;
			gridBagConstraints2.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints2.gridy = 12;
			SeedLabel = new JLabel();
			SeedLabel.setText("Random Seed:");
			GridBagConstraints gridBagConstraints17 = new GridBagConstraints();
			gridBagConstraints17.gridx = 1;
			gridBagConstraints17.fill = GridBagConstraints.BOTH;
			gridBagConstraints17.gridwidth = 2;
			gridBagConstraints17.gridy = 15;
			GridBagConstraints gridBagConstraints18 = new GridBagConstraints();
			gridBagConstraints18.gridx = 0;
			gridBagConstraints18.anchor = GridBagConstraints.WEST;
			gridBagConstraints18.gridy = 15;
			FontSizeLabel = new JLabel();
			FontSizeLabel.setText("Font Size:");
			GridBagConstraints gridBagConstraints15 = new GridBagConstraints();
			gridBagConstraints15.gridx = 0;
			gridBagConstraints15.anchor = GridBagConstraints.WEST;
			gridBagConstraints15.gridy = 16;
			ZoomLabel = new JLabel();
			ZoomLabel.setText("Zoom:");
			GridBagConstraints gridBagConstraints14 = new GridBagConstraints();
			gridBagConstraints14.gridx = 0;
			gridBagConstraints14.anchor = GridBagConstraints.WEST;
			gridBagConstraints14.gridy = 13;
			fontChooserLabel = new JLabel();
			fontChooserLabel.setText("Font:");
			GridBagConstraints gridBagConstraints13 = new GridBagConstraints();
			gridBagConstraints13.fill = GridBagConstraints.BOTH;
			gridBagConstraints13.gridy = 13;
			gridBagConstraints13.weightx = 1.0;
			gridBagConstraints13.anchor = GridBagConstraints.WEST;
			gridBagConstraints13.gridx = 1;
			GridBagConstraints gridBagConstraints12 = new GridBagConstraints();
			gridBagConstraints12.gridx = 0;
			gridBagConstraints12.anchor = GridBagConstraints.WEST;
			gridBagConstraints12.gridy = 10;
			hGridSpacesLabel = new JLabel();
			hGridSpacesLabel.setText("Horizonal Grid Spaces:");
			GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
			gridBagConstraints11.fill = GridBagConstraints.BOTH;
			gridBagConstraints11.gridy = 10;
			gridBagConstraints11.weightx = 1.0;
			gridBagConstraints11.anchor = GridBagConstraints.WEST;
			gridBagConstraints11.gridx = 1;
			GridBagConstraints gridBagConstraints10 = new GridBagConstraints();
			gridBagConstraints10.gridx = 0;
			gridBagConstraints10.anchor = GridBagConstraints.WEST;
			gridBagConstraints10.gridy = 9;
			vGridSpaceLabel = new JLabel();
			vGridSpaceLabel.setText("Vertical Grid Spaces:");
			GridBagConstraints gridBagConstraints9 = new GridBagConstraints();
			gridBagConstraints9.fill = GridBagConstraints.BOTH;
			gridBagConstraints9.gridy = 8;
			gridBagConstraints9.weightx = 1.0;
			gridBagConstraints9.anchor = GridBagConstraints.WEST;
			gridBagConstraints9.gridx = 1;
			GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
			gridBagConstraints7.fill = GridBagConstraints.BOTH;
			gridBagConstraints7.gridy = 9;
			gridBagConstraints7.weightx = 1.0;
			gridBagConstraints7.anchor = GridBagConstraints.WEST;
			gridBagConstraints7.gridx = 1;
			GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
			gridBagConstraints6.gridx = 0;
			gridBagConstraints6.anchor = GridBagConstraints.WEST;
			gridBagConstraints6.gridy = 8;
			hGridLabel = new JLabel();
			hGridLabel.setText("Horizontal Grid Sizes:");
			GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
			gridBagConstraints5.gridx = 0;
			gridBagConstraints5.anchor = GridBagConstraints.WEST;
			gridBagConstraints5.gridy = 6;
			vGridSizeLabel = new JLabel();
			vGridSizeLabel.setText("Vertical Grid Sizes:");
			GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
			gridBagConstraints4.fill = GridBagConstraints.BOTH;
			gridBagConstraints4.gridy = 6;
			gridBagConstraints4.weightx = 1.0;
			gridBagConstraints4.anchor = GridBagConstraints.WEST;
			gridBagConstraints4.gridx = 1;
			GridBagConstraints gridBagConstraints31 = new GridBagConstraints();
			gridBagConstraints31.gridx = 0;
			gridBagConstraints31.anchor = GridBagConstraints.WEST;
			gridBagConstraints31.gridy = 4;
			ImageTextLabel = new JLabel();
			ImageTextLabel.setText("Image Text:");
			GridBagConstraints gridBagConstraints21 = new GridBagConstraints();
			gridBagConstraints21.fill = GridBagConstraints.BOTH;
			gridBagConstraints21.gridy = 4;
			gridBagConstraints21.weightx = 1.0;
			gridBagConstraints21.gridwidth = 1;
			gridBagConstraints21.anchor = GridBagConstraints.WEST;
			gridBagConstraints21.gridx = 1;
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.gridx = 1;
			gridBagConstraints1.gridy = 25;
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 25;
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
			jPanel.add(getFontSpinner(), gridBagConstraints17);
			jPanel.add(SeedLabel, gridBagConstraints2);
			jPanel.add(getSeedSpinner(), gridBagConstraints51);
			jPanel.add(FontColorRangeLabel, gridBagConstraints71);
			jPanel.add(BGColorRangeLabel, gridBagConstraints81);
			jPanel.add(MaskColorRangeLabel, gridBagConstraints91);
			jPanel.add(getFontColorRange(), gridBagConstraints101);
			jPanel.add(getBGColorRange(), gridBagConstraints111);
			jPanel.add(getMaskColorRange(), gridBagConstraints131);
			jPanel.add(ImgHeightLabel, gridBagConstraints171);
			jPanel.add(imgWidthLabel, gridBagConstraints181);
			jPanel.add(getImgHeight(), gridBagConstraints19);
			jPanel.add(getImgWidth(), gridBagConstraints211);
			jPanel.add(getZoom(), gridBagConstraints22);
			jPanel.add(ColorOrderingLabel, gridBagConstraints23);
			jPanel.add(getColorOrdering(), gridBagConstraints24);
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
	 * This method initializes DirectionsTextArea	
	 * 	
	 * @return javax.swing.JTextArea	
	 */
	private JTextArea getDirectionsTextArea() {
		if (DirectionsTextArea == null) {
			DirectionsTextArea = new JTextArea();
			DirectionsTextArea.setText("Inkerator is a testing application for " +
									   "invisible ink\nprinters. It utilizes " +
									   "the InvisibleInkFactory library\nto " +
									   "generate the images and allows a user" +
									   " to save\nthem to file.");
			DirectionsTextArea.setEditable(false);
		}
		return DirectionsTextArea;
	}

	/**
	 * This method initializes FontColorRange	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getFontColorRange() {
		if (FontColorRange == null) {
			FontColorRange = new JTextField();
			FontColorRange.setText("0.0,1.0");
		}
		return FontColorRange;
	}

	/**
	 * This method initializes BGColorRange	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getBGColorRange() {
		if (BGColorRange == null) {
			BGColorRange = new JTextField();
			BGColorRange.setText("0.0,1.0");
		}
		return BGColorRange;
	}

	/**
	 * This method initializes MaskColorRange	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getMaskColorRange() {
		if (MaskColorRange == null) {
			MaskColorRange = new JTextField();
			MaskColorRange.setText("0.0,1.0");
		}
		return MaskColorRange;
	}

	/**
	 * This method initializes ImgHeight	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getImgHeight() {
		if (ImgHeight == null) {
			ImgHeight = new JTextField();
			ImgHeight.setText("0");
		}
		return ImgHeight;
	}

	/**
	 * This method initializes ImgWidth	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getImgWidth() {
		if (ImgWidth == null) {
			ImgWidth = new JTextField();
			ImgWidth.setText("0");
		}
		return ImgWidth;
	}

	/**
	 * This method initializes Zoom	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getZoom() {
		if (Zoom == null) {
			Zoom = new JTextField();
			Zoom.setText("1");
		}
		return Zoom;
	}

	/**
	 * This method initializes ColorOrdering	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getColorOrdering() {
		if (ColorOrdering == null) {
			ColorOrdering = new JTextField();
			ColorOrdering.setText("CMYK");
		}
		return ColorOrdering;
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
