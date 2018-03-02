package uk.co.healtht.healthtouch.analytics;

import android.util.Log;

import com.crashlytics.android.Crashlytics;
import uk.co.healtht.healthtouch.BuildConfig;

public class Crash {
    private static final String TAG = "Crash";

    public static void log(String msg) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, msg);
        }
        else {
            Crashlytics.log(msg);
        }
    }

    public static void logException(Throwable throwable) {
        if (BuildConfig.DEBUG) {
            throwable.printStackTrace();
        }
        else {
            Crashlytics.logException(throwable);
        }
    }

    public static void reportCrash(String msg) {
        try {
            throw new RuntimeException(msg);
        }
        catch (Throwable ex) {
            if (BuildConfig.DEBUG) {
                throw ex;
            }
            else {
                Crashlytics.logException(ex);
            }
        }
    }

    public static void setUserIdentifier(String identifier) {
        if (!BuildConfig.DEBUG) {
            Crashlytics.setUserIdentifier(identifier);
        }
    }

    public static void setUserName(String name) {
        if (!BuildConfig.DEBUG) {
            Crashlytics.setUserName(name);
        }
    }

    public static void setUserEmail(String email) {
        if (!BuildConfig.DEBUG) {
            Crashlytics.setUserEmail(email);
        }
    }
}
