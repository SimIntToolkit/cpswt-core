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
 * @author Harmon Nine
 * 
*/

package org.cpswt.hla;

import org.cpswt.hla.base.*;
import org.cpswt.utils.FederateIdUtility;
import hla.rti.ArrayIndexOutOfBounds;
import hla.rti.AsynchronousDeliveryAlreadyEnabled;
import hla.rti.EnableTimeConstrainedPending;
import hla.rti.EnableTimeRegulationPending;
import hla.rti.EventRetractionHandle;
import hla.rti.FederateAlreadyExecutionMember;
import hla.rti.FederateNotExecutionMember;
import hla.rti.FederateOwnsAttributes;
import hla.rti.InvalidFederationTime;
import hla.rti.InvalidLookahead;
import hla.rti.InvalidResignAction;
import hla.rti.LogicalTime;
import hla.rti.RTIambassador;
import hla.rti.RTIinternalError;
import hla.rti.ReceivedInteraction;
import hla.rti.ReflectedAttributes;
import hla.rti.ResignAction;
import hla.rti.SynchronizationLabelNotAnnounced;
import hla.rti.TimeConstrainedAlreadyEnabled;
import hla.rti.TimeRegulationAlreadyEnabled;
import hla.rti.jlc.NullFederateAmbassador;
import hla.rti.jlc.RtiFactory;
import hla.rti.jlc.RtiFactoryFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.PriorityBlockingQueue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cpswt.hla.base.*;
import org.cpswt.config.FederateConfig;
import org.portico.impl.hla13.types.DoubleTime;
import org.portico.impl.hla13.types.DoubleTimeInterval;

/**
 * SynchronizedFederate is a class that simplifies interaction with the RTI.
 * <br/><br/>
 * All C2 Wind Tunnel federates should inherit, either directly or indirectly,
 * from SynchronizedFederate.  That is, the inheritance hierarchy for a federate
 * called MYFED should be (leaving out the NullFederateAmbassador from which
 * SynchronizedFederate inherits):
 * <br/><br/>
 * SynchronizedFederate <-- MYFEDBase (Automatically generated) <-- MYFED
 * <br/><br/>
 * or, in the case of using the Melder:
 * <br/><br/>
 * SynchronizedFederate <-- MYFED (melder package) <-- MYFEDBase (Automatically generated) <-- MYFED (scenario package)
 * <br/><br/>
 * The SynchonizedFederate provides the following facilities which simplify the
 * writing of a federate:
 * <ul>
 * <li>RTI creation/destruction ( {@link #createLRC()}, {@link #destroyRTI()} )</li>
 * <li>A means of acquiring a handle to the RTI ( {@link #getLRC()} )</li>
 * <li>Joining a federation ( {@link #joinFederation()} )</li>
 * <li>Time-constrained enable ( {@link #enableTimeConstrained()} )</li>
 * <li>Time-regulating enable ( {@link #enableTimeRegulation(double)}, {@link #enableTimeRegulation(double, double)} )</li>
 * <li>Asynchronous delivery enable ( {@link #enableAsynchronousDelivery()} )</li>
 * <li>3 standard synchronization points:  "Populate" "Run" and "Resign" and the
 * ability to indicate when your federate has achieved them
 * ( {@link #readyToPopulate()}, {@link #readyToRun()}, {@link #readyToResign()} )</li>
 * <li>Current federate time ( {@link #getCurrentTime()} )</li>
 * <li>Queuing mechanism for incoming RTI-interactions ( {@link #getNextInteraction()},
 * {@link #getNextInteractionNoWait()} ), which simplifies receiving these
 * interactions and avoids the possibility of a ConcurrentAccessAttempted exception.</li>
 * <li>Mechanism for discovering new object-class instances, as well as a queuing
 * mechanism for incoming attribute-reflections ( {@link #getNextObjectReflector()},
 * {@link #getNextObjectReflectorNoWait()} ), which simplifies discovering objects
 * and reflecting their attributes, as well as avoid ConcurrentAccessAttempted
 * exceptions.</li>
 * <li>A means for requesting specific federation times and synchronizing with the
 * federation at these times to send interactions and attribute updates
 * ( {@link AdvanceTimeThread}, {@link AdvanceTimeRequest},
 * {@link #putAdvanceTimeRequest(AdvanceTimeRequest)},
 * {@link AdvanceTimeRequest#requestSyncStart()}, {@link AdvanceTimeRequest#requestSyncEnd()},
 * {@link #startAdvanceTimeThread()} )</li>
 * </ul>
 *
 * @author Harmon Nine
 */
public class SynchronizedFederate extends NullFederateAmbassador {

    public CpswtFederateInfoObject federateInfo;

    private static final Logger LOG = LogManager.getLogger(SynchronizedFederate.class);
    public static final int internalThreadWaitTimeMs = 250;

    /**
     * Local RTI component. This is where you submit the "requests"
     * to the RTIExec process that manages the whole federation.
     */
    protected RTIambassador lrc;

    public static final String FEDERATION_MANAGER_NAME = "FederationManager";

    public static int logId = 0;

    private Set<String> _achievedSynchronizationPoints = new HashSet<String>();

    private boolean _timeConstrainedNotEnabled = true;
    private boolean _timeRegulationNotEnabled = true;
    private boolean _simEndNotSubscribed = true;
    private boolean _timeAdvanceNotGranted = true;
    private boolean _advanceTimeThreadNotStarted = true;

    /**
     * General federate parameters
     */
    private final String federateId;
    private final String federationId;

    private final String federateType;
    public String getFederateType() { return this.federateType; }

    protected final int federateRTIInitWaitTime;

    private double lookAhead = 0.0;
    public double getLookAhead() {
        return lookAhead;
    }
    public void setLookAhead(double lookAhead) {
        this.lookAhead = lookAhead;
    }

