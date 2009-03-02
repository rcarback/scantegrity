/*
 * Scantegrity - Successor to Punchscan, a High Integrity Voting System
 * Copyright (C) 2006  Richard Carback, David Chaum, Jeremy Clark, 
 * Aleks Essex, Stefan Popoveniuc, and Jeremy Robin
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
package org.scantegrity.scanner.gui;

import org.scantegrity.common.Constants;

/**
 * @author John Conway
 *
 */
public final class ScannerUIConstants extends Constants
{
	/* ****************************
	 * Card Layout Constants
	 *****************************/
	public static final String LOGIN_CARD = "Chief Judge Login";
	public static final String START_ELECTION_CARD = "Start Election";
	public static final String SCANNING_BALLOT_CARD = "Scanning Ballot";
	public static final String WAITING_FOR_BALLOT_CARD = "Waiting for Ballots";
	public static final String BALLOT_INFO_CARD = "Ballot Information";
	public static final String BALLOT_INFO_WITH_REJECTED_CARD = "Ballot Info w/ Rejected";
	public static final String EXTENDED_ELECTION_INFO_CARD = "Extended Elec Info";
	public static final String COMPACT_ELECTION_INFO_CARD = "Compact Elec. Info";
	
	/* ****************************
	 * Field Constants
	 *****************************/
	public static final int NUM_PASSWORD_COLUMNS = 20; 
	
	/* ****************************
	 * Size Constants
	 *****************************/
	public static final int MEDIUM_BUTTON_WIDTH = 200; 
	public static final int MEDIUM_BUTTON_HEIGHT = 75;
}
