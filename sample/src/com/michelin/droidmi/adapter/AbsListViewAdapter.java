package com.michelin.droidmi.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.michelin.droid.data.ConstantSet;
import com.michelin.droid.util.StringUtil;
import com.michelin.droid.util.TelephoneUtil;
import com.michelin.droid.widget.IPageOperator;
import com.michelin.droidmi.R;
import com.michelin.droidmi.widget.ListViewFooter;
import com.michelin.droidmi.widget.LoadingView;
import com.michelin.droidmi.widget.PullToRefreshListView;
import com.michelin.droidmi.widget.PullToRefreshListView.PullToRefreshListViewStateListener;
import com.tsz.afinal.FinalBitmap;

public abstract class AbsListViewAdapter extends BaseAdapter implements
		AbsListView.OnScrollListener, IPageOperator {

	public static final int STATUS_READY = -2;
	public static final int STATUS_PENDDING = -1;
	public static final int STATUS_LOADING = 0;
	public static final int STATUS_LIST_ISNULL = 1;
	public static final int STATUS_ERROR = 2;
	public static final int STATUS_PULL_LOADING = 3;

	protected Context mContext;
	protected LayoutInflater mInflater;
	protected ListView mListView;
	protected ListViewFooter mFootView;
	protected LoadingView mLoadView;

	protected List mBeanList = new ArrayList();

	protected boolean mIsFilter = false;
	protected boolean mIsLastPage = false;
	protected boolean mIsLocalMode = false;
	protected boolean mAddFooter = false;
	protected boolean mIndexMode;
	protected boolean mOnScrolleIdle = true;

	protected int mCurrentPage = 0;
	protected int mIndex = 0;
	protected int mCurrentStatus = STATUS_READY;

	protected String mNoDataTip;
	protected String mUrl;
	protected String tag = "AbsListViewAdapter";

	private View.OnClickListener mErrorClickLisenter = new View.OnClickListener() {
		public void onClick(View view) {
			request();
		}
	};

	Handler mOnScrollChangedHandle = new Handler() {
		public void handleMessage(Message msg) {
			if (mOnScrolleIdle)
				notifyDataSetChanged();
		}
	};

	public AbsListViewAdapter(Context context) {
		mContext = context;
		mInflater = ((LayoutInflater) mContext
				.getSystemService("layout_inflater"));
		mLoadView = new LoadingView(mInflater);
	}

	public AbsListViewAdapter(Context context, ListView listView, String url) {
		this(context);
		if (listView != null) {
			mUrl = url;
			initListView(listView);
			return;
		}
		throw new NullPointerException("listview is null");
	}

	@Override
	public int getPageSize() {
		return ConstantSet.PAGE_SIZE;
	}

	@Override
	public int getTotalCount() {
		return getCount();
	}

	@Override
	public int getNextPage() {
		if (mCurrentStatus == STATUS_PULL_LOADING) {
			return 1;
		}
		int tmp = (getTotalCount() % getPageSize() != 0) ? getTotalCount()
				/ getPageSize() + 2 : getTotalCount() / getPageSize() + 1;
		return tmp;
	}

	@Override
	public int getCurrentPage() {
		int tmp = (getTotalCount() % getPageSize() != 0) ? getTotalCount()
				/ getPageSize() + 1 : getTotalCount() / getPageSize();
		return tmp;
	}

	@Override
	public boolean hasNextPage() {
		return getTotalCount() % getPageSize() == 0;
	}

	protected void addFooterView() {
		if (!mAddFooter) {
			if (mFootView == null)
				mFootView = new ListViewFooter(mInflater);
			mFootView.hideFooterView();
			mListView.addFooterView(mFootView.getFooterView());
			mAddFooter = true;
		}
	}

	public void appendData(List datalist, boolean isLastPage) {
		if (datalist == null) {
			changeRequestStatus(STATUS_ERROR);
		} else {
			// mIsLastPage = isLastPage;
			if (!datalist.isEmpty()) {
				mBeanList.addAll(datalist);
				notifyDataSetChanged();
			}
			// if (mIsLastPage)
			// removeFootView();
			if (mBeanList.isEmpty())
				onDataEmpty();
			boolean flag1;
			if (mBeanList.size() == 0)
				flag1 = true;
			else
				flag1 = false;
			if (flag1)
				changeRequestStatus(STATUS_LIST_ISNULL);
			else
				changeRequestStatus(STATUS_PENDDING);
		}
	}

	public void appendData(List datalist, boolean isLastPage, int pageIndex) {
		if (STATUS_PULL_LOADING != mCurrentStatus) {
			if (((mIndex == 0) || (1 == mCurrentPage))
					&& ((mListView instanceof PullToRefreshListView)))
				// ((PullToRefreshListView) mListView).setRefreshTime();
				((PullToRefreshListView) mListView).onRefreshComplete();
		} else {
			((PullToRefreshListView) mListView).onRefreshComplete();
			reset();
			mCurrentPage = 1;
		}
		// mIndex = pageIndex;
		appendData(datalist, isLastPage);
	}

	public void appendDataByManual(List datalist, boolean isLastPage,
			int pageIndex) {
		mCurrentPage = (1 + mCurrentPage);
		mIndex = pageIndex;
		appendData(datalist, isLastPage);
	}

	protected void changeRequestStatus(int requestStatus) {
		mCurrentStatus = requestStatus;
		switch (mCurrentStatus) {
		case STATUS_LOADING:
			addFooterView();
			if (!mBeanList.isEmpty())
				mFootView.showFooterViewWaiting();
			else
				mLoadView.showViewLoading();
			break;
		case STATUS_LIST_ISNULL:
			addFooterView();
			// mCurrentPage -= 1;
			mIsLastPage = !hasNextPage();
			mIndex = getCurrentPage();
			mCurrentPage = getCurrentPage();
			if (mNoDataTip == null)
				mNoDataTip = mContext.getString(R.string.loading_list_null);
			if (!mBeanList.isEmpty())
				mFootView.showFooterViewError(mNoDataTip, mErrorClickLisenter);
			else
				mLoadView.showNotice(mNoDataTip, mErrorClickLisenter);
			reset();
			break;
		case STATUS_ERROR:
			addFooterView();
			// mCurrentPage -= 1;
			mIsLastPage = !hasNextPage();
			mIndex = getCurrentPage();
			mCurrentPage = getCurrentPage();
			if (!mBeanList.isEmpty())
				mFootView.showFooterViewError(mErrorClickLisenter);
			else
				mLoadView.showLoadFailed(mErrorClickLisenter);
			break;
		default:
			mIsLastPage = !hasNextPage();
			mFootView.hideFooterView();
			break;
		}
		if (mIsLastPage)
			removeFootView();
	}

	protected void doRefreshRequest() {
		if (!mIsLocalMode) {
			if (!StringUtil.isNullOrEmpty(mUrl)) {
				if (!mIndexMode) {
					// TODO 加载非本地分页数据
					// mCurrentPage = (1 + mCurrentPage);
					mCurrentPage = 1;
				} else {
					// TODO 加载非本地数据
				}
				doRequest(mUrl);
			} else {
				changeRequestStatus(STATUS_PENDDING);
			}
		} else
			doRequest("");
	}

	protected void doRequest() {
		if (!mIsLocalMode) {
			if (!StringUtil.isNullOrEmpty(mUrl)) {
				if (!mIndexMode) {
					// TODO 加载非本地分页数据
					// mCurrentPage = (1 + mCurrentPage);
					mCurrentPage = getNextPage();
				} else {
					// TODO 加载本地数据
				}
				doRequest(mUrl);
			} else {
				changeRequestStatus(STATUS_ERROR);
			}
		} else
			doRequest("");
	}

	protected void doRequest(String url) {
		// if(StringUtil.isNullOrEmpty(mUrl)) {
		// return;
		// }
		// Droidmi droidmi = (Droidmi) mContext.getApplicationContext();
		// Droid droid = droidmi.getDroid();
		// droid.findLazyListData(mCurrentPage, getPageSize(), responseHandler);
	}

	public int getCount() {
		return mBeanList.size();
	}

	public Object getItem(int position) {
		return mBeanList.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public LoadingView getLoadView() {
		return mLoadView;
	}

	public int getRequestState() {
		return mCurrentStatus;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View localView = convertView;
		Object localObject;
		if (localView != null) {
			localObject = localView.getTag();
		} else {
			localView = createItem();
			localObject = initHolder(localView);
			localView.setTag(localObject);
		}
		setViewContent(localObject, getItem(position), position);
		if (!mOnScrolleIdle)
			setOnScrollViewContent(localObject, getItem(position), position);
		else
			setOnScrollIdleView(localObject, getItem(position), position);
		return localView;
	}

	public void initListView(ListView listView) {
		if (mListView == null) {
			mListView = listView;
			addFooterView();
			if (!(mListView instanceof PullToRefreshListView)) {
				mListView.setAdapter(this);
			} else {
				((PullToRefreshListView) mListView).setAdapter(this);
				((PullToRefreshListView) mListView)
						.setStateListener(new PullToRefreshListView.PullToRefreshListViewStateListener() {

							@Override
							public void onStateChanged(int state) {
								onListViewStateChanged(state);
							}
						});
			}
			mLoadView.setEmptyView(listView);
			mListView.setOnScrollListener(this);
			mListView
					.setOnItemClickListener(new AdapterView.OnItemClickListener() {
						public void onItemClick(AdapterView<?> parent,
								View view, int position, long id) {
							int i = position;
							if ((mListView.getHeaderViewsCount() > 0)
									|| (mListView.getFooterViewsCount() > 0))
								i = position - mListView.getHeaderViewsCount();
							if ((i >= 0) && (i < mBeanList.size()))
								onDataItemClick(view, i);
						}
					});
		}
	}

	public void myNotifyDataChanged() {
		mOnScrollChangedHandle.sendEmptyMessage(0);
	}

	public void notifyDataSetChanged() {
		if ((mCurrentStatus == STATUS_PENDDING) && (mBeanList.isEmpty()))
			changeRequestStatus(STATUS_LIST_ISNULL);
		super.notifyDataSetChanged();
	}

	public void notifyRequestError() {
		if (STATUS_PULL_LOADING != mCurrentStatus) {
			changeRequestStatus(STATUS_ERROR);
		} else {
			changeRequestStatus(STATUS_PENDDING);
			((PullToRefreshListView) mListView).setRecover();
		}
	}

	protected void onDataEmpty() {
	}

	protected void onDataItemClick(View view, int position) {
	}

	protected void onListViewStateChanged(int paramInt) {
		switch (paramInt) {
		case PullToRefreshListViewStateListener.STATE_PULL:
			break;
		case PullToRefreshListViewStateListener.STATE_LOADING:
			if ((mCurrentStatus == STATUS_LOADING)
					|| (STATUS_PULL_LOADING == mCurrentStatus))
				break;
			changeRequestStatus(STATUS_PULL_LOADING);
			doRefreshRequest();
			break;
		case PullToRefreshListViewStateListener.STATE_RECOVER:
			break;
		}
	}

	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		if (mListView instanceof PullToRefreshListView) {
			((PullToRefreshListView) mListView).onScroll(view,
					firstVisibleItem, visibleItemCount, totalItemCount);
		}

		if ((mCurrentStatus != STATUS_LOADING)
				&& (STATUS_PULL_LOADING != mCurrentStatus)
				&& (mCurrentStatus != STATUS_READY) && (totalItemCount != 0)
				&& (!mIsLocalMode))
			if (TelephoneUtil.isNetworkAvailable()) {
				if ((firstVisibleItem + visibleItemCount >= totalItemCount - 2))
					request();
			} else
				mFootView.showFooterViewError(mErrorClickLisenter);
	}

	public void onScrollStateChanged(AbsListView view, int scrollState) {
		if (mListView instanceof PullToRefreshListView) {
			((PullToRefreshListView) mListView).onScrollStateChanged(view,
					scrollState);
		}
		if (!mIsLocalMode)
			if (scrollState == SCROLL_STATE_IDLE) {
				mOnScrolleIdle = true;
				mOnScrollChangedHandle.sendEmptyMessage(0);
				// 开始图片下载器
				FinalBitmap.create(mContext).pauseWork(false);
			} else {
				mOnScrolleIdle = false;
				// 暂停图片下载器
				FinalBitmap.create(mContext).pauseWork(true);
			}
	}

	public void removeFootView() {
		if ((mAddFooter) && (mFootView != null)
				&& (mFootView.getFooterView() != null)) {
			mListView.removeFooterView(mFootView.getFooterView());
			mAddFooter = false;
		}
	}

	public void request() {
		if ((!mIsLastPage) || (mIsLocalMode)) {
			changeRequestStatus(STATUS_LOADING);
			doRequest();
		}
	}

	public void request(String url) {
		if (!TextUtils.isEmpty(url)) {
			mUrl = url;
			reset();
			request();
		}
	}

	public void reset() {
		mBeanList.clear();
		mCurrentPage = 0;
		mIndex = 0;
		mIsLastPage = false;
		mCurrentStatus = STATUS_READY;
		notifyDataSetChanged();
	}

	public void setFilter(boolean isFilter) {
		mIsFilter = isFilter;
	}

	public void setIndexMode(boolean indexMode) {
		mIndexMode = indexMode;
	}

	public void setLocalMode(boolean isLocalMode) {
		mIsLocalMode = isLocalMode;
	}

	public void setNullTip(int noDataTipId) {
		mNoDataTip = mContext.getString(noDataTipId);
	}

	public void setNullTip(String noDataTip) {
		mNoDataTip = noDataTip;
	}

	protected void setOnScrollIdleView(Object viewHolder, Object bean,
			int position) {
	}

	protected void setOnScrollViewContent(Object viewHolder, Object bean,
			int position) {
	}

	protected abstract View createItem();

	protected abstract void setViewContent(Object viewHolder, Object bean,
			int position);

	protected abstract Object initHolder(View convertView);
}