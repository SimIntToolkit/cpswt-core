/*
 * Copyright (c) 2008, Institute for Software Integrated Systems, Vanderbilt University
 * All rights reserved.
 *
 * Permission to use, copy, modify, and distribute this software and its
 * documentation for any purpose, without fee, and without written agreement is
 * hereby granted, provided that the above copyright notice, the following
 * two paragraphs and the author appear in all copies of this software.
 *
 * IN NO EVENT SHALL THE VANDERBILT UNIVERSITY BE LIABLE TO ANY PARTY FOR
 * DIRECT, INDIRECT, SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES ARISING OUT
 * OF THE USE OF THIS SOFTWARE AND ITS DOCUMENTATION, EVEN IF THE VANDERBILT
 * UNIVERSITY HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * THE VANDERBILT UNIVERSITY SPECIFICALLY DISCLAIMS ANY WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY
 * AND FITNESS FOR A PARTICULAR PURPOSE.  THE SOFTWARE PROVIDED HEREUNDER IS
 * ON AN "AS IS" BASIS, AND THE VANDERBILT UNIVERSITY HAS NO OBLIGATION TO
 * PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, OR MODIFICATIONS.
 * 
 * @author Himanshu Neema
 */

package c2w.hla.matlab;

/**
 * A simple container class to hold a single type of data at any point in time.
 * This can refer to a parameter of an RTI interaction.
 * 
 * @author Himanshu Neema
 */
public class MatlabInteractionParameter {
    // ///////////////////////////////
    // Class variables
    // ///////////////////////////////
    private int parameterHandle = -1;

    private Integer isSet = 0;

    // ///////////////////////////////
    // Data types
    // ///////////////////////////////
    private Double doubleVal = -1.0;

    private String stringVal = null;

    private Integer integerVal = 0;

    private Boolean booleanVal = false;

    private Long longVal = 0L;

    private Float floatVal = 0F;

    // ///////////////////////////////
    // Data type flags
    // ///////////////////////////////
    private boolean isDouble = false;

    private boolean isString = false;

    private boolean isInteger = false;

    private boolean isBoolean = false;

    private boolean isLong = false;

    private boolean isFloat = false;

    // ///////////////////////////////
    // Constructors and factory methods
    // ///////////////////////////////
    private MatlabInteractionParameter(int paramHandle) {
        this.parameterHandle = paramHandle;
    }

    public static MatlabInteractionParameter createMatlabInteractionParameter(
            int paramHandle) {
        MatlabInteractionParameter mip = null;
        try {
            MatlabHLABridgeBase.verifyParameterHandle(paramHandle);
            mip = new MatlabInteractionParameter(paramHandle);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return mip;
    }

    // ///////////////////////////////
    // Class main methods
    // ///////////////////////////////
    public boolean isSet() {
        synchronized (isSet) {
            if (isSet == 0) {
                return false;
            }

            return true;
        }
    }

    public int getParameterHandle() {
        return parameterHandle;
    }

    // ///////////////////////////////
    // Data type: Double
    // ///////////////////////////////
    public boolean isDouble() {
        return isDouble;
    }

    public double getDoubleVal() {
        synchronized (isSet) {
            return doubleVal;
        }
    }

    public void setDoubleVal(double val) {
        synchronized (isSet) {
            if (isSet != 0) {
                System.out
                        .println("Error! A value was already set for this parameter");
                return;
            }
            isSet = 1;
            isDouble = true;
            this.doubleVal = val;
        }
    }

    // ///////////////////////////////
    // Data type: String
    // ///////////////////////////////
    public boolean isString() {
        return isString;
    }

    public String getStringVal() {
        synchronized (isSet) {
            return stringVal;
        }
    }

    public void setStringVal(String val) {
        synchronized (isSet) {
            if (isSet != 0) {
                System.out
                        .println("Error! A value was already set for this parameter");
                return;
            }
            isSet = 1;
            isString = true;
            this.stringVal = val;
        }
    }

    // ///////////////////////////////
    // Data type: Integer
    // ///////////////////////////////
    public boolean isInteger() {
        return isInteger;
    }

    public Integer getIntegerVal() {
        synchronized (isSet) {
            return integerVal;
        }
    }

    public void setIntegerVal(Integer val) {
        synchronized (isSet) {
            if (isSet != 0) {
                System.out
                        .println("Error! A value was already set for this parameter");
                return;
            }
            isSet = 1;
            isInteger = true;
            this.integerVal = val;
        }
    }

    // ///////////////////////////////
    // Data type: Boolean
    // ///////////////////////////////
    public boolean isBoolean() {
        return isBoolean;
    }

    public Boolean getBooleanVal() {
        synchronized (isSet) {
            return booleanVal;
        }
    }

    public void setBooleanVal(Boolean val) {
        synchronized (isSet) {
            if (isSet != 0) {
                System.out
                        .println("Error! A value was already set for this parameter");
                return;
            }
            isSet = 1;
            isBoolean = true;
            this.booleanVal = val;
        }
    }

    // ///////////////////////////////
    // Data type: Long
    // ///////////////////////////////
    public boolean isLong() {
        return isLong;
    }

    public Long getLongVal() {
        synchronized (isSet) {
            return longVal;
        }
    }

    public void setLongVal(Long val) {
        synchronized (isSet) {
            if (isSet != 0) {
                System.out
                        .println("Error! A value was already set for this parameter");
                return;
            }
            isSet = 1;
            isLong = true;
            this.longVal = val;
        }
    }

    // ///////////////////////////////
    // Data type: Float
    // ///////////////////////////////
    public boolean isFloat() {
        return isFloat;
    }

    public Float getFloatVal() {
        synchronized (isSet) {
            return floatVal;
        }
    }

    public void setFloatVal(Float val) {
        synchronized (isSet) {
            if (isSet != 0) {
                System.out
                        .println("Error! A value was already set for this parameter");
                return;
            }
            isSet = 1;
            isFloat = true;
            this.floatVal = val;
        }
    }
}
