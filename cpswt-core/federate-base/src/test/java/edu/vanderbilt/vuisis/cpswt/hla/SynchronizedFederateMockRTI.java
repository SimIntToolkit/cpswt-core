package edu.vanderbilt.vuisis.cpswt.hla;

import hla.rti.RTIambassador;
import edu.vanderbilt.vuisis.cpswt.config.FederateConfig;

public class SynchronizedFederateMockRTI extends SynchronizedFederate {

    private static final RTIAmbassadorProxy2 mock = RTIAmbassadorProxy2.get_instance();
    private static final RTIambassador rtiambassador = mock.getRTIAmbassador();

    public SynchronizedFederateMockRTI(FederateConfig federateConfig) {
        super(federateConfig);
    }

    @Override
    public void createRTI() {
        rti = rtiambassador;
    }
}
