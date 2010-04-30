package table;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class FlatFileTable implements EngineTable {

	ArrayList<ArrayList<Object>> c_list;
	ArrayList<Class<?>> c_typeDef;
	
	//Creates a new table with columns defined by the types in p_typeDef
	public FlatFileTable( ArrayList<Class<?>> p_typeDef )
	{
		c_list = new ArrayList<ArrayList<Object>>();
		c_typeDef = p_typeDef;
	}
	
	//Reads in a table from a file
	public FlatFileTable( String p_path ) throws FileNotFoundException, IOException, ClassNotFoundException
	{
		ObjectInputStream l_in = new ObjectInputStream(new FileInputStream(p_path));
		int l_size = l_in.read();
		for( int x = 0; x < l_size; x++ )
			c_typeDef.add((Class<?>)l_in.readObject());
		
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
	public ArrayList<Class<?>> getTypes() {
		return c_typeDef;
	}

	@Override
	public ArrayList<ArrayList<Object>> getAllRows() {
		return c_list;
	}

	@Override
	public void insertRow(ArrayList<Object> row) {
		if( row.size() != c_typeDef.size() )
			throw new IllegalArgumentException("Row length does not match number of table columns");
		c_list.add(row);
	}

	public void saveFile(String p_path ) throws FileNotFoundException, IOException
	{
		ObjectOutputStream l_out = new ObjectOutputStream(new FileOutputStream(p_path));
		//Write out the number of columns
		l_out.write( c_typeDef.size() );
		//Write out the type definitions
		for( int x = 0; x < c_typeDef.size(); x++ )
			l_out.writeObject( c_typeDef.get(x) );
		//Write out table data
		for( int x = 0; x < c_list.size(); x++ )
			for( int y = 0; y < c_list.get(x).size(); y++ )
				l_out.writeObject(c_list.get(x).get(y));
	}
	
}
