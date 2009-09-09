package org.scantegrity.common; 

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.XMLFormatter;

public class Logging extends Logger
{
	private FileHandler c_fileHandler; 
	private XMLFormatter c_formatter; 
	private static String c_tmpFileName = "/home/jconway/Desktop/log1.txt";
	
	public Logging(String p_logName, String p_logFileName, Level p_level)
	{
		super(p_logName, null); 
		
		try {
			c_fileHandler = new FileHandler(p_logFileName);
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		this.addHandler(c_fileHandler);
		this.setLevel(p_level);
		
		//formatter? 
		//the formatter is probably where we get rid of the log time.
		c_formatter = new XMLFormatter(); 
		c_fileHandler.setFormatter(c_formatter);
	}
	
	public Logging(String p_logName, Level p_level)
	{
		super(p_logName, null); 
		
		try {
			c_fileHandler = new FileHandler(c_tmpFileName);
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		this.addHandler(c_fileHandler);
		this.setLevel(p_level);
		
		//formatter? 
		//the formatter is probably where we get rid of the log time.
		c_formatter = new XMLFormatter(); 
		c_fileHandler.setFormatter(c_formatter);
	}

	/**
	 * Writes the log to the logger
	 * @param p_level
	 * @param p_message
	 */
	public void log(Level p_level, String p_message)
	{
		LogRecord l_rec = new LogRecord(p_level, p_message);
		l_rec.setMillis(0);
		l_rec.setSequenceNumber(0);
		this.log(l_rec);
	} 
}