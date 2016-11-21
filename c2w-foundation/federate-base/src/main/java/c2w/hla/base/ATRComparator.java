package c2w.hla.base;

import java.util.Comparator;

public class ATRComparator implements Comparator<AdvanceTimeRequest> {
    public int compare(AdvanceTimeRequest t1, AdvanceTimeRequest t2) {
        return (int) Math.signum(t1.getRequestedTime() - t2.getRequestedTime());
    }
}
