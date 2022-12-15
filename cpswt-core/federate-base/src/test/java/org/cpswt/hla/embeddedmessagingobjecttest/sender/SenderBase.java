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

package org.cpswt.hla.embeddedmessagingobjecttest.sender;

import hla.rti.EventRetractionHandle;
import hla.rti.LogicalTime;
import hla.rti.ReceivedInteraction;

import org.cpswt.hla.SubscribedInteractionFilter;

import org.cpswt.config.FederateConfig;
import org.cpswt.hla.SynchronizedFederateMockRTI;


@SuppressWarnings("unused")
public class SenderBase extends SynchronizedFederateMockRTI {

    private final SubscribedInteractionFilter _subscribedInteractionFilter = new SubscribedInteractionFilter();

    static {
        // FOR GENERIC INTERFACE, ALL MESSAGING CLASSES MUST BE ACTIVELY LOADED -- THE CALL TO THE load
        // STATIC METHOD FOR EACH LEAF CLASS (IN THE INHERITANCE HEIRARCHY) SHOULD DO THIS
        org.cpswt.hla.InteractionRoot_p.C2WInteractionRoot_p.FederateResignInteraction.load();
        org.cpswt.hla.InteractionRoot_p.C2WInteractionRoot_p.FederateJoinInteraction.load();
        org.cpswt.hla.InteractionRoot_p.C2WInteractionRoot_p.SimulationControl_p.SimEnd.load();
        org.cpswt.hla.InteractionRoot_p.C2WInteractionRoot_p.EmbeddedMessaging_p.Receiver.load();
        org.cpswt.hla.InteractionRoot_p.C2WInteractionRoot_p.EmbeddedMessaging_p.OmnetFederate.load();
        org.cpswt.hla.ObjectRoot_p.TestObject.load();
    }

    // constructor
    public SenderBase(FederateConfig config) throws Exception {
        super(config);

        createRTI();
        joinFederation();

        enableTimeConstrained();
        enableTimeRegulation(getLookahead());

        // DIRECT INTERACTION PUBLICATIONS
        org.cpswt.hla.InteractionRoot_p.C2WInteractionRoot_p.EmbeddedMessaging_p.OmnetFederate.publish_interaction(getRTI());
        org.cpswt.hla.InteractionRoot_p.C2WInteractionRoot_p.EmbeddedMessaging_p.Receiver.publish_interaction(getRTI());

        // OBJECT PUBLICATIONS
        org.cpswt.hla.ObjectRoot_p.TestObject.publish_BooleanValue1_attribute();
        org.cpswt.hla.ObjectRoot_p.TestObject.publish_BooleanValue2_attribute();
        org.cpswt.hla.ObjectRoot_p.TestObject.publish_ByteValue_attribute();
        org.cpswt.hla.ObjectRoot_p.TestObject.publish_CharValue_attribute();
        org.cpswt.hla.ObjectRoot_p.TestObject.publish_DoubleValue_attribute();
        org.cpswt.hla.ObjectRoot_p.TestObject.publish_FloatValue_attribute();
        org.cpswt.hla.ObjectRoot_p.TestObject.publish_IntValue_attribute();
        org.cpswt.hla.ObjectRoot_p.TestObject.publish_object(getRTI());

        // SOFT OBJECT PUBLICATIONS
        org.cpswt.hla.ObjectRoot_p.TestObject.add_federate_name_soft_publish("OmnetFederate");

        // DIRECT OBJECT STATUS PUBLICATION
        org.cpswt.hla.ObjectRoot_p.TestObject.add_federate_name_soft_publish_direct("Receiver");
    }

    public org.cpswt.hla.ObjectRoot_p.TestObject create_ObjectRoot_TestObject() {
        org.cpswt.hla.ObjectRoot_p.TestObject object =
            new org.cpswt.hla.ObjectRoot_p.TestObject();
        return object;
    }

    @Override
    public void receiveInteraction(
        int interactionClassHandle, ReceivedInteraction receivedInteraction, byte[] userSuppliedTag
    ) {
        org.cpswt.hla.InteractionRoot interactionRoot = org.cpswt.hla.InteractionRoot.create_interaction(
            interactionClassHandle, receivedInteraction
        );

        if (interactionRoot instanceof org.cpswt.hla.InteractionRoot_p.C2WInteractionRoot) {
            org.cpswt.hla.InteractionRoot_p.C2WInteractionRoot c2wInteractionRoot =
                (org.cpswt.hla.InteractionRoot_p.C2WInteractionRoot)interactionRoot;

            // Filter interaction if src/origin fed requirements (if any) are not met
            if (_subscribedInteractionFilter.filterC2WInteraction(getFederateId(), c2wInteractionRoot)) {
                return;
            }
        }
        super.receiveInteraction(interactionClassHandle, receivedInteraction, userSuppliedTag);
    }

    @Override
    public void receiveInteraction(
        int interactionClassHandle,
        ReceivedInteraction receivedInteraction,
        byte[] userSuppliedTag,
        LogicalTime logicalTime,
        EventRetractionHandle retractionHandle
    ) {

        org.cpswt.hla.InteractionRoot interactionRoot = org.cpswt.hla.InteractionRoot.create_interaction(
            interactionClassHandle, receivedInteraction, logicalTime
        );

        if (interactionRoot instanceof org.cpswt.hla.InteractionRoot_p.C2WInteractionRoot) {
            org.cpswt.hla.InteractionRoot_p.C2WInteractionRoot c2wInteractionRoot =
                (org.cpswt.hla.InteractionRoot_p.C2WInteractionRoot)interactionRoot;

            // Filter interaction if src/origin fed requirements (if any) are not met
            if (_subscribedInteractionFilter.filterC2WInteraction(getFederateId(), c2wInteractionRoot)) {
                return;
            }
        }
        super.receiveInteraction(
            interactionClassHandle, receivedInteraction, userSuppliedTag, logicalTime, retractionHandle
        );
    }
}
