package c2w.host.api;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonWriter;
import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;
import java.io.IOException;
import java.io.Writer;

/**
 * Json Encoder to send JSON objects through web sockets
 */
public class JsonEncoder implements Encoder.TextStream<JsonObject> {
    public void encode(JsonObject jsonObject, Writer writer) throws EncodeException, IOException {
        try (JsonWriter jsonWriter = Json.createWriter(writer)) {
            jsonWriter.writeObject(jsonObject);
        }
    }

    public void init(EndpointConfig endpointConfig) {}

    public void destroy() {}
}
