package cn.easy.android.library.ui.listeners;

import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import cn.easy.android.library.ui.adapter.ISlowAdapter;

/**
 * Title: OnMultiScrollListener</p> 
 * Description: 列表缓加载滚动监听类</p>
 * 
 * @author lin.xr
 * @date 2014-7-1 下午4:12:12
 */
public class OnSlowAdapterScrollListener implements
		AbsListView.OnScrollListener {
	public static int BIND_DATA_TAG;

	static final String TAG = "OnSlowAdapterScrollListener";

	private ISlowAdapter mAdapter;
	
	private int mScrollState;
	private boolean mStrictMode = false;

	public OnSlowAdapterScrollListener(ISlowAdapter slowAdapter, int bindDataKey) {
		mAdapter = slowAdapter;
		BIND_DATA_TAG = bindDataKey;
	}

	public OnSlowAdapterScrollListener(ISlowAdapter slowAdapter,
			boolean strictMode, int bindDataKey) {
		this(slowAdapter, bindDataKey);
		mStrictMode = strictMode;
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		mScrollState = scrollState;
		switch (scrollState) {
		case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
		default:
			if (!mStrictMode) {
				break;
			}
			mAdapter.setListBusy(true);
			break;
		case OnScrollListener.SCROLL_STATE_IDLE:
			view.postDelayed(new BindRunnable(view), 500L);
			break;
		case OnScrollListener.SCROLL_STATE_FLING:
			mAdapter.setListBusy(true);
		}
	}

	private class BindRunnable implements Runnable {
		private AbsListView mListView;

		public BindRunnable(AbsListView view) {
			mListView = view;
		}

		public void run() {
			int childCount = 0;
			if (mScrollState == OnScrollListener.SCROLL_STATE_IDLE) {
				mAdapter.setListBusy(false);
				childCount = mListView.getChildCount();
			}
			for (int i = 0; i < childCount; i++) {
				View itemView = mListView.getChildAt(i);
				Object itemObject = itemView
						.getTag(OnSlowAdapterScrollListener.BIND_DATA_TAG);
				if (itemObject == null)
					continue;
				mAdapter.bindScrollIdleView(itemView, itemObject);
			}
		}
	}
}