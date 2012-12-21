package com.michelin.droid.download.net;

import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URL;

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
		HttpURLConnection httpurlconnection1;
		try {
			if (!TelephoneUtil.isWifiEnable(context)) {
				String s = Proxy.getDefaultHost();
				if (s != null && !s.equals(""))
					proxy = new java.net.Proxy(java.net.Proxy.Type.HTTP,
								new InetSocketAddress(s, Proxy.getDefaultPort()));
			}
			if (proxy == null)
				httpurlconnection1 = (HttpURLConnection) url.openConnection();
			else
				httpurlconnection1 = (HttpURLConnection) url.openConnection(proxy);
		} catch (Exception exception) {
			Log.e("Exception", exception.getMessage());
			exception.printStackTrace();
			httpurlconnection = null;
		}
		httpurlconnection = httpurlconnection1;

		return httpurlconnection;
	}
}
