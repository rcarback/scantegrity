<%@ include file="/WEB-INF/jsp/resources/page.jsp" %>
<%@taglib prefix="s" uri="http://stripes.sourceforge.net/stripes.tld"%>


<tiles:insertDefinition name="emws">
    <tiles:putAttribute name="body">
    Not much here yet, but there will be:
    <ol>
        <li>
            <s:link beanclass="action.i18nActionBean">i18n Test</s:link>
        </li>
    </ol>
    </tiles:putAttribute>
</tiles:insertDefinition>