package com.michelin.droid.util;

import com.michelin.droid.app.DroidApplicationBase;

import android.content.Context;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class TelephoneUtil {
	private static final String TAG = "TelephoneUtil";
	
	public static boolean isWifiEnable(Context context) {
		ConnectivityManager conManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (conManager != null && conManager.getActiveNetworkInfo() != null
				&& conManager.getActiveNetworkInfo().isAvailable()) {
			if (conManager.getActiveNetworkInfo().getType() == ConnectivityManager.TYPE_WIFI) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean isNetworkAvailable() {
		Context context = DroidApplicationBase.CONTEXT;
		if (context == null) {
			return false;
		}

		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (cm == null) {
			return false;
		}
		NetworkInfo[] info = null;
		try {
			info = cm.getAllNetworkInfo();
		} catch (Exception e) {
			EvtLog.w(TAG, e);
		}
		if (info != null) {
			for (int i = 0; i < info.length; i++) {
				if (info[i].getState() == NetworkInfo.State.CONNECTED) {
					return true;
				}
			}
		}

		return false;
	}
	
	public static boolean isGPSAvailable() {
		boolean result;
		LocationManager locationManager = (LocationManager) DroidApplicationBase.CONTEXT
				.getSystemService(Context.LOCATION_SERVICE);
		if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			result = true;
		} else {
			result = false;
		}
		EvtLog.d(TAG, "result:" + result);

		return result;
	}
}
