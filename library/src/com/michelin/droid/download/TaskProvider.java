package com.michelin.droid.download;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.michelin.droid.provider.PandaSpaceDatabaseHelper;

//lcq
public class TaskProvider {
	public static final String TABLE = "download_task";

	static void deleteAllTask(Context paramContext) {
		excuteSQL(paramContext, "delete from download_task", null);
	}

	public static void deleteTask(Context paramContext, DownloadTask paramDownloadTask) {
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
					"creditsDB", null, 1).getWritableDatabase();
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

	public static DownloadTask findTask(Context context, String pkgName) {
		DownloadTask task = null;
		if (pkgName != null) {
			String[] arrayOfObject = new String[1];
			arrayOfObject[0] = pkgName;
			SQLiteDatabase dataBase = null;
			try{
				dataBase = new PandaSpaceDatabaseHelper(context, "creditsDB", null,
						1).getWritableDatabase();
				Cursor cursor = dataBase
						.rawQuery(
								"select resourceId,name,size,path,url,logo_url,pkg_name,version_name,res_type from download_task where pkg_name=?",
								arrayOfObject);
				if (cursor.moveToFirst())
					do {
						task = new DownloadTask();
						task.resourceId = cursor.getString(0);
						task.name = cursor.getString(1);
						task.totalSize = Long.parseLong(cursor
								.getString(2));
						task.path = cursor.getString(3);
						task.downloadUrl = cursor.getString(4);
						task.logoUrl = cursor.getString(5);
						task.pkgName = cursor.getString(6);
						task.versionName = cursor.getString(7);
						task.resType = cursor.getInt(8);
						
						task.loadSize = task
								.getCurrentSize();
						task.percent = task
								.caculatePercent();
						task.initParse(task.downloadUrl);
					} while (cursor.moveToNext());
				cursor.close();
				return task;
			} catch (Exception localException) {
				localException.printStackTrace();
			}finally {
				if (dataBase != null)
					dataBase.close();
			}
		} 
		return task;
	}

	private static List<DownloadTask> loadTask(Context paramContext,
			String paramString, boolean paramBoolean) {
		ArrayList<DownloadTask> localArrayList = new ArrayList<DownloadTask>();
		SQLiteDatabase localSQLiteDatabase = null;
		try {
			localSQLiteDatabase = new PandaSpaceDatabaseHelper(paramContext,
					"creditsDB", null, 1).getWritableDatabase();
			Cursor localCursor = localSQLiteDatabase
					.rawQuery(paramString, null);
			if (localCursor.moveToFirst())
				do {
					DownloadTask task = new DownloadTask();
					task.resourceId = localCursor.getString(0);
					task.name = localCursor.getString(1);
					task.totalSize = Long.parseLong(localCursor
							.getString(2));
					task.path = localCursor.getString(3);
					task.downloadUrl = localCursor.getString(4);
					task.logoUrl = localCursor.getString(5);
					task.pkgName = localCursor.getString(6);
					task.versionName = localCursor.getString(7);
					task.resType = localCursor.getInt(8);
					task.loadSize = task
							.getCurrentSize();
					task.percent = task
							.caculatePercent();
					if (paramBoolean) {
						task.state = 4;
						task.percent = 100;
					}
					task.initParse(task.downloadUrl);
					localArrayList.add(task);
				} while (localCursor.moveToNext());
			localCursor.close();
			return localArrayList;
		} catch (Exception localException) {
			localException.printStackTrace();
		} finally {
			if (localSQLiteDatabase != null)
				localSQLiteDatabase.close();
		}
		return localArrayList;
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