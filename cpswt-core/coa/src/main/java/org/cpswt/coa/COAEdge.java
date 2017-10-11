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

package org.cpswt.coa;

import java.util.HashSet;

/**
 * A simple class for a COA edge in the COA sequence graph.
 */
public class COAEdge {

	private COAEdgeType _edgeType;

	private String _flowID;

	private HashSet<String> _branchesFinishedCondition = new HashSet<String>();

	private COANode _fromNode = null;

	private COANode _toNode = null;

	public COAEdge(COAEdgeType edgeType, COANode fromNode, COANode toNode,
			String flowID, HashSet<String> branchesFinishedCondition) {
		if (fromNode == null || toNode == null || flowID == null) {
			throw new IllegalArgumentException(
					"Null parameters given while creating an edge.");
		}
		if (COAEdgeType.COAException == edgeType
				&& branchesFinishedCondition == null) {
			throw new IllegalArgumentException(
					"Branches that should have finished not specified for the COAException edge.");
		}
		this._edgeType = edgeType;
		this._fromNode = fromNode;
		this._toNode = toNode;
		this._flowID = flowID;
		if (branchesFinishedCondition != null
				&& !branchesFinishedCondition.isEmpty()) {
			this._branchesFinishedCondition.addAll(branchesFinishedCondition);
		}
	}

	@Override
	public String toString() {
		return _fromNode.getNodeName() + " --to--> " + _toNode.getNodeName();
	}

	public COAEdgeType getEdgeType() {
		return _edgeType;
	}

	public String getFlowID() {
		return _flowID;
	}

	public HashSet<String> getBranchesFinishedCondition() {
		return _branchesFinishedCondition;
	}

	public COANode getFromNode() {
		return _fromNode;
	}

	public COANode getToNode() {
		return _toNode;
	}

}
