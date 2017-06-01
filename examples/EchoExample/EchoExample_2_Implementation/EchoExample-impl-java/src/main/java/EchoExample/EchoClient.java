package EchoExample;

import c2w.hla.InteractionRoot;
import c2w.hla.base.AdvanceTimeRequest;

import java.util.HashSet;
import java.util.Set;

public class EchoClient extends EchoClientBase {

    public EchoClient(String[] federationInfo) throws Exception {
        super(federationInfo);
    }

    private final int sendMessageCount = 500;
    int sequenceNumber = 0;
    Set<Integer> sentSequenceNumbers = new HashSet<Integer>();
    long waitToSendNextMessage = 10000;

    private void execute() throws Exception {

        double currentTime = 0;

        AdvanceTimeRequest atr = new AdvanceTimeRequest( currentTime );
        putAdvanceTimeRequest( atr );

        readyToPopulate();
        readyToRun();

        startAdvanceTimeThread();

        InteractionRoot interactionRoot;

        while( true ) {
            currentTime += 1;

            atr.requestSyncStart();

            // Send interactions to RTI
            this.sendClientMessage(currentTime + this.getLookahead());

            // Request RTI to advance time
            AdvanceTimeRequest newATR = new AdvanceTimeRequest( currentTime );
            putAdvanceTimeRequest( newATR );

            atr.requestSyncEnd();
            atr = newATR;

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
                        System.out.println(this.getFederateId() + ": Got a server reply back with sequence number: " + replySeqNum);
                    }
                    else {
                        System.out.println(this.getFederateId() + ": Server reply with sequence number unknown: " + replySeqNum);
                    }
                }
            }

            if(this.sequenceNumber > this.sendMessageCount) {
                break;
            }

            // wait until next message to send
            Thread.sleep(this.waitToSendNextMessage);
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