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

package org.cpswt.coa.edge;

import org.cpswt.coa.node.COANode;

import java.util.HashSet;

/**
 * A simple class for a COA edge in the COA sequence graph.
 */
public class COAEdge {

	private final COAEdgeType edgeType;
	private final String id;

	private HashSet<String> branchesFinishedCondition = new HashSet<String>();

	private final COANode fromNode;
	private final COANode toNode;

	public COAEdge(COAEdgeType edgeType, COANode fromNode, COANode toNode,
				   String id, HashSet<String> branchesFinishedCondition) {
		if (fromNode == null || toNode == null || id == null) {
			throw new IllegalArgumentException(
					"Null parameters given while creating an edge.");
		}
		if (COAEdgeType.COAException == edgeType
				&& branchesFinishedCondition == null) {
			throw new IllegalArgumentException(
					"Branches that should have finished not specified for the COAException edge.");
		}
		this.edgeType = edgeType;
		this.fromNode = fromNode;
		this.toNode = toNode;
		this.id = id;
		if (branchesFinishedCondition != null
				&& !branchesFinishedCondition.isEmpty()) {
			this.branchesFinishedCondition.addAll(branchesFinishedCondition);
		}
	}

	@Override
	public String toString() {
		return fromNode.getName() + " --to--> " + toNode.getName();
	}

	public COAEdgeType getEdgeType() {
		return edgeType;
	}

	public String getId() {
		return id;
	}

	public HashSet<String> getBranchesFinishedCondition() {
		return branchesFinishedCondition;
	}

	public COANode getFromNode() {
		return fromNode;
	}

	public COANode getToNode() {
		return toNode;
	}

}
