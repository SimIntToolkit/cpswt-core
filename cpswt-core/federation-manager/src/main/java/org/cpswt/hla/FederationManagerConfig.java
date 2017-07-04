package org.cpswt.hla;

import org.cpswt.config.FederateConfig;
import org.cpswt.config.FederateParameter;

public class FederationManagerConfig extends FederateConfig {
    /**
     * The host to bind the RESTful API.
     */
    @FederateParameter
    public String bindHost;

    /**
     * The port where the RESTful API listens.
     */
    @FederateParameter
    public int port;

    /**
     * The endpoint where federation manager control is done.
     */
    @FederateParameter
    public String controlEndpoint;

    /**
     * The endpoint where federation queries can be executed.
     */
    @FederateParameter
    public String federatesEndpoint;

    /**
     * Indicates if federation manager should autostart.
     */
    @Deprecated
    @FederateParameter
    public boolean autoStart;

    /**
     * Indicates the federation end time.
     */
    @FederateParameter
    public double federationEndTime;

    /**
     * Indicates if the federation manager should run in real time mode.
     */
    @FederateParameter
    public boolean realTimeMode;

    /**
     * The fed file for RTI.
     */
    @FederateParameter
    public String fedFile;

    /**
     * Represents the configuration of the experiment.
     */
    @FederateParameter
    public String experimentConfig;

    /**
     * Indicates if the manager should terminate on COA finish.
     */
    @Deprecated
    @FederateParameter
    // TODO: move COA-related stuff from script.xml to COA.json
    public boolean terminateOnCOAFinish;

}
