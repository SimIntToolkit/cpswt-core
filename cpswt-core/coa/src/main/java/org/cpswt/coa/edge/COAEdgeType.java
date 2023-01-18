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

package org.cpswt.coa.edge;

import java.util.HashMap;

public enum COAEdgeType {
    COAFlow("COAFlow"),
    COAFlowWithProbability("COAFlowWithProbability"),
    COAException("COAException"),
    Filter2COAElement("Filter2COAElement"),
    Outcome2Filter("Outcome2Filter"),
    Unknown("Unknown");

    static HashMap<COAEdgeType, Class> classMapping;
    static {
        classMapping = new HashMap<>();
        classMapping.put(COAEdgeType.COAFlow, COAEdge.class);
        classMapping.put(COAEdgeType.COAFlowWithProbability, COAFlowWithProbabilityEdge.class);
        classMapping.put(COAEdgeType.COAException, COAEdge.class);
        classMapping.put(COAEdgeType.Filter2COAElement, COAEdge.class);
        classMapping.put(COAEdgeType.Outcome2Filter, COAEdge.class);
    }

    private String name;
    COAEdgeType(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public Class<COAEdge> getCOAEdgeClass() {
        return classMapping.get(this);
    }
}
