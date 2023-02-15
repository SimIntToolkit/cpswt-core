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

import hla.rti.AttributeHandleSet;
import hla.rti.HandleIterator;
import hla.rti.RTIambassador;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;

import java.io.StringReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static edu.vanderbilt.vuisis.cpswt.hla.ObjectRootInterface.ClassAndPropertyName;
import static org.mockito.Mockito.*;

public class ObjectRootTests {
    static HashMap<String, Integer> classNameHandleMap = new HashMap<>();
    static HashMap<Integer, String> classHandleNameMap = new HashMap<>();

    static {
        int value = 0;
        classNameHandleMap.put("ObjectRoot", value++);
        classNameHandleMap.put("ObjectRoot.TestBase", value++);
        classNameHandleMap.put("ObjectRoot.TestBase.TestDerived", value);

        for (Map.Entry<String, Integer> entry : classNameHandleMap.entrySet()) {
            classHandleNameMap.put(entry.getValue(), entry.getKey());
        }
    }

    static HashMap<ClassAndPropertyName, Integer> classAndPropertyNameHandleMap = new HashMap<>();
    static HashMap<Integer, ClassAndPropertyName> handleClassAndPropertyNameMap = new HashMap<>();

    static {
        int value = 0;
        classAndPropertyNameHandleMap.put(
                new ClassAndPropertyName("ObjectRoot.TestBase", "field1"), value++
        );
        classAndPropertyNameHandleMap.put(
                new ClassAndPropertyName("ObjectRoot.TestBase", "field2"), value++
        );
        classAndPropertyNameHandleMap.put(
                new ClassAndPropertyName("ObjectRoot.TestBase.TestDerived", "field3"), value++
        );
        classAndPropertyNameHandleMap.put(
                new ClassAndPropertyName("ObjectRoot.TestBase.TestDerived", "field4"), value++
        );
        classAndPropertyNameHandleMap.put(
                new ClassAndPropertyName("ObjectRoot.TestBase.TestDerived", "field5"), value
        );

        for (Map.Entry<ClassAndPropertyName, Integer> entry : classAndPropertyNameHandleMap.entrySet()) {
            handleClassAndPropertyNameMap.put(entry.getValue(), entry.getKey());
        }
    }

    static {
        {
            String federationJson =
                    "{\n" +
                    "    \"objects\": {\n" +
                    "        \"ObjectRoot\": {},\n" +
                    "        \"ObjectRoot.TestBase\": {\n" +
                    "             \"field1\": {\n" +
                    "                 \"Delivery\": \"reliable\",\n" +
                    "                 \"Hidden\": false,\n" +
                    "                 \"Order\": \"timestamp\",\n" +
                    "                 \"ParameterType\": \"String\"\n" +
                    "             },\n" +
                    "             \"field2\": {\n" +
                    "                 \"Delivery\": \"reliable\",\n" +
                    "                 \"Hidden\": false,\n" +
                    "                 \"Order\": \"timestamp\",\n" +
                    "                 \"ParameterType\": \"int\"\n" +
                    "             }\n" +
                    "        },\n" +
                    "        \"ObjectRoot.TestBase.TestDerived\": {\n" +
                    "             \"field3\": {\n" +
                    "                 \"Delivery\": \"reliable\",\n" +
                    "                 \"Hidden\": false,\n" +
                    "                 \"Order\": \"timestamp\",\n" +
                    "                 \"ParameterType\": \"boolean\"\n" +
                    "             },\n" +
                    "             \"field4\": {\n" +
                    "                 \"Delivery\": \"reliable\",\n" +
                    "                 \"Hidden\": false,\n" +
                    "                 \"Order\": \"timestamp\",\n" +
                    "                 \"ParameterType\": \"long\"\n" +
                    "             },\n" +
                    "             \"field5\": {\n" +
                    "                 \"Delivery\": \"reliable\",\n" +
                    "                 \"Hidden\": false,\n" +
                    "                 \"Order\": \"timestamp\",\n" +
                    "                 \"ParameterType\": \"double\"\n" +
                    "             },\n" +
                    "             \"field6\": {\n" +
                    "                 \"Delivery\": \"reliable\",\n" +
                    "                 \"Hidden\": false,\n" +
                    "                 \"Order\": \"timestamp\",\n" +
                    "                 \"ParameterType\": \"byte\"\n" +
                    "             },\n" +
                    "             \"field7\": {\n" +
                    "                 \"Delivery\": \"reliable\",\n" +
                    "                 \"Hidden\": false,\n" +
                    "                 \"Order\": \"timestamp\",\n" +
                    "                 \"ParameterType\": \"char\"\n" +
                    "             }\n" +
                    "        },\n" +
                    "        \"ObjectRoot.OtherClass\": {\n" +
                    "             \"field1\": {\n" +
                    "                 \"Delivery\": \"reliable\",\n" +
                    "                 \"Hidden\": false,\n" +
                    "                 \"Order\": \"timestamp\",\n" +
                    "                 \"ParameterType\": \"boolean\"\n" +
                    "             },\n" +
                    "             \"field2\": {\n" +
                    "                 \"Delivery\": \"reliable\",\n" +
                    "                 \"Hidden\": false,\n" +
                    "                 \"Order\": \"timestamp\",\n" +
                    "                 \"ParameterType\": \"long\"\n" +
                    "             },\n" +
                    "             \"field3\": {\n" +
                    "                 \"Delivery\": \"reliable\",\n" +
                    "                 \"Hidden\": false,\n" +
                    "                 \"Order\": \"timestamp\",\n" +
                    "                 \"ParameterType\": \"double\"\n" +
                    "             }\n" +
                    "        }\n" +
                    "    }\n" +
                    "}\n";

            String dynamicMessageTypes =
                    "{\n" +
                    "    \"objects\": [\n" +
                    "        \"ObjectRoot.TestBase\",\n" +
                    "        \"ObjectRoot.TestBase.TestDerived\"\n" +
                    "    ]\n" +
                    "}\n";

            StringReader federationJsonStringReader = new StringReader(federationJson);
            StringReader dynamicMessagingTypesStringReader = new StringReader(dynamicMessageTypes);
            ObjectRoot.loadDynamicClassFederationData(
                    federationJsonStringReader, dynamicMessagingTypesStringReader
            );
        }
    }

