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

import java.lang.String;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MockLogReport {

    private String chrootName;
    private Map<String, List<Map<Integer, AbstractMap.SimpleEntry<String, String>>>> logs;
    private Integer errors;
    private Integer warnings;

    public MockLogReport(String chrootName, Map<String, String> logFiles) {
        this.chrootName = chrootName;

        logs = new HashMap<String, List<Map<Integer, AbstractMap.SimpleEntry<String, String>>>>();
        for (Map.Entry<String, String> e : logFiles.entrySet()) {
            this.logs.put(e.getKey(), processLogFile(e.getValue()));
        }
    }

    public Map<String, List<Map<Integer,AbstractMap.SimpleEntry<String,String>>>> getLogs() {
        return this.logs;
    }

    public String getName() {
        return this.chrootName;
    }

    public Integer getErrors() {
        return this.errors;
    }

    public Integer getWarnings() {
        return this.warnings;
    }

    /**
     * Parse the given log file and look for errors and warnings.
     *
     * @param fullLog the entire log file to be processed.
     *
     * @return list of mappings to the line number and line text/annotation type.
     */
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

        Pattern errorPattern = Pattern.compile("\\W*error\\W.*", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
        Pattern warningPattern = Pattern.compile("\\W*warning\\W.*", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
        Matcher matcher;

        String[] lines = fullLog.split("\n");
        String line;
        for (Integer i = 0; i < lines.length; i++) {
            line = lines[i];

            matcher = errorPattern.matcher(line);
            if (matcher.matches()) {
                selectedLines.put(i, new AbstractMap.SimpleEntry<String, String>("error", strip(line)));
            }

            matcher = warningPattern.matcher(line);
            if (matcher.matches()) {
                selectedLines.put(i, new AbstractMap.SimpleEntry<String, String>("warning", strip(line)));
            }
        }

        selectedLines = buildContext(selectedLines, lines);
        List<Map<Integer, AbstractMap.SimpleEntry<String, String>>> clusters = clusterLines(selectedLines);

        return clusters;
    }

    /**
     * Retrieve the two lines above and below the selected error/warning lines, to build a context around the error for
     * ease of debugging.
     *
     * @param selectedLines the lines identified as errors/warnings.
     * @param lines         the entire log file split into lines.
     *
     * @return map containing the originally selected lines, plus the context lines, sorted by line number.
     */
    private Map<Integer, AbstractMap.SimpleEntry<String, String>> buildContext(
            Map<Integer, AbstractMap.SimpleEntry<String, String>> selectedLines, String[] lines) {

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
                    contextLines.put(i, new AbstractMap.SimpleEntry<String, String>("context", strip(lines[i])));
                }
            }
        }

        return contextLines;
    }

    /**
     * Cluster each set of error/warning lines with the corresponding context lines into a list of maps.
     *
     * @param lines all the selected error/warning/context lines.
     *
     * @return list of line clusters.
     */
    private List<Map<Integer, AbstractMap.SimpleEntry<String, String>>> clusterLines(
            Map<Integer, AbstractMap.SimpleEntry<String, String>> lines) {

        List<Map<Integer, AbstractMap.SimpleEntry<String, String>>> clusters
                = new ArrayList<Map<Integer, AbstractMap.SimpleEntry<String, String>>>();
        Map<Integer, AbstractMap.SimpleEntry<String, String>> currentCluster = null;
        Integer previous = 0;

        for (Integer i : lines.keySet()) {
            AbstractMap.SimpleEntry<String, String> line = lines.get(i);

            if (previous == i - 1) {
                if (currentCluster != null) {
                    currentCluster.put(i, new AbstractMap.SimpleEntry<String, String>(line.getKey(), line.getValue()));
                }
            } else {
                if (currentCluster != null) {
                    clusters.add(currentCluster);
                }
                currentCluster = new TreeMap<Integer, AbstractMap.SimpleEntry<String, String>>();
                currentCluster.put(i, new AbstractMap.SimpleEntry<String, String>(line.getKey(), line.getValue()));
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
}
