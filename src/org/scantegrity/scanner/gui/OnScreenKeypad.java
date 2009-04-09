/*
 * This class implements an on-screen numeric keypad using Swing/AWT.
 */

package org.scantegrity.scanner.gui;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.awt.event.KeyEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.Event;
import java.awt.BorderLayout;
import java.awt.Font;

import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.KeyStroke;
import java.awt.Point;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JMenuItem;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JFrame;
import javax.swing.JDialog;
import javax.swing.JTextField;

import java.awt.Dimension;
import javax.swing.JButton;
import java.awt.Rectangle;
import java.awt.Choice;

/**
 * @author Paul Swenson (ps1@umbc.edu)
 *
 */
@SuppressWarnings("unused")
public class OnScreenKeypad extends SwingWorker<String, Object> {
	private JFrame jFrame = null;  //  @jve:decl-index=0:visual-constraint="3,-14"
	private JPanel jContentPane = null;
	private JButton Button7 = null;
	private JButton Button8 = null;
	private JButton Button9 = null;
	private JButton Button4 = null;
	private JButton Button5 = null;
	private JButton Button6 = null;
	private JButton Button1 = null;
	private JButton Button2 = null;
	private JButton Button3 = null;
	private JButton Button0 = null;
	private JButton ButtonDel = null;
	private JButton ButtonEnter = null;
	private JTextField jTextField = null;
	
	private Font defaultFont = null ;
	
	private int height = 0 ;
	private int width = 0 ;
	private int numRows = 0 ;
	private int numCols = 0 ;
	private int buttonHeight = 0 ;
	private int buttonWidth = 0 ;
	
	private Object sync = null ;
	
	@Override
	protected String doInBackground() throws Exception {
		getJFrame().setVisible(true);
		
		synchronized(sync) {
			sync.wait() ;
		}

		return getJTextField().getText() ;
	}
	
	public OnScreenKeypad(int height, int width) {
		this.height = height + 36 ; // 36 accounts for widget
		this.width = width ;
		
		defaultFont = new Font("Dialog", 1, 32) ;
		
		numRows = 6 ;
		numCols = 3 ;
		buttonHeight = height / numRows ;
		buttonWidth = width / numCols ;
		
		sync = new Object() ;
	}
	
