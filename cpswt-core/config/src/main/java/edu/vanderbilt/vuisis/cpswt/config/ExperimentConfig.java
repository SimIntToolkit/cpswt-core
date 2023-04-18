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

package edu.vanderbilt.vuisis.cpswt.config;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.List;

/**
 * Experiment config
 */
public class ExperimentConfig {
    public List<String> federateTypesAllowed;
    public List<FederateJoinInfo> expectedFederates;
    public List<FederateJoinInfo> lateJoinerFederates;
    public List<Double> pauseTimes;
    public String coaDefinition;
    public String coaSelection;
    public boolean terminateOnCOAFinish;
    public String COASelectionToExecute;
    public List<InjectedInteractionInfo> InjectedInteractions = new ArrayList<>();
    public List<String> MonitoredInteractions = new ArrayList<>();

    @JsonIgnore
    public int expectedFederateItemsCount() {
        int cnt = this.expectedFederates
                .stream()
                .mapToInt(o -> o.count)
                .sum();
        return cnt;
    }

    @JsonIgnore
    public boolean isExpectedFederateType(String federateType) {
        return this.expectedFederates
                .stream()
                .anyMatch(fedInfo -> fedInfo.federateType.equalsIgnoreCase(federateType));
    }

    @JsonIgnore
    public boolean isLateJoinerFederateType(String federateType) {
        return this.lateJoinerFederates
                .stream()
                .anyMatch(fedInfo -> fedInfo.federateType.equalsIgnoreCase(federateType));
    }

    @JsonIgnore
    public int getRemainingCountForExpectedType(String federateType) {
        return this.expectedFederates
                .stream()
                .filter(item -> item.federateType.equalsIgnoreCase(federateType))
                .findFirst().get().count;
    }
}
