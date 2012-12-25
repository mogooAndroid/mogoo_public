package com.michelin.droid.util;

import java.io.File;

public class FileUtil {
	/**
	 * 创建文件夹，但不包含文件名
	 * @author lcq 2012-12-25
	 * @param path
	 * @return
	 */
	public static File createFile(String path) {
		File localFile1 = new File(path);
		File localFile2 = new File(localFile1.getAbsolutePath().substring(0,
				localFile1.getAbsolutePath().lastIndexOf(File.separator)));
		if (!localFile2.exists()) {
			createFile(localFile2.getPath());
			localFile2.mkdirs();
		}
		return localFile1;
	}
}
