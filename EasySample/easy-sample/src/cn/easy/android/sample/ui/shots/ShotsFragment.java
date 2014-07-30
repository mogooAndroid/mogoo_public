package cn.easy.android.sample.ui.shots;

import org.apache.http.Header;

import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;
import uk.co.senab.actionbarpulltorefresh.library.viewdelegates.AbsListViewDelegate;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ListView;
import cn.easy.android.library.ui.listeners.AbsOnLoadMoreScrollListener;
import cn.easy.android.library.ui.listeners.OnDelegateScrollListener;
import cn.easy.android.library.ui.listeners.OnSlowAdapterScrollListener;
import cn.easy.android.library.ui.widget.AbsLoadingFooter;
import cn.easy.android.library.util.HandlerUtils;
import cn.easy.android.library.util.UiHelper;
import cn.easy.android.sample.R;
import cn.easy.android.sample.app.EasySampleApplication;
import cn.easy.android.sample.data.ConstantSet;
import cn.easy.android.sample.net.communication.ShotsCommunication;
import cn.easy.android.sample.net.communication.types.ShotsResponse;
import cn.easy.android.sample.types.Category;
import cn.easy.android.sample.ui.fragment.BaseFragment;
import cn.easy.android.sample.ui.view.LoadingFooter;

import com.google.gson.Gson;
import com.loopj.android.http.BaseJsonHttpResponseHandler;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.PauseOnScrollListener;

public class ShotsFragment extends BaseFragment implements OnRefreshListener {

	public static final String EXTRA_CATEGORY = "EXTRA_CATEGORY";

	private static final int MSG_REFRESH_SUCCESS = 1;
	private static final int MSG_REFRESH_FAILURE = 2;
	private static final int MSG_LOAD_MORE_SUCCESS = 3;
	private static final int MSG_LOAD_MORE_FAILURE = 4;

	private ListView mListView;
	private ShotsAdapter mAdapter;
	private OnDelegateScrollListener mOnDelegateScrollListener;
	private OnLoadMoreScrollListener mOnLoadMoreScrollListener;
	private OnSlowAdapterScrollListener mOnSlowAdapterScrollListener;
	private LoadingFooter mLoadingFooter;

	private PullToRefreshLayout mPullToRefreshLayout;

	private Category mCategory;

	private int mPage = 1;
	private boolean mHasNextPage = true;

	public static ShotsFragment newInstance(Category category) {
		ShotsFragment fragment = new ShotsFragment();
		Bundle bundle = new Bundle();
		bundle.putString(EXTRA_CATEGORY, category.name());
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		parseArgument();
		View contentView = inflater.inflate(R.layout.fragment_listview, null);

		ImageLoader imageloader = ((EasySampleApplication) EasySampleApplication
				.getApplication(mActivity)).getImageLoader();
		mListView = (ListView) contentView.findViewById(R.id.ptr_listview);
		mLoadingFooter = new LoadingFooter(mActivity);
		mAdapter = new ShotsAdapter(mActivity, imageloader);
		mListView.addFooterView(mLoadingFooter.getView());
		mListView.setAdapter(mAdapter);

		PauseOnScrollListener pauseOnScrollListener = new PauseOnScrollListener(
				imageloader, true, true);
		mOnDelegateScrollListener = new OnDelegateScrollListener();
		mOnSlowAdapterScrollListener = new OnSlowAdapterScrollListener(
				mAdapter, R.string.app_name);
		mOnLoadMoreScrollListener = new OnLoadMoreScrollListener(mListView,
				mLoadingFooter, mAdapter);
		
		mOnDelegateScrollListener.addOnScrollListener(
				"mOnSlowAdapterScrollListener", mOnSlowAdapterScrollListener);
		mOnDelegateScrollListener.addOnScrollListener("mOnLoadMoreScrollListener",
				mOnLoadMoreScrollListener);
		mOnDelegateScrollListener.addOnScrollListener("pauseOnScrollListener",
				pauseOnScrollListener);
		
		mListView.setOnScrollListener(mOnDelegateScrollListener);

		// Now find the PullToRefreshLayout and set it up
		mPullToRefreshLayout = (PullToRefreshLayout) contentView
				.findViewById(R.id.ptr_layout);
		ActionBarPullToRefresh.from(mActivity).allChildrenArePullable()
				.listener(this)
				// Here we'll set a custom ViewDelegate
				.useViewDelegate(ListView.class, new AbsListViewDelegate())
				.setup(mPullToRefreshLayout);
		loadData(mPage);
		return contentView;
	}

