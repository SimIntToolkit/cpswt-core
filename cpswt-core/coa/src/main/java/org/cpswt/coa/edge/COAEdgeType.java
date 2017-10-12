package org.cpswt.coa.edge;

import java.util.HashMap;

public enum COAEdgeType {
    COAFlow("COAFlow"),
    COAFlowWithProbability("COAFlowWithProbability"),
    OutcomeToFilter("OutcomeToFilter"),
    Filter2COAElement("Filter2COAElement"),
    COAException("COAException"),
    Unknown("Unknown");

    static HashMap<COAEdgeType, Class> classMapping;
    static {
        classMapping = new HashMap<>();
        classMapping.put(COAEdgeType.COAFlow, COAEdge.class); // ???
        classMapping.put(COAEdgeType.COAFlowWithProbability, COAFlowWithProbabilityEdge.class);
        classMapping.put(COAEdgeType.COAException, COAEdge.class); // ???
    }

    private String name;
    COAEdgeType(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public Class<COAEdge> getCOAEdgeClass() {
        return classMapping.get(this);
    }
}
