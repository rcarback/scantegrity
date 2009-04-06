<%@taglib prefix="s" uri="http://stripes.sourceforge.net/stripes.tld"%>



<h1>File Upload Test</h1>

<s:form beanclass="action.FileuploadActionBean">

	<s:file name="file" />
	<s:submit name="submit" value="Upload" />
	<br /><br />
	
	<br />
	
	<s:errors/>
	
	<br/><br/>
	${actionBean.result}
	<br/><br/>
	${actionBean.errors}
	    
</s:form> 
