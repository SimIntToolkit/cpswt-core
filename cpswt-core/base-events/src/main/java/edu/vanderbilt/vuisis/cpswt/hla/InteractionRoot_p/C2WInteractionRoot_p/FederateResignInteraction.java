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

package edu.vanderbilt.vuisis.cpswt.hla.InteractionRoot_p.C2WInteractionRoot_p;

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
 * Implements edu.vanderbilt.vuisis.cpswt.hla.InteractionRoot_p.C2WInteractionRoot_p.FederateResignInteraction
 */
@SuppressWarnings("unused")
public class FederateResignInteraction extends edu.vanderbilt.vuisis.cpswt.hla.InteractionRoot_p.C2WInteractionRoot {

    private static final Logger logger = LogManager.getLogger();

    // DUMMY STATIC METHOD TO ALLOW ACTIVE LOADING OF CLASS
    public static void load() { }

    // ----------------------------------------------------------------------------
    // STATIC PROPERTYS AND CODE THAT DEAL WITH NAMES
    // THIS CODE IS STATIC BECAUSE IT IS CLASS-DEPENDENT AND NOT INSTANCE-DEPENDENT
    // ----------------------------------------------------------------------------

    /**
     * Returns the fully-qualified (dot-delimited) name of the edu.vanderbilt.vuisis.cpswt.hla.InteractionRoot_p.C2WInteractionRoot_p.FederateResignInteraction interaction class.
     * Note: As this is a static method, it is NOT polymorphic, and so, if called on
     * a reference will return the name of the class pertaining to the reference,
     * rather than the name of the class for the instance referred to by the reference.
     * For the polymorphic version of this method, use {@link #getJavaClassName()}.
     *
     * @return the fully-qualified Java class name for this interaction class
     */
    public static String get_java_class_name() {
        return "edu.vanderbilt.vuisis.cpswt.hla.InteractionRoot_p.C2WInteractionRoot_p.FederateResignInteraction";
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
     * class name) of the edu.vanderbilt.vuisis.cpswt.hla.InteractionRoot_p.C2WInteractionRoot_p.FederateResignInteraction interaction class.
     *
     * @return the name of this interaction class
     */
    public static String get_simple_class_name() {
        return get_simple_class_name(get_hla_class_name());
    }

