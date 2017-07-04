package org.cpswt.hla.base;

import java.util.Comparator;

public class ObjectReflectorComparator implements Comparator<ObjectReflector> {
    public int compare(ObjectReflector objectReflection1, ObjectReflector objectReflection2) {
        if (objectReflection1.getTime() < objectReflection2.getTime()) return -1;
        if (objectReflection1.getTime() > objectReflection2.getTime()) return 1;

        if (objectReflection1.getUniqueID() < objectReflection2.getUniqueID()) return -1;
        if (objectReflection1.getUniqueID() > objectReflection2.getUniqueID()) return 1;

        return 0;
    }
}