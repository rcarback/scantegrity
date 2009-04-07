<%@ taglib prefix="stripes" uri="http://stripes.sourceforge.net/stripes.tld" %>
<stripes:layout-render name="/WEB-INF/layout/default.jsp">
    <stripes:layout-component name="contents">
   <h1>Welcome!</h1>
   <p>The Mock Election Details are:</p>
    <p>
        <b>When:</b> 10am to 2pm on April 11th, 2009<br />
        <b>Where:</b> Takoma Park Community Center Azalea Room<br /> 
        <b>What:</b> City residents and their families and friends are invited to 
        participate in a mock election administered by the City and its Board 
        of Elections. The point of this mock election is to give voters an 
        opportunity to test out and provide feedback to the City on the voting 
        system it will use in the November 2009 municipal elections. 
    </p> 
   <p>Resources and other information:</p>
        <ul>
            <li><a href="${pageContext.request.contextPath}/docs/ArborDayAnnouncement.pdf">
            Announcement in Takoma Park Newsletter [pdf]</a></li>
        </ul>

    </stripes:layout-component>
</stripes:layout-render>