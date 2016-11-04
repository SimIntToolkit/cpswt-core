package c2w.host;

import c2w.hla.FedMgr;
import c2w.hla.FederationManagerParameter;
import c2w.host.resources.FederationManagerControlResource;
import io.dropwizard.Application;
import io.dropwizard.configuration.EnvironmentVariableSubstitutor;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class FederationManagerHostApplication extends Application<FederationManagerHostConfiguration> {

    public static void main(String[] args) throws Exception {
        new FederationManagerHostApplication().run(args);
    }

    @Override
    public String getName() {
        return "FederationManagerHost";
    }

    FedMgr federationManager;

    @Override
    public void initialize(Bootstrap<FederationManagerHostConfiguration> bootstrap) {
        // Enable variable substitution with environment variables
        bootstrap.setConfigurationSourceProvider(
                new SubstitutingSourceProvider(
                        bootstrap.getConfigurationSourceProvider(),
                        new EnvironmentVariableSubstitutor(false)
                )
        );
    }

    @Override
    public void run(FederationManagerHostConfiguration configuration, Environment environment) {
        try {
            FederationManagerParameter fedMgrParams = configuration.getFederationManagerParameter();
            this.federationManager = new FedMgr(fedMgrParams);

            final FederationManagerControlResource fedMgrControlEndpoint = new FederationManagerControlResource(this.federationManager);

            // register resource (endpoint)
            environment.jersey().register(fedMgrControlEndpoint);

        } catch (Exception ex) {

        }

    }
}
