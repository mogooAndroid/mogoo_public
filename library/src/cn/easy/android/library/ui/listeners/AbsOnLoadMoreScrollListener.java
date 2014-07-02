package cn.easy.android.library.ui.listeners;

import android.widget.AbsListView;
import android.widget.Adapter;
import android.widget.ListView;
import cn.easy.android.library.ui.widget.AbsLoadingFooter;

/**
 * Title: AbsOnLoadMoreScrollListener</p>
 * Description: 列表加载更多滚动监听抽象类</p>
 * @author lin.xr
 * @date 2014-7-1 下午4:12:12
 */
public abstract class AbsOnLoadMoreScrollListener implements
		AbsListView.OnScrollListener {

	static final String TAG = "OnLoadMoreScrollListener";

	protected AbsLoadingFooter mLoadingFooter;

	protected Adapter mAdapter;

	protected ListView mListView;

	public AbsOnLoadMoreScrollListener(ListView listview,
			AbsLoadingFooter loadingFooter, Adapter adapter) {
		mListView = listview;
		mLoadingFooter = loadingFooter;
		mAdapter = adapter;
	}

	public abstract void onLoadNextPage();

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		if (mLoadingFooter.getState() == AbsLoadingFooter.State.LOADING
				|| mLoadingFooter.getState() == AbsLoadingFooter.State.END) {
			return;
		}
		if (firstVisibleItem + visibleItemCount >= totalItemCount
				&& totalItemCount != 0
				&& totalItemCount != mListView.getHeaderViewsCount()
						+ mListView.getFooterViewsCount()
				&& mAdapter.getCount() > 0) {
			onLoadNextPage();
		}
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {

	}
}
