package c2w.hla;

import c2w.utils.CpswtDefaults;
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
    static final Logger logger = LogManager.getLogger(FederateParameterParser.class);

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

    /**
     * Add more CLI option to the existing ones.
     * @param option
     */
    public void addCLIOption(Option option) {
        this.cliOptions.addOption(option);
    }

    static final Set<Class<?>> supportedCLIArgTypes = new HashSet();
    static {
        supportedCLIArgTypes.add(Double.class);
        supportedCLIArgTypes.add(Integer.class);
        supportedCLIArgTypes.add(Boolean.class);
        supportedCLIArgTypes.add(Long.class);
    }

    /**
     * Parse the command line arguments provided to the main function.
     * @param args Command line arguments provided to 'main'
     * @param clazz The class that represents the federate parameter.
     * @param <TParam> The generic type of the federate parameter.
     * @return An instance of the class that represents the federate parameter.
     */
    public <TParam extends FederateParameter> TParam parseArgs(final String[] args, final Class<TParam> clazz) {

        CommandLineParser parser  = new DefaultParser();

        try {

            CommandLine commandLine = parser.parse(this.cliOptions, args);
            TParam currentParameter = this.parseCommandLine(commandLine, clazz);

            return currentParameter;
        }
        catch (ParseException parseExp) {
            logger.error("Parsing CLI arguments failed. Reason: " + parseExp.getMessage(), parseExp);
            System.exit(-1);
        }

        return null;

    }

    <TParam extends FederateParameter> TParam parseCommandLine(CommandLine commandLine, final Class<TParam> clazz) {
        try {
            File configFile = null;
            ObjectMapper mapper = new ObjectMapper(new JsonFactory());

            // get the "configFile" parameter from the command line
            String mConfigFilePath = commandLine.getOptionValue("configFile");

            TParam federateParameter = null;

            // fallback to default config from resources
            if (mConfigFilePath == null) {
                logger.trace("configFile CLI parameter not provided");
                logger.trace("Trying to load {} as a resource of {} class.", CpswtDefaults.FederateConfigDefaultResource, clazz.getName());

                ClassLoader classLoader = clazz.getClassLoader();
                URL resource = classLoader.getResource(CpswtDefaults.FederateConfigDefaultResource);

                // fallback to environment variable definition
                if (resource == null) {
                    logger.trace("No resource found for class {}", clazz.getName());
                    logger.trace("Trying to load configFile set in {} environment variable.", CpswtDefaults.FederateConfigEnvironmentVariable);
                    String configFileFromEnv = System.getenv(CpswtDefaults.FederateConfigEnvironmentVariable);
                    if (configFileFromEnv != null) {
                        logger.trace("{} environment variable set, loading config file {}", CpswtDefaults.FederateConfigEnvironmentVariable, configFileFromEnv);
                        configFile = new File(configFileFromEnv);
                    }
                } else {
                    logger.trace("Resource found. Loading {}.", resource.getPath());
                    configFile = new File(resource.getFile());
                }
            }
            // load passed config file
            else {
                logger.trace("Trying to load config file {}.", mConfigFilePath);
                configFile = new File(mConfigFilePath);
            }

            if (configFile != null) {
                federateParameter = mapper.readValue(configFile, clazz);
            }

            // if default config file was not found
            if (federateParameter == null) {
                logger.trace("No configFile could be loaded. Instantiating empty {} parameter class.", clazz.getName());
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
                    else if(supportedCLIArgTypes.contains(optType)) {
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

            // TODO: logger.error -->
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
