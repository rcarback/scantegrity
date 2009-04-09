package org.scantegrity.emws.action;

import javax.servlet.http.HttpSession;

import net.sourceforge.stripes.action.ActionBean;
import net.sourceforge.stripes.action.ActionBeanContext;

public class RestrictedActionBean implements ActionBean {
	
	protected ActionBeanContext c_ctx;
	public ActionBeanContext getContext() {
		// TODO Auto-generated method stub
		return c_ctx;
	}

	public void setContext(ActionBeanContext arg0) {
		// TODO Auto-generated method stub
		c_ctx = arg0;
	}
	
	public boolean checkUser()
	{		
		HttpSession c_sess = getContext().getRequest().getSession(true);
		if( c_sess.getAttribute("username") == null )
		{
			return false;
		}
		return true;
	}
	

}
