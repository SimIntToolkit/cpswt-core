package c2w.host.api;

import c2w.hla.FederateState;
import com.fasterxml.jackson.annotation.JsonProperty;

public class StateMessage {
    FederateState state;
    String error;

    public StateMessage() {}
    public StateMessage(FederateState state) {
        this.state = state;
    }

    public StateMessage(FederateState state, String error) {
        this.state = state;
        this.error = error;
    }

    @JsonProperty
    public FederateState getState() {
        return this.state;
    }

    @JsonProperty
    public String getError() { return this.error; }
}
