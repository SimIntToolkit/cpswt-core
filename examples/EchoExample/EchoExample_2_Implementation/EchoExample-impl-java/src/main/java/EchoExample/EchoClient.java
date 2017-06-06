package EchoExample;

import c2w.hla.InteractionRoot;
import c2w.hla.base.AdvanceTimeRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashSet;
import java.util.Set;

public class EchoClient extends EchoClientBase {

    static final Logger logger = LogManager.getLogger(EchoClient.class);

    public EchoClient(String[] federationInfo) throws Exception {
        super(federationInfo);
    }

    private final int sendMessageCount = 500;
    int sequenceNumber = 0;
    Set<Integer> sentSequenceNumbers = new HashSet<Integer>();
    long waitToSendNextMessage = 10000;
    double stepSize = 1.0;

    private void execute() throws Exception {

        double currentTime = 1.0;

        AdvanceTimeRequest atr = new AdvanceTimeRequest( currentTime );
        putAdvanceTimeRequest( atr );

        readyToPopulate();
        readyToRun();

        startAdvanceTimeThread();

        InteractionRoot interactionRoot;

        while( true ) {
            // Wait for time to be granted by the RTI
            logger.debug("{}: requesting RTI to go to time: {}", this.getFederateId(), currentTime);
            atr.requestSyncStart();

            // Waiting for incoming interactions
            while(  ( interactionRoot = getNextInteractionNoWait() ) != null ) {
                if (!(interactionRoot instanceof ServerReply)) {
                    continue;
                }

                ServerReply reply = (ServerReply) interactionRoot;
                if(reply.get_targetFed().equals(this.getFederateId())) {
                    int replySeqNum = reply.get_sequenceNumber();
                    if(this.sentSequenceNumbers.contains(replySeqNum)) {
                        this.sentSequenceNumbers.remove(replySeqNum);
                        logger.debug("{}: Got a server reply back with sequence number: {}", this.getFederateId(), replySeqNum);
                    }
                    else {
                        logger.debug("{}: Server reply with sequence number unknown: {}", this.getFederateId(), replySeqNum);
                    }
                }
            }

            if(this.sequenceNumber > this.sendMessageCount) {
                break;
            }

            // Send interactions to RTI
            this.sendClientMessage(currentTime + this.getLookahead());

            // Request RTI to advance time
            atr.requestSyncEnd();
            currentTime += stepSize;
            AdvanceTimeRequest newATR = new AdvanceTimeRequest( currentTime );
            putAdvanceTimeRequest( newATR );

            atr.requestSyncEnd();
            atr = newATR;

            // wait until next message to send
            // Thread.sleep(this.waitToSendNextMessage);
        }
    }

    void sendClientMessage(double currentTime) throws Exception {
        ClientMessage message = create_ClientMessage();
        this.sequenceNumber++;
        message.set_sequenceNumber(this.sequenceNumber);

        System.out.println( this.getFederateId() + ": Sending echo message interaction #" + this.sequenceNumber );
        message.sendInteraction( getRTI(), currentTime );

        // store sent sequenceNumber
        sentSequenceNumbers.add(this.sequenceNumber);
    }

    public static void main( String[] args ) {
        try {
            EchoClient echoClient = new EchoClient(args);
            echoClient.execute();
        } catch ( Exception e ) {
            System.err.println( "Exception caught: " + e.getMessage() );
            e.printStackTrace();
        }
    }
}
