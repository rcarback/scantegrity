package action;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class UserManage {
	private static final String c_dbAddress = "jdbc:derby:";
	private static final String c_dbName = "EMWS";
	private static final String c_dbUser = "APP";
	private static final String c_dbPass = "";
	
	
	static String addUser(String p_userName, String p_password) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException
	{
		byte[] l_seed = null;
		byte[] l_digest = null;
		
		try {
			MessageDigest l_md = MessageDigest.getInstance("SHA-512");

			l_md.update(p_password.getBytes());
		
			SecureRandom l_random = new SecureRandom();
			l_seed = new byte[512];
			l_random.nextBytes(l_seed);
			l_md.update(l_seed);
			
			l_digest = l_md.digest();
		}
		catch(NoSuchAlgorithmException e)
		{
			e.printStackTrace();
		}
		
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
			l_query.execute("SELECT COUNT(*) FROM Users");
		}
		catch( SQLException e )
		{
			if( e.getMessage().contains("does not exist") )
			{
				l_query.execute("CREATE TABLE Users ( username varchar(50), password varchar(15), salt varchar(10) )");
			}
		}
		
		//Use prepared statement for queries with parameters
		PreparedStatement l_sqlStatement = l_conn.prepareStatement("INSERT INTO Users VALUES (?, ?, ?)");
		PreparedStatement l_existsQuery = l_conn.prepareStatement("SELECT * FROM Users WHERE username=?");
		
		l_existsQuery.setString(1, p_userName);
		
		if( l_existsQuery.executeQuery().next() )
		{
			l_existsQuery.close();
			l_conn.close();
			return "User already exists.";
		}
		
		l_sqlStatement.setString(1, p_userName);
		BASE64Encoder l_enc = new BASE64Encoder();
		l_sqlStatement.setString(2, l_enc.encode(l_digest));
		l_sqlStatement.setString(3, l_enc.encode(l_seed));
		
		l_sqlStatement.execute();
		
		l_sqlStatement.close();
		l_conn.close();
		
		return "User added successfully.";
	}
	
	static ArrayList<String> getUsers() throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException
	{
		//Load derby database driver
		Class.forName("org.apache.derby.jdbc.ClientDriver").newInstance();
		//Create connection to database.  Create database if it doesn't exist.
		Connection l_conn = DriverManager.getConnection(c_dbAddress + c_dbName + ";create=true;" + "user=" + c_dbUser + ";password=" + c_dbPass);

		PreparedStatement l_query = l_conn.prepareStatement("SELECT username FROM Users");
		
		ResultSet l_results = l_query.executeQuery();
		
		ArrayList<String> l_users = new ArrayList<String>();
		
		while(l_results.next())
		{
			l_users.add(l_results.getString("username"));
		}
		
		return l_users;
	}
	
	static boolean removeUser(String p_user) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException
	{
		//Load derby database driver
		Class.forName("org.apache.derby.jdbc.ClientDriver").newInstance();
		//Create connection to database.  Create database if it doesn't exist.
		Connection l_conn = DriverManager.getConnection(c_dbAddress + c_dbName + ";create=true;" + "user=" + c_dbUser + ";password=" + c_dbPass);

		PreparedStatement l_query = l_conn.prepareStatement("DELETE FROM Users WHERE username=?");
		l_query.setString(1, p_user);
		
		return l_query.executeUpdate() > 0;
	}
	
	static boolean queryUser(String p_userName, String p_pass) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException, IOException
	{
		//Load derby database driver
		Class.forName("org.apache.derby.jdbc.ClientDriver").newInstance();
		//Create connection to database.  Create database if it doesn't exist.
		Connection l_conn = DriverManager.getConnection(c_dbAddress + c_dbName + ";create=true;" + "user=" + c_dbUser + ";password=" + c_dbPass);

		PreparedStatement l_query = l_conn.prepareStatement("SELECT * FROM Users WHERE username=?");
		l_query.setString(1, p_userName);
		
		ResultSet l_results = l_query.executeQuery();
		if( !l_results.next() )
			return false;
		
		byte[] l_seed = null;
		byte[] l_digest = null;
		
		BASE64Decoder l_dec = new BASE64Decoder();
		
		try {
			MessageDigest l_md = MessageDigest.getInstance("SHA-512");

			l_md.update(p_pass.getBytes());	
			
			l_seed = l_dec.decodeBufferToByteBuffer(l_results.getString("seed")).array();
			l_md.update(l_seed);
			
			l_digest = l_md.digest();
		}
		catch(NoSuchAlgorithmException e)
		{
			e.printStackTrace();
		}
		
		byte[] l_testDigest = l_dec.decodeBufferToByteBuffer(l_results.getString("password")).array();
		
		boolean l_match = true;
		
		for( int x = 0; x < l_testDigest.length; x++ )
		{
			if( l_testDigest[x] != l_digest[x] )
				l_match = false;
		}
		
		return l_match;
	}

}
