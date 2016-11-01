package c2w.hla;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public enum FederationManagerState {
    /**
     * FM initialized, but didn't start
     */
    INITIALIZED(1),

    /**
     * FM is running. Either "started" or "resumed"
     */
    RUNNING(2),

    /**
     * FM is paused, after running
     */
    PAUSED(4),

    /**
     * FM not running anymore from external termination signal
     */
    TERMINATED(8),

    /**
     * FM not running anymore because run finished
     */
    FINISHED(16);

    private short value;
    FederationManagerState(short value) {
        this.value = value;
    }

    static HashMap<FederationManagerState, Set<FederationManagerState>> allowedTransitions;
    static {
        allowedTransitions = new HashMap<FederationManagerState, Set<FederationManagerState>>();

        allowedTransitions.put(FederationManagerState.INITIALIZED, new HashSet<FederationManagerState>() {{
            add(FederationManagerState.RUNNING);
        }});
        allowedTransitions.put(FederationManagerState.RUNNING, new HashSet<FederationManagerState>() {{
            add(FederationManagerState.PAUSED);
            add(FederationManagerState.TERMINATED);
            add(FederationManagerState.FINISHED);
        }});
        allowedTransitions.put(FederationManagerState.PAUSED, new HashSet<FederationManagerState>() {{
            add(FederationManagerState.RUNNING);
            add(FederationManagerState.TERMINATED);
        }});
        allowedTransitions.put(FederationManagerState.TERMINATED, new HashSet<FederationManagerState>());
        allowedTransitions.put(FederationManagerState.FINISHED, new HashSet<FederationManagerState>());
    }

    public boolean CanTransitionTo(FederationManagerState toState) {
        return allowedTransitions.get(this.value).contains(toState);
    }
}
