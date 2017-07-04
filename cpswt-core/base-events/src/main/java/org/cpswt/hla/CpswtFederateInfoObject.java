package org.cpswt.hla;

import java.util.HashSet;
import java.util.Set;

import hla.rti.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * CpswtFederateInfoObject represents the information that should be shared when a federate joins a federation.
 */
public class CpswtFederateInfoObject extends ObjectRoot {

    public CpswtFederateInfoObject() {
    }

    private static final String OBJECT_CLASS_NAME = "ObjectRoot.CpswtFederateInfoObject";
    private static final Logger LOG = LogManager.getLogger(CpswtFederateInfoObject.class);

    private static int _FederateId_handle;
    private static int _FederateType_handle;
    private static int _IsLateJoiner_handle;

    public static int get_FederateId_handle() {
        return _FederateId_handle;
    }

    public static int get_FederateType_handle() {
        return _FederateType_handle;
    }

    public static int get_IsLateJoiner_handle() {
        return _IsLateJoiner_handle;
    }

    private static boolean _isInitialized = false;
    private static int _handle;

    public static int get_handle() {
        return _handle;
    }

    public static String get_class_name() {
        return OBJECT_CLASS_NAME;
    }

    public static String get_simple_class_name() {
        return "CpswtFederateInfoObject";
    }

    private static Set<String> _datamemberNames = new HashSet<String>();
    private static Set<String> _allDatamemberNames = new HashSet<String>();

    public static Set<String> get_attribute_names() {
        return new HashSet<String>(_datamemberNames);
    }

    public static Set<String> get_all_attribute_names() {
        return new HashSet<String>(_allDatamemberNames);
    }


    private static AttributeHandleSet _publishedAttributeHandleSet;
    private static Set<String> _publishAttributeNameSet = new HashSet<String>();

    private static AttributeHandleSet _subscribedAttributeHandleSet;
    private static Set<String> _subscribeAttributeNameSet = new HashSet<String>();


    static {
        _classNameSet.add(OBJECT_CLASS_NAME);
        _classNameClassMap.put(OBJECT_CLASS_NAME, CpswtFederateInfoObject.class);

        _datamemberClassNameSetMap.put(OBJECT_CLASS_NAME, _datamemberNames);
        _allDatamemberClassNameSetMap.put(OBJECT_CLASS_NAME, _allDatamemberNames);

        _datamemberNames.add("FederateId");
        _datamemberNames.add("FederateType");
        _datamemberNames.add("IsLateJoiner");

        _allDatamemberNames.add("FederateId");
        _allDatamemberNames.add("FederateType");
        _allDatamemberNames.add("IsLateJoiner");

        _datamemberTypeMap.put("FederateId", "String");
        _datamemberTypeMap.put("FederateType", "String");
        _datamemberTypeMap.put("IsLateJoiner", "boolean");

        _classNamePublishAttributeNameMap.put(OBJECT_CLASS_NAME, _publishAttributeNameSet);
        _publishedAttributeHandleSet = _factory.createAttributeHandleSet();
        _classNamePublishedAttributeMap.put(OBJECT_CLASS_NAME, _publishedAttributeHandleSet);

        _classNameSubscribeAttributeNameMap.put(OBJECT_CLASS_NAME, _subscribeAttributeNameSet);
        _subscribedAttributeHandleSet = _factory.createAttributeHandleSet();
        _classNameSubscribedAttributeMap.put(OBJECT_CLASS_NAME, _subscribedAttributeHandleSet);
    }

    private static String initErrorMessage = "Error:  ObjectRoot.CpswtFederateInfo:  could not initialize:  ";

