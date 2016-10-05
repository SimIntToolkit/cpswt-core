/**
 * * @author Greg Varga <greg@sph3r.com>
 */

package c2w.hla;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

public class FederationManagerParameter {
    public String FederationName;
    public String FOMFilename;
    public String ScriptFilename;

    @Deprecated
    public String DBName;

    public String LogLevel;
    public boolean RealTimeMode;
    public String LockFilename;
    public double Step;
    public double Lookahead;
    public boolean TerminateOnCOAFinish;
    public double FederationEndTime;
    public long Seed4Dur;
    public boolean AutoStart;

    public static FederationManagerParameter ParseInputs(CommandLine line) {
        FederationManagerParameter p = new FederationManagerParameter();

        p.FederationName = line.getOptionValue("federationName");
        p.FOMFilename = line.getOptionValue("fomFileName");
        p.ScriptFilename = line.getOptionValue("scriptFileName");
        p.DBName = null; // not using DB anymore
        p.LogLevel = line.getOptionValue("logLevel");
        p.RealTimeMode = Boolean.parseBoolean(line.getOptionValue("realtime", "false"));
        p.LockFilename = line.getOptionValue("lockfile");
        p.Step = Double.parseDouble(line.getOptionValue("step"));
        p.Lookahead = Double.parseDouble(line.getOptionValue("lookahead"));
        p.TerminateOnCOAFinish = Boolean.parseBoolean(line.getOptionValue("terminateOnCOAFinish", "false"));
        p.FederationEndTime = Double.parseDouble(line.getOptionValue("federationEndTime", "-1"));
        p.Seed4Dur = Long.parseLong(line.getOptionValue("seed4DurRNG"));
        p.AutoStart = Boolean.parseBoolean(line.getOptionValue("autoStart"));

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

        options.addOption(Option.builder("l")
                .longOpt("lockfile")
                .argName("lockfile")
                .hasArg()
                .desc("Path to the lock file")
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

        return options;
    }
}
