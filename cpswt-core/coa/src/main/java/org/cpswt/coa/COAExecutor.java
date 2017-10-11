package org.cpswt.coa;

import hla.rti.RTIambassador;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class COAExecutor {
    private static final Logger logger = LogManager.getLogger(COAExecutor.class);

    private final String federationId;
    private final String federateId;
    private final RTIambassador lrc;

    public COAExecutor(String federationId, String federateId, RTIambassador lrc) {
        this.federationId = federationId;
        this.federateId = federateId;
        this.lrc = lrc;
    }

    public void initialize(String coaDefinitionFilePath, String coaSelectionFilePath) {

    }

}
