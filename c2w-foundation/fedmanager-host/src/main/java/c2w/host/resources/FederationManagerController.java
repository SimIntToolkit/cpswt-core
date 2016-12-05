package c2w.host.resources;

import c2w.hla.FederateState;
import c2w.hla.FederationManager;
import c2w.host.api.ControlAction;
import c2w.host.api.StateChangeResponse;
import c2w.host.api.StateResponse;
import c2w.host.util.OutputWriter;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.glassfish.jersey.server.ChunkedOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;
import spark.Route;

import javax.websocket.Session;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * RESTful endpoint
 * "/fedmgr"
 * WS endpoint
 * "/fedmgr-ws"
 */
@WebSocket
public class FederationManagerController {

    private static final Logger LOG = LoggerFactory.getLogger(FederationManagerController.class);

    private FederationManager federationManager = null;
    final ConcurrentHashMap<String, Session> activeConnections;

    public FederationManagerController(FederationManager federationManager) {
        this.federationManager = federationManager;
        this.activeConnections = new ConcurrentHashMap<>();
    }

    public Route getFederateState = (Request request, Response response) -> {
        return new StateResponse(federationManager.getFederateState());
    };

    public Route changeFederateState = (Request request, Response response) -> {
        String actionStr = request.params("action");
        ControlAction action = ControlAction.valueOf(actionStr);
        OutputWriter output = new OutputWriter(response);

        return this.performAction(action, output);
    };

    @OnWebSocketConnect
    public void onConnect(Session session) {
        String id = session.getId();
        this.activeConnections.put(id, session);

        LOG.info("%s joined through a web socket connection", id);
    }

    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason) {
        String id = session.getId();
        this.activeConnections.remove(id);

        LOG.info("%s closed web socket connection", id);
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) {
        String id = session.getId();

        LOG.debug("Message received from %s: %s", id, message);

        ControlAction action = ControlAction.valueOf(message);
        OutputWriter output = new OutputWriter(session);

        this.performAction(action, output);
    }

    private Object performAction(ControlAction action, OutputWriter output) {
        if(action == ControlAction.GET_STATUS) {
            try {
                output.write(new StateResponse(this.federationManager.getFederateState()));
                return null;
            }
            catch (IOException ioEx) {
                LOG.error("There was an error while trying to write to output", ioEx);
            }
        }

        FederateState currentState = this.federationManager.getFederateState();
        FederateState targetState = action.getTargetState();

        if (currentState.CanTransitionTo(targetState)) {
            try {
                switch (action) {
                    case START:
                        this.startSimulationAsync(output);
                        break;
                    case PAUSE:
                        this.federationManager.pauseSimulation();
                        output.write(new StateChangeResponse(currentState, federationManager.getFederateState()));
                        break;
                    case RESUME:
                        this.federationManager.resumeSimulation();
                        output.write(new StateChangeResponse(currentState, federationManager.getFederateState()));
                        break;
                    case TERMINATE:
                        this.terminateSimulationAsync(output);
                        break;
                }
            }
            catch(IOException ioEx) {
                LOG.error("There was an error while trying to write to output", ioEx);
            }
            catch (Exception ex) {
                LOG.error("There was an error while trying to transition FederationManager for action " + action, ex);
            }
        }

        return null;
    }

    private void startSimulationAsync(final OutputWriter output) {
        new Thread() {
            @Override
            public void run() {
                try {
                    output.write(new StateChangeResponse(federationManager.getFederateState(), FederateState.STARTING), false);
                    federationManager.startSimulation();
                    output.write(new StateChangeResponse(FederateState.STARTING, federationManager.getFederateState()));
                }
                catch(Exception ex) {
                    LOG.error("There was an error while starting the simulation", ex);
                }
            }
        }.start();
    }

    private void terminateSimulationAsync(final OutputWriter output) {
        new Thread() {
            @Override
            public void run() {
                try {
                    output.write(new StateChangeResponse(federationManager.getFederateState(), FederateState.TERMINATING), false);
                    federationManager.terminateSimulation();
                    output.write(new StateChangeResponse(FederateState.TERMINATING, federationManager.getFederateState()));
                }
                catch(Exception ex) {
                    LOG.error("There was an error while terminating the simulation", ex);
                }
            }
        }.start();
    }
}
