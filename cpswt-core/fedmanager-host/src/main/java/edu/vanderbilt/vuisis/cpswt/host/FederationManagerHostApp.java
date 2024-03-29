/*
 * Certain portions of this software are Copyright (C) 2006-present
 * Vanderbilt University, Institute for Software Integrated Systems.
 *
 * Certain portions of this software are contributed as a public service by
 * The National Institute of Standards and Technology (NIST) and are not
 * subject to U.S. Copyright.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above Vanderbilt University copyright notice, NIST contribution
 * notice and this permission and disclaimer notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE. THE AUTHORS OR COPYRIGHT HOLDERS SHALL NOT HAVE
 * ANY OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS,
 * OR MODIFICATIONS.
 */

package edu.vanderbilt.vuisis.cpswt.host;

import akka.actor.ActorSystem;
import akka.http.javadsl.Http;
import akka.http.javadsl.ServerBinding;
import akka.http.javadsl.marshallers.jackson.Jackson;
import akka.http.javadsl.server.AllDirectives;
import akka.http.javadsl.server.Route;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import edu.vanderbilt.vuisis.cpswt.config.FederateConfigParser;
import edu.vanderbilt.vuisis.cpswt.hla.FederateState;
import edu.vanderbilt.vuisis.cpswt.hla.FederationManager;
import edu.vanderbilt.vuisis.cpswt.hla.FederationManagerConfig;
import edu.vanderbilt.vuisis.cpswt.host.api.ControlAction;
import edu.vanderbilt.vuisis.cpswt.host.api.FederationManagerControlRequest;
import edu.vanderbilt.vuisis.cpswt.host.api.StateChangeResponse;
import edu.vanderbilt.vuisis.cpswt.host.api.StateResponse;

//import java.io.IOException;
import java.util.concurrent.CompletionStage;

/**
 * Federation Manager hosting through Akka-HTTP
 */
public class FederationManagerHostApp extends AllDirectives {

    private static final Logger logger = LogManager.getLogger(FederationManagerHostApp.class);
    private FederationManager federationManager;

    private String bindingAddress;

    private String getBindingAddress() {
        return bindingAddress;
    }

    private int port;

    private int getPort() {
        return port;
    }

    private FederationManagerConfig federationManagerConfig;
    private ObjectMapper objectMapper;

    public FederationManagerHostApp() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JodaModule());
        this.objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        this.objectMapper.configure(SerializationFeature.WRITE_DATES_WITH_ZONE_ID, false);
    }

    void parseConfig(String[] args) {
        this.federationManagerConfig = this.getFederationManagerParameter(args);
        this.bindingAddress = this.federationManagerConfig.bindHost;
        this.port = this.federationManagerConfig.port;

    }

    void initFederationManager() {
        try {
            this.federationManager = new FederationManager(this.federationManagerConfig);
        } catch (Exception e) {
            logger.error("Error while initializing FederationManager! " + e.getMessage());
            logger.error(e);
        }
    }

    FederationManagerConfig getFederationManagerParameter(String[] args) {
        try {
            FederationManagerConfig federationManagerConfig;
            FederateConfigParser federateConfigParser = new FederateConfigParser();

            federationManagerConfig = federateConfigParser.parseArgs(args, FederationManagerConfig.class);
            return federationManagerConfig;
        } catch (Exception fedMgrExp) {
            logger.error("There was an error starting the federation manager. Reason: {}", fedMgrExp.getMessage());
            logger.error(fedMgrExp);
            System.exit(-1);
        }

        return null;
    }

    Route createRoute() {
        return route(
                get(() ->
                        path("fedmgr", () -> {
                            if(federationManager == null) {
                                return reject();
                            }
                            return completeOK(new StateResponse(federationManager.getFederateState()), Jackson.marshaller(this.objectMapper));
                        })
                ),
                get(() ->
                        path("federates", () ->
                                completeOK(federationManager.getFederatesStatus(), Jackson.marshaller(this.objectMapper))
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
                                                    logger.debug("Starting simulation");
                                                    response = new StateChangeResponse(currentState, FederateState.STARTING);
                                                    this.startSimulationAsync();
                                                    break;
                                                case PAUSE:
                                                    logger.debug("Pause simulation");
                                                  this.federationManager.pauseSimulation();
                                                    response = new StateChangeResponse(currentState, federationManager.getFederateState());
                                                    break;
                                                case RESUME:
                                                    logger.debug("Resume simulation");
                                                   this.federationManager.resumeSimulation();
                                                    response = new StateChangeResponse(currentState, federationManager.getFederateState());
                                                    break;
                                                case TERMINATE:
                                                    logger.debug("Terminate simulation");
                                                  response = new StateChangeResponse(federationManager.getFederateState(), FederateState.TERMINATING);
                                                    this.terminateSimulationAsync();
                                                    break;
                                            }
//                                        } catch (IOException ioEx) {
//                                            logger.error("Closing ChunkedOutput encountered a problem.");
//                                            logger.error(ioEx);
                                        } catch (Exception ex) {
                                            logger.error("There was an error while trying to transition FederationManager for action {}", action);
                                            logger.error(ex);
                                        }
                                    } else {
                                        response = new StateChangeResponse(currentState, currentState, "FederationManager cannot transition from " + currentState + " state to " + targetState);
                                    }

                                    return completeOK(response, Jackson.marshaller(this.objectMapper));
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
                } catch (Exception ex) {
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
                } catch (Exception ex) {
                    logger.error("There was an error while terminating the simulation", ex);
                }
            }
        }.start();
    }


    public static void main(String[] args) throws Exception {

        ActorSystem system = ActorSystem.create("routes");

        final Http http = Http.get(system);
        FederationManagerHostApp app = new FederationManagerHostApp();
        app.parseConfig(args);

        app.initFederationManager();
        final Route route = app.createRoute();
        final CompletionStage<ServerBinding> binding = http.newServerAt(app.getBindingAddress(), app.getPort()).bind(route);

        logger.info("Server online at {}:{} ...", app.getBindingAddress(), app.getPort());
        System.in.read();

        binding.thenCompose(ServerBinding::unbind)
                .thenAccept(unbound -> system.terminate());
    }
}
