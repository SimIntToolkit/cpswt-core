/**
 * * @author Greg Varga <greg@sph3r.com>
 */

package c2w.hla;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import java.io.*;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.function.Function;

public class FederationManagerParameter {
    public String FederationName;

    public String FOMFilename;

    public String ScriptFilename;

    public String LogLevel;

    public String LogDir;

    public boolean RealTimeMode;

    public String RootPathEnvVarKey;

    public double Step;

    public double Lookahead;

    public boolean TerminateOnCOAFinish;

    public double FederationEndTime;

    public long Seed4Dur;

    public boolean AutoStart;

    public String StopScriptPath;

    @FunctionalInterface
    interface Function2<T1, T2, R> {
        R apply(T1 p1, T2 p2);
    }

    static FederationManagerParameter readArgs(Function<String, String> fn, Function2<String, String, String> fnWithDefaultVal) {
        FederationManagerParameter p = new FederationManagerParameter();

        p.FederationName = fn.apply("federationName");
        p.FOMFilename = fn.apply("fomFilename");
        p.ScriptFilename = fn.apply("scriptFilename");
        p.LogLevel = fn.apply("logLevel");
        p.RealTimeMode = Boolean.parseBoolean(fnWithDefaultVal.apply("realtime", "false"));
        p.RootPathEnvVarKey = fn.apply("rootPathEnvVarKey");
        p.Step = Double.parseDouble(fn.apply("step"));
        p.Lookahead = Double.parseDouble(fn.apply("lookahead"));
        p.TerminateOnCOAFinish = Boolean.parseBoolean(fnWithDefaultVal.apply("terminateOnCOAFinish", "false"));
        p.FederationEndTime = Double.parseDouble(fnWithDefaultVal.apply("federationEndTime", "-1"));
        p.Seed4Dur = Long.parseLong(fn.apply("seed4DurRNG"));
        p.AutoStart = Boolean.parseBoolean(fn.apply("autoStart"));

        p.LogDir = fnWithDefaultVal.apply("logDir", Paths.get(System.getenv(p.RootPathEnvVarKey), "log").toString());

        // default value from previous versions ...
        p.StopScriptPath = fnWithDefaultVal.apply("stopScriptPath", Paths.get(System.getenv(p.RootPathEnvVarKey), "Main", "stop.sh").toString());

        return p;
    }

    public static FederationManagerParameter ParseInputs(final CommandLine line) {
        Function<String, String> fn = new Function<String, String>() {
            public String apply(String s) {
                return line.getOptionValue(s);
            }
        };
        Function2<String, String, String> fnWithDefaultVal = new Function2<String, String, String>() {
            public String apply(String opt, String defaultValue) {
                return line.getOptionValue(opt, defaultValue);
            }
        };

        FederationManagerParameter p = readArgs(fn, fnWithDefaultVal);
        return p;
    }

    public static FederationManagerParameter ParsePropertiesFile(String propertiesFilePath) throws IOException {
        InputStream inputStream = null;
        FederationManagerParameter p = new FederationManagerParameter();

        try {
            final Properties prop = new Properties();
            File propertiesFile = new File(propertiesFilePath);
            inputStream = new FileInputStream(propertiesFile);

            if (inputStream != null) {
                prop.load(inputStream);
            } else {
                throw new FileNotFoundException("Property file '" + propertiesFilePath + "' not found!");
            }

            Function<String, String> fn = new Function<String, String>() {
                public String apply(String s) {
                    return prop.getProperty(s);
                }
            };
            Function2<String, String, String> fnWithDefaultVal = new Function2<String, String, String>() {
                public String apply(String opt, String defaultValue) {
                    return prop.getProperty(opt, defaultValue);
                }
            };

            p = readArgs(fn, fnWithDefaultVal);
        } finally {
            inputStream.close();
        }
        return p;
    }

    public static Options GetCLIOptions() {
        Options options = new Options();

        options.addOption(Option.builder("s")
                .longOpt("step")
                .argName("step")
                .hasArg()
                .desc("Step size for simulation")
                .required()
                .type(double.class)
                .build()
        );

        options.addOption(Option.builder("a")
                .longOpt("lookahead")
                .argName("lookahead")
                .hasArg()
                .desc("The lookahead value for the simulation")
                .required()
                .type(double.class)
                .build()
        );

        options.addOption(Option.builder()
                .longOpt("rootPathEnvVarKey")
                .argName("rootPathEnvVarKey")
                .hasArg()
                .desc("Root path environment variable key")
                .required()
                .type(String.class)
                .build()
        );

        options.addOption(Option.builder("r")
                .longOpt("realtime")
                .argName("realtime")
                .optionalArg(true)
                .desc("Indicates if real-time mode is on")
                .type(boolean.class)
                .build()
        );

        options.addOption(Option.builder("t")
                .longOpt("terminateOnCOAFinish")
                .argName("terminateOnCOAFinish")
                .optionalArg(true)
                .desc("Indicates whether to terminate when COA finishes")
                .type(boolean.class)
                .build()
        );

        options.addOption(Option.builder("e")
                .longOpt("federationEndTime")
                .argName("federationEndTime")
                .hasArg()
                .desc("Federation end time")
                .type(double.class)
                .build()
        );

        options.addOption(Option.builder("g")
                .longOpt("seed4DurRNG")
                .argName("seed4DurRNG")
                .hasArg()
                .desc("seed4DurRNG")
                .type(long.class)
                .build()
        );

        options.addOption(Option.builder("u")
                .longOpt("autoStart")
                .argName("autoStart")
                .optionalArg(true)
                .desc("Indicates if federation should start automatically")
                .type(boolean.class)
                .build()
        );

        options.addOption(Option.builder()
                .longOpt("federationName")
                .argName("federationName")
                .hasArg()
                .desc("The name of the federation")
                .required()
                .type(String.class)
                .build()
        );

        options.addOption(Option.builder()
                .longOpt("fomFilename")
                .argName("fomFilename")
                .hasArg()
                .desc("Path to the FOM filename")
                .required()
                .type(String.class)
                .build()
        );

        options.addOption(Option.builder()
                .longOpt("scriptFilename")
                .argName("scriptFilename")
                .hasArg()
                .desc("Path to the script.xml file")
                .required()
                .type(String.class)
                .build()
        );

        options.addOption(Option.builder()
                .longOpt("logLevel")
                .argName("logLevel")
                .hasArg()
                .desc("Log level")
                .required()
                .type(String.class)
                .build()
        );

        options.addOption(Option.builder()
                .longOpt("logDir")
                .argName("logDir")
                .hasArg()
                .desc("Log directory")
                .type(String.class)
                .build()
        );

        options.addOption(Option.builder()
                .longOpt("stopScriptPath")
                .argName("stopScriptPath")
                .hasArg()
                .desc("Path to the stop script")
                .type(String.class)
                .build()
        );

        return options;
    }
}
