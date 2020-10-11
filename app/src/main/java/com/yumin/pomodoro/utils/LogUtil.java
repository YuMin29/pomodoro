package com.yumin.pomodoro.utils;

import android.util.Log;

public class LogUtil {
    // maybe can use BuildConfig.DEBUG
    private static final boolean DEBUG = true;

    public static void logD(String tag, String logMessage){
        if (DEBUG)
            Log.d(tag,logMessage);
    }

    public static void logW(String tag, String logMessage){
        if (DEBUG)
            Log.w(tag,logMessage);
    }

    public static void logE(String tag, String logMessage){
        if (DEBUG)
            Log.e(tag,logMessage);
    }

    public static void logI(String tag, String logMessage){
        if (DEBUG)
            Log.i(tag,logMessage);
    }

    public static void logV(String tag, String logMessage){
        if (DEBUG)
            Log.v(tag,logMessage);
    }
}
