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

import hla.rti.RTIambassador;
import hla.rti.SuppliedAttributes;
import edu.vanderbilt.vuisis.cpswt.hla.InteractionRoot_p.C2WInteractionRoot_p.SimLog_p.HighPrio;
import edu.vanderbilt.vuisis.cpswt.hla.InteractionRoot_p.C2WInteractionRoot_p.SimulationControl_p.SimEnd;
import edu.vanderbilt.vuisis.cpswt.hla.ObjectRoot_p.BaseObjectClass_p.DerivedObjectClass;
import edu.vanderbilt.vuisis.cpswt.hla.ObjectRoot_p.FederateObject;
import edu.vanderbilt.vuisis.cpswt.hla.ObjectRoot_p.StringListTestObject;
import org.junit.Assert;
import org.junit.Test;

import java.util.*;


public class ObjectTests {

    private static final RTIAmbassadorProxy1 mock = new RTIAmbassadorProxy1();
    private static final RTIambassador rtiambassador = mock.getRTIAmbassador();

    static {
        HighPrio.load();
        SimEnd.load();
        FederateObject.load();
        DerivedObjectClass.load();
        StringListTestObject.load();

        InteractionRoot.init(rtiambassador);
        ObjectRoot.init(rtiambassador);
    }

    @Test
    public void objectClassNamesTest() {

        Set<String> expectedObjectClassNameSet = new HashSet<>();
        expectedObjectClassNameSet.add("ObjectRoot");
        expectedObjectClassNameSet.add("ObjectRoot.FederateObject");
        expectedObjectClassNameSet.add("ObjectRoot.BaseObjectClass");
        expectedObjectClassNameSet.add("ObjectRoot.BaseObjectClass.DerivedObjectClass");
        expectedObjectClassNameSet.add("ObjectRoot.StringListTestObject");

        Set<String> actualObjectClassNameSet = ObjectRoot.get_object_hla_class_name_set();
        Assert.assertEquals(expectedObjectClassNameSet, actualObjectClassNameSet);
    }

    @Test
    public void objectClassHandleTest() {

        ObjectRoot objectRoot;

        // InteractionRoot
        int objectRootClassHandle = mock.getObjectClassNameHandleMap().get("ObjectRoot");
        Assert.assertEquals(objectRootClassHandle, ObjectRoot.get_class_handle());
        objectRoot = new ObjectRoot();
        Assert.assertEquals(objectRootClassHandle, objectRoot.getClassHandle());
        Assert.assertEquals(objectRootClassHandle, ObjectRoot.get_class_handle("ObjectRoot"));

        // InteractionRoot.C2WInteractionRoot
        int federateObjectClassHandle = mock.getObjectClassNameHandleMap().get("ObjectRoot.FederateObject");
        Assert.assertEquals(federateObjectClassHandle, FederateObject.get_class_handle());
        objectRoot = new FederateObject();
        Assert.assertEquals(federateObjectClassHandle, objectRoot.getClassHandle());
        Assert.assertEquals(
                federateObjectClassHandle, ObjectRoot.get_class_handle("ObjectRoot.FederateObject")
        );
    }

