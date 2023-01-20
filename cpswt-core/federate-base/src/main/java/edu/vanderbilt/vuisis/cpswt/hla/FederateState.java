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

package edu.vanderbilt.vuisis.cpswt.hla;

import java.util.HashMap;
import java.util.HashSet;

public enum FederateState {
    /**
     * Federate is initializing
     */
    INITIALIZING(1),

    /**
     * Federate initialized, but didn't start
     */
    INITIALIZED(2),

    /**
     * Federate is starting up.
     */
    STARTING(4),

    /**
     * Federate is running after successful startup
     */
    RUNNING(8),

    /**
     * Federate is paused, after running
     */
    PAUSED(16),

    /**
     * Federate is running again after PAUSED state
     */
    RESUMED(32),

    /**
     * Federate is terminating (not running anymore) from external termination signal
     */
    TERMINATING(64),

    /**
     * Federate finished with terminating (all cleanup code should have finished)
     */
    TERMINATED(128),

    /**
     * Federate not running anymore because run finished
     */
    FINISHED(256);

    private int value;
    FederateState(int value) {
        this.value = value;
    }

    static HashMap<FederateState, HashSet<FederateState>> allowedTransitions;
    static {
        allowedTransitions = new HashMap<FederateState, HashSet<FederateState>>();

        allowedTransitions.put(FederateState.INITIALIZING, new HashSet<FederateState>() {{
            add(FederateState.INITIALIZED);
        }});
        allowedTransitions.put(FederateState.INITIALIZED, new HashSet<FederateState>() {{
            add(FederateState.STARTING);
            add(FederateState.TERMINATING);
        }});
        allowedTransitions.put(FederateState.STARTING, new HashSet<FederateState>() {{
            add(FederateState.RUNNING);
            add(FederateState.TERMINATING);
        }});
        allowedTransitions.put(FederateState.RUNNING, new HashSet<FederateState>() {{
            add(FederateState.PAUSED);
            add(FederateState.TERMINATING);
            add(FederateState.FINISHED);
        }});
        allowedTransitions.put(FederateState.PAUSED, new HashSet<FederateState>() {{
            add(FederateState.RESUMED);
            add(FederateState.TERMINATING);
        }});
        allowedTransitions.put(FederateState.RESUMED, new HashSet<FederateState>() {{
            add(FederateState.PAUSED);
            add(FederateState.TERMINATING);
            add(FederateState.FINISHED);
        }});
        allowedTransitions.put(FederateState.TERMINATING, new HashSet<FederateState>() {{
            add(FederateState.TERMINATED);
        }});
        allowedTransitions.put(FederateState.TERMINATED, new HashSet<FederateState>());
        allowedTransitions.put(FederateState.FINISHED, new HashSet<FederateState>());
    }

    public boolean CanTransitionTo(FederateState toState) {
        return allowedTransitions.get(this).contains(toState);
    }
}
