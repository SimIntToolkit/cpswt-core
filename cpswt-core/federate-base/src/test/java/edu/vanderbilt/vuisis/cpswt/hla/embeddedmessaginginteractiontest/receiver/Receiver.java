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

package edu.vanderbilt.vuisis.cpswt.hla.embeddedmessaginginteractiontest.receiver;

import edu.vanderbilt.vuisis.cpswt.config.FederateConfig;
import edu.vanderbilt.vuisis.cpswt.hla.InteractionRoot;
import edu.vanderbilt.vuisis.cpswt.hla.InteractionRoot_p.C2WInteractionRoot_p.TestInteraction;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;



// Define the  type of federate for the federation.

@SuppressWarnings("unused")
public class Receiver extends ReceiverBase {
    public static void load() {
    }

    private final static Logger log = LogManager.getLogger();

    public Receiver(FederateConfig params) throws Exception {
        super(params);
    }

    private TestInteraction testInteraction = null;

    public TestInteraction getTestInteraction() {
        return testInteraction;
    }

    private void handleInteractionClass_InteractionRoot_C2WInteractionRoot_TestInteraction(InteractionRoot interactionRoot) {

        testInteraction = (TestInteraction) interactionRoot;
    }

    private void checkReceivedSubscriptions() {

        InteractionRoot interactionRoot;
        while ((interactionRoot = getNextInteractionNoWait()) != null) {
        
            if (interactionRoot.isInstanceHlaClassDerivedFromHlaClass("InteractionRoot.C2WInteractionRoot.TestInteraction")) {

                handleInteractionClass_InteractionRoot_C2WInteractionRoot_TestInteraction(interactionRoot);
                continue;
            }

            log.debug("unhandled interaction: {}", interactionRoot.getJavaClassName());
        }
    }

    public void execute() throws Exception {

            checkReceivedSubscriptions();
    }
}
