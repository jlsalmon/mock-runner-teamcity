/**
 * Copyright (c) 2012-2013 by European Organization for Nuclear Research (CERN)
 * Author: Justin Salmon <jsalmon@cern.ch>
 *
 * This file is part of the ABI Compatibility Checker (ACC) TeamCity plugin.
 *
 * ACC is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ACC is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with ACC.  If not, see <http://www.gnu.org/licenses/>.
 */

package ch.cern.dss.teamcity.common;

import java.io.*;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.Arrays;

/**
 * Useful IO utilities.
 */
public class Util {

    /**
     * Execute the specified system command.
     *
     * @param command array of commands, needed for pipes to work
     *
     * @return the process output and exit code.
     * @throws InterruptedException
     * @throws java.io.IOException
     */
    public static SystemCommandResult runSystemCommand(String[] command) throws InterruptedException, IOException {
        ProcessBuilder builder = new ProcessBuilder(command);
        builder.redirectErrorStream(true);
        Process process = builder.start();

        BufferedReader reader = new BufferedReader(
                new InputStreamReader(new BufferedInputStream(process.getInputStream())));

        StringBuilder stringBuilder = new StringBuilder();

        String line;
        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line).append("\n");
        }

        try {
            process.waitFor();
        } finally {
            reader.close();
        }

        return new SystemCommandResult(process.exitValue(), stringBuilder.toString());
    }

    /**
     * Read a file on the local filesystem into a string.
     *
     * @param path the path to the file to read
     *
     * @return the contents of the file, as a string.
     * @throws IOException
     */
    public static String readFile(String path) throws IOException {
        FileInputStream stream = new FileInputStream(new File(path));
        try {
            FileChannel channel = stream.getChannel();
            MappedByteBuffer buffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());
            return Charset.defaultCharset().decode(buffer).toString();
        } finally {
            stream.close();
        }
    }

    /**
     * Concatenate two primitive arrays or arbitrary type.
     *
     * @param first  the first array.
     * @param second the second array.
     * @param <T>    the generic type of the two arrays. They must both share the same type.
     *
     * @return the concatenated array.
     */
    public static <T> T[] concatArrays(T[] first, T[] second) {
        T[] result = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }

}
