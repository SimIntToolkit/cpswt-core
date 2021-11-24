package org.cpswt.hla;
import java.util.*;
import hla.rti.*;

public interface ObjectRootInterface {

    //-----------------------------------------------------------------
    // ClassAndPropertyName CLASS -- USED AS KEY/VALUE FOR MAPS BELOW
    //-----------------------------------------------------------------
    static class ClassAndPropertyName implements Comparable<ClassAndPropertyName> {
        private final String className;
        private final String propertyName;

        public ClassAndPropertyName(String className, String propertyName) {
            this.className = className;
            this.propertyName = propertyName;
        }

        public String getClassName() {
            return className;
        }

        public String getPropertyName() {
            return propertyName;
        }

        @Override
        public int hashCode() {
            return toString().hashCode();
        }

        @Override
        public String toString() {
            return className + ">" + propertyName;
        }

        @Override
        public int compareTo(ClassAndPropertyName other) {
            int compareClassName = -className.compareTo(other.className);
            if (compareClassName != 0) {
                return compareClassName;
            }
            return propertyName.compareTo(other.propertyName);
        }
    }

    //---------------------------------
    // END ClassAndPropertyName CLASS
    //---------------------------------


    int getUniqueID();

    /**
     * Returns the handle (RTI assigned) of this instance's object class .
     *
     * @return the handle (RTI assigned) if this instance's object class
     */
    int getClassHandle();

    /**
     * Returns the fully-qualified (dot-delimited) name of this instance's object class.
     *
     * @return the fully-qualified (dot-delimited) name of this instance's object class
     */
    String getClassName();

    /**
     * Returns the simple name (last name in its fully-qualified dot-delimited name)
     * of this instance's object class.
     *
     * @return the simple name of this instance's object class
     */
    String getSimpleClassName();

    /**
     * Returns a set containing the names of all of the non-hiddenattributes of an
     * object class instance.
     *
     * @return set containing the names of all of the attributes of an
     * object class instance
     */
    List<ClassAndPropertyName> getAttributeNames();

    /**
     * Returns a set containing the names of all of the attributes of an
     * object class instance.
     *
     * @return set containing the names of all of the attributes of an
     * object class instance
     */
    List<ClassAndPropertyName> getAllAttributeNames();

    /**
     * Publishes the object class of this instance of the class for a federate.
     *
     * @param rti handle to the Local RTI Component, usu. obtained through the
     * {@link SynchronizedFederate#getLRC()} call
     */
    void publishObject( RTIambassador rti );

    /**
     * Unpublishes the object class of this instance of this class for a federate.
     *
     * @param rti handle to the Local RTI Component, usu. obtained through the
     * {@link SynchronizedFederate#getLRC()} call
     */
    void unpublishObject( RTIambassador rti );

    /**
     * Subscribes a federate to the object class of this instance of this class.
     *
     * @param rti handle to the Local RTI Component, usu. obtained through the
     * {@link SynchronizedFederate#getLRC()} call
     */
    void subscribeObject( RTIambassador rti );

    /**
     * Unsubscribes a federate from the object class of this instance of this class.
     *
     * @param rti handle to the Local RTI Component, usu. obtained through the
     * {@link SynchronizedFederate#getLRC()} call
     */
    void unsubscribeObject( RTIambassador rti );

    /**
     * Returns the timestamp for this object.  "receive order" objects
     * should have a timestamp of -1.
     *
     * @return timestamp for this object
     */
    double getTime();

    /**
     * Sets the timestamp of this object to "time".
     *
     * @param time new timestamp for this object
     */
    void setTime( double time );

    /**
     * Sets the timestamp of this object to "logicalTime".
     *
     * @param logicalTime new timestamp for this object
     */
    void setTime( LogicalTime logicalTime );

    /**
     * Returns the value of the attribute named "propertyName" for this
     * object.
     *
     * @param propertyName name of attribute whose value to retrieve
     * @return the value of the attribute whose name is "propertyName"
     */
    Object getAttribute( String propertyName );

    /**
     * Returns the value of the attribute whose handle is "propertyHandle"
     * (RTI assigned) for this object.
     *
     * @param propertyHandle handle (RTI assigned) of attribute whose
     * value to retrieve
     * @return the value of the attribute whose handle is "propertyHandle"
     */
    Object getAttribute( int propertyHandle );

    /**
     * Set the values of the attributes in this object using
     * "propertyMap".  "propertyMap" is usually acquired as an argument to
     * an RTI federate callback method such as "receiveInteraction".
     *
     * @param propertyMap  contains new values for the attributes of
     * this object
     */
    void setAttributes( ReflectedAttributes propertyMap );

    /*//*
     * Sets the value of the attribute named "propertyName" to "value"
     * in this object.  "value" is converted to data type of "propertyName"
     * if needed.
     * This action can also be affected by calling the set_&lt;propertyName&gt;( value )
     * method on the object using a reference to the object's actual
     * class.
     *
     * @param propertyName name of attribute whose value is to be set
     * to "value"
     * @param value new value of attribute called "propertyName"
     */
    // TODO: Get rid of this method: void setAttribute( String propertyName, String value );

