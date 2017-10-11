package org.cpswt.coa;

public enum COANodeType {

    Action("Action"),
    Outcome("Outcome"),
    SyncPoint("SyncPoint"),
    AwaitN("AwaitN"),
    Fork("Fork"),
    ProbabilisticChoice("ProbabilisticChoice"),
    Duration("Duration"),
    RandomDuration("RandomDuration"),
    OutcomeFilter("OutcomeFilter"),
    TerminateCOA("TerminateCOA"),
    TerminateSimulation("TerminateSimulation");

    private String name;

    COANodeType(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}
