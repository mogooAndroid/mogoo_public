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
	static final int RECONNECT_TIMES = 10;
	static final int SLEEP_TIME = 5000;
	public static final int STATE_WAIT = 0;
	public static final int STATE_CONNETING = 1;
	public static final int STATE_DOWNLOADING = 2;
	public static final int STATE_PAUSED = 3;
	public static final int STATE_FINISHED = 4;
	public static final int STATE_NET_ERROR = 5;
	public static final int STATE_FILE_ERROR = 6;
	Thread mThread;

	String downloadUrl = null;
	int failCode = 0;
	String path = "";
	String name;
	long loadSize;
	long totalSize;
	int percent;
	String pkgName;
	int state = STATE_WAIT;
	String versionCode;
	String versionName;
	String resourceId;
	String logoUrl;
	int resType;

	String headerMsg = "";
	boolean downloadFlag; // 是否正在下载
	boolean lastState_IsWifi;
	Map<Object, DownloadTaskListener> listenerMap = new ConcurrentHashMap();
	static long lastSendTime = System.currentTimeMillis();

	public DownloadTask() {
	}

	public DownloadTask(String url) {
		this.downloadUrl = url;
	}

	public DownloadTask(String url, String fileName) {
		this.downloadUrl = url;
		this.name = fileName;
		this.resType = ResourceUtility.RESOURCE_TYPE_SOFT;
		this.resourceId = url.hashCode() + "";
	}

	void start() {
		downloadFlag = true;
		setState(STATE_CONNETING);
		mThread = new DownloadThread();
		mThread.start();
	}

	public void stop() {
		downloadFlag = false;
		setState(STATE_PAUSED);
	}

	public void resume() {
		setState(STATE_WAIT);
	}

	class DownloadThread extends Thread {
		@Override
		public void run() {
			int i = 0;
			while (true) {
				try {
					// Log.d(DownloadTask.this.tag, Thread.currentThread()
					// .getId() + ":名称:" + DownloadTask.this.name);
					// Log.d(DownloadTask.this.tag, Thread.currentThread()
					// .getId()
					// + ":downloadFlag:"
					// + DownloadTask.this.downloadFlag);
					if ((i >= 10) || (!downloadFlag)) {
						downloadFlag = false;
						DownloadMgr.scheduleTask(DownloadTask.this);
						if (isError()) {
							// PdStatisticsUtil.downloadFailureStatistics(
							// DownloadMgr.mCtx, DownloadTask.this);
							// PdNotifications.notify(DownloadMgr.mCtx,
							// DownloadTask.this.name, 1);
							return;
						}
					}
					Log.d(DownloadTask.this.tag, Thread.currentThread().getId()
							+ ":循环下载:" + i);
					DownloadTask.this.setState(STATE_CONNETING);
					DownloadTask.this.setNetworkConnectState();
					File file = ResourceUtility.searchApk(name, versionName,
							resourceId);
					if (file.exists()) {
						Log.d(tag, file.getAbsolutePath() + "already exist");
						return ;
					}
					if (download(i)) {
						downloadFlag = false;
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
		sendMsg(DownloadTaskListener.STATE_CHANGE);
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
	 * 获取当前已下载文件的大小，文件断点
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
				&& !s.contains("html")) {
			flag = true;
		} else if (s == null) {// 这里是为了避开某些网站没有设置contentType						
			flag = true;
		} else {
			flag = false;
		}
		return flag;
	}

	boolean download(int i) {
		HttpURLConnection httpUrlConnection = null;
		if (downloadUrl != null) {
			try {
				URL url = new URL(downloadUrl);
				loadSize = getCurrentSize();
				if (loadSize == 0 || loadSize != totalSize) {
					httpUrlConnection = NetChoose.getAvailableNetwork(
							DownloadMgr.mCtx, url);
					httpUrlConnection.setConnectTimeout(TIME_OUT);
					httpUrlConnection.setReadTimeout(TIME_OUT);
					String str = new StringBuilder("bytes=").append(loadSize)
							.append("-").toString();
					if (loadSize > 0) {
						httpUrlConnection.setRequestProperty("Range", str);
					}
					httpUrlConnection.connect();
					if (loadSize > 0) {
						String s2 = httpUrlConnection
								.getRequestProperty("RANGE");
						// 这里可以不要，主要是判断请求中的range的字符串跟自己写的是不是一样
						if (!str.equals(s2)) {
							setState(STATE_NET_ERROR);
							Log.w(tag, "range的字符串不符" + s2);
							return false;
						}
					}
					String contentType = httpUrlConnection.getContentType();
					if (isContentTypeSatisfy(contentType)) {
						int length = httpUrlConnection.getContentLength();
						if (length > 0) {
							if (loadSize == 0) {
								totalSize = length;
								path = new StringBuilder(
										ResourceUtility.getPath(this,
												downloadUrl)).append(
										ResourceUtility.tmp).toString();
								TaskProvider.updateTaskSizeAndPath(
										DownloadMgr.mCtx, this);
							}
							if (i >= 5 || totalSize == loadSize + length) {
								InputStream inputstream;
								inputstream = httpUrlConnection
										.getInputStream();
								setHeaderMsg(httpUrlConnection);
								byte[] buffer;
								buffer = new byte[4096];
								RandomAccessFile randomaccessfile = new RandomAccessFile(path,
										"rw");
								randomaccessfile.seek(loadSize);
								setState(STATE_DOWNLOADING);

								int k;
								while (downloadFlag) {
									if ((k = inputstream.read(buffer, 0, 4096)) != -1) {
										randomaccessfile.write(buffer, 0, k);
										loadSize = loadSize + (long) k;
										int curPercent = caculatePercent();
										if (curPercent != percent)
											setPercent(curPercent);
										if (loadSize > totalSize) {
											setState(STATE_FILE_ERROR);
											return false;
										}
									} else {
										break;
									}
								}

								inputstream.close();
								randomaccessfile.close();
								httpUrlConnection.disconnect();
								if (!downloadFlag) {
									setState(STATE_PAUSED);
									return true;
								} else {
									setState(STATE_FINISHED);
									return true;
								}

							} else {
								setState(STATE_NET_ERROR);
								setFailCode(9002);
								Log.e(tag,
										(new StringBuilder(
												"total.size !=(loadSize + fileSize) ,bean.size:"))
												.append(totalSize)
												.append(",loadSize:")
												.append(loadSize)
												.append(",fileSize:")
												.append(length).toString());
								return false;
							}
						} else {
							setState(STATE_NET_ERROR);
							return false;
						}
					} else {
						setState(STATE_NET_ERROR);
						return false;
					}

				} else {
					setState(STATE_FINISHED);
					return true;
				}

			} catch (IOException e) {
				setState(STATE_NET_ERROR);
				e.printStackTrace();
			}

		} else {
			setState(STATE_NET_ERROR);
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
		return downloadFlag;
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
			sendMsg(DownloadTaskListener.PROGRESS_CHANGE);
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
			Map.Entry entry = (Map.Entry) localIterator.next();
			// ((Map.Entry) localObject).getKey();
			DownloadTaskListener downloadtasklistener = (DownloadTaskListener) ((Map.Entry) entry)
					.getValue();
			if (downloadtasklistener != null) {
				Message message = Message.obtain();
				message.what = paramInt;
				downloadtasklistener.sendMessage(message);
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

	public void setPercent(int percent) {
		this.percent = percent;
		fireProgressChangeEvent();
	}

	void setState(int i) {
		if (state != STATE_PAUSED || i != STATE_FILE_ERROR && i != STATE_NET_ERROR) {
			state = i;
			if (i == STATE_FINISHED) {
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

	public void setFailCode(int paramInt) {
		this.failCode = paramInt;
	}

	public void addDownloadListener(DownloadTaskListener downloadtasklistener,
			Object obj) {
		listenerMap.remove(obj);
		listenerMap.put(obj, downloadtasklistener);
	}
}
