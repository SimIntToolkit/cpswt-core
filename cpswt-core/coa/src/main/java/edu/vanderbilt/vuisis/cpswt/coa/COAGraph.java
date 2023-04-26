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

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.vanderbilt.vuisis.cpswt.hla.InteractionRootInterface;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.vanderbilt.vuisis.cpswt.coa.edge.*;
import edu.vanderbilt.vuisis.cpswt.coa.node.*;
import edu.vanderbilt.vuisis.cpswt.utils.RandomSingleton;

import hla.rti.RTIambassador;
import edu.vanderbilt.vuisis.cpswt.hla.InteractionRoot;

/**
 * This is the main class to represent the COA sequence graph.
 */
public class COAGraph {

	private static final Logger logger = LogManager.getLogger(COAGraph.class);

	private final Map<String, COANode> _coaNodeIdToCOANodeMap = new HashMap<>();

	private final Set<COANode> _rootNodeSet = new HashSet<>();

	private final Set<COANode> _currentRootNodeSet = new HashSet<>();

	private final Map<String, COAEdge> _coaEdgeIdToCOAEdgeMap = new HashMap<>();

	private final Map<COANode, Set<COAEdge>> _edgesFromNodeMap = new HashMap<>();

	private final Map<String, Set<String>> _coaIdToCOANodeIdSetMap = new HashMap<>();

	private final Map<String, Set<String>> _coaIdToCOAEdgeIdSetMap = new HashMap<>();

	private final Map<String, Boolean> _coaIdToRepeatMap = new HashMap<>();

	private final Map<String, Map<String, InteractionRoot>> _coaIdToCOAOutcomeInteractionMapMap = new HashMap<>();

	public COAGraph() {
	}

	public void setCurrentRootNodesAsActive() {
		// Mark COA nodes at the beginning of the graph as active from the
		// beginning
		for (COANode coaNode : _currentRootNodeSet) {
			coaNode.setActive();
		}
	}

	public void setCOAIdToRepeatMap(Map<String, Boolean> coaIdToRepeatMap) {
		_coaIdToRepeatMap.clear();
		_coaIdToRepeatMap.putAll(coaIdToRepeatMap);
	}

	public void initialize(RTIambassador rti) {
		synchronized(rti) {
			// Mark COA nodes at the beginning of the graph as active from the
			// beginning
			for (COANode coaNode : _currentRootNodeSet) {
				coaNode.setActive();
			}

			// Make sure all interaction classes are loaded and pub-sub is configured
			for (COANode coaNode: _coaNodeIdToCOANodeMap.values()) {
				if (COANodeType.Action == coaNode.getNodeType()) {
					COAAction actionNode = (COAAction) coaNode;
					InteractionRoot.publish_interaction(actionNode.getInteractionClassName(), rti);
				} else if (COANodeType.Outcome == coaNode.getNodeType()) {
					COAOutcome outcomeNode = (COAOutcome) coaNode;
					InteractionRoot.subscribe_interaction(outcomeNode.getInteractionClassName(), rti);
				}
			}
		}
	}

	@Override
	public String toString() {
		StringBuilder buffer = new StringBuilder();
		for (COANode coaNode : _rootNodeSet) {
			buffer.append(coaNode.getSuccessorGraphString(""));
			buffer.append("\n");
		}
		return buffer.toString();
	}

	public List<COANode> getCurrentRootNodes() {

		LinkedList<COANode> currentRootNodeList = new LinkedList<>();

		for(COANode currentRootNode: _currentRootNodeSet) {
			if (isRootCOANode(currentRootNode)) {
				currentRootNodeList.addLast(currentRootNode);
			} else {
				currentRootNodeList.addFirst(currentRootNode);
			}
		}

		return currentRootNodeList;
	}

	public void addNode(COANode node) {
		if (node == null) {
			throw new RuntimeException("(" + this
					+ "): Node supplied to add is NULL");
		}
		_coaNodeIdToCOANodeMap.put(node.getId(), node);
		_rootNodeSet.add(node);
		_currentRootNodeSet.add(node);
	}

	public COANode getNode(String nodeUniqueID) {
		if (_coaNodeIdToCOANodeMap.isEmpty() || !_coaNodeIdToCOANodeMap.containsKey(nodeUniqueID)) {
			throw new IllegalStateException(
					"Searched node was not found - make sure all nodes are added before being referred."
			);
		}
		return _coaNodeIdToCOANodeMap.get(nodeUniqueID);
	}

