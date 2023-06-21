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

package edu.vanderbilt.vuisis.cpswt.hla;

import edu.vanderbilt.vuisis.cpswt.coa.COAExecutor;
import edu.vanderbilt.vuisis.cpswt.coa.COAExecutorEventListener;
import edu.vanderbilt.vuisis.cpswt.coa.COAGraph;
import edu.vanderbilt.vuisis.cpswt.coa.COALoader;
import edu.vanderbilt.vuisis.cpswt.hla.base.AdvanceTimeRequest;
import edu.vanderbilt.vuisis.cpswt.utils.CpswtDefaults;
import hla.rti.*;

import java.io.File;
import java.lang.reflect.Field;
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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.vanderbilt.vuisis.cpswt.config.*;
import edu.vanderbilt.vuisis.cpswt.utils.CpswtUtils;
import org.portico.bindings.IConnection;
import org.portico.bindings.jgroups.Federation;
import org.portico.bindings.jgroups.JGroupsConnection;
import org.portico.bindings.jgroups.channel.Manifest;

import org.portico.impl.hla13.Rti13Ambassador;
import org.portico.impl.hla13.types.DoubleTime;

import edu.vanderbilt.vuisis.cpswt.util.RandomWithFixedSeed;
import edu.vanderbilt.vuisis.cpswt.hla.rtievents.IC2WFederationEventsHandler;
import edu.vanderbilt.vuisis.cpswt.hla.rtievents.C2WFederationEventsHandler;

import edu.vanderbilt.vuisis.cpswt.hla.InteractionRoot_p.C2WInteractionRoot_p.FederateJoinInteraction;
import edu.vanderbilt.vuisis.cpswt.hla.InteractionRoot_p.C2WInteractionRoot_p.FederateResignInteraction;

import edu.vanderbilt.vuisis.cpswt.hla.InteractionRoot_p.C2WInteractionRoot_p.SimulationControl_p.SimEnd;
import edu.vanderbilt.vuisis.cpswt.hla.InteractionRoot_p.C2WInteractionRoot_p.SimulationControl_p.SimPause;
import edu.vanderbilt.vuisis.cpswt.hla.InteractionRoot_p.C2WInteractionRoot_p.SimulationControl_p.SimResume;

import edu.vanderbilt.vuisis.cpswt.hla.InteractionRoot_p.C2WInteractionRoot_p.SimLog_p.VeryLowPrio;
import edu.vanderbilt.vuisis.cpswt.hla.InteractionRoot_p.C2WInteractionRoot_p.SimLog_p.LowPrio;
import edu.vanderbilt.vuisis.cpswt.hla.InteractionRoot_p.C2WInteractionRoot_p.SimLog_p.MediumPrio;
import edu.vanderbilt.vuisis.cpswt.hla.InteractionRoot_p.C2WInteractionRoot_p.SimLog_p.HighPrio;

import edu.vanderbilt.vuisis.cpswt.hla.ObjectRoot_p.FederateObject;

import static edu.vanderbilt.vuisis.cpswt.hla.InteractionRootInterface.ClassAndPropertyName;

/**
 * Model class for the Federation Manager.
 */
public class FederationManager extends SynchronizedFederate implements COAExecutorEventListener {

    private static final Logger _logger = LogManager.getLogger(FederationManager.class);

    private final Set<String> _synchronizationLabels = new HashSet<>();

    private final FederatesMaintainer federatesMaintainer = new FederatesMaintainer();
    private final IC2WFederationEventsHandler _federationEventsHandler;

    /*
        ==============================================================================================================
        FederationManager fields
    */

    /**
     * Indicates whether the FederationManager should create synchronization points.
     */
    private boolean _useSyncPoints = true;

    /**
     * The name of the Federation.
     */
    private final String _federationId;

    /**
     * Indicates if real time mode is on.
     */
    private boolean _realTimeMode;

    private double _currentTime = 0;

    /**
     * Experiment config
     */
    private final ExperimentConfig _experimentConfig;

    /**
     * Pause times
     */
    private final Set<Double> _pauseTimes = new HashSet<>();

    private double _federationEndTime;
    private Random _rand4Dur = null;
    private final String _logLevel;

    private boolean _killingFederation = false;


    private final Map<Double, List<InteractionRoot>> _script_interactions = new TreeMap<>();
    private List<InteractionRoot> _initialization_interactions = new ArrayList<>();

    private boolean _running = false;

    private boolean _paused = false;

    private boolean _federationAttempted = false;

