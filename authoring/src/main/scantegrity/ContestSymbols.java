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

package org.scantegrity.authoring.scantegrity;

import org.gwu.voting.standardFormat.electionSpecification.ElectionSpecification;
import org.scantegrity.common.Prow;
import org.scantegrity.common.Util;

public class ContestSymbols extends org.scantegrity.authoring.ContestSymbols {

	int maxNoA=-1;
	
	public ContestSymbols(String pathToPrintsFile, String pathToElectionSpec, String[] chars, boolean charReset) throws Exception {
		super(pathToPrintsFile, pathToElectionSpec, chars, charReset);
		setMaxNoA();
	}

	public ContestSymbols(String pathToPrintsFile, ElectionSpecification es, String[] chars, boolean charReset) throws Exception {
		super(pathToPrintsFile, es, chars, charReset);
		setMaxNoA();
	}
	
	protected void setMaxNoA() {
		maxNoA=0;
		for (int i=0;i<qs.length;i++) {
        	maxNoA+=qs[i].getMax();
		}
	}
	
	public String[] getSelectedSymbols(String pid) throws Exception {
		Prow prow=prows.get(Integer.parseInt(pid));
		if (prow==null)
			throw new Exception("pid="+pid+" not found in m3in.xml");
		
		byte[] vote=prow.getVote();
		
		String[] ret=new String[maxNoA];
		int retPos=0;
		int allSymbolsPos=0;
		for (int i=0;i<qs.length;i++) {
        	for (int j=0;j<qs[i].getMax();j++) {
        		if (vote[retPos]==-1) {
        			ret[retPos]=" ";
        		} else {
        			ret[retPos]=allSymbols[allSymbolsPos+vote[retPos]];
        		}
        		retPos++;
        	}
        	allSymbolsPos+=qs[i].getAnswers().size();
		}
		return ret;
	}
	
	protected byte[] getPermutationForCurrentPage(byte[] p1, byte[] p2,int numberOfAnswers,int numberOfAnswersUntillCurrentQuestion, int page ) {
		byte[] ret = new byte[numberOfAnswers];
		
		byte[] permTop = new byte[numberOfAnswers];
		System.arraycopy(p1,numberOfAnswersUntillCurrentQuestion,permTop,0,numberOfAnswers);
		byte[] permBottom = new byte[numberOfAnswers];
		System.arraycopy(p2,numberOfAnswersUntillCurrentQuestion,permBottom,0,numberOfAnswers);
		byte[] permBottomInverse=Util.getInverse(permBottom);
		for (int j=0;j<permTop.length;j++) {
			ret[j]=permBottomInverse[permTop[j]];				
		}
		return ret;
	}

}
