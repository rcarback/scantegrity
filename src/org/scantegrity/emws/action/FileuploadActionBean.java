package action;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import net.sourceforge.stripes.action.ActionBean;
import net.sourceforge.stripes.action.ActionBeanContext;
import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.FileBean;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.validation.SimpleError;
import net.sourceforge.stripes.validation.Validate;
import net.sourceforge.stripes.validation.ValidationErrors;
import net.sourceforge.stripes.validation.ValidationMethod;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class FileuploadActionBean implements ActionBean {

	//Parameters for database connection
	private static final String c_dbAddress = "jdbc:derby:";
	private static final String c_dbName = "EMWS";
	private static final String c_dbUser = "APP";
	private static final String c_dbPass = "";
	
	@Validate(required=true) FileBean c_file;
	String c_result = "";
	String c_error = "";

	public FileBean getFile()
	{
		return c_file;
	}

	public void setFile(FileBean p_file)
	{
		c_file = p_file;
	}

	public String getResult()
	{
		return c_result;
	}

	public void setResult(String p_text)
	{
		c_result = "<div class=\"results\">" + p_text + "</div>";
	}

	public String getErrors()
	{
		return "<div class=\"error\">" + c_error + "</div>";
	}

	public void setErrors(String p_error)
	{
		c_error = p_error;
	}

	private ActionBeanContext c_ctx;
	public ActionBeanContext getContext() { 
		return c_ctx; 
	}
	public void setContext(ActionBeanContext p_ctx) { 
		this.c_ctx = p_ctx; 
	}

	@DefaultHandler
	public Resolution submit()
	{
		if( c_file != null )
		{
			if( c_file.getFileName().contains(".xml") )
			{
				parseXML();
			}
			
			saveFile();
		}

		return new ForwardResolution("/WEB-INF/pages/fileupload.jsp");
	}
	
	public void saveFile()
	{
		try
		{
			File c_docsDir = new File(c_ctx.getServletContext().getRealPath("/docs/"));
			if( !c_docsDir.exists() )
			{
				throw new FileNotFoundException("Docs folder could not be found");
			}
			File c_saveFile = new File(c_docsDir.getPath() + File.separator + c_file.getFileName());
			c_file.save(c_saveFile);
			
			if( c_file.getFileName().contains(".xml") )
			{
				ZipThread c_zip = new ZipThread(c_docsDir);
				c_zip.start();
			}
		}
		catch(IOException e)
		{
			c_error += e.getMessage() + "\n";
		}
	}
	
	public void parseXML()
	{
		if( c_file != null )
		{
			try {
				//Get stream from uploaded file
				InputStream l_istream = c_file.getInputStream();

				try {
					//Load derby database driver
					Class.forName("org.apache.derby.jdbc.ClientDriver").newInstance();
					//Create connection to database.  Create database if it doesn't exist.
					Connection l_conn = DriverManager.getConnection(c_dbAddress + c_dbName + ";create=true;" + "user=" + c_dbUser + ";password=" + c_dbPass);

					//Create SQL statement object
					Statement l_query = l_conn.createStatement();
					
					//Test to see if the table exists, create it if it doesn't
					/* Now, we just try to select something from the table and if an error is thrown
					 * that contains "does not exist" then we try and create it.  Could be done
					 * better with T-SQL, doesn't handle the case where the table exists but 
					 * doesn't have the columns we are expecting.
					 */
					try
					{
						l_query.execute("SELECT COUNT(*) FROM ContestResults");
					}
					catch( SQLException e )
					{
						if( e.getMessage().contains("does not exist") )
						{
							l_query.execute("CREATE TABLE ContestResults ( serial integer, question integer, symbol integer, code varchar(10) )");
						}
					}


					//Use prepared statement for queries with parameters
					PreparedStatement l_sqlStatement = l_conn.prepareStatement("INSERT INTO ContestResults VALUES (?, ?, ?, ?)");
					PreparedStatement l_existsQuery = l_conn.prepareStatement("SELECT * FROM ContestResults WHERE serial=?");
					
					//Create documentbuilder and parse uploaded file
					DocumentBuilderFactory l_factory = DocumentBuilderFactory.newInstance();
					DocumentBuilder l_builder = l_factory.newDocumentBuilder();
					Document l_doc = l_builder.parse(l_istream);
					
					//Get all <ballot> elements
					NodeList l_nodes = l_doc.getElementsByTagName("ballot");
					for(int x = 0; x < l_nodes.getLength(); x++ )
					{
						
						//Read serial from ballot element
						Node l_node = l_nodes.item(x);
						//Ignore if not a ballot element (like #TEXT)
						if( !l_node.getNodeName().equals("ballot") )
							continue;
						
						int l_serial = Integer.parseInt(l_node.getAttributes().getNamedItem("printedSerial").getNodeValue());
						
						//Check to see if this ID is already in the database
						l_existsQuery.setInt(1, l_serial);
						if( l_existsQuery.executeQuery().next() )
						{
							c_error = "Some elements were already in the database and were ignored.";
							continue;
						}
						NodeList l_questionNodes = l_node.getChildNodes();
					
						//Loop over all <question> nodes
						//TODO: Add code here later if there can be more than one question
						for( int y = 0; y < l_questionNodes.getLength(); y++ )
						{
							//Ignore if not a question element (like #TEXT)
							if( !l_questionNodes.item(y).getNodeName().equals("question") )
								continue;
							
							int l_question = Integer.parseInt(l_questionNodes.item(y).getAttributes().getNamedItem("id").getNodeValue());
							
							NodeList l_symbolNodes = l_questionNodes.item(y).getChildNodes();
							
							//Loop over all <symbol> nodes
							for( int z = 0; z < l_symbolNodes.getLength(); z++ )
							{
								//Ignore if not a symbol element (like #TEXT)
								if( !l_symbolNodes.item(z).getNodeName().equals("symbol") )
									continue;
								
								//Read node attributes to get id and code
								NamedNodeMap l_symbolAttributes = l_symbolNodes.item(z).getAttributes();
								int l_id = Integer.parseInt(l_symbolAttributes.getNamedItem("id").getNodeValue());
								String l_code = l_symbolAttributes.getNamedItem("code").getNodeValue();

								//Set SQL parameters and add to batch
								l_sqlStatement.setInt(1, l_serial);
								l_sqlStatement.setInt(2, l_question);
								l_sqlStatement.setInt(3, l_id);
								l_sqlStatement.setString(4, l_code);
								l_sqlStatement.addBatch();
							}
						}
					}
					
					l_query.close();
					l_sqlStatement.executeBatch();
					
					l_sqlStatement.close();
					l_conn.close();
					c_result = "File added successfully";
				} catch (SQLException e) {
					c_error = "Could not execute SQL: " + e.getMessage();
				} catch (InstantiationException e) {
					c_error = "Could not load derby driver: instantiation exception";
				} catch (IllegalAccessException e) {
					c_error = "Could not load derby driver: illegal access exception";
				} catch (ClassNotFoundException e) {
					c_error = "Could not load derby driver: class not found exception";
				} catch (ParserConfigurationException e) {
					c_error = "Could not load parser configuration";
				} catch (SAXException e) {
					c_error = "Could not parse XML file: " + e.getMessage();
				}

				l_istream.close();
			} catch (IOException e) {
				c_error = e.getMessage();
			}

		}
	}
	
	class ZipThread extends Thread
	{
		File c_dir;
		
		public ZipThread(File p_dir)
		{
			c_dir = p_dir;
		}
		
		public void run()
		{
			try
			{
				File l_zipFile = new File(c_dir, ".ElectionFiles.zip");
				System.err.println("Zipping to " + l_zipFile.getPath());
				FileOutputStream l_fo = new FileOutputStream(l_zipFile);
				ZipOutputStream l_zipOutput = new ZipOutputStream(l_fo);
		
				File[] l_xmlFiles = c_dir.listFiles(new XmlFileFilter());
				
				for(File l_newFile : l_xmlFiles )
				{
					l_zipOutput.putNextEntry(new ZipEntry(l_newFile.getName()));
					FileInputStream l_fi = new FileInputStream(l_newFile);
					byte[] l_bytes = new byte[1024];
					int l_read;
				
					while(-1 != (l_read = l_fi.read(l_bytes)) )
					{
						l_zipOutput.write(l_bytes, 0, l_read);
					}
					
					l_zipOutput.flush();
				}
				
				l_zipOutput.close();
				
				l_zipFile.renameTo(new File(c_dir, "ElectionFiles.zip"));
			}
			catch(IOException e)
			{
				System.err.println(e.getStackTrace());
			}
		}

	}
	
	class XmlFileFilter implements FilenameFilter
	{
		public boolean accept(File dir, String name) {
			return name.contains(".xml");
		}
		
	}

}