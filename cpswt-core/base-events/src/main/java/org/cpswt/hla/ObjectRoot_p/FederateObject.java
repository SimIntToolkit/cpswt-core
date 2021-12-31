
package org.cpswt.hla.ObjectRoot_p;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cpswt.utils.CpswtUtils;

import hla.rti.AttributeHandleSet;
import hla.rti.FederateNotExecutionMember;
import hla.rti.LogicalTime;
import hla.rti.NameNotFound;
import hla.rti.ObjectClassNotDefined;
import hla.rti.ObjectClassNotPublished;
import hla.rti.ObjectClassNotSubscribed;
import hla.rti.RTIambassador;
import hla.rti.ReflectedAttributes;
import hla.rti.SuppliedAttributes;


/**
 * Implements org.cpswt.hla.ObjectRoot_p.FederateObject
 */
@SuppressWarnings("unused")
public class FederateObject extends org.cpswt.hla.ObjectRoot {

    private static final Logger logger = LogManager.getLogger();

    /**
    * Creates an instance of the Object class with default attribute values.
    */
    public FederateObject() {}

    // ----------------------------------------------------------------------------
    // STATIC DATAMEMBERS AND CODE THAT DEAL WITH NAMES
    // THIS CODE IS STATIC BECAUSE IT IS CLASS-DEPENDENT AND NOT INSTANCE-DEPENDENT
    // ----------------------------------------------------------------------------

