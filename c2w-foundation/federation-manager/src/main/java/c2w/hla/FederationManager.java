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

package c2w.hla;

import hla.rti.ConcurrentAccessAttempted;
import hla.rti.EventRetractionHandle;
import hla.rti.FederateLoggingServiceCalls;
import hla.rti.FederateNotExecutionMember;
import hla.rti.FederationExecutionAlreadyExists;
import hla.rti.InteractionClassNotDefined;
import hla.rti.InteractionClassNotSubscribed;
import hla.rti.LogicalTime;
import hla.rti.RTIinternalError;
import hla.rti.ReceivedInteraction;
import hla.rti.ReflectedAttributes;
import hla.rti.ResignAction;
import hla.rti.RestoreInProgress;
import hla.rti.SaveInProgress;

import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.logging.Logger;

import javax.xml.parsers.SAXParserFactory;

import org.portico.impl.hla13.types.DoubleTime;
import org.portico.impl.hla13.types.HLA13ReflectedAttributes;
import org.portico.lrc.services.object.msg.UpdateAttributes;

import c2w.coa.COAAction;
import c2w.coa.COAAwaitN;
import c2w.coa.COADuration;
import c2w.coa.COAFork;
import c2w.coa.COAGraph;
import c2w.coa.COANode;
import c2w.coa.COAOutcome;
import c2w.coa.COAOutcomeFilter;
import c2w.coa.COAProbabilisticChoice;
import c2w.coa.COARandomDuration;
import c2w.coa.COASyncPt;
import c2w.coa.COANode.NODE_TYPE;
import c2w.gui.coa.COASim;
import c2w.util.RandomWithFixedSeed;
import c2w.util.WeakPropertyChangeSupport;
import c2w.hla.rtievents.IC2WFederationEventsHandler;
import c2w.hla.rtievents.C2WFederationEventsHandler;

/**
 * Model class for the Federation Manager.
 */
public class FederationManager extends SynchronizedFederate {


    private Set<String> _synchronizationLabels = new HashSet<String>();
    private static Logger log = Logger.getLogger(FederationManager.class.getName());

    Set<String> _expectedFederates = new HashSet<String>();
    Set<String> _processedFederates = new HashSet<String>();
    Map<Integer, String> _discoveredFederates = new HashMap<Integer, String>();
    Set<FederateObject> _incompleteFederates = new HashSet<FederateObject>();

    InteractionRoot _injectedInteraction = null;
    double _injectionTime = -1;

    COAGraph _coaGraph = new COAGraph();
    COASim _coaSim = null;
    COANode _node = null;

    private IC2WFederationEventsHandler _federationEventsHandler = null;

    // Cache class and methods for COAOutcomeFilter evaluation
    Class _outcomeFilterEvaluatorClass = null;
    HashMap<COAOutcomeFilter, Method> _outcomeFilter2EvalMethodMap = new HashMap<COAOutcomeFilter, Method>();

    public static final String MODE_REALTIME = "Realtime";
    public static final String MODE_AS_FAST_AS_POSSIBLE = "As Fast As Possible";

    public static final String LOG_LEVEL_NONE = "No Logging";
    public static final String LOG_LEVEL_HIGH = "Only High priority";
    public static final String LOG_LEVEL_MEDIUM = "Up to Medium priority";
    public static final String LOG_LEVEL_LOW = "Up to Low priority";
    public static final String LOG_LEVEL_ALL = "Up to Very Low priority (All logs)";

    public static final String SIM_STATUS_STOPPED = "Not started";
    public static final String SIM_STATUS_RUNNING = "Running";
    public static final String SIM_STATUS_PAUSED = "Paused";

    public static final String PROP_LOGICAL_TIME = "propLogicalTime";
    public static final String PROP_LOG_HIGH_PRIO = "propLogHighPrio";
    public static final String PROP_LOG_MEDIUM_PRIO = "propLogMediumPrio";
    public static final String PROP_LOG_LOW_PRIO = "propLogLowPrio";
    public static final String PROP_LOG_VERY_LOW_PRIO = "propLogVeryLowPrio";
    public static final String PROP_EXTERNAL_SIM_PAUSED = "propExternalSimPaused";

    public static enum LOG_TYPE {
        LOG_TYPE_HIGH("High"), LOG_TYPE_MEDIUM("Medium"), LOG_TYPE_LOW("Low"), LOG_TYPE_VERY_LOW(
                "Very low");
        private String name;