    static Map<String, Set<ClassAndPropertyName>> publishedHlaClassNameAttributeHandleSetMap = new HashMap<>();
    static Map<String, Set<ClassAndPropertyName>> subscribedHlaClassNameAttributeHandleSetMap = new HashMap<>();

    private static final RTIambassador rtiambassador;

    static {
        rtiambassador = mock(RTIambassador.class);
        try {
            when(rtiambassador.getObjectClassHandle(anyString())).thenAnswer(
                    (InvocationOnMock invocationOnMock) ->
                            classNameHandleMap.get((String) invocationOnMock.getArgument(0))
            );
            when(rtiambassador.getAttributeHandle(anyString(), anyInt())).thenAnswer(
                    (InvocationOnMock invocationOnMock) -> {
                        String parameterName = invocationOnMock.getArgument(0);
                        int classHandle = invocationOnMock.getArgument(1);
                        String className = ObjectRoot.get_hla_class_name(classHandle);
                        ClassAndPropertyName key = new ClassAndPropertyName(className, parameterName);
                        return classAndPropertyNameHandleMap.get(key);
                    }
            );
            doAnswer(invocationOnMock -> {
                int classHandle = invocationOnMock.getArgument(0);
                AttributeHandleSet attributeHandleSet = invocationOnMock.getArgument(1);

                String hlaClassName = classHandleNameMap.get(classHandle);

                Set<ClassAndPropertyName> classAndPropertyNameSet = new HashSet<>();
                HandleIterator handleIterator = attributeHandleSet.handles();
                int handle = handleIterator.first();
                while (handle >= 0) {
                    classAndPropertyNameSet.add(handleClassAndPropertyNameMap.get(handle));
                    handle = handleIterator.next();
                }

                publishedHlaClassNameAttributeHandleSetMap.put(hlaClassName, classAndPropertyNameSet);
                return null;

            }).when(rtiambassador).publishObjectClass(anyInt(), any(AttributeHandleSet.class));
            doAnswer(invocationOnMock -> {
                int classHandle = invocationOnMock.getArgument(0);
                AttributeHandleSet attributeHandleSet = invocationOnMock.getArgument(1);

                String hlaClassName = classHandleNameMap.get(classHandle);

                Set<ClassAndPropertyName> classAndPropertyNameSet = new HashSet<>();
                HandleIterator handleIterator = attributeHandleSet.handles();
                int handle = handleIterator.first();
                while (handle >= 0) {
                    classAndPropertyNameSet.add(handleClassAndPropertyNameMap.get(handle));
                    handle = handleIterator.next();
                }

                subscribedHlaClassNameAttributeHandleSetMap.put(hlaClassName, classAndPropertyNameSet);
                return null;
            }).when(rtiambassador).subscribeObjectClassAttributes(anyInt(), any(AttributeHandleSet.class));
        } catch (Exception ignored) {
        }

        ObjectRoot.init(rtiambassador);
    }

    private static RTIambassador get_rti_ambassador() {
        return rtiambassador;
    }

    @Before
    public void clearPubSub() {
        publishedHlaClassNameAttributeHandleSetMap.clear();
        subscribedHlaClassNameAttributeHandleSetMap.clear();
    }

    @Test
    public void dynamicMessagingValueTest() {

        ObjectRoot testBase = new ObjectRoot("ObjectRoot.TestBase");
        testBase.setAttribute("field1", "value1");
        testBase.setAttribute("field2", 5);

        ObjectRoot testDerived = new ObjectRoot("ObjectRoot.TestBase.TestDerived");
        testDerived.setAttribute("field1", "value2");
        testDerived.setAttribute("field2", -6);
        testDerived.setAttribute("field3", true);
        testDerived.setAttribute("field4", 10L);
        testDerived.setAttribute("field5", 3.14);

        // TEST ASSIGNMENT VIA STRING TO Byte AND Char FIELDS
        testDerived.setAttribute("field6", "-17");
        testDerived.setAttribute("field7", 'Q');

        Assert.assertTrue(testBase.isDynamicInstance());
        Assert.assertEquals("value1", testBase.getAttribute("field1"));
        Assert.assertEquals(5, testBase.getAttribute("field2"));

        Assert.assertTrue(testDerived.isDynamicInstance());
        Assert.assertEquals("value2", testDerived.getAttribute("field1"));
        Assert.assertEquals(-6, testDerived.getAttribute("field2"));
        Assert.assertTrue((Boolean) testDerived.getAttribute("field3"));
        Assert.assertEquals(10L, testDerived.getAttribute("field4"));
        Assert.assertEquals(3.14, testDerived.getAttribute("field5"));
        Assert.assertEquals((byte) -17, testDerived.getAttribute("field6"));
        Assert.assertEquals('Q', testDerived.getAttribute("field7"));
    }

}
