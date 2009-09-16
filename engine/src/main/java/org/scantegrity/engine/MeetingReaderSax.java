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

package org.scantegrity.engine;

import java.util.TreeMap;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Reads an xml file representing a meeting and puts everything in memory
 * @author stefan
 *
 */
public class MeetingReaderSax extends DefaultHandler {
	TreeMap<Integer,Prow> prows=new TreeMap<Integer, Prow>();
	TreeMap<Byte,TreeMap<Byte,TreeMap<Integer,Drow>>> drows=new TreeMap<Byte, TreeMap<Byte,TreeMap<Integer,Drow>>>();;
	TreeMap<Byte,TreeMap<Integer,Rrow>> rrows=new TreeMap<Byte, TreeMap<Integer,Rrow>>();;
	
	TreeMap<Byte,TreeMap<Integer,Drow>> dPartition=null;
	TreeMap<Integer,Drow> dTable=null;
	
	TreeMap<Integer,Rrow> rTable=null;
	
	private enum Table {print,decrypt,results};
	Table whichTable;
	private byte partitionId;
	private byte dNo;

	boolean doneParsing=false;
	
	public MeetingReaderSax() {

	}
	
	public void startElement(String namespaceURI,String lName,String qName,Attributes attrs) {
        String eName = lName; // element name
        if ("".equals(eName)) eName = qName; // namespaceAware = false
    	if (eName.compareTo("print")==0) {
			whichTable=Table.print;
			return;
		}
    	if (eName.compareTo("decrypt")==0) {
			whichTable=Table.decrypt;			
			return;
		}
    	if (eName.compareTo("results")==0) {
			whichTable=Table.results;
			rTable= new TreeMap<Integer, Rrow>();
			return;
		}
    	
		if (eName.compareTo("partition")==0) {
			for (int i = 0; i < attrs.getLength(); i++) {
                String aName = attrs.getLocalName(i); // Attr name 
                if ("".equals(aName)) aName = attrs.getQName(i);				
				if (aName.equals("id")) {
					partitionId = Byte.parseByte(attrs.getValue(i));
				}
			}
			dPartition=new TreeMap<Byte, TreeMap<Integer,Drow>>();			
		}
		if (eName.compareTo("instance")==0) {
			for (int i = 0; i < attrs.getLength(); i++) {
                String aName = attrs.getLocalName(i); // Attr name 
                if ("".equals(aName)) aName = attrs.getQName(i);				
				if (aName.equals("id")) {
					dNo = Byte.parseByte(attrs.getValue(i));
				}
			}
			dTable = new TreeMap<Integer, Drow>();
		}

		if (eName.compareTo("row")==0) {
			if (whichTable==Table.print) {
				try {
					Prow prow=new Prow(attrs);
					prows.put(prow.getId(),prow);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else
			if (whichTable==Table.decrypt) {
				try {
					Drow drow = new Drow(attrs);
					dTable.put(drow.getId(), drow);
				} catch (Exception e) {
					e.printStackTrace();
				}				
			} else
			if (whichTable==Table.results) {
				try {
					Rrow rrow=new Rrow(attrs);
					rTable.put(rrow.getId(),rrow);
				} catch (Exception e) {
					e.printStackTrace();
				}				
			}
		}		
    }
	
	public void endElement(String namespaceURI,	String sName, String qName) {
    	if (qName.equals("instance"))
    		dPartition.put(dNo, dTable);
    	else
    	if (qName.equals("results"))
    		rrows.put(partitionId, rTable);
    	else
    	if (qName.equals("partition"))
    		drows.put(partitionId, dPartition);
    	if (qName.equals("xml"))
    		doneParsing=true;    	
    }    

	public TreeMap<Byte, TreeMap<Byte, TreeMap<Integer, Drow>>> getDrows() {
		return drows;
	}

	public TreeMap<Integer, Prow> getProws() {
		return prows;
	}

	public TreeMap<Byte,TreeMap<Integer, Rrow>> getRrows() {
		return rrows;
	}

	public boolean isDoneParsing() {
		return doneParsing;
	}
	
	
}