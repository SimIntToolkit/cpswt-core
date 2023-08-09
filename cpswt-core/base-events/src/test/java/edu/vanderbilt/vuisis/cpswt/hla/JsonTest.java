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

import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.junit.Test;
import org.junit.Assert;

import edu.vanderbilt.vuisis.cpswt.hla.InteractionRoot_p.C2WInteractionRoot_p.SimulationControl_p.SimEnd;
import edu.vanderbilt.vuisis.cpswt.hla.ObjectRoot_p.FederateObject;

import hla.rti.RTIambassador;

public class JsonTest {

    protected static ObjectMapper objectMapper = new ObjectMapper();
    static {
        DefaultPrettyPrinter.Indenter indenter = new DefaultIndenter().withIndent("    ");
        DefaultPrettyPrinter defaultPrettyPrinter = new DefaultPrettyPrinter();
        defaultPrettyPrinter.indentArraysWith(indenter);
        defaultPrettyPrinter.indentObjectsWith(indenter);
        objectMapper.setDefaultPrettyPrinter(defaultPrettyPrinter);
    }

    private static final RTIAmbassadorProxy1 mock = new RTIAmbassadorProxy1();

    private static final RTIambassador rtiambassador = mock.getRTIAmbassador();

    private static RTIambassador get_rti_ambassador() {
        return rtiambassador;
    }

    static {
        SimEnd.load();
        FederateObject.load();
        InteractionRoot.init(get_rti_ambassador());
        ObjectRoot.init(get_rti_ambassador());
    }

    @Test
    public void interactionJsonTest() {

        SimEnd simEnd1 = new SimEnd();
        ArrayNode jsonArray = objectMapper.createArrayNode();
        jsonArray.add("Federate1");
        jsonArray.add("Federate2");
        simEnd1.set_federateSequence(jsonArray.toString());
        simEnd1.set_actualLogicalGenerationTime(5.0);
        simEnd1.set_federateFilter("Filter1");

        String jsonString = simEnd1.toJson();

        SimEnd simEnd2 = (SimEnd)InteractionRoot.fromJson(jsonString);

        Assert.assertEquals(simEnd1.get_federateSequence(), simEnd2.get_federateSequence());
        Assert.assertEquals(simEnd1.get_actualLogicalGenerationTime(), simEnd2.get_actualLogicalGenerationTime(), 0.01);
        Assert.assertEquals(simEnd1.get_federateFilter(), simEnd2.get_federateFilter());
    }

    @Test
    public void objectJsonTest() {

        RTIambassador rtiambassador = get_rti_ambassador();

        FederateObject.publish_object(rtiambassador);

        FederateObject.publish_FederateType_attribute();
        FederateObject.publish_FederateHost_attribute();
        FederateObject.publish_FederateHandle_attribute();

        FederateObject.soft_subscribe_FederateType_attribute();
        FederateObject.soft_subscribe_FederateHost_attribute();
        FederateObject.soft_subscribe_FederateHandle_attribute();

        FederateObject federateObject1 = new FederateObject();

        federateObject1.set_FederateType("FederateType1");
        federateObject1.set_FederateHost("FederateHost1");
        federateObject1.set_FederateHandle(20);

        int federateObjectHandle = 0;
        try {
            federateObject1.registerObject(rtiambassador, "MyObject1");
            federateObjectHandle = federateObject1.getObjectHandle();
        } catch (Exception e) {}

        String jsonString = federateObject1.toJson();

        federateObject1.unregisterObject(rtiambassador);

        FederateObject federateObject2 =
                (FederateObject)ObjectRoot.discover(FederateObject.get_class_handle(), federateObjectHandle);

        ObjectRoot.ObjectReflector objectReflector = ObjectRoot.fromJson(jsonString);
        objectReflector.reflect();

        Assert.assertEquals(federateObject1.get_FederateType(), federateObject2.get_FederateType());
        Assert.assertEquals(federateObject1.get_FederateHost(), federateObject2.get_FederateHost());
        Assert.assertEquals(federateObject1.get_FederateHandle(), federateObject2.get_FederateHandle());
    }
}
