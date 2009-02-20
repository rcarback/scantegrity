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

import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyPair;
import java.security.Provider;
import java.util.Collections;
import java.util.TreeMap;

import javax.xml.crypto.MarshalException;
import javax.xml.crypto.dsig.CanonicalizationMethod;
import javax.xml.crypto.dsig.DigestMethod;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.SignatureMethod;
import javax.xml.crypto.dsig.SignedInfo;
import javax.xml.crypto.dsig.Transform;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureException;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.XMLSignature.SignatureValue;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.keyinfo.KeyValue;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.scantegrity.common.BallotGeometryMap;
import org.scantegrity.common.Cluster;
import org.scantegrity.common.Prow;
import org.scantegrity.common.Util;
import org.scantegrity.scanner.ScannedBallot.TypeOfVotes;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.lowagie.text.DocumentException;
import com.lowagie.text.Image;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.BarcodePDF417;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfTemplate;
import com.lowagie.text.pdf.PdfWriter;

/**
 * Given a scanned ballot, it can generate an xml signature, and
 * a pdf overlay for the scanned ballot
 * @author stefan
 *
 */
public class ScannedBallotWithPdfOverlaysAndXmlSignatures {

	KeyPair pair=null;
	XMLSignature signer=null;
	
	SignatureValue signatureValue=null;	

	ScannedBallotInterface sb=null;

	BallotGeometryMap bgm=null;
	
	static Transformer trans=null;
	static {
		TransformerFactory tf = TransformerFactory.newInstance();
		try {
			trans = tf.newTransformer();
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		}		
	}
	
	public ScannedBallotWithPdfOverlaysAndXmlSignatures(ScannedBallotInterface sb,KeyPair pair) throws Exception {
		this.sb=sb;
	
		this.pair=pair;
		
		bgm=sb.getBallotGeometryMap();
		
        String providerName = System.getProperty
        	("jsr105Provider", "org.jcp.xml.dsig.internal.dom.XMLDSigRI");	

    	 XMLSignatureFactory fac = XMLSignatureFactory.getInstance("DOM",
		     (Provider) Class.forName(providerName).newInstance());	

		Reference ref = fac.newReference
		("", fac.newDigestMethod(DigestMethod.SHA1, null),
		     Collections.singletonList
		  (fac.newTransform
		(Transform.ENVELOPED, (TransformParameterSpec) null)), 
		 null, null);
		
		// Create the SignedInfo
		SignedInfo si = fac.newSignedInfo
		(fac.newCanonicalizationMethod
		 (CanonicalizationMethod.INCLUSIVE_WITH_COMMENTS, 
		  (C14NMethodParameterSpec) null), 
		 fac.newSignatureMethod(SignatureMethod.RSA_SHA1, null),
		 Collections.singletonList(ref));
		
		KeyInfoFactory kif = fac.getKeyInfoFactory();
		KeyValue kv = kif.newKeyValue(pair.getPublic());
		
		// Create a KeyInfo and add the KeyValue to it
		KeyInfo ki = kif.newKeyInfo(Collections.singletonList(kv));
		
		signer = fac.newXMLSignature(si, ki);
	}

