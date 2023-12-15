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
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.TextNode;
import hla.rti.*;
import org.junit.Test;
import org.junit.Assert;

import edu.vanderbilt.vuisis.cpswt.hla.InteractionRoot_p.C2WInteractionRoot;
import edu.vanderbilt.vuisis.cpswt.hla.InteractionRoot_p.C2WInteractionRoot_p.SimLog;
import edu.vanderbilt.vuisis.cpswt.hla.InteractionRoot_p.C2WInteractionRoot_p.SimLog_p.HighPrio;
import edu.vanderbilt.vuisis.cpswt.hla.InteractionRoot_p.C2WInteractionRoot_p.SimulationControl;
import edu.vanderbilt.vuisis.cpswt.hla.InteractionRoot_p.C2WInteractionRoot_p.SimulationControl_p.SimEnd;
import edu.vanderbilt.vuisis.cpswt.hla.InteractionRoot_p.C2WInteractionRoot_p.JSONTestInteraction;

import edu.vanderbilt.vuisis.cpswt.hla.ObjectRoot_p.FederateObject;
import edu.vanderbilt.vuisis.cpswt.hla.ObjectRoot_p.BaseObjectClass_p.DerivedObjectClass;
import edu.vanderbilt.vuisis.cpswt.hla.ObjectRoot_p.JSONTestObject;

import java.util.*;


public class InteractionTests {

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
    static {
        HighPrio.load();
        SimEnd.load();
        FederateObject.load();
        JSONTestInteraction.load();
        DerivedObjectClass.load();
        JSONTestObject.load();
        InteractionRoot.init(rtiambassador);
        ObjectRoot.init(rtiambassador);
    }

    @Test
    public void interactionClassNamesTest() {

        Set<String> expectedInteractionClassNameSet = new HashSet<>();
        expectedInteractionClassNameSet.add("InteractionRoot");
        expectedInteractionClassNameSet.add("InteractionRoot.C2WInteractionRoot");
        expectedInteractionClassNameSet.add("InteractionRoot.C2WInteractionRoot.SimLog");
        expectedInteractionClassNameSet.add("InteractionRoot.C2WInteractionRoot.SimLog.HighPrio");
        expectedInteractionClassNameSet.add("InteractionRoot.C2WInteractionRoot.SimulationControl");
        expectedInteractionClassNameSet.add("InteractionRoot.C2WInteractionRoot.SimulationControl.SimEnd");
        expectedInteractionClassNameSet.add("InteractionRoot.C2WInteractionRoot.JSONTestInteraction");

        Set<String> actualInteractionClassNameSet = InteractionRoot.get_interaction_hla_class_name_set();
        Assert.assertEquals(expectedInteractionClassNameSet, actualInteractionClassNameSet);
    }

