
/* THIS IS THE ROOT CLASS OF EITHER THE INTERACTION OR OBJECT-CLASS HIERARCHY
(i.e. "InteractionRoot" OR "ObjectRoot") */

/*
 * Certain portions of this software are Copyright (C) 2006-present
 * Vanderbilt University, Institute for Software Integrated Systems.
 *
 * Certain portions of this software are contributed as a public service by
 * The National Institute of Standards and Technology (NIST) and are not
 * subject to U.S. Copyright.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above Vanderbilt University copyright notice, NIST contribution
 * notice and this permission and disclaimer notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE. THE AUTHORS OR COPYRIGHT HOLDERS SHALL NOT HAVE
 * ANY OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS,
 * OR MODIFICATIONS.
 *
 * @author Himanshu Neema
 * @author Harmon Nine
 */

package org.cpswt.hla;

import hla.rti.*;
import hla.rti.jlc.RtiFactory;
import hla.rti.jlc.RtiFactoryFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;

import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONTokener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cpswt.utils.CpswtUtils;

import org.portico.impl.hla13.types.DoubleTime;

/**
 * InteractionRoot is the base class for all interactions
 * defined in a given federation.  As such, an InteractionRoot
 * variable may refer to any type of interaction defined in the
 * federation.
 * <p/>
 * This InteractionRoot class provides the following:
 * - methods for constructing any interaction in the federation, either from
 * data provided by the RTI (for example, see
 * {@link #create_interaction( int classHandle )}) or from a string argument
 * specifying the name of interaction to construct (see
 * {@link #create_interaction( String className )}).
 * - methods for sendingan interactionto the RTI (see
 *{@link#sendInteraction( RTIambassador rti )}for an example).
 * - methods for publishing/subscribing to anyinteraction
 * defined in the federation (see
 * {@link #publish_interaction( String className, RTIambassador rti )} for example).
 * - methods for getting/setting any parameter in the interaction to
 * which a given InteractionRoot variable is referring
 * (see {@link #getParameter( String propertyName )} and
 * {@link #setParameter( String propertyName, Object value )}
 */
@SuppressWarnings("unused")
public class InteractionRoot implements InteractionRootInterface {

    private static final Logger logger = LogManager.getLogger();

    private static int _globalUniqueID = 0;
    private static int generateUniqueID() {
        return _globalUniqueID++;
    }

    private final int _uniqueID;
    public int getUniqueID() {
        return _uniqueID;
    }

    //-------------------------------
    // _rtiFactory IS USED TO CREATE:
    // - ATTRIBUTE HANDLE SETS
    // - SUPPLIED PARAMETERS
    //-------------------------------
    protected static RtiFactory _rtiFactory;
    static {
        boolean factoryNotAcquired = true;
        while( factoryNotAcquired ) {
            try {
                _rtiFactory = RtiFactoryFactory.getRtiFactory( "org.portico.dlc.HLA13RTIFactory" );
                factoryNotAcquired = false;
            } catch ( Exception e ) {
                logger.error("failed to acquire factory", e);
                CpswtUtils.sleep(100);
            }
        }
    }

