package org.scantegrity.emws.action;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import net.sourceforge.stripes.action.ActionBean;
import net.sourceforge.stripes.action.ActionBeanContext;
import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.Resolution;

public class BallotCheckActionBean implements ActionBean{
	 private boolean c_ballots = false;
	//Parameters for database connection
	private static final String c_dbAddress = "jdbc:derby:";
	private static final String c_dbName = "CPDB";
	private static final String c_dbUser = "APP";
	private static final String c_dbPass = "";
	 
	 public boolean getBallots()
	 {
		 return c_ballots;
	 }
	 
	 public void setBallots(boolean p_ballots)
	 {
		 c_ballots = p_ballots;
	 }
	 
	 ActionBeanContext c_ctx;

	public ActionBeanContext getContext() {
		// TODO Auto-generated method stub
		return c_ctx;
	}

	public void setContext(ActionBeanContext arg0) {
		// TODO Auto-generated method stub
		c_ctx = arg0;
	}
	 
	 @DefaultHandler
	 public Resolution submit()
	 {
		 try
		 {
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
				ResultSet l_data = l_query.executeQuery("SELECT * FROM ContestResults");
				if( l_data.next() )
					c_ballots = true;
			}
			catch( SQLException e )
			{
				if( e.getMessage().contains("does not exist") )
				{
				}
			}
			l_conn.close();
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
		 return new ForwardResolution("/WEB-INF/pages/ballotcheck.jsp");
	 }
}