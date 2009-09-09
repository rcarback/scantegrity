

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

import javax.imageio.ImageIO;

import org.junit.Test;
import org.scantegrity.common.Logging;
import org.scantegrity.scanner.ScannerInterface;

import uk.org.jsane.JSane_Exceptions.JSane_Exception;

public class ScannerInterfaceTest {
	
	@Test
	public void testScannerInterface()
	{
		Logging l_log = new Logging("~/scannerTestLog.xml", Level.ALL);
		ScannerInterface l_si = new ScannerInterface(l_log);
		
		try
		{
			l_si.printDevices();
		}
		catch (JSane_Exception e)
		{
			l_log.log(Level.FINEST, e.getMessage());
		}
		catch (IOException e)
		{
			l_log.log(Level.FINEST, e.getMessage());
		} 
		catch (NullPointerException e_npe)
		{
			l_log.log(Level.FINEST, e_npe.getMessage());
		}
		
		//get an image from the scanner
		BufferedImage l_image = null;
		try
		{
			l_image = l_si.getImageFromScanner();
			ImageIO.write(l_image, "tiff", new File("test.tiff"));
		}
		catch (JSane_Exception e1)
		{
			l_log.log(Level.FINEST, e1.getMessage());
		}
		catch (IOException e)
		{
			l_log.log(Level.FINEST, e.getMessage());
		} 
		catch (NullPointerException e_npe)
		{
			l_log.log(Level.FINEST, e_npe.getMessage());
		}
	}
}
