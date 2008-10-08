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

package software.auditor;

import java.util.TreeMap;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import software.common.Drow;
import software.common.Prow;
import software.common.SecurityUtil;

/**
 * Sax parser for meeting one. It reads in one element at a time and 
 * if it has to check it, it does so. Does not read m1out in memory.
 * 
 * @author stefan
 *
 */
public class ParseMeetingOneOutCTC extends DefaultHandler {

	TreeMap<Integer,Prow> prows;
	TreeMap<Byte,TreeMap<Byte,TreeMap<Integer,Drow>>> drows;
	private boolean print;
	private byte partitionId;
	private byte dNo;

	private String pid;	
	private String c1;
	private String c2;
	
	private String did;
	private String cl;
	private String cr;	
	//the election constant
	private byte[] c=null;

	/**
	 * Creates a copy of the pointer to the 3 paratemers
	 * 
	 * @param prows
	 * @param drows
	 * @param c
	 */
	public ParseMeetingOneOutCTC(TreeMap<Integer,Prow> prows, TreeMap<Byte,TreeMap<Byte,TreeMap<Integer,Drow>>> drows, byte[] c) {
		this.prows=prows;
		this.drows=drows;
		this.c=c;
	}
	
	/**
	 * Reads one xml element at a time from m1out and if it can check a commitment
	 * it does so. If the commitment does not check it throws an Exception. 
	 */	
    public void startElement(String namespaceURI,String lName,String qName,Attributes attrs) throws SAXException {
        String eName = lName; // element name
        if ("".equals(eName)) eName = qName; // namespaceAware = false
    	if (eName.compareTo("print")==0) {
			print=true;
			return;
		}
		if (eName.compareTo("partition")==0) {
	        print=false;
			for (int i = 0; i < attrs.getLength(); i++) {
                String aName = attrs.getLocalName(i); // Attr name 
                if ("".equals(aName)) aName = attrs.getQName(i);				
				if (aName.equals("id")) {
					partitionId = Byte.parseByte(attrs.getValue(i));
					return;
				}
			}
		}
		if (eName.compareTo("instance")==0) {
			for (int i = 0; i < attrs.getLength(); i++) {
                String aName = attrs.getLocalName(i); // Attr name 
                if ("".equals(aName)) aName = attrs.getQName(i);				
				if (aName.equals("id")) {
					dNo = Byte.parseByte(attrs.getValue(i));
					return;
				}
			}
		}
		if (eName.compareTo("row")==0) {
			if (print) {
				/**
				 *  - if a row in P is found, check if the ballot was requested for audit
				 *  	- check the commitments for p1 and p2
				 *  	- check that p1+p2=d2+d4 for all partitions, all instances
				*/
				Prow prow=null;
				for (int i = 0; i < attrs.getLength(); i++) {
	                String aName = attrs.getLocalName(i); // Attr name 
	                if ("".equals(aName)) aName = attrs.getQName(i);
					if (aName.equals("id")) {
						pid = attrs.getValue(i);
						//check if the ballot was requested for audit
						prow=prows.get(Integer.parseInt(pid));
						if (prow==null)
							return;
					} else
					if (aName.equals("c1"))
						c1=attrs.getValue(i);
					else
					if (aName.equals("c2"))
						c2=attrs.getValue(i);
				}
				//check the commitments for p1 and p2
				SecurityUtil.checkProwCommitment(prow.getS1(),prow.getPage1(),c1,pid,c);
				SecurityUtil.checkProwCommitment(prow.getS2(),prow.getPage2(),c2,pid,c);								
				return;
			}
			Drow drow=null;
			for (int i = 0; i < attrs.getLength(); i++) {
                String aName = attrs.getLocalName(i); // Attr name 
                if ("".equals(aName)) aName = attrs.getQName(i);				
				if (aName.equals("id")) {
					did = attrs.getValue(i);
					drow=drows.get(partitionId).get(dNo).get(Integer.parseInt(did));
					if (drow==null)
						return;
				} else
				if (aName.equals("cl"))
					cl=attrs.getValue(i);
				else
				if (aName.equals("cr"))
					cr=attrs.getValue(i);
			}
			SecurityUtil.checkDrowCommitment(drow.getS1(),drow.getPid(),drow.getPage1(),cl,partitionId,dNo,did,c);
			SecurityUtil.checkDrowCommitment(drow.getS2(),drow.getRid(),drow.getPage2(),cr,partitionId,dNo,did,c);
		}		
		
	}
}
