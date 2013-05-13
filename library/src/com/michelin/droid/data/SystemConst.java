package com.michelin.droid.data;

import android.os.Environment;

public class SystemConst {
	public static final String BASE_FOLDER_NAME = "myfolder/";
	public static String BASE_DIR;

	static{
		BASE_DIR = Environment.getExternalStorageDirectory() + "/" + BASE_FOLDER_NAME;
	}
	
	public static void initDirectory(String dir) {
		BASE_DIR = dir;
	}
}
