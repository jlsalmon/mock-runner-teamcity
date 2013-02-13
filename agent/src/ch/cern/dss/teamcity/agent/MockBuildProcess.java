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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class MockBuildProcess implements BuildProcess {

    private final List<String> chrootNames;
    private final String mockConfigDirectory;
    private final List<String> srpms;
    private final BuildProgressLogger logger;
    private List<Future<BuildFinishedStatus>> futures;
    private boolean isInterrupted = false;
    private boolean isFinished = false;

    public MockBuildProcess(@NotNull List<String> chrootNames,
                            @NotNull String mockConfigDirectory,
                            @NotNull List<String> srpms,
                            @NotNull BuildProgressLogger logger) {

        this.chrootNames = chrootNames;
        this.mockConfigDirectory = mockConfigDirectory;
        this.srpms = srpms;
        this.logger = logger;
        this.futures =  new ArrayList<Future<BuildFinishedStatus>>();
    }

    @Override
    public void start() throws RunBuildException {
        ExecutorService executor = Executors.newFixedThreadPool(MockConstants.MAX_CONCURRENT_MOCK_BUILDS);

        for (String chrootName : chrootNames) {
            Callable<BuildFinishedStatus> thread = new MockCallable(
                    new MockContext(chrootName, mockConfigDirectory, srpms), logger);

            Future<BuildFinishedStatus> submit = executor.submit(thread);
            futures.add(submit);
        }
    }

    @Override
    public boolean isInterrupted() {
        return this.isInterrupted;
    }

    @Override
    public boolean isFinished() {
        return this.isFinished;
    }

    @Override
    public void interrupt() {
        this.isInterrupted = true;
    }

    @Override
    public BuildFinishedStatus waitFor() throws RunBuildException {
        for (Future<BuildFinishedStatus> future : futures) {

            try {
                logger.message("Future: " + future.get().name());

                if (future.get().equals(BuildFinishedStatus.FINISHED_FAILED)) {
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
