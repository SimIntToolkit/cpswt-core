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
 */

package edu.vanderbilt.vuisis.cpswt.hla.ObjectRoot_p;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import hla.rti.AttributeHandleSet;
import hla.rti.LogicalTime;
import hla.rti.RTIambassador;
import hla.rti.ReflectedAttributes;


/**
 * Implements edu.vanderbilt.vuisis.cpswt.hla.ObjectRoot_p.BaseObjectClass
 */
@SuppressWarnings("unused")
public class BaseObjectClass extends edu.vanderbilt.vuisis.cpswt.hla.ObjectRoot {

    private static final Logger logger = LogManager.getLogger();

    // DUMMY STATIC METHOD TO ALLOW ACTIVE LOADING OF CLASS
    public static void load() { }

    // ----------------------------------------------------------------------------
    // STATIC PROPERTYS AND CODE THAT DEAL WITH NAMES
    // THIS CODE IS STATIC BECAUSE IT IS CLASS-DEPENDENT AND NOT INSTANCE-DEPENDENT
    // ----------------------------------------------------------------------------

    /**
     * Returns the fully-qualified (dot-delimited) name of the edu.vanderbilt.vuisis.cpswt.hla.ObjectRoot_p.BaseObjectClass object class.
     * Note: As this is a static method, it is NOT polymorphic, and so, if called on
     * a reference will return the name of the class pertaining to the reference,
     * rather than the name of the class for the instance referred to by the reference.
     * For the polymorphic version of this method, use {@link #getJavaClassName()}.
     *
     * @return the fully-qualified Java class name for this object class
     */
    public static String get_java_class_name() {
        return "edu.vanderbilt.vuisis.cpswt.hla.ObjectRoot_p.BaseObjectClass";
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
     * class name) of the edu.vanderbilt.vuisis.cpswt.hla.ObjectRoot_p.BaseObjectClass object class.
     *
     * @return the name of this object class
     */
    public static String get_simple_class_name() {
        return get_simple_class_name(get_hla_class_name());
    }

    /**
     * Returns the fully-qualified (dot-delimited) hla class name of the
     * ObjectRoot.BaseObjectClass object class.
     * Note: As this is a static method, it is NOT polymorphic, and so, if called on
     * a reference will return the federation name of the class pertaining to the reference,
     * rather than the name of the class for the instance referred to by the reference.
     * For the polymorphic version of this method, use {@link #getHlaClassName()}.
     *
     * @return the fully-qualified federation (HLA) class name for this object class
     */
    public static String get_hla_class_name() {
        return "ObjectRoot.BaseObjectClass";
    }

    /**
     * Returns the fully-qualified (dot-delimited) hla class name of this instance's object class.
     * Polymorphic equivalent of get_hla_class_name static method.
     *
     * @return the fully-qualified (dot-delimited) name of this instance's object class
     */
    public String getHlaClassName() {
        return get_hla_class_name();
    }

