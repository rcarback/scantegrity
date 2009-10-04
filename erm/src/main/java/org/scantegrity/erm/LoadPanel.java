package org.scantegrity.erm;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Vector;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.TitledBorder;

import org.apache.commons.io.FileUtils;
import org.scantegrity.common.FindFile;
import org.scantegrity.common.RandomBallotStore;

public class LoadPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private WriteInResolver c_resolver = null;
	private JScrollPane jScrollPane = null;
	private JPanel buttonPanel = null;
	private JList jList = null;
	private JButton scanButton = null;
	private JButton unloadButton = null;
	private JLabel headerLabel = null;
	private DefaultListModel listModel = null;
	private String c_path = null;
	private Frame c_parentFrame = null;
	/**
	 * This is the default constructor
	 */
	public LoadPanel(WriteInResolver p_resolver, String p_destFolder) {
		super();
		c_resolver = p_resolver;
		c_path = p_destFolder;
		initialize();
		c_parentFrame = (Frame)getParent();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		headerLabel = new JLabel();
		headerLabel.setText("Please insert SD cards from each machine, one at a time, and click the 'scan' button.");
		this.setSize(600, 400);
		this.setLayout(new BorderLayout());
		this.add(getJScrollPane(), BorderLayout.WEST);
		this.add(getButtonPanel(), BorderLayout.EAST);
		this.add(headerLabel, BorderLayout.NORTH);
	}

	/**
	 * This method initializes jScrollPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getJScrollPane() {
		if (jScrollPane == null) {
			jScrollPane = new JScrollPane();
			jScrollPane.setPreferredSize(new Dimension(200,400));
			jScrollPane.setViewportView(getJList());
			jScrollPane.setBorder(BorderFactory.createTitledBorder(null, "Loaded Ballots (Scanner ID)", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD, 12), new Color(51, 51, 51)));
		}
		return jScrollPane;
	}

	/**
	 * This method initializes buttonPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getButtonPanel() {
		if (buttonPanel == null) {
			buttonPanel = new JPanel();
			buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
			
			buttonPanel.add(getScanButton(), null);
			buttonPanel.add(getUnloadButton(), null);
		}
		return buttonPanel;
	}

	/**
	 * This method initializes jList	
	 * 	
	 * @return javax.swing.JList	
	 */
	private JList getJList() {
		if (jList == null) {
			jList = new JList();
			jList.setFont(new Font("Helvetica", Font.PLAIN, 20));
			listModel = new DefaultListModel();
			jList.setModel(listModel);
		}
		return jList;
	}

	/**
	 * This method initializes scanButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getScanButton() {
		if (scanButton == null) {
			scanButton = new JButton();
			scanButton.setText("Scan for ballot files");
			scanButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					FindFile l_finder = new FindFile(Pattern.compile(".*\\.sbr"));
					l_finder.c_recurseDepth = 2;
					Vector<File> l_files = l_finder.findMultiple();
					Vector<RandomBallotStore> l_stores = new Vector<RandomBallotStore>();
					for( File l_file : l_files )
					{
						try {
							RandomBallotStore l_newStore = new RandomBallotStore(0,0,0, l_file.getPath(), null, null);
							l_newStore.open();
							l_stores.add(l_newStore);
						} catch (NoSuchAlgorithmException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (IOException e1) {
							JOptionPane.showMessageDialog(getParent(), "Error opening ballot store");
						}
					}
					BallotStoreDialog l_confirmDialog = new BallotStoreDialog(c_parentFrame, l_stores);
					l_confirmDialog.setVisible(true);
					for( RandomBallotStore l_store : l_confirmDialog.c_stores )
					{
						try {
							if( listModel.contains(l_store.getScannerId()))
							{
								int l_resp = JOptionPane.showConfirmDialog(getParent(), "A ballot store from scanner " + l_store.getScannerId() + " has already been loaded.  Are you sure you would like to add this one?");
								if( l_resp != JOptionPane.YES_OPTION )
									continue;
								
							}
							int x = 0;
							String l_newFile = c_path + File.separator + "Ballots-" + l_store.getScannerId() + ".sbr";
							while( new File(l_newFile).exists() )
							{
								l_newFile = c_path + File.separator + "Ballots-" + l_store.getScannerId() + "-" + x + ".sbr";
								x++;
							}
							c_resolver.LoadBallots(l_store);
							String l_curFile = l_store.getLocation();
							l_store.close();
							FileUtils.copyFile(new File(l_curFile), new File(l_newFile));
							listModel.addElement(l_store.getScannerId());
							
						} catch (IOException e1) {
							JOptionPane.showMessageDialog(getParent(), "Error copying ballot file to destination");
						}
					}
				}
			});
			
		}
		return scanButton;
	}

	/**
	 * This method initializes unloadButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getUnloadButton() {
		if (unloadButton == null) {
			unloadButton = new JButton();
			unloadButton.setText("Unload ballot files");
		}
		return unloadButton;
	}

}
