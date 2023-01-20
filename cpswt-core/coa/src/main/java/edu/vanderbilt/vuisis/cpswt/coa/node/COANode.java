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

package org.cpswt.coa.node;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashSet;

/**
 * A simple base class for a COA node in the COA sequence graph.
 */
public class COANode {

    @JsonIgnore
    private static final Logger logger = LogManager.getLogger(COANode.class);

    @JsonProperty("name")
	private String name;

    @JsonProperty("ID")
	private String id;

    @JsonProperty("nodeType")
	private COANodeType nodeType;

	private COANodeStatus nodeStatus;
	private double nodeExecutedTime;
	private boolean enabledAsChoice;

	private HashSet<COANode> predecessors = new HashSet<COANode>();
	private HashSet<COANode> successors = new HashSet<COANode>();

	COANode() {}

	COANode(COANodeType nodeType) {
		this.nodeType = nodeType;
		initializeNode();
	}

	public COANode(String name, String id, COANodeType nodeType) {
		this(nodeType);

		this.name = name;
		this.id = id;
		initializeNode();
	}

	public void initializeNode() {
		this.nodeStatus = COANodeStatus.Inactive;
		this.enabledAsChoice = true;
		this.nodeExecutedTime = -1;
	}

	@Override
	public String toString() {
		return String.format("Name: %s, Type: %s, Status: %s, NodeExecutedTime: %f",
                name, nodeType, nodeStatus, nodeExecutedTime);
	}

	public String getSuccessorGraphString(String prefix) {
		StringBuffer buffer = new StringBuffer();
		buffer.append(prefix + name);
		for (COANode n : successors) {
			buffer.append(prefix + "\n\t");
			buffer.append(n.getSuccessorGraphString(prefix + "\t"));
		}
		return buffer.toString();
	}

	public String getName() {
		return name;
	}
	void setName(String name) {
	    this.name = name;
    }

	public String getId() {
		return id;
	}
	public COANodeType getNodeType() {
		return nodeType;
	}
	public COANodeStatus getNodeStatus() {
		return nodeStatus;
	}
	public boolean enabledAsChoice() {
		return enabledAsChoice;
	}
	public double getNodeExecutedTime() {
		return nodeExecutedTime;
	}

	public HashSet<COANode> getPredecessors() {
		return predecessors;
	}
	public HashSet<COANode> getSuccessors() {
		return successors;
	}

	public void setActive() {
		nodeStatus = COANodeStatus.Active;
	}
	public void setEnabledAsChoice(boolean enabledAsChoice) {
		this.enabledAsChoice = enabledAsChoice;
	}

	public void setExecuted(double nodeExecutedTime) {
		nodeStatus = COANodeStatus.Executed;
		this.nodeExecutedTime = nodeExecutedTime;
	}

	public void addPredecessor(COANode predecessor) {
		if (predecessor == null) {
		    logger.error("({}): Predecessor supplied to add is NULL. Skipping...", this.toString());
		    return;
		}
		predecessors.add(predecessor);
	}

	public void addSuccessor(COANode successor) {
		if (successor == null) {
			logger.error("({}): Successor supplied to add is NULL. Skipping...", this.toString());
			return;
		}
		successors.add(successor);
	}
}
