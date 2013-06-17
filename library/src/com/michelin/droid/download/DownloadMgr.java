package com.michelin.droid.download;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;

import com.michelin.droid.util.TelephoneUtil;

public class DownloadMgr {
	static Context mCtx;
	static Handler onFinish;
	static final int MAX_TASK = 2;
	static List<DownloadTask> list;
	static Handler no_wifi;
	static Handler onSchedule;
	static String tag = "DownloadMgr";
	static int wifi_policy;

	static {
		list = new ArrayList();
		mCtx = null;
		wifi_policy = -1;
		onFinish = new Handler() {
			public void handleMessage(Message message) {
				DownloadTask downloadtask = (DownloadTask) message.obj;
				DownloadMgr.list.remove(downloadtask);
				// PdNotifications.notify(DownloadMgr.mCtx, downloadtask.name,
				// 3);
				// AudioNotification.onFinish(DownloadMgr.mCtx,
				// downloadtask.resType, downloadtask.path);
				TaskProvider.deleteTask(DownloadMgr.mCtx, downloadtask);
				// PdStatisticsUtil.downloadedStatistics(downloadtask);
				// DownloadMgr.apkInstall(downloadtask);
				// ApkCache.addApk(new File(downloadtask.path));
				// if(downloadtask.path.endsWith(".mp3"))
				// RingCache.loadRing(DownloadMgr.mCtx);
				// if(downloadtask.isSoft())
				// BlackList.checkBlackList(downloadtask.pkgName,
				// downloadtask.path);
				// DownloadMgr.fireSystemEvent(downloadtask);
			}
		};
		onSchedule = new Handler() {
			public void handleMessage(Message paramMessage) {
				DownloadMgr.scheduleTask();
			}
		};
		no_wifi = new Handler() {
			public void handleMessage(Message paramMessage) {
				DownloadTask localDownloadTask = (DownloadTask) paramMessage.obj;
				localDownloadTask.stop();
				DownloadMgr.showWifiChangeDialog(localDownloadTask);
			}
		};
	}

	public void init(Context context) {
		mCtx = context;
		destroy();
		list = TaskProvider.loadRunningTasks(mCtx);
		setWifiPolicy();
		if (noWifi())
			pauseAllTask();
//		ApkCache.loadApk();
		// RingCache.loadRing(mCtx);
	}

	static void pauseAllTask() {
		for (int i = 0; i < list.size(); i++)
			((DownloadTask) list.get(i)).stop();
	}

	static boolean noWifi() {
		boolean flag = TelephoneUtil.isWifiEnable(mCtx);
//		boolean flag1 = PreferenceUtil.getBoolean(mCtx,
//				"NOTIFY_LARGE_WITHOUT_WIFI",
//				PreferenceUtil.DEFAULT_NOTIFY_LARGE_FILE_WITHOUT_WIFI);
		return !flag && /*flag1 &&*/ list.size() > 0;
	}

	public static DownloadTask addTask(DownloadTask paramDownloadTask) {
		if (!hasAdd(paramDownloadTask.downloadUrl, true)) {
			list.add(0, paramDownloadTask);
			TaskProvider.insertTask(mCtx, paramDownloadTask);
			if (scheduleTask()) {
				// PdNotifications.notify(mCtx, null, 0);// 提示开始下载
			}
			// fireSystemEvent(paramDownloadTask);
		} else {
			Log.w(tag, "任务已经存在");
		}
		return paramDownloadTask;
	}

	public static boolean hasAdd(String s, boolean paramBoolean) {
		int i = 0;
		do {
			if (i >= list.size())
				return false;
			if (((DownloadTask) list.get(i)).downloadUrl.equals(s))
				return true;
			i++;
		} while (true);
	}

	public static void deleteTask(DownloadTask paramDownloadTask) {
		paramDownloadTask.stop();
		paramDownloadTask.deleteFile();
		list.remove(paramDownloadTask);
		TaskProvider.deleteTask(mCtx, paramDownloadTask);
		if (scheduleTask()) {
			// PdNotifications.notify(mCtx, null, 0);
		}
		// fireSystemEvent(paramDownloadTask);
		// PdNotifications.notify(mCtx, paramDownloadTask.name, 6);
	}

	public static void destroy() {
		stopAll();
		list.clear();
	}

	public static void stopAll() {
		int i = 0;
		do {
			if (i >= list.size()) {
				retSetWifiPolicy();
				return;
			}
			((DownloadTask) list.get(i)).stop();
			i++;
		} while (true);
	}

	static void onFinish(DownloadTask paramDownloadTask) {
		Message localMessage = Message.obtain();
		localMessage.obj = paramDownloadTask;
		onFinish.sendMessage(localMessage);
	}

	static void scheduleTask(DownloadTask paramDownloadTask) {
		Message localMessage = Message.obtain();
		localMessage.obj = paramDownloadTask;
		onSchedule.sendMessage(localMessage);
	}

	static boolean scheduleTask() {
		int downingNum = 0;
		for (int j = 0; j < list.size(); j++) {
			if (((DownloadTask) list.get(j)).isDownloading())
				downingNum++;
		}
		boolean flag = true;
		for (int k = 0; k < list.size() && downingNum < 2; k++) {
			DownloadTask downloadtask = (DownloadTask) list.get(k);
			if (downloadtask.state == 0 && downingNum < 2) {
				// PdNotifications.notify(mCtx, downloadtask.name, 0);
				downloadtask.start();
				downingNum++;
				flag = false;
			}
		}
		if (!flag || list.isEmpty())
			return false;
		else
			return true;
	}

	public static Context getContext() {
		return mCtx;
	}

	static void showWifiChangeDialog(DownloadTask paramDownloadTask) {
		// 自定义提示对话框，提醒用户wifi断开
	}

	// 设置wifi在系统休眠时存活
	private static void setWifiPolicy() {
		wifi_policy = Settings.System.getInt(mCtx.getContentResolver(),
				"wifi_sleep_policy", Settings.System.WIFI_SLEEP_POLICY_DEFAULT);
		Settings.System.putInt(mCtx.getContentResolver(), "wifi_sleep_policy",
				Settings.System.WIFI_SLEEP_POLICY_NEVER);
	}

	private static void retSetWifiPolicy() {
		if (wifi_policy != -1) {
			Settings.System.putInt(mCtx.getContentResolver(),
					"wifi_sleep_policy", wifi_policy);
			wifi_policy = -1;
		}
	}
}