    private final boolean isLateJoiner;
    public boolean isLateJoiner() { return this.isLateJoiner; }

    private double stepSize;
    public double getStepSize() { return this.stepSize; }
    private void setStepSize(double stepSize) { this.stepSize = stepSize; }

    public SynchronizedFederate(FederateConfig federateConfig) {
        this.federateRTIInitWaitTime = federateConfig.federateRTIInitWaitTimeMs;
        this.federateType = federateConfig.federateType;
        this.federationId = federateConfig.federationId;
        this.isLateJoiner = federateConfig.isLateJoiner;
        this.lookAhead = federateConfig.lookAhead;
        this.stepSize = federateConfig.stepSize;

        this.federateId = FederateIdUtility.generateID(this.federateType);
    }

    // -----------------------------------------------------------------------------------------------------------------

    protected FederateState federateState = FederateState.INITIALIZING;
    public FederateState getFederateState() {
        return this.federateState;
    }
    public boolean setFederateState(FederateState newState) {

        // TODO: add Mutex

        if(this.federateState.CanTransitionTo(newState)) {
            FederateState prevState = this.federateState;
            this.federateState = newState;

            // fire FederateStateChanged event - notify listeners
            this.fireFederateStateChanged(prevState, newState);

            return true;
        }
        return false;
    }

    /**
     * Event listeners for FederateStateChange events
     */
    private List<FederateStateChangeListener> federateChangeEventListeners = new ArrayList<>();

    /**
     * Get a handle to the RTI.
     *
     * @return handle (of type RTIambassador) to the RTI.  This can be used as
     * an argument to {@link InteractionRoot#sendInteraction(RTIambassador)} or
     * {@link ObjectRoot#updateAttributeValues(RTIambassador)} calls, for instance.
     */
    public RTIambassador getLRC() {
        return this.lrc;
    }

    /**
     * DO NOT USE -- Should only be used directly by the SynchronizedFederate class.
     * Returns the value of the "timeAdvanceNotGranted" flag.
     *
     * @return true if the requested time advance has not yet been granted, false
     * otherwise.
     */
    public boolean getTimeAdvanceNotGranted() {
        return _timeAdvanceNotGranted;
    }

    /**
     * DO NOT USE -- Should only be used directly by the SynchronizedFederate class.
     * Sets the value of the "timeAdvanceNotGranted" flag.
     *
     * @param timeAdvanceNotGranted value to give to the "timeAdvanceNotGranted"
     *                              flag.
     */
    public void setTimeAdvanceNotGranted(boolean timeAdvanceNotGranted) {
        _timeAdvanceNotGranted = timeAdvanceNotGranted;
    }

    public void createLRC() throws RTIinternalError {

        LOG.debug("Federate {} acquiring connection to RTI ...", this.federateId);
        RtiFactory factory = RtiFactoryFactory.getRtiFactory();
        this.lrc = factory.createRtiAmbassador();
        LOG.debug("Federate {} connection to RTI successful.", this.federateId);
    }

    /**
     * Dissociate from the RTI.  This sets the handle to the RTI acquired via
     * {@link #createLRC} to null.  Thus, {@link #getLRC()} returns null after
     * this call.
     */
    public void destroyRTI() {
        lrc = null;
    }

    /**
     * Joins the federate to a particular federation.
     */
    public void joinFederation() {
        boolean federationNotPresent = true;
        while (federationNotPresent) {
            try {
                LOG.debug("[{}] federate joining federation [{}]", this.federateId, this.federationId);
                synchronized (lrc) {
                    this.lrc.joinFederationExecution(this.federateId, this.federationId, this, null);
                }
                federationNotPresent = false;
                LOG.debug("[{}] federate joined federation [{}] successfully", this.federateId, this.federationId);
            } catch (FederateAlreadyExecutionMember f) {
                LOG.error("Federate already execution member: {}", f);
                return;
            } catch (Exception e) {
                LOG.error("General error while trying to join federation. {}", e);
            }
        }
    }

    public void publishFederateInfoObject() {
        synchronized (this.lrc) {
            CpswtFederateInfoObject.publish_FederateId();
            CpswtFederateInfoObject.publish_FederateType();
            CpswtFederateInfoObject.publish_IsLateJoiner();
            CpswtFederateInfoObject.publish(this.lrc);

            this.federateInfo = new CpswtFederateInfoObject();
            federateInfo.set_FederateId(this.federateId);
            federateInfo.set_FederateType(this.federateType);
            federateInfo.set_IsLateJoiner(this.isLateJoiner);
            federateInfo.registerObject(this.lrc);
        }
    }

    /**
     * Returns the id (name) of the federation in which this federate is running.
     */
    public String getFederationId() {
        return federationId;
    }

    /**
     * Returns the id (name) of this federate as registered with the federation
     * in which it is running.
     */
    public String getFederateId() {
        return federateId;
    }

    /**
     * RTI callback -- DO NOT OVERRIDE.  SynchonizedFederate class uses this
     * method to detect that the RTI has made this federate time-constrained.
     */
    @Override
    public void timeConstrainedEnabled(LogicalTime t) {
        _timeConstrainedNotEnabled = false;
    }

    /**
     * When a federate calls this method, it becomes time-constrained within
     * its federation.
     */
    public void enableTimeConstrained() throws FederateNotExecutionMember {
        if (!_timeConstrainedNotEnabled) return;

        boolean timeConstrainedEnabledNotCalled = true;
        while (timeConstrainedEnabledNotCalled) {
            try {
                synchronized (lrc) {
                    lrc.enableTimeConstrained();
                }
                timeConstrainedEnabledNotCalled = false;
            } catch (TimeConstrainedAlreadyEnabled t) {
                return;
            } catch (EnableTimeConstrainedPending e) {
                timeConstrainedEnabledNotCalled = false;
            } catch (FederateNotExecutionMember f) {
                throw f;
            } catch (Exception e) {
                try {
                    Thread.sleep(SynchronizedFederate.internalThreadWaitTimeMs);
                } catch (Exception e2) {
                }
            }
        }

        try {
            synchronized (lrc) {
                lrc.tick();
            }
        } catch (Exception e) {
        }
        while (_timeConstrainedNotEnabled) {
            try {
                Thread.sleep(SynchronizedFederate.internalThreadWaitTimeMs);
            } catch (Exception e) {
            }
            try {
                synchronized (lrc) {
                    lrc.tick();
                }
            } catch (Exception e) {
            }
        }
    }

