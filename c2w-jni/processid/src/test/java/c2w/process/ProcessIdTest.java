package c2w.process;

import org.junit.Assert;
import org.junit.Test;

public class ProcessIdTest {
	
	@Test public final void testProcessIdJNI()
	        throws Exception
	    {
		
			ProcessId processId = new ProcessId();
			System.out.println("The process id is :" + processId.setProcessGroupId() );
	        Assert.assertNotNull("Process Group Id set!", processId.setProcessGroupId() );
	    }
	
}
