package c2w.host.resources;

import c2w.hla.FederationManager;
import spark.Request;
import spark.Response;
import spark.Route;

/**
 * RESTful endpoint
 * "/fedmgr"
 */
public class FederationManagerController {

    private final FederationManager federationManager;

    public FederationManagerController(FederationManager federationManager) {
        this.federationManager = federationManager;
    }

    public Route getFederateState = (Request request, Response response) -> {
        //return federationManager.getFederateState();
        return null;
    };

    public Route changeFederateState = (Request request, Response response) -> {
        return null;
    };
}
