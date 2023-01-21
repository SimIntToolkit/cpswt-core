package edu.vanderbilt.vuisis.cpswt.hla;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract class InteractionMappingBase {

    private final static Logger log = LogManager.getLogger();

    protected final Set<String> requiredHlaClassNameSet = new HashSet<>();

    private final Set<String> neededHlaClassNameSet = new HashSet<>();

    protected final Map<String, InteractionRoot> hlaClassNameToInteractionMap = new HashMap<>();

    protected abstract void initialize();
            ;
    private void reset() {
        neededHlaClassNameSet.clear();
        neededHlaClassNameSet.addAll(requiredHlaClassNameSet);

    }

    public InteractionMappingBase() {
        initialize();
        reset();
    }

    private boolean checkExecution() {
        return neededHlaClassNameSet.isEmpty();
    }

    public void addInteraction(
            InteractionRoot interactionRoot, SynchronizedFederate synchronizedFederate
    ) throws Exception {
        String hlaClassName = interactionRoot.getInstanceHlaClassName();
        if (requiredHlaClassNameSet.contains(hlaClassName)) {
            if (neededHlaClassNameSet.contains(hlaClassName)) {
                log.info(
                        "Object of class \"{}\" adding interaction of type \"{}\"",
                        getClass().getName(), hlaClassName
                );
                neededHlaClassNameSet.remove(hlaClassName);
            } else {
                log.info(
                        "Object of class \"{}\" replacing interaction of type \"{}\"",
                        getClass().getName(), hlaClassName
                );
            }
            hlaClassNameToInteractionMap.put(hlaClassName, interactionRoot);
            if (checkExecution()) {
                execute(synchronizedFederate);
                reset();
            }
        } else {
            log.warn(
                    "Object of class \"{}\" has received unneeded interaction of type \"{}\"",
                    getClass().getName(), hlaClassName
            );
        }
    }


    public Set<String> getRequiredHlaClassNameSet() {
        return new HashSet<>(requiredHlaClassNameSet);
    }

    public abstract void execute(SynchronizedFederate synchronizedFederate) throws Exception;

    public abstract String getInteractionMappingName();

    public interface Factory {
        InteractionMappingBase create();
    }
}