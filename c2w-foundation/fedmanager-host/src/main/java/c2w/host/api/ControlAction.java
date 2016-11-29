package c2w.host.api;

import c2w.hla.FederateState;

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

    public FederateState getTargetState() {
        switch (this) {
            case START:
                return FederateState.STARTING;
            case PAUSE:
                return FederateState.PAUSED;
            case RESUME:
                return FederateState.RESUMED;
            case TERMINATE:
                return FederateState.TERMINATING;
            default:
                return FederateState.STARTING;
        }
    }
}
