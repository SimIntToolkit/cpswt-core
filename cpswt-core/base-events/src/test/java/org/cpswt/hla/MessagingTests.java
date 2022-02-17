package org.cpswt.hla;

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

import hla.rti.RTIambassador;
import org.mockito.invocation.InvocationOnMock;

import java.util.*;


public class MessagingTests {

    static HashMap<String, Integer> classNameHandleMap = new HashMap<>();
    static {
        int value = 0;
        classNameHandleMap.put("InteractionRoot", value++);
        classNameHandleMap.put("InteractionRoot.C2WInteractionRoot", value++);
        classNameHandleMap.put("InteractionRoot.C2WInteractionRoot.SimLog", value++);
        classNameHandleMap.put("InteractionRoot.C2WInteractionRoot.SimLog.HighPrio", value++);
        classNameHandleMap.put("InteractionRoot.C2WInteractionRoot.SimulationControl", value++);
        classNameHandleMap.put("InteractionRoot.C2WInteractionRoot.SimulationControl.SimEnd", value);
    }

    static HashMap<ClassAndPropertyName, Integer> interactionClassAndPropertyNameHandleMap = new HashMap<>();
    static HashMap<ObjectRootInterface.ClassAndPropertyName, Integer> objectClassAndPropertyNameHandleMap =
            new HashMap<>();
    static RTIambassador rtiambassador;

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
                new ClassAndPropertyName("InteractionRoot.C2WInteractionRoot", "originFed"),
                value++
        );
        interactionClassAndPropertyNameHandleMap.put(
                new ClassAndPropertyName("InteractionRoot.C2WInteractionRoot", "sourceFed"),
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
                            classNameHandleMap.get((String)invocationOnMock.getArgument(0))
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

        Assert.assertEquals((int)classNameHandleMap.get("InteractionRoot"), InteractionRoot.get_class_handle());
        Assert.assertEquals(
                (int)classNameHandleMap.get("InteractionRoot.C2WInteractionRoot"), C2WInteractionRoot.get_class_handle()
        );
        Assert.assertEquals(
                (int)classNameHandleMap.get("InteractionRoot.C2WInteractionRoot.SimLog"), SimLog.get_class_handle()
        );
        Assert.assertEquals(
                (int)classNameHandleMap.get("InteractionRoot.C2WInteractionRoot.SimLog.HighPrio"),
                HighPrio.get_class_handle()
        );
        Assert.assertEquals(
                (int)classNameHandleMap.get("InteractionRoot.C2WInteractionRoot.SimulationControl"),
                SimulationControl.get_class_handle()
        );
        Assert.assertEquals(
                (int)classNameHandleMap.get("InteractionRoot.C2WInteractionRoot.SimulationControl.SimEnd"),
                SimEnd.get_class_handle()
        );

        Assert.assertEquals(
                (int)classNameHandleMap.get("InteractionRoot"),
                InteractionRoot.get_class_handle("InteractionRoot"));
        Assert.assertEquals(
                (int)classNameHandleMap.get("InteractionRoot.C2WInteractionRoot"),
                C2WInteractionRoot.get_class_handle("InteractionRoot.C2WInteractionRoot")
        );
        Assert.assertEquals(
                (int)classNameHandleMap.get("InteractionRoot.C2WInteractionRoot.SimLog"),
                SimLog.get_class_handle("InteractionRoot.C2WInteractionRoot.SimLog")
        );
        Assert.assertEquals(
                (int)classNameHandleMap.get("InteractionRoot.C2WInteractionRoot.SimLog.HighPrio"),
                HighPrio.get_class_handle("InteractionRoot.C2WInteractionRoot.SimLog.HighPrio")
        );
        Assert.assertEquals(
                (int)classNameHandleMap.get("InteractionRoot.C2WInteractionRoot.SimulationControl"),
                SimulationControl.get_class_handle("InteractionRoot.C2WInteractionRoot.SimulationControl")
        );
        Assert.assertEquals(
                (int)classNameHandleMap.get("InteractionRoot.C2WInteractionRoot.SimulationControl.SimEnd"),
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
                C2WInteractionRoot.get_hla_class_name(), "originFed"
        ));
        expectedC2WInteractionRootParameterList.add(new InteractionRoot.ClassAndPropertyName(
                C2WInteractionRoot.get_hla_class_name(), "sourceFed"
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
                new ClassAndPropertyName("InteractionRoot.C2WInteractionRoot", "originFed"));

        Assert.assertEquals(expectedValue, HighPrio.get_parameter_handle("originFed"));
        Assert.assertEquals(expectedValue, SimLog.get_parameter_handle("originFed"));
        Assert.assertEquals(expectedValue, C2WInteractionRoot.get_parameter_handle("originFed"));

        expectedValue = interactionClassAndPropertyNameHandleMap.get(
                new ClassAndPropertyName("InteractionRoot.C2WInteractionRoot.SimLog", "FedName"));
        Assert.assertEquals(expectedValue, HighPrio.get_parameter_handle("FedName"));
        Assert.assertEquals(expectedValue, SimLog.get_parameter_handle("FedName"));

        expectedValue = objectClassAndPropertyNameHandleMap.get(
                new ObjectRootInterface.ClassAndPropertyName("ObjectRoot.FederateObject", "FederateHost"));
        Assert.assertEquals(expectedValue, FederateObject.get_attribute_handle("FederateHost"));
    }
}
