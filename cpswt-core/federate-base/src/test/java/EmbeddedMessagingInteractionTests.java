import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import edu.vanderbilt.vuisis.cpswt.config.FederateConfig;
import edu.vanderbilt.vuisis.cpswt.hla.InteractionRoot;
import edu.vanderbilt.vuisis.cpswt.hla.InteractionRootInterface;
import edu.vanderbilt.vuisis.cpswt.hla.InteractionRoot_p.C2WInteractionRoot_p.EmbeddedMessaging;
import edu.vanderbilt.vuisis.cpswt.hla.InteractionRoot_p.C2WInteractionRoot_p.EmbeddedMessaging_p.TestOmnetFederate;
import edu.vanderbilt.vuisis.cpswt.hla.InteractionRoot_p.C2WInteractionRoot_p.TestInteraction;
import edu.vanderbilt.vuisis.cpswt.hla.RTIAmbassadorProxy2;
import edu.vanderbilt.vuisis.cpswt.hla.embeddedmessaginginteractiontest.receiver.Receiver;
import edu.vanderbilt.vuisis.cpswt.hla.embeddedmessaginginteractiontest.sender.Sender;
import hla.rti.ReceivedInteraction;
import hla.rti.SuppliedParameters;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class EmbeddedMessagingInteractionTests {

    public static ObjectMapper objectMapper = InteractionRoot.objectMapper;

    static {
        Sender.load();
        Receiver.load();
    }

    public FederateConfig getNewFederateConfig(String federateName) {

        FederateConfig federateConfig = new FederateConfig();

        federateConfig.federateType = federateName;
        federateConfig.federationId = "testInteractionNetworkPropagation";
        federateConfig.isLateJoiner = false;
        federateConfig.lookahead = 0.1;
        federateConfig.name = federateName;
        federateConfig.stepSize = 1.0;

        return federateConfig;
    }

    RTIAmbassadorProxy2 rtiAmbassadorProxy2 = RTIAmbassadorProxy2.get_instance();

    @Test
    public void testInteractionNetworkPropagation() throws Exception {

        //
        // CREATE Sender -- ALSO INITIALIZES TABLES IN InteractionRoot AND ObjectRoot
        //
        FederateConfig senderFederateConfig = getNewFederateConfig("Sender");
        Sender sender = new Sender(senderFederateConfig);

        //
        // CLASS HANDLES FOR FEDERATE-SPECIFIC EmbeddedMessaging INTERACTIONS
        //
        int embeddedMessagingOmnetFederateInteractionClassHandle = InteractionRoot.get_class_handle(
                EmbeddedMessaging.get_hla_class_name() + ".TestOmnetFederate"
        );

        //
        // LIST OF INTERACTION-DATA FOR INTERACTIONS SENT BY SENDER
        //
        List<RTIAmbassadorProxy2.SentInteractionData> sentInteractionDataList =
                rtiAmbassadorProxy2.getSentInteractionDataList();

        // ALSO WHEN SENDER WAS CREATED, IT SENT OUT 1 INTERACTION
        // * FederateJoinInteraction
        // THIS TEST BEHAVES DIFFERENTLY WHEN RUN FROM THE COMMAND-LINE "./gradlew :federate-base:build"
        // SAYS THERE ARE 2 INSTEAD OF 1, THE LATTER VALUE (1) IS RENDERED WHEN RUN FROM INTELLIJ DEBUGGER.
        // IN THE CASE OF 2, THEY ARE BOTH FederateJoinInteraction.
        Assert.assertTrue(sentInteractionDataList.size() <= 2);

        // THROW AWAY INTERACTION DATA SENT BY SENDER UP TO THIS POINT -- ALREADY TESTED
        sentInteractionDataList.clear();

        // EXECUTE THE SENDER -- SHOULD ASSIGN PARAMETER VALUES TO TestInteraction INSTANCE AND
        // SEND IT TO THE RTI.
        sender.execute();

        // GET THE SEND INTERACTION DIRECTLY FROM THE SENDER
        // ** THIS WILL BE AN EMBEDDEDMESSSAGING INTERACTION CONTAINING A JSON-ENCODED TESTINTERACTION
        TestInteraction senderTestInteraction = sender.getTestInteraction();

        // NUMBER OF INTERACTIONS SENT BY SENDER SHOULD BE 1
        Assert.assertEquals(1, sentInteractionDataList.size());

        // GET THE DATA ASSOCIATED WITH THE PARAMETERS OF THE SENT INTERACTION
        RTIAmbassadorProxy2.SentInteractionData sentInteractionEmbeddedMessagingOmnetFederateData =
                sentInteractionDataList.get(0);

        // THE CLASS HANDLE OF THE INTERACTION ASSOCIATED WITH THE SENT TESTINTERACTION SHOULD BE
        // THAT OF THE TestOmnetFederate-SPECIFIC EmbeddedMessaging INTERACTION
        int sentInteractionEmbeddedMessagingOmnetFederateClassHandle =
                sentInteractionEmbeddedMessagingOmnetFederateData.getInteractionClassHandle();
        Assert.assertEquals(
                embeddedMessagingOmnetFederateInteractionClassHandle,
                sentInteractionEmbeddedMessagingOmnetFederateClassHandle
        );

        SuppliedParameters sentInteractionEmbeddedMessagingOmnetFederateSuppliedParameters =
                sentInteractionEmbeddedMessagingOmnetFederateData.getSuppliedParameters();

        // SuppliedParameters SHOULD BE PRESENT IN THE SENT INTERACTION DATA, AND SHOULD
        // CONTAIN DATA FOR 6 PARAMETERS -- ALL THOSE PRESENT IN AN EMBEDDEDMESSAGING INTERACTION.
        Assert.assertNotNull(sentInteractionEmbeddedMessagingOmnetFederateSuppliedParameters);
        Assert.assertEquals(4, sentInteractionEmbeddedMessagingOmnetFederateSuppliedParameters.size());

        // ReceivedInteraction OBJECT IS CREATED FROM SuppliedParameters OBJECT AND IS NEEDED TO BUILD
        // AN INTERACTION
        ReceivedInteraction sentInteractionEmbeddedMessagingOmnetFederateReceivedInteraction =
                sentInteractionEmbeddedMessagingOmnetFederateData.getReceivedInteraction();

        // CREATE A LOCAL INTERACTION INSTANCE FROM THE INTERACTION-DATA THAT WAS SENT OUT FROM THE "sendInteraction"
        // CALL IN THE SENDER
        InteractionRoot localEmbeddedMessagingOmnetFederateInteractionRoot = InteractionRoot.create_interaction(
                sentInteractionEmbeddedMessagingOmnetFederateClassHandle,
                sentInteractionEmbeddedMessagingOmnetFederateReceivedInteraction
        );

        // THE LOCAL OBJECT SHOULD BE A TestObject
        Assert.assertTrue(localEmbeddedMessagingOmnetFederateInteractionRoot instanceof TestOmnetFederate);

        // CAST THE LOCAL OBJECT TO A TestObject
        TestOmnetFederate localEmbeddedMessagingOmnetFederateInteraction =
                (TestOmnetFederate)localEmbeddedMessagingOmnetFederateInteractionRoot;

        ObjectNode sentInteractionJson =
                (ObjectNode)objectMapper.readTree(localEmbeddedMessagingOmnetFederateInteraction.get_messagingJson());

        // THE messaging_type FOR THE LOCAL EmbeddedMessaging.TestOmnetFederate INTERACTION SHOULD BE "interaction"
        String message_type = sentInteractionJson.get("messaging_type").asText();
        Assert.assertEquals(message_type, "interaction");

        // THE messaging_name SHOULD BE FOR TestInteraction
        String messaging_name = sentInteractionJson.get("messaging_name").asText();
        Assert.assertEquals(messaging_name, TestInteraction.get_hla_class_name());

        ObjectNode sentInteractionJsonPropertiesMap = (ObjectNode)sentInteractionJson.get("properties");

        // THE messagingJson SHOULD BE FOR ALL OF THE PARAMETERS, INCLUDING THOSE INHERITED FROM C2WINTERACTIONROOT
        Assert.assertEquals(14, sentInteractionJsonPropertiesMap.size());

        // GET TestInteraction CLASS PARAMETER NAMES (ClassAndPropertyName SET)
        List<InteractionRootInterface.ClassAndPropertyName> testInteractionClassAndPropertyNameList =
                TestInteraction.get_parameter_names();

        // testInteractionClassAndPropertyNameList SHOULD HAVE 11 MEMBERS
        Assert.assertEquals(11, testInteractionClassAndPropertyNameList.size());

        // MAKE SURE INTERACTION ENCODED IN EMBEDDEDMESSAGING INTERACTION HAS SAME JSON-ENCODED ATTRIBUTE
        // VALUES AS VALUES OF PARAMETERS FOR senderTestInteraction

        for(
                InteractionRootInterface.ClassAndPropertyName classAndPropertyName:
                testInteractionClassAndPropertyNameList
        ) {
            Object parameterObject = senderTestInteraction.getParameter(classAndPropertyName);
            JsonNode jsonNode = sentInteractionJsonPropertiesMap.get(classAndPropertyName.toString());
            Object jsonObjectConversion = InteractionRoot.castJsonToType(jsonNode, parameterObject.getClass());
            Assert.assertEquals(parameterObject, jsonObjectConversion);
        }

        // CLEAR THE AMBASSADOR PROXY FOR THE Receiver FEDERATE
        rtiAmbassadorProxy2.clear();

        // CREATE THE RECEIVER FEDERATE
        FederateConfig receiverFederateConfig = getNewFederateConfig("Receiver");
        Receiver receiver = new Receiver(receiverFederateConfig);

        // THE RECEIVER SHOULD NOT HAVE THE TestInteraction YET
        Assert.assertNull(receiver.getTestInteraction());

        // TRY TO UPDATE OBJECT IN RECEIVER DIRECTLY FROM RTI
        receiver.receiveInteraction(
                sentInteractionEmbeddedMessagingOmnetFederateClassHandle,
                sentInteractionEmbeddedMessagingOmnetFederateReceivedInteraction,
                null,
                sentInteractionEmbeddedMessagingOmnetFederateData.getLogicalTime(),
                null
        );

        // THE Receiver SHOULD IGNORE THE "reflectAttributeValues" CALL ABOVE, SO PERFORMED THE ACTIONS ASSOCIATED T
        receiver.execute();

        // THE RECEIVER SHOULD NOW HAVE THE OBJECT
        TestInteraction receivedTestInteraction = receiver.getTestInteraction();
        Assert.assertNotNull(receivedTestInteraction);

        // NOT SUBSCRIBED BY Receiver, SO receivedTestInteraction.get_BoolValue1() HAS DEFAULT VALUE OF false,
        // BUT localEmbeddedMessagingOmnetFederateInteraction.get_BoolValue1 IS INCIDENTALLY false
        Assert.assertEquals(receivedTestInteraction.get_BoolValue1(), senderTestInteraction.get_BoolValue1());
        Assert.assertEquals(receivedTestInteraction.get_BoolValue2(), senderTestInteraction.get_BoolValue2());
        Assert.assertEquals(receivedTestInteraction.get_ByteValue(), senderTestInteraction.get_ByteValue());
        Assert.assertEquals(receivedTestInteraction.get_CharValue(), senderTestInteraction.get_CharValue());
        Assert.assertEquals(receivedTestInteraction.get_DoubleValue(), senderTestInteraction.get_DoubleValue(), 0.001);
        Assert.assertEquals(receivedTestInteraction.get_FloatValue(), senderTestInteraction.get_FloatValue(), 0.001);
        Assert.assertEquals(receivedTestInteraction.get_IntValue(), senderTestInteraction.get_IntValue());
        Assert.assertEquals(receivedTestInteraction.get_LongValue(), senderTestInteraction.get_LongValue());
        Assert.assertEquals(receivedTestInteraction.get_ShortValue(), senderTestInteraction.get_ShortValue());
        Assert.assertEquals(receivedTestInteraction.get_StringValue(), senderTestInteraction.get_StringValue());
        Assert.assertEquals(receivedTestInteraction.get_JSONValue(), senderTestInteraction.get_JSONValue());
    }
}
