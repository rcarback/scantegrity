package org.scantegrity.erm;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.util.Vector;


import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

public class WriteInPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private JList jList = null;
	private DefaultListModel listModel = null;
	private JScrollPane jScrollPane = null;
	private JPanel textPanel = null;
	private JTextField textCandidate = null;
	private JPanel imagePanel = null;
	private JLabel imageLabel = null;
	private Vector<String> c_candidateList = null;
	private WriteInResolver c_resolver = null;
	private BufferedImage c_writeInImage = null;
	private WriteInLoaderThread c_loader = null;

	/**
	 * This is the default constructor
	 */
	public WriteInPanel() {
		super();
		initialize();
		
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		listModel = new DefaultListModel();
		listModel.addElement("Test");
		
		c_candidateList = new Vector<String>();
		c_resolver = new WriteInResolver();
		
		this.setSize(538, 336);
		this.setLayout(new BorderLayout());
		this.add(getJScrollPane(), BorderLayout.WEST);
		this.add(getTextPanel(), BorderLayout.SOUTH);
		this.add(getImagePanel(), BorderLayout.CENTER);
		c_loader = new WriteInLoaderThread();
		c_loader.start();
	}

	/**
	 * This method initializes jList	
	 * 	
	 * @return javax.swing.JList	
	 */
	private JList getJList() {
		if (jList == null) {
			jList = new JList();
			jList.setModel(listModel);
		}
		return jList;
	}

	/**
	 * This method initializes jScrollPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getJScrollPane() {
		if (jScrollPane == null) {
			jScrollPane = new JScrollPane();
			jScrollPane.setBorder(BorderFactory.createTitledBorder(null, "Candidate List", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD, 12), new Color(51, 51, 51)));
			jScrollPane.setViewportView(getJList());
		}
		return jScrollPane;
	}

	/**
	 * This method initializes textPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getTextPanel() {
		if (textPanel == null) {
			textPanel = new JPanel();
			textPanel.setLayout(new BoxLayout(getTextPanel(), BoxLayout.X_AXIS));
			textPanel.setBorder(BorderFactory.createTitledBorder(null, "Resolve Candidate", TitledBorder.LEFT, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD, 12), new Color(51, 51, 51)));
			textPanel.add(getTextCandidate(), null);
		}
		return textPanel;
	}

	/**
	 * This method initializes textCandidate	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getTextCandidate() {
		if (textCandidate == null) {
			textCandidate = new JTextField();
			textCandidate.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					AddVote(textCandidate.getText());
				}
			});
		}
		return textCandidate;
	}
	
	public void AddVote(String p_name)
	{
		if( !c_candidateList.contains(p_name) )
		{
			int l_res = JOptionPane.showConfirmDialog(getParent(), "Candidate is not in list, would you like to add?", "Confirm Add", JOptionPane.YES_NO_OPTION);
			if( l_res != JOptionPane.YES_OPTION )
				return;
			else
			{
				c_resolver.AddCandidate(p_name);
			}
		}
		//Add vote for candidate
		c_loader.notify();
	}

	/**
	 * This method initializes imagePanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getImagePanel() {
		if (imagePanel == null) {
			imageLabel = new JLabel();
			imageLabel.setText("JLabel");
			imagePanel = new JPanel();
			imagePanel.setLayout(new BoxLayout(getImagePanel(), BoxLayout.X_AXIS));
			imagePanel.setBorder(BorderFactory.createTitledBorder(null, "Image", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD, 12), new Color(51, 51, 51)));
			imagePanel.add(imageLabel, null);
		}
		return imagePanel;
	}
	
	private void UpdateState()
	{
		DefaultListModel l_model = new DefaultListModel();
		for(String l_candidate : c_candidateList)
		{
			l_model.addElement(l_candidate);
		}
		jList.setModel(l_model);
		
		imageLabel.setIcon(new ImageIcon(c_writeInImage));
	}
	
	//Inner class to load write-ins for resolution
	private class WriteInLoaderThread extends Thread
	{
		public void run()
		{
			while(c_resolver.next())
			{
				c_candidateList = c_resolver.getCandidates();
				c_writeInImage = c_resolver.getImage();
				UpdateState();
				//Wait for user to resolve write-in
				try {
					this.wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			JOptionPane.showMessageDialog(getParent(), "Write-in resolution complete");
		}
	}

}  //  @jve:decl-index=0:visual-constraint="10,10"