    protected static void init(RTIambassador rti) {
        if (_isInitialized) return;
        _isInitialized = true;

        ObjectRoot.init(rti);

        boolean isNotInitialized = true;
        while (isNotInitialized) {
            try {
                _handle = rti.getObjectClassHandle(OBJECT_CLASS_NAME);
                isNotInitialized = false;
                LOG.trace("CpswtFederateInfoObject initialized");
            } catch (FederateNotExecutionMember f) {
                LOG.error("{} Federate Not Execution Member", initErrorMessage);
                LOG.error(f);
                return;
            } catch (NameNotFound n) {
                LOG.error("{} Name Not Found", initErrorMessage);
                LOG.error(n);
                return;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        _classNameHandleMap.put(OBJECT_CLASS_NAME, get_handle());
        _classHandleNameMap.put(get_handle(), OBJECT_CLASS_NAME);
        _classHandleSimpleNameMap.put(get_handle(), "CpswtFederateInfo");


        isNotInitialized = true;
        while (isNotInitialized) {
            try {

                _FederateId_handle = rti.getAttributeHandle("FederateId", get_handle());
                _FederateType_handle = rti.getAttributeHandle("FederateType", get_handle());
                _IsLateJoiner_handle = rti.getAttributeHandle("IsLateJoiner", get_handle());
                isNotInitialized = false;
            } catch (FederateNotExecutionMember f) {
                LOG.error("{} Federate Not Execution Member", initErrorMessage);
                LOG.error(f);
                return;
            } catch (ObjectClassNotDefined i) {
                LOG.error("{} Object Class Not Defined", initErrorMessage);
                LOG.error(i);
                return;
            } catch (NameNotFound n) {
                LOG.error("{} Name Not Found", initErrorMessage);
                LOG.error(n);
                return;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        _datamemberNameHandleMap.put("ObjectRoot.CpswtFederateInfo,FederateId", get_FederateId_handle());
        _datamemberNameHandleMap.put("ObjectRoot.CpswtFederateInfo,FederateType", get_FederateType_handle());
        _datamemberNameHandleMap.put("ObjectRoot.CpswtFederateInfo,IsLateJoiner", get_IsLateJoiner_handle());

        _datamemberHandleNameMap.put(get_FederateId_handle(), "FederateId");
        _datamemberHandleNameMap.put(get_FederateType_handle(), "FederateType");
        _datamemberHandleNameMap.put(get_IsLateJoiner_handle(), "IsLateJoiner");

    }

    private static boolean _isPublished = false;
    private static String publishErrorMessage = "Error:  ObjectRoot.CpswtFederateInfo:  could not publish:  ";

    /**
     * Publishes the CpswtFederateInfoObject object class for a federate.
     */
    public static void publish(RTIambassador rti) {
        if (_isPublished) return;

        init(rti);


        _publishedAttributeHandleSet.empty();
        for (String attributeName : _publishAttributeNameSet) {
            try {
                _publishedAttributeHandleSet.add(_datamemberNameHandleMap.get("ObjectRoot.CpswtFederateInfo," + attributeName));
            } catch (Exception e) {
                System.err.println(publishErrorMessage + "Could not publish \"" + attributeName + "\" attribute.");
            }
        }


        synchronized (rti) {
            boolean isNotPublished = true;
            while (isNotPublished) {
                try {
                    rti.publishObjectClass(get_handle(), _publishedAttributeHandleSet);
                    isNotPublished = false;
                    LOG.trace("CpswtFederateInfoObject published");
                } catch (FederateNotExecutionMember f) {
                    LOG.error("{} Federate Not Execution Member", publishErrorMessage);
                    LOG.error(f);
                    return;
                } catch (ObjectClassNotDefined i) {
                    LOG.error("{} Object Class Not Defined", publishErrorMessage);
                    LOG.error(i);
                    return;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        _isPublished = true;
    }

    private static String unpublishErrorMessage = "Error:  ObjectRoot.CpswtFederateInfo:  could not unpublish:  ";

    /**
     * Unpublishes the CpswtFederateInfoObject object class for a federate.
     */
    public static void unpublish(RTIambassador rti) {
        if (!_isPublished) return;

        init(rti);
        synchronized (rti) {
            boolean isNotUnpublished = true;
            while (isNotUnpublished) {
                try {
                    rti.unpublishObjectClass(get_handle());
                    isNotUnpublished = false;
                } catch (FederateNotExecutionMember f) {
                    LOG.error("{} Federate Not Execution Member", unpublishErrorMessage);
                    LOG.error(f);
                    return;
                } catch (ObjectClassNotDefined i) {
                    LOG.error("{} Object Class Not Defined", unpublishErrorMessage);
                    LOG.error(i);
                    return;
                } catch (ObjectClassNotPublished i) {
                    LOG.error("{} Object Class Not Published", unpublishErrorMessage);
                    LOG.error(i);
                    return;
                } catch (Exception e) {
                    e.printStackTrace();
                    try {
                        Thread.sleep(50);
                    } catch (Exception e1) {
                    }
                }
            }
        }

        _isPublished = false;
    }

    private static boolean _isSubscribed = false;
    private static String subscribeErrorMessage = "Error:  ObjectRoot.CpswtFederateInfo:  could not subscribe:  ";

    /**
     * Subscribes a federate to the CpswtFederateInfoObject object class.
     */
    public static void subscribe(RTIambassador rti) {
        if (_isSubscribed) return;

        init(rti);

        _subscribedAttributeHandleSet.empty();
        for (String attributeName : _subscribeAttributeNameSet) {
            try {
                _subscribedAttributeHandleSet.add(_datamemberNameHandleMap.get("ObjectRoot.CpswtFederateInfo," + attributeName));
            } catch (Exception e) {
                System.err.println(subscribeErrorMessage + "Could not subscribe to \"" + attributeName + "\" attribute.");
            }
        }


        synchronized (rti) {
            boolean isNotSubscribed = true;
            while (isNotSubscribed) {
                try {
                    rti.subscribeObjectClassAttributes(get_handle(), _subscribedAttributeHandleSet);
                    isNotSubscribed = false;
                    LOG.trace("CpswtFederateInfoObject subscribed");
                } catch (FederateNotExecutionMember f) {
                    LOG.error("{} Federate Not Execution Member", subscribeErrorMessage);
                    LOG.error(f);
                    return;
                } catch (ObjectClassNotDefined i) {
                    LOG.error("{} Object Class Not Defined", subscribeErrorMessage);
                    LOG.error(i);
                    return;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        _isSubscribed = true;
    }

    private static String unsubscribeErrorMessage = "Error:  ObjectRoot.CpswtFederateInfo:  could not unsubscribe:  ";

    /**
     * Unsubscribes a federate from the CpswtFederateInfoObject object class.
     */
    public static void unsubscribe(RTIambassador rti) {
        if (!_isSubscribed) return;

        init(rti);
        synchronized (rti) {
            boolean isNotUnsubscribed = true;
            while (isNotUnsubscribed) {
                try {
                    rti.unsubscribeObjectClass(get_handle());
                    isNotUnsubscribed = false;
                } catch (FederateNotExecutionMember f) {
                    LOG.error("{} Federate Not Execution Member", unsubscribeErrorMessage);
                    LOG.error(f);
                    return;
                } catch (ObjectClassNotDefined i) {
                    LOG.error("{} Object Class Not Defined", unsubscribeErrorMessage);
                    LOG.error(i);
                    return;
                } catch (ObjectClassNotSubscribed i) {
                    LOG.error("{} Object Class Not Subscribed", unsubscribeErrorMessage);
                    LOG.error(i);
                    return;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        _isSubscribed = false;
    }

    /**
     * Return true if "handle" is equal to the handle (RTI assigned) of this class
     * (that is, the CpswtFederateInfoObject object class).
     *
     * @param handle handle to compare to the value of the handle (RTI assigned) of
     *               this class (the CpswtFederateInfoObject object class).
     * @return "true" if "handle" matches the value of the handle of this class
     * (that is, the CpswtFederateInfoObject object class).
     */
    public static boolean match(int handle) {
        return handle == get_handle();
    }

    /**
     * Returns the handle (RTI assigned) of this instance's object class .
     *
     * @return the handle (RTI assigned) if this instance's object class
     */
    public int getClassHandle() {
        return get_handle();
    }

    /**
     * Returns the fully-qualified (dot-delimited) name of this instance's object class.
     *
     * @return the fully-qualified (dot-delimited) name of this instance's object class
     */
    public String getClassName() {
        return get_class_name();
    }

    /**
     * Returns the simple name (last name in its fully-qualified dot-delimited name)
     * of this instance's object class.
     *
     * @return the simple name of this instance's object class
     */
    public String getSimpleClassName() {
        return get_simple_class_name();
    }

    /**
     * Returns a set containing the names of all of the non-hiddenattributes of an
     * object class instance.
     *
     * @return set containing the names of all of the attributes of an
     * object class instance
     */
    public Set<String> getAttributeNames() {
        return get_attribute_names();
    }

    /**
     * Returns a set containing the names of all of the attributes of an
     * object class instance.
     *
     * @return set containing the names of all of the attributes of an
     * object class instance
     */
    public Set<String> getAllAttributeNames() {
        return get_all_attribute_names();
    }

    /**
     * Publishes the object class of this instance of the class for a federate.
     */
    public void publishObject(RTIambassador rti) {
        publish(rti);
    }

    /**
     * Unpublishes the object class of this instance of this class for a federate.
     */
    public void unpublishObject(RTIambassador rti) {
        unpublish(rti);
    }

    /**
     * Subscribes a federate to the object class of this instance of this class.
     */
    public void subscribeObject(RTIambassador rti) {
        subscribe(rti);
    }

    /**
     * Unsubscribes a federate from the object class of this instance of this class.
     */
    public void unsubscribeObject(RTIambassador rti) {
        unsubscribe(rti);
    }

    /**
     * Returns a data structure containing the handles of all attributes for this object
     * class that are currently marked for subscription.  To actually subscribe to these
     * attributes, a federate must call <objectclassname>.subscribe( RTIambassador rti ).
     *
     * @return data structure containing the handles of all attributes for this object
     * class that are currently marked for subscription
     */
    public AttributeHandleSet getSubscribedAttributeHandleSet() {
        return _subscribedAttributeHandleSet;
    }


    public String toString() {
        return "CpswtFederateInfo("


                + "FederateId:" + get_FederateId()
                + "," + "FederateType:" + get_FederateType()
                + "," + "IsLateJoiner" + get_IsLateJoiner()
                + ")";
    }


    /**
     * Publishes the "FederateId" attribute of the attribute's containing object
     * class for a federate.
     * Note:  This method only marks the "FederateId" attribute for publication.
     * To actually publish the attribute, the federate must (re)publish its containing
     * object class.
     * (using <objectClassName>.publish( RTIambassador rti ) ).
     */
    public static void publish_FederateId() {
        _publishAttributeNameSet.add("FederateId");
    }

    /**
     * Unpublishes the "FederateId" attribute of the attribute's containing object
     * class for a federate.
     * Note:  This method only marks the "FederateId" attribute for unpublication.
     * To actually publish the attribute, the federate must (re)publish its containing
     * object class.
     * (using <objectClassName>.publish( RTIambassador rti ) ).
     */
    public static void unpublish_FederateId() {
        _publishAttributeNameSet.remove("FederateId");
    }

    /**
     * Subscribes a federate to the "FederateId" attribute of the attribute's
     * containing object class.
     * Note:  This method only marks the "FederateId" attribute for subscription.
     * To actually subscribe to the attribute, the federate must (re)subscribe to its
     * containing object class.
     * (using <objectClassName>.subscribe( RTIambassador rti ) ).
     */
    public static void subscribe_FederateId() {
        _subscribeAttributeNameSet.add("FederateId");
    }

    /**
     * Unsubscribes a federate from the "FederateId" attribute of the attribute's
     * containing object class.
     * Note:  This method only marks the "FederateId" attribute for unsubscription.
     * To actually unsubscribe to the attribute, the federate must (re)subscribe to its
     * containing object class.
     * (using <objectClassName>.subscribe( RTIambassador rti ) ).
     */
    public static void unsubscribe_FederateId() {
        _subscribeAttributeNameSet.remove("FederateId");
    }


    /**
     * Publishes the "FederateType" attribute of the attribute's containing object
     * class for a federate.
     * Note:  This method only marks the "FederateType" attribute for publication.
     * To actually publish the attribute, the federate must (re)publish its containing
     * object class.
     * (using <objectClassName>.publish( RTIambassador rti ) ).
     */
    public static void publish_FederateType() {
        _publishAttributeNameSet.add("FederateType");
    }

    /**
     * Unpublishes the "FederateType" attribute of the attribute's containing object
     * class for a federate.
     * Note:  This method only marks the "FederateType" attribute for unpublication.
     * To actually publish the attribute, the federate must (re)publish its containing
     * object class.
     * (using <objectClassName>.publish( RTIambassador rti ) ).
     */
    public static void unpublish_FederateType() {
        _publishAttributeNameSet.remove("FederateType");
    }

    /**
     * Subscribes a federate to the "FederateType" attribute of the attribute's
     * containing object class.
     * Note:  This method only marks the "FederateType" attribute for subscription.
     * To actually subscribe to the attribute, the federate must (re)subscribe to its
     * containing object class.
     * (using <objectClassName>.subscribe( RTIambassador rti ) ).
     */
    public static void subscribe_FederateType() {
        _subscribeAttributeNameSet.add("FederateType");
    }

    /**
     * Unsubscribes a federate from the "FederateType" attribute of the attribute's
     * containing object class.
     * Note:  This method only marks the "FederateType" attribute for unsubscription.
     * To actually unsubscribe to the attribute, the federate must (re)subscribe to its
     * containing object class.
     * (using <objectClassName>.subscribe( RTIambassador rti ) ).
     */
    public static void unsubscribe_FederateType() {
        _subscribeAttributeNameSet.remove("FederateType");
    }

    /**
     * Publishes the "IsLateJoiner" attribute of the attribute's containing object
     * class for a federate.
     * Note:  This method only marks the "IsLateJoiner" attribute for publication.
     * To actually publish the attribute, the federate must (re)publish its containing
     * object class.
     * (using <objectClassName>.publish( RTIambassador rti ) ).
     */
    public static void publish_IsLateJoiner() {
        _publishAttributeNameSet.add("IsLateJoiner");
    }

    /**
     * Unpublishes the "IsLateJoiner" attribute of the attribute's containing object
     * class for a federate.
     * Note:  This method only marks the "IsLateJoiner" attribute for unpublication.
     * To actually publish the attribute, the federate must (re)publish its containing
     * object class.
     * (using <objectClassName>.publish( RTIambassador rti ) ).
     */
    public static void unpublish_IsLateJoiner() {
        _publishAttributeNameSet.remove("IsLateJoiner");
    }

    /**
     * Subscribes a federate to the "IsLateJoiner" attribute of the attribute's
     * containing object class.
     * Note:  This method only marks the "IsLateJoiner" attribute for subscription.
     * To actually subscribe to the attribute, the federate must (re)subscribe to its
     * containing object class.
     * (using <objectClassName>.subscribe( RTIambassador rti ) ).
     */
    public static void subscribe_IsLateJoiner() {
        _subscribeAttributeNameSet.add("IsLateJoiner");
    }

    /**
     * Unsubscribes a federate from the "IsLateJoiner" attribute of the attribute's
     * containing object class.
     * Note:  This method only marks the "IsLateJoiner" attribute for unsubscription.
     * To actually unsubscribe to the attribute, the federate must (re)subscribe to its
     * containing object class.
     * (using <objectClassName>.subscribe( RTIambassador rti ) ).
     */
    public static void unsubscribe_IsLateJoiner() {
        _subscribeAttributeNameSet.remove("IsLateJoiner");
    }


    private Attribute<String> _FederateId =
            new Attribute<String>("");

    /**
     * Set the value of the "FederateId" attribute to "value" for this object.
     *
     * @param value the new value for the "FederateId" attribute
     */
    public void set_FederateId(String value) {
        _FederateId.setValue(value);
        _FederateId.setTime(getTime());
    }

    /**
     * Returns the value of the "FederateId" attribute of this object.
     *
     * @return the value of the "FederateId" attribute
     */
    public String get_FederateId() {
        return _FederateId.getValue();
    }

    /**
     * Returns the current timestamp of the "FederateId" attribute of this object.
     *
     * @return the current timestamp of the "FederateId" attribute
     */
    public double get_FederateId_time() {
        return _FederateId.getTime();
    }

    private Attribute<String> _FederateType =
            new Attribute<String>("");

    /**
     * Set the value of the "FederateType" attribute to "value" for this object.
     *
     * @param value the new value for the "FederateType" attribute
     */
    public void set_FederateType(String value) {
        _FederateType.setValue(value);
        _FederateType.setTime(getTime());
    }

    /**
     * Returns the value of the "FederateType" attribute of this object.
     *
     * @return the value of the "FederateType" attribute
     */
    public String get_FederateType() {
        return _FederateType.getValue();
    }

    /**
     * Returns the current timestamp of the "FederateType" attribute of this object.
     *
     * @return the current timestamp of the "FederateType" attribute
     */
    public double get_FederateType_time() {
        return _FederateType.getTime();
    }

    private Attribute<Boolean> _IsLateJoiner = new Attribute<Boolean>(false);

    public void set_IsLateJoiner(boolean value) {
        _IsLateJoiner.setValue(value);
        _IsLateJoiner.setTime(getTime());
    }

    public boolean get_IsLateJoiner() {
        return _IsLateJoiner.getValue();
    }

    public double get_IsLateJoiner_time() {
        return _IsLateJoiner.getTime();
    }

    protected CpswtFederateInfoObject(ReflectedAttributes datamemberMap, boolean initFlag) {
        super(datamemberMap, false);
        if (initFlag) setAttributes(datamemberMap);
    }

    protected CpswtFederateInfoObject(ReflectedAttributes datamemberMap, LogicalTime logicalTime, boolean initFlag) {
        super(datamemberMap, logicalTime, false);
        if (initFlag) setAttributes(datamemberMap);
    }


    public CpswtFederateInfoObject(ReflectedAttributes datamemberMap) {
        this(datamemberMap, true);
    }

    public CpswtFederateInfoObject(ReflectedAttributes datamemberMap, LogicalTime logicalTime) {
        this(datamemberMap, logicalTime, true);
    }

    /**
     * Creates a new CpswtFederateInfoObject object class instance that is a duplicate
     * of the instance referred to by CpswtFederateInfoObject_var.
     *
     * @param CpswtFederateInfo_var CpswtFederateInfoObject object class instance of which
     *                              this newly created CpswtFederateInfoObject object class instance will be a
     *                              duplicate
     */
    public CpswtFederateInfoObject(CpswtFederateInfoObject CpswtFederateInfo_var) {
        super(CpswtFederateInfo_var);


        set_FederateId(CpswtFederateInfo_var.get_FederateId());
        set_FederateType(CpswtFederateInfo_var.get_FederateType());
        set_IsLateJoiner(CpswtFederateInfo_var.get_IsLateJoiner());
    }


    /**
     * Returns the value of the attribute whose name is "datamemberName"
     * for this object.
     *
     * @param datamemberName name of attribute whose value is to be
     *                       returned
     * @return value of the attribute whose name is "datamemberName"
     * for this object
     */
    public Object getAttribute(String datamemberName) {
        if ("FederateId".equals(datamemberName)) return new Integer(get_FederateId());
        else if ("FederateType".equals(datamemberName)) return get_FederateType();
        else if ("IsLateJoiner".equals(datamemberName)) return get_IsLateJoiner();
        else return super.getAttribute(datamemberName);
    }

    /**
     * Returns the value of the attribute whose handle (RTI assigned)
     * is "datamemberHandle" for this object.
     *
     * @param datamemberHandle handle (RTI assigned) of attribute whose
     *                         value is to be returned
     * @return value of the attribute whose handle (RTI assigned) is
     * "datamemberHandle" for this object
     */
    public Object getAttribute(int datamemberHandle) {
        if (get_FederateId_handle() == datamemberHandle) return new Integer(get_FederateId());
        else if (get_FederateType_handle() == datamemberHandle) return get_FederateType();
        else if (get_IsLateJoiner_handle() == datamemberHandle) return get_IsLateJoiner();
        else return super.getAttribute(datamemberHandle);
    }

    protected boolean setAttributeAux(int param_handle, String val) {
        boolean retval = true;

        if (param_handle == get_FederateId_handle()) set_FederateId(val);
        else if (param_handle == get_FederateType_handle()) set_FederateType(val);
        else if (param_handle == get_IsLateJoiner_handle()) set_IsLateJoiner(Boolean.parseBoolean(val));
        else retval = super.setAttributeAux(param_handle, val);

        return retval;
    }

    protected boolean setAttributeAux(String datamemberName, String val) {
        boolean retval = true;


        if ("FederateId".equals(datamemberName)) set_FederateId(val);
        else if ("FederateType".equals(datamemberName)) set_FederateType(val);
        else if ("IsLateJoiner".equals(datamemberName)) set_IsLateJoiner(Boolean.parseBoolean(val));
        else retval = super.setAttributeAux(datamemberName, val);

        return retval;
    }

    protected boolean setAttributeAux(String datamemberName, Object val) {
        boolean retval = true;

        if ("FederateId".equals(datamemberName)) set_FederateId((String) val);
        else if ("FederateType".equals(datamemberName)) set_FederateType((String) val);
        else if ("IsLateJoiner".equals(datamemberName)) set_IsLateJoiner((boolean) val);
        else retval = super.setAttributeAux(datamemberName, val);

        return retval;
    }

    protected SuppliedAttributes createSuppliedDatamembers(boolean force) {
        SuppliedAttributes datamembers = super.createSuppliedDatamembers(force);

        boolean isPublished = false;

        try {
            isPublished = _publishedAttributeHandleSet.isMember(get_FederateId_handle());
        } catch (Exception e) {
            System.err.println("ERROR:  ObjectRoot.CpswtFederateInfo.createSuppliedAttributes:  could not determine if FederateId is published.");
            isPublished = false;
        }
        if (isPublished && _FederateId.shouldBeUpdated(force)) {
            datamembers.add(get_FederateId_handle(), get_FederateId().getBytes());
            _FederateId.setHasBeenUpdated();
        }
        try {
            isPublished = _publishedAttributeHandleSet.isMember(get_FederateType_handle());
        } catch (Exception e) {
            System.err.println("ERROR:  ObjectRoot.CpswtFederateInfo.createSuppliedAttributes:  could not determine if FederateType is published.");
            isPublished = false;
        }
        if (isPublished && _FederateType.shouldBeUpdated(force)) {
            datamembers.add(get_FederateType_handle(), get_FederateType().getBytes());
            _FederateType.setHasBeenUpdated();
        }

        try {
            isPublished = _publishedAttributeHandleSet.isMember(get_IsLateJoiner_handle());
        } catch (Exception e) {
            System.err.println("ERROR:  ObjectRoot.CpswtFederateInfo.createSuppliedAttributes:  could not determine if IsLateJoiner is published.");
            isPublished = false;
        }
        if (isPublished && _IsLateJoiner.shouldBeUpdated(force)) {
            datamembers.add(get_IsLateJoiner_handle(), Boolean.toString(get_IsLateJoiner()).getBytes());
            _FederateType.setHasBeenUpdated();
        }

        return datamembers;
    }


    public void copyFrom(Object object) {
        super.copyFrom(object);
        if (object instanceof CpswtFederateInfoObject) {
            CpswtFederateInfoObject data = (CpswtFederateInfoObject) object;

            _FederateId = data._FederateId;
            _FederateType = data._FederateType;
            _IsLateJoiner = data._IsLateJoiner;
        }
    }

}
