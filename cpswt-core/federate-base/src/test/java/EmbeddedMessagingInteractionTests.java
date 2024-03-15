import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import edu.vanderbilt.vuisis.cpswt.config.FederateConfig;
import edu.vanderbilt.vuisis.cpswt.hla.InteractionRoot;
import edu.vanderbilt.vuisis.cpswt.hla.InteractionRootInterface;
import edu.vanderbilt.vuisis.cpswt.hla.InteractionRoot_p.AddProxy;
import edu.vanderbilt.vuisis.cpswt.hla.InteractionRoot_p.C2WInteractionRoot_p.EmbeddedMessaging;
import edu.vanderbilt.vuisis.cpswt.hla.InteractionRoot_p.C2WInteractionRoot_p.EmbeddedMessaging_p.TestOmnetFederate;
import edu.vanderbilt.vuisis.cpswt.hla.InteractionRoot_p.C2WInteractionRoot_p.TestInteraction;
import edu.vanderbilt.vuisis.cpswt.hla.InteractionRoot_p.DeleteProxy;
import edu.vanderbilt.vuisis.cpswt.hla.RTIAmbassadorProxy2;
import edu.vanderbilt.vuisis.cpswt.hla.embeddedmessaginginteractiontest.receiver.Receiver;
import edu.vanderbilt.vuisis.cpswt.hla.embeddedmessaginginteractiontest.sender.Sender;
import hla.rti.ReceivedInteraction;
import hla.rti.SuppliedParameters;
import org.junit.Assert;
import org.junit.Test;
import org.portico.impl.hla13.types.DoubleTime;

