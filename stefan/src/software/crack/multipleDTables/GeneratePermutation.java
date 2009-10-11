package software.crack.multipleDTables;

import java.security.SecureRandom;
import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;

import software.common.Util;

public class GeneratePermutation {	
	
	private int[] masterPerm=null;
	private String[] votes=null;
	
	int noCandidates=-1;
	int noVotes=-1;
	
	private int[] perm=null;
	
	Hashtable<String, Double> idealPercentages=null;
	
	public String[] getVotes() {
		return votes;
	}
	
	public GeneratePermutation() throws Exception {
		masterPerm=new int[8];
		masterPerm[0]=2;
		masterPerm[1]=5;
		masterPerm[2]=0;
		masterPerm[3]=1;
		masterPerm[4]=3;
		masterPerm[5]=4;
		masterPerm[6]=6;
		masterPerm[7]=7;
		
		
		votes=new String[8];
		votes[0]="A";
		votes[1]="A";
		votes[2]="A";
		votes[3]="B";
		votes[4]="B";
		votes[5]="B";
		votes[6]="A";
		votes[7]="B";
		
		noVotes=masterPerm.length;
		
		noCandidates=2;

		perm=masterPerm;
		
		idealPercentages=new Hashtable<String, Double>();
		idealPercentages.put("A", 0.5);
		idealPercentages.put("B", 0.5);
		/*
		System.out.println("perm");
		Util.print(perm);
		System.out.println("votes");
		Util.print(votes);
		*/
	}

	
	public GeneratePermutation(int noBallots, double[] percentages, byte[] seed) throws Exception {
		masterPerm=new int[noBallots];		
		votes=new String[noBallots];
		
		noVotes=masterPerm.length;
		
		noCandidates=percentages.length+1;
	
		idealPercentages=new Hashtable<String, Double>();
		
		int votesPos=0;
		String vote=null;
		
		int noVotesSoFar=0;
		int noVotesForThisContest=0;
		for (int i=0;i<noCandidates-1;i++) {
			vote=Character.toString((char)('A'+(char)i));
			
			noVotesForThisContest=(int)(percentages[i]*noBallots);
//System.out.println(noVotesForThisContest);			
			noVotesSoFar+=noVotesForThisContest;
			for (int j=0;j<noVotesForThisContest;j++){
				votes[votesPos++]=vote;
			}
			
			idealPercentages.put(vote, (double)noVotesForThisContest/noBallots);
		}

		vote=Character.toString((char)('A'+(char)(noCandidates-1)));
		for (int j=0;j<noBallots-noVotesSoFar;j++){
			votes[votesPos++]=vote;
		}
		idealPercentages.put(vote, (double)(noBallots-noVotesSoFar)/noBallots);

//Util.print(votes);		
		
		//create the masterperm;
		for (int i=0;i<masterPerm.length;i++) {
			masterPerm[i]=i;
		}
		
		int randomPos=-1;
		int temp=0;
		String stemp;
		SecureRandom sr=null;
		if (seed!=null)
			sr=new SecureRandom(seed);
		else
			sr=new SecureRandom();
		for (int i=0;i<masterPerm.length;i++) {
			randomPos=Math.abs(sr.nextInt())%noVotes;			
			temp=masterPerm[i];
			masterPerm[i]=masterPerm[randomPos];
			masterPerm[randomPos]=temp;
			
			stemp=votes[i];
			votes[i]=votes[randomPos];
			votes[randomPos]=stemp;
		}
		
		perm=masterPerm;
		
		/*
		System.out.println("perm");
		Util.print(perm);
		System.out.println("votes");
		Util.print(votes);
		*/
	}

	public void newIndependentD(byte[] seed) {
		perm=new int[perm.length];
		for (int i=0;i<perm.length;i++) {
			perm[i]=i;
		}
		
		int randomPos=-1;
		int temp=0;
		//String stemp;
		SecureRandom sr=new SecureRandom(seed);
		
		String[] oldVotes=new String[votes.length];
		System.arraycopy(votes, 0, oldVotes, 0, votes.length);
		
		for (int i=0;i<perm.length;i++) {
			randomPos=Math.abs(sr.nextInt())%noVotes;			
			temp=perm[i];
			perm[i]=perm[randomPos];
			perm[randomPos]=temp;
			
			votes[i]=oldVotes[masterPerm[i]];
		}
		/*
		System.out.println("perm");
		Util.print(perm);
		System.out.println("votes");
		Util.print(votes);
		*/
	}
	
	public String[] getSet(Set<Integer> challenges) {
		String[] ret=new String[perm.length];
		int x=-1;
		for (Iterator<Integer> i=challenges.iterator();i.hasNext();){
			x=i.next();
			ret[perm[x]]=votes[perm[x]];
		}
		return ret;
	}

	public HashSet<Integer> getOutputIndexes(Set<Integer> challenges) {
		HashSet<Integer> ret=new HashSet<Integer>();
		for (Iterator<Integer> i=challenges.iterator();i.hasNext();){
			ret.add(perm[i.next()]);
		}
		return ret;
	}

	public Vector<String> getOutputVotes(Vector<Integer> challenges) {	
		Vector<String> ret=new Vector<String>();
		for (int input:challenges) 
			ret.add(votes[perm[input]]);
		Collections.sort(ret);
		return ret;
	}

	
	public Hashtable<String, Double> getIdealPercentages() {
		return idealPercentages;
	}

	public static void main(String[] args) throws Exception {
		double[] percentages={0.5,0.25,0.25};
		GeneratePermutation gp=new GeneratePermutation(8,percentages,"secureSeed".getBytes());
		HashSet<Integer> challenges=new HashSet<Integer>();
		challenges.add(0);
		challenges.add(2);
		challenges.add(3);
		challenges.add(4);
		Util.print(gp.getSet(challenges));
	}
}