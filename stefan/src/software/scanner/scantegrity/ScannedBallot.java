package software.scanner.scantegrity;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.gwu.voting.standardFormat.basic.Answer;
import org.gwu.voting.standardFormat.basic.Question;
import org.gwu.voting.standardFormat.electionSpecification.ElectionSpecification;

import software.common.BallotGeometry;
import software.common.BallotGeometryMap;
import software.common.Cluster;
import software.common.InputConstants;
import software.common.Prow;

public class ScannedBallot extends software.scanner.ScannedBallot {

	static {
		BallotGeometryMap.ballotType=InputConstants.BallotType.SCANTEGRITY;
		markThreasholdTop=markThreasholdBottom=0.3f;
	}
	
	public ScannedBallot(BallotGeometry geom, ElectionSpecification es) {
		super(geom,es);
	}
	
	public void detectSerialFromPerfectImage(BufferedImage img,double dpi) throws Exception {

		//first detect the serial
		Cluster c=bgm.getSerialBulleted();

		int fromx=(int)(c.getXmin()*dpi);		
		int fromy=(int)(c.getYmin()*dpi);
		double w=((c.getXmax()*dpi)-fromx)/10;
		double h=((c.getYmax()*dpi)-fromy)/bgm.getNoDigitsInserial();		

		serial="";

		innerRadiusCurrent=0;
		outerRadiusCurrent=(int)(outerRadiusBottom*dpi);

		innerRadiusCurrentSquare=innerRadiusCurrent*innerRadiusCurrent;
		outerRadiusCurrentSquare=outerRadiusCurrent*outerRadiusCurrent;
		
		//from a row, get the balckest of them all
		int max=0;
		int noPixels=0;
		int digit=-1;
		for (int row=0;row<bgm.getNoDigitsInserial();row++) {
			max=0;
			digit=-1;
			for (int i=0;i<10;i++) {
				/*
				if (isMarkedSerial(img, (int)(fromx+digit*w),(int)(fromy+h*row),(int)w,(int)h)) {
					serial+=digit;
					break;
				}
				*/
				noPixels=getNoPixelsBlack(img, (int)(fromx+i*w),(int)(fromy+h*row),(int)w,(int)h);
				if (noPixels > max) {
					max=noPixels;
					digit=i;
				}
			}
			serial+=digit;
		}		
		if (serial.length() != bgm.getNoDigitsInserial()) {
			throw new Exception("The serial number ("+serial+")has |"+serial.length()+"| digits instead of |"+bgm.getNoDigitsInserial()+"|, as indicated by the ballotMap");				
		}
	}

	public Prow.ChosenPage getSelectedPage() {
		if (Math.random()<0.5)
			return Prow.ChosenPage.TOP;
		return Prow.ChosenPage.BOTTOM;
	}
	
	protected boolean isMarked(BufferedImage img, Cluster c, double dpi) {
		int fromx=(int)(c.getXmin()*dpi);		
		int fromy=(int)(c.getYmin()*dpi);
		int w=(int)(c.getXmax()*dpi)-fromx;
		int h=(int)(c.getYmax()*dpi)-fromy;
						
		return isMarked(img, fromx,fromy,w,h);
	}

