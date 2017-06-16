package c2w.host.core;

import c2w.hla.FederationManager;
import c2w.hla.FederationManagerConfig;

/**
 * Singleton to contain and control Federation Manager
 */
public class FederationManagerContainer {

    private static FederationManager INSTANCE = null;

    // throws exception because I haven't cleaned up the shitshow on the lower level yet
    public static void init(FederationManagerConfig federationManagerParameter) throws Exception {
        if(INSTANCE == null) {
            INSTANCE = new FederationManager(federationManagerParameter);
        }
    }

    public static FederationManager getInstance() { return INSTANCE; }

}
