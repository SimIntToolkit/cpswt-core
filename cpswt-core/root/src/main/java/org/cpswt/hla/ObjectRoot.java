
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
 * ObjectRoot is the base class for all objects
 * defined in a given federation.  As such, an ObjectRoot
 * variable may refer to any type of interaction defined in the
 * federation.
 * <p/>
 * This ObjectRoot class provides the following:
 * - methods for constructing any object in the federation, either from
 * data provided by the RTI (for example, see
 * {@link #create_object( int classHandle )}) or from a string argument
 * specifying the name of object to construct (see
 * {@link #create_object( String className )}).
 * - methods for sendingobject updatesto the RTI (see
 *{@link#updateAttributeValues( RTIambassador rti )}for an example).
 * - methods for publishing/subscribing to anyobject/object attribute
 * defined in the federation (see
 * {@link #publish_object( String className, RTIambassador rti )} for example).
 * - methods for getting/setting any attribute in the object to
 * which a given ObjectRoot variable is referring
 * (see {@link #getAttribute( String propertyName )} and
 * {@link #setAttribute( String propertyName, Object value )}
 */
@SuppressWarnings("unused")
public class ObjectRoot implements ObjectRootInterface {

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
    // - SUPPLIED ATTRIBUTES
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
                    classHandle = rtiAmbassador.getObjectClassHandle(hlaClassName);
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
                        int propertyHandle = rtiAmbassador.getAttributeHandle(classAndPropertyName.getPropertyName(), classHandle);
                        _classAndPropertyNameHandleMap.put(classAndPropertyName, propertyHandle);
                        _handleClassAndPropertyNameMap.put(propertyHandle, classAndPropertyName);
                        isNotInitialized = false;
                    } catch (FederateNotExecutionMember e) {
                        logger.error("could not initialize: Federate Not Execution Member", e);
                        return;
                    } catch (ObjectClassNotDefined e) {
                        logger.error("could not initialize: Object Class Not Defined", e);
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
            //-------------------------------------------------------
            // FOR OBJECTS, INITIALIZE
            // - _classNamePublishedAttributeHandleSetMap
            // - _classNameSubscribedAttributeHandleSetMap
            // - _classNamePublishedAttributeNameSetMap
            // - _classNameSubscribedAttributeNameSetMap
            //
            // EACH hlaClassName INITIALLY HAS
            // - NO PUBLISHED ATTRIBUTE HANDLES/NAMES
            // - NO SUBSCRIBED ATTRIBUTE HANDLES/NAMES
            //-------------------------------------------------------
            AttributeHandleSet publishedAttributeHandleSet = _rtiFactory.createAttributeHandleSet();
            _classNamePublishedAttributeHandleSetMap.put(hlaClassName, publishedAttributeHandleSet);

            AttributeHandleSet subscribedAttributeHandleSet = _rtiFactory.createAttributeHandleSet();
            _classNameSubscribedAttributeHandleSetMap.put(hlaClassName, subscribedAttributeHandleSet);

            Set<ClassAndPropertyName> publishedAttributeNameSet = new HashSet<>();
            _classNamePublishedAttributeNameSetMap.put(hlaClassName, publishedAttributeNameSet);

            Set<ClassAndPropertyName> subscribedAttributeNameSet = new HashSet<>();
            _classNameSubscribedAttributeNameSetMap.put(hlaClassName, subscribedAttributeNameSet);
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


    //-----------------------------------------------------
    // Attribute CLASS -- USED BY ObjectRoot AND SUBCLASSES
    //-----------------------------------------------------
    protected static class Attribute<T> {
        private T _value;
        private T _oldValue = null;
        private boolean _oldValueInit = false;
        private double _time = 0;

        public Attribute(Attribute<T> other) {
            _value = other._value;
            _oldValue = other._oldValue;
            _oldValueInit = other._oldValueInit;
            _time = other._time;
        }

        public Attribute( T init ) {
            _value = init;
        }

        public T getValue() {
            return _value;
        }

        public void setValue( T value ) {
            if ( value == null ) return;
            _value = value;
        }

        public double getTime() {
            return _time;
        }

        public void setTime( double time ) {
            _time = time;
        }

        public void setHasBeenUpdated() {
            _oldValue = _value;
            _oldValueInit = true;
        }

        public boolean shouldBeUpdated( boolean force ) {
            return force || !_oldValueInit || !_oldValue.equals( _value );
        }

        @Override
        public String toString() {
            return _value.toString();
        }
    }

    //--------------------
    // END Attribute CLASS
    //--------------------

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
    // - ObjectRoot( String hlaClassName ) DYNAMIC CONSTRUCTOR
    // - readFederateDynamicMessageClasses(Reader reader) BELOW
    //----------------------------------------------------------------------------
    /**
      * Returns a set of strings containing the names of all of the object
      * classes in the current federation.
      *
      * @return Set<String> containing the names of all object classes
      * in the current federation
      */
    public static Set<String> get_object_hla_class_name_set() {
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
      * Returns a set of strings containing the names of all of the non-hidden attributes
      * in the object class specified by className.
      *
      * @param hlaClassName name of object class for which to retrieve the
      * names of all of its attributes
      * @return Set<String> containing the names of all attributes in the
      * className object class
      */
    public static List<ClassAndPropertyName> get_attribute_names( String hlaClassName ) {
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
    // - ObjectRoot( String hlaClassName ) DYNAMIC CONSTRUCTOR
    // - readFederateDynamicMessageClasses(Reader reader) BELOW
    //----------------------------------------------------------------------------
    /**
      * Returns a set of strings containing the names of all of the attributes
      * in the object class specified by className.
      *
      * @param hlaClassName name of object class for which to retrieve the
      * names of all of its attributes
      * @return Set<String> containing the names of all attributes in the
      * className object class
      */
    public static List<ClassAndPropertyName> get_all_attribute_names( String hlaClassName ) {
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
      * Returns the integer handle (RTI defined) of the object class
      * corresponding to the fully-qualified object class name in className.
      *
      * @param hlaClassName fully-qualified name of object class for which to
      * retrieve the RTI-defined integer handle
      * @return the RTI-defined handle of the object class
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
    //
    // ALSO USED BY:
    // - discover(class_handle, object_handle ) BELOW
    //--------------------------------------------------
    /**
      * Returns the fully-qualified name of the object class corresponding
      * to the RTI-defined classHandle.
      *
      * @param classHandle handle (defined by RTI) of object class for
      * which to retrieve the fully-qualified name
      * @return the fully-qualified name of the object class that
      * corresponds to the RTI-defined classHandle
      */
    public static String get_hla_class_name( int classHandle ) {
        return _classHandleNameMap.getOrDefault(classHandle, null);
    }

    /**
      * Returns the simple name of the object class corresponding to the
      * RTI-defined classHandle.  The simple name of an object class is
      * the last name in its (dot-delimited) fully-qualified name.
      *
      * @param classHandle handle (defined by RTI) of object class for which
      * to retrieve the simple name
      * @return the simple name of the object class that corresponds to
      * the RTI-defined classHandle
      */
    public static String get_simple_class_name( int classHandle ) {
        String hlaClassName = _classHandleNameMap.getOrDefault(classHandle, null);
        return get_simple_class_name( hlaClassName );
    }

    /**
      * Create an object that is in instance of object class
      * that corresponds to the "classHandle" handle (RTI assigned). An
      * ObjectRoot reference is returned, so to refer to the
      * instance using a reference to a "className" interaction, the returned
      * reference must be cast down the object inheritance hierarchy.
      *
      * @param classHandle handle of object class (RTI assigned) class for
      * which to create an instance
      * @return instance of object class corresponding to "classHandle"
      */
    public static ObjectRoot create_object( int classHandle ) {
        String className = _classHandleNameMap.get( classHandle );
        return create_object( className );
    }

    /**
      * Like {@link #create_object( int classHandle )}, but the object
      * is created with a timestamp based on "logicalTime".
      *
      * @param classHandle handle of object class (RTI assigned) class for
      * which to create an instance
      * @param logicalTime timestamp to place on the new object class instance
      * @return instance of object class corresponding to "classHandle" with
      * "logicalTime" time stamp
      */
    public static ObjectRoot create_object( int classHandle, LogicalTime logicalTime ) {
        String className = _classHandleNameMap.get( classHandle );
        return create_object( className, logicalTime );
    }

    /**
      * Like {@link #create_object( int classHandle )}, but the object's
      * attributes are initialized using "propertyMap".  The "propertyMap"
      * is usually acquired as an argument to an RTI callback method of a federate.
      *
      * @param classHandle handle of object class (RTI assigned) class for
      * which to create an instance
      * @param propertyMap contains initializing values for the attributes
      * of the object class instance
      * @return instance of object class corresponding to "classHandle" with
      * its attributes initialized with the "propertyMap"
      */
    public static ObjectRoot create_object(
      int classHandle, ReflectedAttributes propertyMap
    ) {
        String hlaClassName = _classHandleNameMap.get( classHandle );
        return create_object(hlaClassName, propertyMap);
    }

    /**
      * Like {@link #create_object( int classHandle, ReflectedAttributes propertyMap )},
      * but the object is given a timestamp based on "logicalTime".
      *
      * @param classHandle handle of object class (RTI assigned) class for
      * which to create an instance
      * @param propertyMap initializing values for the attributes of the
      * object class instance
      * @param logicalTime timestamp to place on the new object class instance
      * @return instance of object class corresponding to "classHandle" with
      * its attributes initialized with the "propertyMap" and with
      * "logicalTime" timestamp
      */
    public static ObjectRoot create_object(
      int classHandle, ReflectedAttributes propertyMap, LogicalTime logicalTime
    ) {
        String hlaClassName = _classHandleNameMap.get( classHandle );
        return create_object(hlaClassName, propertyMap, logicalTime);
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
    protected static Map<String, ObjectRoot> _hlaClassNameInstanceMap = new HashMap<>();

    //-----------------------------------------
    // METHODS THAT USE CLASS-NAME INSTANCE MAP
    //-----------------------------------------

    public static ObjectRoot create_object(String hlaClassName) {
        ObjectRoot instance = _hlaClassNameInstanceMap.getOrDefault(hlaClassName, null);
        return instance == null ? new ObjectRoot( hlaClassName )
          : instance.createObject();
    }

    public static ObjectRoot create_object(String hlaClassName, LogicalTime logicalTime) {
        ObjectRoot instance = _hlaClassNameInstanceMap.getOrDefault(hlaClassName, null);
        return instance == null ? new ObjectRoot( hlaClassName, logicalTime )
          : instance.createObject( logicalTime );
    }

    public static ObjectRoot create_object(String hlaClassName, ReflectedAttributes propertyMap) {
        ObjectRoot instance = _hlaClassNameInstanceMap.getOrDefault(hlaClassName, null);
        return instance == null ? new ObjectRoot( hlaClassName, propertyMap )
          : instance.createObject( propertyMap );
    }

    public static ObjectRoot create_object(
      String hlaClassName, ReflectedAttributes propertyMap, LogicalTime logicalTime
    ) {
        ObjectRoot instance = _hlaClassNameInstanceMap.getOrDefault(hlaClassName, null);
        return instance == null ? new ObjectRoot( hlaClassName, propertyMap, logicalTime )
          : instance.createObject( propertyMap, logicalTime );
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
    //----------------------------------------
    // CLASS-NAME PUBLISHED-ATTRIBUTE-NAME SET
    //
    // INITIALIZED BY:
    // - init(RTIambassador) ABOVE
    //----------------------------------------
    protected static Map<String, Set<ClassAndPropertyName>> _classNamePublishedAttributeNameSetMap = new HashMap<>();

    //---------------------------------------------------------
    // METHODS THAT USE CLASS-NAME PUBLISHED-ATTRIBUTE-NAME SET
    //
    // ALSO USED BY:
    // - createSuppliedAttributes(boolean force) BELOW
    // - publish_object BELOW
    //---------------------------------------------------------
    /**
      * Publishes the attribute named by "attributeName" of the object class named
      * by "className" for a federate.  This can also be performed by calling the
      * publish_<attributeName>() method directly on the object class named by
      * "className".
      *
      * Note:  This method only marks the attribute named by "attributeName" for
      * publication.  The attribute doesn't actually get published until the
      * "className" object class, of which it is a member, is (re)published.  See
      * {@link ObjectRoot#publish_object( String className, RTIambassador RTI )} and
      * {@link ObjectRoot#publish_object( RTIambassador RTI )} for examples of how to
      * publish the object class.
      *
      * @param className name of object class for which the attribute named by
      * "attributeName" is to be published
      * @param attributeName name of the attribute to be published
      */
    public static void publish_attribute( String className, String attributeName ) {
        ClassAndPropertyName key = findProperty(className, attributeName);
        if (key == null) {
            logger.error(
                "publish_attribute(\"{}\", \"{}\"):  no such attribute \"{}\" for class \"{}\".",
                className, attributeName, attributeName, className
            );
            return;
        }

        _classNamePublishedAttributeNameSetMap.get(key.getClassName()).add(key);
    }

    /**
      * Unpublishes the attribute named by "attributeName" of the object class named
      * by "className" for a federate.  This can also be performed by calling the
      * unpublish_<attributeName>() method directly on the object class named by
      * "className".
      *
      * Note:  This method only marks the attribute named by "attributeName" for
      * un-publication. The attribute doesn't actually get unpublished until the
      * "className" object class, of which it is a member, is (re)published.  See
      * {@link ObjectRoot#publish_object( String className, RTIambassador RTI )} and
      * {@link ObjectRoot#publish_object( RTIambassador RTI )} for examples of how to
      * publish the object class.
      *
      * @param className name of object class for which the attribute named by
      * "attributeName" is to be unpublished (by a federate)
      * @param attributeName name of the attribute to be unpublished
      */
    public static void unpublish_attribute( String className, String attributeName ) {
        ClassAndPropertyName key = findProperty(className, attributeName);
        if (key == null) {
            logger.error(
                "unpublish_attribute(\"{}\", \"{}\"):  no such attribute \"{}\" for class \"{}\".",
                className, attributeName, attributeName, className
            );
            return;
        }

        _classNamePublishedAttributeNameSetMap.get(key.getClassName()).remove(key);
    }

    //--------------------------------------------
    // END CLASS-NAME PUBLISHED-ATTRIBUTE-NAME SET
    //--------------------------------------------

    //-----------------------------------------
    // CLASS-NAME SUBSCRIBED-ATTRIBUTE-NAME SET
    //
    // INITIALIZED BY:
    // - init(RTIambassador) ABOVE
    //-----------------------------------------
    protected static Map<String, Set<ClassAndPropertyName>> _classNameSubscribedAttributeNameSetMap = new HashMap<>();

    //----------------------------------------------------------
    // METHODS THAT USE CLASS-NAME SUBSCRIBED-ATTRIBUTE-NAME SET
    //
    // ALSO USED BY:
    // - subscribe_object BELOW
    //----------------------------------------------------------
    /**
      * Subscribe a federate to the attribute named by "attributeName" of the
      * object class named by "className".  This can also be performed by calling
      * the subscribe_<attributeName>() method directly on the object class named
      * by "className".
      *
      * Note:  This method only marks the attribute named by "attributeName" for
      * subscription.  The attribute doesn't actually get subscribed to until the
      * "className" object class, of which it is a member, is (re)subscribed to.
      * See {@link ObjectRoot#subscribe_object( String className, RTIambassador RTI )} and
      * {@link ObjectRoot#subscribe_object( RTIambassador RTI )} for examples of how to
      * subscribe to the object class.
      *
      * @param className name of object class for which the attribute named by
      * "attributeName" is to be subcribed
      * @param attributeName name of the attribute to be published
      */
    public static void subscribe_attribute( String className, String attributeName ) {
        ClassAndPropertyName key = findProperty(className, attributeName);
        if (key == null) {
            logger.error(
                "subscribe_attribute(\"{}\", \"{}\"):  no such attribute \"{}\" for class \"{}\".",
                className, attributeName, attributeName, className
            );
            return;
        }
        _classNameSubscribedAttributeNameSetMap.get(key.getClassName()).add(key);
    }

    /**
      * Unsubscribe a federate from the attribute named by "attributeName" of the
      * object class named by "className".  This can also be performed by calling
      * the unsubscribe_<attributeName>() method directly on the object class named
      * by "className".
      *
      * Note:  This method only marks the attribute named by "attributeName" for
      * unsubscription.  The attribute doesn't actually get unsubscribed from until the
      * "className" object class, of which it is a member, is (re)subscribed to.
      * See {@link ObjectRoot#subscribe_object( String className, RTIambassador RTI )} and
      * {@link ObjectRoot#subscribe_object( RTIambassador RTI )} for examples of how to
      * subscribe to the object class.
      *
      * @param className name of object class for which the attribute named by
      * "attributeName" is to be subcribed
      * @param attributeName name of the attribute to be published
      */
    public static void unsubscribe_attribute( String className, String attributeName ) {
        ClassAndPropertyName key = findProperty(className, attributeName);
        if (key == null) {
            logger.error(
                "unsubscribe_attribute(\"{}\", \"{}\"):  no such attribute \"{}\" for class \"{}\".",
                className, attributeName, attributeName, className
            );
            return;
        }
        _classNameSubscribedAttributeNameSetMap.get(key.getClassName()).remove(key);
    }

    //---------------------------------------------
    // END CLASS-NAME SUBSCRIBED-ATTRIBUTE-NAME SET
    //---------------------------------------------

    //--------------------------------------------
    // CLASS-AND-PROPERTY-NAME PROPERTY-HANDLE MAP
    //
    // POPULATED BY:
    // - init(RTIambassador) ABOVE
    //--------------------------------------------
    protected static Map<ClassAndPropertyName, Integer> _classAndPropertyNameHandleMap = new HashMap<>();

    //--------------------------------------------------------------
    // METHODS THAT USE CLASS-NAME-PROPERTY-NAME PROPERTY-HANDLE MAP
    //
    // ALSO USED BY:
    // PUBLISH AND SUBSCRIBE METHODS BELOW
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
      * Returns the handle ofan attribute(RTI assigned) given
      * its object class name and attribute name
      *
      * @param hlaClassName name of object class
      * @param propertyName name of attribute
      * @return the handle (RTI assigned) of the attribute "propertyName" of object class "hlaClassName"
      */
    public static int get_attribute_handle(String hlaClassName, String propertyName) {

        ClassAndPropertyName key = findProperty(hlaClassName, propertyName);

        if (key == null) {
            logger.error(
                    "Bad parameter \"{}\" for class \"{}\" and super-classes on get_attribute_handle.",
                    propertyName, hlaClassName
            );
            return -1;
        }

        return _classAndPropertyNameHandleMap.get(key);
    }
    protected SuppliedAttributes createSuppliedAttributes(boolean force) {
        SuppliedAttributes suppliedAttributes = _rtiFactory.createSuppliedAttributes();

        for(ClassAndPropertyName key: _classNamePublishedAttributeNameSetMap.get(getInstanceHlaClassName())) {
            int handle = _classAndPropertyNameHandleMap.get(key);
            Attribute<?> value = (Attribute<?>)classAndPropertyNameValueMap.get(key);
            if (value.shouldBeUpdated(force)) {
                suppliedAttributes.add(handle, value.toString().getBytes() );
            }
            value.setHasBeenUpdated();
        }

        return suppliedAttributes;
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
      * Returns the name ofan attributecorresponding to
      * its handle (RTI assigned) in propertyHandle.
      *
      * @param propertyHandle handle ofattribute(RTI assigned)
      * for which to return the name
      * @return the name of theattributecorresponding to propertyHandle
      */
    public static ClassAndPropertyName get_class_and_attribute_name( int propertyHandle ) {
        return _handleClassAndPropertyNameMap.getOrDefault(propertyHandle, null);
    }

    /**
      * Returns the full class and attribute names associated with the given handle for an
      * object class instance.  The full name of a parameter is the full name of the class in which the
      * parameter is defined and the parameter name, in that order, delimited by a ",".
      *
      * @param propertyHandle a attribute handle assigned by the RTI
      * @return the full attribute name associated with the handle, or null if the handle does not exist.
      */
    public static String get_attribute_name(int propertyHandle) {

        return _handleClassAndPropertyNameMap.containsKey(propertyHandle) ?
                 _handleClassAndPropertyNameMap.get(propertyHandle).getPropertyName() : null;
    }

    public void setAttribute(int propertyHandle, Object value) {
        ClassAndPropertyName classAndPropertyName = _handleClassAndPropertyNameMap.get(propertyHandle);
        if (classAndPropertyName == null) {
            logger.error(
              "setAttribute(int, Object value): propertyHandle {} does not exist.",
              propertyHandle
            );
            return;
        }

        setAttribute(classAndPropertyName.getPropertyName(), value);
    }

    /**
     * Returns the value of the attribute whose handle is "propertyHandle"
     * (RTI assigned) for this object.
     *
     * @param propertyHandle handle (RTI assigned) of attribute whose
     * value to retrieve
     * @return the value of the attribute whose handle is "propertyHandle"
     */
    public Object getAttribute( int propertyHandle ) {
        ClassAndPropertyName classAndPropertyName = _handleClassAndPropertyNameMap.get(propertyHandle);
        if (classAndPropertyName == null) {
            logger.error("getAttribute: propertyHandle {} does not exist.", propertyHandle);
            return null;
        }
        String propertyName = classAndPropertyName.getPropertyName();
        Object value = getAttribute(propertyName);
        if (value == null) {
            logger.error(
                "getAttribute: propertyHandle {} corresponds to property of name \"{}\", which " +
                "does not exist in class \"{}\" (it's defined in class\"{}\")",
                propertyHandle, propertyName, getClass(), classAndPropertyName.getClassName()
            );
        }

        return value;
    }

    //------------------------------------------------
    // END PROPERTY-HANDLE CLASS-AND-PROPERTY-NAME MAP
    //------------------------------------------------

    //----------------------------------------------
    // CLASS-NAME PUBLISHED-ATTRIBUTE-HANDLE-SET MAP
    //----------------------------------------------
    protected static Map<String, AttributeHandleSet> _classNamePublishedAttributeHandleSetMap = new HashMap<>();

    //---------------------------------------------------------------
    // METHODS THAT USE CLASS-NAME PUBLISHED-ATTRIBUTE-HANDLE-SET MAP
    //---------------------------------------------------------------

    public static void publish_object(String hlaClassName, RTIambassador rti) {

        if (get_is_published(hlaClassName)) {
            return;
        }

        AttributeHandleSet publishedAttributeHandleSet = _classNamePublishedAttributeHandleSetMap.get(hlaClassName);
        publishedAttributeHandleSet.empty();

        Set<ClassAndPropertyName> publishedAttributeNameSet = _classNamePublishedAttributeNameSetMap.get(hlaClassName);
        for(ClassAndPropertyName key : publishedAttributeNameSet) {
            try {
                publishedAttributeHandleSet.add(_classAndPropertyNameHandleMap.get(key));
                logger.trace("publish {}:{}", hlaClassName, key.toString());
            } catch (Exception e) {
                logger.error("could not publish \"" + key.toString() + "\" attribute.", e);
            }
        }

        int classHandle = get_class_handle(hlaClassName);
        synchronized(rti) {
            boolean isNotPublished = true;
            while(isNotPublished) {
                try {
                    rti.publishObjectClass(classHandle, publishedAttributeHandleSet);
                    isNotPublished = false;
                } catch (FederateNotExecutionMember e) {
                    logger.error("could not publish: Federate Not Execution Member", e);
                    return;
                } catch (ObjectClassNotDefined e) {
                    logger.error("could not publish: Object Class Not Defined", e);
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

    //--------------------------------------------------
    // END CLASS-NAME PUBLISHED-ATTRIBUTE-HANDLE-SET MAP
    //--------------------------------------------------

    //-----------------------------------------------
    // CLASS-NAME SUBSCRIBED-ATTRIBUTE-HANDLE-SET MAP
    //-----------------------------------------------
    protected static Map<String, AttributeHandleSet> _classNameSubscribedAttributeHandleSetMap = new HashMap<>();

    //----------------------------------------------------------------
    // METHODS THAT USE CLASS-NAME SUBSCRIBED-ATTRIBUTE-HANDLE-SET MAP
    //----------------------------------------------------------------

    public static void subscribe_object(String hlaClassName, RTIambassador rti) {

        if (get_is_subscribed(hlaClassName)) {
            return;
        }

        AttributeHandleSet subscribedAttributeHandleSet = _classNameSubscribedAttributeHandleSetMap.get(hlaClassName);
        subscribedAttributeHandleSet.empty();

        Set<ClassAndPropertyName> subscribedAttributeNameSet = _classNameSubscribedAttributeNameSetMap.get(hlaClassName);
        for(ClassAndPropertyName key : subscribedAttributeNameSet) {
            try {
                subscribedAttributeHandleSet.add(_classAndPropertyNameHandleMap.get(key));
                logger.trace("subscribe {}:{}", hlaClassName, key.toString());
            } catch (Exception e) {
                logger.error("could not subscribe to \"" + key.toString() + "\" attribute.", e);
            }
        }

        int classHandle = get_class_handle(hlaClassName);
        synchronized(rti) {
            boolean isNotSubscribed = true;
            while(isNotSubscribed) {
                try {
                    rti.subscribeObjectClassAttributes(classHandle, subscribedAttributeHandleSet);
                    isNotSubscribed = false;
                } catch (FederateNotExecutionMember e) {
                    logger.error("could not publish: Federate Not Execution Member", e);
                    return;
                } catch (ObjectClassNotDefined e) {
                    logger.error("could not publish: Object Class Not Defined", e);
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

    //--------------------------------------------------
    // END CLASS-NAME PUBLISHED-ATTRIBUTE-HANDLE-SET MAP
    //--------------------------------------------------

    public static void unpublish_object(String hlaClassName, RTIambassador rti) {

        if (!get_is_published(hlaClassName)) {
            return;
        }

        int classHandle = get_class_handle(hlaClassName);
        synchronized(rti) {
            boolean isNotUnpublished = true;
            while(isNotUnpublished) {
                try {
                    rti.unpublishObjectClass(classHandle);
                    isNotUnpublished = false;
                } catch (FederateNotExecutionMember e) {
                    logger.error("could not unpublish: Federate Not Execution Member", e);
                    return;
                } catch (ObjectClassNotDefined e) {
                    logger.error("could not unpublish: Object Class Not Defined", e);
                    return;
                } catch (ObjectClassNotPublished e) {
                    logger.error("could not unpublish: Object Class Not Published", e);
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

    public static void unsubscribe_object(String hlaClassName, RTIambassador rti) {

        if (!get_is_subscribed(hlaClassName)) {
            return;
        }

        int classHandle = get_class_handle(hlaClassName);
        synchronized(rti) {
            boolean isNotUnsubscribed = true;
            while(isNotUnsubscribed) {
                try {
                    rti.unsubscribeObjectClass(classHandle);
                    isNotUnsubscribed = false;
                } catch (FederateNotExecutionMember e) {
                    logger.error("could not unpublish: Federate Not Execution Member", e);
                    return;
                } catch (ObjectClassNotDefined e) {
                    logger.error("could not unpublish: Object Class Not Defined", e);
                    return;
                } catch (ObjectClassNotSubscribed e) {
                    logger.error("could not unpublish: Object Class Not Published", e);
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

    //--------------
    // OBJECT HANDLE
    //--------------
    private int _objectHandle;


    //------------------------------------
    // METHODS THAT USE ONLY OBJECT HANDLE
    //------------------------------------
    /**
     * Returns the handle (RTI assigned) the corresponds to this object class
     * instance.  This handle is the instance's unique identifier to the RTI.
     *
     * @return the handle (RTI assigned) of this object class instance.
     */
    public int getObjectHandle() {
        return _objectHandle;
    }

    //----------------------------------------
    // END METHODS THAT USE ONLY OBJECT HANDLE
    //----------------------------------------


    //----------------------------------
    // OBJECT-HANDLE OBJECT-INSTANCE MAP
    //----------------------------------
    private static final Map<Integer, ObjectRoot> _objectHandleInstanceMap = new HashMap<>();

    //--------------------------------------------------------
    // METHODS THAT USE ONLY OBJECT-HANDLE OBJECT-INSTANCE MAP
    //--------------------------------------------------------
    /**
      * Returns the object instance corresponding to the "object_handle" (RTI
      * assigned) from a map internal to the ObjectRoot class.
      * The object instance is referred to, via the return value, using an
      * an ObjectRoot reference.  To reference to it using a reference of its
      * actual class, the returned reference must be cast down through the
      * inhertance hierarchy.
      *
      * @param object_handle handle (RTI assigned) of object instance to retrieve
      * from the map internal to the ObjectRoot class.
      * @return object instance corresponding to the object_handle (RTI assigned)
      * in the map that is internal to the ObjectRoot class.
      */
    public static ObjectRoot get_object( int object_handle ) {
        return _objectHandleInstanceMap.get( object_handle );
    }

    /**
     * Returns the object instance corresponding to the "object_handle" (RTI
     * assigned) from a map internal to the ObjectRoot class AND REMOVES IT
     * FROM THIS MAP.
     * The object instance is referred to, via the return value, using an
     * an ObjectRoot reference.  To reference to it using a reference of its
     * actual class, the returned reference must be cast down through the
     * inhertance hierarchy.
     *
     * @param object_handle handle (RTI assigned) of object instance to retrieve
     * from the map internal to the ObjectRoot class.
     * @return object instance corresponding to the object_handle (RTI assigned)
     * in the map that is internal to the ObjectRoot class.
     */
    public static ObjectRoot remove_object( int object_handle ) {
        return _objectHandleInstanceMap.remove( object_handle );
    }

    /**
     * Retrieves the object instance corresponding to "object_handle" from an
     * internal table in the ObjectRoot class, updates its attribute values using
     * "reflectedAttributes", and returns the instance.  Both "object_handle" and
     * "reflectedAttributes" are usually acquired as arguments of the
     * "reflectAttributeValues" RTI callback of a federate.
     * The return value is an ObjectRoot reference to the instance.  So, to refer
     * to the instance as an instance of its actual class, this reference will
     * have to be cast down the inheritance hierarchy.
     *
     * @param object_handle handle (RTI assigned) of object instance for which the
     * attributes are to be updated.
     * @param reflectedAttributes set of updated values for the attributes of the
     * object instance corresponding to object_handle.
     * @return the object instance with updated attribute values
     */
    public static ObjectRoot reflect( int object_handle, ReflectedAttributes reflectedAttributes ) {
        ObjectRoot objectRoot = _objectHandleInstanceMap.get( object_handle );
        if ( objectRoot == null ) return null;
        objectRoot.setTime( -1 );
        objectRoot.setAttributes( reflectedAttributes );
        return objectRoot;
    }

    /**
     * Like {@link #reflect( int object_handle, ReflectedAttributes reflectedAttributes )},
     * except the updated attributes of the object instance have their timestamps
     * updated to "logicalTime".
     *
     * @param object_handle handle (RTI assigned) of object instance for which the
     * attributes are to be updated.
     * @param reflectedAttributes set of updated values for the attributes of the
     * object instance corresponding to object_handle.
     * @param logicalTime new time stamp for attributes that are updated
     * @return the object instance with updated attribute values
     */
    public static ObjectRoot reflect( int object_handle, ReflectedAttributes reflectedAttributes, LogicalTime logicalTime ) {
        ObjectRoot objectRoot = _objectHandleInstanceMap.get( object_handle );
        if ( objectRoot == null ) return null;
        objectRoot.setTime( logicalTime );
        objectRoot.setAttributes( reflectedAttributes );
        return objectRoot;
    }

    /**
     * Like {@link #reflect( int object_handle, ReflectedAttributes reflectedAttributes )},
     * except the updated attributes of the object instance have their timestamps
     * updated to "time".
     *
     * @param object_handle handle (RTI assigned) of object instance for which the
     * attributes are to be updated.
     * @param reflectedAttributes set of updated values for the attributes of the
     * object instance corresponding to object_handle.
     * @param time new time stamp for attributes that are updated
     * @return the object instance with updated attribute values
     */
    public static ObjectRoot reflect( int object_handle, ReflectedAttributes reflectedAttributes, double time ) {
        ObjectRoot objectRoot = _objectHandleInstanceMap.get( object_handle );
        if ( objectRoot == null ) return null;
        objectRoot.setTime( time );
        objectRoot.setAttributes( reflectedAttributes );
        return objectRoot;
    }

    //------------------------------------------------------------
    // END METHODS THAT USE ONLY OBJECT-HANDLE OBJECT-INSTANCE MAP
    //------------------------------------------------------------

    //---------------------------------------------------
    // METHODS THAT USE BOTH OBJECT MAP AND OBJECT HANDLE
    //---------------------------------------------------
    private void setObjectHandle( int object_handle ) {
        _objectHandleInstanceMap.remove( object_handle );
        _objectHandle = object_handle;
        _objectHandleInstanceMap.put( object_handle, this );
    }

    //-------------------------------------------------------
    // END METHODS THAT USE BOTH OBJECT MAP AND OBJECT HANDLE
    //-------------------------------------------------------


    //------------------------------------------------------------------------------------------------------
    // METHODS THAT USE OBJECT-HANDLE INSTANCE MAP, CLASS-HANDLE CLASS-NAME MAP, AND CLASS-NAME INSTANCE MAP
    //------------------------------------------------------------------------------------------------------
    /**
     * Creates a new instance of the object class corresponding to "class_handle",
     * registers it in an map internal to the ObjectRoot class using "object_handle"
     * as a key, and returns a reference to the instance.  Though the created
     * instance is of the object class corresponding to "class_handle" (which is
     * a handle assigned by the RTI), it is referred to, via the return value, by
     * an ObjectRoot reference.  Thus, to refer to it as an instance of the object
     * class corresponding to "class_handle", the ObjectRoot reference needs to be
     * cast down through the inheritance hierarchy.
     * <p/>
     * class_handle and object_handle are usually acquired as arguments of the
     * "discoverObjectInstance" RTI callback method of a federate.
     *
     * @param class_handle handle of object class (RTI assigned) for which to create
     * an instance
     * @param object_handle handle (also RTI assigned) of this instance as it is
     * known to the RTI.  Any updates to the instance attributes provided by the
     * RTI (via a "reflectAttributeValues" federate callback) will be identified
     * with this object_handle.
     * @return new instance of the object class corresponding to class_handle
     */
    public static ObjectRoot discover( int class_handle, int object_handle ) {
        String hlaClassName = _classHandleNameMap.get( class_handle );
        ObjectRoot instance = _hlaClassNameInstanceMap.get( hlaClassName );

        ObjectRoot newInstance = instance.createObject();
        newInstance.setObjectHandle( object_handle );

        _objectHandleInstanceMap.put(object_handle, newInstance);

        return newInstance;
    }

    //----------------------------------------------------------------------------------------------------------
    // END METHODS THAT USE OBJECT-HANDLE INSTANCE MAP, CLASS-HANDLE CLASS-NAME MAP, AND CLASS-NAME INSTANCE MAP
    //----------------------------------------------------------------------------------------------------------

    //--------------
    // IS REGISTERED
    //--------------
    private boolean _isRegistered = false;

    //------------------------------------------------------------------------------
    // METHODS THAT USE OBJECT-HANDLE INSTANCE MAP, OBJECT HANDLE, AND IS REGISTERED
    //------------------------------------------------------------------------------
    /**
      * Registers this object with the RTI.  This method is usually called by a
      * federate who "owns" this object, i.e. the federate that created it and
      * has write-privileges to its attributes (so, it is responsible for updating
      * these attribute and conveying their updated values to the RTI).
      *
      * @param rti handle to the RTI
      */
    public void registerObject(RTIambassador rti) {
        while (!_isRegistered) {
            try {
                synchronized (rti) {
                    setObjectHandle( rti.registerObjectInstance(getClassHandle()) );
                }
                _isRegistered = true;
            } catch (ObjectClassNotDefined | ObjectClassNotPublished | FederateNotExecutionMember e) {
                logger.error("{}", CpswtUtils.getStackTrace(e));
                return;
            } catch (Exception e) {
                CpswtUtils.sleep(500);
            }
        }
    }

    /**
      * Registers this object with the RTI using the given name.  This method is usually
      * called by a federate who "owns" this object, i.e. the federate that created it and
      * has write-privileges to its attributes (so, it is responsible for updating
      * these attribute and conveying their updated values to the RTI).
      *
      * @param rti handle to the RTI
      * @param name unique identifier to assign to the object instance
      * @throws ObjectAlreadyRegistered if the name is already assigned to another object instance
      */
    public void registerObject(RTIambassador rti, String name) throws ObjectAlreadyRegistered {

        while (!_isRegistered) {
            try {
                synchronized (rti) {
                    setObjectHandle( rti.registerObjectInstance(getClassHandle(), name) );
                }
                _isRegistered = true;
            } catch (ObjectClassNotDefined | ObjectClassNotPublished | FederateNotExecutionMember ex) {
                logger.error("{}", CpswtUtils.getStackTrace(ex));
                return;
            } catch (SaveInProgress | RestoreInProgress | RTIinternalError | ConcurrentAccessAttempted e) {
                CpswtUtils.sleep(500);
            }
        }
    }

    /**
     * Unregisters this object with the RTI.  The RTI will destroy all information
     * it contains regarding this object as a result.  This method is usually
     * called by a federate who "owns" this object, i.e. the federate that created
     * it and has write-privileges to its attributes.
     *
     * @param rti handle to the RTI, usu. obtained through the
     * {@link SynchronizedFederate#getRTI()} call
     */
    public void unregisterObject( RTIambassador rti ) {

        while( _isRegistered ) {
            try {
                synchronized( rti ) {
                    rti.deleteObjectInstance(getObjectHandle(), null);
                }
                _objectHandleInstanceMap.remove( getObjectHandle() );
                _isRegistered = false;
            } catch ( ObjectNotKnown | DeletePrivilegeNotHeld | FederateNotExecutionMember e) {
                logger.error("{}", CpswtUtils.getStackTrace(e));
            } catch ( Exception e ) {
                CpswtUtils.sleep(500);
            }
        }
    }
    //------------------------------------------------------------------
    // END METHODS THAT USE OBJECT MAP, OBJECT HANDLE, AND IS REGISTERED
    //------------------------------------------------------------------

    //------------------
    // END IS REGISTERED
    //------------------

    //---------------
    // END OBJECT MAP
    //---------------

    //------------------
    // END OBJECT HANDLE
    //------------------

    /**
     * Requests an attribute update for this object instance from the federate that
     * has modification rights on these attributes.
     *
     * @param rti handle to the RTI, usu. obtained through the
     * {@link SynchronizedFederate#getRTI()} call
     */
    public void requestUpdate( RTIambassador rti ) {
        boolean requestNotSubmitted = true;
        while( requestNotSubmitted ) {
            try {
                rti.requestObjectAttributeValueUpdate( getObjectHandle(), get_subscribed_attribute_handle_set() );
                requestNotSubmitted = false;
            } catch ( FederateNotExecutionMember f ) {
                logger.error( "{}: request for update failed:  Federate Not Execution Member", getInstanceHlaClassName() );
                logger.error("{}", CpswtUtils.getStackTrace(f));
                return;
            } catch ( ObjectNotKnown o ) {
                logger.error( "{}: request for update failed:  Object Not Known", getInstanceHlaClassName() );
                logger.error("{}", CpswtUtils.getStackTrace(o));
                return;
            } catch ( AttributeNotDefined a ) {
                logger.error( "{}: request for update failed:  Name Not Found", getInstanceHlaClassName() );
                logger.error("{}", CpswtUtils.getStackTrace(a));
                return;
            } catch ( Exception e ) {
                logger.error("{}", CpswtUtils.getStackTrace(e));
                CpswtUtils.sleep( 50 );
            }
        }
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

    public void setAttribute(String hlaClassName, String propertyName, Object value) {

        PropertyClassNameAndValue propertyClassNameAndValue =
          getAttributeAux(hlaClassName, propertyName);

        if (propertyClassNameAndValue == null) {
            logger.error(
              "setattribute(\"{}\", {} value): could not find \"{}\" attribute of class \"{}\" or its " +
              "superclasses.", propertyName, value.getClass().getName(), propertyName, getInstanceHlaClassName()
            );
            return;
        }

        // CANNOT SET VALUE TO NULL
        if (value == null) {
            logger.warn(
              "setAttribute(\"{}\", null): attempt to set \"{}\" attribute in " +
              "\"{}\"  class to null.",
              propertyName, propertyName, propertyClassNameAndValue.getClassName()
            );
            return;
        }

        Object currentValue = ((Attribute<?>)propertyClassNameAndValue.getValue()).getValue();

        // IF value IS A STRING, AND THE TYPE OF THE ATTRIBUTE IS A NUMBER-TYPE, TRY TO SEE IF THE
        // STRING CAN BE CONVERTED TO A NUMBER.
        if (value instanceof String && (currentValue instanceof Number || currentValue instanceof Boolean)) {
            Method method;
            try {
                method = currentValue.getClass().getMethod("valueOf", String.class);
            } catch (NoSuchMethodException noSuchMethodException) {
                logger.error(
                  "setAttribute(\"{}\", {} value) (for class \"{}\"): unable to access \"valueOf\" " +
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
              "setattribute(\"{}\", {} value): \"value\" is incorrect type \"{}\" for \"{}\" parameter, " +
              "should be of type \"{}\".",
              propertyName,
              value.getClass().getName(),
              value.getClass().getName(),
              propertyName,
              currentValue.getClass().getName()
            );
            return;
        }
        ((Attribute<Object>)propertyClassNameAndValue.getValue()).setValue(value);
    }

    public void setAttribute(String propertyName, Object value) {
        setAttribute(getInstanceHlaClassName(), propertyName, value);
    }

    private PropertyClassNameAndValue getAttributeAux(String className, String propertyName) {
        ClassAndPropertyName key = findProperty(className, propertyName);
        if (key != null) {
            Object value = classAndPropertyNameValueMap.get(key);
            return new PropertyClassNameAndValue(key.getClassName(), value);
        }

        return null;
    }

    /**
     * Returns the value of the attribute named "propertyName" for this
     * object.
     *
     * @param propertyName name of attribute whose value to retrieve
     * @return the value of the attribute whose name is "propertyName"
     */
    public Object getAttribute(String hlaClassName, String propertyName) {
        PropertyClassNameAndValue propertyClassNameAndValue = getAttributeAux(hlaClassName, propertyName);
        return propertyClassNameAndValue == null ? null
          : ((Attribute<?>)propertyClassNameAndValue.getValue()).getValue();
    }

    public Object getAttribute(String propertyName) {
        return getAttribute(getInstanceHlaClassName(), propertyName);
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
     * Returns the fully-qualified (dot-delimited) name of the org.cpswt.hla.ObjectRoot object class.
     * Note: As this is a static method, it is NOT polymorphic, and so, if called on
     * a reference will return the name of the class pertaining to the reference,
     * rather than the name of the class for the instance referred to by the reference.
     * For the polymorphic version of this method, use {@link #getJavaClassName()}.
     *
     * @return the fully-qualified Java class name for this object class
     */
    public static String get_java_class_name() {
        return "org.cpswt.hla.ObjectRoot";
    }

    /**
     * Returns the fully-qualified (dot-delimited) name of this instance's object class.
     * Polymorphic equivalent of get_java_class_name static method.
     *
     * @return the fully-qualified (dot-delimited) name of this instance's object class
     */
    @Override
    public String getJavaClassName() {
        return get_java_class_name();
    }

    /**
     * Returns the simple name (the last name in the dot-delimited fully-qualified
     * class name) of the org.cpswt.hla.ObjectRoot object class.
     *
     * @return the name of this object class
     */
    public static String get_simple_class_name() {
        return get_simple_class_name(get_hla_class_name());
    }

    /**
     * Returns the simple name (last name in its fully-qualified dot-delimited name)
     * of this instance's object class.
     * Polymorphic equivalent of the get_simple_class_name static method.
     *
     * @return the simple name of this instance's object class
     */
    @Override
    public String getSimpleClassName() {
        return get_simple_class_name();
    }

    /**
     * Returns the fully-qualified (dot-delimited) hla class name of the
     * ObjectRoot object class.
     * Note: As this is a static method, it is NOT polymorphic, and so, if called on
     * a reference will return the federation name of the class pertaining to the reference,
     * rather than the name of the class for the instance referred to by the reference.
     * For the polymorphic version of this method, use {@link #getHlaClassName()}.
     *
     * @return the fully-qualified federation (HLA) class name for this object class
     */
    public static String get_hla_class_name() {
        return "ObjectRoot";
    }

    /**
     * Returns the fully-qualified (dot-delimited) hla class name of this instance's object class.
     * Polymorphic equivalent of get_hla_class_name static method.
     *
     * @return the fully-qualified (dot-delimited) name of this instance's object class
     */
    @Override
    public String getHlaClassName() {
        return get_hla_class_name();
    }

    /**
     * Returns a sorted list containing the names of all of the non-hidden attributes in the
     * org.cpswt.hla.ObjectRoot object class.
     * The property names are paired with name of the hla class in which they are defined in a
     * ClassAndPropertyName POJO.
     * Note: As this is a static method, it is NOT polymorphic, and so, if called on
     * a reference will return a set of class-and0parameter names pertaining to the reference,
     * rather than the parameter names of the class for the instance referred to by
     * the reference.  For the polymorphic version of this method, use
     * {@link #getAttributeNames()}.
     *
     * @return a sorted list of the non-hidden attribute names for this object class
     * paired with name of the hla class in which they are defined in a ClassAndPropertyName POJO.
     */
    public static List<ClassAndPropertyName> get_attribute_names() {
        return get_attribute_names(get_hla_class_name());
    }

    /**
     * Returns a sorted list containing the names of all of the non-hiddenattributes of an
     * object class instance.
     * The property names are paired with name of the hla class in which they are defined in a
     * ClassAndPropertyName POJO.
     * Polymorphic equivalent to get_attribute_names static method.
     *
     * @return sorted list containing the names of all of the attributes of an
     * object class instance paired with name of the hla class in which they are defined in a
     * ClassAndPropertyName POJO.
     */
    @Override
    public List<ClassAndPropertyName> getAttributeNames() {
        return get_attribute_names();
    }

    /**
     * Returns a sorted list containing the names of all of the attributes in the
     * org.cpswt.hla.ObjectRoot object class.
     * The property names are paired with name of the hla class in which they are defined in a
     * ClassAndPropertyName POJO.
     * Note: As this is a static method, it is NOT polymorphic, and so, if called on
     * a reference will return a set of parameter names pertaining to the reference,
     * rather than the parameter names of the class for the instance referred to by
     * the reference.  For the polymorphic version of this method, use
     * {@link #getAttributeNames()}.
     *
     * @return a sorted list of the attribute names for this object class
     * paired with name of the hla class in which they are defined in a ClassAndPropertyName POJO.
     */
    public static List<ClassAndPropertyName> get_all_attribute_names() {
        return get_all_attribute_names(get_hla_class_name());
    }

    /**
     * Returns a sorted list containing the names of all of the attributes of an
     * object class instance.
     * The property names are paired with name of the hla class in which they are defined in a
     * ClassAndPropertyName POJO.
     * Polymorphic equivalent of get_all_attribute_names() static method.
     *
     * @return sorted list containing the names of all of the attributes of an
     * object class instance paired with name of the hla class in which they are defined in a
     * ClassAndPropertyName POJO.
     */
    @Override
    public List<ClassAndPropertyName> getAllAttributeNames() {
        return get_all_attribute_names();
    }

    /*
     * INITIALIZE STATIC PROPERTYS THAT DEAL WITH NAMES
     */
    static {
        _hlaClassNameSet.add(get_hla_class_name());

        ObjectRoot instance = new ObjectRoot();
        instance.classAndPropertyNameValueMap = null;

        _hlaClassNameInstanceMap.put(get_hla_class_name(), instance);

        Set<ClassAndPropertyName> classAndPropertyNameSet = new HashSet<>();

        // ADD THIS CLASS'S classAndPropertyNameSet TO _classNamePropertyNameSetMap DEFINED
        // IN ObjectRoot
        _classNamePropertyNameSetMap.put(get_hla_class_name(), classAndPropertyNameSet);


        Set<ClassAndPropertyName> allClassAndPropertyNameSet = new HashSet<>();


        // ADD THIS CLASS'S _allClassAndPropertyNameSet TO _allClassNamePropertyNameSetMap DEFINED
        // IN ObjectRoot
        _allClassNamePropertyNameSetMap.put(get_hla_class_name(), allClassAndPropertyNameSet);

        logger.info(
          "Class \"org.cpswt.hla.ObjectRoot\" (hla class \"{}\") loaded", get_hla_class_name()
        );

        System.err.println(
          "Class \"org.cpswt.hla.ObjectRoot\" (hla class \"" +
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
     * Returns the handle (RTI assigned) of the org.cpswt.hla.ObjectRoot object class.
     * Note: As this is a static method, it is NOT polymorphic, and so, if called on
     * a reference will return the handle of the class pertaining to the reference,
     * rather than the handle of the class for the instance referred to by the reference.
     * For the polymorphic version of this method, use {@link #getClassHandle()}.
     *
     * @return the RTI assigned integer handle that represents this object class
     */
    public static int get_class_handle() {
        return _classNameHandleMap.get(get_hla_class_name());
    }

    /**
     * Returns the handle (RTI assigned) of this instance's object class.
     * Polymorphic equivalent for get_class_handle static method.
     *
     * @return the handle (RTI assigned) if this instance's object class
     */
    public int getClassHandle() {
        return get_class_handle();
    }


    /**
     * Returns the handle of an attribute (RTI assigned) of
     * this object class (i.e. "org.cpswt.hla.ObjectRoot") given the attribute's name.
     *
     * @param propertyName name of attribute
     * @return the handle (RTI assigned) of the attribute "propertyName" of object class "className"
     */
    public static int get_attribute_handle(String propertyName) {
        return get_attribute_handle(get_hla_class_name(), propertyName);
    }

    /**
     * Returns the handle associated with the given attribute name for an object class instance
     * Polymorphic equivalent of get_attribute_handle static method.
     *
     * @param propertyName the name of a attribute that belongs to this object class
     * @return the RTI handle associated with the attribute name, or -1 if not found
     */
    public int getAttributeHandle(String propertyName) {
        return get_attribute_handle(propertyName);
    }
    /**
     * Returns a data structure containing the handles of all attributes for this object
     * class that are currently marked for subscription.  To actually subscribe to these
     * attributes, a federate must call &lt;objectclassname&gt;.subscribe( RTIambassador rti ).
     *
     * @return data structure containing the handles of all attributes for this object
     * class that are currently marked for subscription
     */
    private static AttributeHandleSet get_subscribed_attribute_handle_set() {
        return _classNamePublishedAttributeHandleSetMap.get( get_hla_class_name() );
    }

    // ----------------------------------------------------------
    // END OF STATIC PROPERTYS AND CODE THAT DEAL WITH HANDLES.
    // ----------------------------------------------------------


    //-------------------------------------------------
    // METHODS FOR PUBLISHING/SUBSCRIBING-TO THIS CLASS
    //-------------------------------------------------

    /**
     * Publishes the org.cpswt.hla.ObjectRoot object class for a federate.
     *
     * @param rti handle to the Local RTI Component
     */
    public static void publish_object(RTIambassador rti) {
        publish_object(get_hla_class_name(), rti);
    }

    /**
     * Publishes the object class of this instance of the class for a federate.
     * Polymorphic equalivalent of publish_object static method.
     *
     * @param rti handle to the Local RTI Component
     */
    @Override
    public void publishObject(RTIambassador rti) {
        publish_object(rti);
    }


    /**
     * Unpublishes the org.cpswt.hla.ObjectRoot object class for a federate.
     *
     * @param rti handle to the Local RTI Component, usu. obtained through the
     *            {@link SynchronizedFederate#getLRC()} call
     */
    public static void unpublish_object(RTIambassador rti) {
        unpublish_object(get_hla_class_name(), rti);
    }

    /**
     * Unpublishes the object class of this instance of this class for a federate.
     * Polymorphic equivalent of unpublish_object static method.
     *
     * @param rti handle to the Local RTI Component
     */
    @Override
    public void unpublishObject(RTIambassador rti) {
        unpublish_object(rti);
    }

    /**
     * Subscribes a federate to the org.cpswt.hla.ObjectRoot object class.
     *
     * @param rti handle to the Local RTI Component
     */
    public static void subscribe_object(RTIambassador rti) {
        subscribe_object(get_hla_class_name(), rti);
    }

    /**
     * Subscribes a federate to the object class of this instance of this class.
     * Polymorphic equivalent of subscribe_object static method.
     *
     * @param rti handle to the Local RTI Component
     */
    @Override
    public void subscribeObject(RTIambassador rti) {
        subscribe_object(rti);
    }

    /**
     * Unsubscribes a federate from the org.cpswt.hla.ObjectRoot object class.
     *
     * @param rti handle to the Local RTI Component
     */
    public static void unsubscribe_object(RTIambassador rti) {
        unsubscribe_object(get_hla_class_name(), rti);
    }

    /**
     * Unsubscribes a federate from the object class of this instance of this class.
     *
     * @param rti handle to the Local RTI Component
     */
    @Override
    public void unsubscribeObject(RTIambassador rti) {
        unsubscribe_object(rti);
    }

    protected static Set<ClassAndPropertyName> get_published_attribute_name_set() {
        return _classNamePublishedAttributeNameSetMap.get(get_hla_class_name());
    }

    protected Set<ClassAndPropertyName> getPublishedAttributeNameSet() {
        return get_published_attribute_name_set();
    }

    protected static Set<ClassAndPropertyName> get_subscribed_attribute_name_set() {
        return _classNameSubscribedAttributeNameSetMap.get(get_hla_class_name());
    }

    protected Set<ClassAndPropertyName> getSubscribedAttributeNameSet() {
        return get_subscribed_attribute_name_set();
    }


    //-----------------------------------------------------
    // END METHODS FOR PUBLISHING/SUBSCRIBING-TO THIS CLASS
    //-----------------------------------------------------

    /**
     * Return true if "handle" is equal to the handle (RTI assigned) of this class
     * (that is, the org.cpswt.hla.ObjectRoot object class).
     *
     * @param handle handle to compare to the value of the handle (RTI assigned) of
     * this class (the org.cpswt.hla.ObjectRoot object class).
     * @return "true" if "handle" matches the value of the handle of this class
     * (that is, the org.cpswt.hla.ObjectRoot object class).
     */
    public static boolean match(int handle) {
        return handle == get_class_handle();
    }

    //-------------
    // CONSTRUCTORS
    //-------------

    public ObjectRoot() {
        this(get_hla_class_name());
    }

    public ObjectRoot(LogicalTime logicalTime) {
        this();
        setTime(logicalTime);
    }

    public ObjectRoot(ReflectedAttributes propertyMap) {
        this();
        setAttributes( propertyMap );
    }

    public ObjectRoot(ReflectedAttributes propertyMap, LogicalTime logicalTime) {
        this(propertyMap);
        setTime(logicalTime);
    }

    //-----------------
    // END CONSTRUCTORS
    //-----------------


    //-----------------
    // CREATION METHODS
    //-----------------
    public static ObjectRoot create_object() {
        return new ObjectRoot();
    }

    public ObjectRoot createObject() {
        return create_object();
    }

    public static ObjectRoot create_object(LogicalTime logicalTime) {
        return new ObjectRoot(logicalTime);
    }

    public ObjectRoot createObject(LogicalTime logicalTime) {
        return create_object(logicalTime);
    }

    public static ObjectRoot create_object(ReflectedAttributes propertyMap) {
        return new ObjectRoot(propertyMap);
    }

    public ObjectRoot createObject(ReflectedAttributes propertyMap) {
        return create_object(propertyMap);
    }

    public static ObjectRoot create_object(ReflectedAttributes propertyMap, LogicalTime logicalTime) {
        return new ObjectRoot(propertyMap, logicalTime);
    }

    public ObjectRoot createObject(ReflectedAttributes propertyMap, LogicalTime logicalTime) {
        return create_object(propertyMap, logicalTime);
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
     * Returns the timestamp for this object.  "receive order" objects
     * should have a timestamp of -1.
     *
     * @return timestamp for this object
     */
    public double getTime() {
        return _time;
    }

    /**
     * Sets the timestamp of this object to "time".
     *
     * @param time new timestamp for this object
     */
    public void setTime( double time ) {
        _time = time;
    }

    /**
     * Sets the timestamp of this object to "logicalTime".
     *
     * @param logicalTime new timestamp for this object
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
    // - ObjectRoot( String hlaClassName ) DYNAMIC CONSTRUCTOR
    // - fromJson()
    // - readFederateDynamicMessageClasses(Reader reader) BELOW
    //------------------------------------------
    protected static Map<ClassAndPropertyName, Object> _classAndPropertyNameInitialValueMap = new HashMap<>();

    //-------------
    // CONSTRUCTORS
    //-------------

    /**
     * Creates a new ObjectRoot instance.
     */
    public ObjectRoot( String hlaClassName ) {
        _uniqueID = generateUniqueID();
        setInstanceHlaClassName(hlaClassName);
        if (!_hlaClassNameSet.contains(hlaClassName)) {
            logger.error("Constructor \"ObjectRoot( String hlaClassName )\": " +
              "hlaClassName \"{}\" is not defined -- creating dummy object with fictitious type \"{}\"",
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

    public ObjectRoot( String hlaClassName, LogicalTime logicalTime ) {
        this(hlaClassName);
        setTime( logicalTime );
    }

    public ObjectRoot( String hlaClassName, ReflectedAttributes propertyMap ) {
        this(hlaClassName);
        setAttributes( propertyMap );
    }

    public ObjectRoot( String hlaClassName, ReflectedAttributes propertyMap, LogicalTime logicalTime ) {
        this(hlaClassName, propertyMap);
        setTime( logicalTime );
    }

    /**
     * Creates a copy of an ObjectRoot instance.  As an
     * ObjectRoot instance contains no attributes,
     * this has the same effect as the default constructor.
     *
     * @param other the object instance to copy
     */
    public ObjectRoot( ObjectRoot other ) {
        this();
        _time = other._time;
        for(ClassAndPropertyName key: classAndPropertyNameValueMap.keySet()) {
            classAndPropertyNameValueMap.put(
                key, new Attribute<>((Attribute<Object>)classAndPropertyNameValueMap.get(key))
            );
        }
    }

    //-----------------
    // END CONSTRUCTORS
    //-----------------

    public void copyFrom( Object object ) {
        if ( object instanceof ObjectRoot ) {
            ObjectRoot messagingRoot = (ObjectRoot)object;
            for(ClassAndPropertyName key: messagingRoot.classAndPropertyNameValueMap.keySet()) {
                if (classAndPropertyNameValueMap.containsKey(key)) {
                    classAndPropertyNameValueMap.put(
                        key, new Attribute<>((Attribute<Object>)messagingRoot.classAndPropertyNameValueMap.get(key)));
                }
            }
        }
    }

    /**
     * Set the values of the attributes in this object using
     * "propertyMap".  "propertyMap" is usually acquired as an argument to
     * an RTI federate callback method such as "receiveInteraction".
     *
     * @param propertyMap  contains new values for the attributes of
     * this object
     */
    public void setAttributes( ReflectedAttributes propertyMap ) {
        int size = propertyMap.size();
        for( int ix = 0 ; ix < size ; ++ix ) {
            try {
                setAttribute(  propertyMap.getAttributeHandle( ix ), propertyMap.getValue( ix )  );
            } catch ( Exception e ) {
                logger.error( "setAttributes: Exception caught!" );
                logger.error("{}", CpswtUtils.getStackTrace(e));
            }
        }
    }

    private void setAttribute( int handle, byte[] value ) {
        if ( value == null ) {
            logger.error( "set:  Attempt to set null value  class \"{}\"", getClass().getName());
            return;
        }
        String valueAsString = new String( value, 0, value.length );
        if (valueAsString.length() > 0 && valueAsString.charAt(valueAsString.length() - 1) == '\0') {
            valueAsString = valueAsString.substring(0, valueAsString.length() - 1);
        }
        setAttribute(handle, valueAsString);
    }

    /**
     * Broadcasts the attributes of this object and their values to the RTI, where
     * the values have "time" as their timestamp.  This call should be used for
     * objects whose attributes have "timestamp" ordering.
     *
     * @param rti handle to the RTI, usu. obtained through the
     * {@link SynchronizedFederate#getRTI()} call
     * @param time timestamp on attribute values of this object
     * @param force if "false", only the attributes whose values have changed since
     * the last call to "updateAttributeValues" will be broadcast to the RTI.  If
     * "true", all attributes and their values are broadcast to the RTI.
     */
    public void updateAttributeValues( RTIambassador rti, double time, boolean force ) {

        SuppliedAttributes suppliedAttributes = createSuppliedAttributes( force );
        if ( suppliedAttributes.size() == 0 ) return;

        synchronized( rti ) {
            try {
                rti.updateAttributeValues(  getObjectHandle(), suppliedAttributes, null, new DoubleTime( time )  );
            } catch ( ObjectNotKnown o ) {
                logger.error( "{}:  could not update attributes:  Object Not Known", getClass().getName() );
                logger.error("{}", CpswtUtils.getStackTrace(o));
            } catch ( FederateNotExecutionMember f ) {
                logger.error( "{}:  could not update attributes:  Federate Not Execution Member", getClass().getName() );
                logger.error("{}", CpswtUtils.getStackTrace(f));
            } catch ( AttributeNotDefined a ) {
                logger.error( "{}:  could not update attributes:  Attribute Not Defined", getClass().getName() );
                logger.error("{}", CpswtUtils.getStackTrace(a));
            } catch ( AttributeNotOwned a ) {
                logger.error( "{}:  could not update attributes:  Attribute Not Owned", getClass().getName() );
                logger.error("{}", CpswtUtils.getStackTrace(a));
            } catch ( ConcurrentAccessAttempted c ) {
                logger.error( "{}:  could not update attributes:  Concurrent Access Attempted", getClass().getName() );
                logger.error("{}", CpswtUtils.getStackTrace(c));
            } catch ( InvalidFederationTime i ) {
                logger.error( "{}:  could not update attributes:  Invalid Federation Time", getClass().getName() );
                logger.error("{}", CpswtUtils.getStackTrace(i));
            } catch ( Exception e ) {
                logger.error( "{}:  could not update attributes", getClass().getName() );
                logger.error("{}", CpswtUtils.getStackTrace(e));
            }
        }
    }

    /**
     * Like {@link #updateAttributeValues( RTIambassador rti, double time, boolean force )},
     * except "force" is always false.
     *
     * @param rti handle to the RTI, usu. obtained through the
     * {@link SynchronizedFederate#getRTI()} call
     * @param time timestamp on attribute values of this object
     */
    public void updateAttributeValues( RTIambassador rti, double time ) {
        updateAttributeValues( rti, time, false );
    }

    /**
     * Broadcasts the attributes of this object and their values to the RTI (with
     * no timestamp).  This call should be used for objects whose attributes have
     * "receive" ordering.
     *
     * @param rti handle to the RTI, usu. obtained through the
     * {@link SynchronizedFederate#getRTI()} call
     * @param force if "false", only the attributes whose values have changed since
     * the last call to "updateAttributeValues" will be broadcast to the RTI.  If
     * "true", all attributes and their values are broadcast to the RTI.
     */
    public void updateAttributeValues( RTIambassador rti, boolean force ) {

        SuppliedAttributes suppliedAttributes = createSuppliedAttributes( force );
        if ( suppliedAttributes.size() == 0 ) return;

        synchronized( rti ) {
            try {
                rti.updateAttributeValues( getObjectHandle(), suppliedAttributes, null );
            } catch ( ObjectNotKnown o ) {
                logger.error( "{}:  could not update attributes:  Object Not Known", getClass().getName() );
                logger.error("{}", CpswtUtils.getStackTrace(o));
            } catch ( FederateNotExecutionMember f ) {
                logger.error( "{}:  could not update attributes:  Federate Not Execution Member", getClass().getName() );
                logger.error("{}", CpswtUtils.getStackTrace(f));
            } catch ( AttributeNotDefined a ) {
                logger.error( "{}:  could not update attributes:  Attribute Not Defined", getClass().getName() );
                logger.error("{}", CpswtUtils.getStackTrace(a));
            } catch ( AttributeNotOwned a ) {
                logger.error( "{}:  could not update attributes:  Attribute Not Owned", getClass().getName() );
                logger.error("{}", CpswtUtils.getStackTrace(a));
            } catch ( ConcurrentAccessAttempted c ) {
                logger.error( "{}:  could not update attributes:  Concurrent Access Attempted", getClass().getName() );
                logger.error("{}", CpswtUtils.getStackTrace(c));
            } catch ( Exception e ) {
                logger.error("{}:  could not update attributes", getClass().getName());
                logger.error("{}", CpswtUtils.getStackTrace(e));
            }
        }
    }

    /**
     * Like {@link #updateAttributeValues( RTIambassador rti, boolean force )},
     * except "force" is always false.
     *
     * @param rti handle to the RTI, usu. obtained through the
     * {@link SynchronizedFederate#getRTI()} call
     */
    public void updateAttributeValues( RTIambassador rti ) {
        updateAttributeValues( rti, false );
    }

    protected static String _fedName = null;
    protected static Map<String, String> _pubAttributeLogMap = new HashMap<>();
    protected static Map<String, String> _subAttributeLogMap = new HashMap<>();

    /**
     * For use with the melding API -- this method is used to cast
     * ObjectRoot instance reference into the
     * ObjectRootInterface interface.
     *
     * @param rootInstance ObjectRoot instance reference to be
     * cast into the ObjectRootInterface interface
     * @return ObjectRootInterface reference to the instance
     */
    public ObjectRootInterface cast( ObjectRoot rootInstance ) {
        return rootInstance;
    }

    /**
     * For use with the melding API -- this method creates a new
     * ObjectRoot instance and returns a
     * ObjectRootInterface reference to it.get
     *
     * @return ObjectRootInterface reference to a newly created
     * ObjectRoot instance
     */
    public ObjectRootInterface create() {
        return new ObjectRoot();
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
        topLevelJSONObject.put("messaging_type", "object");
        topLevelJSONObject.put("object_handle", getObjectHandle());

        JSONObject propertyJSONObject = new JSONObject();
        topLevelJSONObject.put("properties", propertyJSONObject);
        for(ClassAndPropertyName key : getPublishedAttributeNameSet()) {
            Attribute<Object> value = (Attribute<Object>)classAndPropertyNameValueMap.get(key);
            propertyJSONObject.put(key.toString(), value.getValue());
        }
        return topLevelJSONObject.toString(4);
    }

    public static void fromJson(String jsonString) {
        JSONObject jsonObject = new JSONObject(jsonString);
        int objectHandle = jsonObject.getInt("object_handle");
        ObjectRoot objectRoot = _objectHandleInstanceMap.getOrDefault(objectHandle, null);
        if (objectRoot == null) {
            logger.error(
                    "ObjectRoot:  fromJson:  no registered object exists with received object-handle ({})",
                    objectHandle
            );
            return;
        }

        Set<ClassAndPropertyName> subscribedAttributeNameSet = objectRoot.getSubscribedAttributeNameSet();

        JSONObject propertyJSONObject = jsonObject.getJSONObject("properties");
        for (String key : propertyJSONObject.keySet()) {
            ClassAndPropertyName classAndPropertyName = new ClassAndPropertyName(key);
            if (subscribedAttributeNameSet.contains(classAndPropertyName)) {

                Class<?> desiredType = _classAndPropertyNameInitialValueMap.get(classAndPropertyName).getClass();
                Object object = castNumber(propertyJSONObject.get(key), desiredType);
                ((Attribute<Object>)objectRoot.classAndPropertyNameValueMap.get(classAndPropertyName)).setValue(object);
            }
        }
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
        JSONObject federationMessaging = federationJson.getJSONObject("objects");

        Set<String> localHlaClassNameSet = new HashSet<>();

        JSONArray dynamicHlaClassNames = dynamicMessageTypes.getJSONArray("objects");
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
                    String propertyTypeString = typeDataMap.getString("AttributeType");
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
        for(ClassAndPropertyName classAndPropertyName: getAllAttributeNames()) {
            if (first) first = false;
            else stringBuilder.append(",");
            stringBuilder.append(classAndPropertyName).append("=").
              append(classAndPropertyNameValueMap.get(classAndPropertyName));
        }
        return getInstanceHlaClassName() + "(" + stringBuilder + ")";
    }

}