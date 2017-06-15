package c2w.templates;

import c2w.hla.DefaultFederateParameterParser;
import c2w.hla.FederateParameter;
import c2w.hla.FederateParameterParser;
import c2w.hla.SynchronizedFederate;
import org.apache.log4j.Logger;

public class CustomFederate extends SynchronizedFederate {
    static final Logger logger = Logger.getLogger(CustomFederate.class);

    public CustomFederate(FederateParameter federateParameter) {
        super(federateParameter);

        // TODO: custom init logic
    }

    public void start() {
        // TODO: start logic

        logger.debug("Starting custom federate");
    }

    public static void main(String[] args) throws Exception {
        FederateParameter currentParameter;
        FederateParameterParser federateParameterParser = new FederateParameterParser(FederateParameter.getDefaultCLIOptions());

        try {
            currentParameter = federateParameterParser.parseArgs(args, FederateParameter.class);

            CustomFederate customFederate = new CustomFederate(currentParameter);
            customFederate.start();

        }
        catch (Exception e) {
            logger.error("There was an error starting the federate. Reason: " + e.getMessage(), e);
            System.exit(-1);
        }

    }

}