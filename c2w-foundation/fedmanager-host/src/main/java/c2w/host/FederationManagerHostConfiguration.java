package c2w.host;

import c2w.hla.FederationManagerConfig;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;

public class FederationManagerHostConfiguration extends Configuration {
    @JsonProperty
    FederationManagerConfig federationManagerParameter;

    @JsonProperty
    public FederationManagerConfig getFederationManagerParameter() {
        return this.federationManagerParameter;
    }
}
