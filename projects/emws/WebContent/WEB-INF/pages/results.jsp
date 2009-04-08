<%@ taglib prefix="stripes" uri="http://stripes.sourceforge.net/stripes.tld" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<stripes:layout-render name="/WEB-INF/layout/default.jsp">
    <stripes:layout-component name="contents">
    <h1>Results</h1>
    <stripes:form beanclass="org.scantegrity.emws.action.ResultsActionBean">
    <p><c:if test="${actionBean.results == \"\" }"> Results for the Mock election should be available around 8pm on Saturday,
    April 11th 2009. Please check back at that time!</c:if>${actionBean.results }</p>
    </stripes:form>
    <br />
    <br />
    <br />
    <br />
    <br />
    <br />
    <br />
    
    </stripes:layout-component>
</stripes:layout-render>