package com.michelin.droid.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class TelephoneUtil {
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
}
