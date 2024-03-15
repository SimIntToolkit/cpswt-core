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

package edu.vanderbilt.vuisis.cpswt.hla.base;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import edu.vanderbilt.vuisis.cpswt.hla.InteractionRoot;
import edu.vanderbilt.vuisis.cpswt.hla.InteractionRoot_p.C2WInteractionRoot_p.EmbeddedMessaging;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import edu.vanderbilt.vuisis.cpswt.hla.SynchronizedFederate;
import hla.rti.FederationTimeAlreadyPassed;
import hla.rti.RTIambassador;
import edu.vanderbilt.vuisis.cpswt.utils.CpswtUtils;
import org.portico.impl.hla13.types.DoubleTime;

import java.util.*;
import java.util.concurrent.ConcurrentSkipListMap;

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
 */
public class AdvanceTimeThread extends Thread {
    private static final Logger logger = LogManager.getLogger(AdvanceTimeThread.class);

    // private double _atrStepSize = 0.2;

    private final ATRQueue _atrQueue;

    private final SynchronizedFederate _synchronizedFederate;
    private final RTIambassador _rti;
    private final TimeAdvanceMode _timeAdvanceMode;

    private final ConcurrentSkipListMap<Double, List<InteractionRoot>> _timeToSentInteractionListMap =
            new ConcurrentSkipListMap<>();

    public AdvanceTimeThread(SynchronizedFederate synchronizedFederate, ATRQueue atrQueue, TimeAdvanceMode timeAdvanceMode) {
        _synchronizedFederate = synchronizedFederate;
        _rti = _synchronizedFederate.getRTI();
        _atrQueue = atrQueue;
        _timeAdvanceMode = timeAdvanceMode;
    }

    //
    // OMLY TIME-BASED INTERACTIONS ARE SENT USING THE ADVANCE-TIME-THREAD!!
    //
    public void sendInteraction(InteractionRoot interactionRoot, double time) {

        if (!_timeToSentInteractionListMap.containsKey(time)) {
            _timeToSentInteractionListMap.put(time, new ArrayList<>());
        }
        _timeToSentInteractionListMap.get(time).add(interactionRoot);
    }

