package org.scantegrity.common;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.imageio.ImageIO;

/************************************************************************
 * Loads in image from the filesystem, moves it to the directory specified
 * and calls a function specified by the callback object.
 * 
 * @author Travis
 *
 *************************************************************************/

public class ImageLoader {

	ImageHandler c_handler;
	
	/**
	 * Constructor - Creates ImageLoader object and sets callback to the object
	 * specified in the parameter
	 * 
	 * @param p_handler - ImageHandler object to call when images are loaded
	 */
	public ImageLoader(ImageHandler p_handler) throws IllegalArgumentException
	{
		if( p_handler == null )
			throw new IllegalArgumentException();
		
		c_handler = p_handler;
	}
	
	/*
	 * setHandler - Sets the callback handler object
	 * 
	 * @param p_handler - New callback object to use
	 */
	public void setHandler(ImageHandler p_handler)
	{
		if( p_handler == null )
			throw new IllegalArgumentException();
		
		c_handler = p_handler;
	}
	
	/*
	 * loadImage - Loads the image specified into a BufferedImage, moves the
	 * file to the destination folder specified, and calls the callback object
	 * with the newly created BufferedImage
	 * 
	 * @param p_srcFile - File to read the image from
	 * @param p_destFolder - Folder to move the images to
	 * @throws IOException - thrown when the file could not be read or moved
	 * @throws FileNotFoundException - when either the source file or destination folder
	 * do not exist
	 */
	public void loadImage(String p_srcFile, String p_destFolder) throws IOException
	{
		File l_inputFile = new File(p_srcFile);
		
		if( !l_inputFile.exists() )
			throw new FileNotFoundException("Source file does not exist.");
		
		File l_destFolder = new File(p_destFolder);
		
		if( !l_destFolder.exists() )
			throw new FileNotFoundException("Destination directory does not exist.");
		
		BufferedImage l_image = ImageIO.read( l_inputFile );
		
		//Move file to destination directory
		if( !l_inputFile.renameTo( new File( l_destFolder, l_inputFile.getName() ) ) )
		{
			throw new IOException("Could not move file");
		}
		
		c_handler.handleImage(l_image);
	}
}