    /**
     * RTI callback -- DO NOT OVERRIDE.  SynchonizedFederate class uses this
     * method to detect that the RTI has made this federate time-regulating.
     */
    @Override
    public void timeRegulationEnabled(LogicalTime t) {
        _timeRegulationNotEnabled = false;
    }


    /**
     * When a federate calls this method, it becomes time-regulating within
     * its federation.
     *
     * @param time      time at which federate wishes to become time-regulating
     * @param lookahead look-ahead associated with this federate
     */
    public void enableTimeRegulation(double time, double lookahead)
            throws InvalidFederationTime, InvalidLookahead, FederateNotExecutionMember {

        if (!_timeRegulationNotEnabled) return;

        boolean timeRegulationEnabledNotCalled = true;
        while (timeRegulationEnabledNotCalled) {
            try {
                synchronized (lrc) {
                    lrc.enableTimeRegulation(new DoubleTime(time), new DoubleTimeInterval(lookahead));
                }
                timeRegulationEnabledNotCalled = false;
            } catch (TimeRegulationAlreadyEnabled t) {
                return;
            } catch (EnableTimeRegulationPending e) {
                timeRegulationEnabledNotCalled = false;
            } catch (FederateNotExecutionMember f) {
                throw f;
            } catch (InvalidFederationTime i) {
                throw i;
            } catch (InvalidLookahead i) {
                throw i;
            } catch (Exception e) {
                try {
                    Thread.sleep(SynchronizedFederate.internalThreadWaitTimeMs);
                } catch (Exception e2) {
                }
            }
        }

        try {
            synchronized (lrc) {
                lrc.tick();
            }
        } catch (Exception e) {
        }
        while (_timeRegulationNotEnabled) {
            try {
                Thread.sleep(SynchronizedFederate.internalThreadWaitTimeMs);
            } catch (Exception e) {
            }
            try {
                synchronized (lrc) {
                    lrc.tick();
                }
            } catch (Exception e) {
            }
        }
    }

    /**
     * Ensures that the federate is subscribed to SimEnd interaction.
     */
    private void ensureSimEndSubscription() {

        if (_simEndNotSubscribed) {
            // Auto-subscribing also ensures that there is no filter set for SimEnd
            SimEnd.subscribe(getLRC());
            _simEndNotSubscribed = false;
        }
    }

    /**
     * Same as {@link #enableTimeRegulation(double, double)}, where the first
     * argument (that is, "time") is set to 0.
     *
     * @param lookahead look-ahead associated with this federate
     */
    public void enableTimeRegulation(double lookahead)
            throws InvalidFederationTime, InvalidLookahead, FederateNotExecutionMember {
        enableTimeRegulation(0, lookahead);
    }

    /**
     * Enables asynchronous delivery for the federate.
     */
    public void enableAsynchronousDelivery() {

        boolean asynchronousDeliveryNotEnabled = true;
        while (asynchronousDeliveryNotEnabled) {
            try {
                this.lrc.enableAsynchronousDelivery();
                asynchronousDeliveryNotEnabled = false;
            } catch (FederateNotExecutionMember f) {
                System.err.println("ERROR:  Could not enable asynchronous delivery:  Federate Not Execution Member");
                f.printStackTrace();
                return;
            } catch (AsynchronousDeliveryAlreadyEnabled a) {
                return;
            } catch (Exception e) {
                System.err.println("WARNING:  problem encountered enabling asynchronous delivery:  retry");
                e.printStackTrace();
                try {
                    Thread.sleep(SynchronizedFederate.internalThreadWaitTimeMs);
                } catch (Exception e1) {
                }
            }
        }
    }

    public static final int DELETE_OBJECTS = ResignAction.DELETE_OBJECTS;
    public static final int DELETE_OBJECTS_AND_RELEASE_ATTRIBUTES = ResignAction.DELETE_OBJECTS_AND_RELEASE_ATTRIBUTES;
    public static final int NO_ACTION = ResignAction.NO_ACTION;
    public static final int RELEASE_ATTRIBUTES = ResignAction.RELEASE_ATTRIBUTES;

    /**
     * Resigns federate from the federation execution.
     *
     * @param resignAction action to be performed when resigning.  This must be
     *                     one of the following:
     *                     <p>
     *                     DELETE_OBJECTS
     *                     DELETE_OBJECTS_AND_RELEASE_ATTRIBUTES
     *                     NO_ACTION
     *                     RELEASE_ATTRIBUTES
     */
    public void resignFederationExecution(int resignAction) {

        boolean federationNotResigned = true;
        while (federationNotResigned) {
            try {
                getLRC().resignFederationExecution(resignAction);
                federationNotResigned = false;
            } catch (InvalidResignAction i) {
                System.err.println("WARNING:  Invalid resign action when attempting to resign federation.  Changing resign action to DELETE_OBJECTS_AND_RELEASE_ATTRIBUTES.");
                resignAction = DELETE_OBJECTS_AND_RELEASE_ATTRIBUTES;
            } catch (FederateNotExecutionMember f) {
                System.err.println("WARNING:  While resigning federation:  federate not execution member.");
                return;
            } catch (FederateOwnsAttributes f) {
                System.err.println("WARNING:  While resigning federation:  federate owns attributes.  Releasing attributes.");
                resignAction |= RELEASE_ATTRIBUTES;
            } catch (Exception e) {
                System.err.println("WARNING:  problem encountered while resigning federation execution:  retry");
                e.printStackTrace();
                try {
                    Thread.sleep(SynchronizedFederate.internalThreadWaitTimeMs);
                } catch (Exception e1) {
                }
            }
        }
    }

