package cn.easy.android.library.util;


import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Title: StringUtils</p>
 * Description: 字符串操作工具类</p>
 * @author lin.xr
 * @date 2014-6-28 上午10:27:30
 */
public class StringUtils {

	static final String TAG = "StringUtils";

	private static final String ENCRYPT_SALTE = "StringUtils";

	private static final int OffsetBig = 256;

	private static final int OffsetSmall = 16;

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
	 * @return 是否含有中文字符
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
	 * @param phoneNum
	 *            需要处理的手机号码
	 * @return String 处理后的手机号码
	 */
	public static String getProcessedMobile(String phoneNum) {
		String processedDrawMobile = "";
		if (!StringUtils.isNullOrEmpty(phoneNum)) {
			EvtLog.d(TAG, phoneNum);
			Pattern p1 = Pattern.compile("^((\\+{0,1}(0)*86){0,1})1[0-9]{10}");
			Matcher m1 = p1.matcher(phoneNum);
			if (m1.matches()) {
				Pattern p2 = Pattern.compile("^((\\+{0,1}(0)*86){0,1})");
				Matcher m2 = p2.matcher(phoneNum);
				StringBuffer sb = new StringBuffer();
				while (m2.find()) {
					m2.appendReplacement(sb, "");
				}
				m2.appendTail(sb);
				processedDrawMobile = sb.toString();

			} else {
				processedDrawMobile = phoneNum;
			}
		}
		return processedDrawMobile;
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

	/**
	 * 获取是否为email格式
	 * 
	 * @param strEmail
	 *            校验邮件地址字符串
	 * @return 是否为email格式
	 */
	public static boolean isEmail(String strEmail) {
		if (isNullOrEmpty(strEmail))
			return false;
		String strPattern = "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}\\@[a-zA-Z0-9]"
				+ "[a-zA-Z0-9\\-]{0,64}(\\.[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25})+";
		Pattern p = Pattern.compile(strPattern);
		Matcher m = p.matcher(strEmail);
		return m.matches();
	}

	/**
	 * 获取是否为中国移动号码
	 * 
	 * @param phoneNumber
	 *            校验手机号码
	 * @return 是否为中国移动号码
	 */
	public static boolean isChinaMobileNumber(String phoneNumber) {
		if (isNullOrEmpty(phoneNumber))
			return false;
		return phoneNumber
				.matches("^((86)?13)[4-9][0-9]{8}$|^((86)?15)[012789][0-9]{8}$|^((86)?18)"
						+ "[2378][0-9]{8}$|^((86)?14)[7][0-9]{8}$");
	}

	/**
	 * URL检查<br>
	 * <br>
	 * 
	 * @param pInput
	 *            要检查的字符串<br>
	 * @return boolean 返回检查结果<br>
	 */
	public static boolean checkUrl(String pInput) {
		if (isNullOrEmpty(pInput)) {
			return false;
		}
		return pInput.matches("[a-zA-z]+://[^\\s]*");
	}

	/**
	 * 获得一个UUID
	 * 
	 * @return String UUID
	 */
	public static String getUUID() {
		String uuid = UUID.randomUUID().toString();
		return uuid;
	}

	/**
	 * 获取字符串转换整型值
	 * 
	 * @param numStr
	 * @return 字符串转换整型值
	 */
	public static int getIntValue(String numStr) {
		try {
			return Integer.valueOf(numStr).intValue();
		} catch (Exception e) {
			EvtLog.w(TAG, e);
			return 0;
		}
	}

	/**
	 * 获取字符串转换长整型值
	 * 
	 * @param numStr
	 * @return 字符串转换长整型值
	 */
	public static long getLongValue(String numStr) {
		try {
			return Long.valueOf(numStr).longValue();
		} catch (Exception e) {
			return 0;
		}
	}

	/**
	 * 获取邮箱地址前缀
	 * 
	 * @param strEmail
	 * @return 邮箱地址前缀
	 */
	public static String getEmailPrefix(String strEmail) {
		if (!isEmail(strEmail))
			return "";

		int index = strEmail.indexOf("@");
		if (index == -1) {
			return strEmail;
		}
		return strEmail.substring(0, index);
	}
}
