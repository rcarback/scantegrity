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

import java.awt.Dimension;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

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
	protected Point[] c_alignment;
	protected Double c_tolerance = .05;
	//Dimensions of the ballot
	protected Dimension c_dimension;
	protected SerialNumberReader c_serial = null;
	protected AlignmentMarkReader c_alignmentMark = null;

	/**
	 * Create a ballot from the ballot image. 
	 * @param p_serial
	 * @param p_styles
	 * @return
	 */
	abstract public Ballot scanBallot(BallotStyle[] p_styles, 
										BufferedImage p_img);
	
	/**
	 * Use the alignment marks and image data to renormalize the image so that
	 * any x,y coordinate matches any x,y in the scanner configuration.
	 * @param p_img
	 * @return
	 */
	protected AffineTransformOp getAlignmentOp(BufferedImage p_img) {
		//According to image data, is this bigger or smaller?
		Double l_scale = p_img.getWidth()/c_dimension.getWidth();
		//Double l_yScale = p_img.getHeight()/c_dimensions.getHeight();
		
		//Print a warning if the height is off by more than 5%.
		Double l_off = p_img.getHeight()/(c_dimension.getHeight()*l_scale);
		if (l_off < 1-c_tolerance || l_off > 1+c_tolerance) {
			//TODO:Needs to be logged, not printed. Maybe should 
			//throw exception?
			System.out.println("Warning, height is " + p_img.getHeight()
					+ " not " + c_dimension.getHeight()*l_scale);
		}
		
		//Scale the alignment marks
		Point[] l_alignment = new Point[2];
		l_alignment[0] = new Point(c_alignment[0]);
		l_alignment[1] = new Point (c_alignment[1]);
		for (Point l_mark : l_alignment) {
			l_mark.setLocation(l_mark.getX()*l_scale, l_mark.getY()*l_scale);
			//TODO: Logging!
		//	System.out.println("Scaling Alignment Mark: " + l_mark.getX() + ", "
			//					+ l_mark.getY());
		}
		c_alignmentMark.setScale(l_scale);
		c_alignmentMark.setTolerance(c_tolerance);
		
		//Find the alignment marks
		Point l_foundMarks[] = new Point[2];
		try
		{
			l_foundMarks[0] = c_alignmentMark.findMark(p_img, l_alignment[0]);
			l_foundMarks[1] = c_alignmentMark.findMark(p_img, l_alignment[1]);
		} catch(Exception e) {}
		
		if (l_foundMarks[0] == null || l_foundMarks[1] == null) {
			System.out.println("Unable to find alignment marks, aborting!");
			return null;
		}
		
		//System.out.println("Alignment Marks: (" + l_foundMarks[0].x + ", " + 
		//					l_foundMarks[0].y + "), (" + l_foundMarks[1].x +
		//					", " + l_foundMarks[1].y + ")");
		
		//TODO: Make sure the found alignment marks are unique!
				
		//Using the alignment marks (hopefully found), try to translate the image
		//properly and find the serial number
		AffineTransformOp l_transform = compute2DTransform(c_alignment, l_foundMarks);
		
		return l_transform;
	}
	
	
	/**	 
	 * @param sul - scanned upper left
	 * @param slr - scanned lower right
	 */
	private AffineTransformOp compute2DTransform(Point[] p_expected, Point[] p_detected)
	{	
		if (p_expected.length != 2 || p_detected.length != 2) {
			//Die
			return null;
		}
		//We have to convert to double precision for this math.
		Point2D.Double l_exp[] = new Point2D.Double[2];
		l_exp[0] = new Point2D.Double(p_expected[0].x, p_expected[0].y);
		l_exp[1] = new Point2D.Double(p_expected[1].x, p_expected[1].y);
		Point2D.Double l_det[] = new Point2D.Double[2];
		l_det[0] = new Point2D.Double(p_detected[0].x, p_detected[0].y);
		l_det[1] = new Point2D.Double(p_detected[1].x, p_detected[1].y);
		
		//System.out.println(l_det[0].x + ", " + l_det[0].y);
		//System.out.println(l_det[1].x + ", " + l_det[1].y);
				
		//The Transformations we will compute:
		AffineTransform l_scaleTransform, l_rotTransform, l_tranTransform;

		/*Compute the true scale by dividing the distance of the expected
		 * and the detected points. Everything gets converted to the expected
		 * space, so we must divide by detected. 
		 */
		Double l_scale = l_exp[0].distance(l_exp[1])/l_det[0].distance(l_det[1]);
		
		//Now, convert det to exp's scaled space. This assumes a flat space,
		//which is entirely expected here.
		l_scaleTransform = AffineTransform.getScaleInstance(l_scale, l_scale);
		l_scaleTransform.transform(l_det[0], l_det[0]);
		l_scaleTransform.transform(l_det[1], l_det[1]);

		//System.out.println(l_det[0].x + ", " + l_det[0].y);
		//System.out.println(l_det[1].x + ", " + l_det[1].y);
		
		
		/* Determine the translation, or the distance the midpoint lines are
		 * from each other. This gives us a common reference point that
		 * should be in the same spot regardless of how messed up it is.
		 */
		Point2D.Double l_expMid, l_detMid;
		l_expMid = new Point2D.Double((l_exp[0].getX()+l_exp[1].getX())/2,
										(l_exp[0].getY()+l_exp[1].getY())/2);
		l_detMid = new Point2D.Double((l_det[0].getX()+l_det[1].getX())/2,
										(l_det[0].getY()+l_det[1].getY())/2);

		Double l_tx, l_ty;
		l_tx = l_expMid.x - l_detMid.x;
		l_ty = l_expMid.y - l_detMid.y;
		
		//System.out.println("Mid: " + l_expMid.x + ", " + l_expMid.y);
		//System.out.println("Translate: " + l_tx + ", " + l_ty);
		
		l_tranTransform = AffineTransform.getTranslateInstance(l_tx, l_ty);

		l_tranTransform.transform(l_det[0], l_det[0]);
		l_tranTransform.transform(l_det[1], l_det[1]);
		

		//System.out.println(l_det[0].x + ", " + l_det[0].y);
		//System.out.println(l_det[1].x + ", " + l_det[1].y);

		/* Lines are the same size, calculate the rotation of the lines 
		 * assuming a flat 2D space treating the lines as a tan around an 
		 * invisible circle. Uses the dotproduct method.
		 */
		//expected, detected, and basic vectors
		Point2D.Double l_eV, l_dV, l_bV;
		l_dV = new Point2D.Double();
		l_eV = new Point2D.Double();
		l_bV = new Point2D.Double();
		l_dV.x = l_det[0].x - l_expMid.x;
		l_dV.y = l_expMid.y - l_det[0].y;
		l_eV.x = l_exp[0].x - l_expMid.x;
		l_eV.y = l_expMid.y - l_exp[0].y;
		l_bV.x = 1;
		l_bV.y = 0;
				
		//System.out.println("l_dV: " + l_dV.x + ", " + l_dV.y);
		//System.out.println("l_eV: " + l_eV.x + ", " + l_eV.y);
		//System.out.println("l_bV: " + l_bV.x + ", " + l_bV.y);
		
		//Subtract angle between det and base
		Double l_theta = Math.acos((l_dV.x*l_bV.x + l_dV.y*l_bV.y)/(
				Math.sqrt(l_dV.x*l_dV.x + l_dV.y*l_dV.y)
				*Math.sqrt(l_bV.x*l_bV.x + l_bV.y*l_bV.y)));
		
		//Angle is on the underneath. Represent counter clockwise angle.
		if (l_dV.y < 0) l_theta = 4*Math.PI - l_theta;
		
		//System.out.println("Theta: " + l_theta*180/Math.PI);

		//Calculate angle between exp and base
		l_theta -= Math.acos((l_eV.x*l_bV.x + l_eV.y*l_bV.y)/(
						Math.sqrt(l_eV.x*l_eV.x + l_eV.y*l_eV.y)
						*Math.sqrt(l_bV.x*l_bV.x + l_bV.y*l_bV.y)));

		//System.out.println("Theta: " + l_theta*180/Math.PI);

		l_rotTransform = AffineTransform.getRotateInstance(l_theta, 
														l_expMid.getX(), 
														l_expMid.getY());
		l_rotTransform.transform(l_det[0], l_det[0]);
		l_rotTransform.transform(l_det[1], l_det[1]);
		
		//System.out.println(l_det[0].x + ", " + l_det[0].y);
		//System.out.println(l_det[1].x + ", " + l_det[1].y);

		
		/* TODO: If they don't match at this point, we probably got the alignment
		 * marks backwards, or detected the wrong marks.. not sure what to do.
		 * l_det[0] will always match, l_det[1] will be the one off here.
		 * Right now the plan is to see if we can find the serial number, then
		 * retry, then try to find the serial again. If we get neg's on both,
		 * we report an error.
		 */	
		
		/*Combine the transforms 
		 */
		AffineTransform l_finalTransform = new AffineTransform();
		l_finalTransform.concatenate(l_rotTransform);
		l_finalTransform.concatenate(l_tranTransform);
		l_finalTransform.concatenate(l_scaleTransform);

		
		AffineTransformOp l_ret = new AffineTransformOp(l_finalTransform, 
				AffineTransformOp.TYPE_BILINEAR);
		
		return l_ret;
		
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
	public Dimension getDimension()
	{
		return c_dimension;
	}

	/**
	 * @param p_dimensions the dimensions to set
	 */
	public void setDimension(Dimension p_dimension)
	{
		c_dimension = p_dimension;
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
	


	/**
	 * @return the tolerance
	 */
	public Double getTolerance()
	{
		return c_tolerance;
	}

	/**
	 * @param p_tolerance the tolerance to set
	 */
	public void setTolerance(Double p_tolerance)
	{
		c_tolerance = p_tolerance;
	}

	/**
	 * @param alignmentMark the alignmentMark to set
	 */
	public void setAlignmentMark(AlignmentMarkReader alignmentMark)
	{
		c_alignmentMark = alignmentMark;
	}

	/**
	 * @return the alignmentMark
	 */
	public AlignmentMarkReader getAlignmentMark()
	{
		return c_alignmentMark;
	}
}	

