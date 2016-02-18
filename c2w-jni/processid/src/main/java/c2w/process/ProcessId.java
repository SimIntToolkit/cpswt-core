package c2w.process;


public class ProcessId {
	
	static
    {
        NarSystem.loadLibrary();
    }
	
	private static boolean DEBUG_MESSAGES_ON = true;

	public native int setProcessGroupId();

	public ProcessId() {
	}
	
	public static void main(String args[]) throws Exception {
		ProcessId processId = new ProcessId();
		if(ProcessId.DEBUG_MESSAGES_ON) System.out.println("Setting process group ID to process ID (" + processId.setProcessGroupId() + ")");
	}
}
