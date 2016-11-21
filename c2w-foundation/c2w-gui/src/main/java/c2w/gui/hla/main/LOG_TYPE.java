package c2w.gui.hla.main;

public enum LOG_TYPE {
    LOG_TYPE_HIGH("High"),
    LOG_TYPE_MEDIUM("Medium"),
    LOG_TYPE_LOW("Low"),
    LOG_TYPE_VERY_LOW("Very low");

    private String name;

    LOG_TYPE(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}