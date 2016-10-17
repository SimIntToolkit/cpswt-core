/**
 * @author Greg Varga <greg@sph3r.com>
 */

package c2w;

import c2w.hla.FedMgr;
import c2w.hla.FederationManagerParameter;
import org.apache.commons.cli.*;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

public class FederationManagerHost {

    static final Logger logger = Logger.getLogger(FederationManagerHost.class);
    private FedMgr federationManager;

    public FederationManagerHost(FederationManagerParameter federationManagerParameter) {
        try {
            this.federationManager = new FedMgr(federationManagerParameter);
        } catch (Exception e) {
            logger.error("Error during initializing FederationManager! Quitting...", e);
            System.exit(-1);
        }
    }

    public FederationManagerHost(String configFile) {

    }

    public void StartSimulation() throws Exception {
        this.federationManager.startSimulation();
    }


    public static void main(String[] args) throws Exception {
        BasicConfigurator.configure();

        CommandLineParser parser  = new DefaultParser();
        Options cliOptions = FederationManagerParameter.GetCLIOptions();
        FederationManagerParameter currentParameter = null;

        try {
            CommandLine line = parser.parse(cliOptions, args);
            currentParameter = FederationManagerParameter.ParseInputs(line);
        }
        catch (ParseException parseExp) {
            logger.error("Parsing CLI arguments failed. Reason: " + parseExp.getMessage(), parseExp);
            System.exit(-1);
        }

        try {
            FederationManagerHost host = new FederationManagerHost(currentParameter);
            host.StartSimulation();
        }
        catch (Exception fedMgrExp) {
            logger.error("There was an error starting the federation manager. Reason: " + fedMgrExp.getMessage(), fedMgrExp);
            System.exit(-1);
        }

    }
}
