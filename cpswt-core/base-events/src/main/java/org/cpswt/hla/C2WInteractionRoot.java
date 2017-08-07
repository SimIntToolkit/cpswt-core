package org.cpswt.hla;

import java.util.HashSet;
import java.util.Set;

import hla.rti.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cpswt.utils.CpswtDefaults;
import org.cpswt.utils.CpswtUtils;

/**
* The C2WInteractionRoot class implements the C2WInteractionRoot interaction in the
* org.cpswt.hla simulation.
*/
public class C2WInteractionRoot extends InteractionRoot {

	private static final Logger logger = LogManager.getLogger(C2WInteractionRoot.class);

	/**
	* Default constructor -- creates an instance of the C2WInteractionRoot interaction
	* class with default parameter values.
	*/
	public C2WInteractionRoot() { }

	private static int _sourceFed_handle;
	private static int _originFed_handle;
	private static int _federateFilter_handle;
	private static int _actualLogicalGenerationTime_handle;

	/**
	* Returns the handle (RTI assigned) of the "sourceFed" parameter of
	* its containing interaction class.
	*
	* @return the handle (RTI assigned) of the "sourceFed" parameter
	*/
	public static int get_sourceFed_handle() { return _sourceFed_handle; }
	
	/**
	* Returns the handle (RTI assigned) of the "originFed" parameter of
	* its containing interaction class.
	*
	* @return the handle (RTI assigned) of the "originFed" parameter
	*/
	public static int get_originFed_handle() { return _originFed_handle; }
	
	/**
	* Returns the handle (RTI assigned) of the "federateFilter" parameter of
	* its containing interaction class.
	*
	* @return the handle (RTI assigned) of the "federateFilter" parameter
	*/
	public static int get_federateFilter_handle() { return _federateFilter_handle; }
	
	/**
	* Returns the handle (RTI assigned) of the "actualLogicalGenerationTime" parameter of
	* its containing interaction class.
	*
	* @return the handle (RTI assigned) of the "actualLogicalGenerationTime" parameter
	*/
	public static int get_actualLogicalGenerationTime_handle() { return _actualLogicalGenerationTime_handle; }
	
	
	
	private static boolean _isInitialized = false;

	private static int _handle;

	/**
	* Returns the handle (RTI assigned) of the C2WInteractionRoot interaction class.
	* Note: As this is a static method, it is NOT polymorphic, and so, if called on
	* a reference will return the handle of the class pertaining to the reference,\
	* rather than the handle of the class for the instance referred to by the reference.
	* For the polymorphic version of this method, use {@link #getClassHandle()}.
	*/
	public static int get_handle() { return _handle; }

	/**
	* Returns the fully-qualified (dot-delimited) name of the C2WInteractionRoot
	* interaction class.
	* Note: As this is a static method, it is NOT polymorphic, and so, if called on
	* a reference will return the name of the class pertaining to the reference,\
	* rather than the name of the class for the instance referred to by the reference.
	* For the polymorphic version of this method, use {@link #getClassName()}.
	*/
	public static String get_class_name() { return "InteractionRoot.C2WInteractionRoot"; }

	/**
	* Returns the simple name (the last name in the dot-delimited fully-qualified
	* class name) of the C2WInteractionRoot interaction class.
	*/
	public static String get_simple_class_name() { return "C2WInteractionRoot"; }

	private static Set< String > _datamemberNames = new HashSet< String >();
	private static Set< String > _allDatamemberNames = new HashSet< String >();

	/**
	* Returns a set containing the names of all of the non-hidden parameters in the
	* C2WInteractionRoot interaction class.
	* Note: As this is a static method, it is NOT polymorphic, and so, if called on
	* a reference will return a set of parameter names pertaining to the reference,\
	* rather than the parameter names of the class for the instance referred to by
	* the reference.  For the polymorphic version of this method, use
	* {@link #getParameterNames()}.
	*/
	public static Set< String > get_parameter_names() {
		return new HashSet< String >( _datamemberNames );
	}


