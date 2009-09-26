package org.scantegrity.erm;

import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.beans.XMLDecoder;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.scantegrity.common.*;
import org.scantegrity.scanner.*;

public class ERM extends JFrame {

	private static final long serialVersionUID = 1L;
	private JTabbedPane jTabbedPane = null;
	private WriteInPanel writeInPanel = null;
	private File c_scannerConfigFile = null;
	private ScannerConfig c_config = null;
	/**
	 * This method initializes jTabbedPane	
	 * 	
	 * @return javax.swing.JTabbedPane	
	 */
	private JTabbedPane getJTabbedPane() {
		if (jTabbedPane == null) {
			jTabbedPane = new JTabbedPane();
			jTabbedPane.addTab("Write-In", null, getWriteInPanel(), null);
		}
		return jTabbedPane;
	}

	/**
	 * This method initializes writeInPanel	
	 * 	
	 * @return scantegrity.erm.WriteInPanel	
	 */
	private WriteInPanel getWriteInPanel() {
		if (writeInPanel == null) {
			writeInPanel = new WriteInPanel(c_config);
		}
		return writeInPanel;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				ERM thisClass = new ERM();
				thisClass.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				thisClass.setVisible(true);
			}
		});
	}

	/**
	 * This is the default constructor
	 */
	public ERM() {
		super();
		FindFile l_finder = new FindFile(ScannerConstants.DEFAULT_CONFIG_NAME);
		l_finder.c_recurseDepth = 1;
		c_scannerConfigFile = l_finder.find();
		if( c_scannerConfigFile == null )
		{
			JOptionPane.showMessageDialog(getParent(), "Could not locate configuration file, please provide a path to " + ScannerConstants.DEFAULT_CONFIG_NAME);
			JFileChooser l_fileChooser = new JFileChooser();
			FileNameExtensionFilter l_filter = new FileNameExtensionFilter("Xml Configuration File", "xml");
			l_fileChooser.setFileFilter(l_filter);
			int l_ret = JFileChooser.CANCEL_OPTION;
			while( l_ret != JFileChooser.APPROVE_OPTION )
			{
				l_ret = l_fileChooser.showOpenDialog(getParent());
			}
			c_scannerConfigFile = l_fileChooser.getSelectedFile();
		}
		
		try
		{
			XMLDecoder l_dec = new XMLDecoder(new BufferedInputStream(new FileInputStream(c_scannerConfigFile)));
			c_config = (ScannerConfig)l_dec.readObject();
			l_dec.close();	
		}
		catch(FileNotFoundException e_fnf)
		{
			JOptionPane.showMessageDialog(getParent(), "Error loading scanner config file!");
			System.exit(-1);
		}
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(800, 600);
		this.setContentPane(getJTabbedPane());
		this.setTitle("JFrame");
	}

}
