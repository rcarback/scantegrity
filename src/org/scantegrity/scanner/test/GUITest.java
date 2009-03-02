package org.scantegrity.scanner.test;

import java.awt.HeadlessException;

import org.scantegrity.scanner.gui.PollingPlaceGUI;

public class GUITest
{

	/**
	 * @param args
	 */
	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		Runnable l_runnable = null;
		
		try
		{
			l_runnable = new PollingPlaceGUI();
		}
		catch(HeadlessException headEx)
		{
			//eventually send this to dialogs?
			System.err.println("Headless Exception in GUITest when creating PollingPlaceGUI.");
			headEx.printStackTrace(); //goes to System.err
		}

		Thread l_gui = new Thread(l_runnable); 
		l_gui.start();
	}
}
