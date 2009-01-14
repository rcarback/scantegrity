<%@ include file="resources/page.jsp" %>

<tiles:insertDefinition name="myapp.homepage">
	<tiles:putAttribute name="title">Internationalization Test</tiles:putAttribute>
	<tiles:putAttribute name="body">

<c:if test="${param.locale != null}">
		<c:set var="locale" value="${param.locale}" scope="session" />
</c:if>
<c:if test="${param.name != null}">
		<c:set var="name" value="${param.name}" scope="session" />
</c:if>

<h1>Scantegrity Internationalization Test</h1>
The selected name and language, or your Browser's name and default language, 
should appear in the text below:


<fmt:setLocale value="${sessionScope.locale}" />
<fmt:setBundle basename="i18n/Messages" />
<pre>
			<fmt:message key="greeting">
				<fmt:param>${sessionScope.name}</fmt:param>
			</fmt:message>
			<fmt:message key="return"/>
</pre>

<form action="i18n.jsp" method="get">
<fieldset>
<legend>Change Settings</legend>
<ol>
	<li>
    	<label for="name">Name</label>
	  	<input type="text" id="name" name="name" value="${sessionScope.name}" /><br />
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

<p>Your browser's detected locale was: ${header['Accept-Language']}</p>
<p>Your browser's detected name was: ${header['User-Agent']}
<p>The current set locale is: ${locale}
<br /><br />

	</tiles:putAttribute>
</tiles:insertDefinition>


