/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.michelin.droidmi.util;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.StrictMode;

/**
 * Class containing some static utility methods.
 */
public class Utils {
	private static final int FROYO = Build.VERSION_CODES.FROYO;
	private static final int GINGERBREAD = Build.VERSION_CODES.GINGERBREAD;
	private static final int HONEYCOMB = 11;
	private static final int HONEYCOMB_MR1 = 12;
	private static final int JELLY_BEAN = 16;
	
    private Utils() {};

    public static boolean hasFroyo() {
        // Can use static final constants like FROYO, declared in later versions
        // of the OS since they are inlined at compile time. This is guaranteed behavior.
        return Build.VERSION.SDK_INT >= FROYO;
    }

    public static boolean hasGingerbread() {
        return Build.VERSION.SDK_INT >= GINGERBREAD;
    }

    public static boolean hasHoneycomb() {
        return Build.VERSION.SDK_INT >= HONEYCOMB;
    }

    public static boolean hasHoneycombMR1() {
        return Build.VERSION.SDK_INT >= HONEYCOMB_MR1;
    }

    public static boolean hasJellyBean() {
        return Build.VERSION.SDK_INT >= JELLY_BEAN;
    }
}
