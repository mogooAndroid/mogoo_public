package cn.easy.android.library.ui.listeners;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;

/**
 * Title: OnDelegateScrollListener</p>
 * Description: 列表滚动监听委派类</p>
 * @author lin.xr
 * @date 2014-7-1 下午4:12:12
 */
public class OnDelegateScrollListener implements AbsListView.OnScrollListener {

	static final String TAG = "OnDelegateScrollListener";
	
	private Map<String, OnScrollListener> mOnScrollListenerMap = new HashMap<String, AbsListView.OnScrollListener>();

	/**
	 * 添加滚动监听回调
	 * @param key 滚动监听回调key
	 * @param listener 滚动监听回调
	 */
	public synchronized void addOnScrollListener(String key,
			OnScrollListener listener) {
		mOnScrollListenerMap.put(key, listener);
	}

	/**
	 * 移除滚动监听回调
	 * @param key 滚动监听回调key
	 */
	public synchronized void removeScrollListener(String key) {
		mOnScrollListenerMap.remove(key);
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		Iterator<Entry<String, OnScrollListener>> iterator = mOnScrollListenerMap
				.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<String, OnScrollListener> entry = iterator.next();
			OnScrollListener listener = entry.getValue();
			if (listener != null) {
				listener.onScroll(view, firstVisibleItem, visibleItemCount,
						totalItemCount);
			}
		}
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		Iterator<Entry<String, OnScrollListener>> iterator = mOnScrollListenerMap
				.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<String, OnScrollListener> entry = iterator.next();
			OnScrollListener listener = entry.getValue();
			if (listener != null) {
				listener.onScrollStateChanged(view, scrollState);
			}
		}
	}
}
