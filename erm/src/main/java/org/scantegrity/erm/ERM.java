package org.scantegrity.erm;

import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import org.scantegrity.common.*;

public class ERM extends JFrame {

	private static final long serialVersionUID = 1L;
	private JTabbedPane jTabbedPane = null;
	private WriteInPanel writeInPanel = null;
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
			writeInPanel = new WriteInPanel();
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
