package org.cpswt.hla;

import java.util.Comparator;

public class InteractionRootComparator implements Comparator<InteractionRoot> {
    public int compare(InteractionRoot interactionRoot1, InteractionRoot interactionRoot2) {

        C2WInteractionRoot c2wIR1 = (C2WInteractionRoot) interactionRoot1;
        C2WInteractionRoot c2wIR2 = (C2WInteractionRoot) interactionRoot2;
        double agtIR1 = c2wIR1.get_actualLogicalGenerationTime();
        double agtIR2 = c2wIR2.get_actualLogicalGenerationTime();

        if (interactionRoot1.getTime() < interactionRoot2.getTime()) {
            return -1;
        }
        if (interactionRoot1.getTime() > interactionRoot2.getTime()) {
            return 1;
        }
        if (agtIR1 < agtIR2) {
            return -1;
        }
        if (agtIR1 > agtIR2) {
            return 1;
        }
        if (interactionRoot1.getUniqueID() < interactionRoot2.getUniqueID()) {
            return -1;
        }
        if (interactionRoot1.getUniqueID() > interactionRoot2.getUniqueID()) {
            return 1;
        }

        return 0;
    }
}
