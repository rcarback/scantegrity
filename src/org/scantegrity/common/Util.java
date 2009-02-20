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

package org.scantegrity.common;

import java.awt.Color;
import java.awt.Component;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.gwu.voting.standardFormat.basic.Answer;
import org.gwu.voting.standardFormat.basic.Question;
import org.gwu.voting.standardFormat.basic.Section;
import org.gwu.voting.standardFormat.electionSpecification.ElectionSpecification;
import org.gwu.voting.standardFormat.filledInBallot.FilledInBallot;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.lowagie.text.Rectangle;


/**
 * Helper class
 * @author stefan
 *
 */
public class Util {
	
	static DocumentBuilder db = null;
	public static String fileSeparator=System.getProperty("file.separator");
	public static enum BallotType {PUNCHSCAN,SCANTEGRITY,NONE};
	public static SAXParserFactory saxFactory = null;	
	
	static {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		try {
			db = factory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		saxFactory=SAXParserFactory.newInstance();

	}
		
	public static byte[] makePMessage(byte[] serial,byte[] p) {
		byte[] m=new byte[serial.length+p.length];
		int pos=0;
		System.arraycopy(serial, 0, m, pos, serial.length);
		pos=+serial.length;
		System.arraycopy(p, 0, m, pos, p.length);
		return m;
	}
	
	public static byte[] makeDMessage(byte partitionId, byte instanceId, byte[] rowId,int d1,byte[] d2) {
		byte[] d1ByteArray = Integer.toString(d1).getBytes();
		byte[] m=new byte[1+1+rowId.length+d1ByteArray.length+d2.length];
		m[0]=partitionId;
		m[1]=instanceId;
		int pos = 2;
		System.arraycopy(rowId, 0, m, pos, rowId.length);
		pos+=rowId.length;
		System.arraycopy(d1ByteArray,0,m,pos,d1ByteArray.length);
		pos+=d1ByteArray.length;
		System.arraycopy(d2,0,m,pos,d2.length);
		return m;
	}
	
	/*
	//10percent improvment if using hashmac instead of our scheme
	public static byte[] getCommitment(SecretKeySpec skm, byte[] c, byte[] m) throws Exception {
		Mac mac = Mac.getInstance("HMACSHA256","BC");
		mac.init(skm);
		return mac.doFinal(m);
	}
	*/
		
	
	public static int compare(byte[] a, byte[] b) {
		if (a==null && b==null)
			return 0;
		if (a==null)
			return 1;
		if (b==null)
			return -1;
		if (a.length > b.length)
			return 1;
		if (a.length < b.length)
			return -1;
		for (int i=0;i<a.length;i++) {
			if (a[i]>b[i])
				return 1;
			else
				if (a[i]<b[i])
				return -1;
		}
		return 0;
	}
	
	public static byte[] xor(byte[] a, byte[] b) throws Exception {
		byte[] retVal = null;
		if (a.length != b.length) {
			throw new Exception("byte arrays have to have the same size.a["+a.length+"]"+" b["+b.length+"]");
		}
		retVal = new byte[a.length];
		for (int i=0;i<a.length;i++) {
			retVal[i] = (byte)(a[i] ^ b[i]);
		}
		return retVal;	
	}

	public static void print(byte[][] a) {
		for (int i=0;i<a.length;i++) {
			for (int j=0;j<a[0].length;j++) {
				System.out.print(a[i][j]);
			}
			System.out.println();
		}
		System.out.println();
	}
	
	
	public static void print(byte[] a) {
		if (a==null) {
			System.out.print("null\n");
			return;
		}
		for (int i=0;i<a.length;i++) {
			System.out.print(" "+ByteToHex(a[i]));
		}
	}
	
	public static void println(byte[] a) {
		if (a==null) {
			System.out.print("null\n");
			return;
		}
		for (int i=0;i<a.length;i++) {
			System.out.print(" "+ByteToHex(a[i]));
		}
		System.out.println();
	}
	
	public static void print(int[] a) {
		for (int i=0;i<a.length;i++) {
			System.out.print(" "+a[i]);
		}
		System.out.println();
	}

	public static void print(String[] a) {
		for (int i=0;i<a.length;i++) {
			System.out.print("("+i+","+a[i]+") ");
		}
		System.out.println();
	}	
	
	public static void writeComma(String as, OutputStream out) throws IOException {
		for (int i=0;i<as.length();i++) {
			out.write((as.charAt(i)+",").getBytes());
		}
	}
	public static void writeComma(byte[] a,OutputStream out) throws IOException {
		int i=0;
		byte comma = ',';
		for (;i<a.length;i++) {
			out.write(a[i]);
			out.write(comma);
		}		
	}
	
	public static void write(byte[] a,OutputStream out) throws IOException {
		int i=0;
		byte space = ' ';
		for (;i<a.length-1;i++) {
			out.write(Byte.toString(a[i]).getBytes());
			out.write(space);
		}
		out.write(Byte.toString(a[i]).getBytes());		
	}

	public static String toString(byte[] a){
		StringBuffer ret=new StringBuffer("");
		int i=0;
		for (;i<a.length-1;i++) {
			ret.append(Byte.toString(a[i])+" ");
		}
		ret.append(a[i]);
		return ret.toString();
	}
	
	
	public static byte[] intArrayToByteArray(int m[]) {
	    byte[] vect=new byte[4*m.length];
	    int ii=0;
	    for(int i=0; i<m.length; i++) {
	    	ii=i<<2;
	    	vect[ii+0]=(byte)((m[i]>>24)&0xFF);
	    	vect[ii+1]=(byte)((m[i]>>16)&0xFF);
	    	vect[ii+2]=(byte)((m[i]>> 8)&0xFF);
	    	vect[ii+3]=(byte)((m[i]    )&0xFF);
	    }
	    return vect;
	}
	
	public static int[] getInverse(int[] perm) {
		int[] inv = new int[perm.length];
		for (int i=0;i<inv.length;i++)
			inv[perm[i]]=i;
		return inv;
	}

	public static byte[] getInverse(byte[] perm) {
		byte[] inv = new byte[perm.length];
		for (byte i=0;i<inv.length;i++)
			inv[perm[i]]=i;
		return inv;
	}
	
	public static byte[] getInverse(byte[] perm,int start,int length) {
		byte[] inv = new byte[length];
		System.arraycopy(perm,start,inv,0,length);
		return Util.getInverse(inv);
	}

	
	public static String ByteToHex(byte data) {
		StringBuffer buf = new StringBuffer();
		buf.append(toHexChar((data>>>4)&0x0F));
		buf.append(toHexChar(data&0x0F));
		return buf.toString();
	}

	
	public static char toHexChar(int i) {
		if (0<=i && i<=9)
			return (char)('0'+i);
		return (char)('a'+(i-10)); 
	}
	
	public static int getNumberOfWords(String s) {
		int ret = 0;
		StringTokenizer st = new StringTokenizer(s);
		for(;st.hasMoreTokens();ret++) {
			st.nextToken();
		}
		return ret;
	}
	
	public static byte[] parse(String ballot) {
		String[] retString=ballot.split(" ");
		byte[] ret=new byte[retString.length];
		for(int i=0;i<retString.length;i++) {
			ret[i] = Byte.parseByte(retString[i]);
		}
		return ret;
	}
	
	
	public static void makeFib(byte[] psAnswers,FilledInBallot fib) {
		int psAnswersPos = 0;

		//create a new fib
		Section[] orderedSections = fib.getOrderedSections();
		for (int spos = 0;spos < orderedSections.length;spos++) {
			Section s = orderedSections[spos];
			Question[] orderedQuestions = s.getOrderedQuestions();
			for (int qpos =0;qpos<orderedQuestions.length;qpos++) {
				Question q = orderedQuestions[qpos];
				for (int noA = 0;noA<q.getMax();noA++) {
					if (psAnswers[psAnswersPos] == -1) {
						//this was an abstain vote
					} else {
						q.getOrderedAnswers()[psAnswers[psAnswersPos]].setPoints(1);
					}
					psAnswersPos++;
				}
				//eliminate the answers that have ZERO points
				Hashtable answers = q.getAnswers();
				for (Enumeration ea=answers.keys();ea.hasMoreElements();) {
					Answer a = (Answer)answers.get(ea.nextElement());
					if (a.getPoints()>0) {
						a.setPoints(0);
					} else {
						//a.setPoints(-1);
						answers.remove(a.getId());
					}
				}
			}
		}
	}
	
	public static void delete(String path) {
		File f = new File(path);
		if (f.isFile()) {
			f.delete();
			return;
		}
		String[] files = f.list();
		if (files != null) {
			for (int i=0;i<files.length;i++) {
				delete(path+"/"+files[i]);
			}
		}
		f.delete();
	}
/*	
	public static void permString(byte[] perm, String[] src,int offset,String[] dest) {
		for (int i=0;i<perm.length;i++)
			dest[offset+i] = src[offset+perm[i]];
	}
*/
	
	public static void permString(byte[] perm, String[] src,int offset,String[] dest) {
		for (int i=0;i<perm.length;i++)
			dest[offset+i] = src[offset+perm[i]];
	}

	
	public static Color[] permColor(byte[] perm, Color[] orig,int start,int length) {
		Color[] ret = new Color[length];
		for (int i=0;i<length;i++)
			ret[i] = orig[start+perm[i]];
		return ret;
	}	
	
	/**
	 * @deprecated - it should be faster use ot a byte array output stream
	 * @param o
	 * @return
	 * @throws Exception
	 */
	public static Object cloneObjectXXYY(Object o) throws Exception {
		ByteArrayOutputStream baos=new ByteArrayOutputStream();
		//FileOutputStream fos = new FileOutputStream("t.tmp");
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(o);
        oos.close();
		
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()));//new FileInputStream("t.tmp"));
        baos.close();
        return ois.readObject();        
	}
	
	public static void eliminateFakeChilds(Node node) {
		NodeList childNodes = node.getChildNodes();
		if (childNodes == null)
			return;
		int length = childNodes.getLength();
		int currentNodeIndex = 0;
		for (int i=0;i<length;i++) {
			Node childOneNode = childNodes.item(currentNodeIndex);
			if (!isTrueNode(childOneNode)) {
				node.removeChild(childOneNode);
			} else {
				eliminateFakeChilds(childOneNode);
				currentNodeIndex++;
			}
		}
	}
	/**
	 * for some strange reason, formated XML text is interpreted like childs of the DOM node (tabs, spaces, comments etc)
	 * this function says if such a node is of that "fake" type, or not
	 * @param node
	 * @return
	 */
	private static boolean isTrueNode(Node node) {
		if (node == null) {
			return false;
		}
		if (node.getNodeName().compareTo("#comment") == 0)
			return false;
		if (node.getNodeName().compareTo("#text") == 0) {
			String value = node.getNodeValue();
			for (int i=0;i<value.length();i++) {
				char c = value.charAt(i);
				if (
						(c != ' ')
						&& (c != '\t')
						&& (c != '\n')
					) 
				{			
					return true;
				}
			}
			return false;			
		}
			return true;
	}
	
	public static Vector<Vector<Integer>> setPartitions(ElectionSpecification es, String confFile)  {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		Document doc=null;
		Section[] orderedSections = es.getOrderedSections();
		try {
			doc = factory.newDocumentBuilder().parse(confFile);			
		} catch (Exception e) {
			//e.printStackTrace();
			
			//if no partitions file exists, all the questions are in the same partition
			Vector<Vector<Integer>> partitions=new Vector<Vector<Integer>>();
			Vector<Integer> p=new Vector<Integer>();
			partitions.add(p);
			int poz=0;
			for (int s=0;s<orderedSections.length;s++) {
				Question[] orderedQuestion = orderedSections[s].getOrderedQuestions();
				for (int q=0;q<orderedQuestion.length;q++) {					
					p.add(poz++);
				}
			}
			return partitions;
		}
		
		NodeList sections = doc.getElementsByTagName("section");
		
		Vector<Vector<Integer>> partitions=new Vector<Vector<Integer>>();
		
		int poz=0;
		for (int s=0;s<orderedSections.length;s++) {
			Question[] orderedQuestion = orderedSections[s].getOrderedQuestions();
			for (int q=0;q<orderedQuestion.length;q++) {
				int p = getPartition(sections,orderedSections[s].getId(),orderedQuestion[q].getId());
				if (p>=partitions.size()) {
					for (int k=0;k<p-partitions.size()+1;k++)
						partitions.add(new Vector<Integer>());
				}
				if (p!=-1)
					partitions.get(p).add(poz++);
			}
		}
		return partitions;
	}	
	
	private static int getPartition(NodeList sections,String sectionId, String questionId) {
		for (int i=0;i<sections.getLength();i++) {
			Node s = sections.item(i);
			String sId=s.getAttributes().getNamedItem("id").getNodeValue();
			if (sectionId.compareTo(sId)==0) {
				for (Node node1=s.getFirstChild();node1!=null;node1=node1.getNextSibling()) {
					if (node1.getNodeName().compareTo("questions")==0) {
						for (Node node2=node1.getFirstChild();node2!=null;node2=node2.getNextSibling()) {
							if (node2.getNodeName().compareTo("question")==0) {
								if (node2.getAttributes().getNamedItem("id").getNodeValue().compareTo(questionId)==0) {
									return  Integer.parseInt(node2.getAttributes().getNamedItem("partitionNo").getNodeValue());
								}
							}						
						}
						return -1;
					}
				}
			}		
		}
		return -1;
	}
	
	public static String AddleadingZeros(String serial,int serialLenght) {
		String ret=new String(serial);
		while (ret.length()<serialLenght) {
			ret="0"+ret;
		}
		return ret;
	}
	
	public static String prepareXMLforUpload(String xml) {
		String ret = xml;
		ret = ret.replace("=","%3D");
		ret = ret.replaceAll("\"","%22");
		ret = ret.replace(' ','+');
		ret = ret.replaceAll("\t","");
		ret = ret.replaceAll("\n","");		
		return ret;
	}
	
	public static void printPdfSilently(String file) {
        try {
			Runtime.getRuntime().exec("cmd.exe start /C acrord32 /p /h " + file);
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
	
	public static void printPdfPrintDialog(String file) {
        try {
			Runtime.getRuntime().exec("cmd.exe start /C acrord32 /p " + file);
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
		
	public static Document DomParse(String fileToParse) throws SAXException, IOException {
		return DomParse(new File(fileToParse));					
	}

	public static Document DomParse(File fileToParse) throws SAXException, IOException {
		return db.parse(fileToParse);					
	}
	
	public static DocumentBuilder GetDocumentBuilder() {
		return db;
	}
	
	public static SAXParser newSAXParser() {
		SAXParser s=null;
	     try {
			s=saxFactory.newSAXParser();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}
		return s;
	}
	
	public static BallotType askWhatTypeOfBallot(Component parent) {
		BallotType[] options = {
				BallotType.PUNCHSCAN,
				BallotType.SCANTEGRITY};
		int n = JOptionPane.showOptionDialog(parent,
				"What ballot I am looking at?",
				"Question",
			JOptionPane.YES_NO_OPTION,
			JOptionPane.QUESTION_MESSAGE,
			null,     //don't use a custom Icon
			options,  //the titles of buttons
			options[0]); //default button title
		if (n==JOptionPane.CLOSED_OPTION) {
			return null;
		}
		
		
		if (n==JOptionPane.YES_OPTION) {
			return options[0];
		}
		if (n==JOptionPane.NO_OPTION) {
			return options[1];
		}
		return null;
	}
	
	public static Hashtable<String,String> SerialToPid(String serialMap) throws SAXException, IOException {
		Hashtable<String,String> serialToPid=new Hashtable<String,String>();
		Document doc = Util.DomParse(serialMap);
		
		NodeList nodeList = doc.getElementsByTagName("row");
		for (int i=0;i<nodeList.getLength();i++) {
			NamedNodeMap attr = nodeList.item(i).getAttributes();
			serialToPid.put(attr.getNamedItem("serial").getNodeValue(), attr.getNamedItem("id").getNodeValue());
		}
		return serialToPid;
	}

	public static Hashtable<String,String> PidToSerial(String serialMap) throws SAXException, IOException {
		Hashtable<String,String> serialToPid=new Hashtable<String,String>();
		Document doc = Util.DomParse(serialMap);
		NodeList nodeList = doc.getElementsByTagName("row");
		for (int i=0;i<nodeList.getLength();i++) {
			NamedNodeMap attr = nodeList.item(i).getAttributes();
			serialToPid.put(attr.getNamedItem("id").getNodeValue(),attr.getNamedItem("serial").getNodeValue());
		}
		return serialToPid;
	}
	
	public static void print(Rectangle rect) {
		System.out.print("top "+rect.getTop());
		System.out.print(" bottom "+rect.getBottom());
		System.out.print(" width "+rect.getWidth());
		System.out.println(" heigth "+rect.getHeight());
	}	
}

