package org.cpswt.hla;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cpswt.coa.node.COAAction;
import org.cpswt.coa.node.COAAwaitN;
import org.cpswt.coa.node.COADuration;
import org.cpswt.coa.node.COAFork;
import org.cpswt.coa.COAGraph;
import org.cpswt.coa.node.COANode;
import org.cpswt.coa.node.COAOutcome;
import org.cpswt.coa.node.COAOutcomeFilter;
import org.cpswt.coa.node.COAProbabilisticChoice;
import org.cpswt.coa.node.COARandomDuration;
import org.cpswt.coa.node.COASyncPoint;
import org.cpswt.coa.node.COANodeType;
import hla.rti.LogicalTime;
import hla.rti.RTIambassador;
import org.portico.impl.hla13.types.DoubleTime;

import java.lang.reflect.Method;
import java.util.*;

/**
 * COA executor for FederationManager
 */
public class COAExecutor {
    private static final Logger logger = LogManager.getLogger(COAExecutor.class);

    COAGraph _coaGraph = new COAGraph();

    // Cache class and methods for COAOutcomeFilter evaluation
    Class outcomeFilterEvaluatorClass = null;
    HashMap<COAOutcomeFilter, Method> _outcomeFilter2EvalMethodMap = new HashMap<COAOutcomeFilter, Method>();

    final String federationId;
    final String federateId;
    final double lookahead;
    final boolean terminateOnCoaFinish;
    final RTIambassador rti;

    Map<Integer, ArrayList<ArrivedInteraction>> _arrived_interactions = new HashMap<Integer, ArrayList<ArrivedInteraction>>();

    COAExecutorEventListener coaExecutorEventListener;
    public void setCoaExecutorEventListener(COAExecutorEventListener listener) {
        this.coaExecutorEventListener = listener;
    }

    public COAExecutor(String federationId, String federateId, double lookahead, boolean terminateOnCoaFinish, RTIambassador rti) {
        this.federationId = federationId;
        this.federateId = federateId;
        this.lookahead = lookahead;
        this.terminateOnCoaFinish = terminateOnCoaFinish;
        this.rti = rti;
    }

    public void setCOAGraph(COAGraph graph) {
        this._coaGraph = graph;
    }

    public void initializeCOAGraph() {
        this._coaGraph.initialize();
    }

    private void terminateSimulation() {
        if(this.coaExecutorEventListener != null) {
            this.coaExecutorEventListener.onTerminateRequested();
        }
    }
    private double getCurrentTime() {
        if(this.coaExecutorEventListener != null) {
            return this.coaExecutorEventListener.onCurrentTimeRequested();
        }
        return 0.0;
    }

    private void executeCOAAction(COAAction nodeAction) {
        // Create interaction to be sent
        String interactionClassName = nodeAction.getInteractionClassName();
        InteractionRoot interactionRoot = InteractionRoot.create_interaction(interactionClassName);

        // First check for simulation termination
        if (SimEnd.match(interactionRoot.getClassHandle())) {
            terminateSimulation();
        }


        // It is not a SimEnd interaction, send normally
        interactionRoot.setParameter("sourceFed", this.federateId);
        interactionRoot.setParameter("originFed", this.federateId);
        Map<String, String> nameValueParamPairs = nodeAction.getNameValueParamPairs();
        for (String paramName : nameValueParamPairs.keySet()) {
            String paramValue = nameValueParamPairs.get(paramName);
            interactionRoot.setParameter(paramName, paramValue);
        }

        // Create timestamp for the interaction
        double tmin = getCurrentTime() + lookahead + (lookahead / 10000.0);

        // Send the interaction
        try {
            interactionRoot.sendInteraction(this.rti, tmin);
        } catch (Exception e) {
            logger.error("Failed to send interaction: " + interactionRoot);
            logger.error(e);
        }
        logger.info("Successfully sent interaction '{}' at time '{}'", interactionClassName, tmin);
    }

