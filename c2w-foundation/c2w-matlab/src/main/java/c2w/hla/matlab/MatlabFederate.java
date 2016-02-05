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

package c2w.hla.matlab;

import hla.rti.ConcurrentAccessAttempted;
import hla.rti.EventRetractionHandle;
import hla.rti.LogicalTime;
import hla.rti.RTIinternalError;
import hla.rti.ReceivedInteraction;
import hla.rti.ResignAction;

import java.text.DecimalFormat;

import org.portico.impl.hla13.types.DoubleTime;

import c2w.hla.InteractionRoot;
import c2w.hla.SynchronizedFederate;
import c2w.hla.LOG_TYPE;
import c2w.util.FedUtil;

/**
 * This is a generic Federate for connecting a Simulink model to HLA-RTI. Allows
 * to send interactions to RTI and receive interactions from it as per the
 * Federation Object Model (FOM).
 * 
 * @author Himanshu Neema
 */
public abstract class MatlabFederate extends SynchronizedFederate {

    protected boolean running;

    protected double logicalTimeGrantedByRTI = 0.0;

    protected boolean newTimeAdvanceGranted = true;

    protected boolean federateStarted = false;

    private String myFedName;

    private DecimalFormat formatter = new DecimalFormat("0.000");

    public MatlabFederate(String federation_id, String federate_id)
            throws Exception {
    	
    	createRTI();

    	joinFederation( federation_id, federate_id );

        this.myFedName = federate_id;

        running = true;
    }

    @Override
    public void timeAdvanceGrant(LogicalTime arg0) {
        DoubleTime d = new DoubleTime();
        d.setTo(arg0);
        this.logicalTimeGrantedByRTI = d.getTime();
        this.newTimeAdvanceGranted = true;
        super.timeAdvanceGrant(arg0);
    }

    public double getInitialLogicalTime(double lookahead) throws Exception {
        FedUtil.sendLogInteraction(
         getRTI(),
         myFedName,
         "Setting initial logical time to 0.0 and lookahead to " + lookahead,
         -1,
         LOG_TYPE.LOG_TYPE_VERY_LOW
        );
        
        // PER THE HLA BOOK, ENABLE TIME-CONSTRAINED FIRST, THEN TIME-REGULATING

        // Enabling TIME CONSTRAINED
        FedUtil.sendLogInteraction( getRTI(), myFedName, "Enabling time-constrained", -1, LOG_TYPE.LOG_TYPE_VERY_LOW );
        enableTimeConstrained();
        FedUtil.sendLogInteraction( getRTI(), myFedName, "Time constrained granted", -1, LOG_TYPE.LOG_TYPE_VERY_LOW );

        // Enabling TIME REGULATION
        FedUtil.sendLogInteraction( getRTI(), myFedName, "Enabling time-regulation", -1, LOG_TYPE.LOG_TYPE_VERY_LOW );
        enableTimeRegulation( lookahead );
        FedUtil.sendLogInteraction( getRTI(), myFedName, "Time regulation granted", -1, LOG_TYPE.LOG_TYPE_VERY_LOW );


        enableAsynchronousDelivery();

    	// ALL PUBLISHING/SUBSCRIBING SHOULD BE COMPLETE, SO "ReadyToPopulate"
        readyToPopulate();
        
    	// THERE DOESN'T APPEAR TO BE ANY INITIAL PUBLICATIONS OF DATA, SO "ReadyToRun"
        readyToRun();

        // Now querying the Federate's real LOGICAL TIME and return it
        DoubleTime realLogicalTime = new DoubleTime( 0 );
        realLogicalTime.setTo( getRTI().queryFederateTime() );
        return realLogicalTime.getTime();
    }

