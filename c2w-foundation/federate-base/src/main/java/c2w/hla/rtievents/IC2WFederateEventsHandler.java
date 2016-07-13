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

package c2w.hla.rtievents;

/**
 * Very simple interface to register a handler for federate events.
 * 
 * @author Himanshu Neema
 */
public interface IC2WFederateEventsHandler {
	
	public static enum C2W_FEDERATE_EVENTS {
		FEDERATE_AMBASSADOR_CREATED("FEDERATE_AMBASSADOR_CREATED"),
		JOINED_FEDERATION("JOINED_FEDERATION"),
		READY_TO_RUN("READY_TO_RUN"),
		RUNNING("RUNNING"),
		READY_TO_RESIGN("READY_TO_RESIGN"),
		RESIGNED("RESIGNED"),
		READY_TO_EXIT("READY_TO_EXIT");
		
		private String _name;

		C2W_FEDERATE_EVENTS(String name) {
            this._name = name;
        }

        @Override
        public String toString() {
            return _name;
        }
    }
    
    /**
     * This method is called whenever one of the defined federate level event occurs.
     * 
     * @param federateEvent [The federate event that occurred]
     * @param message [Filled with detailed event message, if any]
     */
    void handleEvent( C2W_FEDERATE_EVENTS federateEvent, String message );
}
