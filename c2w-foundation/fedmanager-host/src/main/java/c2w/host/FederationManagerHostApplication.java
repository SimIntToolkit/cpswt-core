package c2w.host;

import c2w.hla.FederationManager;
import c2w.hla.FederationManagerParameter;
import c2w.host.resources.FederationManagerControlResource;
import c2w.host.resources.FederationManagerControlWSResource;
import io.dropwizard.Application;
import io.dropwizard.configuration.EnvironmentVariableSubstitutor;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.dropwizard.websockets.WebsocketBundle;
import org.eclipse.jetty.websocket.common.scopes.WebSocketContainerScope;
import org.eclipse.jetty.websocket.jsr356.server.AnnotatedServerEndpointConfig;
import org.eclipse.jetty.websocket.jsr356.server.BasicServerEndpointConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.server.ServerEndpointConfig;

public class FederationManagerHostApplication extends Application<FederationManagerHostConfiguration> {

    private final Logger logger = LoggerFactory.getLogger(FederationManagerHostApplication.class);
    private WebsocketBundle websocketBundle;

    public static void main(String[] args) throws Exception {
        new FederationManagerHostApplication().run(args);
    }

    @Override
    public String getName() {
        return "FederationManagerHost";
    }

    @Override
    public void initialize(Bootstrap<FederationManagerHostConfiguration> bootstrap) {
        // Enable variable substitution with environment variables
        bootstrap.setConfigurationSourceProvider(
                new SubstitutingSourceProvider(
                        bootstrap.getConfigurationSourceProvider(),
                        new EnvironmentVariableSubstitutor(false)
                )
        );

        websocketBundle = new WebsocketBundle(FederationManagerControlWSResource.class);
        bootstrap.addBundle(websocketBundle);
    }

    @Override
    public void run(FederationManagerHostConfiguration configuration, Environment environment) {
        try {
            FederationManagerParameter fedMgrParams = configuration.getFederationManagerParameter();
            FederationManager federationManager = new FederationManager(fedMgrParams);

            // register resource (endpoint)
            environment.jersey().register(new FederationManagerControlResource(federationManager));

            ServerEndpointConfig config = ServerEndpointConfig.Builder.create(FederationManagerControlWSResource.class, "/fedmgr-ws").build();
            config.getUserProperties().put("federationManager", federationManager);
            websocketBundle.addEndpoint(config);

        } catch (Exception ex) {
            logger.error("Error initializing FederationManagerHostApplication. Reason: " + ex.getMessage());
            System.exit(-1);
        }

    }
}
