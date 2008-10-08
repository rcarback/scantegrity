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

package software.auditor.invisibleink;

import java.util.TreeMap;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import software.common.SecurityUtil;
import software.common.SymbolRow;

public class ParseM2CodesCheckCommitments extends DefaultHandler {

	int printedSerial=-1;
	byte questionId=-1;
	TreeMap<Integer,TreeMap<Byte,TreeMap<Byte,SymbolRow>>> rows=null;	
	byte[] c=null;
	
	public ParseM2CodesCheckCommitments(TreeMap<Integer,TreeMap<Byte,TreeMap<Byte,SymbolRow>>> rows, byte[] c) {
		this.rows=rows;
		this.c=c;
	}
	
	public void startElement(String namespaceURI,String lName,String qName,Attributes attrs) throws SAXException {
        String eName = lName; // element name
        if ("".equals(eName)) eName = qName; // namespaceAware = false
    	if (eName.compareTo("ballot")==0) {
			for (int i = 0; i < attrs.getLength(); i++) {
                String aName = attrs.getLocalName(i); // Attr name 
                if ("".equals(aName)) aName = attrs.getQName(i);				
				if (aName.equals("printedSerial")) {
					printedSerial = Integer.parseInt(attrs.getValue(i));;		
				}
			}
			return;
		}
    	if (eName.compareTo("question")==0) {
			for (int i = 0; i < attrs.getLength(); i++) {
                String aName = attrs.getLocalName(i); // Attr name 
                if ("".equals(aName)) aName = attrs.getQName(i);				
				if (aName.equals("id")) {
					questionId = Byte.parseByte(attrs.getValue(i));;		
				}
			}
			return;
		}
    	if (eName.compareTo("symbol")==0) {
				SymbolRow symbolCommitment=null;
				try {
					symbolCommitment = new SymbolRow(attrs);
				} catch (Exception e) {
					e.printStackTrace();
				}
				TreeMap<Byte,TreeMap<Byte,SymbolRow>> ballot=rows.get(printedSerial);
				if (ballot!=null) {
					TreeMap<Byte,SymbolRow> question=ballot.get(questionId);
					if (question!=null) {						
						SymbolRow symbolSalt=question.get(symbolCommitment.getId()); 
						if (symbolSalt!=null) {							
							SecurityUtil.checkCodeCommitment(printedSerial, questionId, symbolCommitment.getId(), symbolSalt.getSalt(), symbolSalt.getCode(), symbolCommitment.getCommitment(), c);
						}
					}
				}								
				return;
    	}
	}
}
