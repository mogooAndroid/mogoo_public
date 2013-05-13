package com.michelin.droidmi.download;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

public class DownloadMgr {
	static final String tag = "DownloadMgr";
	
	static final int MAX_TASK = 2;
	
	static Context mCtx = null;
	static List list = new ArrayList<DownloadTask>();
	static int wifi_policy = -1;
	
	static Handler no_wifi = new Handler() {
		public void handleMessage(Message message) {
			DownloadTask downloadtask = (DownloadTask) message.obj;
			downloadtask.stop();
			DownloadMgr.showWifiChangeDialog(downloadtask);
		}
	};
	
	static Handler onFinish = new Handler() {
		public void handleMessage(Message message) {
			DownloadTask downloadtask = (DownloadTask) message.obj;
			DownloadMgr.list.remove(downloadtask);
			// TODO 下载完成回调
			// PdNotifications.notify(DownloadMgr.mCtx, downloadtask.name, 3);
			// AudioNotification.onFinish(DownloadMgr.mCtx,
			// downloadtask.resType,
			// downloadtask.path);
			TaskProvider.deleteTask(DownloadMgr.mCtx, downloadtask);
			// PdStatisticsUtil.downloadedStatistics(downloadtask);
			// DownloadMgr.apkInstall(downloadtask);
			// ApkCache.addApk(new File(downloadtask.path));
			// if (downloadtask.path.endsWith(".mp3"))
			// RingCache.loadRing(DownloadMgr.mCtx);
			// if (downloadtask.isSoft())
			// BlackList.checkBlackList(downloadtask.pkgName,
			// downloadtask.path);
			// DownloadMgr.fireSystemEvent(downloadtask);
		}
	};
	
	static Handler onSchedule = new Handler() {
		public void handleMessage(Message paramMessage) {
			DownloadMgr.scheduleTask();
		}
	};
	
	public static void init(Context context) {
		mCtx = context;
	}
	
	public static DownloadTask addTask(DownloadTask downloadtask) {
		if (hasAdd(downloadtask.downloadUrl, true)) {
			// LogUtil.w(tag, "任务已经存在");
		} else {
			list.add(downloadtask);
			TaskProvider.insertTask(mCtx, downloadtask);
			boolean flag = scheduleTask();
			// TODO 存在下载任务通知
			// if (scheduleTask())
			// PdNotifications.notify(mCtx, null, 0);
			// fireSystemEvent(downloadtask);
		}
		return downloadtask;
	}
	
	public static boolean hasAdd(String url, boolean paramBoolean) {
		for (int i = 0; i < list.size(); i++) {
			if (((DownloadTask) list.get(i)).downloadUrl.equals(url))
				return true;
		}
		return false;
	}
	
	static void scheduleTask(DownloadTask downloadtask) {
		Message message = Message.obtain();
		message.obj = downloadtask;
		onSchedule.sendMessage(message);
	}
	
	static void onFinish(DownloadTask downloadtask) {
		Message message = Message.obtain();
		message.obj = downloadtask;
		onFinish.sendMessage(message);
	}

	static boolean scheduleTask() {
		int i = 0;
		for (int j = 0; j < list.size(); j++)
			if (((DownloadTask) list.get(j)).isDownloading())
				i++;

		boolean flag = true;
		for (int k = 0; k < list.size() && i < MAX_TASK; k++) {
			DownloadTask downloadtask = (DownloadTask) list.get(k);
			if (downloadtask.state == 0 && i < MAX_TASK) {
				// TODO 任务开始通知
				// PdNotifications.notify(mCtx, downloadtask.name, 0);
				downloadtask.start();
				i++;
				flag = false;
			}
		}

		if (!flag || list.isEmpty())
			return false;
		else
			return true;
	}

	static void showWifiChangeDialog(DownloadTask paramDownloadTask) {
		// TODO wifi 状态改变对话框
//	    String str1 = paramDownloadTask.resourceId;
//	    String str2 = paramDownloadTask.name;
//	    Intent localIntent = new Intent(mCtx, CustomAlertDialog.class);
//	    localIntent.putExtra("INTENT_EXTRA_TITLE", new DownloadMgr.4(str2, str1));
//	    localIntent.setFlags(268435456);
//	    mCtx.startActivity(localIntent);
  	}
}
