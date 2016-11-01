package c2w.host.api;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ControlMessage {

    private long id;
    private String auth;
    private String action;

    public ControlMessage() {

    }

    public ControlMessage(long id, String auth, String action) {
        this.id = id;
        this.auth = auth;
        this.action = action;
    }

    @JsonProperty
    public long getId() {
        return id;
    }

    @JsonProperty
    public String getAuth() {
        return auth;
    }

    @JsonProperty
    public String getAction() {
        return action;
    }
}
