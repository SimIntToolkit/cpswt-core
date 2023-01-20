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

import com.fasterxml.jackson.annotation.JsonProperty;
import org.cpswt.coa.node.COANode;

import java.util.HashSet;

/**
 * A simple class for a COA edge with probability in the COA sequence graph. This is used to connect COA elements from a probabilistic choice node.
 */
public class COAFlowWithProbabilityEdge extends COAEdge {

	@JsonProperty("probability")
	private double probability;

	COAFlowWithProbabilityEdge() {
	    super(COAEdgeType.COAFlowWithProbability);
    }

	public COAFlowWithProbabilityEdge(COANode fromNode, COANode toNode,
									  String flowID, double probability, HashSet<String> branchesFinishedCondition) {

		super(COAEdgeType.COAFlowWithProbability, fromNode, toNode, flowID, branchesFinishedCondition);

		if (probability < 0) {
			throw new IllegalArgumentException(
					"Probability of COAFlowWithProbability edge must not be negative.");
		}

		this.probability = probability;
	}

	@Override
	public String toString() {
		return getFromNode().getName() + " --to--> " + getToNode().getName() + "[with probability " + probability + "]";
	}

	public double getProbability() {
		return probability;
	}

    public void setProbability(double probability) {
        this.probability = probability;
    }

    public void updateProbability(double probability) {
		this.probability = probability;
	}
}
