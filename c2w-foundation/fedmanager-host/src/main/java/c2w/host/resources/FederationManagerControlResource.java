package c2w.host.resources;

import c2w.hla.FederationManager;
import c2w.hla.FederateState;
import c2w.host.api.ControlAction;
import c2w.host.api.FederationManagerControlRequest;
import c2w.host.api.StateChangeResponse;
import c2w.host.api.StateResponse;
import c2w.host.core.FederationManagerContainer;
import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Metered;
import com.codahale.metrics.annotation.Timed;
import org.glassfish.jersey.server.ChunkedOutput;
import org.glassfish.jersey.server.ManagedAsync;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.IOException;

@Metered
@Timed
@ExceptionMetered
@Path("/fedmgr")
public class FederationManagerControlResource {

    private FederationManager federationManager;
    private Logger logger = LoggerFactory.getLogger(FederationManagerControlResource.class);

    public FederationManagerControlResource() {}

    public void initFederationManager() {
        this.federationManager = FederationManagerContainer.getInstance();
        /*
        if(this.federationManager.getAutoStart()) {
            try {
                this.federationManager.startSimulation();
            }
            catch(Exception e) {
                logger.error("There was an error auto starting federation manager", e);
            }
        }
        */
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public StateResponse getCurrentState() {
        return new StateResponse(this.federationManager.getFederateState());
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @ManagedAsync
    public ChunkedOutput<StateChangeResponse> setNewState(
            final FederationManagerControlRequest controlRequest
    ) {
        FederateState currentState = this.federationManager.getFederateState();
        ControlAction action = controlRequest.action;
        FederateState targetState = action.getTargetState();

        final ChunkedOutput<StateChangeResponse> output = new ChunkedOutput<StateChangeResponse>(StateChangeResponse.class);

        if (currentState.CanTransitionTo(targetState)) {
            try {
                switch (action) {
                    case START:
                        this.startSimulationAsync(output);
                        break;
                    case PAUSE:
                        this.federationManager.pauseSimulation();
                        output.write(new StateChangeResponse(currentState, federationManager.getFederateState()));
                        output.close();
                        break;
                    case RESUME:
                        this.federationManager.resumeSimulation();
                        output.write(new StateChangeResponse(currentState, federationManager.getFederateState()));
                        output.close();
                        break;
                    case TERMINATE:
                        this.terminateSimulationAsync(output);
                        break;
                }
            }
            catch(IOException ioEx) {
                logger.error("Closing ChunkedOutput encountered a problem.", ioEx);
            }
            catch (Exception ex) {
                logger.error("There was an error while trying to transition FederationManager for action " + action, ex);
            }
        }
        else {
            try {
                output.write(new StateChangeResponse(currentState, currentState, "FederationManager cannot transition from " + currentState + " state to " + targetState));
                output.close();
            }
            catch(IOException ioEx) {
                logger.error("ChunkedOutput problem.", ioEx);
            }
        }

        return output;
    }

    private void startSimulationAsync(final ChunkedOutput<StateChangeResponse> output) {
        new Thread() {
            @Override
            public void run() {
                try {
                    output.write(new StateChangeResponse(federationManager.getFederateState(), FederateState.STARTING));
                    federationManager.startSimulation();
                    output.write(new StateChangeResponse(FederateState.STARTING, federationManager.getFederateState()));
                    output.close();
                }
                catch(Exception ex) {
                    logger.error("There was an error while starting the simulation", ex);
                }
            }
        }.start();
    }

    private void terminateSimulationAsync(final ChunkedOutput<StateChangeResponse> output) {
        new Thread() {
            @Override
            public void run() {
                try {
                    output.write(new StateChangeResponse(federationManager.getFederateState(), FederateState.TERMINATING));
                    federationManager.terminateSimulation();
                    output.write(new StateChangeResponse(FederateState.TERMINATING, federationManager.getFederateState()));
                    output.close();
                }
                catch(Exception ex) {
                    logger.error("There was an error while terminating the simulation", ex);
                }
            }
        }.start();
    }

}
