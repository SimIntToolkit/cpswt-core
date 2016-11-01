package c2w.host;

import c2w.hla.FedMgr;
import c2w.hla.FederationManagerState;
import c2w.host.api.State;
import com.codahale.metrics.annotation.Timed;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/fedmgr-control")
@Produces(MediaType.APPLICATION_JSON)
public class FederationManagerControlResource {

    private final FedMgr federationManager;

    public FederationManagerControlResource(FedMgr federationManager) {
        this.federationManager = federationManager;
    }

    @GET
    @Timed
    public State getCurrentState() {
        State current = new State(this.federationManager.getCurrentState());
        return current;
    }

    @POST
    @Timed
    public State setNewState(
            @QueryParam("action")
            FederationManagerState toState
    ) {
        FederationManagerState currentState = this.federationManager.getCurrentState();

        if(currentState.CanTransitionTo(toState)) {
            // ....
        }
    }
}