    private final DoubleTime _time = new DoubleTime(0);

    private long _time_in_millisec = 0;

    private long _time_diff;


    // Default to No logging
    private int _logLevelNum = 0;

    // Default to High priority logs
    private int _logLevelNumToSet = 1;

    // Start and end time markers for the main execution loop
    private double _tMainLoopStartTime = 0.0;
    private double _tMainLoopEndTime = 0.0;
    private boolean _executionTimeRecorded = false;

    private COAExecutor _coaExecutor = null;

    //private PrintStream monitor_out;

//    @Override
//    public boolean setFederateState(FederateState newState) {
//        if(federateState.CanTransitionTo(newState)) {
//            // TODO: transition to new state
//
//
//
//            return true;
//        }
//        return false;
//    }
    /* ============================================================================================================== */

    static {
        FederateJoinInteraction.load();
        FederateResignInteraction.load();

        SimEnd.load();
        SimPause.load();
        SimResume.load();

        VeryLowPrio.load();
        LowPrio.load();
        MediumPrio.load();
        HighPrio.load();

        FederateObject.load();
    }
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
                params.federationJsonFileName,
                false,
                params.lookahead,
                params.stepSize
        ));

        _logger.trace("FederationManager initialization start");

        // record config parameters
        _federationId = params.federationId;
        _federationEndTime = params.federationEndTime;
        _realTimeMode = params.realTimeMode;

        // set project's root directory
        /*
         * Project root directory
         */
        String _rootDir = System.getenv(CpswtDefaults.RootPathEnvVarKey);
        if (_rootDir == null) {
            _logger.trace("There was no {} environment variable set. Setting RootDir to \"user.dir\" system property.", CpswtDefaults.RootPathEnvVarKey);
            _rootDir = System.getProperty("user.dir");
        }

        // build fed file URL
        Path fedFilePath = Paths.get(params.fedFile);
        URL fedFileURL;
        if (fedFilePath.isAbsolute()) {
            fedFileURL = fedFilePath.toUri().toURL();
        } else {
            fedFileURL = Paths.get(_rootDir, params.fedFile).toUri().toURL();
        }
        _logger.trace("FOM file should be found under {}", fedFileURL);

        // TODO: eliminate loglevels @see #18
        _logLevel = "NORMAL";

        // See if fixed seed must be used
        // TODO: WHAT IS THIS
        int seed4Dur = 0;
        if (seed4Dur > 0) {
            RandomWithFixedSeed.init(seed4Dur);
            _rand4Dur = RandomWithFixedSeed.instance();
        } else {
            _rand4Dur = new Random();
        }

        // TODO: logging #18
        Path logDirPath = Paths.get(_rootDir, "log"); // params.LogDir);
        File logDir = logDirPath.toFile();
        if (Files.notExists(logDirPath)) {
            _logger.warn("Log directory not present. Creating {}...", logDirPath);
            logDir.mkdir();
        }

        // TODO: Prepare core to be able to stream events when needed #27
        _federationEventsHandler = new C2WFederationEventsHandler();

        Path experimentConfigFilePath = CpswtUtils.getConfigFilePath(params.experimentConfig, _rootDir);

        _logger.trace("Loading experiment config file {}", experimentConfigFilePath);
        _experimentConfig = ConfigParser.parseConfig(experimentConfigFilePath.toFile(), ExperimentConfig.class);
        _logger.trace("Experiment config loaded");
        /*
         * Indicates if federation manager terminates when COA finishes.
         */
        boolean terminateOnCOAFinish = _experimentConfig.terminateOnCOAFinish;
        /*
         * Represents which combination of COAs to execute in current run of an experiment.
         */
        String coaSelectionToExecute = _experimentConfig.COASelectionToExecute;
        _logger.trace("Updating Federate Join Info in the FederatesMaintainer");
        federatesMaintainer.updateFederateJoinInfo(_experimentConfig);
        _logger.trace("Checking pause times in experiment config: {}", _experimentConfig.pauseTimes);
        if(_experimentConfig.pauseTimes != null) {
            _pauseTimes.addAll(_experimentConfig.pauseTimes);
        }

        // load COA related stuff
        _logger.trace("Checking COA coaDefinitions, coaSelections, and coaSelectionToExecute");
        Path coaDefinitionPath = null;
        if( _experimentConfig.coaDefinition != null && !"".equals(_experimentConfig.coaDefinition) ) {
                coaDefinitionPath = CpswtUtils.getConfigFilePath(_experimentConfig.coaDefinition, _rootDir);
        } else {
                _logger.info("No COA definitions were provided!");
        }
        Path coaSelectionPath = null;
        if( _experimentConfig.coaSelection != null && !"".equals(_experimentConfig.coaSelection) ) {
                coaSelectionPath = CpswtUtils.getConfigFilePath(_experimentConfig.coaSelection, _rootDir);
        } else {
                _logger.info("No COA selections were provided!");
        }
        if( coaSelectionToExecute != null && !"".equals(coaSelectionToExecute) ) {
            _logger.trace("COASelectionToExecute: {}", coaSelectionToExecute);
        } else {
            _logger.info("No specific COA-selection was specified for execution!");
        }

        COALoader coaLoader = null;        
        if( coaDefinitionPath != null && coaSelectionPath != null && coaSelectionToExecute != null) {
                coaLoader = new COALoader(coaDefinitionPath, coaSelectionPath, coaSelectionToExecute);
        }
        if(coaLoader != null) {
                COAGraph coaGraph = coaLoader.loadGraph();

                _coaExecutor = new COAExecutor(getFederationId(), getFederateId(), getLookahead(), terminateOnCOAFinish, this);
                _coaExecutor.setCoaExecutorEventListener(this);
                _coaExecutor.setCOAGraph(coaGraph);
        } else {
                _logger.info("No COAs are used in this experiment.");
        }

        if(federatesMaintainer.expectedFederatesLeftToJoinCount() == 0) {
            // there are no expected federates --> no need for synchronization points
            _useSyncPoints = false;
            _logger.debug("No expected federates are defined, not setting up synchronization points.");
        }

        initializeRTI(fedFileURL);

        readFederationJson(params.federationJsonFileName);

        for(InjectedInteractionInfo injectedInteractionInfo: _experimentConfig.InjectedInteractions) {
            double injectionTime = injectedInteractionInfo.InjectionTime;
            String interactionFullHlaClassName = injectedInteractionInfo.Interaction;

            InteractionRoot.publish_interaction(interactionFullHlaClassName, getRTI());
            InteractionRoot interactionRoot = new InteractionRoot(interactionFullHlaClassName);
            for(Map.Entry<ClassAndPropertyName, Object> entry : injectedInteractionInfo.ParameterValues.entrySet()) {
                interactionRoot.setParameter(entry.getKey(), entry.getValue());
            }

            if (!_script_interactions.containsKey(injectionTime)) {
                _script_interactions.put(injectionTime, new ArrayList<>());
            }
            _script_interactions.get(injectionTime).add(interactionRoot);
        }

        // SUBSCRIBE TO MONITORED INTERACTIONS
        for(String fullHlaClassName: _experimentConfig.MonitoredInteractions) {
            InteractionRoot.subscribe_interaction(fullHlaClassName, getRTI());
        }

        // Before beginning simulation, initialize COA sequence graph
        if(_coaExecutor != null) {
                _coaExecutor.setSynchronizedFederate(this);
                _coaExecutor.initializeCOAGraph();
        }

        setFederateState(FederateState.INITIALIZED);

    }

    void maintainFederatesFromFederationManifest() {
        try {
            RTIambassador genericRTIambassador = getRTI();
            if(genericRTIambassador instanceof Rti13Ambassador) {
                Rti13Ambassador rti13Ambassador = (Rti13Ambassador) getRTI();
                IConnection connection = rti13Ambassador.getHelper().getLrc().getConnection();
                JGroupsConnection jGroupsConnection = (JGroupsConnection) connection;

                Field federationField = JGroupsConnection.class.getDeclaredField("joinedFederation");
                if (!federationField.isAccessible()) {
                    federationField.setAccessible(true);
                }
                Federation joinedFederation = (Federation) federationField.get(jGroupsConnection);
                Manifest joinedFederationManifest = joinedFederation.getManifest();

                synchronized (federatesMaintainer) {
                    List<FederateInfo> onlineFederatesList = federatesMaintainer.getOnlineFederates();
                    FederateInfo[] onlineFederates = onlineFederatesList.toArray(new FederateInfo[0]);
                    for (FederateInfo federateInfo : onlineFederates) {
                        boolean containsFed = joinedFederationManifest.containsFederate(federateInfo.getFederateId());
                        _logger.trace("{} :: isContainedByManifest: {}", federateInfo.getFederateId(), containsFed);

                        if (!containsFed) {
                            federatesMaintainer.federateResigned(federateInfo, true);
                        }
                    }
                }
            }
        }
        catch(Exception ex) {
            _logger.error(ex);
        }
    }

    public void recordMainExecutionLoopStartTime() {
        _logger.debug("Main execution loop of federation started at: {}", new Date());
        _tMainLoopStartTime = System.currentTimeMillis();
    }

    public void recordMainExecutionLoopEndTime() {
        if (!_executionTimeRecorded) {
            _logger.debug("Main execution loop of federation stopped at: {}", new Date());
            _tMainLoopEndTime = System.currentTimeMillis();
            _executionTimeRecorded = true;
            double execTimeInSecs = (_tMainLoopEndTime - _tMainLoopStartTime) / 1000.0;
            if (execTimeInSecs > 0) {
                _logger.debug("Total execution time of the main loop: {} seconds", execTimeInSecs);
            }
        }
    }

    private void initializeRTI(URL fedFileURL) throws Exception {

        _logger.trace("Creating Local RTI component ...");
        createRTI();
        _logger.debug("Local RTI component created successfully.");

        _logger.trace("[{}] Attempting to create federation \"{}\"...", getFederateId(), _federationId);
        try {
            _federationEventsHandler.handleEvent(IC2WFederationEventsHandler.C2W_FEDERATION_EVENTS.CREATING_FEDERATION, _federationId);
            rti.createFederationExecution(_federationId, fedFileURL);
            _federationEventsHandler.handleEvent(IC2WFederationEventsHandler.C2W_FEDERATION_EVENTS.FEDERATION_CREATED, _federationId);
        } catch (FederationExecutionAlreadyExists feae) {
            _logger.error("Federation with the name of \"{}\" already exists.", _federationId);
            return;
        }
        _logger.debug("Federation \"{}\" created successfully.", _federationId);

        // join the federation
        joinFederation();

        // PER THE HLA BOOK, ENABLE TIME-CONSTRAINED FIRST, THEN TIME-REGULATING
        enableTimeConstrained();

        enableTimeRegulation(_currentTime, getLookahead());

        enableAsynchronousDelivery();

        if (_useSyncPoints) {
            _logger.trace("Registering synchronization point: {}", SynchronizationPoints.ReadyToPopulate);
            rti.registerFederationSynchronizationPoint(SynchronizationPoints.ReadyToPopulate, null);
            rti.tick();
            while (!_synchronizationLabels.contains(SynchronizationPoints.ReadyToPopulate)) {
                CpswtUtils.sleep(SynchronizedFederate.internalThreadWaitTimeMs);
                rti.tick();
            }
            _logger.debug("Synchronization point \"{}\" registered successfully.", SynchronizationPoints.ReadyToPopulate);

            _logger.trace("Registering synchronization point: {}", SynchronizationPoints.ReadyToRun);
            rti.registerFederationSynchronizationPoint(SynchronizationPoints.ReadyToRun, null);
            rti.tick();
            while (!_synchronizationLabels.contains(SynchronizationPoints.ReadyToRun)) {
                CpswtUtils.sleep(SynchronizedFederate.internalThreadWaitTimeMs);
                rti.tick();
            }
            _logger.debug("Synchronization point \"{}\" registered successfully.", SynchronizationPoints.ReadyToRun);

            _logger.trace("Registering synchronization point: {}", SynchronizationPoints.ReadyToResign);
            rti.registerFederationSynchronizationPoint(SynchronizationPoints.ReadyToResign, null);
            rti.tick();
            while (!_synchronizationLabels.contains(SynchronizationPoints.ReadyToResign)) {
                CpswtUtils.sleep(SynchronizedFederate.internalThreadWaitTimeMs);
                rti.tick();
            }
            _logger.debug("Synchronization point \"{}\" registered successfully.", SynchronizationPoints.ReadyToResign);
        }

        // subscribe for "join" and "resign" interactions
        FederateJoinInteraction.subscribe_interaction(getRTI());
        FederateResignInteraction.subscribe_interaction(getRTI());

        notifyFederationOfJoin();

        // TODO: overview this later
        SimEnd.publish_interaction(getRTI());
        SimPause.publish_interaction(getRTI());
        SimResume.publish_interaction(getRTI());
    }

    private void handleInteractionClass_InteractionRoot_C2WInteractionRoot_FederateJoinInteraction(
            InteractionRoot interactionRoot
    ) {
        FederateJoinInteraction federateJoinInteraction = (FederateJoinInteraction)interactionRoot;
        _logger.trace("FederateJoinInteraction received :: {} joined", federateJoinInteraction.toString());

        // ??
        _federationEventsHandler.handleEvent(IC2WFederationEventsHandler.C2W_FEDERATION_EVENTS.FEDERATE_JOINED, federateJoinInteraction.get_FederateId());

        federatesMaintainer.federateJoined(new FederateInfo(federateJoinInteraction.get_FederateId(), federateJoinInteraction.get_FederateType(), federateJoinInteraction.get_IsLateJoiner()));
    }

    private void handleInteractionClass_InteractionRoot_C2WInteractionRoot_FederateResignInteraction(
            InteractionRoot interactionRoot
    ) {
        FederateResignInteraction federateResignInteraction = (FederateResignInteraction)interactionRoot;

        _logger.trace("FederateResignInteraction received :: {} resigned", federateResignInteraction.toString());

        // ??
        _federationEventsHandler.handleEvent(IC2WFederationEventsHandler.C2W_FEDERATION_EVENTS.FEDERATE_RESIGNED, federateResignInteraction.get_FederateId());

        FederateInfo federateInfo = federatesMaintainer.getFederateInfo(federateResignInteraction.get_FederateId());
        federatesMaintainer.federateResigned(federateInfo);
    }

    private void checkMonitoredInteractions(InteractionRoot interactionRoot) {
        if (_experimentConfig.MonitoredInteractions.contains(interactionRoot.getInstanceHlaClassName())) {
            System.out.println("Received Monitored Interaction: " + interactionRoot);
            _logger.info("Received Monitored Interaction: {}", interactionRoot);
        }
    }

    private void checkInteraction(InteractionRoot interactionRoot) {

        checkMonitoredInteractions(interactionRoot);
        if (interactionRoot.isInstanceHlaClassDerivedFromHlaClass("InteractionRoot.C2WInteractionRoot.FederateJoinInteraction")) {

            handleInteractionClass_InteractionRoot_C2WInteractionRoot_FederateJoinInteraction(interactionRoot);
            return;
        }

        if (interactionRoot.isInstanceHlaClassDerivedFromHlaClass("InteractionRoot.C2WInteractionRoot.FederateResignInteraction")) {

            handleInteractionClass_InteractionRoot_C2WInteractionRoot_FederateResignInteraction(interactionRoot);
            return;
        }

        _logger.debug("unhandled interaction: {}", interactionRoot.getJavaClassName());

    }

    private void checkReceivedSubscriptions() {

        InteractionRoot interactionRoot;
        while ((interactionRoot = getNextInteractionNoWait()) != null) {
            if (_coaExecutor != null) {
                _coaExecutor.updateArrivedInteractions(interactionRoot);
            }
            checkInteraction(interactionRoot);
        }

        ObjectRoot.ObjectReflector reflector;
        while ((reflector = getNextObjectReflectorNoWait()) != null) {
            reflector.reflect();
            ObjectRoot objectRoot = reflector.getObjectRoot();

            _logger.debug("unhandled object reflection: \"{}\"", objectRoot.getInstanceHlaClassName());
        }

    }

        /**
         * Start the federation run - federation that has been created already in the initializeRTI() -- TEMP comment, needs to be refactored
         *
         * @throws Exception general exception handling
         */
    private synchronized void startFederationRun() throws Exception {
        _federationAttempted = true;

        waitExpectedFederatesToJoin();

        if (_useSyncPoints) {
            _logger.trace("Waiting for \"{}\"...", SynchronizationPoints.ReadyToPopulate);
            readyToPopulate();
            _logger.trace("{} done.", SynchronizationPoints.ReadyToPopulate);

            _logger.trace("Waiting for \"{}\"...", SynchronizationPoints.ReadyToRun);
            readyToRun();
            _logger.trace("{} done.", SynchronizationPoints.ReadyToRun);
        }

        _federationEventsHandler.handleEvent(IC2WFederationEventsHandler.C2W_FEDERATION_EVENTS.FEDERATION_READY_TO_RUN, _federationId);

        // SEND OUT "INITIALIZATION INTERACTIONS," WHICH ARE SUPPOSED TO BE "RECEIVE" ORDERED.
        for (InteractionRoot interactionRoot : _initialization_interactions) {
            _logger.trace("Sending {} interaction.", interactionRoot.getSimpleClassName());
            sendInteraction(interactionRoot);
        }

        // TODO: eliminate this #18
        updateLogLevel(_logLevelNumToSet);

        // set time
        DoubleTime doubleTime = new DoubleTime();
        doubleTime.setTo(getRTI().queryFederateTime());
        _currentTime = doubleTime.getTime();
        resetTimeOffset();

        // run rti on a spearate thread
        Thread mainFederationManagerRunThread = new Thread(() -> {

            try {
                AdvanceTimeRequest atr = new AdvanceTimeRequest(_currentTime);
                putAdvanceTimeRequest(atr);

                startAdvanceTimeThread();
                _logger.info("started logical time progression");

                recordMainExecutionLoopStartTime();

                int numStepsExecuted = 0;
                while (_running && !exitCondition) {
                    if (!_paused) {
                        atr.requestSyncStart();
                    }

                    if (_realTimeMode) {
                        long sleep_time = _time_in_millisec - (_time_diff + System.currentTimeMillis());
                        while (sleep_time > 0 && _realTimeMode) {
                            long local_sleep_time = sleep_time;
                            if (local_sleep_time > 1000) local_sleep_time = 1000;
                            CpswtUtils.sleep(local_sleep_time);
                            sleep_time = _time_in_millisec - (_time_diff + System.currentTimeMillis());
                        }
                    }

                    if (!_paused) {

                        sendScriptInteractions();

                        checkReceivedSubscriptions();

                        if(_coaExecutor != null) {
                           _coaExecutor.executeCOAGraph();
                        }

                        if (_realTimeMode) {
                            _time_diff = _time_in_millisec - System.currentTimeMillis();
                        }

                        numStepsExecuted++;

                        // if we passed next pause time go to pause mode
                        Iterator<Double> it = _pauseTimes.iterator();
                        if (it.hasNext()) {
                            double pause_time = it.next();
                            if (_currentTime > pause_time) {
                                it.remove();
                                pauseSimulation();
                            }
                        }

                        if (numStepsExecuted == 10) {
                            _logger.info("Federation manager current time = {}", _currentTime);
                            numStepsExecuted = 0;
                        }
                    } else {
                        CpswtUtils.sleep(10);
                    }

                    // If we have reached federation end time (if it was configured), terminate the federation
                    if (_federationEndTime > 0 && _currentTime > _federationEndTime) {
                        _federationEventsHandler.handleEvent(
                                IC2WFederationEventsHandler.C2W_FEDERATION_EVENTS.FEDERATION_SIMULATION_FINISHED,
                                _federationId
                        );
                        terminateSimulation();
                    }

                    if (exitCondition) {
                        terminateAdvanceTimeThread(atr);
                    } else if (_running && !_paused) {
                        double previousTime = _currentTime;
                        _currentTime += super.getStepSize();

                        _logger.info(
                                "Current_time = {} and step = {} and requested_time = {}",
                                previousTime, getStepSize(), _currentTime
                        );
                        AdvanceTimeRequest newATR = new AdvanceTimeRequest(_currentTime);
                        putAdvanceTimeRequest(newATR);
                        atr.requestSyncEnd();
                        atr = newATR;
                    }
                }
                _federationEventsHandler.handleEvent(
                        IC2WFederationEventsHandler.C2W_FEDERATION_EVENTS.FEDERATION_SIMULATION_FINISHED,
                        _federationId
                );
//                prepareForFederatesToResign();

                if(_useSyncPoints) {
                    _logger.info("Waiting for \"ReadyToResign\" ... ");
                    readyToResign();
                    _logger.info("Done with resign");
                }

//                waitForFederatesToResign();

                while(getFederateState() != FederateState.TERMINATED) {
                    CpswtUtils.sleepDefault();
                }

                // destroy federation
                getRTI().resignFederationExecution(ResignAction.DELETE_OBJECTS);
                getRTI().destroyFederationExecution(_federationId);
                destroyRTI();
                _logLevelNum = 0;

                // In case some federate is still hanging around
//                killEntireFederation();
            } catch (Exception e) {
                _logger.error(e.getMessage());
            }
        });

        _running = true;

        mainFederationManagerRunThread.start();

        while(_running) {
            if(!_paused) {
                InteractionRoot interactionRoot;
                while ((interactionRoot = getNextInteractionWithoutTimeNoWait()) != null) {
                    checkInteraction(interactionRoot);
                }

                maintainFederatesFromFederationManifest();
            }

            CpswtUtils.sleep(CpswtDefaults.MaintainFederatesLoopWaitTimeMillis);
        }
    }

    private void waitExpectedFederatesToJoin() {
        FederateObject.subscribe_FederateHandle_attribute();
        FederateObject.subscribe_FederateType_attribute();
        FederateObject.subscribe_FederateHost_attribute();
        FederateObject.subscribe_object(getRTI());

        for (FederateJoinInfo federateInfo : _experimentConfig.expectedFederates) {
            _logger.trace("Waiting for {} federate{} of type \"{}\" to join", federateInfo.count, federateInfo.count <= 1 ? "" : "s", federateInfo.federateType);
        }

        int numOfFedsToWaitFor = federatesMaintainer.expectedFederatesLeftToJoinCount();
        while (numOfFedsToWaitFor > 0) {
            try {
                rti.tick();
                checkReceivedSubscriptions();
                CpswtUtils.sleep(SynchronizedFederate.internalThreadWaitTimeMs);
                numOfFedsToWaitFor = federatesMaintainer.expectedFederatesLeftToJoinCount();
            }
            catch(Exception e) {
                CpswtUtils.sleep(SynchronizedFederate.internalThreadWaitTimeMs);
            }
        }
        _logger.debug("All expected federates have joined the federation. Proceeding with the simulation...");
    }

    private void prepareForFederatesToResign() {

        for (FederateInfo federateInfo : federatesMaintainer.getOnlineExpectedFederates()) {
            _logger.info("Waiting for \"{}\" federate to resign ...", federateInfo.getFederateId());
        }
    }

    private void waitForFederatesToResign() {
        while (federatesMaintainer.getOnlineExpectedFederates().size() != 0) {
//            getRTI().tick();
            CpswtUtils.sleep(SynchronizedFederate.internalThreadWaitTimeMs);
        }
        _logger.info("All federates have resigned the federation.  Simulation terminated.\n");
    }

    private void sendScriptInteractions() {
        double tmin = _currentTime + getLookahead() + (getLookahead() / 10000.0);

        for (double intrtime : _script_interactions.keySet()) {
            _logger.trace("Interaction time = {}", intrtime);
            List<InteractionRoot> interactionRootList = _script_interactions.get(intrtime);
            if (interactionRootList.size() == 0)
                continue;
            if (intrtime < tmin) {

                StringBuilder interactionClassList = new StringBuilder();
                boolean notFirst = false;
                for (InteractionRoot interactionRoot : interactionRootList) {
                    if (notFirst) interactionClassList.append(", ");
                    notFirst = true;
                    interactionClassList.append(interactionRoot.getInstanceHlaClassName());
                }
                _logger.error("Error: simulation passed scheduled interaction time: {}, {}", intrtime, interactionClassList.toString());
            } else if (intrtime >= tmin && intrtime < tmin + getStepSize()) {

                List<InteractionRoot> interactionsSent = new ArrayList<>();
                for (InteractionRoot interactionRoot : interactionRootList) {
                    try {
                        sendInteraction(interactionRoot, intrtime);
                    } catch (Exception e) {
                        _logger.error("Failed to send interaction: {}", interactionRoot);
                        _logger.error(e.getStackTrace());
                    }
                    interactionsSent.add(interactionRoot);
                    _logger.info("Sending out the injected interaction");
                }
                interactionRootList.removeAll(interactionsSent);
            }
        }
    }

    private void resetTimeOffset() {
        _time_in_millisec = (long) (_currentTime * 1000);
        _time_diff = _time_in_millisec - System.currentTimeMillis();
    }

    public double getCurrentTime() {
        return _currentTime;
    }

    public boolean isRunning() {
        return _running;
    }

    public boolean isPaused() {
        return _paused;
    }

    public boolean federationAlreadyAttempted() {
        return _federationAttempted;
    }

    public void startSimulation() throws Exception {
        _logger.debug("Starting simulation");
        setFederateState(FederateState.STARTING);
        if (!federationAlreadyAttempted()) {
            startFederationRun();
        }
        _paused = false;
        setFederateState(FederateState.RUNNING);
    }

    public void pauseSimulation() {
        _logger.debug("Pausing simulation");
        _paused = true;
        setFederateState(FederateState.PAUSED);
    }

    public void resumeSimulation() {
        _time_diff = _time_in_millisec - System.currentTimeMillis();
        _logger.debug("Resuming simulation");
        _paused = false;
        setFederateState(FederateState.RESUMED);
    }

    public void terminateSimulation() {

        _logger.debug("Terminating simulation");
        _killingFederation = true;
        recordMainExecutionLoopEndTime();
        setFederateState(FederateState.TERMINATING);

        synchronized (rti) {
            try {
                SimEnd e = new SimEnd();
                double tmin = _currentTime + getLookahead();
                sendInteraction(e, tmin);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        _running = false;
        _paused = false;

        // Wait for 2 seconds for SimEnd to reach others
        CpswtUtils.sleep(CpswtDefaults.SimEndWaitingTimeMillis);

        _logger.info("Simulation terminated");

        setFederateState(FederateState.TERMINATED);

        // Wait for 10 seconds for Simulation to gracefully exit
        CpswtUtils.sleep(2000);

        // If simulation has still not exited gracefully, run kill command
        killEntireFederation();
    }

    public void killEntireFederation() {
        _killingFederation = true;

        recordMainExecutionLoopEndTime();

        System.exit(0);
    }

    public void setRealTimeMode(boolean newRealTimeMode) {
        _logger.debug("Setting simulation to run in realTimeMode as: {}", newRealTimeMode);
        _realTimeMode = newRealTimeMode;
        if (_realTimeMode)
            resetTimeOffset();
    }

    /**
     * LogLevels 0: No logging 1: High priority logs 2: Up to medium priority
     * logs 3: Up to low priority logs 4: Up to very low priority logs (all
     * logs)
     */
    public void updateLogLevel(int selected) {
        _logLevelNumToSet = selected;

        if (getRTI() == null) {
            return;
        }

        if (_logLevelNum != selected) {
            if (_logLevelNum > selected) {
                // Unsubscribe lower logger levels
                for (int i = _logLevelNum; i > selected; i--) {
                    unsubscribeLogLevel(i);
                }
            } else {
                // Subscribe lower logger levels
                for (int i = _logLevelNum + 1; i <= selected; i++) {
                    subscribeLogLevel(i);
                }
            }
            _logLevelNum = selected;
        }
    }

    private void unsubscribeLogLevel(int level) {
        if (level > 0) {
            if (level == 1) {
                _logger.debug("Unsusbcribing to High priority logs");
                HighPrio.unsubscribe_interaction(getRTI());
            } else if (level == 2) {
                _logger.debug("Unsusbcribing to Medium priority logs");
                MediumPrio.unsubscribe_interaction(getRTI());
            } else if (level == 3) {
                _logger.debug("Unsusbcribing to Low priority logs");
                LowPrio.unsubscribe_interaction(getRTI());
            } else if (level == 4) {
                _logger.debug("Unsusbcribing to Very Low priority logs");
                VeryLowPrio.unsubscribe_interaction(getRTI());
            }
        }
    }

    private void subscribeLogLevel(int level) {
        if (level > 0) {
            if (level == 1) {
                _logger.debug("Subscribing to High priority logs");
                HighPrio.subscribe_interaction(getRTI());
            } else if (level == 2) {
                _logger.debug("Subscribing to Medium priority logs");
                MediumPrio.subscribe_interaction(getRTI());
            } else if (level == 3) {
                _logger.debug("Subscribing to Low priority logs");
                LowPrio.subscribe_interaction(getRTI());
            } else if (level == 4) {
                _logger.debug("Subscribing to Very Low priority logs");
                VeryLowPrio.subscribe_interaction(getRTI());
            }
        }
    }

    @Override
    public void synchronizationPointRegistrationSucceeded(String label) {
        _synchronizationLabels.add(label);
    }


    @Override
    public void removeObjectInstance( int theObject, byte[] userSuppliedTag, LogicalTime theTime,
                                      EventRetractionHandle retractionHandle ) {
        _logger.error(" removeObjectInstance --------------------------------------------------------- NOT IMPLEMENTED");
    }

    @Override
    public void onTerminateRequested() {
        terminateSimulation();
    }

    @Override
    public double onCurrentTimeRequested() {
        return getCurrentTime();
    }

    public List<FederateInfo> getFederatesStatus() {
        return federatesMaintainer.getAllMaintainedFederates();
    }

    public static void main(String[] args) {

        FederationManagerConfig federationManagerConfig;
        FederateConfigParser federateConfigParser = new FederateConfigParser();

        System.out.println("args:");
        for(int ix = 0 ; ix < args.length; ++ix) {
            System.out.println("args[" + ix + "] = \"" + args[ix] + "\"");
        }
        federationManagerConfig = federateConfigParser.parseArgs(args, FederationManagerConfig.class);

        try {
            FederationManager federationManager = new FederationManager(federationManagerConfig);
            federationManager.startSimulation();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

}