    /**
     * Returns a sorted list containing the names of all of the non-hidden attributes in the
     * edu.vanderbilt.vuisis.cpswt.hla.ObjectRoot_p.BaseObjectClass object class.
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
     * Returns a sorted list containing the names of all of the attributes in the
     * edu.vanderbilt.vuisis.cpswt.hla.ObjectRoot_p.BaseObjectClass object class.
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

    /*
     * INITIALIZE STATIC PROPERTIES THAT DEAL WITH NAMES
     */
    static {
        _hlaClassNameSet.add(get_hla_class_name());

        BaseObjectClass instance = new BaseObjectClass(noInstanceInit);
        _hlaClassNameInstanceMap.put(get_hla_class_name(), instance);

        Set<ClassAndPropertyName> classAndPropertyNameSet = new HashSet<>();
        classAndPropertyNameSet.add(new ClassAndPropertyName(
            "ObjectRoot.BaseObjectClass", "int_attribute1"
        ));
        classAndPropertyNameSet.add(new ClassAndPropertyName(
            "ObjectRoot.BaseObjectClass", "string_attribute1"
        ));

        // ADD THIS CLASS'S classAndPropertyNameSet TO _classNamePropertyNameSetMap DEFINED
        // IN ObjectRoot
        _classNamePropertyNameSetMap.put(get_hla_class_name(), classAndPropertyNameSet);


        Set<ClassAndPropertyName> allClassAndPropertyNameSet = new HashSet<>();

        allClassAndPropertyNameSet.add(new ClassAndPropertyName(
            "ObjectRoot.BaseObjectClass", "int_attribute1"
        ));

        allClassAndPropertyNameSet.add(new ClassAndPropertyName(
            "ObjectRoot.BaseObjectClass", "string_attribute1"
        ));


        // ADD THIS CLASS'S _allClassAndPropertyNameSet TO _allClassNamePropertyNameSetMap DEFINED
        // IN ObjectRoot
        _allClassNamePropertyNameSetMap.put(get_hla_class_name(), allClassAndPropertyNameSet);

        ClassAndPropertyName key;

        key = new ClassAndPropertyName(get_hla_class_name(), "int_attribute1");
        _classAndPropertyNameInitialValueMap.put(key, new Attribute<>(0));

        key = new ClassAndPropertyName(get_hla_class_name(), "string_attribute1");
        _classAndPropertyNameInitialValueMap.put(key, new Attribute<>(""));

        logger.info(
          "Class \"edu.vanderbilt.vuisis.cpswt.hla.ObjectRoot_p.BaseObjectClass\" (hla class \"{}\") loaded", get_hla_class_name()
        );

        System.err.println(
          "Class \"edu.vanderbilt.vuisis.cpswt.hla.ObjectRoot_p.BaseObjectClass\" (hla class \"" +
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
     * Returns the handle (RTI assigned) of the edu.vanderbilt.vuisis.cpswt.hla.ObjectRoot_p.BaseObjectClass object class.
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
     * Returns the handle of an attribute (RTI assigned) of
     * this object class (i.e. "edu.vanderbilt.vuisis.cpswt.hla.ObjectRoot_p.BaseObjectClass") given the attribute's name.
     *
     * @param propertyName name of attribute
     * @return the handle (RTI assigned) of the attribute "propertyName" of object class "className"
     */
    public static int get_attribute_handle(String propertyName) {
        return get_attribute_handle(get_hla_class_name(), propertyName);
    }

    public static AttributeHandleSet get_published_attribute_handle_set() {
        return get_published_attribute_handle_set( get_hla_class_name() );
    }

    /**
     * Returns a data structure containing the handles of all attributes for this object
     * class that are currently marked for subscription.  To actually subscribe to these
     * attributes, a federate must call &lt;objectclassname&gt;.subscribe( RTIambassador rti ).
     *
     * @return data structure containing the handles of all attributes for this object
     * class that are currently marked for subscription
     */
    public static AttributeHandleSet get_subscribed_attribute_handle_set() {
        return get_subscribed_attribute_handle_set( get_hla_class_name() );
    }

    // ----------------------------------------------------------
    // END OF STATIC PROPERTYS AND CODE THAT DEAL WITH HANDLES.
    // ----------------------------------------------------------


    //-------------------------------------------------
    // METHODS FOR PUBLISHING/SUBSCRIBING-TO THIS CLASS
    //-------------------------------------------------

    /**
     * Publishes the edu.vanderbilt.vuisis.cpswt.hla.ObjectRoot_p.BaseObjectClass object class for a federate.
     *
     * @param rti handle to the Local RTI Component
     */
    public static void publish_object(RTIambassador rti) {
        publish_object(get_hla_class_name(), rti);
    }

    public static Boolean get_is_published() {
        return get_is_published(get_hla_class_name());
    }

    /**
     * Unpublishes the edu.vanderbilt.vuisis.cpswt.hla.ObjectRoot_p.BaseObjectClass object class for a federate.
     *
     * @param rti handle to the Local RTI Component, usu. obtained through the
     *            {@link SynchronizedFederate#getRTI()} call
     */
    public static void unpublish_object(RTIambassador rti) {
        unpublish_object(get_hla_class_name(), rti);
    }

    /**
     * Subscribes a federate to the edu.vanderbilt.vuisis.cpswt.hla.ObjectRoot_p.BaseObjectClass object class.
     *
     * @param rti handle to the Local RTI Component
     */
    public static void subscribe_object(RTIambassador rti) {
        subscribe_object(get_hla_class_name(), rti);
    }

    public static Boolean get_is_subscribed() {
        return get_is_subscribed(get_hla_class_name());
    }

    public static void soft_subscribe_object(RTIambassador rti) {
        soft_subscribe_object(get_hla_class_name(), rti);
    }

    public static Boolean get_is_soft_subscribed() {
        return get_is_soft_subscribed(get_hla_class_name());
    }

    /**
     * Unsubscribes a federate from the edu.vanderbilt.vuisis.cpswt.hla.ObjectRoot_p.BaseObjectClass object class.
     *
     * @param rti handle to the Local RTI Component
     */
    public static void unsubscribe_object(RTIambassador rti) {
        unsubscribe_object(get_hla_class_name(), rti);
    }

    public static void soft_unsubscribe_object(RTIambassador rti) {
        soft_unsubscribe_object(get_hla_class_name(), rti);
    }

    public static Set<ClassAndPropertyName> get_published_attribute_name_set() {
        return _classNamePublishedAttributeNameSetMap.get(get_hla_class_name());
    }

    public static Set<ClassAndPropertyName> get_subscribed_attribute_name_set() {
        return _classNameSubscribedAttributeNameSetMap.get(get_hla_class_name());
    }

    public static Set<ClassAndPropertyName> get_softSubscribed_attribute_name_set() {
        return _classNameSoftSubscribedAttributeNameSetMap.get(get_hla_class_name());
    }

    public static void add_federate_name_soft_publish_direct(String federateName) {
        add_federate_name_soft_publish_direct(get_hla_class_name(), federateName);
    }

    public static void remove_federate_name_soft_publish_direct(String federateName) {
        remove_federate_name_soft_publish_direct(get_hla_class_name(), federateName);
    }

    public static Set<String> get_federate_name_soft_publish_direct_set() {
        return get_federate_name_soft_publish_direct_set(get_hla_class_name());
    }

    public static void add_federate_name_soft_publish(String networkFederateName) {
        add_federate_name_soft_publish(get_hla_class_name(), networkFederateName);
    }

    public static void remove_federate_name_soft_publish(String networkFederateName) {
        remove_federate_name_soft_publish(get_hla_class_name(), networkFederateName);
    }

    //-----------------------------------------------------
    // END METHODS FOR PUBLISHING/SUBSCRIBING-TO THIS CLASS
    //-----------------------------------------------------

    /**
     * Return true if "handle" is equal to the handle (RTI assigned) of this class
     * (that is, the edu.vanderbilt.vuisis.cpswt.hla.ObjectRoot_p.BaseObjectClass object class).
     *
     * @param handle handle to compare to the value of the handle (RTI assigned) of
     * this class (the edu.vanderbilt.vuisis.cpswt.hla.ObjectRoot_p.BaseObjectClass object class).
     * @return "true" if "handle" matches the value of the handle of this class
     * (that is, the edu.vanderbilt.vuisis.cpswt.hla.ObjectRoot_p.BaseObjectClass object class).
     */
    public static boolean match(int handle) {
        return handle == get_class_handle();
    }

    //-------------
    // CONSTRUCTORS
    //-------------
    public BaseObjectClass() {
        this(get_hla_class_name());
    }

    public BaseObjectClass(LogicalTime logicalTime) {
        this();
        setTime(logicalTime);
    }

    public BaseObjectClass(ReflectedAttributes propertyMap) {
        this();
        setAttributes( propertyMap );
    }

    public BaseObjectClass(ReflectedAttributes propertyMap, LogicalTime logicalTime) {
        this(propertyMap);
        setTime(logicalTime);
    }

    //-----------------
    // END CONSTRUCTORS
    //-----------------


    //-----------------
    // CREATION METHODS
    //-----------------
    public static BaseObjectClass create_object() {
        return new BaseObjectClass();
    }

    public BaseObjectClass createObject() {
        return create_object();
    }

    public static BaseObjectClass create_object(LogicalTime logicalTime) {
        return new BaseObjectClass(logicalTime);
    }

    public BaseObjectClass createObject(LogicalTime logicalTime) {
        return create_object(logicalTime);
    }

    public static BaseObjectClass create_object(ReflectedAttributes propertyMap) {
        return new BaseObjectClass(propertyMap);
    }

    public BaseObjectClass createObject(ReflectedAttributes propertyMap) {
        return create_object(propertyMap);
    }

    public static BaseObjectClass create_object(ReflectedAttributes propertyMap, LogicalTime logicalTime) {
        return new BaseObjectClass(propertyMap, logicalTime);
    }

    public BaseObjectClass createObject(ReflectedAttributes propertyMap, LogicalTime logicalTime) {
        return create_object(propertyMap, logicalTime);
    }

    //---------------------
    // END CREATION METHODS
    //---------------------

    public static int get_num_attributes() {
        return _allClassNamePropertyNameSetMap.get(get_hla_class_name()).size();
    }


    //------------------------------
    // PROPERTY MANIPULATION METHODS
    //------------------------------


    /**
     * Set the value of the "int_attribute1" parameter to "value" for this parameter.
     *
     * @param value the new value for the "int_attribute1" parameter
     */
    public void set_int_attribute1(Integer value) {
        ClassAndPropertyName key = new ClassAndPropertyName(get_hla_class_name(), "int_attribute1");
        Attribute<Object> attribute = (Attribute<Object>)classAndPropertyNameValueMap.get(key);
        attribute.setValue(value);
        attribute.setTime(getTime());
    }

    /**
     * Returns the value of the "int_attribute1" parameter of this interaction.
     *
     * @return the value of the "int_attribute1" parameter
     */
    public int get_int_attribute1() {
        ClassAndPropertyName key = new ClassAndPropertyName(get_hla_class_name(), "int_attribute1");
        return (int)((Attribute<Object>)classAndPropertyNameValueMap.get(key)).getValue();
    }

    /**
     * Returns the current timestamp of the "int_attribute1" attribute of this object.
     *
     * @return the current timestamp of the "int_attribute1" attribute
     */
    public double get_int_attribute1_time() {
        return ((Attribute<Object>)classAndPropertyNameValueMap.get(
          new ClassAndPropertyName(get_hla_class_name(), "int_attribute1")
        )).getTime();
    }


    /**
     * Set the value of the "string_attribute1" parameter to "value" for this parameter.
     *
     * @param value the new value for the "string_attribute1" parameter
     */
    public void set_string_attribute1(String value) {
        ClassAndPropertyName key = new ClassAndPropertyName(get_hla_class_name(), "string_attribute1");
        Attribute<Object> attribute = (Attribute<Object>)classAndPropertyNameValueMap.get(key);
        attribute.setValue(value);
        attribute.setTime(getTime());
    }

    /**
     * Returns the value of the "string_attribute1" parameter of this interaction.
     *
     * @return the value of the "string_attribute1" parameter
     */
    public String get_string_attribute1() {
        ClassAndPropertyName key = new ClassAndPropertyName(get_hla_class_name(), "string_attribute1");
        return (String)((Attribute<Object>)classAndPropertyNameValueMap.get(key)).getValue();
    }

    /**
     * Returns the current timestamp of the "string_attribute1" attribute of this object.
     *
     * @return the current timestamp of the "string_attribute1" attribute
     */
    public double get_string_attribute1_time() {
        return ((Attribute<Object>)classAndPropertyNameValueMap.get(
          new ClassAndPropertyName(get_hla_class_name(), "string_attribute1")
        )).getTime();
    }

    //----------------------------------
    // END PROPERTY MANIPULATION METHODS
    //----------------------------------

    public static void publish_attribute(String attributeClass, String attributeName) {
        publish_attribute(get_hla_class_name(), attributeClass, attributeName);
    }

    public static void publish_attribute(String attributeName) {
        publish_attribute(get_hla_class_name(), attributeName);
    }

    public static void unpublish_attribute(String attributeClass, String attributeName) {
        unpublish_attribute(get_hla_class_name(), attributeClass, attributeName);
    }

    public static void unpublish_attribute(String attributeName) {
        unpublish_attribute(get_hla_class_name(), attributeName);
    }

    public static void subscribe_attribute(String attributeClass, String attributeName) {
        subscribe_attribute(get_hla_class_name(), attributeClass, attributeName);
    }

    public static void subscribe_attribute(String attributeName) {
        subscribe_attribute(get_hla_class_name(), attributeName);
    }

    public static void unsubscribe_attribute(String attributeClass, String attributeName) {
        unsubscribe_attribute(get_hla_class_name(), attributeClass, attributeName);
    }

    public static void unsubscribe_attribute(String attributeName) {
        unsubscribe_attribute(get_hla_class_name(), attributeName);
    }

    public static void soft_subscribe_attribute(String attributeClass, String attributeName) {
        soft_subscribe_attribute(get_hla_class_name(), attributeClass, attributeName);
    }

    public static void soft_subscribe_attribute(String attributeName) {
        soft_subscribe_attribute(get_hla_class_name(), attributeName);
    }

    public static void soft_unsubscribe_attribute(String attributeClass, String attributeName) {
        soft_unsubscribe_attribute(get_hla_class_name(), attributeClass, attributeName);
    }

    public static void soft_unsubscribe_attribute(String attributeName) {
        soft_unsubscribe_attribute(get_hla_class_name(), attributeName);
    }

    /**
    * Publishes the "int_attribute1" attribute of the attribute's containing object
    * class for a federate.
    * Note:  This method only marks the "int_attribute1" attribute for publication.
    * To actually publish the attribute, the federate must (re)publish its containing
    * object class.
    * (using <objectClassName>.publish_object( RTIambassador rti ) ).
    */
    public static void publish_int_attribute1_attribute() {
        publish_attribute(get_hla_class_name(), "int_attribute1");
    }

    /**
    * Unpublishes the "int_attribute1" attribute of the attribute's containing object
    * class for a federate.
    * Note:  This method only marks the "int_attribute1" attribute for unpublication.
    * To actually publish the attribute, the federate must (re)publish its containing
    * object class.
    * (using <objectClassName>.publish_object( RTIambassador rti ) ).
    */
    public static void unpublish_int_attribute1_attribute() {
        unpublish_attribute(get_hla_class_name(), "int_attribute1");
    }

    /**
    * Subscribes a federate to the "int_attribute1" attribute of the attribute's
    * containing object class.
    * Note:  This method only marks the "int_attribute1" attribute for subscription.
    * To actually subscribe to the attribute, the federate must (re)subscribe to its
    * containing object class.
    * (using <objectClassName>.subscribe_object( RTIambassador rti ) ).
    */
    public static void subscribe_int_attribute1_attribute() {
        subscribe_attribute(get_hla_class_name(), "int_attribute1");
    }

    /**
    * Unsubscribes a federate from the "int_attribute1" attribute of the attribute's
    * containing object class.
    * Note:  This method only marks the "int_attribute1" attribute for unsubscription.
    * To actually unsubscribe to the attribute, the federate must (re)subscribe to its
    * containing object class.
    * (using <objectClassName>.subscribe_object( RTIambassador rti ) ).
    */
    public static void unsubscribe_int_attribute1_attribute() {
        unsubscribe_attribute(get_hla_class_name(), "int_attribute1");
    }

    /**
    * Soft subscribes a federate to the "int_attribute1" attribute of the attribute's
    * containing object class.
    * Note:  This method only marks the "int_attribute1" attribute for soft subscription.
    * To actually soft subscribe to the attribute, the federate must (re)subscribe to its
    * containing object class.
    * (using <objectClassName>.soft_subscribe_object( RTIambassador rti ) ).
    */
    public static void soft_subscribe_int_attribute1_attribute() {
        soft_subscribe_attribute(get_hla_class_name(), "int_attribute1");
    }

    /**
    * Soft unsubscribes a federate from the "int_attribute1" attribute of the attribute's
    * containing object class.
    * Note:  This method only marks the "int_attribute1" attribute for soft unsubscription.
    * To actually soft unsubscribe to the attribute, the federate must (re)subscribe to its
    * containing object class.
    * (using <objectClassName>.unsubscribe_object( RTIambassador rti ) ).
    */
    public static void soft_unsubscribe_int_attribute1_attribute() {
        soft_unsubscribe_attribute(get_hla_class_name(), "int_attribute1");
    }

    /**
    * Publishes the "string_attribute1" attribute of the attribute's containing object
    * class for a federate.
    * Note:  This method only marks the "string_attribute1" attribute for publication.
    * To actually publish the attribute, the federate must (re)publish its containing
    * object class.
    * (using <objectClassName>.publish_object( RTIambassador rti ) ).
    */
    public static void publish_string_attribute1_attribute() {
        publish_attribute(get_hla_class_name(), "string_attribute1");
    }

    /**
    * Unpublishes the "string_attribute1" attribute of the attribute's containing object
    * class for a federate.
    * Note:  This method only marks the "string_attribute1" attribute for unpublication.
    * To actually publish the attribute, the federate must (re)publish its containing
    * object class.
    * (using <objectClassName>.publish_object( RTIambassador rti ) ).
    */
    public static void unpublish_string_attribute1_attribute() {
        unpublish_attribute(get_hla_class_name(), "string_attribute1");
    }

    /**
    * Subscribes a federate to the "string_attribute1" attribute of the attribute's
    * containing object class.
    * Note:  This method only marks the "string_attribute1" attribute for subscription.
    * To actually subscribe to the attribute, the federate must (re)subscribe to its
    * containing object class.
    * (using <objectClassName>.subscribe_object( RTIambassador rti ) ).
    */
    public static void subscribe_string_attribute1_attribute() {
        subscribe_attribute(get_hla_class_name(), "string_attribute1");
    }

    /**
    * Unsubscribes a federate from the "string_attribute1" attribute of the attribute's
    * containing object class.
    * Note:  This method only marks the "string_attribute1" attribute for unsubscription.
    * To actually unsubscribe to the attribute, the federate must (re)subscribe to its
    * containing object class.
    * (using <objectClassName>.subscribe_object( RTIambassador rti ) ).
    */
    public static void unsubscribe_string_attribute1_attribute() {
        unsubscribe_attribute(get_hla_class_name(), "string_attribute1");
    }

    /**
    * Soft subscribes a federate to the "string_attribute1" attribute of the attribute's
    * containing object class.
    * Note:  This method only marks the "string_attribute1" attribute for soft subscription.
    * To actually soft subscribe to the attribute, the federate must (re)subscribe to its
    * containing object class.
    * (using <objectClassName>.soft_subscribe_object( RTIambassador rti ) ).
    */
    public static void soft_subscribe_string_attribute1_attribute() {
        soft_subscribe_attribute(get_hla_class_name(), "string_attribute1");
    }

    /**
    * Soft unsubscribes a federate from the "string_attribute1" attribute of the attribute's
    * containing object class.
    * Note:  This method only marks the "string_attribute1" attribute for soft unsubscription.
    * To actually soft unsubscribe to the attribute, the federate must (re)subscribe to its
    * containing object class.
    * (using <objectClassName>.unsubscribe_object( RTIambassador rti ) ).
    */
    public static void soft_unsubscribe_string_attribute1_attribute() {
        soft_unsubscribe_attribute(get_hla_class_name(), "string_attribute1");
    }

    protected BaseObjectClass(NoInstanceInit noInstanceInit) {
        super(noInstanceInit);
    }

    /**
    * Creates an instance of the BaseObjectClass object class, using
    * "datamemberMap" to initialize its attribute values.
    * "datamemberMap" is usually acquired as an argument to an RTI federate
    * callback method, such as "receiveInteraction".
    *
    * @param datamemberMap data structure containing initial values for the
    * attributes of this new BaseObjectClass object class instance
    */
    protected BaseObjectClass( String hlaClassName ) {
        super( hlaClassName );
    }

    /**
    * Creates a new BaseObjectClass object class instance that is a duplicate
    * of the instance referred to by messaging_var.
    *
    * @param messaging_var BaseObjectClass object class instance of which
    * this newly created BaseObjectClass object class instance will be a
    * duplicate
    */
    public BaseObjectClass(BaseObjectClass messaging_var) {
    
        // SHALLOW COPY
        classAndPropertyNameValueMap = new HashMap<>(messaging_var.classAndPropertyNameValueMap);

        // DEEP(ER) COPY FOR OBJECTS
        for(ClassAndPropertyName key: classAndPropertyNameValueMap.keySet()) {
            classAndPropertyNameValueMap.put(key, new Attribute<>((Attribute<Object>)classAndPropertyNameValueMap.get(key)));
        }

    }
}
