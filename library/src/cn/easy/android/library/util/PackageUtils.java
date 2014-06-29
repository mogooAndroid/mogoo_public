package cn.easy.android.library.util;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Looper;

import com.michelin.droid.util.EvtLog;

/**
 * Title: PackageUtil</p>
 * Description: 应用程序包工具类</p>
 * @author lin.xr
 * @date 2014-6-27 下午11:59:27
 */
public class PackageUtils {

	static final String TAG = "PackageUtils";

	/**
	 * 获取应用程序的版本号
	 * 
	 * @param context
	 *            上下文
	 * @return 版本号
	 * @throws NameNotFoundException
	 *             找不到改版本号的异常信息
	 */
	public static int getVersionCode(Context context)
			throws NameNotFoundException {
		int verCode = context.getPackageManager().getPackageInfo(
				context.getPackageName(), 0).versionCode;

		return verCode;
	}

	/**
	 * 获取应用程序的外部版本号
	 * 
	 * @param context
	 *            上下文
	 * @return 外部版本号
	 * @throws NameNotFoundException
	 *             找不到信息的异常
	 */
	public static String getVersionName(Context context)
			throws NameNotFoundException {
		String versionName = context.getPackageManager().getPackageInfo(
				context.getPackageName(), 0).versionName;

		return versionName;
	}

	/**
	 * 读取AndroidManifest.xml中application标签下meta的配置的字符串，如果不存在，则返回空字符串
	 * 
	 * @param context
	 *            上下文
	 * @param key
	 *            键名
	 * @return 返回字符串
	 */
	public static String getConfigString(Context context, String key) {
		String val = "";
		try {
			ApplicationInfo appInfo = context.getPackageManager()
					.getApplicationInfo(context.getPackageName(),
							PackageManager.GET_META_DATA);
			val = appInfo.metaData.getString(key);
			if (val == null) {
				EvtLog.w(TAG, "please set config value for " + key
						+ " in AndrodiManifest.xml first");
			}
		} catch (Exception e) {
			EvtLog.w(TAG, e);
		}
		return val;
	}

	/**
	 * 读取AndroidManifest.xml中application标签下meta的配置的整型值
	 * 
	 * @param context
	 *            上下文
	 * @param key
	 *            键名
	 * @return 返回整型值
	 */
	public static int getConfigInt(Context context, String key) {
		int val = 0;
		try {
			ApplicationInfo appInfo = context.getPackageManager()
					.getApplicationInfo(context.getPackageName(),
							PackageManager.GET_META_DATA);
			val = appInfo.metaData.getInt(key);
		} catch (Exception e) {
			EvtLog.w(TAG, e);
		}
		return val;
	}

	/**
	 * 读取AndroidManifest.xml中application标签下meta的配置的布尔值
	 * 
	 * @param context
	 *            上下文
	 * @param key
	 *            键名
	 * @return 返回布尔值
	 */
	public static boolean getConfigBoolean(Context context, String key) {
		boolean val = false;
		try {
			ApplicationInfo appInfo = context.getPackageManager()
					.getApplicationInfo(context.getPackageName(),
							PackageManager.GET_META_DATA);
			val = appInfo.metaData.getBoolean(key);
		} catch (Exception e) {
			EvtLog.w(TAG, e);
		}
		return val;
	}

	/**
	 * 动态获取资源在R类中的常量值
	 * 
	 * @param context
	 *            上下文
	 * @param resourcesName
	 *            资源名
	 * @param defType
	 *            资源所属的类 drawable, id, string, layout等
	 * @return 资源id
	 */
	public static int getIdentifier(Context context, String resourcesName,
			String defType) {
		return context.getResources().getIdentifier(resourcesName, defType,
				context.getPackageName());
	}
	
	/**
	 * 指定的activity所属的应用，是否可见 申请权限 android.permission.GET_TASKS
	 * 
	 * @param context
	 *            上下文
	 * @return 是否可见
	 */
	public static boolean isTopApplication(Context context) {
		String packageName = context.getPackageName();
		ActivityManager activityManager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> tasksInfo = activityManager.getRunningTasks(1);
		if (tasksInfo.size() > 0) {
			// 应用程序位于堆栈的顶层
			if (packageName.equals(tasksInfo.get(0).topActivity
					.getPackageName())) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 获取是否安装指定包名的应用
	 * 
	 * @param context
	 *            上下文
	 * @param packageName
	 *            包名
	 * @return 是否安装指定包名的应用
	 */
	public static boolean haveInstallApp(Context context, String packageName) {
		if (packageName == null || "".equals(packageName)) {
			return false;
		}
		try {
			context.getPackageManager().getApplicationInfo(packageName,
					PackageManager.GET_UNINSTALLED_PACKAGES);
			return true;
		} catch (NameNotFoundException e) {
			return false;
		}
	}
	
	/**
	 * 获取是否在UI主线程
	 * 
	 * @return 是否在UI主线程
	 */
	public static boolean isRunOnUiThread() {
		return Thread.currentThread().getId() == Looper.getMainLooper()
				.getThread().getId();
	}
}
