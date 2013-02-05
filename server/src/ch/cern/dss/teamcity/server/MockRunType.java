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
import jetbrains.buildServer.serverSide.PropertiesProcessor;
import jetbrains.buildServer.serverSide.RunType;
import jetbrains.buildServer.serverSide.RunTypeRegistry;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class MockRunType extends RunType {

    private final PluginDescriptor pluginDescriptor;

    public MockRunType(@NotNull final RunTypeRegistry runTypeRegistry,
                       @NotNull final PluginDescriptor pluginDescriptor) {
        this.pluginDescriptor = pluginDescriptor;
        runTypeRegistry.registerRunType(this);
    }

    @Override
    public String getType() {
        return MockConstants.TYPE;
    }

    @Override
    public String getDisplayName() {
        return MockConstants.DISPLAY_NAME;
    }

    @Override
    public String getDescription() {
        return MockConstants.DESCRIPTION;
    }

    @Override
    public PropertiesProcessor getRunnerPropertiesProcessor() {
        return new MockPropertiesProcessor();
    }

    @Override
    public String getEditRunnerParamsJspFilePath() {
        return this.pluginDescriptor.getPluginResourcesPath() + "editMockRunner.jsp";
    }

    @Override
    public String getViewRunnerParamsJspFilePath() {
        return this.pluginDescriptor.getPluginResourcesPath() + "viewMockRunner.jsp";
    }

    @Override
    public Map<String, String> getDefaultRunnerProperties() {
        return null;
    }

    /**
     * Return a string that describes the important settings for this build runner. This string will be displayed when
     * viewing the set of build steps for a particular build type.
     *
     * @param parameters the map of parameters that the user specified in the web UI.
     *
     * @return a short, readable string describing this runner's parameters.
     */
    @NotNull
    @Override
    public String describeParameters(@NotNull Map<String, String> parameters) {
        StringBuilder sb = new StringBuilder();

        // TODO

        return sb.toString();
    }
}
