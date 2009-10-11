package software.scanner.scantegrity;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Hashtable;

import javax.imageio.ImageIO;

import org.gwu.voting.standardFormat.electionSpecification.ElectionSpecification;

import com.google.zxing.DecodeHintType;
import com.google.zxing.MonochromeBitmapSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageMonochromeBitmapSource;
import com.google.zxing.client.result.ParsedResult;
import com.google.zxing.client.result.ResultParser;

import software.common.BallotGeometry;
import software.common.InputConstants;

public class ScannedBallotWithBarcodes extends ScannedBallot {
	
	public ScannedBallotWithBarcodes(BallotGeometry geom, ElectionSpecification es) {
		super(geom,es);
	}
	
	public void detectSerialFromPerfectImage(BufferedImage img,double dpi) throws Exception {
		long start=System.currentTimeMillis();
		
	    Hashtable<DecodeHintType, Object> hints = new Hashtable<DecodeHintType, Object>(3);
	    hints.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);
		MonochromeBitmapSource source = new BufferedImageMonochromeBitmapSource(img);
		Result result = new MultiFormatReader().decode(source, hints);
		ParsedResult parsedResult = ResultParser.parseResult(result);
		serial=parsedResult.getDisplayResult();	      
	    //System.out.println("Detecting the serial number took "+(System.currentTimeMillis()-start)+" milisseconds");
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
		ScannedBallotWithBarcodes sb=new ScannedBallotWithBarcodes(geom,es);
	
		File[] images=new File(dir+"../scannes/takomaMockBallot_apr2009draft1 150/").listFiles();
/*		
		long start=System.currentTimeMillis();
		for (File f:images) { 
		
System.out.println(f);
			BufferedImage img=ImageIO.read(f);
			//System.out.println("Reading the image in memory took"+(System.currentTimeMillis()-start)+" milisseconds");
			
			sb.detect(img);

			System.out.print(sb.toProw());
		}
	    System.out.println("Processing "+images.length+" took "+(System.currentTimeMillis()-start)+" miliseconds");
*/		
		long start=System.currentTimeMillis();
		File f=new File(dir+"../scannes/tp training/Scan0001.jpg");
		System.out.println(f);
		BufferedImage img=ImageIO.read(f);
		sb.detect(img);
		System.out.println(sb.toProw());
		System.out.println("All processing took "+(System.currentTimeMillis()-start)+" milisseconds");
		
	}
}
