package c2w.hla;

import java.util.HashMap;
import java.util.HashSet;

public enum FederateState {
    /**
     * Federate is initializing
     */
    INITIALIZING(0),

    /**
     * Federate initialized, but didn't start
     */
    INITIALIZED(1),

    /**
     * Federate is running after starting up
     */
    RUNNING(2),

    /**
     * Federate is paused, after running
     */
    PAUSED(4),

    /**
     * Federate is running again after PAUSED state
     */
    RESUMED(8),

    /**
     * Federate not running anymore from external termination signal
     */
    TERMINATED(16),

    /**
     * Federate not running anymore because run finished
     */
    FINISHED(32);

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
            add(FederateState.RUNNING);
            add(FederateState.TERMINATED);
        }});
        allowedTransitions.put(FederateState.RUNNING, new HashSet<FederateState>() {{
            add(FederateState.PAUSED);
            add(FederateState.TERMINATED);
            add(FederateState.FINISHED);
        }});
        allowedTransitions.put(FederateState.PAUSED, new HashSet<FederateState>() {{
            add(FederateState.RESUMED);
            add(FederateState.TERMINATED);
        }});
        allowedTransitions.put(FederateState.RESUMED, new HashSet<FederateState>() {{
            add(FederateState.TERMINATED);
            add(FederateState.FINISHED);
        }});
        allowedTransitions.put(FederateState.TERMINATED, new HashSet<FederateState>());
        allowedTransitions.put(FederateState.FINISHED, new HashSet<FederateState>());
    }

    public boolean CanTransitionTo(FederateState toState) {
        return allowedTransitions.get(this).contains(toState);
    }
}
