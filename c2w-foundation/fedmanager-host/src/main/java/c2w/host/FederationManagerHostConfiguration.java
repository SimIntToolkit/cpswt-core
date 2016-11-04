package c2w.host;

import c2w.hla.FederationManagerParameter;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;

public class FederationManagerHostConfiguration extends Configuration {
    @JsonProperty
    FederationManagerParameter federationManagerParameter;

    @JsonProperty
    public FederationManagerParameter getFederationManagerParameter() {
        return this.federationManagerParameter;
    }
}