	boolean isMarked(BufferedImage img, int fromx,int fromy,int w,int h) {		
		if (debug) {
			Graphics2D g=img.createGraphics();
			g.setColor(Color.RED);
			//draw the outer circle
			g.drawRect(fromx, fromy, w, h);
			
			try {
				ImageIO.write(img.getSubimage(fromx, fromy, w, h),"png",new File(fromx+"_"+fromy+"Mark.png"));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		if (markPixels==null || markPixels.length!=w*h)
			markPixels=new int[w*h];
		
		img.getRGB(fromx, fromy, w, h, markPixels,0, w);
						
		int count=0;
		for (int ii=0;ii<markPixels.length;ii++) {
			Color color=new Color(markPixels[ii]);
			if ( ((Math.abs(color.getRed() - 255) + Math.abs(color.getGreen()-255) + Math.abs(color.getBlue()-255))) > 200 ){
				count++;
			}
		}
		double greyArea=(double)count / (double)(w*h);
		
		if (debug) {
			System.out.print("fromx="+fromx+" fromy="+fromy+" grayArea="+greyArea+" ");
			System.out.println("Scantegrity "+" "+markThreasholdTop);
		}
		
		if (greyArea > markThreasholdTop) 
			return true;
		return false;
	}

	protected int getNoPixelsBlack(BufferedImage img, int fromx, int fromy,int w,int h) {
		if (debug) {
			Graphics2D g=img.createGraphics();
			g.setColor(Color.RED);
			//draw the outer circle
			g.drawRect(fromx, fromy, w, h);
			
			try {
				ImageIO.write(img.getSubimage(fromx, fromy, w, h),"png",new File(fromx+"_"+fromy+"Mark.png"));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		if (markPixels==null || markPixels.length!=w*h)
			markPixels=new int[w*h];
		
		img.getRGB(fromx, fromy, w, h, markPixels,0, w);
						
		int count=0;
		for (int ii=0;ii<markPixels.length;ii++) {
				Color color=new Color(markPixels[ii]);
				if ( ((Math.abs(color.getRed() - 255) + Math.abs(color.getGreen()-255) + Math.abs(color.getBlue()-255))) > 100 ){
					count++;
				}
		}
		return count;
	}
	
	protected boolean isMarkedSerial(BufferedImage img, int fromx, int fromy,int w,int h) {						
		int count=getNoPixelsBlack(img, fromx, fromy, w, h);

		double greyArea=(double)count / (double)(w*h);
		double serialThreashold=0.45;
		if (debug) {
			System.out.print("Serial fromx="+fromx+" fromy="+fromy+" grayArea="+greyArea+" threashold=");
			System.out.println(serialThreashold);
		}
//System.out.println(greyArea +" "+ serialThreashold);		
		if (greyArea > serialThreashold) {
			return true;
		}
		
		return false;
	}
	
	public int containsWritein() throws Exception {
		byte[] votes=toProw().getVote();
		int votesPos=0;
		for (int j=0;j<qs.length;j++) {
			Question q=qs[j];
			Answer[] as=q.getOrderedAnswers();
			for (int i=0;i<q.getMax();i++) {
				if (votes[votesPos]!=-1) {
					if (as[votes[votesPos]].getId().compareTo(org.gwu.voting.standardFormat.Constants.WRITEIN)==0) {
						return j;
					}
				}
				votesPos++;
			}
		}
		
		return -1;
	}

	
	public static void main(String[] args) throws Exception {
/*		
		String dir="Elections/VoComp/scantegrity/";
		BallotGeometry geom=new BallotGeometry(dir+"geometry.xml");
		ElectionSpecification es= new ElectionSpecification(dir+"../ElectionSpec.xml");
		ScannedBallot sb=new ScannedBallot(geom,es);
		BufferedImage img=ImageIO.read(new File(dir+"scannes/ballot0004.JPG"));
		sb.detect(img);
		System.out.println(sb.toProw());
*/		
		String dir=InputConstants.publicFolder;
		BallotGeometry geom=new BallotGeometry(dir+"geometry.xml");
		ElectionSpecification es= new ElectionSpecification(dir+"ElectionSpec.xml");
		ScannedBallot sb=new ScannedBallot(geom,es);
		BufferedImage img=ImageIO.read(new File(InputConstants.publicFolder+"../scannes/Scan0001.JPG"));
		sb.detect(img);
		System.out.println(sb.toProw());

/*		
		img=ImageIO.read(new File(dir+"67890.jpg"));
		sb.detect(img);
		System.out.println(sb.toXMLString());
*/				
	}
}
