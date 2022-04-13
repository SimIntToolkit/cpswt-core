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

package org.cpswt.config;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents the parameter object for a federate.
 */
public class FederateConfig {

    @JsonIgnore
    final Set<String> fieldsSet = new HashSet<>();

    @JsonIgnore
    static final Logger logger = LogManager.getLogger(FederateConfig.class);

    /**
     * Time to wait before acquiring RTI for the first time (in milliseconds)
     */
    @FederateParameter
    public int federateRTIInitWaitTimeMs = 20;

    /**
     * The type of the Federate (i.e.: the model name).
     */
    @FederateParameter
    public String federateType;

    /**
     * The unique identifier of the federation.
     */
    @FederateParameter
    public String federationId;

    /**
     * Indicates if current federate is a late joiner.
     */
    @FederateParameter
    public boolean isLateJoiner;

    /**
     * The lookAhead value.
     */
    @FederateParameter
    public double lookAhead;

    /**
     * The step size value.
     */
    @FederateParameter
    public double stepSize;

    /**
     * Optional 'name' parameter that will be acquired as 'id'
     * Use {@link FederateParameterOptional} to exclude the field from "isSet" check
     */
    @FederateParameter
    @FederateParameterOptional
    public String name;

    /**
     * Optional 'federationJsonFileName' parameter that names the file that contains
     * information on all messaging classes for the federation
     * Use {@link FederateParameterOptional} to exclude the field from "isSet" check
     */
    @FederateParameter
    @FederateParameterOptional
    public String federationJsonFileName;

    /**
     * Optional 'federateDynamicMessagingJsonFileName' parameter that names the file that contains
     * information on all dynamic messaging classes for the federate
     * Use {@link FederateParameterOptional} to exclude the field from "isSet" check
     */
    @FederateParameter
    @FederateParameterOptional
    public String federateDynamicMessagingJsonFileName;

    /**
     * Default constructor for FederateConfig.
     */
    public FederateConfig() {}

    /**
     * Creates a new FederateConfig instance.
     * @param federateType The type of the Federate (i.e.: the model name).
     * @param federationId The unique identifier of the federation.
     * @param isLateJoiner Indicates if current federate is a late joiner.
     * @param lookAhead The lookAhead value.
     * @param stepSize The step size value.
     */
    public FederateConfig(
            String federateType,
            String federationId,
            boolean isLateJoiner,
            double lookAhead,
            double stepSize
    ) {
        this.federateType = federateType;
        this.federationId = federationId;
        this.isLateJoiner = isLateJoiner;
        this.lookAhead = lookAhead;
        this.stepSize = stepSize;
    }

    @JsonIgnore
    public static Set<Field> getFederateParameterFields(Class<? extends  FederateConfig> configClass) {
        Set<Field> fieldSet = new HashSet<>();
        Field[] fields = configClass.getFields();

        for (Field field : fields) {
            if (field.getAnnotation(FederateParameter.class) != null) {
                fieldSet.add(field);
            }
        }

        return fieldSet;
    }

    @JsonIgnore
    public static Set<Field> getMandatoryFederateParameterFields(Class<? extends  FederateConfig> configClass) {
        Set<Field> fieldSet = new HashSet<>();
        Field[] fields = configClass.getFields();

        for (Field field : fields) {
            if (field.getAnnotation(FederateParameter.class) != null
                    && field.getAnnotation(FederateParameterOptional.class) == null) {
                fieldSet.add(field);
            }
        }

        return fieldSet;
    }
}
