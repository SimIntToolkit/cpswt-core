/**
 * @author Greg Varga
 */

package c2w;

import c2w.hla.FedMgr;
import c2w.hla.FedMgrParam;
import org.apache.log4j.Logger;

public class FederationManagerHost {

    static final Logger logger = Logger.getLogger(FederationManagerHost.class);
    private FedMgr federationManager;

    public FederationManagerHost(FedMgrParam fedMgrParam) {

        try {
            federationManager = new FedMgr(fedMgrParam);
        } catch (Exception e) {
            logger.error("Error during initializing FederationManager! Quitting...", e);
            System.exit(-1);
        }
    }

}
