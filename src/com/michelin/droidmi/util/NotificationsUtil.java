/**
 * Copyright 2009 Joe LaPenna
 */

package com.michelin.droidmi.util;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import org.apache.http.client.HttpResponseException;

import com.michelin.droid.error.DroidCredentialsException;
import com.michelin.droid.error.DroidException;
import com.michelin.droidmi.DroidmiSettings;

/**
 * @author Joe LaPenna (joe@joelapenna.com)
 */
public class NotificationsUtil {
    private static final String TAG = "NotificationsUtil";
    private static final boolean DEBUG = DroidmiSettings.DEBUG;

    public static void ToastReasonForFailure(Context context, Throwable e) {
        if (DEBUG) Log.d(TAG, "Toasting for exception: ", e);

        if (e == null) {
            Toast.makeText(context, "A surprising new problem has occured. Try again!",
                    Toast.LENGTH_SHORT).show();
        } else if (e instanceof SocketTimeoutException) {
            Toast.makeText(context, "Droidmi over capacity, server request timed out!", Toast.LENGTH_SHORT).show();
            
        } else if (e instanceof SocketException) {
            Toast.makeText(context, "Droidmi server not responding", Toast.LENGTH_SHORT).show();

        } else if (e instanceof IOException) {
        	String statusCode = "";
        	if (e instanceof HttpResponseException)
        		statusCode = String.valueOf(((HttpResponseException) e).getStatusCode());
            Toast.makeText(context, "Network unavailable" + statusCode, Toast.LENGTH_SHORT).show();

        } else if (e instanceof DroidCredentialsException) {
            Toast.makeText(context, "Authorization failed.", Toast.LENGTH_SHORT).show();

        } else if (e instanceof DroidException) {
            // FoursquareError is one of these
            String message;
            int toastLength = Toast.LENGTH_SHORT;
            if (e.getMessage() == null) {
                message = "Invalid Request";
            } else {
                message = e.getMessage();
                toastLength = Toast.LENGTH_LONG;
            }
            Toast.makeText(context, message, toastLength).show();

        } else {
            Toast.makeText(context, "A surprising new problem has occured. Try again!",
                    Toast.LENGTH_SHORT).show();
            // 未知错误回馈
            // DumpcatcherHelper.sendException(e);
        }
    }
}
