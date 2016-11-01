package c2w.host.api;

import c2w.hla.FederationManagerState;
import com.fasterxml.jackson.annotation.JsonProperty;

public class State {
    private FederationManagerState currentState;

    public State() {}
    public State(FederationManagerState currentState) {
        this.currentState = currentState;
    }

    @JsonProperty
    public FederationManagerState getCurrentState() {
        return currentState;
    }
}
