package org.cpswt.host.api;

import org.cpswt.hla.FederateState;
import com.fasterxml.jackson.annotation.JsonProperty;

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
