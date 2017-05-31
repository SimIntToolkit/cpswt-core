package EchoExample;

import c2w.hla.base.AdvanceTimeRequest;

import java.util.HashSet;
import java.util.Set;

public class EchoClient extends EchoClientBase {

    public EchoClient(String[] federationInfo) throws Exception {
        super(federationInfo);
    }

    private final int echoMessageCount = 500;
    int sequenceNumber = 0;
    Set<Integer> sentSequenceNumbers = new HashSet<Integer>();

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

            this.sequenceNumber++;

            message.set_sequenceNumber(this.sequenceNumber);
            currentTime += 1;

            atr.requestSyncStart();

            System.out.println( this.getFederateId() + ": Sending echo message interaction #" + ix );
            message.sendInteraction( getRTI(), currentTime );

            // store sent sequenceNumber
            sentSequenceNumbers.add(this.sequenceNumber);

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
