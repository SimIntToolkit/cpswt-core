package c2w.hla;

/**
 * Represents the parameter object for a federate.
 */
public class FederateParameter {
    public int federateRTIInitWaitTimeMs = 20;
    public String federateType;
    public String federationId;
    public boolean isLateJoiner;
    public double lookAhead;
    public double stepSize;

    public FederateParameter(String federateType, String federationId, boolean isLateJoiner, double lookAhead, double stepSize) {
        this.federateType = federateType;
        this.federationId = federationId;
        this.isLateJoiner = isLateJoiner;
        this.lookAhead = lookAhead;
        this.stepSize = stepSize;
    }
}
