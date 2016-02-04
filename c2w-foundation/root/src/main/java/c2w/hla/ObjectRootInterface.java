
package c2w.hla;

import java.util.*;
import hla.rti.*;

public interface ObjectRootInterface
{
    public int getUniqueID();

    

/**
* Returns the handle (RTI assigned) of this instance's object class .
* 
* @return the handle (RTI assigned) if this instance's object class
*/
public int getClassHandle();

/**
* Returns the fully-qualified (dot-delimited) name of this instance's object class.
* 
* @return the fully-qualified (dot-delimited) name of this instance's object class
*/
public String getClassName();

/**
* Returns the simple name (last name in its fully-qualified dot-delimited name)
* of this instance's object class.
* 
* @return the simple name of this instance's object class 
*/
public String getSimpleClassName();

/**
* Returns a set containing the names of all of the non-hiddenattributes of an
* object class instance.
*
* @return set containing the names of all of the attributes of an
* object class instance
*/
public Set< String > getAttributeNames();

/**
* Returns a set containing the names of all of the attributes of an
* object class instance.
*
* @return set containing the names of all of the attributes of an
* object class instance
*/
public Set< String > getAllAttributeNames();

/**
* Publishes the object class of this instance of the class for a federate.
*
* @param rti handle to the RTI, usu. obtained through the
* {@link SynchronizedFederate#getRTI()} call
*/
public void publishObject( RTIambassador rti );

/**
* Unpublishes the object class of this instance of this class for a federate.
*
* @param rti handle to the RTI, usu. obtained through the
* {@link SynchronizedFederate#getRTI()} call
*/
public void unpublishObject( RTIambassador rti );

/**
* Subscribes a federate to the object class of this instance of this class.
*
* @param rti handle to the RTI, usu. obtained through the
* {@link SynchronizedFederate#getRTI()} call
*/
public void subscribeObject( RTIambassador rti );

/**
* Unsubscribes a federate from the object class of this instance of this class.
*
* @param rti handle to the RTI, usu. obtained through the
* {@link SynchronizedFederate#getRTI()} call
*/
public void unsubscribeObject( RTIambassador rti );


/**
* Returns a data structure containing the handles of all attributes for this object
* class that are currently marked for subscription.  To actually subscribe to these
* attributes, a federate must call <objectclassname>.subscribe( RTIambassador rti ).
*
* @return data structure containing the handles of all attributes for this object
* class that are currently marked for subscription
*/
public AttributeHandleSet getSubscribedAttributeHandleSet();


    

    /**
    * Requests an attribute update for this object instance from the federate that
    * has modification rights on these attributes.
    *
    * @param rti handle to the RTI, usu. obtained through the
    * {@link SynchronizedFederate#getRTI()} call
    */
    public void requestUpdate( RTIambassador rti );

    /**
    * Returns the handle (RTI assigned) the corresponds to this object class
    * instance.  This handle is the instance's unique identifier to the RTI.
    *
    * @return the handle (RTI assigned) of this object class instance.
    */
    public int getObjectHandle();


    /**
    * Returns the timestamp for this object.  "receive order" objects
    * should have a timestamp of -1.
    *
    * @return timestamp for this object
    */
    public double getTime();
    
    /**
    * Sets the timestamp of this object to "time".
    *
    * @param time new timestamp for this object
    */
    public void setTime( double time );

    /**
    * Sets the timestamp of this object to "logicalTime".
    *
    * @param logicalTime new timestamp for this object
    */
    public void setTime( LogicalTime logicalTime );

    /**
    * Returns the value of the attribute named "datamemberName" for this
    * object.
    *
    * @param datamemberName name of attribute whose value to retrieve
    * @return the value of the attribute whose name is "datamemberName"
    */
    public Object getAttribute( String datamemberName );

    /**
    * Returns the value of the attribute whose handle is "datamemberHandle"
    * (RTI assigned) for this object.
    *
    * @param datamemberHandle handle (RTI assigned) of attribute whose
    * value to retrieve
    * @return the value of the attribute whose handle is "datamemberHandle"
    */
    public Object getAttribute( int datamemberHandle );
        
    /**
    * Set the values of the attributes in this object using
    * "datamemberMap".  "datamemberMap" is usually acquired as an argument to
    * an RTI federate callback method such as "receiveInteraction".
    *
    * @param datamemberMap  contains new values for the attributes of
    * this object
    */
    public void setAttributes( ReflectedAttributes datamemberMap );

    /**
    * Sets the value of the attribute named "datamemberName" to "value"
    * in this object.  "value" is converted to data type of "datamemberName"
    * if needed.
    * This action can also be affected by calling the set_<datamemberName>( value )
    * method on the object using a reference to the object's actual
    * class.
    *
    * @param datamemberName name of attribute whose value is to be set
    * to "value"
    * @param value new value of attribute called "datamemberName"
    */
    public void setAttribute( String datamemberName, String value );

    /**
    * Sets the value of the attribute named "datamemberName" to "value"
    * in this object.  "value" should have the same data type as that of
    * the "datamemberName" attribute.
    * This action can also be affected by calling the set_<datamemberName>( value )
    * method on the object using a reference to the object's actual
    * class.
    *
    * @param datamemberName name of attribute whose value is to be set
    * to "value"
    * @param value new value of attribute called "datamemberName"
    */
    public void setAttribute( String datamemberName, Object value );


    /**
    * Registers this object with the RTI.  This method is usually called by a
    * federate who "owns" this object, i.e. the federate that created it and
    * has write-privileges to its attributes (so, it is responsible for updating
    * these attribute and conveying their updated values to the RTI).
    *
    * @param rti handle to the RTI, usu. obtained through the
    * {@link SynchronizedFederate#getRTI()} call
    */ 
    public void registerObject( RTIambassador rti );
    
    /**
    * Unregisters this object with the RTI.  The RTI will destroy all information
    * it contains regarding this object as a result.  This method is usually
    * called by a federate who "owns" this object, i.e. the federate that created
    * it and has write-privileges to its attributes.
    *
    * @param rti handle to the RTI, usu. obtained through the
    * {@link SynchronizedFederate#getRTI()} call
    */ 
    public void unregisterObject( RTIambassador rti );

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
    public void updateAttributeValues( RTIambassador rti, double time, boolean force );

    /**
    * Like {@link #updateAttributeValues( RTIambassador rti, double time, boolean force )},
    * except "force" is always false.
    *
    * @param rti handle to the RTI, usu. obtained through the
    * {@link SynchronizedFederate#getRTI()} call
    * @param time timestamp on attribute values of this object
    */
    public void updateAttributeValues( RTIambassador rti, double time );

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
    public void updateAttributeValues( RTIambassador rti, boolean force );

    /**
    * Like {@link #updateAttributeValues( RTIambassador rti, boolean force )},
    * except "force" is always false.
    *
    * @param rti handle to the RTI, usu. obtained through the
    * {@link SynchronizedFederate#getRTI()} call
    */
    public void updateAttributeValues( RTIambassador rti );    
    
}