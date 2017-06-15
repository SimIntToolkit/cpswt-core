package c2w.hla;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.cli.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

/**
 * Parser for Federate parameters
 */
public class FederateParameterParser {
    static final Logger logger = LogManager.getLogger(FederateParameter.class);

    Options cliOptions;

    /**
     * Default constructor
     */
    public FederateParameterParser() {
        this.cliOptions = new Options();
    }

    /**
     * Instantiate a parser with pre-defined CLI options.
     * @param cliOptions
     */
    public FederateParameterParser(Options cliOptions) {
        this.cliOptions = cliOptions;
    }

    /**
     * Add more CLI options to the existing ones.
     * @param options
     */
    public void addCLIOptions(Options options) {
        for(Option opt : options.getOptions()) {
            this.cliOptions.addOption(opt);
        }
    }

    static final Set<Class<?>> supportedTypes = new HashSet();
    static {
        supportedTypes.add(Double.class);
        supportedTypes.add(Integer.class);
        supportedTypes.add(Boolean.class);
        supportedTypes.add(Long.class);
    }

    public <T extends FederateParameter> T parseArgs(final String[] args, final Class<T> clazz) {

        CommandLineParser parser  = new DefaultParser();

        try {

            CommandLine commandLine = parser.parse(this.cliOptions, args);
            T currentParameter = this.parseCommandLine(commandLine, clazz);

            return currentParameter;
        }
        catch (ParseException parseExp) {
            logger.error("Parsing CLI arguments failed. Reason: " + parseExp.getMessage(), parseExp);
            System.exit(-1);
        }

        return null;

    }

    <T extends FederateParameter> T parseCommandLine(CommandLine commandLine, final Class<T> clazz) {
        try {

            File configFile = null;
            ObjectMapper mapper = new ObjectMapper(new JsonFactory());

            String mConfigFilePath = commandLine.getOptionValue("configFile");

            T federateParameter = null;

            // fallback to default config from resources
            if (mConfigFilePath == null) {
                ClassLoader classLoader = clazz.getClassLoader();
                URL resource = classLoader.getResource("federateConfig.default.json");
                if (resource == null) {
                    String configFileFromEnv = System.getenv("CPSWTNG_FEDERATE_CONFIG");
                    if (configFileFromEnv != null) {
                        configFile = new File(configFileFromEnv);
                    }
                } else {
                    configFile = new File(resource.getFile());
                }
            }
            // load passed config file
            else {
                configFile = new File(mConfigFilePath);
            }

            if (configFile != null) {
                federateParameter = mapper.readValue(configFile, clazz);
            }

            // if default config file was not found
            if (federateParameter == null) {
                federateParameter = clazz.newInstance();
            }

            // manual override of any parameters
            for (Option opt : this.cliOptions.getOptions()) {
                String optVal = commandLine.getOptionValue(opt.getArgName());
                if (optVal != null) {
                    Class<?> optType = (Class<?>) opt.getType();

                    Field optField = clazz.getDeclaredField(opt.getArgName());
                    boolean accessible = optField.isAccessible();

                    optField.setAccessible(true);

                    if(optType == String.class) {
                        optField.set(federateParameter, optType.cast(optVal));
                    }
                    else if(supportedTypes.contains(optType)) {
                        Object castedValue = optType.cast(optType.getDeclaredMethod("valueOf", String.class).invoke(null, optVal));
                        optField.set(federateParameter, castedValue);
                    }
                    else {
                        logger.error("{} type not supported as command line argument. Skipping...", optType.getName());
                    }

                    optField.setAccessible(accessible);
                }
            }

            return federateParameter;

        } catch (InstantiationException instEx) {
            instEx.printStackTrace();
        } catch (JsonParseException jsonParseEx) {
            jsonParseEx.printStackTrace();
        } catch (IllegalAccessException iaccEx) {
            iaccEx.printStackTrace();
        } catch (NoSuchFieldException noSuchFieldEx) {
            noSuchFieldEx.printStackTrace();
        } catch (JsonMappingException jsonMappingEx) {
            jsonMappingEx.printStackTrace();
        } catch (IOException ioExp) {
            logger.error("Parsing input configuration file failed. Reason: " + ioExp.getMessage(), ioExp);
            System.exit(-1);
        } catch (NoSuchMethodException noSuchMethodEx) {
            noSuchMethodEx.printStackTrace();
        } catch (InvocationTargetException invocationTargetEx) {
            invocationTargetEx.printStackTrace();
        }

        return null;
    }
}
