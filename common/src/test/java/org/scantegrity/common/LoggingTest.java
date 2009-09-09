package org.scantegrity.common;

import java.util.logging.Level;
import java.util.logging.LogRecord;

import org.junit.Test;

public class LoggingTest {

	/**
	 * @param args
	 */
	@Test
	public void testLogging()
	{
		Logging l_log = new Logging("TestLog", Level.ALL);
		l_log.log(Level.CONFIG, "Testing the config level for the logger.");
		l_log.log(Level.ALL, "Test message for the logger.");
	}
}
