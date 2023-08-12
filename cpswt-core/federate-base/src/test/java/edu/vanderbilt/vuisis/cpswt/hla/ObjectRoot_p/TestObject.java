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
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.TextNode;


/**
 * Implements edu.vanderbilt.vuisis.cpswt.hla.ObjectRoot_p.TestObject
 */
@SuppressWarnings("unused")
public class TestObject extends edu.vanderbilt.vuisis.cpswt.hla.ObjectRoot {

    private static final Logger logger = LogManager.getLogger();

    // DUMMY STATIC METHOD TO ALLOW ACTIVE LOADING OF CLASS
    public static void load() { }

    // ----------------------------------------------------------------------------
    // STATIC PROPERTYS AND CODE THAT DEAL WITH NAMES
    // THIS CODE IS STATIC BECAUSE IT IS CLASS-DEPENDENT AND NOT INSTANCE-DEPENDENT
    // ----------------------------------------------------------------------------

    /**
     * Returns the fully-qualified (dot-delimited) name of the edu.vanderbilt.vuisis.cpswt.hla.ObjectRoot_p.TestObject object class.
     * Note: As this is a static method, it is NOT polymorphic, and so, if called on
     * a reference will return the name of the class pertaining to the reference,
     * rather than the name of the class for the instance referred to by the reference.
     * For the polymorphic version of this method, use {@link #getJavaClassName()}.
     *
     * @return the fully-qualified Java class name for this object class
     */
    public static String get_java_class_name() {
        return "edu.vanderbilt.vuisis.cpswt.hla.ObjectRoot_p.TestObject";
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
     * class name) of the edu.vanderbilt.vuisis.cpswt.hla.ObjectRoot_p.TestObject object class.
     *
     * @return the name of this object class
     */
    public static String get_simple_class_name() {
        return get_simple_class_name(get_hla_class_name());
    }

