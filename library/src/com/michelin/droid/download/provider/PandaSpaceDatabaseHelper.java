package com.michelin.droid.download.provider;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class PandaSpaceDatabaseHelper extends SQLiteOpenHelper
{
    private static final String TAG = "PandaSpaceDatabaseHelper";

    public PandaSpaceDatabaseHelper(Context paramContext, String paramString,
            SQLiteDatabase.CursorFactory paramCursorFactory, int paramInt)
    {
        super(paramContext, paramString, paramCursorFactory, paramInt);
    }

    private void createTNoupdate(SQLiteDatabase paramSQLiteDatabase)
    {
        // LogUtil.d("PandaSpaceDatabaseHelper", "create TNoupdate");
        try
        {
            StringBuffer localStringBuffer = new StringBuffer();
            localStringBuffer.append("  CREATE TABLE IF NOT EXISTS ");
            localStringBuffer.append("t_no_update (");
            localStringBuffer.append("_id INTEGER PRIMARY KEY , ");
            localStringBuffer.append("_packagename VARCHAR, ");
            localStringBuffer.append("_versionname VARCHAR, ");
            localStringBuffer.append("_versioncode INTEGER) ");
            paramSQLiteDatabase.execSQL(localStringBuffer.toString());
            return;
        } catch (SQLException localSQLException)
        {
            if (paramSQLiteDatabase != null)
                paramSQLiteDatabase.close();
            localSQLException.printStackTrace();
        }
    }

    private void deleteTNoupdate(SQLiteDatabase paramSQLiteDatabase)
    {
        try
        {
            paramSQLiteDatabase.execSQL("DROP TABLE IF EXISTS t_no_update");
            return;
        } catch (SQLException localSQLException)
        {
            if (paramSQLiteDatabase != null)
                paramSQLiteDatabase.close();
            localSQLException.printStackTrace();
        }
    }

    public void onCreate(SQLiteDatabase paramSQLiteDatabase)
    {
        paramSQLiteDatabase
                .execSQL("CREATE TABLE IF NOT EXISTS user_feedback (id int, user VARCHAR, time int, email VARCHAR, content VARCHAR);");
        paramSQLiteDatabase
                .execSQL("CREATE TABLE IF NOT EXISTS apk_download (file VARCHAR, path VARCHAR, pkg_name VARCHAR, version_code VARCHAR, state int);");
        paramSQLiteDatabase
                .execSQL("CREATE TABLE IF NOT EXISTS   download_task(resourceId nvarchar(100) ,name nvarchar(100), size nvarchar(100),res_type nvarchar(100),path nvarchar(100),url nvarchar(500), logo_url nvarchar(100),pkg_name nvarchar(100), version_name nvarchar(100),start_time nvarchar(100),end_time nvarchar(100),rule nvarchar(100),points nvarchar(100))");
        paramSQLiteDatabase
                .execSQL("CREATE TABLE IF NOT EXISTS act_config (act int, url VARCHAR);");
        paramSQLiteDatabase
                .execSQL("CREATE TABLE IF NOT EXISTS param_config (param int, value VARCHAR);");
        createTNoupdate(paramSQLiteDatabase);
        paramSQLiteDatabase
                .execSQL("CREATE TABLE IF NOT EXISTS ignore_activity (id int, activityId VARCHAR);");
    }

    public void onUpgrade(SQLiteDatabase paramSQLiteDatabase, int paramInt1, int paramInt2)
    {
        paramSQLiteDatabase.execSQL("DROP TABLE IF EXISTS videoDownload");
        paramSQLiteDatabase.execSQL("DROP TABLE IF EXISTS userActionInfo");
        paramSQLiteDatabase.execSQL("DROP TABLE IF EXISTS user_applyhistory");
        onCreate(paramSQLiteDatabase);
    }
}