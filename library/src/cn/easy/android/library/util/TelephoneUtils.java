package cn.easy.android.library.util;

import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.app.KeyguardManager;
import android.content.Context;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.PowerManager;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;

/**
 * Title: TelephoneUtils</p>
 * Description: 设备操作工具类</p>
 * @author lin.xr
 * @date 2014-6-28 下午7:41:35
 */
public class TelephoneUtils {
	static final String TAG = "TelephoneUtils";

	/**
	 * 获取当前网络类型是否为WIFI
	 * 
	 * @param context
	 *            上下文
	 * @return 当前网络类型是否为WIFI
	 */
	public static boolean isWifiEnable(Context context) {
		ConnectivityManager conManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (conManager != null && conManager.getActiveNetworkInfo() != null
				&& conManager.getActiveNetworkInfo().isAvailable()) {
			if (conManager.getActiveNetworkInfo().getType() == ConnectivityManager.TYPE_WIFI) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 获取当前网络状态是否可用
	 * 
	 * @param context
	 *            上下文
	 * @return 当前网络状态是否可用
	 */
	public static boolean isNetworkAvailable(Context context) {

		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (cm == null) {
			return false;
		}
		NetworkInfo[] info = null;
		try {
			info = cm.getAllNetworkInfo();
		} catch (Exception e) {
			EvtLog.w(TAG, e);
		}
		if (info != null) {
			for (int i = 0; i < info.length; i++) {
				if (info[i].getState() == NetworkInfo.State.CONNECTED) {
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * 获取当前GPS是否可用
	 * 
	 * @param context
	 *            上下文
	 * @return 当前GPS是否可用
	 */
	public static boolean isGPSAvailable(Context context) {
		boolean result;
		LocationManager locationManager = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);
		if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			result = true;
		} else {
			result = false;
		}

		return result;
	}

	/**
	 * 判断是否锁屏状态
	 * 
	 * @param context
	 *            上下文
	 * @return true 锁屏 flase 非锁屏
	 */
	public static boolean isScreenLocked(Context context) {
		KeyguardManager mKeyguardManager = (KeyguardManager) context
				.getSystemService(Context.KEYGUARD_SERVICE);
		PowerManager pm = (PowerManager) context
				.getSystemService(Context.POWER_SERVICE);
		if (pm.isScreenOn() == false) {
			return true;
		} else {
			return mKeyguardManager.inKeyguardRestrictedInputMode();
		}
	}

	/**
	 * 获取设备唯一序列号 MD5(IMEI + DEVICE ID + ANDROID ID + WIFI MAC)
	 * 
	 * @param context
	 * @return 设备唯一序列号
	 */
	public static String getDeviceId(Context context) {
		// 1 compute IMEI
		TelephonyManager TelephonyMgr = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		String m_szImei = TelephonyMgr.getDeviceId(); // Requires
														// READ_PHONE_STATE

		// 2 compute DEVICE ID
		String m_szDevIDShort = "35"
				+ // we make this look like a valid IMEI
				Build.BOARD.length() % 10 + Build.BRAND.length() % 10
				+ Build.CPU_ABI.length() % 10 + Build.DEVICE.length() % 10
				+ Build.DISPLAY.length() % 10 + Build.HOST.length() % 10
				+ Build.ID.length() % 10 + Build.MANUFACTURER.length() % 10
				+ Build.MODEL.length() % 10 + Build.PRODUCT.length() % 10
				+ Build.TAGS.length() % 10 + Build.TYPE.length() % 10
				+ Build.USER.length() % 10; // 13 digits
		// 3 android ID - unreliable
		String m_szAndroidID = Secure.getString(context.getContentResolver(),
				Secure.ANDROID_ID);

		// 4 wifi manager, read MAC address - requires
		// android.permission.ACCESS_WIFI_STATE or comes as null
		WifiManager wm = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		String m_szWLANMAC = wm.getConnectionInfo().getMacAddress();

		/*
		 * // 5 Bluetooth MAC address android.permission.BLUETOOTH required
		 * BluetoothAdapter m_BluetoothAdapter = null; // Local Bluetooth
		 * adapter m_BluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		 * String m_szBTMAC = m_BluetoothAdapter.getAddress();
		 */

		// 6 SUM THE IDs
		String m_szLongID = m_szImei + m_szDevIDShort + m_szAndroidID
				+ m_szWLANMAC;
		MessageDigest m = null;
		try {
			m = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		m.update(m_szLongID.getBytes(), 0, m_szLongID.length());
		byte p_md5Data[] = m.digest();

		String m_szUniqueID = new String();
		for (int i = 0; i < p_md5Data.length; i++) {
			int b = (0xFF & p_md5Data[i]);
			// if it is a single digit, make sure it have 0 in front (proper
			// padding)
			if (b <= 0xF)
				m_szUniqueID += "0";
			// add number to string
			m_szUniqueID += Integer.toHexString(b);
		}
		m_szUniqueID = m_szUniqueID.toUpperCase();

		return m_szUniqueID;
	}

	/**
	 * 获取手机sim是否可用
	 * 
	 * @return 手机sim是否可用
	 */
	public static boolean isSimAvailable(Context context) {
		TelephonyManager TelephonyMgr = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		if (TelephonyMgr.getSimState() == TelephonyManager.SIM_STATE_READY) {
			return true;
		}
		return false;
	}

	/**
	 * 获取是否包含smartBar
	 * 
	 * @return 是否包含smartBar
	 */
	public static boolean hasSmartBar() {
		try {
			// 新型号可用反射调用Build.hasSmartBar()
			Method method = Class.forName("android.os.Build").getMethod(
					"hasSmartBar");
			return ((Boolean) method.invoke(null)).booleanValue();
		} catch (Exception e) {
		}

		// 反射不到Build.hasSmartBar()，则用Build.DEVICE判断
		if (Build.DEVICE.equals("mx2")) {
			return true;
		} else if (Build.DEVICE.equals("mx") || Build.DEVICE.equals("m9")) {
			return false;
		}

		return false;
	}
}