    /**
     * Returns the fully-qualified (dot-delimited) name of the org.cpswt.hla.ObjectRoot_p.FederateObject object class.
     * Note: As this is a static method, it is NOT polymorphic, and so, if called on
     * a reference will return the name of the class pertaining to the reference,
     * rather than the name of the class for the instance referred to by the reference.
     * For the polymorphic version of this method, use {@link #getJavaClassName()}.
     *
     * @return the fully-qualified Java class name for this object class
     */
    public static String get_java_class_name() {
        return "org.cpswt.hla.ObjectRoot_p.FederateObject";
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
     * class name) of the org.cpswt.hla.ObjectRoot_p.FederateObject object class.
     *
     * @return the name of this object class
     */
    public static String get_simple_class_name() {
        return "FederateObject";
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
     * ObjectRoot.FederateObject object class.
     * Note: As this is a static method, it is NOT polymorphic, and so, if called on
     * a reference will return the federation name of the class pertaining to the reference,
     * rather than the name of the class for the instance referred to by the reference.
     * For the polymorphic version of this method, use {@link #getHlaClassName()}.
     *
     * @return the fully-qualified federation (HLA) class name for this object class
     */
    public static String get_hla_class_name() {
        return "ObjectRoot.FederateObject";
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

    private static final Set<ClassAndPropertyName> _classAndPropertyNameSet = new HashSet<>();

    /**
     * Returns a sorted list containing the names of all of the non-hidden attributes in the
     * org.cpswt.hla.ObjectRoot_p.FederateObject object class.
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
        List<ClassAndPropertyName> classAndPropertyNameList = new ArrayList<>(_classAndPropertyNameSet);
        Collections.sort(classAndPropertyNameList);
        return classAndPropertyNameList;
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

    private static final Set<ClassAndPropertyName> _allClassAndPropertyNameSet = new HashSet<>();

    /**
     * Returns a sorted list containing the names of all of the attributes in the
     * org.cpswt.hla.ObjectRoot_p.FederateObject object class.
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
        List<ClassAndPropertyName> allClassAndPropertyNameList = new ArrayList<>(_allClassAndPropertyNameSet);
        Collections.sort(allClassAndPropertyNameList);
        return allClassAndPropertyNameList;
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

    private static final Set<ClassAndPropertyName> _publishedAttributeNameSet = new HashSet<>();
    private static final Set<ClassAndPropertyName> _subscribedAttributeNameSet = new HashSet<>();

    protected static Set<ClassAndPropertyName> get_published_attribute_name_set() {
        return _publishedAttributeNameSet;
    }

    protected Set<ClassAndPropertyName> getPublishedAttributeNameSet() {
        return get_published_attribute_name_set();
    }

    protected static Set<ClassAndPropertyName> get_subscribed_attribute_name_set() {
        return _subscribedAttributeNameSet;
    }

    protected Set<ClassAndPropertyName> getSubscribedAttributeNameSet() {
        return get_subscribed_attribute_name_set();
    }


    /*
     * INITIALIZE STATIC DATAMEMBERS THAT DEAL WITH NAMES
     */
    static {
        // ADD THIS CLASS TO THE _classNameSet DEFINED IN ObjectRoot
        _classNameSet.add(get_hla_class_name());

        // ADD CLASS OBJECT OF THIS CLASS TO _classNameClassMap DEFINED IN ObjectRoot
        _classNameClassMap.put(get_hla_class_name(), FederateObject.class);

        // ADD THIS CLASS'S _classAndPropertyNameSet TO _classNamePropertyNameSetMap DEFINED
        // IN ObjectRoot
        _classNamePropertyNameSetMap.put(get_hla_class_name(), _classAndPropertyNameSet);

        // ADD THIS CLASS'S _allClassAndPropertyNameSet TO _classNameAllPropertyNameSetMap DEFINED
        // IN ObjectRoot
        _classNameAllPropertyNameSetMap.put(get_hla_class_name(), _allClassAndPropertyNameSet);
        _classAndPropertyNameSet.add(new ClassAndPropertyName(
            "ObjectRoot.FederateObject", "FederateHandle"
        ));
        _classAndPropertyNameSet.add(new ClassAndPropertyName(
            "ObjectRoot.FederateObject", "FederateHost"
        ));
        _classAndPropertyNameSet.add(new ClassAndPropertyName(
            "ObjectRoot.FederateObject", "FederateType"
        ));

        ClassAndPropertyName key;

        key = new ClassAndPropertyName(get_hla_class_name(), "FederateHandle");
        _classAndPropertyNameTypeMap.put(key, Integer.class);

        key = new ClassAndPropertyName(get_hla_class_name(), "FederateHost");
        _classAndPropertyNameTypeMap.put(key, String.class);

        key = new ClassAndPropertyName(get_hla_class_name(), "FederateType");
        _classAndPropertyNameTypeMap.put(key, String.class);

        _allClassAndPropertyNameSet.add(new ClassAndPropertyName(
            "ObjectRoot.FederateObject", "FederateHandle"
        ));

        _allClassAndPropertyNameSet.add(new ClassAndPropertyName(
            "ObjectRoot.FederateObject", "FederateHost"
        ));

        _allClassAndPropertyNameSet.add(new ClassAndPropertyName(
            "ObjectRoot.FederateObject", "FederateType"
        ));

        _classNamePublishedAttributeNameSetMap.put(get_hla_class_name(), _publishedAttributeNameSet);
        _classNameSubscribedAttributeNameSetMap.put(get_hla_class_name(), _subscribedAttributeNameSet);
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
     * Returns the handle (RTI assigned) of the org.cpswt.hla.ObjectRoot_p.FederateObject object class.
     * Note: As this is a static method, it is NOT polymorphic, and so, if called on
     * a reference will return the handle of the class pertaining to the reference,
     * rather than the handle of the class for the instance referred to by the reference.
     * For the polymorphic version of this method, use {@link #getClassHandle()}.
     *
     * @return the RTI assigned integer handle that represents this object class
     */
    public static int get_class_handle() {
        return _handle;
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
     * THIS METHOD IS INDIRECTLY CALLED VIA THE "get_attribute_handle(String)" METHOD BELOW, WHICH PROVIDES THE
     * VALUE FOR THE "className" ARGUMENT.
     */
    protected static int get_attribute_handle_aux(String className, String propertyName) {
        ClassAndPropertyName key = new ClassAndPropertyName(get_hla_class_name(), propertyName);
        if (_classAndPropertyNameHandleMap.containsKey(key)) {
            return _classAndPropertyNameHandleMap.get(key);
        }
        return org.cpswt.hla.ObjectRoot.get_attribute_handle_aux(className, propertyName);    
    }

    /**
     * Returns the handle of an attribute (RTI assigned) of
     * this object class (i.e. "org.cpswt.hla.ObjectRoot_p.FederateObject") given the attribute's name.
     *
     * @param propertyName name of attribute
     * @return the handle (RTI assigned) of the attribute "propertyName" of object class "className"
     */
    public static int get_attribute_handle(String propertyName) {
        return get_attribute_handle_aux(get_hla_class_name(), propertyName);
    }

    /**
     * Returns the handle associated with the given attribute name for an object class instance
     * Polymorphic equivalent of get_attribute_handle static method.
     *
     * @param propertyName the name of a attribute that belongs to this object class
     * @return the RTI handle associated with the attribute name, or -1 if not found
     */
    @Override
    public int getAttributeHandle(String propertyName) {
        return get_attribute_handle(propertyName);
    }
    private static final AttributeHandleSet _publishedAttributeHandleSet;
    private static final AttributeHandleSet _subscribedAttributeHandleSet;

    static {
        _publishedAttributeHandleSet = _factory.createAttributeHandleSet();
        _classNamePublishedAttributeHandleSetMap.put(get_hla_class_name(), _publishedAttributeHandleSet);

        _subscribedAttributeHandleSet = _factory.createAttributeHandleSet();
        _classNameSubscribedAttributeHandleSetMap.put(get_hla_class_name(), _subscribedAttributeHandleSet);
    }

    private static boolean _isInitialized = false;

    /*
     * THIS FUNCTION INITIALIZES ALL OF THE HANDLES ASSOCIATED WITH THIS OBJECT CLASS
     * IT NEEDS THE RTI TO DO SO.
     */
    protected static void init(RTIambassador rti) {
        if (_isInitialized) return;
        _isInitialized = true;
        org.cpswt.hla.ObjectRoot.init(rti);

        boolean isNotInitialized = true;
        while(isNotInitialized) {
            try {
                _handle = rti.getObjectClassHandle(get_hla_class_name());
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

        ClassAndPropertyName classAndPropertyName;

        isNotInitialized = true;
        int propertyHandle;
        while(isNotInitialized) {
            try {

                propertyHandle = rti.getAttributeHandle("FederateHandle", get_class_handle());
                classAndPropertyName = new ClassAndPropertyName(get_hla_class_name(), "FederateHandle");
                _classAndPropertyNameHandleMap.put(classAndPropertyName, propertyHandle);
                _handleClassAndPropertyNameMap.put(propertyHandle, classAndPropertyName);

                propertyHandle = rti.getAttributeHandle("FederateHost", get_class_handle());
                classAndPropertyName = new ClassAndPropertyName(get_hla_class_name(), "FederateHost");
                _classAndPropertyNameHandleMap.put(classAndPropertyName, propertyHandle);
                _handleClassAndPropertyNameMap.put(propertyHandle, classAndPropertyName);

                propertyHandle = rti.getAttributeHandle("FederateType", get_class_handle());
                classAndPropertyName = new ClassAndPropertyName(get_hla_class_name(), "FederateType");
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

    // ----------------------------------------------------------
    // END OF STATIC DATAMEMBERS AND CODE THAT DEAL WITH HANDLES.
    // ----------------------------------------------------------


    //-------------------------------------------------
    // METHODS FOR PUBLISHING/SUBSCRIBING-TO THIS CLASS
    //-------------------------------------------------

    private static boolean _isPublished = false;

    /**
     * Publishes the org.cpswt.hla.ObjectRoot_p.FederateObject object class for a federate.
     *
     * @param rti handle to the Local RTI Component
     */
    public static void publish_object(RTIambassador rti) {
        if (_isPublished) return;
        _isPublished = true;

        init(rti);

        _publishedAttributeHandleSet.empty();
        for(ClassAndPropertyName key : _publishedAttributeNameSet) {
            try {
                _publishedAttributeHandleSet.add(_classAndPropertyNameHandleMap.get(key));
                logger.trace("publish {}:{}", get_hla_class_name(), key.toString());
            } catch (Exception e) {
                logger.error("could not publish \"" + key.toString() + "\" attribute.", e);
            }
        }

        synchronized(rti) {
            boolean isNotPublished = true;
            while(isNotPublished) {
                try {
                    rti.publishObjectClass(get_class_handle(), _publishedAttributeHandleSet);
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

        logger.debug("publish: {}", get_hla_class_name());
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
     * Unpublishes the org.cpswt.hla.ObjectRoot_p.FederateObject object class for a federate.
     *
     * @param rti handle to the Local RTI Component, usu. obtained through the
     *            {@link SynchronizedFederate#getLRC()} call
     */
    public static void unpublish_object(RTIambassador rti) {
        if (!_isPublished) return;
        _isPublished = false;

        init(rti);

        synchronized(rti) {
            boolean isNotUnpublished = true;
            while(isNotUnpublished) {
                try {
                    rti.unpublishObjectClass(get_class_handle());
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

        logger.debug("unpublish: {}", get_hla_class_name());
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

    private static boolean _isSubscribed = false;

    /**
     * Subscribes a federate to the org.cpswt.hla.ObjectRoot_p.FederateObject object class.
     *
     * @param rti handle to the Local RTI Component
     */
    public static void subscribe_object(RTIambassador rti) {
        if (_isSubscribed) return;
        _isSubscribed= true;

        init(rti);

        _subscribedAttributeHandleSet.empty();
        for(ClassAndPropertyName key : _subscribedAttributeNameSet) {
            try {
                _subscribedAttributeHandleSet.add(_classAndPropertyNameHandleMap.get(key));
                logger.trace("subscribe {}:{}", get_hla_class_name(), key.toString());
            } catch (Exception e) {
                logger.error("could not subscribe to \"" + key + "\" attribute.", e);
            }
        }

        synchronized(rti) {
            boolean isNotSubscribed = true;
            while(isNotSubscribed) {
                try {
                    rti.subscribeObjectClassAttributes(get_class_handle(), _subscribedAttributeHandleSet);
                    isNotSubscribed = false;
                } catch (FederateNotExecutionMember e) {
                    logger.error("could not subscribe: Federate Not Execution Member", e);
                    return;
                } catch (ObjectClassNotDefined e) {
                    logger.error("could not subscribe: Object Class Not Defined", e);
                    return;
                } catch (Exception e) {
                    logger.error(e);
                    CpswtUtils.sleepDefault();
                }
            }
        }

        logger.debug("subscribe: {}", get_hla_class_name());
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
     * Unsubscribes a federate from the org.cpswt.hla.ObjectRoot_p.FederateObject object class.
     *
     * @param rti handle to the Local RTI Component
     */
    public static void unsubscribe_object(RTIambassador rti) {
        if (!_isSubscribed) return;
        _isSubscribed = false;

        init(rti);

        synchronized(rti) {
            boolean isNotUnsubscribed = true;
            while(isNotUnsubscribed) {
                try {
                    rti.unsubscribeObjectClass(get_class_handle());
                    isNotUnsubscribed = false;
                } catch (FederateNotExecutionMember e) {
                    logger.error("could not unsubscribe: Federate Not Execution Member", e);
                    return;
                } catch (ObjectClassNotDefined e) {
                    logger.error("could not unsubscribe: Object Class Not Defined", e);
                    return;
                } catch (ObjectClassNotSubscribed e) {
                    logger.error("could not unsubscribe: Object Class Not Subscribed", e);
                    return;
                } catch (Exception e) {
                    logger.error(e);
                    CpswtUtils.sleepDefault();
                }
            }
        }

        logger.debug("unsubscribe: {}", get_hla_class_name());
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

    //-----------------------------------------------------
    // END METHODS FOR PUBLISHING/SUBSCRIBING-TO THIS CLASS
    //-----------------------------------------------------

    /**
     * Return true if "handle" is equal to the handle (RTI assigned) of this class
     * (that is, the org.cpswt.hla.ObjectRoot_p.FederateObject object class).
     *
     * @param handle handle to compare to the value of the handle (RTI assigned) of
     * this class (the org.cpswt.hla.ObjectRoot_p.FederateObject object class).
     * @return "true" if "handle" matches the value of the handle of this class
     * (that is, the org.cpswt.hla.ObjectRoot_p.FederateObject object class).
     */
    public static boolean match(int handle) {
        return handle == get_class_handle();
    }

    /**
     * Returns a data structure containing the handles of all attributes for this object
     * class that are currently marked for subscription.  To actually subscribe to these
     * attributes, a federate must call &lt;objectclassname&gt;.subscribe( RTIambassador rti ).
     *
     * @return data structure containing the handles of all attributes for this object
     * class that are currently marked for subscription
     */
    public AttributeHandleSet getSubscribedAttributeHandleSet() {
        return _subscribedAttributeHandleSet;
    }

    //--------------------------------
    // DATAMEMBER MANIPULATION METHODS
    //--------------------------------
    {
        ClassAndPropertyName key;

        key = new ClassAndPropertyName(get_hla_class_name(), "FederateHandle");
        classAndPropertyNameValueMap.put(key, new Attribute<>(0));

        key = new ClassAndPropertyName(get_hla_class_name(), "FederateHost");
        classAndPropertyNameValueMap.put(key, new Attribute<>(""));

        key = new ClassAndPropertyName(get_hla_class_name(), "FederateType");
        classAndPropertyNameValueMap.put(key, new Attribute<>(""));
    }


    /**
     * Set the value of the "FederateHandle" parameter to "value" for this parameter.
     *
     * @param value the new value for the "FederateHandle" parameter
     */
    public void set_FederateHandle(Integer value) {
        ClassAndPropertyName key = new ClassAndPropertyName(get_hla_class_name(), "FederateHandle");
        Attribute<Object> attribute = (Attribute<Object>)classAndPropertyNameValueMap.get(key);
        attribute.setValue(value);
        attribute.setTime(getTime());
    }

    /**
     * Returns the value of the "FederateHandle" parameter of this interaction.
     *
     * @return the value of the "FederateHandle" parameter
     */
    public Integer get_FederateHandle() {
        ClassAndPropertyName key = new ClassAndPropertyName(get_hla_class_name(), "FederateHandle");
        return (Integer)((Attribute<Object>)classAndPropertyNameValueMap.get(key)).getValue();
    }

    /**
     * Returns the current timestamp of the "FederateHandle" attribute of this object.
     *
     * @return the current timestamp of the "FederateHandle" attribute
     */
    public double get_FederateHandle_time() {
        return ((Attribute<Object>)classAndPropertyNameValueMap.get(
          new ClassAndPropertyName(get_hla_class_name(), "FederateHandle")
        )).getTime();
    }


    /**
     * Set the value of the "FederateHost" parameter to "value" for this parameter.
     *
     * @param value the new value for the "FederateHost" parameter
     */
    public void set_FederateHost(String value) {
        ClassAndPropertyName key = new ClassAndPropertyName(get_hla_class_name(), "FederateHost");
        Attribute<Object> attribute = (Attribute<Object>)classAndPropertyNameValueMap.get(key);
        attribute.setValue(value);
        attribute.setTime(getTime());
    }

    /**
     * Returns the value of the "FederateHost" parameter of this interaction.
     *
     * @return the value of the "FederateHost" parameter
     */
    public String get_FederateHost() {
        ClassAndPropertyName key = new ClassAndPropertyName(get_hla_class_name(), "FederateHost");
        return (String)((Attribute<Object>)classAndPropertyNameValueMap.get(key)).getValue();
    }

    /**
     * Returns the current timestamp of the "FederateHost" attribute of this object.
     *
     * @return the current timestamp of the "FederateHost" attribute
     */
    public double get_FederateHost_time() {
        return ((Attribute<Object>)classAndPropertyNameValueMap.get(
          new ClassAndPropertyName(get_hla_class_name(), "FederateHost")
        )).getTime();
    }


    /**
     * Set the value of the "FederateType" parameter to "value" for this parameter.
     *
     * @param value the new value for the "FederateType" parameter
     */
    public void set_FederateType(String value) {
        ClassAndPropertyName key = new ClassAndPropertyName(get_hla_class_name(), "FederateType");
        Attribute<Object> attribute = (Attribute<Object>)classAndPropertyNameValueMap.get(key);
        attribute.setValue(value);
        attribute.setTime(getTime());
    }

    /**
     * Returns the value of the "FederateType" parameter of this interaction.
     *
     * @return the value of the "FederateType" parameter
     */
    public String get_FederateType() {
        ClassAndPropertyName key = new ClassAndPropertyName(get_hla_class_name(), "FederateType");
        return (String)((Attribute<Object>)classAndPropertyNameValueMap.get(key)).getValue();
    }

    /**
     * Returns the current timestamp of the "FederateType" attribute of this object.
     *
     * @return the current timestamp of the "FederateType" attribute
     */
    public double get_FederateType_time() {
        return ((Attribute<Object>)classAndPropertyNameValueMap.get(
          new ClassAndPropertyName(get_hla_class_name(), "FederateType")
        )).getTime();
    }

    @Override
    protected PropertyClassNameAndValue getAttributeAux(String className, String propertyName) {
        ClassAndPropertyName key = new ClassAndPropertyName(get_hla_class_name(), "");
        if (classAndPropertyNameValueMap.containsKey(key)) {
            Object value = classAndPropertyNameValueMap.get(key);
            return new PropertyClassNameAndValue(get_hla_class_name(), value);
        }

        return super.getAttributeAux(className, propertyName);
    }

    //------------------------------------
    // END DATAMEMBER MANIPULATION METHODS
    //------------------------------------

    /**
    * Publishes the "FederateHandle" attribute of the attribute's containing object
    * class for a federate.
    * Note:  This method only marks the "FederateHandle" attribute for publication.
    * To actually publish the attribute, the federate must (re)publish its containing
    * object class.
    * (using <objectClassName>.publish_object( RTIambassador rti ) ).
    */
    public static void publish_FederateHandle_attribute() {
        _publishedAttributeNameSet.add(new ClassAndPropertyName(get_hla_class_name(), "FederateHandle"));
    }

    /**
    * Unpublishes the "FederateHandle" attribute of the attribute's containing object
    * class for a federate.
    * Note:  This method only marks the "FederateHandle" attribute for unpublication.
    * To actually publish the attribute, the federate must (re)publish its containing
    * object class.
    * (using <objectClassName>.publish_object( RTIambassador rti ) ).
    */
    public static void unpublish_FederateHandle_attribute() {
        _publishedAttributeNameSet.remove(new ClassAndPropertyName(get_hla_class_name(), "FederateHandle"));
    }

    /**
    * Subscribes a federate to the "FederateHandle" attribute of the attribute's
    * containing object class.
    * Note:  This method only marks the "FederateHandle" attribute for subscription.
    * To actually subscribe to the attribute, the federate must (re)subscribe to its
    * containing object class.
    * (using <objectClassName>.subscribe_object( RTIambassador rti ) ).
    */
    public static void subscribe_FederateHandle_attribute() {
        _subscribedAttributeNameSet.add(new ClassAndPropertyName(get_hla_class_name(), "FederateHandle"));
    }

    /**
    * Unsubscribes a federate from the "FederateHandle" attribute of the attribute's
    * containing object class.
    * Note:  This method only marks the "FederateHandle" attribute for unsubscription.
    * To actually unsubscribe to the attribute, the federate must (re)subscribe to its
    * containing object class.
    * (using <objectClassName>.subscribe_object( RTIambassador rti ) ).
    */
    public static void unsubscribe_FederateHandle_attribute() {
        _subscribedAttributeNameSet.remove(new ClassAndPropertyName(get_hla_class_name(), "FederateHandle"));
    }

    /**
    * Publishes the "FederateHost" attribute of the attribute's containing object
    * class for a federate.
    * Note:  This method only marks the "FederateHost" attribute for publication.
    * To actually publish the attribute, the federate must (re)publish its containing
    * object class.
    * (using <objectClassName>.publish_object( RTIambassador rti ) ).
    */
    public static void publish_FederateHost_attribute() {
        _publishedAttributeNameSet.add(new ClassAndPropertyName(get_hla_class_name(), "FederateHost"));
    }

    /**
    * Unpublishes the "FederateHost" attribute of the attribute's containing object
    * class for a federate.
    * Note:  This method only marks the "FederateHost" attribute for unpublication.
    * To actually publish the attribute, the federate must (re)publish its containing
    * object class.
    * (using <objectClassName>.publish_object( RTIambassador rti ) ).
    */
    public static void unpublish_FederateHost_attribute() {
        _publishedAttributeNameSet.remove(new ClassAndPropertyName(get_hla_class_name(), "FederateHost"));
    }

    /**
    * Subscribes a federate to the "FederateHost" attribute of the attribute's
    * containing object class.
    * Note:  This method only marks the "FederateHost" attribute for subscription.
    * To actually subscribe to the attribute, the federate must (re)subscribe to its
    * containing object class.
    * (using <objectClassName>.subscribe_object( RTIambassador rti ) ).
    */
    public static void subscribe_FederateHost_attribute() {
        _subscribedAttributeNameSet.add(new ClassAndPropertyName(get_hla_class_name(), "FederateHost"));
    }

    /**
    * Unsubscribes a federate from the "FederateHost" attribute of the attribute's
    * containing object class.
    * Note:  This method only marks the "FederateHost" attribute for unsubscription.
    * To actually unsubscribe to the attribute, the federate must (re)subscribe to its
    * containing object class.
    * (using <objectClassName>.subscribe_object( RTIambassador rti ) ).
    */
    public static void unsubscribe_FederateHost_attribute() {
        _subscribedAttributeNameSet.remove(new ClassAndPropertyName(get_hla_class_name(), "FederateHost"));
    }

    /**
    * Publishes the "FederateType" attribute of the attribute's containing object
    * class for a federate.
    * Note:  This method only marks the "FederateType" attribute for publication.
    * To actually publish the attribute, the federate must (re)publish its containing
    * object class.
    * (using <objectClassName>.publish_object( RTIambassador rti ) ).
    */
    public static void publish_FederateType_attribute() {
        _publishedAttributeNameSet.add(new ClassAndPropertyName(get_hla_class_name(), "FederateType"));
    }

    /**
    * Unpublishes the "FederateType" attribute of the attribute's containing object
    * class for a federate.
    * Note:  This method only marks the "FederateType" attribute for unpublication.
    * To actually publish the attribute, the federate must (re)publish its containing
    * object class.
    * (using <objectClassName>.publish_object( RTIambassador rti ) ).
    */
    public static void unpublish_FederateType_attribute() {
        _publishedAttributeNameSet.remove(new ClassAndPropertyName(get_hla_class_name(), "FederateType"));
    }

    /**
    * Subscribes a federate to the "FederateType" attribute of the attribute's
    * containing object class.
    * Note:  This method only marks the "FederateType" attribute for subscription.
    * To actually subscribe to the attribute, the federate must (re)subscribe to its
    * containing object class.
    * (using <objectClassName>.subscribe_object( RTIambassador rti ) ).
    */
    public static void subscribe_FederateType_attribute() {
        _subscribedAttributeNameSet.add(new ClassAndPropertyName(get_hla_class_name(), "FederateType"));
    }

    /**
    * Unsubscribes a federate from the "FederateType" attribute of the attribute's
    * containing object class.
    * Note:  This method only marks the "FederateType" attribute for unsubscription.
    * To actually unsubscribe to the attribute, the federate must (re)subscribe to its
    * containing object class.
    * (using <objectClassName>.subscribe_object( RTIambassador rti ) ).
    */
    public static void unsubscribe_FederateType_attribute() {
        _subscribedAttributeNameSet.remove(new ClassAndPropertyName(get_hla_class_name(), "FederateType"));
    }

    protected FederateObject( ReflectedAttributes datamemberMap, boolean initFlag ) {
        super( datamemberMap, false );
        if ( initFlag ) setAttributes( datamemberMap );
    }

    protected FederateObject( ReflectedAttributes datamemberMap, LogicalTime logicalTime, boolean initFlag ) {
        super( datamemberMap, logicalTime, false );
        if ( initFlag ) setAttributes( datamemberMap );
    }

    /**
    * Creates an instance of the FederateObject object class, using
    * "datamemberMap" to initialize its attribute values.
    * "datamemberMap" is usually acquired as an argument to an RTI federate
    * callback method, such as "receiveInteraction".
    *
    * @param datamemberMap data structure containing initial values for the
    * attributes of this new FederateObject object class instance
    */
    public FederateObject( ReflectedAttributes datamemberMap ) {
        this( datamemberMap, true );
    }

    /**
    * Like {@link #FederateObject( ReflectedAttributes datamemberMap )}, except this
    * new FederateObject attribute class instance is given a timestamp of
    * "logicalTime".
    *
    * @param datamemberMap data structure containing initial values for the
    * attributes of this new FederateObject object class instance
    * @param logicalTime timestamp for this new FederateObject object class instance
    */
    public FederateObject( ReflectedAttributes datamemberMap, LogicalTime logicalTime ) {
        this( datamemberMap, logicalTime, true );
    }

    /**
    * Creates a new FederateObject object class instance that is a duplicate
    * of the instance referred to by messaging_var.
    *
    * @param messaging_var FederateObject object class instance of which
    * this newly created FederateObject object class instance will be a
    * duplicate
    */
    public FederateObject(FederateObject messaging_var) {
    
        // SHALLOW COPY
        classAndPropertyNameValueMap = new HashMap<>(messaging_var.classAndPropertyNameValueMap);

        // DEEP(ER) COPY FOR OBJECTS
        for(ClassAndPropertyName key: classAndPropertyNameValueMap.keySet()) {
            classAndPropertyNameValueMap.put(key, new Attribute<>(classAndPropertyNameValueMap.get(key)));
        }

    }
}