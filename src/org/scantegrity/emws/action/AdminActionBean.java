package org.scantegrity.emws.action;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;

public class AdminActionBean extends RestrictedActionBean {
	
	@DefaultHandler
	public Resolution submit()
	{
		if( !super.checkUser() )
		{
			c_ctx.getRequest().getSession(true).setAttribute("redir", c_ctx.getRequest().getRequestURL().toString());
			
			String l_url = "https://" + c_ctx.getRequest().getServerName() + c_ctx.getRequest().getContextPath() + "/login";
			return new RedirectResolution(l_url,false);
		}
		return new ForwardResolution("/WEB-INF/pages/admin.jsp");
	}

}
