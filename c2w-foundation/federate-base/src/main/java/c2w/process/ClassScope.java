package c2w.process;

import java.lang.reflect.*;
import java.util.*;

public class ClassScope {
    
	private static Field LIBRARIES;
    
    static {
    	try {
	        LIBRARIES = ClassLoader.class.getDeclaredField("loadedLibraryNames");
	        LIBRARIES.setAccessible(true);
    	} catch(Exception e) {
    		e.printStackTrace();
    	}
    }

    public ClassScope() {
		// TODO Auto-generated constructor stub
	}
    
    @SuppressWarnings("unchecked")
	public static String[] getLoadedLibraries(final ClassLoader loader) {
    	if(LIBRARIES != null) {
	    	try {
		        final Vector<String> libraries = (Vector<String>) LIBRARIES.get(loader);
		        return libraries.toArray(new String[] {});
	    	} catch (Exception e) {
	    		e.printStackTrace();
	    	}
    	}
    	return new String[] {};
    }
}
