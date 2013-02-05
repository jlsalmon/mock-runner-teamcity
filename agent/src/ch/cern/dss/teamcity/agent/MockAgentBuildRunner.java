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


package ch.cern.dss.teamcity.agent;

import ch.cern.dss.teamcity.common.MockConstants;
import com.sun.istack.internal.NotNull;
import jetbrains.buildServer.RunBuildException;
import jetbrains.buildServer.agent.*;

public class MockAgentBuildRunner implements AgentBuildRunner, AgentBuildRunnerInfo {
    @Override
    public BuildProcess createBuildProcess(@NotNull AgentRunningBuild agentRunningBuild,
                                           @NotNull BuildRunnerContext buildRunnerContext) throws RunBuildException {

        // Init mock context
        // Run builds in separate threads

        return null;
    }

    @Override
    public AgentBuildRunnerInfo getRunnerInfo() {
        return this;
    }

    @Override
    public String getType() {
        return MockConstants.TYPE;
    }

    @Override
    public boolean canRun(@NotNull BuildAgentConfiguration buildAgentConfiguration) {
        return true;
    }
}