    @Test
    public void interactionClassHandleTest() {

        InteractionRoot interactionRoot;

        // InteractionRoot
        int interactionRootClassHandle = mock.getInteractionClassNameHandleMap().get("InteractionRoot");
        Assert.assertEquals(interactionRootClassHandle, InteractionRoot.get_class_handle());
        interactionRoot = new InteractionRoot();
        Assert.assertEquals(interactionRootClassHandle, interactionRoot.getClassHandle());
        Assert.assertEquals(interactionRootClassHandle, InteractionRoot.get_class_handle("InteractionRoot"));

        // InteractionRoot.C2WInteractionRoot
        int c2wInteractionRootClassHandle = mock.getInteractionClassNameHandleMap().get(
                "InteractionRoot.C2WInteractionRoot"
        );
        Assert.assertEquals(c2wInteractionRootClassHandle, C2WInteractionRoot.get_class_handle());
        interactionRoot = new C2WInteractionRoot();
        Assert.assertEquals(c2wInteractionRootClassHandle, interactionRoot.getClassHandle());
        Assert.assertEquals(
                c2wInteractionRootClassHandle, InteractionRoot.get_class_handle("InteractionRoot.C2WInteractionRoot")
        );

        // InteractionRoot.C2WInteractionRoot.SimLog
        int simLogClassHandle = mock.getInteractionClassNameHandleMap().get(
                "InteractionRoot.C2WInteractionRoot.SimLog"
        );
        Assert.assertEquals(simLogClassHandle, SimLog.get_class_handle());
        interactionRoot = new SimLog();
        Assert.assertEquals(simLogClassHandle, interactionRoot.getClassHandle());
        Assert.assertEquals(
                simLogClassHandle,
                InteractionRoot.get_class_handle("InteractionRoot.C2WInteractionRoot.SimLog")
        );

        // InteractionRoot.C2WInteractionRoot.SimLog.HighPrio
        int highPrioClassHandle = mock.getInteractionClassNameHandleMap().get(
                "InteractionRoot.C2WInteractionRoot.SimLog.HighPrio"
        );
        Assert.assertEquals(highPrioClassHandle, HighPrio.get_class_handle());
        interactionRoot = new HighPrio();
        Assert.assertEquals(highPrioClassHandle, interactionRoot.getClassHandle());
        Assert.assertEquals(
                highPrioClassHandle,
                InteractionRoot.get_class_handle("InteractionRoot.C2WInteractionRoot.SimLog.HighPrio")
        );

        // InteractionRoot.C2WInteractionRoot.SimulationControl
        int simulationControlClassHandle = mock.getInteractionClassNameHandleMap().get(
                "InteractionRoot.C2WInteractionRoot.SimulationControl"
        );
        Assert.assertEquals(simulationControlClassHandle, SimulationControl.get_class_handle());
        interactionRoot = new SimulationControl();
        Assert.assertEquals(simulationControlClassHandle, interactionRoot.getClassHandle());
        Assert.assertEquals(
                simulationControlClassHandle,
                InteractionRoot.get_class_handle("InteractionRoot.C2WInteractionRoot.SimulationControl")
        );

        // InteractionRoot.C2WInteractionRoot.SimulationControl.SimEnd
        int simEndClassHandle = mock.getInteractionClassNameHandleMap().get(
                "InteractionRoot.C2WInteractionRoot.SimulationControl.SimEnd"
        );
        Assert.assertEquals(simEndClassHandle, SimEnd.get_class_handle());
        interactionRoot = new SimEnd();
        Assert.assertEquals(simEndClassHandle, interactionRoot.getClassHandle());
        Assert.assertEquals(
                simEndClassHandle,
                InteractionRoot.get_class_handle("InteractionRoot.C2WInteractionRoot.SimulationControl.SimEnd")
        );
    }

