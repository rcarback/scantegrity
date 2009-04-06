package action;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.NoSuchElementException;

import net.sourceforge.stripes.action.ActionBean;
import net.sourceforge.stripes.action.ActionBeanContext;
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
	ArrayList<ArrayList<String[]>> c_codes = new ArrayList<ArrayList<String[]>>();
	String c_error = "";
	
	public String getResult()
	{
		if( c_codes.size() == 0 )
			return "";
		
		String l_result = "<h4>Results:</h4>";
		for(int x = 0; x < c_codes.size(); x++ )
		{
			l_result += "<h5>Question " + (x + 1) + ": </h5>";
			l_result += "<table><tr><th>Symbol</th><th>Code</th></tr>";
			for( String[] code : c_codes.get(x) )
			{
				l_result += "<tr>";
				l_result += "<td>" + code[0] + "</td>";
				l_result += "<td>" + code[1] + "</td>";
				l_result += "</tr>";
			}
			l_result += "</table><br/>";
		}
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
			PreparedStatement l_query = l_conn.prepareStatement("SELECT question,symbol,code FROM ContestResults WHERE serial=? ORDER BY question,symbol");
			l_query.setInt(1, c_serial);
			
			ResultSet l_results = l_query.executeQuery();
			int c_question = -1;
			ArrayList<String[]> c_newCodes = new ArrayList<String[]>();
			
			while( l_results.next() )
			{
				int c_newQuestion = l_results.getInt("question");
				if( c_question == -1 )
					c_question = c_newQuestion;
				else if( c_question != c_newQuestion )
				{
					c_codes.add(c_newCodes);
					c_newCodes = new ArrayList<String[]>();
				}
				
				String[] c_newCode = new String[2];
				c_newCode[0] = Integer.toString(l_results.getInt("symbol"));
				c_newCode[1] = l_results.getString("code");
				c_newCodes.add(c_newCode);
			}
			if( c_newCodes.size() != 0 )
				c_codes.add(c_newCodes);
			
			
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
