package c2w.host.util;

import c2w.hla.FederationManagerConfig;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents the host configuration.
 */
public class Config {

    @JsonProperty
    public FederationManagerConfig federationManagerParameter;

    @JsonProperty
    public Server server;

    @JsonProperty
    public boolean debug;

    public static class Server {

        public int port;
        public String host;
        public ServerType type;
        public String baseUrlPath;
        public String restfulEndpoint;
        public String websocketEndpoint;

        public enum ServerType {
            http,
            https;
        }
    }
}
