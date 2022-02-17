package org.cpswt.hla;

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
                new ClassAndPropertyName("TestBase.TestBase.TestDerived", "field3"), value++
        );
        interactionClassAndPropertyNameHandleMap.put(
                new ClassAndPropertyName("TestBase.TestBase.TestDerived", "field4"), value++
        );
        interactionClassAndPropertyNameHandleMap.put(
                new ClassAndPropertyName("TestBase.TestBase.TestDerived", "field5"), value
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

            StringReader federateJsonStringReader = new StringReader(federationJson);
            StringReader dynamicMessagingTypesStringReader = new StringReader(dynamicMessageTypes);
            InteractionRoot.loadDynamicClassFederationData(
                    federateJsonStringReader, dynamicMessagingTypesStringReader
            );
        }
    }

    private static RTIambassador getRtiambassador() {
        RTIambassador rtiambassador;
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
        } catch (Exception e) { }

        return rtiambassador;
    }

    @Test
    public void valueTest() {
        RTIambassador rtiambassador = getRtiambassador();

        InteractionRoot.init(rtiambassador);

        InteractionRoot testBase = new InteractionRoot("InteractionRoot.TestBase");
        testBase.setParameter("field1", "value1");
        testBase.setParameter("field2", 5);

        InteractionRoot testDerived = new InteractionRoot("InteractionRoot.TestBase.TestDerived");
        testDerived.setParameter("field1", "value2");
        testDerived.setParameter("field2", -6);
        testDerived.setParameter("field3", true);
        testDerived.setParameter("field4", 10L);
        testDerived.setParameter("field5", 3.14);

        Assert.assertEquals(testBase.getParameter("field1"), "value1");
        Assert.assertEquals(testBase.getParameter("field2"), 5);

        Assert.assertEquals(testDerived.getParameter("field1"), "value2");
        Assert.assertEquals(testDerived.getParameter("field2"), -6);
        Assert.assertTrue((Boolean)testDerived.getParameter("field3"));
        Assert.assertEquals(testDerived.getParameter("field4"), 10L);
        Assert.assertEquals(testDerived.getParameter("field5"), 3.14);
    }

    @Test
    public void publishInteractionTest() {
        Set<String> publishedHlaClassNameSet = new HashSet<>();

        RTIambassador rtiambassador = getRtiambassador();
        try {
            doAnswer(invocationOnMock -> {
                int classHandle = invocationOnMock.getArgument(0);
                String hlaClassName = classHandleNameMap.get(classHandle);
                publishedHlaClassNameSet.add(hlaClassName);
                return null;
            }).when(rtiambassador).publishInteractionClass(anyInt());
        } catch(Exception e) { }

        InteractionRoot.init(rtiambassador);

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
        Set<String> subscribedHlaClassNameSet = new HashSet<>();

        RTIambassador rtiambassador = getRtiambassador();
        try {
            doAnswer(invocationOnMock -> {
                int classHandle = invocationOnMock.getArgument(0);
                String hlaClassName = classHandleNameMap.get(classHandle);
                subscribedHlaClassNameSet.add(hlaClassName);
                return null;
            }).when(rtiambassador).subscribeInteractionClass(anyInt());
        } catch(Exception e) { }

        InteractionRoot.init(rtiambassador);

        Set<String> localSubscribedHlaClassNameSet = new HashSet<>();
        localSubscribedHlaClassNameSet.add("InteractionRoot.TestBase.TestDerived");
        for(String hlaClassName: localSubscribedHlaClassNameSet) {
            InteractionRoot.subscribe_interaction(hlaClassName, rtiambassador);
        }
        Assert.assertEquals(subscribedHlaClassNameSet, localSubscribedHlaClassNameSet);
    }
}