	public void addEdge(COAEdge edge) {
		if (edge == null) {
			throw new RuntimeException("(" + this
					+ "): Edge supplied to add is NULL");
		}

		// fill fromNode and toNode after deserialization
		if(edge.getFromNode() == null) {
			edge.setFromNode(this.getNode(edge.getFromNodeId()));
		}
		if(edge.getToNode() == null) {
			edge.setToNode(this.getNode(edge.getToNodeId()));
		}

		COANode fromNode = edge.getFromNode();
		COANode toNode = edge.getToNode();

		if (_coaNodeIdToCOANodeMap.isEmpty()
				|| !_coaNodeIdToCOANodeMap.containsKey(fromNode.getId())
				|| !_coaNodeIdToCOANodeMap.containsKey(toNode.getId())) {
			throw new RuntimeException(
					"All nodes must be added before edges are added in the graph.");
		}

		// First, check for correct use of Outcome and OutcomeFilter nodes and
		// update link of Outcome node in OutcomeFilter node
		if (toNode.getNodeType() == COANodeType.OutcomeFilter) {
			if (toNode.getPredecessors().size() > 0) {
				throw new RuntimeException("OutcomeFilter "
						+ toNode.getName()
						+ " must be preceeded only by an Outcome node!");
			}
			if (fromNode.getNodeType() != COANodeType.Outcome) {
				throw new RuntimeException(
						"OutcomeFilter "
								+ toNode.getName()
								+ " node can only be preceeded by a node of type Outcome!");
			}

			((COAOutcomeFilter) toNode).setOutcome((COAOutcome) fromNode);
		}

		_rootNodeSet.remove(toNode);
		_currentRootNodeSet.remove(toNode);
		fromNode.addSuccessor(toNode);
		toNode.addPredecessor(fromNode);

		_coaEdgeIdToCOAEdgeMap.put(edge.getId(), edge);

		if (!_edgesFromNodeMap.containsKey(fromNode)) {
			HashSet<COAEdge> fromNodeEdges = new HashSet<>();
			_edgesFromNodeMap.put(fromNode, fromNodeEdges);
		}
		_edgesFromNodeMap.get(fromNode).add(edge);
	}

	public boolean isRootCOANode(COANode coaNode) {
		return _rootNodeSet.contains(coaNode);
	}

	public void initializeRepeatMaps() {

		for(COANode coaNode: _coaNodeIdToCOANodeMap.values()) {
			String coaId = coaNode.getCOAId();
			if (!_coaIdToCOANodeIdSetMap.containsKey(coaId)) {
				_coaIdToCOANodeIdSetMap.put(coaId, new HashSet<>());
			}
			_coaIdToCOANodeIdSetMap.get(coaId).add(coaNode.getId());
		}

		for(COANode rootNode: _rootNodeSet) {
			rootNode.setIsRootCOANode(true);
		}

		for(COAEdge coaEdge: _coaEdgeIdToCOAEdgeMap.values()) {
			String coaId = coaEdge.getCOAId();
			if (!_coaIdToCOAEdgeIdSetMap.containsKey(coaId)) {
				_coaIdToCOAEdgeIdSetMap.put(coaId, new HashSet<>());
			}
			_coaIdToCOAEdgeIdSetMap.get(coaId).add(coaEdge.getId());
		}
	}

