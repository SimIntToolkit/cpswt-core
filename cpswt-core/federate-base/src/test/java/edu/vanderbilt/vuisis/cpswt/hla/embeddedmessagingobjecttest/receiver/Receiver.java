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

package edu.vanderbilt.vuisis.cpswt.hla.embeddedmessagingobjecttest.receiver;

import edu.vanderbilt.vuisis.cpswt.config.FederateConfig;
import edu.vanderbilt.vuisis.cpswt.hla.ObjectRoot;
import static edu.vanderbilt.vuisis.cpswt.hla.ObjectRoot.ObjectReflector;
import edu.vanderbilt.vuisis.cpswt.hla.ObjectRoot_p.TestObject;

import edu.vanderbilt.vuisis.cpswt.hla.base.AdvanceTimeRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;



// Define the  type of federate for the federation.

@SuppressWarnings("unused")
public class Receiver extends ReceiverBase {
    public static void load() {}

    private final static Logger log = LogManager.getLogger();

    public Receiver(FederateConfig params) throws Exception {
        super(params);
    }

    private TestObject testObject = null;
    public TestObject getTestObject() {
        return testObject;
    }

    private void handleObjectClass_ObjectRoot_TestObject(ObjectRoot objectRoot) {
        testObject = (TestObject)objectRoot;
    }

    private void checkReceivedSubscriptions() {

        ObjectReflector reflector;
        while ((reflector = getNextObjectReflectorNoWait()) != null) {
            reflector.reflect();
            ObjectRoot objectRoot = reflector.getObjectRoot();

            if (objectRoot.isInstanceHlaClassDerivedFromHlaClass("ObjectRoot.TestObject")) {
                handleObjectClass_ObjectRoot_TestObject(objectRoot);
                continue;
            }

            log.debug("unhandled object reflection: {}", objectRoot.getJavaClassName());
        }
    }

    private int state = 0;

    private AdvanceTimeRequest atr = new AdvanceTimeRequest(0);
    private double currentTime = 0;


    public void execute() throws Exception {

        if (state == 0) {
            putAdvanceTimeRequest(atr);

            startAdvanceTimeThread();

            atr.requestSyncStart();

            checkReceivedSubscriptions();

            currentTime += getStepSize();
            AdvanceTimeRequest newATR = new AdvanceTimeRequest(currentTime);
            putAdvanceTimeRequest(newATR);
            atr.requestSyncEnd();
            atr = newATR;

            ++state;
            return;
        }

        // WE'LL BE SPLITTING THE RECEPTION OF THE DIRECT ATTRIBUTE UPDATES AND SOFT (INDIRECT) ATTRIBUTE UPDATES
        if (state == 1) {
            atr.requestSyncStart();

            // DIRECT ATTRIBUTE UPDATES
            checkReceivedSubscriptions();

            // NO NEED TO UPDATE THE TIME -- WE'VE ONLY GIVEN THE DIRECT UPDATES AT TIME 0.5
            // AND IT'S ALREADY TIME 1
            putAdvanceTimeRequest(atr);
            atr.requestSyncEnd();

            ++state;
            return;
        }

        if (state == 2) {
            atr.requestSyncStart();

            // SOFT (INDIRECT) ATTRIBUTE UPDATES
            checkReceivedSubscriptions();

            // AGAIN, NO NEED TO UPDATE THE TIME -- NOW WE'VE ONLY GIVEN THE INDIRECT UPDATES (ALSO AT TIME 0.5)
            // AND IT'S ALREADY TIME 1
            putAdvanceTimeRequest(atr);
            atr.requestSyncEnd();

            ++state;
            return;
        }

        if (state == 3) {
            atr.requestSyncStart();
            terminateAdvanceTimeThread(atr);
        }

    }
}
