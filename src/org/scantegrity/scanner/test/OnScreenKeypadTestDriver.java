/**
 * Scantegrity Keypad Test Driver
 */
package org.scantegrity.scanner.test;

import javax.swing.SwingUtilities;

import org.scantegrity.scanner.gui.OnScreenKeypad;

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
		OnScreenKeypad pad = new OnScreenKeypad(null, "Test", 480, 480);
		String result = pad.getBuffer();
		
		//pad.execute();
		/*
		try {
			//result = pad.get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}*/

		System.out.println("Got result: " + result) ;

		// Second test case, secure input/view
		OnScreenKeypad pad2 = new OnScreenKeypad(null, "Test", 480, 480, '*');
		result = pad2.getBuffer();
		//pad2.execute();
		/*
		try {
			//result = pad2.get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}*/

		System.out.println("Got result: " + result) ;
	}
}