    @Test
    public void interactionParameterNamesTest() {

        InteractionRoot interactionRoot;

        // TEST InteractionRoot get_parameter_names()
        List<InteractionRoot.ClassAndPropertyName> expectedInteractionRootParameterList = new ArrayList<>();

        interactionRoot = new InteractionRoot();

        Assert.assertEquals(expectedInteractionRootParameterList, InteractionRoot.get_parameter_names());
        Assert.assertEquals(expectedInteractionRootParameterList, interactionRoot.getParameterNames());
        Assert.assertEquals(
                expectedInteractionRootParameterList, InteractionRoot.get_parameter_names("InteractionRoot")
        );

        // TEST InteractionRoot get_all_parameter_names()
        List<InteractionRoot.ClassAndPropertyName> expectedInteractionRootAllParameterList = new ArrayList<>(
                expectedInteractionRootParameterList
        );

        Assert.assertEquals(expectedInteractionRootAllParameterList, InteractionRoot.get_all_parameter_names());
        Assert.assertEquals(expectedInteractionRootAllParameterList, interactionRoot.getAllParameterNames());
        Assert.assertEquals(
                expectedInteractionRootAllParameterList, InteractionRoot.get_all_parameter_names("InteractionRoot")
        );


        // TEST InteractionRoot.C2WInteractionRoot get_parameter_names()
        List<InteractionRoot.ClassAndPropertyName> expectedC2WInteractionRootParameterList = new ArrayList<>();
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

        interactionRoot = new C2WInteractionRoot();

        Assert.assertEquals(expectedC2WInteractionRootParameterList, C2WInteractionRoot.get_parameter_names());
        Assert.assertEquals(expectedC2WInteractionRootParameterList, interactionRoot.getParameterNames());
        Assert.assertEquals(
                expectedC2WInteractionRootParameterList,
                InteractionRoot.get_parameter_names("InteractionRoot.C2WInteractionRoot")
        );

        // TEST InteractionRoot.C2WInteractionRoot get_all_parameter_names()
        List<InteractionRoot.ClassAndPropertyName> expectedC2WInteractionRootAllParameterList =
                new ArrayList<>(expectedInteractionRootAllParameterList);
        expectedC2WInteractionRootAllParameterList.addAll(expectedC2WInteractionRootParameterList);
        Collections.sort(expectedC2WInteractionRootAllParameterList);

        Assert.assertEquals(expectedC2WInteractionRootAllParameterList, C2WInteractionRoot.get_all_parameter_names());
        Assert.assertEquals(expectedC2WInteractionRootAllParameterList, interactionRoot.getAllParameterNames());
        Assert.assertEquals(
                expectedC2WInteractionRootAllParameterList,
                InteractionRoot.get_all_parameter_names("InteractionRoot.C2WInteractionRoot")
        );


        // TEST InteractionRoot.C2WInteractionRoot.SimLog get_parameter_names()
        List<InteractionRoot.ClassAndPropertyName> expectedSimLogParameterList = new ArrayList<>();
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

        interactionRoot = new SimLog();

        Assert.assertEquals(expectedSimLogParameterList, SimLog.get_parameter_names());
        Assert.assertEquals(expectedSimLogParameterList, interactionRoot.getParameterNames());
        Assert.assertEquals(
                expectedSimLogParameterList,
                InteractionRoot.get_parameter_names("InteractionRoot.C2WInteractionRoot.SimLog")
        );


        // TEST InteractionRoot.C2WInteractionRoot.SimLog get_all_parameter_names()
        List<InteractionRoot.ClassAndPropertyName> expectedSimLogAllParameterList =
                new ArrayList<>(expectedC2WInteractionRootAllParameterList);
        expectedSimLogAllParameterList.addAll(expectedSimLogParameterList);
        Collections.sort(expectedSimLogAllParameterList);

        Assert.assertEquals(expectedSimLogAllParameterList, SimLog.get_all_parameter_names());
        Assert.assertEquals(expectedSimLogAllParameterList, interactionRoot.getAllParameterNames());
        Assert.assertEquals(
                expectedSimLogAllParameterList,
                InteractionRoot.get_all_parameter_names("InteractionRoot.C2WInteractionRoot.SimLog")
        );


        // TEST InteractionRoot.C2WInteractionRoot.SimLog.HighPrio get_parameter_names()
        List<InteractionRoot.ClassAndPropertyName> expectedHighPrioParameterList = new ArrayList<>();

        interactionRoot = new HighPrio();

        Assert.assertEquals(expectedHighPrioParameterList, HighPrio.get_parameter_names());
        Assert.assertEquals(expectedHighPrioParameterList, interactionRoot.getParameterNames());
        Assert.assertEquals(
                expectedHighPrioParameterList,
                InteractionRoot.get_parameter_names("InteractionRoot.C2WInteractionRoot.SimLog.HighPrio")
        );


        // TEST InteractionRoot.C2WInteractionRoot.SimLog.HighPrio get_all_parameter_names()
        List<InteractionRoot.ClassAndPropertyName> expectedHighPrioAllParameterList =
                new ArrayList<>(expectedSimLogAllParameterList);
        expectedHighPrioAllParameterList.addAll(expectedHighPrioParameterList);
        Collections.sort(expectedHighPrioAllParameterList);

        Assert.assertEquals(expectedHighPrioAllParameterList, HighPrio.get_all_parameter_names());
        Assert.assertEquals(expectedHighPrioAllParameterList, interactionRoot.getAllParameterNames());
        Assert.assertEquals(
                expectedHighPrioAllParameterList,
                InteractionRoot.get_all_parameter_names(
                        "InteractionRoot.C2WInteractionRoot.SimLog.HighPrio"
                )
        );

        Assert.assertEquals(6, HighPrio.get_num_parameters());
        Assert.assertEquals(6, interactionRoot.getNumParameters());
        Assert.assertEquals(6, InteractionRoot.get_num_parameters(
                "InteractionRoot.C2WInteractionRoot.SimLog.HighPrio"
        ));
    }