	public void markNodeExecuted(COANode origNode, double nodeExecutedTime) {
		if (origNode == null) {
			throw new RuntimeException(
					"Node give to be marked as executed is NULL.");
		}
		if (nodeExecutedTime < 0) {
			throw new RuntimeException(
					"Time of node execution given as negative.");
		}
		if (_currentRootNodeSet.isEmpty() || !_currentRootNodeSet.contains(origNode)) {
			throw new RuntimeException("Invalid node give to be marked as executed: " + origNode);
		}

		COANode node = origNode;
		if (origNode.getIsRootCOANode() && _coaIdToRepeatMap.get(origNode.getCOAId())) {
			node = copyCOA(origNode);
			origNode.initializeNode();
		} else {
			_currentRootNodeSet.remove(node);
		}

		node.setExecuted(nodeExecutedTime);
		logger.debug("Node executed in sequence graph: {}", node);

		// If the node is a probabilistic choice node, choose one successor
		// and remove the rest, if any.
		if (COANodeType.Outcome == node.getNodeType()) {

			COAOutcome coaOutcome = (COAOutcome)node;
			registerCOAOutcome(coaOutcome);

		} else if (COANodeType.ProbabilisticChoice == node.getNodeType()) {

			Map<COANode, Double> successorsWithCumuProb = new HashMap<>();
			Set<COAEdge> outEdges = _edgesFromNodeMap.get(node);
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
		Set<COANode> successors = node.getSuccessors();
		logger.trace("COAGraph: Node {} has {} successors", node, successors.size());
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
				logger.trace("COAGraph: Checking out the successor {}", succ);
				boolean succAlreadyInCurrentRootNodes = _currentRootNodeSet.contains(succ);
				if (!succAlreadyInCurrentRootNodes
						&& COANodeStatus.Executed != succ.getNodeStatus()) {
					boolean aSuccPredecessorInCurrentRootNodes = false;
					for (COANode succPred : succ.getPredecessors()) {
						if (_currentRootNodeSet.contains(succPred)) {
							aSuccPredecessorInCurrentRootNodes = true;
							break; // inner for-loop
						}
					}
					if (
							succ.enabledAsChoice() && (
									!aSuccPredecessorInCurrentRootNodes
											|| COANodeType.SyncPoint == succ.getNodeType()
											|| COANodeType.AwaitN== succ.getNodeType()
							)
					) {
						_currentRootNodeSet.add(succ);
						succ.setActive();
					}
				}

				// Do post-processing on successors, if any
				if (COANodeType.SyncPoint == succ.getNodeType()) {
					COASyncPoint nodeSyncPt = (COASyncPoint) succ;
					nodeSyncPt.incrementBranchesFinished();
				} else if (COANodeType.AwaitN == succ.getNodeType()) {
					COAAwaitN nodeAwaitN = (COAAwaitN) succ;
					nodeAwaitN.incrementBranchesFinished();
				}
			}
		}
	}

	public void registerCOAOutcome(COAOutcome coaOutcome) {
		String coaId = coaOutcome.getCOAId();
		if (!_coaIdToCOAOutcomeInteractionMapMap.containsKey(coaId)) {
			_coaIdToCOAOutcomeInteractionMapMap.put(coaId, new HashMap<>());
		}
		Map<String, InteractionRoot> outcomeInteractionMap = _coaIdToCOAOutcomeInteractionMapMap.get(coaId);
		outcomeInteractionMap.put(coaOutcome.getName(), coaOutcome.getLastInteraction());
	}

	public InteractionRoot getCOAActionInteraction(COAAction coaAction, double currentTime) {
		String interactionClassName = coaAction.getInteractionClassName();
		logger.trace("COAExecutor:executeCOAAction: Got interaction class name: {}... now trying to create interaction..", interactionClassName);

		String coaId = coaAction.getCOAId();

		InteractionRoot interactionRoot = InteractionRoot.create_interaction(interactionClassName);
		for(
				Map.Entry<InteractionRootInterface.ClassAndPropertyName, Object> entry :
				coaAction.getNameValueParamPairs().entrySet()
		) {
			Object value = entry.getValue();
			if (value instanceof String) {
				String stringValue = (String)value;

				Pattern substitutionPatternWithBraces = Pattern.compile("^((?:.*[^\\\\])?(?:\\\\\\\\)*)\\$\\{(.+)}(.*)$");
				Matcher substitutionMatcherWithBraces = substitutionPatternWithBraces.matcher(stringValue);

				Pattern substitutionPatternWithoutBraces = Pattern.compile("^((?:.*[^\\\\])?(?:\\\\\\\\)*)\\$(.+)$");
				Matcher substitutionMatcherWithoutBraces = substitutionPatternWithoutBraces.matcher(stringValue);

				String beforeString = null;
				String substitutionSpecifier = null;
				String afterString = null;

				if (substitutionMatcherWithBraces.matches()) {

					beforeString = substitutionMatcherWithBraces.group(1);
					substitutionSpecifier = substitutionMatcherWithBraces.group(2);
					afterString = substitutionMatcherWithBraces.group(3);

				} else if (substitutionMatcherWithoutBraces.matches()) {

					beforeString = substitutionMatcherWithoutBraces.group(1);
					substitutionSpecifier = substitutionMatcherWithoutBraces.group(2);
					afterString = "";
				}

				if (substitutionSpecifier != null) {
					int periodPosition = substitutionSpecifier.indexOf('.');
					if (periodPosition >= 0) {
						String outcomeName = substitutionSpecifier.substring(0, periodPosition);
						if (_coaIdToCOAOutcomeInteractionMapMap.containsKey(coaId)) {
							Map<String, InteractionRoot> coaOutcomeInteractionMap =
									_coaIdToCOAOutcomeInteractionMapMap.get(coaId);
							if (coaOutcomeInteractionMap.containsKey(outcomeName)) {
								InteractionRoot outcomeInteraction = coaOutcomeInteractionMap.get(outcomeName);

								String parameterName = substitutionSpecifier.substring(periodPosition + 1);
								if (parameterName.startsWith("(") && parameterName.endsWith(")")) {
									parameterName = parameterName.substring(1, parameterName.length() - 1);
								}

								String className = outcomeInteraction.getInstanceHlaClassName();
								int commaPosition = parameterName.indexOf(',');
								if (commaPosition >= 0) {
									className = parameterName.substring(0, commaPosition);
									parameterName = parameterName.substring(commaPosition + 1);
								}
								if (outcomeInteraction.hasParameter(className, parameterName)) {
									if (beforeString.isEmpty() && afterString.isEmpty()) {
										value = outcomeInteraction.getParameter(className, parameterName);
									} else {
										value = beforeString + outcomeInteraction.getParameter(className, parameterName)
												+ afterString;
									}
								}
							}
						}
					} else if (substitutionSpecifier.equalsIgnoreCase("time")) {
						value = currentTime;
					}
				}
			}
			interactionRoot.setParameter(entry.getKey(), value);
		}

		return interactionRoot;
	}

	private static int suffixNumber = 0;

	private static String getSuffix() {
		return Integer.toString(suffixNumber++);
	}

	// PACKAGE PRIVATE
	void resetCOAOutcome(COAOutcome coaOutcome, COAOutcomeFilter coaOutcomeFilter) {
		coaOutcomeFilter.initializeNode();
		coaOutcome.getLastArrivedInteraction().removeCoaId(coaOutcome.getCOAId());
		coaOutcome.initializeNode();
		coaOutcome.setActive();
		_currentRootNodeSet.add(coaOutcome);

	}

	private COANode copyCOA(COANode coaNode) {
		String coaId = coaNode.getCOAId();
		Map<String, COANode> originalCOANodeIdToCOANodeCopyMap = new HashMap<>();
		String idSuffix = getSuffix();

		for(String coaNodeId: _coaIdToCOANodeIdSetMap.get(coaId)) {
			COANode originalCOANode = _coaNodeIdToCOANodeMap.get(coaNodeId);
			COANode coaNodeCopy = originalCOANode.copy(originalCOANodeIdToCOANodeCopyMap, idSuffix);

			String coaNodeCopyId = coaNodeCopy.getId();
			_coaNodeIdToCOANodeMap.put(coaNodeCopyId, coaNodeCopy);

			String newCOAId = coaNodeCopy.getCOAId();
			if (!_coaIdToCOANodeIdSetMap.containsKey(newCOAId)) {
				_coaIdToCOANodeIdSetMap.put(newCOAId, new HashSet<>());
			}
			_coaIdToCOANodeIdSetMap.get(newCOAId).add(coaNodeCopyId);
		}

		for(String coaEdgeId: _coaIdToCOAEdgeIdSetMap.get(coaId)) {
			COAEdge originalCOAEdge = _coaEdgeIdToCOAEdgeMap.get(coaEdgeId);
			COAEdge coaEdgeCopy = originalCOAEdge.copy(originalCOANodeIdToCOANodeCopyMap, idSuffix);

			String coaEdgeCopyId = coaEdgeCopy.getId();
			_coaEdgeIdToCOAEdgeMap.put(coaEdgeCopyId, coaEdgeCopy);

			String newCOAId = coaEdgeCopy.getCOAId();
			if (!_coaIdToCOAEdgeIdSetMap.containsKey(newCOAId)) {
				_coaIdToCOAEdgeIdSetMap.put(newCOAId, new HashSet<>());
			}
			_coaIdToCOAEdgeIdSetMap.get(newCOAId).add(coaEdgeCopyId);

			COANode fromNode = coaEdgeCopy.getFromNode();
			if (!_edgesFromNodeMap.containsKey(fromNode)) {
				_edgesFromNodeMap.put(fromNode, new HashSet<>());
			}
			_edgesFromNodeMap.get(fromNode).add(coaEdgeCopy);
		}

		return originalCOANodeIdToCOANodeCopyMap.get(coaNode.getId());
	}

	public void purgeCOAs() {
		Set<String> coaIdSet = new HashSet<>(_coaIdToCOANodeIdSetMap.keySet());
		for(COANode coaNode: _currentRootNodeSet) {
			coaIdSet.remove(coaNode.getCOAId());
		}

		for(String coaId: coaIdSet) {
			_coaIdToCOAOutcomeInteractionMapMap.remove(coaId);

			Set<String> coaNodeIdSet = _coaIdToCOANodeIdSetMap.get(coaId);
			for(String coaNodeId: coaNodeIdSet) {
				COANode coaNode = _coaNodeIdToCOANodeMap.get(coaNodeId);
				_edgesFromNodeMap.remove(coaNode);
				_coaNodeIdToCOANodeMap.remove(coaNodeId);
			}
			_coaIdToCOANodeIdSetMap.remove(coaId);

			Set<String> coaEdgeIdSet = _coaIdToCOAEdgeIdSetMap.get(coaId);
			for(String codeEdgeId: coaEdgeIdSet) {
				_coaEdgeIdToCOAEdgeMap.remove(codeEdgeId);
			}
			_coaIdToCOAEdgeIdSetMap.remove(coaId);
		}
	}

}
