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

package c2w.hla;

import hla.rti.ArrayIndexOutOfBounds;
import hla.rti.AsynchronousDeliveryAlreadyEnabled;
import hla.rti.EnableTimeConstrainedPending;
import hla.rti.EnableTimeRegulationPending;
import hla.rti.EventRetractionHandle;
import hla.rti.FederateAlreadyExecutionMember;
import hla.rti.FederateNotExecutionMember;
import hla.rti.FederateOwnsAttributes;
import hla.rti.FederationTimeAlreadyPassed;
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

import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.SynchronousQueue;

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
 * <li>RTI creation/destruction ( {@link #createRTI()}, {@link #destroyRTI()} )</li>
 * <li>A means of acquiring a handle to the RTI ( {@link #getRTI()} )</li>
 * <li>Joining a federation ( {@link #joinFederation(String, String)} )</li>
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
 * {@link #putAdvanceTimeRequest(c2w.hla.SynchronizedFederate.AdvanceTimeRequest)},
 * {@link AdvanceTimeRequest#requestSyncStart()}, {@link AdvanceTimeRequest#requestSyncEnd()},
 * {@link #startAdvanceTimeThread()} )</li>
 * </ul>
 *
 * @author Harmon Nine
 */
public class SynchronizedFederate extends NullFederateAmbassador {

    private RTIambassador _rti;

    public static final String FEDERATION_MANAGER_NAME = "manager";

    public static final String ReadyToPopulateSynch = "readyToPopulate";
    public static final String ReadyToRunSynch = "readyToRun";
    public static final String ReadyToResignSynch = "readyToResign";

    public static int logId = 0;

    private Set<String> _achievedSynchronizationPoints = new HashSet<String>();

    private boolean _timeConstrainedNotEnabled = true;
    private boolean _timeRegulationNotEnabled = true;
    private boolean _simEndNotSubscribed = true;

    private boolean _timeAdvanceNotGranted = true;

    private boolean _advanceTimeThreadNotStarted = true;

    private String _federateId = "";
    private String _federationId = "";
    private File _lockFile;

    private double _lookahead = 0.0;

    public void setLookahead(double lookahead) {
        _lookahead = lookahead;
    }

    public double getLookahead() {
        return _lookahead;
    }

    public SynchronizedFederate() {
        // Set process group ID as the same as process ID
        //this.PGID = new ProcessId().setProcessGroupId();
    }

    public static enum TIME_ADVANCE_MODE {
        TIME_ADVANCE_REQUEST("TimeAdvanceRequest"),
        TIME_ADVANCE_REQUEST_AVAILABLE("TimeAdvanceRequestAvailable"),
        NEXT_EVENT_REQUEST("NextEventRequest"),
        NEXT_EVENT_REQUEST_AVAILABLE("NextEventRequestAvailable");

        private String _name;

        TIME_ADVANCE_MODE(String name) {
            this._name = name;
        }

        public String getName() {
            return _name;
        }

        @Override
        public String toString() {
            return _name;
        }
    }

    /**
     * Get a handle to the RTI.
     *
     * @return handle (of type RTIambassador) to the RTI.  This can be used as
     * an argument to {@link InteractionRoot#sendInteraction(RTIambassador)} or
     * {@link ObjectRoot#updateAttributeValues(RTIambassador)} calls, for instance.
     */
    public RTIambassador getRTI() {
        return _rti;
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

    /**
     * Create the RTI, or, more specifically, acquire a handle to the RTI that
     * can be accessed via the {@link #getRTI()} call.
     */
    public void createRTI() throws RTIinternalError {
        createRTI("");
    }

    public void createRTI(String federate_id) throws RTIinternalError {

        if (!federate_id.equals("")) System.out.print("[" + federate_id + "] federate ");
        System.out.print("acquiring connection to RTI ... ");
        if (SynchronizedFederate.FEDERATION_MANAGER_NAME.compareTo(federate_id) != 0) {
            // Himanshu: This is a regular federate, wait 20 seconds for federation manager to initialize first
            System.out.println("Regular federate waiting 20 secs for Federation Manager to initialize");
            try {
                Thread.sleep(20000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        RtiFactory factory = RtiFactoryFactory.getRtiFactory();
        _rti = factory.createRtiAmbassador();
        System.out.println("done.");
    }

    /**
     * Dissociate from the RTI.  This sets the handle to the RTI acquired via
     * {@link #createRTI} to null.  Thus, {@link #getRTI()} returns null after
     * this call.
     */
    public void destroyRTI() {
        _rti = null;
    }

    /**
     * Joins the federate to a particular federation.
     *
     * @param federation_id a unique name for the federation to be joined by
     *                      the federate
     * @param federate_id   a unique name for this federate within the federation
     *                      it is joining.  The name must be unique within a particular federation.
     *                      <p>
     *                      Supplies true to the argument 'ignoreLockFile'
     */
    public void joinFederation(String federation_id, String federate_id) {
        this.joinFederation(federation_id, federate_id, true);
    }

    /**
     * Joins the federate to a particular federation.
     *
     * @param federation_id a unique name for the federation to be joined by
     *                      the federate
     * @param federate_id   a unique name for this federate within the federation
     *                      it is joining.  The name must be unique within a particular federation.
     */
    public void joinFederation(String federation_id, String federate_id, boolean ignoreLockFile) {
        this._federateId = federate_id;
        this._federationId = federation_id;
        boolean federationNotPresent = true;
        while (federationNotPresent) {
            try {
                if (!ignoreLockFile) {
                    try {
                        int counter = 0;
                        while (!_lockFile.createNewFile()) {
                            if (++counter >= 60) {
                                System.err.println("ERROR: [" + federate_id + "] federate:  could not open lock file \"" + _lockFile + "\": timeout after 60 seconds.  Exiting.");
                            }
                            try {
                                Thread.sleep(1000);
                            } catch (Exception e) {
                            }
                        }
                    } catch (Exception e) {
                        System.err.println("ERROR: [" + federate_id + "] federate:  could not open lock file \"" + _lockFile + "\": " + e.getMessage() + ".  Exiting.");
                    }
                }

                System.out.print("[" + federate_id + "] federate joining federation [" + federation_id + "] ... ");
                synchronized (_rti) {
                    _rti.joinFederationExecution(federate_id, federation_id, this, null);
                }
                System.out.println("done.");

                if (!ignoreLockFile) {
                    _lockFile.delete();
                }

                federationNotPresent = false;
            } catch (FederateAlreadyExecutionMember f) {
                System.err.println(f.getMessage());
                return;
            } catch (Exception e) {
                System.err.println(e.getMessage());
                try {
                    Thread.sleep(500);
                } catch (Exception e2) {
                }
            }
        }

//		try {
//		    File lockFile = new File( federation_id + "_" + federate_id + ".lck" );
//		    FileOutputStream lockFileStream = new FileOutputStream( lockFile );
//		    lockFileStream.close();
//		} catch( Exception e ) { }

    }

    /**
     * Returns the id (name) of the federation in which this federate is running.
     */
    public String getFederationId() {
        return _federationId;
    }

    /**
     * Returns the id (name) of this federate as registered with the federation
     * in which it is running.
     */
    public String getFederateId() {
        return _federateId;
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
                synchronized (_rti) {
                    _rti.enableTimeConstrained();
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
                    Thread.sleep(500);
                } catch (Exception e2) {
                }
            }
        }

        try {
            synchronized (_rti) {
                _rti.tick();
            }
        } catch (Exception e) {
        }
        while (_timeConstrainedNotEnabled) {
            try {
                Thread.sleep(500);
            } catch (Exception e) {
            }
            try {
                synchronized (_rti) {
                    _rti.tick();
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
                synchronized (_rti) {
                    _rti.enableTimeRegulation(new DoubleTime(time), new DoubleTimeInterval(lookahead));
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
                    Thread.sleep(500);
                } catch (Exception e2) {
                }
            }
        }

        try {
            synchronized (_rti) {
                _rti.tick();
            }
        } catch (Exception e) {
        }
        while (_timeRegulationNotEnabled) {
            try {
                Thread.sleep(500);
            } catch (Exception e) {
            }
            try {
                synchronized (_rti) {
                    _rti.tick();
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
            SimEnd.subscribe(getRTI());
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
                getRTI().enableAsynchronousDelivery();
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
                    Thread.sleep(500);
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
                getRTI().resignFederationExecution(resignAction);
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
                    Thread.sleep(500);
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

        achieveSynchronizationPoint(ReadyToPopulateSynch);
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
        achieveSynchronizationPoint(ReadyToRunSynch);
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
        achieveSynchronizationPoint(ReadyToResignSynch);
    }

    private void achieveSynchronizationPoint(String label) throws FederateNotExecutionMember, RTIinternalError {
        boolean synchronizationPointNotAccepted = true;
        while (synchronizationPointNotAccepted) {
            try {
                synchronized (_rti) {
                    _rti.synchronizationPointAchieved(label);
                }
                while (!_achievedSynchronizationPoints.contains(label)) {
                    Thread.sleep(500);
                    synchronized (_rti) {
                        _rti.tick();
                    }
                }
                synchronizationPointNotAccepted = false;
            } catch (FederateNotExecutionMember f) {
                throw f;
            } catch (SynchronizationLabelNotAnnounced s) {
                if (_achievedSynchronizationPoints.contains(label)) {
                    synchronizationPointNotAccepted = false;
                } else {
                    synchronized (_rti) {
                        try {
                            _rti.tick();
                        } catch (RTIinternalError r) {
                            throw r;
                        } catch (Exception e) {
                            try {
                                Thread.sleep(500);
                            } catch (Exception e2) {
                            }
                        }
                    }
                }
            } catch (Exception e) {
                try {
                    Thread.sleep(500);
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
                synchronized (getRTI()) {
                    logicalTime = getRTI().queryFederateTime();
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
                synchronized (getRTI()) {
                    lbtsTime = getRTI().queryLBTS();
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
                synchronized (getRTI()) {
                    lbtsTime = getRTI().queryLBTS();
                    logicalTime = getRTI().queryFederateTime();
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

        double timestampWithLogicalTime = dblLogicalTime + getLookahead();

        if (dblLBTSTime > timestampWithLogicalTime)
            return dblLBTSTime;
        else
            return timestampWithLogicalTime;
    }

    /**
     * Synchronous Queue class that the AdvanceTimeThread uses to synchronize
     * itself with the threads of the federate that are interacting with the RTI.
     *
     * @author Harmon Nine
     */
    public static class SyncQueue extends SynchronousQueue<Object> {
        public final static long serialVersionUID = 1;
    }

    /**
     * A thread in a federate uses an object of this class to request a federation
     * time to which the AdvanceTimeThread should advance.  Once the time is
     * reached, the federate thread uses this object to synchronize its execution
     * with the AdvanceTimeThread.  That is, the AdvanceTimeThread is prevented
     * from advancing the time any further until the federate thread has completed
     * its processing for this time.
     * See {@link AdvanceTimeThread}.
     *
     * @author Harmon Nine
     */
    public static class AdvanceTimeRequest {
        private static Object object = new Object();

        private double _requestedTime;
        private double _currentTime = -1;
        private SyncQueue _syncQueue;

        /**
         * Creates a new AdvanceTimeRequest with a new (and unique) SyncQueue.
         *
         * @param requestedTime time at which the federate wishes to perform
         *                      processing for the simulation.
         */
        public AdvanceTimeRequest(double requestedTime) {
            _requestedTime = requestedTime;
            _syncQueue = new SyncQueue();
        }

        /**
         * Creates a new AdvanceTimeRequest with a supplied SyncQueue.
         *
         * @param requestedTime time at which the federate wishes to perform
         *                      processing for the simulation.
         * @param syncQueue     SyncQueue
         */
        public AdvanceTimeRequest(double requestedTime, SyncQueue syncQueue) {
            _requestedTime = requestedTime;
            _syncQueue = syncQueue;
        }

        /**
         * returns the time to which the federate thread has requested the
         * AdvanceTimeThread advance using this AdvanceTimeRequest object.
         *
         * @return time to which the federate thread has requested the
         * AdvanceTimeThread advance using this AdvanceTimeRequest object
         */
        public double getRequestedTime() {
            return _requestedTime;
        }

        /**
         * returns the time at which this AdvanceTimeRequest object is actually
         * processed by the AdvanceTimeThread.  Usually, this is the same as the
         * requested time (see {@link #getRequestedTime()}).  However, the current
         * time can be greater than the requested time -- this only happens if the
         * requested time is previous to the current federation time to begin with,
         * and is usually the result of some kind of error in processing by the
         * federate that created and is using this AdvanceTimeRequest.
         *
         * @return time at which the AdvanceTimeRequest is processed by the
         * AdvanceTimeThread
         */
        public double getCurrentTime() {
            return _currentTime;
        }

        /**
         * Called by the AdvanceTimeThread ONLY, this method coordinates the
         * AdvanceTimeThread with the federate thread that created and is using
         * this AdvanceTimeRequest object.
         * <p/>
         * Usually, after submitting this AdvanceTimeRequest object to the
         * AdvanceTimeThread, a federate thread calls {@link #requestSyncStart()}
         * on this object.  This causes the federate to suspend execution until
         * the AdvanceTimeThread advances the federate time to the requested time
         * in the AdvanceTimeRequest object.  Once the AdvanceTimeThread does
         * this, it calls this method (that is, threadSyncStart( double )) on
         * the AdvanceTimeRequest object.  This causes the federate thread to
         * resume execution and perform the processing it needs to at the requested
         * time (see {@link AdvanceTimeThread}).
         *
         * @param currentTime the time at which the AdvanceTimeThread actually
         *                    processed this AdvanceTimeRequest object.
         */
        public void threadSyncStart(double currentTime) {
            _currentTime = currentTime;
            threadSyncEnd();
        }

        /**
         * Called by the AdvanceTimeThread ONLY, this method coordinates the
         * AdvanceTimeThread with the federate thread that created and is using
         * this AdvanceTimeRequest object.
         * <p>
         * After calling the {@link #threadSyncStart(double)} method, the
         * {@link AdvanceTimeThread} immediately calls this method (that is, threadSyncEnd()).
         * This causes it to suspend its execution.  It resumes its execution
         * when the federate thread using this AdvanceTimeRequest object calls
         * {@link #requestSyncEnd()} on this object, indicating that it has completed
         * the processing it needed to perform at the requested time of this
         * AdvanceTimeRequest object.
         */
        public void threadSyncEnd() {
            boolean putNotExecuted = true;
            while (putNotExecuted) {
                try {
                    _syncQueue.put(object);
                    putNotExecuted = false;
                } catch (InterruptedException i) {
                }
            }
        }

        /**
         * Called by a federate thread to suspend its execution until the
         * {@link AdvanceTimeThread} advances the federation time to the time
         * requested in this AdvanceTimeRequest object.
         */
        public void requestSyncStart() {
            boolean takeNotExecuted = true;
            while (takeNotExecuted) {
                try {
                    _syncQueue.take();
                    takeNotExecuted = false;
                } catch (InterruptedException i) {
                }
            }
        }

        /**
         * Called by a federate thread to indicate to the {@link AdvanceTimeThread}
         * that it has completed the processing it needed to perform at the time
         * requested in this AdvanceTimeRequest object.
         */
        public void requestSyncEnd() {
            _currentTime = -1;
            requestSyncStart();
        }
    }

    private static class ATRComparator implements Comparator<AdvanceTimeRequest> {
        public int compare(AdvanceTimeRequest t1, AdvanceTimeRequest t2) {
            return (int) Math.signum(t1._requestedTime - t2._requestedTime);
        }
    }

    public static class ATRQueue extends PriorityBlockingQueue<AdvanceTimeRequest> {

        public static final long serialVersionUID = 1;

        public ATRQueue(int size, ATRComparator tatComparator) {
            super(size, tatComparator);
        }
    }

    private ATRQueue _atrQueue = new ATRQueue(100, new ATRComparator());


    /**
     * DO NOT USE -- Should only be used directly by the SyncronizedFederate class.
     * This method is used to access a queue of AdvanceTimeRequest objects.
     * AdvanceTimeRequests are placed on this queue using
     * {@link #putAdvanceTimeRequest(c2w.hla.SynchronizedFederate.AdvanceTimeRequest)}
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
     * This class is run in a separate thread and is responsible for temporal
     * coordination between the RTI and one or more threads in a given federate.
     * The means by which the AdvanceTimeThread is able perform this coordination
     * is via objects of the {@link AdvanceTimeRequest} class.  That is, a federate
     * thread that has processing to perform at federation time X places this time
     * in an AdvanceTimeRequest object and submits it to the AdvanceTimeThread.
     * Once the AdvanceTimeThread has advanced to time X, it signals the federate
     * thread to start processing and suspends its own execution until this
     * processing is complete.  It then goes on to service another
     * AdvanceTimeRequest from another thread.
     * <p>
     * The program statements in the main federate thread should look like this:
     * ------
     * // start federate threads that interact with RTI
     * thread1.start();
     * thread2.start();
     * // ...
     * // Wait for threads to suspend
     * // ...
     * // start AdvanceTimeThread
     * startAdvanceTimeThread();
     * ------
     * <p>
     * The program statements in one of the federate threads should look like this:
     * ------
     * AdvanceTimeRequest atr = null;
     * double time = init_time; // Initial time thread needs to interact with RTI.
     * <p>
     * // Submit request to AdvanceTimeThread to notify this thread when
     * // federation time "time" has been reached
     * atr = putAdvanceTimeRequest( time );
     * <p>
     * while( true ) {
     * <p>
     * // Wait for notification from AdvanceTimeThread that federate time "time"
     * // has been reached
     * atr.requestSyncStart();
     * <p>
     * // Perform processing for time "time"
     * // ...
     * <p>
     * // Compute next RTI time that processing is needed
     * time = next_time;
     * <p>
     * // Submit request to AdvanceTimeThread to notify this thread when
     * // next federation time "time" has been reached.
     * // NOTE THAT THIS IS DONE BEFORE "requestSyncEnd()" BELOW, I.E. BEFORE
     * // TELLING THE AdvanceTimeThread TO CONTINUE ADVANCING TIME.  IF THIS
     * // WHERE DONE AFTER "requestSyncEnd()", IT WOULD RESULT IN A RACE
     * // CONDITION.
     * AdvanceTimeRequest new_atr = putAdvanceTimeRequest( time );
     * <p>
     * // Notify AdvanceTimeThread that processing is complete for time "time",
     * // so that the AdvanceTimeThread may advance to other times and process
     * // other AdvanceTimeRequest's.
     * atr.requestSyncEnd();
     * <p>
     * // Reassign atr from new_atr for loop
     * atr = new_atr;
     * }
     * --------
     *
     * @author Harmon Nine
     */
    public static class AdvanceTimeThread extends Thread {

        // private double _atrStepSize = 0.2;

        private ATRQueue _atrQueue;

        private SynchronizedFederate _synchronizedFederate;
        private RTIambassador _rti;
        private TIME_ADVANCE_MODE _timeAdvanceMode = TIME_ADVANCE_MODE.TIME_ADVANCE_REQUEST;

        public AdvanceTimeThread(SynchronizedFederate synchronizedFederate, ATRQueue atrQueue, TIME_ADVANCE_MODE timeAdvanceMode) {
            _synchronizedFederate = synchronizedFederate;
            _rti = _synchronizedFederate.getRTI();
            _atrQueue = atrQueue;
            _timeAdvanceMode = timeAdvanceMode;
        }

        public void run() {

            double currentTime = _synchronizedFederate.getCurrentTime();
            if (currentTime < 0) return;

            while (true) {
                AdvanceTimeRequest advanceTimeRequest = null;
                advanceTimeRequest = _atrQueue.peek();
                if (advanceTimeRequest == null) {
                    break;
                }


                boolean takeNotExecuted = true;
                while (takeNotExecuted) {
                    try {
                        advanceTimeRequest = _atrQueue.take();
                        takeNotExecuted = false;
                    } catch (InterruptedException i) {
                    }
                }

                DoubleTime timeRequest = null;
                // System.out.println("Current time = " + currentTime + ", and ATR's requested time = " + advanceTimeRequest.getRequestedTime());
                if (advanceTimeRequest.getRequestedTime() > currentTime) {
                    timeRequest = new DoubleTime(advanceTimeRequest.getRequestedTime());
                } else {
                    advanceTimeRequest.threadSyncStart(currentTime);
                    advanceTimeRequest.threadSyncEnd();
                    continue;
                }

                if (timeRequest != null) {
                    _synchronizedFederate.setTimeAdvanceNotGranted(true);

                    boolean tarNotCalled = true;
                    while (tarNotCalled) {
                        try {
                            // System.out.println( "TimeAdvanceThread: Using " + _timeAdvanceMode + " to request time: " + timeRequest.getTime() );
                            synchronized (_rti) {
                                if (_timeAdvanceMode == TIME_ADVANCE_MODE.TIME_ADVANCE_REQUEST) {
                                    _rti.timeAdvanceRequest(timeRequest);
                                    // System.out.println( "TimeAdvanceThread: Called timeAdvanceRequest() to go to: " + timeRequest.getTime() );
                                } else if (_timeAdvanceMode == TIME_ADVANCE_MODE.NEXT_EVENT_REQUEST) {
                                    _rti.nextEventRequest(timeRequest);
                                    // System.out.println( "TimeAdvanceThread: Using nextEventRequest() to go to: " + timeRequest.getTime() );
                                } else if (_timeAdvanceMode == TIME_ADVANCE_MODE.TIME_ADVANCE_REQUEST_AVAILABLE) {
                                    _rti.timeAdvanceRequestAvailable(timeRequest);
                                    // System.out.println( "TimeAdvanceThread: Using timeAdvanceRequestAvailable() to go to: " + timeRequest.getTime() );
                                } else if (_timeAdvanceMode == TIME_ADVANCE_MODE.NEXT_EVENT_REQUEST_AVAILABLE) {
                                    _rti.nextEventRequestAvailable(timeRequest);
                                    // System.out.println( "TimeAdvanceThread: Using nextEventRequestAvailable() to go to: " + timeRequest.getTime() );
                                }
                            }
                            tarNotCalled = false;
                        } catch (FederationTimeAlreadyPassed f) {
                            System.err.println("Time already passed detected.");
                            _synchronizedFederate.setTimeAdvanceNotGranted(false);
                            tarNotCalled = false;
                        } catch (Exception e) {
                        }
                    }

                    while (_synchronizedFederate.getTimeAdvanceNotGranted()) {
                        try {
                            synchronized (_rti) {
                                _rti.tick();
                            }
                        } catch (Exception e) {
                        }
                        try {
                            Thread.sleep(10);
                        } catch (Exception e) {
                        }
                    }

                    currentTime = _synchronizedFederate.getCurrentTime();
                }

                if (advanceTimeRequest != null) {
                    advanceTimeRequest.threadSyncStart(currentTime);
                    advanceTimeRequest.threadSyncEnd();
                }
            }
        }

    }

    /**
     * Start the {@link AdvanceTimeThread}
     * Assumes the federate is a lookahead value greater than zero. Uses
     * {@link hla.rti.RTIambassador#timeAdvanceRequest(LogicalTime)} for advancing
     * federates time.
     */
    protected void startAdvanceTimeThread() {
        if (_advanceTimeThreadNotStarted) {
            (new AdvanceTimeThread(this, this._atrQueue, TIME_ADVANCE_MODE.TIME_ADVANCE_REQUEST)).start();
            _advanceTimeThreadNotStarted = false;
        }
    }

    /**
     * Start the {@link AdvanceTimeThread}
     *
     * @param #timeAdvanceMode If
     *                         {@link TIME_ADVANCE_MODE#TIME_ADVANCE_REQUEST_AVAILABLE} or
     *                         {@link TIME_ADVANCE_MODE#NEXT_EVENT_REQUEST_AVAILABLE} is used, the
     *                         federate's lookahead value is allowed to be zero. For other two cases,
     *                         federate's lookahead must be greater than zero.
     */
    protected void startAdvanceTimeThread(TIME_ADVANCE_MODE timeAdvanceMode) {
        if (_advanceTimeThreadNotStarted) {
            (new AdvanceTimeThread(this, this._atrQueue, timeAdvanceMode)).start();
            _advanceTimeThreadNotStarted = false;
        }
    }

    private static class InteractionRootComparator implements Comparator<InteractionRoot> {
        public int compare(InteractionRoot interactionRoot1, InteractionRoot interactionRoot2) {
            // System.out.println("Comparing IR1 and IR2");
            // System.out.println("IR1 = " + interactionRoot1);
            // System.out.println("IR2 = " + interactionRoot2);

            C2WInteractionRoot c2wIR1 = (C2WInteractionRoot) interactionRoot1;
            C2WInteractionRoot c2wIR2 = (C2WInteractionRoot) interactionRoot2;
            double agtIR1 = c2wIR1.get_actualLogicalGenerationTime();
            double agtIR2 = c2wIR2.get_actualLogicalGenerationTime();

            // System.out.println("IR1-ID = " + interactionRoot1.getUniqueID() + ", IR2-ID = " + interactionRoot2.getUniqueID());
            // System.out.println("IR1-Time = " + interactionRoot1.getTime() + ", IR2-Time = " + interactionRoot2.getTime());
            // System.out.println("IR1-ActualGenerationTime = " + agtIR1 + ", IR2-ActualGenerationTime = " + agtIR2);

            if (interactionRoot1.getTime() < interactionRoot2.getTime()) {
                // System.out.println("IR1-time < IR2-time, so returning -1");
                return -1;
            }
            if (interactionRoot1.getTime() > interactionRoot2.getTime()) {
                // System.out.println("IR1-time > IR2-time, so, returning 1");
                return 1;
            }
            if (agtIR1 < agtIR2) {
                // System.out.println("IR1-actualGenerationTime < IR2-actualGenerationTime, so returning -1");
                return -1;
            }
            if (agtIR1 > agtIR2) {
                // System.out.println("IR1-actualGenerationTime > IR2-actualGenerationTime, so returning 1");
                return 1;
            }
            if (interactionRoot1.getUniqueID() < interactionRoot2.getUniqueID()) {
                // System.out.println("IR1-uniqueID < IR2-uniqueID, so returning -1");
                return -1;
            }
            if (interactionRoot1.getUniqueID() > interactionRoot2.getUniqueID()) {
                // .println("IR1-uniqueID > IR2-uniqueID, so returning 1");
                return 1;
            }

            // System.out.println("No difference at all between IR1 and IR2, so returning 0");
            return 0;
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
            handleIfSimEnd(interactionClass, theInteraction, assumedTimestamp);
            addInteraction(ir);
            createLog(interactionClass, theInteraction, assumedTimestamp);
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
            handleIfSimEnd(interactionClass, theInteraction, theTime);
            addInteraction(ir);
            createLog(interactionClass, theInteraction, theTime);
        }
    }

    protected void handleIfSimEnd(int interactionClass, ReceivedInteraction theInteraction, LogicalTime theTime) {
        if (SimEnd.match(interactionClass)) {
            System.out.println(getFederateId() + ": SimEnd interaction received, exiting...");
            createLog(interactionClass, theInteraction, theTime);
            try {
                getRTI().resignFederationExecution(ResignAction.DELETE_OBJECTS);
            } catch (Exception e) {
                System.out.println("Error during resigning federate: " + getFederateId());
                e.printStackTrace();
            }

            // Wait for 10 seconds for Federation Manager to recognize that the federate has resigned.
            try {
                Thread.sleep(10000);
            } catch (Exception e) {
                e.printStackTrace();
            }

            // TODO: CONSIDER SETTING UP A SHUTDOWN HOOK
            // this one will terminate the JVM not only the current process
            Runtime.getRuntime().exit(0);

            // Exit
            System.exit(0);
        }
    }

    /**
     * This class serializes reflections of the attributes of object class
     * instances that come in from the RTI.  An object of this class contains:
     * <p>
     * - a reference to the object class instance for whom attribute reflections
     * have been received
     * - the reflected attributes and their new (reflected) values
     * - the timestamp of the reflections
     * <p>
     * This class is necessary because potentially many attribute reflections
     * can come in from the RTI before a federate thread processes them.  If the
     * reflections were simply performed when they came in, such a federate thread
     * could miss several reflections.
     * <p>
     * Instead, this class allows a federate thread to apply the reflections
     * itself.  The thread calls either {@link SynchronizedFederate#getNextObjectReflector()}
     * or {@link SynchronizedFederate#getNextObjectReflectorNoWait()} to get the
     * next ObjectReflector.  It then calls {@link SynchronizedFederate.ObjectReflector#reflect()}
     * on this ObjectReflector to apply the attribute reflections for the object
     * class instance it contains, and then calls {@link SynchronizedFederate.ObjectReflector#getObjectRoot()}
     * to retrieve this instance.
     *
     * @author Harmon Nine
     */
    public static class ObjectReflector {
        private int _objectHandle;
        private ReflectedAttributes _reflectedAttributes;
        private double _time;

        /**
         * DO NOT USE -- Should only be used directly by the SyncronizedFederate class.
         * The {@link SynchronizedFederate#reflectAttributeValues(int, ReflectedAttributes, byte[])}
         * method uses this constructor to create a new "receive-order" ObjectReflector.
         */
        public ObjectReflector(int objectHandle, ReflectedAttributes reflectedAttributes) {
            _objectHandle = objectHandle;
            _reflectedAttributes = reflectedAttributes;
        }

        /**
         * DO NOT USE -- Should only be used directly by the SyncronizedFederate class.
         * The {@link SynchronizedFederate#reflectAttributeValues(int, ReflectedAttributes, byte[], LogicalTime, EventRetractionHandle)}
         * method uses this constructor to create a new "timestamp-order" ObjectReflector.
         */
        public ObjectReflector(int objectHandle, ReflectedAttributes reflectedAttributes, LogicalTime logicalTime) {
            _objectHandle = objectHandle;
            _reflectedAttributes = reflectedAttributes;
            DoubleTime doubleTime = new DoubleTime();
            doubleTime.setTo(logicalTime);
            _time = doubleTime.getTime();
        }

        /**
         * A federate or federate thread calls this method to perform the attribute
         * reflections contained in this ObjectReflector object to the object class
         * instance contained by this ObjectReflector object.
         */
        public void reflect() {
            if (_time < 0) ObjectRoot.reflect(_objectHandle, _reflectedAttributes);
            else ObjectRoot.reflect(_objectHandle, _reflectedAttributes, _time);
        }

        /**
         * A federate or federate thread calls this method to retrieve the object
         * class instance contained by the ObjectReflector object.  Note that if
         * this is done before {@link #reflect()} is called, the instance will not have
         * the attribute reflections contained in this ObjectReflector object.
         * <p>
         * Note that the type of the reference returned by this method is always
         * "ObjectRoot", as this is the highest super-class for all object class
         * instances.  If a reference to the actual class of the instance is desired,
         * then this ObjectRoot reference will have to be cast up the inheritance
         * hierarchy.
         *
         * @return the object class instance contained by the ObjectReflector object.
         */
        public ObjectRoot getObjectRoot() {
            return ObjectRoot.getObject(_objectHandle);
        }

        public double getTime() {
            return _time;
        }

        public int getUniqueID() {
            return getObjectRoot().getUniqueID();
        }
    }

    private static class ObjectReflectorComparator implements Comparator<ObjectReflector> {
        public int compare(ObjectReflector objectReflection1, ObjectReflector objectReflection2) {
            if (objectReflection1.getTime() < objectReflection2.getTime()) return -1;
            if (objectReflection1.getTime() > objectReflection2.getTime()) return 1;

            if (objectReflection1.getUniqueID() < objectReflection2.getUniqueID()) return -1;
            if (objectReflection1.getUniqueID() > objectReflection2.getUniqueID()) return 1;

            return 0;
        }
    }

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
     * @param theObject      handle (RTI assigned) of a new object class instance that
     *                       has been created by another federate in the federation and accounced on
     *                       the RTI.
     * @param theObjectClass handle (RTI assigned) of the object class to which the
     *                       new instance belongs.
     * @param objectName     name of the new object class instance (currently ignored).
     */
    @Override
    public void discoverObjectInstance(int theObject, int theObjectClass, String objectName) {
        ObjectRoot.discover(theObjectClass, theObject);
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
        createLog(theObject, theAttributes, assumedTimestamp);
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
        createLog(theObject, theAttributes, theTime);
    }

    protected void createLog(
            final int interactionClass,
            final ReceivedInteraction theInteraction,
            final LogicalTime theTime
    ) {
        if (!InteractionRoot.enableSubLog) return;


        Thread t = new Thread(new Runnable() {
            public void run() {
                try {
                    String logIdLocal = null;
                    synchronized (SynchronizedFederate.class) {
                        logIdLocal = Integer.toString(logId++);
                    }
                    String interactionName = InteractionRoot.get_simple_class_name(interactionClass);
                    double time = 0;
                    if (theTime != null) {
                        DoubleTime doubleTime = new DoubleTime();
                        doubleTime.setTo(theTime);
                        time = doubleTime.getTime();
                    }
                    for (int i = 0; i < theInteraction.size(); i++) {
                        String parameter = InteractionRoot.get_parameter_name(theInteraction.getParameterHandle(i));
                        String value = new String(theInteraction.getValue(i));
                        String type = new String(InteractionRoot._datamemberTypeMap.get(parameter));
                        C2WLogger.addLog(interactionName + "_sub_" + _federateId, time, parameter, value, type, InteractionRoot.subLogLevel, logIdLocal);
                    }
                } catch (ArrayIndexOutOfBounds e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();
    }

    protected void createLog(
            final int objectHandle,
            final ReflectedAttributes reflectedAttributes,
            final LogicalTime theTime
    ) {
        if (ObjectRoot._subAttributeLogMap.isEmpty()) return;
        Thread t = new Thread(new Runnable() {
            public void run() {
                try {
                    String logIdLocal = null;
                    synchronized (SynchronizedFederate.class) {
                        logIdLocal = Integer.toString(logId++);
                    }
                    String objectName = ObjectRoot.getObject(objectHandle).getSimpleClassName();
                    double time = 0;
                    if (theTime != null) {
                        DoubleTime doubleTime = new DoubleTime();
                        doubleTime.setTo(theTime);
                        time = doubleTime.getTime();
                    }
                    for (int i = 0; i < reflectedAttributes.size(); i++) {
                        String attribute = ObjectRoot.get_attribute_name(reflectedAttributes.getAttributeHandle(i));
                        if (!ObjectRoot._subAttributeLogMap.containsKey(attribute)) continue;
                        String value = new String(reflectedAttributes.getValue(i));
                        String type = new String(ObjectRoot._datamemberTypeMap.get(attribute));
                        String loglevel = ObjectRoot._subAttributeLogMap.get(attribute);
                        C2WLogger.addLog(objectName + "_" + attribute + "_sub_" + _federateId, time, attribute, value, type, loglevel, logIdLocal);
                    }
                } catch (ArrayIndexOutOfBounds e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();
    }
}