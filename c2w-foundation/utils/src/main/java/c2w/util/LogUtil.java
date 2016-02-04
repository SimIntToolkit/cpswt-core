/*
 * Copyright (c) 2008, Institute for Software Integrated Systems, Vanderbilt University
 * All rights reserved.
 *
 * Permission to use, copy, modify, and distribute this software and its
 * documentation for any purpose, without fee, and without written agreement is
 * hereby granted, provided that the above copyright notice, the following
 * two paragraphs and the author appear in all copies of this software.
 *
 * IN NO EVENT SHALL THE VANDERBILT UNIVERSITY BE LIABLE TO ANY PARTY FOR
 * DIRECT, INDIRECT, SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES ARISING OUT
 * OF THE USE OF THIS SOFTWARE AND ITS DOCUMENTATION, EVEN IF THE VANDERBILT
 * UNIVERSITY HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * THE VANDERBILT UNIVERSITY SPECIFICALLY DISCLAIMS ANY WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY
 * AND FITNESS FOR A PARTICULAR PURPOSE.  THE SOFTWARE PROVIDED HEREUNDER IS
 * ON AN "AS IS" BASIS, AND THE VANDERBILT UNIVERSITY HAS NO OBLIGATION TO
 * PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, OR MODIFICATIONS.
 * 
 * @author Himanshu Neema
 */

package c2w.util;

import java.io.PrintStream;

/**
 * Small utility for logging to a file and to standard out -- can be turned
 * ON/OFF individually.
 * 
 * @author Himanshu Neema
 */
public class LogUtil {

    private static boolean FILE_LOGGING_ON = true;

    private static boolean STD_OUT_ON = true;

    public static void println(PrintStream outStream, String message) {
        if (FILE_LOGGING_ON && outStream != null) {
            outStream.println(message);
        }

        if (STD_OUT_ON) {
            System.out.println(message);
        }
    }

    public static void setFileLoggingOn(boolean bON) {
        FILE_LOGGING_ON = bON;
    }

    public static void setStdOutLoggingOn(boolean bON) {
        STD_OUT_ON = bON;
    }
}
