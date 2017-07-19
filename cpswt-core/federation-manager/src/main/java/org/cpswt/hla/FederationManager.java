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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.cpswt.config.*;
import org.cpswt.utils.CpswtUtils;
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

    private static Logger logger = LogManager.getLogger(FederationManager.class);

    private Set<String> _synchronizationLabels = new HashSet<>();

    private FederatesMaintainer federatesMaintainer = new FederatesMaintainer();
    private IC2WFederationEventsHandler _federationEventsHandler = null;

    /*
        ==============================================================================================================
        FederationManager fields
    */

    /**
     * Indicates whether the FederationManager should create synchronization points.
     */
    private boolean useSyncPoints = true;

    /**
     * The name of the Federation.
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

        logger.trace("FederationManager initialization start");

        // record config parameters
        this.federationId = params.federationId;
        this._federationEndTime = params.federationEndTime;
        this.realTimeMode = params.realTimeMode;
        this.terminateOnCOAFinish = params.terminateOnCOAFinish;

        // set project's root directory
        this.rootDir = System.getenv(CpswtDefaults.RootPathEnvVarKey);
        if (this.rootDir == null) {
            logger.trace("There was no {} environment variable set. Setting RootDir to \"user.dir\" system property.", CpswtDefaults.RootPathEnvVarKey);
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
        logger.trace("FOM file should be found under {}", fedFileURL);

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
            logger.warn("Log directory not present. Creating {}...", logDirPath);
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

        logger.trace("Loading experiment config file {}", experimentConfigFilePath);
        this.experimentConfig = ConfigParser.parseConfig(experimentConfigFile, ExperimentConfig.class);
        this.federatesMaintainer.updateFederateJoinInfo(this.experimentConfig);

        if(this.federatesMaintainer.expectedFederatesLeftToJoinCount() == 0) {
            // there are no expected federates --> no need for synchronization points
            this.useSyncPoints = false;
            logger.debug("No expected federates are defined, not setting up synchronization points.");
        }

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
        logger.debug("Main execution loop of federation started at: {}", new Date());
        tMainLoopStartTime = System.currentTimeMillis();
    }

    public void recordMainExecutionLoopEndTime() {
        if (!executionTimeRecorded) {
            logger.debug("Main execution loop of federation stopped at: {}", new Date());
            tMainLoopEndTime = System.currentTimeMillis();
            executionTimeRecorded = true;
            double execTimeInSecs = (tMainLoopEndTime - tMainLoopStartTime) / 1000.0;
            if (execTimeInSecs > 0) {
                logger.debug("Total execution time of the main loop: {} seconds", execTimeInSecs);
            }
        }
    }

    private void initializeLRC(URL fedFileURL) throws Exception {

        logger.trace("Creating Local RTI component ...");
        super.createLRC();
        logger.debug("Local RTI component created successfully.");

        logger.trace("[{}] Attempting to create federation \"{}\"...", super.getFederateId(), federationId);
        try {
            _federationEventsHandler.handleEvent(IC2WFederationEventsHandler.C2W_FEDERATION_EVENTS.CREATING_FEDERATION, federationId);
            super.lrc.createFederationExecution(this.federationId, fedFileURL);
            _federationEventsHandler.handleEvent(IC2WFederationEventsHandler.C2W_FEDERATION_EVENTS.FEDERATION_CREATED, federationId);
        } catch (FederationExecutionAlreadyExists feae) {
            logger.error("Federation with the name of \"{}\" already exists.", federationId);
            return;
        }
        logger.debug("Federation \"{}\" created successfully.", this.federationId);

        // join the federation
        super.joinFederation();

        // PER THE HLA BOOK, ENABLE TIME-CONSTRAINED FIRST, THEN TIME-REGULATING
        super.enableTimeConstrained();

        super.enableTimeRegulation(time.getTime(), super.getLookAhead());

        super.enableAsynchronousDelivery();

        if (useSyncPoints) {
            logger.trace("Registering synchronization point: {}", SynchronizationPoints.ReadyToPopulate);
            super.lrc.registerFederationSynchronizationPoint(SynchronizationPoints.ReadyToPopulate, null);
            super.lrc.tick();
            while (!_synchronizationLabels.contains(SynchronizationPoints.ReadyToPopulate)) {
                CpswtUtils.sleep(SynchronizedFederate.internalThreadWaitTimeMs);
                super.lrc.tick();
            }
            logger.debug("Synchronization point \"{}\" registered successfully.", SynchronizationPoints.ReadyToPopulate);

            logger.trace("Registering synchronization point: {}", SynchronizationPoints.ReadyToRun);
            super.lrc.registerFederationSynchronizationPoint(SynchronizationPoints.ReadyToRun, null);
            super.lrc.tick();
            while (!_synchronizationLabels.contains(SynchronizationPoints.ReadyToRun)) {
                CpswtUtils.sleep(SynchronizedFederate.internalThreadWaitTimeMs);
                super.lrc.tick();
            }
            logger.debug("Synchronization point \"{}\" registered successfully.", SynchronizationPoints.ReadyToRun);

            logger.trace("Registering synchronization point: {}", SynchronizationPoints.ReadyToResign);
            super.lrc.registerFederationSynchronizationPoint(SynchronizationPoints.ReadyToResign, null);
            super.lrc.tick();
            while (!_synchronizationLabels.contains(SynchronizationPoints.ReadyToResign)) {
                CpswtUtils.sleep(SynchronizedFederate.internalThreadWaitTimeMs);
                super.lrc.tick();
            }
            logger.debug("Synchronization point \"{}\" registered successfully.", SynchronizationPoints.ReadyToResign);
        }

        // subscribe for "join" and "resign" interactions
        FederateJoinInteraction.subscribe(super.getLRC());
        FederateResignInteraction.subscribe(super.getLRC());

        super.notifyFederationOfJoin();

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

//        Thread waitExpectedThread = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    waitExpectedFederatesToJoin();
//                }
//                catch(Exception ex) {
//                    logger.error("ERROR: {}", ex);
//                }
//            }
//        });
//
//        waitExpectedThread.run();
//        waitExpectedThread.join();

        waitExpectedFederatesToJoin();

        if (useSyncPoints) {
            logger.trace("Waiting for \"{}\"...", SynchronizationPoints.ReadyToPopulate);
            super.readyToPopulate();
            logger.trace("{} done.", SynchronizationPoints.ReadyToPopulate);

            logger.trace("Waiting for \"{}\"...", SynchronizationPoints.ReadyToRun);
            super.readyToRun();
            logger.trace("{} done.", SynchronizationPoints.ReadyToRun);
        }

        _federationEventsHandler.handleEvent(IC2WFederationEventsHandler.C2W_FEDERATION_EVENTS.FEDERATION_READY_TO_RUN, federationId);

        // SEND OUT "INITIALIZATION INTERACTIONS," WHICH ARE SUPPOSED TO BE "RECEIVE" ORDERED.
        for (InteractionRoot interactionRoot : initialization_interactions) {
            logger.trace("Sending {} interaction.", interactionRoot.getSimpleClassName());
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
                                CpswtUtils.sleep(local_sleep_time);
                                sleep_time = time_in_millisec - (time_diff + System.currentTimeMillis());
                            }
                        }

                        if (!paused) {
                            synchronized (getLRC()) {

                                sendScriptInteractions();

                                // coaExecutor.executeCOAGraph();

                                DoubleTime next_time = new DoubleTime(time.getTime() + step);
                                logger.info("Current_time = {} and step = {} and requested_time = {}", time.getTime(), step, next_time.getTime());
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
                                logger.info("Federation manager current time = {}", time.getTime());
                                numStepsExecuted = 0;
                            }
                        } else {
                            CpswtUtils.sleep(10);
                        }

                        // If we have reached federation end time (if it was configured), terminate the federation
                        if (_federationEndTime > 0 && time.getTime() > _federationEndTime) {
                            _federationEventsHandler.handleEvent(IC2WFederationEventsHandler.C2W_FEDERATION_EVENTS.FEDERATION_SIMULATION_FINISHED, federationId);
                            terminateSimulation();
                        }

                    }
                    _federationEventsHandler.handleEvent(IC2WFederationEventsHandler.C2W_FEDERATION_EVENTS.FEDERATION_SIMULATION_FINISHED, federationId);
                    prepareForFederatesToResign();

                    logger.info("Waiting for \"ReadyToResign\" ... ");
                    readyToResign();
                    logger.info("done.\n");

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
            logger.trace("Waiting for {} federate{} of type \"{}\" to join", federateInfo.count, federateInfo.count <= 1 ? "" : "s", federateInfo.federateType);
        }

        int numOfFedsToWaitFor = this.federatesMaintainer.expectedFederatesLeftToJoinCount();

        // requestUpdate is called on the federateObject which causes an RTI callback
        // that is handled in << this.reflectAttributeValues >>
        // TODO: mutex or some sort of synchronization
        while (numOfFedsToWaitFor > 0) {
            try {
//                for (ObjectRoot objectRoot : this.rtiDiscoveredFederateObjects) {
//                    objectRoot.requestUpdate(this.lrc);
//                }
//                for (ObjectRoot objectRoot : this.rtiDiscoveredCpswtFederateInfoObjects) {
//                    objectRoot.requestUpdate(this.lrc);
//                }

                super.lrc.tick();
                CpswtUtils.sleep(SynchronizedFederate.internalThreadWaitTimeMs);
                numOfFedsToWaitFor = this.federatesMaintainer.expectedFederatesLeftToJoinCount();
            }
            catch(Exception e) {
                CpswtUtils.sleep(SynchronizedFederate.internalThreadWaitTimeMs);
            }
        }
        logger.debug("All expected federates have joined the federation. Proceeding with the simulation...");

        // PREPARE FOR FEDERATES TO RESIGN NOW -- INITIALIZE _processedFederates AND ELIMINATE
        // FEDERATES NAMES FROM IT AS THEY RESIGN (WHICH COULD BE AT ANY TIME).            
//        _processedFederates.addAll(expectedFederateTypes);
    }

    private void prepareForFederatesToResign() throws Exception {

        for (FederateInfo federateInfo : this.federatesMaintainer.getOnlineExpectedFederates()) {
            logger.info(
                    "Waiting for \"" + federateInfo.getFederateId() + "\" federate to resign ...\n"
            );
        }
    }

    private void waitForFederatesToResign() throws Exception {
        while (this.federatesMaintainer.getOnlineExpectedFederates().size() != 0) {
//            getLRC().tick();
            CpswtUtils.sleep(SynchronizedFederate.internalThreadWaitTimeMs);
        }
        logger.info("All federates have resigned the federation.  Simulation terminated.\n");
    }

    private void sendScriptInteractions() {
        double tmin = time.getTime() + super.getLookAhead() + (super.getLookAhead() / 10000.0);

        for (double intrtime : script_interactions.keySet()) {
            logger.trace("Interaction time = {}", intrtime);
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
                logger.error("Error: simulation passed scheduled interaction time: {}, {}", intrtime, interactionClassList);
            } else if (intrtime >= tmin && intrtime < tmin + super.getStepSize()) {

                List<InteractionRoot> interactionsSent = new ArrayList<InteractionRoot>();
                for (InteractionRoot interactionRoot : interactionRootList) {
                    try {
                        interactionRoot.sendInteraction(getLRC(), intrtime);
                    } catch (Exception e) {
                        logger.error("Failed to send interaction: {}", interactionRoot);
                        logger.error(e.getStackTrace());
                    }
                    interactionsSent.add(interactionRoot);
                    logger.info("Sending out the injected interaction");
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
        logger.debug("Pausing simulation");
        this.paused = true;
        this.setFederateState(FederateState.PAUSED);
    }

    public void resumeSimulation() throws Exception {
        time_diff = time_in_millisec - System.currentTimeMillis();
        logger.debug("Resuming simulation");
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
        CpswtUtils.sleep(2000);

        logger.info("Simulation terminated");

        running = false;
        paused = false;
        this.setFederateState(FederateState.TERMINATED);

        // Wait for 10 seconds for Simulation to gracefully exit
        CpswtUtils.sleep(10000);

        // If simulation has still not exited gracefully, run kill command
        killEntireFederation();
    }

    public void killEntireFederation() {
        _killingFederation = true;

        recordMainExecutionLoopEndTime();

        // Kill the entire federation
        String killCommand = "bash -x " + _stopScriptFilepath;
        try {
            logger.info("Killing federation by executing: {}\tIn directory: {}", killCommand, rootDir);

            // TODO: why is this called 3 times???
            Runtime.getRuntime().exec(killCommand, null, new File(rootDir));
            Runtime.getRuntime().exec(killCommand, null, new File(rootDir));
            Runtime.getRuntime().exec(killCommand, null, new File(rootDir));
        } catch (IOException e) {
            logger.error("Exception while killing the federation");
            logger.error(e);
        }
        System.exit(0);
    }

    public void setRealTimeMode(boolean newRealTimeMode) {
        logger.debug("Setting simulation to run in realTimeMode as: {}", newRealTimeMode);
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
                // Unsubscribe lower logger levels
                for (int i = logLevel; i > selected; i--) {
                    unsubscribeLogLevel(i);
                }
            } else {
                // Subscribe lower logger levels
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
                logger.debug("Unsusbcribing to High priority logs");
                HighPrio.unsubscribe(getLRC());
            } else if (level == 2) {
                logger.debug("Unsusbcribing to Medium priority logs");
                MediumPrio.unsubscribe(getLRC());
            } else if (level == 3) {
                logger.debug("Unsusbcribing to Low priority logs");
                LowPrio.unsubscribe(getLRC());
            } else if (level == 4) {
                logger.debug("Unsusbcribing to Very Low priority logs");
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
                logger.debug("Susbcribing to High priority logs");
                HighPrio.subscribe(getLRC());
            } else if (level == 2) {
                logger.debug("Susbcribing to Medium priority logs");
                MediumPrio.subscribe(getLRC());
            } else if (level == 3) {
                logger.debug("Susbcribing to Low priority logs");
                LowPrio.subscribe(getLRC());
            } else if (level == 4) {
                logger.debug("Susbcribing to Very Low priority logs");
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

        // we don't need this anymore

//        ObjectRoot objectRoot = ObjectRoot.discover(objectClassHandle, objectHandle);
//        if (FederateObject.match(objectClassHandle)) {
//            FederateObject federateObject = (FederateObject) objectRoot;
//            logger.debug("New federateObject discovered");
//            this.rtiDiscoveredFederateObjects.add(federateObject);
//        } else if (CpswtFederateInfoObject.match(objectClassHandle)) {
//            CpswtFederateInfoObject infoObject = (CpswtFederateInfoObject) objectRoot;
//            logger.debug("New CpswtFederateInfoObject discovered");
//            this.rtiDiscoveredCpswtFederateInfoObjects.add(infoObject);
//        }
//
//        this.rtiDiscoveredObjectsObs.add(objectRoot);
    }

    @Override
    public void removeObjectInstance( int theObject, byte[] userSuppliedTag, LogicalTime theTime,
                                      EventRetractionHandle retractionHandle ) {
        logger.error(" removeObjectInstance --------------------------------------------------------- NOT IMPLEMENTED");
    }

    @Override
    public void removeObjectInstance(int theObject, byte[] tag) {
//        try {
//            String federateType = _discoveredFederates.get(theObject);
//            boolean registeredFederate = expectedFederateTypes.contains(federateType);
//
//            if (!registeredFederate) {
//                logger.info("Unregistered \"" + federateType + "\" federate has resigned the federation.\n");
//            } else {
//                logger.info("\"" + federateType + "\" federate has resigned the federation\n");
//                _processedFederates.remove(federateType);
//                _federationEventsHandler.handleEvent(IC2WFederationEventsHandler.C2W_FEDERATION_EVENTS.FEDERATE_RESIGNED, federateType);
//            }
//            return;
//        } catch (Exception e) {
//            logger.error("Error while parsing the Federate object: {}", e.getMessage());
//            logger.error(e);
//        }
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

//        if (!this.rtiDiscoveredFederateObjects.contains(o) && !this.rtiDiscoveredCpswtFederateInfoObjects.contains(o))
//            return;

        // for user-defined objects there's no need to change to length-1
        // I have no idea why this piece of code was here
//        if(!(o instanceof CpswtFederateInfoObject)) {
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
                logger.error("Error while processing reflectedAttributes. {}", aioob);
            }

            reflectedAttributes = new HLA13ReflectedAttributes(updateAttributes.getFilteredAttributes());
//        }

        try {
            ObjectRoot objRootInstance = ObjectRoot.reflect(objectHandle, reflectedAttributes);

            logger.trace("ObjectRootInstance received through reflectAttributeValues");

            // Federate info type object
//            if(objRootInstance instanceof CpswtFederateInfoObject) {
//            // if (CpswtFederateInfoObject.match(objectHandle)) {
//                logger.trace("Handling CpswtFederateInfoObject");
//                CpswtFederateInfoObject federateInfoObject = (CpswtFederateInfoObject) objRootInstance;
//
//                this.rtiDiscoveredCpswtFederateInfoObjects.remove(federateInfoObject);
//
//                if (federateInfoObject.get_FederateId().isEmpty() ||
//                        federateInfoObject.get_FederateType().isEmpty()) {
//                    logger.error("THIS SHOULDN'T HAPPEN RIGHT??");
//                    return;
//                }
//
//                String federateId = federateInfoObject.get_FederateId();
//                String federateType = federateInfoObject.get_FederateType();
//                boolean isLateJoiner = federateInfoObject.get_IsLateJoiner();
//
//                // this?
//                _federationEventsHandler.handleEvent(IC2WFederationEventsHandler.C2W_FEDERATION_EVENTS.FEDERATE_JOINED, federateId);
//
//                // federationManager case
//                if (federateType.equals(SynchronizedFederate.FEDERATION_MANAGER_NAME)) {
//                    logger.info("{} federate joined the federation", SynchronizedFederate.FEDERATION_MANAGER_NAME);
//                    return;
//                }
//
//                if (!this.experimentConfig.federateTypesAllowed.contains(federateType)) {
//                    logger.warn("{} federate type is not allowed to join this federation. Ignoring...", federateType);
//                    return;
//                }
//                // everything else
//                else {
//                    if(!isLateJoiner) {
//                        int remaining = this.workingExperimentConfig.getRemainingCountForExpectedType(federateType);
//                        if (remaining == 0) {
//                            logger.warn("{} federate is not late joiner but all expected federates already joined. Ignoring...");
//                            return;
//                        }
//                    }
//
//                    // expected
//                    if (this.workingExperimentConfig.isExpectedFederateType(federateType)) {
//                        MutableInt v = this.onlineExpectedFederateTypes.get(federateType);
//                        if (v == null) {
//                            v = new MutableInt(1);
//                            this.onlineExpectedFederateTypes.put(federateType, v);
//                        } else {
//                            v.increment();
//                        }
//
//                        logger.info("{} #{} expected federate joined the federation with ID {}", federateType, v.getValue(), federateId);
//
//                        // decrease the counter for the expected federate
//                        for (FederateJoinInfo fed : this.workingExperimentConfig.expectedFederates) {
//                            if (fed.federateType.equals(federateType)) {
//                                fed.count--;
//                                break;
//                            }
//                        }
//                    }
//                    // late joiner
//                    else if (this.workingExperimentConfig.isLateJoinerFederateType(federateType)) {
//                        MutableInt v = this.onlineLateJoinerFederateTypes.get(federateType);
//                        if (v == null) {
//                            v = new MutableInt(1);
//                            this.onlineLateJoinerFederateTypes.put(federateType, v);
//                        } else {
//                            v.increment();
//                        }
//
//                        logger.info("{} #{} late joiner federate joined the federation with ID {}", federateType, v.getValue(), federateId);
//                    }
//                    // unknown
//                    else {
//                        logger.warn("FederateType \"{}\" is neither expected nor late joiner. Ignoring...", federateType);
//                    }
//                }
//            } else {
//                FederateObject federateObject = (FederateObject) ObjectRoot.reflect(objectHandle, reflectedAttributes);
//                logger.trace("Handling FederateObject ({})", federateObject.get_FederateId());
//
//                // if any attribute of the federateObject is empty, ignore
//                if (federateObject.get_FederateHandle() == 0 ||
//                        "".equals(federateObject.get_FederateId()) ||
//                        "".equals(federateObject.get_FederateHost())
//                        ) return;
//
//                //
//                this.rtiDiscoveredFederateObjects.remove(federateObject);
//            }
        } catch (Exception e) {
            logger.error("Error while parsing the Federate object: " + e.getMessage());
            logger.error(e);
        }
    }

    @Override
    public void receiveInteraction(int intrHandle, ReceivedInteraction receivedIntr, byte[] tag) {

        try {
            // TODO: get rid of this sh*t
            // First dump the interaction (if monitored)
            dumpInteraction(intrHandle, receivedIntr, null);

            // "federate join" interaction
            if(FederateJoinInteraction.match(intrHandle)) {
                FederateJoinInteraction federateJoinInteraction = new FederateJoinInteraction(receivedIntr);
                logger.trace("FederateJoinInteraction received :: {} joined", federateJoinInteraction.toString());

                // ??
                _federationEventsHandler.handleEvent(IC2WFederationEventsHandler.C2W_FEDERATION_EVENTS.FEDERATE_JOINED, federateJoinInteraction.getFederateId());

                this.federatesMaintainer.federateJoined(new FederateInfo(federateJoinInteraction.getFederateId(), federateJoinInteraction.getFederateType(), federateJoinInteraction.isLateJoiner()));

                this.federatesMaintainer.logCurrentStatus();
            }
            // "federate resign" interaction
            else if(FederateResignInteraction.match(intrHandle)) {
                FederateResignInteraction federateResignInteraction = new FederateResignInteraction(receivedIntr);
                logger.trace("FederateResignInteraction received :: {} resigned", federateResignInteraction.toString());

                // ??
                _federationEventsHandler.handleEvent(IC2WFederationEventsHandler.C2W_FEDERATION_EVENTS.FEDERATE_RESIGNED, federateResignInteraction.getFederateId());

                FederateInfo federateInfo = this.federatesMaintainer.getFederateInfo(federateResignInteraction.getFederateId());
                this.federatesMaintainer.federateResigned(federateInfo);

                this.federatesMaintainer.logCurrentStatus();
            }

//            TODO: get rid of this shit
//            // Now process the interactions as needed
//            if (HighPrio.match(intrHandle) && logLevel >= 1) {
//                HighPrio hp = new HighPrio(receivedIntr);
//                // support.firePropertyChange(PROP_LOG_HIGH_PRIO, null, hp);
//            } else if (MediumPrio.match(intrHandle) && logLevel >= 2) {
//                MediumPrio mp = new MediumPrio(receivedIntr);
//                // support.firePropertyChange(PROP_LOG_MEDIUM_PRIO, null, mp);
//            } else if (LowPrio.match(intrHandle) && logLevel >= 3) {
//                LowPrio lp = new LowPrio(receivedIntr);
//                // support.firePropertyChange(PROP_LOG_LOW_PRIO, null, lp);
//            } else if (VeryLowPrio.match(intrHandle) && logLevel >= 4) {
//                VeryLowPrio vlp = new VeryLowPrio(receivedIntr);
//                // support.firePropertyChange(PROP_LOG_VERY_LOW_PRIO, null, vlp);
//            }
        } catch (Exception e) {
            logger.error("Error while parsing the logger interaction");
            logger.error(e);
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

        // TODO: this callback shouldn't be called, right?
        if(FederateJoinInteraction.match(intrHandle)) {
            logger.trace("FederateJoinInteraction received in FederationManager!");
            FederateJoinInteraction federateJoinInteraction = new FederateJoinInteraction(receivedIntr);

            logger.trace("Received: {}", federateJoinInteraction.toString());
        }

        // TODO: get rid of this sh*t
        // First dump the interaction (if monitored)
        dumpInteraction(intrHandle, receivedIntr, intrTime);
    }

    private void dumpInteraction(int handle, ReceivedInteraction receivedInteraction, LogicalTime time) {

        try {
            InteractionRoot interactionRoot = InteractionRoot.create_interaction(handle, receivedInteraction);

            if (interactionRoot != null) {
                logger.debug("FederationManager: Received interaction {}", interactionRoot);
            } else {
                logger.warn("FederationManager: WARNING! Received interaction with handle {}.. COULD NOT CREATE PROPER INTERACTION", handle);
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
            logger.info(intrBuf.toString());
        } catch (Exception e) {
            logger.error("Exception while dumping interaction with handle: {}", handle);
            logger.error(e);
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

    public List<FederateInfo> getFederatesStatus() {
        return this.federatesMaintainer.getAllMaintainedFederates();
    }
}