    @Test
    public void objectAttributesNamesTest() {

        ObjectRoot objectRoot;

        // TEST ObjectRoot get_attribute_names()
        List<ObjectRoot.ClassAndPropertyName> expectedObjectRootAttributeList = new ArrayList<>();

        objectRoot = new ObjectRoot();

        Assert.assertEquals(expectedObjectRootAttributeList, ObjectRoot.get_attribute_names());
        Assert.assertEquals(expectedObjectRootAttributeList, objectRoot.getAttributeNames());
        Assert.assertEquals(
                expectedObjectRootAttributeList, ObjectRoot.get_attribute_names("ObjectRoot")
        );

        // TEST ObjectRoot get_all_attribute_names()
        List<ObjectRootInterface.ClassAndPropertyName> expectedInteractionRootAllParameterList =
                new ArrayList<>(expectedObjectRootAttributeList);

        Assert.assertEquals(expectedInteractionRootAllParameterList, ObjectRoot.get_all_attribute_names());
        Assert.assertEquals(expectedInteractionRootAllParameterList, objectRoot.getAllAttributeNames());
        Assert.assertEquals(
                expectedInteractionRootAllParameterList,
                ObjectRoot.get_all_attribute_names("ObjectRoot")
        );


        // TEST ObjectRoot.FederateObject get_attribute_names()
        List<ObjectRoot.ClassAndPropertyName> expectedFederateObjectAttributeList = new ArrayList<>();
        expectedFederateObjectAttributeList.add(new ObjectRoot.ClassAndPropertyName(
                FederateObject.get_hla_class_name(), "FederateHandle"
        ));
        expectedFederateObjectAttributeList.add(new ObjectRoot.ClassAndPropertyName(
                FederateObject.get_hla_class_name(), "FederateHost"
        ));
        expectedFederateObjectAttributeList.add(new ObjectRoot.ClassAndPropertyName(
                FederateObject.get_hla_class_name(), "FederateType"
        ));
        Collections.sort(expectedFederateObjectAttributeList);

        objectRoot = new FederateObject();

        Assert.assertEquals(expectedFederateObjectAttributeList, FederateObject.get_attribute_names());
        Assert.assertEquals(expectedFederateObjectAttributeList, objectRoot.getAttributeNames());
        Assert.assertEquals(
                expectedFederateObjectAttributeList,
                ObjectRoot.get_attribute_names("ObjectRoot.FederateObject")
        );

        Assert.assertEquals(3, FederateObject.get_num_attributes());
        Assert.assertEquals(3, objectRoot.getNumAttributes());
        Assert.assertEquals(3, ObjectRoot.get_num_attributes("ObjectRoot.FederateObject"));
    }

    @Test
    public void objectAttributeHandleTest() {

        int expectedValue = mock.getObjectClassAndPropertyNameHandleMap().get(
                new ObjectRootInterface.ClassAndPropertyName("ObjectRoot.FederateObject", "FederateHost"));

        ObjectRoot objectRoot;

        Assert.assertEquals(expectedValue, FederateObject.get_attribute_handle("FederateHost"));
        objectRoot = new FederateObject();
        Assert.assertEquals(expectedValue, objectRoot.getAttributeHandle("FederateHost"));
        Assert.assertEquals(expectedValue, ObjectRoot.get_attribute_handle("ObjectRoot.FederateObject", "FederateHost"));
    }

    @Test
    public void messagingInstanceHlaClassTest() {
        InteractionRoot interactionRoot = new InteractionRoot(
                "InteractionRoot.C2WInteractionRoot.Simlog.HighPrio"
        );

        Assert.assertTrue(interactionRoot.isInstanceOfHlaClass(
                "InteractionRoot.C2WInteractionRoot.Simlog.HighPrio"
        ));
        Assert.assertTrue(interactionRoot.isInstanceHlaClassDerivedFromHlaClass(
                "InteractionRoot.C2WInteractionRoot.Simlog.HighPrio"
        ));
        Assert.assertTrue(interactionRoot.isInstanceHlaClassDerivedFromHlaClass(
                "InteractionRoot.C2WInteractionRoot.Simlog"
        ));

        Assert.assertFalse(interactionRoot.isInstanceOfHlaClass(
                "InteractionRoot.C2WInteractionRoot.Simlog"
        ));
        Assert.assertFalse(interactionRoot.isInstanceHlaClassDerivedFromHlaClass(
                "InteractionRoot.C2WInteractionRoot.SimulationControl"
        ));

        ObjectRoot objectRoot = new ObjectRoot();

        Assert.assertTrue(objectRoot.isInstanceOfHlaClass("ObjectRoot"));
        Assert.assertTrue(objectRoot.isInstanceHlaClassDerivedFromHlaClass("ObjectRoot"));
        Assert.assertFalse(objectRoot.isInstanceHlaClassDerivedFromHlaClass("ObjectRoot.FederateObject"));
        Assert.assertFalse(objectRoot.isInstanceOfHlaClass("ObjectRoot.FederateObject"));

        ObjectRoot federateObject = new ObjectRoot("ObjectRoot.FederateObject");
        Assert.assertFalse(federateObject.isInstanceOfHlaClass("ObjectRoot"));
        Assert.assertTrue(federateObject.isInstanceHlaClassDerivedFromHlaClass("ObjectRoot"));
        Assert.assertTrue(federateObject.isInstanceHlaClassDerivedFromHlaClass("ObjectRoot.FederateObject"));
        Assert.assertTrue(federateObject.isInstanceOfHlaClass("ObjectRoot.FederateObject"));
    }

