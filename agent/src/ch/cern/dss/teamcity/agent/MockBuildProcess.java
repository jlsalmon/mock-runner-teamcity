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

import jetbrains.buildServer.RunBuildException;
import jetbrains.buildServer.agent.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class MockBuildProcess implements BuildProcess {

    private final AgentRunningBuild agentRunningBuild;
    private final BuildRunnerContext buildRunnerContext;
    private final BuildProgressLogger logger;
    private List<MockRunnable> threads;
    private boolean isInterrupted = false;
    private boolean isFinished = false;

    public MockBuildProcess(@NotNull AgentRunningBuild agentRunningBuild,
                            @NotNull BuildRunnerContext buildRunnerContext,
                            @NotNull BuildProgressLogger logger) {
        this.agentRunningBuild = agentRunningBuild;
        this.buildRunnerContext = buildRunnerContext;
        this.logger = logger;
        this.threads = new ArrayList<MockRunnable>();
    }

    @Override
    public void start() throws RunBuildException {
        logger.message("start()");

        for (int i = 0; i < 3; i++) {
            MockRunnable thread = new MockRunnable(agentRunningBuild, buildRunnerContext, logger);
            thread.start();
            threads.add(thread);
        }
    }

    @Override
    public boolean isInterrupted() {
        logger.message("isInterrupted()");
        return this.isInterrupted;
    }

    @Override
    public boolean isFinished() {
        logger.message("isFinished()");
        return this.isFinished;
    }

    @Override
    public void interrupt() {
        logger.message("interrupt()");
    }

    @Override
    public BuildFinishedStatus waitFor() throws RunBuildException {
        logger.message("waitFor()");

        for (MockRunnable thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                return BuildFinishedStatus.INTERRUPTED;
            }
        }

        return BuildFinishedStatus.FINISHED_SUCCESS;
    }
}