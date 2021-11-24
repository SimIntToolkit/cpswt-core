package org.cpswt.hla.InteractionRoot_p.C2WInteractionRoot_p.SimulationControl_p;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cpswt.utils.CpswtUtils;

import hla.rti.FederateNotExecutionMember;
import hla.rti.InteractionClassNotDefined;
import hla.rti.InteractionClassNotPublished;
import hla.rti.InteractionClassNotSubscribed;
import hla.rti.LogicalTime;
import hla.rti.NameNotFound;
import hla.rti.RTIambassador;
import hla.rti.ReceivedInteraction;


/**
 * Implements InteractionRoot_p.C2WInteractionRoot_p.SimulationControl_p.SimPause
 */
@SuppressWarnings("unused")
public class SimPause extends org.cpswt.hla.InteractionRoot_p.C2WInteractionRoot_p.SimulationControl {

    private static final Logger logger = LogManager.getLogger();

    /**
    * Creates an instance of the Interaction class with default parameter values.
    */
    public SimPause() {}// ----------------------------------------------------------------------------
// STATIC DATAMEMBERS AND CODE THAT DEAL WITH NAMES
// THIS CODE IS STATIC BECAUSE IT IS CLASS-DEPENDENT AND NOT INSTANCE-DEPENDENT
// ----------------------------------------------------------------------------

/**
 * Returns the fully-qualified (dot-delimited) name of the InteractionRoot_p.C2WInteractionRoot_p.SimulationControl_p.SimPause interaction class.
 * Note: As this is a static method, it is NOT polymorphic, and so, if called on
 * a reference will return the name of the class pertaining to the reference,
 * rather than the name of the class for the instance referred to by the reference.
 * For the polymorphic version of this method, use {@link #getClassName()}.
 *
 * @return the fully-qualified HLA class path for this interaction class
 */
public static String get_class_name() {
    return "InteractionRoot_p.C2WInteractionRoot_p.SimulationControl_p.SimPause";
}

/**
 * Returns the fully-qualified (dot-delimited) name of this instance's interaction class.
 * Polymorphic equivalent of get_class_name static method.
 *
 * @return the fully-qualified (dot-delimited) name of this instance's interaction class
 */
@Override
public String getClassName() {
    return get_class_name();
}

/**
 * Returns the simple name (the last name in the dot-delimited fully-qualified
 * class name) of the InteractionRoot_p.C2WInteractionRoot_p.SimulationControl_p.SimPause interaction class.
 *
 * @return the name of this interaction class
 */
public static String get_simple_class_name() {
    return "SimPause";
}

/**
 * Returns the simple name (last name in its fully-qualified dot-delimited name)
 * of this instance's interaction class.
 * Polymorphic equivalent of the get_simple_class_name static method.
 *
 * @return the simple name of this instance's interaction class
 */
@Override
public String getSimpleClassName() {
    return get_simple_class_name();
}

private static final Set<ClassAndPropertyName> _classAndPropertyNameList = new HashSet<>();

/**
 * Returns a set containing the names of all of the non-hidden parameters in the
 * InteractionRoot_p.C2WInteractionRoot_p.SimulationControl_p.SimPause interaction class.
 * Note: As this is a static method, it is NOT polymorphic, and so, if called on
 * a reference will return a set of parameter names pertaining to the reference,
 * rather than the parameter names of the class for the instance referred to by
 * the reference.  For the polymorphic version of this method, use
 * {@link #getParameterNames()}.
 *
 * @return a modifiable set of the non-hidden parameter names for this interaction class
 */
public static List<ClassAndPropertyName> get_parameter_names() {
    return new ArrayList<>(_classAndPropertyNameList);
}

/**
 * Returns a set containing the names of all of the non-hiddenparameters of an
 * interaction class instance.
 * Polymorphic equivalent to get_parameter_names static method.
 *
 * @return set containing the names of all of the parameters of an
 * interaction class instance
 */
@Override
public List<ClassAndPropertyName> getParameterNames() {
    return get_parameter_names();
}

private static final Set<ClassAndPropertyName> _allClassAndPropertyNameList = new HashSet<>();

/**
 * Returns a set containing the names of all of the parameters in the
 * InteractionRoot_p.C2WInteractionRoot_p.SimulationControl_p.SimPause interaction class.
 * Note: As this is a static method, it is NOT polymorphic, and so, if called on
 * a reference will return a set of parameter names pertaining to the reference,
 * rather than the parameter names of the class for the instance referred to by
 * the reference.  For the polymorphic version of this method, use
 * {@link #getParameterNames()}.
 *
 * @return a modifiable set of the parameter names for this interaction class
 */
public static List<ClassAndPropertyName> get_all_parameter_names() {
    return new ArrayList<>(_allClassAndPropertyNameList);
}

/**
 * Returns a set containing the names of all of the parameters of an
 * interaction class instance.
 * Polymorphic equivalent of get_all_parameter_names() static method.
 *
 * @return set containing the names of all of the parameters of an
 * interaction class instance
 */
@Override
public List<ClassAndPropertyName> getAllParameterNames() {
    return get_all_parameter_names();
}


/*
 * INITIALIZE STATIC DATAMEMBERS THAT DEAL WITH NAMES
 */
static {
    // ADD THIS CLASS TO THE _classNameSet DEFINED IN InteractionRoot
    _classNameSet.add(get_class_name());
    
    // ADD CLASS OBJECT OF THIS CLASS TO _classNameClassMap DEFINED IN InteractionRoot
    _classNameClassMap.put(get_class_name(), SimPause.class);

    // ADD THIS CLASS'S _classAndPropertyNameList TO _classNamePropertyNameSetMap DEFINED
    // IN InteractionRoot
    _classNamePropertyNameSetMap.put(get_class_name(), _classAndPropertyNameList);

    // ADD THIS CLASS'S _allClassAndPropertyNameList TO _classNameAllPropertyNameSetMap DEFINED
    // IN InteractionRoot
    _classNameAllPropertyNameSetMap.put(get_class_name(), _allClassAndPropertyNameList);
    _classAndPropertyNameList.add(new ClassAndPropertyName(
        "InteractionRoot_p.C2WInteractionRoot_p.SimulationControl_p.SimPause", "originFed"
    ));
    _classAndPropertyNameList.add(new ClassAndPropertyName(
        "InteractionRoot_p.C2WInteractionRoot_p.SimulationControl_p.SimPause", "sourceFed"
    ));

    ClassAndPropertyName key;

    key = new ClassAndPropertyName(get_class_name(), "originFed");
    _classAndPropertyNameTypeMap.put(key, "String");

    key = new ClassAndPropertyName(get_class_name(), "sourceFed");
    _classAndPropertyNameTypeMap.put(key, "String");

    _allClassAndPropertyNameList.add(new ClassAndPropertyName(
        "InteractionRoot_p.C2WInteractionRoot_p.SimulationControl_p.SimPause", "originFed"
    ));

    _allClassAndPropertyNameList.add(new ClassAndPropertyName(
        "InteractionRoot_p.C2WInteractionRoot_p.SimulationControl_p.SimPause", "sourceFed"
    ));
}

// --------------------------------------------------------
// END OF STATIC DATAMEMBERS AND CODE THAT DEAL WITH NAMES.
// --------------------------------------------------------


// ----------------------------------------------------------------------------
// STATIC DATAMEMBERS AND CODE THAT DEAL WITH HANDLES.
// THIS CODE IS STATIC BECAUSE IT IS CLASS-DEPENDENT AND NOT INSTANCE-DEPENDENT
// ----------------------------------------------------------------------------

private static int _handle;

/**
 * Returns the handle (RTI assigned) of the InteractionRoot_p.C2WInteractionRoot_p.SimulationControl_p.SimPause interaction class.
 * Note: As this is a static method, it is NOT polymorphic, and so, if called on
 * a reference will return the handle of the class pertaining to the reference,
 * rather than the handle of the class for the instance referred to by the reference.
 * For the polymorphic version of this method, use {@link #getClassHandle()}.
 *
 * @return the RTI assigned integer handle that represents this interaction class
 */
public static int get_class_handle() {
    return _handle;
}

/**
 * Returns the handle (RTI assigned) of this instance's interaction class.
 * Polymorphic equivalent for get_class_handle static method.
 *
 * @return the handle (RTI assigned) if this instance's interaction class
 */
public int getClassHandle() {
    return get_class_handle();
}


/*
 * THIS IS A PROTECTED METHOD THAT WILL (TRY TO) RETURN THE HANDLE OF A GIVEN DATAMEMBER, GIVEN THE DATAMEMBER'S NAME.
 * FOR A GIVEN CLASS, IT WILL ATTEMPT TO FIND THE ENTRY IN THE _classAndPropertyNameHandleMap USING AS A KEY
 * A ClassAndPropertyName POJO, ClassAndPropertyName(A, B), WHERE "A" IS THE FULL CLASS NAME OF THIS CLASS,
 * AND "B" IS THE NAME OF THE DATAMEMBER. IF THERE IS NO SUCH ENTRY, THIS METHOD CALLS THE SAME METHOD IN ITS
 * SUPER CLASS.  THIS METHOD CHAIN BOTTOMS OUT IN THE "InteractionRoot" CLASS, WHERE AN ERROR IS RAISED INDICATING
 * THERE IS NO SUCH DATAMEMBER.
 *
 * THE "className" ARGUMENT IS THE FULL NAME OF THE CLASS FOR WHICH THIS METHOD WAS ORIGINALLY CALLED, I.E. THE NAME
 * OF THE CLASS AT THE TOP OF THE CALL-CHAIN.  IT IS INCLUDED FOR ERROR REPORTING IN THE "InteractionRoot" CLASS.
 *
 * THIS METHOD IS INDIRECTLY CALLED VIA THE "get_parameter_handle(String)" METHOD BELOW, WHICH PROVIDES THE
 * VALUE FOR THE "className" ARGUMENT.
 */
protected static int get_parameter_handle_aux(String className, String propertyName) {
    ClassAndPropertyName key = new ClassAndPropertyName(get_class_name(), propertyName);
    if (_classAndPropertyNameHandleMap.containsKey(key)) {
        return _classAndPropertyNameHandleMap.get(key);
    }
    return org.cpswt.hla.InteractionRoot_p.C2WInteractionRoot_p.SimulationControl.get_parameter_handle_aux(className, propertyName);    
}

/**
 * Returns the handle of an parameter (RTI assigned) of
 * this interaction class (i.e. "InteractionRoot_p.C2WInteractionRoot_p.SimulationControl_p.SimPause") given the parameter's name.
 *
 * @param propertyName name of parameter
 * @return the handle (RTI assigned) of the parameter "propertyName" of interaction class "className"
 */
public static int get_parameter_handle(String propertyName) {
    return get_parameter_handle_aux(get_class_name(), propertyName);
}

/**
 * Returns the handle associated with the given parameter name for an interaction class instance
 * Polymorphic equivalent of get_parameter_handle static method.
 *
 * @param propertyName the name of a parameter that belongs to this interaction class
 * @return the RTI handle associated with the parameter name, or -1 if not found
 */
@Override
public int getParameterHandle(String propertyName) {
    return get_parameter_handle(propertyName);
}

private static boolean _isInitialized = false;

/*
 * THIS FUNCTION INITIALIZES ALL OF THE HANDLES ASSOCIATED WITH THIS INTERACTION CLASS
 * IT NEEDS THE RTI TO DO SO.
 */
protected static void init(RTIambassador rti) {
    if (_isInitialized) return;
    _isInitialized = true;
    org.cpswt.hla.InteractionRoot_p.C2WInteractionRoot_p.SimulationControl.init(rti);

    boolean isNotInitialized = true;
    while(isNotInitialized) {
        try {
            _handle = rti.getInteractionClassHandle(get_class_name());
            isNotInitialized = false;
        } catch (FederateNotExecutionMember e) {
            logger.error("could not initialize: Federate Not Execution Member", e);
            return;
        } catch (NameNotFound e) {
            logger.error("could not initialize: Name Not Found", e);
            return;
        } catch (Exception e) {
            logger.error(e);
            CpswtUtils.sleepDefault();
        }
    }

    _classNameHandleMap.put(get_class_name(), get_class_handle());
    _classHandleNameMap.put(get_class_handle(), get_class_name());
    _classHandleSimpleNameMap.put(get_class_handle(), get_simple_class_name());

    ClassAndPropertyName classAndPropertyName;

    isNotInitialized = true;
    int propertyHandle;
    while(isNotInitialized) {
        try {

            propertyHandle = rti.getParameterHandle("{'class_name': 'InteractionRoot_p.C2WInteractionRoot_p.SimulationControl_p.SimPause', 'property_name': 'originFed'}", get_class_handle());
            classAndPropertyName = new ClassAndPropertyName(get_class_name(), "{'class_name': 'InteractionRoot_p.C2WInteractionRoot_p.SimulationControl_p.SimPause', 'property_name': 'originFed'}");
            _classAndPropertyNameHandleMap.put(classAndPropertyName, propertyHandle);
            _handleClassAndPropertyNameMap.put(propertyHandle, classAndPropertyName);

            propertyHandle = rti.getParameterHandle("{'class_name': 'InteractionRoot_p.C2WInteractionRoot_p.SimulationControl_p.SimPause', 'property_name': 'sourceFed'}", get_class_handle());
            classAndPropertyName = new ClassAndPropertyName(get_class_name(), "{'class_name': 'InteractionRoot_p.C2WInteractionRoot_p.SimulationControl_p.SimPause', 'property_name': 'sourceFed'}");
            _classAndPropertyNameHandleMap.put(classAndPropertyName, propertyHandle);
            _handleClassAndPropertyNameMap.put(propertyHandle, classAndPropertyName);

            isNotInitialized = false;
        } catch (FederateNotExecutionMember e) {
            logger.error("could not initialize: Federate Not Execution Member", e);
            return;
        } catch (InteractionClassNotDefined e) {
            logger.error("could not initialize: Interaction Class Not Defined", e);
            return;
        } catch (NameNotFound e) {
            logger.error("could not initialize: Name Not Found", e);
            return;
        } catch (Exception e) {
            logger.error(e);
            CpswtUtils.sleepDefault();
        }
    }
}

// ----------------------------------------------------------
// END OF STATIC DATAMEMBERS AND CODE THAT DEAL WITH HANDLES.
// ----------------------------------------------------------


//-------------------------------------------------
// METHODS FOR PUBLISHING/SUBSCRIBING-TO THIS CLASS
//-------------------------------------------------

private static boolean _isPublished = false;

/**
 * Publishes the InteractionRoot_p.C2WInteractionRoot_p.SimulationControl_p.SimPause interaction class for a federate.
 *
 * @param rti handle to the Local RTI Component
 */
public static void publish_interaction(RTIambassador rti) {
    if (_isPublished) return;
    _isPublished = true;

    init(rti);

    synchronized(rti) {
        boolean isNotPublished = true;
        while(isNotPublished) {
            try {
                rti.publishInteractionClass(get_class_handle());
                isNotPublished = false;
            } catch (FederateNotExecutionMember e) {
                logger.error("could not publish: Federate Not Execution Member", e);
                return;
            } catch (InteractionClassNotDefined e) {
                logger.error("could not publish: Interaction Class Not Defined", e);
                return;
            } catch (Exception e) {
                logger.error(e);
                CpswtUtils.sleepDefault();
            }
        }
    }

    logger.debug("publish: {}", get_class_name());
}

/**
 * Publishes the interaction class of this instance of the class for a federate.
 * Polymorphic equalivalent of publish_interaction static method.
 *
 * @param rti handle to the Local RTI Component
 */
@Override
public void publishInteraction(RTIambassador rti) {
    publish_interaction(rti);
}


/**
 * Unpublishes the InteractionRoot_p.C2WInteractionRoot_p.SimulationControl_p.SimPause interaction class for a federate.
 *
 * @param rti handle to the Local RTI Component, usu. obtained through the
 *            {@link SynchronizedFederate#getLRC()} call
 */
public static void unpublish_interaction(RTIambassador rti) {
    if (!_isPublished) return;
    _isPublished = false;

    init(rti);

    synchronized(rti) {
        boolean isNotUnpublished = true;
        while(isNotUnpublished) {
            try {
                rti.unpublishInteractionClass(get_class_handle());
                isNotUnpublished = false;
            } catch (FederateNotExecutionMember e) {
                logger.error("could not unpublish: Federate Not Execution Member", e);
                return;
            } catch (InteractionClassNotDefined e) {
                logger.error("could not unpublish: Interaction Class Not Defined", e);
                return;
            } catch (InteractionClassNotPublished e) {
                logger.error("could not unpublish: Interaction Class Not Published", e);
                return;
            } catch (Exception e) {
                logger.error(e);
                CpswtUtils.sleepDefault();
            }
        }
    }

    logger.debug("unpublish: {}", get_class_name());
}

/**
 * Unpublishes the interaction class of this instance of this class for a federate.
 * Polymorphic equivalent of unpublish_interaction static method.
 *
 * @param rti handle to the Local RTI Component
 */
@Override
public void unpublishInteraction(RTIambassador rti) {
    unpublish_interaction(rti);
}

private static boolean _isSubscribed = false;

/**
 * Subscribes a federate to the InteractionRoot_p.C2WInteractionRoot_p.SimulationControl_p.SimPause interaction class.
 *
 * @param rti handle to the Local RTI Component
 */
public static void subscribe_interaction(RTIambassador rti) {
    if (_isSubscribed) return;

    init(rti);

    synchronized(rti) {
        boolean isNotSubscribed = true;
        while(isNotSubscribed) {
            try {
                rti.subscribeInteractionClass(get_class_handle());
                isNotSubscribed = false;
            } catch (FederateNotExecutionMember e) {
                logger.error("could not subscribe: Federate Not Execution Member", e);
                return;
            } catch (InteractionClassNotDefined e) {
                logger.error("could not subscribe: Interaction Class Not Defined", e);
                return;
            } catch (Exception e) {
                logger.error(e);
                CpswtUtils.sleepDefault();
            }
        }
    }

    _isSubscribed = true;
    logger.debug("subscribe: {}", get_class_name());
}

/**
 * Subscribes a federate to the interaction class of this instance of this class.
 * Polymorphic equivalent of subscribe_interaction static method.
 *
 * @param rti handle to the Local RTI Component
 */
@Override
public void subscribeInteraction(RTIambassador rti) {
    subscribe_interaction(rti);
}

/**
 * Unsubscribes a federate from the InteractionRoot_p.C2WInteractionRoot_p.SimulationControl_p.SimPause interaction class.
 *
 * @param rti handle to the Local RTI Component
 */
public static void unsubscribe_interaction(RTIambassador rti) {
    if (!_isSubscribed) return;

    init(rti);

    synchronized(rti) {
        boolean isNotUnsubscribed = true;
        while(isNotUnsubscribed) {
            try {
                rti.unsubscribeInteractionClass(get_class_handle());
                isNotUnsubscribed = false;
            } catch (FederateNotExecutionMember e) {
                logger.error("could not unsubscribe: Federate Not Execution Member", e);
                return;
            } catch (InteractionClassNotDefined e) {
                logger.error("could not unsubscribe: Interaction Class Not Defined", e);
                return;
            } catch (InteractionClassNotSubscribed e) {
                logger.error("could not unsubscribe: Interaction Class Not Subscribed", e);
                return;
            } catch (Exception e) {
                logger.error(e);
                CpswtUtils.sleepDefault();
            }
        }
    }

    _isSubscribed = false;
    logger.debug("unsubscribe: {}", get_class_name());
}

/**
 * Unsubscribes a federate from the interaction class of this instance of this class.
 *
 * @param rti handle to the Local RTI Component
 */
@Override
public void unsubscribeInteraction(RTIambassador rti) {
    unsubscribe_interaction(rti);
}

//-----------------------------------------------------
// END METHODS FOR PUBLISHING/SUBSCRIBING-TO THIS CLASS
//-----------------------------------------------------

/**
 * Return true if "handle" is equal to the handle (RTI assigned) of this class
 * (that is, the InteractionRoot_p.C2WInteractionRoot_p.SimulationControl_p.SimPause interaction class).
 *
 * @param handle handle to compare to the value of the handle (RTI assigned) of
 * this class (the InteractionRoot_p.C2WInteractionRoot_p.SimulationControl_p.SimPause interaction class).
 * @return "true" if "handle" matches the value of the handle of this class
 * (that is, the InteractionRoot_p.C2WInteractionRoot_p.SimulationControl_p.SimPause interaction class).
 */
public static boolean match(int handle) {
    return handle == get_class_handle();
}

//--------------------------------
// DATAMEMBER MANIPULATION METHODS
//--------------------------------
{
    ClassAndPropertyName key;

    key = new ClassAndPropertyName(get_class_name(), "originFed");
    classAndPropertyNameValueMap.put(key, "");

    key = new ClassAndPropertyName(get_class_name(), "sourceFed");
    classAndPropertyNameValueMap.put(key, "");
}


/**
 * Set the value of the "originFed" parameter to "value" for this parameter.
 *
 * @param value the new value for the "originFed" parameter
 */
public void set_originFed(String value) {
    ClassAndPropertyName key = new ClassAndPropertyName(get_class_name(), "originFed");
    classAndPropertyNameValueMap.put(key, value);
}

/**
 * Returns the value of the "originFed" parameter of this interaction.
 *
 * @return the value of the "originFed" parameter
 */
public String get_originFed() {
    ClassAndPropertyName key = new ClassAndPropertyName(get_class_name(), "originFed");
    return (String)classAndPropertyNameValueMap.get(key);
}


/**
 * Set the value of the "sourceFed" parameter to "value" for this parameter.
 *
 * @param value the new value for the "sourceFed" parameter
 */
public void set_sourceFed(String value) {
    ClassAndPropertyName key = new ClassAndPropertyName(get_class_name(), "sourceFed");
    classAndPropertyNameValueMap.put(key, value);
}

/**
 * Returns the value of the "sourceFed" parameter of this interaction.
 *
 * @return the value of the "sourceFed" parameter
 */
public String get_sourceFed() {
    ClassAndPropertyName key = new ClassAndPropertyName(get_class_name(), "sourceFed");
    return (String)classAndPropertyNameValueMap.get(key);
}

@Override
protected PropertyClassNameAndValue getParameterAux(String className, String propertyName) {
    ClassAndPropertyName key = new ClassAndPropertyName(get_class_name(), "");
    if (classAndPropertyNameValueMap.containsKey(key)) {
        Object value = classAndPropertyNameValueMap.get(key);
        return new PropertyClassNameAndValue(get_class_name(), value);
    }

    return super.getParameterAux(className, propertyName);
}

//------------------------------------
// END DATAMEMBER MANIPULATION METHODS
//------------------------------------

