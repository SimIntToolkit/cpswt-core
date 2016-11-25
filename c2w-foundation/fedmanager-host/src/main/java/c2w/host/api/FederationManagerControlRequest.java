package c2w.host.api;

import c2w.hla.FederateState;
import com.fasterxml.jackson.annotation.JsonProperty;

public class FederationManagerControlRequest {
    @JsonProperty
    public ControlAction action;

    public FederateState getTargetState() {
        switch (this.action) {
            case START:
                return FederateState.STARTING;
            case PAUSE:
                return FederateState.PAUSED;
            case RESUME:
                return FederateState.RESUMED;
            case TERMINATE:
                return FederateState.TERMINATING;
            default:
                return FederateState.STARTING;
        }
    }
}
