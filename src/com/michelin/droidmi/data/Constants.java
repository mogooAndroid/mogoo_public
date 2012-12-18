package com.michelin.droidmi.data;

import com.michelin.droid.util.PackageUtil;
import com.michelin.droidmi.app.Droidmi;

public class Constants {
	public static final long CATEGORY_ICON_EXPIRATION = 60L * 60L * 24L * 7L * 1000L * 2L; // two weeks.
	public static final boolean DUMPCATCHER_TEST = false;
	public static boolean IS_DEVELOPING = false;
    public static final boolean LOCATION_DEBUG = false;
    public static final int PAGE_SIZE = 10;
    public static boolean USE_DEBUG_SERVER = false;
    public static final boolean USE_DUMPCATCHER = true;

    static {
		IS_DEVELOPING = Droidmi.IS_DEVELOPING;
		USE_DEBUG_SERVER = PackageUtil.getConfigBoolean("use_debug_server");
	}
}
