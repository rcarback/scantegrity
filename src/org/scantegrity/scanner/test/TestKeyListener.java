package org.scantegrity.scanner.test;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import org.scantegrity.scanner.gui.PollingPlaceGUI;

public class TestKeyListener implements KeyListener
{
	private PollingPlaceGUI c_parent;
	public TestKeyListener(PollingPlaceGUI p_parent)
	{
		c_parent = p_parent;
	}

	public void keyPressed(KeyEvent p_arg0)
	{
		if(p_arg0.getKeyCode() == KeyEvent.VK_F4)
		{
			c_parent.setToScanningBallot();
		}
		else if(p_arg0.getKeyCode() == KeyEvent.VK_F5)
		{
			c_parent.displayScanResults();
		}
	}

	public void keyReleased(KeyEvent p_arg0)
	{
		if(p_arg0.getKeyCode() == KeyEvent.VK_F4)
		{
			c_parent.setToScanningBallot();
		}
		else if(p_arg0.getKeyCode() == KeyEvent.VK_F5)
		{
			c_parent.displayScanResults();
		}
	}

	public void keyTyped(KeyEvent p_arg0)
	{
		if(p_arg0.getKeyCode() == KeyEvent.VK_F4)
		{
			c_parent.setToScanningBallot();
		}
		else if(p_arg0.getKeyCode() == KeyEvent.VK_F5)
		{
			c_parent.displayScanResults();
		}
	}

}
