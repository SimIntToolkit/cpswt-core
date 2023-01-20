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

package org.cpswt.hla;

import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;

import java.io.StringReader;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;

import static org.cpswt.hla.InteractionRootInterface.ClassAndPropertyName;

import static org.mockito.Mockito.*;

import hla.rti.RTIambassador;
import org.mockito.invocation.InvocationOnMock;

public class InteractionRootTests {
    static HashMap<String, Integer> classNameHandleMap = new HashMap<>();
    static HashMap<Integer, String> classHandleNameMap = new HashMap<>();
    static {
        int value = 0;
        classNameHandleMap.put("InteractionRoot", value++);
        classNameHandleMap.put("InteractionRoot.TestBase", value++);
        classNameHandleMap.put("InteractionRoot.TestBase.TestDerived", value);

        for(Map.Entry<String, Integer> entry: classNameHandleMap.entrySet()) {
            classHandleNameMap.put(entry.getValue(), entry.getKey());
        }
    }

    static HashMap<ClassAndPropertyName, Integer> interactionClassAndPropertyNameHandleMap = new HashMap<>();
    static {
        int value = 0;
        interactionClassAndPropertyNameHandleMap.put(
                new ClassAndPropertyName("InteractionRoot.TestBase", "field1"), value++
        );
        interactionClassAndPropertyNameHandleMap.put(
                new ClassAndPropertyName("InteractionRoot.TestBase", "field2"), value++
        );
        interactionClassAndPropertyNameHandleMap.put(
                new ClassAndPropertyName("InteractionRoot.TestBase.TestDerived", "field3"), value++
        );
        interactionClassAndPropertyNameHandleMap.put(
                new ClassAndPropertyName("InteractionRoot.TestBase.TestDerived", "field4"), value++
        );
        interactionClassAndPropertyNameHandleMap.put(
                new ClassAndPropertyName("InteractionRoot.TestBase.TestDerived", "field5"), value
        );
    }

    static {
        {
            String federationJson =
                    "{\n" +
                    "    \"interactions\": {\n" +
                    "        \"InteractionRoot\": {},\n" +
                    "        \"InteractionRoot.TestBase\": {\n" +
                    "             \"field1\": {\n" +
                    "                 \"Hidden\": false,\n" +
                    "                 \"ParameterType\": \"String\"\n" +
                    "             },\n" +
                    "             \"field2\": {\n" +
                    "                 \"Hidden\": false,\n" +
                    "                 \"ParameterType\": \"int\"\n" +
                    "             }\n" +
                    "        },\n" +
                    "        \"InteractionRoot.TestBase.TestDerived\": {\n" +
                    "             \"field3\": {\n" +
                    "                 \"Hidden\": false,\n" +
                    "                 \"ParameterType\": \"boolean\"\n" +
                    "             },\n" +
                    "             \"field4\": {\n" +
                    "                 \"Hidden\": false,\n" +
                    "                 \"ParameterType\": \"long\"\n" +
                    "             },\n" +
                    "             \"field5\": {\n" +
                    "                 \"Hidden\": false,\n" +
                    "                 \"ParameterType\": \"double\"\n" +
                    "             },\n" +
                    "             \"field6\": {\n" +
                    "                 \"Hidden\": false,\n" +
                    "                 \"ParameterType\": \"byte\"\n" +
                    "             },\n" +
                    "             \"field7\": {\n" +
                    "                 \"Hidden\": false,\n" +
                    "                 \"ParameterType\": \"char\"\n" +
                    "             }\n" +
                    "        },\n" +
                    "        \"InteractionRoot.OtherClass\": {\n" +
                    "             \"field1\": {\n" +
                    "                 \"Hidden\": false,\n" +
                    "                 \"ParameterType\": \"boolean\"\n" +
                    "             },\n" +
                    "             \"field2\": {\n" +
                    "                 \"Hidden\": false,\n" +
                    "                 \"ParameterType\": \"long\"\n" +
                    "             },\n" +
                    "             \"field3\": {\n" +
                    "                 \"Hidden\": false,\n" +
                    "                 \"ParameterType\": \"double\"\n" +
                    "             }\n" +
                    "        }\n" +
                    "    }\n" +
                    "}\n";

            String dynamicMessageTypes =
                    "{\n" +
                    "    \"interactions\": [\n" +
                    "        \"InteractionRoot.TestBase\",\n" +
                    "        \"InteractionRoot.TestBase.TestDerived\"\n" +
                    "    ]\n" +
                    "}\n";

            StringReader federationJsonStringReader = new StringReader(federationJson);
            StringReader dynamicMessagingTypesStringReader = new StringReader(dynamicMessageTypes);
            InteractionRoot.loadDynamicClassFederationData(
                    federationJsonStringReader, dynamicMessagingTypesStringReader
            );
        }
    }

