
package com.michelin.droid.download;

import android.os.Message;
import android.util.Log;

import com.michelin.droid.download.net.NetChoose;
import com.michelin.droid.util.TelephoneUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DownloadTask {
    String tag = "DownloadTask";
    static final int TIME_OUT = 20000;
    Thread mThread;

    String downloadUrl = null;
    String path = "";
    String name;
    long loadSize;
    long totalSize;
    int percent;
    String pkgName;
    int state = 0;
    String versionCode;
    String versionName;
    String resourceId;
    String logoUrl;
    int resType;

    String headerMsg = "";
    boolean downloadFlag;
    boolean lastState_IsWifi;
    Map<Object, DownloadTaskListener> listenerMap = new ConcurrentHashMap();
    static long lastSendTime = System.currentTimeMillis();

    public DownloadTask() {
    }

    public DownloadTask(String Url) {
        this.downloadUrl = Url;
    }

    void start() {
        downloadFlag = true;
        setState(1);
        mThread = new DownloadThread();
        mThread.start();
    }

    public void stop() {
        downloadFlag = false;
        setState(3);
    }

    class DownloadThread extends Thread {
        @Override
        public void run() {
            while (true) {
                int i;
                try {
                    // Log.d(DownloadTask.this.tag, Thread.currentThread()
                    // .getId() + ":名称:" + DownloadTask.this.name);
                    // Log.d(DownloadTask.this.tag, Thread.currentThread()
                    // .getId()
                    // + ":downloadFlag:"
                    // + DownloadTask.this.downloadFlag);
                    i = 0;
                    if ((i < 10) && (DownloadTask.this.downloadFlag))
                        continue;
                    DownloadTask.this.downloadFlag = false;
                    DownloadMgr.scheduleTask(DownloadTask.this);
                    if (!DownloadTask.this.isError()) {
                        // PdStatisticsUtil.downloadFailureStatistics(
                        // DownloadMgr.mCtx, DownloadTask.this);
                        // PdNotifications.notify(DownloadMgr.mCtx,
                        // DownloadTask.this.name, 1);
                        return;
                    }
                    Log.d(DownloadTask.this.tag, Thread.currentThread().getId()
                            + ":循环下载:" + i);
                    DownloadTask.this.setState(1);
                    DownloadTask.this.setNetworkConnectState();
                    if (DownloadTask.this.download(i)) {
                        DownloadTask.this.downloadFlag = false;
                        DownloadMgr.scheduleTask(DownloadTask.this);
                        return;
                    }
                } catch (Exception localException) {
                    localException.printStackTrace();
                    return;
                }
                i++;
            }
        }
    }

    void fireStateChangeEvent() {
        sendMsg(2);
    }

    void setNetworkConnectState() {
        boolean flag = TelephoneUtil.isWifiEnable(DownloadMgr.mCtx);
        // boolean flag1 = PreferenceUtil.getBoolean(DownloadMgr.mCtx,
        // "NOTIFY_LARGE_WITHOUT_WIFI",
        // PreferenceUtil.DEFAULT_NOTIFY_LARGE_FILE_WITHOUT_WIFI);
        if (lastState_IsWifi && !flag/* && flag1 */) {
            Message message = Message.obtain();
            message.obj = this;
            DownloadMgr.no_wifi.sendMessage(message);
        }
        lastState_IsWifi = flag;
    }

    void initParse(String paramString) {
        // this.mParse = new UrlParse(paramString);
        // initParse(this.mParse);
        downloadUrl = paramString;
    }

    /**
     * 获取当前下载文件的大小
     * 
     * @return
     */
    long getCurrentSize() {
        File file = new File(this.path);
        long length = 0L;
        if (file.exists()) {
            length = file.length();
        }
        return length;
    }

    public boolean isError() {
        return state == 5 || state == 6;
    }

    boolean isContentTypeSatisfy(String s) {
        boolean flag;
        if (s != null && !s.contains("xml") && !s.contains("json")
                && !s.contains("html"))
            flag = true;
        else
            flag = false;
        return flag;
    }

    boolean download(int i) {
        HttpURLConnection httpUrlConnection = null;
        if (downloadUrl != null) {
            try {
                URL url = new URL(downloadUrl);
                loadSize = getCurrentSize();
                if (loadSize == 0 || loadSize != totalSize) {
                    String str = new StringBuilder("bytes=").append("-")
                            .toString();
                    httpUrlConnection = NetChoose.getAvailableNetwork(
                            DownloadMgr.mCtx, url);
                    httpUrlConnection.setConnectTimeout(TIME_OUT);
                    httpUrlConnection.setReadTimeout(TIME_OUT);
                    if (loadSize > 0) {
                        httpUrlConnection.setRequestProperty("Range", str);
                    }
                    httpUrlConnection.connect();
                    String contentType = httpUrlConnection.getContentType();
                    if (loadSize >= 0) {
                        String s2 = httpUrlConnection.getRequestProperty("RANGE");
                        // 这里可以不要，主要是判断range的字符串跟自己写的是不是一样
                        if (!str.equals(s2)) {
                            setState(5);
                            Log.w(tag, "range的字符串不符");
                            return false;
                        }
                    }
                    if (isContentTypeSatisfy(contentType)) {
                        int length = httpUrlConnection.getContentLength();
                        if (length > 0) {
                            if (loadSize == 0) {
                                totalSize = length;
                                path = new StringBuilder(
                                        ResourceUtility.getPath(this,
                                                downloadUrl)).append(
                                        ResourceUtility.tmp).toString();
                                TaskProvider.updateTaskSizeAndPath(DownloadMgr.mCtx,
                                        this);
                            }
                            if (totalSize == loadSize + length) {
                                InputStream inputstream;
                                inputstream = httpUrlConnection
                                        .getInputStream();
                                setHeaderMsg(httpUrlConnection);
                                byte[] buffer;
                                RandomAccessFile randomaccessfile;
                                buffer = new byte[4096];
                                randomaccessfile = new RandomAccessFile(path,
                                        "rw");
                                randomaccessfile.seek(loadSize);

                                if (downloadFlag) {
                                    int k;
                                    while (true) {
                                        if ((k = inputstream.read(buffer, 0,
                                                4096)) <= 0) {
                                            // goto _L15; else goto _L19
                                            inputstream.close();
                                            randomaccessfile.close();
                                            httpUrlConnection.disconnect();
                                            return true;
                                        } else {
                                            randomaccessfile
                                                    .write(buffer, 0, k);
                                            loadSize = loadSize + (long) k;
                                            int i1 = caculatePercent();
                                            if (i1 != percent)
                                                setPercent(i1);
                                        }
                                    }
                                }

                            } else {
                                Log.e(tag,
                                        (new StringBuilder(
                                                "total.size !=(loadSize + fileSize) ,bean.size:"))
                                                .append(totalSize)
                                                .append(",loadSize:")
                                                .append(loadSize)
                                                .append(",fileSize:")
                                                .append(length).toString());
                            }
                        } else {
                            setState(5);
                            return false;
                        }
                    } else {
                        setState(5);
                        return false;
                    }

                } else {
                    setState(4);
                    return true;
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            setState(5);
        }
        return false;
    }

    /**
     * 计算进度
     * 
     * @author lcq 2013-1-4
     * @return
     */
    int caculatePercent() {
        int i = 0;
        if (totalSize != 0L && loadSize != 0L)
            i = Long.valueOf((100L * loadSize) / totalSize).intValue();
        return i;
    }

    public boolean isDownloading() {
        return this.downloadFlag;
    }

    /**
     * 每500ms通知进度更改了，防止更新太频繁
     * 
     * @author lcq 2013-1-4
     */
    void fireProgressChangeEvent() {
        long current = System.currentTimeMillis();
        if ((current - lastSendTime > 500L) || (this.percent == 100)) {
            lastSendTime = current;
            sendMsg(1);
        }
    }

    /**
     * 通知观察者进度改变了
     * 
     * @author lcq 2013-1-4
     * @param paramInt
     */
    void sendMsg(int paramInt) {
        Iterator localIterator = this.listenerMap.entrySet().iterator();
        while (localIterator.hasNext()) {
            Object localObject = (Map.Entry) localIterator.next();
            ((Map.Entry) localObject).getKey();
            DownloadTaskListener localDownloadTaskListener = (DownloadTaskListener) ((Map.Entry) localObject)
                    .getValue();
            if (localDownloadTaskListener != null) {
                localObject = Message.obtain();
                ((Message) localObject).what = paramInt;
                localDownloadTaskListener.sendMessage((Message) localObject);
            }

        }
    }

    public void setHeaderMsg(HttpURLConnection httpURLConnection) {
        if (httpURLConnection != null) {
            StringBuffer localStringBuffer = new StringBuffer();
            Iterator localIterator1 = httpURLConnection.getHeaderFields()
                    .entrySet().iterator();
            while (localIterator1.hasNext()) {
                Object entry = (Map.Entry) localIterator1.next();
                String str = (String) ((Map.Entry) entry).getKey();
                Object entryValue = (List) ((Map.Entry) entry).getValue();
                entry = new StringBuffer();
                Iterator valueList = ((List) entryValue).iterator();
                while (valueList.hasNext()) {
                    entryValue = (String) valueList.next();
                    ((StringBuffer) entry).append(str + ":"
                            + (String) entryValue);
                    ((StringBuffer) entry).append("  ");
                }
                localStringBuffer.append((StringBuffer) entry);
                localStringBuffer.append("\t\n");
            }
            this.headerMsg = localStringBuffer.toString();
        }
    }

    public void setPercent(int paramInt) {
        this.percent = paramInt;
        fireProgressChangeEvent();
    }

    void setState(int i) {
        if (state != 3 || i != 6 && i != 5) {
            state = i;
            if (i == 4) {
                rename();
                ResourceUtility.changeDirectoryPrivilege(path);
                DownloadMgr.onFinish(this);
            }
            fireStateChangeEvent();
            if (!isDownloading()) {
                DownloadMgr.scheduleTask(this);
                return;
            }
        }
    }

    void rename() {
        String s = path.substring(0, path.indexOf(ResourceUtility.tmp));
        (new File(path)).renameTo(new File(s));
        path = s;
    }

    void deleteFile() {
        File file = new File(path);
        if (file.exists())
            file.delete();
    }
}
