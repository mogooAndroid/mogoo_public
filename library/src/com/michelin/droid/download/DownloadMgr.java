package com.michelin.droid.download;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

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
}
