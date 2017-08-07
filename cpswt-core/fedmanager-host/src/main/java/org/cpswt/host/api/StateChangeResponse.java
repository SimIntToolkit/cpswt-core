package org.cpswt.host.api;

import org.cpswt.hla.FederateState;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents the JSON data in response for a federation manager state change request.
 */
public class StateChangeResponse {

    FederateState prevState;
    FederateState newState;
    String message;

    public StateChangeResponse() {}

    public StateChangeResponse(FederateState prevState, FederateState newState) {
        this.prevState = prevState;
        this.newState = newState;
    }

    public StateChangeResponse(FederateState prevState, FederateState newState, String message) {
        this(prevState, newState);
        this.message = message;
    }

    @JsonProperty
    public FederateState getPrevState() {
        return this.prevState;
    }

    @JsonProperty
    public FederateState getNewState() {
        return this.newState;
    }

    @JsonProperty
    public String getMessage() { return this.message; }
}
