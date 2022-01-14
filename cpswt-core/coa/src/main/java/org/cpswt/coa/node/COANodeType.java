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
 *
 * @author Himanshu Neema
 */

package org.cpswt.coa.node;

import java.util.HashMap;

public enum COANodeType {
    Action("Action"),
    AwaitN("AwaitN"),
    Dur("Dur"),
    Fork("Fork"),
    ProbabilisticChoice("ProbabilisticChoice"),
    RandomDur("RandomDur"),
    Outcome("Outcome"),
    OutcomeFilter("OutcomeFilter"),
    SyncPoint("SyncPoint"),
    TerminateCOA("TerminateCOA"),
    TerminateSimulation("TerminateSimulation"),
    Unknown("Unknown");

    static HashMap<COANodeType, Class> classMapping;
    static {
        classMapping = new HashMap<>();
        classMapping.put(COANodeType.Action, COAAction.class);
        classMapping.put(COANodeType.AwaitN, COAAwaitN.class);
        classMapping.put(COANodeType.Dur, COADuration.class);
        classMapping.put(COANodeType.Fork, COAFork.class);
        classMapping.put(COANodeType.ProbabilisticChoice, COAProbabilisticChoice.class);
        classMapping.put(COANodeType.RandomDur, COARandomDuration.class);
        classMapping.put(COANodeType.Outcome, COAOutcome.class);
        classMapping.put(COANodeType.OutcomeFilter, COAOutcomeFilter.class);
        classMapping.put(COANodeType.SyncPoint, COASyncPoint.class);
        classMapping.put(COANodeType.TerminateCOA, COANode.class);
        classMapping.put(COANodeType.TerminateSimulation, COANode.class);
    }

    private String name;

    COANodeType(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
    public Class<COANode> getCOANodeClass() {
        return classMapping.get(this);
    }
}
