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

package edu.vanderbilt.vuisis.cpswt.coa;

import edu.vanderbilt.vuisis.cpswt.hla.InteractionRoot;

import java.util.Set;
import java.util.HashSet;

/**
 * Stores last arrived interaction in the Federation Manager and is used by
 * OutcomeFilters of sequence model orchestrator to check the last arrived
 * interaction of the Outcome it filters.
 */
public class ArrivedInteraction {
    private final InteractionRoot _ir;
    private final Double _arrTime;

    private boolean _usedByNonRootCOANode = false;

    private final Set<String> _coaIdSet = new HashSet<>();

    private final Set<String> _coaNodeIdSet = new HashSet<>();

    public ArrivedInteraction(InteractionRoot ir, Double arrTime) {
        if (ir == null) {
            throw new NullPointerException("Passed InteractionRoot instance is null!");
        }
        if (arrTime == null) {
            throw new NullPointerException("Passed 'arrivedTime' parameter is null!");
        }
        if (arrTime < 0) {
            throw new IllegalArgumentException("Passed 'arrivedTime' can't be a negative number!");
        }

        this._ir = ir;
        this._arrTime = arrTime;
    }

    public InteractionRoot getInteractionRoot() {
        return _ir;
    }

    public Double getArrivalTime() {
        return _arrTime;
    }

    public void setUsedByNonRootCOANode() {
        _usedByNonRootCOANode = true;
    }

    public boolean getUsedByNonRootCOANode() {
        return _usedByNonRootCOANode;
    }

    public void addCoaId(String coaId) {
        _coaIdSet.add(coaId);
    }

    public void removeCoaId(String coaId) {
        _coaIdSet.remove(coaId);
    }

    public boolean hasCoaId(String coaId) {
        return _coaIdSet.contains(coaId);
    }

    public void addCoaNodeId(String coaNodeId) {
        _coaNodeIdSet.add(coaNodeId);
    }

    public boolean hasCoaNodeId(String coaNodeId) {
        return _coaNodeIdSet.contains(coaNodeId);
    }
}
