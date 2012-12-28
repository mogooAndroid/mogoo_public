package com.michelin.droid.download;

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

import android.util.Log;

import com.michelin.droid.download.net.NetChoose;

public class DownloadTask {
	String tag = "DownloadTask";
	String urlStr = null;
	String path = "";
	String name;
	long loadSize;
	long totalSize;
	int percent;
	String versionCode;
	String versionName;
	String resourceId;
	int resType;
	String headerMsg = "";

	boolean download(int i) {
		HttpURLConnection httpUrlConnection = null;
		if (urlStr != null) {
			try {
				URL url = new URL(urlStr);
				loadSize = getCurrentSize();
				if (loadSize == 0 || loadSize != totalSize) {
					String str = new StringBuilder("bytes=").append("-").toString();
					httpUrlConnection = NetChoose.getAvailableNetwork(DownloadMgr.mCtx, url);
					httpUrlConnection.setConnectTimeout(20000);
					httpUrlConnection.setReadTimeout(20000);
					if(loadSize > 0) {
						httpUrlConnection.setRequestProperty("Range", str);
					}
					httpUrlConnection.connect();
					String contentType = httpUrlConnection.getContentType();
					if(loadSize <= 0) {
						if(isContentTypeSatisfy(contentType)) {
							int length = httpUrlConnection.getContentLength();
							if(length > 0){
								if(loadSize == 0) {
									totalSize = length;
									path = new StringBuilder(ResourceUtility.getPath(this, urlStr)).append(ResourceUtility.tmp).toString();
									//TaskProvider.updateTaskSizeAndPath(DownloadMgr.mCtx, this);
								}
								if (totalSize == loadSize + length) {
									InputStream inputstream;
									inputstream = httpUrlConnection.getInputStream();
									setHeaderMsg(httpUrlConnection);
									byte abyte0[];
									RandomAccessFile randomaccessfile;
									abyte0 = new byte[4096];
									randomaccessfile = new RandomAccessFile(path, "rw");
									randomaccessfile.seek(loadSize);
									
								} else {
									Log.e(tag, (new StringBuilder("total.size !=(loadSize + fileSize) ,bean.size:")).
											append(totalSize).append(",loadSize:").append(loadSize).append(",fileSize:").append(length).toString());
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
		File file = new File(this.path);
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

	public void setHeaderMsg(HttpURLConnection httpURLConnection)
	  {
	    if (httpURLConnection != null)
	    {
	      StringBuffer localStringBuffer = new StringBuffer();
	      Iterator localIterator1 = httpURLConnection.getHeaderFields().entrySet().iterator();
	      while (localIterator1.hasNext())
	      {
	        Object entry = (Map.Entry)localIterator1.next();
	        String str = (String)((Map.Entry)entry).getKey();
	        Object entryValue = (List)((Map.Entry)entry).getValue();
	        entry = new StringBuffer();
	        Iterator valueList = ((List)entryValue).iterator();
	        while (valueList.hasNext())
	        {
	          entryValue = (String)valueList.next();
	          ((StringBuffer)entry).append(str + ":" + (String)entryValue);
	          ((StringBuffer)entry).append("  ");
	        }
	        localStringBuffer.append((StringBuffer)entry);
	        localStringBuffer.append("\t\n");
	      }
	      this.headerMsg = localStringBuffer.toString();
	    }
	  }
}
