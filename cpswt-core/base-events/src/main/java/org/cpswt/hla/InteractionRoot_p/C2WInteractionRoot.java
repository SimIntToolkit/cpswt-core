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

package org.cpswt.hla.InteractionRoot_p;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;
import org.json.JSONTokener;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import hla.rti.LogicalTime;
import hla.rti.RTIambassador;
import hla.rti.ReceivedInteraction;


/**
 * Implements org.cpswt.hla.InteractionRoot_p.C2WInteractionRoot
 */
@SuppressWarnings("unused")
public class C2WInteractionRoot extends org.cpswt.hla.InteractionRoot {

    private static final Logger logger = LogManager.getLogger();

    // DUMMY STATIC METHOD TO ALLOW ACTIVE LOADING OF CLASS
    public static void load() { }

    // ----------------------------------------------------------------------------
    // STATIC PROPERTYS AND CODE THAT DEAL WITH NAMES
    // THIS CODE IS STATIC BECAUSE IT IS CLASS-DEPENDENT AND NOT INSTANCE-DEPENDENT
    // ----------------------------------------------------------------------------

    /**
     * Returns the fully-qualified (dot-delimited) name of the org.cpswt.hla.InteractionRoot_p.C2WInteractionRoot interaction class.
     * Note: As this is a static method, it is NOT polymorphic, and so, if called on
     * a reference will return the name of the class pertaining to the reference,
     * rather than the name of the class for the instance referred to by the reference.
     * For the polymorphic version of this method, use {@link #getJavaClassName()}.
     *
     * @return the fully-qualified Java class name for this interaction class
     */
    public static String get_java_class_name() {
        return "org.cpswt.hla.InteractionRoot_p.C2WInteractionRoot";
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
     * class name) of the org.cpswt.hla.InteractionRoot_p.C2WInteractionRoot interaction class.
     *
     * @return the name of this interaction class
     */
    public static String get_simple_class_name() {
        return get_simple_class_name(get_hla_class_name());
    }

