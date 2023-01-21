/*
 * Certain portions of this software are Copyright (C) 2006-present
 * Vanderbilt University, Institute for Software Integrated Systems.
 *
 * Certain portions of this software are contributed as a public service by
 * The National Institute of Standards and Technology (NIST) and are not
 * subject to U.S. Copyright.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above Vanderbilt University copyright notice, NIST contribution
 * notice and this permission and disclaimer notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE. THE AUTHORS OR COPYRIGHT HOLDERS SHALL NOT HAVE
 * ANY OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS,
 * OR MODIFICATIONS.
 */

package edu.vanderbilt.vuisis.cpswt.utils;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Utils for Cpswt
 */
public class CpswtUtils {
    /**
     * Portico has this "feature". This is just a copy.
     * @param millis Milliseconds to sleep.
     */
    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        }
        catch(InterruptedException e) {}
    }

    public static void sleepDefault() {
        CpswtUtils.sleep(50);
    }

    /**
     * Returns the {@link File} object of a parameter config file.
     * @param filePath The path to the config file (relative or absolute)
     * @param filePathDir The path to the root directory that contains the config file if filePath param is relative.
     * @return The config {@link File}.
     */
    public static File loadConfigFile(String filePath, String filePathDir) {
        Path configFilePath = getConfigFilePath(filePath, filePathDir);
        return configFilePath.toFile();
    }

    public static Path getConfigFilePath(String filePath, String filePathDir) {
        Path configFilePath = Paths.get(filePath);
        if(!configFilePath.isAbsolute()) {
            configFilePath = Paths.get(filePathDir, filePath);
        }
        return configFilePath;
    }

    public static String getStackTrace(Exception exception) {
        String output = null;
        try(
                StringWriter stringWriter = new StringWriter();
                PrintWriter printWriter = new PrintWriter(stringWriter)
        ) {
            exception.printStackTrace(printWriter);
            output = stringWriter.toString();
        } catch (IOException ioException) {
            output = "Error trying to get stacktrack of exception with message \"" + exception.getMessage() + "\"";
        }

        return output;
    }
}
