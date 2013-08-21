package com.michelin.droid.download;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;

//lcq
public class PdNotifications {
	public static final int TYPE_ADD_QUEUE = 5;
	public static final int TYPE_DELETE = 6;
	public static final int TYPE_FINISH = 3;
	public static final int TYPE_INSTALL = 4;
	public static final int TYPE_PAUSE = 2;
	public static final int TYPE_START = 0;
	public static final int TYPE_STOP = 1;

	private static void addQueue(Context paramContext, String paramString) {
	}

	public static void cancel(Context paramContext, String paramString) {
		((NotificationManager) paramContext.getSystemService("notification"))
				.cancel(2131296933);
	}

	private static void delete(Context paramContext) {
		if (DownloadMgr.getCount() == 0)
			doDelete(paramContext,
					(NotificationManager) paramContext
							.getSystemService("notification"));
	}

	private static void doDelete(Context paramContext,
			NotificationManager paramNotificationManager) {
		paramNotificationManager.cancel(2131296933);
	}

	private static void finish(Context paramContext, String paramString) {
		// show(paramContext, paramString, paramContext.getString(2131297270),
		// 2130837636);
	}

	/*
	 * private static String getTipContentString(Context paramContext) { int j =
	 * DownloadMgr.getErrorCount(); int i = 0; int k = DownloadMgr.getCount() -
	 * j; Object localObject = new int[4]; localObject[0] = 1; localObject[1] =
	 * 4; localObject[2] = 3; localObject[3] = 2; for (int m = 0; m <
	 * localObject.length; m++) { File[] arrayOfFile =
	 * ResourceUtility.getFiles(localObject[m]); if (arrayOfFile == null)
	 * continue; i += arrayOfFile.length; } localObject = new
	 * StringBuffer(paramContext.getString(2131297515)); if (k > 0) { Object[]
	 * arrayOfObject3 = new Object[1]; arrayOfObject3[0] = k;
	 * ((StringBuffer)localObject).append(paramContext.getString(2131297516,
	 * arrayOfObject3)); } if (j > 0) { Object[] arrayOfObject2 = new Object[1];
	 * arrayOfObject2[0] = j;
	 * ((StringBuffer)localObject).append(paramContext.getString(2131297517,
	 * arrayOfObject2)); } if (i > 0) { Object[] arrayOfObject1 = new Object[1];
	 * arrayOfObject1[0] = i;
	 * ((StringBuffer)localObject).append(paramContext.getString(2131297518,
	 * arrayOfObject1)); }
	 * ((StringBuffer)localObject).append(paramContext.getString(2131297519));
	 * return (String)((StringBuffer)localObject).toString(); }
	 */

	/*
	 * private static String getTipTitleString(String paramString) { List
	 * localList = DownloadMgr.getDownloadList(); int i = localList.size();
	 * StringBuffer localStringBuffer = new StringBuffer(); if (i <= 1) { if (i
	 * != 1) localStringBuffer.append(paramString); else
	 * localStringBuffer.append(((DownloadTask)localList.get(0)).getName()); }
	 * else {
	 * localStringBuffer.append(((DownloadTask)localList.get(0)).getName());
	 * localStringBuffer.append(",");
	 * localStringBuffer.append(((DownloadTask)localList.get(1)).getName());
	 * localStringBuffer.append("..."); } return localStringBuffer.toString(); }
	 */

	private static void installed(Context paramContext, String paramString) {
		String str = paramContext.getString(2131297522);
		// show(paramContext, paramString, str, 2130837697);
	}

	public static void notify(Context paramContext, String paramString,
			int paramInt) {
		switch (paramInt) {
		case 0:
			start(paramContext, paramString);
			break;
		case 1:
			pause(paramContext, paramString);
			break;
		case 3:
			finish(paramContext, paramString);
			break;
		case 4:
			installed(paramContext, paramString);
			break;
		case 5:
			addQueue(paramContext, paramString);
			break;
		case 6:
			delete(paramContext);
		case 2:
		}
	}

	private static void pause(Context paramContext, String paramString) {
		// show(paramContext, paramString, paramContext.getString(2131297272),
		// 2130838115);
	}

	private static void setNum(Notification paramNotification) {
		paramNotification.number = DownloadMgr.getCount();
	}

	/*
	 * private static void show(Context paramContext, String paramString1,
	 * String paramString2, int paramInt) { String str1 =
	 * getTipTitleString(paramString1); String str2 =
	 * getTipContentString(paramContext); if (paramString1 == null)
	 * show(paramContext, null, str1, str2, paramInt); else show(paramContext,
	 * paramString1 + paramString2, str1, str2, paramInt); }
	 */

	private static void show(Context paramContext, String paramString1,
			String paramString2, String paramString3, int icon) {
		Notification localNotification = null;
		if (paramString1 == null) {
			localNotification = new Notification();
			localNotification.icon = icon;
			localNotification.when = System.currentTimeMillis();
		}
		while (true) {
//			localNotification.setLatestEventInfo(paramContext, paramString2,
//					paramString3, PendingIntent
//							.getActivity(paramContext, 0, new Intent(
//									paramContext, DownloadActivity.class), 0));
			localNotification.flags = 16;
			setNum(localNotification);
			NotificationManager localNotificationManager = (NotificationManager) paramContext
					.getSystemService("notification");
			doDelete(paramContext, localNotificationManager);
			try {
				localNotificationManager.notify(2131296933, localNotification);
				return;
				/*localNotification = new Notification(paramInt, paramString1,
						System.currentTimeMillis());*/
			} catch (Exception localException) {
				while (true)
					localException.printStackTrace();
			}
		}
	}

	private static void start(Context paramContext, String paramString) {
		// show(paramContext, paramString, paramContext.getString(2131297271),
		// 2130838114);
	}
}