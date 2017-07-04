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

package org.cpswt.hla;

import org.cpswt.utils.CpswtDefaults;
import hla.rti.*;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.*;
import java.util.ArrayList;
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

import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import org.apache.commons.lang3.mutable.MutableInt;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cpswt.config.*;
import org.portico.impl.hla13.types.DoubleTime;
import org.portico.impl.hla13.types.HLA13ReflectedAttributes;
import org.portico.lrc.services.object.msg.UpdateAttributes;


import org.cpswt.util.RandomWithFixedSeed;
import org.cpswt.hla.rtievents.IC2WFederationEventsHandler;
import org.cpswt.hla.rtievents.C2WFederationEventsHandler;

/**
 * Model class for the Federation Manager.
 */
public class FederationManager extends SynchronizedFederate implements COAExecutorEventListener {

    private Set<String> _synchronizationLabels = new HashSet<String>();
    private static Logger LOG = LogManager.getLogger(FederationManager.class);

    private final Map<String, MutableInt> onlineExpectedFederateTypes;
    private final Map<String, MutableInt> onlineLateJoinerFederateTypes;


    private Set<String> expectedFederateTypes = new HashSet<String>();
    private Set<String> _processedFederates = new HashSet<String>();
    private Map<Integer, String> _discoveredFederates = new HashMap<Integer, String>();

    private ObservableSet<ObjectRoot> rtiDiscoveredObjectsObs = FXCollections.observableSet();

    private Set<ObjectRoot> rtiDiscoveredFederateObjects = new HashSet<>();
    private Set<ObjectRoot> rtiDiscoveredCpswtFederateInfoObjects = new HashSet<>();

    InteractionRoot _injectedInteraction = null;
    double _injectionTime = -1;

    private IC2WFederationEventsHandler _federationEventsHandler = null;

//    public static final String PROP_LOGICAL_TIME = "propLogicalTime";
//    public static final String PROP_LOG_HIGH_PRIO = "propLogHighPrio";
//    public static final String PROP_LOG_MEDIUM_PRIO = "propLogMediumPrio";
//    public static final String PROP_LOG_LOW_PRIO = "propLogLowPrio";
//    public static final String PROP_LOG_VERY_LOW_PRIO = "propLogVeryLowPrio";
//    public static final String PROP_EXTERNAL_SIM_PAUSED = "propExternalSimPaused";

    /*
        ==============================================================================================================
        FederationManager fields
    */

    // THIS IS ONLY FOR DEVELOPMENT PURPOSES
    // MUST BE true IN PRODUCTION
    boolean useSyncPoints = true;

    /**
     * The name of the Federation
     */
    private String federationId;

    /**
     * Indicates if real time mode is on.
     */
    private boolean realTimeMode = true;

    /**
     * Indicates if federation manager terminates when COA finishes.
     */
    private boolean terminateOnCOAFinish;

    /**
     * Project root directory
     */
    private String rootDir;

    /**
     * Experiment config
     */
    private ExperimentConfig experimentConfig;

    private ExperimentConfig workingExperimentConfig;


    private boolean _autoStart;
    private double _federationEndTime = 0.0;
    private Random _rand4Dur = null;
    private String _stopScriptFilepath;

    private String _logLevel;

    private boolean _killingFederation = false;


    private Map<Double, List<InteractionRoot>> script_interactions = new TreeMap<Double, List<InteractionRoot>>();
    private List<InteractionRoot> initialization_interactions = new ArrayList<InteractionRoot>();

    private Set<Double> pause_times = new TreeSet<Double>();

    private List<Integer> monitored_interactions = new ArrayList<Integer>();

    private boolean running = false;

    private boolean paused = false;

    private boolean federationAttempted = false;

    boolean timeRegulationEnabled = false;

    boolean timeConstrainedEnabled = false;

    private boolean granted = false;

    private DoubleTime time = new DoubleTime(0);

    private long time_in_millisec = 0;

    private long time_diff;


    // Default to No logging
    private int logLevel = 0;

    // Default to High priority logs
    private int logLevelToSet = 1;

    // Start and end time markers for the main execution loop
    private double tMainLoopStartTime = 0.0;
    private double tMainLoopEndTime = 0.0;
    private boolean executionTimeRecorded = false;

