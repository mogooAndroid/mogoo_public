package com.michelin.droid.util;


import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * @author
 * 
 */
public class StringUtil {
	private static final int OffsetBig = 256;
	private static final int OffsetSmall = 16;

	private static final int MOBIL_F_TAG = 3;
	private static final int MOBIL_L_TAG = 7;
	private static final int MOBIL_P_TAG_PREFIX = 6;
	private static final int MOBIL_N_TAG_PREFIX = 10;

	private static final String ENCRYPT_SALTE = "paidui888";
	private static final String TAG = "StringUtil";

	/**
	 * 字符串去空格，回车，换行，制表符
	 * 
	 * @param str
	 *            要修改的字符串
	 * @return 修改完成的字符串
	 */
	public static String replaceBlank(String str) {
		String dest = "";
		if (str != null) {
			Pattern p = Pattern.compile("\t|\r|\n");
			Matcher m = p.matcher(str);
			dest = m.replaceAll("");
		}
		return dest;
	}

	/**
	 * 将输入的字符串进行html编码
	 * 
	 * @param input
	 *            输入的字符串
	 * @return html编码后的结果
	 */
	public static String htmEncode(String input) {
		if (null == input || "".equals(input)) {
			return input;
		}

		StringBuffer stringbuffer = new StringBuffer();
		int j = input.length();
		for (int i = 0; i < j; i++) {
			char c = input.charAt(i);
			switch (c) {
				case 60:
					stringbuffer.append("&lt;");
					break;
				case 62:
					stringbuffer.append("&gt;");
					break;
				case 38:
					stringbuffer.append("&amp;");
					break;
				case 34:
					stringbuffer.append("&quot;");
					break;
				case 169:
					stringbuffer.append("&copy;");
					break;
				case 174:
					stringbuffer.append("&reg;");
					break;
				case 165:
					stringbuffer.append("&yen;");
					break;
				case 8364:
					stringbuffer.append("&euro;");
					break;
				case 8482:
					stringbuffer.append("&#153;");
					break;
				case 13:
					if (i < j - 1 && input.charAt(i + 1) == 10) {
						stringbuffer.append("<br>");
						i++;
					}
					break;
				case 32:
					if (i < j - 1 && input.charAt(i + 1) == ' ') {
						stringbuffer.append(" &nbsp;");
						i++;
						break;
					}
				default:
					stringbuffer.append(c);
					break;
			}
		}
		return new String(stringbuffer.toString());
	}

	/**
	 * 判断字符串是否为null或者空字符串
	 * 
	 * @param input
	 *            输入的字符串
	 * @return 如果为null或者空字符串，返回true；否则返回false
	 */
	public static boolean isNullOrEmpty(String input) {
		if (null == input || "".equals(input)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 判断字符串中是否含有中文字符
	 * 
	 * @param s
	 * @return
	 */
	public static boolean containChinese(String s) {
		if (null == s) {
			return false;
		}

		Pattern pattern = Pattern.compile(".*[\u4e00-\u9fbb]+.*");
		Matcher matcher = pattern.matcher(s);
		return matcher.matches();
	}

	/**
	 * 获取MD5加密后Hash字符串
	 * 
	 * @param strOriginal
	 *            初始字符串
	 * 
	 * @return MD5加密后Hash字符串
	 */
	public static String getMd5Hash(String strOriginal) {
		StringBuilder sbList = new StringBuilder();
		try {
			MessageDigest mMD5 = MessageDigest.getInstance("MD5");
			byte[] data = strOriginal.getBytes("utf-8");
			byte[] dataPWD = mMD5.digest(data);
			for (int offset = 0; offset < dataPWD.length; offset++) {
				int i = dataPWD[offset];
				if (i < 0) {
					i += OffsetBig;
				}
				if (i < OffsetSmall) {
					sbList.append("0");
				}
				sbList.append(Integer.toHexString(i));
			}
			return sbList.toString();
		} catch (NoSuchAlgorithmException e) {
			EvtLog.w(TAG, e);
		} catch (UnsupportedEncodingException e) {
			EvtLog.w(TAG, e);
		}
		return null;
	}

	/**
	 * 获取MD5加密后Hash字符串
	 * 
	 * 
	 * @param strOriginal
	 *            初始字符串
	 * 
	 * @param strSalt
	 *            种子字符串
	 * 
	 * @return MD5加密后Hash字符串
	 */
	public static String getMd5Hash(String strOriginal, String strSalt) {
		String mStrSalt;
		String mStrOriginal = strOriginal;
		// 如果调用未给Salt值,则默认
		if (strSalt == null) {
			mStrSalt = ENCRYPT_SALTE;
		} else {
			mStrSalt = strSalt;
		}
		mStrOriginal = mStrOriginal + mStrSalt;
		return getMd5Hash(mStrOriginal);
	}

	/**
	 * @Method: getProcessedDrawMobile
	 * @Description: 处理手机号码
	 * @param userMobile
	 *            需要处理的手机号码
	 * @return String 处理后的手机号码
	 * @throws
	 */
	public static String getProcessedMobile(String userMobile) {
		String mProcessedDrawMobile = "";
		if (!StringUtil.isNullOrEmpty(userMobile)) {
			EvtLog.d(TAG, userMobile);
			// 判断是否是+86开头的手机号码
			if ('+' == userMobile.charAt(0)) {
				mProcessedDrawMobile = userMobile.substring(0, MOBIL_P_TAG_PREFIX) + "****"
						+ userMobile.substring(MOBIL_N_TAG_PREFIX);
			} else {
				mProcessedDrawMobile = userMobile.substring(0, MOBIL_F_TAG) + "****"
						+ userMobile.substring(MOBIL_L_TAG);
			}
		}
		return mProcessedDrawMobile;
	}

	/**
	 * 
	 * 获取当期是星期几（从星期天开始）
	 * 
	 * @param weeknum
	 *            当前是第几天（0-6）
	 * @return 星期*
	 */
	public static String getDayOfWeek(int weeknum) {
		weeknum--;
		if (weeknum > 7) {
			weeknum = weeknum % 7;
		}

		if (weeknum < 0) {
			weeknum = -weeknum;
		}

		String[] weekArray = new String[] { "星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"
		};
		return weekArray[weeknum];

	}

	/**
	 * 重载方法，获取当期是星期几
	 * 
	 * @param c
	 *            日历
	 * @return 星期*
	 */
	public static String getDayOfWeek(Calendar c) {
		return getDayOfWeek(c.get(Calendar.DAY_OF_WEEK));
	}

	/**
	 * 检查是不是中文
	 * 
	 * @Method: checkStringIsChinses
	 * @param str
	 *            检查字符串
	 * @return boolean 是否为中文
	 * @throws
	 */
	public static boolean checkStringIsChinses(String str) {
		for (int i = 0; i < str.length(); i++) {
			String test = str.substring(i, i + 1);
			if (!test.matches("[\\u4E00-\\u9FA5]+")) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 转换保留小数位 字符串
	 * 
	 * @param i
	 *            小数位数
	 * @param numStr
	 *            数字字符串
	 * @return String
	 */
	public static String getDecimalFormat(int i, String numStr) {
		try {
			if (numStr != null && !"".equals(numStr)) {
				BigDecimal bd = new BigDecimal(numStr);
				bd = bd.setScale(i, BigDecimal.ROUND_HALF_UP);

				return bd.toString();
			} else {
				return "";
			}
		} catch (Exception e) {
			return "";
		}
	}

}
