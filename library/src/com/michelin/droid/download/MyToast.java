package com.michelin.droid.download;

import android.content.Context;
import android.widget.Toast;

/**
 * Title:
 * Description:
 * Copyright: Copyright (c) 2013
 * Company:深圳彩讯科技有限公司
 *
 * @author licq 2013-6-28
 * @version 1.0
 */
public class MyToast {
	public static void show(Context context, String text){
		Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
	}
}
