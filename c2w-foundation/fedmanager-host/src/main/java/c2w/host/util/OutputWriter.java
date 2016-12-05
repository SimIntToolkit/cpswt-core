package c2w.host.util;

import c2w.host.api.StateChangeResponse;
import c2w.host.api.StateResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import spark.Response;

import javax.websocket.Session;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Helper class to write to the proper output
 */
public class OutputWriter {

    Session session;
    Response response;

    public OutputWriter(Session session) {
        this.session = session;
    }

    public OutputWriter(Response response) {
        this.response = response;
    }

    public void write(StateResponse stateResponse) throws IOException {
        if (this.session != null) {
            this.session.getAsyncRemote().sendObject(stateResponse);
        }

        if (this.response != null) {
            ObjectMapper mapper = new ObjectMapper();
            String jsonString = mapper.writeValueAsString(stateResponse);
            final byte[] content = jsonString.getBytes();
            final OutputStream output = this.response.raw().getOutputStream();
            output.write(content);
            output.close();
        }
    }

    public void write(StateChangeResponse stateChangeResponse) throws IOException {
        this.write(stateChangeResponse, true);
    }

    public void write(StateChangeResponse stateChangeResponse, boolean close) throws IOException {
        if (this.session != null) {
            this.session.getAsyncRemote().sendObject(stateChangeResponse);
        }

        if (this.response != null) {
            ObjectMapper mapper = new ObjectMapper();
            String jsonString = mapper.writeValueAsString(stateChangeResponse);
            final byte[] content = jsonString.getBytes();
            final OutputStream output = this.response.raw().getOutputStream();
            output.write(content);

            if (close) {
                output.close();
            }
        }
    }
}