    // expose autostart
    public boolean getAutoStart() {
        return this._autoStart;
    }

    private COAExecutor coaExecutor;

    //private PrintStream monitor_out;

//    @Override
//    public boolean setFederateState(FederateState newState) {
//        if(this.federateState.CanTransitionTo(newState)) {
//            // TODO: transition to new state
//
//
//
//            return true;
//        }
//        return false;
//    }
    /* ============================================================================================================== */

    /**
     * Creates a @FederationManager instance.
     *
     * @param params The passed parameters to initialize the federation manager. See {@link FederationManagerConfig}.
     * @throws Exception I have no idea why we have this here. Especially pure Exception type...
     */
    public FederationManager(FederationManagerConfig params) throws Exception {
        super(new FederateConfig(
                SynchronizedFederate.FEDERATION_MANAGER_NAME,
                params.federationId,
                false,
                params.lookAhead,
                params.stepSize));

        LOG.trace("FederationManager initialization start");

        // record config parameters
        this.federationId = params.federationId;
        // this.federateRTIInitWaitTime = params.federateRTIInitWaitTimeMs;
        this._autoStart = params.autoStart;
        this._federationEndTime = params.federationEndTime;
        this.realTimeMode = params.realTimeMode;
        this.terminateOnCOAFinish = params.terminateOnCOAFinish;

        // set project's root directory
        this.rootDir = System.getenv(CpswtDefaults.RootPathEnvVarKey);
        if (this.rootDir == null) {
            LOG.trace("There was no {} environment variable set. Setting RootDir to \"user.dir\" system property.", CpswtDefaults.RootPathEnvVarKey);
            this.rootDir = System.getProperty("user.dir");
        }

        // build fed file URL
        Path fedFilePath = Paths.get(params.fedFile);
        URL fedFileURL;
        if (fedFilePath.isAbsolute()) {
            fedFileURL = fedFilePath.toUri().toURL();
        } else {
            fedFileURL = Paths.get(this.rootDir, params.fedFile).toUri().toURL();
        }
        LOG.trace("FOM file should be found under {}", fedFileURL);

        // TODO: eliminate loglevels @see #18
        this._logLevel = "NORMAL";

        // See if fixed see must be used
        // TODO: WHAT IS THIS SHIT
        int seed4Dur = 0;
        if (seed4Dur > 0) {
            RandomWithFixedSeed.init(seed4Dur);
            this._rand4Dur = RandomWithFixedSeed.instance();
        } else {
            this._rand4Dur = new Random();
        }

        // TODO: logging #18 , #13, #7
        Path logDirPath = Paths.get(this.rootDir, "log"); // params.LogDir);
        File logDir = logDirPath.toFile();
        if (Files.notExists(logDirPath)) {
            LOG.warn("Log directory not present. Creating {}...", logDirPath);
            logDir.mkdir();
        }

        // TODO: Prepare core to be able to stream events when needed #27
        this._federationEventsHandler = new C2WFederationEventsHandler();

        Path experimentConfigFilePath = Paths.get(params.experimentConfig);
        File experimentConfigFile;
        if (experimentConfigFilePath.isAbsolute()) {
            experimentConfigFile = experimentConfigFilePath.toFile();
        } else {
            experimentConfigFile = Paths.get(this.rootDir, params.experimentConfig).toFile();
        }

        LOG.trace("Loading experiment config file {}", experimentConfigFilePath);
        this.experimentConfig = ConfigParser.parseConfig(experimentConfigFile, ExperimentConfig.class);

        this.onlineExpectedFederateTypes = new HashMap<>(this.experimentConfig.expectedFederates.size());
        this.onlineLateJoinerFederateTypes = new HashMap<>(this.experimentConfig.lateJoinerFederates.size());

        this.workingExperimentConfig = new ExperimentConfig();
        this.workingExperimentConfig.federateTypesAllowed = new ArrayList<>(this.experimentConfig.federateTypesAllowed);
        this.workingExperimentConfig.expectedFederates = new ArrayList<>(this.experimentConfig.expectedFederates);
        this.workingExperimentConfig.lateJoinerFederates = new ArrayList<>(this.experimentConfig.lateJoinerFederates);


        this.rtiDiscoveredObjectsObs.addListener((SetChangeListener<ObjectRoot>) change -> {
            if (change.wasAdded()) {
                Thread thread = new Thread() {
                    public void run() {
                        boolean done = false;
                        while (!done) {
                            try {
                                synchronized (getLRC()) {
                                    ObjectRoot obj = change.getElementAdded();

                                    if(obj instanceof CpswtFederateInfoObject) {
                                        LOG.trace("RTIDiscoveredObjects listener :: add :: Requesting update for CpswtFederateInfoObject {}", ((CpswtFederateInfoObject) obj).get_FederateId());
                                    } else if (obj instanceof FederateObject) {
                                        LOG.trace("RTIDiscoveredObjects listener :: add :: Requesting update for FederateObject {}", ((FederateObject) obj).get_FederateId());
                                    }
                                    else {
                                        LOG.trace("RTIDiscoveredObjects listener :: add :: Requesting update for ObjectRoot with handle {}", obj.getObjectHandle());
                                    }

                                    obj.requestUpdate(getLRC());
                                    done = true;

                                    getLRC().tick();
                                    rtiDiscoveredObjectsObs.remove(obj);
                                }
                            } catch (Exception e) {
                                try {
                                    Thread.sleep(SynchronizedFederate.internalThreadWaitTimeMs);
                                } catch (Exception e2) {
                                }
                            }
                        }
                    }
                };
                thread.start();
            }
        });



        this.initializeLRC(fedFileURL);

        /*

        // read script file
        if (params.experimentConfig != null) {
            File f = Paths.get(this.rootDir, params.experimentConfig).toFile();

            ConfigXMLHandler xmlHandler = new ConfigXMLHandler(this.federationId, this.getFederateId(), this._rand4Dur, this._logLevel, this.getLRC());

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
            expectedFederateTypes.addAll(xmlHandler.getExpectedFederates());

            this.coaExecutor = new COAExecutor(this.getFederationId(), this.getFederateId(), super.getLookAhead(), this.terminateOnCOAFinish, getLRC());
            this.coaExecutor.setCoaExecutorEventListener(this);
            coaExecutor.setCOAGraph(xmlHandler.getCoaGraph());

            initialization_interactions.addAll(xmlHandler.getInitInteractions());
            script_interactions = xmlHandler.getScriptInteractions();

            // Remember stop script file's full path
            // TODO: stop script remove, @see #25
            _stopScriptFilepath = Paths.get(this.rootDir, "Main/stop.sh").toString(); // params.StopScriptPath).toString();
        }

        */

        // Before beginning simulation, initialize COA sequence graph
        // coaExecutor.initializeCOAGraph();
        this.setFederateState(FederateState.INITIALIZED);

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

    private void initializeLRC(URL fedFileURL) throws Exception {

        LOG.trace("Creating Local RTI component ...");
        super.createLRC();
        LOG.debug("Local RTI component created successfully.");

        LOG.trace("[{}] Attempting to create federation \"{}\"...", super.getFederateId(), federationId);
        try {
            _federationEventsHandler.handleEvent(IC2WFederationEventsHandler.C2W_FEDERATION_EVENTS.CREATING_FEDERATION, federationId);
            super.lrc.createFederationExecution(this.federationId, fedFileURL);
            _federationEventsHandler.handleEvent(IC2WFederationEventsHandler.C2W_FEDERATION_EVENTS.FEDERATION_CREATED, federationId);
        } catch (FederationExecutionAlreadyExists feae) {
            LOG.error("Federation with the name of \"{}\" already exists.", federationId);
            return;
        }
        LOG.debug("Federation \"{}\" created successfully.", this.federationId);

        // join the federation
        super.joinFederation();

        // PER THE HLA BOOK, ENABLE TIME-CONSTRAINED FIRST, THEN TIME-REGULATING
        super.enableTimeConstrained();

        super.enableTimeRegulation(time.getTime(), super.getLookAhead());

        super.enableAsynchronousDelivery();

        LOG.trace("Registering synchronization point: {}", SynchronizationPoints.ReadyToPopulate);

        if (useSyncPoints) {
            // REGISTER "ReadyToPopulate" SYNCHRONIZATION POINT
            super.lrc.registerFederationSynchronizationPoint(SynchronizationPoints.ReadyToPopulate, null);
            super.lrc.tick();
            while (!_synchronizationLabels.contains(SynchronizationPoints.ReadyToPopulate)) {
                Thread.sleep(SynchronizedFederate.internalThreadWaitTimeMs);
                super.lrc.tick();
            }
            LOG.debug("Synchronization point \"{}\" registered successfully.", SynchronizationPoints.ReadyToPopulate);

            LOG.trace("Registering synchronization point: {}", SynchronizationPoints.ReadyToRun);
            super.lrc.registerFederationSynchronizationPoint(SynchronizationPoints.ReadyToRun, null);
            super.lrc.tick();
            while (!_synchronizationLabels.contains(SynchronizationPoints.ReadyToRun)) {
                Thread.sleep(SynchronizedFederate.internalThreadWaitTimeMs);
                super.lrc.tick();
            }
            LOG.debug("Synchronization point \"{}\" registered successfully.", SynchronizationPoints.ReadyToRun);

            LOG.trace("Registering synchronization point: {}", SynchronizationPoints.ReadyToResign);
            super.lrc.registerFederationSynchronizationPoint(SynchronizationPoints.ReadyToResign, null);
            super.lrc.tick();
            while (!_synchronizationLabels.contains(SynchronizationPoints.ReadyToResign)) {
                Thread.sleep(SynchronizedFederate.internalThreadWaitTimeMs);
                super.lrc.tick();
            }
            LOG.debug("Synchronization point \"{}\" registered successfully.", SynchronizationPoints.ReadyToResign);

        }

        super.publishFederateInfoObject();

        // ---------- subscribe for CpswtFederateInfo
        CpswtFederateInfoObject.subscribe_FederateId();
        CpswtFederateInfoObject.subscribe_FederateType();
        CpswtFederateInfoObject.subscribe_IsLateJoiner();
        CpswtFederateInfoObject.subscribe(super.lrc);

        // TODO: overview this later
        SimEnd.publish(getLRC());
        SimPause.publish(getLRC());
        SimResume.publish(getLRC());
    }

    /**
     * Start the federation run - federation that has been created already in the initializeLRC() -- TEMP comment, needs to be refactored
     *
     * @throws Exception
     */
    private synchronized void startFederationRun() throws Exception {
        federationAttempted = true;

        this.waitExpectedFederatesToJoin();

        if (useSyncPoints) {
            LOG.trace("Waiting for \"{}\"...", SynchronizationPoints.ReadyToPopulate);
            super.readyToPopulate();
            LOG.trace("{} done.", SynchronizationPoints.ReadyToPopulate);
        }

        // IF FEDERATION MANAGER WAS NOT CONFIGURED TO AUTO-START, THEN
        // PROCEED SIMULATION ONLY WHEN USER PRESSES THE START BUTTON
        if (!_autoStart) {
            this.pauseSimulation();
            while (this.paused) {
                Thread.sleep(SynchronizedFederate.internalThreadWaitTimeMs);
            }
        }

        if (useSyncPoints) {
            LOG.trace("Waiting for \"{}\"...", SynchronizationPoints.ReadyToRun);
            super.readyToRun();
            LOG.trace("{} done.", SynchronizationPoints.ReadyToRun);
        }

        _federationEventsHandler.handleEvent(IC2WFederationEventsHandler.C2W_FEDERATION_EVENTS.FEDERATION_READY_TO_RUN, federationId);

        // SEND OUT "INITIALIZATION INTERACTIONS," WHICH ARE SUPPOSED TO BE "RECEIVE" ORDERED.
        for (InteractionRoot interactionRoot : initialization_interactions) {
            LOG.trace("Sending {} interaction.", interactionRoot.getSimpleClassName());
            interactionRoot.sendInteraction(getLRC());
        }

        // TODO: eliminate this #18
        updateLogLevel(logLevelToSet);

        fireTimeUpdate(0.0);

        // set time
        fireTimeUpdate(getLRC().queryFederateTime());
        resetTimeOffset();

        double step = super.getStepSize();

        // run rti on a spearate thread
        Thread t = new Thread() {
            public void run() {

                try {
                    recordMainExecutionLoopStartTime();

                    int numStepsExecuted = 0;
                    while (running) {
                        if (realTimeMode) {
                            long sleep_time = time_in_millisec - (time_diff + System.currentTimeMillis());
                            while (sleep_time > 0 && realTimeMode) {
                                long local_sleep_time = sleep_time;
                                if (local_sleep_time > 1000) local_sleep_time = 1000;
                                Thread.sleep(local_sleep_time);
                                sleep_time = time_in_millisec - (time_diff + System.currentTimeMillis());
                            }
                        }

                        if (!paused) {
                            synchronized (getLRC()) {

                                sendScriptInteractions();

                                // coaExecutor.executeCOAGraph();

                                DoubleTime next_time = new DoubleTime(time.getTime() + step);
                                System.out.println("Current_time = " + time.getTime() + " and step = " + step + " and requested_time = " + next_time.getTime());
                                getLRC().timeAdvanceRequest(next_time);
                                if (realTimeMode) {
                                    time_diff = time_in_millisec - System.currentTimeMillis();
                                }

                                // wait for grant
                                granted = false;
                                int numTicks = 0;
                                boolean stuckWhileWaiting = false;
                                while (!granted && running) {
                                    getLRC().tick();
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
                            _federationEventsHandler.handleEvent(IC2WFederationEventsHandler.C2W_FEDERATION_EVENTS.FEDERATION_SIMULATION_FINISHED, federationId);
                            terminateSimulation();
                        }

                    }
                    _federationEventsHandler.handleEvent(IC2WFederationEventsHandler.C2W_FEDERATION_EVENTS.FEDERATION_SIMULATION_FINISHED, federationId);
                    prepareForFederatesToResign();

                    LOG.info("Waiting for \"ReadyToResign\" ... ");
                    readyToResign();
                    LOG.info("done.\n");

                    waitForFederatesToResign();

                    // destroy federation
                    getLRC().resignFederationExecution(ResignAction.DELETE_OBJECTS);
                    getLRC().destroyFederationExecution(federationId);
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

    private void waitExpectedFederatesToJoin() throws Exception {
        FederateObject.subscribe_FederateHandle();
        FederateObject.subscribe_FederateType();
        FederateObject.subscribe_FederateHost();
        FederateObject.subscribe(getLRC());

        for (FederateJoinInfo federateInfo : this.experimentConfig.expectedFederates) {
            LOG.trace("Waiting for {} federate{} of type \"{}\" to join", federateInfo.count, federateInfo.count == 1 ? "" : "s", federateInfo.federateType);
        }

        int numOfFedsToWaitFor = this.workingExperimentConfig.expectedFederateItemsCount();

        // requestUpdate is called on the federateObject which causes an RTI callback
        // that is handled in << this.reflectAttributeValues >>
        // TODO: mutex or some sort of synchronization
        while (numOfFedsToWaitFor > 0) {
            try {
                for (ObjectRoot objectRoot : this.rtiDiscoveredFederateObjects) {
                    objectRoot.requestUpdate(this.lrc);
                }
                for (ObjectRoot objectRoot : this.rtiDiscoveredCpswtFederateInfoObjects) {
                    objectRoot.requestUpdate(this.lrc);
                }

                super.lrc.tick();
                Thread.sleep(SynchronizedFederate.internalThreadWaitTimeMs);
                numOfFedsToWaitFor = this.workingExperimentConfig.expectedFederateItemsCount();
            }
            catch(Exception e) {
                Thread.sleep(SynchronizedFederate.internalThreadWaitTimeMs);
            }
        }
        LOG.debug("All expected federates have joined the federation. Proceeding with the simulation...");

        // PREPARE FOR FEDERATES TO RESIGN NOW -- INITIALIZE _processedFederates AND ELIMINATE
        // FEDERATES NAMES FROM IT AS THEY RESIGN (WHICH COULD BE AT ANY TIME).            
        _processedFederates.addAll(expectedFederateTypes);
    }

    private void prepareForFederatesToResign() throws Exception {

        for (String federateType : expectedFederateTypes) {
            LOG.info(
                    "Waiting for \"" + federateType + "\" federate to resign ...\n"
            );
        }
    }

    private void waitForFederatesToResign() throws Exception {
        while (_processedFederates.size() != 0) {
            getLRC().tick();
            Thread.sleep(SynchronizedFederate.internalThreadWaitTimeMs);
        }
        LOG.info("All federates have resigned the federation.  Simulation terminated.\n");
    }

    private void sendScriptInteractions() {
        double tmin = time.getTime() + super.getLookAhead() + (super.getLookAhead() / 10000.0);

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
            } else if (intrtime >= tmin && intrtime < tmin + super.getStepSize()) {

                List<InteractionRoot> interactionsSent = new ArrayList<InteractionRoot>();
                for (InteractionRoot interactionRoot : interactionRootList) {
                    try {
                        interactionRoot.sendInteraction(getLRC(), intrtime);
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
        this.setFederateState(FederateState.STARTING);
        if (!federationAlreadyAttempted()) {
            this.startFederationRun();
        }
        paused = false;
        this.setFederateState(FederateState.RUNNING);
    }

    public void pauseSimulation() throws Exception {
        LOG.debug("Pausing simulation");
        this.paused = true;
        this.setFederateState(FederateState.PAUSED);
    }

    public void resumeSimulation() throws Exception {
        time_diff = time_in_millisec - System.currentTimeMillis();
        LOG.debug("Resuming simulation");
        this.paused = false;
        this.setFederateState(FederateState.RESUMED);
    }

    public void terminateSimulation() {

        _killingFederation = true;
        recordMainExecutionLoopEndTime();

        synchronized (super.lrc) {
            try {
                SimEnd e = new SimEnd();
                e.set_originFed(getFederateId());
                e.set_sourceFed(getFederateId());
                double tmin = time.getTime() + super.getLookAhead();
                e.sendInteraction(getLRC(), tmin);
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
        this.setFederateState(FederateState.TERMINATED);

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
            System.out.println("Killing federation by executing: " + killCommand + "\n\tIn directory: " + rootDir);
            Runtime.getRuntime().exec(killCommand, null, new File(rootDir));
            Runtime.getRuntime().exec(killCommand, null, new File(rootDir));
            Runtime.getRuntime().exec(killCommand, null, new File(rootDir));
        } catch (IOException e) {
            System.out.println("Exception while killing the federation");
            e.printStackTrace();
        }
        System.exit(0);
    }

    public void setRealTimeMode(boolean newRealTimeMode) {
        LOG.debug("Setting simulation to run in realTimeMode as: {}", newRealTimeMode);
        this.realTimeMode = newRealTimeMode;
        if (this.realTimeMode)
            this.resetTimeOffset();
    }

    /**
     * LogLevels 0: No logging 1: High priority logs 2: Up to medium priority
     * logs 3: Up to low priority logs 4: Up to very low priority logs (all
     * logs)
     */
    public void updateLogLevel(int selected) throws Exception {
        logLevelToSet = selected;

        if (getLRC() == null) {
            return;
        }

        if (logLevel == selected) {
            // do nothing
        } else {
            if (logLevel > selected) {
                // Unsubscribe lower LOG levels
                for (int i = logLevel; i > selected; i--) {
                    unsubscribeLogLevel(i);
                }
            } else {
                // Subscribe lower LOG levels
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
                HighPrio.unsubscribe(getLRC());
            } else if (level == 2) {
                System.out.println("Unsusbcribing to Medium priority logs");
                MediumPrio.unsubscribe(getLRC());
            } else if (level == 3) {
                System.out.println("Unsusbcribing to Low priority logs");
                LowPrio.unsubscribe(getLRC());
            } else if (level == 4) {
                System.out.println("Unsusbcribing to Very Low priority logs");
                VeryLowPrio.unsubscribe(getLRC());
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
                HighPrio.subscribe(getLRC());
            } else if (level == 2) {
                System.out.println("Susbcribing to Medium priority logs");
                MediumPrio.subscribe(getLRC());
            } else if (level == 3) {
                System.out.println("Susbcribing to Low priority logs");
                LowPrio.subscribe(getLRC());
            } else if (level == 4) {
                System.out.println("Susbcribing to Very Low priority logs");
                VeryLowPrio.subscribe(getLRC());
            }
        }
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
    }

    @Override
    public void synchronizationPointRegistrationSucceeded(String label) {
        _synchronizationLabels.add(label);
    }


    @Override
    public void discoverObjectInstance(int objectHandle, int objectClassHandle, String objectName) {
        ObjectRoot objectRoot = ObjectRoot.discover(objectClassHandle, objectHandle);
        if (FederateObject.match(objectClassHandle)) {
            FederateObject federateObject = (FederateObject) objectRoot;
            LOG.debug("New federateObject discovered");
            this.rtiDiscoveredFederateObjects.add(federateObject);
        } else if (CpswtFederateInfoObject.match(objectClassHandle)) {
            CpswtFederateInfoObject infoObject = (CpswtFederateInfoObject) objectRoot;
            LOG.debug("New CpswtFederateInfoObject discovered");
            this.rtiDiscoveredCpswtFederateInfoObjects.add(infoObject);
        }

        this.rtiDiscoveredObjectsObs.add(objectRoot);
    }

    @Override
    public void removeObjectInstance(int theObject, byte[] tag) {
        try {
            String federateType = _discoveredFederates.get(theObject);
            boolean registeredFederate = expectedFederateTypes.contains(federateType);

            if (!registeredFederate) {
                LOG.info("Unregistered \"" + federateType + "\" federate has resigned the federation.\n");
            } else {
                LOG.info("\"" + federateType + "\" federate has resigned the federation\n");
                _processedFederates.remove(federateType);
                _federationEventsHandler.handleEvent(IC2WFederationEventsHandler.C2W_FEDERATION_EVENTS.FEDERATE_RESIGNED, federateType);
            }
            return;
        } catch (Exception e) {
            System.out.println("Error while parsing the Federate object: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * reflectAttributeValues handles federates that join the federation
     *
     * @param objectHandle        handle (RTI assigned) of the object class instance to which
     *                            the attribute reflections are to be applied
     * @param reflectedAttributes data structure containing attribute reflections for
     *                            the object class instance, i.e. new values for the instance's attributes.
     * @param theTag
     */
    @Override
    public void reflectAttributeValues(int objectHandle, ReflectedAttributes reflectedAttributes, byte[] theTag) {

        // check if current objectHandle has been already discovered by the RTI
        ObjectRoot o = ObjectRoot.getObject(objectHandle);
        if (!this.rtiDiscoveredFederateObjects.contains(o) && !this.rtiDiscoveredCpswtFederateInfoObjects.contains(o))
            return;

        // for user-defined objects there's no need to change to length-1
        // I have no idea why this piece of code was here
        if(!(o instanceof CpswtFederateInfoObject)) {
            // transform attributes
            UpdateAttributes updateAttributes = new UpdateAttributes();
            try {
                for (int ix = 0; ix < reflectedAttributes.size(); ++ix) {
                    byte[] currentValue = reflectedAttributes.getValue(ix);

                    byte[] newValue = new byte[currentValue.length - 1];
                    for (int jx = 0; jx < newValue.length; ++jx) {
                        newValue[jx] = currentValue[jx];
                    }

                    updateAttributes.addFilteredAttribute(reflectedAttributes.getAttributeHandle(ix), newValue, null);
                }
            } catch (ArrayIndexOutOfBounds aioob) {
                LOG.error("Error while processing reflectedAttributes. {}", aioob);
            }

            reflectedAttributes = new HLA13ReflectedAttributes(updateAttributes.getFilteredAttributes());
        }

        try {
            ObjectRoot objRootInstance = ObjectRoot.reflect(objectHandle, reflectedAttributes);

            // Federate info type object
            if(objRootInstance instanceof CpswtFederateInfoObject) {
            // if (CpswtFederateInfoObject.match(objectHandle)) {
                LOG.trace("Handling CpswtFederateInfoObject");
                CpswtFederateInfoObject federateInfoObject = (CpswtFederateInfoObject) objRootInstance;

                this.rtiDiscoveredCpswtFederateInfoObjects.remove(federateInfoObject);

                if (federateInfoObject.get_FederateId().isEmpty() ||
                        federateInfoObject.get_FederateType().isEmpty()) {
                    LOG.error("THIS SHOULDN'T HAPPEN RIGHT??");
                    return;
                }

                String federateId = federateInfoObject.get_FederateId();
                String federateType = federateInfoObject.get_FederateType();
                boolean isLateJoiner = federateInfoObject.get_IsLateJoiner();

                // this?
                _federationEventsHandler.handleEvent(IC2WFederationEventsHandler.C2W_FEDERATION_EVENTS.FEDERATE_JOINED, federateId);

                // federationManager case
                if (federateType.equals(SynchronizedFederate.FEDERATION_MANAGER_NAME)) {
                    LOG.info("{} federate joined the federation", SynchronizedFederate.FEDERATION_MANAGER_NAME);
                    return;
                }

                if (!this.experimentConfig.federateTypesAllowed.contains(federateType)) {
                    LOG.warn("{} federate type is not allowed to join this federation. Ignoring...", federateType);
                    return;
                }
                // everything else
                else {
                    if(!isLateJoiner) {
                        int remaining = this.workingExperimentConfig.getRemainingCountForExpectedType(federateType);
                        if (remaining == 0) {
                            LOG.warn("{} federate is not late joiner but all expected federates already joined. Ignoring...");
                            return;
                        }
                    }

                    // expected
                    if (this.workingExperimentConfig.isExpectedFederateType(federateType)) {
                        MutableInt v = this.onlineExpectedFederateTypes.get(federateType);
                        if (v == null) {
                            v = new MutableInt(1);
                            this.onlineExpectedFederateTypes.put(federateType, v);
                        } else {
                            v.increment();
                        }

                        LOG.info("{} #{} expected federate joined the federation with ID {}", federateType, v.getValue(), federateId);

                        // decrease the counter for the expected federate
                        for (FederateJoinInfo fed : this.workingExperimentConfig.expectedFederates) {
                            if (fed.federateType.equals(federateType)) {
                                fed.count--;
                                break;
                            }
                        }
                    }
                    // late joiner
                    else if (this.workingExperimentConfig.isLateJoinerFederateType(federateType)) {
                        MutableInt v = this.onlineLateJoinerFederateTypes.get(federateType);
                        if (v == null) {
                            v = new MutableInt(1);
                            this.onlineLateJoinerFederateTypes.put(federateType, v);
                        } else {
                            v.increment();
                        }

                        LOG.info("{} #{} late joiner federate joined the federation with ID {}", federateType, v.getValue(), federateId);
                    }
                    // unknown
                    else {
                        LOG.warn("FederateType \"{}\" is neither expected nor late joiner. Ignoring...", federateType);
                    }
                }
            } else {
                FederateObject federateObject = (FederateObject) ObjectRoot.reflect(objectHandle, reflectedAttributes);
                LOG.trace("Handling FederateObject ({})", federateObject.get_FederateId());

                // if any attribute of the federateObject is empty, ignore
                if (federateObject.get_FederateHandle() == 0 ||
                        "".equals(federateObject.get_FederateId()) ||
                        "".equals(federateObject.get_FederateHost())
                        ) return;

                //
                this.rtiDiscoveredFederateObjects.remove(federateObject);
            }
        } catch (Exception e) {
            LOG.error("Error while parsing the Federate object: " + e.getMessage());
            LOG.error(e);
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
                // support.firePropertyChange(PROP_LOG_HIGH_PRIO, null, hp);
            } else if (MediumPrio.match(intrHandle) && logLevel >= 2) {
                MediumPrio mp = new MediumPrio(receivedIntr);
                // support.firePropertyChange(PROP_LOG_MEDIUM_PRIO, null, mp);
            } else if (LowPrio.match(intrHandle) && logLevel >= 3) {
                LowPrio lp = new LowPrio(receivedIntr);
                // support.firePropertyChange(PROP_LOG_LOW_PRIO, null, lp);
            } else if (VeryLowPrio.match(intrHandle) && logLevel >= 4) {
                VeryLowPrio vlp = new VeryLowPrio(receivedIntr);
                // support.firePropertyChange(PROP_LOG_VERY_LOW_PRIO, null, vlp);
            }
        } catch (Exception e) {
            System.out.println("Error while parsing the LOG interaction");
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
            // createLog(handle, receivedInteraction, intrTimestamp);


            // Inform COA orchestrator of arrival of interaction (for awaited Outcomes, if any)
            coaExecutor.updateArrivedInteractions(handle, time, interactionRoot);

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
            // monitor_out.print(intrBuf.toString());
            LOG.info(intrBuf.toString());
        } catch (Exception e) {
            System.out.println("Exception while dumping interaction with handle: " + handle);
            e.printStackTrace();
        }
    }

    @Override
    public void onTerminateRequested() {
        this.terminateSimulation();
    }

    @Override
    public double onCurrentTimeRequested() {
        return this.getCurrentTime();
    }
}
