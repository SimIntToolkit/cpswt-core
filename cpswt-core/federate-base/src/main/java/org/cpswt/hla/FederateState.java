package org.cpswt.hla;

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
