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
 * 
 * @author Himanshu Neema
 */

package org.cpswt.util;

import hla.rti.FederationExecutionAlreadyExists;
import hla.rti.LogicalTime;
import hla.rti.RTIambassador;
import hla.rti.RTIinternalError;
import hla.rti.ResignAction;
import hla.rti.jlc.NullFederateAmbassador;
import hla.rti.jlc.RtiFactory;
import hla.rti.jlc.RtiFactoryFactory;

import java.io.File;

import org.portico.impl.hla13.types.DoubleTime;
import org.portico.impl.hla13.types.DoubleTimeInterval;

/**
 * Test class to measure performance of rti.tick() method.
 * 
 * @author Himanshu Neema
 */
public class RTITest
        extends NullFederateAmbassador {

    DoubleTime time = new DoubleTime(0);

    RTIambassador rti;

    boolean running = false;

    boolean timeRegulationEnabled = false;

    boolean timeConstrainedEnabled = false;

    boolean granted = false;

    public RTITest(final String FED_file_name, final String federation_name,
            final String federate_name) throws Exception {
        System.setProperty("portico.lrc.connection",
                "org.portico.binding.jsop.lrc.JSOPClientConnection");
        RtiFactory factory = RtiFactoryFactory
                .getRtiFactory("org.portico.dlc.HLA13RTIFactory");

        {
        	boolean rtiNotPresent = true;
	       	while( rtiNotPresent ) {
		   		try {
		   	        rti = factory.createRtiAmbassador();
	        		rtiNotPresent = false;
				} catch ( RTIinternalError rtiInternalError ) {   			
		   			Thread.sleep( 1000 );
		   		}
	       	}
        }

        File fom_file = new File( FED_file_name );

   		try {   	        
    		rti.createFederationExecution( federation_name, fom_file.toURL() );
   		} catch ( FederationExecutionAlreadyExists feae ) {
   		}

   		rti.joinFederationExecution(federate_name, federation_name, this, null);
        
        run(federation_name);
    }

    private void run(final String federation_name) throws Exception {
        // Initialize to time zero
        time.setTime(0);

        // PER THE HLA BOOK, ENABLE TIME-CONSTRAINED FIRST, THEN TIME-REGULATING
        timeConstrainedEnabled = false;
        rti.enableTimeConstrained();
        while ( !timeConstrainedEnabled ) rti.tick();

        timeRegulationEnabled = false;
        rti.enableTimeRegulation(  time, new DoubleTimeInterval( 0.1 )  );
        while ( !timeRegulationEnabled ) rti.tick();

        // Update current federate time
        DoubleTime curTime = new DoubleTime(0);
        curTime.setTo(rti.queryFederateTime());
        time.setTime(curTime.getTime());

        // Keep calling rti.tick() in a spearate thread
        Thread t = new Thread() {
            public void run() {
                long count = 1;
                long totalTime = 0;
                long maxTotalTime = 0;
                long maxAnySingleCall = 0;
                try {
                    while (running) {
                        synchronized (rti) {
                            // DoubleTime next_time = new DoubleTime(time
                            // .getTime() + 0.1);
                            // rti.timeAdvanceRequest(next_time);

                            // wait for grant
                            // granted = false;
                            // while (!granted && running) {
                            // rti.tick();
                            // }
                            long nextTime = 0;
                            long prevTime = 0;
                            prevTime = System.currentTimeMillis();
                            rti.tick();
                            nextTime = System.currentTimeMillis();
                            long diff = nextTime - prevTime;
                            totalTime = totalTime + diff;
                            if ((count % 1000) == 0) {
                                if (totalTime > maxTotalTime) {
                                    maxTotalTime = totalTime;
                                }
                                System.out
                                        .print("Last 1000 calls to rti.tick() took: "
                                                + totalTime + " milliseconds");
                                count = 1;
                                totalTime = 0;
                            } else {
                                count++;
                            }
                            if (diff > maxAnySingleCall) {
                                maxAnySingleCall = diff;
                            }
                            System.out.println("Last rti.tick() call took: "
                                    + diff + " milliseconds");
                            System.out
                                    .println("Max time per 1000 rti.tick() calls so far = "
                                            + maxTotalTime + " milliseconds");
                            System.out
                                    .println("Max time so far for a single rti.tick() = "
                                            + maxAnySingleCall
                                            + " milliseconds");

                        }
                    }

                    // destroy federateion
                    rti.resignFederationExecution(ResignAction.DELETE_OBJECTS);
                    rti.destroyFederationExecution(federation_name);
                    rti = null;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        running = true;
        t.start();

    }

    public void timeRegulationEnabled(LogicalTime t) {
        timeRegulationEnabled = true;
    }

    public void timeConstrainedEnabled(LogicalTime t) {
        timeConstrainedEnabled = true;
    }

    public void timeAdvanceGrant(LogicalTime t) {
        DoubleTime curTime = new DoubleTime(0);
        curTime.setTo(t);
        time.setTime(curTime.getTime());
        granted = true;
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 3) {
            System.out
                    .println("Usage: RTITest <FED_file_name> <federation_name> <federate_name>");
            System.exit(-1);
        }
        new RTITest(args[0], args[1], args[2]);
    }
}
