/*
 * @(#)BallotReader.java.java
 *  
 * Copyright (C) 2008-2009 Scantegrity Project
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
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

import org.scantegrity.common.Cluster;
import org.scantegrity.common.ImageToCoordinatesInches;

/**
 * BallotReader is an interface for reading ballot data, normalizing the ballot
 * and other tasks. It abstracts scanner functionality away from the Scanning
 * user interface. There should only be two known types, one for Scantegrity and
 * one for Punchscan, but there may be differences in the future.
 * 
 * Alignment marks (which may need to be abstracted in the future into their
 * own detection algorithm) are stored as circles. with a point at the center
 * and a radius.
 * 
 * TODO: Needs constructors.
 * 
 * @author Richard Carback
 *
 */
public abstract class BallotReader
{
	//Alignment marks
	private Point[] c_alignment;
	private Integer c_radius;
	//Dimensions of the ballot
	private Rectangle c_dimensions;
	private SerialNumberReader c_serial = null;
	private BallotStyle[] c_styles = null;
	
	
	
	/**
	 * Create a ballot from the ballot image. 
	 * @param p_serial
	 * @param p_styles
	 * @return
	 */
	abstract public Ballot scanBallot(SerialNumberReader p_serial, 
										BallotStyle[] p_styles, 
										BufferedImage p_img);
	
	/**
	 * Use the alignment marks and image data to renormalize the image so that
	 * any x,y coordinate matches any x,y in the scanner configuration.
	 * @param p_img
	 * @return
	 */
	public BufferedImage normalizeImage(BufferedImage p_img) {
		//According to image data, is this bigger or smaller?
		Double l_scale = p_img.getWidth()/c_dimensions.getWidth();
		//Double l_yScale = p_img.getHeight()/c_dimensions.getHeight();

		//Rescale the alignment marks
		for (Point l_mark : c_alignment) {
			l_mark.setLocation(l_mark.getX()*l_scale, l_mark.getY()*l_scale);
		}
		
		AffineTransform l_at = AffineTransform.getScaleInstance(l_scale, 
				l_scale);
		
		//g.drawRenderedImage(p_img,at);
		
		
		return p_img;
		/* The rest of this is stefan's code, needs to be integrated).
		//System.out.println("dpi="+dpi);		
		//check if the alignment marks are where they should be.
		int b = 70;
		Cluster black = new Cluster(new Color(b,b,b),new Color(b,b,b),0.05);
				
		long start = System.currentTimeMillis();
		//ImageToCoordinatesInches itc=new ImageToCoordinatesInches(img,dpi, null);
		//System.out.println("Making ImageToCoordinatesInches took "+(System.currentTimeMillis()-start)+" milisseconds");
				
		Cluster ulc=null;		
				
		start = System.currentTimeMillis();
		long timeToWait = 7000;//7seconds
		boolean timeout = false;
		do {
			if (System.currentTimeMillis()-start>timeToWait) {
				timeout=true;
				break;
			}
			
			ulc = itc.detectCircular(ul.getX(), ul.getY(), 0.5, black);
			//System.out.println(ulc);			
		} while (ulc !=null && !isCircle(img, ulc) && !timeout);
	
		if (timeout)
			throw new Exception("Cound not detect upper left alignment mark in "+(timeToWait/1000)+" seconds. Aborting");
		if (ulc==null)
			throw new Exception("Cannot detect Upper left alignment mark. Aborting");

		//System.out.println("Detecting one alignment mark took "+(System.currentTimeMillis()-start)+" milisseconds");
	
		Cluster lrc=null;
		start=System.currentTimeMillis();
		timeout=false;
		do {
			//System.out.println(lrc+" "+lr.getX()+" "+lr.getY());			
			if (System.currentTimeMillis()-start>timeToWait) {
				timeout=true;
				break;
			}
			lrc=itc.detectCircular(lr.getX(), lr.getY(), 0.5, black);			
		} while(ulc !=null && !isCircle(img, lrc) && !timeout);

		if (timeout)
			throw new Exception("Cound not detect lower right alignment mark in "+(timeToWait/1000)+" seconds. Aborting");
	
		if (lrc==null)
			throw new Exception("Cannot detect lower right alignment mark. Aborting");
	
		//System.out.println("Detecting one alignment mark took "+(System.currentTimeMillis()-start)+" milisseconds");		
	
		//make the image perfectly aligned
		start=System.currentTimeMillis();
		BufferedImage corectedImage = computeTransformation(img, ulc.getCenterOfMass(),lrc.getCenterOfMass(), dpi);
		//System.out.println("Making the image perfect took "+(System.currentTimeMillis()-start)+" milisseconds");						
		if (debug) {
			itc=new ImageToCoordinatesInches(corectedImage,dpi, null);
			ulc=null;
			do { 
				ulc=itc.detectCircular(ul.getX(), ul.getY(), 1, black);
			} while (ulc !=null && !isCircle(img, ulc));
			lrc=null;
			do {
				lrc=itc.detectCircular(lr.getX(), lr.getY(), 1, black);
			} while(ulc !=null && !isCircle(img, lrc));

			Graphics2D g=corectedImage.createGraphics();
			g.setColor(Color.GREEN);
			
			//draw rectangles where the alignment marks are
			g.drawRect((int)(ulc.getCenterOfMass().getX()*dpi)-20,(int)(ulc.getCenterOfMass().getY()*dpi)-20,40,40);
			g.drawRect((int)(lrc.getCenterOfMass().getX()*dpi)-20,(int)(lrc.getCenterOfMass().getY()*dpi)-20,40,40);
			
			g.setColor(Color.BLUE);
			//draw rectengles where the alignment marks should be
			Point2D.Double ul=bgm.getUpperAlignment();
			g.drawRect((int)(ul.getX()*dpi)-20, (int)(ul.getY()*dpi)-20, 40,40);
			
			Point2D.Double lr=bgm.getLowerAlignment();
			g.drawRect((int)(lr.getX()*dpi)-20, (int)(lr.getY()*dpi)-20, 40,40);
			
			System.out.println("perfect "+bgm.getUpperAlignment());
			System.out.println("detected "+ulc.getCenterOfMass());
			System.out.println("perfect "+bgm.getLowerAlignment());
			System.out.println("detected "+lrc.getCenterOfMass());
		}
		
		detectFromPerfectImage(corectedImage,dpi);		*/
	}

