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
import java.awt.Dimension;
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
	private Integer c_radius = 36;
	private Double c_tolerance = .05;
	//Dimensions of the ballot
	private Dimension c_dimension;
	private SerialNumberReader c_serial = null;
	private BallotStyle[] c_styles = null;
	
	
	//RGB for black.
	private int c_br, c_bg, c_bb;
	
	
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
			System.out.println("Scaling Alignment Mark: " + l_mark.getX() + ", "
								+ l_mark.getY());
		}
		int p_radius = (int)(c_radius*l_scale);
		//p_radius = c_radius;
		long l_start = System.currentTimeMillis();
		try
		{
		Point l_foundmark = findAlignmentMark(p_img, l_alignment[0], p_radius);
		System.out.println("Alignment Mark found: " + l_foundmark.x + ", " +
							l_foundmark.y);
		l_foundmark = findAlignmentMark(p_img, l_alignment[1], p_radius);
		System.out.println("Alignment Mark found: " + l_foundmark.x + ", " +
							l_foundmark.y);
		} catch(Exception e) {}
		System.out.println(System.currentTimeMillis()-l_start + "ms");
		
		try
		{
			System.out.println("Serial Number!");
			c_serial.getSerialNumber(p_img);
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
	 * Returns the center of the circle given p_loc as the expected position.
	 * Assumes circle is black. Won't exceed tolerance level in search. Uses
	 * the radius defined globally.
	 * @param p_img the Image to search
	 * @param p_loc the starting location
	 * @param p_radius radius of the circle
	 * @return
	 */
	protected Point findAlignmentMark(BufferedImage p_img, Point p_loc, 
			Integer p_radius) throws ArrayIndexOutOfBoundsException
	{
		//System.out.println("Black is: " + c_br + ", " + c_bg + ", " + c_bb);'
		
		Point l_cur = new Point(p_loc);
		Point l_res = new Point(l_cur);
		Point l_next;
		//Search space is twice the tolerance on the width over the radius 
		Integer l_search = (int)(c_tolerance*p_img.getWidth()*2/p_radius);
		//..or at least 4 spots on each side of the starting point
		l_search = Math.max(l_search, 8);
		
		try {
			l_res = detectCircle(p_img, l_cur, p_radius);
		} catch (ArrayIndexOutOfBoundsException l_e) {}
		SquareSpiralPattern l_spiral = new SquareSpiralPattern(l_search);
		while (l_res == null && !l_spiral.isEmpty())
		{
			//System.out.println("Did not find on first try, keep going...");
			l_next = l_spiral.next();
			
			//Clip anything we know is out of bounds
			if (l_next.x*p_radius+ l_cur.x > p_img.getWidth() 
					|| l_next.x*p_radius+ l_cur.x < 0 
					|| l_next.y*p_radius + l_cur.y > p_img.getHeight()
					|| l_next.y*p_radius + l_cur.y < 0)
			{
			//	continue;
			}

			//NOTE: We might be able to jump by Diameter, that would speed up
			//the process. A factor like 1.5 is more likely, because there is
			//whitespace on each end and you don't want to accidentally jump
			//a whole alignment mark! (consider a grid of circles, and you will
			//be jumping between them into the whitespace of each).
			//TODO: Needs grate math-fu behind it. 
			l_cur.setLocation(l_next.x*p_radius+ l_cur.x, 
								l_next.y*p_radius + l_cur.y);
			
			System.out.println("Trying: " + l_cur.x + ", " + l_cur.y);
			try {
				l_res = detectCircle(p_img, l_cur, p_radius);
			} catch (ArrayIndexOutOfBoundsException l_e) {}
		}
		
		return l_res;
	}
	
	/**
	 * Detects if the given point belongs to a circle.
	 * @param p_img The current image.
	 * @param p_point the location of the point.
	 * @return the center of the circle or null if the point was not in a circle
	 * @throws ArrayIndexOutOfBoundsException
	 */
	protected Point detectCircle(BufferedImage p_img, Point p_point, 
			Integer p_radius) throws ArrayIndexOutOfBoundsException
	{
		//Gratuitous Bounds Check
		if (p_point.x < 0 || p_point.x > p_img.getWidth() 
			|| p_point.y < 0 || p_point.y > p_img.getHeight())
		{
			//System.out.println("OOB!");
			return null;
		}
		
		Point l_c = null;		
		Integer l_color = p_img.getRGB(p_point.x, p_point.y);
		//If we hit, see where we are in relation to the center
		if (isBlack(l_color)) {
			Integer l_lx, l_rx, l_uy, l_dy;
			l_lx = l_rx = p_point.x;
			l_uy = l_dy = p_point.y;
			for (int l_i = 0; l_i < p_radius*2; l_i++) {
				//NOTE: These stop moving when they hit whitespace, if the 
				//Alignment mark is not contiguouse, this can cause a problem!
				//Left
				if (l_lx-1 > 0 
						&& isBlack(p_img.getRGB(l_lx-1, p_point.y))) {
					l_lx--;
				}
				//Right
				if (l_rx+1 < p_img.getWidth() 
						&&isBlack(p_img.getRGB(l_rx+1, p_point.y))) {
					l_rx++;
				}
				//Up
				if (l_uy-1 > 0 
						&& isBlack(p_img.getRGB(p_point.x, l_uy-1))) {
					l_uy--;
				}
				//Down
				if (l_dy+1 < p_img.getHeight()
						&& isBlack(p_img.getRGB(p_point.x, l_dy+1))) {
					l_dy++;
				}
			}
			//a possible center point
			Double l_cy = (l_dy - l_uy)/2.0 + l_uy;
			Double l_cx = (l_rx - l_lx)/2.0 + l_lx;
			l_c = new Point();
			l_c.setLocation(l_cx, l_cy);
			System.out.println("Center is :" + l_c.x +", " + l_c.y);
			
			//TODO: The following block might not really be necessary at all.
			//Is the radius we think we know consistant and close enough?
			Double l_d[] = new Double[4];
			l_d[0] = l_c.distance(l_lx, p_point.y);
			l_d[1] = l_c.distance(l_rx, p_point.y);
			l_d[2] = l_c.distance(p_point.x, l_uy);
			l_d[3] = l_c.distance(p_point.x, l_dy);
			//Detect if it is probably not a circle
			//double tolerance here, because the next routine will really
			//determine if it's a circle! Could be up to 5 pixels off anyway!
			for (int l_i = 0; l_i < 4; l_i++)
			{
				if (Math.abs(l_d[l_i] - p_radius) 
						> Math.max(p_radius*c_tolerance, 5)) {
					System.out.println("Probably not a circle");
					return null;
				}
			}
			
			//probably a circle, but might not be 
			int l_i;
			Point l_tmp = new Point();
			Double l_rad = 45.0; //Radians
			Double l_ed = c_tolerance*p_radius*2; //Tolerable edge distance
			Integer l_edx, l_edy;
			for (l_i = 0; l_i < 360/l_rad; l_i++)
			{			
				//Check for a circle, x = r*cos(t), y = r*sin(t)
				//45 is 360/8 Math.cos takes a radian, we do an 8 point check
				double l_x = p_radius*Math.cos(l_i*l_rad)+l_c.x;
				double l_y = p_radius*Math.sin(l_i*l_rad)+l_c.y;
				l_tmp.setLocation(l_x, l_y);
				
				//Go out or in depending if we hit again.
				l_edx = (int)Math.ceil(l_ed*Math.cos(l_i*l_rad));
				l_edy = (int)Math.ceil(l_ed*Math.sin(l_i*l_rad));
				if (!isBlack(p_img.getRGB(l_tmp.x, l_tmp.y))) {
					//We didn't hit, check inward.
					if (!isBlack(p_img.getRGB(l_tmp.x - l_edx, l_tmp.y - l_edy)))
					{
						return null;
					}
				} else {
					//We did hit, make sure it's not just a giant blob of ink!
					if (isBlack(p_img.getRGB(l_tmp.x + l_edx, l_tmp.y + l_edy)))
					{
						return null;
					}
				}
			}
		}
		
		return l_c;
	}
	
	/**
	 * Checks if a color is close enough to black to be black and not white.
	 * @param p_color the color to check.
	 * @return
	 */
	protected boolean isBlack(Color p_color) {
		int l_cr, l_cg, l_cb;
		l_cr = p_color.getRed();
		l_cg = p_color.getGreen();
		l_cb = p_color.getBlue();
		//System.out.println("Black is:" + c_br + ", " + c_bg + ", " + c_bb);
		//System.out.println("Color is:" + l_cr + ", " + l_cg + ", " + l_cb);
		//This tolerance might be too high! 
		//TODO: Might want to make it configurable.
		if (Math.abs(l_cr - c_br) <= 100 
				&& Math.abs(l_cg - c_bg) <= 100 
				&& Math.abs(l_cb - c_bb) <=100) 
		{
			return true;
		}
		return false;
	}

	/**
	 * Convenience function for lazy programmers.
	 * @see isBlack(Color p_color)
	 * @param p_color
	 * @return
	 */
	protected boolean isBlack(Integer p_color) {
		return isBlack(new Color(p_color));
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
	
	/**
	 * @return the current black color
	 */
	public Color getBlack() 
	{
		return new Color(c_br, c_bg, c_bb);
	}
	
	/**
	 * Set the black (alignment mark) color.
	 * @param p_black the alignment mark color.
	 */
	public void setBlack(Color p_black) 
	{
		c_br = p_black.getRed();
		c_bg = p_black.getGreen();
		c_bb = p_black.getBlue();
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
}	