    @Test
    public void interactionParameterHandleTest() {

        InteractionRoot interactionRoot;

        int expectedValue = mock.getInteractionClassAndPropertyNameHandleMap().get(
                new InteractionRoot.ClassAndPropertyName("InteractionRoot.C2WInteractionRoot", "federateSequence"));

        Assert.assertEquals(expectedValue, C2WInteractionRoot.get_parameter_handle("federateSequence"));
        interactionRoot = new C2WInteractionRoot();
        Assert.assertEquals(expectedValue, interactionRoot.getParameterHandle("federateSequence"));
        Assert.assertEquals(expectedValue, InteractionRoot.get_parameter_handle(
                "InteractionRoot.C2WInteractionRoot", "federateSequence"
        ));

        Assert.assertEquals(expectedValue, SimLog.get_parameter_handle("federateSequence"));
        interactionRoot = new SimLog();
        Assert.assertEquals(expectedValue, interactionRoot.getParameterHandle("federateSequence"));
        Assert.assertEquals(expectedValue, InteractionRoot.get_parameter_handle(
                "InteractionRoot.C2WInteractionRoot.SimLog", "federateSequence"
        ));

        Assert.assertEquals(expectedValue, HighPrio.get_parameter_handle("federateSequence"));
        interactionRoot = new HighPrio();
        Assert.assertEquals(expectedValue, interactionRoot.getParameterHandle("federateSequence"));
        Assert.assertEquals(expectedValue, InteractionRoot.get_parameter_handle(
                "InteractionRoot.C2WInteractionRoot.SimLog.HighPrio", "federateSequence"
        ));

        expectedValue = mock.getInteractionClassAndPropertyNameHandleMap().get(
                new InteractionRoot.ClassAndPropertyName("InteractionRoot.C2WInteractionRoot.SimLog", "FedName")
        );

        Assert.assertEquals(expectedValue, SimLog.get_parameter_handle("FedName"));
        interactionRoot = new SimLog();
        Assert.assertEquals(expectedValue, interactionRoot.getParameterHandle("FedName"));
        Assert.assertEquals(expectedValue, InteractionRoot.get_parameter_handle(
                "InteractionRoot.C2WInteractionRoot.SimLog", "FedName"
        ));

        Assert.assertEquals(expectedValue, HighPrio.get_parameter_handle("FedName"));
        interactionRoot = new HighPrio();
        Assert.assertEquals(expectedValue, interactionRoot.getParameterHandle("FedName"));
        Assert.assertEquals(expectedValue, InteractionRoot.get_parameter_handle(
                "InteractionRoot.C2WInteractionRoot.SimLog.HighPrio", "FedName"
        ));
    }

