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

import java.io.File;

import javax.swing.filechooser.FileFilter;

/**
 * A file filter for JFileChooser to open only XML files.
 * 
 * @author Himanshu Neema
 */
public class XmlFileFilter
        extends FileFilter {
    /**
     * Whether the given file is accepted by this filter.
     */
    public boolean accept(File aFile) {
        if (aFile.isDirectory()) {
            return true;
        }

        String filename = aFile.getName();
        int i = filename.lastIndexOf('.');
        if ((i > 0) && (i < (filename.length() - 1))) {
            String extn = filename.substring(i + 1).toLowerCase();
            if (extn.equals("xml")) {
                return true;
            }
        }

        return false;
    }

    /**
     * Filter description.
     */
    public String getDescription() {
        return "XML file";
    }
}
