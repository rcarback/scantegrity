package org.scantegrity.erm;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.TreeMap;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.io.FileUtils;
import org.scantegrity.common.Ballot;
import org.scantegrity.common.BallotStyle;
import org.scantegrity.common.Contest;
import org.scantegrity.common.Contestant;
import org.scantegrity.common.RandomBallotStore;
import org.scantegrity.common.methods.ContestChoice;
import org.scantegrity.scanner.ScannerConfig;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.lowagie.text.BadElementException;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

/* This class loads in ballots and uses a queue to pull up all the write-in positions that need to be
 * resolved.
 */

public class WriteInResolver {
	
	private Map<Integer, BallotStyle> c_ballotStyles = null;
	private Map<Integer, Contest> c_contests = null;
	private Map<Integer, ContestQueue> c_locations = null;
	private Contest c_currentContest = null;
	private ScannerConfig c_config = null;
	private WriteInLocation c_currentLocation = null;
	private TreeMap<Integer, Vector<ContestChoice>> c_contestChoices = null;
	private TreeMap<Integer, Vector<ContestChoice>> c_unalteredChoices = null;
	private Vector<WriteInResolution> c_resolutions = null;
	private HashSet<Integer> c_usedIds = null;
	private SecureRandom c_random = null;
	
	
	//Represents a resolution that was done by the user.  Used for the resolution pdf.
	class WriteInResolution
	{
		public BufferedImage image;
		public String name;
		public String id;
		public Contest contest;
		public ContestChoice choice;
		
		public WriteInResolution(BufferedImage p_image, String p_name, String p_id)
		{
			image = p_image;
			name = p_name;
			id = p_id;
		}
	}
	
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
	
	class ContestQueue
	{
		public int contestId;
		public Queue<WriteInLocation> queue;
		public String contestName;
		public ContestQueue(int p_id, String p_name)
		{
			queue = new LinkedList<WriteInLocation>();
			contestName = p_name;
		}
	}
	
	public WriteInResolver(ScannerConfig p_config)
	{
		c_config = p_config;
		Vector<BallotStyle> l_styles = c_config.getStyles();
		c_contestChoices = new TreeMap<Integer, Vector<ContestChoice>>();
		c_unalteredChoices = new TreeMap<Integer, Vector<ContestChoice>>();
		c_locations = new TreeMap<Integer, ContestQueue>();
		c_resolutions = new Vector<WriteInResolution>();
		
		c_usedIds = new HashSet<Integer>();
		c_random = new SecureRandom();
		
		c_ballotStyles = new HashMap<Integer, BallotStyle>();
		for( BallotStyle l_style : l_styles )
		{
			c_ballotStyles.put(l_style.getId(), l_style);
		}
		
		Vector<Contest> l_contests = c_config.getContests();
		c_contests = new HashMap<Integer, Contest>();
		for( Contest l_contest : l_contests )
		{
			c_contests.put(l_contest.getId(), l_contest);
		}
		
	}
	
	//1) Finds ballot stores on all removable disks
	//2) Reads ballots from each store and figures out which ballot positions need to be resolved
	//3) Adds each ballot position to a queue for the user to resolve
	public void LoadBallots(RandomBallotStore p_store) throws IOException
	{
			Vector<Ballot> l_ballots;
			l_ballots = p_store.getBallots();
			ParseBallots(l_ballots);
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
				
				int l_newId = Math.abs(c_random.nextInt());
				while( c_usedIds.contains(l_newId) )
				{
					l_newId = Math.abs(c_random.nextInt());
				}
				
				c_usedIds.add(l_newId);
				
				//Create contestchoice and add to vector
				ContestChoice l_choice = new ContestChoice(l_newId, l_ballot, l_style, l_contest);
				
				if( !c_contestChoices.containsKey(l_contestId) )
				{
					c_contestChoices.put(l_contestId, new Vector<ContestChoice>());
					c_unalteredChoices.put(l_contestId, new Vector<ContestChoice>());
				}
				
				c_contestChoices.get(l_contestId).add(l_choice);
				c_unalteredChoices.get(l_contestId).add(new ContestChoice(l_choice));
				
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
							if( c_locations.containsKey(l_contestId) )
								c_locations.get(l_contestId).queue.add(new WriteInLocation(l_choice, l_ballot, l_contestId, l_contestants.get(x).getId()));
							else
							{
								ContestQueue l_newQueue = new ContestQueue(l_contestId, l_contest.getContestName());
								l_newQueue.queue.add(new WriteInLocation(l_choice, l_ballot, l_contestId, l_contestants.get(x).getId()));
								c_locations.put(l_contestId, l_newQueue);
							}
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
		c_currentLocation = null;
		for( int x = 0; x < c_contests.size() && c_currentLocation == null; x++ )
		{
			int l_id = c_contests.get(x).getId();
			if( c_locations.containsKey(l_id) && c_locations.get(l_id).queue.peek() != null )
			{
				c_currentLocation = c_locations.get(l_id).queue.poll();
			}
		}
		if( c_currentLocation == null )
			return false;
		c_currentContest = c_contests.get(c_currentLocation.contestId);
		return true;
	}
		
