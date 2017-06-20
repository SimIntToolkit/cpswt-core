package org.cpswt;

import org.cpswt.config.FederateConfigParser;
import c2w.hla.FederationManager;
import c2w.hla.FederationManagerConfig;
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

        FederationManagerConfig fmp = getDefaultParam();

        try {
            FederationManager federationManager = new FederationManager(fmp);
        }
        catch (Exception e) {

        }
    }
    
    FederationManagerConfig getDefaultParam() {
        FederateConfigParser parser = new FederateConfigParser();
        FederationManagerConfig fmp = parser.parseArgs(new String[] { "-configFile", "fedmgrconfig.json" }, FederationManagerConfig.class);
        return fmp;
    }

}