    /**
     * Resigns federate from the federation execution with resign action of
     * DELETE_OBJECTS_AND_RELEASE_ATTRIBUTES.
     */
    public void resignFederationExecution() {
        resignFederationExecution(DELETE_OBJECTS_AND_RELEASE_ATTRIBUTES);
    }

    /**
     * RTI callback -- DO NOT OVERRIDE.  SynchronizedFederate uses this method
     * to determine when a synchronization point has been reached by the
     * federation.
     */
    @Override
    public void federationSynchronized(String label) {
        _achievedSynchronizationPoints.add(label);
    }

    /**
     * Federate should call this method when it has reached an initial stage in
     * processing that allows it to "populate" its data structures with
     * initializing data from other federates in the federation.  This is before
     * full simulation execution begins, and causes the federate to suspend
     * execution until all federates in the federation have called this method,
     * that is, are "ready to populate."
     *
     * @throws FederateNotExecutionMember
     * @throws RTIinternalError
     */
    public void readyToPopulate() throws FederateNotExecutionMember, RTIinternalError {
        ensureSimEndSubscription();

        achieveSynchronizationPoint(SynchronizationPoints.ReadyToPopulate);
    }

    /**
     * Federate should call this method when it has reached a point in execution
     * where it is ready to run the simulation.  It will cause the federate to
     * suspend execution until all other federates in the federation called this
     * method, that is, they also are ready to run the simulation.
     *
     * @throws FederateNotExecutionMember
     * @throws RTIinternalError
     */
    public void readyToRun() throws FederateNotExecutionMember, RTIinternalError {
        achieveSynchronizationPoint(SynchronizationPoints.ReadyToRun);
    }

    /**
     * Federate should call this method when it has reached a point in execution
     * where it is ready to terminate the simulation.  It will cause the federate
     * to suspend execution until all other federates in the federation called this
     * method, that is, they also are ready to terminate the simulation.
     *
     * @throws FederateNotExecutionMember
     * @throws RTIinternalError
     */
    public void readyToResign() throws FederateNotExecutionMember, RTIinternalError {
        achieveSynchronizationPoint(SynchronizationPoints.ReadyToResign);
    }

    private void achieveSynchronizationPoint(String label) throws FederateNotExecutionMember, RTIinternalError {
        boolean synchronizationPointNotAccepted = true;
        while (synchronizationPointNotAccepted) {
            try {
                synchronized (lrc) {
                    lrc.synchronizationPointAchieved(label);
                }
                while (!_achievedSynchronizationPoints.contains(label)) {
                    Thread.sleep(SynchronizedFederate.internalThreadWaitTimeMs);
                    synchronized (lrc) {
                        lrc.tick();
                    }
                }
                synchronizationPointNotAccepted = false;
            } catch (FederateNotExecutionMember f) {
                throw f;
            } catch (SynchronizationLabelNotAnnounced s) {
                if (_achievedSynchronizationPoints.contains(label)) {
                    synchronizationPointNotAccepted = false;
                } else {
                    synchronized (lrc) {
                        try {
                            lrc.tick();
                        } catch (RTIinternalError r) {
                            throw r;
                        } catch (Exception e) {
                            try {
                                Thread.sleep(SynchronizedFederate.internalThreadWaitTimeMs);
                            } catch (Exception e2) {
                            }
                        }
                    }
                }
            } catch (Exception e) {
                try {
                    Thread.sleep(SynchronizedFederate.internalThreadWaitTimeMs);
                } catch (Exception e2) {
                }
            }
        }
    }

    /**
     * RTI callback -- DO NOT OVERRIDE.  The SynchronizedFederate uses this method
     * to determine if a time to which it has requested the RTI advance has been
     * reached.
     */
    @Override
    public void timeAdvanceGrant(LogicalTime t) {
        _timeAdvanceNotGranted = false;
    }

    /**
     * Returns the current time for this federate.
     *
     * @return the current time for this federate
     */
    public double getCurrentTime() {
        LogicalTime logicalTime = null;
        boolean timeNotAcquired = true;
        while (timeNotAcquired) {
            try {
                synchronized (getLRC()) {
                    logicalTime = getLRC().queryFederateTime();
                }
                timeNotAcquired = false;
            } catch (FederateNotExecutionMember f) {
                System.err.println("SynchronizedFederate:  getCurrentTime:  ERROR:  Federate not execution member");
                f.printStackTrace();
                return -1;
            } catch (Exception e) {
                System.err.println("SynchronizedFederate:  getCurrentTime:  Exception caught:  " + e.getMessage());
                e.printStackTrace(System.err);
                return -1;
            }
        }

        DoubleTime doubleTime = new DoubleTime();
        doubleTime.setTo(logicalTime);
        return doubleTime.getTime();
    }

    /**
     * Returns the current Lower Bound on Time-Stamps (LBTS) for this federate.
     *
     * @return the current LBTS time for this federate
     */
    public double getLBTS() {
        LogicalTime lbtsTime = null;
        boolean timeNotAcquired = true;
        while (timeNotAcquired) {
            try {
                synchronized (getLRC()) {
                    lbtsTime = getLRC().queryLBTS();
                }
                timeNotAcquired = false;
            } catch (FederateNotExecutionMember f) {
                System.err.println("SynchronizedFederate:  getLBTS:  ERROR:  Federate not execution member");
                f.printStackTrace();
                return -1;
            } catch (Exception e) {
                System.err.println("SynchronizedFederate:  getLBTS:  Exception caught:  " + e.getMessage());
                e.printStackTrace(System.err);
                return -1;
            }
        }

        DoubleTime doubleTime = new DoubleTime();
        doubleTime.setTo(lbtsTime);
        return doubleTime.getTime();
    }

