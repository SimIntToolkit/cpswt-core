package c2w.hla;

/**
 * Experiment config
 */
public class ExperimentConfig {

    public class ExpectedFederateInfo {
        public String federateType;
        public int count;
    }

    public class LateJoinerFederateInfo {
        public String federateType;
        public int maxCount;
    }

    public String[] federateTypesAllowed;
    public ExpectedFederateInfo[] expectedFederates;
    public LateJoinerFederateInfo[] lateJoinerFederates;
}
