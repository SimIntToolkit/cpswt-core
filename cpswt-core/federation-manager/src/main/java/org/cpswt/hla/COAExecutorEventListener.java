package org.cpswt.hla;

/**
 * COAExecutorEventListeren
 */
public interface COAExecutorEventListener {
    void onTerminateRequested();
    double onCurrentTimeRequested();
}
