
/* THIS IS THE ROOT CLASS OF EITHER THE INTERACTION OR OBJECT-CLASS HIERARCHY
(i.e. "InteractionRoot" OR "ObjectRoot") */

/*
 * Copyright (c) 2016, Institute for Software Integrated Systems, Vanderbilt University
 * All rights reserved.
 *
 * Permission to use, copy, modify, and distribute this software and its
 * documentation for any purpose, without fee, and without written agreement is
 * hereby granted, provided that the above copyright notice, the following
 * two paragraphs and the author appear in all copies of this software.
 *
 * IN NO EVENT SHALL THE VANDERBILT UNIVERSITY BE LIABLE TO ANY PARTY FOR
 * DIRECT, INDIRECT, SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES ARISING OUT
 * OF THE USE OF THIS SOFTWARE AND ITS DOCUMENTATION, EVEN IF THE VANDERBILT
 * UNIVERSITY HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * THE VANDERBILT UNIVERSITY SPECIFICALLY DISCLAIMS ANY WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY
 * AND FITNESS FOR A PARTICULAR PURPOSE.  THE SOFTWARE PROVIDED HEREUNDER IS
 * ON AN "AS IS" BASIS, AND THE VANDERBILT UNIVERSITY HAS NO OBLIGATION TO
 * PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, OR MODIFICATIONS.
 *
 * @author Himanshu Neema
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

import org.json.JSONObject;

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
 * {@link #publish( String className, RTIambassador rti )} for example).
 * - methods for getting/setting any parameter in the interaction to
 * which a given InteractionRoot variable is referring
 * (see {@link #getparameter( String propertyName )} and
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

    protected static RtiFactory _factory;
    static {
        boolean factoryNotAcquired = true;
        while( factoryNotAcquired ) {
            try {
                _factory = RtiFactoryFactory.getRtiFactory( "org.portico.dlc.HLA13RTIFactory" );
                factoryNotAcquired = false;
            } catch ( Exception e ) {
                logger.error("failed to acquire factory", e);
                CpswtUtils.sleep(100);
            }
        }
    }

    //------------------------------------------------------
    // BASIC InteractionRoot CREATION METHODS
    //------------------------------------------------------

    private static InteractionRoot create_interaction( Class<?> rtiClass ) {
        InteractionRoot classRoot = null;
        try {
            classRoot = (InteractionRoot)rtiClass.newInstance();
        } catch( Exception e ) {
            logger.error( "InteractionRoot:  create_interaction:  could not create/cast new Interaction" );
            logger.error("{}", CpswtUtils.getStackTrace(e));
        }

        return classRoot;
    }

    private static InteractionRoot create_interaction( Class<?> rtiClass, LogicalTime logicalTime ) {
        InteractionRoot classRoot = create_interaction( rtiClass );
        if ( classRoot != null ) classRoot.setTime( logicalTime );
        return classRoot;
    }

    private static InteractionRoot create_interaction( Class<?> rtiClass, ReceivedInteraction propertyMap ) {
        InteractionRoot classRoot = create_interaction( rtiClass );
        classRoot.setParameters( propertyMap );
        return classRoot;
    }

    private static InteractionRoot create_interaction( Class<?> rtiClass, ReceivedInteraction propertyMap, LogicalTime logicalTime ) {
        InteractionRoot classRoot = create_interaction( rtiClass );
        classRoot.setParameters( propertyMap );
        classRoot.setTime( logicalTime );
        return classRoot;
    }

    //----------------------------------------------------------
    // END BASIC InteractionRoot CREATION METHODS
    //----------------------------------------------------------


    //---------------
    // CLASS-NAME SET
    //---------------
    protected static Set<String> _classNameSet = new HashSet<>();

    //--------------------------------
    // METHODS THAT USE CLASS-NAME-SET
    //--------------------------------
    /**
      * Returns a set of strings containing the names of all of the interaction
      * classes in the current federation.
      *
      * @return Set<String> containing the names of all interaction classes
      * in the current federation
      */
    public static Set<String> get_interaction_names() {
        return new HashSet<>( _classNameSet );
    }
    //-------------------
    // END CLASS-NAME-SET
    //-------------------


    //----------------------------
    // CLASS-NAME CLASS-HANDLE MAP
    //----------------------------
    protected static Map<String, Integer> _classNameHandleMap = new HashMap<>();

    //---------------------------------------------
    // METHODS THAT USE CLASS-NAME CLASS-HANDLE MAP
    //---------------------------------------------
    /**
      * Returns the integer handle (RTI defined) of the interaction class
      * corresponding to the fully-qualified interaction class name in className.
      *
      * @param className fully-qualified name of interaction class for which to
      * retrieve the RTI-defined integer handle
      * @return the RTI-defined handle of the interaction class
      */
    public static int get_class_handle( String className ) {

        Integer classHandle = _classNameHandleMap.get( className );
        if ( classHandle == null ) {
            logger.error( "Bad class name \"{}\" on get_handle.", className );
            return -1;
        }

        return classHandle;
    }

    //--------------------------------
    // END CLASS-NAME CLASS-HANDLE MAP
    //--------------------------------


    //-----------------------------------
    // CLASS-NAME DATAMEMBER-NAME-SET MAP
    //-----------------------------------
    protected static Map<String, Set<ClassAndPropertyName>> _classNamePropertyNameSetMap = new HashMap<>();

    //----------------------------------------------------
    // METHODS THAT USE CLASS-NAME DATAMEMBER-NAME-SET MAP
    //----------------------------------------------------
    /**
      * Returns a set of strings containing the names of all of the non-hidden parameters
      * in the interaction class specified by className.
      *
      * @param className name of interaction class for which to retrieve the
      * names of all of its parameters
      * @return Set<String> containing the names of all parameters in the
      * className interaction class
      */
    public static Set<ClassAndPropertyName> get_parameter_names( String className ) {
        return new HashSet<>(  _classNamePropertyNameSetMap.get( className )  );
    }

    //---------------------------------------
    // END CLASS-NAME DATAMEMBER-NAME-SET MAP
    //---------------------------------------


    //---------------------------------------
    // CLASS-NAME ALL-DATAMEMBER-NAME-SET MAP
    //---------------------------------------
    protected static Map<String, Set<ClassAndPropertyName>> _classNameAllPropertyNameSetMap = new HashMap<>();

    //--------------------------------------------------------
    // METHODS THAT USE CLASS-NAME ALL-DATAMEMBER-NAME-SET MAP
    //--------------------------------------------------------
    /**
      * Returns a set of strings containing the names of all of the parameters
      * in the interaction class specified by className.
      *
      * @param className name of interaction class for which to retrieve the
      * names of all of its parameters
      * @return Set<String> containing the names of all parameters in the
      * className interaction class
      */
    public static Set<ClassAndPropertyName> get_all_parameter_names( String className ) {
        return new HashSet<>(  _classNameAllPropertyNameSetMap.get( className )  );
    }

    //---------------------------------------
    // END CLASS-NAME DATAMEMBER-NAME-SET MAP
    //---------------------------------------

    //-------------------------------------------------
    // CLASS-NAME-DATAMEMBER-NAME DATAMEMBER-HANDLE MAP
    //-------------------------------------------------
    protected static Map<ClassAndPropertyName, Integer> _classAndPropertyNameHandleMap = new HashMap<>();

    //------------------------------------------------------------------
    // METHODS THAT USE CLASS-NAME-DATAMEMBER-NAME DATAMEMBER-HANDLE MAP
    //------------------------------------------------------------------

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
      * @param className name of interaction class
      * @param propertyName name of parameter
      * @return the handle (RTI assigned) of the parameter "propertyName" of interaction class "className"
      */
    public static int get_parameter_handle(String className, String propertyName) {

        ClassAndPropertyName key = findProperty(className, propertyName);

        if (key == null) {
            logger.error(
                    "Bad parameter \"{}\" for class \"{}\" and super-classes on get_parameter_handle.",
                    propertyName, className
            );
            return -1;
        }

        return _classAndPropertyNameHandleMap.get(key);
    }

    //-----------------------------------------------------
    // END CLASS-NAME-DATAMEMBER-NAME DATAMEMBER-HANDLE MAP
    //-----------------------------------------------------


    //------------------------------------------------
    // DATAMEMBER-HANDLE CLASS-AND-DATAMEMBER-NAME MAP
    //------------------------------------------------
    protected static Map<Integer, ClassAndPropertyName> _handleClassAndPropertyNameMap = new HashMap<>();

    //-----------------------------------------------------------------
    // METHODS THAT USE DATAMEMBER-HANDLE CLASS-AND-DATAMEMBER-NAME MAP
    //-----------------------------------------------------------------
    /**
      * Returns the name ofa parametercorresponding to
      * its handle (RTI assigned) in propertyHandle.
      *
      * @param propertyHandle handle ofparameter(RTI assigned)
      * for which to return the name
      * @return the name of theparametercorresponding to propertyHandle
      */
    public static ClassAndPropertyName get_class_and_parameter_name( int propertyHandle ) {
        return _handleClassAndPropertyNameMap.get( propertyHandle );
    }

    /**
      * Returns the parameter name associated with the given handle for an interaction class instance.
      *
      * @param propertyHandle a parameter handle assigned by the RTI
      * @return the parameter name associated with the handle, or null
      */
    public ClassAndPropertyName getClassAndParameterName(int propertyHandle) {
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
    public String getParameterName(int propertyHandle) {
        return _handleClassAndPropertyNameMap.containsKey(propertyHandle) ?
                 _handleClassAndPropertyNameMap.get(propertyHandle).getPropertyName() : null;
    }

    //----------------------------------------------------
    // END DATAMEMBER-HANDLE CLASS-AND-DATAMEMBER-NAME MAP
    //----------------------------------------------------


    //-----------------------------------
    // CLASS-HANDLE CLASS-SIMPLE-NAME MAP
    //-----------------------------------
    protected static Map<Integer, String> _classHandleSimpleNameMap = new HashMap<>();

    //----------------------------------------------------
    // METHODS THAT USE CLASS-HANDLE CLASS-SIMPLE-NAME MAP
    //----------------------------------------------------
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
        return _classHandleSimpleNameMap.get( classHandle );
    }

    //---------------------------------------
    // END CLASS-HANDLE CLASS-SIMPLE-NAME MAP
    //---------------------------------------


    //----------------------------
    // CLASS-HANDLE CLASS-NAME MAP
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
        return _classHandleNameMap.get( classHandle );
    }

    //------------------------------------------------------
    // END METHODS THAT USE ONLY CLASS-HANDLE CLASS-NAME MAP
    //------------------------------------------------------


    //---------------------
    // CLASS-NAME CLASS MAP
    //---------------------
    protected static Map<String, Class<?>> _classNameClassMap = new HashMap<>();

    //-------------------------------------------
    // METHODS THAT USE ONLY CLASS-NAME CLASS MAP
    //-------------------------------------------
    /**
      * Create an interaction that is in instance of interaction class
      * "className". An InteractionRoot reference is returned,
      * so to refer to the instance using a reference to a "className" interaction,
      * the returned reference must be cast down the interaction inheritance
      * hierarchy.
      * An instance of the "className" interaction class may also be created
      * by using the "new" operator directory on the "className" interaction
      * class.  For instance, two ways to create an InteractionRoot
      * instance are
      * Interaction.create_interaction( "InteractionRoot" ),
      * and
      * new InteractionRoot()
      *
      * @param className fully-qualified (dot-delimited) name of the interaction
      * class for which to create an instance
      * @return instance of "className" interaction class
      */
    public static InteractionRoot create_interaction( String className ) {
        Class<?> rtiClass = _classNameClassMap.get( className );
        if ( rtiClass == null ) return null;

        return create_interaction( rtiClass );
    }

    /**
     * Like {@link #create_interaction( String className )}, but interaction
     * is created with a timestamp based on "logicalTime".
     *
     * @param className fully-qualified (dot-delimited) name of the interaction
     * class for which to create an instance
     * @param logicalTime timestamp to place on the new interaction class instance
     * @return instance of "className" interaction class with "logicalTime" time stamp.
     */
    public static InteractionRoot create_interaction( String className, LogicalTime logicalTime ) {
        Class<?> rtiClass = _classNameClassMap.get( className );
        if ( rtiClass == null ) return null;

        return create_interaction( rtiClass, logicalTime );
    }

    //-----------------------------------------------
    // END METHODS THAT USE ONLY CLASS-NAME CLASS MAP
    //-----------------------------------------------


    //---------------------------------------------------------------------------
    // METHODS THAT USE BOTH CLASS-HANDLE CLASS-NAME MAP AND CLASS-NAME CLASS MAP
    //---------------------------------------------------------------------------
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
        Class<?> rtiClass = _classNameClassMap.get(  _classHandleNameMap.get( classHandle )  );
        if ( rtiClass == null ) return null;

        return create_interaction( rtiClass );
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
        Class<?> rtiClass = _classNameClassMap.get(  _classHandleNameMap.get( classHandle )  );
        if ( rtiClass == null ) return null;

        return create_interaction( rtiClass, logicalTime );
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
    public static InteractionRoot create_interaction( int classHandle, ReceivedInteraction propertyMap ) {
        Class<?> rtiClass = _classNameClassMap.get(  _classHandleNameMap.get( classHandle )  );
        if ( rtiClass == null ) return null;

        return create_interaction( rtiClass, propertyMap );
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
    public static InteractionRoot create_interaction( int classHandle, ReceivedInteraction propertyMap, LogicalTime logicalTime ) {
        Class<?> rtiClass = _classNameClassMap.get(  _classHandleNameMap.get( classHandle )  );
        if ( rtiClass == null ) return null;

        return create_interaction( rtiClass, propertyMap, logicalTime );
    }

    //-------------------------------------------------------------------------------
    // END METHODS THAT USE BOTH CLASS-HANDLE CLASS-NAME MAP AND CLASS-NAME CLASS MAP
    //-------------------------------------------------------------------------------

    //--------------------------------
    // END CLASS-HANDLE CLASS-NAME MAP
    //--------------------------------


    //------------------------
    // PUB-SUB-ARGUMENTS ARRAY
    //------------------------
    private static final Class<?>[] pubsubArguments = new Class<?>[] { RTIambassador.class };

    //----------------------------------------------------------------------
    // METHODS THAT USE BOTH PUB-SUB-ARGUMENT-ARRAY AND CLASS-NAME CLASS MAP
    //----------------------------------------------------------------------

    /**
     * Publishes the interaction class named by "className" for a federate.
     * This can also be performed by calling the publish( RTIambassador rti )
     * method directly on the interaction class named by "className" (for
     * example, to publish the InteractionRoot class in particular,
     * see {@link InteractionRoot#publish( RTIambassador rti )}).
     *
     * @param className name of interaction class to be published for the federate
     * @param rti handle to the RTI, usu. obtained through the
     * {@link SynchronizedFederate#getRTI()} call
     */
    public static void publish( String className, RTIambassador rti ) {
        Class<?> rtiClass = _classNameClassMap.get( className );
        if ( rtiClass == null ) {
            logger.error( "Bad class name \"{}\" on publish.", className);
            return;
        }
        try {
            Method method = rtiClass.getMethod( "publish", pubsubArguments );
            method.invoke(null, rti);
        } catch ( Exception e ) {
            logger.error( "Exception caught on publish!" );
            logger.error("{}", CpswtUtils.getStackTrace(e));
        }
    }

    /**
     * Unpublishes the interaction class named by "className" for a federate.
     * This can also be performed by calling the unpublish( RTIambassador rti )
     * method directly on the interaction class named by "className" (for
     * example, to unpublish the InteractionRoot class in particular,
     * see {@link InteractionRoot#unpublish( RTIambassador rti )}).
     *
     * @param className name of interaction class to be unpublished for the federate
     * @param rti handle to the RTI, usu. obtained through the
     * {@link SynchronizedFederate#getRTI()} call
     */
    public static void unpublish( String className, RTIambassador rti ) {
        Class<?> rtiClass = _classNameClassMap.get( className );
        if ( rtiClass == null ) {
            logger.error( "Bad class name \"{}\" on unpublish.", className );
            return;
        }
        try {
            Method method = rtiClass.getMethod( "unpublish", pubsubArguments );
            method.invoke(null, rti);
        } catch ( Exception e ) {
            logger.error( "Exception caught on unpublish!" );
            logger.error("{}", CpswtUtils.getStackTrace(e));
        }
    }

    /**
     * Subscribes federate to the interaction class names by "className"
     * This can also be performed by calling the subscribe( RTIambassador rti )
     * method directly on the interaction class named by "className" (for
     * example, to subscribe a federate to the InteractionRoot class
     * in particular, see {@link InteractionRoot#subscribe( RTIambassador rti )}).
     *
     * @param className name of interaction class to which to subscribe the federate
     * @param rti handle to the RTI, usu. obtained through the
     * {@link SynchronizedFederate#getRTI()} call
     */
    public static void subscribe( String className, RTIambassador rti ) {
        Class<?> rtiClass = _classNameClassMap.get( className );
        if ( rtiClass == null ) {
            logger.error( "Bad class name \"{}\" on subscribe.", className );
            return;
        }
        try {
            Method method = rtiClass.getMethod( "subscribe", pubsubArguments );
            method.invoke(null, rti);
        } catch ( Exception e ) {
            logger.error( "Exception caught on subscribe!" );
            logger.error("{}", CpswtUtils.getStackTrace(e));
        }
    }

    /**
     * Unsubscribes federate from the interaction class names by "className"
     * This can also be performed by calling the unsubscribe( RTIambassador rti )
     * method directly on the interaction class named by "className" (for
     * example, to unsubscribe a federate to the InteractionRoot class
     * in particular, see {@link InteractionRoot#unsubscribe( RTIambassador rti )}).
     *
     * @param className name of interaction class to which to unsubscribe the federate
     * @param rti handle to the RTI, usu. obtained through the
     * {@link SynchronizedFederate#getRTI()} call
     */
    public static void unsubscribe( String className, RTIambassador rti ) {
        Class<?> rtiClass = _classNameClassMap.get( className );
        try {
            Method method = rtiClass.getMethod( "unsubscribe", pubsubArguments );
            method.invoke(null, rti);
        } catch ( Exception e ) {
            logger.error( "Exception caught on unsubscribe!" );
            logger.error("{}", CpswtUtils.getStackTrace(e));
        }
    }

    //--------------------------------------------------------------------------
    // END METHODS THAT USE BOTH PUB-SUB-ARGUMENT-ARRAY AND CLASS-NAME CLASS MAP
    //--------------------------------------------------------------------------//-----------------------------------------------
    // CLASS-AND-DATAMEMBER-NAME DATAMEMBER-VALUE MAP
    //-----------------------------------------------
    protected Map<ClassAndPropertyName, Object> classAndPropertyNameValueMap = new HashMap<>();

    //-----------------------------------------------------------------------------------------------------------
    // DATAMEMBER-CLASS-NAME AND DATAMEMBER-VALUE DATA CLASS
    // THIS CLASS IS USED ESPECIALLY FOR THE BENEFIT OF THE SET METHOD BELOW.  WHEN A VALUE IS RETRIEVED FROM THE
    // classPropertyNameValueMap USING A GET METHOD, IT IS PAIRED WITH THE NAME OF THE CLASS IN WHICH THE
    // DATAMEMBER IS DEFINED. IN THIS WAY, THE SET METHOD CAN PLACE THE NEW VALUE FOR THE DATAMEMBER USING THE
    // CORRECT (CLASS-NAME, DATAMEMBER-NAME) KEY.
    //-----------------------------------------------------------------------------------------------------------
    protected static class PropertyClassNameAndValue {
        String className;
        Object value;

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

    //----------------------------------------------------------------
    // METHODS THAT USE CLASS-AND-DATAMEMBER-NAME DATAMEMBER-VALUE MAP
    //----------------------------------------------------------------
    public void setParameter(String propertyName, Object value) {

        PropertyClassNameAndValue propertyClassNameAndValue =
          getParameterAux(getHlaClassName(), propertyName);

        if (propertyClassNameAndValue == null) {
            logger.error(
              "setparameter(\"{}\", {} value): could not find \"{}\" parameter of class \"{}\" or its " +
              "superclasses.", propertyName, value.getClass().getName(), propertyName, getHlaClassName()
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

        Object currentValue =propertyClassNameAndValue.getValue();

        // IF value IS A STRING, AND THE TYPE OF THE PARAMETER IS A NUMBER-TYPE, TRY TO SEE IF THE
        // STRING CAN BE CONVERTED TO A NUMBER.
        if (value instanceof String && currentValue instanceof Number) {
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
                newValue = method.invoke(null, value);
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

    public void setParameter(int propertyHandle, Object value) {
        ClassAndPropertyName classAndPropertyName = _handleClassAndPropertyNameMap.get(propertyHandle);
        if (classAndPropertyName == null) {
            logger.error(
              "setParameter(int, Object value): propertyHandle {} does not exist.",
              propertyHandle
            );
            return;
        }

        String propertyName = classAndPropertyName.getPropertyName();
        Object currentValue = getParameter(propertyName);
        if (currentValue == null) {
            logger.error(
                "setParameter: propertyHandle {} corresponds to property of name \"{}\", which " +
                "does not exist in class \"{}\" (it's defined in class\"{}\")",
                propertyHandle, propertyName, getClass(), classAndPropertyName.getClassName()
            );
        }

        setParameter(propertyName, value);
    }

    //
    // getParameterAux METHODS ARE DEFINED IN SUBCLASSES.
    //

    /**
     * Returns the value of the parameter named "propertyName" for this
     * interaction.
     *
     * @param propertyName name of parameter whose value to retrieve
     * @return the value of the parameter whose name is "propertyName"
     */
    public Object getParameter(String propertyName) {
        return getParameterAux(getHlaClassName(), propertyName).getValue();
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

    //---------------------------------------------------
    // END CLASS-AND-DATAMEMBER-NAME DATAMEMBER-VALUE MAP
    //---------------------------------------------------

    //---------------------------
    // START OF INCLUDED TEMPLATE
    //---------------------------
    // ----------------------------------------------------------------------------
    // STATIC DATAMEMBERS AND CODE THAT DEAL WITH NAMES
    // THIS CODE IS STATIC BECAUSE IT IS CLASS-DEPENDENT AND NOT INSTANCE-DEPENDENT
    // ----------------------------------------------------------------------------

    /**
     * Returns the fully-qualified (dot-delimited) name of the InteractionRoot interaction class.
     * Note: As this is a static method, it is NOT polymorphic, and so, if called on
     * a reference will return the name of the class pertaining to the reference,
     * rather than the name of the class for the instance referred to by the reference.
     * For the polymorphic version of this method, use {@link #getJavaClassName()}.
     *
     * @return the fully-qualified Java class name for this interaction class
     */
    public static String get_java_class_name() {
        return "InteractionRoot";
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
     * class name) of the InteractionRoot interaction class.
     *
     * @return the name of this interaction class
     */
    public static String get_simple_class_name() {
        return "InteractionRoot";
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
     * Returns the fully-qualified (dot-delimited) federation name of the InteractionRoot interaction class.
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
     * Returns the fully-qualified (dot-delimited) name of this instance's interaction class.
     * Polymorphic equivalent of get_hla_class_name static method.
     *
     * @return the fully-qualified (dot-delimited) name of this instance's interaction class
     */
    @Override
    public String getHlaClassName() {
        return get_hla_class_name();
    }

    private static final Set<ClassAndPropertyName> _classAndPropertyNameList = new HashSet<>();

    /**
     * Returns a set containing the names of all of the non-hidden parameters in the
     * InteractionRoot interaction class.
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
     * InteractionRoot interaction class.
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
        _classNameSet.add(get_hla_class_name());

        // ADD CLASS OBJECT OF THIS CLASS TO _classNameClassMap DEFINED IN InteractionRoot
        _classNameClassMap.put(get_hla_class_name(), InteractionRoot.class);

        // ADD THIS CLASS'S _classAndPropertyNameList TO _classNamePropertyNameSetMap DEFINED
        // IN InteractionRoot
        _classNamePropertyNameSetMap.put(get_hla_class_name(), _classAndPropertyNameList);

        // ADD THIS CLASS'S _allClassAndPropertyNameList TO _classNameAllPropertyNameSetMap DEFINED
        // IN InteractionRoot
        _classNameAllPropertyNameSetMap.put(get_hla_class_name(), _allClassAndPropertyNameList);
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
     * Returns the handle (RTI assigned) of the InteractionRoot interaction class.
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
        ClassAndPropertyName key = new ClassAndPropertyName(get_hla_class_name(), propertyName);
        if (_classAndPropertyNameHandleMap.containsKey(key)) {
            return _classAndPropertyNameHandleMap.get(key);
        }
        logger.error(
          "get_parameter_handle: could not find handle for \"{}\" parameter of class \"{}\" or its " +
          "superclasses.", propertyName, className
        );
        return -1;    
    }

    /**
     * Returns the handle of an parameter (RTI assigned) of
     * this interaction class (i.e. "InteractionRoot") given the parameter's name.
     *
     * @param propertyName name of parameter
     * @return the handle (RTI assigned) of the parameter "propertyName" of interaction class "className"
     */
    public static int get_parameter_handle(String propertyName) {
        return get_parameter_handle_aux(get_hla_class_name(), propertyName);
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

    private static boolean _isInitialized = false;

    /*
     * THIS FUNCTION INITIALIZES ALL OF THE HANDLES ASSOCIATED WITH THIS INTERACTION CLASS
     * IT NEEDS THE RTI TO DO SO.
     */
    protected static void init(RTIambassador rti) {
        if (_isInitialized) return;
        _isInitialized = true;

        boolean isNotInitialized = true;
        while(isNotInitialized) {
            try {
                _handle = rti.getInteractionClassHandle(get_hla_class_name());
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

        _classNameHandleMap.put(get_hla_class_name(), get_class_handle());
        _classHandleNameMap.put(get_class_handle(), get_hla_class_name());
        _classHandleSimpleNameMap.put(get_class_handle(), get_simple_class_name());
    }

    // ----------------------------------------------------------
    // END OF STATIC DATAMEMBERS AND CODE THAT DEAL WITH HANDLES.
    // ----------------------------------------------------------


    //-------------------------------------------------
    // METHODS FOR PUBLISHING/SUBSCRIBING-TO THIS CLASS
    //-------------------------------------------------

    private static boolean _isPublished = false;

    /**
     * Publishes the InteractionRoot interaction class for a federate.
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

        logger.debug("publish: {}", get_hla_class_name());
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
     * Unpublishes the InteractionRoot interaction class for a federate.
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

        logger.debug("unpublish: {}", get_hla_class_name());
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
     * Subscribes a federate to the InteractionRoot interaction class.
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
        logger.debug("subscribe: {}", get_hla_class_name());
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
     * Unsubscribes a federate from the InteractionRoot interaction class.
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
        logger.debug("unsubscribe: {}", get_hla_class_name());
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
     * (that is, the InteractionRoot interaction class).
     *
     * @param handle handle to compare to the value of the handle (RTI assigned) of
     * this class (the InteractionRoot interaction class).
     * @return "true" if "handle" matches the value of the handle of this class
     * (that is, the InteractionRoot interaction class).
     */
    public static boolean match(int handle) {
        return handle == get_class_handle();
    }

    //--------------------------------
    // DATAMEMBER MANIPULATION METHODS
    //--------------------------------
    protected PropertyClassNameAndValue getParameterAux(String className, String propertyName) {
        ClassAndPropertyName key = new ClassAndPropertyName(get_hla_class_name(), "");
        if (classAndPropertyNameValueMap.containsKey(key)) {
            Object value = classAndPropertyNameValueMap.get(key);
            return new PropertyClassNameAndValue(get_hla_class_name(), value);
        }

        logger.error(
          "getparameter(\"{}\"): could not find value for \"{}\" parameter of class \"{}\" or " +
          "its superclasses.", propertyName, propertyName, className
        );

        return null;
    }

    //------------------------------------
    // END DATAMEMBER MANIPULATION METHODS
    //------------------------------------

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


    //-------------
    // CONSTRUCTORS
    //-------------

    /**
     * Creates a new InteractionRoot instance.
     */
    public InteractionRoot() {
        _uniqueID = generateUniqueID();
    }

    /**
     * Creates a copy of an InteractionRoot instance.  As an
     * InteractionRoot instance contains no parameters,
     * this has the same effect as the default constructor.
     *
     * @param interactionRoot the interaction instance to copy
     */
    public InteractionRoot( InteractionRoot other ) {
        this();
        _time = other._time;
        classAndPropertyNameValueMap = new HashMap<>(other.classAndPropertyNameValueMap);

    }

    protected InteractionRoot( ReceivedInteraction propertyMap, boolean initFlag ) {
        this();
        if ( initFlag ) setParameters( propertyMap );
    }

    protected InteractionRoot( ReceivedInteraction propertyMap, LogicalTime logicalTime, boolean initFlag ) {
        this();
        setTime( logicalTime );
        if ( initFlag ) setParameters( propertyMap );
    }


    /**
     * Creates a new interaction instance and initializes its parameters
     * using the "propertyMap" -- this constructor is usually called as a
     * super-class constructor to create and initialize an instance of an
     * interaction further down in the inheritance hierarchy.  "propertyMap"
     * is usually acquired as an argument to an RTI federate callback method, such
     * as "receiveInteraction".
     *
     * @param propertyMap contains parameter values for the newly created
     * interaction
     */
    public InteractionRoot( ReceivedInteraction propertyMap ) {
        this( propertyMap, true );
    }

    /**
     * Like {@link #InteractionRoot( ReceivedInteraction propertyMap )},
     * except the new instance has an initial timestamp of "logicalTime".
     *
     * @param propertyMap contains parameter values for the newly created
     * interaction
     * @param logicalTime initial timestamp for newly created interaction instance
     */
    public InteractionRoot( ReceivedInteraction propertyMap, LogicalTime logicalTime ) {
        this( propertyMap, logicalTime, true );
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

    protected static Map<ClassAndPropertyName, Class> _classAndPropertyNameTypeMap = new HashMap<>();

    private SuppliedParameters createSuppliedParameters() {
        SuppliedParameters suppliedParameters = _factory.createSuppliedParameters();
        for(ClassAndPropertyName classAndPropertyName: classAndPropertyNameValueMap.keySet()) {
            int handle = _classAndPropertyNameHandleMap.get(classAndPropertyName);
            byte[] value = classAndPropertyNameValueMap.get(classAndPropertyName).toString().getBytes();
            suppliedParameters.add(handle, value);
        }
        return suppliedParameters;
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
     * InteractionRootInterface reference to it.
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
        topLevelJSONObject.put("messaging_name", getHlaClassName());

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

            Class<?> desiredType = _classAndPropertyNameTypeMap.get(classAndPropertyName);
            Object object = castNumber(propertyJSONObject.get(key), desiredType);
            interactionRoot.classAndPropertyNameValueMap.put(classAndPropertyName, object);
        }

        return interactionRoot;
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
        return getHlaClassName() + "(" + stringBuilder + ")";
    }

}