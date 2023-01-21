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

package edu.vanderbilt.vuisis.cpswt.coa.edge;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.vanderbilt.vuisis.cpswt.coa.node.COANode;

import java.util.HashSet;

/**
 * A simple class for a COA edge in the COA sequence graph.
 */
public class COAEdge {

    @JsonProperty("type")
	private COAEdgeType edgeType;

	@JsonProperty("ID")
	private String id;

	@JsonIgnore
	private HashSet<String> branchesFinishedCondition = new HashSet<String>();

	@JsonProperty("name")
    private String name;

	@JsonProperty("fromNode")
    private String fromNodeId;

	@JsonProperty("toNode")
    private String toNodeId;

	@JsonIgnore
	private COANode fromNode;
	@JsonIgnore
	private COANode toNode;

	COAEdge() {}

    COAEdge(COAEdgeType edgeType) {
	    this.edgeType = edgeType;
    }

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
	    return String.format("[%s] %s --to--> %s", name, fromNode.getName(), toNode.getName());
	}

	public COAEdgeType getEdgeType() {
		return edgeType;
	}

    public void setEdgeType(COAEdgeType edgeType) {
        this.edgeType = edgeType;
    }

    public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFromNodeId() {
        return fromNodeId;
    }

    public void setFromNodeId(String fromNodeId) {
        this.fromNodeId = fromNodeId;
    }

    public String getToNodeId() {
        return toNodeId;
    }

    public void setToNodeId(String toNodeId) {
        this.toNodeId = toNodeId;
    }

    public HashSet<String> getBranchesFinishedCondition() {
		return branchesFinishedCondition;
	}

	public COANode getFromNode() {
		return fromNode;
	}

    public void setFromNode(COANode fromNode) {
        this.fromNode = fromNode;
    }

    public COANode getToNode() {
		return toNode;
	}

	public void setToNode(COANode toNode) {
        this.toNode = toNode;
    }
}
