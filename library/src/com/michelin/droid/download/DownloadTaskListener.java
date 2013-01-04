package com.michelin.droid.download;

import android.os.Handler;
import android.os.Message;

public abstract class DownloadTaskListener extends Handler
{
  public static final int PROGRESS_CHANGE = 1;
  public static final int STATE_CHANGE = 2;
  static String tag = "DownloadTaskListener";
  DownloadTask task = null;

  public DownloadTaskListener(DownloadTask paramDownloadTask)
  {
    this.task = paramDownloadTask;
  }

  public void handleMessage(Message paramMessage)
  {
    if (this.task != null)
      switch (paramMessage.what)
      {
      case 1:
        onProgressChange(this.task.percent, this.task.loadSize);
        //LogUtil.d(tag, "handleMessage:" + this.task.name + ",percent:" + this.task.percent);
        break;
      case 2:
        //LogUtil.d(tag, "handleMessage:" + this.task.name + ",state:" + DownloadUtility.getStateInfo(this.task.getState()));
        onStateChange(this.task.state);
      }
  }

  public abstract void onProgressChange(int paramInt, long paramLong);

  public abstract void onStateChange(int paramInt);
}