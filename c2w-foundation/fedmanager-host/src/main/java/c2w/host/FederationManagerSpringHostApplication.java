package c2w.host;

import c2w.hla.FederationManagerParameter;
import c2w.host.core.FederationManagerContainer;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class FederationManagerSpringHostApplication {

    public static void main(String[] args) {
        SpringApplication.run(FederationManagerSpringHostApplication.class, args);
    }

    @Bean
    CommandLineRunner init() {
        return (evt) -> {
            CommandLineParser parser  = new DefaultParser();
            Options cliOptions = FederationManagerParameter.GetCLIOptions();
            FederationManagerParameter currentParameter = null;

            if(evt.length == 1) {
                currentParameter = FederationManagerParameter.ParsePropertiesFile(evt[0]);
            }
            else {
                CommandLine line = parser.parse(cliOptions, evt);
                currentParameter = FederationManagerParameter.ParseInputs(line);
            }

            FederationManagerContainer.init(currentParameter);
        };
    }
}