    private static boolean _isInitialized = false;
    public static void init(RTIambassador rtiAmbassador) {
        if (_isInitialized) {
            return;
        }
        _isInitialized = true;

        //-------------------------------------------------------------------------
        // _hlaClassNameSet IS POPULATED BY
        // - STATIC INITIALIZATION BLOCKS IN THE DERIVED INTERACTION/OBJECT CLASSES
        // - THE DYNAMIC-MESSAGE-CLASSES FILE
        //-------------------------------------------------------------------------
        for(String hlaClassName: _hlaClassNameSet) {

            //------------------------------------------
            // GET HANDLE FOR hlaClassName TO INITIALIZE
            // - _classNameHandleMap
            // - _classHandleNameMap
            //------------------------------------------
            boolean isNotInitialized = true;
            int classHandle = 0;
            while(isNotInitialized) {
                try {
                    classHandle = rtiAmbassador.getInteractionClassHandle(hlaClassName);
                    _classNameHandleMap.put(hlaClassName, classHandle);
                    _classHandleNameMap.put(classHandle, hlaClassName);
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

            //-------------------------------------------------------------------------------------------
            // _classAndPropertyNameSetMap MAPS hlaClassName TO THE PROPERTIES (PARAMETERS OR ATTRIBUTES)
            // DEFINED *DIRECTLY* IN THE hlaClassName
            //
            // GET HANDLE FOR THESE PROPERTIES TO INITIALIZE
            // - _classAndPropertyNameHandleMap
            // - _handleClassAndPropertyNameMap
            //-------------------------------------------------------------------------------------------
            Set<ClassAndPropertyName> classAndPropertyNameSet = _classNamePropertyNameSetMap.get(hlaClassName);
            for(ClassAndPropertyName classAndPropertyName: classAndPropertyNameSet) {
                isNotInitialized = true;
                while(isNotInitialized) {
                    try {
                        int propertyHandle = rtiAmbassador.getParameterHandle(classAndPropertyName.getPropertyName(), classHandle);
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

            //-------------------------------------------------------
            // INITIALIZE ALL CLASSES TO NOT-PUBLISHED NOT-SUBSCRIBED
            //-------------------------------------------------------
            _classNamePublishStatusMap.put(hlaClassName, false);
            _classNameSubscribeStatusMap.put(hlaClassName, false);
        }
    }

    //-------------------------------------------------------------------------------------------
    // _instanceHlaClassName IS THE HLA CLASS NAME OF THIS MESSAGING OBJECT JAVA INSTANCE.
    // FOR DYNAMIC MESSAGING IN PARTICULAR, JAVA-CLASS OF MESSAGING OBJECT DOES NOT DETERMINE THE
    // MESSAGING CLASS OF THE OBJECT.  _instanceHlaClassName DOES.
    //-------------------------------------------------------------------------------------------
    private String _instanceHlaClassName = null;

    public String getInstanceHlaClassName() {
        return _instanceHlaClassName;
    }

    protected void setInstanceHlaClassName(String instanceHlaClassName) {
        _instanceHlaClassName = instanceHlaClassName;
    }

    public static String get_simple_class_name(String hlaClassName) {
        if (hlaClassName == null) {
            return null;
        }
        int position = hlaClassName.lastIndexOf(".");
        return position >= 0 ? hlaClassName.substring(position + 1) : hlaClassName;
    }

    //-----------------------------------------------------------------------------------------
    // THIS JAVA-INSTANCE INITIALIZATION BLOCK SETS THE INITIAL VALUE FOR _instanceHlaClassName
    //-----------------------------------------------------------------------------------------
    {
        // GENERALLY CONSIDERED POOR FORM TO CALL A POLYMORPHIC FUNCTION FROM A CONSTRUCTOR
        // (OR, MORE ACCURATELY AN INSTANCE INITIALIZATION BLOCK), BUT THE POLYMORPHIC FUNCTION
        // USED (getHlaClassName) DOES NOT DEPEND ON OBJECT STATE.
        setInstanceHlaClassName(getHlaClassName());
    }

    //-------------------------------------------------------------------------
    // HLA CLASS-NAME SET
    //
    // POPULATED BY:
    // - STATIC INITIALIZATION BLOCKS IN THE DERIVED INTERACTION/OBJECT CLASSES
    // - THE DYNAMIC-MESSAGE-CLASSES FILE
    //-------------------------------------------------------------------------
    protected static Set<String> _hlaClassNameSet = new HashSet<>();

    //----------------------------------------------------------------------------
    // METHODS THAT USE HLA CLASS-NAME-SET
    //
    // ALSO USED BY:
    // - init(RTIambassador) ABOVE
    // - InteractionRoot( String hlaClassName ) DYNAMIC CONSTRUCTOR
    // - readFederateDynamicMessageClasses(Reader reader) BELOW
    //----------------------------------------------------------------------------
    /**
      * Returns a set of strings containing the names of all of the interaction
      * classes in the current federation.
      *
      * @return Set<String> containing the names of all interaction classes
      * in the current federation
      */
    public static Set<String> get_interaction_hla_class_name_set() {
        return new HashSet<>( _hlaClassNameSet );
    }

    //-----------------------
    // END HLA CLASS-NAME-SET
    //-----------------------

    //-------------------------------------------------------------------------
    // CLASS-NAME PROPERTY-NAME-SET MAP
    //
    // POPULATED BY:
    // - STATIC INITIALIZATION BLOCKS IN THE DERIVED INTERACTION/OBJECT CLASSES
    // - THE DYNAMIC-MESSAGE-CLASSES FILE
    //-------------------------------------------------------------------------
    protected static Map<String, Set<ClassAndPropertyName>> _classNamePropertyNameSetMap = new HashMap<>();

    //---------------------------------------------------------
    // METHODS THAT USE CLASS-NAME PROPERTY-NAME-SET MAP
    //
    // ALSO USED BY:
    // - init(RTIambassador) ABOVE
    // - readFederateDynamicMessageClasses(Reader reader) BELOW
    //---------------------------------------------------------
    /**
      * Returns a set of strings containing the names of all of the non-hidden parameters
      * in the interaction class specified by className.
      *
      * @param hlaClassName name of interaction class for which to retrieve the
      * names of all of its parameters
      * @return Set<String> containing the names of all parameters in the
      * className interaction class
      */
    public static List<ClassAndPropertyName> get_parameter_names( String hlaClassName ) {
        List<ClassAndPropertyName> classAndPropertyNameList = new ArrayList<>(
          _classNamePropertyNameSetMap.get( hlaClassName )
        );
        Collections.sort(classAndPropertyNameList);
        return classAndPropertyNameList;
    }

    //-------------------------------------
    // END CLASS-NAME PROPERTY-NAME-SET MAP
    //-------------------------------------

    //-------------------------------------------------------------------------
    // CLASS-NAME ALL-PROPERTY-NAME-SET MAP
    //
    // POPULATED BY:
    // - STATIC INITIALIZATION BLOCKS IN THE DERIVED INTERACTION/OBJECT CLASSES
    // - THE DYNAMIC-MESSAGE-CLASSES FILE
    //-------------------------------------------------------------------------
    protected static Map<String, Set<ClassAndPropertyName>> _allClassNamePropertyNameSetMap = new HashMap<>();

    //----------------------------------------------------------------------------
    // METHODS THAT USE CLASS-NAME ALL-PROPERTY-NAME-SET MAP
    //
    // ALSO USED BY:
    // - InteractionRoot( String hlaClassName ) DYNAMIC CONSTRUCTOR
    // - readFederateDynamicMessageClasses(Reader reader) BELOW
    //----------------------------------------------------------------------------
    /**
      * Returns a set of strings containing the names of all of the parameters
      * in the interaction class specified by className.
      *
      * @param hlaClassName name of interaction class for which to retrieve the
      * names of all of its parameters
      * @return Set<String> containing the names of all parameters in the
      * className interaction class
      */
    public static List<ClassAndPropertyName> get_all_parameter_names( String hlaClassName ) {
        List<ClassAndPropertyName> allClassAndPropertyNameList = new ArrayList<>(
          _allClassNamePropertyNameSetMap.get( hlaClassName )
        );
        Collections.sort(allClassAndPropertyNameList);
        return allClassAndPropertyNameList;
    }

    //-------------------------------------------
    // END CLASS-NAME All-PROPERTY-NAME-SET MAP
    //-------------------------------------------

    //----------------------------
    // CLASS-NAME CLASS-HANDLE MAP
    //
    // POPULATED BY:
    // - init(RTIambassador) ABOVE
    //----------------------------
    protected static Map<String, Integer> _classNameHandleMap = new HashMap<>();

    //-------------------------------------------------------------
    // METHODS THAT USE CLASS-NAME CLASS-HANDLE MAP
    //-------------------------------------------------------------
    /**
      * Returns the integer handle (RTI defined) of the interaction class
      * corresponding to the fully-qualified interaction class name in className.
      *
      * @param hlaClassName fully-qualified name of interaction class for which to
      * retrieve the RTI-defined integer handle
      * @return the RTI-defined handle of the interaction class
      */
    public static int get_class_handle( String hlaClassName ) {

        Integer classHandle = _classNameHandleMap.getOrDefault( hlaClassName, null );
        if ( classHandle == null ) {
            logger.error( "Bad HLA class name \"{}\" on get_class_handle.", hlaClassName );
            return -1;
        }

        return classHandle;
    }

    //--------------------------------
    // END CLASS-NAME CLASS-HANDLE MAP
    //--------------------------------

    //----------------------------
    // CLASS-HANDLE CLASS-NAME MAP
    //
    // POPULATED BY:
    // - init(RTIambassador) ABOVE
    //----------------------------
    protected static Map<Integer, String> _classHandleNameMap = new HashMap<>();

    //--------------------------------------------------
    // METHODS THAT USE ONLY CLASS-HANDLE CLASS-NAME MAP
    //--------------------------------------------------
    /**
      * Returns the fully-qualified name of the interaction class corresponding
      * to the RTI-defined classHandle.
      *
      * @param classHandle handle (defined by RTI) of interaction class for
      * which to retrieve the fully-qualified name
      * @return the fully-qualified name of the interaction class that
      * corresponds to the RTI-defined classHandle
      */
    public static String get_hla_class_name( int classHandle ) {
        return _classHandleNameMap.getOrDefault(classHandle, null);
    }

    /**
      * Returns the simple name of the interaction class corresponding to the
      * RTI-defined classHandle.  The simple name of an interaction class is
      * the last name in its (dot-delimited) fully-qualified name.
      *
      * @param classHandle handle (defined by RTI) of interaction class for which
      * to retrieve the simple name
      * @return the simple name of the interaction class that corresponds to
      * the RTI-defined classHandle
      */
    public static String get_simple_class_name( int classHandle ) {
        String hlaClassName = _classHandleNameMap.getOrDefault(classHandle, null);
        return get_simple_class_name( hlaClassName );
    }

    /**
      * Create an interaction that is in instance of interaction class
      * that corresponds to the "classHandle" handle (RTI assigned). An
      * InteractionRoot reference is returned, so to refer to the
      * instance using a reference to a "className" interaction, the returned
      * reference must be cast down the interaction inheritance hierarchy.
      *
      * @param classHandle handle of interaction class (RTI assigned) class for
      * which to create an instance
      * @return instance of interaction class corresponding to "classHandle"
      */
    public static InteractionRoot create_interaction( int classHandle ) {
        String className = _classHandleNameMap.get( classHandle );
        return create_interaction( className );
    }

    /**
      * Like {@link #create_interaction( int classHandle )}, but the interaction
      * is created with a timestamp based on "logicalTime".
      *
      * @param classHandle handle of interaction class (RTI assigned) class for
      * which to create an instance
      * @param logicalTime timestamp to place on the new interaction class instance
      * @return instance of interaction class corresponding to "classHandle" with
      * "logicalTime" time stamp
      */
    public static InteractionRoot create_interaction( int classHandle, LogicalTime logicalTime ) {
        String className = _classHandleNameMap.get( classHandle );
        return create_interaction( className, logicalTime );
    }

    /**
      * Like {@link #create_interaction( int classHandle )}, but the interaction's
      * parameters are initialized using "propertyMap".  The "propertyMap"
      * is usually acquired as an argument to an RTI callback method of a federate.
      *
      * @param classHandle handle of interaction class (RTI assigned) class for
      * which to create an instance
      * @param propertyMap contains initializing values for the parameters
      * of the interaction class instance
      * @return instance of interaction class corresponding to "classHandle" with
      * its parameters initialized with the "propertyMap"
      */
    public static InteractionRoot create_interaction(
      int classHandle, ReceivedInteraction propertyMap
    ) {
        String hlaClassName = _classHandleNameMap.get( classHandle );
        return create_interaction(hlaClassName, propertyMap);
    }

    /**
      * Like {@link #create_interaction( int classHandle, ReceivedInteraction propertyMap )},
      * but the interaction is given a timestamp based on "logicalTime".
      *
      * @param classHandle handle of interaction class (RTI assigned) class for
      * which to create an instance
      * @param propertyMap initializing values for the parameters of the
      * interaction class instance
      * @param logicalTime timestamp to place on the new interaction class instance
      * @return instance of interaction class corresponding to "classHandle" with
      * its parameters initialized with the "propertyMap" and with
      * "logicalTime" timestamp
      */
    public static InteractionRoot create_interaction(
      int classHandle, ReceivedInteraction propertyMap, LogicalTime logicalTime
    ) {
        String hlaClassName = _classHandleNameMap.get( classHandle );
        return create_interaction(hlaClassName, propertyMap, logicalTime);
    }

    //------------------------------------------------------
    // END METHODS THAT USE ONLY CLASS-HANDLE CLASS-NAME MAP
    //------------------------------------------------------

    //-------------------------------------------------------------------------
    // CLASS-NAME INSTANCE MAP
    //
    // POPULATED BY:
    // - STATIC INITIALIZATION BLOCKS IN THE DERIVED INTERACTION/OBJECT CLASSES
    //-------------------------------------------------------------------------
    protected static Map<String, InteractionRoot> _hlaClassNameInstanceMap = new HashMap<>();

    //-----------------------------------------
    // METHODS THAT USE CLASS-NAME INSTANCE MAP
    //-----------------------------------------

    public static InteractionRoot create_interaction(String hlaClassName) {
        InteractionRoot instance = _hlaClassNameInstanceMap.getOrDefault(hlaClassName, null);
        return instance == null ? new InteractionRoot( hlaClassName )
          : instance.createInteraction();
    }

    public static InteractionRoot create_interaction(String hlaClassName, LogicalTime logicalTime) {
        InteractionRoot instance = _hlaClassNameInstanceMap.getOrDefault(hlaClassName, null);
        return instance == null ? new InteractionRoot( hlaClassName, logicalTime )
          : instance.createInteraction( logicalTime );
    }

    public static InteractionRoot create_interaction(String hlaClassName, ReceivedInteraction propertyMap) {
        InteractionRoot instance = _hlaClassNameInstanceMap.getOrDefault(hlaClassName, null);
        return instance == null ? new InteractionRoot( hlaClassName, propertyMap )
          : instance.createInteraction( propertyMap );
    }

    public static InteractionRoot create_interaction(
      String hlaClassName, ReceivedInteraction propertyMap, LogicalTime logicalTime
    ) {
        InteractionRoot instance = _hlaClassNameInstanceMap.getOrDefault(hlaClassName, null);
        return instance == null ? new InteractionRoot( hlaClassName, propertyMap, logicalTime )
          : instance.createInteraction( propertyMap, logicalTime );
    }

    //----------------------------
    // END CLASS-NAME INSTANCE MAP
    //----------------------------

    //------------------------------
    // CLASS-NAME PUBLISH-STATUS MAP
    //
    // POPULATED BY:
    // - init(RTIambassador) ABOVE
    //------------------------------
    protected static HashMap<String, Boolean> _classNamePublishStatusMap = new HashMap<>();

    //-----------------------------------------------
    // METHODS THAT USE CLASS-NAME PUBLISH-STATUS MAP
    //-----------------------------------------------
    public static Boolean get_is_published(String hlaClassName) {
        return _classNamePublishStatusMap.getOrDefault(hlaClassName, null);
    }

    private static void set_is_published(String hlaClassName, boolean publishStatus) {
        if (_classNamePublishStatusMap.containsKey(hlaClassName)) {
            _classNamePublishStatusMap.put(hlaClassName, publishStatus);
            return;
        }
        logger.warn(
          "set_is_published: Could not set publish-status of class \"{}\" to \"{}\":  class not defined.",
          hlaClassName, publishStatus
        );
    }

    //----------------------------------
    // END CLASS-NAME PUBLISH-STATUS MAP
    //----------------------------------

    //--------------------------------
    // CLASS-NAME SUBSCRIBE-STATUS MAP
    //
    // POPULATED BY:
    // - init(RTIambassador) ABOVE
    //--------------------------------
    protected static HashMap<String, Boolean> _classNameSubscribeStatusMap = new HashMap<>();

    //-------------------------------------------------
    // METHODS THAT USE CLASS-NAME SUBSCRIBE-STATUS MAP
    //-------------------------------------------------
    public static Boolean get_is_subscribed(String className) {
        return _classNameSubscribeStatusMap.getOrDefault(className, null);
    }

    private static void set_is_subscribed(String className, boolean subscribeStatus) {
        if (_classNameSubscribeStatusMap.containsKey(className)) {
            _classNameSubscribeStatusMap.put(className, subscribeStatus);
            return;
        }
        logger.warn(
          "setIsSubscribeed: Could not set subscribe-status of class \"{}\" to \"{}\":  class not defined.",
          className, subscribeStatus
        );
    }

    //------------------------------------
    // END CLASS-NAME SUBSCRIBE-STATUS MAP
    //------------------------------------

    //--------------------------------------------
    // CLASS-AND-PROPERTY-NAME PROPERTY-HANDLE MAP
    //
    // POPULATED BY:
    // - init(RTIambassador) ABOVE
    //--------------------------------------------
    protected static Map<ClassAndPropertyName, Integer> _classAndPropertyNameHandleMap = new HashMap<>();

    //--------------------------------------------------------------
    // METHODS THAT USE CLASS-NAME-PROPERTY-NAME PROPERTY-HANDLE MAP
    //--------------------------------------------------------------

    public static ClassAndPropertyName findProperty(String className, String propertyName) {

        List<String> classNameComponents = new ArrayList<>(Arrays.asList(className.split("\\.")));

        while(!classNameComponents.isEmpty()) {
            String localClassName = String.join(".", classNameComponents);

            ClassAndPropertyName key = new ClassAndPropertyName(localClassName, propertyName);
            if (_classAndPropertyNameHandleMap.containsKey(key)) {
                return key;
            }

            classNameComponents.remove(classNameComponents.size() - 1);
        }
        return null;
    }

    /**
      * Returns the handle ofa parameter(RTI assigned) given
      * its interaction class name and parameter name
      *
      * @param hlaClassName name of interaction class
      * @param propertyName name of parameter
      * @return the handle (RTI assigned) of the parameter "propertyName" of interaction class "hlaClassName"
      */
    public static int get_parameter_handle(String hlaClassName, String propertyName) {

        ClassAndPropertyName key = findProperty(hlaClassName, propertyName);

        if (key == null) {
            logger.error(
                    "Bad parameter \"{}\" for class \"{}\" and super-classes on get_parameter_handle.",
                    propertyName, hlaClassName
            );
            return -1;
        }

        return _classAndPropertyNameHandleMap.get(key);
    }
    private SuppliedParameters createSuppliedParameters() {
        SuppliedParameters suppliedParameters = _rtiFactory.createSuppliedParameters();
        for(ClassAndPropertyName classAndPropertyName: classAndPropertyNameValueMap.keySet()) {
            int handle = _classAndPropertyNameHandleMap.get(classAndPropertyName);
            byte[] value = classAndPropertyNameValueMap.get(classAndPropertyName).toString().getBytes();
            suppliedParameters.add(handle, value);
        }
        return suppliedParameters;
    }

    //-------------------------------------------------
    // END CLASS-NAME-PROPERTY-NAME PROPERTY-HANDLE MAP
    //-------------------------------------------------

    //--------------------------------------------
    // PROPERTY-HANDLE CLASS-AND-PROPERTY-NAME MAP
    //
    // POPULATED BY:
    // - init(RTIambassador) ABOVE
    //--------------------------------------------
    protected static Map<Integer, ClassAndPropertyName> _handleClassAndPropertyNameMap = new HashMap<>();

    //-------------------------------------------------------------
    // METHODS THAT USE PROPERTY-HANDLE CLASS-AND-PROPERTY-NAME MAP
    //-------------------------------------------------------------
    /**
      * Returns the name ofa parametercorresponding to
      * its handle (RTI assigned) in propertyHandle.
      *
      * @param propertyHandle handle ofparameter(RTI assigned)
      * for which to return the name
      * @return the name of theparametercorresponding to propertyHandle
      */
    public static ClassAndPropertyName get_class_and_parameter_name( int propertyHandle ) {
        return _handleClassAndPropertyNameMap.getOrDefault(propertyHandle, null);
    }

    /**
      * Returns the full class and parameter names associated with the given handle for an
      * interaction class instance.  The full name of a parameter is the full name of the class in which the
      * parameter is defined and the parameter name, in that order, delimited by a ",".
      *
      * @param propertyHandle a parameter handle assigned by the RTI
      * @return the full parameter name associated with the handle, or null if the handle does not exist.
      */
    public static String get_parameter_name(int propertyHandle) {

        return _handleClassAndPropertyNameMap.containsKey(propertyHandle) ?
                 _handleClassAndPropertyNameMap.get(propertyHandle).getPropertyName() : null;
    }

    public void setParameter(int propertyHandle, Object value) {
        ClassAndPropertyName classAndPropertyName = _handleClassAndPropertyNameMap.get(propertyHandle);
        if (classAndPropertyName == null) {
            logger.error(
              "setParameter(int, Object value): propertyHandle {} does not exist.",
              propertyHandle
            );
            return;
        }

        setParameter(classAndPropertyName.getPropertyName(), value);
    }

    /**
     * Returns the value of the parameter whose handle is "propertyHandle"
     * (RTI assigned) for this interaction.
     *
     * @param propertyHandle handle (RTI assigned) of parameter whose
     * value to retrieve
     * @return the value of the parameter whose handle is "propertyHandle"
     */
    public Object getParameter( int propertyHandle ) {
        ClassAndPropertyName classAndPropertyName = _handleClassAndPropertyNameMap.get(propertyHandle);
        if (classAndPropertyName == null) {
            logger.error("getParameter: propertyHandle {} does not exist.", propertyHandle);
            return null;
        }
        String propertyName = classAndPropertyName.getPropertyName();
        Object value = getParameter(propertyName);
        if (value == null) {
            logger.error(
                "getParameter: propertyHandle {} corresponds to property of name \"{}\", which " +
                "does not exist in class \"{}\" (it's defined in class\"{}\")",
                propertyHandle, propertyName, getClass(), classAndPropertyName.getClassName()
            );
        }

        return value;
    }

    //------------------------------------------------
    // END PROPERTY-HANDLE CLASS-AND-PROPERTY-NAME MAP
    //------------------------------------------------

    public static void publish_interaction(String hlaClassName, RTIambassador rti) {

        if (get_is_published(hlaClassName)) {
            return;
        }

        int classHandle = get_class_handle(hlaClassName);
        synchronized(rti) {
            boolean isNotPublished = true;
            while(isNotPublished) {
                try {
                    rti.publishInteractionClass(classHandle);
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

        logger.debug("publish: {}", hlaClassName);

        set_is_published(hlaClassName, true);
    }

    public static void subscribe_interaction(String hlaClassName, RTIambassador rti) {

        if (get_is_subscribed(hlaClassName)) {
            return;
        }

        int classHandle = get_class_handle(hlaClassName);
        synchronized(rti) {
            boolean isNotSubscribed = true;
            while(isNotSubscribed) {
                try {
                    rti.subscribeInteractionClass(classHandle);
                    isNotSubscribed = false;
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

        logger.debug("subscribe: {}", hlaClassName);

        set_is_subscribed(hlaClassName, true);
    }

    public static void unpublish_interaction(String hlaClassName, RTIambassador rti) {

        if (!get_is_published(hlaClassName)) {
            return;
        }

        int classHandle = get_class_handle(hlaClassName);
        synchronized(rti) {
            boolean isNotUnpublished = true;
            while(isNotUnpublished) {
                try {
                    rti.unpublishInteractionClass(classHandle);
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

        logger.debug("unpublish: {}", hlaClassName);

        set_is_published(hlaClassName, false);
    }

    public static void unsubscribe_interaction(String hlaClassName, RTIambassador rti) {

        if (!get_is_subscribed(hlaClassName)) {
            return;
        }

        int classHandle = get_class_handle(hlaClassName);
        synchronized(rti) {
            boolean isNotUnsubscribed = true;
            while(isNotUnsubscribed) {
                try {
                    rti.unsubscribeInteractionClass(classHandle);
                    isNotUnsubscribed = false;
                } catch (FederateNotExecutionMember e) {
                    logger.error("could not unpublish: Federate Not Execution Member", e);
                    return;
                } catch (InteractionClassNotDefined e) {
                    logger.error("could not unpublish: Interaction Class Not Defined", e);
                    return;
                } catch (InteractionClassNotSubscribed e) {
                    logger.error("could not unpublish: Interaction Class Not Published", e);
                    return;
                } catch (Exception e) {
                    logger.error(e);
                    CpswtUtils.sleepDefault();
                }
            }
        }

        logger.debug("unsubscribe: {}", hlaClassName);

        set_is_subscribed(hlaClassName, false);
    }

    //-----------------------------------------------------------------------------------------------------------
    // PROPERTY-CLASS-NAME AND PROPERTY-VALUE DATA CLASS
    // THIS CLASS IS USED ESPECIALLY FOR THE BENEFIT OF THE SET METHOD BELOW.  WHEN A VALUE IS RETRIEVED FROM THE
    // classPropertyNameValueMap USING A GET METHOD, IT IS PAIRED WITH THE NAME OF THE CLASS IN WHICH THE
    // PROPERTY IS DEFINED. IN THIS WAY, THE SET METHOD CAN PLACE THE NEW VALUE FOR THE PROPERTY USING THE
    // CORRECT (CLASS-NAME, PROPERTY-NAME) KEY.
    //-----------------------------------------------------------------------------------------------------------
    protected static class PropertyClassNameAndValue {
        private final String className;
        private final Object value;

        public PropertyClassNameAndValue(String className, Object value) {
            this.className = className;
            this.value = value;
        }

        public String getClassName() {
            return className;
        }
        public Object getValue() {
            return value;
        }
    }

    //-------------------------------------------
    // CLASS-AND-PROPERTY-NAME PROPERTY-VALUE MAP
    //-------------------------------------------
    protected Map<ClassAndPropertyName, Object> classAndPropertyNameValueMap = new HashMap<>();

    //------------------------------------------------------------
    // METHODS THAT USE CLASS-AND-PROPERTY-NAME PROPERTY-VALUE MAP
    //------------------------------------------------------------
    public Map<ClassAndPropertyName, Object> getClassAndPropertyNameValueMap() {
        return new HashMap<>(classAndPropertyNameValueMap);
    }

    public void setParameter(String propertyName, Object value) {

        PropertyClassNameAndValue propertyClassNameAndValue =
          getParameterAux(getInstanceHlaClassName(), propertyName);

        if (propertyClassNameAndValue == null) {
            logger.error(
              "setparameter(\"{}\", {} value): could not find \"{}\" parameter of class \"{}\" or its " +
              "superclasses.", propertyName, value.getClass().getName(), propertyName, getInstanceHlaClassName()
            );
            return;
        }

        // CANNOT SET VALUE TO NULL
        if (value == null) {
            logger.warn(
              "setParameter(\"{}\", null): attempt to set \"{}\" parameter in " +
              "\"{}\"  class to null.",
              propertyName, propertyName, propertyClassNameAndValue.getClassName()
            );
            return;
        }

        Object currentValue = propertyClassNameAndValue.getValue();

        // IF value IS A STRING, AND THE TYPE OF THE PARAMETER IS A NUMBER-TYPE, TRY TO SEE IF THE
        // STRING CAN BE CONVERTED TO A NUMBER.
        if (value instanceof String && (currentValue instanceof Number || currentValue instanceof Boolean)) {
            Method method;
            try {
                method = currentValue.getClass().getMethod("valueOf", String.class);
            } catch (NoSuchMethodException noSuchMethodException) {
                logger.error(
                  "setParameter(\"{}\", {} value) (for class \"{}\"): unable to access \"valueOf\" " +
                  "method of \"Number\" object: cannot set value!",
                  propertyName, value.getClass().getName(), propertyClassNameAndValue.getClassName()
                );
                return;
            }
            Object newValue = null;
            try {
                // DEAL WITH STRING-VERSIONS OF FLOATING-POINT VALUES THAT ARE TO BE CONVERTED TO AN INTEGRAL TYPE
                String intermediateValue = (String)value;
                if (!(currentValue instanceof Double) && !(currentValue instanceof Float)) {
                    int dotPosition = intermediateValue.indexOf(".");
                    if (dotPosition > 0) {
                        intermediateValue = intermediateValue.substring(0, dotPosition);
                    }
                }
                newValue = method.invoke(null, intermediateValue);
            } catch(Exception e) { }

            if (newValue != null) {
                value = newValue;
            }
        }

        if (currentValue.getClass() != value.getClass()) {
            logger.error(
              "setparameter(\"{}\", {} value): \"value\" is incorrect type \"{}\" for \"{}\" parameter, " +
              "should be of type \"{}\".",
              propertyName,
              value.getClass().getName(),
              value.getClass().getName(),
              propertyName,
              currentValue.getClass().getName()
            );
            return;
        }
        ClassAndPropertyName key =
          new ClassAndPropertyName(propertyClassNameAndValue.getClassName(), propertyName);
        classAndPropertyNameValueMap.put(key, value);
    }

    private PropertyClassNameAndValue getParameterAux(String className, String propertyName) {
        ClassAndPropertyName key = findProperty(className, propertyName);
        if (key != null) {
            Object value = classAndPropertyNameValueMap.get(key);
            return new PropertyClassNameAndValue(key.getClassName(), value);
        }

        return null;
    }

    /**
     * Returns the value of the parameter named "propertyName" for this
     * interaction.
     *
     * @param propertyName name of parameter whose value to retrieve
     * @return the value of the parameter whose name is "propertyName"
     */
    public Object getParameter(String propertyName) {
        PropertyClassNameAndValue propertyClassNameAndValue = getParameterAux(getInstanceHlaClassName(), propertyName);
        return propertyClassNameAndValue == null ? null
          : propertyClassNameAndValue.getValue();
    }

    //-----------------------------------------------
    // END CLASS-AND-PROPERTY-NAME PROPERTY-VALUE MAP
    //-----------------------------------------------

    //---------------------------
    // START OF INCLUDED TEMPLATE
    //---------------------------


    // DUMMY STATIC METHOD TO ALLOW ACTIVE LOADING OF CLASS
    public static void load() { }

    // ----------------------------------------------------------------------------
    // STATIC PROPERTYS AND CODE THAT DEAL WITH NAMES
    // THIS CODE IS STATIC BECAUSE IT IS CLASS-DEPENDENT AND NOT INSTANCE-DEPENDENT
    // ----------------------------------------------------------------------------

    /**
     * Returns the fully-qualified (dot-delimited) name of the org.cpswt.hla.InteractionRoot interaction class.
     * Note: As this is a static method, it is NOT polymorphic, and so, if called on
     * a reference will return the name of the class pertaining to the reference,
     * rather than the name of the class for the instance referred to by the reference.
     * For the polymorphic version of this method, use {@link #getJavaClassName()}.
     *
     * @return the fully-qualified Java class name for this interaction class
     */
    public static String get_java_class_name() {
        return "org.cpswt.hla.InteractionRoot";
    }

    /**
     * Returns the fully-qualified (dot-delimited) name of this instance's interaction class.
     * Polymorphic equivalent of get_java_class_name static method.
     *
     * @return the fully-qualified (dot-delimited) name of this instance's interaction class
     */
    @Override
    public String getJavaClassName() {
        return get_java_class_name();
    }

    /**
     * Returns the simple name (the last name in the dot-delimited fully-qualified
     * class name) of the org.cpswt.hla.InteractionRoot interaction class.
     *
     * @return the name of this interaction class
     */
    public static String get_simple_class_name() {
        return get_simple_class_name(get_hla_class_name());
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

    /**
     * Returns the fully-qualified (dot-delimited) hla class name of the
     * InteractionRoot interaction class.
     * Note: As this is a static method, it is NOT polymorphic, and so, if called on
     * a reference will return the federation name of the class pertaining to the reference,
     * rather than the name of the class for the instance referred to by the reference.
     * For the polymorphic version of this method, use {@link #getHlaClassName()}.
     *
     * @return the fully-qualified federation (HLA) class name for this interaction class
     */
    public static String get_hla_class_name() {
        return "InteractionRoot";
    }

    /**
     * Returns the fully-qualified (dot-delimited) hla class name of this instance's interaction class.
     * Polymorphic equivalent of get_hla_class_name static method.
     *
     * @return the fully-qualified (dot-delimited) name of this instance's interaction class
     */
    @Override
    public String getHlaClassName() {
        return get_hla_class_name();
    }

    /**
     * Returns a sorted list containing the names of all of the non-hidden parameters in the
     * org.cpswt.hla.InteractionRoot interaction class.
     * The property names are paired with name of the hla class in which they are defined in a
     * ClassAndPropertyName POJO.
     * Note: As this is a static method, it is NOT polymorphic, and so, if called on
     * a reference will return a set of class-and0parameter names pertaining to the reference,
     * rather than the parameter names of the class for the instance referred to by
     * the reference.  For the polymorphic version of this method, use
     * {@link #getParameterNames()}.
     *
     * @return a sorted list of the non-hidden parameter names for this interaction class
     * paired with name of the hla class in which they are defined in a ClassAndPropertyName POJO.
     */
    public static List<ClassAndPropertyName> get_parameter_names() {
        return get_parameter_names(get_hla_class_name());
    }

    /**
     * Returns a sorted list containing the names of all of the non-hiddenparameters of an
     * interaction class instance.
     * The property names are paired with name of the hla class in which they are defined in a
     * ClassAndPropertyName POJO.
     * Polymorphic equivalent to get_parameter_names static method.
     *
     * @return sorted list containing the names of all of the parameters of an
     * interaction class instance paired with name of the hla class in which they are defined in a
     * ClassAndPropertyName POJO.
     */
    @Override
    public List<ClassAndPropertyName> getParameterNames() {
        return get_parameter_names();
    }

    /**
     * Returns a sorted list containing the names of all of the parameters in the
     * org.cpswt.hla.InteractionRoot interaction class.
     * The property names are paired with name of the hla class in which they are defined in a
     * ClassAndPropertyName POJO.
     * Note: As this is a static method, it is NOT polymorphic, and so, if called on
     * a reference will return a set of parameter names pertaining to the reference,
     * rather than the parameter names of the class for the instance referred to by
     * the reference.  For the polymorphic version of this method, use
     * {@link #getParameterNames()}.
     *
     * @return a sorted list of the parameter names for this interaction class
     * paired with name of the hla class in which they are defined in a ClassAndPropertyName POJO.
     */
    public static List<ClassAndPropertyName> get_all_parameter_names() {
        return get_all_parameter_names(get_hla_class_name());
    }

    /**
     * Returns a sorted list containing the names of all of the parameters of an
     * interaction class instance.
     * The property names are paired with name of the hla class in which they are defined in a
     * ClassAndPropertyName POJO.
     * Polymorphic equivalent of get_all_parameter_names() static method.
     *
     * @return sorted list containing the names of all of the parameters of an
     * interaction class instance paired with name of the hla class in which they are defined in a
     * ClassAndPropertyName POJO.
     */
    @Override
    public List<ClassAndPropertyName> getAllParameterNames() {
        return get_all_parameter_names();
    }

    /*
     * INITIALIZE STATIC PROPERTYS THAT DEAL WITH NAMES
     */
    static {
        _hlaClassNameSet.add(get_hla_class_name());

        InteractionRoot instance = new InteractionRoot();
        instance.classAndPropertyNameValueMap = null;

        _hlaClassNameInstanceMap.put(get_hla_class_name(), instance);

        Set<ClassAndPropertyName> classAndPropertyNameSet = new HashSet<>();

        // ADD THIS CLASS'S classAndPropertyNameSet TO _classNamePropertyNameSetMap DEFINED
        // IN InteractionRoot
        _classNamePropertyNameSetMap.put(get_hla_class_name(), classAndPropertyNameSet);


        Set<ClassAndPropertyName> allClassAndPropertyNameSet = new HashSet<>();


        // ADD THIS CLASS'S _allClassAndPropertyNameSet TO _allClassNamePropertyNameSetMap DEFINED
        // IN InteractionRoot
        _allClassNamePropertyNameSetMap.put(get_hla_class_name(), allClassAndPropertyNameSet);

        logger.info(
          "Class \"org.cpswt.hla.InteractionRoot\" (hla class \"{}\") loaded", get_hla_class_name()
        );

        System.err.println(
          "Class \"org.cpswt.hla.InteractionRoot\" (hla class \"" +
          get_hla_class_name() + "\") loaded"
        );
    }

    // --------------------------------------------------------
    // END OF STATIC PROPERTYS AND CODE THAT DEAL WITH NAMES.
    // --------------------------------------------------------


    // ----------------------------------------------------------------------------
    // STATIC PROPERTYS AND CODE THAT DEAL WITH HANDLES.
    // THIS CODE IS STATIC BECAUSE IT IS CLASS-DEPENDENT AND NOT INSTANCE-DEPENDENT
    // ----------------------------------------------------------------------------

    /**
     * Returns the handle (RTI assigned) of the org.cpswt.hla.InteractionRoot interaction class.
     * Note: As this is a static method, it is NOT polymorphic, and so, if called on
     * a reference will return the handle of the class pertaining to the reference,
     * rather than the handle of the class for the instance referred to by the reference.
     * For the polymorphic version of this method, use {@link #getClassHandle()}.
     *
     * @return the RTI assigned integer handle that represents this interaction class
     */
    public static int get_class_handle() {
        return _classNameHandleMap.get(get_hla_class_name());
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


    /**
     * Returns the handle of an parameter (RTI assigned) of
     * this interaction class (i.e. "org.cpswt.hla.InteractionRoot") given the parameter's name.
     *
     * @param propertyName name of parameter
     * @return the handle (RTI assigned) of the parameter "propertyName" of interaction class "className"
     */
    public static int get_parameter_handle(String propertyName) {
        return get_parameter_handle(get_hla_class_name(), propertyName);
    }

    /**
     * Returns the handle associated with the given parameter name for an interaction class instance
     * Polymorphic equivalent of get_parameter_handle static method.
     *
     * @param propertyName the name of a parameter that belongs to this interaction class
     * @return the RTI handle associated with the parameter name, or -1 if not found
     */
    public int getParameterHandle(String propertyName) {
        return get_parameter_handle(propertyName);
    }

    // ----------------------------------------------------------
    // END OF STATIC PROPERTYS AND CODE THAT DEAL WITH HANDLES.
    // ----------------------------------------------------------


    //-------------------------------------------------
    // METHODS FOR PUBLISHING/SUBSCRIBING-TO THIS CLASS
    //-------------------------------------------------

    /**
     * Publishes the org.cpswt.hla.InteractionRoot interaction class for a federate.
     *
     * @param rti handle to the Local RTI Component
     */
    public static void publish_interaction(RTIambassador rti) {
        publish_interaction(get_hla_class_name(), rti);
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
     * Unpublishes the org.cpswt.hla.InteractionRoot interaction class for a federate.
     *
     * @param rti handle to the Local RTI Component, usu. obtained through the
     *            {@link SynchronizedFederate#getLRC()} call
     */
    public static void unpublish_interaction(RTIambassador rti) {
        unpublish_interaction(get_hla_class_name(), rti);
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

    /**
     * Subscribes a federate to the org.cpswt.hla.InteractionRoot interaction class.
     *
     * @param rti handle to the Local RTI Component
     */
    public static void subscribe_interaction(RTIambassador rti) {
        subscribe_interaction(get_hla_class_name(), rti);
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
     * Unsubscribes a federate from the org.cpswt.hla.InteractionRoot interaction class.
     *
     * @param rti handle to the Local RTI Component
     */
    public static void unsubscribe_interaction(RTIambassador rti) {
        unsubscribe_interaction(get_hla_class_name(), rti);
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
     * (that is, the org.cpswt.hla.InteractionRoot interaction class).
     *
     * @param handle handle to compare to the value of the handle (RTI assigned) of
     * this class (the org.cpswt.hla.InteractionRoot interaction class).
     * @return "true" if "handle" matches the value of the handle of this class
     * (that is, the org.cpswt.hla.InteractionRoot interaction class).
     */
    public static boolean match(int handle) {
        return handle == get_class_handle();
    }

    //-------------
    // CONSTRUCTORS
    //-------------

    public InteractionRoot() {
        this(get_hla_class_name());
    }

    public InteractionRoot(LogicalTime logicalTime) {
        this();
        setTime(logicalTime);
    }

    public InteractionRoot(ReceivedInteraction propertyMap) {
        this();
        setParameters( propertyMap );
    }

    public InteractionRoot(ReceivedInteraction propertyMap, LogicalTime logicalTime) {
        this(propertyMap);
        setTime(logicalTime);
    }

    //-----------------
    // END CONSTRUCTORS
    //-----------------


    //-----------------
    // CREATION METHODS
    //-----------------
    public static InteractionRoot create_interaction() {
        return new InteractionRoot();
    }

    public InteractionRoot createInteraction() {
        return create_interaction();
    }

    public static InteractionRoot create_interaction(LogicalTime logicalTime) {
        return new InteractionRoot(logicalTime);
    }

    public InteractionRoot createInteraction(LogicalTime logicalTime) {
        return create_interaction(logicalTime);
    }

    public static InteractionRoot create_interaction(ReceivedInteraction propertyMap) {
        return new InteractionRoot(propertyMap);
    }

    public InteractionRoot createInteraction(ReceivedInteraction propertyMap) {
        return create_interaction(propertyMap);
    }

    public static InteractionRoot create_interaction(ReceivedInteraction propertyMap, LogicalTime logicalTime) {
        return new InteractionRoot(propertyMap, logicalTime);
    }

    public InteractionRoot createInteraction(ReceivedInteraction propertyMap, LogicalTime logicalTime) {
        return create_interaction(propertyMap, logicalTime);
    }

    //---------------------
    // END CREATION METHODS
    //---------------------

    //------------------------------
    // PROPERTY MANIPULATION METHODS
    //------------------------------

    //----------------------------------
    // END PROPERTY MANIPULATION METHODS
    //----------------------------------

    //-------------------------
    // END OF INCLUDED TEMPLATE
    //-------------------------
    

    //-------------
    // TIME SET/GET
    //-------------
    private double _time = -1;

    /**
     * Returns the timestamp for this interaction.  "receive order" interactions
     * should have a timestamp of -1.
     *
     * @return timestamp for this interaction
     */
    public double getTime() {
        return _time;
    }

    /**
     * Sets the timestamp of this interaction to "time".
     *
     * @param time new timestamp for this interaction
     */
    public void setTime( double time ) {
        _time = time;
    }

    /**
     * Sets the timestamp of this interaction to "logicalTime".
     *
     * @param logicalTime new timestamp for this interaction
     */
    public void setTime( LogicalTime logicalTime ) {
        DoubleTime doubleTime = new DoubleTime();
        doubleTime.setTo( logicalTime );
        setTime( doubleTime.getTime() );
    }

    //-----------------
    // END TIME SET/GET
    //-----------------

    //------------------------------------------
    // CLASS-AND-PROPERTY-NAME INITIAL-VALUE MAP
    //
    // USED IN:
    // - InteractionRoot( String hlaClassName ) DYNAMIC CONSTRUCTOR
    // - fromJson()
    // - readFederateDynamicMessageClasses(Reader reader) BELOW
    //------------------------------------------
    protected static Map<ClassAndPropertyName, Object> _classAndPropertyNameInitialValueMap = new HashMap<>();

    //-------------
    // CONSTRUCTORS
    //-------------

    /**
     * Creates a new InteractionRoot instance.
     */
    public InteractionRoot( String hlaClassName ) {
        _uniqueID = generateUniqueID();
        setInstanceHlaClassName(hlaClassName);
        if (!_hlaClassNameSet.contains(hlaClassName)) {
            logger.error("Constructor \"InteractionRoot( String hlaClassName )\": " +
              "hlaClassName \"{}\" is not defined -- creating dummy interaction with fictitious type \"{}\"",
              hlaClassName, hlaClassName
            );
            return;
        }

        Set<ClassAndPropertyName> allClassAndPropertyNameSet =
          _allClassNamePropertyNameSetMap.getOrDefault(hlaClassName, null);
        if (allClassAndPropertyNameSet != null) {
            for(ClassAndPropertyName classAndPropertyName: allClassAndPropertyNameSet) {
                Object initialValue = _classAndPropertyNameInitialValueMap.get(classAndPropertyName);
                classAndPropertyNameValueMap.put(classAndPropertyName, initialValue);
            }
        }
    }

    public InteractionRoot( String hlaClassName, LogicalTime logicalTime ) {
        this(hlaClassName);
        setTime( logicalTime );
    }

    public InteractionRoot( String hlaClassName, ReceivedInteraction propertyMap ) {
        this(hlaClassName);
        setParameters( propertyMap );
    }

    public InteractionRoot( String hlaClassName, ReceivedInteraction propertyMap, LogicalTime logicalTime ) {
        this(hlaClassName, propertyMap);
        setTime( logicalTime );
    }

    /**
     * Creates a copy of an InteractionRoot instance.  As an
     * InteractionRoot instance contains no parameters,
     * this has the same effect as the default constructor.
     *
     * @param other the interaction instance to copy
     */
    public InteractionRoot( InteractionRoot other ) {
        this();
        _time = other._time;
        classAndPropertyNameValueMap = new HashMap<>(other.classAndPropertyNameValueMap);
    }

    //-----------------
    // END CONSTRUCTORS
    //-----------------

    public void copyFrom( Object object ) {
        if ( object instanceof InteractionRoot ) {
            InteractionRoot messagingRoot = (InteractionRoot)object;
            for(ClassAndPropertyName key: messagingRoot.classAndPropertyNameValueMap.keySet()) {
                if (classAndPropertyNameValueMap.containsKey(key)) {
                    classAndPropertyNameValueMap.put(
                        key, messagingRoot.classAndPropertyNameValueMap.get(key));
                }
            }
        }
    }

    /**
     * Set the values of the parameters in this interaction using
     * "propertyMap".  "propertyMap" is usually acquired as an argument to
     * an RTI federate callback method such as "receiveInteraction".
     *
     * @param propertyMap  contains new values for the parameters of
     * this interaction
     */
    public void setParameters( ReceivedInteraction propertyMap ) {
        int size = propertyMap.size();
        for( int ix = 0 ; ix < size ; ++ix ) {
            try {
                setParameter(  propertyMap.getParameterHandle( ix ), propertyMap.getValue( ix )  );
            } catch ( Exception e ) {
                logger.error( "setParameters: Exception caught!" );
                logger.error("{}", CpswtUtils.getStackTrace(e));
            }
        }
    }

    private void setParameter( int handle, byte[] value ) {
        if ( value == null ) {
            logger.error( "set:  Attempt to set null value  class \"{}\"", getClass().getName());
            return;
        }
        String valueAsString = new String( value, 0, value.length );
        if (valueAsString.length() > 0 && valueAsString.charAt(valueAsString.length() - 1) == '\0') {
            valueAsString = valueAsString.substring(0, valueAsString.length() - 1);
        }
        setParameter(handle, valueAsString);
    }

    /**
     * Sends this interaction to the RTI, with the specified timestamp "time".
     * This method should be used to send interactions that have "timestamp"
     * ordering.
     *
     * @param rti handle to the RTI, usu. obtained through the
     * {@link SynchronizedFederate#getRTI()} call
     * @param time timestamp for this interaction.  The timestamp should be no
     * less than the current federation time + the LOOKAHEAD value of the federate
     * sending this interaction.
     */
    public void sendInteraction( RTIambassador rti, double time ) throws Exception {
        synchronized( rti ) {
            try {
                SuppliedParameters suppliedParameters = createSuppliedParameters();
                if (suppliedParameters.size() == 0) {
                    return;
                }
                rti.sendInteraction(  getClassHandle(), suppliedParameters, null, new DoubleTime( time )  );
            } catch ( Exception e ) {
                logger.error( "{}:  could not send interaction", getClass().getName());
                logger.error("{}", CpswtUtils.getStackTrace(e));
            }
        }
    }

    /**
     * Sends this interaction to the RTI (without a timestamp).
     * This method should be used to send interactions that have "receive"
     * ordering.
     *
     * @param rti handle to the RTI, usu. obtained through the
     * {@link SynchronizedFederate#getRTI()} call
     */
    public void sendInteraction( RTIambassador rti ) throws Exception {
        synchronized( rti ) {
            try {
                SuppliedParameters suppliedParameters = createSuppliedParameters();
                if (suppliedParameters.size() == 0) {
                    return;
                }
                rti.sendInteraction( getClassHandle(), suppliedParameters, null );
            } catch ( Exception e ) {
                logger.error( "{}:  could not send interaction", getClass().getName() );
                logger.error("{}", CpswtUtils.getStackTrace(e));
            }
        }
    }

    protected static String fedName = null;
    public static Boolean enablePubLog = false;
    public static Boolean enableSubLog = false;
    public static String pubLogLevel = null;
    public static String subLogLevel = null;

    /**
     * For use with the melding API -- this method is used to cast
     * InteractionRoot instance reference into the
     * InteractionRootInterface interface.
     *
     * @param rootInstance InteractionRoot instance reference to be
     * cast into the InteractionRootInterface interface
     * @return InteractionRootInterface reference to the instance
     */
    public InteractionRootInterface cast( InteractionRoot rootInstance ) {
        return rootInstance;
    }

    /**
     * For use with the melding API -- this method creates a new
     * InteractionRoot instance and returns a
     * InteractionRootInterface reference to it.get
     *
     * @return InteractionRootInterface reference to a newly created
     * InteractionRoot instance
     */
    public InteractionRootInterface create() {
        return new InteractionRoot();
    }

    private static Object castNumber(Object object, Class<?> desiredType) {
        if (!desiredType.isInstance(object) && object instanceof Number && Number.class.isAssignableFrom(desiredType)) {
            String desiredTypeName = desiredType.getSimpleName().toLowerCase();
            Method conversionMethod;
            try {
                conversionMethod = object.getClass().getMethod(desiredTypeName + "Value");
                return conversionMethod.invoke(object);
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {}
        }
        return object;
    }

    public String toJson() {
        JSONObject topLevelJSONObject = new JSONObject();
        topLevelJSONObject.put("messaging_type", "interaction");
        topLevelJSONObject.put("messaging_name", getInstanceHlaClassName());

        JSONObject propertyJSONObject = new JSONObject();
        topLevelJSONObject.put("properties", propertyJSONObject);
        for(ClassAndPropertyName key : classAndPropertyNameValueMap.keySet()) {
            Object value = classAndPropertyNameValueMap.get(key);
            propertyJSONObject.put(key.toString(), value);
        }
        return topLevelJSONObject.toString(4);
    }

    public static InteractionRoot fromJson(String jsonString) {
        JSONObject jsonObject = new JSONObject(jsonString);
        String className = jsonObject.getString("messaging_name");
        InteractionRoot interactionRoot = create_interaction(className);
        if (interactionRoot == null) {
            logger.error("InteractionRoot:  fromJson(String):  no such interaction class \"{}\"", className);
            return null;
        }

        JSONObject propertyJSONObject = jsonObject.getJSONObject("properties");
        for (String key : propertyJSONObject.keySet()) {
            ClassAndPropertyName classAndPropertyName = new ClassAndPropertyName(key);

            Class<?> desiredType = _classAndPropertyNameInitialValueMap.get(classAndPropertyName).getClass();
            Object object = castNumber(propertyJSONObject.get(key), desiredType);
            interactionRoot.classAndPropertyNameValueMap.put(classAndPropertyName, object);
        }

        return interactionRoot;
    }
    private static JSONObject federationJson = null;

    public static void readFederationJson(File federationJsonFile) {
        try (
            FileReader fileReader = new FileReader(federationJsonFile)
        ) {
            readFederationJson(fileReader);
        } catch(Exception e) {
            logger.error(
              "readFederationJson: \"{}\" exception on file \"{}\"",
              e.getClass().getSimpleName(),
              federationJsonFile.getAbsolutePath()
            );
        }
    }

    public static void readFederationJson(Reader reader) {
        federationJson = new JSONObject( new JSONTokener(reader) );
    }

    private static final Map<String, Object> _typeInitialValueMap = new HashMap<>();
    static {
        _typeInitialValueMap.put("boolean", false);
        _typeInitialValueMap.put("byte", (byte)0);
        _typeInitialValueMap.put("char", (char)0);
        _typeInitialValueMap.put("double", (double)0);
        _typeInitialValueMap.put("float", (float)0);
        _typeInitialValueMap.put("int", 0);
        _typeInitialValueMap.put("long", (long)0);
        _typeInitialValueMap.put("short", (short)0);
        _typeInitialValueMap.put("String", "");
    }

    public static void readFederateDynamicMessageClasses(File dynamicMessageTypesJsonFile) {
        try (
            FileReader fileReader = new FileReader(dynamicMessageTypesJsonFile)
        ) {
            readFederateDynamicMessageClasses(fileReader);
        } catch(Exception e) {
            logger.error(
              "readFederateDynamicMessageClasses: \"{}\" exception on file \"{}\"",
              e.getClass().getSimpleName(),
              dynamicMessageTypesJsonFile.getAbsolutePath()
            );
        }
    }

    public static void readFederateDynamicMessageClasses(Reader reader) {

        JSONObject dynamicMessageTypes = new JSONObject( new JSONTokener(reader) );
        JSONObject federationMessaging = federationJson.getJSONObject("interactions");

        Set<String> localHlaClassNameSet = new HashSet<>();

        JSONArray dynamicHlaClassNames = dynamicMessageTypes.getJSONArray("interactions");
        for(Object object: dynamicHlaClassNames) {
            String hlaClassName = (String)object;
            List<String> hlaClassNameComponents = new ArrayList<>(Arrays.asList(hlaClassName.split("\\.")));
            while(!hlaClassNameComponents.isEmpty()) {
                String localHlaClassName = String.join(".", hlaClassNameComponents);
                localHlaClassNameSet.add(localHlaClassName);
                hlaClassNameComponents.remove(hlaClassNameComponents.size() - 1);
            }
        }

        for(String hlaClassName: localHlaClassNameSet) {

            _hlaClassNameSet.add(hlaClassName);

            Set<ClassAndPropertyName> classAndPropertyNameSet = new HashSet<>();

            JSONObject messagingPropertyDataMap = federationMessaging.getJSONObject(hlaClassName);
            for(String propertyName: messagingPropertyDataMap.keySet()) {
                ClassAndPropertyName classAndPropertyName = new ClassAndPropertyName(hlaClassName, propertyName);
                classAndPropertyNameSet.add(classAndPropertyName);

                JSONObject typeDataMap = messagingPropertyDataMap.getJSONObject(propertyName);
                if (!typeDataMap.getBoolean("Hidden")) {
                    String propertyTypeString = typeDataMap.getString("ParameterType");
                    Object initialValue = _typeInitialValueMap.get(propertyTypeString);
                    _classAndPropertyNameInitialValueMap.put(classAndPropertyName, initialValue);
                }
            }

            _classNamePropertyNameSetMap.put(hlaClassName, classAndPropertyNameSet);
        }

        for(String hlaClassName: localHlaClassNameSet) {

            Set<ClassAndPropertyName> allClassAndPropertyNameSet = new HashSet<>();

            List<String> hlaClassNameComponents = new ArrayList<>(Arrays.asList(hlaClassName.split("\\.")));
            while(!hlaClassNameComponents.isEmpty()) {
                String localHlaClassName = String.join(".", hlaClassNameComponents);
                allClassAndPropertyNameSet.addAll(_classNamePropertyNameSetMap.get(localHlaClassName));
                hlaClassNameComponents.remove(hlaClassNameComponents.size() - 1);
            }

            _allClassNamePropertyNameSetMap.put(hlaClassName, allClassAndPropertyNameSet);
        }
    }

    public static void loadDynamicClassFederationData(
      Reader federationJsonReader, Reader federateDynamicMessageClassesReader
    ) {
        readFederationJson(federationJsonReader);
        readFederateDynamicMessageClasses(federateDynamicMessageClassesReader);
    }

    public static void loadDynamicClassFederationData(File federationJsonFile, File federateDynamicMessageClassesFile) {
        readFederationJson(federationJsonFile);
        readFederateDynamicMessageClasses(federateDynamicMessageClassesFile);
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        boolean first = true;
        for(ClassAndPropertyName classAndPropertyName: getAllParameterNames()) {
            if (first) first = false;
            else stringBuilder.append(",");
            stringBuilder.append(classAndPropertyName).append("=").
              append(classAndPropertyNameValueMap.get(classAndPropertyName));
        }
        return getInstanceHlaClassName() + "(" + stringBuilder + ")";
    }

}