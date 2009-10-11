package temp;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;
import java.util.TreeMap;

import software.common.Util;

public class RTF_TO_CCPro {

	String electionTitle=null;
	int numberOfSeats = 0;
	String[] candidates=null;
	TreeMap<Integer, TreeMap<Integer, String>> ballots=null;
	int numberOfBallots=-1;
	int numberOfCandidates=-1;
	
	BufferedReader br=null;

	public RTF_TO_CCPro(File in) throws IOException {
		br=new BufferedReader(new FileReader(in));
		
		br.mark(1000);
		readElectionDetails(br);
		ballots=new TreeMap<Integer, TreeMap<Integer, String>>();
		br.reset();
	}
	
	
	
	public String getElectionTitle() {
		return electionTitle;
	}



	public void read() throws IOException {
		
		int ballotNumber=-1;
		
		String line="";
		int index=-1;
		
		//read in the ballots
		while ((line=br.readLine()) !=null) {
			if ((index=line.indexOf("#"))>=0) {
				ballotNumber=Integer.parseInt(line.substring(index+1, line.indexOf(" ", index+2)).trim());

				for (int i=0;i<8;i++) {
					br.readLine();
				}
				
				//read the rankings
				TreeMap<Integer, String> ballot=new TreeMap<Integer, String>();
				for (int i=0;i<numberOfCandidates;i++) {
					line=br.readLine();
					StringTokenizer st=new StringTokenizer(line);
					st.nextToken();
					
					String sRank=st.nextToken();
					
					int rank=sRank.indexOf("X")/3+1;
					if (sRank.indexOf("X")>=0) {
						if (ballot.get(rank)==null)
							ballot.put(rank, (i+1)+"");
						else {
							ballot.put(rank, ballot.get(rank)+"="+(i+1));
						}
					}
					br.readLine();
				}
				
				ballots.put(ballotNumber, ballot);
			}
		}
		br.close();

	}
	
	public void write(File out) throws IOException {
		BufferedOutputStream bos=new BufferedOutputStream(new FileOutputStream(out));
		
		bos.write((".TITLE "+electionTitle+"\n").getBytes());
		bos.write(("\n").getBytes());
		bos.write((".STATISTICS-ON\n").getBytes());
		bos.write((".ELECT "+numberOfSeats+"\n").getBytes());
		
		for (int i=0;i<numberOfCandidates;i++) {
			bos.write((".CANDIDATE "+(i+1)+", \""+candidates[i]+"\"\n").getBytes());
		}
		bos.write(("\n").getBytes());
		
		for (int ballotNumber:ballots.keySet()) {
			bos.write((Util.AddleadingZeros(ballotNumber+"", 7)+")").getBytes());
			TreeMap<Integer, String> ballot=ballots.get(ballotNumber);
			
			for (int rank:ballot.keySet()) {
				if (rank!=1) {
					bos.write((",").getBytes());				
				}
				bos.write((" "+ballot.get(rank)).getBytes());
			}
			bos.write(("\n").getBytes());
		}
		bos.close();
	}
	
	
	void readElectionDetails(BufferedReader br) throws NumberFormatException, IOException {
		
		String line="";
		while ((line=br.readLine()) !=null) {
			if ((line.indexOf("#"))>=0) {				
				//read the number of ballots
				numberOfBallots=Integer.parseInt(line.substring(line.lastIndexOf(" ")).trim());
				
				//go to the next line and read in the election title
				electionTitle=(line=br.readLine()).trim();
				
				//go to the next line and read in the number of candidates and the number of seats
				line=br.readLine();
				numberOfCandidates = Integer.parseInt(line.substring(0,line.indexOf("candidates")).trim());
				numberOfSeats = Integer.parseInt(line.substring(line.indexOf("~")+1,line.indexOf("seats")).trim());
				
				candidates=new String[numberOfCandidates];
				
				//read in the names of the candidates
				for (int i=0;i<6;i++) {
					line=br.readLine();
					//System.out.println(line);					
				}
				
				for (int i=0;i<numberOfCandidates;i++) {					
					line=br.readLine();					
					StringTokenizer st=new StringTokenizer(line," \t");
					candidates[i]=st.nextToken();
					br.readLine();
				}
				
				
				return;
			}
		}		
	}

	public static void main(String[] args) throws IOException {
		RTF_TO_CCPro rtf=new RTF_TO_CCPro(new File("C:\\Documents and Settings\\stefan\\Desktop\\temp\\CPREfrmX3.txt"));
		rtf.read();
		
		StringTokenizer st=new StringTokenizer(rtf.getElectionTitle()," \"");
		rtf.write(new File("C:\\Documents and Settings\\stefan\\Desktop\\temp\\"+st.nextToken()+".in"));
	}

}
