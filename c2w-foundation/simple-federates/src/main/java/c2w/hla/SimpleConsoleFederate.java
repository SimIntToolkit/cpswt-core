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
 * @author Gyorgy Balogh
 */

package c2w.hla;

import hla.rti.ResignAction;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import c2w.hla.SynchronizedFederate;

public class SimpleConsoleFederate extends SynchronizedFederate 
{
    protected String        federation_id;
    protected String        federate_id;

    protected boolean callback_thread_running;

    public SimpleConsoleFederate( String federation_id, String federate_id )
            throws Exception {
        this.federation_id = federation_id;
        this.federate_id = federate_id;

        createRTI();
        
        joinFederation( federation_id, federate_id );
    }
    
    protected void start_callback_thread() {

    	try {
    		// ALL PUBLISHING/SUBSCRIBING SHOULD BE COMPLETE, SO "ReadyToPopulate"
    		readyToPopulate();
    		// THERE DOESN'T APPEAR TO BE ANY INITIAL PUBLICATIONS OF DATA, SO "ReadyToRun"
    		readyToRun();
    	} catch ( Exception e ) {
            e.printStackTrace();
    	}
    	
    	// create a separate thread for callbacks
        Thread t = new Thread() {
            public void run() {
                while (callback_thread_running) {
                    try {
                        synchronized ( getRTI() ) {
                            if (callback_thread_running)
                                getRTI().tick();
                        }
                        Thread.sleep(10);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                
                try {
                    // "ReadyToResign"
                    readyToResign();
                    getRTI().resignFederationExecution(ResignAction.DELETE_OBJECTS);
                    getRTI().tick();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                System.out.println("finished");
            }
        };

        callback_thread_running = true;
        t.start();
    }

    protected void exit() {
        callback_thread_running = false;
    }

    protected void processCommand(String command) throws Exception {
    }

    protected void execute() throws Exception {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            System.out.print(">");
            String line = in.readLine();
            if (line != null && line.compareTo("exit") == 0) {
                exit();
                break;
            } else
                processCommand(line);
        }
    }
}
