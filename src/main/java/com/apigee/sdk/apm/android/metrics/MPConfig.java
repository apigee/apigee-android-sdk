package com.apigee.sdk.apm.android.metrics;

/**
 * Stores global configuration options for the Mixpanel library.
 * May be overridden to achieve custom behavior.
 */
public class MPConfig {
    // Remove events and people records that have sat around for this many milliseconds
    // on first initialization of the library. Default is 48 hours.
    // Must be reconfigured before the library is initialized for the first time.
    public static int DATA_EXPIRATION = 1000 * 60 * 60 * 48;

    // Time in milliseconds that the submission thread must be idle for before it dies.
    // Must be reconfigured before the library is initialized for the first time.
    public static int SUBMIT_THREAD_TTL = 180 * 1000;

    // Set to true to see debugging logcat output:
    public static boolean DEBUG = false;

    // Closes old sessions that have not been closed for this many milliseconds
    // on first initialization of the library. Default is 2 hours.
    // Must be reconfigured before the library is initialized for the first time
    public static int SESSION_EXPIRATION = 1000 * 60 * 30;    
}