    /**
     * Returns the fully-qualified (dot-delimited) hla class name of the
     * InteractionRoot.C2WInteractionRoot.FederateResignInteraction interaction class.
     * Note: As this is a static method, it is NOT polymorphic, and so, if called on
     * a reference will return the federation name of the class pertaining to the reference,
     * rather than the name of the class for the instance referred to by the reference.
     * For the polymorphic version of this method, use {@link #getHlaClassName()}.
     *
     * @return the fully-qualified federation (HLA) class name for this interaction class
     */
    public static String get_hla_class_name() {
        return "InteractionRoot.C2WInteractionRoot.FederateResignInteraction";
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
     * edu.vanderbilt.vuisis.cpswt.hla.InteractionRoot_p.C2WInteractionRoot_p.FederateResignInteraction interaction class.
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
     * edu.vanderbilt.vuisis.cpswt.hla.InteractionRoot_p.C2WInteractionRoot_p.FederateResignInteraction interaction class.
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

        FederateResignInteraction instance = new FederateResignInteraction(noInstanceInit);
        _hlaClassNameInstanceMap.put(get_hla_class_name(), instance);

        Set<ClassAndPropertyName> classAndPropertyNameSet = new HashSet<>();
        classAndPropertyNameSet.add(new ClassAndPropertyName(
            "InteractionRoot.C2WInteractionRoot.FederateResignInteraction", "FederateId"
        ));
        classAndPropertyNameSet.add(new ClassAndPropertyName(
            "InteractionRoot.C2WInteractionRoot.FederateResignInteraction", "FederateType"
        ));
        classAndPropertyNameSet.add(new ClassAndPropertyName(
            "InteractionRoot.C2WInteractionRoot.FederateResignInteraction", "IsLateJoiner"
        ));

        // ADD THIS CLASS'S classAndPropertyNameSet TO _classNamePropertyNameSetMap DEFINED
        // IN InteractionRoot
        _classNamePropertyNameSetMap.put(get_hla_class_name(), classAndPropertyNameSet);

        _completeClassAndPropertyNameSet.addAll(classAndPropertyNameSet);

        Set<ClassAndPropertyName> allClassAndPropertyNameSet = new HashSet<>();

        allClassAndPropertyNameSet.add(new ClassAndPropertyName(
            "InteractionRoot.C2WInteractionRoot.FederateResignInteraction", "FederateId"
        ));

        allClassAndPropertyNameSet.add(new ClassAndPropertyName(
            "InteractionRoot.C2WInteractionRoot.FederateResignInteraction", "FederateType"
        ));

        allClassAndPropertyNameSet.add(new ClassAndPropertyName(
            "InteractionRoot.C2WInteractionRoot.FederateResignInteraction", "IsLateJoiner"
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

        key = new ClassAndPropertyName(get_hla_class_name(), "FederateId");
        _classAndPropertyNameInitialValueMap.put(key, "");

        key = new ClassAndPropertyName(get_hla_class_name(), "FederateType");
        _classAndPropertyNameInitialValueMap.put(key, "");

        key = new ClassAndPropertyName(get_hla_class_name(), "IsLateJoiner");
        _classAndPropertyNameInitialValueMap.put(key, false);

        commonInit(get_hla_class_name());

        logger.info(
          "Class \"edu.vanderbilt.vuisis.cpswt.hla.InteractionRoot_p.C2WInteractionRoot_p.FederateResignInteraction\" (hla class \"{}\") loaded", get_hla_class_name()
        );

        System.err.println(
          "Class \"edu.vanderbilt.vuisis.cpswt.hla.InteractionRoot_p.C2WInteractionRoot_p.FederateResignInteraction\" (hla class \"" +
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
     * Returns the handle (RTI assigned) of the edu.vanderbilt.vuisis.cpswt.hla.InteractionRoot_p.C2WInteractionRoot_p.FederateResignInteraction interaction class.
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
     * this interaction class (i.e. "edu.vanderbilt.vuisis.cpswt.hla.InteractionRoot_p.C2WInteractionRoot_p.FederateResignInteraction") given the parameter's name.
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
     * Publishes the edu.vanderbilt.vuisis.cpswt.hla.InteractionRoot_p.C2WInteractionRoot_p.FederateResignInteraction interaction class for a federate.
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
     * Unpublishes the edu.vanderbilt.vuisis.cpswt.hla.InteractionRoot_p.C2WInteractionRoot_p.FederateResignInteraction interaction class for a federate.
     *
     * @param rti handle to the Local RTI Component, usu. obtained through the
     *            {@link SynchronizedFederate#getRTI()} call
     */
    public static void unpublish_interaction(RTIambassador rti) {
        unpublish_interaction(get_hla_class_name(), rti);
    }

    /**
     * Subscribes a federate to the edu.vanderbilt.vuisis.cpswt.hla.InteractionRoot_p.C2WInteractionRoot_p.FederateResignInteraction interaction class.
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
     * Unsubscribes a federate from the edu.vanderbilt.vuisis.cpswt.hla.InteractionRoot_p.C2WInteractionRoot_p.FederateResignInteraction interaction class.
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

    //-----------------------------------------------------
    // END METHODS FOR PUBLISHING/SUBSCRIBING-TO THIS CLASS
    //-----------------------------------------------------

    /**
     * Return true if "handle" is equal to the handle (RTI assigned) of this class
     * (that is, the edu.vanderbilt.vuisis.cpswt.hla.InteractionRoot_p.C2WInteractionRoot_p.FederateResignInteraction interaction class).
     *
     * @param handle handle to compare to the value of the handle (RTI assigned) of
     * this class (the edu.vanderbilt.vuisis.cpswt.hla.InteractionRoot_p.C2WInteractionRoot_p.FederateResignInteraction interaction class).
     * @return "true" if "handle" matches the value of the handle of this class
     * (that is, the edu.vanderbilt.vuisis.cpswt.hla.InteractionRoot_p.C2WInteractionRoot_p.FederateResignInteraction interaction class).
     */
    public static boolean match(int handle) {
        return handle == get_class_handle();
    }

    //-------------
    // CONSTRUCTORS
    //-------------
    public FederateResignInteraction() {
        this(get_hla_class_name());
    }

    public FederateResignInteraction(LogicalTime logicalTime) {
        this();
        setTime(logicalTime);
    }

    public FederateResignInteraction(ReceivedInteraction propertyMap) {
        this();
        setParameters( propertyMap );
    }

    public FederateResignInteraction(ReceivedInteraction propertyMap, LogicalTime logicalTime) {
        this(propertyMap);
        setTime(logicalTime);
    }

    //-----------------
    // END CONSTRUCTORS
    //-----------------


    //-----------------
    // CREATION METHODS
    //-----------------
    public static FederateResignInteraction create_interaction() {
        return new FederateResignInteraction();
    }

    public FederateResignInteraction createInteraction() {
        return create_interaction();
    }

    public static FederateResignInteraction create_interaction(LogicalTime logicalTime) {
        return new FederateResignInteraction(logicalTime);
    }

    public FederateResignInteraction createInteraction(LogicalTime logicalTime) {
        return create_interaction(logicalTime);
    }

    public static FederateResignInteraction create_interaction(ReceivedInteraction propertyMap) {
        return new FederateResignInteraction(propertyMap);
    }

    public FederateResignInteraction createInteraction(ReceivedInteraction propertyMap) {
        return create_interaction(propertyMap);
    }

    public static FederateResignInteraction create_interaction(ReceivedInteraction propertyMap, LogicalTime logicalTime) {
        return new FederateResignInteraction(propertyMap, logicalTime);
    }

    public FederateResignInteraction createInteraction(ReceivedInteraction propertyMap, LogicalTime logicalTime) {
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
     * Set the value of the "FederateId" parameter to "value" for this parameter.
     *
     * @param value the new value for the "FederateId" parameter
     */
    public void set_FederateId(String value) {
        ClassAndPropertyName key = new ClassAndPropertyName(get_hla_class_name(), "FederateId");
        classAndPropertyNameValueMap.put(key, value);
    }

    /**
     * Returns the value of the "FederateId" parameter of this interaction.
     *
     * @return the value of the "FederateId" parameter
     */
    public String get_FederateId() {
        ClassAndPropertyName key = new ClassAndPropertyName(get_hla_class_name(), "FederateId");
        return (String)classAndPropertyNameValueMap.get(key);
    }


    /**
     * Set the value of the "FederateType" parameter to "value" for this parameter.
     *
     * @param value the new value for the "FederateType" parameter
     */
    public void set_FederateType(String value) {
        ClassAndPropertyName key = new ClassAndPropertyName(get_hla_class_name(), "FederateType");
        classAndPropertyNameValueMap.put(key, value);
    }

    /**
     * Returns the value of the "FederateType" parameter of this interaction.
     *
     * @return the value of the "FederateType" parameter
     */
    public String get_FederateType() {
        ClassAndPropertyName key = new ClassAndPropertyName(get_hla_class_name(), "FederateType");
        return (String)classAndPropertyNameValueMap.get(key);
    }


    /**
     * Set the value of the "IsLateJoiner" parameter to "value" for this parameter.
     *
     * @param value the new value for the "IsLateJoiner" parameter
     */
    public void set_IsLateJoiner(Boolean value) {
        ClassAndPropertyName key = new ClassAndPropertyName(get_hla_class_name(), "IsLateJoiner");
        classAndPropertyNameValueMap.put(key, value);
    }

    /**
     * Returns the value of the "IsLateJoiner" parameter of this interaction.
     *
     * @return the value of the "IsLateJoiner" parameter
     */
    public boolean get_IsLateJoiner() {
        ClassAndPropertyName key = new ClassAndPropertyName(get_hla_class_name(), "IsLateJoiner");
        return (boolean)classAndPropertyNameValueMap.get(key);
    }

    //----------------------------------
    // END PROPERTY MANIPULATION METHODS
    //----------------------------------

    protected FederateResignInteraction(NoInstanceInit noInstanceInit) {
        super(noInstanceInit);
    }

    /**
    * Creates an instance of the FederateResignInteraction interaction class, using
    * "datamemberMap" to initialize its parameter values.
    * "datamemberMap" is usually acquired as an argument to an RTI federate
    * callback method, such as "receiveInteraction".
    *
    * @param datamemberMap data structure containing initial values for the
    * parameters of this new FederateResignInteraction interaction class instance
    */
    protected FederateResignInteraction( String hlaClassName ) {
        super( hlaClassName );
    }

    /**
    * Creates a new FederateResignInteraction interaction class instance that is a duplicate
    * of the instance referred to by messaging_var.
    *
    * @param messaging_var FederateResignInteraction interaction class instance of which
    * this newly created FederateResignInteraction interaction class instance will be a
    * duplicate
    */
    public FederateResignInteraction(FederateResignInteraction messaging_var) {
        super(messaging_var);
    }

    public FederateResignInteraction create_interaction( FederateResignInteraction messaging_var ) {
        return (FederateResignInteraction)edu.vanderbilt.vuisis.cpswt.hla.InteractionRoot.create_interaction(messaging_var);
    }
}
