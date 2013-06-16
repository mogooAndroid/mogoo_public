
package com.michelin.droid.download;

import com.michelin.droid.data.SystemConst;
import com.michelin.droid.data.SystemVal;
import com.michelin.droid.util.FileUtil;

import java.io.File;

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
     * 获取下载文件的完整路径
     * 
     * @author lcq 2012-12-25
     * @param downloadTask
     * @param downStr
     * @return
     */
    public static String getPath(DownloadTask downloadTask, String downStr)
    {
        String path = getFileDir(downloadTask.resType, null);
        String name = getFileName(downloadTask.name, downloadTask.versionName,
                downloadTask.resourceId);
        String fullPath = getExtname(downStr);
        fullPath = path + name + fullPath;
        FileUtil.createFile(fullPath);
        return fullPath;
    }

    /**
     * 用给的后缀名，获取下载文件的完整路径
     * 
     * @author lcq 2012-12-25
     * @param downloadTask
     * @param downStr
     * @param extName 后缀名字
     * @return
     */
    public static String getPath(DownloadTask downloadTask, String downStr, String extName)
    {
        String path = getFileDir(downloadTask.resType, null);
        String name = getFileName(downloadTask.name, downloadTask.versionName,
                downloadTask.resourceId);
        String fullPath = extName;
        fullPath = path + name + fullPath;
        FileUtil.createFile(fullPath);
        return fullPath;
    }

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
     * @author lcq
     * @date 2012-12-24
     * @param resName apk的名称
     * @param verName apk的版本号
     * @param resId apk的id
     * @return
     */
    public static String getFileName(String resName, String verName, String resId) {
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
        return localStrBuffer.toString().replaceAll("/", "").replaceAll("<", "")
                .replaceAll(">", "")
                .replaceAll(":", "").replaceAll("\"", "").replaceAll("\\?", "");
    }

    /**
     * 获取后缀文字
     * 
     * @author lcq 2012-12-24
     * @param urlStr
     * @return
     */
    public static String getExtname(String urlStr) {
        if (urlStr.contains("?"))
            urlStr = urlStr.substring(0, urlStr.indexOf("?"));
        return urlStr.substring(urlStr.lastIndexOf("."));
    }

    public static void changeDirectoryPrivilege(String paramString)
    {
        try
        {
            if (isPrivateStoreDir(paramString))
            {
                String str2 = paramString;
                String str1 = new File(SystemVal.private_file_dir).getParent();
                while (!str2.equals(str1))
                {
                    changeFilePrivilege(str2);
                    str2 = new File(str2).getParent();
                    str2 = str2;
                }
            }
        } catch (Exception localException)
        {
            localException.printStackTrace();
        }
    }

    public static void changeFilePrivilege(String s)
    {
        try
        {
            if (isPrivateStoreDir(s))
            {
                String as[] = {
                        "chmod", "777", s
                };
                Runtime.getRuntime().exec(as);
            }
            return;
        } catch (Exception exception)
        {
            exception.printStackTrace();
        }
    }

    private static boolean isPrivateStoreDir(String s)
    {
        return s != null && s.startsWith(SystemVal.private_file_dir);
    }
}
