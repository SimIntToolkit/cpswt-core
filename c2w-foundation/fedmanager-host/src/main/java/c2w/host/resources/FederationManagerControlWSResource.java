package c2w.host.resources;

import c2w.hla.FederateState;
import c2w.hla.FederateStateChangeEvent;
import c2w.hla.FederateStateChangeListener;
import c2w.hla.FederationManager;
import c2w.host.api.*;
import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Metered;
import com.codahale.metrics.annotation.Timed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Represents the web socket resource.
 */
@Metered
@Timed
@ExceptionMetered
@ServerEndpoint(value = "/fedmgr-ws") // , encoders = {JsonEncoder.class}
public class FederationManagerControlWSResource implements FederateStateChangeListener {
    private final FederationManager federationManager;
    private static Logger logger = LoggerFactory.getLogger(FederationManagerControlWSResource.class);
    private ConcurrentHashMap<String, Session> clients;

    public FederationManagerControlWSResource(FederationManager federationManager) {
        this.federationManager = federationManager;
        this.clients = new ConcurrentHashMap<>();
        this.federationManager.addFederateStateChangeListener(this);
    }

    @OnOpen
    public void open(final Session session) throws IOException {
        String id = session.getId();

        if (!this.clients.containsKey(id)) {
            this.clients.put(id, session);
        }

        logger.debug("Connection opened: " + id);
        session.getAsyncRemote().sendObject(new StateResponse(this.federationManager.getFederateState()));
    }

    @OnMessage
    public void handleMessage(final Session session, String message) {
        String id = session.getId();
        //session.getUserProperties("federationManager")

        logger.debug("Message received from " + id + ": " + message);
        ControlAction action = ControlAction.valueOf(message.toUpperCase());
        FederateState currentState = this.federationManager.getFederateState();
        FederateState targetState = action.getTargetState();

        if (currentState.CanTransitionTo(targetState)) {
            try {
                switch (action) {
                    case START:
                        break;
                    case PAUSE:
                        break;
                    case RESUME:
                        break;
                    case TERMINATE:
                        break;
                }
            }
            catch(Exception ex){
                logger.error("There was an error while trying to transition FederationManager for action " + action, ex);
            }
        }

        session.getAsyncRemote().sendText("test back");
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
        for (Map.Entry<String, Session> client: this.clients.entrySet()) {
            client.getValue().getAsyncRemote().sendObject(response);
        }
    }
}
