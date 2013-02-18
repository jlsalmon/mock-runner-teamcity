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

import ch.cern.dss.teamcity.agent.util.FileUtil;
import ch.cern.dss.teamcity.common.MockConstants;
import com.intellij.openapi.util.text.StringUtil;
import jetbrains.buildServer.RunBuildException;
import jetbrains.buildServer.agent.*;
import org.apache.commons.io.FilenameUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.*;

/**
 * AgentBuildRunner has been extended here, instead of implementing CommandLineBuildServiceFactory, because of the need
 * to run multiple builds inside separate threads.
 */
public class MockAgentBuildRunner implements AgentBuildRunner, AgentBuildRunnerInfo {

    /**
     * Create the BuildProcess extension that will be actually run as this build stage.
     *
     * @param build   the currently running build.
     * @param context this build runner's context parameters.
     *
     * @return an instance of our custom BuildProcess extension.
     * @throws RunBuildException
     */
    @NotNull
    @Override
    public BuildProcess createBuildProcess(@NotNull AgentRunningBuild build,
                                           @NotNull BuildRunnerContext context) throws RunBuildException {

        Map<String, String> runnerParameters = context.getRunnerParameters();
        BuildProgressLogger logger = build.getBuildLogger();

        // Append config extension if necessary
        List<String> chrootNamesPattern = FileUtil.splitStringOnWhitespace(runnerParameters.get(MockConstants.CHROOTS));
        for (ListIterator<String> i = chrootNamesPattern.listIterator(); i.hasNext(); ) {
            String chrootName = i.next();
            if (!chrootName.endsWith(".cfg")) {
                i.set(chrootName + ".cfg");
            }
        }

        // Find mock config files
        List<String> mockConfigFiles = FileUtil.findFiles(runnerParameters.get(MockConstants.CONFIG_DIR),
                StringUtil.join(chrootNamesPattern, " "));

        List<String> chrootNames = new ArrayList<String>();
        for (String configFile : mockConfigFiles) {
            chrootNames.add(FilenameUtils.removeExtension(new File(configFile).getName()));
        }
        logger.message("Building in the following chroots: " + Arrays.toString(chrootNames.toArray()));

        // Find srpms
        List<String> srpms = FileUtil.findFiles(runnerParameters.get(MockConstants.SOURCE_RPM_DIR),
                runnerParameters.get(MockConstants.SOURCE_RPMS));
        logger.message("Building packages: " + Arrays.toString(srpms.toArray()));

        // Return custom build process
        return new MockBuildProcess(chrootNames, srpms, runnerParameters, build.getArtifactsPaths(),
                logger);
    }

    /**
     * @return ourselves, we implement the interface here.
     */
    @NotNull
    @Override
    public AgentBuildRunnerInfo getRunnerInfo() {
        return this;
    }

    /**
     * @return the unique type of this agent plugin.
     */
    @NotNull
    @Override
    public String getType() {
        return MockConstants.TYPE;
    }

    /**
     * Perform checks to determine whether this build stage can be run on this agent.
     *
     * @param configuration
     *
     * @return false if the agent cannot run this stage, true otherwise.
     */
    @Override
    public boolean canRun(@NotNull BuildAgentConfiguration configuration) {
        return new File(MockConstants.MOCK_EXECUTABLE).exists();
    }
}
