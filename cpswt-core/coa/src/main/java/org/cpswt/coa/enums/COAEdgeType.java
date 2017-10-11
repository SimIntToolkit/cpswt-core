package org.cpswt.coa.enums;

public enum COAEdgeType {
    COAFlow("COAFlow"),
    COAFlowWithProbability("COAFlowWithProbability"),
    OutcomeToFilter("OutcomeToFilter"),
    Filter2COAElement("Filter2COAElement"),
    COAException("COAException");

    private String name;
    COAEdgeType(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}