	/**
	* Returns a set containing the names of all of the parameters in the
	* C2WInteractionRoot interaction class.
	* Note: As this is a static method, it is NOT polymorphic, and so, if called on
	* a reference will return a set of parameter names pertaining to the reference,\
	* rather than the parameter names of the class for the instance referred to by
	* the reference.  For the polymorphic version of this method, use
	* {@link #getParameterNames()}.
	*/
	public static Set< String > get_all_parameter_names() {
		return new HashSet< String >( _allDatamemberNames );
	}

	static {
		_classNameSet.add( "InteractionRoot.C2WInteractionRoot" );
		_classNameClassMap.put( "InteractionRoot.C2WInteractionRoot", C2WInteractionRoot.class );
		
		_datamemberClassNameSetMap.put( "InteractionRoot.C2WInteractionRoot", _datamemberNames );
		_allDatamemberClassNameSetMap.put( "InteractionRoot.C2WInteractionRoot", _allDatamemberNames );

		_allDatamemberNames.add( "sourceFed" );
		_allDatamemberNames.add( "originFed" );
		_allDatamemberNames.add( "federateFilter" );
		_allDatamemberNames.add( "actualLogicalGenerationTime" );
		
		_datamemberTypeMap.put( "sourceFed", "String" );
		_datamemberTypeMap.put( "originFed", "String" );
		_datamemberTypeMap.put( "federateFilter", "String" );
		_datamemberTypeMap.put( "actualLogicalGenerationTime", "double" );
	}


	private static String initErrorMessage = "Error:  InteractionRoot.C2WInteractionRoot:  could not initialize:  ";
	protected static void init( RTIambassador rti ) {
		if ( _isInitialized ) return;
		_isInitialized = true;
		
		InteractionRoot.init( rti );
		
		boolean isNotInitialized = true;
		while( isNotInitialized ) {
			try {
				_handle = rti.getInteractionClassHandle( "InteractionRoot.C2WInteractionRoot" );
				isNotInitialized = false;
			} catch ( FederateNotExecutionMember f ) {
				logger.error("{} Federate Not Execution Member", initErrorMessage);
				logger.error(f);
				return;
			} catch ( NameNotFound n ) {
				logger.error("{} Name Not Found", initErrorMessage);
				logger.error(n);
				return;
			} catch ( Exception e ) {
				logger.error(e);
                CpswtUtils.sleepDefault();
			}
		}

		_classNameHandleMap.put( "InteractionRoot.C2WInteractionRoot", get_handle() );
		_classHandleNameMap.put( get_handle(), "InteractionRoot.C2WInteractionRoot" );
		_classHandleSimpleNameMap.put( get_handle(), "C2WInteractionRoot" );


		isNotInitialized = true;
		while( isNotInitialized ) {
			try {

				_sourceFed_handle = rti.getParameterHandle( "sourceFed", get_handle() );
				_originFed_handle = rti.getParameterHandle( "originFed", get_handle() );
				_federateFilter_handle = rti.getParameterHandle( "federateFilter", get_handle() );
				_actualLogicalGenerationTime_handle = rti.getParameterHandle( "actualLogicalGenerationTime", get_handle() );
				isNotInitialized = false;
			} catch ( FederateNotExecutionMember f ) {
				logger.error("{} Federate Not Execution Member", initErrorMessage);
				logger.error(f);
				return;
			} catch ( InteractionClassNotDefined i ) {
				logger.error("{} Interaction Class Not Defined", initErrorMessage);
				logger.error(i);
				return;
			} catch ( NameNotFound n ) {
				logger.error("{} Name Not Found", initErrorMessage);
				logger.error(n);
				return;
			} catch ( Exception e ) {
				logger.error(e);
                CpswtUtils.sleepDefault();
			}
		}


		_datamemberNameHandleMap.put( "InteractionRoot.C2WInteractionRoot,sourceFed", get_sourceFed_handle() );
		_datamemberNameHandleMap.put( "InteractionRoot.C2WInteractionRoot,originFed", get_originFed_handle() );
		_datamemberNameHandleMap.put( "InteractionRoot.C2WInteractionRoot,federateFilter", get_federateFilter_handle() );
		_datamemberNameHandleMap.put( "InteractionRoot.C2WInteractionRoot,actualLogicalGenerationTime", get_actualLogicalGenerationTime_handle() );


		_datamemberHandleNameMap.put( get_sourceFed_handle(), "sourceFed" );
		_datamemberHandleNameMap.put( get_originFed_handle(), "originFed" );
		_datamemberHandleNameMap.put( get_federateFilter_handle(), "federateFilter" );
		_datamemberHandleNameMap.put( get_actualLogicalGenerationTime_handle(), "actualLogicalGenerationTime" );

	}


