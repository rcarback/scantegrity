package software.crack.multipleDTables;

import java.security.SecureRandom;
import java.util.Collections;
import java.util.TreeSet;
import java.util.Vector;

import java.util.Hashtable;

public class CrackUnlinkedDs {

	Vector<Integer> inputRed=null;		
	Vector<Integer> inputBlue=null;
	
	
	private void generateRandomRedChallenge(int noBallots,double percentageLeft,byte[] seed) {
		int[] allIndexes=new int[noBallots];
		for (int i=0;i<noBallots;i++)
			allIndexes[i]=i;
		
		
		SecureRandom sr=null;
		if (seed==null)
			sr=new SecureRandom();
		else
			sr=new SecureRandom(seed);
		int randomPos=0;
		int temp;
		for (int i=0;i<allIndexes.length;i++) {
			randomPos=Math.abs(sr.nextInt())%noBallots;			
			temp=allIndexes[i];
			allIndexes[i]=allIndexes[randomPos];
			allIndexes[randomPos]=temp;
		}
		
		inputRed=new Vector<Integer>();
		int i=0;
		for (i=0;i<allIndexes.length*percentageLeft;i++) {
			inputRed.add(allIndexes[i]);
		}
		Collections.sort(inputRed);
		
		inputBlue=new Vector<Integer>();
		for (;i<allIndexes.length;i++) {
			inputBlue.add(allIndexes[i]);
		}
		Collections.sort(inputBlue);
	}

	private void generateRandomRedChallenge(int step) {
		
		if (step==0) {
			inputRed=new Vector<Integer>();
			inputRed.add(0);
			inputRed.add(1);
			inputRed.add(2);
			inputRed.add(3);

			inputBlue=new Vector<Integer>();
			inputBlue.add(4);
			inputBlue.add(5);
			inputBlue.add(6);
			inputBlue.add(7);
		} else {
			if (step==1) {
				inputRed=new Vector<Integer>();
				inputRed.add(0);
				inputRed.add(1);
				inputRed.add(4);
				inputRed.add(5);

				inputBlue=new Vector<Integer>();
				inputBlue.add(2);
				inputBlue.add(3);
				inputBlue.add(6);
				inputBlue.add(7);				
			}			
		}
	}
	
/*	
	public static HashSet<String> getDistinctVotesInSet(GeneratePermutation gp,HashSet<Integer> currentSet1) {
		HashSet<String> votes=new HashSet<String>();
		for (Iterator<Integer> j=currentSet1.iterator();j.hasNext();) {
			votes.add(gp.getVotes()[j.next()]);
		}
		return votes;
	}
*/

	
	/**
	 * @param args
	 * @throws Exception 
	 */
	public int generateRandomChallenges(int noBallots,int noChallenges,double percentageLeft,double[] percentages) throws Exception {
		//GeneratePermutation gp=new GeneratePermutation();//noBallots,percentages,"secureSeed".getBytes());
		GeneratePermutation gp=new GeneratePermutation(noBallots,percentages,"secureSeed".getBytes());
		
		Hashtable<Vector<Integer>, Vector<String>> allPartitions=new Hashtable<Vector<Integer>, Vector<String>>();
//		Hashtable<String, String> initialPartition=new Hashtable<String, String>();
		
		Vector<Integer> initialIndexes=new Vector<Integer>();
		Vector<String> initialVotes=new Vector<String>();
		String[] allVotes=gp.getVotes();
		for (int i=0;i<allVotes.length;i++) {
			initialIndexes.add(i);
			initialVotes.add(allVotes[i]);
		}
		allPartitions.put(initialIndexes, initialVotes);		
				
		Hashtable<String, Double> idealpercentages=gp.getIdealPercentages();
//System.out.println(idealpercentages);
		
		for (int challengeNumber=0;challengeNumber<noChallenges;challengeNumber++) {
//System.out.println(challengeNumber+" "+allPartitions);

			if (challengeNumber!=0)
				gp.newIndependentD(("masterSeed"+challengeNumber).getBytes());

			//generateRandomRedChallenge(challengeNumber);		
			generateRandomRedChallenge(noBallots, percentageLeft, null);
			
			processNewPartition(allPartitions, inputRed, gp.getOutputVotes(inputRed));
//System.out.println("all partitions after processing first input "+allPartitions);			
			
			
			processNewPartition(allPartitions, inputBlue, gp.getOutputVotes(inputBlue));
//System.out.println("all partitions after processing second input "+allPartitions);			
			
			//get rid of eventual duplicates 
			eliminateDuplicates(allPartitions);
//System.out.println("all partitions after eliminating duplicates "+allPartitions);
			
			//make all the possible interferances with the current partitions
			makeInterfearences(allPartitions);
//System.out.println("all partitions after making intearences "+allPartitions);
			
			//get rid of eventual duplicates 
			eliminateDuplicates(allPartitions);
//System.out.println("all partitions after eliminating duplicates "+allPartitions);

			//check the distribution of votes in the modified parittions			
			for (Vector<Integer> input:allPartitions.keySet()) {
				Vector<String> output=allPartitions.get(input);
				
				Hashtable<String, Double> currentPercentages=getPercentagesVotesInSet(output);
				
				
				for (String vote:idealpercentages.keySet()) {
					if (currentPercentages.get(vote)!=null && currentPercentages.get(vote)==1) {
						//System.out.println("!! In step number "+(challengeNumber+1)+" all the encrypted votes "+input +" corresponds to votes for "+ vote);
						return challengeNumber;
					} 
					if (currentPercentages.get(vote)==null) {
						//System.out.println("In step number "+(challengeNumber+1)+" the set "+input +" does not have any votes for "+ vote+". In the ideal case it should have"+idealpercentages.get(vote));
						//return challengeNumber;
					}															
					if (currentPercentages.get(vote)!=null && Math.abs(currentPercentages.get(vote)/idealpercentages.get(vote)-1)>0.1) {
						//System.out.println("In step number "+(challengeNumber+1)+" the set "+input+"-->"+output+" contains "+currentPercentages.get(vote)*100+ " % votes for "+vote+" whereas in the reported tally candidate "+vote+" has "+idealpercentages.get(vote)*100+"%");
						//return challengeNumber;
					}
				}
			}
		}
		
		return -1;
	}

