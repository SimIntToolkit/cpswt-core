package c2w.host;

import c2w.hla.FederationManagerParameter;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import org.hibernate.validator.constraints.NotEmpty;

public class FederationManagerHostConfiguration extends Configuration {
    @NotEmpty
    FederationManagerParameter federationManagerParameter;

    @JsonProperty
    public FederationManagerParameter getFederationManagerParameter() {
        return this.federationManagerParameter;
    }
}
