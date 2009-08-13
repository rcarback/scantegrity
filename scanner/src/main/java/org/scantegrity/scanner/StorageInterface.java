/*
 * @(#)StorageInterface.java
 *  
 * Copyright (C) 2008 Scantegrity Project
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

package org.scantegrity.scanner;

/**
 * StorageInterface.java - Provides an interface for classes that will provide
 * ballot storage to implement.  Every class that implements this interface
 * will represent a different storage medium or method.  The straight forward
 * implementation would be to store to the file system, but the interface would
 * work with database storage or network storage.
 * 
 * @author Travis Mayberry
 * @date 28/02/09
 */

/**
 * TODO
 *
 * May work better as an abstract class because all implementations will likely have to 
 * serialize and encrypt based on configuration information.  The difference will be mostly in 
 * where the bytes are going to after decryption.
 */

import java.util.logging.Logger;

public interface StorageInterface {
	
	/**
	 * setLogger - Sets the logging object being used.
	 * 
	 * @param p_logger the configured logger to use.
	 */
	void setLogger(Logger p_logger);
	
	/**
	 * storeBallot - Encrypts and stores a single ballot.  The ballot object 
	 * must implement Serializable so it can be written to as an object.
	 * The encryption key will be read from the configuration objection.
	 * 
	 * @param p_config - The configuration object to be used
	 */
	//void storeBallot(ScannerConfiguration p_config, ScanContest p_ballot);
	
	/**
	 * retrieveBallot - Retrieves and decrypts a single ballot.  Throws BallotNotFoundException
	 * if no ballot with the given serial number can be found.
	 * 
	 * @param p_config - The configuration object to be used
	 * @param p_serial - The serial number of the ballot being retrieved
	 * @return - The ballot with the provided serial number
	 */
	//ScanContest retrieveBallot(ScannerConfiguration p_config, String p_serial) throws BallotNotFoundException;
	
	/**
	 * retrieveBallots - Retrieves and decrypts all ballots. 
	 * 
	 * @param p_config - The configuration object to be used
	 * @return - All ballots stored in this storage object
	 */
	//ScanContest[] retrieveBallots(ScannerConfiguration p_config);

}