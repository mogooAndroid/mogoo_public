package com.michelin.droid.util;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;

import com.michelin.droid.app.DroidApplicationBase;

/**
 * 应用工具类.
 * 
 * @author 
 * 
 */
public class PackageUtil {

	private static final String TAG = "PackageUtil";
	private static final String DEVICE_ID = "Unknow";

	/**
	 * 获取应用程序的版本号
	 * 
	 * @return 版本号
	 * @throws NameNotFoundException
	 *             找不到改版本号的异常信息
	 */
	public static int getVersionCode() throws NameNotFoundException {
		int verCode = DroidApplicationBase.CONTEXT.getPackageManager().getPackageInfo(
				DroidApplicationBase.CONTEXT.getPackageName(), 0).versionCode;

		return verCode;
	}

	/**
	 * 获取应用程序的外部版本号
	 * 
	 * @return 外部版本号
	 * @throws NameNotFoundException
	 *             找不到信息的异常
	 */
	public static String getVersionName() throws NameNotFoundException {
		String versionName = DroidApplicationBase.CONTEXT.getPackageManager().getPackageInfo(
				DroidApplicationBase.CONTEXT.getPackageName(), 0).versionName;

		return versionName;
	}

	/**
	 * 获取MAC地址
	 * 
	 * @return 返回MAC地址
	 */
	public static String getLocalMacAddress() {
		WifiManager wifi = (WifiManager) DroidApplicationBase.CONTEXT.getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = wifi.getConnectionInfo();

		return info.getMacAddress();
	}

	/**
	 * 获取 string.xml 文件定义的字符串
	 * 
	 * @param resourceId
	 *            资源id
	 * @return 返回 string.xml 文件定义的字符串
	 */
	public static String getString(int resourceId) {
		Resources res = DroidApplicationBase.CONTEXT.getResources();
		return res.getString(resourceId);
	}

	/**
	 * 
	 * @return 获得手机端终端标识
	 */
	public static String getTerminalSign() {
		String tvDevice = null;
		TelephonyManager tm = (TelephonyManager) DroidApplicationBase.CONTEXT.getSystemService(Context.TELEPHONY_SERVICE);
		tvDevice = tm.getDeviceId();
		if (tvDevice == null) {
			tvDevice = getLocalMacAddress();
		}

		if (tvDevice == null) {
			tvDevice = DEVICE_ID;
		}

		EvtLog.d(TAG, "唯一终端标识号：" + tvDevice);
		return tvDevice;
	}

	/**
	 * 
	 * @return 获得手机型号
	 */
	public static String getDeviceType() {
		String deviceType = android.os.Build.MODEL;
		return deviceType;
	}

	/**
	 * 
	 * @return 获得操作系统版本号
	 */

	public static String getSysVersion() {
		String sysVersion = android.os.Build.VERSION.RELEASE;
		return sysVersion;
	}

	/**
	 * 读取manifest.xml中application标签下的配置项，如果不存在，则返回空字符串
	 * 
	 * @param key
	 *            键名
	 * @return 返回字符串
	 */
	public static String getConfigString(String key) {
		String val = "";
		try {
			ApplicationInfo appInfo = DroidApplicationBase.CONTEXT.getPackageManager().getApplicationInfo(
					DroidApplicationBase.CONTEXT.getPackageName(), PackageManager.GET_META_DATA);
			val = appInfo.metaData.getString(key);
			if (val == null) {
				EvtLog.e(TAG, "please set config value for " + key + " in manifest.xml first");
			}
		} catch (Exception e) {
			EvtLog.w(TAG, e);
		}
		return val;
	}

	/**
	 * 读取manifest.xml中application标签下的配置项
	 * 
	 * @param key
	 *            键名
	 * @return 返回字符串
	 */
	public static int getConfigInt(String key) {
		int val = 0;
		try {
			ApplicationInfo appInfo = DroidApplicationBase.CONTEXT.getPackageManager().getApplicationInfo(
					DroidApplicationBase.CONTEXT.getPackageName(), PackageManager.GET_META_DATA);
			val = appInfo.metaData.getInt(key);
		} catch (NameNotFoundException e) {
			EvtLog.e(TAG, e);
		}
		return val;
	}

	/**
	 * 读取manifest.xml中application标签下的配置项
	 * 
	 * @param key
	 *            键名
	 * @return 返回字符串
	 */
	public static boolean getConfigBoolean(String key) {
		boolean val = false;
		try {
			ApplicationInfo appInfo = DroidApplicationBase.CONTEXT.getPackageManager().getApplicationInfo(
					DroidApplicationBase.CONTEXT.getPackageName(), PackageManager.GET_META_DATA);
			val = appInfo.metaData.getBoolean(key);
		} catch (NameNotFoundException e) {
			EvtLog.e(TAG, e);
		}
		return val;
	}

	/**
	 * 指定的activity所属的应用，是否是当前手机的顶级
	 * 
	 * @param context
	 *            activity界面或者application
	 * @return 如果是，返回true；否则返回false
	 */
	public static boolean isTopApplication(Context context) {
		if (context == null) {
			return false;
		}

		try {
			String packageName = context.getPackageName();
			ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
			List<RunningTaskInfo> tasksInfo = activityManager.getRunningTasks(1);
			if (tasksInfo.size() > 0) {
				// 应用程序位于堆栈的顶层
				if (packageName.equals(tasksInfo.get(0).topActivity.getPackageName())) {
					return true;
				}
			}
		} catch (Exception e) {
			// 什么都不做
			EvtLog.w(TAG, e);
		}
		return false;
	}

	/**
	 * 判断APP是否已经打开
	 * 
	 * @param context
	 *            activity界面或者application
	 * @return true表示已经打开 false表示没有打开
	 */
	public static boolean isAppOpen(Context context) {
		ActivityManager mManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> mRunningApp = mManager.getRunningAppProcesses();
		int size = mRunningApp.size();
		for (int i = 0; i < size; i++) {
			if ("com.pdw.pmh".equals(mRunningApp.get(i).processName)) {
				EvtLog.d(TAG, "接收闹钟   找到进程");
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 动态获取资源id
	 * 
	 * @param context
	 * 			activity界面或者application
	 * @param name
	 * 			资源名
	 * @param defType
	 * 			资源所属的类 drawable, id, string, layout等
	 * @return 资源id
	 */
	public static int getIdentifier(Context context, String name, String defType) {
		return context.getResources().getIdentifier(name, defType, context.getPackageName());
	}
}