    /**
     * Returns the fully-qualified (dot-delimited) hla class name of the
     * InteractionRoot.C2WInteractionRoot interaction class.
     * Note: As this is a static method, it is NOT polymorphic, and so, if called on
     * a reference will return the federation name of the class pertaining to the reference,
     * rather than the name of the class for the instance referred to by the reference.
     * For the polymorphic version of this method, use {@link #getHlaClassName()}.
     *
     * @return the fully-qualified federation (HLA) class name for this interaction class
     */
    public static String get_hla_class_name() {
        return "InteractionRoot.C2WInteractionRoot";
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
     * org.cpswt.hla.InteractionRoot_p.C2WInteractionRoot interaction class.
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
     * org.cpswt.hla.InteractionRoot_p.C2WInteractionRoot interaction class.
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

        C2WInteractionRoot instance = new C2WInteractionRoot(noInstanceInit);
        _hlaClassNameInstanceMap.put(get_hla_class_name(), instance);

        Set<ClassAndPropertyName> classAndPropertyNameSet = new HashSet<>();
        classAndPropertyNameSet.add(new ClassAndPropertyName(
            "InteractionRoot.C2WInteractionRoot", "actualLogicalGenerationTime"
        ));
        classAndPropertyNameSet.add(new ClassAndPropertyName(
            "InteractionRoot.C2WInteractionRoot", "federateFilter"
        ));
        classAndPropertyNameSet.add(new ClassAndPropertyName(
            "InteractionRoot.C2WInteractionRoot", "federateSequence"
        ));

        // ADD THIS CLASS'S classAndPropertyNameSet TO _classNamePropertyNameSetMap DEFINED
        // IN InteractionRoot
        _classNamePropertyNameSetMap.put(get_hla_class_name(), classAndPropertyNameSet);


        Set<ClassAndPropertyName> allClassAndPropertyNameSet = new HashSet<>();

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

        key = new ClassAndPropertyName(get_hla_class_name(), "actualLogicalGenerationTime");
        _classAndPropertyNameInitialValueMap.put(key, (double)0);

        key = new ClassAndPropertyName(get_hla_class_name(), "federateFilter");
        _classAndPropertyNameInitialValueMap.put(key, "");

        key = new ClassAndPropertyName(get_hla_class_name(), "federateSequence");
        _classAndPropertyNameInitialValueMap.put(key, "");

        logger.info(
          "Class \"org.cpswt.hla.InteractionRoot_p.C2WInteractionRoot\" (hla class \"{}\") loaded", get_hla_class_name()
        );

        System.err.println(
          "Class \"org.cpswt.hla.InteractionRoot_p.C2WInteractionRoot\" (hla class \"" +
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
     * Returns the handle (RTI assigned) of the org.cpswt.hla.InteractionRoot_p.C2WInteractionRoot interaction class.
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
     * this interaction class (i.e. "org.cpswt.hla.InteractionRoot_p.C2WInteractionRoot") given the parameter's name.
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
     * Publishes the org.cpswt.hla.InteractionRoot_p.C2WInteractionRoot interaction class for a federate.
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
     * Unpublishes the org.cpswt.hla.InteractionRoot_p.C2WInteractionRoot interaction class for a federate.
     *
     * @param rti handle to the Local RTI Component, usu. obtained through the
     *            {@link SynchronizedFederate#getRTI()} call
     */
    public static void unpublish_interaction(RTIambassador rti) {
        unpublish_interaction(get_hla_class_name(), rti);
    }

    /**
     * Subscribes a federate to the org.cpswt.hla.InteractionRoot_p.C2WInteractionRoot interaction class.
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
     * Unsubscribes a federate from the org.cpswt.hla.InteractionRoot_p.C2WInteractionRoot interaction class.
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
     * (that is, the org.cpswt.hla.InteractionRoot_p.C2WInteractionRoot interaction class).
     *
     * @param handle handle to compare to the value of the handle (RTI assigned) of
     * this class (the org.cpswt.hla.InteractionRoot_p.C2WInteractionRoot interaction class).
     * @return "true" if "handle" matches the value of the handle of this class
     * (that is, the org.cpswt.hla.InteractionRoot_p.C2WInteractionRoot interaction class).
     */
    public static boolean match(int handle) {
        return handle == get_class_handle();
    }

    //-------------
    // CONSTRUCTORS
    //-------------
    public C2WInteractionRoot() {
        this(get_hla_class_name());
    }

    public C2WInteractionRoot(LogicalTime logicalTime) {
        this();
        setTime(logicalTime);
    }

    public C2WInteractionRoot(ReceivedInteraction propertyMap) {
        this();
        setParameters( propertyMap );
    }

    public C2WInteractionRoot(ReceivedInteraction propertyMap, LogicalTime logicalTime) {
        this(propertyMap);
        setTime(logicalTime);
    }

    //-----------------
    // END CONSTRUCTORS
    //-----------------


    //-----------------
    // CREATION METHODS
    //-----------------
    public static C2WInteractionRoot create_interaction() {
        return new C2WInteractionRoot();
    }

    public C2WInteractionRoot createInteraction() {
        return create_interaction();
    }

    public static C2WInteractionRoot create_interaction(LogicalTime logicalTime) {
        return new C2WInteractionRoot(logicalTime);
    }

    public C2WInteractionRoot createInteraction(LogicalTime logicalTime) {
        return create_interaction(logicalTime);
    }

    public static C2WInteractionRoot create_interaction(ReceivedInteraction propertyMap) {
        return new C2WInteractionRoot(propertyMap);
    }

    public C2WInteractionRoot createInteraction(ReceivedInteraction propertyMap) {
        return create_interaction(propertyMap);
    }

    public static C2WInteractionRoot create_interaction(ReceivedInteraction propertyMap, LogicalTime logicalTime) {
        return new C2WInteractionRoot(propertyMap, logicalTime);
    }

    public C2WInteractionRoot createInteraction(ReceivedInteraction propertyMap, LogicalTime logicalTime) {
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
     * Set the value of the "actualLogicalGenerationTime" parameter to "value" for this parameter.
     *
     * @param value the new value for the "actualLogicalGenerationTime" parameter
     */
    public void set_actualLogicalGenerationTime(Double value) {
        ClassAndPropertyName key = new ClassAndPropertyName(get_hla_class_name(), "actualLogicalGenerationTime");
        classAndPropertyNameValueMap.put(key, value);
    }

    /**
     * Returns the value of the "actualLogicalGenerationTime" parameter of this interaction.
     *
     * @return the value of the "actualLogicalGenerationTime" parameter
     */
    public double get_actualLogicalGenerationTime() {
        ClassAndPropertyName key = new ClassAndPropertyName(get_hla_class_name(), "actualLogicalGenerationTime");
        return (double)classAndPropertyNameValueMap.get(key);
    }


    /**
     * Set the value of the "federateFilter" parameter to "value" for this parameter.
     *
     * @param value the new value for the "federateFilter" parameter
     */
    public void set_federateFilter(String value) {
        ClassAndPropertyName key = new ClassAndPropertyName(get_hla_class_name(), "federateFilter");
        classAndPropertyNameValueMap.put(key, value);
    }

    /**
     * Returns the value of the "federateFilter" parameter of this interaction.
     *
     * @return the value of the "federateFilter" parameter
     */
    public String get_federateFilter() {
        ClassAndPropertyName key = new ClassAndPropertyName(get_hla_class_name(), "federateFilter");
        return (String)classAndPropertyNameValueMap.get(key);
    }


    /**
     * Set the value of the "federateSequence" parameter to "value" for this parameter.
     *
     * @param value the new value for the "federateSequence" parameter
     */
    public void set_federateSequence(String value) {
        ClassAndPropertyName key = new ClassAndPropertyName(get_hla_class_name(), "federateSequence");
        classAndPropertyNameValueMap.put(key, value);
    }

    /**
     * Returns the value of the "federateSequence" parameter of this interaction.
     *
     * @return the value of the "federateSequence" parameter
     */
    public String get_federateSequence() {
        ClassAndPropertyName key = new ClassAndPropertyName(get_hla_class_name(), "federateSequence");
        return (String)classAndPropertyNameValueMap.get(key);
    }

    //----------------------------------
    // END PROPERTY MANIPULATION METHODS
    //----------------------------------

    private static boolean is_federate_sequence(String federateSequence) {

        JSONArray jsonArray;

        try {
            jsonArray = new JSONArray( federateSequence );
        } catch (JSONException jsonException) {
            return false;
        }

        int jsonArrayLength = jsonArray.length();
        for(int ix = 0 ; ix < jsonArrayLength ; ++ix) {
            if (!(jsonArray.get(ix) instanceof String)) {
                return false;
            }
        }
        return true;
    }

    private static void update_federate_sequence_aux(org.cpswt.hla.InteractionRoot interactionRoot, String federateId) {

        if (interactionRoot.getFederateAppendedToFederateSequence()) {
            return;
        }
        interactionRoot.setFederateAppendedToFederateSequence(true);

        String federateSequence = (String)interactionRoot.getParameter("federateSequence");

        JSONArray jsonArray = is_federate_sequence( federateSequence ) ?
            new JSONArray( federateSequence ) : new JSONArray();

        jsonArray.put( federateId );
        interactionRoot.setParameter("federateSequence", jsonArray.toString());
    }

    public static void update_federate_sequence(org.cpswt.hla.InteractionRoot interactionRoot, String federateId) {
        String instanceHlaClassName = interactionRoot.getInstanceHlaClassName();

        if ( instanceHlaClassName.startsWith("InteractionRoot.C2WInteractionRoot") ) {
            update_federate_sequence_aux(interactionRoot, federateId);
        }
    }

    public void updateFederateSequence(String federateId) {
        update_federate_sequence_aux(this, federateId);
    }

    public static List<String> get_federate_sequence_list(String federateSequence) {
        JSONArray jsonArray = is_federate_sequence( federateSequence ) ?
            new JSONArray( federateSequence ) : new JSONArray();

        ArrayList<String> retval = new ArrayList<String>();
        for(Object federateId: jsonArray.toList()) {
            retval.add( (String)federateId );
        }

        return retval;
    }

    private static List<String> get_federate_sequence_list_aux(org.cpswt.hla.InteractionRoot interactionRoot) {
        String federateSequence = (String)interactionRoot.getParameter("federateSequence");

        return get_federate_sequence_list(federateSequence);
    }

    public static List<String> get_federate_sequence_list(org.cpswt.hla.InteractionRoot interactionRoot) {

        String instanceHlaClassName = interactionRoot.getInstanceHlaClassName();

        return instanceHlaClassName.startsWith("InteractionRoot.C2WInteractionRoot") ?
          get_federate_sequence_list_aux(interactionRoot) : new ArrayList<>();
    }

    public List<String> getFederateSequenceList() {
        return get_federate_sequence_list_aux(this);
    }

    public static String get_origin_federate_id(org.cpswt.hla.InteractionRoot interactionRoot) {
        List<String> federateSequenceList = get_federate_sequence_list(interactionRoot);
        return federateSequenceList.isEmpty() ? null : federateSequenceList.get(0);
    }

    public String getOriginFederateId() {
        return get_origin_federate_id(this);
    }

    public static String get_source_federate_id(org.cpswt.hla.InteractionRoot interactionRoot) {
        List<String> federateSequenceList = get_federate_sequence_list(interactionRoot);
        return federateSequenceList.isEmpty() ? null : federateSequenceList.get(federateSequenceList.size() - 1);
    }

    public String getSourceFederateId() {
        return get_source_federate_id(this);
    }

    private static Map<String, Set<String>> _hlaClassNameToRejectSourceFederateIdSetMap = new HashMap<>();

    public static void add_reject_source_federate_id(String hlaClassName, String federateId) {
        if (
          "InteractionRoot.C2WInteractionRoot".equals(hlaClassName) ||
          (
            hlaClassName.startsWith("InteractionRoot.C2WInteractionRoot" + ".") &&
            _hlaClassNameSet.contains(hlaClassName)
          )
        ) {
            if (!_hlaClassNameToRejectSourceFederateIdSetMap.containsKey(hlaClassName)) {
                _hlaClassNameToRejectSourceFederateIdSetMap.put(hlaClassName, new HashSet<>());
            }
            _hlaClassNameToRejectSourceFederateIdSetMap.get(hlaClassName).add(federateId);
        }
    }

    public void addRejectSourceFederateId(String federateId) {
        add_reject_source_federate_id(getInstanceHlaClassName(), federateId);
    }

    public static boolean is_reject_source_federate_id(String hlaClassName, String federateId) {
        return _hlaClassNameToRejectSourceFederateIdSetMap.containsKey(hlaClassName) &&
          _hlaClassNameToRejectSourceFederateIdSetMap.get(hlaClassName).contains(federateId);
    }

    public static boolean is_reject_source_federate_id(
      org.cpswt.hla.InteractionRoot interactionRoot
    ) {
        return is_reject_source_federate_id(
          interactionRoot.getInstanceHlaClassName(), get_source_federate_id(interactionRoot)
        );
    }

    public boolean isRejectSourceFederateId() {
        return is_reject_source_federate_id(this);
    }

    public static void remove_reject_source_federate_id(String hlaClassName, String federateId) {
        if (_hlaClassNameToRejectSourceFederateIdSetMap.containsKey(hlaClassName)) {
            _hlaClassNameToRejectSourceFederateIdSetMap.get(hlaClassName).remove(federateId);
        }
    }

    public void removeRejectSourceFederateId(String federateId) {
        remove_reject_source_federate_id(getInstanceHlaClassName(), federateId);
    }

    // THIS METHOD ACTS AS AN ERROR DETECTOR -- ALL INSTANCES OF C2WInteractionRoot
    // SHOULD HAVE A NON-EMPTY JSON-ARRAY VALUE FOR THEIR federateSequence PARAMETER.
    @Override
    public void sendInteraction( RTIambassador rti, double time ) throws Exception {

        if (  !is_federate_sequence( get_federateSequence() )  ) {
            throw new Exception(
                "federateSequence parameter is invalid: must contain sequence " +
                "of federate-ids of federates that have handled this interaction."
            );
        }
        super.sendInteraction( rti, time );
    }

    // THIS METHOD ACTS AS AN ERROR DETECTOR -- ALL INSTANCES OF C2WInteractionRoot
    // SHOULD HAVE A NON-EMPTY JSON-ARRAY VALUE FOR THEIR federateSequence PARAMETER.
    @Override
    public void sendInteraction( RTIambassador rti ) throws Exception {

        if (  !is_federate_sequence( get_federateSequence() )  ) {
            throw new Exception(
                "federateSequence parameter is invalid: must contain sequence " +
                "of federate-ids of federates that have handled this interaction."
            );
        }
        super.sendInteraction( rti );
    }

    public static void readRejectSourceFederateIdData(File rejectSourceFederateIdJsonFile) {
        try (
            FileReader fileReader = new FileReader(rejectSourceFederateIdJsonFile)
        ) {
            readRejectSourceFederateIdData(fileReader);
        } catch(Exception e) {
            logger.error(
              "readRejectSourceFederateIdJson: \"{}\" exception on file \"{}\"",
              e.getClass().getSimpleName(),
              rejectSourceFederateIdJsonFile.getAbsolutePath()
            );
        }
    }

    public static void readRejectSourceFederateIdData(Reader reader) {
        Map<String, Object> rejectSourceFederateIdMap = new JSONObject( new JSONTokener(reader) ).toMap();

        for(String hlaClassName: rejectSourceFederateIdMap.keySet()) {
            for(Object object: (JSONArray)rejectSourceFederateIdMap.get(hlaClassName)) {
                add_reject_source_federate_id(hlaClassName, (String)object);
            }
        }
    }

    public static void add_reject_source_federate_id(String federateId) {
        add_reject_source_federate_id(get_hla_class_name(), federateId);
    }

    public static boolean is_reject_source_federate_id(String federateId) {
        return is_reject_source_federate_id(get_hla_class_name(), federateId);
    }

    public static void remove_reject_source_federate_id(String federateId) {
        remove_reject_source_federate_id(get_hla_class_name(), federateId);
    }

    protected C2WInteractionRoot(NoInstanceInit noInstanceInit) {
        super(noInstanceInit);
    }

    /**
    * Creates an instance of the C2WInteractionRoot interaction class, using
    * "datamemberMap" to initialize its parameter values.
    * "datamemberMap" is usually acquired as an argument to an RTI federate
    * callback method, such as "receiveInteraction".
    *
    * @param datamemberMap data structure containing initial values for the
    * parameters of this new C2WInteractionRoot interaction class instance
    */
    protected C2WInteractionRoot( String hlaClassName ) {
        super( hlaClassName );
    }

    /**
    * Creates a new C2WInteractionRoot interaction class instance that is a duplicate
    * of the instance referred to by messaging_var.
    *
    * @param messaging_var C2WInteractionRoot interaction class instance of which
    * this newly created C2WInteractionRoot interaction class instance will be a
    * duplicate
    */
    public C2WInteractionRoot(C2WInteractionRoot messaging_var) {
    
        // SHALLOW COPY
        classAndPropertyNameValueMap = new HashMap<>(messaging_var.classAndPropertyNameValueMap);

    }
}