	public Vector<String> getCandidates() {
		Vector<String> l_contestantList = new Vector<String>();
		
		BallotStyle l_style = c_ballotStyles.get(c_currentLocation.ballot.getBallotStyleID());
		
		TreeMap<Integer, Rectangle> l_writeInMap = null;
		
		//Look in write-in map to see which candidates are write-ins.  Don't include these.
		if( !l_style.getWriteInRects().containsKey(c_currentContest.getId()))
			l_writeInMap = new TreeMap<Integer, Rectangle>();
		else
			l_writeInMap = l_style.getWriteInRects().get(c_currentContest.getId());
		
		for( Contestant l_contestant : c_currentContest.getContestants() )
		{
			if( !l_writeInMap.containsKey(l_contestant.getId() ) )
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
		WriteInResolution l_res = null;
		for( Contestant l_contestant : c_currentContest.getContestants() )
		{
			if( l_contestant.getName() == p_name )
			{
				l_candidateId = l_contestant.getId();
				l_res = new WriteInResolution(getImage(), p_name, Integer.toString(l_contestant.getId()));
				l_res.contest = c_currentContest;
				l_res.choice = c_currentLocation.choice;
			}
		}
		
		if( l_res != null )
			c_resolutions.add(l_res);
		
		c_currentLocation.choice.normalizeChoiceWriteIn(c_currentLocation.candidateId, l_candidateId);
		
	}
	
	@SuppressWarnings("unchecked")
	public void WriteResults(String p_outDir)
	{
		Collection l_files = FileUtils.listFiles(new File(p_outDir), new String[]{"sbr"}, false);
		TreeMap<Integer, BufferedWriter> l_writers = new TreeMap<Integer, BufferedWriter>();
		
		Iterator l_iterator = l_files.iterator();
		
		while(l_iterator.hasNext())
		{
			File l_file = (File)l_iterator.next();
			try {
				RandomBallotStore l_store = new RandomBallotStore(0, l_file.getPath(), null, null);
				l_store.open();
				Vector<Ballot> l_ballots = l_store.getBallots();
				for( Ballot l_ballot : l_ballots )
				{
					if( !l_writers.containsKey(l_ballot.getBallotStyleID()) )
					{
						BufferedWriter l_writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(p_outDir + File.separator + l_ballot.getBallotStyleID() + "M3in.xml")));
						l_writers.put(l_ballot.getBallotStyleID(), l_writer);
						l_writer.write("<xml>\n\t<print>\n");
					}
					
					BufferedWriter l_writer = l_writers.get(l_ballot.getBallotStyleID());
					
					if (l_ballot.isCounted())
					{
						l_writer.write("\t\t<row id=\"" + l_ballot.getId() + "\" p3=\"");
						String l_tmp = "";
						
						BallotStyle l_style = c_ballotStyles.get(l_ballot.getBallotStyleID());
						for (Integer l_cId : l_style.getContests())
						{
							Contest l_c = c_contests.get(l_cId);
							if (l_ballot.hasContest(l_c.getId()))
							{
								Integer l_data[][] = l_ballot.getContestData(l_c.getId());
								//For each column
								for (int l_i = 0; l_i < l_data[0].length; l_i++)
								{
									Vector<Integer> l_cans = new Vector<Integer>();
									//Each contestant
									for (int l_j = 0; l_j < l_data.length; l_j++)
									{
										if (l_data[l_j][l_i] == 1)
										{
											l_cans.add(l_j);
										}
									}
									if (l_cans.size() != 1) l_tmp += -1;
									else l_tmp += l_cans.get(0);
									l_tmp += " ";
								}
							}
						}
						l_writer.write( l_tmp.substring(0, l_tmp.length()-1) );
						l_writer.write( "\" page=\"NONE\"/>\n" );
					}
				}
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		while( !l_writers.isEmpty() )
		{
			BufferedWriter l_writer = l_writers.pollFirstEntry().getValue();
			try {
				l_writer.write("\t</print>\n</xml>");
				l_writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void Tally(String p_outDir)
	{
		while( !c_contestChoices.isEmpty() )
		{
			Integer l_key = c_contestChoices.firstKey();
			
			Vector<ContestChoice> l_choices = c_contestChoices.get(l_key);
			Collections.shuffle(l_choices, new SecureRandom());
			c_contestChoices.remove(l_key);
			
			Vector<ContestChoice> l_shortChoices = c_unalteredChoices.get(l_key);
			Collections.shuffle(l_choices, new SecureRandom());
			c_unalteredChoices.remove(l_key);
			
			Contest l_curContest = c_contests.get(l_key);
			WriteContestResults(l_choices, l_curContest, p_outDir + File.separator + l_curContest.getShortName() + ".xml");
			WriteContestResults(l_shortChoices, l_curContest, p_outDir + File.separator + l_curContest.getShortName() + "Short.xml");
		}
	}
	
	private void WriteContestResults(Vector<ContestChoice> p_choices, Contest p_contest, String p_out)
	{
		DocumentBuilderFactory l_factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder l_builder = null;
		try {
			l_builder = l_factory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			return;
		}
		Document l_doc = l_builder.newDocument();
		
		Element l_root = l_doc.createElement("Results");
		l_doc.appendChild(l_root);
		
		Element l_contestNode = l_doc.createElement("Contest");
		l_root.appendChild(l_contestNode);
		
		Element l_contestNameNode = l_doc.createElement("Name");
		l_contestNameNode.appendChild(l_doc.createTextNode(p_contest.getContestName()));
		l_contestNode.appendChild(l_contestNameNode);
		
		Element l_contestIdNode = l_doc.createElement("ID");
		l_contestIdNode.appendChild(l_doc.createTextNode(p_contest.getId().toString()));
		l_contestNode.appendChild(l_contestIdNode);
		
		Element l_tallyMethodNode = l_doc.createElement("TallyMethod");
		l_tallyMethodNode.appendChild(l_doc.createTextNode(p_contest.getTallyMethod().getClass().toString()));
		l_contestNode.appendChild(l_tallyMethodNode);
		
		Element l_contestants = l_doc.createElement("Contestants");
		l_contestNode.appendChild(l_contestants);
		
		for( Contestant l_contestant : p_contest.getContestants() )
		{
			Element l_contestantNode = l_doc.createElement("Contestant");
			l_contestants.appendChild(l_contestantNode);
			
			Element l_contestantNameNode = l_doc.createElement("Name");
			l_contestantNameNode.appendChild(l_doc.createTextNode(l_contestant.getName()));
			l_contestantNode.appendChild(l_contestantNameNode);
			
			Element l_contestantIdNode = l_doc.createElement("ID");
			l_contestantIdNode.appendChild(l_doc.createTextNode(l_contestant.getId().toString()));
			l_contestantNode.appendChild(l_contestantIdNode);
		}
		
		Element l_votesNode = l_doc.createElement("Votes");
		l_root.appendChild(l_votesNode);
		
		for( ContestChoice l_choice : p_choices )
		{
			Element l_voteNode = l_doc.createElement("Vote");
			l_votesNode.appendChild(l_voteNode);
			
			int[][] l_data = l_choice.getChoices();
			for( int x = 0; x < l_data.length; x++ )
			{
				Element l_rankNode = l_doc.createElement("Rank");
				l_rankNode.setAttribute("id", x + "");
				String l_rankValue = "";
				for( int y = 0; y < l_data[x].length; y++ )
				{
					l_rankValue += Integer.toString(l_data[x][y]);
					if( y != l_data[x].length - 1 )
					{
						l_rankValue += " ";
					}
				}
				l_rankNode.appendChild(l_doc.createTextNode(l_rankValue));
				l_voteNode.appendChild(l_rankNode);
			}
		}
		
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer;
		try {
			transformer = transformerFactory.newTransformer();
	        DOMSource source = new DOMSource(l_doc);
	        FileOutputStream l_out = new FileOutputStream(p_out);
	        StreamResult result =  new StreamResult(l_out);
	        transformer.transform(source, result);
	        l_out.close();
		} catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void WriteResolutionPdf(String p_outDir)
	{
		com.lowagie.text.Document l_doc = new com.lowagie.text.Document();
		com.lowagie.text.Image l_img;
		String l_formatString = "Name: %s (%s)\nContest Name: %s\nWrite-in Choice ID: %s";
		
		//Make sure we don't overwrite previous saves.
		try {
			PdfWriter.getInstance(l_doc, new FileOutputStream(p_outDir + File.separator + "Resolves.pdf"));
			l_doc.open();
			PdfPTable l_table = new PdfPTable(2);
			for( WriteInResolution l_res : c_resolutions )
			{
				l_img = com.lowagie.text.Image.getInstance(l_res.image, null);
				l_table.addCell(l_img);
				l_table.addCell(String.format(l_formatString, l_res.name, l_res.id, l_res.contest.getShortName(), l_res.choice.getId()));
			}
			l_doc.add(l_table);
			l_doc.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	public String getContestName()
	{
		return c_currentContest.getContestName();
	}

}
