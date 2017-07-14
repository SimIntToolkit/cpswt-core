package org.cpswt.utils;

/**
 * Defaults for Cpswt
 */
public class CpswtDefaults {
    public static final String FederateConfigDefaultResource = "federateConfig.default.json";
    public static final String FederateConfigEnvironmentVariable = "CPSWT_FEDERATE_CONFIG";

    public static final String RootPathEnvVarKey = "CPSWT_ROOT";
    public static final String ConfigFileOptionName = "configFile";

    public static final double EPSILON = 1e-6;

    public static final int MaxJoinResignAttempt = 10;
    public static final int JoinResignWaitMillis = 500;
}
