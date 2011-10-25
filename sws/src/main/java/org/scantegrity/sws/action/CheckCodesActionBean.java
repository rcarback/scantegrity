package org.scantegrity.sws.action;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Vector;

import net.sourceforge.stripes.action.ActionBean;
import net.sourceforge.stripes.action.ActionBeanContext;
import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.validation.Validate;

public class CheckCodesActionBean implements ActionBean {
	//Parameters for database connection
	private static final String c_dbAddress = "jdbc:derby:";
	private static final String c_dbName = "TPDB2011";
	private static final String c_dbUser = "APP";
	private static final String c_dbPass = "";
	
	@Validate(required=true) String c_serial;
	ArrayList<ArrayList<String[]>> c_codes = new ArrayList<ArrayList<String[]>>();
	String c_error = "";
	
	public String getResult()
	{
		if( c_codes.size() == 0 )
			return "";
		
		String l_result = "";
		
		for(int x = 0; x < c_codes.size(); x++ )
		{
			l_result += "<div>";
			l_result += "<h4>Contest " + (x + 1) + ": </h4>";
			l_result += "<table style=\"width:60%;\"><tr><th style=\"text-align:left;\">Symbol</th><th style=\"text-align:left;\">Code</th></tr>";
			for( String[] code : c_codes.get(x) )
			{
				l_result += "<tr>";
				l_result += "<td>" + code[0] + "</td>";
				l_result += "<td>" + code[1] + "</td>";
				l_result += "</tr>";
			}
			l_result += "</table><br/>";
			l_result += "</div>";
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
	
	public String getSerial()
	{
		return c_serial;
	}
	
	public void setSerial(String p_serial)
	{
		c_serial = p_serial;
	}
	
	@DefaultHandler
	public Resolution submit()
	{
		System.setProperty("derby.system.home", "/opt/db-derby");
		if( c_serial == null || c_serial.isEmpty() )
			return new ForwardResolution("/WEB-INF/pages/checkcodes.jsp");
		try
		{ 
			c_codes.clear();
			
			//Load derby database driver
			Class.forName("org.apache.derby.jdbc.ClientDriver").newInstance();
			//Create connection to database.  Create database if it doesn't exist.
			Connection l_conn = DriverManager.getConnection(c_dbAddress + c_dbName + ";create=true;" + "user=" + c_dbUser + ";password=" + c_dbPass);
	
			//Create SQL statement object
			PreparedStatement l_query = l_conn.prepareStatement("SELECT question,symbol,code FROM ContestResults WHERE serial=? AND ballotstyle=? ORDER BY question,symbol");
			String l_serialString = c_serial;
			l_serialString = l_serialString.replace("-", "");
			l_serialString = l_serialString.replace(" ", "");
			if (l_serialString.length() == 7)
			{
				//Get all the digits but the first for the serial
				l_query.setInt(1, Integer.parseInt(l_serialString.substring(1)));
				//Get the first digit for the style ID
				l_query.setInt(2, Integer.parseInt(l_serialString.substring(0, 1)));
			}
			else if (l_serialString.length() == 6) 
			{
				//Get all the digits but the first for the serial
				l_query.setInt(1, Integer.parseInt(l_serialString));
				//Guess that it's ward 1
				l_query.setInt(2, 1);
			}
			else
			{
				throw new Exception("Could not find ballot. WebID numbers should be 6 or 7 digits long.");
			}
			
			ResultSet l_results = l_query.executeQuery();
			
			/*if (l_results.next() == false)
			{
				Vector<ResultSet> l_goodResults = new Vector<ResultSet>();
				for (int l_i = 1; l_i < 7; l_i++)
				{
					l_query.setInt(2, l_i);
					l_results = l_query.executeQuery();
					if (l_results.next() == true) 
					{
						l_goodResults.add(l_results);
					}
				}
				
				if (l_goodResults.size() == 1)
				{
					l_results = l_goodResults.get(0);
				}
				else if (l_goodResults.size() > 0)
				{
					throw new Exception("Could not find a unique ballot. Please include your ward number in the front of your WebID number.");
				}
			}*/
			
			
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
					c_question = c_newQuestion;
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
			{
				l_conn.close();
				throw new NoSuchElementException();
			}

			
			//Create SQL statement object
			Statement l_existQuery = l_conn.createStatement();
			
			//Test to see if the table exists, create it if it doesn't
			/* Now, we just try to select something from the table and if an error is thrown
			 * that contains "does not exist" then we try and create it.  Could be done
			 * better with T-SQL, doesn't handle the case where the table exists but 
			 * doesn't have the columns we are expecting.
			 */
			try
			{
				l_existQuery.execute("SELECT COUNT(*) FROM UserTracking");
			}
			catch( SQLException e )
			{
				if( e.getMessage().contains("does not exist") )
				{
					l_existQuery.execute("CREATE TABLE UserTracking ( ipaddress varchar(20), ballotserial varchar(20) )");
				}
			}
			
			PreparedStatement l_trackingQuery = l_conn.prepareStatement("INSERT INTO UserTracking VALUES (?,?)");
			
			String l_ipaddress = c_ctx.getRequest().getRemoteAddr();
			
			l_trackingQuery.setString(1, l_ipaddress);
			l_trackingQuery.setString(2, c_serial);
			
			l_trackingQuery.executeUpdate();
			l_conn.close();
			
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
		} catch(Exception l_e)
		{
			c_error = l_e.getMessage();
		}
		
		return new ForwardResolution("/WEB-INF/pages/checkcodes.jsp");
	}
	
	
	private ActionBeanContext c_ctx;
	public ActionBeanContext getContext() { 
		return c_ctx; 
	}
	public void setContext(ActionBeanContext p_ctx) { 
		this.c_ctx = p_ctx; 
	}
}
