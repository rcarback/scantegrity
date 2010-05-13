package table;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class FlatFileTable implements EngineTable {

	ArrayList<ArrayList<Object>> c_list;
	
	//Creates a new table with columns defined by the types in p_typeDef
	public FlatFileTable( ArrayList<Class<?>> p_typeDef )
	{
		c_list = new ArrayList<ArrayList<Object>>();
	}
	
	//Reads in a table from a binary (serialized objects) file
	public FlatFileTable( String p_path ) throws FileNotFoundException, IOException, ClassNotFoundException
	{
		ObjectInputStream l_in = new ObjectInputStream(new FileInputStream(p_path));
		int l_size = l_in.read();
		
		c_list = new ArrayList<ArrayList<Object>>();
		
		while( l_in.available() > 0 )
		{
			ArrayList<Object> l_row = new ArrayList<Object>();
			for( int x = 0; x < l_size; x++ )
				l_row.add( l_in.readObject() );
			c_list.add(l_row);
		}
	}
	
	@Override
	public ArrayList<Object> getRow(int index) {
		if( index > c_list.size() )
			return null;
		else return c_list.get(index);
	}

	@Override
	public ArrayList<Object> getRow(Object key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<ArrayList<Object>> getAllRows() {
		return c_list;
	}

	@Override
	public void insertRow(ArrayList<Object> row) {
		c_list.add(row);
	}

	//Saves serialized data to a binary file
	public void saveSerializedFile(String p_path ) throws FileNotFoundException, IOException
	{
		ObjectOutputStream l_out = new ObjectOutputStream(new FileOutputStream(p_path));

		//Write out table data
		for( int x = 0; x < c_list.size(); x++ )
			for( int y = 0; y < c_list.get(x).size(); y++ )
				l_out.writeObject(c_list.get(x).get(y));
	}
	
	//Saves to XML
	public void saveXmlFile(String p_path)
	{
		try
		{
			DocumentBuilder l_b = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document l_doc = l_b.newDocument();
			
			Element l_root = l_doc.createElement("table");
			
			for( int x = 0; x < c_list.size(); x++ )
			{
				Element l_row = l_doc.createElement("row");
				for( int y = 0; x < c_list.get(y).size(); y++ )
				{
					Object l_obj = c_list.get(x).get(y);
					Element l_cell = l_doc.createElement("cell");
					
					l_row.appendChild(getXmlRepresentation(l_doc, l_obj));
				}
				l_root.appendChild(l_row);
			}

			Transformer l_trans = TransformerFactory.newInstance().newTransformer();
			DOMSource l_source = new DOMSource(l_doc);
			StreamResult l_res = new StreamResult(new FileOutputStream(p_path));
			
			l_trans.transform(l_source, l_res);
		}
		catch( ParserConfigurationException e )
		{
			e.printStackTrace();
		} catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerFactoryConfigurationError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private Element getXmlRepresentation(Document p_doc, Object p_obj)
	{
		return getXmlRepresentation(p_doc, p_obj, "cell");
	}

	private Element getXmlRepresentation(Document p_doc, Object p_obj, String p_nodeName)
	{
		if( p_obj.getClass() == ArrayList.class )
		{
			Element l_node = p_doc.createElement("list");
			ArrayList<Object> l_list = (ArrayList<Object>)p_obj;
			for( int x = 0; x < l_list.size(); x++ )
			{
				l_node.appendChild(getXmlRepresentation(p_doc, l_list.get(x), "listdata"));
			}
			return l_node;
		}
		else if( p_obj.getClass().isArray() )
		{
			Element l_node = p_doc.createElement("list");
			for( int x = 0; x < Array.getLength(p_obj); x++ )
			{
				l_node.appendChild(getXmlRepresentation(p_doc, Array.get(p_obj, x), "listdata"));
			}
			return l_node;
		}
		else
		{
			Element l_node = p_doc.createElement(p_nodeName);
			l_node.setTextContent(getStringRepresentation(p_obj));
			return l_node;
		}
	}
	
	private String getStringRepresentation(Object p_obj)
	{
		String l_ret = "";
		if( p_obj.getClass() == String.class )
		{
			l_ret = p_obj.toString();
		}
		else if( p_obj.getClass() == Integer.class || p_obj.getClass() == Double.class )
		{
			l_ret = p_obj.toString();
		}
		else if( p_obj.getClass() == byte[].class )
		{
			l_ret = org.apache.commons.codec.binary.Base64.encodeBase64URLSafeString((byte[])p_obj);
		}
		return l_ret;
	}
}
