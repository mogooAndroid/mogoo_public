package cn.easy.android.library.util;

import android.os.Message;

/**
 * Title: HandlerUtils</p>
 * Description: handler工具类</p>
 * @author lin.xr
 * @date 2014-6-27 下午11:35:33
 */
public class HandlerUtils {
	static final String TAG = "HandlerUtils";

	/**
	 * 返回一个消息
	 * 
	 * @param msgWhat
	 *            类型
	 * @param obj
	 *            内容
	 * @return 一个消息
	 */
	public static Message obtainMessage(int msgWhat, Object obj) {
		Message msg = new Message();
		msg.what = msgWhat;
		msg.obj = obj;
		return msg;
	}
}
