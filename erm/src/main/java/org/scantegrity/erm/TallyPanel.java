package org.scantegrity.erm;

import java.awt.GridBagLayout;

import javax.swing.JPanel;

public class TallyPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	WriteInResolver c_resolver = null;
	String c_path = null;

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
		this.setSize(300, 200);
		this.setLayout(new GridBagLayout());
	}

}