    public void run() {

        double currentTime = _synchronizedFederate.getCurrentTime();
        if (currentTime < 0) return;

        while (true) {
            AdvanceTimeRequest advanceTimeRequest = _atrQueue.peek();
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

            double requestedTime = advanceTimeRequest.getRequestedTime();

            Map<String, List<InteractionRoot>> hlaClassNameToInteractionListMap = new HashMap<>();

            Set<Double> keySet = new TreeSet<>(_timeToSentInteractionListMap.keySet());

            for (double key : keySet) {
                if (key > requestedTime) {
                    break;
                }

                List<InteractionRoot> interactionRootList = _timeToSentInteractionListMap.get(key);
                for (InteractionRoot interactionRoot : interactionRootList) {

                    interactionRoot.setTime(key);

                    String hlaClassName = interactionRoot.getHlaClassName();
                    if (!hlaClassNameToInteractionListMap.containsKey(hlaClassName)) {
                        hlaClassNameToInteractionListMap.put(hlaClassName, new ArrayList<>());
                    }
                    hlaClassNameToInteractionListMap.get(hlaClassName).add(interactionRoot);
                }

                _timeToSentInteractionListMap.remove(key);
            }

            ArrayNode dummyArrayNode = InteractionRoot.objectMapper.createArrayNode();
            for(List<InteractionRoot> interactionRootList : hlaClassNameToInteractionListMap.values()) {
                if (
                        interactionRootList.size() > 1 &&
                                interactionRootList.get(0).isInstanceHlaClassDerivedFromHlaClass(
                                        EmbeddedMessaging.get_hla_class_name()
                                )
                ) {
                    double minTime = Double.MAX_VALUE;
                    ArrayNode arrayNode = InteractionRoot.objectMapper.createArrayNode();
                    for (InteractionRoot interactionRoot : interactionRootList) {
                        double time = interactionRoot.getTime();
                        if (time < minTime) {
                            minTime = time;
                        }
                        String messagingJson = (String)interactionRoot.getParameter("messagingJson");

                        JsonNode jsonNode = dummyArrayNode;
                        try {
                            jsonNode = InteractionRoot.objectMapper.readTree(messagingJson);
                        } catch (Exception e) { }

                        if (jsonNode.isArray()) {
                            for(JsonNode item : jsonNode) {
                                arrayNode.add(item);
                            }
                        } else {
                            arrayNode.add(jsonNode);
                        }
                    }

                    String newMessagingJson = arrayNode.toPrettyString();
                    InteractionRoot sendInteractionRoot = interactionRootList.get(0);
                    sendInteractionRoot.setParameter("messagingJson", newMessagingJson);
                    try {
                        sendInteractionRoot.sendInteraction(_synchronizedFederate.getRTI(), minTime);
                    } catch (Exception e) { }

                } else {
                    for(InteractionRoot interactionRoot: interactionRootList) {
                        double time = interactionRoot.getTime();
                        try {
                            interactionRoot.sendInteraction(_synchronizedFederate.getRTI(), time);
                        } catch (Exception e) { }
                    }
                }
            }

            DoubleTime timeRequest;
            // System.out.println("Current time = " + currentTime + ", and ATR's requested time = " + advanceTimeRequest.getRequestedTime());
            if (requestedTime > currentTime) {
                timeRequest = new DoubleTime(requestedTime);
            } else {
                advanceTimeRequest.threadSyncStart(currentTime);
                advanceTimeRequest.threadSyncEnd();
                continue;
            }

            _synchronizedFederate.setTimeAdvanceNotGranted(true);

            boolean tarNotCalled = true;
            while (tarNotCalled) {
                try {
                    // System.out.println( "TimeAdvanceThread: Using " + _timeAdvanceMode + " to request time: " + timeRequest.getTime() );
                    synchronized (_rti) {
                        if (_timeAdvanceMode == TimeAdvanceMode.TimeAdvanceRequest) {
                            _rti.timeAdvanceRequest(timeRequest);
                            // System.out.println( "TimeAdvanceThread: Called timeAdvanceRequest() to go to: " + timeRequest.getTime() );
                        } else if (_timeAdvanceMode == TimeAdvanceMode.NextEventRequest) {
                            _rti.nextEventRequest(timeRequest);
                            // System.out.println( "TimeAdvanceThread: Using nextEventRequest() to go to: " + timeRequest.getTime() );
                        } else if (_timeAdvanceMode == TimeAdvanceMode.TimeAdvanceRequestAvailable) {
                            _rti.timeAdvanceRequestAvailable(timeRequest);
                            // System.out.println( "TimeAdvanceThread: Using timeAdvanceRequestAvailable() to go to: " + timeRequest.getTime() );
                        } else if (_timeAdvanceMode == TimeAdvanceMode.NextEventRequestAvailable) {
                            _rti.nextEventRequestAvailable(timeRequest);
                            // System.out.println( "TimeAdvanceThread: Using nextEventRequestAvailable() to go to: " + timeRequest.getTime() );
                        }
                    }
                    tarNotCalled = false;
                } catch (FederationTimeAlreadyPassed f) {
                    logger.error("Time already passed detected.");
                    _synchronizedFederate.setTimeAdvanceNotGranted(false);
                    tarNotCalled = false;
                } catch (Exception e) {
                }

                while (_synchronizedFederate.getTimeAdvanceNotGranted()) {
                    try {
                        synchronized (_rti) {
                            _rti.tick();
                        }
                    } catch (Exception e) {
                    }
                    CpswtUtils.sleep(10);
                }

                currentTime = _synchronizedFederate.getCurrentTime();
            }

            advanceTimeRequest.threadSyncStart(currentTime);
            advanceTimeRequest.threadSyncEnd();
        }
    }

}
