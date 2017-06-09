package org.cpswt;

import c2w.hla.FederationManager;
import c2w.hla.FederationManagerParameter;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

public class FederationManagerBenchmark {

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(FederationManagerBenchmark.class.getSimpleName())
                .warmupIterations(1)
                .measurementIterations(1)
                .forks(1)
                .build();

        new Runner(opt).run();
    }

    @Benchmark
    @BenchmarkMode(Mode.SingleShotTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void testFederationManagerCtor() {

        FederationManagerParameter fmp = getDefaultParam();

        try {
            FederationManager federationManager = new FederationManager(fmp);
        }
        catch (Exception e) {

        }
    }
    
    FederationManagerParameter getDefaultParam() {
        FederationManagerParameter fmp = new FederationManagerParameter();
        fmp.Step = 1.0;
        fmp.Lookahead = 0.1;
        fmp.RootPathEnvVarKey = "CPSWT_ROOT";
        fmp.RealTimeMode = true;
        fmp.FederationEndTime = 100.0;
        fmp.Seed4Dur = 0;
        fmp.AutoStart = true;
        fmp.FOMFilename = "fedtest.fed";
        fmp.ScriptFilename = "script.xml";
        fmp.LogDir = "log";
        fmp.LogLevel = "NORMAL";
        fmp.FederationName = "HelloWorld_Java_Tutorial";
        fmp.StopScriptPath = "Main/stop.sh";
        fmp.FederateRTIInitWaitTime = 1000;

        return fmp;
    }

}