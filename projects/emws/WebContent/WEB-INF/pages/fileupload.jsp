<%@taglib prefix="stripes" uri="http://stripes.sourceforge.net/stripes.tld"%>
<stripes:layout-render name="/WEB-INF/layout/default.jsp">
    <stripes:layout-component name="contents">


<h1>File Upload</h1>

<<<<<<< .mine
<stripes:form beanclass="org.scantegrity.emws.action.FileuploadActionBean">
<div>
=======
<<<<<<< .mine
<stripes:form beanclass="org.scantegrity.emws.action.FileuploadActionBean">
<div>
=======
<stripes:form beanclass="org.scantegrity.emws.action.FileuploadActionBean">

>>>>>>> .r351
>>>>>>> .r319
	<stripes:file name="file" />
	<stripes:submit name="submit" value="Upload" />
	<br /><br />
	
	<br />
	
	<stripes:errors/>
	
	<br/><br/>
	${actionBean.result}
	<br/><br/>
	${actionBean.errors}
	  </div>
</stripes:form> 

    </stripes:layout-component>
</stripes:layout-render>