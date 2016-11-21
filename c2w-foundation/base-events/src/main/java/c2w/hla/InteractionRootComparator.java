package c2w.hla;

import java.util.Comparator;

public class InteractionRootComparator implements Comparator<InteractionRoot> {
    public int compare(InteractionRoot interactionRoot1, InteractionRoot interactionRoot2) {
        // System.out.println("Comparing IR1 and IR2");
        // System.out.println("IR1 = " + interactionRoot1);
        // System.out.println("IR2 = " + interactionRoot2);

        C2WInteractionRoot c2wIR1 = (C2WInteractionRoot) interactionRoot1;
        C2WInteractionRoot c2wIR2 = (C2WInteractionRoot) interactionRoot2;
        double agtIR1 = c2wIR1.get_actualLogicalGenerationTime();
        double agtIR2 = c2wIR2.get_actualLogicalGenerationTime();

        // System.out.println("IR1-ID = " + interactionRoot1.getUniqueID() + ", IR2-ID = " + interactionRoot2.getUniqueID());
        // System.out.println("IR1-Time = " + interactionRoot1.getTime() + ", IR2-Time = " + interactionRoot2.getTime());
        // System.out.println("IR1-ActualGenerationTime = " + agtIR1 + ", IR2-ActualGenerationTime = " + agtIR2);

        if (interactionRoot1.getTime() < interactionRoot2.getTime()) {
            // System.out.println("IR1-time < IR2-time, so returning -1");
            return -1;
        }
        if (interactionRoot1.getTime() > interactionRoot2.getTime()) {
            // System.out.println("IR1-time > IR2-time, so, returning 1");
            return 1;
        }
        if (agtIR1 < agtIR2) {
            // System.out.println("IR1-actualGenerationTime < IR2-actualGenerationTime, so returning -1");
            return -1;
        }
        if (agtIR1 > agtIR2) {
            // System.out.println("IR1-actualGenerationTime > IR2-actualGenerationTime, so returning 1");
            return 1;
        }
        if (interactionRoot1.getUniqueID() < interactionRoot2.getUniqueID()) {
            // System.out.println("IR1-uniqueID < IR2-uniqueID, so returning -1");
            return -1;
        }
        if (interactionRoot1.getUniqueID() > interactionRoot2.getUniqueID()) {
            // .println("IR1-uniqueID > IR2-uniqueID, so returning 1");
            return 1;
        }

        // System.out.println("No difference at all between IR1 and IR2, so returning 0");
        return 0;
    }
}
