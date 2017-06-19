package c2w.host.api;

import c2w.hla.FederateState;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Represents the JSON data in response for a federation manager get state request.
 */
public class StateResponse {

    private FederateState currentState;

    @JsonProperty
    public FederateState getCurrentState() {
        return this.currentState;
    }

    public StateResponse() {}

    public StateResponse(FederateState currentState) {
        this.currentState = currentState;
    }


}
