<%@taglib prefix="stripes" uri="http://stripes.sourceforge.net/stripes.tld"%>

<stripes:layout-render name="/WEB-INF/layout/default.jsp">
    <stripes:layout-component name="contents">
<h1>Check Codes</h1>

<p>Please input your ballot serial below:</p>
<stripes:form beanclass="action.CheckCodesActionBean">

	<label for="name">Serial:</label>
	<input type="text" id="serial" name="serial" value="${acionBean.serial}" />
	<stripes:submit name="submit" value="Submit" />
	<br /><br />
	
	<br />
	
	<stripes:errors/>
	
	<br/><br/>
	${actionBean.result}
	<br/><br/>
	${actionBean.errors}
	
</stripes:form> 

    </stripes:layout-component>
</stripes:layout-render>
