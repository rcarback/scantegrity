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
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

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
	private Double c_tolerance = .05;
	//Dimensions of the ballot
	private Dimension c_dimension;
	private SerialNumberReader c_serial = null;
	private BallotStyle[] c_styles = null;
	private AlignmentMarkReader c_alignmentMark = null;
	private boolean debug = true;
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
	protected BufferedImage normalizeImage(BufferedImage p_img) {
		//According to image data, is this bigger or smaller?
		Double l_scale = p_img.getWidth()/c_dimension.getWidth();
		//Double l_yScale = p_img.getHeight()/c_dimensions.getHeight();
		
		//Print a warning if the height is off by more than 5%.
		Double l_off = p_img.getHeight()*l_scale/c_dimension.getHeight();
		if (l_off < 1-c_tolerance || l_off > 1+c_tolerance) {
			//TODO:Needs to be logged, not printed. Maybe should 
			//throw exception?
			System.out.println("Warning, height is " + p_img.getHeight()*l_scale
					+ "not " + c_dimension.getHeight());
		}
		
		//Scale the alignment marks
		Point[] l_alignment = c_alignment.clone();
		for (Point l_mark : l_alignment) {
			l_mark.setLocation(l_mark.getX()*l_scale, l_mark.getY()*l_scale);
			//TODO: Logging!
			//System.out.println("Scaling Alignment Mark: " + l_mark.getX() + ", "
				//				+ l_mark.getY());
		}
		//p_radius = c_radius;
		
		//Find the alignment marks
		long l_start = System.currentTimeMillis();
		Point l_foundMarks[] = new Point[2];
		try
		{
		
			c_alignmentMark.setScale(l_scale);
			l_foundMarks[0] = c_alignmentMark.findMark(p_img, l_alignment[0]);
			System.out.println("Alignment Mark found: " + l_foundMarks[0].x + ", " +
					l_foundMarks[0].y);
			l_foundMarks[1] = c_alignmentMark.findMark(p_img, l_alignment[1]);
			System.out.println("Alignment Mark found: " + l_foundMarks[1].x + ", " +
					l_foundMarks[1].y);
		} catch(Exception e) {}

		System.out.println(System.currentTimeMillis()-l_start + "ms");
		//TODO: Make sure the found alignment marks are unique!
		
		
		//Using the alignment marks (hopefully found), try to translate the image
		//properly and find the serial number
		l_start = System.currentTimeMillis();
		AffineTransformOp l_transform = compute2DTransform(l_alignment, l_foundMarks);
		
		Point l_tst = new Point();
		l_transform.getPoint2D(l_foundMarks[0], l_tst);
		System.out.println("Alignment Mark loc: " + l_foundMarks[0].x + ", " +
				l_foundMarks[0].y);
		System.out.println("Transformed:" + l_tst.x + "," + l_tst.y);
		
		l_transform.getPoint2D(l_foundMarks[1], l_tst);
		System.out.println("Alignment Mark loc: " + l_foundMarks[1].x + ", " +
				l_foundMarks[1].y);
		System.out.println("Transformed:" + l_tst.x + "," + l_tst.y);		
		System.out.println("Transformed in " + (System.currentTimeMillis()-l_start) + "ms");
		
		
		/*
		try
		{
			System.out.println("Serial Number!");
			if (c_serial.getSerialNumber(p_img, l_transform) == null) {
				Point l_backwards[] = new Point[2];
				l_backwards[0] = l_foundMarks[1];
				l_backwards[1] = l_foundMarks[0];				
				l_transform = compute2DTransform(l_alignment, l_backwards);
				l_transform.getPoint2D(l_foundMarks[0], l_tst);
				System.out.println("Alignment Mark loc: " + l_foundMarks[0].x + ", " +
						l_foundMarks[0].y);
				System.out.println("Transformed:" + l_tst.x + "," + l_tst.y);
				
				l_transform.getPoint2D(l_foundMarks[1], l_tst);
				System.out.println("Alignment Mark loc: " + l_foundMarks[1].x + ", " +
						l_foundMarks[1].y);
				System.out.println("Transformed:" + l_tst.x + "," + l_tst.y);		
				System.out.println("Transformed in " + (System.currentTimeMillis()-l_start) + "ms");				
				c_serial.getSerialNumber(p_img, l_transform);
			}
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		/* Stefan's Old Code
		BufferedImage img = p_img;
		
		Double dpi = img.getWidth() / bgm.getWidth();
		// System.out.println("dpi="+dpi);
		// check if the alignment marks are where they should be.

		int b = 70;
		Cluster black = new Cluster(new Color(b, b, b), new Color(b, b, b),
				0.05);

		long start = System.currentTimeMillis();
		ImageToCoordinatesInches itc = new ImageToCoordinatesInches(img, dpi,
				null);
		// System.out.println("Making ImageToCoordinatesInches took "+(System.currentTimeMillis()-start)+" milisseconds");

		Cluster ulc = null;

		start = System.currentTimeMillis();
		long timeToWait = 7000;// 7seconds
		boolean timeout = false;
		do
		{
			if (System.currentTimeMillis() - start > timeToWait)
			{
				timeout = true;
				break;
			}
			ulc = itc.detectCircular(ul.getX(), ul.getY(), 0.5, black);
			// System.out.println(ulc);
		} while (ulc != null && !isCircle(img, ulc) && !timeout);

		if (timeout)
			throw new Exception(
					"Cound not detect upper left alignment mark in "
							+ (timeToWait / 1000) + " seconds. Aborting");
		if (ulc == null)
			throw new Exception(
					"Cannot detect Upper left alignment mark. Aborting");

		// System.out.println("Detecting one alignment mark took "+(System.currentTimeMillis()-start)+" milisseconds");

		Cluster lrc = null;
		start = System.currentTimeMillis();
		timeout = false;
		do
		{
			// System.out.println(lrc+" "+lr.getX()+" "+lr.getY());
			if (System.currentTimeMillis() - start > timeToWait)
			{
				timeout = true;
				break;
			}
			lrc = itc.detectCircular(lr.getX(), lr.getY(), 0.5, black);
		} while (ulc != null && !isCircle(img, lrc) && !timeout);

		if (timeout)
			throw new Exception(
					"Cound not detect lower right alignment mark in "
							+ (timeToWait / 1000) + " seconds. Aborting");

		if (lrc == null)
			throw new Exception(
					"Cannot detect lower right alignment mark. Aborting");

		// System.out.println("Detecting one alignment mark took "+(System.currentTimeMillis()-start)+" milisseconds");

		// make the image perfectly aligned
		start = System.currentTimeMillis();
		BufferedImage corectedImage = computeTransformation(img, ulc
				.getCenterOfMass(), lrc.getCenterOfMass(), dpi);
		// System.out.println("Making the image perfect took "+(System.currentTimeMillis()-start)+" milisseconds");
		if (debug)
		{
			itc = new ImageToCoordinatesInches(corectedImage, dpi, null);
			ulc = null;
			do
			{
				ulc = itc.detectCircular(ul.getX(), ul.getY(), 1, black);
			} while (ulc != null && !isCircle(img, ulc));
			lrc = null;
			do
			{
				lrc = itc.detectCircular(lr.getX(), lr.getY(), 1, black);
			} while (ulc != null && !isCircle(img, lrc));

			Graphics2D g = corectedImage.createGraphics();
			g.setColor(Color.GREEN);

			// draw rectangles where the alignment marks are
			g.drawRect((int) (ulc.getCenterOfMass().getX() * dpi) - 20,
					(int) (ulc.getCenterOfMass().getY() * dpi) - 20, 40, 40);
			g.drawRect((int) (lrc.getCenterOfMass().getX() * dpi) - 20,
					(int) (lrc.getCenterOfMass().getY() * dpi) - 20, 40, 40);

			g.setColor(Color.BLUE);
			// draw rectengles where the alignment marks should be
			Point2D.Double ul = bgm.getUpperAlignment();
			g.drawRect((int) (ul.getX() * dpi) - 20,
					(int) (ul.getY() * dpi) - 20, 40, 40);

			Point2D.Double lr = bgm.getLowerAlignment();
			g.drawRect((int) (lr.getX() * dpi) - 20,
					(int) (lr.getY() * dpi) - 20, 40, 40);

			System.out.println("perfect " + bgm.getUpperAlignment());
			System.out.println("detected " + ulc.getCenterOfMass());
			System.out.println("perfect " + bgm.getLowerAlignment());
			System.out.println("detected " + lrc.getCenterOfMass());
		}

		detectFromPerfectImage(corectedImage, dpi);
		*/
		return p_img;
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
		//The Transformations we will compute:
		AffineTransform l_scaleTransform, l_rotTransform, l_tranTransform;
		
		Point2D.Double l_expMid, l_detMid;
		l_expMid = new Point2D.Double((l_exp[0].getX()+l_exp[1].getX())/2,
										(l_exp[0].getY()+l_exp[1].getY())/2);
		l_detMid = new Point2D.Double((l_det[0].getX()+l_det[1].getX())/2,
										(l_det[0].getY()+l_det[1].getY())/2);
		
		
		Double l_tx, l_ty;
		l_tx = l_expMid.x - l_detMid.x;
		l_ty = l_expMid.y - l_detMid.y;
		l_tranTransform = AffineTransform.getTranslateInstance(l_tx, l_ty);

		l_tranTransform.transform(l_det[0], l_det[0]);
		l_tranTransform.transform(l_det[1], l_det[1]);
		
		System.out.println("Transform: ");
		System.out.println(l_det[0].getX() + ", " + l_det[0].getY());
		System.out.println(l_det[1].getX() + ", " + l_det[1].getY());
		
		
		/*First, compute the true scale by dividing the distance of the expected
		 * and the detected points. Everything gets converted to the expected
		 * space, so we must divide by detected. 
		 */
		Double l_scale = l_exp[0].distance(l_exp[1])/l_det[0].distance(l_det[1]);
		
		//Now, convert det to exp's scaled space. This assumes a flat space,
		//which is entirely expected here.
		l_scaleTransform = AffineTransform.getScaleInstance(l_scale, l_scale);
		l_scaleTransform.transform(l_det[0], l_det[0]);
		l_scaleTransform.transform(l_det[1], l_det[1]);
		
		System.out.println("Scale: ");
		System.out.println(l_det[0].getX() + ", " + l_det[0].getY());
		System.out.println(l_det[1].getX() + ", " + l_det[1].getY());
		
				
		/* Lines are now the same size, calculate the rotation of the lines 
		 * assuming a flat 2D space treating the lines as a tan around an 
		 * invisible circle.
		 */
		Double l_detAng, l_expAng, l_rotAng;

		//l_detAng = Math.atan((l_det[0].x-l_expMid.x)/(l_expMid.y-l_det[0].y));
		//l_expAng = Math.atan((l_exp[0].x-l_expMid.x)/(l_expMid.y-l_exp[0].y));
		l_detAng = Math.atan((l_det[0].x-l_det[1].x)/(l_det[1].y-l_det[0].y));
		l_expAng = Math.atan((l_exp[0].x-l_exp[1].x)/(l_exp[1].y-l_exp[0].y));
		l_rotAng = l_expAng-l_detAng;

		
		System.out.println("Angles: " + l_detAng + ", " + l_expAng);
		System.out.println(l_rotAng);
		
		if (l_det[0].distance(l_exp[0]) > c_dimension.getHeight()/3)
		{
			l_rotAng += Math.PI;
		}
		
		l_rotTransform = AffineTransform.getRotateInstance(l_rotAng, 
														l_expMid.getX(), l_expMid.getY());
		l_rotTransform.transform(l_det[0], l_det[0]);
		l_rotTransform.transform(l_det[1], l_det[1]);
		
		
		System.out.println("Rot: ");
		System.out.println(l_det[0].getX() + ", " + l_det[0].getY());
		System.out.println(l_det[1].getX() + ", " + l_det[1].getY());		

		/* Lines are now same size and rotation. Final step is to calc the
		 * translation
		 */
		//Double l_tx, l_ty;
		/*l_tx = l_exp[0].x - l_det[0].x;
		l_ty = l_exp[0].y - l_det[0].y;
		l_tranTransform = AffineTransform.getTranslateInstance(l_tx, l_ty);
		
		l_tranTransform.transform(l_det[0], l_det[0]);
		l_tranTransform.transform(l_det[1], l_det[1]);
		
		if ((int)l_det[0].x != (int)l_exp[0].x || 
				(int)l_det[0].y != (int)l_exp[0].y ||
				(int)l_det[1].x != (int)l_exp[1].x ||
				(int)l_det[1].y != (int)l_exp[1].y)
		{
				//l_detAng = Math.atan((l_det[1].y-l_det[0].y)/(l_det[0].x-l_det[1].x));
				//l_rotAng = l_detAng - l_expAng;
				//l_rotTransform = AffineTransform.getRotateInstance(l_rotAng); 
		}*/

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
		l_finalTransform.concatenate(l_scaleTransform);
		l_finalTransform.concatenate(l_tranTransform);

		
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