	/**
	 * @return the alignment
	 */
	public Point[] getAlignment()
	{
		return c_alignment;
	}

	/**
	 * @param p_alignment the alignment to set
	 */
	public void setAlignment(Point[] p_alignment)
	{
		c_alignment = p_alignment;
	}

	/**
	 * @return the dimensions
	 */
	public Rectangle getDimensions()
	{
		return c_dimensions;
	}

	/**
	 * @param p_dimensions the dimensions to set
	 */
	public void setDimensions(Rectangle p_dimensions)
	{
		c_dimensions = p_dimensions;
	}

	/**
	 * @return the radius
	 */
	public Integer getRadius()
	{
		return c_radius;
	}

	/**
	 * @param p_radius the radius to set
	 */
	public void setRadius(Integer p_radius)
	{
		c_radius = p_radius;
	}

	/**
	 * @return the styles
	 */
	public BallotStyle[] getStyles()
	{
		return c_styles;
	}


	/**
	 * @param p_styles the styles to set
	 */
	public void setStyles(BallotStyle[] p_styles)
	{
		c_styles = p_styles;
	}
	
	/**
	 * @return the serial
	 */
	public SerialNumberReader getSerial()
	{
		return c_serial;
	}


	/**
	 * @param p_serial the serial to set
	 */
	public void setSerial(SerialNumberReader p_serial)
	{
		c_serial = p_serial;
	}	
	
}	

