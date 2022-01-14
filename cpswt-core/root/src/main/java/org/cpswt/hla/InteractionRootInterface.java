
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

package org.cpswt.hla;
import java.util.*;
import hla.rti.*;

public interface InteractionRootInterface {

    //-----------------------------------------------------------------
    // ClassAndPropertyName CLASS -- USED AS KEY/VALUE FOR MAPS BELOW
    //-----------------------------------------------------------------
    class ClassAndPropertyName implements Comparable<ClassAndPropertyName> {
        private static final String separatorChar = ">";

        private final String className;
        private final String propertyName;

        public ClassAndPropertyName(String className, String propertyName) {
            this.className = className;
            this.propertyName = propertyName;
        }

        public ClassAndPropertyName(String classAndPropertyNameString) {
            String[] classAndPropertyNameArray = classAndPropertyNameString.split(separatorChar);
            this.className = classAndPropertyNameArray[0];
            this.propertyName = classAndPropertyNameArray[1];
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
        public boolean equals(Object object) {
            if (!(object instanceof ClassAndPropertyName)) {
                return false;
            }
            ClassAndPropertyName other = (ClassAndPropertyName)object;

            return className.equals(other.className) && propertyName.equals(other.propertyName);
        }

        @Override
        public String toString() {
            return className + separatorChar + propertyName;
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
     * Returns the fully-qualified (dot-delimited) Java class name of this instance's interaction class.
     *
     * @return the fully-qualified (dot-delimited) Java class name of this instance's interaction class
     */
    String getJavaClassName();

    /**
     * Returns the simple name (last name in its fully-qualified dot-delimited name)
     * of this instance's interaction class.
     *
     * @return the simple name of this instance's interaction class
     */
    String getSimpleClassName();

    /**
     * Returns the fully-qualified (dot-delimited) HLA name of this instance's interaction class.
     *
     * @return the fully-qualified (dot-delimited) HLA class name of this instance's interaction class
     */
    String getHlaClassName();

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