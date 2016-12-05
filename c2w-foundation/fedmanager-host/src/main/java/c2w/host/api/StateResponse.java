package c2w.host.api;

import c2w.hla.FederateState;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.websocket.EncodeException;
import javax.websocket.EndpointConfig;
import java.io.IOException;
import java.io.Writer;

/**
 * Represents the JSON data in response for a federation manager get state request.
 */
public class StateResponse {

    private static final ObjectMapper mapper = new ObjectMapper();
    private FederateState currentState;

    @JsonProperty
    public FederateState getCurrentState() {
        return this.currentState;
    }

    public StateResponse() {}

    public StateResponse(FederateState currentState) {
        this.currentState = currentState;
    }

    /**
     * Encoder for web socket sendObject.
     */
    public static class Encoder implements javax.websocket.Encoder.TextStream<StateResponse> {
        @Override
        public void encode(StateResponse stateResponse, Writer writer) throws EncodeException, IOException {
            writer.write(mapper.writeValueAsString(stateResponse));
        }

        @Override
        public void init(EndpointConfig endpointConfig) {
        }

        @Override
        public void destroy() {
        }
    }
}
