package org.cpswt.utils;

/**
 * Utils for Cpswt
 */
public class CpswtUtils {
    /**
     * Portico has this "feature". This is just a copy.
     * @param millis Milliseconds to sleep.
     */
    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        }
        catch(InterruptedException e) {}
    }

    public static void sleepDefault() {
        CpswtUtils.sleep(50);
    }
}
