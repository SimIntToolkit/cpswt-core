package c2w.hla.base;

/**
 * Synchronous Queue class that the AdvanceTimeThread uses to synchronize
 * itself with the threads of the federate that are interacting with the RTI.
 *
 * @author Harmon Nine
 */
import java.util.concurrent.SynchronousQueue;

public class SyncQueue extends SynchronousQueue<Object> {
    public final static long serialVersionUID = 1;
}