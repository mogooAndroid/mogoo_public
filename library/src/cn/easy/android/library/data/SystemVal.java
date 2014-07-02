package cn.easy.android.library.data;import android.content.Context;import cn.easy.android.library.util.PackageUtils;/** * Title: SystemVal</p> * Description: 应用程序常量基类</p> * @author lin.xr * @date 2014-6-27 下午11:32:37 */public class SystemVal {	/**	 * Title: Mode</p>	 * Description: api接口模式枚举类</p>	 * @author lin.xr	 * @date 2014-6-28 下午10:09:03	 */	public enum Mode {		DEBUG, TEST, RELEASE	}		/** 是否开发模式 */	public static boolean IS_DEVELOPING = false;	/** 是否打印debug级别日志 */	public static boolean IS_DEBUG_LOGGABLE = false;	/** 是否打印error级别日志 */	public static boolean IS_ERROR_LOGGABLE = true;	/** api接口模式 */	public static Mode USE_SERVER_MODE = Mode.RELEASE;		/** meta key: 是否开发模式 */	public static final String FIELD_META_IS_DEVELOPING = "is_developing";	/** meta key: 是否打印debug级别日志 */	public static final String FIELD_META_IS_DEBUG_LOGGABLE = "debug_log_enable";	/** meta key: 是否打印error级别日志 */	public static final String FIELD_META_IS_ERROR_LOGGABLE = "error_log_enable";	/** meta key: api接口模式 */	public static final String FIELD_META_USE_SERVER_MODE = "use_server_mode";	/** meta key: test api 接口前缀 */	public static final String FIELD_META_TEST_ROOT_URL = "test_root_url";	/** meta key: debug api 接口前缀 */	public static final String FIELD_META_DEBUG_ROOT_URL = "debug_root_url";	/** meta key: release api 接口前缀 */	public static final String FIELD_META_RELEASE_ROOT_URL = "release_root_url";		static final String TAG = "SystemVal";		public static void init(Context context) {		IS_DEVELOPING = PackageUtils.getConfigBoolean(context,				FIELD_META_IS_DEVELOPING);		IS_DEBUG_LOGGABLE = IS_DEVELOPING || PackageUtils.getConfigBoolean(context,				FIELD_META_IS_DEBUG_LOGGABLE);		IS_ERROR_LOGGABLE = IS_DEVELOPING || PackageUtils.getConfigBoolean(context,				FIELD_META_IS_ERROR_LOGGABLE);		USE_SERVER_MODE = Enum.valueOf(Mode.class, PackageUtils				.getConfigString(context, FIELD_META_USE_SERVER_MODE));	}}