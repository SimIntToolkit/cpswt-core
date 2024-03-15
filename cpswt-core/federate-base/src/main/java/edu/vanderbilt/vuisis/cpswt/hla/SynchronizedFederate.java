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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import hla.rti.*;
import edu.vanderbilt.vuisis.cpswt.hla.base.AdvanceTimeRequest;
import edu.vanderbilt.vuisis.cpswt.hla.base.AdvanceTimeThread;
import edu.vanderbilt.vuisis.cpswt.hla.base.ATRComparator;
import edu.vanderbilt.vuisis.cpswt.hla.base.ATRQueue;
import edu.vanderbilt.vuisis.cpswt.hla.base.TimeAdvanceMode;
import edu.vanderbilt.vuisis.cpswt.hla.InteractionRoot_p.AddProxy;
import edu.vanderbilt.vuisis.cpswt.hla.InteractionRoot_p.C2WInteractionRoot;
import edu.vanderbilt.vuisis.cpswt.hla.InteractionRoot_p.C2WInteractionRoot_p.EmbeddedMessaging;
import edu.vanderbilt.vuisis.cpswt.hla.InteractionRoot_p.C2WInteractionRoot_p.FederateJoinInteraction;
import edu.vanderbilt.vuisis.cpswt.hla.InteractionRoot_p.C2WInteractionRoot_p.FederateResignInteraction;
import edu.vanderbilt.vuisis.cpswt.hla.InteractionRoot_p.C2WInteractionRoot_p.SimulationControl_p.SimEnd;
import edu.vanderbilt.vuisis.cpswt.hla.InteractionRoot_p.DeleteProxy;
import edu.vanderbilt.vuisis.cpswt.utils.CpswtDefaults;
import edu.vanderbilt.vuisis.cpswt.utils.CpswtUtils;
import edu.vanderbilt.vuisis.cpswt.utils.FederateIdUtility;
import hla.rti.jlc.NullFederateAmbassador;
import hla.rti.jlc.RtiFactory;
import hla.rti.jlc.RtiFactoryFactory;

import static edu.vanderbilt.vuisis.cpswt.hla.ObjectRoot.ObjectReflector;
import static edu.vanderbilt.vuisis.cpswt.hla.ObjectRoot.ObjectReflectorComparator;

import java.io.File;
import java.io.PrintWriter;

