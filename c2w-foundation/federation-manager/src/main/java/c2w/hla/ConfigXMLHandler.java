package c2w.hla;

import c2w.coa.*;
import c2w.util.FedUtil;
import hla.rti.InteractionClassNotDefined;
import hla.rti.RTIambassador;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import java.lang.reflect.Method;
import java.util.*;

class ConfigXMLHandler extends DefaultHandler {

    private String federationName;
    private String federateId;
    private Random rand4Dur;
    private RTIambassador rti;

    private InteractionRoot injectedInteraction;

    public InteractionRoot getInjectedInteraction() {
        return this.injectedInteraction;
    }

    private double injectionTime;

    public double getInjectionTime() {
        return this.injectionTime;
    }

    private Set<Double> pauseTimes = new HashSet<Double>();

    public Set<Double> getPauseTimes() {
        return this.pauseTimes;
    }

    private List<Integer> monitoredInteractions = new ArrayList<Integer>();

    public List<Integer> getMonitoredInteractions() {
        return this.monitoredInteractions;
    }

    private Set<String> expectedFederates = new HashSet<String>();

    public Set<String> getExpectedFederates() {
        return this.expectedFederates;
    }

    private boolean parseFailed = false;
    public boolean getParseFailed() {
        return this.parseFailed;
    }

    private COANode coaNode;
    private COAGraph coaGraph;
    public COAGraph getCoaGraph() {
        return this.coaGraph;
    }
    private List<InteractionRoot> initInteractions = new ArrayList<InteractionRoot>();
    public List<InteractionRoot> getInitInteractions() {
        return this.initInteractions;
    }

    private Map<Double, List<InteractionRoot>> scriptInteractions = new TreeMap<Double, List<InteractionRoot>>();
    public Map<Double, List<InteractionRoot>> getScriptInteractions() {
        return this.scriptInteractions;
    }

    private String logLevel;

    public ConfigXMLHandler(String federationName, String federateId, Random rand4Dur, String logLevel, RTIambassador rti) {
        this.federationName = federationName;
        this.federateId = federateId;
        this.rand4Dur = rand4Dur;
        this.logLevel = logLevel;
        this.rti = rti;

        this.coaGraph = new COAGraph();
    }

