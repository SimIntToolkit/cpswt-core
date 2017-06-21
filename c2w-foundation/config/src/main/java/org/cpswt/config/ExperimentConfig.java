package org.cpswt.config;

import java.util.List;

/**
 * Experiment config
 */
public class ExperimentConfig {
    public List<String> federateTypesAllowed;
    public List<FederateJoinInfo> expectedFederates;
    public List<FederateJoinInfo> lateJoinerFederates;

    public int expectedFederateItemsCount() {
        int cnt = this.expectedFederates
                .stream()
                .mapToInt(o -> o.count)
                .sum();
        return cnt;
    }
}
