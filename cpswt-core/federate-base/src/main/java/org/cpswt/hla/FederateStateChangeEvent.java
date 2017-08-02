package org.cpswt.hla;

import java.util.EventObject;

/**
 * Represents the change object of a FederateState change event.
 */
public class FederateStateChangeEvent extends EventObject {
    FederateState prevState;
    FederateState newState;

    public FederateStateChangeEvent(Object source, FederateState prevState, FederateState newState) {
        super(source);

        this.prevState = prevState;
        this.newState = newState;
    }

    public FederateState getPrevState() {
        return prevState;
    }

    public FederateState getNewState() {
        return newState;
    }
}
