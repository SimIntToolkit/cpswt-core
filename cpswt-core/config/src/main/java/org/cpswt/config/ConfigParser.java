package org.cpswt.config;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;

/**
 * ConfigParser
 */
public class ConfigParser {

    public static <TConfig> TConfig parseConfig(File configFile, final Class<TConfig> clazz) throws IOException {

        ObjectMapper mapper = new ObjectMapper(new JsonFactory());
        TConfig configObj = mapper.readValue(configFile, clazz);

        return configObj;
    }
}