    /**
     * Sets the value of the attribute named "propertyName" to "value"
     * in this object.  "value" should have the same data type as that of
     * the "propertyName" attribute.
     * This action can also be affected by calling the set_&lt;propertyName&gt;( value )
     * method on the object using a reference to the object's actual
     * class.
     *
     * @param propertyName name of attribute whose value is to be set
     * to "value"
     * @param value new value of attribute called "propertyName"
     */
    void setAttribute( String propertyName, Object value );

    /**
     * Returns a data structure containing the handles of all attributes for this object
     * class that are currently marked for subscription.  To actually subscribe to these
     * attributes, a federate must call &lt;objectclassname&gt;.subscribe( RTIambassador rti ).
     *
     * @return data structure containing the handles of all attributes for this object
     * class that are currently marked for subscription
     */
    AttributeHandleSet getSubscribedAttributeHandleSet();

    /**
     * Requests an attribute update for this object instance from the federate that
     * has modification rights on these attributes.
     *
     * @param rti handle to the Local RTI Component, usu. obtained through the
     * {@link SynchronizedFederate#getLRC()} call
     */
    void requestUpdate( RTIambassador rti );

    /**
     * Returns the handle (RTI assigned) the corresponds to this object class
     * instance.  This handle is the instance's unique identifier to the RTI.
     *
     * @return the handle (RTI assigned) of this object class instance.
     */
    int getObjectHandle();

    /**
     * Registers this object with the RTI.  This method is usually called by a
     * federate who "owns" this object, i.e. the federate that created it and
     * has write-privileges to its attributes (so, it is responsible for updating
     * these attribute and conveying their updated values to the RTI).
     *
     * @param rti handle to the Local RTI Component, usu. obtained through the
     * {@link SynchronizedFederate#getLRC()} call
     */
    void registerObject( RTIambassador rti );

    /**
     * Registers this object with the RTI using the given name.  This method is usually
     * called by a federate who "owns" this object, i.e. the federate that created it and
     * has write-privileges to its attributes (so, it is responsible for updating
     * these attribute and conveying their updated values to the RTI).
     *
     * @param rti handle to the Local RTI Component, usu. obtained through the
     * {@link SynchronizedFederate#getLRC()} call
     * @param name unique identifier to assign to the object instance
     * @throws ObjectAlreadyRegistered if the name is already assigned to another object instance
     */
    void registerObject(RTIambassador rti, String name) throws ObjectAlreadyRegistered;

    /**
     * Unregisters this object with the RTI.  The RTI will destroy all information
     * it contains regarding this object as a result.  This method is usually
     * called by a federate who "owns" this object, i.e. the federate that created
     * it and has write-privileges to its attributes.
     *
     * @param rti handle to the Local RTI Component, usu. obtained through the
     * {@link SynchronizedFederate#getLRC()} call
     */
    void unregisterObject( RTIambassador rti );

    /**
     * Broadcasts the attributes of this object and their values to the RTI, where
     * the values have "time" as their timestamp.  This call should be used for
     * objects whose attributes have "timestamp" ordering.
     *
     * @param rti handle to the Local RTI Component, usu. obtained through the
     * {@link SynchronizedFederate#getLRC()} call
     * @param time timestamp on attribute values of this object
     * @param force if "false", only the attributes whose values have changed since
     * the last call to "updateAttributeValues" will be broadcast to the RTI.  If
     * "true", all attributes and their values are broadcast to the RTI.
     */
    void updateAttributeValues( RTIambassador rti, double time, boolean force );

    /**
     * Like {@link #updateAttributeValues( RTIambassador rti, double time, boolean force )},
     * except "force" is always false.
     *
     * @param rti handle to the Local RTI Component, usu. obtained through the
     * {@link SynchronizedFederate#getLRC()} call
     * @param time timestamp on attribute values of this object
     */
    void updateAttributeValues( RTIambassador rti, double time );

    /**
     * Broadcasts the attributes of this object and their values to the RTI (with
     * no timestamp).  This call should be used for objects whose attributes have
     * "receive" ordering.
     *
     * @param rti handle to the Local RTI Component, usu. obtained through the
     * {@link SynchronizedFederate#getLRC()} call
     * @param force if "false", only the attributes whose values have changed since
     * the last call to "updateAttributeValues" will be broadcast to the RTI.  If
     * "true", all attributes and their values are broadcast to the RTI.
     */
    void updateAttributeValues( RTIambassador rti, boolean force );

    /**
     * Like {@link #updateAttributeValues( RTIambassador rti, boolean force )},
     * except "force" is always false.
     *
     * @param rti handle to the Local RTI Component, usu. obtained through the
     * {@link SynchronizedFederate#getLRC()} call
     */
    void updateAttributeValues( RTIambassador rti );
}