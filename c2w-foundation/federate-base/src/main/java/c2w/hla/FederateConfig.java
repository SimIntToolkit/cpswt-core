package c2w.hla;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.cli.*;

import org.apache.commons.lang3.ClassUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;

/**
 * Represents the parameter object for a federate.
 */
public class FederateConfig {

    @JsonIgnore
    static final Logger logger = LogManager.getLogger(FederateConfig.class);

    /**
     * Time to wait before acquiring RTI for the first time (in milliseconds)
     */
    @FederateParameter
    public int federateRTIInitWaitTimeMs = 20;

    /**
     * The type of the Federate (i.e.: the model name).
     */
    @FederateParameter
    public String federateType;

    /**
     * The unique identifier of the federation.
     */
    @FederateParameter
    public String federationId;

    /**
     * Indicates if current federate is a late joiner.
     */
    @FederateParameter
    public boolean isLateJoiner;

    /**
     * The lookahead value.
     */
    @FederateParameter
    public double lookAhead;

    /**
     * The step size value.
     */
    @FederateParameter
    public double stepSize;

    /**
     * Default constructor for FederateConfig.
     */
    public FederateConfig() {}

    /**
     * Creates a new FederateConfig instance.
     * @param federateType The type of the Federate (i.e.: the model name).
     * @param federationId The unique identifier of the federation.
     * @param isLateJoiner Indicates if current federate is a late joiner.
     * @param lookAhead The lookahead value.
     * @param stepSize The step size value.
     */
    public FederateConfig(String federateType, String federationId, boolean isLateJoiner, double lookAhead, double stepSize) {
        this.federateType = federateType;
        this.federationId = federationId;
        this.isLateJoiner = isLateJoiner;
        this.lookAhead = lookAhead;
        this.stepSize = stepSize;
    }
}
