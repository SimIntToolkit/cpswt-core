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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FederationManagerHostApplication extends Application<FederationManagerHostConfiguration> {

    private final Logger logger = LoggerFactory.getLogger(FederationManagerHostApplication.class);

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

        bootstrap.addBundle(new WebsocketBundle(FederationManagerControlWSResource.class));
    }

    @Override
    public void run(FederationManagerHostConfiguration configuration, Environment environment) {
        try {
            FederationManagerParameter fedMgrParams = configuration.getFederationManagerParameter();
            FederationManager federationManager = new FederationManager(fedMgrParams);

            // register resource (endpoint)
            environment.jersey().register(new FederationManagerControlResource(federationManager));

        } catch (Exception ex) {
            logger.error("Error initializing FederationManagerHostApplication. Reason: " + ex.getMessage());
            System.exit(-1);
        }

    }
}
