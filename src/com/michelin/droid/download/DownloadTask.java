package com.michelin.droid.download;

import java.io.File;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import com.michelin.droid.download.net.NetChoose;

public class DownloadTask {
	String mUrlStr = null;
	String mPath;
	long mLoadSize;
	long mTotalSize;

	boolean download(int i) {
		HttpURLConnection httpUrlConnection = null;
		if (mUrlStr != null) {
			try {
				URL url = new URL(mUrlStr);
				mLoadSize = getCurrentSize();
				if (mLoadSize == 0 || mLoadSize != mTotalSize) {
					String str = new StringBuilder("bytes=").append("-").toString();
					httpUrlConnection = NetChoose.getAvailableNetwork(DownloadMgr.mCtx, url);
				}

			} catch (MalformedURLException e) {
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
}
