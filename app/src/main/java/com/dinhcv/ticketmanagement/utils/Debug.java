/**
 * Debug
 *    Log control with FileLog & ServerLog
 *
 * @author : tu.pham
 * @created : 2012-12-30
 * @modified : $Date: 2016-03-23 13:41:49 +0700 (Wed, 23 Mar 2016) $
 * @Revision : $Revision: 1983 $
 *
 *           Copyright (C)-2014 www.mdi-astec.vn
 ******************************************************************************/
package com.dinhcv.ticketmanagement.utils;

import android.util.Log;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Locale;


@SuppressWarnings("unused")
public class Debug
{
    /*
     * define constants for debug mode
     */
    private static final int      DBG_LEVEL_DBG    = 5;
    private static final int      DBG_LEVEL_NORMAL = 4;
    private static final int      DBG_LEVEL_WARN   = 3;
    private static final int      DBG_LEVEL_ERROR  = 2;
    private static final int      DBG_LEVEL_FATAL  = 1;

    /*
     * variables
     */
    private static final int     DEBUG_LEVEL    = DBG_LEVEL_NORMAL;
    private static final String  TAG            = "Pachinko";
    private static final double  MB = 1.0 * 1024 * 1024; // MB

    /**
     * Hide constructor
     */
    private Debug(){
    }

    private static int getDebugLevel(){
        return DEBUG_LEVEL;
    }

    /**
     * Description : Trace fatal message
     *
     * @param fmt : text message
     */
    public static void fatal(String fmt, Object... args)
    {
        if (getDebugLevel() >= DBG_LEVEL_FATAL)
        {
            final String stMsg   = format("[FATAL]" + fmt, args);
            final String stPoint = format("   at %s", getDebugPoint());

            Log.e(TAG, "[FATAL]###########################################");
            Log.e(TAG, stMsg);
            Log.e(TAG, stPoint);
            Log.e(TAG, "[FATAL]###########################################");
        }
    }

    /**
     * Description : Trace error message
     *
     * @param args : text message
     */
    public static void error(String fmt, Object... args)
    {
        if (getDebugLevel() >= DBG_LEVEL_ERROR)
        {
            final String stMsg   = format("[ERR]" + fmt, args);
            final String stPoint = format("   at %s", getDebugPoint());

            Log.e(TAG, stMsg);
            Log.e(TAG, stPoint);
        }
    }

    /**
     * Description : Trace warning message
     *
     * @param args : text message
     */
    public static void warn(String fmt, Object... args)
    {
        if (getDebugLevel() >= DBG_LEVEL_WARN)
        {
            final String stMsg = format("[WRN]" + fmt, args);
            final String stPoint = format("   at %s", getDebugPoint());

            Log.w(TAG, stMsg);
            Log.w(TAG, stPoint);
        }
    }

    /**
     * Description : Trace warning message
     *
     * @param args : text message
     */
    public static void dbg(String fmt, Object... args)
    {
        if (getDebugLevel() >= DBG_LEVEL_DBG)
        {
            Log.v(TAG, format("[ d ]" + fmt, args));
        }
    }

    /**
     * Description : Trace normal message
     *
     * @param args : text message
     */
    public static void normal(String fmt, Object... args)
    {
        if (getDebugLevel() >= DBG_LEVEL_NORMAL)
        {
            Log.v(TAG, format("[ - ]" + fmt, args));
        }
    }

    /**
     * Description : verify
     *
     * @param b : Condition assert
     */
    @SuppressWarnings("unused")
    public static void verify(boolean b) {
        if (!(b)) {
            final String stMsg = format("###### ASSERT FAILED ######");
            final String stPoint = format("   at %s", getDebugPoint());

            Log.w(TAG, stMsg);
            Log.w(TAG, stPoint);
        }
    }

    /**
     * Description : verify
     *
     * @param b : Condition assert
     */
    @SuppressWarnings("unused")
    public static void verify(boolean b, String msg) {
        if (!(b)) {
            final String stMsg   = format("###### ASSERT FAILED ######");
            final String stPoint = format("   at %s", getDebugPoint());
            Log.w(TAG, stMsg);
            if (msg != null) {
                Log.w(TAG, "   msg: " + msg);
            }
            Log.w(TAG, stPoint);
        }
    }

