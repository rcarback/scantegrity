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
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.TreeMap;

import javax.xml.parsers.SAXParser;

import org.scantegrity.common.Drow;

public class MeetingFourIn {

	public static void write(String m3out,String m4in) throws Exception {
		org.scantegrity.common.MeetingReaderSax handler = new org.scantegrity.common.MeetingReaderSax();
        try {
            SAXParser saxParser = org.scantegrity.common.Util.newSAXParser();
            saxParser.parse( new File(m3out), handler);
        } catch (Throwable t) {
            t.printStackTrace();
        }
        
        while (!handler.isDoneParsing()) {
        	Thread.sleep(100);
        }
        
        TreeMap<Byte, TreeMap<Byte, TreeMap<Integer, Drow>>> drows = handler.getDrows();
		
		Drow.ChosenSide side=Drow.ChosenSide.NONE;
		

		
OutputStream fos = new BufferedOutputStream(new FileOutputStream(m4in));
fos.write("<xml>\n".getBytes());
fos.write("\t<database>\n".getBytes());		
		
		for (byte p:drows.keySet()) {
			TreeMap<Byte, TreeMap<Integer, Drow>> partition=drows.get(p);
fos.write(("\t\t<partition id=\""+p+"\">\n").getBytes());
fos.write("\t\t\t<decrypt>\n".getBytes());
			for (byte i:partition.keySet()) {
				TreeMap<Integer, Drow> instance=partition.get(i);
fos.write(("\t\t\t\t<instance id=\""+i+"\">\n").getBytes());
				for (int did:instance.keySet()) {
					if (Math.random()<0.5)
						side=Drow.ChosenSide.LEFT;
					else
						side=Drow.ChosenSide.RIGHT;
fos.write(("\t\t\t\t\t<row id=\""+did+"\" side=\""+side+"\"/>\n").getBytes());
				}
				fos.write("\t\t\t\t</instance>\n".getBytes());							
			}
fos.write("\t\t\t</decrypt>\n".getBytes());
fos.write(("\t\t</partition>\n").getBytes());			
		}
fos.write("\t</database>\n".getBytes());
fos.write("</xml>".getBytes());
fos.close();
	}
}