    @Test
    public void dynamicMessagingTest() {
        InteractionRoot dynamicSimLogInteraction = new InteractionRoot(SimLog.get_hla_class_name());

        Assert.assertTrue(dynamicSimLogInteraction.isDynamicInstance());
        Assert.assertFalse(dynamicSimLogInteraction instanceof SimLog);
        Assert.assertEquals(SimLog.get_hla_class_name(), dynamicSimLogInteraction.getInstanceHlaClassName());

        String string1 = "string1";
        double doubleValue1 = 1.2;

        ArrayNode jsonArray = objectMapper.createArrayNode();
        jsonArray.add(string1);
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

        // AS ONLY ONE FEDERATE NAME CAN BE ADDED TO THE federateSequence OF AN INTERACTION THAT IS DERIVED FROM
        // "InteractionRoot.C2WInteractionRoot" USING EITHER THE "update_federate_sequence" or "updateFederateSequence"
        // METHODS, THESE METHODS WILL NOT CHANGE THE "federateSequence" FOR simLogInteraction AFTER THE
        // CALL TO "update_federate_sequence" ABOVE
        simLogInteraction.updateFederateSequence(string4);
        simLogInteraction.set_Time(doubleValue4);

        federateSequenceList = simLogInteraction.getFederateSequenceList();
        Assert.assertEquals(string3, federateSequenceList.get(0));
        Assert.assertEquals(string3, federateSequenceList.get(federateSequenceList.size() - 1));
        Assert.assertEquals(doubleValue4, simLogInteraction.getParameter("Time"));

        Assert.assertEquals(string3, simLogInteraction.getSourceFederateId());
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
    public void federateSequenceTest() {

        // CHECK federateSequence THAT STARTS OUT EMPTY
        InteractionRoot interactionRoot1 = new InteractionRoot("InteractionRoot.C2WInteractionRoot.SimLog");

        String federateName1 = "federateName1";

        // ADD federateName1 TO federateSequence
        C2WInteractionRoot.update_federate_sequence(interactionRoot1, federateName1);

        // MAKE SURE IT'S THERE
        List<String> federateSequenceList1 = C2WInteractionRoot.get_federate_sequence_list(interactionRoot1);

        Assert.assertEquals(1, federateSequenceList1.size());
        Assert.assertEquals(federateName1, federateSequenceList1.get(0));

        // ADD IT AGAIN
        C2WInteractionRoot.update_federate_sequence(interactionRoot1, federateName1);

        // MAKE SURE federateSequence IS UNCHANGED
        List<String> federateSequenceList2 = C2WInteractionRoot.get_federate_sequence_list(interactionRoot1);

        Assert.assertEquals(1, federateSequenceList2.size());
        Assert.assertEquals(federateName1, federateSequenceList2.get(0));


        // CHECK federateSequence THAT STARTS OUT NON-EMPTY
        InteractionRoot interactionRoot2 = new InteractionRoot("InteractionRoot.C2WInteractionRoot.SimLog");

        List<String> federateNameList = new ArrayList<>(Arrays.asList("federateName2", "federateName3"));

        ArrayNode jsonArray = objectMapper.createArrayNode();
        for(String federateName: federateNameList) {
            jsonArray.add(federateName);
        }
        interactionRoot2.setParameter("federateSequence", jsonArray.toString());

        // ADD federateName1 TO federateSequence
        C2WInteractionRoot.update_federate_sequence(interactionRoot2, federateName1);

        // MAKE SURE IT'S THERE
        List<String> federateSequenceList3 = C2WInteractionRoot.get_federate_sequence_list(interactionRoot2);
        federateNameList.add(federateName1);

        Assert.assertEquals(federateNameList, federateSequenceList3);

        // ADD IT AGAIN
        C2WInteractionRoot.update_federate_sequence(interactionRoot2, federateName1);

        // MAKE SURE federateSequence IS UNCHANGED
        List<String> federateSequenceList4 = C2WInteractionRoot.get_federate_sequence_list(interactionRoot2);

        Assert.assertEquals(federateNameList, federateSequenceList3);
    }

    @Test
    public void jsonTest() {
        InteractionRoot jsonTestInteractionRoot = InteractionRoot.create_interaction(
                "InteractionRoot.C2WInteractionRoot.JSONTestInteraction"
        );

        JsonNode emptyList = new TextNode("");

        JsonNode jsonParameterGetParameterEmptyList =
                (JsonNode)jsonTestInteractionRoot.getParameter("JSONParameter");

        Assert.assertEquals(emptyList, jsonParameterGetParameterEmptyList);

        JSONTestInteraction jsonTestInteraction = (JSONTestInteraction)jsonTestInteractionRoot;

        JsonNode jsonParameterGetParameterDirectEmptyList =
                jsonTestInteraction.get_JSONParameter();

        Assert.assertEquals(emptyList, jsonParameterGetParameterDirectEmptyList);

        ArrayNode thingList = objectMapper.createArrayNode();
        thingList.add("this");
        thingList.add("that");
        thingList.add("other");
        jsonTestInteractionRoot.setParameter("JSONParameter", thingList);

        JsonNode jsonParameterGetParameterThingList =
                (JsonNode)jsonTestInteractionRoot.getParameter("JSONParameter");
        Assert.assertEquals(thingList, jsonParameterGetParameterThingList);

        JsonNode jsonParameterGetParameterDirectThingList =
                jsonTestInteraction.get_JSONParameter();
        Assert.assertEquals(thingList, jsonParameterGetParameterDirectThingList);

        ArrayNode stoogeList = objectMapper.createArrayNode();
        stoogeList.add("Moe");
        stoogeList.add("Larry");
        stoogeList.add("Curly");
        jsonTestInteraction.set_JSONParameter(stoogeList);

        JsonNode JSONParameterGetParameterStoogeList =
                (JsonNode)jsonTestInteractionRoot.getParameter("JSONParameter");
        Assert.assertEquals(stoogeList, JSONParameterGetParameterStoogeList);

        JsonNode jsonParameterGetParameterDirectStoogeList =
                jsonTestInteraction.get_JSONParameter();
        Assert.assertEquals(stoogeList, jsonParameterGetParameterDirectStoogeList);
    }
}
