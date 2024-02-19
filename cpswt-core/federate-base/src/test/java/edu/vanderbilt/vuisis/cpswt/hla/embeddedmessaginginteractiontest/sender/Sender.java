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

package edu.vanderbilt.vuisis.cpswt.hla.embeddedmessaginginteractiontest.sender;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import edu.vanderbilt.vuisis.cpswt.config.FederateConfig;

import edu.vanderbilt.vuisis.cpswt.hla.base.AdvanceTimeRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import edu.vanderbilt.vuisis.cpswt.hla.InteractionRoot_p.C2WInteractionRoot_p.TestInteraction;
import edu.vanderbilt.vuisis.cpswt.hla.InteractionRoot;


// Define the  type of federate for the federation.

@SuppressWarnings("unused")
public class Sender extends SenderBase {

    public static ObjectMapper objectMapper = InteractionRoot.objectMapper;


    private final static Logger log = LogManager.getLogger();

    private final TestInteraction _testInteraction = create_InteractionRoot_C2WInteractionRoot_TestInteraction();

    public TestInteraction getTestInteraction() {
        return _testInteraction;
    }

    public Sender(FederateConfig params) throws Exception {
        super(params);

        _testInteraction.set_BoolValue1(false);
        _testInteraction.set_BoolValue2(true);
        _testInteraction.set_ByteValue((byte) 42);
        _testInteraction.set_CharValue('X');
        _testInteraction.set_DoubleValue(2.7181);
        _testInteraction.set_FloatValue(3.16f);
        _testInteraction.set_IntValue(1000000);
        _testInteraction.set_LongValue(1000000000000000000L);
        _testInteraction.set_ShortValue((short) 300);

        ArrayNode arrayNode = objectMapper.createArrayNode();
        arrayNode.add("this");
        arrayNode.add("that");
        arrayNode.add("other");
        _testInteraction.set_JSONValue(arrayNode);

        _testInteraction.set_StringValue("Hello");
        _testInteraction.set_actualLogicalGenerationTime(0.0);
        _testInteraction.set_federateFilter("");
    }

    private int state = 0;

    private AdvanceTimeRequest atr = new AdvanceTimeRequest(0);
    private double currentTime = 0;

    public void execute() throws Exception {
        // WE NOW NEED AN AdvanceTimeThread, AS IT IS RESPONSIBLE FOR SENDING SENT-INTERACTIONS
        if (state == 0) {
            putAdvanceTimeRequest(atr);

            startAdvanceTimeThread();

            atr.requestSyncStart();

            // FIRST SENT-INTERACTION SHOULD BE SENT ON FIRST ADVANCE-TIME-REQUEST (TO 1 SEC)
            sendInteraction(_testInteraction, 0.5);

            // SECOND AND THIRD SENT-INTERACTIONS SHOULD BE SENT ON NEXT ADVANCE-TIME-REQUEST (TO 2 SEC)
            // SEE state == 1 BELOW
            sendInteraction(_testInteraction, 1.5);
            sendInteraction(_testInteraction, 1.6);

            currentTime += getStepSize();
            AdvanceTimeRequest newATR = new AdvanceTimeRequest(currentTime);
            putAdvanceTimeRequest(newATR);
            atr.requestSyncEnd();
            atr = newATR;

            // TO AVOID A RACE CONDITION IN THE TEST, WE MAKE THE ADVANCE-TIME-THREAD COMPLETE
            // THE SENDING OF THE INTERACTION(S) BEFORE WE RETURN FROM execute
            atr.requestSyncStart();
            putAdvanceTimeRequest(atr);
            atr.requestSyncEnd();

            ++state;
            return;
        }

        if (state == 1) {

            atr.requestSyncStart();

            currentTime += getStepSize();
            AdvanceTimeRequest newATR = new AdvanceTimeRequest(currentTime);

            // SECOND INTERACTION SHOULD BE SENT HERE
            putAdvanceTimeRequest(newATR);
            atr.requestSyncEnd();
            atr = newATR;

            // TO AVOID A RACE CONDITION IN THE TEST, WE MAKE THE ADVANCE-TIME-THREAD COMPLETE
            // THE SENDING OF THE INTERACTION(S) BEFORE WE RETURN FROM execute
            atr.requestSyncStart();
            putAdvanceTimeRequest(atr);
            atr.requestSyncEnd();

            ++state;
            return;
        }

        // THIS STATE IS MEANT TO KILL THE AdvanceTimeThread
        if (state == 2) {

            atr.requestSyncStart();
            terminateAdvanceTimeThread(atr);

        }
    }
}
