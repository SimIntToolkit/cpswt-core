package org.cpswt.coa;


import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;

public class COALoader {

    File coaDefinitionFile;
    File coaSelectionFile;

    public COALoader(File coaDefinitionFile, File coaSelectionFile) {
        this.coaDefinitionFile = coaDefinitionFile;
        this.coaSelectionFile = coaSelectionFile;
    }

    public COAGraph loadGraph() throws IOException {
        COAGraph coaGraph = new COAGraph();

        JsonFactory jsonFactory = new JsonFactory();
        ObjectMapper objectMapper = new ObjectMapper(jsonFactory);

        JsonParser jsonParser = jsonFactory.createParser(this.coaDefinitionFile);
        while(jsonParser.nextToken() != JsonToken.END_OBJECT) {
            String coaName = jsonParser.getCurrentName();

            // JsonToken coaToken = jsonParser.getCurrentToken();
            // objectMapper.readValue(coaToken.asByteArray(), )
        }

        return coaGraph;
    }
}