	public static Hashtable<String, Double> getPercentagesVotesInSet(Vector<String> votes) {
		
		Hashtable<String, Integer> numberOfVotesForEachCandidate=new Hashtable<String, Integer>();
		for (String vote:votes) {
			if (numberOfVotesForEachCandidate.get(vote)!=null) {
				numberOfVotesForEachCandidate.put(vote, numberOfVotesForEachCandidate.get(vote)+1);
			} else {
				numberOfVotesForEachCandidate.put(vote, 1);
			}
		}
		Hashtable<String, Double> percentages=new Hashtable<String, Double>();
		for (String candidate:numberOfVotesForEachCandidate.keySet()) {
			percentages.put(candidate, (double)numberOfVotesForEachCandidate.get(candidate)/votes.size());
		}		
		return percentages;
	}

	
	/**
	 * Eliminates all the duplicates
	 * @param allPartitions
	 */
	private <I extends Comparable,S> void eliminateDuplicates(Hashtable<Vector<I>, Vector<S>>  allPartitions) {
		TreeSet<String> uniques=new TreeSet<String>();
		
		Vector<Vector<I>> duplicates=new Vector<Vector<I>>();
		
		for (Vector<I> input:allPartitions.keySet()) {
			Collections.sort(input);
			if (uniques.contains(input.toString())) {
				//this is a duplicate and it should be deleted
				duplicates.add(input);
			} else {
				uniques.add(input.toString());
			}
		}
		
		for (Vector<I> input:duplicates) {
			allPartitions.remove(input);
		}
	}

	private <I extends Comparable,S> void makeInterfearences(Hashtable<Vector<I>, Vector<S>>  allPartitions) {
		boolean foundNewPartitions=true;
		
		//this is not exactly efficient, but it is elegant
		while (foundNewPartitions) {
			foundNewPartitions=false;			
			for (Vector<I> input:allPartitions.keySet()) {
				if (processNewPartition(allPartitions, input, allPartitions.get(input))) {
					//we have found new partitions
					foundNewPartitions=true;
					break;
				}
			}
		}
	}
	
