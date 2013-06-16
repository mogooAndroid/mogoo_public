
package com.michelin.droid.download;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DownloadMgr {
    static Context mCtx;
    static Handler onFinish;
    static final int MAX_TASK = 2;
    static List<DownloadTask> list;
    static Handler no_wifi;
    static Handler onSchedule;
    static String tag = "DownloadMgr";
    static int wifi_policy;

    static
    {
        list = new ArrayList();
        mCtx = null;
        wifi_policy = -1;
        onFinish = new Handler()
        {
            public void handleMessage(Message message)
            {
                DownloadTask downloadtask = (DownloadTask)message.obj;
                DownloadMgr.list.remove(downloadtask);
                //PdNotifications.notify(DownloadMgr.mCtx, downloadtask.name, 3);
                //AudioNotification.onFinish(DownloadMgr.mCtx, downloadtask.resType, downloadtask.path);
                TaskProvider.deleteTask(DownloadMgr.mCtx, downloadtask);
                //PdStatisticsUtil.downloadedStatistics(downloadtask);
                DownloadMgr.apkInstall(downloadtask);
                //ApkCache.addApk(new File(downloadtask.path));
                //if(downloadtask.path.endsWith(".mp3"))
                //    RingCache.loadRing(DownloadMgr.mCtx);
                //if(downloadtask.isSoft())
                 //   BlackList.checkBlackList(downloadtask.pkgName, downloadtask.path);
                //DownloadMgr.fireSystemEvent(downloadtask);
            }
        };
        onSchedule = new Handler()
        {
            public void handleMessage(Message paramMessage)
            {
                DownloadMgr.scheduleTask();
            }
        };
        no_wifi = new Handler()
        {
            public void handleMessage(Message paramMessage)
            {
                DownloadTask localDownloadTask = (DownloadTask) paramMessage.obj;
                localDownloadTask.stop();
                DownloadMgr.showWifiChangeDialog(localDownloadTask);
            }
        };
    }

    public void init(Context context) {
        mCtx = context;
    }

    static void onFinish(DownloadTask paramDownloadTask)
    {
        Message localMessage = Message.obtain();
        localMessage.obj = paramDownloadTask;
        onFinish.sendMessage(localMessage);
    }
}
