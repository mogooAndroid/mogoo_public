package cn.easy.android.library.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Environment;

/**
 * Title: CrashHandler</p>
 * Description: 应用程序全局异常处理工具类</p>
 * @author lin.xr
 * @date 2014-6-28 上午10:52:00
 */
public class CrashHandler implements UncaughtExceptionHandler {

	/**
	 * Title: ICrashListener</p> 
	 * Description: 应用程序全局异常回调接口类</p>
	 * 
	 * @author lin.xr
	 * @date 2014-6-28 上午10:51:53
	 */
	public interface ICrashListener {
		void onCrash();
	}

	static final String TAG = "CrashHandler";

	/** 错误日志文件目录名 */
	private static final String CRASH_DIR = "crash";
	/** 错误报告文件的扩展名 */
	private static final String CRASH_REPORT_EXT = ".log";

	/** CrashHandler实例 */
	private static CrashHandler INSTANCE = new CrashHandler();

	private static final int FOUR = 4;
	private static final int SLEEP_INTERVAL = 3000;

	/** 程序的Context对象 */
	private Context mContext;

	/** 用于格式化日期,作为日志文件名的一部分 */
	private DateFormat mFormatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.getDefault());

	/** 用来存储设备信息和异常信息 */
	private Map<String, String> mInfos = new HashMap<String, String>();

	/** 系统默认的UncaughtException处理类 */
	private Thread.UncaughtExceptionHandler mDefaultHandler;
	/** 应用全局异常回调 */
	private ICrashListener mCrashListener = null;

	/** 日志文件路径 */
	private String mCrashPath = Environment.getExternalStorageDirectory()
			.getPath() + CRASH_DIR;

	/**
	 * 私有CrashHandler构造方法
	 */
	private CrashHandler() {
	}

	/**
	 * 当UncaughtException发生时会转入该函数来处理
	 * 
	 * @param thread
	 *            发生异常的线程
	 * @param ex
	 *            异常信息
	 */
	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		if (!handleException(ex) && mDefaultHandler != null) {
			// 如果用户没有处理则让系统默认的异常处理器来处理
			mDefaultHandler.uncaughtException(thread, ex);
		} else {
			try {
				Thread.sleep(SLEEP_INTERVAL);
			} catch (InterruptedException e) {
				EvtLog.w(TAG, e);
			}

			// 退出程序
			android.os.Process.killProcess(android.os.Process.myPid());
			System.exit(1);
		}
	}

	/**
	 * 获取CrashHandler实例 ,单例模式
	 * 
	 * @return boolean 返回单一实例
	 */
	public static CrashHandler getInstance() {
		return INSTANCE;
	}

	/**
	 * 初始化
	 * 
	 * @param context
	 *            上下文
	 * @param crashPath
	 *            异常的文件目录
	 * @return self
	 */
	public CrashHandler init(Context context) {
		mContext = context;
		mCrashPath = FileUtils.getDiskCachePath(mContext, CRASH_DIR);

		// 获取系统默认的UncaughtException处理器
		mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
		// 设置该CrashHandler为程序的默认处理器
		Thread.setDefaultUncaughtExceptionHandler(this);
		return this;
	}

	/**
	 * 设置应用全局异常回调
	 * 
	 * @param crashListener
	 *            应用全局异常回调
	 * @return self
	 */
	public CrashHandler setCrashListener(ICrashListener crashListener) {
		mCrashListener = crashListener;
		return this;
	}

	/**
	 * 从异常信息中获取详细的堆栈信息
	 * 
	 * @param ex
	 *            输入的异常信息
	 * @return 返回详细的堆栈信息
	 */
	public static String getStackTraceDetail(Throwable ex) {
		String stackTrace = "";
		Throwable cause = ex;
		while (cause != null) {
			StackTraceElement[] st = cause.getStackTrace();
			for (int i = 0; i < st.length; i++) {
				if ("".equals(stackTrace)) {
					stackTrace += st[i];
				} else {
					stackTrace = stackTrace + "\n" + st[i];
				}
			}
			Throwable causeBy = cause.getCause();
			stackTrace = stackTrace + "\n" + causeBy.toString();
			stackTrace = stackTrace + "\n" + "===============================";
			cause = cause.getCause();
		}
		return stackTrace;
	}

	/**
	 * 获取错误报告文件名
	 * 
	 * @param ctx
	 *            上下文
	 * @return 返回异常信息文件的列表
	 */
	public File[] getCrashReportFiles(Context ctx) {
		File[] file = null;

		File filesDirs = new File(mCrashPath);
		FilenameFilter filter = new FilenameFilter() {
			@Override
			public boolean accept(File dir, String filename) {
				return filename.endsWith(CRASH_REPORT_EXT);
			}
		};
		file = filesDirs.listFiles(filter);

		return file;
	}

	/**
	 * 保存异常信息
	 * 
	 * @param ex
	 *            异常信息
	 * @return boolean 保存状态
	 */
	private boolean saveInformation(Throwable ex) {

		try {
			// 收集设备参数信息
			collectDeviceInfo(mContext);
			// 保存日志文件
			saveCrashInfo2File(ex);
			// 保存错误报告文件
			String errorText = getLogContent(ex);
			EvtLog.w(TAG, "errorText : \n" + errorText);
		} catch (Exception e) {
			return false;
		}

		return true;
	}

	/**
	 * 收集设备参数信息
	 * 
	 * @param ctx
	 *            上下文
	 */
	private void collectDeviceInfo(Context ctx) {
		try {
			PackageManager pm = ctx.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(),
					PackageManager.GET_ACTIVITIES);
			if (pi != null) {
				String versionName = pi.versionName == null ? "null"
						: pi.versionName;
				String versionCode = pi.versionCode + "";
				mInfos.put("versionName", versionName);
				mInfos.put("versionCode", versionCode);
			}
		} catch (NameNotFoundException e) {
			EvtLog.w(TAG, e);
		}
		EvtLog.d(TAG, "device information--------------------------------->");
		Field[] fields = Build.class.getDeclaredFields();
		for (Field field : fields) {
			try {
				field.setAccessible(true);
				mInfos.put(field.getName(), field.get(null).toString());
				EvtLog.d(TAG, field.getName() + " : " + field.get(null));
			} catch (Exception e) {
				EvtLog.w(TAG, e);
			}
		}
		EvtLog.d(TAG, "device information---------------------------------<");
	}

	/**
	 * 保存错误信息到文件中
	 * 
	 * @param ex
	 *            异常信息
	 * @return 返回文件名称,便于将文件传送到服务器
	 */
	private String saveCrashInfo2File(Throwable ex) {
		String logContent = getLogContent(ex);
		try {
			long timestamp = System.currentTimeMillis();
			String time = mFormatter.format(new Date());
			String fileName = "crash-" + time + "-" + timestamp
					+ CRASH_REPORT_EXT;
			File dir = new File(mCrashPath);
			if (!dir.exists()) {
				dir.mkdirs();
			}
			File errFile = new File(dir, fileName);
			if (!errFile.exists()) {
				errFile.createNewFile();
			}
			FileOutputStream fos = new FileOutputStream(errFile);
			fos.write(logContent.getBytes());
			fos.close();
			return fileName;
		} catch (Exception e) {
			EvtLog.w(TAG, e);
		}
		return null;
	}

	/**
	 * 获取并构造友好的异常信息
	 * 
	 * @param ex
	 *            异常信息
	 * @return 以文本的方式返回异常信息内容
	 */
	private String getLogContent(Throwable ex) {
		JSONObject headerContent = new JSONObject();
		JSONObject result = new JSONObject();
		for (Map.Entry<String, String> entry : mInfos.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			try {
				headerContent.put(key, value);
			} catch (JSONException e) {
				EvtLog.w(TAG, e.getMessage());
			}
		}
		String stackTrace = "";
		JSONObject logContent = new JSONObject();
		JSONArray logArray = new JSONArray();
		JSONObject logs = new JSONObject();
		if (ex != null) {
			// stackTrace = getStackTraceDetail(ex);
			stackTrace = getSimpleStackTraceInfo(ex);
			try {
				logContent.put("Level", FOUR);
				// logContent.put("EmailEnabled",
				// PackageUtil.getConfigBoolean("email_log_enable"));
				logContent.put("Message", StringUtils.htmEncode(ex.toString()));
				logContent.put("StackTrace", StringUtils.htmEncode(stackTrace));
				logContent.put("DateTime", getDateTime());
				logArray.put(logContent);
				logs.put("Logs", logArray);
			} catch (JSONException e) {
				EvtLog.w(TAG, e.getMessage());
			}
		}

		try {
			result.put("Header", headerContent);
			result.put("Body", logs);
		} catch (JSONException e) {
			EvtLog.w(TAG, e.getMessage());
		}

		return result.toString();
	}

	/**
	 * 从异常信息中获取详细的堆栈信息
	 * 
	 * @param ex
	 *            输入的异常信息
	 * @return 返回详细的堆栈信息
	 */
	private static String getSimpleStackTraceInfo(Throwable ex) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		ex.printStackTrace(pw);
		pw.close();
		return sw.toString();
	}

	/**
	 * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.
	 * 
	 * @param ex
	 *            异常信息
	 * @return true:如果处理了该异常信息;否则返回false.
	 */
	private boolean handleException(Throwable ex) {
		if (ex == null) {
			return true;
		}

		// 默认异常打印
		EvtLog.e(TAG, ex);

		// 使用Toast来显示异常信息
		new Thread() {
			@Override
			public void run() {
				if (mCrashListener != null) {
					mCrashListener.onCrash();
				}
			}
		}.start();

		return saveInformation(ex);
	}

	/**
	 * 获取yyyy-MM-dd HH:mm:ss 时间格式化字符串
	 * 
	 * @return yyyy-MM-dd HH:mm:ss 时间格式化字符串
	 */
	private String getDateTime() {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss", Locale.getDefault());
		Date localDate = new Date();
		return simpleDateFormat.format(localDate);
	}
}
