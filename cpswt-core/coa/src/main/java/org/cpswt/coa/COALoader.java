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
 *
 * @author Himanshu Neema
 */

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
import java.util.ArrayList;

public class COALoader {

    private static final Logger logger = LogManager.getLogger(COALoader.class);

    Path coaDefinitionPath;
    Path coaSelectionPath;
    String coaSelectionToExecute;

    public COALoader(Path coaDefinitionPath, Path coaSelectionPath, String coaSelectionToExecute) {
        this.coaDefinitionPath = coaDefinitionPath;
        this.coaSelectionPath = coaSelectionPath;
        this.coaSelectionToExecute = coaSelectionToExecute;
    }

    public ArrayList<String> getListOfIDsofCOAsToExecute() throws IOException {
        ArrayList<String> coaIDs = new ArrayList<>();
        if (coaSelectionToExecute == null || "".equals(coaSelectionToExecute)) {
            return null;
        }

        byte[] jsonData = Files.readAllBytes(this.coaSelectionPath);
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonRoot = objectMapper.readTree(jsonData);

        logger.trace("Looking at COASelection for IDs: {}", coaSelectionToExecute);
        for(Iterator<Map.Entry<String, JsonNode>> iter = jsonRoot.fields(); iter.hasNext(); ) {
            Map.Entry<String, JsonNode> coaKV = iter.next();
            logger.trace("Next COASelection to check is: {}", coaKV.getKey());
            if(coaSelectionToExecute.equals(coaKV.getKey())) { // Found COA-Selection
                logger.trace("This COASelection matches what's needed");
                JsonNode coas = coaKV.getValue();
                logger.trace("This COASelection has value: {}", coas);
                for(JsonNode coaArray: coas) {
                    logger.trace("In this COASelection, next COA to get its ID is: {}", coaArray);
                    for(JsonNode coa : coaArray) {
                        // Has only 1 COA in it, so break after processing
                        logger.trace("Got COA as : {}", coa);
                        String coaID = coa.get("Name").asText("");
                        logger.trace("Got COA's ID as {}", coaID);
                        coaIDs.add(coaID);

                        break;
                    }
                }

                return coaIDs.size() > 0 ? coaIDs : null;
            }
        }

        return null;
    }

    public COAGraph loadGraph() throws IOException {

        COAGraph coaGraph = new COAGraph();

        ArrayList<String> idsOfCoasToExecute = null;
        boolean coaSelectionSpecified = false;

        if(coaSelectionToExecute != null && !"".equals(coaSelectionToExecute)) {
            coaSelectionSpecified = true;
            logger.trace("COASelection was specified as: {}", coaSelectionToExecute);
            idsOfCoasToExecute = getListOfIDsofCOAsToExecute();
            if(idsOfCoasToExecute == null || idsOfCoasToExecute.size() == 0) {
                logger.trace("COASelection was specified, couldn't find IDs");
                return coaGraph; // If COA-Selection given, but wrongly, return empty graph
            }
        }

        byte[] jsonData = Files.readAllBytes(this.coaDefinitionPath);
        ObjectMapper objectMapper = new ObjectMapper();

        JsonNode jsonRoot = objectMapper.readTree(jsonData);

        for(Iterator<Map.Entry<String, JsonNode>> iter = jsonRoot.fields(); iter.hasNext(); ) {
            Map.Entry<String, JsonNode> coaKV = iter.next();

            String coaName = coaKV.getKey();
            if(coaSelectionSpecified && !idsOfCoasToExecute.contains(coaName)) {
                continue;
            }

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
