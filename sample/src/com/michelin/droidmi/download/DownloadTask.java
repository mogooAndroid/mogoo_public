package com.michelin.droidmi.download;

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

import android.os.Message;
import android.util.Log;

import com.michelin.droid.download.net.NetChoose;

public class DownloadTask {

	public static final int STATE_WAIT = 0;
	public static final int STATE_CONNETING = 1;
	public static final int STATE_DOWNLOADING = 2;
	public static final int STATE_PAUSED = 3;
	public static final int STATE_FINISHED = 4;
	public static final int STATE_NET_ERROR = 5;
	public static final int STATE_FILE_ERROR = 6;

	static final String tag = "DownloadTask";
	static final int TIME_OUT = 20000;

	static long lastSendTime = System.currentTimeMillis();

	Thread mThread;

	String path = "";
	String name;
	String versionCode;
	String versionName;
	String resourceId;
	String headerMsg = "";
	String downloadUrl;
	String pkgName;
	String logoUrl;
	String cateName;

	boolean downloadFlag;
	boolean lastState_IsWifi = false;
	int percent;
	int resType;
	int state = 0;
	long loadSize;
	long totalSize;
	long size;

	Map<Object, DownloadTaskListener> listenerMap = new ConcurrentHashMap();

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

	public boolean isDownloading() {
		return this.downloadFlag;
	}

	public boolean isError() {
		if ((state == STATE_NET_ERROR) || (state == STATE_FILE_ERROR))
			return true;
		else
			return false;
	}

	public void resume() {
		setState(STATE_WAIT);
	}
	
	public void stop() {
		downloadFlag = false;
		setState(STATE_PAUSED);
	}

	public void addDownloadListener(DownloadTaskListener downloadtasklistener,
			Object obj) {
		listenerMap.remove(obj);
		listenerMap.put(obj, downloadtasklistener);
	}
	
	public void setPercent(int i) {
		percent = i;
		fireProgressChangeEvent();
	}

	public void setDownloadUrl(String s) {
		downloadUrl = s;
	}

	public String getDownloadUrl() {
		return downloadUrl;
	}

	public void setName(String s) {
		name = s;
	}

	public String getName() {
		return name;
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
					if (loadSize <= 0) {
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
								if (totalSize == loadSize + length) {
									InputStream inputstream;
									inputstream = httpUrlConnection
											.getInputStream();
									setHeaderMsg(httpUrlConnection);
									byte[] buffer;
									RandomAccessFile randomaccessfile;
									buffer = new byte[4096];
									randomaccessfile = new RandomAccessFile(
											path, "rw");
									randomaccessfile.seek(loadSize);
									setState(STATE_DOWNLOADING);
									if (downloadFlag) {
										int k;
										while (true) {
											if ((k = inputstream.read(buffer,
													0, 4096)) <= 0) /*
																	 * goto
																	 * _L15;
																	 * else goto
																	 * _L19
																	 */{
												inputstream.close();
												randomaccessfile.close();
												httpUrlConnection.disconnect();
												setState(STATE_FINISHED);
												return true;
											} else {
												randomaccessfile.write(buffer,
														0, k);
												loadSize = loadSize + (long) k;
												int i1 = caculatePercent();
												if (i1 != percent)
													setPercent(i1);
												if (loadSize > length) {
													setState(STATE_FILE_ERROR);
												}
											}
										}
									} else {
										setState(STATE_PAUSED);
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
								setState(STATE_NET_ERROR);
							}
						} else {
							setState(STATE_NET_ERROR);
						}
					}
				} else {
					setState(STATE_FINISHED);
				}

			} catch (MalformedURLException e) {
				e.printStackTrace();
				setState(STATE_NET_ERROR);
			} catch (IOException e) {
				e.printStackTrace();
				setState(STATE_FILE_ERROR);
			}

		} else {
			setState(STATE_NET_ERROR);
		}

		return false;
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

	boolean isContentTypeSatisfy(String s) {
		// boolean flag;
		// if (s != null && !s.contains("xml") && !s.contains("json") &&
		// !s.contains("html"))
		// flag = true;
		// else
		// flag = false;
		// return flag;
		return true;
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

	void start() {
		downloadFlag = true;
		setState(STATE_CONNETING);
		mThread = new DownloadThread();
		mThread.start();
	}

	void setState(int i) {
		// LogUtil.d(tag, (new
		// StringBuilder()).append(Thread.currentThread().getId()).append(":setState:").append(DownloadUtility.getStateInfo(i)).toString());
		if ((state == STATE_PAUSED)
				&& (((i == STATE_FILE_ERROR) || (i == STATE_NET_ERROR)))) {
			return;
		}
		state = i;
		if (i == STATE_FINISHED) {
			rename();
			ResourceUtility.changeDirectoryPrivilege(path);
			DownloadMgr.onFinish(this);
		}
		fireStateChangeEvent();
		if (!isDownloading())
			DownloadMgr.scheduleTask(this);
	}

	void removeFile() {
		File localFile = new File(this.path);
		if (!localFile.exists())
			return;
		localFile.delete();
	}
	
	void rename() {
		String str = path.substring(0, path.indexOf(ResourceUtility.tmp));
		new File(path).renameTo(new File(str));
		path = str;
	}

	void fireStateChangeEvent() {
		sendMsg(DownloadTaskListener.STATE_CHANGE);
	}

	void setNetworkConnectState() {
		// boolean bool1 = TelephoneUtil.isWifiEnable(DownloadMgr.mCtx);
		// boolean bool2 = PreferenceUtil.getBoolean(DownloadMgr.mCtx,
		// "NOTIFY_LARGE_WITHOUT_WIFI",
		// PreferenceUtil.DEFAULT_NOTIFY_LARGE_FILE_WITHOUT_WIFI);
		// if ((this.lastState_IsWifi) && (!bool1) && (bool2)) {
		// Message localMessage = Message.obtain();
		// localMessage.obj = this;
		// DownloadMgr.no_wifi.sendMessage(localMessage);
		// }
		// this.lastState_IsWifi = bool1;
	}

	void initParse(String s) {
		// mParse = new UrlParse(s);
		// initParse(mParse);
		downloadUrl = s;
	}

	void deleteFile() {
		File localFile = new File(this.path);
		if (!localFile.exists())
			return;
		localFile.delete();
	}
	
	class DownloadThread extends Thread {
		public void run() {
			int i = 0;
			while (true) {
				try {
					// LogUtil.d(tag, Thread.currentThread().getId()
					// + ":名称:" + name);
					// LogUtil.d(tag, Thread.currentThread().getId()
					// + ":downloadFlag:" + this.downloadFlag);
					if ((i >= 10) || (!downloadFlag)) {
						downloadFlag = false;
						DownloadMgr.scheduleTask(DownloadTask.this);
						if (isError()) {
							// PdStatisticsUtil.downloadFailureStatistics(
							// DownloadMgr.mCtx, DownloadTask.this);
							// PdNotifications.notify(DownloadMgr.mCtx,
							// name, 1);
						}
						return;
					} else {
						// LogUtil.d(tag, Thread.currentThread()
						// .getId() + ":循环下载:" + i);
						setState(STATE_CONNETING);
						setNetworkConnectState();
						if (download(i)) {
							downloadFlag = false;
							DownloadMgr.scheduleTask(DownloadTask.this);
						}
					}
				} catch (Exception localException) {
					localException.printStackTrace();
					return;
				}
				++i;
			}
		}
	}
}