        LOG_TYPE(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    String federation_name;
    String FOM_file_name;
    String lockFilename;
    double step;
    double lookahead;
    boolean _terminateOnCOAFinish;
    boolean _autoStart;
    double _federationEndTime = 0.0;
    Random _rand4Dur = null;
    String _stopScriptFilepath;

    // Himanshu: Enabling Manage Logging to Database
    String _dbName;
    String _logLevel;

    boolean _killingFederation = false;
    String _c2wtRoot;

    Map<Double, List<InteractionRoot>> script_interactions = new TreeMap<Double, List<InteractionRoot>>();
    List<InteractionRoot> initialization_interactions = new ArrayList<InteractionRoot>();

    Set<Double> pause_times = new TreeSet<Double>();

    List<Integer> monitored_interactions = new ArrayList<Integer>();

    Map<Integer, ArrayList<ArrivedInteraction>> _arrived_interactions = new HashMap<Integer, ArrayList<ArrivedInteraction>>();

    boolean running = false;

    boolean paused = false;

    boolean federationAttempted = false;

    boolean timeRegulationEnabled = false;

    boolean timeConstrainedEnabled = false;

    boolean granted = false;

    DoubleTime time = new DoubleTime(0);

    long time_in_millisec = 0;

    long time_diff;

    // Default to run simulation in realtime
    boolean realtime = true;

    // Default to No logging
    private int logLevel = 0;

    // Default to High priority logs
    private int logLevelToSet = 1;

    // Start and end time markers for the main execution loop
    double tMainLoopStartTime = 0.0;
    double tMainLoopEndTime = 0.0;
    boolean executionTimeRecorded = false;


    private final WeakPropertyChangeSupport support = new WeakPropertyChangeSupport(
            this);

    private PrintStream monitor_out;

    //private ConfigXMLHandler scriptXmlHandler;

    FederationManagerState currentState;
    public FederationManagerState getCurrentState() {
        return this.currentState;
    }

    public FederationManager(FederationManagerParameter params) throws Exception {

        this.federation_name = params.FederationName;
        this.FOM_file_name = params.FOMFilename;
        this.lockFilename = params.LockFilename;
        this.step = params.Step;
        this.lookahead = params.Lookahead;
        this._terminateOnCOAFinish = params.TerminateOnCOAFinish;
        this._federationEndTime = params.FederationEndTime;
        this._autoStart = params.AutoStart;

        this._dbName = params.DBName;
        this._logLevel = params.LogLevel;

        // See if fixed see must be used
        if (params.Seed4Dur > 0) {
            RandomWithFixedSeed.init(params.Seed4Dur);
            this._rand4Dur = RandomWithFixedSeed.instance();
        } else {
            this._rand4Dur = new Random();
        }

        this._c2wtRoot = System.getenv("C2WTROOT");
        if (this._c2wtRoot == null) {
            this._c2wtRoot = System.getProperty("user.dir");
        }

        File logDir = new File(this._c2wtRoot, "log");
        if (Files.notExists(logDir.toPath())) {
            logDir.mkdir();
        }

        monitor_out = new PrintStream(new File(logDir, "monitor_" + this.federation_name + ".vec"));

        // Update simulation mode
        realtime = params.RealTimeMode;

        this._federationEventsHandler = new C2WFederationEventsHandler();

        initRTI();

        // read script file
        if (params.ScriptFilename != null) {
            File f = new File(params.ScriptFilename);

            ConfigXMLHandler xmlHandler = new ConfigXMLHandler(this.federation_name, this.getFederateId(), this._rand4Dur, this._logLevel, this.getRTI());

            SAXParserFactory.newInstance().newSAXParser().parse(f, xmlHandler);
            if (xmlHandler.getParseFailed())
                throw new Exception("Config file reading failed.");

            // Script file loaded
            // System.out.println("COAGraph is:\n" + _coaGraph.toString());

            // PREPARE FOR FEDERATES TO JOIN -- INITIALIZE _processedFederates AND ELIMINATE
            // FEDERATES NAMES FROM IT AS THEY JOIN            
            _processedFederates.addAll(xmlHandler.getExpectedFederates());

            _injectedInteraction = xmlHandler.getInjectedInteraction();
            pause_times.addAll(xmlHandler.getPauseTimes());
            monitored_interactions.addAll(xmlHandler.getMonitoredInteractions());
            _expectedFederates.addAll(xmlHandler.getExpectedFederates());
            _coaGraph = xmlHandler.getCoaGraph();
            initialization_interactions.addAll(xmlHandler.getInitInteractions());
            script_interactions = xmlHandler.getScriptInteractions();

            // Remember stop script file's full path
            _stopScriptFilepath = params.ScriptFilename;
            int dotPos = _stopScriptFilepath.lastIndexOf('/');
            if ((dotPos > 0) && (dotPos < (_stopScriptFilepath.length() - 1))) {
                _stopScriptFilepath = _stopScriptFilepath.substring(0, dotPos) + "/Main/stop.sh";
            }
        }

        // Before beginning simulation, initialize COA sequence graph
        _coaGraph.initialize();
        _coaSim = new COASim(_coaGraph);
        boolean coasUsed = _coaGraph.getAllCOANodes().size() > 0;
        // No GUIs are to be shown in batch mode execution
        // Also, No COA Display, if there are no COAs to begin with
        if (!params.AutoStart && coasUsed) {
            _coaSim.setVisible(true);
        }

        this.currentState = FederationManagerState.INITIALIZED;
    }

    public void recordMainExecutionLoopStartTime() {
        System.out.println("Main execution loop of federation started at: " + new Date());
        tMainLoopStartTime = System.currentTimeMillis();
    }

    public void recordMainExecutionLoopEndTime() {
        if (!executionTimeRecorded) {
            System.out.println("Main execution loop of federation stopped at: " + new Date());
            tMainLoopEndTime = System.currentTimeMillis();
            executionTimeRecorded = true;
            double execTimeInSecs = (tMainLoopEndTime - tMainLoopStartTime) / 1000.0;
            if (execTimeInSecs > 0) {
                System.out.println("Total execution time of the main loop: " + execTimeInSecs + " seconds");
            }
        }
    }

    private void initRTI() throws Exception {

        log.info("Waiting for RTI ... ");
        createRTI(SynchronizedFederate.FEDERATION_MANAGER_NAME);
        log.info(" done.\n");

        File fom_file = new File(FOM_file_name);

        log.info("Attempting to create federation \"" + federation_name + "\" ... ");
        try {
            _federationEventsHandler.handleEvent(IC2WFederationEventsHandler.C2W_FEDERATION_EVENTS.CREATING_FEDERATION, federation_name);
            getRTI().createFederationExecution(federation_name, fom_file.toURI().toURL());
            _federationEventsHandler.handleEvent(IC2WFederationEventsHandler.C2W_FEDERATION_EVENTS.FEDERATION_CREATED, federation_name);

        } catch (FederationExecutionAlreadyExists feae) {
            log.info("already ");
        }
        log.info("created.\n");

        joinFederation(federation_name, SynchronizedFederate.FEDERATION_MANAGER_NAME);

        // Himanshu: Enabling Manager Logging to Database
        if (_dbName != null) {
            C2WLogger.init(_dbName);
        }

        // PER THE HLA BOOK, ENABLE TIME-CONSTRAINED FIRST, THEN TIME-REGULATING
        enableTimeConstrained();

        enableTimeRegulation(time.getTime(), lookahead);

        enableAsynchronousDelivery();

        log.info("Registering synchronization points ... ");
        // REGISTER "ReadyToPopulate" SYNCHRONIZATION POINT
        getRTI().registerFederationSynchronizationPoint(ReadyToPopulateSynch, null);
        getRTI().tick();
        while (!_synchronizationLabels.contains(ReadyToPopulateSynch)) {
            Thread.sleep(500);
            getRTI().tick();
        }

        // REGISTER "ReadyToRun" SYNCHRONIZATION POINT
        getRTI().registerFederationSynchronizationPoint(ReadyToRunSynch, null);
        getRTI().tick();
        while (!_synchronizationLabels.contains(ReadyToRunSynch)) {
            Thread.sleep(500);
            getRTI().tick();
        }

        // REGISTER "ReadyToResign" SYNCHRONIZATION POINT
        getRTI().registerFederationSynchronizationPoint(ReadyToResignSynch, null);
        getRTI().tick();
        while (!_synchronizationLabels.contains(ReadyToResignSynch)) {
            Thread.sleep(500);
            getRTI().tick();
        }
        log.info("done.\n");


// Himanshu: Commenting out waiting for lockfiles (using while loops in federates)
//        // LOCKFILE SHOULD BE CREATED *ONLY* AFTER SYNCHRONIZATION POINTS HAVE BEEN REGISTERED
//        if ( lockFilename != null ) {
//            File lockFile = new File( lockFilename );
//            FileOutputStream lockFileStream = new FileOutputStream( lockFile );
//            lockFileStream.close();
//            log.info( "Created lockfile \"" + lockFilename + "\"\n" );
//        }


        SimEnd.publish(getRTI());
        SimPause.publish(getRTI());
        SimResume.publish(getRTI());
    }

    private synchronized void sleepSomeRelative2StepSize() {
        try {
            // Either some other federate is stuck or is running slow
            // --> sleep this thread relative to step size
            // Sleep 10 times (in milliseconds) of step-size (in seconds) if step-size is > 0.1 seconds
            int numMillisecondsToSleep = (int) (step * 10.1);
            if (numMillisecondsToSleep > 0) {
                System.out.println("Other federates running slow, sleeping for " + numMillisecondsToSleep + " milliseconds...");
                Thread.sleep(numMillisecondsToSleep);
            } else {
                // Step-size is too small
                // Sleep 10,000 times (in nanoseconds) of step-size (in seconds) if step-size is < 0.1 seconds
                int numNanosecondsToSleep = (int) (step * 10000);
                if (numNanosecondsToSleep > 0) {
                    System.out.println("Other federates running slow, sleeping for " + numNanosecondsToSleep + " nanoseconds...");
                    Thread.sleep(0, numNanosecondsToSleep);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private synchronized void createFederation() throws Exception {

        federationAttempted = true;

        waitForFederatesToJoin();

        log.info("Waiting for \"" + ReadyToPopulateSynch + "\" ... ");
        readyToPopulate();
        log.info("done.\n");

        log.info("Waiting for \"" + ReadyToRunSynch + "\" ... ");

//        // INITIALLY MAKING SURE THAT ALL SIMULATORS ARE READY_TO_RUN AT TIME 0.0,
//        // THEN PROCEED SIMULATION FROM FEDERATION MANAGER GUI
//        pauseSimulation();
//        fireSimPaused();
//        while(paused) {
//        	Thread.sleep(500);
//        }
//        
//        readyToRun();
//        log.info( "done.\n" );
//        
        // IF FEDERATION MANAGER WAS NOT CONFIGURED TO AUTO-START, THEN
        // PROCEED SIMULATION ONLY WHEN USER PRESSES THE START BUTTON
        if (!_autoStart) {
            pauseSimulation();
            fireSimPaused();
            while (paused) {
                Thread.sleep(500);
            }
        }

        readyToRun();
        log.info("done.\n");

        _federationEventsHandler.handleEvent(IC2WFederationEventsHandler.C2W_FEDERATION_EVENTS.FEDERATION_READY_TO_RUN, federation_name);

        // AS ALL FEDERATES ARE READY TO RUN, WAIT 3 SECS FOR BRITNEY TO INITIALIZE
        Thread.sleep(3000);


        // SEND OUT "INITIALIZATION INTERACTIONS," WHICH ARE SUPPOSED TO BE "RECEIVE" ORDERED.
        for (InteractionRoot interactionRoot : initialization_interactions) {
            System.out.println("Sending \"" + interactionRoot.getSimpleClassName() + "\" interaction.");
            interactionRoot.sendInteraction(getRTI());
        }

        updateLogLevel(logLevelToSet);

        fireTimeUpdate(0.0);

        // set time
        fireTimeUpdate(getRTI().queryFederateTime());
        resetTimeOffset();

        // run rti on a spearate thread
        Thread t = new Thread() {
            public void run() {

                try {
                    recordMainExecutionLoopStartTime();

                    int numStepsExecuted = 0;
                    while (running) {
                        if (realtime) {
                            long sleep_time = time_in_millisec - (time_diff + System.currentTimeMillis());
                            while (sleep_time > 0 && realtime) {
                                long local_sleep_time = sleep_time;
                                if (local_sleep_time > 1000) local_sleep_time = 1000;
                                Thread.sleep(local_sleep_time);
                                sleep_time = time_in_millisec - (time_diff + System.currentTimeMillis());
                            }
                        }

                        if (!paused) {
                            synchronized (getRTI()) {

                                sendScriptInteractions();

                                executeCOAGraph();

                                DoubleTime next_time = new DoubleTime(time.getTime() + step);
                                System.out.println("Current_time = " + time.getTime() + " and step = " + step + " and requested_time = " + next_time.getTime());
                                getRTI().timeAdvanceRequest(next_time);
                                if (realtime) {
                                    time_diff = time_in_millisec - System.currentTimeMillis();
                                }

                                // wait for grant
                                granted = false;
                                int numTicks = 0;
                                boolean stuckWhileWaiting = false;
                                while (!granted && running) {
                                    getRTI().tick();
//                                    numTicks++;
//                                    if(numTicks > 1500 && !_killingFederation) {
//                                    	String warningMsg = "WARNING! C2WT detected a very tight loop among federates.\n\tEither federate lookahead/step-sizes are too small,\n\tor too many messages are being generated in a very small time-period,\n\tor one of the federate had an exception and is stuck.";
//                                    	System.out.println("WARNING! No. of RTI.tick() calls without yet getting a grant = " + numTicks);
//                                    	System.out.println(warningMsg);
//                                    	if(!_autoStart && !stuckWhileWaiting) {
//                                    		// TODO: Add a dialog to not show this dialog again
//                                    		int choice = JOptionPane.showConfirmDialog( null, warningMsg + "\n\nDo you want to terminate simulation?", "Tight-loop detected!", JOptionPane.YES_NO_OPTION );
//                                    		if (choice == JOptionPane.YES_OPTION) {
//                                    			// Himanshu: Here RTI is stuck, so we can't properly resign all federates
//                                    			// Only option is to kill all federates.
//                                    			killEntireFederation();
//                                    		}
//                                    		stuckWhileWaiting = true;
//                                    	}
//                                    }
//
//                                    if(numTicks > 1200) { // Stuck while waiting for others to catch, sleep some
//                                    	sleepSomeRelative2StepSize();
//                                    }
//                                    
//                                    // Sleep within each tick for step-size/1000 seconds
//                                    // int numNanoSecs2SleepWithinEachTick = (int) ((step / 1000.0) * 1000000000.0);
//                                    // if(numNanoSecs2SleepWithinEachTick <= 0) {
//                                    // 	numNanoSecs2SleepWithinEachTick = 1;
//                                    // }
//                                    // if(numNanoSecs2SleepWithinEachTick > 999999) {
//                                    // 	numNanoSecs2SleepWithinEachTick = 999999;
//                                    // }
//                                    // Thread.sleep(0, numNanoSecs2SleepWithinEachTick);
                                }
                                numTicks = 0;

                                numStepsExecuted++;


                                // if we passed next pause time go to pause mode
                                Iterator<Double> it = pause_times.iterator();
                                if (it.hasNext()) {
                                    double pause_time = it.next();
                                    if (time.getTime() > pause_time) {
                                        it.remove();
                                        pauseSimulation();
                                        fireSimPaused();
                                    }
                                }
                            }

                            if (numStepsExecuted == 10) {
                                System.out.println("Federation manager current time = " + time.getTime());
                                numStepsExecuted = 0;
                            }
                        } else {
                            Thread.sleep(10);
                        }

                        // If we have reached federation end time (if it was configured), terminate the federation
                        if (_federationEndTime > 0 && time.getTime() > _federationEndTime) {
                            _federationEventsHandler.handleEvent(IC2WFederationEventsHandler.C2W_FEDERATION_EVENTS.FEDERATION_SIMULATION_FINISHED, federation_name);
                            terminateSimulation();
                        }

                    }
                    _federationEventsHandler.handleEvent(IC2WFederationEventsHandler.C2W_FEDERATION_EVENTS.FEDERATION_SIMULATION_FINISHED, federation_name);
                    prepareForFederatesToResign();

                    log.info("Waiting for \"ReadyToResign\" ... ");
                    readyToResign();
                    log.info("done.\n");

                    waitForFederatesToResign();

                    // destroy federation
                    getRTI().resignFederationExecution(ResignAction.DELETE_OBJECTS);
                    getRTI().destroyFederationExecution(federation_name);
                    destroyRTI();
                    logLevel = 0;

                    // In case some federate is still hanging around
                    killEntireFederation();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        running = true;
        t.start();
    }

    private void waitForFederatesToJoin() throws Exception {
        FederateObject.subscribe_FederateHandle();
        FederateObject.subscribe_FederateType();
        FederateObject.subscribe_FederateHost();
        FederateObject.subscribe(getRTI());

        for (String federateType : _expectedFederates) {
            log.info(
                    "Waiting for \"" + federateType + "\" federate to join ...\n"
            );
        }

        while (_processedFederates.size() != 0) {
            getRTI().tick();
            for (FederateObject federateObject : _incompleteFederates) {
                federateObject.requestUpdate(getRTI());
            }
            Thread.sleep(500);
        }
        log.info("All expected federates have joined the federation.  Proceeding with simulation.\n");

        // PREPARE FOR FEDERATES TO RESIGN NOW -- INITIALIZE _processedFederates AND ELIMINATE
        // FEDERATES NAMES FROM IT AS THEY RESIGN (WHICH COULD BE AT ANY TIME).            
        _processedFederates.addAll(_expectedFederates);
    }

    private void prepareForFederatesToResign() throws Exception {

        for (String federateType : _expectedFederates) {
            log.info(
                    "Waiting for \"" + federateType + "\" federate to resign ...\n"
            );
        }
    }

    private void waitForFederatesToResign() throws Exception {
        while (_processedFederates.size() != 0) {
            getRTI().tick();
            Thread.sleep(500);
        }
        log.info("All federates have resigned the federation.  Simulation terminated.\n");
    }

    private void sendScriptInteractions() {
        double tmin = time.getTime() + lookahead + (lookahead / 10000.0);

        for (double intrtime : script_interactions.keySet()) {
//            System.out.println("Interaction time = " + intrtime);
            List<InteractionRoot> interactionRootList = script_interactions.get(intrtime);
            if (interactionRootList.size() == 0)
                continue;
            if (intrtime < tmin) {

                String interactionClassList = new String();
                boolean notFirst = false;
                for (InteractionRoot interactionRoot : interactionRootList) {
                    if (notFirst) interactionClassList += ", ";
                    notFirst = true;
                    interactionClassList += interactionRoot.getClassName();
                }
                System.out.println(
                        "error: simulation passed scheduled interaction time: " + intrtime + "," + interactionClassList
                );
            } else if (intrtime >= tmin && intrtime < tmin + this.step) {

                List<InteractionRoot> interactionsSent = new ArrayList<InteractionRoot>();
                for (InteractionRoot interactionRoot : interactionRootList) {
                    try {
                        interactionRoot.sendInteraction(getRTI(), intrtime);
                    } catch (Exception e) {
                        System.out.println("Failed to send interaction: " + interactionRoot);
                        e.printStackTrace();
                    }
                    interactionsSent.add(interactionRoot);
                    System.out.println("Sending out the injected interaction");
                }
                interactionRootList.removeAll(interactionsSent);
            }
        }
    }

    private void executeCOAAction(COAAction nodeAction) {
        // Create interaction to be sent
        String interactionClassName = nodeAction.getInteractionClassName();
        InteractionRoot interactionRoot = InteractionRoot.create_interaction(interactionClassName);

        // First check for simulation termination
        if (SimEnd.match(interactionRoot.getClassHandle())) {
            terminateSimulation();
        }


        // It is not a SimEnd interaction, send normally
        interactionRoot.setParameter("sourceFed", getFederateId());
        interactionRoot.setParameter("originFed", getFederateId());
        HashMap<String, String> nameValueParamPairs = nodeAction.getNameValueParamPairs();
        for (String paramName : nameValueParamPairs.keySet()) {
            String paramValue = nameValueParamPairs.get(paramName);
            interactionRoot.setParameter(paramName, paramValue);
        }

        // Create timestamp for the interaction
        double tmin = time.getTime() + lookahead + (lookahead / 10000.0);

        // Send the interaction
        try {
            interactionRoot.sendInteraction(getRTI(), tmin);
        } catch (Exception e) {
            System.out.println("Failed to send interaction: " + interactionRoot);
            e.printStackTrace();
        }
        System.out.println("Successfully sent interaction '" + interactionClassName + "' at time '" + tmin + "'");
    }

    private void executeCOAGraph() {
        HashSet<COANode> currentRootNodes = new HashSet<COANode>(_coaGraph.getCurrentRootNodes());

        // If at any point, there are no COA nodes remaining to execute, and
        // the experiment was configured to terminate when all COA nodes have
        // been executed, terminate the federation.
        if (currentRootNodes.size() == 0 && _terminateOnCOAFinish) {
            terminateSimulation();

        }


        // There may be COA nodes still to be executed, see if some root nodes
        // can be executed.
        boolean nodeExecuted = false;
        for (COANode n : currentRootNodes) {
            if (n.getNodeType() == NODE_TYPE.NODE_SYNC_PT) {
                COASyncPt nodeSyncPt = (COASyncPt) n;
                double timeToReachSyncPt = nodeSyncPt.getSyncTime() - getCurrentTime();
                if (timeToReachSyncPt > 0.0) {
                    // SyncPt is not reached, nothing to be done
                } else {
                    // SyncPt reached, mark executed
                    _coaGraph.markNodeExecuted(n, getCurrentTime());
                    nodeExecuted = true;
                }
            } else if (n.getNodeType() == NODE_TYPE.NODE_AWAITN) {
                COAAwaitN nodeAwaitN = (COAAwaitN) n;
                if (!nodeAwaitN.getIsRequiredNumOfBranchesFinished()) {
                    // AwaitN is not reached, nothing to be done
                } else {
                    // AwaitN reached, mark executed
                    _coaGraph.markNodeExecuted(n, getCurrentTime());
                    nodeExecuted = true;
                }
            } else if (n.getNodeType() == NODE_TYPE.NODE_DURATION) {
                COADuration nodeDuration = (COADuration) n;
                if (!nodeDuration.getIsTimerOn()) {
                    // Start executing duration element
                    nodeDuration.startTimer(getCurrentTime());
                } else {
                    // Check if the duration node has executed
                    if (getCurrentTime() >= nodeDuration.getEndTime()) {
                        // Duration node finished, mark executed
                        _coaGraph.markNodeExecuted(n, getCurrentTime());
                        nodeExecuted = true;
                    }
                }
            } else if (n.getNodeType() == NODE_TYPE.NODE_RANDOM_DURATION) {
                COARandomDuration nodeDuration = (COARandomDuration) n;
                if (!nodeDuration.getIsTimerOn()) {
                    // Start executing duration element
                    nodeDuration.startTimer(getCurrentTime());
                } else {
                    // Check if the duration node has executed
                    if (getCurrentTime() >= nodeDuration.getEndTime()) {
                        // Duration node finished, mark executed
                        _coaGraph.markNodeExecuted(n, getCurrentTime());
                        nodeExecuted = true;
                    }
                }
            } else if (n.getNodeType() == NODE_TYPE.NODE_FORK) {
                COAFork nodeFork = (COAFork) n;
                boolean isDecisionPoint = nodeFork.getIsDecisionPoint(); // TODO: handle decision points

                // As of now Fork is always executed as soon as it is encountered
                _coaGraph.markNodeExecuted(n, getCurrentTime());
                nodeExecuted = true;
            } else if (n.getNodeType() == NODE_TYPE.NODE_PROBABILISTIC_CHOICE) {
                COAProbabilisticChoice nodeProbChoice = (COAProbabilisticChoice) n;
                boolean isDecisionPoint = nodeProbChoice.getIsDecisionPoint(); // TODO: handle decision points

                // As of now Probabilistic Choice is always executed as soon as it is encountered
                _coaGraph.markNodeExecuted(n, getCurrentTime());
                nodeExecuted = true;
            } else if (n.getNodeType() == NODE_TYPE.NODE_ACTION) {
                COAAction nodeAction = (COAAction) n;

                // As of now Action is always executed as soon as it is encountered
                executeCOAAction(nodeAction);
                _coaGraph.markNodeExecuted(n, getCurrentTime());
                nodeExecuted = true;
            } else if (n.getNodeType() == NODE_TYPE.NODE_OUTCOME) {
                COAOutcome nodeOutcome = (COAOutcome) n;
                if (!nodeOutcome.getIsTimerOn()) {
                    // Start executing Outcome element
                    nodeOutcome.startTimer(getCurrentTime());
                } else {
                    boolean outcomeExecutable = checkIfOutcomeExecutableAndUpdateArrivedInteraction(nodeOutcome);
                    if (outcomeExecutable) {
                        _coaGraph.markNodeExecuted(n, getCurrentTime());
                        nodeExecuted = true;
                    }
                }
            } else if (n.getNodeType() == NODE_TYPE.NODE_OUTCOME_FILTER) {
                COAOutcomeFilter outcomeFilter = (COAOutcomeFilter) n;
                COAOutcome outcomeToFilter = outcomeFilter.getOutcome();

                // Update last arrived interaction in the corresponding Outcome node
                checkIfOutcomeExecutableAndUpdateArrivedInteraction(outcomeToFilter);

                boolean filterEvaluation = false;
                if (outcomeToFilter == null) {
                    System.err.println("WARNING! OutcomeFilter not connected to an Outcome: " + outcomeFilter);
                    filterEvaluation = true;
                } else {
                    // Evaluate filter, first get evaluator class
                    if (_outcomeFilterEvaluatorClass == null) {
                        String class2Load = this.getFederationId() + ".COAOutcomeFilterEvaluator";
                        try {
                            _outcomeFilterEvaluatorClass = Class.forName(class2Load);
                            if (_outcomeFilterEvaluatorClass == null) {
                                System.err.println("ERROR! Cannot find evaluator class for OutcomeFilter: " + outcomeFilter);
                            }
                        } catch (Exception e) {
                            System.err.println("Exception caught while evaluating OutcomeFilter: " + outcomeFilter);
                            e.printStackTrace();
                        }
                    }

                    // Now, get the filter method to invoke for evaluation in the evaluator class
                    if (_outcomeFilterEvaluatorClass != null) {
                        Method outcomeFilterEvalMethod = null;
                        if (_outcomeFilter2EvalMethodMap.containsKey(outcomeFilter)) {
                            // Method already exists in the cache, no need to use reflection loading
                            outcomeFilterEvalMethod = _outcomeFilter2EvalMethodMap.get(outcomeFilter);
                        } else {
                            // Method doesn't exist in the cache, use reflection to load it
                            String filterID = outcomeFilter.getUniqueID();
                            filterID = filterID.replaceAll("-", "_");
                            String method2Load = "evaluateFilter_" + filterID;

                            try {
                                outcomeFilterEvalMethod = _outcomeFilterEvaluatorClass.getMethod(method2Load, new Class[]{COAOutcome.class});
                                if (outcomeFilterEvalMethod == null) {
                                    System.err.println("ERROR! Cannot find evaluation method in " + _outcomeFilterEvaluatorClass.getName() + " for OutcomeFilter: " + outcomeFilter);
                                } else {
                                    _outcomeFilter2EvalMethodMap.put(outcomeFilter, outcomeFilterEvalMethod);
                                }
                            } catch (Exception e) {
                                System.err.println("Exception caught while finding evaluation method in " + _outcomeFilterEvaluatorClass.getName() + " for OutcomeFilter: " + outcomeFilter);
                                e.printStackTrace();
                            }
                        }
                        if (outcomeFilterEvalMethod != null) {
                            // Method loaded, now call evaluation function to evaluate OutcomeFilter
                            try {
                                Object retval = outcomeFilterEvalMethod.invoke(null, outcomeToFilter);
                                if (retval instanceof Boolean) {
                                    filterEvaluation = (Boolean) retval;
                                }
                            } catch (Exception e) {
                                System.err.println("Exception caught while evaluating OutcomeFilter: " + outcomeFilter);
                                e.printStackTrace();
                            }
                        } else {
                            System.err.println("ERROR! Failed to load OutcomeFilter evaluation method for OutcomeFilter: " + outcomeFilter);
                        }
                    }
                }

                if (filterEvaluation) {
                    _coaGraph.markNodeExecuted(n, getCurrentTime());
                    nodeExecuted = true;
                }
                // System.out.println("Result of evaluation of filter for outcome: " + outcomeToFilter.getNodeName() + " = " + filterEvaluation + ". Interaction it contained was: " + outcomeToFilter.getLastArrivedInteraction());
            }
        }

        if (nodeExecuted) {
            // Some paths were executed, execute more enabled nodes, if any
            executeCOAGraph();
        }

        // Clear arrived interactions that we no longer need to keep in memory
        clearUnusedArrivedInteractionsForOutcomes();
    }

    private boolean checkIfOutcomeExecutableAndUpdateArrivedInteraction(COAOutcome nodeOutcome) {
        // Check if the outcome can be executed
        boolean outcomeExecutable = false;
        if (_arrived_interactions.keySet().contains(nodeOutcome.getInteractionClassHandle())) {
            ArrayList<ArrivedInteraction> arrivedIntrs = _arrived_interactions.get(nodeOutcome.getInteractionClassHandle());
            if (arrivedIntrs != null) {
                for (ArrivedInteraction arrivedIntr : arrivedIntrs) {
                    if (arrivedIntr.getArrivalTime() > nodeOutcome.getAwaitStartTime()) {
                        // Awaited interaction arrived after outcome was initiated
                        // System.out.println("Setting last arrived interaction in outcome node " + nodeOutcome.getNodeName() + ": " + arrivedIntr.getInteractionRoot());
                        nodeOutcome.setLastArrivedInteraction(arrivedIntr.getInteractionRoot());
                        outcomeExecutable = true;
                    }
                }
                // We shouldn't clear the arrivedIntrs here because all parallel
                // Outcomes could wait for the same interaction
                // Instead we use clearUnusedArrivedInteractionsForOutcomes()
                // at the end of a step of COA execution
            }
        }

        return outcomeExecutable;
    }

    private void clearUnusedArrivedInteractionsForOutcomes() {
        Collection<ArrayList<ArrivedInteraction>> allArrivedIntrLists = _arrived_interactions.values();
        for (ArrayList<ArrivedInteraction> aArrivedIntrList : allArrivedIntrLists) {
            aArrivedIntrList.clear();
        }
    }

    private void resetTimeOffset() {
        time_in_millisec = (long) (time.getTime() * 1000);
        time_diff = time_in_millisec - System.currentTimeMillis();
    }

    public void timeAdvanceGrant(LogicalTime t) {
        fireTimeUpdate(t);
        time_in_millisec = (long) (time.getTime() * 1000);
        granted = true;
    }

    public double getCurrentTime() {
        return time.getTime();
    }

    public boolean isRunning() {
        return running;
    }

    public boolean isPaused() {
        return paused;
    }

    public boolean federationAlreadyAttempted() {
        return federationAttempted;
    }

    public static void configureSimulation(File f) throws Exception {
    }

    public void startSimulation() throws Exception {
        if (!federationAlreadyAttempted()) {
            createFederation();
        }
        paused = false;
    }

    public void pauseSimulation() throws Exception {
        System.out.println("Simulation paused");
        paused = true;
    }

    private void fireSimPaused() {
        support.firePropertyChange(PROP_EXTERNAL_SIM_PAUSED, false, true);
    }

    public void resumeSimulation() throws Exception {
        time_diff = time_in_millisec - System.currentTimeMillis();
        System.out.println("Simulation resumed");
        paused = false;
    }

    public void terminateSimulation() {

        _killingFederation = true;
        recordMainExecutionLoopEndTime();

        synchronized (getRTI()) {
            try {
                SimEnd e = new SimEnd();
                e.set_originFed(getFederateId());
                e.set_sourceFed(getFederateId());
                double tmin = time.getTime() + lookahead;
                e.sendInteraction(getRTI(), tmin);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Wait for 2 seconds for SimEnd to reach others
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Simulation terminated");

        running = false;
        paused = false;

        // Wait for 10 seconds for Simulation to gracefully exit
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // If simulation has still not exited gracefully, run kill command
        killEntireFederation();
    }

    public void killEntireFederation() {
        _killingFederation = true;

        recordMainExecutionLoopEndTime();

        // Kill the entire federation
        String killCommand = "bash -x " + _stopScriptFilepath;
        try {
            System.out.println("Killing federation by executing: " + killCommand + "\n\tIn directory: " + _c2wtRoot);
            Runtime.getRuntime().exec(killCommand, null, new File(_c2wtRoot));
            Runtime.getRuntime().exec(killCommand, null, new File(_c2wtRoot));
            Runtime.getRuntime().exec(killCommand, null, new File(_c2wtRoot));
        } catch (IOException e) {
            System.out.println("Exception while killing the federation");
            e.printStackTrace();
        }
        System.exit(0);
    }

    public void setRealtime(boolean b) {
        System.out.println("Setting simulation to run in realtime as: " + b);
        realtime = b;
        if (realtime)
            resetTimeOffset();
    }

    /**
     * LogLevels 0: No logging 1: High priority logs 2: Up to medium priority
     * logs 3: Up to low priority logs 4: Up to very low priority logs (all
     * logs)
     */
    public void updateLogLevel(int selected) throws Exception {
        logLevelToSet = selected;

        if (getRTI() == null) {
            return;
        }

        if (logLevel == selected) {
            // do nothing
        } else {
            if (logLevel > selected) {
                // Unsubscribe lower log levels
                for (int i = logLevel; i > selected; i--) {
                    unsubscribeLogLevel(i);
                }
            } else {
                // Subscribe lower log levels
                for (int i = logLevel + 1; i <= selected; i++) {
                    subscribeLogLevel(i);
                }
            }
            logLevel = selected;
        }
    }

    private void unsubscribeLogLevel(int level)
            throws Exception, InteractionClassNotDefined, InteractionClassNotSubscribed,
            FederateNotExecutionMember, SaveInProgress, RestoreInProgress,
            RTIinternalError, ConcurrentAccessAttempted {
        if (level > 0) {
            if (level == 1) {
                System.out.println("Unsusbcribing to High priority logs");
                HighPrio.unsubscribe(getRTI());
            } else if (level == 2) {
                System.out.println("Unsusbcribing to Medium priority logs");
                MediumPrio.unsubscribe(getRTI());
            } else if (level == 3) {
                System.out.println("Unsusbcribing to Low priority logs");
                LowPrio.unsubscribe(getRTI());
            } else if (level == 4) {
                System.out.println("Unsusbcribing to Very Low priority logs");
                VeryLowPrio.unsubscribe(getRTI());
            }
        }
    }

    private void subscribeLogLevel(int level)
            throws Exception, InteractionClassNotDefined, FederateNotExecutionMember,
            FederateLoggingServiceCalls, SaveInProgress, RestoreInProgress,
            RTIinternalError, ConcurrentAccessAttempted {
        if (level > 0) {
            if (level == 1) {
                System.out.println("Susbcribing to High priority logs");
                HighPrio.subscribe(getRTI());
            } else if (level == 2) {
                System.out.println("Susbcribing to Medium priority logs");
                MediumPrio.subscribe(getRTI());
            } else if (level == 3) {
                System.out.println("Susbcribing to Low priority logs");
                LowPrio.subscribe(getRTI());
            } else if (level == 4) {
                System.out.println("Susbcribing to Very Low priority logs");
                VeryLowPrio.subscribe(getRTI());
            }
        }
    }

    public synchronized void addPropertyChangeListener(String propertyName,
                                                       PropertyChangeListener li) {
        support.addPropertyChangeListener(propertyName, li);
    }

    private void fireTimeUpdate(LogicalTime t) {
        DoubleTime newTime = new DoubleTime(0);
        newTime.setTo(t);
        fireTimeUpdate(newTime.getTime());
    }

    private void fireTimeUpdate(double t) {
        DoubleTime prevTime = new DoubleTime(0);
        prevTime.setTime(time.getTime());
        time.setTime(t);
        support.firePropertyChange(PROP_LOGICAL_TIME, Double.valueOf(prevTime
                .getTime()), Double.valueOf(time.getTime()));
    }

    @Override
    public void synchronizationPointRegistrationSucceeded(String label) {
        _synchronizationLabels.add(label);
    }

    @Override
    public void discoverObjectInstance(int theObject, int theObjectClass, String objectName) {
        ObjectRoot objectRoot = ObjectRoot.discover(theObjectClass, theObject);
        if (FederateObject.match(theObjectClass)) _incompleteFederates.add((FederateObject) objectRoot);
    }

    @Override
    public void removeObjectInstance(int theObject, byte[] tag) {
        try {
            String federateType = _discoveredFederates.get(theObject);
            boolean registeredFederate = _expectedFederates.contains(federateType);

            if (!registeredFederate) {
                log.info("Unregistered \"" + federateType + "\" federate has resigned the federation.\n");
            } else {
                log.info("\"" + federateType + "\" federate has resigned the federation\n");
                _processedFederates.remove(federateType);
                _federationEventsHandler.handleEvent(IC2WFederationEventsHandler.C2W_FEDERATION_EVENTS.FEDERATE_RESIGNED, federateType);
            }
            return;
        } catch (Exception e) {
            System.out.println("Error while parsing the Federate object: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void reflectAttributeValues(int theObject, ReflectedAttributes theAttributes, byte[] theTag) {

        UpdateAttributes updateAttributes = new UpdateAttributes();
        for (int ix = 0; ix < theAttributes.size(); ++ix) {
            byte[] currentValue = null;
            try {
                currentValue = theAttributes.getValue(ix);
            } catch (Exception e) {
            }
            byte[] newValue = new byte[currentValue.length - 1];
            for (int jx = 0; jx < newValue.length; ++jx) newValue[jx] = currentValue[jx];
            try {
                updateAttributes.addFilteredAttribute(theAttributes.getAttributeHandle(ix), newValue, null);
            } catch (Exception e) {
            }
        }
        theAttributes = new HLA13ReflectedAttributes(updateAttributes.getFilteredAttributes());

        if (!_incompleteFederates.contains(ObjectRoot.getObject(theObject))) return;
        try {
            FederateObject federateObject = (FederateObject) ObjectRoot.reflect(theObject, theAttributes);

            if (
                    federateObject.get_FederateHandle() == 0 ||
                            "".equals(federateObject.get_FederateType()) ||
                            "".equals(federateObject.get_FederateHost())
                    ) return;
            _incompleteFederates.remove(federateObject);

            String federateType = federateObject.get_FederateType();
            _discoveredFederates.put(theObject, federateType);

            boolean registeredFederate = _expectedFederates.contains(federateType);

            if (!registeredFederate) {
                if (federateType.equals(SynchronizedFederate.FEDERATION_MANAGER_NAME)) {
                    log.info("\"" + SynchronizedFederate.FEDERATION_MANAGER_NAME + "\" federate detected (that's me) ... ignored.\n");
                    _discoveredFederates.remove(theObject);
                } else if (federateType.equals("c2wt_mapper_federate")) {
                    log.info("\"C2WT Mapper Federate\" detected (expected) ... ignored.\n");
                    _discoveredFederates.remove(theObject);
                } else {
                    log.info("Unexpected \"" + federateType + "\" federate has joined the federation.\n");
                }
            } else {
                log.info("\"" + federateType + "\" federate has joined the federation\n");
                _processedFederates.remove(federateType);
                _federationEventsHandler.handleEvent(IC2WFederationEventsHandler.C2W_FEDERATION_EVENTS.FEDERATE_JOINED, federateType);
            }
            return;
        } catch (Exception e) {
            System.out.println("Error while parsing the Federate object: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void receiveInteraction(int intrHandle, ReceivedInteraction receivedIntr, byte[] tag) {

        try {
            // First dump the interaction (if monitored)
            dumpInteraction(intrHandle, receivedIntr, null);

            // Now process the interactions as needed
            if (HighPrio.match(intrHandle) && logLevel >= 1) {
                HighPrio hp = new HighPrio(receivedIntr);
                support.firePropertyChange(PROP_LOG_HIGH_PRIO, null, hp);
            } else if (MediumPrio.match(intrHandle) && logLevel >= 2) {
                MediumPrio mp = new MediumPrio(receivedIntr);
                support.firePropertyChange(PROP_LOG_MEDIUM_PRIO, null, mp);
            } else if (LowPrio.match(intrHandle) && logLevel >= 3) {
                LowPrio lp = new LowPrio(receivedIntr);
                support.firePropertyChange(PROP_LOG_LOW_PRIO, null, lp);
            } else if (VeryLowPrio.match(intrHandle) && logLevel >= 4) {
                VeryLowPrio vlp = new VeryLowPrio(receivedIntr);
                support.firePropertyChange(PROP_LOG_VERY_LOW_PRIO, null, vlp);
            }
        } catch (Exception e) {
            System.out.println("Error while parsing the log interaction");
            e.printStackTrace();
        }
    }

    @Override
    public void receiveInteraction(
            int intrHandle,
            ReceivedInteraction receivedIntr,
            byte[] tag,
            LogicalTime intrTime,
            EventRetractionHandle erh
    ) {
        // First dump the interaction (if monitored)
        dumpInteraction(intrHandle, receivedIntr, intrTime);
    }

    private void dumpInteraction(int handle, ReceivedInteraction receivedInteraction, LogicalTime time) {

        try {
            InteractionRoot interactionRoot = InteractionRoot.create_interaction(handle, receivedInteraction);

            if (interactionRoot != null) {
                System.out.println("FederationManager: Received interaction " + interactionRoot);
            } else {
                System.err.println("FederationManager: WARNING! Received interaction with handle " + handle + ".. COULD NOT CREATE PROPER INTERACTION");
                return;
            }

            // Himanshu: Enabling Manager Logging to Database
            DoubleTime intrTimestamp = new DoubleTime();
            if (time != null) {
                intrTimestamp.setTo(time);
            } else {
                // Himanshu: We normally use only TSO updates, so this shouldn't be
                // called, but due to an RTI bug, it seemingly is getting called. So,
                // for now, use the federate's current time or LBTS whichever is greater
                // as the timestamp
                if (getLBTS() >= getCurrentTime()) {
                    intrTimestamp.setTime(getLBTS());
                } else {
                    intrTimestamp.setTime(getCurrentTime());
                }
            }
            createLog(handle, receivedInteraction, intrTimestamp);


            // Inform COA orchestrator of arrival of interaction (for awaited Outcomes, if any)
            updateArrivedInteractions(handle, time, interactionRoot);

            if (!monitored_interactions.contains(handle)) {
                // This is not a monitored interaction
                return;
            }

            /*String str = "time=";
            if (time == null) {
                str += "unknown\t";
            } else {
                DoubleTime t = new DoubleTime();
                t.setTo(time);
                str += String.format("%.4f\t", t.getTime());
            }
            //str += monitored_interactions.get(handle) + "\t";
            for (int j = 0; j < i.size(); ++j) {
                str += new String(i.getValue(j)) + "\t";
            }*/

            double t = this.time.getTime();
            if (time != null) {
                DoubleTime t2 = new DoubleTime();
                t2.setTo(time);
                t = t2.getTime();
            }

            StringBuffer intrBuf = new StringBuffer();
            intrBuf.append("Received " + InteractionRoot.get_class_name(handle) + " interaction at time (" + t + "):\n");
            for (int ix = 0; ix < receivedInteraction.size(); ++ix) {
                int parameterHandle = receivedInteraction.getParameterHandle(ix);
                Object val = interactionRoot.getParameter(parameterHandle);
                if (val != null) {
                    intrBuf.append("\t" + InteractionRoot.get_parameter_name(parameterHandle) + " = " + val.toString() + "\n");
                }
            }
            monitor_out.print(intrBuf.toString());
            log.info(intrBuf.toString());
        } catch (Exception e) {
            System.out.println("Exception while dumping interaction with handle: " + handle);
            e.printStackTrace();
        }
    }

    // This method and arrivalTimes are used by the COA Orchestrator while executing
    // Outcome elements of the COA sequence graph.
    private void updateArrivedInteractions(int handle, LogicalTime time, InteractionRoot receivedIntr) throws Exception {
        ArrayList<ArrivedInteraction> intrArrivalTimeList = null;
        if (!_arrived_interactions.keySet().contains(handle)) {
            intrArrivalTimeList = new ArrayList<ArrivedInteraction>();
            _arrived_interactions.put(handle, intrArrivalTimeList);
        } else {
            intrArrivalTimeList = _arrived_interactions.get(handle);
        }

        DoubleTime arrivalTime = new DoubleTime();
        if (time != null) {
            arrivalTime.setTo(time);
        } else {
            arrivalTime.setTime(getCurrentTime());
        }

        ArrivedInteraction arrivedIntr = new ArrivedInteraction(receivedIntr, arrivalTime.getTime());
        intrArrivalTimeList.add(arrivedIntr);
        // System.out.println("Adding interaction to arrived list: " + receivedIntr);
    }

}
