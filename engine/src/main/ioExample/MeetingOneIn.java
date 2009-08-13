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
import java.io.IOException;
import java.io.OutputStream;

import org.bouncycastle.util.encoders.Base64;

public class MeetingOneIn {

	public static void write(String es,int noB,int noD,byte[] c,String outFile) throws IOException {
				
		//File f=new File(outFile);
		//if (!f.exists())
			//f.createNewFile();
			
		//write meeting one in
		OutputStream fos = new BufferedOutputStream(new FileOutputStream(outFile));
		fos.write("<xml>\n".getBytes());
			fos.write("\t<electionSpec>".getBytes());
			fos.write(new File(es).getAbsolutePath().getBytes());
			fos.write("</electionSpec>\n".getBytes());
		
			fos.write(("\t<noBallots>"+noB+"</noBallots>\n").getBytes());
			
			fos.write(("\t<noDs>"+noD+"</noDs>\n").getBytes());
		
			fos.write("\t<constant>".getBytes());
			fos.write(Base64.encode(c));
			fos.write("</constant>\n".getBytes());
			
		fos.write("</xml>\n".getBytes());		
		fos.close();
	}	
}
