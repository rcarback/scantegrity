

package org.scantegrity.emws.action;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.Resolution;

public class ResultsActionBean extends DefaultActionBean{
	
	@DefaultHandler
	public Resolution submit(){
		return new ForwardResolution("/WEB-INF/pages/results.jsp");
	}
}