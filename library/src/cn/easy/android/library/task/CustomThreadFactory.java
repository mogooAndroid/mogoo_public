package cn.easy.android.library.task;

import java.util.concurrent.ThreadFactory;

/**
 * Title: CustomThreadFactory</p>
 * Description: 线程(带线程名)创建工厂类</p>
 * @author lin.xr
 * @date 2014-6-28 下午8:10:33
 */
public class CustomThreadFactory implements ThreadFactory {

	static final String TAG = "CustomThreadFactory";

	private final String mThreadName;

	public CustomThreadFactory(String threadName) {
		mThreadName = threadName;
	}

	public CustomThreadFactory() {
		this(null);
	}

	public Thread newThread(final Runnable r) {
		if (null != mThreadName) {
			return new Thread(r, mThreadName);
		} else {
			return new Thread(r);
		}
	}
}