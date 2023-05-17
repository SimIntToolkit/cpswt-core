import hla.rti.AttributeHandleSet;
import hla.rti.ReceivedInteraction;
import edu.vanderbilt.vuisis.cpswt.config.FederateConfig;
import edu.vanderbilt.vuisis.cpswt.hla.InteractionRoot;
import edu.vanderbilt.vuisis.cpswt.hla.InteractionRoot_p.C2WInteractionRoot_p.EmbeddedMessaging;
import edu.vanderbilt.vuisis.cpswt.hla.ObjectRoot;
import edu.vanderbilt.vuisis.cpswt.hla.ObjectRootInterface;
import edu.vanderbilt.vuisis.cpswt.hla.ObjectRoot_p.TestObject;
import edu.vanderbilt.vuisis.cpswt.hla.RTIAmbassadorProxy2;
import edu.vanderbilt.vuisis.cpswt.hla.embeddedmessagingobjecttest.receiver.Receiver;
import edu.vanderbilt.vuisis.cpswt.hla.embeddedmessagingobjecttest.sender.Sender;
import hla.rti.ReflectedAttributes;
import hla.rti.SuppliedAttributes;
import hla.rti.jlc.RtiFactory;
import hla.rti.jlc.RtiFactoryFactory;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class EmbeddedMessagingObjectTests {

    static {
        Sender.load();
        Receiver.load();
    }

    public FederateConfig getNewFederateConfig(String federateName) {

        FederateConfig federateConfig = new FederateConfig();

        federateConfig.federateType = federateName;
        federateConfig.federationId = "testOjbectNetworkPropagation";
        federateConfig.isLateJoiner = false;
        federateConfig.lookahead = 0.1;
        federateConfig.name = federateName;
        federateConfig.stepSize = 1.0;

        return federateConfig;
    }

    RTIAmbassadorProxy2 rtiAmbassadorProxy2 = RTIAmbassadorProxy2.get_instance();

    @Test
    public void testObjectNetworkPropagation() throws Exception {

        //
        // CREATE Sender -- ALSO INITIALIZES TABLES IN InteractionRoot AND ObjectRoot
        //
        FederateConfig senderFederateConfig = getNewFederateConfig("Sender");
        Sender sender = new Sender(senderFederateConfig);

        //
        // CLASS HANDLES FOR FEDERATE-SPECIFIC EmbeddedMessaging INTERACTIONS
        //
        int embeddedMessagingOmnetFederateInteractionClassHandle = InteractionRoot.get_class_handle(
                EmbeddedMessaging.get_hla_class_name() + ".OmnetFederate"
        );

        //
        // LIST OF INTERACTION-DATA FOR INTERACTIONS SENT BY SENDER
        //
        List<RTIAmbassadorProxy2.SentInteractionData> sentInteractionDataList =
                rtiAmbassadorProxy2.getSentInteractionDataList();

        //
        // LIST OF REGISTERED-OBJECT-DATA FOR OBJECTS REGISTERED BY SENDER
        //
        List<RTIAmbassadorProxy2.RegisteredObjectData> registeredObjectDataList =
                rtiAmbassadorProxy2.getRegisteredObjectDataList();

        //
        // LIST OF UPDATED-OBJECT-DATA FOR OBJECTS FOR WHICH "updateAttibutes" WAS CALLED BY SENDER
        //
        List<RTIAmbassadorProxy2.UpdatedObjectData> updatedObjectDataList =
                rtiAmbassadorProxy2.getUpdatedObjectDataList();

        // WHEN SENDER WAS CREATED, IT REGISTERED ONE OBJECT
        Assert.assertEquals(1, registeredObjectDataList.size());

        // GET DATA FOR REGISTERED OBJECT
        RTIAmbassadorProxy2.RegisteredObjectData registeredObjectData = registeredObjectDataList.get(0);
        int objectClassHandle = registeredObjectData.getObjectClassHandle();
        int objectHandle = registeredObjectData.getObjectHandle();

        // MAKE SURE CLASS HANDLE OF REGISTERED OBJECT IS CORRECT
        Assert.assertEquals(TestObject.get_class_handle(), objectClassHandle);

        // ALSO WHEN SENDER WAS CREATED, IT SENT OUT 1 INTERACTION
        // * FederateJoinInteraction
        Assert.assertEquals(1, sentInteractionDataList.size());

        // THROW AWAY INTERACTION DATA SENT BY SENDER UP TO THIS POINT -- ALREADY TESTED
        sentInteractionDataList.clear();

        // EXECUTE THE SENDER -- SHOULD ASSIGN ATTRIBUTE VALUES TO REGISTERED OBJECT AND SEND OUT
        // THE NEW VALUES OF THE ATTRIBUTES IT PUBLISHES VIA AN "updateAttributes" CALL.
        sender.execute();

        // NUMBER OF OBJECTS UPDATED BY SENDER SHOULD BE 1
        Assert.assertEquals(1, updatedObjectDataList.size());

        // GET THE DATA ASSOCIATED WITH THE UPDATED ATTRIBUTES OF THE REGISTERED OBJECT
        RTIAmbassadorProxy2.UpdatedObjectData updatedObjectData = updatedObjectDataList.get(0);

        // GET THE REGISTERED OBJECT DIRECTLY FROM THE SENDER
        TestObject senderTestObject = sender.getTestObject();

        // OBJECT HANDLE OF UPDATED OBJECT SHOULD BE EQUAL TO OBJECT HANDLE OF REGISTERED OBJECT
        Assert.assertEquals(senderTestObject.getObjectHandle(), updatedObjectData.getObjectHandle());

        // ReflectedAttributes SHOULD BE PRESENT IN THE UPDATED-OBJECT DATA, AND SHOULD ONLY
        // CONTAIN DATA FOR 7 ATTRIBUTE -- THE NUMBER THAT ARE PUBLISHED.
        Assert.assertNotNull(updatedObjectData.getReflectedAttributes());
        Assert.assertEquals(updatedObjectData.getReflectedAttributes().size(), 8);

        // CREATE A LOCAL OBJECT INSTANCE FROM THE OBJECT-DATA THAT WAS SENT OUT FROM THE "updateAttributes"
        // CALL
        ObjectRoot localTestObjectObjectRoot =
                ObjectRoot.create_object(objectClassHandle, updatedObjectData.getReflectedAttributes());

        // THE LOCAL OBJECT SHOULD BE A TestObject
        Assert.assertTrue(localTestObjectObjectRoot instanceof TestObject);

        // CAST THE LOCAL OBJECT TO A TestObject
        TestObject localTestObjectObject = (TestObject)localTestObjectObjectRoot;

        // THE ATTRIBUTES THAT ARE PUBLISHED FOR THE REGISTERED OBJECT SHOULD HAVE THE SAME VALUES
        // AS THE CORRESPONDING ATTRIBUTES IN THE LOCAL OBJECT
        Assert.assertEquals(senderTestObject.get_BooleanValue1(), localTestObjectObject.get_BooleanValue1());
        Assert.assertEquals(senderTestObject.get_BooleanValue2(), localTestObjectObject.get_BooleanValue2());
        Assert.assertEquals(senderTestObject.get_ByteValue(), localTestObjectObject.get_ByteValue());
        Assert.assertEquals(senderTestObject.get_CharValue(), localTestObjectObject.get_CharValue());
        Assert.assertEquals(senderTestObject.get_FloatValue(), localTestObjectObject.get_FloatValue(), 0.001);
        Assert.assertEquals(senderTestObject.get_IntValue(), localTestObjectObject.get_IntValue());
        Assert.assertEquals(senderTestObject.get_LongValue(), localTestObjectObject.get_LongValue());
        Assert.assertEquals(senderTestObject.get_ShortValue(), localTestObjectObject.get_ShortValue());

        // THE ATTRIBUTES THAT ARE *NOT* PUBLISHED FOR THE REGISTERED OBJECT SHOULD *NOT* HAVE THE SAME VALUES
        // AS THE CORRESPONDING ATTRIBUTES IN THE LOCAL OBJECT
        Assert.assertNotEquals(senderTestObject.get_DoubleValue(), localTestObjectObject.get_DoubleValue(), 0.001);
        Assert.assertNotEquals(senderTestObject.get_StringValue(), localTestObjectObject.get_StringValue());

        // WHEN THE SENDER CALLED "updateAttributes" FOR THE REGISTERED OBJECT, IT SHOULD ALSO HAVE
        // SENT AN EmbeddedMessaging.OmnetFederate INTERACTION TO SEND THE UPDATED ATTRIBUTES THROUGH
        // A SIMULATED NETWORK.
        Assert.assertEquals(1, sentInteractionDataList.size());

        // GET THE INTERACTION-DATA ASSOCIATED WITH THE ATTRIBUTES UPDATE
        RTIAmbassadorProxy2.SentInteractionData updatedAttributesEmbeddedMessagingOmnetFederateData =
                sentInteractionDataList.get(0);

        // THE CLASS HANDLE OF THE INTERACTION ASSOCIATED WITH THE ATTRIBUTES UPDATE SHOULD
        // THAT OF THE OmnetFederate-SPECIFIC EmbeddedMessaging INTERACTION
        int updateAttributesEmbeddedMessagingOmnetFederateClassHandle =
                updatedAttributesEmbeddedMessagingOmnetFederateData.getInteractionClassHandle();
        Assert.assertEquals(
                embeddedMessagingOmnetFederateInteractionClassHandle,
                updateAttributesEmbeddedMessagingOmnetFederateClassHandle
        );

        // CREATE A LOCAL INSTANCE OF THE INTERACTION ASSOCIATED WITH THE ATTRIBUTES UPDATE
        ReceivedInteraction updateAttributesEmbeddedMessagingOmnetFederateReceivedInteraction =
                updatedAttributesEmbeddedMessagingOmnetFederateData.getReceivedInteraction();
        InteractionRoot localEmbeddedMessagingOmnetFederateInteractionRoot = InteractionRoot.create_interaction(
                updateAttributesEmbeddedMessagingOmnetFederateClassHandle,
                updateAttributesEmbeddedMessagingOmnetFederateReceivedInteraction
        );

        // THE LOCAL INTERACTION SHOULD BE OF THE OmnetFederate-SPECIFIC EmbeddedMessaging CLASS
        Assert.assertTrue(
                localEmbeddedMessagingOmnetFederateInteractionRoot instanceof
                        edu.vanderbilt.vuisis.cpswt.hla.InteractionRoot_p.C2WInteractionRoot_p.EmbeddedMessaging_p.OmnetFederate
        );

        // CAST THE LOCAL INTERACTION TO THE OmnetFederate-SPECIFIC EmbeddedMessaging CLASS
        edu.vanderbilt.vuisis.cpswt.hla.InteractionRoot_p.C2WInteractionRoot_p.EmbeddedMessaging_p.OmnetFederate
                localEmbeddedMessagingOmnetFederateInteraction =
                (edu.vanderbilt.vuisis.cpswt.hla.InteractionRoot_p.C2WInteractionRoot_p.EmbeddedMessaging_p.OmnetFederate)
                        localEmbeddedMessagingOmnetFederateInteractionRoot;

        // THE command FOR THE LOCAL EmbeddedMessaging.OmnetFederate INTERACTION SHOULD BE "object"
        Assert.assertEquals(localEmbeddedMessagingOmnetFederateInteraction.get_command(), "object");

        // THE hlaClassName SHOULD BE FOR TestObject
        Assert.assertEquals(
                localEmbeddedMessagingOmnetFederateInteraction.get_hlaClassName(), TestObject.get_hla_class_name()
        );

        JSONObject objectReflectorJson =
                new JSONObject(localEmbeddedMessagingOmnetFederateInteraction.get_messagingJson());
        Map<String, Object> objectReflectorJsonPropertiesMap =
                ((JSONObject)objectReflectorJson.get("properties")).toMap();

        // THE messagingJson SHOULD BE FOR ALL OF THE PUBLISHED ATTRIBUTES
        Assert.assertEquals(8, objectReflectorJsonPropertiesMap.size());

        // GET TestObject CLASS PUBLISHED ATTRIBUTE NAMES (ClassAndPropertyName SET)
        Set<ObjectRootInterface.ClassAndPropertyName> testObjectPublishedClassAndPropertyNameSet =
                TestObject.get_published_attribute_name_set();

        // testObjectClassAndPropertyNameSet SHOULD HAVE 7 MEMBERS
        Assert.assertEquals(8, testObjectPublishedClassAndPropertyNameSet.size());

        // MAKE SURE objectReflector HAS SAME JSON-ENCODED ATTRIBUTE VALUES AS VALUES OF ATTRIBUTES
        // FOR senderTestObject

        for(
                ObjectRootInterface.ClassAndPropertyName classAndPropertyName:
                testObjectPublishedClassAndPropertyNameSet
        ) {
            Assert.assertEquals(
                    senderTestObject.getAttribute(classAndPropertyName).toString(),
                    objectReflectorJsonPropertiesMap.get(classAndPropertyName.toString()).toString()
            );
        }

        // CLEAR OBJECTS IN ObjectRoot._objectHandleInstanceMap, SINCE THE Receiver FEDERATE ALSO USES IT.
        sender.unregisterObject(senderTestObject);


        // CLEAR THE AMBASSADOR PROXY FOR THE Receiver FEDERATE
        rtiAmbassadorProxy2.clear();

        // CREATE THE RECEIVER FEDERATE
        FederateConfig receiverFederateConfig = getNewFederateConfig("Receiver");
        Receiver receiver = new Receiver(receiverFederateConfig);

        // THE RECEIVER SHOULD NOT HAVE THE TestObject YET
        Assert.assertNull(receiver.getTestObject());

        // HAVE THE RECEIVER FEDERATE DISCOVER THE OBJECT INSTANCE SENT BY THE SENDER
        receiver.discoverObjectInstance(objectHandle, objectClassHandle, null);

        AttributeHandleSet attributeHandleSet = TestObject.get_subscribed_attribute_handle_set();
        Assert.assertEquals(4, attributeHandleSet.size());

        RtiFactory rtiFactory = RtiFactoryFactory.getRtiFactory( "org.portico.dlc.HLA13RTIFactory" );
        SuppliedAttributes suppliedAttributes = rtiFactory.createSuppliedAttributes();

        ReflectedAttributes reflectedAttributes = updatedObjectData.getReflectedAttributes();
        for(int ix = 0 ; ix < reflectedAttributes.size() ; ++ix) {
            int attributeHandle = reflectedAttributes.getAttributeHandle(ix);
            if (attributeHandleSet.isMember(attributeHandle)) {
                suppliedAttributes.add(attributeHandle, reflectedAttributes.getValue(ix));
            }
        }

        reflectedAttributes = new RTIAmbassadorProxy2.ReflectedAttributeImpl(suppliedAttributes);

        // TRY TO UPDATE OBJECT IN RECEIVER DIRECTLY FROM RTI
        receiver.reflectAttributeValues(
                objectHandle,
                reflectedAttributes,
                null,
                updatedObjectData.getLogicalTime(),
                null
        );

        // THE Receiver SHOULD IGNORE THE "reflectAttributeValues" CALL ABOVE, SO PERFORMED THE ACTIONS ASSOCIATED T
        receiver.execute();

        // THE RECEIVER SHOULD NOW HAVE THE OBJECT
        TestObject receivedTestObject = receiver.getTestObject();
        Assert.assertNotNull(receivedTestObject);

        // NOT SUBSCRIBED BY Receiver, SO receivedTestObject.get_BooleanValue1() HAS DEFAULT VALUE OF false,
        // BUT localTestObjectObject.get_BooleanValue1 IS INCIDENTALLY false
        Assert.assertEquals(receivedTestObject.get_BooleanValue1(), localTestObjectObject.get_BooleanValue1());
        // NOT PUBLISHED BY Sender, SO BOTH HAVE DEFAULT VALUE
        Assert.assertEquals(receivedTestObject.get_DoubleValue(), localTestObjectObject.get_DoubleValue(), 0.001);
        // NOT SUBSCRIBED BY Receiver
        Assert.assertNotEquals(receivedTestObject.get_FloatValue(), localTestObjectObject.get_FloatValue(), 0.001);
        // NOT PUBLISHED BY Sender, SO BOTH HAVE DEFAULT VALUE
        Assert.assertEquals(receivedTestObject.get_StringValue(), localTestObjectObject.get_StringValue());

        // BOTH PUBLISHED BY SENDER AND SUBSCRIBED BY RECEIVER
        Assert.assertEquals(receivedTestObject.get_BooleanValue2(), localTestObjectObject.get_BooleanValue2());
        Assert.assertEquals(receivedTestObject.get_ByteValue(), localTestObjectObject.get_ByteValue());
        Assert.assertEquals(receivedTestObject.get_CharValue(), localTestObjectObject.get_CharValue());

        // THESE ARE UPDATED THROUGH THE NETWORK, SO NOT RECEIVED YET
        Assert.assertNotEquals(receivedTestObject.get_IntValue(), localTestObjectObject.get_IntValue());
        Assert.assertNotEquals(receivedTestObject.get_LongValue(), localTestObjectObject.get_LongValue());
        Assert.assertNotEquals(receivedTestObject.get_ShortValue(), localTestObjectObject.get_ShortValue());

        // SEND THE RECEIVER THE EmbeddedMessaging.Receiver INTERACTION THAT CONTAINS THE ATTRIBUTES UPDATE
        // FOR THE DISCOVERED OBJECT
        receiver.receiveInteraction(
                updatedAttributesEmbeddedMessagingOmnetFederateData.getInteractionClassHandle(),
                updatedAttributesEmbeddedMessagingOmnetFederateData.getReceivedInteraction(),
                null,
                updatedAttributesEmbeddedMessagingOmnetFederateData.getLogicalTime(),
                null
        );

        // THIS WILL CAUSE THE Receiver TO APPLY THE ObjectReflector TO ITS LOCAL COPY OF THE TestObject INSTANCE
        // AND MAKE THE INSTANCE ACCESSIBLE THROUGH ITS getTestObject() METHOD
        receiver.execute();

        // NOT SUBSCRIBED BY Receiver, SO receivedTestObject.get_BooleanValue1() HAS DEFAULT VALUE OF false,
        // BUT localTestObjectObject.get_BooleanValue1 IS INCIDENTALLY false
        Assert.assertEquals(receivedTestObject.get_BooleanValue1(), localTestObjectObject.get_BooleanValue1());
        // NOT PUBLISHED BY Sender, SO BOTH HAVE DEFAULT VALUE
        Assert.assertEquals(receivedTestObject.get_DoubleValue(), localTestObjectObject.get_DoubleValue(), 0.001);
        // NOT SUBSCRIBED BY Receiver
        Assert.assertNotEquals(receivedTestObject.get_FloatValue(), localTestObjectObject.get_FloatValue(), 0.001);
        // NOT PUBLISHED BY Sender, SO BOTH HAVE DEFAULT VALUE
        Assert.assertEquals(receivedTestObject.get_StringValue(), localTestObjectObject.get_StringValue());

        // BOTH PUBLISHED BY SENDER AND SUBSCRIBED BY RECEIVER
        Assert.assertEquals(receivedTestObject.get_BooleanValue2(), localTestObjectObject.get_BooleanValue2());
        Assert.assertEquals(receivedTestObject.get_ByteValue(), localTestObjectObject.get_ByteValue());
        Assert.assertEquals(receivedTestObject.get_CharValue(), localTestObjectObject.get_CharValue());
        Assert.assertEquals(receivedTestObject.get_IntValue(), localTestObjectObject.get_IntValue());
        Assert.assertEquals(receivedTestObject.get_LongValue(), localTestObjectObject.get_LongValue());
        Assert.assertEquals(receivedTestObject.get_ShortValue(), localTestObjectObject.get_ShortValue());
    }
}
