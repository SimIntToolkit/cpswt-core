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

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cpswt.util.RandomSingleton;

/**
 * This is the main class to represent the COA sequence graph.
 */
public class COAGraph {

	private static final Logger logger = LogManager.getLogger(COAGraph.class);

	private HashMap<String, COANode> _allNodes = new HashMap<String, COANode>();

	private HashSet<COANode> _rootNodes = new HashSet<COANode>();

	private HashSet<COANode> _currentRootNodes = new HashSet<COANode>();

	private HashSet<COAEdge> _allEdges = new HashSet<COAEdge>();

	private HashMap<COANode, HashSet<COAEdge>> _edgesFromNodeMap = new HashMap<COANode, HashSet<COAEdge>>();

	public static final String PROP_GRAPH_STATUS = "property_graph_status";

	/**
	 * Property change event handling.
	 */
	private PropertyChangeSupport changeSupport = new PropertyChangeSupport(
			this);

	public COAGraph() {
	}

	public void initialize() {
		// Mark COA nodes at the beginning of the graph as active from the
		// beginning
		for (COANode n : _currentRootNodes) {
			n.setActive();
		}
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		for (COANode n : _rootNodes) {
			buffer.append(n.getSuccessorGraphString(""));
			buffer.append("\n");
		}
		return buffer.toString();
	}

	public HashMap<String, COANode> getAllCOANodes() {
		return _allNodes;
	}

	public HashSet<COANode> getRootNodes() {
		return _rootNodes;
	}

	public HashSet<COANode> getCurrentRootNodes() {
		return _currentRootNodes;
	}

	public HashSet<COAEdge> getAllCOAEdges() {
		return _allEdges;
	}

	public void addNode(COANode node) {
		if (node == null) {
			throw new RuntimeException("(" + this
					+ "): Node supplied to add is NULL");
		}
		_allNodes.put(node.getUniqueID(), node);
		_rootNodes.add(node);
		_currentRootNodes.add(node);
	}

	public COANode getNode(String nodeUniqueID) {
		if (_allNodes.isEmpty() || !_allNodes.containsKey(nodeUniqueID)) {
			throw new IllegalStateException(
					"Searched node was not found - make sure all nodes are added before being referred.");
		}
		return _allNodes.get(nodeUniqueID);
	}

	public void addEdge(COAEdge edge) {
		if (edge == null) {
			throw new RuntimeException("(" + this
					+ "): Edge supplied to add is NULL");
		}

		COANode fromNode = edge.getFromNode();
		COANode toNode = edge.getToNode();

		if (_allNodes.isEmpty()
				|| !_allNodes.containsKey(fromNode.getUniqueID())
				|| !_allNodes.containsKey(toNode.getUniqueID())) {
			throw new RuntimeException(
					"All nodes must be added before edges are added in the graph.");
		}

		// First, check for correct use of Outcome and OutcomeFilter nodes and
		// update link of Outcome node in OutcomeFilter node
		if (toNode.getNodeType() == COANode.NODE_TYPE.NODE_OUTCOME_FILTER) {
			if (toNode.getPredecessors().size() > 0) {
				throw new RuntimeException("OutcomeFilter "
						+ toNode.getNodeName()
						+ " must be preceeded only by an Outcome node!");
			}
			if (fromNode.getNodeType() != COANode.NODE_TYPE.NODE_OUTCOME) {
				throw new RuntimeException(
						"OutcomeFilter "
								+ toNode.getNodeName()
								+ " node can only be preceeded by a node of type Outcome!");
			}

			((COAOutcomeFilter) toNode).setOutcome((COAOutcome) fromNode);
		}

		_rootNodes.remove(toNode);
		_currentRootNodes.remove(toNode);
		fromNode.addSucccessor(toNode);
		toNode.addPredecessor(fromNode);

		_allEdges.add(edge);

		if (!_edgesFromNodeMap.containsKey(fromNode)) {
			HashSet<COAEdge> fromNodeEdges = new HashSet<COAEdge>();
			_edgesFromNodeMap.put(fromNode, fromNodeEdges);
		}
		_edgesFromNodeMap.get(fromNode).add(edge);
	}