    public void executeCOAGraph() {
        HashSet<COANode> currentRootNodes = new HashSet<COANode>(_coaGraph.getCurrentRootNodes());

        // If at any point, there are no COA nodes remaining to execute, and
        // the experiment was configured to terminate when all COA nodes have
        // been executed, terminate the federation.
        if (currentRootNodes.size() == 0 && terminateOnCoaFinish) {
            terminateSimulation();

        }


        // There may be COA nodes still to be executed, see if some root nodes
        // can be executed.
        boolean nodeExecuted = false;
        for (COANode n : currentRootNodes) {
            COANodeType nodeType = n.getNodeType();
            if (nodeType == COANodeType.SyncPoint) {
                COASyncPoint nodeSyncPt = (COASyncPoint) n;
                double timeToReachSyncPt = nodeSyncPt.getSyncTime() - getCurrentTime();
                if (timeToReachSyncPt > 0.0) {
                    // SyncPt is not reached, nothing to be done
                } else {
                    // SyncPt reached, mark executed
                    _coaGraph.markNodeExecuted(n, getCurrentTime());
                    nodeExecuted = true;
                }
            } else if (nodeType == COANodeType.AwaitN) {
                COAAwaitN nodeAwaitN = (COAAwaitN) n;
                if (!nodeAwaitN.getIsRequiredNumOfBranchesFinished()) {
                    // AwaitN is not reached, nothing to be done
                } else {
                    // AwaitN reached, mark executed
                    _coaGraph.markNodeExecuted(n, getCurrentTime());
                    nodeExecuted = true;
                }
            } else if (nodeType == COANodeType.Duration || nodeType == COANodeType.RandomDuration) {
                COADuration nodeDuration = null;
                if (nodeType == COANodeType.Duration) {
                    nodeDuration = (COADuration) n;
                } else {
                    nodeDuration = (COARandomDuration) n;
                }

                if (!nodeDuration.getIsTimerOn()) {
                    // Start executing duration element
                    nodeDuration.startTimer(getCurrentTime());
                } else {
                    // Check if the duration node has executed
                    if (getCurrentTime() >= nodeDuration.getEndTime()) {
                        // Duration node finished, mark executed
                        _coaGraph.markNodeExecuted(n, getCurrentTime());
                        nodeExecuted = true;
                    }
                }
            } else if (nodeType == COANodeType.Fork) {
                COAFork nodeFork = (COAFork) n;
                boolean isDecisionPoint = nodeFork.getIsDecisionPoint(); // TODO: handle decision points

                // As of now Fork is always executed as soon as it is encountered
                _coaGraph.markNodeExecuted(n, getCurrentTime());
                nodeExecuted = true;
            } else if (nodeType == COANodeType.ProbabilisticChoice) {
                COAProbabilisticChoice nodeProbChoice = (COAProbabilisticChoice) n;
                boolean isDecisionPoint = nodeProbChoice.getIsDecisionPoint(); // TODO: handle decision points

                // As of now Probabilistic Choice is always executed as soon as it is encountered
                _coaGraph.markNodeExecuted(n, getCurrentTime());
                nodeExecuted = true;
            } else if (nodeType == COANodeType.Action) {
                COAAction nodeAction = (COAAction) n;

                // As of now Action is always executed as soon as it is encountered
                executeCOAAction(nodeAction);
                _coaGraph.markNodeExecuted(n, getCurrentTime());
                nodeExecuted = true;
            } else if (nodeType == COANodeType.Outcome) {
                COAOutcome nodeOutcome = (COAOutcome) n;
                if (!nodeOutcome.getIsTimerOn()) {
                    // Start executing Outcome element
                    nodeOutcome.startTimer(getCurrentTime());
                } else {
                    boolean outcomeExecutable = checkIfOutcomeExecutableAndUpdateArrivedInteraction(nodeOutcome);
                    if (outcomeExecutable) {
                        _coaGraph.markNodeExecuted(n, getCurrentTime());
                        nodeExecuted = true;
                    }
                }
            } else if (nodeType == COANodeType.OutcomeFilter) {
                COAOutcomeFilter outcomeFilter = (COAOutcomeFilter) n;
                COAOutcome outcomeToFilter = outcomeFilter.getOutcome();

                // Update last arrived interaction in the corresponding Outcome node
                checkIfOutcomeExecutableAndUpdateArrivedInteraction(outcomeToFilter);

                boolean filterEvaluation = false;
                if (outcomeToFilter == null) {
                    System.err.println("WARNING! OutcomeFilter not connected to an Outcome: " + outcomeFilter);
                    filterEvaluation = true;
                } else {
                    // Evaluate filter, first get evaluator class
                    if (outcomeFilterEvaluatorClass == null) {
                        String class2Load = this.federationId + ".COAOutcomeFilterEvaluator";
                        try {
                            outcomeFilterEvaluatorClass = Class.forName(class2Load);
                            if (outcomeFilterEvaluatorClass == null) {
                                System.err.println("ERROR! Cannot find evaluator class for OutcomeFilter: " + outcomeFilter);
                            }
                        } catch (Exception e) {
                            System.err.println("Exception caught while evaluating OutcomeFilter: " + outcomeFilter);
                            e.printStackTrace();
                        }
                    }

                    // Now, get the filter method to invoke for evaluation in the evaluator class
                    if (outcomeFilterEvaluatorClass != null) {
                        Method outcomeFilterEvalMethod = null;
                        if (_outcomeFilter2EvalMethodMap.containsKey(outcomeFilter)) {
                            // Method already exists in the cache, no need to use reflection loading
                            outcomeFilterEvalMethod = _outcomeFilter2EvalMethodMap.get(outcomeFilter);
                        } else {
                            // Method doesn't exist in the cache, use reflection to load it
                            String filterID = outcomeFilter.getId();
                            filterID = filterID.replaceAll("-", "_");
                            String method2Load = "evaluateFilter_" + filterID;

                            try {
                                outcomeFilterEvalMethod = outcomeFilterEvaluatorClass.getMethod(method2Load, new Class[]{COAOutcome.class});
                                if (outcomeFilterEvalMethod == null) {
                                    System.err.println("ERROR! Cannot find evaluation method in " + outcomeFilterEvaluatorClass.getName() + " for OutcomeFilter: " + outcomeFilter);
                                } else {
                                    _outcomeFilter2EvalMethodMap.put(outcomeFilter, outcomeFilterEvalMethod);
                                }
                            } catch (Exception e) {
                                System.err.println("Exception caught while finding evaluation method in " + outcomeFilterEvaluatorClass.getName() + " for OutcomeFilter: " + outcomeFilter);
                                e.printStackTrace();
                            }
                        }
                        if (outcomeFilterEvalMethod != null) {
                            // Method loaded, now call evaluation function to evaluate OutcomeFilter
                            try {
                                Object retval = outcomeFilterEvalMethod.invoke(null, outcomeToFilter);
                                if (retval instanceof Boolean) {
                                    filterEvaluation = (Boolean) retval;
                                }
                            } catch (Exception e) {
                                System.err.println("Exception caught while evaluating OutcomeFilter: " + outcomeFilter);
                                e.printStackTrace();
                            }
                        } else {
                            System.err.println("ERROR! Failed to load OutcomeFilter evaluation method for OutcomeFilter: " + outcomeFilter);
                        }
                    }
                }

                if (filterEvaluation) {
                    _coaGraph.markNodeExecuted(n, getCurrentTime());
                    nodeExecuted = true;
                }
                // System.out.println("Result of evaluation of filter for outcome: " + outcomeToFilter.getName() + " = " + filterEvaluation + ". Interaction it contained was: " + outcomeToFilter.getLastArrivedInteraction());
            }
        }

        if (nodeExecuted) {
            // Some paths were executed, execute more enabled nodes, if any
            executeCOAGraph();
        }

        // Clear arrived interactions that we no longer need to keep in memory
        clearUnusedArrivedInteractionsForOutcomes();
    }

