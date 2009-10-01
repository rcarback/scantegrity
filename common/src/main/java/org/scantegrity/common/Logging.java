package org.scantegrity.common; 

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.XMLFormatter;

/**
 * This class is an extension of java.util.logging.Logger that
 * is customized for Scantegrity. The log methods have been overridden 
 * to provide for time and sequence information to always 
 * be set to 0 when the log is written to in order to preserve
 * anonymity. When using this class, only use the log methods 
 * that take LogRecord, or level, msg. The 
 * logp methods and the rest of the log methods have not been overridden.
 * 
 * @author jconway
 *
 */
public class Logging extends Logger
{
	private FileHandler c_fileHandler; 
	private XMLFormatter c_formatter; 
	private static String c_tmpFileName = "log1.txt";
	
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
	
	public void log(LogRecord p_record)
	{
		p_record.setMillis(0);
		p_record.setSequenceNumber(0);
		super.log(p_record);
	}

	/**
	 * Writes the log to the logger
	 * @param p_level
	 * @param p_message
	 */
	public void log(Level p_level, String p_message)
	{
		LogRecord l_record = new LogRecord(p_level, p_message);
		this.log(l_record);
	} 
}