	@Override
	protected void handleMessage(Message msg) {
		int what = msg.what;
		Object[] objs;
		switch (what) {
		case MSG_REFRESH_SUCCESS:
			objs = (Object[]) msg.obj;
			ShotsResponse refreshResponse = (ShotsResponse) objs[1];
			mAdapter.setShots(refreshResponse.getShots());
			mAdapter.notifyDataSetChanged();

			mHasNextPage = mPage < refreshResponse.getPages();
			mPage = mPage + 1;
			mPullToRefreshLayout.setRefreshComplete();
			break;
		case MSG_REFRESH_FAILURE:
			mPullToRefreshLayout.setRefreshComplete();
			break;
		case MSG_LOAD_MORE_SUCCESS:
			objs = (Object[]) msg.obj;
			ShotsResponse loadMoreResponse = (ShotsResponse) objs[1];
			mAdapter.addShots(loadMoreResponse.getShots());
			mAdapter.notifyDataSetChanged();

			mHasNextPage = mPage < loadMoreResponse.getPages();
			mPage = mPage + 1;
			mLoadingFooter.setState(AbsLoadingFooter.State.IDLE);
			break;
		case MSG_LOAD_MORE_FAILURE:
			mLoadingFooter.setState(AbsLoadingFooter.State.IDLE);
			break;
		}
	}

	@Override
	public void onRefreshStarted(View view) {
		mPage = 1;
		loadData(mPage);
	}

	public void loadFirstPageAndScrollToTop() {
		mPage = 1;
		loadData(mPage);
		mPullToRefreshLayout.setRefreshing(true);
		UiHelper.smoothScrollListViewToTop(mListView);
	}

	private void parseArgument() {
		Bundle bundle = getArguments();
		mCategory = Category.valueOf(bundle.getString(EXTRA_CATEGORY));
	}

	private void loadData(int page) {
		final boolean isRefreshFromTop = page == 1;
		ShotsResponseHandler responseHandler = new ShotsResponseHandler(
				isRefreshFromTop);
		ShotsCommunication communication = new ShotsCommunication(mActivity,
				responseHandler, mCategory.getDisplayName(), page,
				ConstantSet.PAGE_SIZE);
		communication.execute(true);
	}

	private class OnLoadMoreScrollListener extends AbsOnLoadMoreScrollListener {

		public OnLoadMoreScrollListener(ListView listview,
				AbsLoadingFooter loadingFooter, Adapter adapter) {
			super(listview, loadingFooter, adapter);
		}

		@Override
		public void onLoadNextPage() {
			if (!mHasNextPage) {
				return;
			}
			mLoadingFooter.setState(AbsLoadingFooter.State.LOADING);
			loadData(mPage);
		}
	}

	class ShotsResponseHandler extends
			BaseJsonHttpResponseHandler<ShotsResponse> {
		final boolean mIsRefreshFromTop;

		public ShotsResponseHandler(boolean isRefreshFromTop) {
			mIsRefreshFromTop = isRefreshFromTop;
		}

		@Override
		public void onStart() {
		}

		@Override
		public void onSuccess(int statusCode, Header[] headers,
				String rawJsonResponse, ShotsResponse response) {
			final int what = mIsRefreshFromTop ? MSG_REFRESH_SUCCESS
					: MSG_LOAD_MORE_SUCCESS;
			sendMsg(HandlerUtils.obtainMessage(what, new Object[] { statusCode,
					response }));
		}

		@Override
		public void onFailure(int statusCode, Header[] headers,
				Throwable throwable, String rawJsonData,
				ShotsResponse errorResponse) {
			final int what = mIsRefreshFromTop ? MSG_REFRESH_FAILURE
					: MSG_LOAD_MORE_FAILURE;
			sendMsg(HandlerUtils.obtainMessage(what, new Object[] { statusCode,
					throwable }));
		}

		@Override
		protected ShotsResponse parseResponse(String rawJsonData,
				boolean isFailure) throws Throwable {
			Gson gson = new Gson();
			return gson.fromJson(rawJsonData, ShotsResponse.class);
		}
	}
}
