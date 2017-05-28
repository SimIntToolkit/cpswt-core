package EchoExample;

import c2w.hla.base.AdvanceTimeRequest;

public class EchoClient extends EchoClientBase {

    public EchoClient(String[] federationInfo) throws Exception {
        super(federationInfo);
    }

    private final int echoMessageCount = 500;

    private void execute() throws Exception {

        double currentTime = 0;

        AdvanceTimeRequest atr = new AdvanceTimeRequest( currentTime );
        putAdvanceTimeRequest( atr );

        readyToPopulate();
        readyToRun();

        startAdvanceTimeThread();

        int ix = 0;
        while( true ) {
            ClientMessage message = create_ClientMessage();
            currentTime += 1;

            atr.requestSyncStart();

            System.out.println( this.getFederateId() + ": Sending echo message interaction #" + ix );
            message.sendInteraction( getRTI(), currentTime );

            AdvanceTimeRequest newATR = new AdvanceTimeRequest( currentTime );
            putAdvanceTimeRequest( newATR );

            atr.requestSyncEnd();
            atr = newATR;

            ++ix;

            if(ix > this.echoMessageCount) {
                break;
            }
        }

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
