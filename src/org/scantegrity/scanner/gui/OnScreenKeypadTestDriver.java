/**
 * Scantegrity Keypad Test Driver
 */
package org.scantegrity.scanner.gui;

import javax.swing.SwingUtilities;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.ExecutionException;

/**
 * @author pdswenso
 *
 */
@SuppressWarnings("unused")
public class OnScreenKeypadTestDriver {
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// First test case, standard input/view
		OnScreenKeypad pad = new OnScreenKeypad(480, 480);
		String result = "";
		
		pad.execute();
		
		try {
			result = pad.get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}

		System.out.println("Got result: " + result) ;

		// Second test case, secure input/view
		OnScreenKeypad pad2 = new OnScreenKeypad(480, 480, '*');
		
		pad2.execute();
		
		try {
			result = pad2.get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}

		System.out.println("Got result: " + result) ;
	}
}