    @Test
    public void attributePubSubTest() {

        // PUBLISH
        Set<ObjectRootInterface.ClassAndPropertyName> expectedPublishedClassAndPropertyNameSet =
                new HashSet<>();
        expectedPublishedClassAndPropertyNameSet.add(new ObjectRootInterface.ClassAndPropertyName(
                "ObjectRoot.BaseObjectClass.DerivedObjectClass", "int_attribute1"
        ));
        expectedPublishedClassAndPropertyNameSet.add(new ObjectRootInterface.ClassAndPropertyName(
                "ObjectRoot.BaseObjectClass.DerivedObjectClass", "int_attribute2"
        ));
        expectedPublishedClassAndPropertyNameSet.add(new ObjectRootInterface.ClassAndPropertyName(
                "ObjectRoot.BaseObjectClass.DerivedObjectClass", "string_attribute2"
        ));
        expectedPublishedClassAndPropertyNameSet.add(new ObjectRootInterface.ClassAndPropertyName(
                "ObjectRoot.BaseObjectClass", "int_attribute1"
        ));
        expectedPublishedClassAndPropertyNameSet.add(new ObjectRootInterface.ClassAndPropertyName(
                "ObjectRoot.BaseObjectClass", "string_attribute1"
        ));

        DerivedObjectClass.publish_int_attribute1_attribute();
        DerivedObjectClass.publish_int_attribute2_attribute();
        DerivedObjectClass.publish_attribute("ObjectRoot.BaseObjectClass", "int_attribute1");
        DerivedObjectClass.publish_string_attribute1_attribute();
        DerivedObjectClass.publish_string_attribute2_attribute();

        Set<ObjectRootInterface.ClassAndPropertyName> actualPublishedClassAndPropertyNameSet =
                DerivedObjectClass.get_published_attribute_name_set();

        Assert.assertEquals(expectedPublishedClassAndPropertyNameSet, actualPublishedClassAndPropertyNameSet);

        // UNPUBLISH
        DerivedObjectClass.unpublish_int_attribute1_attribute();
        DerivedObjectClass.unpublish_attribute("ObjectRoot.BaseObjectClass", "int_attribute1");

        expectedPublishedClassAndPropertyNameSet.remove(new ObjectRootInterface.ClassAndPropertyName(
                "ObjectRoot.BaseObjectClass.DerivedObjectClass", "int_attribute1"
        ));
        expectedPublishedClassAndPropertyNameSet.remove(new ObjectRootInterface.ClassAndPropertyName(
                "ObjectRoot.BaseObjectClass", "int_attribute1"
        ));

        actualPublishedClassAndPropertyNameSet = DerivedObjectClass.get_published_attribute_name_set();

        Assert.assertEquals(expectedPublishedClassAndPropertyNameSet, actualPublishedClassAndPropertyNameSet);


        // SUBSCRIBE
        Set<ObjectRootInterface.ClassAndPropertyName> expectedSubscribedClassAndPropertyNameSet =
                new HashSet<>();
        expectedSubscribedClassAndPropertyNameSet.add(new ObjectRootInterface.ClassAndPropertyName(
                "ObjectRoot.BaseObjectClass.DerivedObjectClass", "int_attribute1"
        ));
        expectedSubscribedClassAndPropertyNameSet.add(new ObjectRootInterface.ClassAndPropertyName(
                "ObjectRoot.BaseObjectClass.DerivedObjectClass", "int_attribute2"
        ));
        expectedSubscribedClassAndPropertyNameSet.add(new ObjectRootInterface.ClassAndPropertyName(
                "ObjectRoot.BaseObjectClass.DerivedObjectClass", "string_attribute2"
        ));
        expectedSubscribedClassAndPropertyNameSet.add(new ObjectRootInterface.ClassAndPropertyName(
                "ObjectRoot.BaseObjectClass", "int_attribute1"
        ));
        expectedSubscribedClassAndPropertyNameSet.add(new ObjectRootInterface.ClassAndPropertyName(
                "ObjectRoot.BaseObjectClass", "string_attribute1"
        ));

        DerivedObjectClass.subscribe_int_attribute1_attribute();
        DerivedObjectClass.subscribe_int_attribute2_attribute();
        DerivedObjectClass.subscribe_attribute("ObjectRoot.BaseObjectClass", "int_attribute1");
        DerivedObjectClass.subscribe_string_attribute1_attribute();
        DerivedObjectClass.subscribe_string_attribute2_attribute();

        Set<ObjectRootInterface.ClassAndPropertyName> actualSubscribedClassAndPropertyNameSet =
                DerivedObjectClass.get_subscribed_attribute_name_set();

        Assert.assertEquals(expectedSubscribedClassAndPropertyNameSet, actualSubscribedClassAndPropertyNameSet);

        // UNSUBSCRIBE
        DerivedObjectClass.unsubscribe_int_attribute1_attribute();
        DerivedObjectClass.unsubscribe_attribute("ObjectRoot.BaseObjectClass", "int_attribute1");

        expectedSubscribedClassAndPropertyNameSet.remove(new ObjectRootInterface.ClassAndPropertyName(
                "ObjectRoot.BaseObjectClass.DerivedObjectClass", "int_attribute1"
        ));
        expectedSubscribedClassAndPropertyNameSet.remove(new ObjectRootInterface.ClassAndPropertyName(
                "ObjectRoot.BaseObjectClass", "int_attribute1"
        ));

        actualSubscribedClassAndPropertyNameSet = DerivedObjectClass.get_subscribed_attribute_name_set();

        Assert.assertEquals(expectedSubscribedClassAndPropertyNameSet, actualSubscribedClassAndPropertyNameSet);
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
        Assert.assertEquals(0, mock.getCurrentObjectHandle());
        Assert.assertEquals(FederateObject.get_class_handle(), mock.getCurrentClassHandle());

        // DISCOVER OBJECT INSTANCE TO CREATE A SECOND INSTANCE
        ObjectRoot objectRoot1 = ObjectRoot.discover(
                mock.getCurrentClassHandle(), mock.getCurrentDiscoverObjectHandle()
        );
        Assert.assertTrue(objectRoot1 instanceof FederateObject);

        // INITIALLY, SECOND INSTANCE SHOULD HAVE DEFAULT VALUES
        FederateObject federateObject2 = (FederateObject) objectRoot1;
        Assert.assertEquals(0, federateObject2.get_FederateHandle());
        Assert.assertEquals("", federateObject2.get_FederateHost());
        Assert.assertEquals("", federateObject2.get_FederateType());

        // SEND OUT ATTRIBUTE VALUES OF FIRST INSTANCE TO MOCK RTI
        federateObject1.updateAttributeValues(rtiambassador, 5.0);

        // CHECK MOCK RTI VALUES
        Assert.assertNotNull(mock.getCurrentDoubleTime());
        Assert.assertEquals(5.0, mock.getCurrentDoubleTime().getTime(), 0.1);

        // ALL VALUES SHOULD BE UPDATED (3)
        SuppliedAttributes currentSuppliedAttributes = mock.getCurrentSuppliedAttributes();
        Assert.assertEquals(3, currentSuppliedAttributes.size());

        // REFLECT UPDATED ATTRIBUTE VALUES TO SECOND INSTANCE
        FederateObject.reflect(mock.getCurrentDiscoverObjectHandle(), mock.getCurrentReflectedAttributes(), mock.getCurrentDoubleTime());
        Assert.assertEquals(2, federateObject2.get_FederateHandle());
        Assert.assertEquals("localhost", federateObject2.get_FederateHost());
        Assert.assertEquals("test", federateObject2.get_FederateType());

        // CHANGE ONLY ONE VALUE IN FIRST INSTANCE AND SEND OUT UPDATE TO MOCK RTI
        federateObject1.set_FederateType("foobar");
        federateObject1.updateAttributeValues(rtiambassador, 6.0);

        // CHECK MOCK RTI VALUES
        Assert.assertNotNull(mock.getCurrentDoubleTime());
        Assert.assertEquals(6.0, mock.getCurrentDoubleTime().getTime(), 0.1);

        // ONLY ONE VALUE SHOULD BE UPDATED SINCE ONLY ONE WAS CHANGED
        currentSuppliedAttributes = mock.getCurrentSuppliedAttributes();
        Assert.assertEquals(1, currentSuppliedAttributes.size());

        // REFLECT CHANGED ATTRIBUTE INTO SECOND INSTANCE, CHECK VALUES
        FederateObject.reflect(mock.getCurrentDiscoverObjectHandle(), mock.getCurrentReflectedAttributes(), mock.getCurrentDoubleTime());
        Assert.assertEquals(2, federateObject2.get_FederateHandle());
        Assert.assertEquals("localhost", federateObject2.get_FederateHost());
        Assert.assertEquals("foobar", federateObject2.get_FederateType());
    }

