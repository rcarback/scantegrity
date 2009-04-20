<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="stripes" uri="http://stripes.sourceforge.net/stripes.tld" %>

<stripes:layout-definition>
    <?xml version="1.0" encoding="utf-8"?>
    <!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" 
                            "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
    <html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en">
        <head>
			<title>Takoma Park Arbor Day Mock Election</title>
			<meta http-equiv="content-language" content="en">
			<meta name="keywords" 
			     content="Scantegrity, Takoma Park, Maryland, Election, Voting" />
			<meta name="description" 
			         content="The city of Takoma Park, Maryland hosts a mock 
			                 election on Arbor Day, April 11, 2009, to test the 
			                 Scantegrity voting system." />
			<meta http-equiv="Content-Type" 
			                 content="application/xhtml+xml; charset=utf-8" />
			<link rel="stylesheet" type="text/css" 
			         href="${pageContext.request.contextPath}/styles/adme.css" />            

            <stripes:layout-component name="html_head"/>
        </head>
        <body>
			<div id="skipLinks"><a href="#content">Skip Header</a></div>
			<div id="page">        
	            <stripes:layout-component name="header">
	                <jsp:include page="/WEB-INF/layout/default/header.jsp"/>
	            </stripes:layout-component>
	            <div id="middle">
	                <div id="content">
	                <!-- Hidden for now!
					<ul id="languageLinks">
					   <li><a href="${pageContext.request.contextPath}/espanol">Espa&#241;ol</a></li>
					
					   <li><a href="${pageContext.request.contextPath}/index">English</a></li>
					</ul>
                    // -->
                    <stripes:layout-component name="contents"/>
		            <ul id="navigation">
		
		               <li><a href="${pageContext.request.contextPath}/index">Home</a></li>
		               <li><a href="${pageContext.request.contextPath}/ballotcheck">Ballot Check</a></li>
		               <li><a href="${pageContext.request.contextPath}/results">Results</a></li>
                       <li><a href="${pageContext.request.contextPath}/getfiles">Downloads</a></li>
		               <li><a href="${pageContext.request.contextPath}/faq">FAQ</a></li>
                       <li><a href="${pageContext.request.contextPath}/contact">Contact Us</a></li>
                       <li><a href="${pageContext.request.contextPath}/admin">Admin</a></li>
		               <li><a href="http://takomaparkmd.gov/">Takoma Park Website</a></li>
		            </ul>
		            </div>
		         </div>
                 <stripes:layout-component name="footer">
                    <jsp:include page="/WEB-INF/layout/default/footer.jsp"/>
                 </stripes:layout-component>                
            </div>
			<ul id="validate">
			   <li><a href="http://validator.w3.org/check?uri=referer">Valid XHTML</a></li>
			   <li><a href="http://jigsaw.w3.org/css-validator/check/referer">Valid CSS</a></li>
			</ul>            
        </body>
    </html>
</stripes:layout-definition>

