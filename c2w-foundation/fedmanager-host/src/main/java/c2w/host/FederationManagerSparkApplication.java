package c2w.host;

import c2w.hla.FederationManager;
import c2w.host.resources.FederationManagerController;
import c2w.host.util.Config;
import c2w.host.util.Filters;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLParser;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static spark.Spark.*;
import static spark.debug.DebugScreen.*;

/**
 * The Spark host application
 */
public class FederationManagerSparkApplication {
    private static final Logger logger = LoggerFactory.getLogger(FederationManagerSparkApplication.class);

    public static void main(String[] args) {

        String configFile = "config.yml";
        if (args.length == 1) {
            configFile = args[0];
        }

        FederationManagerSparkApplication app = new FederationManagerSparkApplication(configFile);
        app.init();
    }

    Config config;
    FederationManager federationManager;

    public FederationManagerSparkApplication(String configPath) {
        try {
            this.config = this.loadConfig(configPath);
        } catch (IOException ioEx) {
            logger.error("There was an error while loading configuration file.", ioEx);
            System.exit(-1);
        }
    }

    private Config loadConfig(String configPath) throws IOException {
        File configFile = new File(configPath);
        if (Files.notExists(configFile.toPath())) {
            throw new IOException("Config file doesn't exist (" + configPath + ")");
        }

        String rawConfigContent = new String(Files.readAllBytes(configFile.toPath()));
        String configContent = this.replaceEnvVars(rawConfigContent);

        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        Config config = mapper.readValue(configContent, Config.class);

        return config;
    }

    public void init() {
        try {

            port(this.config.server.port);

            if (this.config.debug) {
                enableDebugScreen();
            }

            before("*", Filters.addTrailingSlashes);

            // create federation manager
            // TODO: REMOVE INIT CODE FROM CTOR TO SPEED UP OBJECT CREATION
            this.federationManager = new FederationManager(this.config.federationManagerParameter);

            // rest controller
            FederationManagerController restController = new FederationManagerController(federationManager);
            get(this.config.server.restfulEndpoint, restController.getFederateState);
            post(this.config.server.restfulEndpoint, restController.changeFederateState);

            // ws controller
            // TODO: add WS controller implementation

        } catch (Exception ex) {
            logger.error("Error initializing FederationManagerHost application. Reason: " + ex.getMessage());
            System.exit(-1);
        }
    }

    private String replaceEnvVars(String str) {
        StrSubstitutor sub = new StrSubstitutor(System.getenv());
        String resolvedString = sub.replace(str);

        return resolvedString;
    }
}
