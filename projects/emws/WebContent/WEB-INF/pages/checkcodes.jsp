<%@taglib prefix="stripes" uri="http://stripes.sourceforge.net/stripes.tld"%>

<stripes:layout-render name="/WEB-INF/layout/default.jsp">
    <stripes:layout-component name="contents">
<h1>Check Codes</h1>

<<<<<<< .mine
<p>Please input your ballot serial below.  Any questions or concerns should be directed <a href="contact">here</a>.</p>
<stripes:form beanclass="org.scantegrity.emws.action.CheckCodesActionBean">
<p>
=======
<p>Please input your ballot serial below:</p>
<stripes:form beanclass="org.scantegrity.emws.action.CheckCodesActionBean">

>>>>>>> .r319
	<label for="name">Serial:</label>
	<input type="text" id="serial" name="serial" value="${actionBean.serial}" />
	<stripes:submit name="submit" value="Submit" />
	<br /><br />
	
	<br />
	
	<stripes:errors/>
	
	${actionBean.result}
	<br/><br/>
	${actionBean.errors}
	<br/>
	
</p>
</stripes:form> 

    </stripes:layout-component>
</stripes:layout-render>
