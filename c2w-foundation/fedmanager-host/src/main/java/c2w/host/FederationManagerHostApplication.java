package c2w.host;

import io.dropwizard.Application;
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

    @Override
    public void initialize(Bootstrap<FederationManagerHostConfiguration> bootstrap) {

    }

    @Override
    public void run(FederationManagerHostConfiguration configuration, Environment environment) {

    }
}
