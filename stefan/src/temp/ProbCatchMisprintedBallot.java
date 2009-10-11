package temp;

public class ProbCatchMisprintedBallot {

	public static void main(String[] args) {
		double noVotedBallots=3000f;
		double percentVerified=1f/10;
		double percentMisprinted=1f/10;
		double noVerifiedBallots=150f;//noVotedBallots*percentVerified;
		double noMissprintedballots=150f;//noVotedBallots*percentMisprinted;
		
		double prob=0;
		double comb;
		double prod1;
		double prod2;
		for (int x=0;x<=noMissprintedballots;x++) {
			comb=1;
			for (int i=1;i<=x;i++)
				comb*=(noVerifiedBallots-x+i)/i;
			prod1=1;
			for (int i=0;i<=x-1;i++)
				prod1*=(noMissprintedballots-i)/(noVotedBallots-i);
			prod2=1;
			for (int i=0;i<=noVerifiedBallots-x-1;i++)
				prod2*=(noVotedBallots-noMissprintedballots-i)/(noVotedBallots-x-i);
			
			//System.out.println("x="+x+" comb="+comb+" prod1="+prod1+" prod2="+prod2);
			
			prob+=(1/Math.pow(2, x))*comb*prod1*prod2;
		}
		
		System.out.println("When "+noVotedBallots+" ballots are cast, "+noVerifiedBallots+" " +
				"voters check them and do not find any inconsistencies, " +
				"\nthe probability that the Election Authority misprinted "+
				noMissprintedballots+" ballots is ");
		System.out.format("%f",prob*100);
		System.out.println("%");
	}
}
