package com.michelin.droidmi.download.net;

import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URL;

import com.michelin.droid.util.TelephoneUtil;

import android.content.Context;
import android.net.Proxy;
import android.util.Log;

/**
 * 
 * @author Administrator
 * 
 */
public class NetChoose {
	public static HttpURLConnection getAvailableNetwork(Context context, URL url) {
		HttpURLConnection httpurlconnection;
		java.net.Proxy proxy = null;
		try {
			if (!TelephoneUtil.isWifiEnable(context)) {
				String s = Proxy.getDefaultHost();
				if (s != null && !s.equals(""))
					proxy = new java.net.Proxy(java.net.Proxy.Type.HTTP,
								new InetSocketAddress(s, Proxy.getDefaultPort()));
			}
			if (proxy == null)
				httpurlconnection = (HttpURLConnection) url.openConnection();
			else
				httpurlconnection = (HttpURLConnection) url.openConnection(proxy);
		} catch (Exception exception) {
			Log.e("Exception", exception.getMessage());
			exception.printStackTrace();
			httpurlconnection = null;
		}

		return httpurlconnection;
	}
}
