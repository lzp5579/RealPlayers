package test.realplayers.util;

import android.util.Log;

import test.realplayers.BuildConfig;

/**
 * Created by slon on 22.03.2017.
 */

public class AppLog {
    public static void e(String tag, String message, Throwable t) {
        if (BuildConfig.DEBUG) {
            Log.e(tag, message, t);
        }
    }

    public static void d(String tag, String message) {
        if (BuildConfig.DEBUG) {
            Log.d(tag, message);
        }
    }
}
