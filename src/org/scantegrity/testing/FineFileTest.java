/*
 * @(#)FineFileTest.java.java
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
package org.scantegrity.testing;

import java.awt.HeadlessException;
import java.io.File;

import org.scantegrity.common.gui.Dialogs;
import org.scantegrity.lib.FindFile;

/**
 * @author John Conway
 *
 */
public class FineFileTest
{

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{	
		FindFile l_ff = new FindFile();
		File l_file = l_ff.find("ScannerConfig.xml");
		
		if(l_file != null)
			System.out.println(l_file.getPath());
		else
			System.out.println("File Not Found");
	}

}
