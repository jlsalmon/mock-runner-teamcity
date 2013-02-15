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
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MockLogReportTab extends ViewLogTab {

    public MockLogReportTab(@NotNull PagePlaces pagePlaces,
                            @NotNull SBuildServer server,
                            @NotNull PluginDescriptor pluginDescriptor) {
        super(MockConstants.TAB_TITLE, MockConstants.TAB_ID, pagePlaces, server);
        setIncludeUrl(pluginDescriptor.getPluginResourcesPath() + "mockLogReport.jsp");
    }

    @Override
    protected void fillModel(@NotNull Map<String, Object> model,
                             @NotNull HttpServletRequest request,
                             @NotNull SBuild build) {
        try {
            model.put("reports", getReports(build));
        } catch (IOException e) {
            Loggers.SERVER.error("Error filling report tab model: " + e.getMessage());
        }
    }

    private Map<String, List<Map<Integer, AbstractMap.SimpleEntry<String, String>>>> getReports(SBuild build)
            throws IOException {
        Map<String, List<Map<Integer, AbstractMap.SimpleEntry<String, String>>>> reports
                = new HashMap<String, List<Map<Integer, AbstractMap.SimpleEntry<String, String>>>>();

        for (Map.Entry<String, String> e : getLogFiles(build).entrySet()) {
            reports.put(e.getKey(), processLogFile(e.getValue()));
        }

        return reports;
    }

    private Map<String, String> getLogFiles(SBuild build) throws IOException {
        Map<String, String> logFiles = new HashMap<String, String>();

        File[] chrootDirectories = build.getArtifactsDirectory().listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return new File(dir, name).isDirectory();
            }
        });

        for (File chrootDirectory : chrootDirectories) {
            File[] logDirectory = chrootDirectory.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return new File(dir, name).isDirectory() && name.equals("logs");
                }
            });

            if (logDirectory != null && logDirectory.length > 0) {
                File buildLogFile = new File(logDirectory[0], "build.log");
                logFiles.put(buildLogFile.getAbsolutePath(), Util.readFile(buildLogFile.getAbsolutePath()));
            }
        }

        return logFiles;
    }

    private List<Map<Integer, AbstractMap.SimpleEntry<String, String>>> processLogFile(String fullLog) {
        Map<Integer, AbstractMap.SimpleEntry<String, String>> selectedLines
                = new TreeMap<Integer, AbstractMap.SimpleEntry<String, String>>();

        if (fullLog.length() == 0) {
            List<Map<Integer, AbstractMap.SimpleEntry<String, String>>> emptyList
                    = new ArrayList<Map<Integer, AbstractMap.SimpleEntry<String, String>>>();
            Map<Integer, AbstractMap.SimpleEntry<String, String>> emptyMap
                    = new HashMap<Integer, AbstractMap.SimpleEntry<String, String>>();

            emptyMap.put(1, new AbstractMap.SimpleEntry<String, String>("warning", "Log file is empty"));
            emptyList.add(emptyMap);
            return emptyList;
        }

        Pattern errorPattern = Pattern.compile("\\W*error\\W.*", Pattern.CASE_INSENSITIVE|Pattern.MULTILINE);
        Pattern warningPattern = Pattern.compile("\\W*warning\\W.*", Pattern.CASE_INSENSITIVE|Pattern.MULTILINE);
        Matcher matcher;

        String[] lines = fullLog.split("\n");
        String line;
        for (Integer i = 0; i < lines.length; i++) {
            line = lines[i];

            matcher = errorPattern.matcher(line);
            if (matcher.matches()) {
                Loggers.SERVER.info("Matched error: " + i + ": " + line);
                selectedLines.put(i, new AbstractMap.SimpleEntry<String, String>("error", strip(line)));
            }

            matcher = warningPattern.matcher(line);
            if (matcher.matches()) {
                Loggers.SERVER.info("Matched warning: " + i + ": " + line);
                selectedLines.put(i, new AbstractMap.SimpleEntry<String, String>("warning", strip(line)));
            }
        }

        selectedLines = buildContext(selectedLines, lines);
        List<Map<Integer, AbstractMap.SimpleEntry<String, String>>> clusters = clusterLines(selectedLines);

        return clusters;
    }

    private Map<Integer, AbstractMap.SimpleEntry<String, String>> buildContext(
            Map<Integer, AbstractMap.SimpleEntry<String, String>> selectedLines, String[] lines) {

        Loggers.SERVER.info("Building context");
        Map<Integer, AbstractMap.SimpleEntry<String, String>> contextLines
                = new TreeMap<Integer, AbstractMap.SimpleEntry<String, String>>();
        contextLines.putAll(selectedLines);

        for (Integer lineNo : selectedLines.keySet()) {
            Integer start = lineNo - 2;
            Integer end = lineNo + 2;

            if (start < 0) start = 0;
            if (end >= lines.length) end = lines.length - 1;

            for (int i = start; i < end + 1; i++) {
                if (!selectedLines.containsKey(i)) {
                    Loggers.SERVER.info("Context line: " + i + ": " + lines[i]);
                    contextLines.put(i, new AbstractMap.SimpleEntry<String, String>("context", strip(lines[i])));
                }
            }
        }

        return contextLines;
    }

    private List<Map<Integer, AbstractMap.SimpleEntry<String, String>>> clusterLines(
            Map<Integer, AbstractMap.SimpleEntry<String, String>> lines) {

        Loggers.SERVER.info("Clustering lines");
        List<Map<Integer, AbstractMap.SimpleEntry<String, String>>> clusters
                = new ArrayList<Map<Integer, AbstractMap.SimpleEntry<String, String>>>();
        Map<Integer, AbstractMap.SimpleEntry<String, String>> currentCluster = null;
        Integer previous = 0;

        for (Integer i : lines.keySet()) {
            AbstractMap.SimpleEntry<String, String> line = lines.get(i);

            if (previous == i - 1) {
                if (currentCluster != null) {
                    currentCluster.put(i, new AbstractMap.SimpleEntry<String, String>(line.getKey(), line.getValue()));
                    Loggers.SERVER.info("clustering line " + i + ": " + line.getKey() + " -> " + line.getValue());
                }
            } else {
                if (currentCluster != null) {
                    clusters.add(currentCluster);
                }
                currentCluster = new TreeMap<Integer, AbstractMap.SimpleEntry<String, String>>();
                currentCluster.put(i, new AbstractMap.SimpleEntry<String, String>(line.getKey(), line.getValue()));
                Loggers.SERVER.info("clustering line " + i + ": " + line.getKey() + " -> " + line.getValue());
            }
            previous = i;
        }

        if (currentCluster != null) {
            clusters.add(currentCluster);
        }

        return clusters;
    }

    private String strip(String string) {
        return string.replace("\n", "");
    }

    @Override
    protected boolean isAvailable(@NotNull HttpServletRequest request, @NotNull SBuild build) {
        try {
            return !getLogFiles(build).isEmpty();
        } catch (IOException e) {
            return false;
        }
    }
}
