package c2w.host.api;

/**
 * Represents the JSON data to control the federation manager.
 */
public enum ControlAction {
    START(1),
    PAUSE(2),
    RESUME(4),
    TERMINATE(8);

    int value;
    ControlAction(int value) { this.value = value; }
}
