package c2w.host.api;

import c2w.hla.FederationManagerState;
import com.fasterxml.jackson.annotation.JsonProperty;

public class StateMessage {
    FederationManagerState state;
    String success;
    String error;

    public StateMessage() {}
    public StateMessage(FederationManagerState state) {
        this.state = state;
    }

    public StateMessage(FederationManagerState state, String success, String error) {
        this.state = state;
        this.success = success;
        this.error = error;
    }

    @JsonProperty
    public FederationManagerState getState() {
        return this.state;
    }

    @JsonProperty
    public String getSuccess() { return this.success; }

    @JsonProperty
    public String getError() { return this.error; }
}
