package org.scantegrity.emws.action;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.HandlesEvent;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;

public class AdminActionBean extends RestrictedActionBean {
	//Parameters for database connection
	private static final String c_dbAddress = "jdbc:derby:";
	private static final String c_dbName = "EMWS";
	private static final String c_dbUser = "APP";
	private static final String c_dbPass = "";
	
	
	@HandlesEvent(value="deleteDatabase")
	public Resolution deleteDatabase()
	{
		try
		{
			//Load derby database driver
			Class.forName("org.apache.derby.jdbc.ClientDriver").newInstance();
			//Create connection to database.  Create database if it doesn't exist.
			Connection l_conn = DriverManager.getConnection(c_dbAddress + c_dbName + ";create=true;" + "user=" + c_dbUser + ";password=" + c_dbPass);
	
			//Create SQL statement object
			Statement l_query = l_conn.createStatement();
		

			l_query.executeUpdate("DELETE FROM ContestResults");
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally{}
		
		return new ForwardResolution("/WEB-INF/pages/admin.jsp");
	}
	
	@DefaultHandler
	public Resolution submit()
	{
		if( !super.checkUser() )
		{
			c_ctx.getRequest().getSession(true).setAttribute("redir", c_ctx.getRequest().getRequestURL().toString().replace("http://","http://"));
			
			String l_url = "http://" + c_ctx.getRequest().getServerName() + c_ctx.getRequest().getContextPath() + "/login";
			return new RedirectResolution(l_url,false);
		}
		return new ForwardResolution("/WEB-INF/pages/admin.jsp");
	}

}
