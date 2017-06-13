package c2w.utils;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents the FederateID generator
 */
public class FederateIdUtility {

    private static boolean useUUID = true;

    private static final String uuidPatternStr = "(.*)-([0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12})";
    private static final Pattern uuidPattern = Pattern.compile(uuidPatternStr);

    // TODO: later we can support increasing integers per type

    public static String generateID(String federateType) {
        if(useUUID) {
            return String.format("%s-%s", federateType, UUID.randomUUID());
        }
        else {
            return String.format("%s", federateType);
        }
    }

    /**
     * Extracts the federateType from the federateId.
     * @param federateId The unique identifier of the federate.
     * @return The federate type.
     */
    public static String getFederateType(String federateId) {
        if(useUUID) {
            Matcher m = uuidPattern.matcher(federateId);
            if(m.find()) {
                return m.group(1);
            }
            else {
                return federateId;
            }
        }
        else {
            return federateId;
        }
    }
}
