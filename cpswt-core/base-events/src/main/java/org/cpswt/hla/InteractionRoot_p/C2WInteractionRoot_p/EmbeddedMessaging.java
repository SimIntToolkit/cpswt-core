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

package org.cpswt.hla.InteractionRoot_p.C2WInteractionRoot_p;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import hla.rti.LogicalTime;
import hla.rti.RTIambassador;
import hla.rti.ReceivedInteraction;


/**
 * Implements org.cpswt.hla.InteractionRoot_p.C2WInteractionRoot_p.EmbeddedMessaging
 */
@SuppressWarnings("unused")
public class EmbeddedMessaging extends org.cpswt.hla.InteractionRoot_p.C2WInteractionRoot {

    private static final Logger logger = LogManager.getLogger();

    // DUMMY STATIC METHOD TO ALLOW ACTIVE LOADING OF CLASS
    public static void load() { }

    // ----------------------------------------------------------------------------
    // STATIC PROPERTYS AND CODE THAT DEAL WITH NAMES
    // THIS CODE IS STATIC BECAUSE IT IS CLASS-DEPENDENT AND NOT INSTANCE-DEPENDENT
    // ----------------------------------------------------------------------------

    /**
     * Returns the fully-qualified (dot-delimited) name of the org.cpswt.hla.InteractionRoot_p.C2WInteractionRoot_p.EmbeddedMessaging interaction class.
     * Note: As this is a static method, it is NOT polymorphic, and so, if called on
     * a reference will return the name of the class pertaining to the reference,
     * rather than the name of the class for the instance referred to by the reference.
     * For the polymorphic version of this method, use {@link #getJavaClassName()}.
     *
     * @return the fully-qualified Java class name for this interaction class
     */
    public static String get_java_class_name() {
        return "org.cpswt.hla.InteractionRoot_p.C2WInteractionRoot_p.EmbeddedMessaging";
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
     * class name) of the org.cpswt.hla.InteractionRoot_p.C2WInteractionRoot_p.EmbeddedMessaging interaction class.
     *
     * @return the name of this interaction class
     */
    public static String get_simple_class_name() {
        return get_simple_class_name(get_hla_class_name());
    }

    /**
     * Returns the fully-qualified (dot-delimited) hla class name of the
     * InteractionRoot.C2WInteractionRoot.EmbeddedMessaging interaction class.
     * Note: As this is a static method, it is NOT polymorphic, and so, if called on
     * a reference will return the federation name of the class pertaining to the reference,
     * rather than the name of the class for the instance referred to by the reference.
     * For the polymorphic version of this method, use {@link #getHlaClassName()}.
     *
     * @return the fully-qualified federation (HLA) class name for this interaction class
     */
    public static String get_hla_class_name() {
        return "InteractionRoot.C2WInteractionRoot.EmbeddedMessaging";
    }

    /**
     * Returns the fully-qualified (dot-delimited) hla class name of this instance's interaction class.
     * Polymorphic equivalent of get_hla_class_name static method.
     *
     * @return the fully-qualified (dot-delimited) name of this instance's interaction class
     */
    public String getHlaClassName() {
        return get_hla_class_name();
    }

