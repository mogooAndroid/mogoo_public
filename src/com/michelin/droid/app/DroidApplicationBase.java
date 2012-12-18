package com.michelin.droid.app;

import com.michelin.droid.util.PackageUtil;

import android.app.Application;
import android.content.Context;

/**
 * 全局应用程序
 * 
 * @author
 * 
 */
public class DroidApplicationBase extends Application {
	public static Context CONTEXT;
	public static boolean IS_DEVELOPING = false;
	
	@Override
	public void onCreate() {
		super.onCreate();
		CONTEXT = getApplicationContext();
		IS_DEVELOPING = PackageUtil.getConfigBoolean("is_developing");
	}
}
