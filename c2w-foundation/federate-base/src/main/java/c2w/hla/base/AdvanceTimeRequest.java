package c2w.hla.base;

/**
 * A thread in a federate uses an object of this class to request a federation
 * time to which the AdvanceTimeThread should advance.  Once the time is
 * reached, the federate thread uses this object to synchronize its execution
 * with the AdvanceTimeThread.  That is, the AdvanceTimeThread is prevented
 * from advancing the time any further until the federate thread has completed
 * its processing for this time.
 * See {@link AdvanceTimeThread}.
 *
 * @author Harmon Nine
 */
public class AdvanceTimeRequest {
    private static Object object = new Object();

    private double _requestedTime;
    private double _currentTime = -1;
    private SyncQueue _syncQueue;

    /**
     * Creates a new AdvanceTimeRequest with a new (and unique) SyncQueue.
     *
     * @param requestedTime time at which the federate wishes to perform
     *                      processing for the simulation.
     */
    public AdvanceTimeRequest(double requestedTime) {
        _requestedTime = requestedTime;
        _syncQueue = new SyncQueue();
    }

    /**
     * Creates a new AdvanceTimeRequest with a supplied SyncQueue.
     *
     * @param requestedTime time at which the federate wishes to perform
     *                      processing for the simulation.
     * @param syncQueue     SyncQueue
     */
    public AdvanceTimeRequest(double requestedTime, SyncQueue syncQueue) {
        _requestedTime = requestedTime;
        _syncQueue = syncQueue;
    }

    /**
     * returns the time to which the federate thread has requested the
     * AdvanceTimeThread advance using this AdvanceTimeRequest object.
     *
     * @return time to which the federate thread has requested the
     * AdvanceTimeThread advance using this AdvanceTimeRequest object
     */
    public double getRequestedTime() {
        return _requestedTime;
    }

    /**
     * returns the time at which this AdvanceTimeRequest object is actually
     * processed by the AdvanceTimeThread.  Usually, this is the same as the
     * requested time (see {@link #getRequestedTime()}).  However, the current
     * time can be greater than the requested time -- this only happens if the
     * requested time is previous to the current federation time to begin with,
     * and is usually the result of some kind of error in processing by the
     * federate that created and is using this AdvanceTimeRequest.
     *
     * @return time at which the AdvanceTimeRequest is processed by the
     * AdvanceTimeThread
     */
    public double getCurrentTime() {
        return _currentTime;
    }

    /**
     * Called by the AdvanceTimeThread ONLY, this method coordinates the
     * AdvanceTimeThread with the federate thread that created and is using
     * this AdvanceTimeRequest object.
     * <p/>
     * Usually, after submitting this AdvanceTimeRequest object to the
     * AdvanceTimeThread, a federate thread calls {@link #requestSyncStart()}
     * on this object.  This causes the federate to suspend execution until
     * the AdvanceTimeThread advances the federate time to the requested time
     * in the AdvanceTimeRequest object.  Once the AdvanceTimeThread does
     * this, it calls this method (that is, threadSyncStart( double )) on
     * the AdvanceTimeRequest object.  This causes the federate thread to
     * resume execution and perform the processing it needs to at the requested
     * time (see {@link AdvanceTimeThread}).
     *
     * @param currentTime the time at which the AdvanceTimeThread actually
     *                    processed this AdvanceTimeRequest object.
     */
    public void threadSyncStart(double currentTime) {
        _currentTime = currentTime;
        threadSyncEnd();
    }

    /**
     * Called by the AdvanceTimeThread ONLY, this method coordinates the
     * AdvanceTimeThread with the federate thread that created and is using
     * this AdvanceTimeRequest object.
     * <p>
     * After calling the {@link #threadSyncStart(double)} method, the
     * {@link AdvanceTimeThread} immediately calls this method (that is, threadSyncEnd()).
     * This causes it to suspend its execution.  It resumes its execution
     * when the federate thread using this AdvanceTimeRequest object calls
     * {@link #requestSyncEnd()} on this object, indicating that it has completed
     * the processing it needed to perform at the requested time of this
     * AdvanceTimeRequest object.
     */
    public void threadSyncEnd() {
        boolean putNotExecuted = true;
        while (putNotExecuted) {
            try {
                _syncQueue.put(object);
                putNotExecuted = false;
            } catch (InterruptedException i) {
            }
        }
    }

    /**
     * Called by a federate thread to suspend its execution until the
     * {@link AdvanceTimeThread} advances the federation time to the time
     * requested in this AdvanceTimeRequest object.
     */
    public void requestSyncStart() {
        boolean takeNotExecuted = true;
        while (takeNotExecuted) {
            try {
                _syncQueue.take();
                takeNotExecuted = false;
            } catch (InterruptedException i) {
            }
        }
    }

    /**
     * Called by a federate thread to indicate to the {@link AdvanceTimeThread}
     * that it has completed the processing it needed to perform at the time
     * requested in this AdvanceTimeRequest object.
     */
    public void requestSyncEnd() {
        _currentTime = -1;
        requestSyncStart();
    }
}