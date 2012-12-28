package com.michelin.droidmi.data;

import com.michelin.droid.data.ConstantSet;
import com.michelin.droid.util.PackageUtil;

public class Constants {
	public static final long CATEGORY_ICON_EXPIRATION = 60L * 60L * 24L * 7L * 1000L * 2L; // two weeks.
	public static final boolean DUMPCATCHER_TEST = false;
	public static final boolean LOCATION_DEBUG = false;
	public static final boolean USE_DUMPCATCHER = true;

	public static boolean IS_DEVELOPING = false;
    public static int PAGE_SIZE = 10;
    public static boolean USE_DEBUG_SERVER = false;

    static {
		IS_DEVELOPING = ConstantSet.IS_DEVELOPING;
		PAGE_SIZE = ConstantSet.PAGE_SIZE;
		USE_DEBUG_SERVER = PackageUtil.getConfigBoolean("use_debug_server");
	}
}
