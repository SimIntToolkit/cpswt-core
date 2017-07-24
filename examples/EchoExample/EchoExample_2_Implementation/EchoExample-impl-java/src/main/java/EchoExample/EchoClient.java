package EchoExample;

import org.cpswt.utils.CpswtDefaults;
import org.cpswt.config.FederateConfig;
import org.cpswt.config.FederateConfigParser;
import org.cpswt.hla.InteractionRoot;
import org.cpswt.hla.base.AdvanceTimeRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cpswt.utils.CpswtUtils;

import java.util.HashSet;
import java.util.Set;

public class EchoClient extends EchoClientBase {

    private static final Logger logger = LogManager.getLogger(EchoClient.class);

    public EchoClient(FederateConfig params) throws Exception {
        super(params);
    }

    private final int sendMessageCount = 125;
    int sequenceNumber = 0;
    Set<Integer> sentSequenceNumbers = new HashSet<Integer>();
    // long waitToSendNextMessage = 10000;

    private void execute() throws Exception {

        double currentTime = 1.0;

        if (super.isLateJoiner()) {
            currentTime = super.getLBTS() - super.getLookAhead();
            super.disableTimeRegulation();
        }

        AdvanceTimeRequest atr = new AdvanceTimeRequest(currentTime);
        putAdvanceTimeRequest(atr);

        if (!super.isLateJoiner()) {
            readyToPopulate();
            readyToRun();
        }

        startAdvanceTimeThread();

        InteractionRoot interactionRoot;

        while (true) {
            // Wait for time to be granted by the RTI
            logger.debug("{}: requesting RTI to go to time: {}", this.getFederateId(), currentTime);

            currentTime += super.getStepSize();

            atr.requestSyncStart();
            enteredTimeGrantedStated();

            // Waiting for incoming interactions
            while ((interactionRoot = getNextInteractionNoWait()) != null) {
                if (interactionRoot instanceof ServerReply) {

                    ServerReply reply = (ServerReply) interactionRoot;
                    if (reply.get_targetFed().equals(this.getFederateId())) {
                        int replySeqNum = reply.get_sequenceNumber();
                        if (this.sentSequenceNumbers.contains(replySeqNum)) {
                            this.sentSequenceNumbers.remove(replySeqNum);
                            logger.debug("{}: Got a server reply back with sequence number: {}", this.getFederateId(), replySeqNum);
                        } else {
                            logger.debug("{}: Server reply with sequence number unknown: {}", this.getFederateId(), replySeqNum);
                        }
                    }
                }
                else {
                    logger.trace("Interaction received is not type of ServerReply");
                }
            }

            if (this.sequenceNumber >= this.sendMessageCount) {
                break;
            }

            // Send interactions to RTI
            this.sendClientMessage(currentTime + this.getLookAhead());


            AdvanceTimeRequest newATR = new AdvanceTimeRequest(currentTime);
            putAdvanceTimeRequest(newATR);

            atr.requestSyncEnd();
            atr = newATR;

            // wait until next message to send
            // Thread.sleep(this.waitToSendNextMessage);
        }

        // done with sending the messages, time to resign
        super.notifyFederationOfResign();
    }

    void sendClientMessage(double currentTime) throws Exception {
        ClientMessage message = create_ClientMessage();
        this.sequenceNumber++;
        message.set_sequenceNumber(this.sequenceNumber);

        logger.debug("{}: Sending echo message interaction #{}", this.getFederateId(), this.sequenceNumber);
        message.sendInteraction(getLRC(), currentTime);

        // store sent sequenceNumber
        sentSequenceNumbers.add(this.sequenceNumber);
    }

    public static void main(String[] args) {
        try {
            FederateConfigParser federateConfigParser = new FederateConfigParser();
            FederateConfig federateConfig = federateConfigParser.parseArgs(args, FederateConfig.class);
            EchoClient echoClient = new EchoClient(federateConfig);
            echoClient.execute();

            System.exit(0);
        } catch (Exception e) {
            logger.error("There was a problem executing the EchoClient federate: {}", e.getMessage());
            logger.error(e);
        }
        System.exit(1);
    }
}
