package com.michelin.droidmi.app;

import java.io.File;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

import com.michelin.droid.app.DroidApplicationBase;
import com.michelin.droid.data.ConstantSet;
import com.michelin.droid.util.EvtLog;
import com.michelin.droid.util.IconUtils;
import com.michelin.droidmi.data.Constants;

public class Droidmi extends DroidApplicationBase {
	public static final String TAG = Droidmi.class.getSimpleName();

    public static final String PACKAGE_NAME = "com.michelin.droidmi";

    private String mVersion = null;

    private Droid mDroid;

    private boolean mIsFirstRun;
    

    @Override
    public void onCreate() {
    	super.onCreate();
        EvtLog.i(Droidmi.class, TAG, "Using Debug Server:\t" + Constants.USE_DEBUG_SERVER);
        EvtLog.i(Droidmi.class, TAG, "Using Dumpcatcher:\t" + Constants.USE_DUMPCATCHER);
        EvtLog.i(Droidmi.class, TAG, "Using Debug Log:\t" + ConstantSet.IS_DEVELOPING);
        
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
		mDroid = new Droid(Droid.createHttpApi(mVersion, false));
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
		return new Droid(Droid.createHttpApi(version, false));
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
            EvtLog.e(e, TAG, "Could not retrieve package info");
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
