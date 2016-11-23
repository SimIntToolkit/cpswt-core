package c2w.host.resources;

import c2w.hla.FederationManager;
import c2w.hla.FederateState;
import c2w.host.api.FederationManagerControlMessage;
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
        StateMessage current = new StateMessage(this.federationManager.getFederateState());
        return current;
    }

    @POST
    @Timed
    @Consumes(MediaType.APPLICATION_JSON)
    public StateMessage setNewState(
            //@QueryParam("target_state")
            FederationManagerControlMessage msg) {
        FederateState currentState = this.federationManager.getFederateState();
        FederateState targetState = msg.targetState;

        if (currentState.CanTransitionTo(targetState)) {
            try {

                switch (targetState) {
                    case RUNNING:
                        this.federationManager.startSimulation();
                        break;
                    case PAUSED:
                        this.federationManager.pauseSimulation();
                        break;
                    case RESUMED:
                        this.federationManager.resumeSimulation();
                        break;
                    case TERMINATED:
                        this.federationManager.terminateSimulation();
                        break;
                }
                // make sure state changed
                if(this.federationManager.getFederateState() == targetState) {

                }
            }
            catch (Exception ex) {
                return new StateMessage(currentState, "There was an error while transitioning to " + targetState + " state. \n" + ex.getMessage());
            }
            return new StateMessage(targetState);
        } else {
            return new StateMessage(currentState, "FederationManager can transition from " + currentState + " state to " + targetState);
        }
    }
}