	private static boolean _isPublished = false;
	private static String publishErrorMessage = "Error:  InteractionRoot.C2WInteractionRoot:  could not publish:  ";

	/**
	* Publishes the C2WInteractionRoot interaction class for a federate.
	*
	* @param rti handle to the Local RTI Component
	*/
	public static void publish( RTIambassador rti ) {
		if ( _isPublished ) return;

		init( rti );



		synchronized( rti ) {
			boolean isNotPublished = true;
			while( isNotPublished ) {
				try {
					rti.publishInteractionClass( get_handle() );
					isNotPublished = false;
				} catch ( FederateNotExecutionMember f ) {
					logger.error("{} Federate Not Execution Member", publishErrorMessage);
					logger.error(f);
					return;
				} catch ( InteractionClassNotDefined i ) {
					logger.error("{} Interaction Class Not Defined", publishErrorMessage);
					logger.error(i);
					return;
				} catch ( Exception e ) {
					logger.error(e);
					//CpswtUtils.sleep(50);
                    CpswtUtils.sleepDefault();
				}
			}
		}

		_isPublished = true;
	}

	private static String unpublishErrorMessage = "Error:  InteractionRoot.C2WInteractionRoot:  could not unpublish:  ";
	/**
	* Unpublishes the C2WInteractionRoot interaction class for a federate.
	*
	* @param rti handle to the Local RTI Component
	*/
	public static void unpublish( RTIambassador rti ) {
		if ( !_isPublished ) return;

		init( rti );
		synchronized( rti ) {
			boolean isNotUnpublished = true;
			while( isNotUnpublished ) {
				try {
					rti.unpublishInteractionClass( get_handle() );
					isNotUnpublished = false;
				} catch ( FederateNotExecutionMember f ) {
					logger.error("{} Federate Not Execution Member", unpublishErrorMessage);
					logger.error(f);
					return;
				} catch ( InteractionClassNotDefined i ) {
					logger.error("{} Interaction Class Not Defined", unpublishErrorMessage);
					logger.error(i);
					return;
				} catch ( InteractionClassNotPublished i ) {
					logger.error("{} Interaction Class Not Published", unpublishErrorMessage);
					logger.error(i);
					return;
				} catch ( Exception e ) {
					logger.error(e);
                    CpswtUtils.sleepDefault();
				}
			}
		}

		_isPublished = false;
	}

	private static boolean _isSubscribed = false;
	private static String subscribeErrorMessage = "Error:  InteractionRoot.C2WInteractionRoot:  could not subscribe:  ";
	/**
	* Subscribes a federate to the C2WInteractionRoot interaction class.
	*
	* @param rti handle to the Local RTI Component
	*/
	public static void subscribe( RTIambassador rti ) {
		if ( _isSubscribed ) return;

		init( rti );


		synchronized( rti ) {
			boolean isNotSubscribed = true;
			while( isNotSubscribed ) {
				try {
					rti.subscribeInteractionClass( get_handle() );
					isNotSubscribed = false;
				} catch ( FederateNotExecutionMember f ) {
					logger.error("{} Federate Not Execution Member", subscribeErrorMessage);
					logger.error(f);
					return;
				} catch ( InteractionClassNotDefined i ) {
					logger.error("{} Interaction Class Not Defined", subscribeErrorMessage);
					logger.error(i);
					return;
				} catch ( Exception e ) {
					logger.error(e);
                    CpswtUtils.sleepDefault();
				}
			}
		}

		_isSubscribed = true;
	}

