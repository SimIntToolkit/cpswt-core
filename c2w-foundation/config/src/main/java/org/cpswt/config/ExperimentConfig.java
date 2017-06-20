package org.cpswt.config;

import java.util.List;

/**
 * Experiment config
 */
public class ExperimentConfig {
    public List<String> federateTypesAllowed;
    public List<ExpectedFederateInfo> expectedFederates;
    public List<LateJoinerFederateInfo> lateJoinerFederates;

    public int expectedFederateItemsCount() {
        int cnt = 0;
        for(ExpectedFederateInfo e : expectedFederates) {
            cnt += e.count;
        }
        return cnt;
    }
}
