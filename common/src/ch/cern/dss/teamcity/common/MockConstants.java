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

package ch.cern.dss.teamcity.common;

public interface MockConstants {
    String TYPE = "mock-runner";
    String DESCRIPTION = "Build RPMs for multiple architectures concurrently using mock";
    String DISPLAY_NAME = "Mock Runner";

    String CHROOTS = "mock.chroots";
    String CONFIG_DIR = "mock.config.dir";
    String SOURCE_RPMS = "mock.srpms";

    String DEFAULT_CONFIG_DIR = "/etc/mock";
}
