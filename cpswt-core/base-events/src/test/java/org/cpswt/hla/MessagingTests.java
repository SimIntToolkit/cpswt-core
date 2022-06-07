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

import hla.rti.*;
import org.json.JSONArray;
import org.junit.Test;
import org.junit.Assert;
import static org.mockito.Mockito.*;

import org.cpswt.hla.InteractionRoot_p.C2WInteractionRoot;
import org.cpswt.hla.InteractionRoot_p.C2WInteractionRoot_p.SimLog;
import org.cpswt.hla.InteractionRoot_p.C2WInteractionRoot_p.SimLog_p.HighPrio;
import org.cpswt.hla.InteractionRoot_p.C2WInteractionRoot_p.SimulationControl;
import org.cpswt.hla.InteractionRoot_p.C2WInteractionRoot_p.SimulationControl_p.SimEnd;

import static org.cpswt.hla.InteractionRootInterface.ClassAndPropertyName;

import org.cpswt.hla.ObjectRoot_p.FederateObject;

import org.mockito.invocation.InvocationOnMock;
import org.portico.impl.hla13.types.DoubleTime;

import java.util.*;


public class MessagingTests {

    static class ReflectedAttributeImpl implements ReflectedAttributes {

        private final SuppliedAttributes _suppliedAttributes;

        public ReflectedAttributeImpl(SuppliedAttributes suppliedAttributes) {
            _suppliedAttributes = suppliedAttributes;
        }

        public int getAttributeHandle(int offset) throws ArrayIndexOutOfBounds {
            return _suppliedAttributes.getHandle(offset);
        }

        public int getOrderType(int offset) throws ArrayIndexOutOfBounds {
            return 0; // DUMMY VALUE -- NOT USED
        }

        public Region getRegion(int offset) throws ArrayIndexOutOfBounds {
            return null; // DUMMY VALUE -- NOT USED
        }

        public int getTransportType(int offset) throws ArrayIndexOutOfBounds {
            return 0; // DUMMY VALUE -=- NOT USED
        }

        public byte[] getValue(int offset) throws ArrayIndexOutOfBounds {
            return  _suppliedAttributes.getValue(offset);
        }

        public int getValueLength(int offset) throws ArrayIndexOutOfBounds {
            return _suppliedAttributes.getValueLength(offset);
        }

        public byte[] getValueReference(int offset) throws ArrayIndexOutOfBounds {
            return _suppliedAttributes.getValueReference(offset);
        }

        public int size() {
            return _suppliedAttributes.size();
        }
    }

    static class EventRetractionHandleImpl implements EventRetractionHandle { }

    static HashMap<String, Integer> interactionClassNameHandleMap = new HashMap<>();
    static {
        int value = 0;
        interactionClassNameHandleMap.put("InteractionRoot", value++);
        interactionClassNameHandleMap.put("InteractionRoot.C2WInteractionRoot", value++);
        interactionClassNameHandleMap.put("InteractionRoot.C2WInteractionRoot.SimLog", value++);
        interactionClassNameHandleMap.put("InteractionRoot.C2WInteractionRoot.SimLog.HighPrio", value++);
        interactionClassNameHandleMap.put("InteractionRoot.C2WInteractionRoot.SimulationControl", value++);
        interactionClassNameHandleMap.put("InteractionRoot.C2WInteractionRoot.SimulationControl.SimEnd", value);
    }

    static HashMap<String, Integer> objectClassNameHandleMap = new HashMap<>();
    static {
        int value = 0;
        objectClassNameHandleMap.put("ObjectRoot", value++);
        objectClassNameHandleMap.put("ObjectRoot.FederateObject", value);
    }

    static HashMap<ClassAndPropertyName, Integer> interactionClassAndPropertyNameHandleMap = new HashMap<>();
    static HashMap<ObjectRootInterface.ClassAndPropertyName, Integer> objectClassAndPropertyNameHandleMap =
            new HashMap<>();
    static RTIambassador rtiambassador;

