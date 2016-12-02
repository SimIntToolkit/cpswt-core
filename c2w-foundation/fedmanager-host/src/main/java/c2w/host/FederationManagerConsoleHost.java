package c2w.host;

import c2w.hla.FederationManager;
import c2w.hla.FederationManagerParameter;
import org.apache.commons.cli.*;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import java.io.IOException;

/**
 * This is a simple host class for federation manager. This does NOT support federation manager control.
 * For RESTful federation manager control @see FederationManagerHostApplication class.
 */
public class FederationManagerConsoleHost {

    static final Logger logger = Logger.getLogger(FederationManagerConsoleHost.class);
    private FederationManager federationManager;

    public FederationManagerConsoleHost(FederationManagerParameter federationManagerParameter) {
        try {
            this.federationManager = new FederationManager(federationManagerParameter);
        } catch (Exception e) {
            logger.error("Error during initializing FederationManager! Quitting...", e);
            System.exit(-1);
        }
    }

    void startSimulation() throws Exception {
        this.federationManager.startSimulation();
    }

    public static void main(String[] args) throws Exception {
        BasicConfigurator.configure();

        CommandLineParser parser  = new DefaultParser();
        Options cliOptions = FederationManagerParameter.GetCLIOptions();
        FederationManagerParameter currentParameter = null;

        try {
            if(args.length == 1) {
                currentParameter = FederationManagerParameter.ParsePropertiesFile(args[0]);
            }
            else {
                CommandLine line = parser.parse(cliOptions, args);
                currentParameter = FederationManagerParameter.ParseInputs(line);
            }
            FederationManagerConsoleHost host = new FederationManagerConsoleHost(currentParameter);
            host.startSimulation();
        }
        catch (ParseException parseExp) {
            logger.error("Parsing CLI arguments failed. Reason: " + parseExp.getMessage(), parseExp);
            System.exit(-1);
        }
        catch(IOException ioExp) {
            logger.error("Parsing input configuration file failed. Reason: " + ioExp.getMessage(), ioExp);
            System.exit(-1);
        }
        catch (Exception fedMgrExp) {
            logger.error("There was an error starting the federation manager. Reason: " + fedMgrExp.getMessage(), fedMgrExp);
            System.exit(-1);
        }

    }
}
