package c2w.hla;

/**
 * COAExecutorEventListeren
 */
public interface COAExecutorEventListener {
    void onTerminateRequested();
    double onCurrentTimeRequested();
}
