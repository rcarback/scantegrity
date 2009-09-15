package org.scantegrity.erm;

import org.scantegrity.common.Ballot;
import org.scantegrity.common.BallotStyle;
import org.scantegrity.common.Contest;
import org.scantegrity.common.Contestant;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class WriteInResolver {
	
	private Map<Integer, BallotStyle> c_ballotStyles = null;
	private int c_nextCandidateId = 0;
	private Vector<Ballot> c_ballots = null;
	
	public WriteInResolver()
	{
		
	}

	public void AddCandidate(String text) {
		// TODO Auto-generated method stub
		return;
	}

	public boolean next() {
		// TODO Auto-generated method stub
		return false;
	}

	public Vector<String> getCandidates() {
		// TODO Auto-generated method stub
		return null;
	}

	public BufferedImage getImage() {
		// TODO Auto-generated method stub
		return null;
	}

}