    /**
     * When sending an interaction in timestamp order, we should use
     * currentTime+Lookahead or LBTS whichever is greater. Current Portico RTI
     * implementation dictates to use a timestamp which is greater than or
     * equal to federate's LBTS.
     *
     * @return the timestamp to use for outgoing TSO interactions
     */
    public double getMinTSOTimestamp() {
        LogicalTime lbtsTime = null;
        LogicalTime logicalTime = null;
        boolean timeNotAcquired = true;
        while (timeNotAcquired) {
            try {
                synchronized (getLRC()) {
                    lbtsTime = getLRC().queryLBTS();
                    logicalTime = getLRC().queryFederateTime();
                }
                timeNotAcquired = false;
            } catch (FederateNotExecutionMember f) {
                System.err.println("SynchronizedFederate:  getMinTSOTimestamp:  ERROR:  Federate not execution member");
                f.printStackTrace();
                return -1;
            } catch (Exception e) {
                System.err.println("SynchronizedFederate:  getMinTSOTimestamp:  Exception caught:  " + e.getMessage());
                e.printStackTrace(System.err);
                return -1;
            }
        }

        DoubleTime dtLBTSTime = new DoubleTime();
        dtLBTSTime.setTo(lbtsTime);
        double dblLBTSTime = dtLBTSTime.getTime();

        DoubleTime dtLogicalTime = new DoubleTime();
        dtLogicalTime.setTo(logicalTime);
        double dblLogicalTime = dtLogicalTime.getTime();

        double timestampWithLogicalTime = dblLogicalTime + getLookAhead();

        if (dblLBTSTime > timestampWithLogicalTime)
            return dblLBTSTime;
        else
            return timestampWithLogicalTime;
    }

    private ATRQueue _atrQueue = new ATRQueue(100, new ATRComparator());

    /**
     * DO NOT USE -- Should only be used directly by the SyncronizedFederate class.
     * This method is used to access a queue of AdvanceTimeRequest objects.
     * AdvanceTimeRequests are placed on this queue using
     * {@link #putAdvanceTimeRequest(AdvanceTimeRequest)}
     * and are taken off the queue in order of requested time by the
     * {@link AdvanceTimeThread}
     */
    public ATRQueue getATRQueue() {
        return _atrQueue;
    }

    /**
     * Called by a federate to submit an {@link AdvanceTimeRequest} to the
     * {@link AdvanceTimeThread}.
     *
     * @param advanceTimeRequest
     */
    public final void putAdvanceTimeRequest(AdvanceTimeRequest advanceTimeRequest) {
        _atrQueue.put(advanceTimeRequest);
    }

    /**
     * Start the {@link AdvanceTimeThread}
     * Assumes the federate is a lookAhead value greater than zero. Uses
     * {@link hla.rti.RTIambassador#timeAdvanceRequest(LogicalTime)} for advancing
     * federates time.
     */
    protected void startAdvanceTimeThread() {
        if (_advanceTimeThreadNotStarted) {
            (new AdvanceTimeThread(this, this._atrQueue, TimeAdvanceMode.TimeAdvanceRequest)).start();
            _advanceTimeThreadNotStarted = false;
        }
    }

    /**
     * Start the {@link AdvanceTimeThread}
     *
     * @param #timeAdvanceMode If
     *                         {@link TimeAdvanceMode#TimeAdvanceRequestAvailable} or
     *                         {@link TimeAdvanceMode#NextEventRequestAvailable} is used, the
     *                         federate's lookAhead value is allowed to be zero. For other two cases,
     *                         federate's lookAhead must be greater than zero.
     */
    protected void startAdvanceTimeThread(TimeAdvanceMode timeAdvanceMode) {
        if (_advanceTimeThreadNotStarted) {
            (new AdvanceTimeThread(this, this._atrQueue, timeAdvanceMode)).start();
            _advanceTimeThreadNotStarted = false;
        }
    }

    private static PriorityBlockingQueue<InteractionRoot> _interactionQueue = new PriorityBlockingQueue<InteractionRoot>(10, new InteractionRootComparator());

    /**
     * DO NOT USE -- Should only be used directly by the SyncronizedFederate class.
     * This method places an interaction on a queue internal to the {@link InteractionRoot}
     * class.  Usu. this interaction has just been received from the RTI using
     * the {@link #receiveInteraction(int, ReceivedInteraction, byte[])} or
     * {@link #receiveInteraction(int, ReceivedInteraction, byte[], LogicalTime, EventRetractionHandle)}
     * callback method.
     *
     * @param interactionRoot reference to an interaction.  This could be an
     *                        instance of any interaction in the federation, as InteractionRoot will
     *                        always be its highest super class.
     */
    public static void addInteraction(InteractionRoot interactionRoot) {
        _interactionQueue.add(interactionRoot);
        // System.out.println("Received: " + interactionRoot);
    }

    /**
     * Gets the next interaction that was received from the RTI, waiting for an
     * interaction if none are currently available.  The interaction is retrieved
     * from a queue that is internal to the {@link InteractionRoot} class.  The
     * queue is ordered by the timestamp of the interactions, with "receive-order"
     * interactions being placed at the front of the queue (they are given a
     * timestamp of -1).
     * <p>
     * Note that the type of the reference returned by this method is always
     * "InteractionRoot", as this is the highest super-class for all interactions.
     * If a reference to the actual class of the interaction is desired, then
     * this InteractionRoot reference will have to be cast up the inheritance
     * hierarchy.
     *
     * @return the next interaction received from the RTI in order of timestamp,
     * where receive-order interactions have a timestamp of -1.
     */
    public static InteractionRoot getNextInteraction() {
        InteractionRoot interactionRoot = null;
        boolean takeNotComplete = true;
        while (takeNotComplete) {
            try {
                interactionRoot = _interactionQueue.take();
                takeNotComplete = false;
            } catch (InterruptedException i) {
            }

        }
        return interactionRoot;
    }