    @Test
    public void stringListTest() {
        ObjectRoot stringListTestObjectRoot = ObjectRoot.create_object(
                "ObjectRoot.StringListTestObject"
        );

        List<String> emptyList = new ArrayList<>();

        List<String> stringListAttributeGetAttributeEmptyList =
                (List<String>)stringListTestObjectRoot.getAttribute("stringListAttribute");

        Assert.assertEquals(emptyList, stringListAttributeGetAttributeEmptyList);

        StringListTestObject stringListTestObject = (StringListTestObject)stringListTestObjectRoot;

        List<String> stringListAttributeGetAttributeDirectEmptyList =
                stringListTestObject.get_stringListAttribute();

        Assert.assertEquals(emptyList, stringListAttributeGetAttributeDirectEmptyList);

        List<String> thingList = Arrays.asList("this", "that", "other");
        stringListTestObjectRoot.setAttribute("stringListAttribute", thingList);

        List<String> stringListAttributeGetAttributeThingList =
                (List<String>)stringListTestObjectRoot.getAttribute("stringListAttribute");
        Assert.assertEquals(thingList, stringListAttributeGetAttributeThingList);

        List<String> stringListAttributeGetAttributeDirectThingList =
                stringListTestObject.get_stringListAttribute();
        Assert.assertEquals(thingList, stringListAttributeGetAttributeDirectThingList);

        List<String> stoogeList = Arrays.asList("Moe", "Larry", "Curly");
        stringListTestObject.set_stringListAttribute(stoogeList);

        List<String> stringListAttributeGetAttributeStoogeList =
                (List<String>)stringListTestObjectRoot.getAttribute("stringListAttribute");
        Assert.assertEquals(stoogeList, stringListAttributeGetAttributeStoogeList);

        List<String> stringListAttributeGetAttributeDirectStoogeList =
                stringListTestObject.get_stringListAttribute();
        Assert.assertEquals(stoogeList, stringListAttributeGetAttributeDirectStoogeList);
    }
}