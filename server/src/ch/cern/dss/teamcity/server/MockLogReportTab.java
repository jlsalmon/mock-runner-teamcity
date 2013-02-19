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
import ch.cern.dss.teamcity.common.Util;
import jetbrains.buildServer.log.Loggers;
import jetbrains.buildServer.serverSide.SBuild;
import jetbrains.buildServer.serverSide.SBuildServer;
import jetbrains.buildServer.web.openapi.PagePlaces;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import jetbrains.buildServer.web.openapi.ViewLogTab;
import org.jetbrains.annotations.NotNull;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MockLogReportTab extends ViewLogTab {

    /**
     * @param pagePlaces       the object with which we register this page extension.
     * @param server           the build server object.
     * @param pluginDescriptor the plugin descriptor used to get base path to JSP files.
     */
    public MockLogReportTab(@NotNull PagePlaces pagePlaces,
                            @NotNull SBuildServer server,
                            @NotNull PluginDescriptor pluginDescriptor) {
        super(MockConstants.TAB_TITLE, MockConstants.TAB_ID, pagePlaces, server);
        setIncludeUrl(pluginDescriptor.getPluginResourcesPath() + "mockLogReport.jsp");
    }

    /**
     * Called when the user clicks on the custom report tab.
     *
     * @param model   the map of data objects that will be passed to the JSP page.
     * @param request the HTTP request object.
     * @param build   the current build.
     */
    @Override
    protected void fillModel(@NotNull Map<String, Object> model,
                             @NotNull HttpServletRequest request,
                             @NotNull SBuild build) {
        try {
            List<MockLogReport> reports = getReports(build);
            model.put("reports", reports);
        } catch (IOException e) {
            Loggers.SERVER.error("Error filling report tab model: " + e.getMessage());
        }
    }

    /**
     * Get the processed reports for each chroot, in a format easily parsable within JSP.
     *
     * @param build the current build being viewed.
     *
     * @return the data structure holding the processed report information.
     * @throws IOException
     */
    private List<MockLogReport> getReports(SBuild build) throws IOException {
        List<MockLogReport> reports = new ArrayList<MockLogReport>();

        for (Map.Entry<String, Map<String, String>> e : getLogFiles(build).entrySet()) {
            reports.add(new MockLogReport(e.getKey(), e.getValue()));
        }

        return reports;
    }

    /**
     * Get a list of all mock build log files within the artifacts of this build.
     *
     * @param build the current build being viewed.
     *
     * @return a map of chroot names : build log files.
     * @throws IOException
     */
    private Map<String, Map<String, String>> getLogFiles(SBuild build) throws IOException {
        Map<String, Map<String, String>> logFiles = new HashMap<String, Map<String, String>>();

        File[] chrootDirectories = build.getArtifactsDirectory().listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return new File(dir, name).isDirectory();
            }
        });

        if (chrootDirectories != null && chrootDirectories.length > 0) {
            for (File chrootDirectory : chrootDirectories) {
                File[] logDirectory = chrootDirectory.listFiles(new FilenameFilter() {
                    @Override
                    public boolean accept(File dir, String name) {
                        return new File(dir, name).isDirectory() && name.equals("logs");
                    }
                });

                if (logDirectory != null && logDirectory.length > 0) {
                    File buildLogFile = new File(logDirectory[0], "build.log");
                    File stateLogFile = new File(logDirectory[0], "state.log");
                    File rootLogFile = new File(logDirectory[0], "root.log");

                    Map<String, String> logFileMap = new HashMap<String, String>();
                    logFileMap.put(buildLogFile.getName(), Util.readFile(buildLogFile.getAbsolutePath()));
                    logFileMap.put(stateLogFile.getName(), Util.readFile(stateLogFile.getAbsolutePath()));
                    logFileMap.put(rootLogFile.getName(), Util.readFile(rootLogFile.getAbsolutePath()));

                    logFiles.put(chrootDirectory.getName(), logFileMap);
                }
            }
        }

        return logFiles;
    }

    /**
     * Perform checks to see whether this page is available to be displayed or not.
     *
     * @param request the HTTP request object.
     * @param build   the current build.
     *
     * @return true if the page is available, false otherwise.
     */
    @Override
    protected boolean isAvailable(@NotNull HttpServletRequest request, @NotNull SBuild build) {
        try {
            return !getLogFiles(build).isEmpty();
        } catch (IOException e) {
            return false;
        }
    }
}
