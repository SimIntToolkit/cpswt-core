package org.cpswt.coa.enums;

public enum COANodeStatus {
    Inactive("Inactive"),
    Active("Active"),
    Executed("Executed");

    private String name;
    COANodeStatus(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}