import java.util.*;
import java.util.concurrent.PriorityBlockingQueue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import edu.vanderbilt.vuisis.cpswt.config.FederateConfig;
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
 * The SynchronizedFederate provides the following facilities which simplify the
 * writing of a federate:
 * <ul>
 * <li>RTI creation/destruction ( {@link #createRTI()}, {@link #destroyRTI()} )</li>
 * <li>A means of acquiring a handle to the RTI ( {@link #getRTI()} )</li>
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
 */

@SuppressWarnings("unused")
public class SynchronizedFederate extends NullFederateAmbassador {

    static {
        AddProxy.load();
        DeleteProxy.load();
        SimEnd.load();
    }

    private static class UniquePriorityBlockingQueue<T> extends PriorityBlockingQueue<T> {

        private static int _unique_num = 0;

        private static int get_unique_num() {
            return _unique_num++;
        }

        public UniquePriorityBlockingQueue() {
            super();
        }

        public UniquePriorityBlockingQueue(Collection<? extends T> c) {
            super(c);
        }

        public UniquePriorityBlockingQueue(int initialCapacity) {
            super(initialCapacity);
        }

        public UniquePriorityBlockingQueue(int initialCapacity, Comparator<T> comparator) {
            super(initialCapacity, comparator);
        }

        private final int _uniqueNum = get_unique_num();

        public int getUniqueNum() {
            return _uniqueNum;
        }
    }

    private static class UniquePriorityBlockingQueueComparator<T> implements Comparator<UniquePriorityBlockingQueue<T>> {

        @Override
        public int compare(
                UniquePriorityBlockingQueue<T> uniquePriorityBlockingQueue1,
                UniquePriorityBlockingQueue<T> uniquePriorityBlockingQueue2
        ) {
            return uniquePriorityBlockingQueue1.getUniqueNum() - uniquePriorityBlockingQueue2.getUniqueNum();
        }
    }

    private static class PriorityBlockingMultiQueue<T> {
        private final Set<Integer> _uniqueNumSet = new HashSet<>();
        private final PriorityBlockingQueue<UniquePriorityBlockingQueue<T>> _priorityBlockingQueue =
                new PriorityBlockingQueue<>(2, new UniquePriorityBlockingQueueComparator<T>());

        public int size() {
            int size = 0;
            for (UniquePriorityBlockingQueue<T> uniquePriorityBlockingQueue : _priorityBlockingQueue) {
                size += uniquePriorityBlockingQueue.size();
            }
            return size;
        }

        public boolean isEmpty() {
            for (UniquePriorityBlockingQueue<T> uniquePriorityBlockingQueue : _priorityBlockingQueue) {
                if (!uniquePriorityBlockingQueue.isEmpty()) {
                    return true;
                }
            }
            return false;
        }

        public synchronized boolean add(UniquePriorityBlockingQueue<T> uniquePriorityBlockingQueue) {
            if (_uniqueNumSet.contains(uniquePriorityBlockingQueue.getUniqueNum())) {
                return true;
            }
            _uniqueNumSet.add(uniquePriorityBlockingQueue.getUniqueNum());
            return _priorityBlockingQueue.add(uniquePriorityBlockingQueue);
        }

        public T poll() {
            for (UniquePriorityBlockingQueue<T> uniquePriorityBlockingQueue : _priorityBlockingQueue) {
                T item = uniquePriorityBlockingQueue.poll();
                if (item != null) {
                    return item;
                }
            }
            return null;
        }

        public T peek() {
            for (UniquePriorityBlockingQueue<T> uniquePriorityBlockingQueue : _priorityBlockingQueue) {
                T item = uniquePriorityBlockingQueue.peek();
                if (item != null) {
                    return item;
                }
            }
            return null;
        }

        public synchronized T take() {

            while(true) {
                boolean notTaken = true;
                while(notTaken) {
                    try {
                        UniquePriorityBlockingQueue<T> uniquePriorityBlockingQueue = _priorityBlockingQueue.take();
                        notTaken = false;
                        T item = uniquePriorityBlockingQueue.poll();
                        if (item != null) {
                            _priorityBlockingQueue.add(uniquePriorityBlockingQueue);
                            return item;
                        }
                        _uniqueNumSet.remove(uniquePriorityBlockingQueue.getUniqueNum());
                    } catch (Exception ignored) {
                    }
                }
            }
        }
    }


    private static final Logger logger = LogManager.getLogger(SynchronizedFederate.class);
    public static final int internalThreadWaitTimeMs = 250;

    public static void load() { }

    /**
     * Local RTI component. This is where you submit the "requests"
     * to the RTIExec process that manages the whole federation.
     */
    public static final String FEDERATION_MANAGER_NAME = "FederationManager";

    private final Map<String, String> federateNameToProxyFederateNameMap = new HashMap<>();
    private final Map<String, Set<String>> proxyFederateNameToFederateNameSetMap = new HashMap<>();

    private void addProxy(String federateName, String proxyFederateName) {
        if (hasProxy(federateName)) {
            String currentProxyFederateName = federateNameToProxyFederateNameMap.get(federateName);
            logger.warn(
                    "Federate \"{}\" is already proxied by federate \"{}\".  " +
                            "You should delete this proxy before establishing a new one.  " +
                            "Federate \"{}\" will now be proxied by federate \"{}\"",
                    federateName, currentProxyFederateName,
                    federateName, proxyFederateName
            );
            deleteProxy(federateName);
        }

        federateNameToProxyFederateNameMap.put(federateName, proxyFederateName);

        if (!proxyFederateNameToFederateNameSetMap.containsKey(proxyFederateName)) {
            proxyFederateNameToFederateNameSetMap.put(proxyFederateName, new HashSet<>());
        }
        proxyFederateNameToFederateNameSetMap.get(proxyFederateName).add(federateName);
    }

    private void deleteProxy(String federateName) {
        if (!hasProxy(federateName)) {
            logger.warn(
                    "deleteProxy:  There is currently no proxy for federate \"{}\".  Nothing to delete.", federateName
            );
            return;
        }

        String currentProxyFederateName = federateNameToProxyFederateNameMap.get(federateName);
        federateNameToProxyFederateNameMap.remove(federateName);

        Set<String> proxiedFederateNameSet = proxyFederateNameToFederateNameSetMap.get(currentProxyFederateName);
        proxiedFederateNameSet.remove(federateName);
        if (proxiedFederateNameSet.isEmpty()) {
            proxyFederateNameToFederateNameSetMap.remove(currentProxyFederateName);
        }
    }

    protected void addProxiedFederate(String federateName) {
        addProxy(federateName, getFederateType());

        AddProxy addProxyInteraction = new AddProxy();
        addProxyInteraction.set_proxyFederateName(getFederateType());
        addProxyInteraction.set_federateName(federateName);

        try {
            sendInteraction(addProxyInteraction);
            getRTI().tick();
        } catch (Exception e) {
            logger.warn("Could not send \"AddProxy\" interaction: ", e);
        }
    }

    protected void deleteProxiedFederate(String federateName) {
        deleteProxy(federateName);

        DeleteProxy deleteProxyInteraction = new DeleteProxy();
        deleteProxyInteraction.set_federateName(federateName);
        try {
            sendInteraction(deleteProxyInteraction);
            getRTI().tick();
        } catch (Exception e) {
            logger.warn("Could not send \"DeleteProxy\" interaction: ", e);
        }
    }

    protected boolean hasProxy(String federateName) {
        return federateNameToProxyFederateNameMap.containsKey(federateName);
    }

    protected String getProxyFor(String federateName) {
        return federateNameToProxyFederateNameMap.getOrDefault(federateName, null);
    }

    protected Set<String> getProxiedFederateNameSet(String federateName) {
        return proxyFederateNameToFederateNameSetMap.getOrDefault(federateName, new HashSet<>());
    }

    protected Set<String> getProxiedFederateNameSet() {
        return getProxiedFederateNameSet(getFederateType());
    }

    protected RTIambassador rti = null;

    /**
     * Get a handle to the RTI.
     *
     * @return handle (of type RTIambassador) to the RTI.  This can be used as
     * an argument to {@link InteractionRoot#sendInteraction(RTIambassador)} or
     * {@link ObjectRoot#updateAttributeValues(RTIambassador)} calls, for instance.
     */
    public RTIambassador getRTI() {
        return rti;
    }

    private int _status = 0;

    protected void setStatus(int status) {
        _status = status;
    }

    protected int getStatus() {
        return _status;
    }

    /**
     * General federate parameters
     */
    private final String federationId;

    /**
     * Returns the id (name) of the federation in which this federate is running.
     */
    public String getFederationId() {
        return federationId;
    }

    private final String federateType;
    public String getFederateType() {
        return this.federateType;
    }

    private final String federateId;

    /**
     * Returns the id (name) of this federate as registered with the federation
     * in which it is running.
     */
    public String getFederateId() {
        return federateId;
    }

    private final String federationJsonFileName;
    private final String federateDynamicMessagingJsonFileName;

    private final boolean isLateJoiner;
    public boolean isLateJoiner() { return this.isLateJoiner; }

    private final Set<String> _achievedSynchronizationPoints = new HashSet<>();

    private boolean _timeConstrainedNotEnabled = true;
    private boolean _timeRegulationNotEnabled = true;

    private boolean _timeAdvanceNotGranted = true;
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

    private boolean _simEndNotPubsub = true;

    private double lookahead;
    public double getLookahead() {
        return lookahead;
    }
    public void setLookahead(double lookahead) {
        this.lookahead = lookahead;
    }

    private double stepSize;

    public double getStepSize() { return this.stepSize; }
    private void setStepSize(double stepSize) { this.stepSize = stepSize; }

    private boolean _advanceTimeThreadNotStarted = true;
    private InteractionRoot _receivedSimEnd = null;

    protected boolean exitCondition = false;	// set to true when SimEnd is received

    protected final int federateRTIInitWaitTime;

    public SynchronizedFederate(FederateConfig federateConfig) {

        this.federationId = federateConfig.federationId;
        this.federateType = federateConfig.federateType;
        this.federateId = federateConfig.name == null || federateConfig.name.isEmpty() ?
                FederateIdUtility.generateID(this.federateType) : federateConfig.name;

        this.isLateJoiner = federateConfig.isLateJoiner;

        this.federationJsonFileName = federateConfig.federationJsonFileName;
        this.federateDynamicMessagingJsonFileName = federateConfig.federateDynamicMessagingJsonFileName;

        this.lookahead = federateConfig.lookahead;
        this.stepSize = federateConfig.stepSize;

        this.federateRTIInitWaitTime = federateConfig.federateRTIInitWaitTimeMs;
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
    private final List<FederateStateChangeListener> federateChangeEventListeners = new ArrayList<>();

    public void createRTI() throws RTIinternalError {
        if (this.rti == null) {
            logger.debug("Federate {} acquiring connection to RTI ...", this.federateId);
            RtiFactory factory = RtiFactoryFactory.getRtiFactory();
            this.rti = factory.createRtiAmbassador();
            logger.debug("Federate {} connection to RTI successful.", this.federateId);
        }
    }

    /**
     * Dissociate from the RTI.  This sets the handle to the RTI acquired via
     * {@link #createRTI} to null.  Thus, {@link #getRTI()} returns null after
     * this call.
     */
    public void destroyRTI() {
        rti = null;
    }

    public void readFederationJson(String federationJsonFileName) {
        if (federationJsonFileName == null || federationJsonFileName.isEmpty()) {
            return;
        }
        File federationJsonFile = new File(federationJsonFileName);
        InteractionRoot.readFederationJson(federationJsonFile);
        ObjectRoot.readFederationJson(federationJsonFile);
    }

    public void initializeMessaging() {
        InteractionRoot.init(getRTI());
        ObjectRoot.init(getRTI());
    }

    public void initializeDynamicMessaging(File federationJsonFile, File federateDynamicMessagingClassesJsonFile) {
        InteractionRoot.loadDynamicClassFederationData(federationJsonFile, federateDynamicMessagingClassesJsonFile);
        ObjectRoot.loadDynamicClassFederationData(federationJsonFile, federateDynamicMessagingClassesJsonFile);
        initializeMessaging();
    }

    public void initializeDynamicMessaging(
            String federationJsonFileName,
            String federateDynamicMessagingClassesJsonFileName
    ) {
        if (
                federationJsonFileName == null ||
                        federationJsonFileName.isEmpty() ||
                        federateDynamicMessagingClassesJsonFileName == null ||
                        federateDynamicMessagingClassesJsonFileName.isEmpty()
        ) {
            initializeMessaging();
            return;
        }
        File federationJsonFile = new File(federationJsonFileName);
        File federateDynamicMessagingClassesJsonFile = new File(federateDynamicMessagingClassesJsonFileName);
        initializeDynamicMessaging(federationJsonFile, federateDynamicMessagingClassesJsonFile);
    }

    public void notifyFederationOfJoin() {
        synchronized (rti) {
            // every federate will send a "FederateJoinInteraction" and a "FederateResignInteraction"
            // so we need to publish these objects on current RTI
            FederateJoinInteraction.publish_interaction(this.rti);
            FederateResignInteraction.publish_interaction(this.rti);

            // create a notification for "join" and send it
            FederateJoinInteraction joinInteraction = new FederateJoinInteraction();
            joinInteraction.set_FederateId(this.federateId);
            joinInteraction.set_FederateType(this.federateType);
            joinInteraction.set_IsLateJoiner(this.isLateJoiner);

            try {
                logger.trace("Sending FederateJoinInteraction for federate {}", this.federateId);
                sendInteraction(joinInteraction);
            }
            catch (Exception ex) {
                logger.error(
                        "Error while sending FederateJoinInteraction for federate \"{}\": , {}", this.federateId, ex
                );
            }
        }
    }

    /**
     * Ensures that the federate is subscribed to SimEnd interaction.
     */
    private void ensureSimEndPubsub() {

        if (_simEndNotPubsub) {
            SimEnd.publish_interaction(getRTI());
            // Auto-subscribing also ensures that there is no filter set for SimEnd
            SimEnd.subscribe_interaction(getRTI());
            _simEndNotPubsub = false;
        }
    }

    /**
     * Joins the federate to a particular federation.
     */
    public void joinFederation() {
        boolean federationNotPresent = true;
        int attempts = 0;
        while (federationNotPresent) {
            try {
                attempts++;
                logger.debug("[{}] federate joining federation [{}] attempt #{}", this.federateId, this.federationId, attempts);
                synchronized (rti) {
                    rti.joinFederationExecution(this.federateId, this.federationId, this, null);
                }
                federationNotPresent = false;
                logger.debug("[{}] federate joined federation [{}] successfully", this.federateId, this.federationId);
            } catch (FederateAlreadyExecutionMember f) {
                logger.error("Federate already execution member: {}", f.toString());
                return;
            } catch(FederationExecutionDoesNotExist ex) {
                if(attempts < CpswtDefaults.MaxJoinResignAttempt) {
                    logger.warn("Federation with the name {} doesn't exist (yet). Trying to wait and join...", this.federationId);
                    CpswtUtils.sleep(CpswtDefaults.JoinResignWaitMillis);
                }
                else {
                    logger.error("Federation was not found with the name {}. Quitting...", this.federationId);
                    return;
                }
            } catch (Exception e) {
                logger.error("General error while trying to join federation. {}", e.toString());
            }
        }

        initializeDynamicMessaging(
          federationJsonFileName, federateDynamicMessagingJsonFileName
        );

        this.ensureSimEndPubsub();

        AddProxy.subscribe_interaction(getRTI());
        AddProxy.publish_interaction(getRTI());

        DeleteProxy.subscribe_interaction(getRTI());
        DeleteProxy.publish_interaction(getRTI());

        this.notifyFederationOfJoin();
    }

    public InteractionRoot updateFederateSequence(InteractionRoot interactionRoot) {

        String proxiedFederateName = interactionRoot.getProxiedFederateName();

        List<String> federateTypeList = new ArrayList<>();

        if ( proxiedFederateName != null && hasProxy(proxiedFederateName) ) {

            String proxyFederateName = getProxyFor(proxiedFederateName);
            if (proxyFederateName.equals(getFederateType())) {
                federateTypeList.add(proxiedFederateName);
            }
            federateTypeList.addAll(List.of(proxyFederateName, proxiedFederateName));

        } else {

            federateTypeList.add(getFederateType());
        }

        return C2WInteractionRoot.update_federate_sequence(interactionRoot, federateTypeList);

    }

    public void sendInteraction(
            InteractionRoot interactionRoot, Set<String> federateNameSet, double time
    ) throws Exception {

        if (!interactionRoot.isInstanceHlaClassDerivedFromHlaClass(EmbeddedMessaging.get_hla_class_name())) {

            InteractionRoot newInteractionRoot = updateFederateSequence(interactionRoot);
            String interactionJson = newInteractionRoot.toJson(time);

            for (String federateName : federateNameSet) {
                String embeddedMessagingHlaClassName = EmbeddedMessaging.get_hla_class_name() + "." + federateName;
                InteractionRoot embeddedMessagingForNetworkFederate = new InteractionRoot(
                        embeddedMessagingHlaClassName
                );
                if (interactionRoot.isInstanceHlaClassDerivedFromHlaClass(C2WInteractionRoot.get_hla_class_name())) {
                    embeddedMessagingForNetworkFederate.setParameter(
                            "federateSequence",
                            interactionRoot.getParameter("federateSequence")
                    );
                }
                embeddedMessagingForNetworkFederate.setParameter("messagingJson", interactionJson);

                if (time >= 0) {
                    sendInteraction(embeddedMessagingForNetworkFederate, time);
                } else {
                    sendInteraction(embeddedMessagingForNetworkFederate);
                }
            }
        }
    }

    public void sendInteraction(InteractionRoot interactionRoot, String federateName, double time) throws Exception {

        Set<String> stringSet = new HashSet<>();
        stringSet.add(federateName);
        sendInteraction(interactionRoot, stringSet, time);
    }

    public void sendInteraction( InteractionRoot interactionRoot, double time ) throws Exception {
        if (interactionRoot.getIsPublished()) {
            InteractionRoot newInteractionRoot = updateFederateSequence(interactionRoot);
            if (_advanceTimeThread == null) {
                logger.warn(
                        "AdvanceTimeThread not started:  sending time-based \"{}\" interaction directly",
                        newInteractionRoot.getHlaClassName()
                );
                newInteractionRoot.sendInteraction(getRTI(), time);
            } else {
                _advanceTimeThread.sendInteraction(newInteractionRoot, time);
//                newInteractionRoot.sendInteraction(getRTI(), time);
            }
        }

        sendInteraction(interactionRoot, interactionRoot.getFederateNameSoftPublishSet(), time);
    }

    public void sendInteraction(InteractionRoot interactionRoot) throws Exception {
        if (interactionRoot.getIsPublished()) {
            InteractionRoot newInteractionRoot = updateFederateSequence(interactionRoot);
            newInteractionRoot.sendInteraction(getRTI());
        }

        sendInteraction(interactionRoot, interactionRoot.getFederateNameSoftPublishSet(),-1);
    }

    public void sendInteraction(InteractionRoot interactionRoot, String federateName) throws Exception {

        Set<String> stringSet = new HashSet<>();
        stringSet.add(federateName);
        sendInteraction(interactionRoot, stringSet, -1);
    }

    public void registerObject(ObjectRoot objectRoot) throws Exception {
        objectRoot.registerObject(getRTI());
    }

    public void unregisterObject(ObjectRoot objectRoot) {
        objectRoot.unregisterObject(getRTI());
    }

    private void sendInteraction(
            String objectReflectorJson,
            String hlaClassName,
            String federateSequence,
            Set<String> federateNameSet,
            double time
    ) throws Exception {
        for(String federateName : federateNameSet) {
            String embeddedMessagingHlaClassName = EmbeddedMessaging.get_hla_class_name() + "." + federateName;
            InteractionRoot embeddedMessagingForNetworkFederate = new InteractionRoot(
                    embeddedMessagingHlaClassName
            );
            embeddedMessagingForNetworkFederate.setParameter("federateSequence", federateSequence);
            embeddedMessagingForNetworkFederate.setParameter("messagingJson", objectReflectorJson);

            if (time >= 0) {
                sendInteraction(embeddedMessagingForNetworkFederate, time);
            } else {
                sendInteraction(embeddedMessagingForNetworkFederate);
            }
        }
    }

    public void sendInteraction(
            ObjectReflector objectReflector, Set<String> federateNameSet, double time
    ) throws Exception {
        sendInteraction(
                objectReflector.toJson(time),
                objectReflector.getHlaClassName(),
                objectReflector.getFederateSequence(),
                federateNameSet,
                time
        );
    }
    
    public void sendInteraction(
            ObjectRoot objectRoot, Set<String> federateNameSet, double time, boolean force
    ) throws Exception {
        sendInteraction(
                objectRoot.toJson(force, time),
                objectRoot.getInstanceHlaClassName(),
                "[]",
                federateNameSet,
                time
        );
    }

    public void sendInteraction(ObjectRoot objectRoot, Set<String> federateNameSet, double time) throws Exception {
        sendInteraction(objectRoot, federateNameSet, time, false);
    }

    public void sendInteraction(ObjectReflector objectReflector, String federateName, double time) throws Exception {
        Set<String> stringSet = new HashSet<>();
        stringSet.add(federateName);
        sendInteraction(objectReflector, stringSet, time);
    }

    public void sendInteraction(ObjectRoot objectRoot, String federateName, double time) throws Exception {
        Set<String> stringSet = new HashSet<>();
        stringSet.add(federateName);
        sendInteraction(objectRoot, stringSet, time);
    }

    public void sendInteraction(ObjectReflector objectReflector, String federateName) throws Exception {
        Set<String> stringSet = new HashSet<>();
        stringSet.add(federateName);
        sendInteraction(objectReflector, stringSet, -1);
    }

    public void sendInteraction(ObjectRoot objectRoot, String federateName) throws Exception {
        Set<String> stringSet = new HashSet<>();
        stringSet.add(federateName);
        sendInteraction(objectRoot, stringSet, -1);
    }

    public void updateAttributeValues(ObjectRoot objectRoot, double time, boolean force) throws Exception {
        Set<ObjectRootInterface.ClassAndPropertyName> attributesToBeUpdatedClassAndPropertyNameSet =
                objectRoot.getAttributesToBeUpdatedClassAndPropertyNameSet();

        objectRoot.updateAttributeValues(getRTI(), time, force);

        objectRoot.restoreAttributesToBeUpdated(attributesToBeUpdatedClassAndPropertyNameSet);
        sendInteraction(objectRoot, objectRoot.getFederateNameSoftPublishSet(), time, force);
    }

    public void updateAttributeValues(ObjectRoot objectRoot, double time) throws Exception {
        updateAttributeValues(objectRoot, time, false);
    }

    public void updateAttributeValues(ObjectRoot objectRoot, boolean force) throws Exception {
        Set<ObjectRootInterface.ClassAndPropertyName> attributesToBeUpdatedClassAndPropertyNameSet =
                objectRoot.getAttributesToBeUpdatedClassAndPropertyNameSet();

        objectRoot.updateAttributeValues(getRTI(), force);

        objectRoot.restoreAttributesToBeUpdated(attributesToBeUpdatedClassAndPropertyNameSet);
        sendInteraction(objectRoot, objectRoot.getFederateNameSoftPublishSet(), -1, true);
    }

    public void updateAttributeValues(ObjectRoot objectRoot) throws Exception {
        updateAttributeValues(objectRoot, false);
    }

    /**
     * RTI callback -- DO NOT OVERRIDE.  SynchronizedFederate class uses this
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

        boolean enableTimeConstrainedNotCalled = true;
        while (enableTimeConstrainedNotCalled) {
            try {
                synchronized (rti) {
                    rti.enableTimeConstrained();
                }
                enableTimeConstrainedNotCalled = false;
            } catch (TimeConstrainedAlreadyEnabled t) {
                return;
            } catch (EnableTimeConstrainedPending e) {
                enableTimeConstrainedNotCalled = false;
            } catch (FederateNotExecutionMember f) {
                throw f;
            } catch (Exception e) {
                CpswtUtils.sleep(SynchronizedFederate.internalThreadWaitTimeMs);
            }
        }

        try {
            synchronized (rti) {
                rti.tick();
            }
        } catch (Exception ignored) {
        }
        while (_timeConstrainedNotEnabled) {
            CpswtUtils.sleep(SynchronizedFederate.internalThreadWaitTimeMs);
            try {
                synchronized (rti) {
                    rti.tick();
                }
            } catch (Exception ignored) {
            }
        }
    }

    // DISABLING TIME CONSTRAINED IN PORTICO IS PROBLEMATIC.
    // IF THE FEDERATE (A) THAT CALLS THIS METHOD EXITS RIGHT AFTER CALLING, THE OTHER
    // FEDERATES (b) WILL INCUR AN EXCEPTION, SPEC. IN THE "TimeManager.class" FILE,
    // LINE 150.  HERE, THERE IS A TABLE THAT NEEDS TO CONTAIN INFO ABOUT THE
    // FEDERATE (A) THAT HAS DISABLED ITS TIME-REGULATION, BUT THIS INFORMATION IS
    // NO LONGER AVAILABLE AS THE FEDERATE (A) HAS EXITED, SO THE FEDERATES (B) INCUR
    // AN NPE.
    // EVEN IF FEDERATE (A) WAITS BEFORE EXITING AFTER CALLING THIS METHOD, THE
    // OTHER FEDERATES (B) NEVER RECEIVED A TIME-ADVANCE-GRANT FOR SOME REASON.
//    public void disableTimeConstrained() throws FederateNotExecutionMember {
//        if (_timeConstrainedNotEnabled) {
//            return;
//        }
//
//        boolean disableTimeConstrainedNotCalled = true;
//        while(disableTimeConstrainedNotCalled) {
//            try {
//                synchronized (rti) {
//                    rti.disableTimeConstrained();
//                }
//                disableTimeConstrainedNotCalled = false;
//            } catch (TimeConstrainedWasNotEnabled t) {
//                break;
//            } catch (FederateNotExecutionMember f) {
//                throw f;
//            } catch (Exception e) {
//                logger.warn("Exception encountered on \"disableTimeConstrained\"", e);
//                e.printStackTrace();
//                CpswtUtils.sleep(SynchronizedFederate.internalThreadWaitTimeMs);
//            }
//        }
//
//        try {
//            synchronized (rti) {
//                rti.tick();
//            }
//        } catch (Exception ignored) {
//        }
//        int counter = 0;
//        while (counter < 40) {
//            CpswtUtils.sleep(SynchronizedFederate.internalThreadWaitTimeMs);
//            try {
//                synchronized (rti) {
//                    rti.tick();
//                }
//            } catch (Exception ignored) {
//            }
//            ++counter;
//        }
//
//        _timeConstrainedNotEnabled = true;
//    }

    /**
     * RTI callback -- DO NOT OVERRIDE.  SynchronizedFederate class uses this
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
                synchronized (rti) {
                    rti.enableTimeRegulation(new DoubleTime(time), new DoubleTimeInterval(lookahead));
                }
                timeRegulationEnabledNotCalled = false;
            } catch (TimeRegulationAlreadyEnabled t) {
                return;
            } catch (EnableTimeRegulationPending e) {
                timeRegulationEnabledNotCalled = false;
            } catch (FederateNotExecutionMember | InvalidFederationTime | InvalidLookahead ex) {
                throw ex;
            } catch (Exception e) {
                CpswtUtils.sleep(SynchronizedFederate.internalThreadWaitTimeMs);
            }
        }

        try {
            synchronized (rti) {
                rti.tick();
            }
        } catch (Exception ignored) {
        }
        while (_timeRegulationNotEnabled) {
            CpswtUtils.sleep(SynchronizedFederate.internalThreadWaitTimeMs);
            try {
                synchronized (rti) {
                    rti.tick();
                }
            } catch (Exception ignored) {
            }
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
     * When a federate calls this method, it stops time-regulating within
     * its federation.
     */
    // DISABLING TIME REGULATION IN PORTICO IS PROBLEMATIC.
    // IF THE FEDERATE (A) THAT CALLS THIS METHOD EXITS RIGHT AFTER CALLING, THE OTHER
    // FEDERATES (b) WILL INCUR AN EXCEPTION, SPEC. IN THE "TimeManager.class" FILE,
    // LINE 150.  HERE, THERE IS A TABLE THAT NEEDS TO CONTAIN INFO ABOUT THE
    // FEDERATE (A) THAT HAS DISABLED ITS TIME-REGULATION, BUT THIS INFORMATION IS
    // NO LONGER AVAILABLE AS THE FEDERATE (A) HAS EXITED, SO THE FEDERATES (B) INCUR
    // AN NPE.
    // EVEN IF FEDERATE (A) WAITS BEFORE EXITING AFTER CALLING THIS METHOD, THE
    // OTHER FEDERATES (B) NEVER RECEIVED A TIME-ADVANCE-GRANT FOR SOME REASON.
    public void disableTimeRegulation() throws FederateNotExecutionMember {

        if (_timeRegulationNotEnabled) {
            return;
        }

        boolean timeRegulationDisabledNotCalled = true;
        while (timeRegulationDisabledNotCalled) {
            try {
                synchronized (rti) {
                    rti.disableTimeRegulation();
                }
                timeRegulationDisabledNotCalled = false;
            } catch (TimeRegulationWasNotEnabled t) {
                break;
            } catch (FederateNotExecutionMember f) {
                throw f;
            } catch (Exception e) {
                CpswtUtils.sleep(SynchronizedFederate.internalThreadWaitTimeMs);
            }
        }

        try {
            synchronized (rti) {
                rti.tick();
            }
        } catch (Exception ignored) {
        }

        _timeRegulationNotEnabled = true;
    }

    /**
     * Enables asynchronous delivery for the federate.
     */
    public void enableAsynchronousDelivery() {

        boolean asynchronousDeliveryNotEnabled = true;
        while(asynchronousDeliveryNotEnabled) {
            try {
                this.rti.enableAsynchronousDelivery();
                asynchronousDeliveryNotEnabled = false;
            } catch (FederateNotExecutionMember f) {
                logger.error("ERROR:  Could not enable asynchronous delivery:  Federate Not Execution Member");
                logger.error(f);
                return;
            } catch (AsynchronousDeliveryAlreadyEnabled a) {
                return;
            } catch (Exception e) {
                logger.warn("WARNING:  problem encountered enabling asynchronous delivery:  retry");
                logger.warn(e);
                CpswtUtils.sleep(SynchronizedFederate.internalThreadWaitTimeMs);
            }
        }
    }

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
        int attempts = 0;
        while (federationNotResigned) {
            try {
                attempts++;
                getRTI().resignFederationExecution(resignAction);
                federationNotResigned = false;
            } catch (InvalidResignAction i) {
                logger.warn("WARNING:  Invalid resign action when attempting to resign federation.  Changing resign action to DELETE_OBJECTS_AND_RELEASE_ATTRIBUTES.");
                resignAction = ResignAction.DELETE_OBJECTS_AND_RELEASE_ATTRIBUTES;
            } catch (FederateNotExecutionMember f) {
                logger.warn("WARNING:  While resigning federation:  federate not execution member.");
                return;
            } catch (FederateOwnsAttributes f) {
                logger.warn("WARNING:  While resigning federation:  federate owns attributes.  Releasing attributes.");
                resignAction |= ResignAction.RELEASE_ATTRIBUTES;
            } catch (Exception e) {
                if (attempts < CpswtDefaults.MaxJoinResignAttempt) {
                    logger.warn("WARNING:  problem encountered while resigning federation execution: {} | retrying #{}",
                            e.getMessage(), attempts);

                    CpswtUtils.sleep(CpswtDefaults.JoinResignWaitMillis);
                }
                else {
                    logger.error( "Resigned Failed. Exiting from the Federation" );
                    federationNotResigned = false;
                    logger.error(e);
                }
            }
        }
    }

    /**
     * Resigns federate from the federation execution with resign action of
     * DELETE_OBJECTS_AND_RELEASE_ATTRIBUTES.
     */
    public void resignFederationExecution() {
        resignFederationExecution(ResignAction.DELETE_OBJECTS_AND_RELEASE_ATTRIBUTES);
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
     * @throws FederateNotExecutionMember Thrown if federate is not a member of the federation
     * @throws RTIinternalError Thrown if there is an error in the RTI
     */
    public void readyToPopulate() throws FederateNotExecutionMember, RTIinternalError {
        ensureSimEndPubsub();

        achieveSynchronizationPoint(SynchronizationPoints.ReadyToPopulate);
    }

    /**
     * Federate should call this method when it has reached a point in execution
     * where it is ready to run the simulation.  It will cause the federate to
     * suspend execution until all other federates in the federation called this
     * method, that is, they also are ready to run the simulation.
     *
     * @throws FederateNotExecutionMember Thrown if federate is not a member of the federation
     * @throws RTIinternalError Thrown if there is an error in the RTI
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
     * @throws FederateNotExecutionMember Thrown if federate is not a member of the federation
     * @throws RTIinternalError Thrown if there is an error in the RTI
     */
    public void readyToResign() throws FederateNotExecutionMember, RTIinternalError {
        achieveSynchronizationPoint(SynchronizationPoints.ReadyToResign);
        this.setFederateState(FederateState.TERMINATING);
    }

    private void achieveSynchronizationPoint(String label) throws FederateNotExecutionMember, RTIinternalError {
        logger.trace("achieveSynchronizationPoint==>");
        boolean synchronizationPointNotAccepted = true;
        while (synchronizationPointNotAccepted) {
            try {
                synchronized (rti) {
                    rti.synchronizationPointAchieved(label);
                }
                while (!_achievedSynchronizationPoints.contains(label)) {
                    CpswtUtils.sleep(SynchronizedFederate.internalThreadWaitTimeMs);
                    synchronized (rti) {
                         rti.tick();
                    }
                }
                synchronizationPointNotAccepted = false;
            } catch (FederateNotExecutionMember f) {
                throw f;
            } catch (SynchronizationLabelNotAnnounced s) {
                if (_achievedSynchronizationPoints.contains(label)) {
                    synchronizationPointNotAccepted = false;
                } else {
                    synchronized (rti) {
                        try {
                            rti.tick();
                        } catch (RTIinternalError r) {
                            throw r;
                        } catch (Exception e) {
                            CpswtUtils.sleep(SynchronizedFederate.internalThreadWaitTimeMs);
                        }
                    }
                }
            } catch (Exception e) {
                CpswtUtils.sleep(SynchronizedFederate.internalThreadWaitTimeMs);
            }
        }
        logger.trace("<==achieveSynchronizationPoint");
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
                logger.error("SynchronizedFederate:  getCurrentTime:  ERROR:  Federate not execution member");
                logger.error(f);
                return -1;
            } catch (Exception e) {
                logger.error("SynchronizedFederate:  getCurrentTime:  Exception caught: {}", e.getMessage());
                logger.error(e);
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
                logger.error("SynchronizedFederate:  getLBTS:  ERROR:  Federate not execution member");
                logger.error(f);
                return -1;
            } catch (Exception e) {
                logger.error("SynchronizedFederate:  getLBTS:  Exception caught:  " + e.getMessage());
                logger.error(e);
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
                logger.error("SynchronizedFederate:  getMinTSOTimestamp:  ERROR:  Federate not execution member");
                logger.error(f);
                return -1;
            } catch (Exception e) {
                logger.error("SynchronizedFederate:  getMinTSOTimestamp:  Exception caught: {}", e.getMessage());
                logger.error(e);
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

        return Math.max(dblLBTSTime, timestampWithLogicalTime);
    }

    private final ATRQueue _atrQueue = new ATRQueue(100, new ATRComparator());

    private AdvanceTimeThread _advanceTimeThread = null;

    /**
     * DO NOT USE -- Should only be used directly by the SynchronizedFederate class.
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
     * @param advanceTimeRequest Contains RTI time to which to advance this federate
     */
    public final void putAdvanceTimeRequest(AdvanceTimeRequest advanceTimeRequest) {
        _atrQueue.put(advanceTimeRequest);
    }

    /**
     * Start the {@link AdvanceTimeThread}
     * Assumes the federate is a lookahead value greater than zero. Uses
     * {@link hla.rti.RTIambassador#timeAdvanceRequest(LogicalTime)} for advancing
     * federates time.
     */
    protected void startAdvanceTimeThread() {
        if (_advanceTimeThreadNotStarted) {
            _advanceTimeThread = new AdvanceTimeThread(this, this._atrQueue, TimeAdvanceMode.TimeAdvanceRequest);
            _advanceTimeThread.start();
            _advanceTimeThreadNotStarted = false;
        }
    }

    /**
     * Start the {@link AdvanceTimeThread}
     *
     * @param #timeAdvanceMode If
     *                         {@link TimeAdvanceMode#TimeAdvanceRequestAvailable} or
     *                         {@link TimeAdvanceMode#NextEventRequestAvailable} is used, the
     *                         federate's lookahead value is allowed to be zero. For other two cases,
     *                         federate's lookahead must be greater than zero.
     */
    protected void startAdvanceTimeThread(TimeAdvanceMode timeAdvanceMode) {
        if (_advanceTimeThreadNotStarted) {
            _advanceTimeThread = new AdvanceTimeThread(this, this._atrQueue, timeAdvanceMode);
            _advanceTimeThread.start();
            _advanceTimeThreadNotStarted = false;
        }
    }

    private void waitForAdvanceTimeThreadToTerminate() {
        boolean advanceTimeThreadNotExited = true;
        while(advanceTimeThreadNotExited) {
            try {
                _advanceTimeThread.join();
                advanceTimeThreadNotExited = false;
            } catch(InterruptedException interruptedException) {
                CpswtUtils.sleep(500);
            } catch(Exception e) {
                logger.error("Exception caught on waiting for AdvanceTimeThread to terminate", e);
                e.printStackTrace();
                advanceTimeThreadNotExited = false;
            }
        }
        _advanceTimeThreadNotStarted = true;
    }

    protected void terminateAdvanceTimeThread(AdvanceTimeRequest advanceTimeRequest) {
        advanceTimeRequest.requestSyncEnd();
        waitForAdvanceTimeThreadToTerminate();
    }

    private final UniquePriorityBlockingQueue<InteractionRoot> _interactionQueueWithoutTime =
            new UniquePriorityBlockingQueue<>(10, new InteractionRootComparator());

    private final UniquePriorityBlockingQueue<InteractionRoot> _interactionQueueWithTime =
            new UniquePriorityBlockingQueue<>(10, new InteractionRootComparator());

    private final PriorityBlockingMultiQueue<InteractionRoot> _fullInteractionQueue =
            new PriorityBlockingMultiQueue<>();

    /**
     * DO NOT USE -- Should only be used directly by the SynchronizedFederate class.
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
    public void addInteraction(InteractionRoot interactionRoot) {
        logger.trace("Received: {}", interactionRoot);
        if (interactionRoot.getTime() >= 0) {
            _interactionQueueWithTime.add(interactionRoot);
            _fullInteractionQueue.add(_interactionQueueWithTime);
        } else {
            _interactionQueueWithoutTime.add(interactionRoot);
            _fullInteractionQueue.add(_interactionQueueWithoutTime);
        }
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
    public InteractionRoot getNextInteraction() {
        return _fullInteractionQueue.take();
    }

    /**
     * Returns a boolean value indicating that there are interactions from the
     * RTI that can be retrieved via the {@link #getNextInteraction()} or
     * {@link #getNextInteractionNoWait()} methods.
     *
     * @return true if there are interactions available on the queue internal
     * to the {@link InteractionRoot} class.  False, otherwise.
     */
    public boolean isNotEmpty() {
        return !_fullInteractionQueue.isEmpty();
    }

    /**
     * Like {@link #getNextInteraction()}, but returns immediately with a null
     * value if no interaction is available.
     *
     * @return the next interaction received from the RTI in order of timestamp,
     * where receive-order interactions have a timestamp of -1, or null if there
     * are no interactions currently available
     */
    public InteractionRoot getNextInteractionNoWait() {
        InteractionRoot peekInteractionRoot = _fullInteractionQueue.peek();
        if (peekInteractionRoot == null) {
            return null;
        }
        if (peekInteractionRoot.getTime() > getCurrentTime()) {
            return null;
        }
        InteractionRoot interactionRoot = _fullInteractionQueue.poll();
        logger.trace("Removed interaction from queue (poll), size now = {}", _fullInteractionQueue.size());
        return interactionRoot;
    }

    public InteractionRoot getNextInteractionWithTime() {
        InteractionRoot interactionRoot = null;
        boolean takeNotComplete = true;
        while (takeNotComplete) {
            try {
                interactionRoot = _interactionQueueWithTime.take();
                takeNotComplete = false;
            } catch (InterruptedException ignored) {
            }
        }
        return interactionRoot;
    }

    public boolean isNotEmptyWithTime() {
        return !_interactionQueueWithTime.isEmpty();
    }

    public InteractionRoot getNextInteractionWithTimeNoWait() {
        InteractionRoot peekInteractionRoot = _interactionQueueWithTime.peek();
        if (peekInteractionRoot == null) {
            return null;
        }
        if (peekInteractionRoot.getTime() > getCurrentTime()) {
            return null;
        }
        InteractionRoot interactionRoot = _interactionQueueWithTime.poll();
        logger.trace("Removed interaction from queue (poll), size now = {}", _interactionQueueWithTime.size());
        return interactionRoot;
    }

    public InteractionRoot getNextInteractionWithoutTime() {
        InteractionRoot interactionRoot = null;
        boolean takeNotComplete = true;
        while (takeNotComplete) {
            try {
                interactionRoot = _interactionQueueWithoutTime.take();
                takeNotComplete = false;
            } catch (InterruptedException ignored) {
            }

        }
        return interactionRoot;
    }

    public boolean isNotEmptyWithoutTime() {
        return !_interactionQueueWithoutTime.isEmpty();
    }

    public InteractionRoot getNextInteractionWithoutTimeNoWait() {
        InteractionRoot peekInteractionRoot = _interactionQueueWithoutTime.peek();
        if (peekInteractionRoot == null) {
            return null;
        }
        if (peekInteractionRoot.getTime() > getCurrentTime()) {
            return null;
        }
        InteractionRoot interactionRoot = _interactionQueueWithoutTime.poll();
        logger.trace("Removed interaction from queue (poll), size now = {}", _interactionQueueWithoutTime.size());
        return interactionRoot;
    }

    /**
     * This should be overridden in the base classes of all federates
     */
    public boolean isMapperFederate() {
        return false;
    }

    private boolean unmatchingFedFilterProvided(InteractionRoot interactionRoot) {
        if (
                !isMapperFederate() &&
                interactionRoot.isInstanceHlaClassDerivedFromHlaClass(C2WInteractionRoot.get_hla_class_name())
        ) {
            String fedFilter = (String) interactionRoot.getParameter("federateFilter");
            if (fedFilter != null) {
                fedFilter = fedFilter.trim();
                if (!fedFilter.isEmpty() && (fedFilter.compareTo(getFederateId()) != 0)) {
                    logger.trace("Filtering due to fed filter: {}", fedFilter);
                    logger.trace("Filtered interaction was: {}", interactionRoot);
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
    public void receiveInteraction(int interactionClass, ReceivedInteraction theInteraction, byte[] userSuppliedTag) {
        logger.trace("SynchronizedFederate::receiveInteraction (no time): Received interactionClass as: {} and interaction as: {}", interactionClass, theInteraction);

        InteractionRoot interactionRoot = InteractionRoot.create_interaction(interactionClass, theInteraction);
        logger.trace(
                "SynchronizedFederate::receiveInteractionSF (no time): Created interaction root as: {}", interactionRoot
        );

        receiveInteractionAux(interactionRoot);
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
        logger.trace("SynchronizedFederate::receiveInteraction (with time): Received interactionClass as: {} and interaction as: {}", interactionClass, theInteraction);

        InteractionRoot interactionRoot = InteractionRoot.create_interaction(interactionClass, theInteraction, theTime);
        logger.trace(
                "SynchronizedFederate::receiveInteractionSF (with time): Created interaction root as: {}",
                interactionRoot
        );

        receiveInteractionAux(interactionRoot);
    }

    private void receiveInteractionAux(InteractionRoot interactionRoot) {

        if (interactionRoot.isInstanceOfHlaClass(SimEnd.get_hla_class_name())) {
            exitImmediately();
        }

        if (interactionRoot.isInstanceOfHlaClass(AddProxy.get_hla_class_name())) {
            AddProxy addProxy = (AddProxy)interactionRoot;
            addProxy(addProxy.get_federateName(), addProxy.get_proxyFederateName());
            return;
        }

        if (interactionRoot.isInstanceOfHlaClass(DeleteProxy.get_hla_class_name())) {
            DeleteProxy deleteProxy = (DeleteProxy)interactionRoot;
            deleteProxy(deleteProxy.get_federateName());
            return;
        }

        if (!unmatchingFedFilterProvided(interactionRoot)) {

            String sourceFederateId = C2WInteractionRoot.get_source_federate_id(interactionRoot);
            if (
                    sourceFederateId != null &&
                            hasProxy(sourceFederateId) &&
                            getProxyFor(sourceFederateId).equals(getFederateType())
            ) {
                interactionRoot.setProxiedFederateName(sourceFederateId);
            }

            if (interactionRoot.isInstanceHlaClassDerivedFromHlaClass(EmbeddedMessaging.get_hla_class_name())) {
                receiveEmbeddedMessagingInteraction((EmbeddedMessaging)interactionRoot);
                return;
            }

            addInteraction(interactionRoot);
            // createLog(interactionClass, theInteraction, assumedTimestamp);
        }
    }

    private void processEmbeddedMessagingInteraction(ObjectNode jsonObject) {

        String messagingType = jsonObject.get("messaging_type").asText();
        String hlaClassName = jsonObject.get("messaging_name").asText();

        if ("interaction".equals(messagingType)) {
            if (!InteractionRoot.get_is_soft_subscribed(hlaClassName)) {
                logger.warn(
                        "SynchronizedFederate.processEmbeddedMessagingInteraction:  interaction class \"{}\" " +
                                "not soft subscribed",
                        hlaClassName
                );
                return;
            }
            InteractionRoot unwrappedInteraction = InteractionRoot.fromJson(jsonObject);

            receiveInteractionAux(unwrappedInteraction);
            return;
        }

        if ("object".equals(messagingType)) {
            if (!ObjectRoot.get_is_soft_subscribed(hlaClassName)) {
                logger.warn(
                        "SynchronizedFederate.processEmbeddedMessagingInteraction:  object class \"{}\" " +
                                "is not soft subscribed",
                        hlaClassName
                );
                return;
            }
            ObjectReflector objectReflector = ObjectRoot.fromJson(jsonObject);

            _objectReflectionQueue.add(objectReflector);
            return;
        }

        logger.warn(
                "SynchronizedFederate.processEmbeddedMessagingInteraction, unrecognized messaging type \"{}\"",
                messagingType
        );
    }

    private void receiveEmbeddedMessagingInteraction(EmbeddedMessaging embeddedMessaging) {

        String jsonObjectString = embeddedMessaging.get_messagingJson();
        JsonNode jsonNode;
        try {
            jsonNode = InteractionRoot.objectMapper.readTree(jsonObjectString);
        } catch (JsonProcessingException jsonProcessingException) {
            logger.error("Exception parsing JSON for interaction: ", jsonProcessingException);
            return;
        }

        if (jsonNode.isArray()) {
            for(JsonNode item: jsonNode) {
                processEmbeddedMessagingInteraction((ObjectNode)item);
            }
        } else {
            processEmbeddedMessagingInteraction((ObjectNode)jsonNode);
        }
    }

    private final PriorityBlockingQueue<ObjectReflector> _objectReflectionQueue =
            new PriorityBlockingQueue<>(10, new ObjectReflectorComparator());


    /**
     * DO NOT USE -- Should only be used directly by the SynchronizedFederate class.
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
    public void addObjectReflector(int objectHandle, ReflectedAttributes reflectedAttributes) {
        _objectReflectionQueue.add(new ObjectReflector(objectHandle, reflectedAttributes));
    }

    /**
     * DO NOT USE -- Should only be used directly by the SynchronizedFederate class.
     * This method like the {@link #addObjectReflector(int, ReflectedAttributes)}
     * method, except it is for attribute reflections that are "timestamp-ordered".
     *
     * @param objectHandle        handle (RTI assigned) to the object class instance for
     *                            which the reflected attributes are to be applied
     * @param reflectedAttributes attribute reflections for the object class
     *                            instance corresponding to objectHandle
     * @param logicalTime         timestamp of the attribute reflections
     */
    public void addObjectReflector(
            int objectHandle, ReflectedAttributes reflectedAttributes, LogicalTime logicalTime
    ) {
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
    public ObjectReflector getNextObjectReflector() {
        ObjectReflector objectReflection = null;
        boolean takeNotComplete = true;
        while (takeNotComplete) {
            try {
                objectReflection = _objectReflectionQueue.take();
                takeNotComplete = false;
            } catch (InterruptedException ignored) {
            }

        }
        return objectReflection;
    }

    /**
     * Like {@link #getNextObjectReflector()}, except returns null
     * if there are no ObjectReflectors on the queue.
     *
     * @return An object reflector is one was available on the queue, null otherwise.
     */
    public ObjectReflector getNextObjectReflectorNoWait() {
        ObjectReflector peekObjectReflector = _objectReflectionQueue.peek();
        if (peekObjectReflector == null) {
            return null;
        }
        if (peekObjectReflector.getTime() > getCurrentTime()) {
            return null;
        }
        ObjectReflector objectReflector = _objectReflectionQueue.poll();
        logger.trace("Removed object reflector from queue (poll), size now = {}", _objectReflectionQueue.size());
        return objectReflector;
    }

    /**
     * RTI callback -- DO NOT OVERRIDE.  SynchronizedFederate class uses this
     * method to detect new instances of object classes to which a federate has
     * subscribed that have been created by other federates in the federation.
     * When such an instance is detected, the SynchronizedFederate directs the
     * {@link ObjectRoot} class to create a local instance of this object and
     * place it in a table local to the ObjectRoot class.  This table is indexed
     * by the handles (RTI assigned) of the instances.
     *
     * @param objectHandle      handle (RTI assigned) of a new object class instance that
     *                       has been created by another federate in the federation and announced on
     *                       the RTI.
     * @param objectClassHandle handle (RTI assigned) of the object class to which the
     *                       new instance belongs.
     * @param objectName     name of the new object class instance (currently ignored).
     */
    @Override
    public void discoverObjectInstance(int objectHandle, int objectClassHandle, String objectName) {
        ObjectRoot.discover(objectClassHandle, objectHandle);
    }

    @Override
    public void removeObjectInstance(int theObject, byte[] userSuppliedTag) {
        ObjectRoot.remove_object(theObject);
    }

    @Override
    public void removeObjectInstance(
            int theObject, byte[] userSuppliedTag, LogicalTime theTime, EventRetractionHandle retractionHandle
    ) {
        ObjectRoot.remove_object(theObject);
    }

    /**
     * RTI callback -- DO NOT OVERRIDE.  SynchronizedFederate class uses this
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
        assumedTimestamp.setTime(Math.max(getLBTS(), getCurrentTime()));
        // createLog(theObject, theAttributes, assumedTimestamp);
    }

    /**
     * RTI callback -- DO NOT OVERRIDE.  SynchronizedFederate class uses this
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

    private void notifyFederationOfSimEnd() {
        SimEnd simEnd = new SimEnd();

        try {
            logger.trace("Sending SimEnd for federate {}", this.federateId);
            sendInteraction(simEnd);
            getRTI().tick();
        }
        catch(Exception ex) {
            logger.error("Error while sending SimEnd for federate {}", this.federateId);
        }
    }

    private void writeStatus() {
        // WRITE STATUS
        File statusDirectory = new File("StatusDirectory");
        statusDirectory.mkdirs();
        File statusFile = new File(statusDirectory, "exitStatus");
        try (
                PrintWriter printWriter = new PrintWriter(statusFile, "UTF-8")
        ) {
            printWriter.print(getStatus());
        } catch(Exception e) {
            logger.error("Error writing status to file \"" + statusFile.getAbsolutePath() + "\"", e);
            e.printStackTrace();
        }
    }

    /**
     * Processes graceful shut-down of hla federate
     *
     */
    public void exitGracefully()
    {
        logger.info("Exiting gracefully ....");

        notifyFederationOfSimEnd();

//        try {
//            disableTimeConstrained();
//        } catch (Exception e) {
//            logger.warn("Exception encountered in \"disableTimeConstrained\"", e);
//            e.printStackTrace();
//        }
//
//        try {
//            disableTimeRegulation();
//        } catch (Exception e) {
//            logger.warn("Exception encountered in \"disableTimeRegulation\"", e);
//            e.printStackTrace();
//        }

        writeStatus();
//        if (!isLateJoiner()) {
//            try {
//                readyToResign();
//            } catch (Exception e) {
//                logger.error("Exception on ready-to-resign synch-point", e);
//                e.printStackTrace();
//            }
//        }

//        resignFederationExecution(ResignAction.DELETE_OBJECTS);

        // SLEEP 3 SECONDS TO ALLOW SimEnd TO BE SENT
        CpswtUtils.sleep(3000);
    }

    public void exitImmediately() {
        writeStatus();
//        resignFederationExecution(ResignAction.DELETE_OBJECTS);
        System.exit(0);
    }
}