    static Set<String> publishedHlaClassNameSet = new HashSet<>();
    static Set<String> subscribedHlaClassNameSet = new HashSet<>();

    private static final RTIambassador rtiambassador;
    static {
        rtiambassador = mock(RTIambassador.class);
        try {
            when(rtiambassador.getInteractionClassHandle(anyString())).thenAnswer(
                    (InvocationOnMock invocationOnMock) ->
                            classNameHandleMap.get((String) invocationOnMock.getArgument(0))
            );
            when(rtiambassador.getParameterHandle(anyString(), anyInt())).thenAnswer(
                    (InvocationOnMock invocationOnMock) -> {
                        String parameterName = invocationOnMock.getArgument(0);
                        int classHandle = invocationOnMock.getArgument(1);
                        String className = InteractionRoot.get_hla_class_name(classHandle);
                        ClassAndPropertyName key = new ClassAndPropertyName(className, parameterName);
                        return interactionClassAndPropertyNameHandleMap.get(key);
                    }
            );
            doAnswer(invocationOnMock -> {
                int classHandle = invocationOnMock.getArgument(0);
                String hlaClassName = classHandleNameMap.get(classHandle);
                publishedHlaClassNameSet.add(hlaClassName);
                return null;
            }).when(rtiambassador).publishInteractionClass(anyInt());
            doAnswer(invocationOnMock -> {
                int classHandle = invocationOnMock.getArgument(0);
                String hlaClassName = classHandleNameMap.get(classHandle);
                subscribedHlaClassNameSet.add(hlaClassName);
                return null;
            }).when(rtiambassador).subscribeInteractionClass(anyInt());
        } catch (Exception e) { }

        InteractionRoot.init(rtiambassador);
    }

    private static RTIambassador get_rti_ambassador() {
        return rtiambassador;
    }

    @Before
    public void clearPubSub() {
        publishedHlaClassNameSet.clear();
        subscribedHlaClassNameSet.clear();
    }

    @Test
    public void dynamicMessagingValueTest() {

        InteractionRoot testBase = new InteractionRoot("InteractionRoot.TestBase");
        testBase.setParameter("field1", "value1");
        testBase.setParameter("field2", 5);

        InteractionRoot testDerived = new InteractionRoot("InteractionRoot.TestBase.TestDerived");
        testDerived.setParameter("field1", "value2");
        testDerived.setParameter("field2", -6);
        testDerived.setParameter("field3", true);
        testDerived.setParameter("field4", 10L);
        testDerived.setParameter("field5", 3.14);

        // TEST ASSIGNMENT VIA STRING TO Byte AND Char FIELDS
        testDerived.setParameter("field6", "-17");
        testDerived.setParameter("field7", 'Q');

        Assert.assertTrue(testBase.isDynamicInstance());
        Assert.assertEquals("value1", testBase.getParameter("field1"));
        Assert.assertEquals(5, testBase.getParameter("field2"));

        Assert.assertTrue(testDerived.isDynamicInstance());
        Assert.assertEquals("value2", testDerived.getParameter("field1"));
        Assert.assertEquals(-6, testDerived.getParameter("field2"));
        Assert.assertTrue((Boolean)testDerived.getParameter("field3"));
        Assert.assertEquals(10L, testDerived.getParameter("field4"));
        Assert.assertEquals(3.14, testDerived.getParameter("field5"));
        Assert.assertEquals((byte)-17, testDerived.getParameter("field6"));
        Assert.assertEquals('Q', testDerived.getParameter("field7"));
    }

    @Test
    public void publishInteractionTest() {

        RTIambassador rtiambassador = get_rti_ambassador();

        Set<String> localPublishedHlaClassNameSet = new HashSet<>();
        localPublishedHlaClassNameSet.add("InteractionRoot.TestBase");
        localPublishedHlaClassNameSet.add("InteractionRoot.TestBase.TestDerived");
        for(String hlaClassName: localPublishedHlaClassNameSet) {
            InteractionRoot.publish_interaction(hlaClassName, rtiambassador);
        }
        Assert.assertEquals(publishedHlaClassNameSet, localPublishedHlaClassNameSet);

    }

    @Test
    public void subscribeInteractionTest() {

        RTIambassador rtiambassador = get_rti_ambassador();

        Set<String> localSubscribedHlaClassNameSet = new HashSet<>();
        localSubscribedHlaClassNameSet.add("InteractionRoot.TestBase.TestDerived");
        for(String hlaClassName: localSubscribedHlaClassNameSet) {
            InteractionRoot.subscribe_interaction(hlaClassName, rtiambassador);
        }
        Assert.assertEquals(subscribedHlaClassNameSet, localSubscribedHlaClassNameSet);
    }
}
