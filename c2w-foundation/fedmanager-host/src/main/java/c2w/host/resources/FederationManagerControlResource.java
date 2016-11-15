package c2w.host.resources;

import c2w.hla.FederationManager;
import c2w.hla.FederationManagerState;
import c2w.host.api.StateMessage;
import com.codahale.metrics.annotation.Timed;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/fedmgr")
@Produces(MediaType.APPLICATION_JSON)
public class FederationManagerControlResource {

    private final FederationManager federationManager;
    public FederationManagerControlResource(FederationManager federationManager) {
            this.federationManager = federationManager;
    }

    @GET
    @Timed
    public StateMessage getCurrentState() {
        StateMessage current = new StateMessage(this.federationManager.getCurrentState());
        return current;
    }

    @POST
    @Timed
    public StateMessage setNewState(
            @QueryParam("target_state")
            FederationManagerState targetState
    ) {
        FederationManagerState currentState = this.federationManager.getCurrentState();

        if(currentState.CanTransitionTo(targetState)) {
            // this.federationManager.setNewState(targetState);
        }

        StateMessage m = new StateMessage(targetState);
        return m;
    }
}
