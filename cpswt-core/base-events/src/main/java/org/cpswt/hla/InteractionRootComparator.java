package org.cpswt.hla;

import java.util.Comparator;
import org.cpswt.hla.InteractionRoot_p.C2WInteractionRoot;

public class InteractionRootComparator implements Comparator<InteractionRoot> {
    public int compare(InteractionRoot interactionRoot1, InteractionRoot interactionRoot2) {

        final int timeCompare = Double.compare(interactionRoot1.getTime(), interactionRoot2.getTime());
        if (timeCompare != 0) {
            return timeCompare;
        }

        final int actualLogicalGenerationTimeCompare = Double.compare(
                ((C2WInteractionRoot)interactionRoot1).get_actualLogicalGenerationTime(),
                ((C2WInteractionRoot)interactionRoot2).get_actualLogicalGenerationTime()
        );
        if (actualLogicalGenerationTimeCompare != 0) {
            return actualLogicalGenerationTimeCompare;
        }

        return Integer.compare(interactionRoot1.getUniqueID(), interactionRoot2.getUniqueID());
    }
}
