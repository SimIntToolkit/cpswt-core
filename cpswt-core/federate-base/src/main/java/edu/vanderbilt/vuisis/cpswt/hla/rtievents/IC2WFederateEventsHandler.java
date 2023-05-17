/*
 * Certain portions of this software are Copyright (C) 2006-present
 * Vanderbilt University, Institute for Software Integrated Systems.
 *
 * Certain portions of this software are contributed as a public service by
 * The National Institute of Standards and Technology (NIST) and are not
 * subject to U.S. Copyright.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above Vanderbilt University copyright notice, NIST contribution
 * notice and this permission and disclaimer notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE. THE AUTHORS OR COPYRIGHT HOLDERS SHALL NOT HAVE
 * ANY OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS,
 * OR MODIFICATIONS.
 */

package edu.vanderbilt.vuisis.cpswt.hla.rtievents;

/**
 * Very simple interface to register a handler for federate events.
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
