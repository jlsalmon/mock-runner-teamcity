/**
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
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package ch.cern.dss.teamcity.server;

import ch.cern.dss.teamcity.common.MockConstants;
import org.jetbrains.annotations.NotNull;

public class MockConstantsBean {

    @NotNull
    public String getChrootsKey() {
        return MockConstants.CHROOTS;
    }

    @NotNull
    public String getConfigDirectoryKey() {
        return MockConstants.CONFIG_DIR;
    }

    @NotNull
    public String getSourceRpmDirectoryKey() {
        return MockConstants.SOURCE_RPM_DIR;
    }

    @NotNull
    public String getSourceRpmsKey() {
        return MockConstants.SOURCE_RPMS;
    }

    @NotNull
    public String getRpmMacrosKey() {
        return MockConstants.RPM_MACROS;
    }
}