    /**
     * Returns the fully-qualified (dot-delimited) hla class name of the
     * ObjectRoot.TestObject object class.
     * Note: As this is a static method, it is NOT polymorphic, and so, if called on
     * a reference will return the federation name of the class pertaining to the reference,
     * rather than the name of the class for the instance referred to by the reference.
     * For the polymorphic version of this method, use {@link #getHlaClassName()}.
     *
     * @return the fully-qualified federation (HLA) class name for this object class
     */
    public static String get_hla_class_name() {
        return "ObjectRoot.TestObject";
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
     * edu.vanderbilt.vuisis.cpswt.hla.ObjectRoot_p.TestObject object class.
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
     * edu.vanderbilt.vuisis.cpswt.hla.ObjectRoot_p.TestObject object class.
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

        TestObject instance = new TestObject(noInstanceInit);
        _hlaClassNameInstanceMap.put(get_hla_class_name(), instance);

        Set<ClassAndPropertyName> classAndPropertyNameSet = new HashSet<>();
        classAndPropertyNameSet.add(new ClassAndPropertyName(
            "ObjectRoot.TestObject", "BoolValue1"
        ));
        classAndPropertyNameSet.add(new ClassAndPropertyName(
            "ObjectRoot.TestObject", "BoolValue2"
        ));
        classAndPropertyNameSet.add(new ClassAndPropertyName(
            "ObjectRoot.TestObject", "ByteValue"
        ));
        classAndPropertyNameSet.add(new ClassAndPropertyName(
            "ObjectRoot.TestObject", "CharValue"
        ));
        classAndPropertyNameSet.add(new ClassAndPropertyName(
            "ObjectRoot.TestObject", "DoubleValue"
        ));
        classAndPropertyNameSet.add(new ClassAndPropertyName(
            "ObjectRoot.TestObject", "FloatValue"
        ));
        classAndPropertyNameSet.add(new ClassAndPropertyName(
            "ObjectRoot.TestObject", "IntValue"
        ));
        classAndPropertyNameSet.add(new ClassAndPropertyName(
            "ObjectRoot.TestObject", "JSONValue1"
        ));
        classAndPropertyNameSet.add(new ClassAndPropertyName(
            "ObjectRoot.TestObject", "JSONValue2"
        ));
        classAndPropertyNameSet.add(new ClassAndPropertyName(
            "ObjectRoot.TestObject", "LongValue"
        ));
        classAndPropertyNameSet.add(new ClassAndPropertyName(
            "ObjectRoot.TestObject", "ShortValue"
        ));
        classAndPropertyNameSet.add(new ClassAndPropertyName(
            "ObjectRoot.TestObject", "StringValue"
        ));

        // ADD THIS CLASS'S classAndPropertyNameSet TO _classNamePropertyNameSetMap DEFINED
        // IN ObjectRoot
        _classNamePropertyNameSetMap.put(get_hla_class_name(), classAndPropertyNameSet);

        _completeClassAndPropertyNameSet.addAll(classAndPropertyNameSet);

        Set<ClassAndPropertyName> allClassAndPropertyNameSet = new HashSet<>();

        allClassAndPropertyNameSet.add(new ClassAndPropertyName(
            "ObjectRoot.TestObject", "BoolValue1"
        ));

        allClassAndPropertyNameSet.add(new ClassAndPropertyName(
            "ObjectRoot.TestObject", "BoolValue2"
        ));

        allClassAndPropertyNameSet.add(new ClassAndPropertyName(
            "ObjectRoot.TestObject", "ByteValue"
        ));

        allClassAndPropertyNameSet.add(new ClassAndPropertyName(
            "ObjectRoot.TestObject", "CharValue"
        ));

        allClassAndPropertyNameSet.add(new ClassAndPropertyName(
            "ObjectRoot.TestObject", "DoubleValue"
        ));

        allClassAndPropertyNameSet.add(new ClassAndPropertyName(
            "ObjectRoot.TestObject", "FloatValue"
        ));

        allClassAndPropertyNameSet.add(new ClassAndPropertyName(
            "ObjectRoot.TestObject", "IntValue"
        ));

        allClassAndPropertyNameSet.add(new ClassAndPropertyName(
            "ObjectRoot.TestObject", "JSONValue1"
        ));

        allClassAndPropertyNameSet.add(new ClassAndPropertyName(
            "ObjectRoot.TestObject", "JSONValue2"
        ));

        allClassAndPropertyNameSet.add(new ClassAndPropertyName(
            "ObjectRoot.TestObject", "LongValue"
        ));

        allClassAndPropertyNameSet.add(new ClassAndPropertyName(
            "ObjectRoot.TestObject", "ShortValue"
        ));

        allClassAndPropertyNameSet.add(new ClassAndPropertyName(
            "ObjectRoot.TestObject", "StringValue"
        ));


        // ADD THIS CLASS'S _allClassAndPropertyNameSet TO _allClassNamePropertyNameSetMap DEFINED
        // IN ObjectRoot
        _allClassNamePropertyNameSetMap.put(get_hla_class_name(), allClassAndPropertyNameSet);

        ClassAndPropertyName key;

        key = new ClassAndPropertyName(get_hla_class_name(), "BoolValue1");
        _classAndPropertyNameInitialValueMap.put(key, new Attribute<>(false));

        key = new ClassAndPropertyName(get_hla_class_name(), "BoolValue2");
        _classAndPropertyNameInitialValueMap.put(key, new Attribute<>(false));

        key = new ClassAndPropertyName(get_hla_class_name(), "ByteValue");
        _classAndPropertyNameInitialValueMap.put(key, new Attribute<>((byte)0));

        key = new ClassAndPropertyName(get_hla_class_name(), "CharValue");
        _classAndPropertyNameInitialValueMap.put(key, new Attribute<>('\0'));

        key = new ClassAndPropertyName(get_hla_class_name(), "DoubleValue");
        _classAndPropertyNameInitialValueMap.put(key, new Attribute<>((double)0));

        key = new ClassAndPropertyName(get_hla_class_name(), "FloatValue");
        _classAndPropertyNameInitialValueMap.put(key, new Attribute<>((float)0));

        key = new ClassAndPropertyName(get_hla_class_name(), "IntValue");
        _classAndPropertyNameInitialValueMap.put(key, new Attribute<>(0));

        key = new ClassAndPropertyName(get_hla_class_name(), "JSONValue1");
        _classAndPropertyNameInitialValueMap.put(key, new Attribute<>(new TextNode("")));

        key = new ClassAndPropertyName(get_hla_class_name(), "JSONValue2");
        _classAndPropertyNameInitialValueMap.put(key, new Attribute<>(new TextNode("")));

        key = new ClassAndPropertyName(get_hla_class_name(), "LongValue");
        _classAndPropertyNameInitialValueMap.put(key, new Attribute<>((long)0));

        key = new ClassAndPropertyName(get_hla_class_name(), "ShortValue");
        _classAndPropertyNameInitialValueMap.put(key, new Attribute<>((short)0));

        key = new ClassAndPropertyName(get_hla_class_name(), "StringValue");
        _classAndPropertyNameInitialValueMap.put(key, new Attribute<>(""));

        commonInit(get_hla_class_name());

        logger.info(
          "Class \"edu.vanderbilt.vuisis.cpswt.hla.ObjectRoot_p.TestObject\" (hla class \"{}\") loaded", get_hla_class_name()
        );

        System.err.println(
          "Class \"edu.vanderbilt.vuisis.cpswt.hla.ObjectRoot_p.TestObject\" (hla class \"" +
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
     * Returns the handle (RTI assigned) of the edu.vanderbilt.vuisis.cpswt.hla.ObjectRoot_p.TestObject object class.
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
     * this object class (i.e. "edu.vanderbilt.vuisis.cpswt.hla.ObjectRoot_p.TestObject") given the attribute's name.
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
     * Publishes the edu.vanderbilt.vuisis.cpswt.hla.ObjectRoot_p.TestObject object class for a federate.
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
     * Unpublishes the edu.vanderbilt.vuisis.cpswt.hla.ObjectRoot_p.TestObject object class for a federate.
     *
     * @param rti handle to the Local RTI Component, usu. obtained through the
     *            {@link SynchronizedFederate#getRTI()} call
     */
    public static void unpublish_object(RTIambassador rti) {
        unpublish_object(get_hla_class_name(), rti);
    }

    /**
     * Subscribes a federate to the edu.vanderbilt.vuisis.cpswt.hla.ObjectRoot_p.TestObject object class.
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
     * Unsubscribes a federate from the edu.vanderbilt.vuisis.cpswt.hla.ObjectRoot_p.TestObject object class.
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
     * (that is, the edu.vanderbilt.vuisis.cpswt.hla.ObjectRoot_p.TestObject object class).
     *
     * @param handle handle to compare to the value of the handle (RTI assigned) of
     * this class (the edu.vanderbilt.vuisis.cpswt.hla.ObjectRoot_p.TestObject object class).
     * @return "true" if "handle" matches the value of the handle of this class
     * (that is, the edu.vanderbilt.vuisis.cpswt.hla.ObjectRoot_p.TestObject object class).
     */
    public static boolean match(int handle) {
        return handle == get_class_handle();
    }

    //-------------
    // CONSTRUCTORS
    //-------------
    public TestObject() {
        this(get_hla_class_name());
    }

    public TestObject(LogicalTime logicalTime) {
        this();
        setTime(logicalTime);
    }

    public TestObject(ReflectedAttributes propertyMap) {
        this();
        setAttributes( propertyMap );
    }

    public TestObject(ReflectedAttributes propertyMap, LogicalTime logicalTime) {
        this(propertyMap);
        setTime(logicalTime);
    }

    //-----------------
    // END CONSTRUCTORS
    //-----------------


    //-----------------
    // CREATION METHODS
    //-----------------
    public static TestObject create_object() {
        return new TestObject();
    }

    public TestObject createObject() {
        return create_object();
    }

    public static TestObject create_object(LogicalTime logicalTime) {
        return new TestObject(logicalTime);
    }

    public TestObject createObject(LogicalTime logicalTime) {
        return create_object(logicalTime);
    }

    public static TestObject create_object(ReflectedAttributes propertyMap) {
        return new TestObject(propertyMap);
    }

    public TestObject createObject(ReflectedAttributes propertyMap) {
        return create_object(propertyMap);
    }

    public static TestObject create_object(ReflectedAttributes propertyMap, LogicalTime logicalTime) {
        return new TestObject(propertyMap, logicalTime);
    }

    public TestObject createObject(ReflectedAttributes propertyMap, LogicalTime logicalTime) {
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
     * Set the value of the "BoolValue1" parameter to "value" for this parameter.
     *
     * @param value the new value for the "BoolValue1" parameter
     */
    public void set_BoolValue1(Boolean value) {
        ClassAndPropertyName key = new ClassAndPropertyName(get_hla_class_name(), "BoolValue1");
        Attribute<Object> attribute = (Attribute<Object>)classAndPropertyNameValueMap.get(key);
        attribute.setValue(value);
        attribute.setTime(getTime());
    }

    /**
     * Returns the value of the "BoolValue1" parameter of this interaction.
     *
     * @return the value of the "BoolValue1" parameter
     */
    public boolean get_BoolValue1() {
        ClassAndPropertyName key = new ClassAndPropertyName(get_hla_class_name(), "BoolValue1");
        return (boolean)((Attribute<Object>)classAndPropertyNameValueMap.get(key)).getValue();
    }

    /**
     * Returns the current timestamp of the "BoolValue1" attribute of this object.
     *
     * @return the current timestamp of the "BoolValue1" attribute
     */
    public double get_BoolValue1_time() {
        return ((Attribute<Object>)classAndPropertyNameValueMap.get(
          new ClassAndPropertyName(get_hla_class_name(), "BoolValue1")
        )).getTime();
    }


    /**
     * Set the value of the "BoolValue2" parameter to "value" for this parameter.
     *
     * @param value the new value for the "BoolValue2" parameter
     */
    public void set_BoolValue2(Boolean value) {
        ClassAndPropertyName key = new ClassAndPropertyName(get_hla_class_name(), "BoolValue2");
        Attribute<Object> attribute = (Attribute<Object>)classAndPropertyNameValueMap.get(key);
        attribute.setValue(value);
        attribute.setTime(getTime());
    }

    /**
     * Returns the value of the "BoolValue2" parameter of this interaction.
     *
     * @return the value of the "BoolValue2" parameter
     */
    public boolean get_BoolValue2() {
        ClassAndPropertyName key = new ClassAndPropertyName(get_hla_class_name(), "BoolValue2");
        return (boolean)((Attribute<Object>)classAndPropertyNameValueMap.get(key)).getValue();
    }

    /**
     * Returns the current timestamp of the "BoolValue2" attribute of this object.
     *
     * @return the current timestamp of the "BoolValue2" attribute
     */
    public double get_BoolValue2_time() {
        return ((Attribute<Object>)classAndPropertyNameValueMap.get(
          new ClassAndPropertyName(get_hla_class_name(), "BoolValue2")
        )).getTime();
    }


    /**
     * Set the value of the "ByteValue" parameter to "value" for this parameter.
     *
     * @param value the new value for the "ByteValue" parameter
     */
    public void set_ByteValue(Byte value) {
        ClassAndPropertyName key = new ClassAndPropertyName(get_hla_class_name(), "ByteValue");
        Attribute<Object> attribute = (Attribute<Object>)classAndPropertyNameValueMap.get(key);
        attribute.setValue(value);
        attribute.setTime(getTime());
    }

    /**
     * Returns the value of the "ByteValue" parameter of this interaction.
     *
     * @return the value of the "ByteValue" parameter
     */
    public byte get_ByteValue() {
        ClassAndPropertyName key = new ClassAndPropertyName(get_hla_class_name(), "ByteValue");
        return (byte)((Attribute<Object>)classAndPropertyNameValueMap.get(key)).getValue();
    }

    /**
     * Returns the current timestamp of the "ByteValue" attribute of this object.
     *
     * @return the current timestamp of the "ByteValue" attribute
     */
    public double get_ByteValue_time() {
        return ((Attribute<Object>)classAndPropertyNameValueMap.get(
          new ClassAndPropertyName(get_hla_class_name(), "ByteValue")
        )).getTime();
    }


    /**
     * Set the value of the "CharValue" parameter to "value" for this parameter.
     *
     * @param value the new value for the "CharValue" parameter
     */
    public void set_CharValue(Character value) {
        ClassAndPropertyName key = new ClassAndPropertyName(get_hla_class_name(), "CharValue");
        Attribute<Object> attribute = (Attribute<Object>)classAndPropertyNameValueMap.get(key);
        attribute.setValue(value);
        attribute.setTime(getTime());
    }

    /**
     * Returns the value of the "CharValue" parameter of this interaction.
     *
     * @return the value of the "CharValue" parameter
     */
    public char get_CharValue() {
        ClassAndPropertyName key = new ClassAndPropertyName(get_hla_class_name(), "CharValue");
        return (char)((Attribute<Object>)classAndPropertyNameValueMap.get(key)).getValue();
    }

    /**
     * Returns the current timestamp of the "CharValue" attribute of this object.
     *
     * @return the current timestamp of the "CharValue" attribute
     */
    public double get_CharValue_time() {
        return ((Attribute<Object>)classAndPropertyNameValueMap.get(
          new ClassAndPropertyName(get_hla_class_name(), "CharValue")
        )).getTime();
    }


    /**
     * Set the value of the "DoubleValue" parameter to "value" for this parameter.
     *
     * @param value the new value for the "DoubleValue" parameter
     */
    public void set_DoubleValue(Double value) {
        ClassAndPropertyName key = new ClassAndPropertyName(get_hla_class_name(), "DoubleValue");
        Attribute<Object> attribute = (Attribute<Object>)classAndPropertyNameValueMap.get(key);
        attribute.setValue(value);
        attribute.setTime(getTime());
    }

    /**
     * Returns the value of the "DoubleValue" parameter of this interaction.
     *
     * @return the value of the "DoubleValue" parameter
     */
    public double get_DoubleValue() {
        ClassAndPropertyName key = new ClassAndPropertyName(get_hla_class_name(), "DoubleValue");
        return (double)((Attribute<Object>)classAndPropertyNameValueMap.get(key)).getValue();
    }

    /**
     * Returns the current timestamp of the "DoubleValue" attribute of this object.
     *
     * @return the current timestamp of the "DoubleValue" attribute
     */
    public double get_DoubleValue_time() {
        return ((Attribute<Object>)classAndPropertyNameValueMap.get(
          new ClassAndPropertyName(get_hla_class_name(), "DoubleValue")
        )).getTime();
    }


    /**
     * Set the value of the "FloatValue" parameter to "value" for this parameter.
     *
     * @param value the new value for the "FloatValue" parameter
     */
    public void set_FloatValue(Float value) {
        ClassAndPropertyName key = new ClassAndPropertyName(get_hla_class_name(), "FloatValue");
        Attribute<Object> attribute = (Attribute<Object>)classAndPropertyNameValueMap.get(key);
        attribute.setValue(value);
        attribute.setTime(getTime());
    }

    /**
     * Returns the value of the "FloatValue" parameter of this interaction.
     *
     * @return the value of the "FloatValue" parameter
     */
    public float get_FloatValue() {
        ClassAndPropertyName key = new ClassAndPropertyName(get_hla_class_name(), "FloatValue");
        return (float)((Attribute<Object>)classAndPropertyNameValueMap.get(key)).getValue();
    }

    /**
     * Returns the current timestamp of the "FloatValue" attribute of this object.
     *
     * @return the current timestamp of the "FloatValue" attribute
     */
    public double get_FloatValue_time() {
        return ((Attribute<Object>)classAndPropertyNameValueMap.get(
          new ClassAndPropertyName(get_hla_class_name(), "FloatValue")
        )).getTime();
    }


    /**
     * Set the value of the "IntValue" parameter to "value" for this parameter.
     *
     * @param value the new value for the "IntValue" parameter
     */
    public void set_IntValue(Integer value) {
        ClassAndPropertyName key = new ClassAndPropertyName(get_hla_class_name(), "IntValue");
        Attribute<Object> attribute = (Attribute<Object>)classAndPropertyNameValueMap.get(key);
        attribute.setValue(value);
        attribute.setTime(getTime());
    }

    /**
     * Returns the value of the "IntValue" parameter of this interaction.
     *
     * @return the value of the "IntValue" parameter
     */
    public int get_IntValue() {
        ClassAndPropertyName key = new ClassAndPropertyName(get_hla_class_name(), "IntValue");
        return (int)((Attribute<Object>)classAndPropertyNameValueMap.get(key)).getValue();
    }

    /**
     * Returns the current timestamp of the "IntValue" attribute of this object.
     *
     * @return the current timestamp of the "IntValue" attribute
     */
    public double get_IntValue_time() {
        return ((Attribute<Object>)classAndPropertyNameValueMap.get(
          new ClassAndPropertyName(get_hla_class_name(), "IntValue")
        )).getTime();
    }


    /**
     * Set the value of the "JSONValue1" parameter to "value" for this parameter.
     *
     * @param value the new value for the "JSONValue1" parameter
     */
    public void set_JSONValue1(JsonNode value) {
        ClassAndPropertyName key = new ClassAndPropertyName(get_hla_class_name(), "JSONValue1");
        Attribute<Object> attribute = (Attribute<Object>)classAndPropertyNameValueMap.get(key);
        attribute.setValue(value);
        attribute.setTime(getTime());
    }

    /**
     * Returns the value of the "JSONValue1" parameter of this interaction.
     *
     * @return the value of the "JSONValue1" parameter
     */
    public JsonNode get_JSONValue1() {
        ClassAndPropertyName key = new ClassAndPropertyName(get_hla_class_name(), "JSONValue1");
        return (JsonNode)((Attribute<Object>)classAndPropertyNameValueMap.get(key)).getValue();
    }

    /**
     * Returns the current timestamp of the "JSONValue1" attribute of this object.
     *
     * @return the current timestamp of the "JSONValue1" attribute
     */
    public double get_JSONValue1_time() {
        return ((Attribute<Object>)classAndPropertyNameValueMap.get(
          new ClassAndPropertyName(get_hla_class_name(), "JSONValue1")
        )).getTime();
    }


    /**
     * Set the value of the "JSONValue2" parameter to "value" for this parameter.
     *
     * @param value the new value for the "JSONValue2" parameter
     */
    public void set_JSONValue2(JsonNode value) {
        ClassAndPropertyName key = new ClassAndPropertyName(get_hla_class_name(), "JSONValue2");
        Attribute<Object> attribute = (Attribute<Object>)classAndPropertyNameValueMap.get(key);
        attribute.setValue(value);
        attribute.setTime(getTime());
    }

    /**
     * Returns the value of the "JSONValue2" parameter of this interaction.
     *
     * @return the value of the "JSONValue2" parameter
     */
    public JsonNode get_JSONValue2() {
        ClassAndPropertyName key = new ClassAndPropertyName(get_hla_class_name(), "JSONValue2");
        return (JsonNode)((Attribute<Object>)classAndPropertyNameValueMap.get(key)).getValue();
    }

    /**
     * Returns the current timestamp of the "JSONValue2" attribute of this object.
     *
     * @return the current timestamp of the "JSONValue2" attribute
     */
    public double get_JSONValue2_time() {
        return ((Attribute<Object>)classAndPropertyNameValueMap.get(
          new ClassAndPropertyName(get_hla_class_name(), "JSONValue2")
        )).getTime();
    }


    /**
     * Set the value of the "LongValue" parameter to "value" for this parameter.
     *
     * @param value the new value for the "LongValue" parameter
     */
    public void set_LongValue(Long value) {
        ClassAndPropertyName key = new ClassAndPropertyName(get_hla_class_name(), "LongValue");
        Attribute<Object> attribute = (Attribute<Object>)classAndPropertyNameValueMap.get(key);
        attribute.setValue(value);
        attribute.setTime(getTime());
    }

    /**
     * Returns the value of the "LongValue" parameter of this interaction.
     *
     * @return the value of the "LongValue" parameter
     */
    public long get_LongValue() {
        ClassAndPropertyName key = new ClassAndPropertyName(get_hla_class_name(), "LongValue");
        return (long)((Attribute<Object>)classAndPropertyNameValueMap.get(key)).getValue();
    }

    /**
     * Returns the current timestamp of the "LongValue" attribute of this object.
     *
     * @return the current timestamp of the "LongValue" attribute
     */
    public double get_LongValue_time() {
        return ((Attribute<Object>)classAndPropertyNameValueMap.get(
          new ClassAndPropertyName(get_hla_class_name(), "LongValue")
        )).getTime();
    }


    /**
     * Set the value of the "ShortValue" parameter to "value" for this parameter.
     *
     * @param value the new value for the "ShortValue" parameter
     */
    public void set_ShortValue(Short value) {
        ClassAndPropertyName key = new ClassAndPropertyName(get_hla_class_name(), "ShortValue");
        Attribute<Object> attribute = (Attribute<Object>)classAndPropertyNameValueMap.get(key);
        attribute.setValue(value);
        attribute.setTime(getTime());
    }

    /**
     * Returns the value of the "ShortValue" parameter of this interaction.
     *
     * @return the value of the "ShortValue" parameter
     */
    public short get_ShortValue() {
        ClassAndPropertyName key = new ClassAndPropertyName(get_hla_class_name(), "ShortValue");
        return (short)((Attribute<Object>)classAndPropertyNameValueMap.get(key)).getValue();
    }

    /**
     * Returns the current timestamp of the "ShortValue" attribute of this object.
     *
     * @return the current timestamp of the "ShortValue" attribute
     */
    public double get_ShortValue_time() {
        return ((Attribute<Object>)classAndPropertyNameValueMap.get(
          new ClassAndPropertyName(get_hla_class_name(), "ShortValue")
        )).getTime();
    }


    /**
     * Set the value of the "StringValue" parameter to "value" for this parameter.
     *
     * @param value the new value for the "StringValue" parameter
     */
    public void set_StringValue(String value) {
        ClassAndPropertyName key = new ClassAndPropertyName(get_hla_class_name(), "StringValue");
        Attribute<Object> attribute = (Attribute<Object>)classAndPropertyNameValueMap.get(key);
        attribute.setValue(value);
        attribute.setTime(getTime());
    }

    /**
     * Returns the value of the "StringValue" parameter of this interaction.
     *
     * @return the value of the "StringValue" parameter
     */
    public String get_StringValue() {
        ClassAndPropertyName key = new ClassAndPropertyName(get_hla_class_name(), "StringValue");
        return (String)((Attribute<Object>)classAndPropertyNameValueMap.get(key)).getValue();
    }

    /**
     * Returns the current timestamp of the "StringValue" attribute of this object.
     *
     * @return the current timestamp of the "StringValue" attribute
     */
    public double get_StringValue_time() {
        return ((Attribute<Object>)classAndPropertyNameValueMap.get(
          new ClassAndPropertyName(get_hla_class_name(), "StringValue")
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
    * Publishes the "BoolValue1" attribute of the attribute's containing object
    * class for a federate.
    * Note:  This method only marks the "BoolValue1" attribute for publication.
    * To actually publish the attribute, the federate must (re)publish its containing
    * object class.
    * (using <objectClassName>.publish_object( RTIambassador rti ) ).
    */
    public static void publish_BoolValue1_attribute() {
        publish_attribute(get_hla_class_name(), "BoolValue1");
    }

    /**
    * Unpublishes the "BoolValue1" attribute of the attribute's containing object
    * class for a federate.
    * Note:  This method only marks the "BoolValue1" attribute for unpublication.
    * To actually publish the attribute, the federate must (re)publish its containing
    * object class.
    * (using <objectClassName>.publish_object( RTIambassador rti ) ).
    */
    public static void unpublish_BoolValue1_attribute() {
        unpublish_attribute(get_hla_class_name(), "BoolValue1");
    }

    /**
    * Subscribes a federate to the "BoolValue1" attribute of the attribute's
    * containing object class.
    * Note:  This method only marks the "BoolValue1" attribute for subscription.
    * To actually subscribe to the attribute, the federate must (re)subscribe to its
    * containing object class.
    * (using <objectClassName>.subscribe_object( RTIambassador rti ) ).
    */
    public static void subscribe_BoolValue1_attribute() {
        subscribe_attribute(get_hla_class_name(), "BoolValue1");
    }

    /**
    * Unsubscribes a federate from the "BoolValue1" attribute of the attribute's
    * containing object class.
    * Note:  This method only marks the "BoolValue1" attribute for unsubscription.
    * To actually unsubscribe to the attribute, the federate must (re)subscribe to its
    * containing object class.
    * (using <objectClassName>.subscribe_object( RTIambassador rti ) ).
    */
    public static void unsubscribe_BoolValue1_attribute() {
        unsubscribe_attribute(get_hla_class_name(), "BoolValue1");
    }

    /**
    * Soft subscribes a federate to the "BoolValue1" attribute of the attribute's
    * containing object class.
    * Note:  This method only marks the "BoolValue1" attribute for soft subscription.
    * To actually soft subscribe to the attribute, the federate must (re)subscribe to its
    * containing object class.
    * (using <objectClassName>.soft_subscribe_object( RTIambassador rti ) ).
    */
    public static void soft_subscribe_BoolValue1_attribute() {
        soft_subscribe_attribute(get_hla_class_name(), "BoolValue1");
    }

    /**
    * Soft unsubscribes a federate from the "BoolValue1" attribute of the attribute's
    * containing object class.
    * Note:  This method only marks the "BoolValue1" attribute for soft unsubscription.
    * To actually soft unsubscribe to the attribute, the federate must (re)subscribe to its
    * containing object class.
    * (using <objectClassName>.unsubscribe_object( RTIambassador rti ) ).
    */
    public static void soft_unsubscribe_BoolValue1_attribute() {
        soft_unsubscribe_attribute(get_hla_class_name(), "BoolValue1");
    }

    /**
    * Publishes the "BoolValue2" attribute of the attribute's containing object
    * class for a federate.
    * Note:  This method only marks the "BoolValue2" attribute for publication.
    * To actually publish the attribute, the federate must (re)publish its containing
    * object class.
    * (using <objectClassName>.publish_object( RTIambassador rti ) ).
    */
    public static void publish_BoolValue2_attribute() {
        publish_attribute(get_hla_class_name(), "BoolValue2");
    }

    /**
    * Unpublishes the "BoolValue2" attribute of the attribute's containing object
    * class for a federate.
    * Note:  This method only marks the "BoolValue2" attribute for unpublication.
    * To actually publish the attribute, the federate must (re)publish its containing
    * object class.
    * (using <objectClassName>.publish_object( RTIambassador rti ) ).
    */
    public static void unpublish_BoolValue2_attribute() {
        unpublish_attribute(get_hla_class_name(), "BoolValue2");
    }

    /**
    * Subscribes a federate to the "BoolValue2" attribute of the attribute's
    * containing object class.
    * Note:  This method only marks the "BoolValue2" attribute for subscription.
    * To actually subscribe to the attribute, the federate must (re)subscribe to its
    * containing object class.
    * (using <objectClassName>.subscribe_object( RTIambassador rti ) ).
    */
    public static void subscribe_BoolValue2_attribute() {
        subscribe_attribute(get_hla_class_name(), "BoolValue2");
    }

    /**
    * Unsubscribes a federate from the "BoolValue2" attribute of the attribute's
    * containing object class.
    * Note:  This method only marks the "BoolValue2" attribute for unsubscription.
    * To actually unsubscribe to the attribute, the federate must (re)subscribe to its
    * containing object class.
    * (using <objectClassName>.subscribe_object( RTIambassador rti ) ).
    */
    public static void unsubscribe_BoolValue2_attribute() {
        unsubscribe_attribute(get_hla_class_name(), "BoolValue2");
    }

    /**
    * Soft subscribes a federate to the "BoolValue2" attribute of the attribute's
    * containing object class.
    * Note:  This method only marks the "BoolValue2" attribute for soft subscription.
    * To actually soft subscribe to the attribute, the federate must (re)subscribe to its
    * containing object class.
    * (using <objectClassName>.soft_subscribe_object( RTIambassador rti ) ).
    */
    public static void soft_subscribe_BoolValue2_attribute() {
        soft_subscribe_attribute(get_hla_class_name(), "BoolValue2");
    }

    /**
    * Soft unsubscribes a federate from the "BoolValue2" attribute of the attribute's
    * containing object class.
    * Note:  This method only marks the "BoolValue2" attribute for soft unsubscription.
    * To actually soft unsubscribe to the attribute, the federate must (re)subscribe to its
    * containing object class.
    * (using <objectClassName>.unsubscribe_object( RTIambassador rti ) ).
    */
    public static void soft_unsubscribe_BoolValue2_attribute() {
        soft_unsubscribe_attribute(get_hla_class_name(), "BoolValue2");
    }

    /**
    * Publishes the "ByteValue" attribute of the attribute's containing object
    * class for a federate.
    * Note:  This method only marks the "ByteValue" attribute for publication.
    * To actually publish the attribute, the federate must (re)publish its containing
    * object class.
    * (using <objectClassName>.publish_object( RTIambassador rti ) ).
    */
    public static void publish_ByteValue_attribute() {
        publish_attribute(get_hla_class_name(), "ByteValue");
    }

    /**
    * Unpublishes the "ByteValue" attribute of the attribute's containing object
    * class for a federate.
    * Note:  This method only marks the "ByteValue" attribute for unpublication.
    * To actually publish the attribute, the federate must (re)publish its containing
    * object class.
    * (using <objectClassName>.publish_object( RTIambassador rti ) ).
    */
    public static void unpublish_ByteValue_attribute() {
        unpublish_attribute(get_hla_class_name(), "ByteValue");
    }

    /**
    * Subscribes a federate to the "ByteValue" attribute of the attribute's
    * containing object class.
    * Note:  This method only marks the "ByteValue" attribute for subscription.
    * To actually subscribe to the attribute, the federate must (re)subscribe to its
    * containing object class.
    * (using <objectClassName>.subscribe_object( RTIambassador rti ) ).
    */
    public static void subscribe_ByteValue_attribute() {
        subscribe_attribute(get_hla_class_name(), "ByteValue");
    }

    /**
    * Unsubscribes a federate from the "ByteValue" attribute of the attribute's
    * containing object class.
    * Note:  This method only marks the "ByteValue" attribute for unsubscription.
    * To actually unsubscribe to the attribute, the federate must (re)subscribe to its
    * containing object class.
    * (using <objectClassName>.subscribe_object( RTIambassador rti ) ).
    */
    public static void unsubscribe_ByteValue_attribute() {
        unsubscribe_attribute(get_hla_class_name(), "ByteValue");
    }

    /**
    * Soft subscribes a federate to the "ByteValue" attribute of the attribute's
    * containing object class.
    * Note:  This method only marks the "ByteValue" attribute for soft subscription.
    * To actually soft subscribe to the attribute, the federate must (re)subscribe to its
    * containing object class.
    * (using <objectClassName>.soft_subscribe_object( RTIambassador rti ) ).
    */
    public static void soft_subscribe_ByteValue_attribute() {
        soft_subscribe_attribute(get_hla_class_name(), "ByteValue");
    }

    /**
    * Soft unsubscribes a federate from the "ByteValue" attribute of the attribute's
    * containing object class.
    * Note:  This method only marks the "ByteValue" attribute for soft unsubscription.
    * To actually soft unsubscribe to the attribute, the federate must (re)subscribe to its
    * containing object class.
    * (using <objectClassName>.unsubscribe_object( RTIambassador rti ) ).
    */
    public static void soft_unsubscribe_ByteValue_attribute() {
        soft_unsubscribe_attribute(get_hla_class_name(), "ByteValue");
    }

    /**
    * Publishes the "CharValue" attribute of the attribute's containing object
    * class for a federate.
    * Note:  This method only marks the "CharValue" attribute for publication.
    * To actually publish the attribute, the federate must (re)publish its containing
    * object class.
    * (using <objectClassName>.publish_object( RTIambassador rti ) ).
    */
    public static void publish_CharValue_attribute() {
        publish_attribute(get_hla_class_name(), "CharValue");
    }

    /**
    * Unpublishes the "CharValue" attribute of the attribute's containing object
    * class for a federate.
    * Note:  This method only marks the "CharValue" attribute for unpublication.
    * To actually publish the attribute, the federate must (re)publish its containing
    * object class.
    * (using <objectClassName>.publish_object( RTIambassador rti ) ).
    */
    public static void unpublish_CharValue_attribute() {
        unpublish_attribute(get_hla_class_name(), "CharValue");
    }

    /**
    * Subscribes a federate to the "CharValue" attribute of the attribute's
    * containing object class.
    * Note:  This method only marks the "CharValue" attribute for subscription.
    * To actually subscribe to the attribute, the federate must (re)subscribe to its
    * containing object class.
    * (using <objectClassName>.subscribe_object( RTIambassador rti ) ).
    */
    public static void subscribe_CharValue_attribute() {
        subscribe_attribute(get_hla_class_name(), "CharValue");
    }

    /**
    * Unsubscribes a federate from the "CharValue" attribute of the attribute's
    * containing object class.
    * Note:  This method only marks the "CharValue" attribute for unsubscription.
    * To actually unsubscribe to the attribute, the federate must (re)subscribe to its
    * containing object class.
    * (using <objectClassName>.subscribe_object( RTIambassador rti ) ).
    */
    public static void unsubscribe_CharValue_attribute() {
        unsubscribe_attribute(get_hla_class_name(), "CharValue");
    }

    /**
    * Soft subscribes a federate to the "CharValue" attribute of the attribute's
    * containing object class.
    * Note:  This method only marks the "CharValue" attribute for soft subscription.
    * To actually soft subscribe to the attribute, the federate must (re)subscribe to its
    * containing object class.
    * (using <objectClassName>.soft_subscribe_object( RTIambassador rti ) ).
    */
    public static void soft_subscribe_CharValue_attribute() {
        soft_subscribe_attribute(get_hla_class_name(), "CharValue");
    }

    /**
    * Soft unsubscribes a federate from the "CharValue" attribute of the attribute's
    * containing object class.
    * Note:  This method only marks the "CharValue" attribute for soft unsubscription.
    * To actually soft unsubscribe to the attribute, the federate must (re)subscribe to its
    * containing object class.
    * (using <objectClassName>.unsubscribe_object( RTIambassador rti ) ).
    */
    public static void soft_unsubscribe_CharValue_attribute() {
        soft_unsubscribe_attribute(get_hla_class_name(), "CharValue");
    }

    /**
    * Publishes the "DoubleValue" attribute of the attribute's containing object
    * class for a federate.
    * Note:  This method only marks the "DoubleValue" attribute for publication.
    * To actually publish the attribute, the federate must (re)publish its containing
    * object class.
    * (using <objectClassName>.publish_object( RTIambassador rti ) ).
    */
    public static void publish_DoubleValue_attribute() {
        publish_attribute(get_hla_class_name(), "DoubleValue");
    }

    /**
    * Unpublishes the "DoubleValue" attribute of the attribute's containing object
    * class for a federate.
    * Note:  This method only marks the "DoubleValue" attribute for unpublication.
    * To actually publish the attribute, the federate must (re)publish its containing
    * object class.
    * (using <objectClassName>.publish_object( RTIambassador rti ) ).
    */
    public static void unpublish_DoubleValue_attribute() {
        unpublish_attribute(get_hla_class_name(), "DoubleValue");
    }

    /**
    * Subscribes a federate to the "DoubleValue" attribute of the attribute's
    * containing object class.
    * Note:  This method only marks the "DoubleValue" attribute for subscription.
    * To actually subscribe to the attribute, the federate must (re)subscribe to its
    * containing object class.
    * (using <objectClassName>.subscribe_object( RTIambassador rti ) ).
    */
    public static void subscribe_DoubleValue_attribute() {
        subscribe_attribute(get_hla_class_name(), "DoubleValue");
    }

    /**
    * Unsubscribes a federate from the "DoubleValue" attribute of the attribute's
    * containing object class.
    * Note:  This method only marks the "DoubleValue" attribute for unsubscription.
    * To actually unsubscribe to the attribute, the federate must (re)subscribe to its
    * containing object class.
    * (using <objectClassName>.subscribe_object( RTIambassador rti ) ).
    */
    public static void unsubscribe_DoubleValue_attribute() {
        unsubscribe_attribute(get_hla_class_name(), "DoubleValue");
    }

    /**
    * Soft subscribes a federate to the "DoubleValue" attribute of the attribute's
    * containing object class.
    * Note:  This method only marks the "DoubleValue" attribute for soft subscription.
    * To actually soft subscribe to the attribute, the federate must (re)subscribe to its
    * containing object class.
    * (using <objectClassName>.soft_subscribe_object( RTIambassador rti ) ).
    */
    public static void soft_subscribe_DoubleValue_attribute() {
        soft_subscribe_attribute(get_hla_class_name(), "DoubleValue");
    }

    /**
    * Soft unsubscribes a federate from the "DoubleValue" attribute of the attribute's
    * containing object class.
    * Note:  This method only marks the "DoubleValue" attribute for soft unsubscription.
    * To actually soft unsubscribe to the attribute, the federate must (re)subscribe to its
    * containing object class.
    * (using <objectClassName>.unsubscribe_object( RTIambassador rti ) ).
    */
    public static void soft_unsubscribe_DoubleValue_attribute() {
        soft_unsubscribe_attribute(get_hla_class_name(), "DoubleValue");
    }

    /**
    * Publishes the "FloatValue" attribute of the attribute's containing object
    * class for a federate.
    * Note:  This method only marks the "FloatValue" attribute for publication.
    * To actually publish the attribute, the federate must (re)publish its containing
    * object class.
    * (using <objectClassName>.publish_object( RTIambassador rti ) ).
    */
    public static void publish_FloatValue_attribute() {
        publish_attribute(get_hla_class_name(), "FloatValue");
    }

    /**
    * Unpublishes the "FloatValue" attribute of the attribute's containing object
    * class for a federate.
    * Note:  This method only marks the "FloatValue" attribute for unpublication.
    * To actually publish the attribute, the federate must (re)publish its containing
    * object class.
    * (using <objectClassName>.publish_object( RTIambassador rti ) ).
    */
    public static void unpublish_FloatValue_attribute() {
        unpublish_attribute(get_hla_class_name(), "FloatValue");
    }

    /**
    * Subscribes a federate to the "FloatValue" attribute of the attribute's
    * containing object class.
    * Note:  This method only marks the "FloatValue" attribute for subscription.
    * To actually subscribe to the attribute, the federate must (re)subscribe to its
    * containing object class.
    * (using <objectClassName>.subscribe_object( RTIambassador rti ) ).
    */
    public static void subscribe_FloatValue_attribute() {
        subscribe_attribute(get_hla_class_name(), "FloatValue");
    }

    /**
    * Unsubscribes a federate from the "FloatValue" attribute of the attribute's
    * containing object class.
    * Note:  This method only marks the "FloatValue" attribute for unsubscription.
    * To actually unsubscribe to the attribute, the federate must (re)subscribe to its
    * containing object class.
    * (using <objectClassName>.subscribe_object( RTIambassador rti ) ).
    */
    public static void unsubscribe_FloatValue_attribute() {
        unsubscribe_attribute(get_hla_class_name(), "FloatValue");
    }

    /**
    * Soft subscribes a federate to the "FloatValue" attribute of the attribute's
    * containing object class.
    * Note:  This method only marks the "FloatValue" attribute for soft subscription.
    * To actually soft subscribe to the attribute, the federate must (re)subscribe to its
    * containing object class.
    * (using <objectClassName>.soft_subscribe_object( RTIambassador rti ) ).
    */
    public static void soft_subscribe_FloatValue_attribute() {
        soft_subscribe_attribute(get_hla_class_name(), "FloatValue");
    }

    /**
    * Soft unsubscribes a federate from the "FloatValue" attribute of the attribute's
    * containing object class.
    * Note:  This method only marks the "FloatValue" attribute for soft unsubscription.
    * To actually soft unsubscribe to the attribute, the federate must (re)subscribe to its
    * containing object class.
    * (using <objectClassName>.unsubscribe_object( RTIambassador rti ) ).
    */
    public static void soft_unsubscribe_FloatValue_attribute() {
        soft_unsubscribe_attribute(get_hla_class_name(), "FloatValue");
    }

    /**
    * Publishes the "IntValue" attribute of the attribute's containing object
    * class for a federate.
    * Note:  This method only marks the "IntValue" attribute for publication.
    * To actually publish the attribute, the federate must (re)publish its containing
    * object class.
    * (using <objectClassName>.publish_object( RTIambassador rti ) ).
    */
    public static void publish_IntValue_attribute() {
        publish_attribute(get_hla_class_name(), "IntValue");
    }

    /**
    * Unpublishes the "IntValue" attribute of the attribute's containing object
    * class for a federate.
    * Note:  This method only marks the "IntValue" attribute for unpublication.
    * To actually publish the attribute, the federate must (re)publish its containing
    * object class.
    * (using <objectClassName>.publish_object( RTIambassador rti ) ).
    */
    public static void unpublish_IntValue_attribute() {
        unpublish_attribute(get_hla_class_name(), "IntValue");
    }

    /**
    * Subscribes a federate to the "IntValue" attribute of the attribute's
    * containing object class.
    * Note:  This method only marks the "IntValue" attribute for subscription.
    * To actually subscribe to the attribute, the federate must (re)subscribe to its
    * containing object class.
    * (using <objectClassName>.subscribe_object( RTIambassador rti ) ).
    */
    public static void subscribe_IntValue_attribute() {
        subscribe_attribute(get_hla_class_name(), "IntValue");
    }

    /**
    * Unsubscribes a federate from the "IntValue" attribute of the attribute's
    * containing object class.
    * Note:  This method only marks the "IntValue" attribute for unsubscription.
    * To actually unsubscribe to the attribute, the federate must (re)subscribe to its
    * containing object class.
    * (using <objectClassName>.subscribe_object( RTIambassador rti ) ).
    */
    public static void unsubscribe_IntValue_attribute() {
        unsubscribe_attribute(get_hla_class_name(), "IntValue");
    }

    /**
    * Soft subscribes a federate to the "IntValue" attribute of the attribute's
    * containing object class.
    * Note:  This method only marks the "IntValue" attribute for soft subscription.
    * To actually soft subscribe to the attribute, the federate must (re)subscribe to its
    * containing object class.
    * (using <objectClassName>.soft_subscribe_object( RTIambassador rti ) ).
    */
    public static void soft_subscribe_IntValue_attribute() {
        soft_subscribe_attribute(get_hla_class_name(), "IntValue");
    }

    /**
    * Soft unsubscribes a federate from the "IntValue" attribute of the attribute's
    * containing object class.
    * Note:  This method only marks the "IntValue" attribute for soft unsubscription.
    * To actually soft unsubscribe to the attribute, the federate must (re)subscribe to its
    * containing object class.
    * (using <objectClassName>.unsubscribe_object( RTIambassador rti ) ).
    */
    public static void soft_unsubscribe_IntValue_attribute() {
        soft_unsubscribe_attribute(get_hla_class_name(), "IntValue");
    }

    /**
    * Publishes the "JSONValue1" attribute of the attribute's containing object
    * class for a federate.
    * Note:  This method only marks the "JSONValue1" attribute for publication.
    * To actually publish the attribute, the federate must (re)publish its containing
    * object class.
    * (using <objectClassName>.publish_object( RTIambassador rti ) ).
    */
    public static void publish_JSONValue1_attribute() {
        publish_attribute(get_hla_class_name(), "JSONValue1");
    }

    /**
    * Unpublishes the "JSONValue1" attribute of the attribute's containing object
    * class for a federate.
    * Note:  This method only marks the "JSONValue1" attribute for unpublication.
    * To actually publish the attribute, the federate must (re)publish its containing
    * object class.
    * (using <objectClassName>.publish_object( RTIambassador rti ) ).
    */
    public static void unpublish_JSONValue1_attribute() {
        unpublish_attribute(get_hla_class_name(), "JSONValue1");
    }

    /**
    * Subscribes a federate to the "JSONValue1" attribute of the attribute's
    * containing object class.
    * Note:  This method only marks the "JSONValue1" attribute for subscription.
    * To actually subscribe to the attribute, the federate must (re)subscribe to its
    * containing object class.
    * (using <objectClassName>.subscribe_object( RTIambassador rti ) ).
    */
    public static void subscribe_JSONValue1_attribute() {
        subscribe_attribute(get_hla_class_name(), "JSONValue1");
    }

    /**
    * Unsubscribes a federate from the "JSONValue1" attribute of the attribute's
    * containing object class.
    * Note:  This method only marks the "JSONValue1" attribute for unsubscription.
    * To actually unsubscribe to the attribute, the federate must (re)subscribe to its
    * containing object class.
    * (using <objectClassName>.subscribe_object( RTIambassador rti ) ).
    */
    public static void unsubscribe_JSONValue1_attribute() {
        unsubscribe_attribute(get_hla_class_name(), "JSONValue1");
    }

    /**
    * Soft subscribes a federate to the "JSONValue1" attribute of the attribute's
    * containing object class.
    * Note:  This method only marks the "JSONValue1" attribute for soft subscription.
    * To actually soft subscribe to the attribute, the federate must (re)subscribe to its
    * containing object class.
    * (using <objectClassName>.soft_subscribe_object( RTIambassador rti ) ).
    */
    public static void soft_subscribe_JSONValue1_attribute() {
        soft_subscribe_attribute(get_hla_class_name(), "JSONValue1");
    }

    /**
    * Soft unsubscribes a federate from the "JSONValue1" attribute of the attribute's
    * containing object class.
    * Note:  This method only marks the "JSONValue1" attribute for soft unsubscription.
    * To actually soft unsubscribe to the attribute, the federate must (re)subscribe to its
    * containing object class.
    * (using <objectClassName>.unsubscribe_object( RTIambassador rti ) ).
    */
    public static void soft_unsubscribe_JSONValue1_attribute() {
        soft_unsubscribe_attribute(get_hla_class_name(), "JSONValue1");
    }

    /**
    * Publishes the "JSONValue2" attribute of the attribute's containing object
    * class for a federate.
    * Note:  This method only marks the "JSONValue2" attribute for publication.
    * To actually publish the attribute, the federate must (re)publish its containing
    * object class.
    * (using <objectClassName>.publish_object( RTIambassador rti ) ).
    */
    public static void publish_JSONValue2_attribute() {
        publish_attribute(get_hla_class_name(), "JSONValue2");
    }

    /**
    * Unpublishes the "JSONValue2" attribute of the attribute's containing object
    * class for a federate.
    * Note:  This method only marks the "JSONValue2" attribute for unpublication.
    * To actually publish the attribute, the federate must (re)publish its containing
    * object class.
    * (using <objectClassName>.publish_object( RTIambassador rti ) ).
    */
    public static void unpublish_JSONValue2_attribute() {
        unpublish_attribute(get_hla_class_name(), "JSONValue2");
    }

    /**
    * Subscribes a federate to the "JSONValue2" attribute of the attribute's
    * containing object class.
    * Note:  This method only marks the "JSONValue2" attribute for subscription.
    * To actually subscribe to the attribute, the federate must (re)subscribe to its
    * containing object class.
    * (using <objectClassName>.subscribe_object( RTIambassador rti ) ).
    */
    public static void subscribe_JSONValue2_attribute() {
        subscribe_attribute(get_hla_class_name(), "JSONValue2");
    }

    /**
    * Unsubscribes a federate from the "JSONValue2" attribute of the attribute's
    * containing object class.
    * Note:  This method only marks the "JSONValue2" attribute for unsubscription.
    * To actually unsubscribe to the attribute, the federate must (re)subscribe to its
    * containing object class.
    * (using <objectClassName>.subscribe_object( RTIambassador rti ) ).
    */
    public static void unsubscribe_JSONValue2_attribute() {
        unsubscribe_attribute(get_hla_class_name(), "JSONValue2");
    }

    /**
    * Soft subscribes a federate to the "JSONValue2" attribute of the attribute's
    * containing object class.
    * Note:  This method only marks the "JSONValue2" attribute for soft subscription.
    * To actually soft subscribe to the attribute, the federate must (re)subscribe to its
    * containing object class.
    * (using <objectClassName>.soft_subscribe_object( RTIambassador rti ) ).
    */
    public static void soft_subscribe_JSONValue2_attribute() {
        soft_subscribe_attribute(get_hla_class_name(), "JSONValue2");
    }

    /**
    * Soft unsubscribes a federate from the "JSONValue2" attribute of the attribute's
    * containing object class.
    * Note:  This method only marks the "JSONValue2" attribute for soft unsubscription.
    * To actually soft unsubscribe to the attribute, the federate must (re)subscribe to its
    * containing object class.
    * (using <objectClassName>.unsubscribe_object( RTIambassador rti ) ).
    */
    public static void soft_unsubscribe_JSONValue2_attribute() {
        soft_unsubscribe_attribute(get_hla_class_name(), "JSONValue2");
    }

    /**
    * Publishes the "LongValue" attribute of the attribute's containing object
    * class for a federate.
    * Note:  This method only marks the "LongValue" attribute for publication.
    * To actually publish the attribute, the federate must (re)publish its containing
    * object class.
    * (using <objectClassName>.publish_object( RTIambassador rti ) ).
    */
    public static void publish_LongValue_attribute() {
        publish_attribute(get_hla_class_name(), "LongValue");
    }

    /**
    * Unpublishes the "LongValue" attribute of the attribute's containing object
    * class for a federate.
    * Note:  This method only marks the "LongValue" attribute for unpublication.
    * To actually publish the attribute, the federate must (re)publish its containing
    * object class.
    * (using <objectClassName>.publish_object( RTIambassador rti ) ).
    */
    public static void unpublish_LongValue_attribute() {
        unpublish_attribute(get_hla_class_name(), "LongValue");
    }

    /**
    * Subscribes a federate to the "LongValue" attribute of the attribute's
    * containing object class.
    * Note:  This method only marks the "LongValue" attribute for subscription.
    * To actually subscribe to the attribute, the federate must (re)subscribe to its
    * containing object class.
    * (using <objectClassName>.subscribe_object( RTIambassador rti ) ).
    */
    public static void subscribe_LongValue_attribute() {
        subscribe_attribute(get_hla_class_name(), "LongValue");
    }

    /**
    * Unsubscribes a federate from the "LongValue" attribute of the attribute's
    * containing object class.
    * Note:  This method only marks the "LongValue" attribute for unsubscription.
    * To actually unsubscribe to the attribute, the federate must (re)subscribe to its
    * containing object class.
    * (using <objectClassName>.subscribe_object( RTIambassador rti ) ).
    */
    public static void unsubscribe_LongValue_attribute() {
        unsubscribe_attribute(get_hla_class_name(), "LongValue");
    }

    /**
    * Soft subscribes a federate to the "LongValue" attribute of the attribute's
    * containing object class.
    * Note:  This method only marks the "LongValue" attribute for soft subscription.
    * To actually soft subscribe to the attribute, the federate must (re)subscribe to its
    * containing object class.
    * (using <objectClassName>.soft_subscribe_object( RTIambassador rti ) ).
    */
    public static void soft_subscribe_LongValue_attribute() {
        soft_subscribe_attribute(get_hla_class_name(), "LongValue");
    }

    /**
    * Soft unsubscribes a federate from the "LongValue" attribute of the attribute's
    * containing object class.
    * Note:  This method only marks the "LongValue" attribute for soft unsubscription.
    * To actually soft unsubscribe to the attribute, the federate must (re)subscribe to its
    * containing object class.
    * (using <objectClassName>.unsubscribe_object( RTIambassador rti ) ).
    */
    public static void soft_unsubscribe_LongValue_attribute() {
        soft_unsubscribe_attribute(get_hla_class_name(), "LongValue");
    }

    /**
    * Publishes the "ShortValue" attribute of the attribute's containing object
    * class for a federate.
    * Note:  This method only marks the "ShortValue" attribute for publication.
    * To actually publish the attribute, the federate must (re)publish its containing
    * object class.
    * (using <objectClassName>.publish_object( RTIambassador rti ) ).
    */
    public static void publish_ShortValue_attribute() {
        publish_attribute(get_hla_class_name(), "ShortValue");
    }

    /**
    * Unpublishes the "ShortValue" attribute of the attribute's containing object
    * class for a federate.
    * Note:  This method only marks the "ShortValue" attribute for unpublication.
    * To actually publish the attribute, the federate must (re)publish its containing
    * object class.
    * (using <objectClassName>.publish_object( RTIambassador rti ) ).
    */
    public static void unpublish_ShortValue_attribute() {
        unpublish_attribute(get_hla_class_name(), "ShortValue");
    }

    /**
    * Subscribes a federate to the "ShortValue" attribute of the attribute's
    * containing object class.
    * Note:  This method only marks the "ShortValue" attribute for subscription.
    * To actually subscribe to the attribute, the federate must (re)subscribe to its
    * containing object class.
    * (using <objectClassName>.subscribe_object( RTIambassador rti ) ).
    */
    public static void subscribe_ShortValue_attribute() {
        subscribe_attribute(get_hla_class_name(), "ShortValue");
    }

    /**
    * Unsubscribes a federate from the "ShortValue" attribute of the attribute's
    * containing object class.
    * Note:  This method only marks the "ShortValue" attribute for unsubscription.
    * To actually unsubscribe to the attribute, the federate must (re)subscribe to its
    * containing object class.
    * (using <objectClassName>.subscribe_object( RTIambassador rti ) ).
    */
    public static void unsubscribe_ShortValue_attribute() {
        unsubscribe_attribute(get_hla_class_name(), "ShortValue");
    }

    /**
    * Soft subscribes a federate to the "ShortValue" attribute of the attribute's
    * containing object class.
    * Note:  This method only marks the "ShortValue" attribute for soft subscription.
    * To actually soft subscribe to the attribute, the federate must (re)subscribe to its
    * containing object class.
    * (using <objectClassName>.soft_subscribe_object( RTIambassador rti ) ).
    */
    public static void soft_subscribe_ShortValue_attribute() {
        soft_subscribe_attribute(get_hla_class_name(), "ShortValue");
    }

    /**
    * Soft unsubscribes a federate from the "ShortValue" attribute of the attribute's
    * containing object class.
    * Note:  This method only marks the "ShortValue" attribute for soft unsubscription.
    * To actually soft unsubscribe to the attribute, the federate must (re)subscribe to its
    * containing object class.
    * (using <objectClassName>.unsubscribe_object( RTIambassador rti ) ).
    */
    public static void soft_unsubscribe_ShortValue_attribute() {
        soft_unsubscribe_attribute(get_hla_class_name(), "ShortValue");
    }

    /**
    * Publishes the "StringValue" attribute of the attribute's containing object
    * class for a federate.
    * Note:  This method only marks the "StringValue" attribute for publication.
    * To actually publish the attribute, the federate must (re)publish its containing
    * object class.
    * (using <objectClassName>.publish_object( RTIambassador rti ) ).
    */
    public static void publish_StringValue_attribute() {
        publish_attribute(get_hla_class_name(), "StringValue");
    }

    /**
    * Unpublishes the "StringValue" attribute of the attribute's containing object
    * class for a federate.
    * Note:  This method only marks the "StringValue" attribute for unpublication.
    * To actually publish the attribute, the federate must (re)publish its containing
    * object class.
    * (using <objectClassName>.publish_object( RTIambassador rti ) ).
    */
    public static void unpublish_StringValue_attribute() {
        unpublish_attribute(get_hla_class_name(), "StringValue");
    }

    /**
    * Subscribes a federate to the "StringValue" attribute of the attribute's
    * containing object class.
    * Note:  This method only marks the "StringValue" attribute for subscription.
    * To actually subscribe to the attribute, the federate must (re)subscribe to its
    * containing object class.
    * (using <objectClassName>.subscribe_object( RTIambassador rti ) ).
    */
    public static void subscribe_StringValue_attribute() {
        subscribe_attribute(get_hla_class_name(), "StringValue");
    }

    /**
    * Unsubscribes a federate from the "StringValue" attribute of the attribute's
    * containing object class.
    * Note:  This method only marks the "StringValue" attribute for unsubscription.
    * To actually unsubscribe to the attribute, the federate must (re)subscribe to its
    * containing object class.
    * (using <objectClassName>.subscribe_object( RTIambassador rti ) ).
    */
    public static void unsubscribe_StringValue_attribute() {
        unsubscribe_attribute(get_hla_class_name(), "StringValue");
    }

    /**
    * Soft subscribes a federate to the "StringValue" attribute of the attribute's
    * containing object class.
    * Note:  This method only marks the "StringValue" attribute for soft subscription.
    * To actually soft subscribe to the attribute, the federate must (re)subscribe to its
    * containing object class.
    * (using <objectClassName>.soft_subscribe_object( RTIambassador rti ) ).
    */
    public static void soft_subscribe_StringValue_attribute() {
        soft_subscribe_attribute(get_hla_class_name(), "StringValue");
    }

    /**
    * Soft unsubscribes a federate from the "StringValue" attribute of the attribute's
    * containing object class.
    * Note:  This method only marks the "StringValue" attribute for soft unsubscription.
    * To actually soft unsubscribe to the attribute, the federate must (re)subscribe to its
    * containing object class.
    * (using <objectClassName>.unsubscribe_object( RTIambassador rti ) ).
    */
    public static void soft_unsubscribe_StringValue_attribute() {
        soft_unsubscribe_attribute(get_hla_class_name(), "StringValue");
    }

    protected TestObject(NoInstanceInit noInstanceInit) {
        super(noInstanceInit);
    }

    /**
    * Creates an instance of the TestObject object class, using
    * "datamemberMap" to initialize its attribute values.
    * "datamemberMap" is usually acquired as an argument to an RTI federate
    * callback method, such as "receiveInteraction".
    *
    * @param datamemberMap data structure containing initial values for the
    * attributes of this new TestObject object class instance
    */
    protected TestObject( String hlaClassName ) {
        super( hlaClassName );
    }

    /**
    * Creates a new TestObject object class instance that is a duplicate
    * of the instance referred to by messaging_var.
    *
    * @param messaging_var TestObject object class instance of which
    * this newly created TestObject object class instance will be a
    * duplicate
    */
    public TestObject(TestObject messaging_var) {
    
        // SHALLOW COPY
        classAndPropertyNameValueMap = new HashMap<>(messaging_var.classAndPropertyNameValueMap);

        // DEEP(ER) COPY FOR OBJECTS
        for(ClassAndPropertyName key: classAndPropertyNameValueMap.keySet()) {
            classAndPropertyNameValueMap.put(key, new Attribute<>((Attribute<Object>)classAndPropertyNameValueMap.get(key)));
        }

    }
}
