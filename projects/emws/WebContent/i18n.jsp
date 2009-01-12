<%@ include file="resources/page.jsp" %>

<c:choose>
	<c:when test="${param.locale != null}">
		<c:set var="locale" value="${param.locale}" />
	</c:when>
	<%-- Try to get locale from Header. --%>
	<c:when test="${header['Accept-Language'] != null}">
		<c:set var="locale" value="${header['Accept-Language']}" />	
	</c:when>
	<c:otherwise>
		<c:set var="locale" value="en" />	
	</c:otherwise>  
</c:choose>

<h1>Scantegrity Internationalization Test</h1>
The selected name and language, or your Browser's name and default language, 
should appear in the text below:

<fmt:setLocale value="de" />
<fmt:setBundle basename="i18n/Messages" />
<pre>
			<fmt:message key="greeting"><fmt:param value="Rick"/></fmt:message>
			<fmt:message key="return"/>
</pre>

<form action="i18n.jsp" method="get">
<fieldset>
<legend>Change Settings</legend>
<ol>
	<li>
    	<label for="name">Name</label>
	  	<input type="text" id="name" name="name" value="Rick" /><br />
    </li>
    <li>
    	<label for="locale">Locale</label>
		<select id="locale" name="locale">
			<option value="en">English (en)</option>
			<option value="fr">French (fr)</option>
			<option value="de">German (de)</option>
			<option value="es">Spanish (es)</option>
		</select>
    </li>
    <li>
    	<input type="submit" id="submit" name="submit" value="Submit" />
    </li>
  </ol>
</fieldset>
</form>

<br /><br />
<h3>Other Info</h3>

<p>Your browser's detected locale was: <c:out value="${header['Accept-Language']}" /></p>
<p>Your browser's detected name was: <c:out value="${header['User-Agent']}" />
<p>The current set locale is: <c:out value="${locale}" />
<br /><br />
</body>
</html>