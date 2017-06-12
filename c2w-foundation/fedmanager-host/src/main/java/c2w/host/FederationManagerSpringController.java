package c2w.host;

import c2w.hla.FederateState;
import c2w.hla.FederationManager;
import c2w.host.api.ControlAction;
import c2w.host.api.FederationManagerControlRequest;
import c2w.host.api.StateChangeResponse;
import c2w.host.api.StateResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.jersey.server.ChunkedOutput;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/fedmgr")
public class FederationManagerSpringController {

    private final FederationManager federationManager;
    private final Logger logger = LogManager.getLogger(FederationManagerSpringHostApplication.class);

    public FederationManagerSpringController(FederationManager federationManager) {
        this.federationManager = federationManager;
    }

    @RequestMapping(method = RequestMethod.GET)
    public StateResponse getCurrentState() {
        return new StateResponse(this.federationManager.getFederateState());
    }

    @RequestMapping(method = RequestMethod.POST)
    public ChunkedOutput<StateChangeResponse> setNewState(@RequestBody FederationManagerControlRequest controlRequest) {
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
                    output.close();
                    federationManager.startSimulation();
                    //output.write(new StateChangeResponse(FederateState.STARTING, federationManager.getFederateState()));
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