    static int currentClassHandle = 0;
    static void setCurrentClassHandle(int classHandle) {
        currentClassHandle = classHandle;
    }
    static int getCurrentClassHandle() {
        return currentClassHandle;
    }

    static int uniqueObjectHandle = 0;
    static int currentObjectHandle = 0;
    static void setCurrentObjectHandle(int objectHandle) {
        currentObjectHandle = objectHandle;
    }
    static void setCurrentObjectHandle() {
        currentObjectHandle = uniqueObjectHandle++;
    }
    static int getCurrentObjectHandle() {
        return currentObjectHandle;
    }

    static SuppliedAttributes currentSuppliedAttributes = null;
    static void setCurrentSuppliedAttributes(SuppliedAttributes suppliedAttributes) {
        currentSuppliedAttributes = suppliedAttributes;
    }
    static SuppliedAttributes getCurrentSuppliedAttributes() {
        return currentSuppliedAttributes;
    }

    static ReflectedAttributes getCurrentReflectedAttributes() {
        return new ReflectedAttributeImpl(getCurrentSuppliedAttributes());
    }

    static LogicalTime currentLogicalTime = null;
    static void setCurrentLogicalTime(LogicalTime logicalTime) {
        currentLogicalTime = logicalTime;
    }
    static LogicalTime getCurrentLogicalTime() {
        return currentLogicalTime;
    }
    static DoubleTime getCurrentDoubleTime() {
        LogicalTime logicalTime = getCurrentLogicalTime();
        return logicalTime instanceof DoubleTime ? (DoubleTime)logicalTime : null;
    }

