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

package org.scantegrity.engine.ioExample;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Vector;

public class MeetingTwoIn {

	//write meeting two in
	public static void write(double percentCheck,int noBallots,String outFile) throws Exception {
		OutputStream fos = new BufferedOutputStream(new FileOutputStream(outFile));
fos.write("<xml>\n".getBytes());
fos.write("\t<challenges>\n".getBytes());
fos.write("\t\t<print>\n".getBytes());
		int[] a=random(percentCheck,noBallots);
		for (int i=0;i<a.length;i++) {
fos.write(("\t\t\t<row id=\""+a[i]+"\"/>\n").getBytes());				
		}
fos.write("\t\t</print>\n".getBytes());
fos.write("\t</challenges>\n".getBytes());
fos.write("</xml>\n".getBytes());			
fos.close();
	}
	
	public static int[] random(double percentCheck, int noBallots) {
		Vector<Integer> b = new Vector<Integer>(noBallots);
		for (int i=0;i<noBallots;i++)
			b.add(i);
		Collections.shuffle(b);
		
		int[] ret = new int[(int)(percentCheck*noBallots)];
		for (int i=0;i<ret.length;i++)
			ret[i] = b.get(i);
		
		return ret;
	}	
}
