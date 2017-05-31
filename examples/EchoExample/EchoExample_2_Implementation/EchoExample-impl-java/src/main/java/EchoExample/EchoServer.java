package EchoExample;

import c2w.hla.InteractionRoot;
import c2w.hla.base.AdvanceTimeRequest;

public class EchoServer extends EchoServerBase {

    public EchoServer(String[] federationInfo) throws Exception {
        super(federationInfo);
    }

    private void execute() throws Exception {

        double currentTime = 0.0;

        AdvanceTimeRequest atr = new AdvanceTimeRequest( currentTime );
        putAdvanceTimeRequest( atr );

        readyToPopulate();
        readyToRun();

        startAdvanceTimeThread();

        InteractionRoot interactionRoot;

        while( true ) {
            currentTime += 1;

            atr.requestSyncStart();

            while(  ( interactionRoot = getNextInteractionNoWait() ) != null ) {
                if(!(interactionRoot instanceof ClientMessage)) {
                    continue;
                }

                ClientMessage message = (ClientMessage) interactionRoot;
                System.out.println( this.getFederateId() + ": Received ClientMessage interaction from " + message.get_originFed() );

                ServerReply reply = create_ServerReply();
                reply.set_sequenceNumber(message.get_sequenceNumber());
                reply.set_targetFed(message.get_originFed());

                reply.sendInteraction(getRTI(), currentTime);

                AdvanceTimeRequest newATR = new AdvanceTimeRequest( currentTime );
                putAdvanceTimeRequest( newATR );

                atr.requestSyncEnd();
                atr = newATR;
            }

            AdvanceTimeRequest newATR = new AdvanceTimeRequest( currentTime );
            putAdvanceTimeRequest( newATR );

            atr.requestSyncEnd();
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