    public synchronized void startFederateIfNotAlreadyStarted() {
        if (federateStarted) {
            return;
        }
        FedUtil.sendLogInteraction( getRTI(), myFedName, "Starting Simulink federate", -1, LOG_TYPE.LOG_TYPE_LOW );
        federateStarted = true;

        Thread t = new Thread() {
            public void run() {
                try {
                    while (running) {
                        Thread.sleep(50);
                        tickSynchronized();
                    }
                    
                    // "ReadyToResign"
                    readyToResign();

                    synchronized ( getRTI() ) {
                        getRTI().resignFederationExecution(ResignAction.DELETE_OBJECTS);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        t.start();
    }

    public void stopFederate() {
        waitForTimeAdvanceGrant();
        running = false;
    }

    public void requestRTIForAdvancingTimeTo(double newLogicalTime) {
        this.newTimeAdvanceGranted = false;
        if (!running) {
            return;
        }
        try {
            FedUtil.sendLogInteraction(
             getRTI(),
             myFedName,
             "Requesting RTI to advance logical time to " + formatter.format(newLogicalTime),
             -1,
             LOG_TYPE.LOG_TYPE_VERY_LOW
            );
            synchronized ( getRTI() ) {
                getRTI().timeAdvanceRequest(new DoubleTime(newLogicalTime));
                // Himanshu: DON'T USE nextEventRequest, blocks multiple federates due to time drifts
				// getRTI().nextEventRequest(new DoubleTime(newLogicalTime));
            }
            FedUtil.sendLogInteraction( getRTI(), myFedName, "Waiting for time advance grant", -1, LOG_TYPE.LOG_TYPE_VERY_LOW );
            waitForTimeAdvanceGrant();
        } catch (Exception e) {
            System.out.println("Error while requesting time advance");
            e.printStackTrace();
        }
    }

    /**
     * Returns -1, if there is an exception while querying federate logical
     * time, else returns the federate logical time as returned by RTI.
     */
    public double getCurrentLogicalTimeFromRTI() {
        DoubleTime newLogicalTime = new DoubleTime();
        double retval = -1.0;
        if (!running) {
            return -1;
        }
        try {
            synchronized ( getRTI() ) {
                newLogicalTime.setTo( getRTI().queryFederateTime() );
                retval = newLogicalTime.getTime();
            }
        } catch (Exception e) {
            System.out.println("Error while querying federate logical time");
            e.printStackTrace();
        }
        return retval;
    }

    public double getLogicalTimeGrantedByRTI() {
        return logicalTimeGrantedByRTI;
    }

    public void sendInteractionToRTI( InteractionRoot interactionRoot ) {
        if ( !running ) {
            System.out.println("Not running.");
            return;
        }

        try {
            DoubleTime timestamp = new DoubleTime(0);
            double curFederateTime = 0;
            timestamp.setTo( getRTI().queryFederateTime() );
            curFederateTime = timestamp.getTime();
            timestamp.setTime(timestamp.getTime() + getLookahead() + 0.01);
            interactionRoot.sendInteraction( getRTI(),  timestamp.getTime());
            FedUtil.sendLogInteraction(
             getRTI(), myFedName, "Successfully sent the interaction from Matlab to RTI", curFederateTime, LOG_TYPE.LOG_TYPE_VERY_LOW
            );
        } catch (Exception e) {
            System.out.println("Failed to send interaction");
            e.printStackTrace();
        }
    }

    protected void waitForTimeAdvanceGrant() {
        while (!newTimeAdvanceGranted) {
            try {
                Thread.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    protected synchronized void tickSynchronized() throws RTIinternalError,
            ConcurrentAccessAttempted {
        if ( running ) {
            synchronized ( getRTI() ) { getRTI().tick(); }
        }
    }

    protected void receiveSubscribedInteractions() {
        while(  ( getNextInteractionNoWait() )  !=  null  );
    }
    
    @Override
    public void receiveInteraction(
     int interactionClass, ReceivedInteraction theInteraction, byte[] userSuppliedTag
    ) {
        super.receiveInteraction( interactionClass, theInteraction, userSuppliedTag );
        receiveSubscribedInteractions();
    }

    @Override
    public void receiveInteraction(
     int interactionClass,
     ReceivedInteraction theInteraction,
     byte[] userSuppliedTag,
     LogicalTime theTime,
     EventRetractionHandle retractionHandle
    ) {
        super.receiveInteraction( interactionClass, theInteraction, userSuppliedTag, theTime, retractionHandle );
        receiveSubscribedInteractions();
    }

}
