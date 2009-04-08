/*
 * @(#)FindFile.java.java
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
package org.scantegrity.lib;

import java.io.File;
import java.util.ArrayList;
import java.util.ListIterator;

import org.scantegrity.lib.constants.FindFileConst;

/**
 * @author John Conway
 *
 * Using regex, will find a file on any OS
 */
public class FindFile implements Runnable
{
	//String variables
	private String c_filename;
	private String c_osname;
	
	//threads running array
	private ArrayList<Thread> c_threadList;
	
	//directory stack
	private ArrayList<File> c_dirToSearch;
	
	//reference to parent
	private FindFile c_parent; 
	
	//Configuration File
	private File c_fileFound;
	
	//flag for config found
	private boolean c_isConfigFound = false;

	/**
	 * Searches for a file based on a regular expression for that file
	 * 
	 * @param p_regex : regular expression for the filename
	 */
	public FindFile()
	{
		c_filename = null;
		c_dirToSearch = new ArrayList<File>(); 
		c_parent = null; 
		c_fileFound = null;
		
		determineOS(); 
		
		getPathsToSearch(); 
	}
	
	/**
	 * This constructor takes an exact file to search for 
	 * @param p_filename : the filename to search for
	 */
	public FindFile(String p_filename)
	{
		c_filename = p_filename; 
		c_dirToSearch = new ArrayList<File>();
		c_parent = null;
		c_fileFound = null;
		
		determineOS(); 
		
		getPathsToSearch(); 
	}
	
	/**
	 * Takes a path to search down from 
	 * 
	 * @param p_dirToSearch
	 */
	public FindFile(FindFile p_parent)
	{
		c_filename = p_parent.getFilename();
		c_dirToSearch = new ArrayList<File>();
		c_dirToSearch.add(p_parent.getDirToSearch());
		c_parent = p_parent;
		c_fileFound = null;
	}
	
	public File find()
	{
		return find(c_filename);
	}
	
	/*
	 * finds the file
	 */
	public File find(String p_filename)
	{
		c_filename = p_filename;
		
		c_threadList = new ArrayList<Thread>();
		
		/*
		 * Following code is for thread driven search
		 *
		//starts a thread for each directory in the paths array
		for(int i = 0; i < c_dirToSearch.size(); i++)
		{
			Thread l_thread = new Thread(new FindFile(this));
			l_thread.start(); 
			
			c_threadList.add(l_thread);
		}
		
		while(true)
		{
			if(c_isConfigFound)
			{
				for(ListIterator<Thread> l_it = c_threadList.listIterator(); l_it.hasNext(); )
				{
					l_it.next().interrupt(); 
					l_it.remove();
				}
				
				synchronized (c_fileFound)
				{
					return c_fileFound; 
				}
			}
		}
		*/
		while(!c_isConfigFound && c_dirToSearch.size() != 0)
			searchNextDir(); 
		
		if(c_isConfigFound)
			return c_fileFound;
		else
			return null;
			
		
	}
	
	public synchronized void setFileFound(File p_fileFound)
	{
		c_fileFound = p_fileFound;
		c_isConfigFound = true;
	}
	
	public synchronized String getFilename()
	{
		return c_filename; 
	}
	
	public synchronized File getDirToSearch()
	{
		if(c_dirToSearch.size() != 0)
			return c_dirToSearch.remove(0);
		
		return null;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run()
	{
		while(c_dirToSearch.size() != 0 && !c_isConfigFound)
		{
			searchNextDir(); 
			
			if(c_dirToSearch.size() == 0)
			{
				File l_dir = c_parent.getDirToSearch();
				
				if(l_dir == null)
					return;
				else
					c_dirToSearch.add(l_dir);
			}
		}
	}
	
	private void determineOS()
	{
		c_osname = System.getProperty("os.name");
	}
	
	private void getPathsToSearch()
	{	
		String[] l_paths = null; 
		
		if(c_osname.equals(FindFileConst.LINUX_NAME))
		{
			l_paths = FindFileConst.LINUX_MOUNTS;
		}
		else if(c_osname.equals(FindFileConst.MAC_OSX_NAME))
		{
			l_paths = FindFileConst.MAC_MOUNTS;
		}
		
		for(int i = 0; i < l_paths.length; i++)
		{
			c_dirToSearch.add(new File(l_paths[i]));
		}
	}
	
	private void searchNextDir()
	{
		File l_file = c_dirToSearch.remove(0);
		
		//get the listing of directories and files in current dir
		String[] l_currDirList = l_file.list(); 
		
		//TODO: make this an exception
		if(l_currDirList == null)
			return;
		
		for(int i = 0; i < l_currDirList.length; i++)
		{
			File l_tempFile = new File(l_file.getPath() + System.getProperty("file.separator") + l_currDirList[i]);
			
			if(l_tempFile.isFile())
			{	
				if(l_tempFile.getName().equals(c_filename))
				{
					setFileFound(l_tempFile);
				
					return;
				}
			}
			else if(l_tempFile.isDirectory())
			{
				//push the directory on the directories to search stack 
				c_dirToSearch.add(0, l_tempFile);
			}	
		}
	}
}