import java.util.*;

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

    private void checkTestInteractionJson(
            TestInteraction senderTestInteraction, ObjectNode sentInteractionJson, double time
    ) {

        Assert.assertEquals(time, sentInteractionJson.get("time").asDouble(), 0.001);

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
    }

    @Test
    public void testProxyFederateInteractions() throws Exception {
        FederateConfig senderFederateConfig = getNewFederateConfig("Sender");
        Sender sender = new Sender(senderFederateConfig);

        rtiAmbassadorProxy2.setSynchronizedFederate(sender);

        //
        // LIST OF INTERACTION-DATA FOR INTERACTIONS SENT BY SENDER
        //
        List<RTIAmbassadorProxy2.SentInteractionData> sentInteractionDataList =
                rtiAmbassadorProxy2.getSentInteractionDataList();

        // ERASE FederateJoinInteraction INTERACTION
        sentInteractionDataList.clear();

        // 3 INTERACTIONS SHOULD BE SENT TO RTI
        sender.executeForProxyFederateInteractions();

        Assert.assertEquals(3, sentInteractionDataList.size());


        // THIS SHOULD BE FIRST addProxy INTERACTION
        RTIAmbassadorProxy2.SentInteractionData sentInteractionEmbeddedMessagingOmnetFederateData1 =
                sentInteractionDataList.get(0);

        InteractionRoot interactionRoot1 = InteractionRoot.create_interaction(
                sentInteractionEmbeddedMessagingOmnetFederateData1.getInteractionClassHandle(),
                sentInteractionEmbeddedMessagingOmnetFederateData1.getReceivedInteraction()
        );

        Assert.assertTrue(interactionRoot1 instanceof AddProxy);
        AddProxy addProxy1 = (AddProxy)interactionRoot1;

        Assert.assertEquals(sender.getFederateType(), addProxy1.get_proxyFederateName());
        Assert.assertEquals(sender.virtualFederateName1, addProxy1.get_federateName());


        // THIS SHOULD BE SECOND addProxy INTERACTION
        RTIAmbassadorProxy2.SentInteractionData sentInteractionEmbeddedMessagingOmnetFederateData2 =
                sentInteractionDataList.get(1);

        InteractionRoot interactionRoot2 = InteractionRoot.create_interaction(
                sentInteractionEmbeddedMessagingOmnetFederateData2.getInteractionClassHandle(),
                sentInteractionEmbeddedMessagingOmnetFederateData2.getReceivedInteraction()
        );
        Assert.assertTrue(interactionRoot2 instanceof AddProxy);
        AddProxy addProxy2 = (AddProxy)interactionRoot2;

        Assert.assertEquals(sender.getFederateType(), addProxy2.get_proxyFederateName());
        Assert.assertEquals(sender.virtualFederateName2, addProxy2.get_federateName());


        // CHECK OUT PROXY FEDERATE TABLES IN SENDER
        Assert.assertEquals(sender.getFederateType(), sender.getProxyFederateName(sender.virtualFederateName1));
        Assert.assertEquals(sender.getFederateType(), sender.getProxyFederateName(sender.virtualFederateName2));

        Set<String> expectedProxiedFederateNameSet1 =
                new HashSet<>(Arrays.asList(sender.virtualFederateName1, sender.virtualFederateName2));
        Set<String> actualProxiedFederateNameSet1 = sender.getProxiedFederateNameSetCopy();
        Assert.assertEquals(expectedProxiedFederateNameSet1, actualProxiedFederateNameSet1);


        // THIRD SHOULD BE TestOmnetFederate INTERACTION
        RTIAmbassadorProxy2.SentInteractionData sentInteractionEmbeddedMessagingOmnetFederateData3 =
                sentInteractionDataList.get(2);

        InteractionRoot interactionRoot3 = InteractionRoot.create_interaction(
                sentInteractionEmbeddedMessagingOmnetFederateData3.getInteractionClassHandle(),
                sentInteractionEmbeddedMessagingOmnetFederateData3.getReceivedInteraction()
        );

        Assert.assertTrue(interactionRoot3 instanceof TestOmnetFederate);
        TestOmnetFederate testOmnetFederate1 = (TestOmnetFederate)interactionRoot3;

        List<String> federateSequenceList1 = List.of("Sender");
        Assert.assertEquals(federateSequenceList1, testOmnetFederate1.getFederateSequenceList());

        rtiAmbassadorProxy2.clear();

        // 2 INTERACTIONS SHOULD BE SENT TO RTI
        sender.executeForProxyFederateInteractions();

        Assert.assertEquals(2, sentInteractionDataList.size());


        // THIS SHOULD BE FIRST deleteProxy INTERACTION
        RTIAmbassadorProxy2.SentInteractionData sentInteractionEmbeddedMessagingOmnetFederateData4 =
                sentInteractionDataList.get(0);

        InteractionRoot interactionRoot4 = InteractionRoot.create_interaction(
                sentInteractionEmbeddedMessagingOmnetFederateData4.getInteractionClassHandle(),
                sentInteractionEmbeddedMessagingOmnetFederateData4.getReceivedInteraction()
        );

        Assert.assertTrue(interactionRoot4 instanceof DeleteProxy);
        DeleteProxy deleteProxy1 = (DeleteProxy)interactionRoot4;

        Assert.assertEquals(sender.virtualFederateName1, deleteProxy1.get_federateName());


        // THIS SHOULD BE SECOND addProxy INTERACTION
        RTIAmbassadorProxy2.SentInteractionData sentInteractionEmbeddedMessagingOmnetFederateData5 =
                sentInteractionDataList.get(1);

        InteractionRoot interactionRoot5 = InteractionRoot.create_interaction(
                sentInteractionEmbeddedMessagingOmnetFederateData5.getInteractionClassHandle(),
                sentInteractionEmbeddedMessagingOmnetFederateData5.getReceivedInteraction()
        );
        Assert.assertTrue(interactionRoot5 instanceof DeleteProxy);
        DeleteProxy deleteProxy2 = (DeleteProxy)interactionRoot5;

        Assert.assertEquals(sender.virtualFederateName2, deleteProxy2.get_federateName());


        // CHECK OUT PROXY FEDERATE TABLES IN SENDER
        Assert.assertNull(sender.getProxyFederateName(sender.virtualFederateName1));
        Assert.assertNull(sender.getProxyFederateName(sender.virtualFederateName2));

        Set<String> actualProxiedFederateNameSet2 = sender.getProxiedFederateNameSetCopy();
        Assert.assertEquals(new HashSet<>(), actualProxiedFederateNameSet2);

        // THIS SHOULD BE FIRST addProxy INTERACTION

        //
        // CREATE THE RECEIVER FEDERATE
        //
        FederateConfig receiverFederateConfig = getNewFederateConfig("Receiver");
        Receiver receiver = new Receiver(receiverFederateConfig);

        rtiAmbassadorProxy2.setSynchronizedFederate(receiver);

        // RECEIVER FIRST ADDPROXY INTERACTION
        receiver.receiveInteraction(
                sentInteractionEmbeddedMessagingOmnetFederateData1.getInteractionClassHandle(),
                sentInteractionEmbeddedMessagingOmnetFederateData1.getReceivedInteraction(),
                null
        );

        // RECEIVER SECOND ADDPROXY INTERACTION
        receiver.receiveInteraction(
                sentInteractionEmbeddedMessagingOmnetFederateData2.getInteractionClassHandle(),
                sentInteractionEmbeddedMessagingOmnetFederateData2.getReceivedInteraction(),
                null
        );

        // CHECK OUT PROXY FEDERATE TABLES IN RECEIVER
        Assert.assertEquals(sender.getFederateType(), receiver.getProxyFederateName(sender.virtualFederateName1));
        Assert.assertEquals(sender.getFederateType(), receiver.getProxyFederateName(sender.virtualFederateName2));

        Set<String> expectedProxiedFederateNameSet3 =
                new HashSet<>(Arrays.asList(sender.virtualFederateName1, sender.virtualFederateName2));
        Set<String> actualProxiedFederateNameSet3 = receiver.getProxiedFederateNameSetCopy(sender.getFederateType());
        Assert.assertEquals(expectedProxiedFederateNameSet3, actualProxiedFederateNameSet3);

        // RECEIVE TESTOMNETFEDERATE INTERACTION
        receiver.receiveInteraction(
                sentInteractionEmbeddedMessagingOmnetFederateData3.getInteractionClassHandle(),
                sentInteractionEmbeddedMessagingOmnetFederateData3.getReceivedInteraction(),
                null,
                sentInteractionEmbeddedMessagingOmnetFederateData3.getLogicalTime(),
                null

        );

        receiver.executeForProxyFederateInteractions();

        // THE RECEIVER SHOULD NOW HAVE AN INTERACTION
        List<TestInteraction> testInteractionList = receiver.getTestInteractionList();
        Assert.assertEquals(1, testInteractionList.size());

        TestInteraction testInteraction1 = testInteractionList.get(0);

        List<String> federateSequence1 = List.of(
                sender.virtualFederateName2, sender.getFederateType(), sender.virtualFederateName2
        );
        Assert.assertEquals(federateSequence1, testInteraction1.getFederateSequenceList());

        // RECEIVER FIRST ADDPROXY INTERACTION
        receiver.receiveInteraction(
                sentInteractionEmbeddedMessagingOmnetFederateData4.getInteractionClassHandle(),
                sentInteractionEmbeddedMessagingOmnetFederateData4.getReceivedInteraction(),
                null
        );

        // RECEIVER SECOND ADDPROXY INTERACTION
        receiver.receiveInteraction(
                sentInteractionEmbeddedMessagingOmnetFederateData5.getInteractionClassHandle(),
                sentInteractionEmbeddedMessagingOmnetFederateData5.getReceivedInteraction(),
                null
        );

        // CHECK OUT PROXY FEDERATE TABLES IN RECEIVER
        Assert.assertNull(sender.getFederateType(), receiver.getProxyFederateName(sender.virtualFederateName1));
        Assert.assertNull(sender.getFederateType(), receiver.getProxyFederateName(sender.virtualFederateName2));

        Set<String> actualProxiedFederateNameSet4 = receiver.getProxiedFederateNameSetCopy(sender.getFederateType());
        Assert.assertEquals(new HashSet<>(), actualProxiedFederateNameSet4);

    }

    @Test
    public void testInteractionNetworkPropagation() throws Exception {

        //
        // CREATE Sender -- ALSO INITIALIZES TABLES IN InteractionRoot AND ObjectRoot
        //
        FederateConfig senderFederateConfig = getNewFederateConfig("Sender");
        Sender sender = new Sender(senderFederateConfig);

        rtiAmbassadorProxy2.setSynchronizedFederate(sender);

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
        // SEND IT TO THE RTI
        sender.executeForInteractionNetworkPropagation();

        // GET THE SEND INTERACTION DIRECTLY FROM THE SENDER
        // ** THIS WILL BE AN EMBEDDEDMESSSAGING INTERACTION CONTAINING A JSON-ENCODED TESTINTERACTION
        TestInteraction senderTestInteraction = sender.getTestInteraction();

        // NUMBER OF INTERACTIONS SENT BY SENDER SHOULD BE 1
        Assert.assertEquals(1, sentInteractionDataList.size());

        // GET THE DATA ASSOCIATED WITH THE PARAMETERS OF THE SENT INTERACTION
        RTIAmbassadorProxy2.SentInteractionData sentInteractionEmbeddedMessagingOmnetFederateData1 =
                sentInteractionDataList.get(0);

        // THE CLASS HANDLE OF THE INTERACTION ASSOCIATED WITH THE SENT TESTINTERACTION SHOULD BE
        // THAT OF THE TestOmnetFederate-SPECIFIC EmbeddedMessaging INTERACTION
        int sentInteractionEmbeddedMessagingOmnetFederateClassHandle1 =
                sentInteractionEmbeddedMessagingOmnetFederateData1.getInteractionClassHandle();
        Assert.assertEquals(
                embeddedMessagingOmnetFederateInteractionClassHandle,
                sentInteractionEmbeddedMessagingOmnetFederateClassHandle1
        );

        // THE RECEIVED INTERACTION SHOULD HAVE A TIME OF 0.5
        DoubleTime doubleTime1 = sentInteractionEmbeddedMessagingOmnetFederateData1.getDoubleTime();
        Assert.assertEquals(doubleTime1.getTime(), 0.5, 0.001);

        SuppliedParameters sentInteractionEmbeddedMessagingOmnetFederateSuppliedParameters1 =
                sentInteractionEmbeddedMessagingOmnetFederateData1.getSuppliedParameters();

        // SuppliedParameters SHOULD BE PRESENT IN THE SENT INTERACTION DATA, AND SHOULD
        // CONTAIN DATA FOR 4 PARAMETERS -- ALL THOSE PRESENT IN AN EmbeddedMessaging INTERACTION.
        Assert.assertNotNull(sentInteractionEmbeddedMessagingOmnetFederateSuppliedParameters1);
        Assert.assertEquals(4, sentInteractionEmbeddedMessagingOmnetFederateSuppliedParameters1.size());

        // ReceivedInteraction OBJECT IS CREATED FROM SuppliedParameters OBJECT AND IS NEEDED TO BUILD
        // AN INTERACTION
        ReceivedInteraction sentInteractionEmbeddedMessagingOmnetFederateReceivedInteraction1 =
                sentInteractionEmbeddedMessagingOmnetFederateData1.getReceivedInteraction();

        // CREATE A LOCAL INTERACTION INSTANCE FROM THE INTERACTION-DATA THAT WAS SENT OUT FROM THE "sendInteraction"
        // CALL IN THE SENDER
        InteractionRoot localEmbeddedMessagingOmnetFederateInteractionRoot1 = InteractionRoot.create_interaction(
                sentInteractionEmbeddedMessagingOmnetFederateClassHandle1,
                sentInteractionEmbeddedMessagingOmnetFederateReceivedInteraction1
        );

        // THE LOCAL OBJECT SHOULD BE A TestObject
        Assert.assertTrue(localEmbeddedMessagingOmnetFederateInteractionRoot1 instanceof TestOmnetFederate);

        // CAST THE LOCAL OBJECT TO A TestObject
        TestOmnetFederate localEmbeddedMessagingOmnetFederateInteraction1 =
                (TestOmnetFederate)localEmbeddedMessagingOmnetFederateInteractionRoot1;

        ObjectNode sentInteractionJson1 =
                (ObjectNode)objectMapper.readTree(localEmbeddedMessagingOmnetFederateInteraction1.get_messagingJson());

        checkTestInteractionJson(senderTestInteraction, sentInteractionJson1, 0.5);

        // CLEAR THE AMBASSADOR PROXY FOR THE Receiver FEDERATE
        rtiAmbassadorProxy2.clear();

        // THE NEXT TWO INTERACTIONS SHOULD BE SENT ON THE NEXT execute() CALL OF THE SENDER
        sender.executeForInteractionNetworkPropagation();

        // NUMBER OF INTERACTIONS SENT BY SENDER SHOULD BE 1 EVEN THOUGH 2 WERE SENT BY SENDER --
        // THEY SHOULD BE COMBINED INTO A SINGLE INTERACTION
        Assert.assertEquals(1, sentInteractionDataList.size());

        // GET THE DATA ASSOCIATED WITH THE PARAMETERS OF THE SENT INTERACTION
        RTIAmbassadorProxy2.SentInteractionData sentInteractionEmbeddedMessagingOmnetFederateData2 =
                sentInteractionDataList.get(0);

        // THE CLASS HANDLE OF THE INTERACTION ASSOCIATED WITH THE SENT TESTINTERACTION SHOULD BE
        // THAT OF THE TestOmnetFederate-SPECIFIC EmbeddedMessaging INTERACTION
        int sentInteractionEmbeddedMessagingOmnetFederateClassHandle2 =
                sentInteractionEmbeddedMessagingOmnetFederateData2.getInteractionClassHandle();
        Assert.assertEquals(
                embeddedMessagingOmnetFederateInteractionClassHandle,
                sentInteractionEmbeddedMessagingOmnetFederateClassHandle2
        );

        // THE RECEIVED INTERACTION SHOULD HAVE A TIME OF 0.5
        DoubleTime doubleTime2 = sentInteractionEmbeddedMessagingOmnetFederateData2.getDoubleTime();
        Assert.assertEquals(doubleTime2.getTime(), 1.5, 0.001);

        SuppliedParameters sentInteractionEmbeddedMessagingOmnetFederateSuppliedParameters2 =
                sentInteractionEmbeddedMessagingOmnetFederateData2.getSuppliedParameters();

        // SuppliedParameters SHOULD BE PRESENT IN THE SENT INTERACTION DATA, AND SHOULD
        // CONTAIN DATA FOR 4 PARAMETERS -- ALL THOSE PRESENT IN AN EmbeddedMessaging INTERACTION.
        Assert.assertNotNull(sentInteractionEmbeddedMessagingOmnetFederateSuppliedParameters2);
        Assert.assertEquals(4, sentInteractionEmbeddedMessagingOmnetFederateSuppliedParameters2.size());

        // ReceivedInteraction OBJECT IS CREATED FROM SuppliedParameters OBJECT AND IS NEEDED TO BUILD
        // AN INTERACTION
        ReceivedInteraction sentInteractionEmbeddedMessagingOmnetFederateReceivedInteraction2 =
                sentInteractionEmbeddedMessagingOmnetFederateData2.getReceivedInteraction();

        // CREATE A LOCAL INTERACTION INSTANCE FROM THE INTERACTION-DATA THAT WAS SENT OUT FROM THE "sendInteraction"
        // CALL IN THE SENDER
        InteractionRoot localEmbeddedMessagingOmnetFederateInteractionRoot2 = InteractionRoot.create_interaction(
                sentInteractionEmbeddedMessagingOmnetFederateClassHandle2,
                sentInteractionEmbeddedMessagingOmnetFederateReceivedInteraction2
        );

        // THE LOCAL OBJECT SHOULD BE A TestObject
        Assert.assertTrue(localEmbeddedMessagingOmnetFederateInteractionRoot2 instanceof TestOmnetFederate);

        // CAST THE LOCAL OBJECT TO A TestObject
        TestOmnetFederate localEmbeddedMessagingOmnetFederateInteraction2 =
                (TestOmnetFederate)localEmbeddedMessagingOmnetFederateInteractionRoot2;

        // THIS SHOULD BE AN ARRAY
        ArrayNode sentInteractionJson2 =
                (ArrayNode)objectMapper.readTree(localEmbeddedMessagingOmnetFederateInteraction2.get_messagingJson());

        Assert.assertEquals(2, sentInteractionJson2.size());

        checkTestInteractionJson(senderTestInteraction, (ObjectNode)sentInteractionJson2.get(0), 1.5);
        checkTestInteractionJson(senderTestInteraction, (ObjectNode)sentInteractionJson2.get(1), 1.6);

        // TERMINATE ADVANCE-TIME-THREAD
        sender.executeForInteractionNetworkPropagation();

        // CLEAR THE AMBASSADOR PROXY FOR THE Receiver FEDERATE
        rtiAmbassadorProxy2.clear();
        rtiAmbassadorProxy2.resetCurrentTime();

        //
        // CREATE THE RECEIVER FEDERATE
        //
        FederateConfig receiverFederateConfig = getNewFederateConfig("Receiver");
        Receiver receiver = new Receiver(receiverFederateConfig);

        rtiAmbassadorProxy2.setSynchronizedFederate(receiver);

        // THE RECEIVER SHOULD NOT HAVE THE TestInteraction YET
        Assert.assertEquals(0, receiver.getTestInteractionList().size());

        // TRY TO UPDATE OBJECT IN RECEIVER DIRECTLY FROM RTI
        receiver.receiveInteraction(
                sentInteractionEmbeddedMessagingOmnetFederateClassHandle1,
                sentInteractionEmbeddedMessagingOmnetFederateReceivedInteraction1,
                null,
                sentInteractionEmbeddedMessagingOmnetFederateData1.getLogicalTime(),
                null
        );

        receiver.receiveInteraction(
                sentInteractionEmbeddedMessagingOmnetFederateClassHandle2,
                sentInteractionEmbeddedMessagingOmnetFederateReceivedInteraction2,
                null,
                sentInteractionEmbeddedMessagingOmnetFederateData2.getLogicalTime(),
                null
        );

        // THE Receiver SHOULD GET NO INTERACTIONS ON FIRST EXECUTION
        receiver.executeForInteractionNetworkPropagation();

        // THE RECEIVER SHOULD NOT HAVE THE TestInteraction YET
        Assert.assertEquals(0, receiver.getTestInteractionList().size());

        // THE Receiver SHOULD GET 1 INTERACTION
        receiver.executeForInteractionNetworkPropagation();

        // THE RECEIVER SHOULD NOW HAVE AN INTERACTION
        List<TestInteraction> testInteractionList = receiver.getTestInteractionList();
        Assert.assertEquals(1, testInteractionList.size());

        TestInteraction receivedTestInteraction = testInteractionList.get(0);

        Assert.assertEquals(0.5, receivedTestInteraction.getTime(), 0.001);

        // NOT SUBSCRIBED BY Receiver, SO receivedTestInteraction.get_BoolValue1() HAS DEFAULT VALUE OF false,
        // BUT localEmbeddedMessagingOmnetFederateInteraction2.get_BoolValue1 IS INCIDENTALLY false
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

        // THE Receiver SHOULD GET 2 INTERACTION2
        receiver.executeForInteractionNetworkPropagation();

        // THE RECEIVER SHOULD NOW HAVE AN INTERACTION
        testInteractionList = receiver.getTestInteractionList();
        Assert.assertEquals(2, testInteractionList.size());

        Assert.assertEquals(1.5, testInteractionList.get(0).getTime(), 0.001);
        Assert.assertEquals(1.6, testInteractionList.get(1).getTime(), 0.001);
    }
}
