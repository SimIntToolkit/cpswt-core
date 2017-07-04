package org.cpswt.host;

import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.http.javadsl.ConnectHttp;
import akka.http.javadsl.Http;
import akka.http.javadsl.ServerBinding;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;
import akka.http.javadsl.marshallers.jackson.Jackson;
import akka.http.javadsl.server.AllDirectives;
import akka.http.javadsl.server.Route;
import akka.stream.ActorMaterializer;
import akka.stream.javadsl.Flow;
import org.cpswt.host.api.FederationManagerControlRequest;
import org.cpswt.host.api.StateResponse;
import org.cpswt.config.FederateConfigParser;
import org.cpswt.hla.FederateState;
import org.cpswt.hla.FederationManager;
import org.cpswt.hla.FederationManagerConfig;
import org.cpswt.host.api.ControlAction;
import org.cpswt.host.api.StateChangeResponse;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.concurrent.CompletionStage;

/**
 * Federation Manager hosting through Akka-HTTP
 */
public class FederationManagerHostApp extends AllDirectives {

    static final Logger logger = Logger.getLogger(FederationManagerHostApp.class);
    private FederationManager federationManager;

    String bindingAddress;
    public String getBindingAddress() {
        return bindingAddress;
    }
    int port;
    public int getPort() {
        return port;
    }
    FederationManagerConfig federationManagerConfig;

    void parseConfig(String[] args) {
        this.federationManagerConfig = this.getFederationManagerParameter(args);
        this.bindingAddress = this.federationManagerConfig.bindHost;
        this.port = this.federationManagerConfig.port;

    }

    void initFederationManager() {
        try {
            this.federationManager = new FederationManager(this.federationManagerConfig);
        }
        catch(Exception e) {
            System.err.println("Error while initializing FederationManager!" + e.getMessage());
            System.err.println(e);
        }
    }

    FederationManagerConfig getFederationManagerParameter(String[] args) {
        try {
            FederationManagerConfig federationManagerConfig;
            FederateConfigParser federateConfigParser = new FederateConfigParser();

            federationManagerConfig = federateConfigParser.parseArgs(args, FederationManagerConfig.class);
            return federationManagerConfig;
        }
        catch (Exception fedMgrExp) {
            logger.error("There was an error starting the federation manager. Reason: " + fedMgrExp.getMessage(), fedMgrExp);
            System.exit(-1);
        }

        return null;
    }

    Route createRoute() {
        return route(
                get(() ->
                        path("fedmgr", () ->
                            completeOK(new StateResponse(federationManager.getFederateState()), Jackson.marshaller())
                        )
                ),
                post(() ->
                    path("fedmgr", () ->
                                    entity(Jackson.unmarshaller(FederationManagerControlRequest.class), controlRequest -> {
                        //parameter("action", actionStr -> {

                            ControlAction action = controlRequest.action; // ControlAction.valueOf(actionStr);
                            FederateState currentState = federationManager.getFederateState();
                            FederateState targetState = action.getTargetState();

                            StateChangeResponse response = null;

                            if (currentState.CanTransitionTo(targetState)) {
                                try {
                                    switch (action) {
                                        case START:
                                            response = new StateChangeResponse(currentState, FederateState.STARTING);
                                            this.startSimulationAsync();
                                            break;
                                        case PAUSE:
                                            this.federationManager.pauseSimulation();
                                            response = new StateChangeResponse(currentState, federationManager.getFederateState());
                                            break;
                                        case RESUME:
                                            this.federationManager.resumeSimulation();
                                            response = new StateChangeResponse(currentState, federationManager.getFederateState());
                                            break;
                                        case TERMINATE:
                                            response = new StateChangeResponse(federationManager.getFederateState(), FederateState.TERMINATING);
                                            this.terminateSimulationAsync();
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
                                response = new StateChangeResponse(currentState, currentState, "FederationManager cannot transition from " + currentState + " state to " + targetState);
                            }

                            return completeOK(response, Jackson.marshaller());
                        })
                    )
                )
        );
    }

    private void startSimulationAsync() {
        new Thread() {
            @Override
            public void run() {
                try {
                    federationManager.startSimulation();
                }
                catch(Exception ex) {
                    logger.error("There was an error while starting the simulation", ex);
                }
            }
        }.start();
    }

    private void terminateSimulationAsync() {
        new Thread() {
            @Override
            public void run() {
                try {
                    federationManager.terminateSimulation();
                }
                catch(Exception ex) {
                    logger.error("There was an error while terminating the simulation", ex);
                }
            }
        }.start();
    }


    public static void main(String[] args) throws Exception {

        ActorSystem system = ActorSystem.create("routes");

        final Http http = Http.get(system);
        final ActorMaterializer materializer = ActorMaterializer.create(system);

        FederationManagerHostApp app = new FederationManagerHostApp();
        app.parseConfig(args);

        final Flow<HttpRequest, HttpResponse, NotUsed> routeFlow = app.createRoute().flow(system, materializer);
        final CompletionStage<ServerBinding> binding = http.bindAndHandle(routeFlow,
                ConnectHttp.toHost(app.getBindingAddress(), app.getPort()), materializer);

        app.initFederationManager();

        System.out.println("Server online at " + app.getBindingAddress() + ":" + app.getPort() + "...");
        System.in.read();

        binding.thenCompose(ServerBinding::unbind)
                .thenAccept(unbound -> system.terminate());
    }
}
