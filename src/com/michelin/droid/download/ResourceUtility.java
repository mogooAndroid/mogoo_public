package com.michelin.droid.download;

import com.michelin.droid.data.SystemConst;

public class ResourceUtility {
	private static final String TAG = "ResourceUtility";
	private static final String DIR_PICTURE = "pictures";
	private static final String DIR_THEME = "themes";
	public static final String DIR_RING = "rings";
	public static final String DIR_SOFT = "apps";
	public static final String EXT_MP3 = ".mp3";
	public static final String EXT_SOFT = ".apk";
	public static String dat = ".dat";
	public static String img = ".img";
	public static String tmp = ".tmp";
	public static String unfinished = ".unfinished";

	/**
	 * 返回下载路径
	 * 
	 * @author lcq
	 * @date 2012-12-24
	 * @param type 下载的文件类型1为app,2为theme,3为rings,4为pic
	 * @param subPath 下载路径的子路径
	 * @return
	 */
	public static String getFileDir(int type, String subPath) {
		String str = SystemConst.BASE_DIR;
		switch (type) {
		case 1:
			str = str + "/apps/";
			break;
		case 2:
			str = str + "/themes/";
			break;
		case 3:
			str = str + "/rings/";
			break;
		case 4:
			str = str + "/pictures/";
			break;
		default:
			break;
		}
		if (subPath != null)
			str = str + subPath + "/";
		return str;
	}

	/**
	 * 
	 * @author lcq
	 * @date 2012-12-24
	 * @param resName apk的名称
	 * @param verName apk的版本号
	 * @param resId apk的id
	 * @return
	 */
	public static String getFileName(String resName, String verName,
			String resId) {
		StringBuffer localStrBuffer = new StringBuffer();
		if ((resName != null) && (!resName.trim().equals(""))) {
			localStrBuffer.append(resName);
			String str;
			if ((verName != null) && (!verName.trim().equals("")))
				str = "_" + verName;
			else
				str = "";
			localStrBuffer.append(str);
			if ((resId != null) && (!resId.trim().equals("")))
				str = "_" + resId;
			else
				str = "";
			localStrBuffer.append(str);
		}
		return localStrBuffer.toString().replaceAll("/", "")
				.replaceAll("<", "").replaceAll(">", "").replaceAll(":", "")
				.replaceAll("\"", "").replaceAll("\\?", "");
	}
}
