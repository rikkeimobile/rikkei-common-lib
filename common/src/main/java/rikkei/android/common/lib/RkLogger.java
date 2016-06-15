package rikkei.android.common.lib;

/*
 * Copyright (C) 2016 Rikkeisoft Co., Ltd.
 */

import android.util.Log;

/**
 * API for sending log output.
 * This one support to show correctly function , class name and line of code .
 * Default:
 * - Logcat: enabled
 */

public class RkLogger {
    /**
     * Priority constant for the println method; use RkLogger.v.
     */
    public static final int VERBOSE = 2;

    /**
     * Priority constant for the println method; use RkLogger.d.
     */
    public static final int DEBUG = 3;

    /**
     * Priority constant for the println method; use RkLogger.i.
     */
    public static final int INFO = 4;

    /**
     * Priority constant for the println method; use RkLogger.w.
     */
    public static final int WARN = 5;

    /**
     * Priority constant for the println method; use RkLogger.e.
     */
    public static final int ERROR = 6;

    /**
     * Priority constant for the println method. use RkLogger.wtf.
     */
    public static final int ASSERT = 7;

    private static boolean sIsEnableLogcat = true;

    private static final String DOT = ".";


    /**
     * Send an {@link #INFO} log message.
     *
     * @param identifier Used to identify the source of a log message.
     * @param message    The message you would like logged.
     */
    public static void i(final String identifier, final String message) {
        log(INFO, identifier, message);
    }

    /**
     * Send a {@link #DEBUG} log message.
     *
     * @param identifier Used to identify the source of a log message.
     * @param message    The message you would like logged.
     */

    public static void d(final String identifier, final String message) {
        log(DEBUG, identifier, message);
    }

    /**
     * Send a {@link #WARN} log message.
     *
     * @param identifier Used to identify the source of a log message.
     * @param message    The message you would like logged.
     */

    public static void w(final String identifier, final String message) {
        log(WARN, identifier, message);
    }

    /**
     * Send a {@link #VERBOSE} log message.
     *
     * @param identifier Used to identify the source of a log message.
     * @param message    The message you would like logged.
     */

    public static void v(final String identifier, final String message) {
        log(VERBOSE, identifier, message);
    }

    /**
     * Send an {@link #ERROR} log message.
     *
     * @param identifier Used to identify the source of a log message.
     * @param message    The message you would like logged.
     */

    public static void e(final String identifier, final String message) {
        log(ERROR, identifier, message);
    }

    /**
     * Send an {@link #ASSERT} log message.
     *
     * @param identifier Used to identify the source of a log message.
     * @param message    The message you would like logged.
     */

    public static void wtf(final String identifier, final String message) {
        log(ASSERT, identifier, message);
    }

    /**
     * Enable or disable log to Logcat
     *
     * @param on enable or disable state
     */

    public static void setEnableLogcat(boolean on) {
        sIsEnableLogcat = on;
    }

    private static void log(int type, final String identifier, final String message) {
        if (sIsEnableLogcat) {
            Throwable stack = new Throwable().fillInStackTrace();
            StackTraceElement[] trace = stack.getStackTrace();
            String className = trace[2].getClassName();
            className = className.substring(className.lastIndexOf(DOT) + 1);
            String methodName = trace[2].getMethodName();
            String line = trace[2].getLineNumber() + "";
            String TAG = className + "#" + methodName + "#" + line;
            switch (type) {
                case VERBOSE:
                    Log.v(TAG, identifier + ": " + message);
                    break;
                case DEBUG:
                    Log.d(TAG, identifier + ": " + message);
                    break;
                case INFO:
                    Log.i(TAG, identifier + ": " + message);
                    break;
                case WARN:
                    Log.w(TAG, identifier + ": " + message);
                    break;
                case ERROR:
                    Log.e(TAG, identifier + ": " + message);
                    break;
                case ASSERT:
                    Log.wtf(TAG, identifier + ": " + message);
                    break;
                default:
                    break;
            }
        }
    }
}
