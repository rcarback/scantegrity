package scantegrity.erm;

import java.awt.GridBagLayout;

import javax.swing.DefaultListModel;
import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.BorderLayout;
import javax.swing.JList;
import javax.swing.JScrollPane;

public class WriteInPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private JList jList = null;
	private DefaultListModel listModel = null;
	private JScrollPane jScrollPane = null;

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
		this.setSize(538, 336);
		this.setLayout(new BorderLayout());
		this.add(getJScrollPane(), BorderLayout.WEST);
	}

	/**
	 * This method initializes jList	
	 * 	
	 * @return javax.swing.JList	
	 */
	private JList getJList() {
		if (jList == null) {
			jList = new JList();
			listModel = new DefaultListModel();
			listModel.addElement("Test");
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
			jScrollPane.setViewportView(getJList());
		}
		return jScrollPane;
	}

}  //  @jve:decl-index=0:visual-constraint="10,10"
