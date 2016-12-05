package c2w.host.resources;

import c2w.hla.FederateState;
import c2w.hla.FederateStateChangeEvent;
import c2w.hla.FederateStateChangeListener;
import c2w.hla.FederationManager;
import c2w.host.api.*;
import c2w.host.core.FederationManagerContainer;
import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Metered;
import com.codahale.metrics.annotation.Timed;
import org.eclipse.jetty.websocket.api.WebSocketException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Represents the web socket resource.
 */
@Metered
@Timed
@ExceptionMetered
@ServerEndpoint(value = "/fedmgr-ws", encoders = {StateResponse.Encoder.class, StateChangeResponse.Encoder.class})
public class FederationManagerControlWSResource implements FederateStateChangeListener {
    private final FederationManager federationManager;
    private static Logger LOG = LoggerFactory.getLogger(FederationManagerControlWSResource.class);
    private ConcurrentHashMap<String, Session> clients;

    public FederationManagerControlWSResource() {
        this.federationManager = FederationManagerContainer.getInstance();
        this.clients = new ConcurrentHashMap<>();
        this.federationManager.addFederateStateChangeListener(this);
    }

    @OnOpen
    public void open(final Session session) throws IOException {
        String id = session.getId();

        if (!this.clients.containsKey(id)) {
            this.clients.put(id, session);
        }

        LOG.debug("Connection opened: " + id);
        sendStateResponse(session, this.federationManager.getFederateState());
    }

    @OnMessage
    public void handleMessage(final Session session, String message) {
        String id = session.getId();
        //session.getUserProperties("federationManager")

        LOG.debug("Message received from " + id + ": " + message);
        ControlAction action = ControlAction.valueOf(message.toUpperCase());

        if (action == ControlAction.GET_STATUS) {
            sendStateResponse(session, this.federationManager.getFederateState());
        }

        FederateState currentState = this.federationManager.getFederateState();
        FederateState targetState = action.getTargetState();

        if (currentState.CanTransitionTo(targetState)) {
            try {
                switch (action) {
                    case START:
                        sendStateChangeResponse(session, new StateChangeResponse(federationManager.getFederateState(), FederateState.STARTING));
                        federationManager.startSimulation();
                        sendStateChangeResponse(session, new StateChangeResponse(FederateState.STARTING, federationManager.getFederateState()));
                        break;
                    case PAUSE:
                        federationManager.pauseSimulation();
                        sendStateChangeResponse(session, new StateChangeResponse(currentState, federationManager.getFederateState()));
                        break;
                    case RESUME:
                        federationManager.resumeSimulation();
                        sendStateChangeResponse(session, new StateChangeResponse(currentState, federationManager.getFederateState()));
                        break;
                    case TERMINATE:
                        sendStateChangeResponse(session, new StateChangeResponse(federationManager.getFederateState(), FederateState.TERMINATING));
                        federationManager.terminateSimulation();
                        sendStateChangeResponse(session, new StateChangeResponse(FederateState.TERMINATING, federationManager.getFederateState()));
                        break;
                }
            } catch (Exception ex) {
                LOG.error("There was an error while trying to transition FederationManager for action " + action, ex);
            }
        }
    }

    @OnClose
    public void close(final Session session, CloseReason closeReason) {
        String id = session.getId();
        if (this.clients.containsKey(id)) {
            this.clients.remove(id);
        }
    }

    @Override
    public void federateStateChanged(FederateStateChangeEvent e) {
        StateChangeResponse response = new StateChangeResponse(e.getPrevState(), e.getNewState());
        for (Map.Entry<String, Session> client : this.clients.entrySet()) {
            client.getValue().getAsyncRemote().sendObject(response);
        }
    }

    void sendStateResponse(Session session, FederateState state) {
        try {
            session.getBasicRemote().sendObject(new StateResponse(state));
        } catch (IOException ioEx) {
            LOG.error("IO Exception while responding on WS", ioEx);
        } catch (EncodeException wsEx) {
            LOG.error("EncodeException while responding on WS", wsEx);
        }
    }

    void sendStateChangeResponse(Session session, StateChangeResponse stateChangeResponse) {
        session.getAsyncRemote().sendObject(stateChangeResponse);
    }
}
