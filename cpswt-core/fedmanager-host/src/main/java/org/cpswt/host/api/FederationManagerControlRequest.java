package org.cpswt.host.api;

import com.fasterxml.jackson.annotation.JsonProperty;

public class FederationManagerControlRequest {
    @JsonProperty
    public ControlAction action;
}
