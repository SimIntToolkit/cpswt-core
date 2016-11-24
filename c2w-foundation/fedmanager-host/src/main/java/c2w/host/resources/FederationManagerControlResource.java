package c2w.host.resources;

import c2w.hla.FederationManager;
import c2w.hla.FederateState;
import c2w.host.api.FederationManagerControlMessage;
import c2w.host.api.StateMessage;
import com.codahale.metrics.annotation.Timed;
import org.glassfish.jersey.server.ChunkedOutput;
import org.glassfish.jersey.server.ManagedAsync;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.plaf.nimbus.State;
import javax.ws.rs.*;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.MediaType;

@Path("/fedmgr")
@Produces(MediaType.APPLICATION_JSON)
public class FederationManagerControlResource {

    private final FederationManager federationManager;
    Logger logger = LoggerFactory.getLogger(FederationManagerControlResource.class);

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
    @Produces(MediaType.APPLICATION_JSON)
    @ManagedAsync
    public ChunkedOutput<String> setNewState(
            //@QueryParam("target_state")
            final FederationManagerControlMessage msg
            //@Suspended final AsyncResponse response
    ) {
        FederateState currentState = this.federationManager.getFederateState();
        FederateState targetState = msg.targetState;

        final ChunkedOutput<String> output = new ChunkedOutput<String>(String.class);

        if (currentState.CanTransitionTo(targetState)) {
            try {
                switch (targetState) {
                    case RUNNING:
                        //this.federationManager.startSimulation();
                        return this.startSimulationAsync(output);
                        //break;
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
                //return new StateMessage(currentState, "There was an error while transitioning to " + targetState + " state. \n" + ex.getMessage());
            }
            //return new StateMessage(targetState);
        } else {
            //return new StateMessage(currentState, "FederationManager can transition from " + currentState + " state to " + targetState);
        }

        return output;
    }

    private ChunkedOutput<String> startSimulationAsync(final ChunkedOutput<String> output) {
        new Thread(new Runnable() {
            public void run() {
                try {
                    output.write(FederateState.STARTING.toString());
                    federationManager.startSimulation();
                    output.write(FederateState.RUNNING.toString());
                    output.close();
                }
                catch(Exception ex) {
                    // output.write(new StateMessage(FederateState.STARTING, "There was an error while starting simulation: " + ex.getMessage()));
                    logger.error("There was an error while starting the simulation", ex);
                }
            }
        }).start();
        return output;
    }
}
