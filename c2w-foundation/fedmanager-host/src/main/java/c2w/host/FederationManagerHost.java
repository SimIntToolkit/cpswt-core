/**
 * @author Greg Varga <greg@sph3r.com>
 */

package c2w.host;

import c2w.hla.FedMgr;
import c2w.hla.FederationManagerParameter;
import org.apache.commons.cli.*;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import java.io.IOException;

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

    public void StartListening() {
        // this is where the jetty setup is done
    }

    void startSimulation() throws Exception {
        this.federationManager.startSimulation();
    }

    void terminateSimulation() {
        this.federationManager.terminateSimulation();
    }

    void pauseSimuation() throws Exception {
        this.federationManager.pauseSimulation();
    }

    void resumeSimulation() throws Exception {
        this.federationManager.resumeSimulation();
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
        }
        catch (ParseException parseExp) {
            logger.error("Parsing CLI arguments failed. Reason: " + parseExp.getMessage(), parseExp);
            System.exit(-1);
        }
        catch(IOException ioExp) {
            logger.error("Parsing input configuration file failed. Reason: " + ioExp.getMessage(), ioExp);
            System.exit(-1);
        }

        try {
            FederationManagerHost host = new FederationManagerHost(currentParameter);
            host.StartListening();
        }
        catch (Exception fedMgrExp) {
            logger.error("There was an error starting the federation manager. Reason: " + fedMgrExp.getMessage(), fedMgrExp);
            System.exit(-1);
        }

    }
}
