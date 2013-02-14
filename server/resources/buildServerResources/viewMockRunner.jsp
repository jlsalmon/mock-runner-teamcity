<%--
* Copyright (c) 2012-2013 by European Organization for Nuclear Research (CERN)
* Author: Justin Salmon <jsalmon@cern.ch>
*
* This file is part of the Mock TeamCity plugin.
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Lesser General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public License
* along with this program..  If not, see <http://www.gnu.org/licenses/>.
--%>

<%@ taglib prefix="props" tagdir="/WEB-INF/tags/props" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<jsp:useBean id="propertiesBean" scope="request" type="jetbrains.buildServer.controllers.BasePropertiesBean"/>
<jsp:useBean id="constants" class="ch.cern.dss.teamcity.server.MockConstantsBean"/>

<div class="parameter">
    Chroot names: <strong><props:displayValue name="${constants.chrootsKey}"/></strong>
</div>

<div class="parameter">
    Config directory: <strong><props:displayValue name="${constants.configDirectoryKey}"/></strong>
</div>

<div class="parameter">
    Source RPM directory: <strong><props:displayValue name="${constants.sourceRpmDirectoryKey}"/></strong>
</div>

<div class="parameter">
    Source RPMs: <strong><props:displayValue name="${constants.sourceRpmsKey}"/></strong>
</div>

<div class="parameter">
    Additional RPM macro definitions: <strong><props:displayValue name="${constants.rpmMacrosKey}"
                                                                  emptyValue="<not specified>"/></strong>
</div>