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
 * A simple base class for a COA node in the COA sequence graph.
 */
public class COANode {

	private String _nodeName;

	private String _uniqueID;

	private COANodeType _nodeType;

	private COANodeStatus _nodeStatus;

	private double _nodeExecutedTime;
	
	private boolean _enabledAsChoice;

	private HashSet<COANode> _predecessors = new HashSet<COANode>();

	private HashSet<COANode> _successors = new HashSet<COANode>();

	public COANode(String nodeName, String uniqueID, COANodeType nodeType) {

		this._nodeName = nodeName;
		this._uniqueID = uniqueID;
		this._nodeType = nodeType;

		this._nodeStatus = COANodeStatus.Inactive;
		this._enabledAsChoice = true;
		this._nodeExecutedTime = -1;
	}

	@Override
	public String toString() {
		return "Name: " + _nodeName + ", Type: " + _nodeType + ", Status: "
				+ _nodeStatus + ", NodeExecutedTime: " + _nodeExecutedTime;
	}

	public String getSuccessorGraphString(String prefix) {
		StringBuffer buffer = new StringBuffer();
		buffer.append(prefix + _nodeName);
		for (COANode n : _successors) {
			buffer.append(prefix + "\n\t");
			buffer.append(n.getSuccessorGraphString(prefix + "\t"));
		}
		return buffer.toString();
	}

	public String getNodeName() {
		return _nodeName;
	}

	public String getUniqueID() {
		return _uniqueID;
	}

	public COANodeType getNodeType() {
		return _nodeType;
	}

	public COANodeStatus getNodeStatus() {
		return _nodeStatus;
	}
	
	public boolean enabledAsChoice() {
		return _enabledAsChoice;
	}

	public String getNodeStatusImagePath() {
		return "/org.cpswt/gui/coa/resources/" + _nodeStatus + _nodeType + ".bmp";
	}

	public double getNodeExecutedTime() {
		return _nodeExecutedTime;
	}

	public HashSet<COANode> getPredecessors() {
		return _predecessors;
	}

	public HashSet<COANode> getSuccessors() {
		return _successors;
	}

	public void setActive() {
		_nodeStatus = COANodeStatus.Active;
	}
	
	public void setEnabledAsChoice(boolean enabledAsChoice) {
		_enabledAsChoice = enabledAsChoice;
	}

	public void setExecuted(double nodeExecutedTime) {
		_nodeStatus = COANodeStatus.Executed;
		_nodeExecutedTime = nodeExecutedTime;
	}

	public void addPredecessor(COANode predecessor) {
		if (predecessor == null) {
			throw new RuntimeException("(" + this
					+ "): Predecessor supplied to add is NULL");
		}
		_predecessors.add(predecessor);
	}

	public void addSucccessor(COANode succecessor) {
		if (succecessor == null) {
			throw new RuntimeException("(" + this
					+ "): Succecessor supplied to add is NULL");
		}
		_successors.add(succecessor);
	}
}
