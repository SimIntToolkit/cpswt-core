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

package edu.vanderbilt.vuisis.cpswt.hla.embeddedmessagingobjecttest.sender;

import edu.vanderbilt.vuisis.cpswt.config.FederateConfig;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import edu.vanderbilt.vuisis.cpswt.hla.ObjectRoot_p.TestObject;


// Define the  type of federate for the federation.

@SuppressWarnings("unused")
public class Sender extends SenderBase {

    public static void load() {}

    private final static Logger log = LogManager.getLogger();

    edu.vanderbilt.vuisis.cpswt.hla.ObjectRoot_p.TestObject TestObject_0 =
            new edu.vanderbilt.vuisis.cpswt.hla.ObjectRoot_p.TestObject();

    public Sender(FederateConfig params) throws Exception {
        super(params);

        registerObject(TestObject_0);
    }

    public TestObject getTestObject() {
        return TestObject_0;
    }

    public void execute() throws Exception {

        TestObject_0.set_BooleanValue1(false);
        TestObject_0.set_BooleanValue2(true);
        TestObject_0.set_ByteValue((byte)42);
        TestObject_0.set_CharValue('X');
        TestObject_0.set_DoubleValue(2.7181);
        TestObject_0.set_FloatValue(3.14f);
        TestObject_0.set_IntValue(1000000);
        TestObject_0.set_ShortValue((short)300);
        TestObject_0.set_LongValue(1000000000000000000L);
        TestObject_0.set_StringValue("Hello");
        updateAttributeValues(TestObject_0, 0.0);
    }
}
