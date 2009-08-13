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

package org.scantegrity.authoring;

import java.awt.image.BufferedImage;
import java.util.Vector;

import org.scantegrity.common.Cluster;

public interface BmpTogeometryInterface {

	/** 
	 * @param bi - the image to be scanned
	 * @param dpi - the dpi of the image (it cannot be infered from the image since
	 * some images do not have a dpi)
	 * @param noCols - the number of columns the contests are arranged in
	 * @param geometryFile - where to write the geometry file
	 * @param electionSpecFile - where to write the default Election Specification
	 * @throws Exception - an exception is thrown if the number of bullets is different
	 *  from the number of dunots for one contest (or for the serial number)  
	 */
	public void createGeometry(BufferedImage bi, float dpi, int noCols,String geometryFile,String electionSpecFile) throws Exception;
	
	/**
	 * @return - all the Colors that the image is scanned for
	 */
	public Vector<Cluster> getAllColors();
}