	public void markNodeExecuted(COANode node, double nodeExecutedTime) {
		if (node == null) {
			throw new RuntimeException(
					"Node give to be marked as executed is NULL.");
		}
		if (nodeExecutedTime < 0) {
			throw new RuntimeException(
					"Time of node execution given as negative.");
		}
		if (_currentRootNodes.isEmpty() || !_currentRootNodes.contains(node)) {
			throw new RuntimeException(
					"Invalid node give to be marked as executed: " + node);
		}

		node.setExecuted(nodeExecutedTime);
		_currentRootNodes.remove(node);
		logger.debug("Node executed in sequence graph: {}", node);

		// If the node is a probabilistic choice node, choose one successor
		// and remove the rest, if any.
		if (COANode.NODE_TYPE.NODE_PROBABILISTIC_CHOICE == node.getNodeType()) {
			HashMap<COANode, Double> successorsWithCumuProb = new HashMap<COANode, Double>();
			HashSet<COAEdge> outEdges = _edgesFromNodeMap.get(node);
			COANode chosenSuccessor = null;

			// Normalize probabilities on the successor elements
			if (outEdges != null) {
				// Get the sum of all probabilities
				double probabilitiesSum = 0;
				for (COAEdge edge : outEdges) {
					COAFlowWithProbabilityEdge edgeWithProb = (COAFlowWithProbabilityEdge) edge;
					probabilitiesSum += edgeWithProb.getProbability();
				}
				// Divide individual probabilities by the sum, so that the total
				// of all probabilities becomes equal to 1.0
				for (COAEdge edge : outEdges) {
					COAFlowWithProbabilityEdge edgeWithProb = (COAFlowWithProbabilityEdge) edge;
					double updatedProbability = edgeWithProb.getProbability()
							/ probabilitiesSum;
					logger.debug("Normalizing probability of execution of node '{}' to {}",edgeWithProb.getToNode(), updatedProbability);
					edgeWithProb.updateProbability(updatedProbability);
				}
				// Update successors map with cumulative probabilities for
				// successor selection
				double cumulativeProbabilitySum = 0;
				for (COAEdge edge : outEdges) {
					COAFlowWithProbabilityEdge edgeWithProb = (COAFlowWithProbabilityEdge) edge;
					// Update cumulative sum using the normalized probability of
					// successors
					cumulativeProbabilitySum += edgeWithProb.getProbability();
					successorsWithCumuProb.put(edge.getToNode(),
							cumulativeProbabilitySum);
				}
			}

			// Choose
			Random rand = RandomSingleton.instance();
			double choiceProb = rand.nextDouble();
			logger.debug("ProbabilisticChoice cumulative probability chosen = {}", choiceProb);
			for (COANode n : successorsWithCumuProb.keySet()) {
				if (successorsWithCumuProb.get(n) >= choiceProb) {
					chosenSuccessor = n;
					logger.debug("\tThus, choosing successor node: {}", n);
					break;
				}
			}

			// Remove not chosen successors
			for (COANode n : successorsWithCumuProb.keySet()) {
				if (n != chosenSuccessor) {
					n.setEnabledAsChoice(false);
				}
			}
		}

		// Successor status updates and graph updates
		HashSet<COANode> successors = node.getSuccessors();
		if (successors.size() > 0) {
			for (COANode succ : successors) {
				// Take care of multiple directed edges coming to the successor
				// node. Add successor to the currentRootNodes if: (A) the
				// successor is not already in currentRootNodes, and (B) the
				// successor wasn't already executed, and (C) none of its
				// predecessors are still in the currentRootNodes (except when
				// the successor is of type SyncPt or AwaitN).
				// Also, before activating make sure that the chosen successor
				// is enabled as choice.
				boolean succAlreadyInCurrentRootNodes = _currentRootNodes
						.contains(succ);
				if (!succAlreadyInCurrentRootNodes
						&& COANode.NODE_STATUS.NODE_EXECUTED != succ.getNodeStatus()) {
					boolean aSuccPredecessorInCurrentRootNodes = false;
					for (COANode succPred : succ.getPredecessors()) {
						if (_currentRootNodes.contains(succPred)) {
							aSuccPredecessorInCurrentRootNodes = true;
							break; // inner for-loop
						}
					}
					if (!aSuccPredecessorInCurrentRootNodes
							|| COANode.NODE_TYPE.NODE_SYNC_PT == succ.getNodeType()
							|| COANode.NODE_TYPE.NODE_AWAITN == succ.getNodeType()) {
						if (COANode.NODE_TYPE.NODE_SYNC_PT == node.getNodeType()) {
							// TODO: If node that executed is SyncPt, then
							// enable only those successors that have valid
							// branches finished (i.e., handle exceptions)
							if (succ.enabledAsChoice()) {
								_currentRootNodes.add(succ);
								succ.setActive();
							}
						} else {
							if (succ.enabledAsChoice()) {
								_currentRootNodes.add(succ);
								succ.setActive();
							}
						}
					}
				}

				// Do post-processing on successors, if any
				if (COANode.NODE_TYPE.NODE_SYNC_PT == succ.getNodeType()) {
					COASyncPt nodeSyncPt = (COASyncPt) succ;
					nodeSyncPt.incrementBranchesFinished();
				} else if (COANode.NODE_TYPE.NODE_AWAITN == succ.getNodeType()) {
					COAAwaitN nodeAwaitN = (COAAwaitN) succ;
					nodeAwaitN.incrementBranchesFinished();
				}
			}
		}

		changeSupport.firePropertyChange(PROP_GRAPH_STATUS, 0, 1);
	}

	public void addPropertyChangeListener(String propertyName,
			PropertyChangeListener listener) {
		changeSupport.addPropertyChangeListener(propertyName, listener);
	}
}
