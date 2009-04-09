package org.scantegrity.emws.action;

import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import net.sourceforge.stripes.action.ActionBeanContext;
import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.HandlesEvent;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;

import org.scantegrity.emws.action.UserManage;

public class UserAddActionBean extends RestrictedActionBean {
	private static final String VIEW = "/WEB-INF/pages/useradd.jsp";
	
	//Parameters for database connection
	private static final String c_dbAddress = "jdbc:derby:";
	private static final String c_dbName = "EMWS";
	private static final String c_dbUser = "APP";
	private static final String c_dbPass = "";
	
	private List<String> c_allUsers;
	private String c_error = null;
	private String c_target;
	private String c_newUser;
	private String c_pass;
	
	public String getUsername() { return c_newUser; }
	public void setUsername(String p_user) { c_newUser = p_user; }
	public String getPassword() { return c_pass; }
	public void setPassword(String p_pass) { c_pass = p_pass; }
	
	public String getTarget()
	{
		return c_target;
	}
	public void setTarget(String p_target)
	{
		c_target = p_target;
	}
	
	public List<String> getAllUsers()
	{
		return c_allUsers;
	}
	
	public void setAllUsers(List<String> p_users)
	{
		c_allUsers = p_users;
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
	
	@HandlesEvent(value="addUser")
	public Resolution addUser()
	{
		try
		{
			UserManage.addUser(c_newUser, c_pass);
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
		
		return submit();
	}
	
	@HandlesEvent(value="deleteUser")
	public Resolution deleteUser()
	{
		try
		{
			UserManage.removeUser(c_target);
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
		
		return submit();
	}
	
	@DefaultHandler
	public Resolution submit()
	{
		if( !super.checkUser() )
		{
			c_ctx.getRequest().getSession(true).setAttribute("redir", c_ctx.getRequest().getRequestURL().toString());
			
			String l_url = "https://" + c_ctx.getRequest().getServerName() + c_ctx.getRequest().getContextPath() + "/login";
			return new RedirectResolution(l_url,false);
		}
		ArrayList<String> l_users = new ArrayList<String>();
		try
		{
			//Load derby database driver
			Class.forName("org.apache.derby.jdbc.ClientDriver").newInstance();
			//Create connection to database.  Create database if it doesn't exist.
			Connection l_conn = DriverManager.getConnection(c_dbAddress + c_dbName + ";create=true;" + "user=" + c_dbUser + ";password=" + c_dbPass);
	
			//Create SQL statement object
			PreparedStatement l_query = l_conn.prepareStatement("SELECT username FROM users");
			
			ResultSet l_res = l_query.executeQuery();
			while( l_res.next() )
			{
				l_users.add(l_res.getString("username"));
			}
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
		c_allUsers = l_users;
		
		return new ForwardResolution(VIEW);
	}
	
	
}
