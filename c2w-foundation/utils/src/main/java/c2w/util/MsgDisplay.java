package c2w.util;

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