	private static String unsubscribeErrorMessage = "Error:  InteractionRoot.C2WInteractionRoot:  could not unsubscribe:  ";
	/**
	* Unsubscribes a federate from the C2WInteractionRoot interaction class.
	*
	* @param rti handle to the Local RTI Component
	*/
	public static void unsubscribe( RTIambassador rti ) {
		if ( !_isSubscribed ) return;

		init( rti );
		synchronized( rti ) {
			boolean isNotUnsubscribed = true;
			while( isNotUnsubscribed ) {
				try {
					rti.unsubscribeInteractionClass( get_handle() );
					isNotUnsubscribed = false;
				} catch ( FederateNotExecutionMember f ) {
					logger.error("{} Federate Not Execution Member", unsubscribeErrorMessage);
					logger.error(f);
					return;
				} catch ( InteractionClassNotDefined i ) {
					logger.error("{} Interaction Class Not Defined", unsubscribeErrorMessage);
					logger.error(i);
					return;
				} catch ( InteractionClassNotSubscribed i ) {
					logger.error("{} Interaction Class Not Subscribed", unsubscribeErrorMessage);
					logger.error(i);
					return;
				} catch ( Exception e ) {
					logger.error(e);
                    CpswtUtils.sleepDefault();
				}
			}
		}
		
		_isSubscribed = false;
	}

	/**
	* Return true if "handle" is equal to the handle (RTI assigned) of this class
	* (that is, the C2WInteractionRoot interaction class).
	*
	* @param handle handle to compare to the value of the handle (RTI assigned) of
	* this class (the C2WInteractionRoot interaction class).
	* @return "true" if "handle" matches the value of the handle of this class
	* (that is, the C2WInteractionRoot interaction class).
	*/
	public static boolean match( int handle ) { return handle == get_handle(); }

	/**
	* Returns the handle (RTI assigned) of this instance's interaction class .
	* 
	* @return the handle (RTI assigned) if this instance's interaction class
	*/
	public int getClassHandle() { return get_handle(); }

	/**
	* Returns the fully-qualified (dot-delimited) name of this instance's interaction class.
	* 
	* @return the fully-qualified (dot-delimited) name of this instance's interaction class
	*/
	public String getClassName() { return get_class_name(); }

	/**
	* Returns the simple name (last name in its fully-qualified dot-delimited name)
	* of this instance's interaction class.
	* 
	* @return the simple name of this instance's interaction class 
	*/
	public String getSimpleClassName() { return get_simple_class_name(); }

	/**
	* Returns a set containing the names of all of the non-hiddenparameters of an
	* interaction class instance.
	*
	* @return set containing the names of all of the parameters of an
	* interaction class instance
	*/
	public Set< String > getParameterNames() { return get_parameter_names(); }

	/**
	* Returns a set containing the names of all of the parameters of an
	* interaction class instance.
	*
	* @return set containing the names of all of the parameters of an
	* interaction class instance
	*/
	public Set< String > getAllParameterNames() { return get_all_parameter_names(); }

	/**
	* Publishes the interaction class of this instance of the class for a federate.
	*
	* @param rti handle to the Local RTI Component
	*/
	public void publishInteraction( RTIambassador rti ) { publish( rti ); }

	/**
	* Unpublishes the interaction class of this instance of this class for a federate.
	*
	* @param rti handle to the Local RTI Component
	*/
	public void unpublishInteraction( RTIambassador rti ) { unpublish( rti ); }

	/**
	* Subscribes a federate to the interaction class of this instance of this class.
	*
	* @param rti handle to the Local RTI Component
	*/
	public void subscribeInteraction( RTIambassador rti ) { subscribe( rti ); }

	/**
	* Unsubscribes a federate from the interaction class of this instance of this class.
	*
	* @param rti handle to the Local RTI Component
	*/
	public void unsubscribeInteraction( RTIambassador rti ) { unsubscribe( rti ); }

	

	public String toString() {
		return "C2WInteractionRoot("
			
			
			+ "sourceFed:" + get_sourceFed()
			+ "," + "originFed:" + get_originFed()
			+ "," + "federateFilter:" + get_federateFilter()
			+ "," + "actualLogicalGenerationTime:" + get_actualLogicalGenerationTime()
			+ ")";
	}
	
    private String _sourceFed = "";
	
	private String _originFed = "";
	
	private String _federateFilter = "";
	
	private double _actualLogicalGenerationTime = 0;
	
	/**
	* Set the value of the "sourceFed" parameter to "value" for this parameter.
	*
	* @param value the new value for the "sourceFed" parameter
	*/
	public void set_sourceFed( String value ) { _sourceFed = value; }
	
