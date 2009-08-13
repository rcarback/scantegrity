/*
 * @(#)Dialogs.java.java
 *  
 * Copyright (C) 2008-2009 Scantegrity Project
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
package org.scantegrity.common.gui;

import javax.swing.JOptionPane;


/**
 * @author John Conway
 *
 */
public class Dialogs
{
	public static void displayDialogBox(String message)
	{
		ScantegrityJFrame dialog = new ScantegrityJFrame(); 
		JOptionPane.showMessageDialog(dialog, message, "Message", JOptionPane.PLAIN_MESSAGE);
	}
	
	public static void displayErrorDialog(String message)
	{
		ScantegrityJFrame dialog = new ScantegrityJFrame(); 
		JOptionPane.showMessageDialog(dialog, message, "Error!", JOptionPane.ERROR_MESSAGE);
	}
	
	public static void displayInfoDialog(String message, String title)
	{
		ScantegrityJFrame dialog = new ScantegrityJFrame(); 
		JOptionPane.showMessageDialog(dialog, message, title, JOptionPane.INFORMATION_MESSAGE);
	}
	
	public static void displayWarningDialog(String message)
	{
		ScantegrityJFrame dialog = new ScantegrityJFrame(); 
		JOptionPane.showMessageDialog(dialog, message, "Warning", JOptionPane.WARNING_MESSAGE);
	}
	
	public static void displayMessageDialog(String message)
	{
		ScantegrityJFrame dialog = new ScantegrityJFrame(); 
		JOptionPane.showMessageDialog(dialog, message);
	}
	
	//Dialogs with JFrame as parameter
	
	public static void displayDialogBox(String message, ScantegrityJFrame p_frame)
	{ 
		JOptionPane.showMessageDialog(p_frame, message, "Message", JOptionPane.PLAIN_MESSAGE);
	}
	
	public static void displayErrorDialog(String message, ScantegrityJFrame p_frame)
	{ 
		JOptionPane.showMessageDialog(p_frame, message, "Error!", JOptionPane.ERROR_MESSAGE);
	}
	
	public static void displayInfoDialog(String message, String title, ScantegrityJFrame p_frame)
	{ 
		JOptionPane.showMessageDialog(p_frame, message, title, JOptionPane.INFORMATION_MESSAGE);
	}
	
	public static void displayWarningDialog(String message, ScantegrityJFrame p_frame)
	{ 
		JOptionPane.showMessageDialog(p_frame, message, "Warning", JOptionPane.WARNING_MESSAGE);
	}
	
	public static void displayMessageDialog(String message, ScantegrityJFrame p_frame)
	{
		JOptionPane.showMessageDialog(p_frame, message);
	}

}
