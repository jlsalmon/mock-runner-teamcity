<%--
* Copyright (c) 2012-2013 by European Organization for Nuclear Research (CERN)
* Author: Justin Salmon <jsalmon@cern.ch>
*
* This file is part of the ABI Compatibility Checker (ACC) TeamCity plugin.
*
* ACC is free software: you can redistribute it and/or modify
* it under the terms of the GNU Lesser General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* ACC is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public License
* along with ACC.  If not, see <http://www.gnu.org/licenses/>.
--%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="bs" tagdir="/WEB-INF/tags" %>

<jsp:useBean id="constants" class="ch.cern.dss.teamcity.server.MockConstantsBean"/>
<jsp:useBean id="reports" type="java.util.List" scope="request"/>

<link rel="stylesheet" href="<c:url value="${teamcityPluginResourcesPath}css/custom.css"/>"/>

<script src="${teamcityPluginResourcesPath}js/prettify.js"></script>
<script src="${teamcityPluginResourcesPath}js/bootstrap-collapse.js"></script>
<script>
    jQuery(document).ready(function () {
        prettyPrint();
    });
</script>

<div>
    <c:forEach var="report" items="${reports}">

        <div class="accordion" id="accordion">
            <div class="accordion-group">
                <div class="accordion-heading">
                    <h2 class="accordion-toggle">chroot: ${report.name}</h2>

                    <span class="accordion-toggle">
                        <span class="error-summary">Errors: <strong>${report.errors}</strong></span>
                        <span class="warning-summary">Warnings: <strong>${report.warnings}</strong></span>
                    </span>

                    <a class="accordion-toggle" data-toggle="collapse" data-parent="#accordion" href="#${report.name}">
                        &raquo; Click to view report
                    </a>
                </div>
                <div id="${report.name}" class="accordion-body collapse">
                    <div class="accordion-inner">

                        <c:forEach var="log" items="${report.logs}">

                            <strong>${log.key}</strong>
                            <c:if test="${empty log.value}">
<pre class="prettyprint">Nothing suspicious found</pre>
                            </c:if>

                            <c:forEach var="cluster" items="${log.value}">

                                <c:forEach var="entry" items="${cluster}" begin="0" end="0" step="1">
                                    <c:set var="beginLine" value="${entry.key}"/>
                                </c:forEach>

                                <pre class="prettyprint linenums:${beginLine} lang-bsh"
                                    ><c:forEach var="lineEntry" items="${cluster}">
<span class="${lineEntry.value.key}">${lineEntry.value.value}</span
                                    ></c:forEach
                                ></pre>
                            </c:forEach>

                        </c:forEach>

                    </div>
                </div>
            </div>
        </div>
    </c:forEach>
</div>