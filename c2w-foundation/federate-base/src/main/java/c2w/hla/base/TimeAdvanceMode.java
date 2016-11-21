package c2w.hla.base;

public enum TimeAdvanceMode {
    TimeAdvanceRequest("TimeAdvanceRequest"),
    TimeAdvanceRequestAvailable("TimeAdvanceRequestAvailable"),
    NextEventRequest("NextEventRequest"),
    NextEventRequestAvailable("NextEventRequestAvailable");

    private String name;
    TimeAdvanceMode(String name) { this.name = name; }

    public String getName() {
        return this.name;
    }

    @Override
    public String toString() { return this.name; }
}
