package action;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.NoSuchElementException;

import net.sourceforge.stripes.action.ActionBean;
import net.sourceforge.stripes.action.ActionBeanContext;
import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.validation.Validate;

public class CheckCodesActionBean implements ActionBean {
	//Parameters for database connection
	private static final String c_dbAddress = "jdbc:derby:";
	private static final String c_dbName = "EMWS";
	private static final String c_dbUser = "APP";
	private static final String c_dbPass = "";
	
	@Validate(required=true) int c_serial;
	ArrayList<String[]> c_codes = new ArrayList<String[]>();
	String c_error = "";
	
	public String getResult()
	{
		if( c_codes.size() == 0 )
			return "";
		
		String l_result = "<table><tr><th>Contest</th><th>Code</th></tr>";
		for( String[] code : c_codes )
		{
			l_result += "<tr>";
			l_result += "<td>" + code[0] + "</td>";
			l_result += "<td>" + code[1] + "</td>";
			l_result += "</tr>";
		}
		l_result += "</table>";
		return l_result;
	}
	
	public void setResult(String p_res)
	{
		
	}
	
	public String getErrors()
	{
		return "<div class=\"error\">" + c_error + "</div>";
	}

	public void setErrors(String p_error)
	{
		c_error = p_error;
	}
	
	public int getSerial()
	{
		return c_serial;
	}
	
	public void setSerial(int p_serial)
	{
		c_serial = p_serial;
	}
	
	public Resolution submit()
	{
		if( c_serial == 0 )
			return new ForwardResolution("/WEB-INF/jsp/checkcodes.jsp");
		try
		{
			c_codes.clear();
			
			//Load derby database driver
			Class.forName("org.apache.derby.jdbc.ClientDriver").newInstance();
			//Create connection to database.  Create database if it doesn't exist.
			Connection l_conn = DriverManager.getConnection(c_dbAddress + c_dbName + ";create=true;" + "user=" + c_dbUser + ";password=" + c_dbPass);
	
			//Create SQL statement object
			PreparedStatement l_query = l_conn.prepareStatement("SELECT contest,code FROM ContestResults WHERE serial=? ORDER BY contest");
			l_query.setInt(1, c_serial);
			
			ResultSet l_results = l_query.executeQuery();
			while( l_results.next() )
			{
				String[] c_newCode = new String[2];
				c_newCode[0] = Integer.toString(l_results.getInt("contest"));
				c_newCode[1] = l_results.getString("code");
				c_codes.add(c_newCode);
			}
			
			l_results.close();
			
			if( c_codes.size() == 0 )
				throw new NoSuchElementException();
			
		} catch (SQLException e) {
			c_error = "Could not execute SQL: " + e.getMessage();
		} catch (InstantiationException e) {
			c_error = "Could not load derby driver: instantiation exception";
		} catch (IllegalAccessException e) {
			c_error = "Could not load derby driver: illegal access exception";
		} catch (ClassNotFoundException e) {
			c_error = "Could not load derby driver: class not found exception";
		} catch (NoSuchElementException e)
		{
			c_error = "Could not locate ballot with given serial. Please try again.";
		}
		
		return new ForwardResolution("/WEB-INF/jsp/checkcodes.jsp");
	}
	
	
	private ActionBeanContext c_ctx;
	public ActionBeanContext getContext() { 
		return c_ctx; 
	}
	public void setContext(ActionBeanContext p_ctx) { 
		this.c_ctx = p_ctx; 
	}
}
