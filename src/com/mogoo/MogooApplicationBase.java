package com.mogoo;

import android.app.Application;
import android.content.Context;

/**
 * 全局应用程序
 * 
 * @author 
 * 
 */
public class MogooApplicationBase extends Application {

	public static Context CONTEXT;

	@Override
	public void onCreate() {
		super.onCreate();
		CONTEXT = getApplicationContext();
	}
}