	/**
	* Returns the value of the "sourceFed" parameter of this interaction.
	*
	* @return the value of the "sourceFed" parameter
	*/
	public String get_sourceFed() { return _sourceFed; }
	
	
	/**
	* Set the value of the "originFed" parameter to "value" for this parameter.
	*
	* @param value the new value for the "originFed" parameter
	*/
	public void set_originFed( String value ) { _originFed = value; }
	
	/**
	* Returns the value of the "originFed" parameter of this interaction.
	*
	* @return the value of the "originFed" parameter
	*/
	public String get_originFed() { return _originFed; }
	
	
	/**
	* Set the value of the "federateFilter" parameter to "value" for this parameter.
	*
	* @param value the new value for the "federateFilter" parameter
	*/
	public void set_federateFilter( String value ) { _federateFilter = value; }
	
	/**
	* Returns the value of the "federateFilter" parameter of this interaction.
	*
	* @return the value of the "federateFilter" parameter
	*/
	public String get_federateFilter() { return _federateFilter; }
	
	
	/**
	* Set the value of the "actualLogicalGenerationTime" parameter to "value" for this parameter.
	*
	* @param value the new value for the "actualLogicalGenerationTime" parameter
	*/
	public void set_actualLogicalGenerationTime( double value ) { _actualLogicalGenerationTime = value; }
	
	/**
	* Returns the value of the "actualLogicalGenerationTime" parameter of this interaction.
	*
	* @return the value of the "actualLogicalGenerationTime" parameter
	*/
	public double get_actualLogicalGenerationTime() { return _actualLogicalGenerationTime; }
	


	protected C2WInteractionRoot( ReceivedInteraction datamemberMap, boolean initFlag ) {
		super( datamemberMap, false );
		if ( initFlag ) setParameters( datamemberMap );
	}
	
	protected C2WInteractionRoot( ReceivedInteraction datamemberMap, LogicalTime logicalTime, boolean initFlag ) {
		super( datamemberMap, logicalTime, false );
		if ( initFlag ) setParameters( datamemberMap );
	}


	/**
	* Creates an instance of the C2WInteractionRoot interaction class, using
	* "datamemberMap" to initialize its parameter values.
	* "datamemberMap" is usually acquired as an argument to an RTI federate
	* callback method, such as "receiveInteraction".
	*
	* @param datamemberMap data structure containing initial values for the
	* parameters of this new C2WInteractionRoot interaction class instance
	*/
	public C2WInteractionRoot( ReceivedInteraction datamemberMap ) {
		this( datamemberMap, true );
	}
	
	/**
	* Like {@link #C2WInteractionRoot( ReceivedInteraction datamemberMap )}, except this
	* new C2WInteractionRoot interaction class instance is given a timestamp of
	* "logicalTime".
	*
	* @param datamemberMap data structure containing initial values for the
	* parameters of this new C2WInteractionRoot interaction class instance
	* @param logicalTime timestamp for this new C2WInteractionRoot interaction class
	* instance
	*/
	public C2WInteractionRoot( ReceivedInteraction datamemberMap, LogicalTime logicalTime ) {
		this( datamemberMap, logicalTime, true );
	}

	/**
	* Creates a new C2WInteractionRoot interaction class instance that is a duplicate
	* of the instance referred to by C2WInteractionRoot_var.
	*
	* @param C2WInteractionRoot_var C2WInteractionRoot interaction class instance of which
	* this newly created C2WInteractionRoot interaction class instance will be a
	* duplicate
	*/
	public C2WInteractionRoot( C2WInteractionRoot C2WInteractionRoot_var ) {
		super( C2WInteractionRoot_var );
		
		
		set_sourceFed( C2WInteractionRoot_var.get_sourceFed() );
		set_originFed( C2WInteractionRoot_var.get_originFed() );
		set_federateFilter( C2WInteractionRoot_var.get_federateFilter() );
		set_actualLogicalGenerationTime( C2WInteractionRoot_var.get_actualLogicalGenerationTime() );
	}


