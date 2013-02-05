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
<%@ taglib prefix="layout" tagdir="/WEB-INF/tags/layout" %>
<%@ taglib prefix="forms" tagdir="/WEB-INF/tags/forms" %>
<%@ taglib prefix="bs" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<jsp:useBean id="constants" class="ch.cern.dss.teamcity.server.MockConstantsBean"/>
<jsp:useBean id="propertiesBean" scope="request" type="jetbrains.buildServer.controllers.BasePropertiesBean"/>

<layout:settingsGroup title="Mock Settings">
    <tr>
        <th><label for="${constants.chrootsKey}">Chroots:</label></th>
        <td>
            <props:textProperty name="${constants.chrootsKey}" className="longField" maxlength="256"/>
            <span class="error" id="error_${constants.chrootsKey}"></span>
            <span class="smallNote">Specify the chroots to use, separated by whitespace
                (e.g. <b>epel-6-i386 epel-6-x86_64</b>). You must have a <b>&lt;chroot_name&gt;.cfg</b> in either the
                default config directory or a custom directory (specified below).</span>
        </td>
    </tr>
    <tr>
        <th><label for="${constants.configDirectoryKey}">Config directory (optional):</label></th>
        <td>
            <props:textProperty name="${constants.configDirectoryKey}" className="longField" maxlength="256"/>
            <span class="error" id="error_${constants.configDirectoryKey}"></span>
            <span class="smallNote">Optionally specify a custom directory in which to search for
                <b>&lt;chroot_name&gt;.cfg</b> files. If not specified, the default will be used (/etc/mock).</span>
        </td>
    </tr>
    <tr>
        <th><label for="${constants.sourceRpmsKey}">Source RPMs:</label></th>
        <td>
            <props:textProperty name="${constants.sourceRpmsKey}" className="longField" maxlength="256"/>
            <span class="error" id="error_${constants.sourceRpmsKey}"></span>
            <span class="smallNote">Specify the source RPMs to be built, separated by whitespace. TeamCity variables
                can be used (e.g. %system.teamcity.build.tempDir%/foo-1.0.src.rpm).</span>
        </td>
    </tr>
</layout:settingsGroup>