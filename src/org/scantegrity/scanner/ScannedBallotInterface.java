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

package org.scantegrity.scanner;

import java.awt.image.BufferedImage;
import java.util.TreeMap;
import java.util.Vector;

import org.scantegrity.common.BallotGeometryMap;
import org.scantegrity.common.Cluster;
import org.scantegrity.common.Prow;
import org.scantegrity.scanner.ScannedBallot.TypeOfVotes;

public interface ScannedBallotInterface {

	public String getSerial();
	/**
	 * Given a scanned ballot, detects the votes and the serial number
	 * @param img
	 * @throws Exception - if the serial number cannot be properly read
	 */
	public void detect(BufferedImage img) throws Exception;
	/** 
	 * @return the correct marks (only) for the contest that are fully votes
	 */
	public Vector<Cluster> getFullVotes();
	/** 
	 * @return the overvotes that appear on the ballot
	 */	
	public Vector<Cluster> getOverVotes();
	/** 
	 * @return the correct marks (only) for the contest that are undervoted
	 */	
	public Vector<Cluster> getUnderVotes();
	public TreeMap<Integer, TreeMap<Integer, TreeMap<Integer, TypeOfVotes>>> getAllContests();
	/**
	 * @return the scanned ballot in xml format, accepted by the web server for uploading
	 */
	public byte[] toXMLString();
	/**
	 * @return - the streighen image (perfectly aligned)
	 */
	public BufferedImage getPerfectImage();
	/** 
	 * @return the dpi of the scanned image
	 */
	public double getDpi();
	/** 
	 * @return the scanned page
	 */
	public Prow.ChosenPage getSelectedPage();
	public BallotGeometryMap getBallotGeometryMap();
	/**
	 * [T|B]serial space separated marked possitions. If a possition is not marked, -1 is used
	 * @return a one line representation of the scanned ballot
	 */
	public String getCompactRepresentation();
	/** 
	 * @param mailIn if a ballot is mailed in, the other page is marked as scanned
	 * (i.e. the the top page is scanned, bottom is recorded).
	 * This can be done because only the possitions are retained
	 */
	public void setMailIn(boolean mailIn);
	/** 
	 * @return if the ballot commes from a mailed in ballot
	 */
	public boolean isMailIn();
	/** 
	 * @return a scanned ballot in Prow representation
	 */
	public Prow toProw();
}