    protected SimPause( ReceivedInteraction datamemberMap, boolean initFlag ) {
        super( datamemberMap, false );
        if ( initFlag ) setParameters( datamemberMap );
    }

    protected SimPause( ReceivedInteraction datamemberMap, LogicalTime logicalTime, boolean initFlag ) {
        super( datamemberMap, logicalTime, false );
        if ( initFlag ) setParameters( datamemberMap );
    }

    /**
    * Creates an instance of the SimPause interaction class, using
    * "datamemberMap" to initialize its parameter values.
    * "datamemberMap" is usually acquired as an argument to an RTI federate
    * callback method, such as "receiveInteraction".
    *
    * @param datamemberMap data structure containing initial values for the
    * parameters of this new SimPause interaction class instance
    */
    public SimPause( ReceivedInteraction datamemberMap ) {
        this( datamemberMap, true );
    }

    /**
    * Like {@link #SimPause( ReceivedInteraction datamemberMap )}, except this
    * new SimPause parameter class instance is given a timestamp of
    * "logicalTime".
    *
    * @param datamemberMap data structure containing initial values for the
    * parameters of this new SimPause interaction class instance
    * @param logicalTime timestamp for this new SimPause interaction class instance
    */
    public SimPause( ReceivedInteraction datamemberMap, LogicalTime logicalTime ) {
        this( datamemberMap, logicalTime, true );
    }

    /**
    * Creates a new SimPause interaction class instance that is a duplicate
    * of the instance referred to by messaging_var.
    *
    * @param messaging_var SimPause interaction class instance of which
    * this newly created SimPause interaction class instance will be a
    * duplicate
    */
    public SimPause(SimPause messaging_var) {
    
        // SHALLOW COPY
        classAndPropertyNameValueMap = new HashMap<>(messaging_var.classAndPropertyNameValueMap);

    }
}