    private boolean checkIfOutcomeExecutableAndUpdateArrivedInteraction(COAOutcome nodeOutcome) {
        // Check if the outcome can be executed
        boolean outcomeExecutable = false;
        if (_arrived_interactions.keySet().contains(nodeOutcome.getInteractionClassHandle())) {
            ArrayList<ArrivedInteraction> arrivedIntrs = _arrived_interactions.get(nodeOutcome.getInteractionClassHandle());
            if (arrivedIntrs != null) {
                for (ArrivedInteraction arrivedIntr : arrivedIntrs) {
                    if (arrivedIntr.getArrivalTime() > nodeOutcome.getAwaitStartTime()) {
                        // Awaited interaction arrived after outcome was initiated
                        // System.out.println("Setting last arrived interaction in outcome node " + nodeOutcome.getName() + ": " + arrivedIntr.getInteractionRoot());
                        nodeOutcome.setLastArrivedInteraction(arrivedIntr.getInteractionRoot());
                        outcomeExecutable = true;
                    }
                }
                // We shouldn't clear the arrivedIntrs here because all parallel
                // Outcomes could wait for the same interaction
                // Instead we use clearUnusedArrivedInteractionsForOutcomes()
                // at the end of a step of COA execution
            }
        }

        return outcomeExecutable;
    }

    private void clearUnusedArrivedInteractionsForOutcomes() {
        Collection<ArrayList<ArrivedInteraction>> allArrivedIntrLists = _arrived_interactions.values();
        for (ArrayList<ArrivedInteraction> aArrivedIntrList : allArrivedIntrLists) {
            aArrivedIntrList.clear();
        }
    }

    // This method and arrivalTimes are used by the COA Orchestrator while executing
    // Outcome elements of the COA sequence graph.
    public void updateArrivedInteractions(int handle, LogicalTime time, InteractionRoot receivedIntr) throws Exception {
        ArrayList<ArrivedInteraction> intrArrivalTimeList = null;
        if (!_arrived_interactions.keySet().contains(handle)) {
            intrArrivalTimeList = new ArrayList<ArrivedInteraction>();
            _arrived_interactions.put(handle, intrArrivalTimeList);
        } else {
            intrArrivalTimeList = _arrived_interactions.get(handle);
        }

        DoubleTime arrivalTime = new DoubleTime();
        if (time != null) {
            arrivalTime.setTo(time);
        } else {
            arrivalTime.setTime(getCurrentTime());
        }

        ArrivedInteraction arrivedIntr = new ArrivedInteraction(receivedIntr, arrivalTime.getTime());
        intrArrivalTimeList.add(arrivedIntr);
        // System.out.println("Adding interaction to arrived list: " + receivedIntr);
    }
}
