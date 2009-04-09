<%@taglib prefix="stripes" uri="http://stripes.sourceforge.net/stripes.tld"%>

<stripes:layout-render name="/WEB-INF/layout/default.jsp">
    <stripes:layout-component name="contents">
<h1>Check Codes</h1>

<<<<<<< .mine
<p>Please input your ballot serial below.  Any questions or concerns should be directed <a href="contact">here</a>.  Please also consider taking a short survey <a href="http://www.surveymonkey.com/s.aspx?sm=dZipmL8roQtNNvKtwqT1iA_3d_3d">here</a> to help us improve your voting experience.</p>
<stripes:form beanclass="org.scantegrity.emws.action.CheckCodesActionBean">
<p>
=======
<<<<<<< .mine
<p>Please input your ballot serial below.  Any questions or concerns should be directed <a href="contact">here</a>.</p>
<stripes:form beanclass="org.scantegrity.emws.action.CheckCodesActionBean">
<p>
=======
<p>Please input your ballot serial below:</p>
<stripes:form beanclass="org.scantegrity.emws.action.CheckCodesActionBean">

>>>>>>> .r351
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
