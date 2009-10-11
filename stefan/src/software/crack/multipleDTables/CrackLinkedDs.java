package software.crack.multipleDTables;

import java.security.SecureRandom;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;

import java.util.Hashtable;

public class CrackLinkedDs {

	public static HashSet<Integer> generateRandomRedChallenge(int noBallots,double percentageLeft,byte[] seed) {
		int[] ret=new int[noBallots];
		for (int i=0;i<noBallots;i++)
			ret[i]=i;
		
		
		SecureRandom sr=null;
		if (seed!=null)
			sr=new SecureRandom(seed);
		else
			sr=new SecureRandom();
		int randomPos=0;
		int temp;
		for (int i=0;i<ret.length;i++) {
			randomPos=Math.abs(sr.nextInt())%noBallots;			
			temp=ret[i];
			ret[i]=ret[randomPos];
			ret[randomPos]=temp;
		}
		
		HashSet<Integer> retSet=new HashSet<Integer>();
		for (int i=0;i<ret.length*percentageLeft;i++) {
			retSet.add(ret[i]);
		}
		
		return retSet;
	}

	public static HashSet<Integer> generateRandomRedChallenge(int step) {
		HashSet<Integer> retSet=new HashSet<Integer>();
		if (step==0) {
			retSet.add(0);
			retSet.add(1);
			retSet.add(2);
			retSet.add(3);
		} else {
			if (step==1) {
				retSet.add(0);
				retSet.add(1);
				retSet.add(4);
				retSet.add(5);
			}			
		}
		return retSet;
	}
	
	
	public static HashSet<String> getDistinctVotesInSet(GeneratePermutation gp,HashSet<Integer> currentSet1) {
		HashSet<String> votes=new HashSet<String>();
		for (Iterator<Integer> j=currentSet1.iterator();j.hasNext();) {
			votes.add(gp.getVotes()[j.next()]);
		}
		return votes;
	}

	public static Hashtable<String, Double> getPercentagesVotesInSet(GeneratePermutation gp,HashSet<Integer> currentSet1) {
		HashSet<String> votes=new HashSet<String>();
		Hashtable<String, Integer> numberOfVotesForEachCandidate=new Hashtable<String, Integer>();
		for (Iterator<Integer> j=currentSet1.iterator();j.hasNext();) {
			String vote=gp.getVotes()[j.next()];
			if (votes.contains(vote)) {
				numberOfVotesForEachCandidate.put(vote, numberOfVotesForEachCandidate.get(vote)+1);
			} else {
				votes.add(vote);
				numberOfVotesForEachCandidate.put(vote, 1);
			}
		}
		Hashtable<String, Double> percentages=new Hashtable<String, Double>();
		for (String candidate:numberOfVotesForEachCandidate.keySet()) {
			percentages.put(candidate, (double)numberOfVotesForEachCandidate.get(candidate)/currentSet1.size());
		}
		
		return percentages;
	}

	
	public static Vector<String> getAllVotesInSet(GeneratePermutation gp,HashSet<Integer> currentSet1) {
		Vector<String> votes=new Vector<String>();
		for (Iterator<Integer> j=currentSet1.iterator();j.hasNext();) {
			votes.add(gp.getVotes()[j.next()]);
		}
		return votes;
	}
	
