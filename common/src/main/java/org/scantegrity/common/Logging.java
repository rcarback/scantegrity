package org.scantegrity.common.Logging; 

import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.XMLFormatter;

public class Logging
{
	private Logger c_log; 
	private FileHandler c_fileHandler; 
	private XMLFormatter c_formatter; 
	private static String c_tmpFileName = "/Users/jay12701/Desktop/logFile.txt";
	
	public Logging(String p_logName, String p_logFileName, Level p_level)
	{
		c_log = new Logger(p_logName); 
		c_fileHandler = new FileHandler(p_logFileName);
		c_log.addHandler(c_fileHandler);
		c_log.setLevel(p_level);
		
		//formatter? 
		//the formatter is probably where we get rid of the log time.
		c_formatter = new XMLFormatter(); 
		c_fileHandler.setFormatter(c_formatter);
	}
	
	public Logging(String p_logName, Level p_level)
	{
		Logging(p_logName, c_tmpFileName, p_level);
	}
	
	/**
	 * Writes the log to the logger
	 * @param p_level
	 * @param p_message
	 */
	public void writeEntry(Level p_level, String p_message)
	{
		c_log.log(p_level, p_message);
	} 
	
	public static void main(String args[])
	{
		Logging l_log = new Logging("TestLog", Level.ALL);
		l_log.writeEntry(Level.ALL, "Test message for the logger.");
	}
}