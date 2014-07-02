package cn.easy.android.library.ui.listeners;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;

/**
 * Title: OnMultiScrollListener</p>
 * Description: 列表多滚动监听类</p>
 * @author lin.xr
 * @date 2014-7-1 下午4:12:12
 */
public class OnMultiScrollListener implements AbsListView.OnScrollListener {

	static final String TAG = "OnMultiScrollListener";
	
	private Map<String, OnScrollListener> mOnScrollListenerMap = new HashMap<String, AbsListView.OnScrollListener>();

	public synchronized void addOnScrollListener(String key,
			OnScrollListener listener) {
		mOnScrollListenerMap.put(key, listener);
	}

	public synchronized void removeScrollListener(String key,
			OnScrollListener listener) {
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