	/**
	* Returns the value of the parameter whose name is "datamemberName"
	* for this interaction.
	*
	* @param datamemberName name of parameter whose value is to be
	* returned
	* @return value of the parameter whose name is "datamemberName"
	* for this interaction
	*/
	public Object getParameter( String datamemberName ) {
		
		
		
		if (  "sourceFed".equals( datamemberName )  ) return get_sourceFed();
		else if (  "originFed".equals( datamemberName )  ) return get_originFed();
		else if (  "federateFilter".equals( datamemberName )  ) return get_federateFilter();
		else if (  "actualLogicalGenerationTime".equals( datamemberName )  ) return new Double( get_actualLogicalGenerationTime() );
		else return super.getParameter( datamemberName );
	}
	
	/**
	* Returns the value of the parameter whose handle (RTI assigned)
	* is "datamemberHandle" for this interaction.
	*
	* @param datamemberHandle handle (RTI assigned) of parameter whose
	* value is to be returned
	* @return value of the parameter whose handle (RTI assigned) is
	* "datamemberHandle" for this interaction
	*/
	public Object getParameter( int datamemberHandle ) {
		
				
		
		if ( get_sourceFed_handle() == datamemberHandle ) return get_sourceFed();
		else if ( get_originFed_handle() == datamemberHandle ) return get_originFed();
		else if ( get_federateFilter_handle() == datamemberHandle ) return get_federateFilter();
		else if ( get_actualLogicalGenerationTime_handle() == datamemberHandle ) return new Double( get_actualLogicalGenerationTime() );
		else return super.getParameter( datamemberHandle );
	}
	
	protected boolean setParameterAux( int param_handle, String val ) {
		boolean retval = true;		
		
			
		
		if ( param_handle == get_sourceFed_handle() ) set_sourceFed( val );
		else if ( param_handle == get_originFed_handle() ) set_originFed( val );
		else if ( param_handle == get_federateFilter_handle() ) set_federateFilter( val );
		else if ( param_handle == get_actualLogicalGenerationTime_handle() ) set_actualLogicalGenerationTime( Double.parseDouble( val ) );
		else retval = super.setParameterAux( param_handle, val );
		
		return retval;
	}
	
	protected boolean setParameterAux( String datamemberName, String val ) {
		boolean retval = true;
		
			
		
		if (  "sourceFed".equals( datamemberName )  ) set_sourceFed( val );
		else if (  "originFed".equals( datamemberName )  ) set_originFed( val );
		else if (  "federateFilter".equals( datamemberName )  ) set_federateFilter( val );
		else if (  "actualLogicalGenerationTime".equals( datamemberName )  ) set_actualLogicalGenerationTime( Double.parseDouble( val ) );	
		else retval = super.setParameterAux( datamemberName, val );
		
		return retval;
	}
	
	protected boolean setParameterAux( String datamemberName, Object val ) {
		boolean retval = true;
		
		
		
		if (  "sourceFed".equals( datamemberName )  ) set_sourceFed( (String)val );
		else if (  "originFed".equals( datamemberName )  ) set_originFed( (String)val );
		else if (  "federateFilter".equals( datamemberName )  ) set_federateFilter( (String)val );
		else if (  "actualLogicalGenerationTime".equals( datamemberName )  ) set_actualLogicalGenerationTime( (Double)val );		
		else retval = super.setParameterAux( datamemberName, val );
		
		return retval;
	}

	protected SuppliedParameters createSuppliedDatamembers() {
		SuppliedParameters datamembers = super.createSuppliedDatamembers();

	
		
		
			datamembers.add( get_sourceFed_handle(), get_sourceFed().getBytes() );
		
			datamembers.add( get_originFed_handle(), get_originFed().getBytes() );
		
			datamembers.add( get_federateFilter_handle(), get_federateFilter().getBytes() );
		
			datamembers.add( get_actualLogicalGenerationTime_handle(), Double.toString( get_actualLogicalGenerationTime() ).getBytes() );
		
	
		return datamembers;
	}

	
	public void copyFrom( Object object ) {
		super.copyFrom( object );
		if ( object instanceof C2WInteractionRoot ) {
			C2WInteractionRoot data = (C2WInteractionRoot)object;
			
			
				_sourceFed = data._sourceFed;
				_originFed = data._originFed;
				_federateFilter = data._federateFilter;
				_actualLogicalGenerationTime = data._actualLogicalGenerationTime;
			
		}
	}
}
