/*
 * Copyright (c) 2008, Institute for Software Integrated Systems, Vanderbilt University
 * All rights reserved.
 *
 * Permission to use, copy, modify, and distribute this software and its
 * documentation for any purpose, without fee, and without written agreement is
 * hereby granted, provided that the above copyright notice, the following
 * two paragraphs and the author appear in all copies of this software.
 *
 * IN NO EVENT SHALL THE VANDERBILT UNIVERSITY BE LIABLE TO ANY PARTY FOR
 * DIRECT, INDIRECT, SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES ARISING OUT
 * OF THE USE OF THIS SOFTWARE AND ITS DOCUMENTATION, EVEN IF THE VANDERBILT
 * UNIVERSITY HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * THE VANDERBILT UNIVERSITY SPECIFICALLY DISCLAIMS ANY WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY
 * AND FITNESS FOR A PARTICULAR PURPOSE.  THE SOFTWARE PROVIDED HEREUNDER IS
 * ON AN "AS IS" BASIS, AND THE VANDERBILT UNIVERSITY HAS NO OBLIGATION TO
 * PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, OR MODIFICATIONS.
 */

package HelloWorld;

import c2w.hla.InteractionRoot;

public class Sink3 extends Sink3Base {

    public Sink3(String args[] ) throws Exception {
        super( args );
    }

    private void execute() throws Exception {

        double currentTime = 0;
        double timeOrderOffsetIncrement = 0.00001;

        AdvanceTimeRequest atr = new AdvanceTimeRequest( currentTime );
        putAdvanceTimeRequest( atr );

        readyToPopulate();
        readyToRun();

        startAdvanceTimeThread();

        InteractionRoot interactionRoot;

//        PingCount pingCount = new PingCount();
//        pingCount.set_SinkName( "Sink2" );
//        pingCount.set_RunningCount( 0 );
//
//        pingCount.registerObject( getRTI() );
//        pingCount.updateAttributeValues( getRTI(), currentTime + getLookahead() );

        while( true ) {
//            double timeOrderOffset = 0;
            currentTime += 1;

            atr.requestSyncStart();

            while(  ( interactionRoot = getNextInteractionNoWait() ) != null ) {
                if ( interactionRoot instanceof Ping3 ) {
                    Ping3 ping = (Ping3) interactionRoot;
                    System.out.println("" + this.getFederateId() + ": Received Ping3 interaction #" + ping.get_Count() + " from: " + ping.get_originFed());
                } else if ( interactionRoot instanceof Ping2 ) {
                    Ping2 ping = (Ping2) interactionRoot;
                    System.out.println("" + this.getFederateId() + ": Received Ping2 interaction #" + ping.get_Count() + " from: " + ping.get_originFed());
                } else if ( interactionRoot instanceof Ping1 ) {
                    Ping1 ping = (Ping1) interactionRoot;
                    System.out.println("" + this.getFederateId() + ": Received Ping1 interaction #" + ping.get_Count() + " from: " + ping.get_originFed());
                }
//
            }

            AdvanceTimeRequest newATR = new AdvanceTimeRequest( currentTime );
            putAdvanceTimeRequest( newATR );

            atr.requestSyncEnd();
            atr = newATR;
        }

    }

    public static void main( String[] args ) {
        try {
            Sink3 sink = new Sink3( args );
            sink.execute();
        } catch ( Exception e ) {
            System.err.println( "Exception caught: " + e.getMessage() );
            e.printStackTrace();
        }
    }
}