    /**
     * Returns a sorted list containing the names of all of the non-hidden parameters in the
     * org.cpswt.hla.InteractionRoot_p.C2WInteractionRoot_p.EmbeddedMessaging interaction class.
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
     * Returns a sorted list containing the names of all of the parameters in the
     * org.cpswt.hla.InteractionRoot_p.C2WInteractionRoot_p.EmbeddedMessaging interaction class.
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

    /*
     * INITIALIZE STATIC PROPERTIES THAT DEAL WITH NAMES
     */
    static {
        _hlaClassNameSet.add(get_hla_class_name());

        EmbeddedMessaging instance = new EmbeddedMessaging(noInstanceInit);
        _hlaClassNameInstanceMap.put(get_hla_class_name(), instance);

        Set<ClassAndPropertyName> classAndPropertyNameSet = new HashSet<>();
        classAndPropertyNameSet.add(new ClassAndPropertyName(
            "InteractionRoot.C2WInteractionRoot.EmbeddedMessaging", "hlaClassName"
        ));
        classAndPropertyNameSet.add(new ClassAndPropertyName(
            "InteractionRoot.C2WInteractionRoot.EmbeddedMessaging", "messagingJson"
        ));

        // ADD THIS CLASS'S classAndPropertyNameSet TO _classNamePropertyNameSetMap DEFINED
        // IN InteractionRoot
        _classNamePropertyNameSetMap.put(get_hla_class_name(), classAndPropertyNameSet);


        Set<ClassAndPropertyName> allClassAndPropertyNameSet = new HashSet<>();

        allClassAndPropertyNameSet.add(new ClassAndPropertyName(
            "InteractionRoot.C2WInteractionRoot.EmbeddedMessaging", "hlaClassName"
        ));

        allClassAndPropertyNameSet.add(new ClassAndPropertyName(
            "InteractionRoot.C2WInteractionRoot.EmbeddedMessaging", "messagingJson"
        ));

        allClassAndPropertyNameSet.add(new ClassAndPropertyName(
            "InteractionRoot.C2WInteractionRoot", "actualLogicalGenerationTime"
        ));

        allClassAndPropertyNameSet.add(new ClassAndPropertyName(
            "InteractionRoot.C2WInteractionRoot", "federateFilter"
        ));

        allClassAndPropertyNameSet.add(new ClassAndPropertyName(
            "InteractionRoot.C2WInteractionRoot", "federateSequence"
        ));


        // ADD THIS CLASS'S _allClassAndPropertyNameSet TO _allClassNamePropertyNameSetMap DEFINED
        // IN InteractionRoot
        _allClassNamePropertyNameSetMap.put(get_hla_class_name(), allClassAndPropertyNameSet);

        ClassAndPropertyName key;

        key = new ClassAndPropertyName(get_hla_class_name(), "hlaClassName");
        _classAndPropertyNameInitialValueMap.put(key, "");

        key = new ClassAndPropertyName(get_hla_class_name(), "messagingJson");
        _classAndPropertyNameInitialValueMap.put(key, "");

        logger.info(
          "Class \"org.cpswt.hla.InteractionRoot_p.C2WInteractionRoot_p.EmbeddedMessaging\" (hla class \"{}\") loaded", get_hla_class_name()
        );

        System.err.println(
          "Class \"org.cpswt.hla.InteractionRoot_p.C2WInteractionRoot_p.EmbeddedMessaging\" (hla class \"" +
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
     * Returns the handle (RTI assigned) of the org.cpswt.hla.InteractionRoot_p.C2WInteractionRoot_p.EmbeddedMessaging interaction class.
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
     * Returns the handle of an parameter (RTI assigned) of
     * this interaction class (i.e. "org.cpswt.hla.InteractionRoot_p.C2WInteractionRoot_p.EmbeddedMessaging") given the parameter's name.
     *
     * @param propertyName name of parameter
     * @return the handle (RTI assigned) of the parameter "propertyName" of interaction class "className"
     */
    public static int get_parameter_handle(String propertyName) {
        return get_parameter_handle(get_hla_class_name(), propertyName);
    }

    // ----------------------------------------------------------
    // END OF STATIC PROPERTYS AND CODE THAT DEAL WITH HANDLES.
    // ----------------------------------------------------------


    //-------------------------------------------------
    // METHODS FOR PUBLISHING/SUBSCRIBING-TO THIS CLASS
    //-------------------------------------------------

    /**
     * Publishes the org.cpswt.hla.InteractionRoot_p.C2WInteractionRoot_p.EmbeddedMessaging interaction class for a federate.
     *
     * @param rti handle to the Local RTI Component
     */
    public static void publish_interaction(RTIambassador rti) {
        publish_interaction(get_hla_class_name(), rti);
    }

    public static Boolean get_is_published() {
        return get_is_published(get_hla_class_name());
    }

    /**
     * Unpublishes the org.cpswt.hla.InteractionRoot_p.C2WInteractionRoot_p.EmbeddedMessaging interaction class for a federate.
     *
     * @param rti handle to the Local RTI Component, usu. obtained through the
     *            {@link SynchronizedFederate#getLRC()} call
     */
    public static void unpublish_interaction(RTIambassador rti) {
        unpublish_interaction(get_hla_class_name(), rti);
    }

    /**
     * Subscribes a federate to the org.cpswt.hla.InteractionRoot_p.C2WInteractionRoot_p.EmbeddedMessaging interaction class.
     *
     * @param rti handle to the Local RTI Component
     */
    public static void subscribe_interaction(RTIambassador rti) {
        subscribe_interaction(get_hla_class_name(), rti);
    }

    public static Boolean get_is_subscribed() {
        return get_is_subscribed(get_hla_class_name());
    }

    public static void soft_subscribe_interaction(RTIambassador rti) {
        soft_subscribe_interaction(get_hla_class_name(), rti);
    }

    public static Boolean get_is_soft_subscribed() {
        return get_is_soft_subscribed(get_hla_class_name());
    }

    /**
     * Unsubscribes a federate from the org.cpswt.hla.InteractionRoot_p.C2WInteractionRoot_p.EmbeddedMessaging interaction class.
     *
     * @param rti handle to the Local RTI Component
     */
    public static void unsubscribe_interaction(RTIambassador rti) {
        unsubscribe_interaction(get_hla_class_name(), rti);
    }

    public static void soft_unsubscribe_interaction(RTIambassador rti) {
        soft_unsubscribe_interaction(get_hla_class_name(), rti);
    }

    public static void add_federate_name_soft_publish(String networkFederateName) {
        add_federate_name_soft_publish(get_hla_class_name(), networkFederateName);
    }

    public static void remove_federate_name_soft_publish(String networkFederateName) {
        remove_federate_name_soft_publish(get_hla_class_name(), networkFederateName);
    }

    public Set<String> getFederateNameSoftPublishSet() {
        return get_federate_name_soft_publish_set(get_hla_class_name());
    }

    //-----------------------------------------------------
    // END METHODS FOR PUBLISHING/SUBSCRIBING-TO THIS CLASS
    //-----------------------------------------------------

    /**
     * Return true if "handle" is equal to the handle (RTI assigned) of this class
     * (that is, the org.cpswt.hla.InteractionRoot_p.C2WInteractionRoot_p.EmbeddedMessaging interaction class).
     *
     * @param handle handle to compare to the value of the handle (RTI assigned) of
     * this class (the org.cpswt.hla.InteractionRoot_p.C2WInteractionRoot_p.EmbeddedMessaging interaction class).
     * @return "true" if "handle" matches the value of the handle of this class
     * (that is, the org.cpswt.hla.InteractionRoot_p.C2WInteractionRoot_p.EmbeddedMessaging interaction class).
     */
    public static boolean match(int handle) {
        return handle == get_class_handle();
    }

    //-------------
    // CONSTRUCTORS
    //-------------
    public EmbeddedMessaging() {
        this(get_hla_class_name());
    }

    public EmbeddedMessaging(LogicalTime logicalTime) {
        this();
        setTime(logicalTime);
    }

    public EmbeddedMessaging(ReceivedInteraction propertyMap) {
        this();
        setParameters( propertyMap );
    }

    public EmbeddedMessaging(ReceivedInteraction propertyMap, LogicalTime logicalTime) {
        this(propertyMap);
        setTime(logicalTime);
    }

    //-----------------
    // END CONSTRUCTORS
    //-----------------


    //-----------------
    // CREATION METHODS
    //-----------------
    public static EmbeddedMessaging create_interaction() {
        return new EmbeddedMessaging();
    }

    public EmbeddedMessaging createInteraction() {
        return create_interaction();
    }

    public static EmbeddedMessaging create_interaction(LogicalTime logicalTime) {
        return new EmbeddedMessaging(logicalTime);
    }

    public EmbeddedMessaging createInteraction(LogicalTime logicalTime) {
        return create_interaction(logicalTime);
    }

    public static EmbeddedMessaging create_interaction(ReceivedInteraction propertyMap) {
        return new EmbeddedMessaging(propertyMap);
    }

    public EmbeddedMessaging createInteraction(ReceivedInteraction propertyMap) {
        return create_interaction(propertyMap);
    }

    public static EmbeddedMessaging create_interaction(ReceivedInteraction propertyMap, LogicalTime logicalTime) {
        return new EmbeddedMessaging(propertyMap, logicalTime);
    }

    public EmbeddedMessaging createInteraction(ReceivedInteraction propertyMap, LogicalTime logicalTime) {
        return create_interaction(propertyMap, logicalTime);
    }

    //---------------------
    // END CREATION METHODS
    //---------------------

    public static int get_num_parameters() {
        return _allClassNamePropertyNameSetMap.get(get_hla_class_name()).size();
    }


    //------------------------------
    // PROPERTY MANIPULATION METHODS
    //------------------------------


    /**
     * Set the value of the "hlaClassName" parameter to "value" for this parameter.
     *
     * @param value the new value for the "hlaClassName" parameter
     */
    public void set_hlaClassName(String value) {
        ClassAndPropertyName key = new ClassAndPropertyName(get_hla_class_name(), "hlaClassName");
        classAndPropertyNameValueMap.put(key, value);
    }

    /**
     * Returns the value of the "hlaClassName" parameter of this interaction.
     *
     * @return the value of the "hlaClassName" parameter
     */
    public String get_hlaClassName() {
        ClassAndPropertyName key = new ClassAndPropertyName(get_hla_class_name(), "hlaClassName");
        return (String)classAndPropertyNameValueMap.get(key);
    }


    /**
     * Set the value of the "messagingJson" parameter to "value" for this parameter.
     *
     * @param value the new value for the "messagingJson" parameter
     */
    public void set_messagingJson(String value) {
        ClassAndPropertyName key = new ClassAndPropertyName(get_hla_class_name(), "messagingJson");
        classAndPropertyNameValueMap.put(key, value);
    }

    /**
     * Returns the value of the "messagingJson" parameter of this interaction.
     *
     * @return the value of the "messagingJson" parameter
     */
    public String get_messagingJson() {
        ClassAndPropertyName key = new ClassAndPropertyName(get_hla_class_name(), "messagingJson");
        return (String)classAndPropertyNameValueMap.get(key);
    }

    //----------------------------------
    // END PROPERTY MANIPULATION METHODS
    //----------------------------------

    public static void add_reject_source_federate_id(String federateId) {
        add_reject_source_federate_id(get_hla_class_name(), federateId);
    }

    public static boolean is_reject_source_federate_id(String federateId) {
        return is_reject_source_federate_id(get_hla_class_name(), federateId);
    }

    public static void remove_reject_source_federate_id(String federateId) {
        remove_reject_source_federate_id(get_hla_class_name(), federateId);
    }

    protected EmbeddedMessaging(NoInstanceInit noInstanceInit) {
        super(noInstanceInit);
    }

    /**
    * Creates an instance of the EmbeddedMessaging interaction class, using
    * "datamemberMap" to initialize its parameter values.
    * "datamemberMap" is usually acquired as an argument to an RTI federate
    * callback method, such as "receiveInteraction".
    *
    * @param datamemberMap data structure containing initial values for the
    * parameters of this new EmbeddedMessaging interaction class instance
    */
    protected EmbeddedMessaging( String hlaClassName ) {
        super( hlaClassName );
    }

    /**
    * Creates a new EmbeddedMessaging interaction class instance that is a duplicate
    * of the instance referred to by messaging_var.
    *
    * @param messaging_var EmbeddedMessaging interaction class instance of which
    * this newly created EmbeddedMessaging interaction class instance will be a
    * duplicate
    */
    public EmbeddedMessaging(EmbeddedMessaging messaging_var) {
    
        // SHALLOW COPY
        classAndPropertyNameValueMap = new HashMap<>(messaging_var.classAndPropertyNameValueMap);

    }
}
