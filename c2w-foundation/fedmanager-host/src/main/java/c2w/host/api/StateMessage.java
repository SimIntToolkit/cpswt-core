package c2w.host.api;

import c2w.hla.FederateState;
import com.fasterxml.jackson.annotation.JsonProperty;

public class StateMessage {
    FederateState state;
    String success;
    String error;

    public StateMessage() {}
    public StateMessage(FederateState state) {
        this.state = state;
    }

    public StateMessage(FederateState state, String success, String error) {
        this.state = state;
        this.success = success;
        this.error = error;
    }

    @JsonProperty
    public FederateState getState() {
        return this.state;
    }

    @JsonProperty
    public String getSuccess() { return this.success; }

    @JsonProperty
    public String getError() { return this.error; }
}
