package com.michelin.droidmi;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.michelin.droid.Droid;
import com.michelin.droid.util.IconUtils;
import com.michelin.droidmi.util.JavaLoggingHandler;

public class Droidmi extends Application {
    private static final String TAG = "Droidmi";
    private static final boolean DEBUG = DroidmiSettings.DEBUG;
    static {
        Logger.getLogger("com.michelin.droid").addHandler(new JavaLoggingHandler());
        Logger.getLogger("com.michelin.droid").setLevel(Level.ALL);
    }

    public static final String PACKAGE_NAME = "com.michelin.droidmi";

    private String mVersion = null;

    private Droid mDroid;

    private boolean mIsFirstRun;
    

    @Override
    public void onCreate() {
        Log.i(TAG, "Using Debug Server:\t" + DroidmiSettings.USE_DEBUG_SERVER);
        Log.i(TAG, "Using Dumpcatcher:\t" + DroidmiSettings.USE_DUMPCATCHER);
        Log.i(TAG, "Using Debug Log:\t" + DEBUG);

        mVersion = getVersionString(this);
        
        // Check if this is a new install by seeing if our preference file exists on disk.
        mIsFirstRun = checkIfIsFirstRun();

        // If we're on a high density device, request higher res images. This singleton
        // is picked up by the parsers to replace their icon urls with high res versions.
        float screenDensity = getApplicationContext().getResources().getDisplayMetrics().density;
        IconUtils.get().setRequestHighDensityIcons(screenDensity > 1.0f);

        // Log into Droid, if we can.
        loadDroid();
    }

    public Droid getDroid() {
        return mDroid;
    }

    public String getVersion() {

        if (mVersion != null) {
            return mVersion;
        } else {
            return "";
        }
    }

    private void loadDroid() {
        // Try logging in and setting up foursquare oauth, then user
        // credentials.
        if (DroidmiSettings.USE_DEBUG_SERVER) {
            mDroid = new Droid(Droid.createHttpApi("10.0.2.2:8080", mVersion, false));
        } else {
            mDroid = new Droid(Droid.createHttpApi(mVersion, false));
        }
    }

    /**
     * Provides static access to a Droid instance. This instance is
     * initiated without user credentials.
     * 
     * @param context the context to use when constructing the Droid
     *            instance
     * @return the Droid instace
     */
    public static Droid createDroid(Context context) {
        String version = getVersionString(context);
        if (DroidmiSettings.USE_DEBUG_SERVER) {
            return new Droid(Droid.createHttpApi("10.0.2.2:8080", version, false));
        } else {
            return new Droid(Droid.createHttpApi(version, false));
        }
    }

    /**
     * Constructs the version string of the application.
     * 
     * @param context the context to use for getting package info
     * @return the versions string of the application
     */
    private static String getVersionString(Context context) {
        // Get a version string for the app.
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(PACKAGE_NAME, 0);
            return PACKAGE_NAME + ":" + String.valueOf(pi.versionCode);
        } catch (NameNotFoundException e) {
            if (DEBUG) Log.d(TAG, "Could not retrieve package info", e);
            throw new RuntimeException(e);
        }
    }

    public boolean getIsFirstRun() {
        return mIsFirstRun;
    }

    private boolean checkIfIsFirstRun() {
        File file = new File(
            "/data/data/com.michelin.droidmi/shared_prefs/com.michelin.droidmi_preferences.xml");
        return !file.exists();
    }
}
