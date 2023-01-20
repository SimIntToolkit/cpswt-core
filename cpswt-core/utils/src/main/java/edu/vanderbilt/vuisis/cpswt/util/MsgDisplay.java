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

package edu.vanderbilt.vuisis.cpswt.util;

import java.awt.Component;

import javax.swing.JOptionPane;

public class MsgDisplay {

	public static void displayException(Component parentComponent, String dlgTitle, Throwable e, String msg, boolean nonGUIMode) {
		String msgString = (msg == null) ? e.toString() : msg + "\n" + e.toString();
		if(nonGUIMode) {
			System.err.println(msgString);
		} else {
			JOptionPane.showMessageDialog(parentComponent, msgString, dlgTitle, JOptionPane.ERROR_MESSAGE);
		}
		e.printStackTrace();
	}

    public static void displayInformationMessage(Component parentComponent, String dlgTitle, String message, boolean nonGUIMode) {
    	if(nonGUIMode) {
    		System.out.println(message);
    	} else {
	    	JOptionPane.showMessageDialog(parentComponent, message, dlgTitle, JOptionPane.INFORMATION_MESSAGE);
    	}
    }

    public static void displayWarningMessage(Component parentComponent, String dlgTitle, String message, boolean nonGUIMode) {
    	if(nonGUIMode) {
    		System.out.println(message);
    	} else {
	    	JOptionPane.showMessageDialog(parentComponent, message, dlgTitle, JOptionPane.WARNING_MESSAGE);
    	}
    }

    public static void displayErrorMessage(Component parentComponent, String dlgTitle, String message, boolean nonGUIMode) {
    	if(nonGUIMode) {
    		System.err.println(message);
    	} else {
	    	JOptionPane.showMessageDialog(parentComponent, message, dlgTitle, JOptionPane.ERROR_MESSAGE);
    	}
    }
}
