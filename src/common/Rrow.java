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

package software.common;

import java.io.Serializable;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.Attributes;
/**
 * A row in the R table of the punchboard
 * @author stefan
 *
 */
public class Rrow implements Serializable {
	
	private static final long serialVersionUID = -7226106359429728051L;
	
	int id=-1;
	byte[] vote=null;

	public static final String idAttr="id";
	public static final String voteAttr="r";
	/**
	 * empty constructor
	 */
	public Rrow() {
		
	}
	/** 
	 * @param row a DOM node contaning a R row 
	 * @throws Exception
	 */	
	public Rrow(Node row) throws Exception {
		setUp(row);
	}
	/** 
	 * @param attrs a SAX set of attributes contaning a R row
	 * @throws Exception
	 */	
	public Rrow(Attributes attrs) throws Exception {
		setUp(attrs);
	}
	
	protected void setUp(Node row) throws Exception {
		NamedNodeMap attrs = row.getAttributes();
		Node attr=attrs.getNamedItem(idAttr);
		id=Integer.parseInt(attr.getNodeValue());

		attr=attrs.getNamedItem(voteAttr);
		if (attr!=null)
			vote=Util.parse(attr.getNodeValue());
	}
	
	protected void setUp(Attributes attrs) throws Exception {
		for (int i = 0; i < attrs.getLength(); i++) {
            String aName = attrs.getLocalName(i); // Attr name 
            if ("".equals(aName)) aName = attrs.getQName(i);
			if (aName.equals(idAttr))
				id = Integer.parseInt(attrs.getValue(i));
			else
			if (aName.equals(voteAttr))
				vote=Util.parse(attrs.getValue(i));
		}
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public byte[] getVote() {
		return vote;
	}

	public void setVote(byte[] vote) {
		this.vote = vote;
	}
	
	/**
	 * creates an xml representation of the R row.
	 */	
	public String toString() {
		StringBuffer ret=new StringBuffer("");
		ret.append("\t\t\t\t<row id=\""+getId()+"\"");
		if (vote!=null) {
			ret.append(" "+Rrow.voteAttr+"=\""+Util.toString(vote)+"\"");
		}
		ret.append("/>\n");
		return ret.toString();
	}	
}
