package EchoExample;

import c2w.hla.InteractionRoot;
import c2w.hla.base.AdvanceTimeRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class EchoServer extends EchoServerBase {

    static final Logger logger = LogManager.getLogger(EchoServer.class);

    public EchoServer(String[] federationInfo) throws Exception {
        super(federationInfo);
    }

    double stepSize = 1.0;
    List<ServerReply> replies = new ArrayList<ServerReply>();

    private void execute() throws Exception {

        // Add time advance request to RTI to go to 1.0 from the start
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

            // Process any incoming interactions in the queue
            while(  ( interactionRoot = getNextInteractionNoWait() ) != null ) {
                if(!(interactionRoot instanceof ClientMessage)) {
                    continue;
                }

                // handle incoming message
                ClientMessage message = (ClientMessage) interactionRoot;
                logger.debug("{}: Received ClientMessage interaction | from: {}\t seq#: {}", this.getFederateId(), message.get_originFed(), message.get_sequenceNumber());

                // assemble reply to that message
                ServerReply reply = create_ServerReply();
                reply.set_sequenceNumber(message.get_sequenceNumber());
                reply.set_targetFed(message.get_originFed());

                replies.add(reply);
            }

            // send replies
            for(ServerReply reply : replies) {
                reply.sendInteraction(getRTI(), currentTime + this.getLookahead());
            }
            replies.clear();

            // Prepare to request RTI to advance time to next step
            atr.requestSyncEnd();
            currentTime += this.stepSize;
            AdvanceTimeRequest newATR = new AdvanceTimeRequest( currentTime );
            putAdvanceTimeRequest( newATR );
            atr = newATR;
        }

    }

    public static void main( String[] args ) {
        try {
            EchoServer echoServer = new EchoServer( args );
            echoServer.execute();
        } catch ( Exception e ) {
            System.err.println( "Exception caught: " + e.getMessage() );
            e.printStackTrace();
        }
    }
}
