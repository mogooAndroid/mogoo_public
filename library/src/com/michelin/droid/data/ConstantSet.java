package com.michelin.droid.data;

import com.michelin.droid.util.PackageUtil;

public class ConstantSet {
	public static boolean IS_DEVELOPING = false;
    public static final int PAGE_SIZE = 10;
    
	static {
		IS_DEVELOPING = PackageUtil.getConfigBoolean("is_developing");
	}
}
