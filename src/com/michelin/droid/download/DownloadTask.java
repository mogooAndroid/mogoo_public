package com.michelin.droid.download;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import com.michelin.droid.download.net.NetChoose;

public class DownloadTask {
	String mUrlStr = null;
	String mPath;
	long mLoadSize;
	long mTotalSize;
	String mPath = "";

	boolean download(int i) {
		HttpURLConnection httpUrlConnection = null;
		if (mUrlStr != null) {
			try {
				URL url = new URL(mUrlStr);
				mLoadSize = getCurrentSize();
				if (mLoadSize == 0 || mLoadSize != mTotalSize) {
					String str = new StringBuilder("bytes=").append("-").toString();
					httpUrlConnection = NetChoose.getAvailableNetwork(DownloadMgr.mCtx, url);
					httpUrlConnection.setConnectTimeout(20000);
					httpUrlConnection.setReadTimeout(20000);
					if(mLoadSize > 0) {
						httpUrlConnection.setRequestProperty("Range", str);
					}
					httpUrlConnection.connect();
					String contentType = httpUrlConnection.getContentType();
					if(mLoadSize <= 0) {
						if(isContentTypeSatisfy(contentType)) {
							int length = httpUrlConnection.getContentLength();
							if(length > 0){
								if(mLoadSize == 0) {
									mTotalSize = length;
									
								}
							} else {
								return false;
							}
						}
					}
				} else {
					
				}

			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

		return false;
	}

	/**
	 * 获取当前下载文件的大小
	 * 
	 * @return
	 */
	long getCurrentSize() {
		File file = new File(this.mPath);
		long length = 0L;
		if (file.exists()) {
			length = file.length();
		}
		return length;
	}

	boolean isContentTypeSatisfy(String s)
	{
		boolean flag;
		if (s != null && !s.contains("xml") && !s.contains("json") && !s.contains("html"))
			flag = true;
		else
			flag = false;
		return flag;
	}
}
