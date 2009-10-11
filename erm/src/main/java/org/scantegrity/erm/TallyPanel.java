package org.scantegrity.erm;

import java.awt.GridBagLayout;

import javax.swing.JPanel;
import javax.swing.JButton;
import java.awt.GridBagConstraints;

public class TallyPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	WriteInResolver c_resolver = null;
	String c_path = null;
	private JButton jButton = null;

	/**
	 * This is the default constructor
	 */
	public TallyPanel(WriteInResolver p_resolver, String p_path) {
		super();
		c_path = p_path;
		c_resolver = p_resolver;
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		this.setSize(300, 200);
		this.setLayout(new GridBagLayout());
		this.add(getJButton(), gridBagConstraints);
	}

	/**
	 * This method initializes jButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButton() {
		if (jButton == null) {
			jButton = new JButton();
			jButton.setText("Tally!");
			jButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					c_resolver.Tally(c_path);
					c_resolver.WriteResults(c_path);
					c_resolver.WriteResolutionPdf(c_path);
				}
			});
		}
		return jButton;
	}

}