	/**
	 * This method initializes Button7	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getButton7() {
		if (Button7 == null) {
			Button7 = new JButton();
			Button7.setActionCommand("Button7");
			Button7.setHorizontalAlignment(SwingConstants.CENTER);
			Button7.setText("7");
			Button7.setLocation(new Point(0*buttonWidth, 1*buttonHeight));
			Button7.setSize(new Dimension(buttonWidth, buttonHeight));
			Button7.setEnabled(true);
			Button7.setFont(defaultFont) ;
			Button7.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					jTextAppend("7") ;
				}
			});
		}
		return Button7;
	}
	
	/**
	 * This method initializes Button8	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getButton8() {
		if (Button8 == null) {
			Button8 = new JButton();
			Button8.setActionCommand("Button8");
			Button8.setHorizontalAlignment(SwingConstants.CENTER);
			Button8.setText("8");
			Button8.setLocation(new Point(1*buttonWidth, 1*buttonHeight));
			Button8.setSize(new Dimension(buttonWidth, buttonHeight));
			Button8.setEnabled(true);
			Button8.setFont(defaultFont) ;
			Button8.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					jTextAppend("8") ;
				}
			});
		}
		return Button8;
	}
	
	/**
	 * This method initializes Button9
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getButton9() {
		if (Button9 == null) {
			Button9 = new JButton();
			Button9.setActionCommand("Button9");
			Button9.setHorizontalAlignment(SwingConstants.CENTER);
			Button9.setText("9");
			Button9.setLocation(new Point(2*buttonWidth, 1*buttonHeight));
			Button9.setSize(new Dimension(buttonWidth, buttonHeight));
			Button9.setEnabled(true);
			Button9.setFont(defaultFont) ;
			Button9.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					jTextAppend("9") ;
				}
			});
		}
		return Button9;
	}

	/**
	 * This method initializes Button4
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getButton4() {
		if (Button4 == null) {
			Button4 = new JButton();
			Button4.setActionCommand("Button4");
			Button4.setHorizontalAlignment(SwingConstants.CENTER);
			Button4.setText("4");
			Button4.setLocation(new Point(0*buttonWidth, 2*buttonHeight));
			Button4.setSize(new Dimension(buttonWidth, buttonHeight));
			Button4.setEnabled(true);
			Button4.setFont(defaultFont) ;
			Button4.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					jTextAppend("4") ;
				}
			});
		}
		return Button4;
	}
	
	/**
	 * This method initializes Button5
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getButton5() {
		if (Button5 == null) {
			Button5 = new JButton();
			Button5.setActionCommand("Button5");
			Button5.setHorizontalAlignment(SwingConstants.CENTER);
			Button5.setText("5");
			Button5.setLocation(new Point(1*buttonWidth, 2*buttonHeight));
			Button5.setSize(new Dimension(buttonWidth, buttonHeight));
			Button5.setEnabled(true);
			Button5.setFont(defaultFont) ;
			Button5.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					jTextAppend("5") ;
				}
			});
		}
		return Button5;
	}
	
	/**
	 * This method initializes Button6
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getButton6() {
		if (Button6 == null) {
			Button6 = new JButton();
			Button6.setActionCommand("Button6");
			Button6.setHorizontalAlignment(SwingConstants.CENTER);
			Button6.setText("6");
			Button6.setLocation(new Point(2*buttonWidth, 2*buttonHeight));
			Button6.setSize(new Dimension(buttonWidth, buttonHeight));
			Button6.setEnabled(true);
			Button6.setFont(defaultFont) ;
			Button6.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					jTextAppend("6") ;
				}
			});
		}
		return Button6;
	}
	
	/**
	 * This method initializes Button1
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getButton1() {
		if (Button1 == null) {
			Button1 = new JButton();
			Button1.setActionCommand("Button1");
			Button1.setHorizontalAlignment(SwingConstants.CENTER);
			Button1.setText("1");
			Button1.setLocation(new Point(0*buttonWidth, 3*buttonHeight));
			Button1.setSize(new Dimension(buttonWidth, buttonHeight));
			Button1.setEnabled(true);
			Button1.setFont(defaultFont) ;
			Button1.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					jTextAppend("1") ;
				}
			});
		}
		return Button1;
	}
	
	/**
	 * This method initializes Button2
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getButton2() {
		if (Button2 == null) {
			Button2 = new JButton();
			Button2.setActionCommand("Button2");
			Button2.setHorizontalAlignment(SwingConstants.CENTER);
			Button2.setText("2");
			Button2.setLocation(new Point(1*buttonWidth, 3*buttonHeight));
			Button2.setSize(new Dimension(buttonWidth, buttonHeight));
			Button2.setEnabled(true);
			Button2.setFont(defaultFont) ;
			Button2.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					jTextAppend("2") ;
				}
			});
		}
		return Button2;
	}

	/**
	 * This method initializes Button3
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getButton3() {
		if (Button3 == null) {
			Button3 = new JButton();
			Button3.setActionCommand("Button3");
			Button3.setHorizontalAlignment(SwingConstants.CENTER);
			Button3.setText("3");
			Button3.setLocation(new Point(2*buttonWidth, 3*buttonHeight));
			Button3.setSize(new Dimension(buttonWidth, buttonHeight));
			Button3.setEnabled(true);
			Button3.setFont(defaultFont) ;
			Button3.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					jTextAppend("3") ;
				}
			});
		}
		return Button3;
	}
	
	/**
	 * This method initializes Button0
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getButton0() {
		if (Button0 == null) {
			Button0 = new JButton();
			Button0.setActionCommand("Button0");
			Button0.setHorizontalAlignment(SwingConstants.CENTER);
			Button0.setText("0");
			Button0.setLocation(new Point(0*buttonWidth, 4*buttonHeight));
			Button0.setSize(new Dimension(3*buttonWidth, buttonHeight));
			Button0.setEnabled(true);
			Button0.setFont(defaultFont) ;
			Button0.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					jTextAppend("0") ;
				}
			});
		}
		return Button0;
	}
	
	/**
	 * This method initializes ButtonDel
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getButtonDel() {
		if (ButtonDel == null) {
			ButtonDel = new JButton();
			ButtonDel.setActionCommand("ButtonDel");
			ButtonDel.setHorizontalAlignment(SwingConstants.CENTER);
			ButtonDel.setText("Del");
			ButtonDel.setLocation(new Point((int)(0*buttonWidth), 5*buttonHeight));
			ButtonDel.setSize(new Dimension((int)(1.5*buttonWidth), buttonHeight));
			ButtonDel.setEnabled(true);
			ButtonDel.setFont(defaultFont) ;
			ButtonDel.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					jTextDelete() ;
				}
			});
		}
		return ButtonDel;
	}
	
	/**
	 * This method initializes ButtonEnter
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getButtonEnter() {
		if (ButtonEnter == null) {
			ButtonEnter= new JButton();
			ButtonEnter.setActionCommand("ButtonEnter");
			ButtonEnter.setHorizontalAlignment(SwingConstants.CENTER);
			ButtonEnter.setText("Enter");
			ButtonEnter.setLocation(new Point((int)(1.5*buttonWidth), 5*buttonHeight));
			ButtonEnter.setSize(new Dimension((int)(1.5*buttonWidth), buttonHeight));
			ButtonEnter.setEnabled(true);
			ButtonEnter.setFont(defaultFont) ;
			ButtonEnter.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					synchronized(sync)
					{
						sync.notifyAll() ;
					}
					getJFrame().dispose() ;
				}
			});
		}
		return ButtonEnter;
	}
	
	/**
	 * This method initializes jTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextField() {
		if (jTextField == null) {
			jTextField = new JTextField();
			jTextField.setBounds(new Rectangle(0, 0, 3*buttonWidth, buttonHeight));
			jTextField.setHorizontalAlignment(JTextField.CENTER) ;
			jTextField.setFocusable(false) ;
			jTextField.setEnabled(true) ;
			jTextField.setFont(defaultFont) ;
		}
		return jTextField;
	}
	
	/**
	 * This method initializes jFrame
	 * 
	 * @return javax.swing.JFrame
	 */
	public JFrame getJFrame() {
		if (jFrame == null) {
			jFrame = new JFrame();
			jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			jFrame.setSize(width, height);
			jFrame.setContentPane(getJContentPane());
			jFrame.setTitle("Scantegrity On-Screen Keypad");
			jFrame.addWindowListener(new WindowAdapter()
			{
				public void windowClosing(WindowEvent e)
				{
					synchronized(sync)
					{
						sync.notifyAll() ;
					}
				}
			});
		}
		return jFrame;
	}

	/**
	 * This method appends text to the JTextField
	 */
	public void jTextAppend(String str) {
		getJTextField().setText(getJTextField().getText()+str) ;
	}
	
	/**
	 * This method deletes text from the JTextField
	 */
	public void jTextDelete() {
		String tmp = getJTextField().getText() ;
		if ( tmp.length() > 0 )
		{
			tmp = tmp.substring(0, tmp.length()-1) ;
		}
		getJTextField().setText(tmp) ;
	}
	
	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.setLayout(null);
			jContentPane.add(getButton7(), null);
			jContentPane.add(getButton8(), null);
			jContentPane.add(getButton9(), null);
			jContentPane.add(getButton4(), null);
			jContentPane.add(getButton5(), null);
			jContentPane.add(getButton6(), null);
			jContentPane.add(getButton1(), null);
			jContentPane.add(getButton2(), null);
			jContentPane.add(getButton3(), null);
			jContentPane.add(getButton0(), null);
			jContentPane.add(getButtonDel(), null);
			jContentPane.add(getButtonEnter(), null);
			jContentPane.add(getJTextField(), null);

		}
		return jContentPane;
	}
}
