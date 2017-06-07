package c2w.utils;

import java.util.UUID;

/**
 * Represents the FederateID generator
 */
public class FederateIdGenerator {

    boolean useUUID = true;

    // TODO: later we can support increasing integers per type

    public static String generateID(String federateType) {
        return String.format("%s-%s", federateType, UUID.randomUUID());
    }
}
