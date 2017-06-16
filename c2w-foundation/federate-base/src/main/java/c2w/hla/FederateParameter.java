package c2w.hla;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.cli.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * Represents the parameter object for a federate.
 */
public class FederateParameter {

    @JsonIgnore
    static final Logger logger = LogManager.getLogger(FederateParameter.class);

    /**
     * Time to wait before acquiring RTI for the first time (in milliseconds)
     */
    public int federateRTIInitWaitTimeMs = 20;

    /**
     * The type of the Federate (i.e.: the model name).
     */
    public String federateType;

    /**
     * The unique identifier of the federation.
     */
    public String federationId;

    /**
     * Indicates if current federate is a late joiner.
     */
    public boolean isLateJoiner;

    /**
     * The lookahead value.
     */
    public double lookAhead;

    /**
     * The step size value.
     */
    public double stepSize;

    /**
     * Default constructor for FederateParameter.
     */
    public FederateParameter() {}

    /**
     * Creates a new FederateParameter instance.
     * @param federateType The type of the Federate (i.e.: the model name).
     * @param federationId The unique identifier of the federation.
     * @param isLateJoiner Indicates if current federate is a late joiner.
     * @param lookAhead The lookahead value.
     * @param stepSize The step size value.
     */
    public FederateParameter(String federateType, String federationId, boolean isLateJoiner, double lookAhead, double stepSize) {
        this.federateType = federateType;
        this.federationId = federationId;
        this.isLateJoiner = isLateJoiner;
        this.lookAhead = lookAhead;
        this.stepSize = stepSize;
    }

    /**
     * Helper to determine what command line arguments we support as the default federate parameters.
     * @return The command line argument options.
     */
    public static Options getDefaultCLIOptions() {
        Options options = new Options();

        options.addOption(Option.builder("c")
                .longOpt("configFile")
                .argName("configFile")
                .desc("Configuration file path")
                .hasArg()
                .required(false)
                .type(String.class)
                .build()
        );

        options.addOption(Option.builder()
                .longOpt("federateRTIInitWaitTimeMs")
                .argName("federateRTIInitWaitTimeMs")
                .desc("Time to wait for RTI in milliseconds")
                .hasArg()
                .required(false)
                .type(Integer.class)
                .build()
        );

        options.addOption(Option.builder()
                .longOpt("federateType")
                .argName("federateType")
                .desc("The type of the federate")
                .hasArg()
                .required(false)
                .type(String.class)
                .build()
        );

        options.addOption(Option.builder()
                .longOpt("federationId")
                .argName("federationId")
                .desc("The identifier of the federation the federate belongs to/will join")
                .hasArg()
                .required(false)
                .type(String.class)
                .build()
        );

        options.addOption(Option.builder()
                .longOpt("isLateJoiner")
                .argName("isLateJoiner")
                .desc("Indicates if the federate is a late joiner")
                .hasArg()
                .required(false)
                .type(Boolean.class)
                .build()
        );

        options.addOption(Option.builder()
                .longOpt("lookAhead")
                .argName("lookAhead")
                .desc("The lookahead value")
                .hasArg()
                .required(false)
                .type(Double.class)
                .build()
        );

        options.addOption(Option.builder()
                .longOpt("stepSize")
                .argName("stepSize")
                .desc("The step size value")
                .hasArg()
                .required(false)
                .type(Double.class)
                .build()
        );

        return options;
    }
}
