package c2w.templates;

import org.cpswt.config.FederateConfig;
import org.cpswt.config.FederateConfigParser;
import c2w.hla.SynchronizedFederate;
import org.apache.log4j.Logger;

public class CustomFederate extends SynchronizedFederate {
    static final Logger logger = Logger.getLogger(CustomFederate.class);

    public CustomFederate(FederateConfig federateConfig) {
        super(federateConfig);

        // TODO: custom init logic
    }

    public void start() {
        // TODO: start logic

        logger.debug("Starting custom federate");
    }

    public static void main(String[] args) throws Exception {
        FederateConfig currentParameter;
        FederateConfigParser federateConfigParser = new FederateConfigParser();

        try {
            currentParameter = federateConfigParser.parseArgs(args, FederateConfig.class);

            CustomFederate customFederate = new CustomFederate(currentParameter);
            customFederate.start();

        }
        catch (Exception e) {
            logger.error("There was an error starting the federate. Reason: " + e.getMessage(), e);
            System.exit(-1);
        }

    }

}