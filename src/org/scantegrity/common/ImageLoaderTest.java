package org.scantegrity.common;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/*
 * Test the ImageLoader class.  Provides an implementation of ImageHandler that simply
 * prints out the width and height of the image provided.  Source and destination folders
 * are provided as command line arguments.  The source folder is monitored and all
 * files discovered are loaded as images and moved into the destination folder.
 */

public class ImageLoaderTest {
	
	public static void main(String[] args) throws IOException, InterruptedException
	{
		File l_srcDirectory = new File(args[0]);
		File l_destDirectory = new File(args[1]);
		
		if( !l_srcDirectory.exists() || !l_destDirectory.exists() )
		{
			System.out.println("Directory could not be found");
			return;
		}
		
		TestHandler l_handler = new TestHandler();
		ImageLoader l_loader = new ImageLoader(l_handler);
		
		while(true)
		{
			File[] l_files = l_srcDirectory.listFiles();
			if(l_files.length > 0)
			{
				for(int x = 0; x < l_files.length; x++ )
				{
					l_loader.loadImage(l_files[x], l_destDirectory);
				}
			}
			Thread.sleep(1000);
		}
		
	}

}

class TestHandler implements ImageHandler
{
	public TestHandler()
	{}

	public void handleImage(BufferedImage p_image) {
		String[] l_properties = p_image.getPropertyNames();
		
		if( l_properties != null )
		{
			System.out.println("Properties:");
			for(int x = 0; x < l_properties.length; x++)
			{
				System.out.println(l_properties[x]);
			}
		}
		System.out.println("Width: " + p_image.getWidth());
		System.out.println("Heigth: " + p_image.getHeight());
		
	}
}