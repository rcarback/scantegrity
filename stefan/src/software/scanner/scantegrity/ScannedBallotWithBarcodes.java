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

	public static String GetStringFromBarcode(BufferedImage img) throws Exception {		
	    Hashtable<DecodeHintType, Object> hints = new Hashtable<DecodeHintType, Object>(3);
	    hints.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);
		MonochromeBitmapSource source = new BufferedImageMonochromeBitmapSource(img);
		Result result = new MultiFormatReader().decode(source, hints);
		ParsedResult parsedResult = ResultParser.parseResult(result);
		return parsedResult.getDisplayResult();	      
	}
	
	public static void main(String[] args) throws Exception {
		String dir="C:\\TP Nov 3 2009, mock PRIVATE\\ward1\\barcode.bmp";
		BufferedImage bi=ImageIO.read(new File(dir));
		System.out.println(ScannedBallotWithBarcodes.GetStringFromBarcode(bi));
	}
}
