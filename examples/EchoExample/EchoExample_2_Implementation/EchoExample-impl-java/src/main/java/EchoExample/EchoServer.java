package EchoExample;

import org.cpswt.utils.CpswtDefaults;
import org.cpswt.config.FederateConfig;
import org.cpswt.config.FederateConfigParser;
import org.cpswt.hla.InteractionRoot;
import org.cpswt.hla.base.AdvanceTimeRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class EchoServer extends EchoServerBase {

    static final Logger logger = LogManager.getLogger(EchoServer.class);

    public EchoServer(FederateConfig params) throws Exception {
        super(params);
    }

    List<ServerReply> replies = new ArrayList<ServerReply>();

    private void execute() throws Exception {

        // Add time advance request to RTI to go to 1.0 from the start
        double currentTime = 1.0;

//        super.federateInfo.updateAttributeValues(getLRC());

        if (super.isLateJoiner()) {
            currentTime = super.getLBTS() - super.getLookAhead();
            super.disableTimeRegulation();
        }

        AdvanceTimeRequest atr = new AdvanceTimeRequest( currentTime );
        putAdvanceTimeRequest( atr );

        if(!super.isLateJoiner()) {
            readyToPopulate();
            readyToRun();
        }

        startAdvanceTimeThread();

        InteractionRoot interactionRoot;

        while( true ) {
            // Wait for time to be granted by the RTI
            logger.debug("{}: requesting RTI to go to time: {}", this.getFederateId(), currentTime);

            currentTime += super.getStepSize();

            atr.requestSyncStart();

            // Process any incoming interactions in the queue
            while(  ( interactionRoot = getNextInteractionNoWait() ) != null ) {
                if(interactionRoot instanceof ClientMessage) {

                    // handle incoming message
                    ClientMessage message = (ClientMessage) interactionRoot;
                    logger.debug("{}: Received ClientMessage interaction | from: {}\t seq#: {}", this.getFederateId(), message.get_originFed(), message.get_sequenceNumber());

                    // assemble reply to that message
                    ServerReply reply = create_ServerReply();
                    reply.set_sequenceNumber(message.get_sequenceNumber());
                    reply.set_targetFed(message.get_originFed());

                    replies.add(reply);
                }
                else {
                    logger.trace("Interaction object was not a ClientMessage");
                }
            }

            // send replies
            for(ServerReply reply : replies) {
                reply.sendInteraction(getLRC(), currentTime + this.getLookAhead());
            }
            replies.clear();

            // Prepare to request RTI to advance time to next step
            AdvanceTimeRequest newATR = new AdvanceTimeRequest( currentTime );
            putAdvanceTimeRequest( newATR );

            atr.requestSyncEnd();
            atr = newATR;
        }

    }

    public static void main( String[] args ) {
        try {
            FederateConfigParser federateConfigParser = new FederateConfigParser();
            FederateConfig federateConfig = federateConfigParser.parseArgs(args, FederateConfig.class);
            EchoServer echoServer = new EchoServer(federateConfig);
            echoServer.execute();

            System.exit(0);
        } catch ( Exception e ) {
            logger.error("There was a problem executing the EchoServer federate: {}", e.getMessage());
            logger.error(e);
        }
        System.exit(1);
    }
}