	/** 
	 * @return an xml signed document representing a scanned ballot in prow format  
	 */
	public byte[] getSignedXml() {
		Document doc;
		try {
			//doc = Util.GetDocumentBuilder().parse(new ByteArrayInputStream(sb.toXMLString()));			
			doc = Util.GetDocumentBuilder().parse(new ByteArrayInputStream(sb.toProw().toString().getBytes()));
			DOMSignContext dsc = new DOMSignContext(pair.getPrivate(), doc.getDocumentElement());
	        signer.sign(dsc);

	        signatureValue=signer.getSignatureValue();
	        
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			trans.transform(new DOMSource(doc), new StreamResult(bos));

			return bos.toByteArray();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (MarshalException e) {
			e.printStackTrace();
		} catch (XMLSignatureException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * creates a pdf file contaning marks for all valid and invalid marks,
	 * a bar code with the digital signature of the serial number + the marks and
	 * in a corner it has the chosen page, the serial number and the marked possitions
	 * @param file - the file where the overlay is outputed
	 */
	public void makePdfOverlay(String file) {
		float w=(float)bgm.getWidth()*72;
		float h=(float)bgm.getHeight()*72;
		float r=5f/16f/2f*72f;
		Rectangle pageSize = new Rectangle(w,h);
		com.lowagie.text.Document document = new com.lowagie.text.Document(pageSize);
		
        try {
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(file));            
            document.open();
            PdfContentByte cb = writer.getDirectContent();
            PdfTemplate template = cb.createTemplate(w,h);

            float xsize=0;            
            if (sb.getSelectedPage().equals(Prow.ChosenPage.TOP)
            		&& sb.getClass().toString().equals("class software.scanner.ScannedBallot")
            		) 
            	xsize=2*r;
            else
            	xsize=r;
            float x,y;  
            
            TreeMap<Integer, TreeMap<Integer, TreeMap<Integer, Cluster>>> contests=bgm.getMarkedContests();
            
        	TreeMap<Integer, TreeMap<Integer, TreeMap<Integer, TypeOfVotes>>> markedContests=sb.getAllContests();
        	
    		for (Integer contest:markedContests.keySet()) {
    			for (Integer row:markedContests.get(contest).keySet()) {
    				for (Integer candidate:markedContests.get(contest).get(row).keySet()) {
    					Cluster c=contests.get(contest).get(row).get(candidate);
    					//proper vote
    					if (markedContests.get(contest).get(row).get(candidate).equals(TypeOfVotes.Vote)
    							|| markedContests.get(contest).get(row).get(candidate).equals(TypeOfVotes.UnderVote)
    							) {
    		    			template.setColorStroke(Color.GREEN);   			
    		    			template.circle((float)c.getCenterOfMass().getX()*72f, (float)(h-c.getCenterOfMass().getY()*72f), r);
    		    			template.stroke();    						
    					} else {
        					//overvotes
        					if (markedContests.get(contest).get(row).get(candidate).equals(TypeOfVotes.OverVote)
        							||
            					markedContests.get(contest).get(row).get(candidate).equals(TypeOfVotes.NoVote)) {
            	     				x=(float)c.getCenterOfMass().getX()*72f;
            	    				y=h-(float)c.getCenterOfMass().getY()*72f;
            	    				template.setColorStroke(Color.BLACK);
            	    				template.moveTo(x-xsize,y+xsize);
            	    				template.lineTo(x+xsize, y-xsize);
            	    				template.moveTo(x-xsize, y-xsize);
            	    				template.lineTo(x+xsize,y+xsize);
            	        			template.stroke();
            	                    if (sb.getSelectedPage().equals(Prow.ChosenPage.TOP)
            	                    		&& sb.getClass().toString().equals("class software.scanner.ScannedBallot")
            	                    		) {
            	        				template.setColorStroke(Color.WHITE);
            	        				template.setColorFill(Color.WHITE);
            	        				template.circle(x,y,r);
            	        				template.fillStroke();
            	        			}
            					}
    					}
    				}
    			}			
    		}

    		
    		//write the serial number and the votes on a corner
    		String compactRepresentation=sb.getCompactRepresentation();
    		//show string under signature
           	BaseFont symbolFont = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.EMBEDDED);
            int symbolFontSize = 10;
            template.setFontAndSize(symbolFont,symbolFontSize);
            template.setColorStroke(Color.BLACK);
            template.setColorFill(Color.BLACK);
            template.beginText();
    		//print the text on the lower right corner
    		template.showTextAligned(PdfContentByte.ALIGN_LEFT, compactRepresentation.substring(0,compactRepresentation.indexOf(" ")), w-1.1f*72f,0.6f*72f,45);
    		template.showTextAligned(PdfContentByte.ALIGN_LEFT, compactRepresentation.substring(compactRepresentation.indexOf(" ")+1), w-1.3f*72f,0.65f*72f,45);
    		//template.showTextAligned(PdfContentByte.ALIGN_LEFT, s, 0.2f*72f,0.2f*72f,0);
            template.endText();

            try {
	    		String signature=new String(signatureValue.getValue());
	    		
	    		//show signature
	    		BarcodePDF417 codeEAN = new BarcodePDF417();
	    		codeEAN.setText(signature);
	    		Image imageEAN = codeEAN.getImage();
	    		template.addImage(imageEAN, imageEAN.getWidth(), 0f, 0f, imageEAN.getHeight(), 0.5f*72f,0.8f*72f);
            } catch (Exception e) {
    			e.printStackTrace();
    		}
            
            cb.addTemplate(template,0,0);
            //UPSIDEDOWNcb.addTemplate(template,-1,0,0,-1,w,h);
        }
        catch(DocumentException de) {
            System.err.println(de.getMessage());
        }
        catch(IOException ioe) {
            System.err.println(ioe.getMessage());
        }     
        document.close();        
	}	
}
