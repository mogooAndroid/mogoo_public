package com.michelin.droid.data;

import android.content.Context;
import android.os.Environment;

public class SystemVal {
	public static String private_file_dir;
	
	public static void init(Context context)
	{
		private_file_dir = context.getFilesDir().getAbsolutePath();
		if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()))
			SystemConst.initDirectory(private_file_dir);
	}
}
