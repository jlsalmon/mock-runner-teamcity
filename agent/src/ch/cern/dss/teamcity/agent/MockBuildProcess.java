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
import jetbrains.buildServer.RunBuildException;
import jetbrains.buildServer.agent.BuildFinishedStatus;
import jetbrains.buildServer.agent.BuildProcess;
import jetbrains.buildServer.agent.BuildProgressLogger;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * Custom build process implementation.
 */
public class MockBuildProcess implements BuildProcess {

    private final List<String> chrootNames;
    private final List<String> srpms;
    private final Map<String, String> runnerParameters;
    private final Map<String, String> environmentVariables;
    private final String artifactPaths;
    private final BuildProgressLogger logger;
    private Map<String, Future<BuildFinishedStatus>> futures;
    private boolean isInterrupted = false;
    private boolean isFinished = false;

    /**
     *
     * @param chrootNames
     * @param srpms
     * @param runnerParameters
     * @param artifactPaths
     * @param environmentVariables
     * @param logger
     */
    public MockBuildProcess(@NotNull List<String> chrootNames,
                            @NotNull List<String> srpms,
                            @NotNull Map<String, String> runnerParameters,
                            @NotNull String artifactPaths,
                            @NotNull Map<String, String> environmentVariables,
                            @NotNull BuildProgressLogger logger) {
        this.chrootNames = chrootNames;
        this.srpms = srpms;
        this.runnerParameters = runnerParameters;
        this.artifactPaths = artifactPaths;
        this.environmentVariables = environmentVariables;
        this.logger = logger;
        this.futures = new HashMap<String, Future<BuildFinishedStatus>>();
    }

    /**
     *
     * @throws RunBuildException
     */
    @Override
    public void start() throws RunBuildException {
        ExecutorService executor = Executors.newFixedThreadPool(MockConstants.MAX_CONCURRENT_MOCK_BUILDS);

        for (String chrootName : chrootNames) {
            Callable<BuildFinishedStatus> thread = new MockCallable(
                    new MockContext(chrootName, srpms, runnerParameters, artifactPaths, environmentVariables), logger);

            Future<BuildFinishedStatus> submit = executor.submit(thread);
            futures.put(chrootName, submit);
        }
    }

    /**
     *
     * @return
     */
    @Override
    public boolean isInterrupted() {
        return this.isInterrupted;
    }

    /**
     *
     * @return
     */
    @Override
    public boolean isFinished() {
        return this.isFinished;
    }

    /**
     *
     */
    @Override
    public void interrupt() {
        this.isInterrupted = true;
    }

    /**
     *
     * @return
     * @throws RunBuildException
     */
    @NotNull
    @Override
    public BuildFinishedStatus waitFor() throws RunBuildException {
        for (Map.Entry<String, Future<BuildFinishedStatus>> entry : futures.entrySet()) {

            try {
                logger.message("Result (" + entry.getKey() + "): " + entry.getValue().get().name());

                if (entry.getValue().get().equals(BuildFinishedStatus.FINISHED_FAILED)) {
                    return BuildFinishedStatus.FINISHED_FAILED;
                }

            } catch (InterruptedException e) {
                logger.exception(e);
                return BuildFinishedStatus.INTERRUPTED;
            } catch (ExecutionException e) {
                logger.exception(e);
                return BuildFinishedStatus.FINISHED_FAILED;
            }
        }

        // All threads apparently finished successfully
        return BuildFinishedStatus.FINISHED_SUCCESS;
    }
}
