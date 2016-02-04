package c2w.process;

import java.lang.reflect.*;
import java.util.*;
import c2w.process.ClassScope;

public class ProcessId {

	private static final String c2wtRoot = System.getenv("C2WTROOT");
	private static final String processIdSoPath = c2wtRoot + "/core/src/cpp/ProcessId";
	private static final String processIdLibFullPath = processIdSoPath + "/libProcessId.so";
	private static boolean INIT_CALLED = false;
	private static boolean DEBUG_MESSAGES_ON = false;

	public native int setProcessGroupId();

	public ProcessId() {
		ProcessId.INIT_CALLED = true;
		if(ProcessId.DEBUG_MESSAGES_ON) System.out.println("Updated isInitNotCalled = " + !ProcessId.INIT_CALLED);

		// Edit the system library path
		System.setProperty("java.library.path", processIdSoPath );
		try {
			Field fieldSysPath = ClassLoader.class.getDeclaredField( "sys_paths" );
			fieldSysPath.setAccessible( true );
			fieldSysPath.set( null, null );
		} catch(Exception e1) {
			e1.printStackTrace();
		}

		// Check if the library is already loaded
		final String[] libraries = ClassScope.getLoadedLibraries(ClassLoader.getSystemClassLoader());
		if(ProcessId.DEBUG_MESSAGES_ON) System.out.println("Libraries loaded are:\n");
		if(ProcessId.DEBUG_MESSAGES_ON) {for(String aLib: libraries) System.out.println("\t" + aLib);}
		boolean processIdLibraryAlreadyLoaded = false;
		if(libraries != null && libraries.length > 0 && Arrays.asList(libraries).contains(processIdLibFullPath)) {
			processIdLibraryAlreadyLoaded = true;
			if(ProcessId.DEBUG_MESSAGES_ON) System.out.println("ProcessId library already loaded, so no need to load");
		}
		
		// If not, try loading the library
		if(!processIdLibraryAlreadyLoaded) {
			try {
				System.loadLibrary("ProcessId");
			} catch (java.lang.UnsatisfiedLinkError e) {
				System.out.println("Error while loading the library libProcessId.so (it can't be loaded)");
				System.out.println("java.library.path = \n\t" + System.getProperty("java.library.path"));
				e.printStackTrace();
			}
		}
	}
	
	public static boolean isInitNotCalled() {
		boolean initNotCalled = !ProcessId.INIT_CALLED;
		if(ProcessId.DEBUG_MESSAGES_ON) System.out.println("Returning isInitNotCalled = " + initNotCalled);
		return initNotCalled;
	}
	
	public static void main(String args[]) throws Exception {
		ProcessId processId = new ProcessId();
		if(ProcessId.DEBUG_MESSAGES_ON) System.out.println("Setting process group ID to process ID (" + processId.setProcessGroupId() + ")");
	}
}