    /**
     * Returns a boolean value indicating that there are interactions from the
     * RTI that can be retrieved via the {@link #getNextInteraction()} or
     * {@link #getNextInteractionNoWait()} methods.
     *
     * @return true if there are interactions available on the queue internal
     * to the {@link InteractionRoot} class.  False, otherwise.
     */
    public static boolean isNotEmpty() {
        return !_interactionQueue.isEmpty();
    }

    /**
     * Like {@link #getNextInteraction()}, but returns immediately with a null
     * value if no interaction is available.
     *
     * @return the next interaction received from the RTI in order of timestamp,
     * where receive-order interactions have a timestamp of -1, or null if there
     * are no interactions currently available
     */
    public static InteractionRoot getNextInteractionNoWait() {
        InteractionRoot interactionRoot = _interactionQueue.poll();
        // System.out.println( "Removed interaction from queue (poll), size now = " + _interactionQueue.size() );
        return interactionRoot;
    }

    /**
     * This should be overridden in the base classes of all federates
     */
    public boolean isMapperFederate() {
        return false;
    }

    private boolean unmatchingFedFilterProvided(InteractionRoot interactionRoot) {
        if (!isMapperFederate()) {
            C2WInteractionRoot c2wInteractionRoot = (C2WInteractionRoot) interactionRoot;
            String fedFilter = c2wInteractionRoot.get_federateFilter();
            if (fedFilter != null) {
                fedFilter = fedFilter.trim();
                if ((fedFilter.length() > 0) && (fedFilter.compareTo(getFederateId()) != 0)) {
                    // System.out.println("Filtering due to fed filter: " + fedFilter);
                    // System.out.println("Filtered interaction was: " + interactionRoot);
                    return true;
                }
            }
        }
        return false;
    }


    /**
     * RTI callback -- DO NOT OVERRIDE.  SynchronizedFederate uses this method
     * to accept receive-order interactions from the RTI.
     * <p>
     * To access an interaction received from the RTI, use {@link #getNextInteraction()}
     * or {@link #getNextInteractionNoWait()}.
     *
     * @param interactionClass integer handle (RTI assigned) indicating the class
     *                         of interaction being received
     * @param theInteraction   data structure containing the parameter data for the
     *                         received interaction
     * @param userSuppliedTag  optional tag provided by the federate that sent the
     *                         interaction.  Currently ignored.
     */
    @Override
    public void receiveInteraction(
            int interactionClass, ReceivedInteraction theInteraction, byte[] userSuppliedTag
    ) {
        // System.out.println( "SynchronizedFederate::receiveInteraction (no time) for interactionHandle: " + interactionClass);
        receiveInteractionSF(interactionClass, theInteraction, userSuppliedTag);
    }

    public final void receiveInteractionSF(
            int interactionClass, ReceivedInteraction theInteraction, byte[] userSuppliedTag
    ) {
        // Himanshu: We normally use only TSO updates, so this shouldn't be
        // called, but due to an RTI bug, it seemingly is getting called. So,
        // for now, use the federate's current time or LBTS whichever is greater
        // as the timestamp

        DoubleTime assumedTimestamp = new DoubleTime();
        if (getLBTS() >= getCurrentTime()) {
            assumedTimestamp.setTime(getLBTS());
        } else {
            assumedTimestamp.setTime(getCurrentTime());
        }

        InteractionRoot ir = InteractionRoot.create_interaction(interactionClass, theInteraction);
        if (!unmatchingFedFilterProvided(ir)) {
            // handleIfSimEnd(interactionClass, theInteraction, assumedTimestamp);
            addInteraction(ir);
            // createLog(interactionClass, theInteraction, assumedTimestamp);
        }
    }


    /**
     * RTI callback -- DO NOT OVERRIDE.  SynchronizedFederate uses this method
     * to accept timestamp-order interactions from the RTI.
     * <p>
     * To access an interaction received from the RTI, use {@link #getNextInteraction()}
     * or {@link #getNextInteractionNoWait()}.
     *
     * @param interactionClass integer handle (RTI assigned) indicating the class
     *                         of interaction being received
     * @param theInteraction   data structure containing the parameter data for the
     *                         received interaction
     * @param userSuppliedTag  optional tag provided by the federate that sent the
     *                         interaction.  Currently ignored.
     * @param theTime          timestamp of the received interaction
     * @param retractionHandle a handle that allows the federate that sent the
     *                         interaction to retract it.  Currently ignored.
     */
    @Override
    public void receiveInteraction(
            int interactionClass,
            ReceivedInteraction theInteraction,
            byte[] userSuppliedTag,
            LogicalTime theTime,
            EventRetractionHandle retractionHandle
    ) {
        // System.out.println( "SynchronizedFederate::receiveInteraction (with time) for interactionHandle: " + interactionClass);
        this.receiveInteractionSF(interactionClass, theInteraction, userSuppliedTag, theTime, retractionHandle);
    }

