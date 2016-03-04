package c2w.hla;

import java.io.File;

import hla.rti.jlc.NullFederateAmbassador;
import hla.rti.jlc.RtiFactory;
import hla.rti.jlc.RtiFactoryFactory;
import hla.rti.FederationExecutionAlreadyExists;
import hla.rti.RTIambassador;

public class TestFederationRunning extends NullFederateAmbassador  {

	/**
	 * @param args
	 */
	
	public void test( String federation_id, String FOM_file_name ) {
		RTIambassador rti = null;
		
        File fom_file = new File( FOM_file_name );
		
		try {
			RtiFactory factory = RtiFactoryFactory.getRtiFactory();  
			rti = factory.createRtiAmbassador();
		} catch ( Exception e ) {
			System.exit( 2 );
		}

		try {
		    rti.createFederationExecution( federation_id, fom_file.toURI().toURL() );
		} catch ( FederationExecutionAlreadyExists feae ) {
			System.exit( 3 );
		} catch ( Exception e ) {
			System.exit( 2 );
		}

	}
	
	public static void main(String[] args) {
		
		if ( args.length != 2 ) {
			System.out.println( "Usage:  TestFederationRunning <federation_id> <FOM_file>" );
			System.exit( 1 );
		}
		String federate_id = args[ 0 ];
		String federation_id = args[ 1 ];

		TestFederationRunning testFederationRunning = new TestFederationRunning();
		testFederationRunning.test( federate_id, federation_id );
		
		System.exit( 0 );
	}

}