    static {
        int value = 0;
        interactionClassAndPropertyNameHandleMap.put(
                new ClassAndPropertyName("InteractionRoot.C2WInteractionRoot", "actualLogicalGenerationTime"),
                value++
        );
        interactionClassAndPropertyNameHandleMap.put(
                new ClassAndPropertyName("InteractionRoot.C2WInteractionRoot", "federateFilter"),
                value++
        );
        interactionClassAndPropertyNameHandleMap.put(
                new ClassAndPropertyName("InteractionRoot.C2WInteractionRoot", "federateSequence"),
                value++
        );
        interactionClassAndPropertyNameHandleMap.put(
                new ClassAndPropertyName("InteractionRoot.C2WInteractionRoot.SimLog", "Comment"),
                value++
        );
        interactionClassAndPropertyNameHandleMap.put(
                new ClassAndPropertyName("InteractionRoot.C2WInteractionRoot.SimLog", "FedName"),
                value++
        );
        interactionClassAndPropertyNameHandleMap.put(
                new ClassAndPropertyName("InteractionRoot.C2WInteractionRoot.SimLog", "Time"),
                value++
        );
        interactionClassAndPropertyNameHandleMap.put(
                new ClassAndPropertyName("InteractionRoot.C2WInteractionRoot.FederateJoinInteraction", "FederateId"),
                value++
        );
        interactionClassAndPropertyNameHandleMap.put(
                new ClassAndPropertyName("InteractionRoot.C2WInteractionRoot.FederateJoinInteraction", "FederateType"),
                value++
        );
        interactionClassAndPropertyNameHandleMap.put(
                new ClassAndPropertyName("InteractionRoot.C2WInteractionRoot.FederateJoinInteraction", "IsLateJoiner"),
                value++
        );
        interactionClassAndPropertyNameHandleMap.put(
                new ClassAndPropertyName("InteractionRoot.C2WInteractionRoot.FederateResignInteraction", "FederateId"),
                value++
        );
        interactionClassAndPropertyNameHandleMap.put(
                new ClassAndPropertyName("InteractionRoot.C2WInteractionRoot.FederateResignInteraction", "FederateType"),
                value++
        );
        interactionClassAndPropertyNameHandleMap.put(
                new ClassAndPropertyName("InteractionRoot.C2WInteractionRoot.FederateResignInteraction", "IsLateJoiner"),
                value++
        );
        objectClassAndPropertyNameHandleMap.put(
                new ObjectRootInterface.ClassAndPropertyName("ObjectRoot.FederateObject", "FederateHandle"), value++
        );
        objectClassAndPropertyNameHandleMap.put(
                new ObjectRootInterface.ClassAndPropertyName("ObjectRoot.FederateObject", "FederateHost"), value++
        );
        objectClassAndPropertyNameHandleMap.put(
                new ObjectRootInterface.ClassAndPropertyName("ObjectRoot.FederateObject", "FederateType"), value
        );

        rtiambassador = mock(RTIambassador.class);
        try {
            when(rtiambassador.getInteractionClassHandle(anyString())).thenAnswer(
                    (InvocationOnMock invocationOnMock) ->
                            interactionClassNameHandleMap.get((String)invocationOnMock.getArgument(0))
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
            when(rtiambassador.getAttributeHandle(anyString(), anyInt())).thenAnswer(
                    (InvocationOnMock invocationOnMock) -> {
                        String attributeName = invocationOnMock.getArgument(0);
                        int classHandle = invocationOnMock.getArgument(1);
                        String className = ObjectRoot.get_hla_class_name(classHandle);
                        ObjectRootInterface.ClassAndPropertyName key =
                                new ObjectRootInterface.ClassAndPropertyName(className, attributeName);
                        return objectClassAndPropertyNameHandleMap.get(key);
                    }
            );
            when(rtiambassador.registerObjectInstance(anyInt())).thenAnswer(
                    (InvocationOnMock invocationOnMock) -> {
                        int classHandle = invocationOnMock.getArgument(0);
                        setCurrentClassHandle(classHandle);
                        setCurrentObjectHandle();
                        return getCurrentClassHandle();
                    }
            );
            when(rtiambassador.updateAttributeValues(
                    anyInt(), any(SuppliedAttributes.class), nullable(byte[].class), any(LogicalTime.class)
            )).thenAnswer(
                    (InvocationOnMock invocationMock) -> {
                        int objectHandle = invocationMock.getArgument(0);
                        SuppliedAttributes suppliedAttributes = invocationMock.getArgument(1);
                        LogicalTime logicalTime = invocationMock.getArgument(3);

                        setCurrentObjectHandle(objectHandle);
                        setCurrentSuppliedAttributes(suppliedAttributes);
                        setCurrentLogicalTime(logicalTime);

                        return new EventRetractionHandleImpl(); // DUMMY EventRestractionHandle
                    }
            );
        } catch(Exception e) {}

        HighPrio.load();
        SimEnd.load();
        FederateObject.load();
        InteractionRoot.init(rtiambassador);
        ObjectRoot.init(rtiambassador);
    }

    @Test
    public void messagingNamesTest() {

        Set<String> expectedInteractionClassNameSet = new HashSet<>();
        expectedInteractionClassNameSet.add("InteractionRoot");
        expectedInteractionClassNameSet.add("InteractionRoot.C2WInteractionRoot");
        expectedInteractionClassNameSet.add("InteractionRoot.C2WInteractionRoot.SimLog");
        expectedInteractionClassNameSet.add("InteractionRoot.C2WInteractionRoot.SimLog.HighPrio");
        expectedInteractionClassNameSet.add("InteractionRoot.C2WInteractionRoot.SimulationControl");
        expectedInteractionClassNameSet.add("InteractionRoot.C2WInteractionRoot.SimulationControl.SimEnd");

        Set<String> actualInteractionClassNameSet = InteractionRoot.get_interaction_hla_class_name_set();
        Assert.assertEquals(expectedInteractionClassNameSet, actualInteractionClassNameSet);


        new FederateObject();

        Set<String> expectedObjectClassNameSet = new HashSet<>();
        expectedObjectClassNameSet.add("ObjectRoot");
        expectedObjectClassNameSet.add("ObjectRoot.FederateObject");

        Set<String> actualObjectClassNameSet = ObjectRoot.get_object_hla_class_name_set();
        Assert.assertEquals(expectedObjectClassNameSet, actualObjectClassNameSet);
    }

    @Test
    public void classHandleTest() {

        Assert.assertEquals((int) interactionClassNameHandleMap.get("InteractionRoot"), InteractionRoot.get_class_handle());
        Assert.assertEquals(
                (int) interactionClassNameHandleMap.get("InteractionRoot.C2WInteractionRoot"), C2WInteractionRoot.get_class_handle()
        );
        Assert.assertEquals(
                (int) interactionClassNameHandleMap.get("InteractionRoot.C2WInteractionRoot.SimLog"), SimLog.get_class_handle()
        );
        Assert.assertEquals(
                (int) interactionClassNameHandleMap.get("InteractionRoot.C2WInteractionRoot.SimLog.HighPrio"),
                HighPrio.get_class_handle()
        );
        Assert.assertEquals(
                (int) interactionClassNameHandleMap.get("InteractionRoot.C2WInteractionRoot.SimulationControl"),
                SimulationControl.get_class_handle()
        );
        Assert.assertEquals(
                (int) interactionClassNameHandleMap.get("InteractionRoot.C2WInteractionRoot.SimulationControl.SimEnd"),
                SimEnd.get_class_handle()
        );

        Assert.assertEquals(
                (int) interactionClassNameHandleMap.get("InteractionRoot"),
                InteractionRoot.get_class_handle("InteractionRoot"));
        Assert.assertEquals(
                (int) interactionClassNameHandleMap.get("InteractionRoot.C2WInteractionRoot"),
                C2WInteractionRoot.get_class_handle("InteractionRoot.C2WInteractionRoot")
        );
        Assert.assertEquals(
                (int) interactionClassNameHandleMap.get("InteractionRoot.C2WInteractionRoot.SimLog"),
                SimLog.get_class_handle("InteractionRoot.C2WInteractionRoot.SimLog")
        );
        Assert.assertEquals(
                (int) interactionClassNameHandleMap.get("InteractionRoot.C2WInteractionRoot.SimLog.HighPrio"),
                HighPrio.get_class_handle("InteractionRoot.C2WInteractionRoot.SimLog.HighPrio")
        );
        Assert.assertEquals(
                (int) interactionClassNameHandleMap.get("InteractionRoot.C2WInteractionRoot.SimulationControl"),
                SimulationControl.get_class_handle("InteractionRoot.C2WInteractionRoot.SimulationControl")
        );
        Assert.assertEquals(
                (int) interactionClassNameHandleMap.get("InteractionRoot.C2WInteractionRoot.SimulationControl.SimEnd"),
                SimEnd.get_class_handle("InteractionRoot.C2WInteractionRoot.SimulationControl.SimEnd")
        );
    }

    @Test
    public void parameterNamesTest() {

        // TEST InteractionRoot get_parameter_names()
        List<ClassAndPropertyName> expectedInteractionRootParameterList = new ArrayList<>();

        Assert.assertEquals(expectedInteractionRootParameterList, InteractionRoot.get_parameter_names());
        Assert.assertEquals(
                expectedInteractionRootParameterList,
                InteractionRoot.get_parameter_names("InteractionRoot")
        );

        // TEST InteractionRoot get_all_parameter_names()
        List<ClassAndPropertyName> expectedInteractionRootAllParameterList = new ArrayList<>(
                expectedInteractionRootParameterList
        );

        Assert.assertEquals(expectedInteractionRootAllParameterList, InteractionRoot.get_all_parameter_names());
        Assert.assertEquals(
                expectedInteractionRootAllParameterList,
                InteractionRoot.get_all_parameter_names("InteractionRoot")
        );


        // TEST InteractionRoot.C2WInteractionRoot get_parameter_names()
        List<ClassAndPropertyName> expectedC2WInteractionRootParameterList = new ArrayList<>();
        expectedC2WInteractionRootParameterList.add(new InteractionRoot.ClassAndPropertyName(
                C2WInteractionRoot.get_hla_class_name(), "actualLogicalGenerationTime"
        ));
        expectedC2WInteractionRootParameterList.add(new InteractionRoot.ClassAndPropertyName(
                C2WInteractionRoot.get_hla_class_name(), "federateFilter"
        ));
        expectedC2WInteractionRootParameterList.add(new InteractionRoot.ClassAndPropertyName(
                C2WInteractionRoot.get_hla_class_name(), "federateSequence"
        ));
        Collections.sort(expectedC2WInteractionRootParameterList);

        Assert.assertEquals(expectedC2WInteractionRootParameterList, C2WInteractionRoot.get_parameter_names());
        Assert.assertEquals(
                expectedC2WInteractionRootParameterList,
                InteractionRoot.get_parameter_names("InteractionRoot.C2WInteractionRoot")
        );


        // TEST InteractionRoot.C2WInteractionRoot get_all_parameter_names()
        List<ClassAndPropertyName> expectedC2WInteractionRootAllParameterList =
                new ArrayList<>(expectedInteractionRootAllParameterList);
        expectedC2WInteractionRootAllParameterList.addAll(expectedC2WInteractionRootParameterList);
        Collections.sort(expectedC2WInteractionRootAllParameterList);

        Assert.assertEquals(expectedC2WInteractionRootAllParameterList, C2WInteractionRoot.get_all_parameter_names());
        Assert.assertEquals(
                expectedC2WInteractionRootAllParameterList,
                InteractionRoot.get_all_parameter_names("InteractionRoot.C2WInteractionRoot")
        );


        // TEST InteractionRoot.C2WInteractionRoot.SimLog get_parameter_names()
        List<ClassAndPropertyName> expectedSimLogParameterList = new ArrayList<>();
        expectedSimLogParameterList.add(new InteractionRoot.ClassAndPropertyName(
                SimLog.get_hla_class_name(), "Comment"
        ));
        expectedSimLogParameterList.add(new InteractionRoot.ClassAndPropertyName(
                SimLog.get_hla_class_name(), "FedName"
        ));
        expectedSimLogParameterList.add(new InteractionRoot.ClassAndPropertyName(
                SimLog.get_hla_class_name(), "Time"
        ));
        Collections.sort(expectedSimLogParameterList);

        Assert.assertEquals(expectedSimLogParameterList, SimLog.get_parameter_names());
        Assert.assertEquals(
                expectedSimLogParameterList,
                InteractionRoot.get_parameter_names("InteractionRoot.C2WInteractionRoot.SimLog")
        );


        // TEST InteractionRoot.C2WInteractionRoot.SimLog get_all_parameter_names()
        List<ClassAndPropertyName> expectedSimLogAllParameterList =
                new ArrayList<>(expectedC2WInteractionRootAllParameterList);
        expectedSimLogAllParameterList.addAll(expectedSimLogParameterList);
        Collections.sort(expectedSimLogAllParameterList);

        Assert.assertEquals(expectedSimLogAllParameterList, SimLog.get_all_parameter_names());
        Assert.assertEquals(
                expectedSimLogAllParameterList,
                InteractionRoot.get_all_parameter_names("InteractionRoot.C2WInteractionRoot.SimLog")
        );


        // TEST InteractionRoot.C2WInteractionRoot.SimLog get_parameter_names()
        List<ClassAndPropertyName> expectedHighPrioParameterList = new ArrayList<>();

        Assert.assertEquals(expectedHighPrioParameterList, HighPrio.get_parameter_names());
        Assert.assertEquals(
                expectedHighPrioParameterList,
                InteractionRoot.get_parameter_names("InteractionRoot.C2WInteractionRoot.SimLog.HighPrio")
        );


        // TEST InteractionRoot.C2WInteractionRoot.SimLog get_all_parameter_names()
        List<ClassAndPropertyName> expectedHighPrioAllParameterList =
                new ArrayList<>(expectedSimLogAllParameterList);
        expectedHighPrioAllParameterList.addAll(expectedHighPrioParameterList);
        Collections.sort(expectedHighPrioAllParameterList);

        Assert.assertEquals(expectedHighPrioAllParameterList, HighPrio.get_all_parameter_names());
        Assert.assertEquals(
                expectedHighPrioAllParameterList,
                InteractionRoot.get_all_parameter_names(
                        "InteractionRoot.C2WInteractionRoot.SimLog.HighPrio"
                )
        );
    }

    @Test
    public void propertyHandleTest() {

        int expectedValue = interactionClassAndPropertyNameHandleMap.get(
                new ClassAndPropertyName("InteractionRoot.C2WInteractionRoot", "federateSequence"));

        Assert.assertEquals(expectedValue, HighPrio.get_parameter_handle("federateSequence"));
        Assert.assertEquals(expectedValue, SimLog.get_parameter_handle("federateSequence"));
        Assert.assertEquals(expectedValue, C2WInteractionRoot.get_parameter_handle("federateSequence"));

        expectedValue = interactionClassAndPropertyNameHandleMap.get(
                new ClassAndPropertyName("InteractionRoot.C2WInteractionRoot.SimLog", "FedName"));
        Assert.assertEquals(expectedValue, HighPrio.get_parameter_handle("FedName"));
        Assert.assertEquals(expectedValue, SimLog.get_parameter_handle("FedName"));

        expectedValue = objectClassAndPropertyNameHandleMap.get(
                new ObjectRootInterface.ClassAndPropertyName("ObjectRoot.FederateObject", "FederateHost"));
        Assert.assertEquals(expectedValue, FederateObject.get_attribute_handle("FederateHost"));
    }

    @Test
    public void dynamicMessagingTest() {
        InteractionRoot dynamicSimLogInteraction = new InteractionRoot(SimLog.get_hla_class_name());

        Assert.assertTrue(dynamicSimLogInteraction.isDynamicInstance());
        Assert.assertFalse(dynamicSimLogInteraction instanceof SimLog);
        Assert.assertEquals(SimLog.get_hla_class_name(), dynamicSimLogInteraction.getInstanceHlaClassName());

        String string1 = "string1";
        double doubleValue1 = 1.2;

        JSONArray jsonArray = new JSONArray();
        jsonArray.put(string1);
        dynamicSimLogInteraction.setParameter("InteractionRoot.C2WInteractionRoot", "federateSequence", jsonArray.toString());
        dynamicSimLogInteraction.setParameter("Time", doubleValue1);

        Assert.assertEquals(string1, C2WInteractionRoot.get_origin_federate_id(dynamicSimLogInteraction));
        Assert.assertEquals(doubleValue1, (double)dynamicSimLogInteraction.getParameter("Time"), 0.01);

        String string2 = "string2";
        double doubleValue2 = 3.4;

        C2WInteractionRoot.update_federate_sequence(dynamicSimLogInteraction, string2);
        dynamicSimLogInteraction.setParameter("Time", doubleValue2);

        List<String> federateSequenceList = C2WInteractionRoot.get_federate_sequence_list(dynamicSimLogInteraction);
        Assert.assertEquals(string1, federateSequenceList.get(0));
        Assert.assertEquals(string2, federateSequenceList.get(1));
        Assert.assertEquals(doubleValue2, (double)dynamicSimLogInteraction.getParameter("Time"), 0.01);

        InteractionRoot staticSimLogInteraction1 = InteractionRoot.create_interaction(SimLog.get_hla_class_name());
        Assert.assertFalse(staticSimLogInteraction1.isDynamicInstance());
        Assert.assertTrue(staticSimLogInteraction1 instanceof SimLog);
        Assert.assertEquals(SimLog.get_hla_class_name(), staticSimLogInteraction1.getInstanceHlaClassName());

        String string3 = "string3";
        double doubleValue3 = 5.6;

        SimLog simLogInteraction = (SimLog)staticSimLogInteraction1;
        C2WInteractionRoot.update_federate_sequence(simLogInteraction, string3);
        simLogInteraction.setParameter("Time", doubleValue3);

        federateSequenceList = simLogInteraction.getFederateSequenceList();
        Assert.assertEquals(string3, federateSequenceList.get(0));
        Assert.assertEquals(doubleValue3, simLogInteraction.getParameter("Time"));

        Assert.assertEquals(string3, simLogInteraction.getOriginFederateId());
        Assert.assertEquals(doubleValue3, simLogInteraction.get_Time(), 0.01);

        String string4 = "string4";
        double doubleValue4 = 17.3;

        simLogInteraction.updateFederateSequence(string4);
        simLogInteraction.set_Time(doubleValue4);

        federateSequenceList = simLogInteraction.getFederateSequenceList();
        Assert.assertEquals(string3, federateSequenceList.get(0));
        Assert.assertEquals(string4, federateSequenceList.get(federateSequenceList.size() - 1));
        Assert.assertEquals(doubleValue4, simLogInteraction.getParameter("Time"));

        Assert.assertEquals(string4, simLogInteraction.getSourceFederateId());
        Assert.assertEquals(string3, simLogInteraction.getOriginFederateId());
        Assert.assertEquals(doubleValue4, simLogInteraction.get_Time(), 0.01);
    }

    @Test
    public void messagingInstanceHlaClassTest() {
        InteractionRoot interactionRoot = new InteractionRoot(
                "InteractionRoot.C2WInteractionRoot.Simlog.HighPrio"
        );

        Assert.assertTrue( interactionRoot.isInstanceOfHlaClass(
                "InteractionRoot.C2WInteractionRoot.Simlog.HighPrio"
        ));
        Assert.assertTrue( interactionRoot.isInstanceHlaClassDerivedFromHlaClass(
                "InteractionRoot.C2WInteractionRoot.Simlog.HighPrio"
        ));
        Assert.assertTrue( interactionRoot.isInstanceHlaClassDerivedFromHlaClass(
                "InteractionRoot.C2WInteractionRoot.Simlog"
        ));

        Assert.assertFalse( interactionRoot.isInstanceOfHlaClass(
                "InteractionRoot.C2WInteractionRoot.Simlog"
        ));
        Assert.assertFalse( interactionRoot.isInstanceHlaClassDerivedFromHlaClass(
                "InteractionRoot.C2WInteractionRoot.SimulationControl"
        ));

        ObjectRoot objectRoot = new ObjectRoot();

        Assert.assertTrue( objectRoot.isInstanceOfHlaClass("ObjectRoot"));
        Assert.assertTrue( objectRoot.isInstanceHlaClassDerivedFromHlaClass("ObjectRoot"));
        Assert.assertFalse( objectRoot.isInstanceHlaClassDerivedFromHlaClass("ObjectRoot.FederateObject"));
        Assert.assertFalse( objectRoot.isInstanceOfHlaClass("ObjectRoot.FederateObject"));

        ObjectRoot federateObject = new ObjectRoot("ObjectRoot.FederateObject");
        Assert.assertFalse( federateObject.isInstanceOfHlaClass("ObjectRoot"));
        Assert.assertTrue( federateObject.isInstanceHlaClassDerivedFromHlaClass("ObjectRoot"));
        Assert.assertTrue( federateObject.isInstanceHlaClassDerivedFromHlaClass("ObjectRoot.FederateObject"));
        Assert.assertTrue( federateObject.isInstanceOfHlaClass("ObjectRoot.FederateObject"));
    }

    @Test
    public void objectTest1() {
        // MAKE SURE ATTRIBUTES ARE PUBLISHED
        FederateObject.publish_FederateHandle_attribute();
        FederateObject.publish_FederateHost_attribute();
        FederateObject.publish_FederateType_attribute();
        FederateObject.publish_object(rtiambassador);  // NOT REALLY NEEDED FOR TESTING

        // CREATE FederateObject, GIVE ATTRIBUTES VALUES
        FederateObject federateObject1 = new FederateObject();
        federateObject1.set_FederateHandle(2);
        federateObject1.set_FederateHost("localhost");
        federateObject1.set_FederateType("test");

        // REGISTER THE OBJECT WITH MOCK RTI
        federateObject1.registerObject(rtiambassador);

        // CHECK MOST RTI VALUES
        Assert.assertEquals(0, getCurrentObjectHandle());
        Assert.assertEquals(FederateObject.get_class_handle(), getCurrentClassHandle());

        // DISCOVER OBJECT INSTANCE TO CREATE A SECOND INSTANCE
        ObjectRoot objectRoot1 = ObjectRoot.discover(getCurrentClassHandle(), getCurrentObjectHandle());
        Assert.assertTrue(objectRoot1 instanceof FederateObject);

        // INITIALLY, SECOND INSTANCE SHOULD HAVE DEFAULT VALUES
        FederateObject federateObject2 = (FederateObject)objectRoot1;
        Assert.assertEquals(0, federateObject2.get_FederateHandle());
        Assert.assertEquals("", federateObject2.get_FederateHost());
        Assert.assertEquals("", federateObject2.get_FederateType());

        // SEND OUT ATTRIBUTE VALUES OF FIRST INSTANCE TO MOCK RTI
        federateObject1.updateAttributeValues(rtiambassador, 5.0);

        // CHECK MOCK RTI VALUES
        Assert.assertNotNull(getCurrentDoubleTime());
        Assert.assertEquals(5.0, getCurrentDoubleTime().getTime(), 0.1);

        // ALL VALUES SHOULD BE UPDATED (3)
        SuppliedAttributes currentSuppliedAttributes = getCurrentSuppliedAttributes();
        Assert.assertEquals(3, currentSuppliedAttributes.size());

        // REFLECT UPDATED ATTRIBUTE VALUES TO SECOND INSTANCE
        FederateObject.reflect(getCurrentObjectHandle(), getCurrentReflectedAttributes(), getCurrentDoubleTime());
        Assert.assertEquals(2, federateObject2.get_FederateHandle());
        Assert.assertEquals("localhost", federateObject2.get_FederateHost());
        Assert.assertEquals("test", federateObject2.get_FederateType());

        // CHANGE ONLY ONE VALUE IN FIRST INSTANCE AND SEND OUT UPDATE TO MOCK RTI
        federateObject1.set_FederateType("foobar");
        federateObject1.updateAttributeValues(rtiambassador, 6.0);

        // CHECK MOCK RTI VALUES
        Assert.assertNotNull(getCurrentDoubleTime());
        Assert.assertEquals(6.0, getCurrentDoubleTime().getTime(), 0.1);

        // ONLY ONE VALUE SHOULD BE UPDATED SINCE ONLY ONE WAS CHANGED
        currentSuppliedAttributes = getCurrentSuppliedAttributes();
        Assert.assertEquals(1, currentSuppliedAttributes.size());

        // REFLECT CHANGED ATTRIBUTE INTO SECOND INSTANCE, CHECK VALUES
        FederateObject.reflect(getCurrentObjectHandle(), getCurrentReflectedAttributes(), getCurrentDoubleTime());
        Assert.assertEquals(2, federateObject2.get_FederateHandle());
        Assert.assertEquals("localhost", federateObject2.get_FederateHost());
        Assert.assertEquals("foobar", federateObject2.get_FederateType());
    }
}