    public final void receiveInteractionSF(
            int interactionClass,
            ReceivedInteraction theInteraction,
            byte[] userSuppliedTag,
            LogicalTime theTime,
            EventRetractionHandle retractionHandle
    ) {
        InteractionRoot ir = InteractionRoot.create_interaction(interactionClass, theInteraction, theTime);
        if (!unmatchingFedFilterProvided(ir)) {
            // handleIfSimEnd(interactionClass, theInteraction, theTime);
            addInteraction(ir);
            // createLog(interactionClass, theInteraction, theTime);
        }
    }

//    protected void handleIfSimEnd(int interactionClass, ReceivedInteraction theInteraction, LogicalTime theTime) {
//        if (SimEnd.match(interactionClass)) {
//            System.out.println(getFederateId() + ": SimEnd interaction received, exiting...");
//            createLog(interactionClass, theInteraction, theTime);
//            try {
//                getLRC().resignFederationExecution(ResignAction.DELETE_OBJECTS);
//            } catch (Exception e) {
//                System.out.println("Error during resigning federate: " + getFederateId());
//                e.printStackTrace();
//            }
//
//            // Wait for 10 seconds for Federation Manager to recognize that the federate has resigned.
//            try {
//                Thread.sleep(10000);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//            // TODO: CONSIDER SETTING UP A SHUTDOWN HOOK
//            // this one will terminate the JVM not only the current process
//            Runtime.getRuntime().exit(0);
//
//            // Exit
//            System.exit(0);
//        }
//    }

    private static PriorityBlockingQueue<ObjectReflector> _objectReflectionQueue = new PriorityBlockingQueue<ObjectReflector>(10, new ObjectReflectorComparator());


    /**
     * DO NOT USE -- Should only be used directly by the SyncronizedFederate class.
     * This method places an ObjectReflector on a queue internal to the
     * {@link ObjectRoot} class.  Usu. this ObjectReflector contains attribute
     * reflections that have just been received from the RTI using
     * the {@link #reflectAttributeValues(int, ReflectedAttributes, byte[])} or
     * {@link #reflectAttributeValues(int, ReflectedAttributes, byte[], LogicalTime, EventRetractionHandle)}
     * callback method.  In this method, the attribute reflections are "receive-order".
     *
     * @param objectHandle        handle (RTI assigned) to the object class instance for
     *                            which the reflected attributes are to be applied
     * @param reflectedAttributes attribute reflections for the object class
     *                            instance corresponding to objectHandle
     */
    public static void addObjectReflector(int objectHandle, ReflectedAttributes reflectedAttributes) {
        _objectReflectionQueue.add(new ObjectReflector(objectHandle, reflectedAttributes));
    }

    /**
     * DO NOT USE -- Should only be used directly by the SyncronizedFederate class.
     * This method like the {@link #addObjectReflector(int, ReflectedAttributes)}
     * method, except it is for attribute reflections that are "timestamp-ordered".
     *
     * @param objectHandle        handle (RTI assigned) to the object class instance for
     *                            which the reflected attributes are to be applied
     * @param reflectedAttributes attribute reflections for the object class
     *                            instance corresponding to objectHandle
     * @param logicalTime         timestamp of the attribute reflections
     */
    public static void addObjectReflector(int objectHandle, ReflectedAttributes reflectedAttributes, LogicalTime logicalTime) {
        _objectReflectionQueue.add(new ObjectReflector(objectHandle, reflectedAttributes, logicalTime));
    }

    /**
     * Gets the next ObjectReflector from a queue that is internal to the {@link ObjectRoot}
     * class, waiting for an ObjectReflector is none are currently on the queue.
     * The queue is ordered by the timestamp of the attribute reflections in the
     * ObjectReflector's, with ObjectReflectors having "receive-order" attribute
     * reflections being placed at the front of the queue (they are given a
     * timestamp of -1).
     *
     * @return the next ObjectReflector on the ObjectRoot class queue in the order
     * of timestamp of their contained attribute reflections, where receive-order
     * attribute reflections have a timestamp of -1.
     */
    public static ObjectReflector getNextObjectReflector() {
        ObjectReflector objectReflection = null;
        boolean takeNotComplete = true;
        while (takeNotComplete) {
            try {
                objectReflection = _objectReflectionQueue.take();
                takeNotComplete = false;
            } catch (InterruptedException i) {
            }

        }
        return objectReflection;
    }

    /**
     * Like {@link #getNextObjectReflector()}, except returns null
     * if there are no ObjectReflectors on the queue.
     *
     * @return
     */
    public static ObjectReflector getNextObjectReflectorNoWait() {
        return _objectReflectionQueue.poll();
    }

    /**
     * RTI callback -- DO NOT OVERRIDE.  SynchonizedFederate class uses this
     * method to detect new instances of object classes to which a federate has
     * subscribed that have been created by other federates in the federation.
     * When such an instance is detected, the SynchronizedFederate directs the
     * {@link ObjectRoot} class to create a local instance of this object and
     * place it in a table local to the ObjectRoot class.  This table is indexed
     * by the handles (RTI assigned) of the instances.
     *
     * @param objectHandle      handle (RTI assigned) of a new object class instance that
     *                       has been created by another federate in the federation and accounced on
     *                       the RTI.
     * @param objectClassHandle handle (RTI assigned) of the object class to which the
     *                       new instance belongs.
     * @param objectName     name of the new object class instance (currently ignored).
     */
    @Override
    public void discoverObjectInstance(int objectHandle, int objectClassHandle, String objectName) {
        ObjectRoot.discover(objectClassHandle, objectHandle);
    }

    /**
     * RTI callback -- DO NOT OVERRIDE.  SynchonizedFederate class uses this
     * method to receive receive-order attribute reflections for an object class
     * instance.  This instance should already have been detected by the
     * {@link #discoverObjectInstance(int, int, String)} method.  Attribute reflections
     * for a given object class instance are not immediately applied to the
     * instance.  Rather, they are packaged along with the instance into an
     * {@link ObjectReflector}, which allows the federate code to apply the
     * attribute reflections to the instance on demand.  The attribute reflections
     * received by this method have "receive" ordering, and so are given a -1
     * timestamp.
     * <p>
     * To access the ObjectReflector created here, use {@link #getNextObjectReflector()}
     * or {@link #getNextObjectReflectorNoWait()}.
     *
     * @param theObject       handle (RTI assigned) of the object class instance to which
     *                        the attribute reflections are to be applied
     * @param theAttributes   data structure containing attribute reflections for
     *                        the object class instance, i.e. new values for the instance's attributes.
     * @param userSuppliedTag optional tag provided by the federate that sent the
     *                        interaction.  Currently ignored.
     */
    @Override
    public void reflectAttributeValues(int theObject, ReflectedAttributes theAttributes, byte[] userSuppliedTag) {
        addObjectReflector(theObject, theAttributes);

        // Himanshu: We normally use only TSO updates, so this shouldn't be
        // called, but due to an RTI bug, it seemingly is getting called. So,
        // for now, use the federate's current time or LBTS whichever is greater
        // as the timestamp
        DoubleTime assumedTimestamp = new DoubleTime();
        if (getLBTS() >= getCurrentTime()) {
            assumedTimestamp.setTime(getLBTS());
        } else {
            assumedTimestamp.setTime(getCurrentTime());
        }
        // createLog(theObject, theAttributes, assumedTimestamp);
    }

