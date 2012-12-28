package com.michelin.droid.app;

import android.app.Application;
import android.content.Context;

import com.michelin.droid.util.CrashHandler;
import com.michelin.droid.util.FileUtil;

/**
 * 全局应用程序
 * 
 * @author
 * 
 */
public class DroidApplicationBase extends Application {
	public static Context CONTEXT;
	
	@Override
	public void onCreate() {
		super.onCreate();
		CONTEXT = getApplicationContext();
		CrashHandler crashHandler = CrashHandler.getInstance();
		crashHandler.init(CONTEXT);
	}
}
