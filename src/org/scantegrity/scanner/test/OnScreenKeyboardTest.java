/**
 * Scantegrity Keypad Test Driver
 */
package org.scantegrity.scanner.test;

import org.scantegrity.scanner.gui.OnScreenKeyboard;

/**
 * @author pdswenso
 *
 */
public class OnScreenKeyboardTest {
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// First test case, standard input/view
		OnScreenKeyboard pad = new OnScreenKeyboard(null, "Enter Judge Password:", true);
		System.out.println(pad.getBuffer());
		
	}
}