    /**
     * RTI callback -- DO NOT OVERRIDE.  SynchonizedFederate class uses this
     * method to receive timestamp-order attribute reflections for an object class
     * instance.  This is like the {@link #reflectAttributeValues(int, ReflectedAttributes, byte[])}
     * method, but receives timestamp-order, rather than receive-order, attribute
     * reflections.
     * <p>
     * To access the ObjectReflector created here, use {@link #getNextObjectReflector()}
     * or {@link #getNextObjectReflectorNoWait()}.
     *
     * @param theObject        handle (RTI assigned) of the object class instance to which
     *                         the attribute reflections are to be applied
     * @param theAttributes    data structure containing attribute reflections for
     *                         the object class instance, i.e. new values for the instance's attributes.
     * @param userSuppliedTag  optional tag provided by the federate that sent the
     *                         interaction.  Currently ignored.
     * @param theTime          timestamp of the received interaction
     * @param retractionHandle a handle that allows the federate that sent the
     *                         interaction to retract it.  Currently ignored.
     */
    @Override
    public void reflectAttributeValues(
            int theObject,
            ReflectedAttributes theAttributes,
            byte[] userSuppliedTag,
            LogicalTime theTime,
            EventRetractionHandle retractionHandle
    ) {
        addObjectReflector(theObject, theAttributes, theTime);
        // createLog(theObject, theAttributes, theTime);
    }

//    protected void createLog(
//            final int interactionClass,
//            final ReceivedInteraction theInteraction,
//            final LogicalTime theTime
//    ) {
//        if (!InteractionRoot.enableSubLog) return;
//
//
//        Thread t = new Thread(new Runnable() {
//            public void run() {
//                try {
//                    String logIdLocal = null;
//                    synchronized (SynchronizedFederate.class) {
//                        logIdLocal = Integer.toString(logId++);
//                    }
//                    String interactionName = InteractionRoot.get_simple_class_name(interactionClass);
//                    double time = 0;
//                    if (theTime != null) {
//                        DoubleTime doubleTime = new DoubleTime();
//                        doubleTime.setTo(theTime);
//                        time = doubleTime.getTime();
//                    }
//                    for (int i = 0; i < theInteraction.size(); i++) {
//                        String parameter = InteractionRoot.get_parameter_name(theInteraction.getParameterHandle(i));
//                        String value = new String(theInteraction.getValue(i));
//                        String type = new String(InteractionRoot._datamemberTypeMap.get(parameter));
//                        // C2WLogger.addLog(interactionName + "_sub_" + federateId, time, parameter, value, type, InteractionRoot.subLogLevel, logIdLocal);
//                    }
//                } catch (ArrayIndexOutOfBounds e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//        t.start();
//    }

//    protected void createLog(
//            final int objectHandle,
//            final ReflectedAttributes reflectedAttributes,
//            final LogicalTime theTime
//    ) {
//        if (ObjectRoot._subAttributeLogMap.isEmpty()) return;
//        Thread t = new Thread(new Runnable() {
//            public void run() {
//                try {
//                    String logIdLocal = null;
//                    synchronized (SynchronizedFederate.class) {
//                        logIdLocal = Integer.toString(logId++);
//                    }
//                    String objectName = ObjectRoot.getObject(objectHandle).getSimpleClassName();
//                    double time = 0;
//                    if (theTime != null) {
//                        DoubleTime doubleTime = new DoubleTime();
//                        doubleTime.setTo(theTime);
//                        time = doubleTime.getTime();
//                    }
//                    for (int i = 0; i < reflectedAttributes.size(); i++) {
//                        String attribute = ObjectRoot.get_attribute_name(reflectedAttributes.getAttributeHandle(i));
//                        if (!ObjectRoot._subAttributeLogMap.containsKey(attribute)) continue;
//                        String value = new String(reflectedAttributes.getValue(i));
//                        String type = new String(ObjectRoot._datamemberTypeMap.get(attribute));
//                        String loglevel = ObjectRoot._subAttributeLogMap.get(attribute);
//                        // C2WLogger.addLog(objectName + "_" + attribute + "_sub_" + federateId, time, attribute, value, type, loglevel, logIdLocal);
//                    }
//                } catch (ArrayIndexOutOfBounds e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//        t.start();
//    }

    /**
     * Adds a FederateChangeListener to the federateChangeEventListeners collection.
     * @param listener The object that implements the FederateChangeListener interface.
     */
    public void addFederateStateChangeListener(FederateStateChangeListener listener) {
        if (!this.federateChangeEventListeners.contains(listener)) {
            this.federateChangeEventListeners.add(listener);
        }
    }

    public void removeFederateStateChangeListener(FederateStateChangeListener listener) {
        this.federateChangeEventListeners.remove(listener);
    }

    protected void fireFederateStateChanged(FederateState prevState, FederateState newState) {
        FederateStateChangeEvent e = new FederateStateChangeEvent(this, prevState, newState);
        for (FederateStateChangeListener listener : this.federateChangeEventListeners) {
            listener.federateStateChanged(e);
        }
    }
}