    public void startElement(String uri, String localName, String qName, Attributes attributes) {

        try {
            if ("interaction".equals(qName)) {

                String interactionClassName = attributes.getValue("name");
                String simpleInteractionClassName = interactionClassName.substring(interactionClassName.lastIndexOf('.') + 1);

                String packageName = this.federationName;
                try {
                    Class.forName(packageName + "." + simpleInteractionClassName);
                } catch (Exception e) {
                    System.err.println("WARNING:  Could not load class \"" + simpleInteractionClassName + "\"");
                    e.printStackTrace();
                }

                InteractionRoot.publish(interactionClassName, this.rti);
                System.out.println("publish: " + interactionClassName + "(" + InteractionRoot.get_handle(interactionClassName) + ")");

                // Himanshu: Enabling Manager Logging to Database
                InteractionRoot.enablePublishLog(simpleInteractionClassName, "manager", "IMPORTANT", this.logLevel);

                InteractionRoot interactionRoot = InteractionRoot.create_interaction(interactionClassName);
                interactionRoot.setParameter("sourceFed", this.federateId);
                interactionRoot.setParameter("originFed", this.federateId);

                int noAttributes = attributes.getLength();
                for (int ix = 0; ix < noAttributes; ++ix) {
                    String name = attributes.getQName(ix);
                    if ("name".equals(name)) continue;
                    String val = attributes.getValue(ix);
                    interactionRoot.setParameter(name, val);
                }

                this.injectedInteraction = interactionRoot;

            } else if ("injection_time".equals(qName)) {

                if (this.injectedInteraction == null) {
                    System.err.println("ERROR!  no interaction to inject at specified time");
                    return;
                }

                String timeString = attributes.getValue("value");
                if (timeString == null) {
                    System.err.println("ERROR:  interaction does not contain time of interaction.");
                    return;
                }

                this.injectionTime = Double.parseDouble(timeString);

            } else if ("pause".equals(qName)) {

                String time = attributes.getValue("time");
                this.pauseTimes.add(Double.parseDouble(time));

            } else if ("monitor".equals(qName)) {

                String interactionClassName = attributes.getValue("name");
                InteractionRoot.subscribe(interactionClassName, this.rti);
                int interactionClassHandle = InteractionRoot.get_handle(interactionClassName);
                System.out.println("subscribe: " + interactionClassName + "(" + interactionClassHandle + ")");

                // int noAttributes = attributes.getLength();
                // Set< Integer > parameterHandleSet = new HashSet< Integer >();
                // for( int ix = 0 ; ix < noAttributes ; ++ix ) {
                //     String name = attributes.getQName( ix );
                //     if (  "name".equals( name )  ) continue;
                //     int parameter_handle = InteractionRoot.get_parameter_handle( interactionClassName, name );
                //     if ( parameter_handle != -1 ) parameterHandleSet.add( parameter_handle );
                // }
                this.monitoredInteractions.add(interactionClassHandle);

            } else if ("expect".equals(qName)) {

                String federateType = null;
                int noAttributes = attributes.getLength();

                for (int ix = 0; ix < noAttributes; ++ix) {
                    String attributeName = attributes.getQName(ix);
                    String attributeValue = attributes.getValue(ix);
                    if (attributeName.equals("federateType")) federateType = attributeValue;
                }

                this.expectedFederates.add(federateType);

            } else if ("coaNode".equals(qName)) {
                int noAttributes = attributes.getLength();
                String nodeType = null;
                String nodeName = null;
                String nodeUniqueID = null;
                HashMap<String, String> attrsMap = new HashMap<String, String>();
                for (int ix = 0; ix < noAttributes; ++ix) {
                    String attributeName = attributes.getQName(ix);
                    String attributeValue = attributes.getValue(ix);
                    if (attributeName.equals("ID")) {
                        nodeUniqueID = attributeValue;
                    } else if (attributeName.equals("name")) {
                        nodeName = attributeValue;
                    } else if (attributeName.equals("nodeType")) {
                        nodeType = attributeValue;
                    } else {
                        attrsMap.put(attributeName, attributeValue);
                    }
                }
                if (COANode.NODE_TYPE.NODE_SYNC_PT.getName().equals(nodeType)) {
                    double nodeSyncTime = Double.parseDouble(attrsMap.get("time"));
                    int nodeNumBranchesToFinish = Integer.parseInt(attrsMap.get("minBranchesToSync"));
                    this.coaNode = new COASyncPt(nodeName, nodeUniqueID, nodeSyncTime, nodeNumBranchesToFinish);
                } else if (COANode.NODE_TYPE.NODE_AWAITN.getName().equals(nodeType)) {
                    int nodeNumBranchesToAwait = Integer.parseInt(attrsMap.get("minBranchesToAwait"));
                    this.coaNode = new COAAwaitN(nodeName, nodeUniqueID, nodeNumBranchesToAwait);
                } else if (COANode.NODE_TYPE.NODE_DURATION.getName().equals(nodeType)) {
                    double nodeDuration = Double.parseDouble(attrsMap.get("time"));
                    this.coaNode = new COADuration(nodeName, nodeUniqueID, nodeDuration);
                } else if (COANode.NODE_TYPE.NODE_RANDOM_DURATION.getName().equals(nodeType)) {
                    double lowerBound = Double.parseDouble(attrsMap.get("lowerBound"));
                    double upperBound = Double.parseDouble(attrsMap.get("upperBound"));
                    this.coaNode = new COARandomDuration(nodeName, nodeUniqueID, lowerBound, upperBound, this.rand4Dur);
                } else if (COANode.NODE_TYPE.NODE_FORK.getName().equals(nodeType)) {
                    boolean nodeIsDecisionPoint = Boolean.parseBoolean(attrsMap.get("isDecisionPoint"));
                    this.coaNode = new COAFork(nodeName, nodeUniqueID, nodeIsDecisionPoint);
                } else if (COANode.NODE_TYPE.NODE_PROBABILISTIC_CHOICE.getName().equals(nodeType)) {
                    boolean nodeIsDecisionPoint = Boolean.parseBoolean(attrsMap.get("isDecisionPoint"));
                    this.coaNode = new COAProbabilisticChoice(nodeName, nodeUniqueID, nodeIsDecisionPoint);
                } else if (COANode.NODE_TYPE.NODE_ACTION.getName().equals(nodeType)) {
                    String nodeInteractionName = attrsMap.get("interactionName");
                    this.coaNode = new COAAction(nodeName, nodeUniqueID, nodeInteractionName);

                    // Make sure the interaction corresponding to the action is published
                    String simpleInteractionClassName = nodeInteractionName.substring(nodeInteractionName.lastIndexOf('.') + 1);
                    String packageName = this.federationName;
                    String fullyQualifiedClassname = packageName + "." + simpleInteractionClassName;
                    String fullyQualifiedGenericC2WTClassname = "c2w.hla." + simpleInteractionClassName;
                    Class intrClass = FedUtil.loadClassByName(fullyQualifiedClassname);
                    if (intrClass == null) {
                        intrClass = FedUtil.loadClassByName(fullyQualifiedGenericC2WTClassname);
                    }
                    if (intrClass != null) {
                        InteractionRoot.publish(nodeInteractionName, this.rti);
                        int intrClassHandle = InteractionRoot.get_handle(nodeInteractionName);
                        System.out.println("publish: " + nodeInteractionName + "(" + intrClassHandle + ")");

                        // Himanshu: Enabling Manager Logging to Database
                        enableManagerPubSubLog(intrClass, simpleInteractionClassName, true);
                    } else {
                        System.err.println("ERROR:  Could not load class \"" + simpleInteractionClassName + "\"... OR c2w.hla." + simpleInteractionClassName);
                        this.coaNode = null;
                    }

                    // Set interaction's parameter values
                    for (String paramName : attrsMap.keySet()) {
                        if (!"interactionName".equals(paramName)) {
                            COAAction actionNode = (COAAction) this.coaNode;
                            actionNode.addNameValueParamPair(paramName, attrsMap.get(paramName));
                        }
                    }
                } else if (COANode.NODE_TYPE.NODE_OUTCOME.getName().equals(nodeType)) {
                    String nodeInteractionName = attrsMap.get("interactionName");
                    this.coaNode = new COAOutcome(nodeName, nodeUniqueID, nodeInteractionName);

                    // Make sure the interaction corresponding to the action is subscribed
                    String simpleInteractionClassName = nodeInteractionName.substring(nodeInteractionName.lastIndexOf('.') + 1);
                    String packageName = this.federationName;
                    String fullyQualifiedClassname = packageName + "." + simpleInteractionClassName;
                    String fullyQualifiedGenericC2WTClassname = "c2w.hla." + simpleInteractionClassName;
                    Class intrClass = FedUtil.loadClassByName(fullyQualifiedClassname);
                    if (intrClass == null) {
                        intrClass = FedUtil.loadClassByName(fullyQualifiedGenericC2WTClassname);
                    }
                    if (intrClass != null) {
                        InteractionRoot.subscribe(nodeInteractionName, this.rti);
                        int intrClassHandle = InteractionRoot.get_handle(nodeInteractionName);
                        System.out.println("subscribe: " + nodeInteractionName + "(" + intrClassHandle + ")");

                        ((COAOutcome) this.coaNode).setInteractionClassHandle(intrClassHandle);

                        // Himanshu: Enable Manager Logging to Database
                        enableManagerPubSubLog(intrClass, simpleInteractionClassName, false);
                    } else {
                        System.err.println("ERROR:  Could not load class \"" + simpleInteractionClassName + "\"... OR c2w.hla." + simpleInteractionClassName);
                        this.coaNode = null;
                    }
                } else if (COANode.NODE_TYPE.NODE_OUTCOME_FILTER.getName().equals(nodeType)) {
                    this.coaNode = new COAOutcomeFilter(nodeName, nodeUniqueID);
                } else {
                    // Unknown node type
                    System.out.println("WARNING! Unsupported node type in COA sequence graph: " + nodeType);
                    this.coaNode = null;
                }

            } else if ("coaEdge".equals(qName)) {
                int noAttributes = attributes.getLength();
                String edgeType = null;
                String edgeFlowID = null;
                String fromNodeID = null;
                String toNodeID = null;
                HashMap<String, String> attrsMap = new HashMap<String, String>();
                for (int ix = 0; ix < noAttributes; ++ix) {
                    String attributeName = attributes.getQName(ix);
                    String attributeValue = attributes.getValue(ix);
                    if (attributeName.equals("type")) {
                        edgeType = attributeValue;
                    } else if (attributeName.equals("flowID")) {
                        edgeFlowID = attributeValue;
                    } else if (attributeName.equals("fromNode")) {
                        fromNodeID = attributeValue;
                    } else if (attributeName.equals("toNode")) {
                        toNodeID = attributeValue;
                    } else {
                        attrsMap.put(attributeName, attributeValue);
                    }
                }
                COANode fromNode = this.coaGraph.getNode(fromNodeID);
                COANode toNode = this.coaGraph.getNode(toNodeID);
                if (fromNode != null && toNode != null) {
                    if (COAEdge.EDGE_TYPE.EDGE_COAFLOW.getName().equals(edgeType) || COAEdge.EDGE_TYPE.EDGE_OUTCOME2FILTER.getName().equals(edgeType) || COAEdge.EDGE_TYPE.EDGE_FILTER2COAELEMENT.getName().equals(edgeType)) {
                        // TODO: For clarity we may actually use different classes for other edge types
                        COAEdge coaEdge = new COAEdge(COAEdge.EDGE_TYPE.EDGE_COAFLOW, fromNode, toNode, edgeFlowID, null);
                        this.coaGraph.addEdge(coaEdge);
                        System.out.println("Added COAEdge: " + coaEdge);
                    } else if (COAEdge.EDGE_TYPE.EDGE_COAFLOW_WITH_PROBABILITY.getName().equals(edgeType)) {
                        double probability = Double.parseDouble(attrsMap.get("probability"));
                        COAFlowWithProbabilityEdge coaProbChoiceEdge = new COAFlowWithProbabilityEdge(fromNode, toNode, edgeFlowID, probability, null);
                        this.coaGraph.addEdge(coaProbChoiceEdge);
                        System.out.println("Added COAEdge: " + coaProbChoiceEdge);
                    } else if (COAEdge.EDGE_TYPE.EDGE_COAEXCEPTION.getName().equals(edgeType)) {
                        String branchesFinishedCondition = attrsMap.get("branchesFinishedCondition");
                        branchesFinishedCondition = branchesFinishedCondition.trim();
                        String[] flowIDs = branchesFinishedCondition.split("\\s+");
                        HashSet<String> flowIDsAsSet = new HashSet<String>();
                        Collections.addAll(flowIDsAsSet, flowIDs);
                        COAEdge coaEdge = new COAEdge(COAEdge.EDGE_TYPE.EDGE_COAEXCEPTION, fromNode, toNode, edgeFlowID, flowIDsAsSet);
                        this.coaGraph.addEdge(coaEdge);
                        System.out.println("Added COAEdge: " + coaEdge);
                    }
                }
            }
        } catch (Throwable e) {
            System.out.println("XML parsing error");
            e.printStackTrace();
            this.parseFailed = true;
        }
    }

