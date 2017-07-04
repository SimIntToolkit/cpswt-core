package org.cpswt.hla.base;

import java.util.concurrent.PriorityBlockingQueue;

public class ATRQueue extends PriorityBlockingQueue<AdvanceTimeRequest> {

    public static final long serialVersionUID = 1;
    public ATRQueue(int size, ATRComparator tatComparator) {
        super(size, tatComparator);
    }
}