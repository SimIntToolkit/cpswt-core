package org.cpswt.coa;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cpswt.coa.edge.COAEdge;
import org.cpswt.coa.edge.COAEdgeType;
import org.cpswt.coa.node.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.Map;

public class COALoader {

    private static final Logger logger = LogManager.getLogger(COALoader.class);

    Path coaDefinitionPath;
    Path coaSelectionPath;

    public COALoader(Path coaDefinitionPath, Path coaSelectionPath) {
        this.coaDefinitionPath = coaDefinitionPath;
        this.coaSelectionPath = coaSelectionPath;
    }

    public COAGraph loadGraph() throws IOException {
        COAGraph coaGraph = new COAGraph();

        byte[] jsonData = Files.readAllBytes(this.coaDefinitionPath);
        ObjectMapper objectMapper = new ObjectMapper();

        JsonNode jsonRoot = objectMapper.readTree(jsonData);

        for(Iterator<Map.Entry<String, JsonNode>> iter = jsonRoot.fields(); iter.hasNext(); ) {
            Map.Entry<String, JsonNode> coaKV = iter.next();

            // COA coa = new COA(coaKV.getKey());

            JsonNode nodes = coaKV.getValue().get("nodes");
            JsonNode edges = coaKV.getValue().get("edges");

            for(JsonNode jsonNode : nodes) {
                COANodeType nodeType = COANodeType.valueOf(jsonNode.get("nodeType").asText(COANodeType.Unknown.getName()));

                if(nodeType == COANodeType.Unknown) {
                    logger.warn("COA node type ({}) unknown! Skipping...", jsonNode.get("nodeType").asText());
                    continue;
                }

                COANode node = objectMapper.treeToValue(jsonNode, nodeType.getCOANodeClass());
                coaGraph.addNode(node);
                logger.debug("Adding node to the COA graph: {}", node);
            }

            for(JsonNode jsonNode : edges) {
                COAEdgeType edgeType = COAEdgeType.valueOf(jsonNode.get("type").asText("Unknown"));

                if(edgeType == COAEdgeType.Unknown) {
                    logger.warn("COA edge type ({}) unknown! Skipping...", jsonNode.get("type").asText());
                    continue;
                }

                COAEdge edge = objectMapper.treeToValue(jsonNode, edgeType.getCOAEdgeClass());
                coaGraph.addEdge(edge);
                logger.debug("Adding edge to the COA graph: {}", edge);
            }
        }

        logger.info("Loaded COAGraph successfully");
        logger.debug("Loaded the following COAGraph:\n{}" + coaGraph);

        return coaGraph;
    }
}
