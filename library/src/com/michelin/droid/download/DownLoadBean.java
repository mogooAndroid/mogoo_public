package com.michelin.droid.download;

import java.io.Serializable;

public class DownLoadBean implements Serializable {
	public String resId;
	public String appName;
	public String versionName;
	public String downloadUrl;
	public String passID;
	public String pkgName;
	public String ruleType;
	public int localIntegral;//本地积分
}
