package c2w.host.api;

import c2w.hla.FederateState;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.websocket.EncodeException;
import javax.websocket.EndpointConfig;
import java.io.IOException;
import java.io.Writer;

/**
 * Represents the JSON data in response for a federation manager state change request.
 */
public class StateChangeResponse {
    private final static ObjectMapper mapper = new ObjectMapper();

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

    public static class Encoder implements javax.websocket.Encoder.TextStream<StateChangeResponse> {

        @Override
        public void encode(StateChangeResponse stateChangeResponse, Writer writer) throws EncodeException, IOException {
            writer.write(mapper.writeValueAsString(stateChangeResponse));
        }

        @Override
        public void init(EndpointConfig endpointConfig) {}

        @Override
        public void destroy() {}
    }
}
