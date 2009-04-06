<%@taglib prefix="s" uri="http://stripes.sourceforge.net/stripes.tld"%>


<h1>Check Codes</h1>

<s:form beanclass="action.CheckCodesActionBean">

	<label for="name">Serial:</label>
	<input type="text" id="serial" name="serial" value="${acionBean.serial}" />
	<s:submit name="submit" value="Submit" />
	<br /><br />
	
	<br />
	
	<s:errors/>
	
	<br/><br/>
	${actionBean.result}
	<br/><br/>
	${actionBean.errors}
	
</s:form> 