    public void endElement(String uri, String localName, String qName) {

        try {
            if ("interaction".equals(qName)) {
                if (this.injectionTime < 0) {
                    this.initInteractions.add(this.injectedInteraction);
                } else {
                    if (!this.scriptInteractions.containsKey(this.injectionTime))
                        this.scriptInteractions.put(this.injectionTime, new ArrayList<InteractionRoot>());
                    this.scriptInteractions.get(this.injectionTime).add(this.injectedInteraction);
                }
                this.injectedInteraction = null;
                this.injectionTime = -1;
            } else if ("coaNode".equals(qName)) {
                this.coaGraph.addNode(this.coaNode);
                System.out.println("Added COANode: " + this.coaNode);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Himanshu: Enabling Manager Logging to Database
    private void enableManagerPubSubLog(Class intrClass, String simpleIntrClassName, boolean isPublishLog) {
        if (intrClass == null) {
            return;
        }
        String method2Load = isPublishLog ? "enablePublishLog" : "enableSubscribeLog";
        try {
            Method method = intrClass.getMethod(method2Load, new Class[]{String.class, String.class, String.class, String.class});
            if (method == null) {
                System.err.println("ERROR! FederationManager: Cannot find method '" + method2Load + "' in class '" + simpleIntrClassName + "'");
            } else {
                method.invoke(null, simpleIntrClassName, "manager", "IMPORTANT", this.logLevel);
            }
        } catch (Exception e) {
            System.err.println("FederationManager: Exception caught while calling '" + method2Load + "' on interaction class '" + simpleIntrClassName + "'");
            e.printStackTrace();
        }
    }

}
