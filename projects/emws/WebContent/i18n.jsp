<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>    
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Internationalization Test</title>
</head>
<body>
<c:choose>
	<c:when test="$param.locale != null">
		<c:set var="locale" value="$param.locale" />
	</c:when>
	<%-- Try to get locale from Header. --%> 
	<c:when test="$param['header.Accept-Language'] != null">
		<c:set var="locale" value="$param['header.Accept-Language']" />	
	</c:when>	
	<c:otherwise>
		<c:set var="locale" value="None" />	
	</c:otherwise>
</c:choose>

Your Browser's Detected Locale: ${locale}
<form action="i18n.jsp" method="get">
<select id="locale">
	<option value="EN">English</option>
	<option value="FR">French</option>
	<option value="ES">Spanish</option>
</select>
<input type="submit" value="submit" />
</form>
</body>
</html>