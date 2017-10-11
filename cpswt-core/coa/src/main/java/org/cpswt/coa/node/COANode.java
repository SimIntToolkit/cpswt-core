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

package org.cpswt.coa.node;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cpswt.coa.enums.COANodeStatus;
import org.cpswt.coa.enums.COANodeType;

import java.util.HashSet;

/**
 * A simple base class for a COA node in the COA sequence graph.
 */
public class COANode {

    private static final Logger logger = LogManager.getLogger(COANode.class);

	private String nodeName;
	private String uniqueID;
	private COANodeType nodeType;
	private COANodeStatus nodeStatus;
	private double nodeExecutedTime;
	private boolean enabledAsChoice;

	private HashSet<COANode> predecessors = new HashSet<COANode>();
	private HashSet<COANode> successors = new HashSet<COANode>();

	public COANode(String nodeName, String uniqueID, COANodeType nodeType) {

		this.nodeName = nodeName;
		this.uniqueID = uniqueID;
		this.nodeType = nodeType;

		this.nodeStatus = COANodeStatus.Inactive;
		this.enabledAsChoice = true;
		this.nodeExecutedTime = -1;
	}

	@Override
	public String toString() {
		return String.format("Name: %s, Type: %s, Status: %s, NodeExecutedTime: %f",
				nodeName, nodeType, nodeStatus, nodeExecutedTime);
	}

	public String getSuccessorGraphString(String prefix) {
		StringBuffer buffer = new StringBuffer();
		buffer.append(prefix + nodeName);
		for (COANode n : successors) {
			buffer.append(prefix + "\n\t");
			buffer.append(n.getSuccessorGraphString(prefix + "\t"));
		}
		return buffer.toString();
	}

	public String getNodeName() {
		return nodeName;
	}
	public String getUniqueID() {
		return uniqueID;
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
