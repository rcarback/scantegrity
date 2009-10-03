package org.scantegrity.erm;

import java.awt.GridBagLayout;

import javax.swing.JPanel;

public class SpoiledPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	WriteInResolver c_resolver = null;

	/**
	 * This is the default constructor
	 */
	public SpoiledPanel(WriteInResolver p_resolver) {
		super();
		c_resolver = p_resolver;
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(300, 200);
		this.setLayout(new GridBagLayout());
	}

}
