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

package c2w.gui.hla.main;

import gnu.getopt.Getopt;
import gnu.getopt.LongOpt;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;

import c2w.util.MsgDisplay;

/**
 * This is the main application frame starting the C2Windtunnel HLA-Based
 * Simulation.
 * 
 * @author Himanshu Neema
 */
public class C2WSim
        extends JFrame {
    private static final long serialVersionUID = 1L;

    private static Logger log = Logger.getLogger( C2WSim.class.getName() );

    private static final int WIN_SIZEX = 650;
    private static final int WIN_SIZEY = 520;

    private JTabbedPane tabbedPane;

    private C2WMainPanel mainPanel;
    
    public C2WSim(
     String federationName, String fedFile, String scriptFile, String dbName, String logLevel, boolean realtimeMode, String lockFilename, double step, double lookahead, boolean terminateOnCOAFinish, double federationEndTime, long seed4Dur, boolean autoStart
    ) {
        init( federationName, fedFile, scriptFile, dbName, logLevel, realtimeMode, lockFilename, step, lookahead, terminateOnCOAFinish, federationEndTime, seed4Dur, autoStart );
    }

    public void close() { System.exit(0); }

    /**
     * Initialize components
     */
    private void init(
     String federationName, String fedFile, String scriptFile, String dbName, String logLevel, boolean realtimeMode, String lockFilename, double step, double lookahead, boolean terminateOnCOAFinish, double federationEndTime, long seed4Dur, boolean autoStart
    ) {
        setTitle("C2Windtunnel HLA-Based Simulation Tool");
        
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener( new WindowAdapter() {
            public void windowClosing( WindowEvent e ) { mainPanel.confirmYesNoBeforeExit(); }
        } );

        initMainPanel( federationName, fedFile, scriptFile, dbName, logLevel, realtimeMode, lockFilename, step, lookahead, terminateOnCOAFinish, federationEndTime, seed4Dur, autoStart );

        tabbedPane = createTabbedPane();
        getContentPane().add( tabbedPane, BorderLayout.CENTER );
        getContentPane().add(  new MenuBar( this ), BorderLayout.NORTH  );

        setSize( WIN_SIZEX, WIN_SIZEY );
    }

    private void initMainPanel(
     String federationName, String fedFile, String scriptFile, String dbName, String logLevel, boolean realtimeMode, String lockFilename, double step, double lookahead, boolean terminateOnCOAFinish, double federationEndTime, long seed4Dur, boolean autoStart
    ) {
        mainPanel = new C2WMainPanel( federationName, fedFile, scriptFile, dbName, logLevel, realtimeMode, lockFilename, step, lookahead, terminateOnCOAFinish, federationEndTime, seed4Dur, autoStart );
    }

    private JTabbedPane createTabbedPane() {
        JTabbedPane aTabbedPane = new JTabbedPane();
        aTabbedPane.add( "Main", mainPanel );
        aTabbedPane.setPreferredSize(  new Dimension( (int)(WIN_SIZEX * 0.95), (int)(WIN_SIZEY * 0.95) )  );
        return aTabbedPane;
    }

    public void repaint() {
        Component[] widgets = getContentPane().getComponents();
        for( int i = 0; i < widgets.length; i++ ) widgets[i].repaint();
        super.repaint();
    }

    /**
     * Confirm before exit
     */
    public void handleCloseAction() {
    	mainPanel.confirmYesNoBeforeExit();
    }

    public static void main(String[] args) throws Exception {

    	// Himanshu: TODO: Separate arguments that are really optional from those which are required

    	String arguments = ""; for(String arg: args) arguments += "\n" + arg;
    	System.out.println("C2WSim called with arguments:\n" + arguments);
    	
    	LongOpt[] longopts = new LongOpt[8];
    	// 
    	longopts[0] = new LongOpt( "step", LongOpt.REQUIRED_ARGUMENT, null, 's' );
    	longopts[1] = new LongOpt( "lookahead", LongOpt.REQUIRED_ARGUMENT, null, 'a' );
    	longopts[2] = new LongOpt( "lockfile", LongOpt.REQUIRED_ARGUMENT, null, 'l' );
    	longopts[3] = new LongOpt( "realtime", LongOpt.NO_ARGUMENT, null, 'r' );
    	longopts[4] = new LongOpt( "terminateOnCOAFinish", LongOpt.NO_ARGUMENT, null, 't');
    	longopts[5] = new LongOpt( "federationEndTime", LongOpt.REQUIRED_ARGUMENT, null, 'e' );
    	longopts[6] = new LongOpt( "seed4DurRNG", LongOpt.REQUIRED_ARGUMENT, null, 'g');
    	longopts[7] = new LongOpt( "autoStart", LongOpt.NO_ARGUMENT, null, 'u');
    	// 
    	Getopt g = new Getopt( "C2WSim", args, "s:a:l:e:g:rtu", longopts);
    	g.setOpterr(false);

    	boolean realtimeMode = false;
    	String lockFilename = null;
    	double step = 0.2;
    	double lookahead = 0.2;
    	boolean terminateOnCOAFinish = false;
    	boolean autoStart = false;
    	double federationEndTime = -1.0;
    	long seed4DurRNG = 0;
    	String dbName = null;
    	String logLevel = null;
    	
    	int c;
    	while(  ( c = g.getopt() ) != -1  ) {
    		switch( c ) {
    			case 'r':
    				realtimeMode = true;
    				break;
    			case 'l':
    				lockFilename = g.getOptarg();
    				break;
    			case 's':
    				step = Double.parseDouble( g.getOptarg() );
    				break;
    			case 'a':
    				lookahead = Double.parseDouble( g.getOptarg() );
    				break;
    			case 't':
    				terminateOnCOAFinish = true;
    				break;
    			case 'e':
    				federationEndTime = Double.parseDouble( g.getOptarg() );
    				break;
    			case 'g':
    				seed4DurRNG = Long.parseLong( g.getOptarg() );
    				break;
    			case 'u':
    				autoStart = true;
    				break;
    		}
    	}
    	
    	int argPos = g.getOptind();

    	String federationName = null;
    	String fomFilename = null;
    	String scriptFilename = null;
    	
    	while( argPos < args.length ) {
    		if ( federationName == null )      federationName = args[ argPos ];
    		else if ( fomFilename == null )    fomFilename = args[ argPos ];
    		else if ( scriptFilename == null ) scriptFilename = args[ argPos ];
    		else if ( dbName == null ) dbName = args[ argPos ];
    		else if ( logLevel == null ) logLevel = args[ argPos ];
    		++argPos;
    	}
    	
    	if ( fomFilename == null ) {
    		String errMsg = "Usage: C2WSim [--realtime] [--step stepSize] [--lockfile LOCKFILE] [--terminateOnCOAFinish] [--federationEndTime experimentEndTime] [--seed4DurRNG seed4DurRNG] [--autoStart] <federation-name> <fed-file> <script-file> <dbName> <logLevel>";
    		MsgDisplay.displayErrorMessage(null, "C2WSim: Illegal arguments", errMsg, autoStart);
            System.exit( -1 );
        }


        File fom_file = new File( fomFilename );
        if ( !fom_file.exists() ) {
        	String errMsg = "Federation file\n\n    " + fomFilename + "\n\nwas not found";
        	MsgDisplay.displayErrorMessage(null, "C2WSim: Invalid federation file", errMsg, autoStart);
            System.exit( -1 );
        }
        fomFilename = fom_file.getCanonicalPath();

        if ( scriptFilename != null ) {
            File script_file = new File( scriptFilename );
            if ( !script_file.exists() ) {
            	String errMsg = "Script file\n\n    " + scriptFilename + "\n\nwas not found";
            	MsgDisplay.displayErrorMessage(null, "C2WSim: Invalid script file", errMsg, autoStart);
                System.exit( -1 );
            }
            scriptFilename = script_file.getCanonicalPath();
        }

        if ( lockFilename != null ) {
        	File lockFile = new File( lockFilename );
        	if ( lockFile.exists() ) {
        		String errMsg = "Lock file \"" + lockFilename + "\" already exists.\n" +
                				"For the lock file to work properly,\n" +
                				"it must not exist when this program is executed.";
        		MsgDisplay.displayErrorMessage(null, "C2WSim: Lock file already exists", errMsg, autoStart);
                System.exit( -1 );
        	}
        	lockFilename = lockFile.getCanonicalPath();
        }
        
        if (dbName == null || logLevel == null) {
        	MsgDisplay.displayErrorMessage( null, "C2WSim: Incorrect start-up options", "Couldn't find database name and/or logging level", autoStart);
        	System.exit( -1 );
        }

        C2WSim sim = new C2WSim( federationName, fomFilename, scriptFilename, dbName, logLevel, realtimeMode, lockFilename, step, lookahead, terminateOnCOAFinish, federationEndTime, seed4DurRNG, autoStart );
        if(!autoStart) {
        	// Make Federation Manager GUI visible only when NOT running in batch mode
        	sim.setVisible(true);
        }
        sim.mainPanel.startSimulation(autoStart);
    }
}