	/**
	 * @param args
	 * @throws Exception 
	 */
	public int generateRandomChallenges(int noBallots,int noChallenges,double percentageLeft,double[] percentages) throws Exception {
		GeneratePermutation gp=new GeneratePermutation(noBallots,percentages,null);//"secureSeed".getBytes());

		HashSet<Integer> allIndexes=new HashSet<Integer>(noBallots);
		for (int i=0;i<noBallots;i++) {
			allIndexes.add(i);
		}

		int totalBroken=0;
		
		Hashtable<HashSet<Integer>, HashSet<Integer>> inputPartitionsToOutputPartitions=new Hashtable<HashSet<Integer>, HashSet<Integer>>();
		inputPartitionsToOutputPartitions.put(new HashSet<Integer>(allIndexes), new HashSet<Integer>(allIndexes));
		
		Hashtable<String, Double> idealpercentages=gp.getIdealPercentages();
		
		for (int challengeNumber=0;challengeNumber<noChallenges;challengeNumber++) {
//System.out.println("Table number "+challengeNumber);			
			HashSet<Integer> inputRed=generateRandomRedChallenge(noBallots, percentageLeft, null);//("challenge"+challengeNumber).getBytes());
			
			//HashSet<Integer> inputRed=generateRandomRedChallenge(challengeNumber);		

			HashSet<Integer> inputBlue=new HashSet<Integer>(allIndexes);
			inputBlue.removeAll(inputRed);
			
			HashSet<Integer> outputRed=gp.getOutputIndexes(inputRed);
			HashSet<Integer> outputBlue=gp.getOutputIndexes(inputBlue);
			
			//System.out.println(inputRed+"-->"+outputRed+"="+getAllVotesInSet(gp, outputRed));			
			//System.out.println(inputBlue+"-->"+outputBlue+"="+getAllVotesInSet(gp, outputBlue));
			
			Hashtable<HashSet<Integer>, HashSet<Integer>> TEMPinputPartitionsToOutputPartitions=new Hashtable<HashSet<Integer>, HashSet<Integer>>();
			for (Iterator<HashSet<Integer>> i=inputPartitionsToOutputPartitions.keySet().iterator();i.hasNext();) {
				HashSet<Integer> currentInputIndexes1=i.next();
				HashSet<Integer> currentInputIndexes2=new HashSet<Integer>(currentInputIndexes1);

				HashSet<Integer> currentOutputIndexes1=inputPartitionsToOutputPartitions.get(currentInputIndexes1);				
				HashSet<Integer> currentOutputIndexes2=new HashSet<Integer>(currentOutputIndexes1);
				
				currentInputIndexes1.retainAll(inputRed);
				currentInputIndexes2.retainAll(inputBlue);
				
//				System.out.println("currentInputIndex1 "+currentInputIndexes1);				
//				System.out.println("currentInputIndex2 "+currentInputIndexes2);				
				
				currentOutputIndexes1.retainAll(outputRed);
				currentOutputIndexes2.retainAll(outputBlue);

//				System.out.println("currentOutputIndex1 "+currentOutputIndexes1);				
//				System.out.println("currentOututIndex2 "+currentOutputIndexes2);				
								
				TEMPinputPartitionsToOutputPartitions.put(currentInputIndexes1, currentOutputIndexes1);
				TEMPinputPartitionsToOutputPartitions.put(currentInputIndexes2, currentOutputIndexes2);				
			}

			//inspect the newly created partitions
			totalBroken=0;
			for (Iterator<HashSet<Integer>> i=TEMPinputPartitionsToOutputPartitions.keySet().iterator();i.hasNext();) {
				HashSet<Integer> currentInputIndexes1=i.next();				
				
				HashSet<String> votes=getDistinctVotesInSet(gp, TEMPinputPartitionsToOutputPartitions.get(currentInputIndexes1));
/*				
				if (votes.size()!=1 && votes.size()<percentages.length+1) {
					System.out.println("NOT ALL VOTES step number "+(challengeNumber+1)+" inputs "+currentInputIndexes1 +" "+ votes.size()+" "+votes);
					return challengeNumber;
				} else

				if (votes.size()==1) {
					System.out.println("BROKEN step number "+(challengeNumber+1)+currentInputIndexes1+ "-->"+TEMPinputPartitionsToOutputPartitions.get(currentInputIndexes1) +"="+ votes);					
					totalBroken+=currentInputIndexes1.size();
				}
*/				
				Hashtable<String, Double> currentPercentages=getPercentagesVotesInSet(gp, TEMPinputPartitionsToOutputPartitions.get(currentInputIndexes1));
				
				if (currentInputIndexes1.size()>0)
					for (String vote:idealpercentages.keySet()) {
						if (currentPercentages.get(vote)==null) {
							//System.out.println("In step number "+(challengeNumber+1)+" the set "+currentInputIndexes1 +" does not have any votes for "+ vote+". In the ideal case it should have"+idealpercentages.get(vote));
							return challengeNumber;
						}
						if (currentPercentages.get(vote)!=null && currentPercentages.get(vote)==1) {
							//System.out.println("!! In step number "+(challengeNumber+1)+" all the encrypted votes "+currentInputIndexes1 +" corresponds to votes for "+ vote);
							return challengeNumber;
						}															
						if (currentPercentages.get(vote)!=null && Math.abs(currentPercentages.get(vote)/idealpercentages.get(vote)-1)>0.1) {
							//System.out.println("In step number "+(challengeNumber+1)+" the set contains "+currentPercentages.get(vote)*100+ " % votes for "+vote+" whereas in the reported tally candidate "+vote+" has "+idealpercentages.get(vote)*100+"%");
							//return challengeNumber;
						}
					}
				
			}
			if (totalBroken!=0) {
				System.out.println("totalBroken: "+totalBroken);
				return challengeNumber;
			}
			
			
			
			inputPartitionsToOutputPartitions=null;
			inputPartitionsToOutputPartitions=TEMPinputPartitionsToOutputPartitions;
//System.out.println(inputPartitionsToOutputPartitions);			
		}
		return -1;
	}
	
	public static void main(String[] args) throws Exception {
		int noBallots=1000;//1<<0;//System.out.println(noBallots);
		int noChallenges=2;
		double percentageLeft=0.5;
		
		double[] percentages={0.48,0.47,0.02,0.015};//,0.47,0.01,0.01};

		CrackLinkedDs cl=new CrackLinkedDs();
		int numberOfTries=10000;
		int numberOfSuccesses=0;
		for (int i=0;i<numberOfTries;i++) {
			if (cl.generateRandomChallenges(noBallots, noChallenges, percentageLeft, percentages)!=-1)
			{
				numberOfSuccesses++;
			}
		}
		System.out.println(numberOfSuccesses+" out of "+numberOfTries);		
	}
}
	