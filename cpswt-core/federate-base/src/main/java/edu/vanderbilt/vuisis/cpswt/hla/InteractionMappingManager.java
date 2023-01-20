package edu.vanderbilt.vuisis.cpswt.hla;

import java.util.*;

public class InteractionMappingManager {

    private final Map<String, List<InteractionMappingBase>>
            hlaClassNameToInteractionMappingBaseList = new HashMap<>();

    private static final InteractionMappingManager singletonInstance = new InteractionMappingManager();
    public static InteractionMappingManager getInstance() {
        return singletonInstance;
    }

    public void addInteractionMapping(InteractionMappingBase interactionMappingBase) {
        for(String hlaClassName: interactionMappingBase.getRequiredHlaClassNameSet()) {
            if (!hlaClassNameToInteractionMappingBaseList.containsKey(hlaClassName)) {
                hlaClassNameToInteractionMappingBaseList.put(hlaClassName, new ArrayList<>());
            }
            hlaClassNameToInteractionMappingBaseList.get(hlaClassName).add(interactionMappingBase);
        }
    }

    // ONLY ACCESS SINGLETON INSTANCE
    private InteractionMappingManager() {}

    /**
     * This method adds all the mappings associated with an interaction.
     *
     * @param interactionRoot
     */
    public void addInteraction(
            InteractionRoot interactionRoot, SynchronizedFederate synchronizedFederate
    ) throws Exception {

        // HLA CLASS NAME OF INTERACTION
        String hlaClassName = interactionRoot.getInstanceHlaClassName();
        if (hlaClassNameToInteractionMappingBaseList.containsKey(hlaClassName)) {
            for (InteractionMappingBase interactionMappingBase : hlaClassNameToInteractionMappingBaseList.get(hlaClassName)) {
                interactionMappingBase.addInteraction(interactionRoot, synchronizedFederate);
            }
        }
    }
}
