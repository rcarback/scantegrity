<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>
<%
	request.setAttribute("eTime", System.currentTimeMillis()); 	
%>
<small>
Generated in <fmt:formatNumber type="number" maxFractionDigits="3">
${(eTime-sTime)/1000}</fmt:formatNumber>s
</small>