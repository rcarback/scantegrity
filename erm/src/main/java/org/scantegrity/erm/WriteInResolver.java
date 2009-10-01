package org.scantegrity.erm;

import org.scantegrity.common.Ballot;
import org.scantegrity.common.BallotStyle;
import org.scantegrity.common.Contest;
import org.scantegrity.common.Contestant;
import org.scantegrity.common.FindFile;
import org.scantegrity.common.RandomBallotStore;
import org.scantegrity.common.methods.ContestChoice;
import org.scantegrity.scanner.ScannerConfig;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.TreeMap;
import java.util.Vector;
import java.util.regex.Pattern;

/* This class loads in ballots and uses a queue to pull up all the write-in positions that need to be
 * resolved.
 */

public class WriteInResolver {
	
	private Map<Integer, BallotStyle> c_ballotStyles = null;
	private Map<Integer, Contest> c_contests = null;
	private Queue<WriteInLocation> c_locations = null;
	private Contest c_currentContest = null;
	private ScannerConfig c_config = null;
	private WriteInLocation c_currentLocation = null;
	private Vector<ContestChoice> c_contestChoices = null;
	
	class WriteInLocation
	{
		public Ballot ballot;
		public ContestChoice choice;
		public int contestId;
		public int candidateId;
		public WriteInLocation(ContestChoice p_choice, Ballot p_ballot, int p_contestId, int p_candidateId)
		{
			ballot = p_ballot;
			contestId = p_contestId;
			candidateId = p_candidateId;
			choice = p_choice;
		}
	}
	
	public WriteInResolver(ScannerConfig p_config)
	{
		c_config = p_config;
		Vector<BallotStyle> l_styles = c_config.getStyles();
		c_contestChoices = new Vector<ContestChoice>();
		c_locations = new LinkedList<WriteInLocation>();
		
		c_ballotStyles = new HashMap<Integer, BallotStyle>();
		for( BallotStyle l_style : l_styles )
		{
			System.err.println(l_style.toString());
			c_ballotStyles.put(l_style.getId(), l_style);
		}
		
		Vector<Contest> l_contests = c_config.getContests();
		c_contests = new HashMap<Integer, Contest>();
		for( Contest l_contest : l_contests )
		{
			System.err.println(l_contest.toString());
			c_contests.put(l_contest.getId(), l_contest);
		}
		
		LoadBallots();
	}
	
	//1) Finds ballot stores on all removable disks
	//2) Reads ballots from each store and figures out which ballot positions need to be resolved
	//3) Adds each ballot position to a queue for the user to resolve
	private void LoadBallots()
	{
		FindFile l_finder = new FindFile(Pattern.compile(".*\\.sbr"));
		l_finder.c_recurseDepth = 2;
		Vector<File> l_files = l_finder.findMultiple();
		for( File l_file : l_files )
		{
			try {
				RandomBallotStore l_store = new RandomBallotStore(l_file.getPath(), null, null);
				l_store.open();
				Vector<Ballot> l_ballots = l_store.getBallots();
				l_store.close();
				ParseBallots(l_ballots);
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	//Search through each ballot to find positions where there are votes for a write-in candidate
	private void ParseBallots(Vector<Ballot> p_ballots)
	{
		for( Ballot l_ballot : p_ballots )
		{
			BallotStyle l_style = c_ballotStyles.get(l_ballot.getBallotStyleID());
			Vector<Integer> l_contestIds = l_style.getContests();
			//Look in each contest for write-ins
			for( Integer l_contestId : l_contestIds )
			{
				Integer[][] l_contestData = l_ballot.getContestData(l_contestId);
				Contest l_contest = c_contests.get(l_contestId);
				Vector<Contestant> l_contestants = l_contest.getContestants();
				
				//Create contestchoice and add to vector
				ContestChoice l_choice = new ContestChoice(0, l_ballot, l_style, l_contest);
				c_contestChoices.add(l_choice);
				
				if( !l_style.getWriteInRects().containsKey(l_contestId))
					continue;
				
				TreeMap<Integer, Rectangle> l_writeInMap = l_style.getWriteInRects().get(l_contestId);
				
				//If it is a write in contestant, search the row for a vote
				for(int x = 0; x < l_contestData.length; x++ )
				{
					if( l_writeInMap.containsKey(l_contestants.get(x).getId()) )
					{
						boolean l_voteFound = false;
						for(int y = 0; y < l_contestData[x].length; y++ )
						{
							if( l_contestData[x][y] != 0 )
								l_voteFound = true;
						}
						
						if( l_voteFound )
						{
							c_locations.add(new WriteInLocation(l_choice, l_ballot, l_contestId, l_contestants.get(x).getId()));
						}
					}
				}
			}
		}
	}

	public void AddCandidate(String p_name) {
		Contestant l_newContestant = new Contestant(c_currentContest.getNextId(), p_name);
		c_currentContest.addContestant(l_newContestant);
	}

	public boolean next() {
		c_currentLocation = c_locations.poll();
		if( c_currentLocation == null )
			return false;
		c_currentContest = c_contests.get(c_currentLocation.contestId);
		return true;
	}
		
	public Vector<String> getCandidates() {
		Vector<String> l_contestantList = new Vector<String>();
		for( Contestant l_contestant : c_currentContest.getContestants() )
		{
			l_contestantList.add(l_contestant.getName());
		}
		return l_contestantList;
	}

	public BufferedImage getImage() {
		Ballot l_curBallot = c_currentLocation.ballot;
		TreeMap<Integer, TreeMap<Integer, BufferedImage>> l_imageMap = l_curBallot.getWriteInImgs();
		return l_imageMap.get(c_currentLocation.contestId).get(c_currentLocation.candidateId);
	}
	
	public void Resolve(String p_name)
	{
		int l_candidateId = 0;
		for( Contestant l_contestant : c_currentContest.getContestants() )
		{
			if( l_contestant.getName() == p_name )
			{
				l_candidateId = l_contestant.getId();
			}
		}
		
		/*Map<Integer, Map<Integer, Integer>> l_map = c_currentLocation.ballot.getWriteInMap();
		if( l_map == null )
			l_map = new HashMap<Integer, Map<Integer, Integer>>();
		
		if( !l_map.containsKey(c_currentContest.getId()))
			l_map.put(c_currentContest.getId(), new HashMap<Integer, Integer>());
		
		Map<Integer, Integer> l_innerMap = l_map.get(c_currentContest.getId());
		l_innerMap.put(c_currentLocation.candidateId, l_candidateId);*/
		c_currentLocation.choice.normalizeChoiceWriteIn(c_currentLocation.candidateId, l_candidateId);
		
	}

}
