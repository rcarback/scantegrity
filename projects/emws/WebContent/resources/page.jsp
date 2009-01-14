<%--
@(#)page.jsp
Copyright (C) 2008 Scantegrity Project

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License along
with this program; if not, write to the Free Software Foundation, Inc.,
51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.

-----

This file is included on every jsp page. It includes the tag libraries used
by emws, and some other minor functionality (like SQL and time tracking). 

@author carback1
@version 0.0.2 
@date 12/1/09
--%>

<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%
	//Record the time for measurement purposes, works with: 
	//    /resources/tiles/footer.jsp
	request.setAttribute("sTime", System.currentTimeMillis()); 
%>
    
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %> 
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>

<%-- 
-- Internationalization Support
-- Automatically detect language settings if not already set.
<c:if test="${sessionScope.locale == null}">
	<%
		java.util.Enumeration l_loc = request.getLocales();
		while (l_loc.hasMoreElements()) {
			if (l_loc.nextElement()) {

			}
		}
	%>
</c:if>
--%>

