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

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.TreeMap;

import org.gwu.voting.standardFormat.Constants;
import org.gwu.voting.standardFormat.basic.Question;
import org.gwu.voting.standardFormat.electionSpecification.ElectionSpecification;

/**
 * A wrapper for the Ballot Geometry. Does not handle xml.
 * @author stefan
 *
 */
public class BallotGeometryMap {

	BallotGeometry geom=null;
	double width=-1;
	double height=-1;
	
	int noDigitsInserial;
	Point2D.Double upperAlignment=null;
	Point2D.Double lowerAlignment=null;
	
	Rectangle2D.Double serialTopRectangle=null;
	Rectangle2D.Double serialBottomRectangle=null;
	
	//contest, rank, candidate
	TreeMap<Integer, TreeMap<Integer,TreeMap<Integer,Cluster>>> markedContests;
	
	Cluster serialBulleted=null;
	
	public static Util.BallotType ballotType=Util.BallotType.PUNCHSCAN;
	
	public BallotGeometryMap(BallotGeometry geom, ElectionSpecification es) {
		this.geom=geom;
		this.width=geom.getWidth();
		this.height=geom.getHeight();
		
		noDigitsInserial=geom.getNoDigitsInSerial();
		upperAlignment=geom.getUpperAlignment();
		lowerAlignment=geom.getLowerAlignment();
		
		double xcorrection=0.1;
		double ycorrection=0.1;
		
		Cluster rect=new Cluster(geom.getSerialTopNode("0"));
		double ulx=rect.getXmin()-xcorrection;
		double uly=rect.getYmin()-ycorrection;
		rect=new Cluster(geom.getSerialTopNode((noDigitsInserial-1)+""));
		double lrx=rect.getXmax()+xcorrection;
		double lry=rect.getYmax()+ycorrection;
		serialTopRectangle=new Rectangle2D.Double();
		serialTopRectangle.setRect(ulx,uly,lrx-ulx,lry-uly);
		
		if (ballotType.equals(Util.BallotType.PUNCHSCAN)) {
			rect=new Cluster(geom.getSerialBottomNode("0"));
			ulx=rect.getXmin()-xcorrection;
			uly=rect.getYmin()-ycorrection;
			rect=new Cluster(geom.getSerialBottomNode((noDigitsInserial-1)+""));
			lrx=rect.getXmax()+xcorrection;
			lry=rect.getYmax()+ycorrection;
			serialBottomRectangle=new Rectangle2D.Double();
			serialBottomRectangle.setRect(ulx,uly,lrx-ulx,lry-uly);
		}
		
		markedContests=new TreeMap<Integer, TreeMap<Integer,TreeMap<Integer,Cluster>>>();
		Question[] qs = es.getOrderedQuestions();
		int noRanks=1;
        for (int qno=0;qno<qs.length;qno++) {
        	TreeMap<Integer,TreeMap<Integer,Cluster>> race= new TreeMap<Integer, TreeMap<Integer,Cluster>>();
        	noRanks=1;
            if (qs[qno].getTypeOfAnswer().compareTo(Constants.RANK)==0)
            	noRanks=qs[qno].getMax(); 
        	for (int rank=0;rank<noRanks;rank++) {
        		TreeMap<Integer,Cluster> contest= new TreeMap<Integer,Cluster>();
        		for (int c=0;c<qs[qno].getAnswers().size();c++) {
        			if (ballotType.equals(Util.BallotType.PUNCHSCAN)) {
        				rect = new Cluster(geom.getBottomNode(qno+"",rank+"",c+""));
        			}
        			else
            			if (ballotType.equals(Util.BallotType.SCANTEGRITY)) {            				
            				rect = new Cluster(geom.getTopNode(qno+"",rank+"",c+""));     
            			}
        			contest.put(c, rect);
        		}
        		race.put(rank, contest);
        	}
        	markedContests.put(qno,race);
        }
        
        if (ballotType.equals(Util.BallotType.SCANTEGRITY)) {
        	serialBulleted=new Cluster(geom.getSerialBulletedNode());
        }
	}

	public Point2D.Double getLowerAlignment() {
		return lowerAlignment;
	}

	public int getNoDigitsInserial() {
		return noDigitsInserial;
	}

	public Rectangle2D.Double getSerialBottomRectangle() {
		return serialBottomRectangle;
	}

	public Rectangle2D.Double getSerialTopRectangle() {
		return serialTopRectangle;
	}

	public Point2D.Double getUpperAlignment() {
		return upperAlignment;
	}

	public TreeMap<Integer, TreeMap<Integer, TreeMap<Integer, Cluster>>> getMarkedContests() {
		return markedContests;
	}

	public double getWidth() {
		return width;
	}

	public double getHeight() {
		return height;
	}	
	
	
	public Cluster getSerialBulleted() {
		return serialBulleted;
	}	
}
