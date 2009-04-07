package org.scantegrity.emws.action;

import java.io.IOException;

import javax.servlet.http.HttpSession;

import net.sourceforge.stripes.action.ActionBean;
import net.sourceforge.stripes.action.ActionBeanContext;
import net.sourceforge.stripes.validation.ValidationErrors;
import net.sourceforge.stripes.validation.ValidationMethod;
import net.sourceforge.stripes.validation.ValidationState;

public class RestrictedActionBean implements ActionBean {
	private static final String VIEW = "login";
	
	protected ActionBeanContext c_ctx;
	public ActionBeanContext getContext() {
		// TODO Auto-generated method stub
		return c_ctx;
	}

	public void setContext(ActionBeanContext arg0) {
		// TODO Auto-generated method stub
		c_ctx = arg0;
	}
	
	@ValidationMethod(when=ValidationState.ALWAYS)
	public void checkUser(ValidationErrors errors)
	{		
		HttpSession c_sess = getContext().getRequest().getSession(true);
		if( c_sess.getAttribute("username") == null )
		{
			try {
				c_sess.setAttribute("redir", getContext().getSourcePage());
				getContext().getResponse().sendRedirect(VIEW);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	

}
