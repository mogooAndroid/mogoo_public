package com.michelin.droid.download;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.michelin.droid.provider.PandaSpaceDatabaseHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

//lcq
public class TaskProvider {
	public static final String TABLE = "download_task";

	static void deleteAllTask(Context paramContext) {
		excuteSQL(paramContext, "delete from download_task", null);
	}

	static void deleteTask(Context paramContext, DownloadTask paramDownloadTask) {
		Object[] arrayOfObject = new Object[2];
		arrayOfObject[0] = paramDownloadTask.resourceId;
		arrayOfObject[1] = paramDownloadTask.name;
		excuteSQL(paramContext,
				"delete from download_task where resourceId = ? and name = ?",
				arrayOfObject);
	}

	private static void excuteSQL(Context paramContext, String paramString,
			Object[] paramArrayOfObject) {
		SQLiteDatabase localSQLiteDatabase = null;
		try {
			localSQLiteDatabase = new PandaSpaceDatabaseHelper(paramContext,
					"pandaspaceDB", null, 6).getWritableDatabase();
			if (paramArrayOfObject != null)
				localSQLiteDatabase.execSQL(paramString, paramArrayOfObject);
			else {
				localSQLiteDatabase.execSQL(paramString);
			}
		} catch (Exception localException) {
			localException.printStackTrace();
			if (localSQLiteDatabase != null)
				localSQLiteDatabase.close();
		} finally {
			if (localSQLiteDatabase != null)
				localSQLiteDatabase.close();
		}
	}

	public static String getTimeStr() {
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
	}

	static void insertTask(Context paramContext, DownloadTask paramDownloadTask) {
		Object[] arrayOfObject = new Object[10];
		arrayOfObject[0] = paramDownloadTask.resourceId;
		arrayOfObject[1] = paramDownloadTask.name;
		arrayOfObject[2] = paramDownloadTask.totalSize;
		arrayOfObject[3] = paramDownloadTask.path;
		arrayOfObject[4] = paramDownloadTask.downloadUrl;
		arrayOfObject[5] = paramDownloadTask.logoUrl;
		arrayOfObject[6] = paramDownloadTask.pkgName;
		arrayOfObject[7] = paramDownloadTask.versionName;
		arrayOfObject[8] = getTimeStr();
		arrayOfObject[9] = Integer.valueOf(paramDownloadTask.resType);
		excuteSQL(
				paramContext,
				"insert into download_task (resourceId,name,size,path,url,logo_url,pkg_name,version_name,start_time,res_type) values(?,?,?,?,?,?,?,?,?,?)",
				arrayOfObject);
	}

	static List<DownloadTask> loadRunningTasks(Context paramContext) {
		return loadTask(
				paramContext,
				"select resourceId,name,size,path,url,logo_url,pkg_name,version_name,res_type from download_task where end_time is null order by start_time desc",
				false);
	}

	private static List<DownloadTask> loadTask(Context paramContext,
			String paramString, boolean paramBoolean) {
		ArrayList localArrayList = new ArrayList();
		SQLiteDatabase localSQLiteDatabase = null;
		try {
			localSQLiteDatabase = new PandaSpaceDatabaseHelper(paramContext,
					"pandaspaceDB", null, 6).getWritableDatabase();
			Cursor localCursor = localSQLiteDatabase
					.rawQuery(paramString, null);
			if (localCursor.moveToFirst())
				do {
					DownloadTask localDownloadTask = new DownloadTask();
					localDownloadTask.resourceId = localCursor.getString(0);
					localDownloadTask.name = localCursor.getString(1);
					localDownloadTask.totalSize = Long.parseLong(localCursor
							.getString(2));
					localDownloadTask.path = localCursor.getString(3);
					localDownloadTask.downloadUrl = localCursor.getString(4);
					localDownloadTask.logoUrl = localCursor.getString(5);
					localDownloadTask.pkgName = localCursor.getString(6);
					localDownloadTask.versionName = localCursor.getString(7);
					localDownloadTask.resType = localCursor.getInt(8);
					localDownloadTask.loadSize = localDownloadTask
							.getCurrentSize();
					localDownloadTask.percent = localDownloadTask
							.caculatePercent();
					if (paramBoolean) {
						localDownloadTask.state = 4;
						localDownloadTask.percent = 100;
					}
					localDownloadTask.initParse(localDownloadTask.downloadUrl);
					localArrayList.add(localDownloadTask);
				} while (localCursor.moveToNext());
			localCursor.close();
			return localArrayList;
		} catch (Exception localException) {
				localException.printStackTrace();
		} finally {
			if (localSQLiteDatabase != null)
				localSQLiteDatabase.close();
			return localArrayList;
		}
	}

	static void updateTaskSizeAndPath(Context paramContext,
			DownloadTask paramDownloadTask) {
		Object[] arrayOfObject = new Object[4];
		arrayOfObject[0] = paramDownloadTask.totalSize;
		arrayOfObject[1] = paramDownloadTask.path;
		arrayOfObject[2] = paramDownloadTask.resourceId;
		arrayOfObject[3] = paramDownloadTask.name;
		excuteSQL(
				paramContext,
				"update download_task set size=?, path =?  where resourceId = ? and name = ?",
				arrayOfObject);
	}
}