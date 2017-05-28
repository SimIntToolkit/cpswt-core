package c2w.hla;

import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.EnvironmentVariables;

public class FederationManagerTest {

    @ClassRule
    public static final EnvironmentVariables environmentVariables = new EnvironmentVariables();

    static FederationManager federationManager;
    static String rootPathEnvVarKey = "CPSWTNG_ROOT";

    private static FederationManagerParameter defaultFederationManagerParameter() {
        FederationManagerParameter p = new FederationManagerParameter();

        p.AutoStart = true;
        p.FederateRTIInitWaitTime = 20;
        p.FederationEndTime = 0;
        p.FederationName = "TestFederation";
        p.FOMFilename = "EchoExample.fed";
        p.LogDir = "log/";
        p.LogLevel = "NORMAL";
        p.Lookahead = 0.1;
        p.RealTimeMode = true;
        p.RootPathEnvVarKey = rootPathEnvVarKey;
        p.ScriptFilename = "script.xml";
        p.Seed4Dur = 0;
        p.Step = 1;
        p.StopScriptPath = "Main/stop.sh";

        return p;
    }

    @BeforeClass
    public static void setEnvironmentVars() {
        environmentVariables.set(rootPathEnvVarKey, "/Users/sph3r/projects/c2wt/repos/cpswtng-core/testfiles/");
    }

    @BeforeClass
    public static void initFederationManager() {
        try {
            federationManager = new FederationManager(defaultFederationManagerParameter());
        }
        catch(Exception e) {
            e.printStackTrace(System.err);
        }
    }

    @Test
    public void startFederation() {
        try {
            federationManager.startSimulation();
        }
        catch(Exception e) {
            e.printStackTrace(System.err);
        }
    }

}