    /**
     * Print Stack trace when exception
     * @param e Exception
     */
    public static void printStackTrace( Exception e ){
        StringWriter sw = new StringWriter();
        e.printStackTrace( new PrintWriter(sw) );
        String msg = sw.toString();

        if (getDebugLevel() >= DBG_LEVEL_ERROR) {
            Log.e(TAG, msg);
        }
    }

    /**
     * Is debug mode, true for debug mode
     * @return DEBUG mode
     */
    public static boolean isDebugEnable() {
        return DEBUG_LEVEL >= DBG_LEVEL_DBG;
    }

    /**
     * Use to trace current line/file at debug log point
     *
     * @return The point with the Java's logging default format, so, you can
     *         double click on the tracing line in the LogCat tool
     */
    private static String getDebugPoint()
    {
        final String fullClassName = Thread.currentThread().getStackTrace()[4].getClassName();
        final String className     = fullClassName.substring(fullClassName.lastIndexOf(".") + 1);
        final String methodName    = Thread.currentThread().getStackTrace()[4].getMethodName();
        final int lineNumber       = Thread.currentThread().getStackTrace()[4].getLineNumber();

        //
        return format("%s.%s(%s.java:%d)", fullClassName, methodName, className, lineNumber);
    }

    /**
     * Log current status of Heap
     *
     */
    @SuppressWarnings("unused")
    public static void logHeap() {
        final String tag = "HEAP";


        final Double allocated = android.os.Debug.getNativeHeapAllocatedSize() / MB;
        final Double available = android.os.Debug.getNativeHeapSize() / MB;
        final Double free      = android.os.Debug.getNativeHeapFreeSize() / MB;
        final Double memUsed   = Runtime.getRuntime().totalMemory() / MB;
        final Double memMax    = Runtime.getRuntime().maxMemory() / MB;
        final Double memFree   = Runtime.getRuntime().freeMemory() / MB;

        Log.d(tag, format("Heap native: allocated %6.3f MB of %6.3f - free %6.3f MB", allocated, available, free));
        Log.d(tag, format("Memory     : allocated %6.3f MB of %6.3f - free %6.3f MB", memUsed, memMax, memFree));
    }

    private static String format(String fmt, Object... args)
    {
        if ((args != null) && (args.length > 0)) {
            return String.format(Locale.US, fmt, args);
        }
        else {
            return fmt;
        }
    }

    /**
     * Register a UnExceptionHandler to catch force-close error for thread
     */
    public static void registerUnExceptionHandler() {
        if(!(Thread.getDefaultUncaughtExceptionHandler() instanceof ExceptionHandler)) {
            Thread.setDefaultUncaughtExceptionHandler( new ExceptionHandler() );
        }
    }

    /**
     * Trace current stack
     */
    @SuppressWarnings("unused")
    public static void stackTrace() {
        Log.w(TAG, "########### STACK TRACE START #############");
        for (final StackTraceElement ste : Thread.currentThread().getStackTrace()) {
            System.out.println(ste);
        }
        Log.w(TAG, "########### STACK TRACE END   #############");
    }

    public static class ExceptionHandler implements UncaughtExceptionHandler {
        private final UncaughtExceptionHandler defaultUEH;

        /*
         * if any of the parameters is null, the respective functionality will
         * not be used
         */
        public ExceptionHandler() {
            this.defaultUEH = Thread.getDefaultUncaughtExceptionHandler();
        }

        @Override
        public void uncaughtException(Thread t, Throwable e) {
            final Writer result = new StringWriter();
            final PrintWriter printWriter = new PrintWriter(result);
            e.printStackTrace(printWriter);
            printWriter.close();

            defaultUEH.uncaughtException(t, e);
        }
    }
}

/*******************************************************************************
 * End of file
 ******************************************************************************/
