package c2w.hla;

import java.util.HashMap;
import java.util.HashSet;

public enum FederationManagerState {
    /**
     * FM is initializing
     */
    INITIALIZING(0),

    /**
     * FM initialized, but didn't start
     */
    INITIALIZED(1),

    /**
     * FM is running after starting up
     */
    RUNNING(2),

    /**
     * FM is paused, after running
     */
    PAUSED(4),

    /**
     * FM is running again after PAUSED state
     */
    RESUMED(8),

    /**
     * FM not running anymore from external termination signal
     */
    TERMINATED(16),

    /**
     * FM not running anymore because run finished
     */
    FINISHED(32);

    private int value;
    FederationManagerState(int value) {
        this.value = value;
    }

    static HashMap<FederationManagerState, HashSet<FederationManagerState>> allowedTransitions;
    static {
        allowedTransitions = new HashMap<>();

        allowedTransitions.put(FederationManagerState.INITIALIZING, new HashSet<FederationManagerState>() {{
            add(FederationManagerState.INITIALIZED);
        }});
        allowedTransitions.put(FederationManagerState.INITIALIZED, new HashSet<FederationManagerState>() {{
            add(FederationManagerState.RUNNING);
            add(FederationManagerState.TERMINATED);
        }});
        allowedTransitions.put(FederationManagerState.RUNNING, new HashSet<FederationManagerState>() {{
            add(FederationManagerState.PAUSED);
            add(FederationManagerState.TERMINATED);
            add(FederationManagerState.FINISHED);
        }});
        allowedTransitions.put(FederationManagerState.PAUSED, new HashSet<FederationManagerState>() {{
            add(FederationManagerState.RESUMED);
            add(FederationManagerState.TERMINATED);
        }});
        allowedTransitions.put(FederationManagerState.RESUMED, new HashSet<FederationManagerState>() {{
            add(FederationManagerState.TERMINATED);
            add(FederationManagerState.FINISHED);
        }});
        allowedTransitions.put(FederationManagerState.TERMINATED, new HashSet<FederationManagerState>());
        allowedTransitions.put(FederationManagerState.FINISHED, new HashSet<FederationManagerState>());
    }

    public boolean CanTransitionTo(FederationManagerState toState) {
        return allowedTransitions.get(this.value).contains(toState);
    }
}