	//private <I extends Comparable,S> Hashtable<Vector<I>, Vector<S>> processNewPartition(Hashtable<Vector<I>, Vector<S>> allPartitions,	Vector<I> inputRed, Vector<S> outputRed) {
	private <I extends Comparable,S> boolean processNewPartition(Hashtable<Vector<I>, Vector<S>> allPartitions,	Vector<I> inputRed, Vector<S> outputRed) {
		Hashtable<Vector<I>, Vector<S>> toAdd=new Hashtable<Vector<I>, Vector<S>>();
		Hashtable<Vector<I>, Vector<S>> toRemove=new Hashtable<Vector<I>, Vector<S>>();
		
		boolean foundIntersection=false;
		
		//System.out.println(inputRed+"-->"+outputRed);
		
		for (Vector<I> input:allPartitions.keySet()) {
			//intersect the input with inputRed
			Vector<I> inputIntersection=myIntersectAll(input, inputRed);
			
			//if the intersection is not null
			if (inputIntersection.size()>0 && inputIntersection.size() < input.size()) {
				//compute the intersection of the outputs
				Vector<S> output=allPartitions.get(input);
				
				Vector<S> outputIntersection=myIntersectAll(output, outputRed);

			//if the intersection of the inputs is the same size 
			//as the intersection of the outputs

				if (inputIntersection.size() == outputIntersection.size()) {					
					foundIntersection=true;

					//add the following sets to all the partitions
					//the intersection
					toAdd.put(inputIntersection, outputIntersection);
					
					//partition-inputRed
					Vector<I> partitionMinusInputRed=myMinusAll(input, inputRed);
					Vector<S>	partitionMinusOutputRed=myMinusAll(output, outputRed);
					if (partitionMinusInputRed.size()>0)
						toAdd.put(partitionMinusInputRed, partitionMinusOutputRed);
					
					//inputRed-partition
					Vector<I> inputRedMinusInput=myMinusAll(inputRed,input);
					Vector<S>	outputRedMinusOutput=myMinusAll(outputRed,output);
					if (inputRedMinusInput.size()>0)
						toAdd.put(inputRedMinusInput, outputRedMinusOutput);
					else {
						//eliminate input
						toRemove.put(input, output);
					}
				}					
			}
		}
		
		if (! foundIntersection) {
			toAdd.put(inputRed, outputRed);
		} else {
			for (Vector<I> i:toRemove.keySet()) {
				allPartitions.remove(i);
			}
		}
		
		allPartitions.putAll(toAdd);
		
		return foundIntersection;
	}		


	public static <T> Vector<T> myMinusAll(Vector<T> a, Vector<T> b) {
		Vector<T> aa=new Vector<T>(a);
		Vector<T> bb=new Vector<T>(b);
		
		for (T k:a) { 
			if (bb.contains(k)) {
				aa.remove(k);
				bb.remove(k);
			}
		}
		return aa;
	}

	public static <T> Vector<T> myIntersectAll(Vector<T> a, Vector<T> b) {
		Vector<T> aa=new Vector<T>(a);
		Vector<T> bb=new Vector<T>(b);
		Vector<T> ret=new Vector<T>();
		
		for (T k:a) { 
			if (bb.contains(k)) {
				aa.remove(k);
				bb.remove(k);
				ret.add(k);
			}
		}
		return ret;
	}

	
	public static void main(String[] args) throws Exception {
		int noBallots=8;//1<<0;//System.out.println(noBallots);
		int noChallenges=2;
		double percentageLeft=0.5;
		
		double[] percentages={0.5};//{0.48,0.47,0.02,0.015};

		CrackUnlinkedDs cl=new CrackUnlinkedDs();
		int numberOfTries=1000;
		int numberOfSuccesses=0;
		for (int i=1;i<=numberOfTries;i++) {
			if (cl.generateRandomChallenges(noBallots, noChallenges, percentageLeft, percentages)!=-1)
			{
				numberOfSuccesses++;
			}
			if (i%100==0) {
				System.out.println("Finished running "+i+" elections. NumberOfSuccesses="+numberOfSuccesses);
			}
		}
		System.out.println(numberOfSuccesses+" out of "+numberOfTries);		
	}
}
