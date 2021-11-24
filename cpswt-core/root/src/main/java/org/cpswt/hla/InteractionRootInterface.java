package org.cpswt.hla;
import java.util.*;
import hla.rti.*;

public interface InteractionRootInterface {

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
     * Returns the handle (RTI assigned) of this instance's interaction class .
     *
     * @return the handle (RTI assigned) if this instance's interaction class
     */
    int getClassHandle();

    /**
     * Returns the fully-qualified (dot-delimited) name of this instance's interaction class.
     *
     * @return the fully-qualified (dot-delimited) name of this instance's interaction class
     */
    String getClassName();

    /**
     * Returns the simple name (last name in its fully-qualified dot-delimited name)
     * of this instance's interaction class.
     *
     * @return the simple name of this instance's interaction class
     */
    String getSimpleClassName();

    /**
     * Returns a set containing the names of all of the non-hiddenparameters of an
     * interaction class instance.
     *
     * @return set containing the names of all of the parameters of an
     * interaction class instance
     */
    List<ClassAndPropertyName> getParameterNames();

    /**
     * Returns a set containing the names of all of the parameters of an
     * interaction class instance.
     *
     * @return set containing the names of all of the parameters of an
     * interaction class instance
     */
    List<ClassAndPropertyName> getAllParameterNames();

    /**
     * Publishes the interaction class of this instance of the class for a federate.
     *
     * @param rti handle to the Local RTI Component, usu. obtained through the
     * {@link SynchronizedFederate#getLRC()} call
     */
    void publishInteraction( RTIambassador rti );

    /**
     * Unpublishes the interaction class of this instance of this class for a federate.
     *
     * @param rti handle to the Local RTI Component, usu. obtained through the
     * {@link SynchronizedFederate#getLRC()} call
     */
    void unpublishInteraction( RTIambassador rti );

    /**
     * Subscribes a federate to the interaction class of this instance of this class.
     *
     * @param rti handle to the Local RTI Component, usu. obtained through the
     * {@link SynchronizedFederate#getLRC()} call
     */
    void subscribeInteraction( RTIambassador rti );

    /**
     * Unsubscribes a federate from the interaction class of this instance of this class.
     *
     * @param rti handle to the Local RTI Component, usu. obtained through the
     * {@link SynchronizedFederate#getLRC()} call
     */
    void unsubscribeInteraction( RTIambassador rti );

    /**
     * Returns the timestamp for this interaction.  "receive order" interactions
     * should have a timestamp of -1.
     *
     * @return timestamp for this interaction
     */
    double getTime();

    /**
     * Sets the timestamp of this interaction to "time".
     *
     * @param time new timestamp for this interaction
     */
    void setTime( double time );

    /**
     * Sets the timestamp of this interaction to "logicalTime".
     *
     * @param logicalTime new timestamp for this interaction
     */
    void setTime( LogicalTime logicalTime );

    /**
     * Returns the value of the parameter named "propertyName" for this
     * interaction.
     *
     * @param propertyName name of parameter whose value to retrieve
     * @return the value of the parameter whose name is "propertyName"
     */
    Object getParameter( String propertyName );

    /**
     * Returns the value of the parameter whose handle is "propertyHandle"
     * (RTI assigned) for this interaction.
     *
     * @param propertyHandle handle (RTI assigned) of parameter whose
     * value to retrieve
     * @return the value of the parameter whose handle is "propertyHandle"
     */
    Object getParameter( int propertyHandle );

    /**
     * Set the values of the parameters in this interaction using
     * "propertyMap".  "propertyMap" is usually acquired as an argument to
     * an RTI federate callback method such as "receiveInteraction".
     *
     * @param propertyMap  contains new values for the parameters of
     * this interaction
     */
    void setParameters( ReceivedInteraction propertyMap );

    /*//*
     * Sets the value of the parameter named "propertyName" to "value"
     * in this interaction.  "value" is converted to data type of "propertyName"
     * if needed.
     * This action can also be affected by calling the set_&lt;propertyName&gt;( value )
     * method on the interaction using a reference to the interaction's actual
     * class.
     *
     * @param propertyName name of parameter whose value is to be set
     * to "value"
     * @param value new value of parameter called "propertyName"
     */
    // TODO: Get rid of this method: void setParameter( String propertyName, String value );

    /**
     * Sets the value of the parameter named "propertyName" to "value"
     * in this interaction.  "value" should have the same data type as that of
     * the "propertyName" parameter.
     * This action can also be affected by calling the set_&lt;propertyName&gt;( value )
     * method on the interaction using a reference to the interaction's actual
     * class.
     *
     * @param propertyName name of parameter whose value is to be set
     * to "value"
     * @param value new value of parameter called "propertyName"
     */
    void setParameter( String propertyName, Object value );

    /**
     * Sends this interaction to the RTI, with the specified timestamp "time".
     * This method should be used to send interactions that have "timestamp"
     * ordering.
     *
     * @param rti handle to the Local RTI Component, usu. obtained through the
     * {@link SynchronizedFederate#getLRC()} call
     * @param time timestamp for this interaction.  The timestamp should be no
     * less than the current federation time + the LOOKAHEAD value of the federate
     * sending this interaction.
     */
    void sendInteraction( RTIambassador rti, double time ) throws Exception;

    /**
     * Sends this interaction to the RTI (without a timestamp).
     * This method should be used to send interactions that have "receive"
     * ordering.
     *
     * @param rti handle to the Local RTI Component, usu. obtained through the
     * {@link SynchronizedFederate#getLRC()} call
     */
    void sendInteraction( RTIambassador rti ) throws Exception;
}