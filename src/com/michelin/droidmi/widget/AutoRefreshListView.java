package com.michelin.droidmi.widget;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.michelin.droidmi.R;

public class AutoRefreshListView extends ListView implements
		AbsListView.OnScrollListener {
	private static final long AnimationsDurationMillis = 250L;
	private static final int AnimationsToDegrees = -180;
	private static final int DELAY_CHECK_INTERVAL_IN_MS = 300;
	private static final int FOOTER_ADDING = 5;
	private static final int HAS_POINTERS = 1;
	private static final int HEADER_REFRESHING = 4;
	private static final int NO_POINTER = 0;
	private static final int PULL_MAX_LENGTH = 400;
	private static final int PULL_TO_REFRESH = 2;
	private static final int RELEASE_TO_REFRESH = 3;
	private static final String TAG = "AutoRefreshListView";
	private int mCurrentScrollState;
	private DataSetObserver mDataSetObserver = new DataSetObserver() {
		public void onChanged() {
			super.onChanged();
		}
	};
	private DisplayMetrics mDm;
	private boolean mEnableTouch = true;
	private RotateAnimation mFlipAnimation;
	private RelativeLayout mFooterContentView;
	private int mFooterHeightOriginal;
	private int mFooterLastPaddingBottom;
	private RelativeLayout mFooterRefreshView;
	private int mFooterRefreshViewHeight;
	private ProgressBar mFooterRefreshViewProgress;
	private TextView mFooterRefreshViewText;
	private RelativeLayout mHeaderContentView;
	private int mHeaderHeightOriginal;
	private int mHeaderLastPaddingTop;
	private RelativeLayout mHeaderRefreshView;
	private int mHeaderRefreshViewHeight;
	private ImageView mHeaderRefreshViewImage;
	private ProgressBar mHeaderRefreshViewProgress;
	private LinearLayout mHeaderRefreshViewPrompt;
	private TextView mHeaderRefreshViewText;
	private TextView mHeaderRefreshViewTime;
	private LayoutInflater mInflater;
	private Boolean mIsCompleted = Boolean.valueOf(false);
	private int mItemHeight;
	long mLastCheckTime = 0L;
	private float mLastMotionY;
	private Date mLastRefreshTime;
	private String mNoDataFoundText;
	private OnRefreshListener mOnFooterRefreshListener;
	private OnRefreshListener mOnHeaderRefreshListener;
	private AbsListView.OnScrollListener mOnScrollListener;
	private int mRefreshOriginalBottomPadding;
	private int mRefreshOriginalTopPadding;
	private int mRefreshState = 2;
	private RotateAnimation mReverseFlipAnimation;
	private int mReverseHeight;
	private int mTouchSlot;
	private int mTouchStatus = 0;

	private Context mContext = null;

	public AutoRefreshListView(Context paramContext) {
		super(paramContext);
		init(paramContext, null);
	}

	public AutoRefreshListView(Context paramContext,
			AttributeSet paramAttributeSet) {
		super(paramContext, paramAttributeSet);
		init(paramContext, paramAttributeSet);
	}

	public AutoRefreshListView(Context paramContext,
			AttributeSet paramAttributeSet, int paramInt) {
		super(paramContext, paramAttributeSet, paramInt);
		init(paramContext, paramAttributeSet);
	}

	private void applyFooterPadding(MotionEvent paramMotionEvent) {
		int i = (int) paramMotionEvent.getY();
		if ((i < this.mLastMotionY) && (this.mIsCompleted.booleanValue())) {
			i = (int) (this.mLastMotionY - i + this.mFooterLastPaddingBottom);
			if (i <= 400)
				this.mFooterRefreshView.setPadding(
						this.mFooterRefreshView.getPaddingLeft(),
						this.mFooterRefreshView.getPaddingTop(),
						this.mFooterRefreshView.getPaddingRight(), i);
		}
	}

	private void applyHeaderPadding(MotionEvent paramMotionEvent) {
		int i = (int) paramMotionEvent.getY();
		if (i > this.mLastMotionY) {
			i = (int) ((i - this.mLastMotionY) / 1.7D + this.mHeaderLastPaddingTop);
			if (i <= 400)
				this.mHeaderRefreshView.setPadding(
						this.mHeaderRefreshView.getPaddingLeft(), i,
						this.mHeaderRefreshView.getPaddingRight(),
						this.mHeaderRefreshView.getPaddingBottom());
		}
	}

	private void applyPadding(MotionEvent paramMotionEvent) {
		applyHeaderPadding(paramMotionEvent);
		applyFooterPadding(paramMotionEvent);
	}

	private int checkItemHeight() {
	    int k = getHeaderViewsCount() + getFooterViewsCount();
	    ListAdapter localListAdapter = getAdapter();
	    int i;
	    if ((localListAdapter != null) && (localListAdapter.getCount() != k))
	    {
	      int j = localListAdapter.getCount();
	      i = 0;
	      if (j > k)
	      {
	        k = getFirstVisiblePosition();
	        int n = getLastVisiblePosition();
	        boolean flag = true;
	        k = k;
	        while (k <= n)
	        {
	          if ((k >= getHeaderViewsCount()) && (k < j - getFooterViewsCount()))
	          {
	            flag = true;
	            break;
	          }else {
	        	  k++;
	          }
	        }
	        if (flag)
	        {
	          View localView = getAdapter().getView(k, null, this);
	          if (localView != null)
	          {
	            measureView(localView);
	            i = localView.getMeasuredHeight();
	            if (i <= 0)
	            {
	              localView = getChildAt(k);
	              if (localView != null)
	              {
	                measureView(localView);
	                i = localView.getMeasuredHeight();
	              }
	            }
	          }
	        }
	      }
	      if (i > 0)
	        this.mItemHeight = i;
	      Log.d("AutoRefreshListView", "checkItemHeight, ih: " + this.mItemHeight);
	      i = this.mItemHeight;
	    }
	    else
	    {
	      this.mItemHeight = 0;
	      Log.d("AutoRefreshListView", "checkItemHeight, ih: " + this.mItemHeight);
	      i = this.mItemHeight;
	    }
	    return i;
	}

	private void checkTop() {
		checkTop(300);
	}

	private void checkTop(int paramInt) {
		postDelayed(new Runnable() {
			public void run() {
				if ((AutoRefreshListView.this.isHeaderVisible())
						&& (AutoRefreshListView.this.mTouchStatus == 0)
						&& (!AutoRefreshListView.this.isLoadingData())) {
					AutoRefreshListView.this.fixFooterPadding();
					AutoRefreshListView.this.hideHeader();
				}
			}
		}, paramInt);
	}

	private void fixFooterPadding() {
		int m = this.mFooterRefreshView.getPaddingLeft();
		int i = this.mFooterRefreshView.getPaddingTop();
		int k = this.mFooterRefreshView.getPaddingRight();
		int j = getFooterPaddingBottomNeed();
		this.mFooterRefreshView.setPadding(m, i, k, j);
	}

	private int getFooterPaddingBottomNeed() {
		int i = 0;
		int j = getHeight();
		if (j > 0) {
			ListAdapter localListAdapter = getAdapter();
			if (localListAdapter != null) {
				i = 0;
				int k = localListAdapter.getCount();
				int m = 0;
				int n = getHeaderViewsCount() + getFooterViewsCount();
				if (k != n) {
					m = getItemHeight();
					if (m > 0)
						i = j - (m + getDividerHeight()) * (k - n)
								- this.mFooterHeightOriginal;
				} else {
					i = j;
				}
				if (i < 0)
					i = 0;
				Log.d("AutoRefreshListView", "getPaddingNeed, lh: " + j
						+ ", hh: " + this.mHeaderRefreshViewHeight + ", fho: "
						+ this.mFooterHeightOriginal + ", fh: "
						+ this.mFooterRefreshView.getHeight() + ", efh: " + i
						+ ", ic: " + (k - n) + ", ac: " + k + ", ih: " + m);
			}
		}
		return i;
	}

	private int getItemHeight() {
		int i;
		if (this.mItemHeight <= 0)
			i = checkItemHeight();
		else
			i = this.mItemHeight;
		return i;
	}

	private String getTimeString() {
		Object localObject;
		if (this.mLastRefreshTime != null) {
			localObject = new SimpleDateFormat("MM-dd HH:mm");
			localObject = mContext.getResources().getString(
					R.string.pull_to_refresh_header_subtext_label)
					+ ((SimpleDateFormat) localObject)
							.format(this.mLastRefreshTime);
		} else {
			localObject = "没有刷新";
		}
		return (String) localObject;
	}

	private boolean hasItems() {
		boolean flag;
		if (getAdapter() != null
				&& getAdapter().getCount() > getHeaderViewsCount()
						+ getFooterViewsCount())
			flag = true;
		else
			flag = false;
		return flag;
	}

	private void hideFooterText() {
		Log.d("AutoRefreshListView", "hideFooterText");
		this.mFooterRefreshViewText.setText(" ");
	}

	private void hideHeader() {
		setSelectionFromTop(1, 0);
	}

	private void init(Context paramContext, AttributeSet paramAttributeSet) {
		this.mContext = paramContext;
		this.mDm = paramContext.getResources().getDisplayMetrics();
		this.mInflater = ((LayoutInflater) paramContext
				.getSystemService("layout_inflater"));
		this.mHeaderRefreshView = ((RelativeLayout) this.mInflater.inflate(
				R.layout.auto_refresh_header, this, false));
		this.mHeaderContentView = ((RelativeLayout) this.mHeaderRefreshView
				.findViewById(R.id.header_content));
		this.mHeaderRefreshViewPrompt = ((LinearLayout) this.mHeaderRefreshView
				.findViewById(R.id.refresh_prompt_text));
		this.mHeaderRefreshViewText = ((TextView) this.mHeaderRefreshView
				.findViewById(R.id.pull_to_refresh_text));
		this.mHeaderRefreshViewTime = ((TextView) this.mHeaderRefreshView
				.findViewById(R.id.pull_to_refresh_time));
		this.mHeaderRefreshViewProgress = ((ProgressBar) this.mHeaderRefreshView
				.findViewById(R.id.pull_to_refresh_progress));
		this.mHeaderRefreshViewImage = ((ImageView) this.mHeaderRefreshView
				.findViewById(R.id.pull_to_refresh_image));
		this.mHeaderRefreshView
				.setOnClickListener(new OnClickHeaderRefreshListener());
		this.mFooterRefreshView = ((RelativeLayout) this.mInflater.inflate(
				R.layout.auto_refresh_footer, this, false));
		this.mFooterContentView = ((RelativeLayout) this.mFooterRefreshView
				.findViewById(R.id.footer_content));
		this.mFooterRefreshViewText = ((TextView) this.mFooterRefreshView
				.findViewById(R.id.pull_to_refresh_text));
		this.mFooterRefreshViewProgress = ((ProgressBar) this.mFooterRefreshView
				.findViewById(R.id.pull_to_refresh_progress));
		this.mRefreshOriginalTopPadding = this.mHeaderRefreshView
				.getPaddingTop();
		this.mRefreshOriginalBottomPadding = this.mFooterRefreshView
				.getPaddingBottom();
		this.mHeaderRefreshView.setFocusable(true);
		this.mHeaderRefreshView.setClickable(true);
		addHeaderView(this.mHeaderRefreshView);
		addFooterView(this.mFooterRefreshView);
		super.setOnScrollListener(this);
		measureView(this.mHeaderRefreshView);
		measureView(this.mFooterRefreshView);
		this.mHeaderRefreshViewHeight = this.mHeaderRefreshView
				.getMeasuredHeight();
		this.mFooterRefreshViewHeight = this.mFooterRefreshView
				.getMeasuredHeight();
		this.mHeaderHeightOriginal = this.mHeaderRefreshViewHeight;
		this.mFooterHeightOriginal = this.mFooterRefreshViewHeight;
		this.mReverseHeight = (int) (0.8D * this.mHeaderRefreshViewHeight);
		this.mNoDataFoundText = getResources().getString(R.string.no_data);
		setupAnimations();
		this.mFooterRefreshView.setPadding(
				this.mFooterRefreshView.getPaddingLeft(),
				this.mFooterRefreshView.getPaddingTop(),
				this.mFooterRefreshView.getPaddingRight(),
				this.mDm.heightPixels);
		hideFooterText();
		setCompleted(true);
		this.mTouchSlot = ViewConfiguration.get(paramContext)
				.getScaledTouchSlop();
		Log.d("AutoRefreshListView", "touchSlot: " + this.mTouchSlot
				+ ", dm.h: " + this.mDm.heightPixels);
		resetHeader();
	}

	private boolean isFooterVisible() {
		int i = getLastVisiblePosition();
		int j;
		if (getAdapter() == null)
			j = getHeaderViewsCount();
		else
			j = getAdapter().getCount() - getFooterViewsCount();
		if (i < j || mFooterRefreshView.getTop() >= -2 + getHeight())
			return false;
		else
			return true;
	}

	private boolean isHeaderVisible() {
		boolean flag;
		if (getFirstVisiblePosition() != 0
				|| mHeaderRefreshView.getBottom() <= 2)
			flag = false;
		else
			flag = true;
		return flag;
	}

	private boolean isLoadingData() {
		boolean flag;
		if (mRefreshState != 4 && mRefreshState != 5)
			flag = false;
		else
			flag = true;
		return flag;
	}

	private void makeLastItemBottom() {
		Log.d("AutoRefreshListView", "makeLastItemBottom");
		if (hasItems()) {
			hideFooterText();
			int i = getItemHeight();
			if (getLastVisiblePosition() == -1 + getAdapter().getCount()) {
				int j = i;
				int k;
				int i1;
				try {
					int l = -1
							+ (getAdapter().getCount() - getFooterViewsCount());
					View view = getAdapter().getView(l, null, this);
					if (view != null) {
						measureView(view);
						j = view.getMeasuredHeight();
						Log.d("AutoRefreshListView",
								(new StringBuilder("makeLastItemBottom, idx: "))
										.append(l).append(", realHeight: ")
										.append(j).toString());
					}
				} catch (Exception e) {

				}
				k = getHeight()
						- i
						* (getAdapter().getCount() - getHeaderViewsCount() - getFooterViewsCount());
				if (k < 0)
					k = 0;
				// 这个好不用加上headerCount - 1
				i1 = /*-1 + */(getAdapter().getCount() - getFooterViewsCount());
				j = getHeight() - i - k - (j - i);
				setSelectionFromTop(i1, j);
				Log.d("AutoRefreshListView",
						(new StringBuilder("makeLastItemBottom, first: "))
								.append(getFirstVisiblePosition())
								.append(", last: ")
								.append(getLastVisiblePosition())
								.append(", real last: ").append(i1)
								.append(", hc: ").append(getHeaderViewsCount())
								.append(", ih: ").append(i).append(", lh: ")
								.append(getHeight()).append(", offset: ")
								.append(k).append(", fromTop: ").append(j)
								.toString());
			}
		} else {

			Log.d("AutoRefreshListView", "makeLastItemBottom, no items");
			showNoDataText();
		}
	}

	private void maxFooterPadding() {
		int m = this.mFooterRefreshView.getPaddingLeft();
		int j = this.mFooterRefreshView.getPaddingTop();
		int k = this.mFooterRefreshView.getPaddingRight();
		int i = this.mDm.heightPixels;
		this.mFooterRefreshView.setPadding(m, j, k, i);
	}

	private void measureView(View child) {
	    try {
			ViewGroup.LayoutParams p = child.getLayoutParams();
			if (p == null) {
				p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
			}
	
			int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0, p.width);
			int lpHeight = p.height;
			int childHeightSpec;
			if (lpHeight > 0) {
				childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight, MeasureSpec.EXACTLY);
			} else {
				childHeightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
			}
			child.measure(childWidthSpec, childHeightSpec);
	    } catch (Exception localException) {
	    }
	}

	private void refreshTime() {
		this.mHeaderRefreshViewTime.setText(getTimeString());
	}

	private void resetFooter() {
		Log.d("AutoRefreshListView", "重置底部footer ，  resetFooter");
		if (this.mRefreshState != 2) {
			this.mRefreshState = 2;
			this.mHeaderRefreshViewText
					.setText(R.string.pull_to_refresh_pull_label);
			this.mHeaderRefreshViewProgress.setVisibility(8);
			this.mFooterRefreshViewProgress.setVisibility(8);
		}
	}

	private void resetHeader() {
		Log.d("AutoRefreshListView", "resetHeader, " + this.mRefreshState);
		if (this.mRefreshState != 2)
			this.mRefreshState = 2;
		resetHeaderPadding();
		this.mHeaderRefreshView.setVisibility(0);
		this.mHeaderRefreshViewImage.setVisibility(0);
		this.mHeaderRefreshViewPrompt.setVisibility(0);
		this.mHeaderRefreshViewText.setVisibility(0);
		this.mHeaderRefreshViewText
				.setText(R.string.pull_to_refresh_pull_label);
		this.mHeaderRefreshViewTime.setVisibility(0);
		this.mHeaderRefreshViewTime.setText(getTimeString());
		this.mHeaderRefreshViewProgress.setVisibility(8);
		this.mFooterRefreshViewProgress.setVisibility(8);
	}

	private void resetHeaderPadding() {
		this.mHeaderRefreshView.setPadding(
				this.mHeaderRefreshView.getPaddingLeft(),
				this.mRefreshOriginalTopPadding,
				this.mHeaderRefreshView.getPaddingRight(),
				this.mHeaderRefreshView.getPaddingBottom());
	}

	private void saveRefreshTime() {
		this.mLastRefreshTime = null;
		this.mLastRefreshTime = new Date();
	}

	private void setFooterVisiable(boolean paramBoolean) {
		int i = this.mFooterRefreshView.getChildCount();
		Log.d("AutoRefreshListView", "setFooterVisiable, --> " + paramBoolean);
		if (!paramBoolean) {
			if (i > 0) {
				this.mFooterRefreshView.removeAllViews();
				setFooterDividersEnabled(false);
				measureView(this.mFooterRefreshView);
				this.mFooterRefreshViewHeight = this.mFooterRefreshView
						.getMeasuredHeight();
			}
		} else if (i == 0) {
			this.mFooterRefreshView.addView(this.mFooterContentView);
			setFooterDividersEnabled(true);
			measureView(this.mFooterRefreshView);
			this.mFooterRefreshViewHeight = this.mFooterRefreshView
					.getMeasuredHeight();
		}
	}

	private void setHeaderVisiable(boolean paramBoolean) {
		int i = this.mHeaderRefreshView.getChildCount();
		Log.d("AutoRefreshListView", "setHeaderVisiable, --> " + paramBoolean);
		if (!paramBoolean) {
			if (i > 0) {
				this.mHeaderRefreshView.removeAllViews();
				setHeaderDividersEnabled(false);
				measureView(this.mHeaderRefreshView);
				this.mHeaderRefreshViewHeight = this.mHeaderRefreshView
						.getMeasuredHeight();
			}
		} else if (i == 0) {
			this.mHeaderRefreshView.addView(this.mHeaderContentView);
			setHeaderDividersEnabled(true);
			measureView(this.mHeaderRefreshView);
			this.mHeaderRefreshViewHeight = this.mHeaderRefreshView
					.getMeasuredHeight();
		}
		Log.d("AutoRefreshListView", "setHeaderVisiable, hh: "
				+ this.mHeaderRefreshViewHeight);
	}

	private void setupAnimations() {
		this.mFlipAnimation = new RotateAnimation(0.0F, -180.0F, 1, 0.5F, 1,
				0.5F);
		this.mFlipAnimation.setInterpolator(new LinearInterpolator());
		this.mFlipAnimation.setDuration(250L);
		this.mFlipAnimation.setFillAfter(true);
		this.mReverseFlipAnimation = new RotateAnimation(-180.0F, 0.0F, 1,
				0.5F, 1, 0.5F);
		this.mReverseFlipAnimation.setInterpolator(new LinearInterpolator());
		this.mReverseFlipAnimation.setDuration(250L);
		this.mReverseFlipAnimation.setFillAfter(true);
	}

	private void showNoDataText() {
		this.mFooterContentView.setVisibility(0);
		this.mFooterRefreshViewProgress.setVisibility(8);
		this.mFooterRefreshViewText.setText(this.mNoDataFoundText);
		int j = (int) getResources().getDimension(
				R.dimen.no_data_text_padding_top);
		int i = getHeight() - j - this.mFooterHeightOriginal;
		this.mFooterRefreshView.setPadding(
				this.mFooterRefreshView.getPaddingLeft(), j,
				this.mFooterRefreshView.getPaddingRight(), i);
		Log.d("AutoRefreshListView", "showNoDataText, t: " + j + ", b: " + i);
	}

	private void showNoMoreText() {
		int i = this.mFooterRefreshView.getPaddingBottom();
		Log.d("AutoRefreshListView", "showNoMoreText, pb: " + i + ", fho: "
				+ this.mFooterHeightOriginal);
		if (i > this.mFooterHeightOriginal) {
			this.mFooterContentView.setVisibility(0);
			this.mFooterRefreshViewProgress.setVisibility(8);
			this.mFooterRefreshViewText.setText(getResources().getString(
					R.string.no_more_data));
			this.mFooterRefreshView.setPadding(
					this.mFooterRefreshView.getPaddingLeft(), 0,
					this.mFooterRefreshView.getPaddingRight(), i);
		}
	}

	public void enableTouch(boolean paramBoolean) {
		Log.d("AutoRefreshListView", "enableTouch, enable: " + paramBoolean);
		this.mEnableTouch = paramBoolean;
	}

	protected void layoutChildren() {
		super.layoutChildren();
		postDelayed(new Runnable() {
			public void run() {
				long l = System.currentTimeMillis();
				if (l - AutoRefreshListView.this.mLastCheckTime >= 100L) {
					AutoRefreshListView.this.checkItemHeight();
					AutoRefreshListView.this.getItemHeight();
					AutoRefreshListView.this.mLastCheckTime = l;
				}
			}
		}, 100 + 30 * getCount());
		Log.d("AutoRefreshListView", "layoutChildren, checkItemHeight");
	}

	protected void onDraw(Canvas paramCanvas) {
		super.onDraw(paramCanvas);
		checkTop();
	}

	public void onFilterComplete(int paramInt) {
		super.onFilterComplete(paramInt);
	}

	protected void onFinishInflate() {
		super.onFinishInflate();
	}

	public void onFooterRefresh() {
		Log.d("AutoRefreshListView", "onFooterRefresh");
		if (!this.mIsCompleted.booleanValue()) {
			this.mHeaderRefreshViewProgress.setVisibility(0);
			this.mHeaderRefreshViewImage.setVisibility(4);
			this.mHeaderRefreshViewText
					.setText(R.string.pull_to_refresh_refreshing_label);
			this.mFooterRefreshViewProgress.setVisibility(0);
			this.mRefreshState = 5;
			if (this.mOnFooterRefreshListener != null)
				this.mOnFooterRefreshListener.onRefresh();
		}
	}

	public void onFooterRefreshComplete() {
		if (this.mFooterRefreshView.getBottom() > 0)
			invalidateViews();
		resetFooter();
		makeLastItemBottom();
	}

	public void onHeaderRefresh() {
		Log.d("AutoRefreshListView", "onHeaderRefresh");
		resetHeaderPadding();
		setSelectionFromTop(0, 0);
		this.mHeaderRefreshViewImage.clearAnimation();
		this.mHeaderRefreshViewImage.setVisibility(8);
		this.mHeaderRefreshViewText
				.setText(R.string.pull_to_refresh_refreshing_label);
		this.mHeaderRefreshViewProgress.setVisibility(0);
		hideFooterText();
		this.mRefreshState = 4;
		if (this.mOnHeaderRefreshListener != null)
			this.mOnHeaderRefreshListener.onRefresh();
		setCompleted(false);
		Log.d("AutoRefreshListView", "onHeaderRefresh end");
	}

	public void onHeaderRefreshComplete() {
		Log.d("AutoRefreshListView", "resetHeader,  onHeaderRefreshComplete");
		resetHeader();
		saveRefreshTime();
		if (hasItems())
			hideFooterText();
		else
			showNoDataText();
		if (isHeaderVisible()) {
			Log.d("AutoRefreshListView", "onHeaderRefreshComplete setSelection");
			if (getFooterPaddingBottomNeed() > 0)
				fixFooterPadding();
			else
				maxFooterPadding();
			hideHeader();
		}
	}

	protected void onMeasure(int paramInt1, int paramInt2) {
		super.onMeasure(paramInt1, paramInt2);
		checkTop();
	}

	public void onScroll(AbsListView paramAbsListView, int paramInt1,
			int paramInt2, int paramInt3) {
		if ((isLoadingData()) || (paramInt1 + paramInt2 != paramInt3)
				|| (this.mIsCompleted.booleanValue())) {
			if ((this.mCurrentScrollState != 1) || (isLoadingData())) {
				if ((this.mCurrentScrollState != 2) || (isLoadingData())
						|| (!isHeaderVisible())) {
					if ((this.mCurrentScrollState == 2) && (!isLoadingData())
							&& (this.mIsCompleted.booleanValue())
							&& (isFooterVisible())) {
						fixFooterPadding();
						makeLastItemBottom();
						Log.d("AutoRefreshListView", "手势往上一滑, mIsCompleted: "
								+ this.mIsCompleted);
					}
				} else {
					fixFooterPadding();
					hideHeader();
					Log.d("AutoRefreshListView", "手势往下一滑");
				}
			} else if (paramInt1 != 0) {
				Log.d("AutoRefreshListView", "header 不可见");
				this.mHeaderRefreshViewImage.clearAnimation();
				this.mHeaderRefreshViewImage.setVisibility(8);
				resetHeader();
			} else {
				this.mHeaderRefreshViewImage.setVisibility(0);
				if (((this.mHeaderRefreshView.getBottom() < this.mReverseHeight) && (this.mHeaderRefreshView
						.getTop() < 0)) || (this.mRefreshState == 3)) {
					if ((this.mHeaderRefreshView.getBottom() < this.mReverseHeight)
							&& (this.mRefreshState != 2)) {
						this.mHeaderRefreshViewText
								.setText(R.string.pull_to_refresh_pull_label);
						this.mHeaderRefreshViewImage.clearAnimation();
						this.mHeaderRefreshViewImage
								.startAnimation(this.mReverseFlipAnimation);
						this.mRefreshState = 2;
						hideFooterText();
						Log.d("AutoRefreshListView", "下拉刷新, refresh state: "
								+ this.mRefreshState);
					}
				} else {
					this.mHeaderRefreshViewText
							.setText(R.string.pull_to_refresh_release_label);
					this.mHeaderRefreshViewImage.clearAnimation();
					this.mHeaderRefreshViewImage
							.startAnimation(this.mFlipAnimation);
					this.mRefreshState = 3;
					hideFooterText();
					Log.d("AutoRefreshListView", "释放刷新, refresh state: "
							+ this.mRefreshState);
				}
			}
			if (this.mOnScrollListener != null)
				this.mOnScrollListener.onScroll(paramAbsListView, paramInt1,
						paramInt2, paramInt3);
		} else {
			Log.d("AutoRefreshListView", "onScroll, 尾部自动加载");
			onFooterRefresh();
		}
	}

	public void onScrollStateChanged(AbsListView paramAbsListView, int paramInt) {
		this.mCurrentScrollState = paramInt;
		if (this.mOnScrollListener != null)
			this.mOnScrollListener.onScrollStateChanged(paramAbsListView,
					paramInt);
	}

	public boolean onTouchEvent(MotionEvent motionevent) {
		boolean flag = false;
		if (mRefreshState != 4 && mEnableTouch) {
			int i = (int) motionevent.getY();
			switch (motionevent.getAction()) {
			default:
				break;

			case 0: // '\0'
				mLastMotionY = i;
				mTouchStatus = 1;
				mFooterLastPaddingBottom = mFooterRefreshView
						.getPaddingBottom();
				mHeaderLastPaddingTop = mHeaderRefreshView.getPaddingTop();
				refreshTime();
				fixFooterPadding();
				break;

			case 1: // '\001'
				mTouchStatus = ((flag) ? 1 : 0);
				Log.d("AutoRefreshListView",
						(new StringBuilder("Up, dis: "))
								.append(Math.abs((float) i - mLastMotionY))
								.append(", slot: ").append(mTouchSlot)
								.append(", first: ")
								.append(getFirstVisiblePosition())
								.append(", header bottom: ")
								.append(mHeaderRefreshView.getBottom())
								.append(", ").append(mReverseHeight)
								.append(", state: ").append(mRefreshState)
								.append(", footer ori padding: ")
								.append(mRefreshOriginalBottomPadding)
								.append(", footer padding: ")
								.append(mFooterRefreshView.getPaddingBottom())
								.toString());
				if (Math.abs((float) i - mLastMotionY) > (float) mTouchSlot
						&& getFirstVisiblePosition() == 0 && !isLoadingData())
					if (mHeaderRefreshView.getBottom() < mReverseHeight
							|| mRefreshState != 3) {
						if (mHeaderRefreshView.getBottom() < mReverseHeight
								|| mHeaderRefreshView.getTop() <= 0) {
							Log.d("AutoRefreshListView", (new StringBuilder(
									"resetHeader, 不刷新数据, ")).append(hasItems())
									.toString());
							if (hasItems()) {
								resetHeader();
								hideHeader();
							}
						}
					} else {
						onHeaderRefresh();
						Log.d("AutoRefreshListView", "onHeaderRefresh, 刷新数据");
					}
				Log.d("AutoRefreshListView",
						(new StringBuilder("ACTION_UP, mIsCompleted: "))
								.append(mIsCompleted)
								.append(", isFooterVisible: ")
								.append(isFooterVisible())
								.append(", hasItems: ").append(hasItems())
								.append(", f: ")
								.append(getFirstVisiblePosition())
								.append(", l: ")
								.append(getLastVisiblePosition())
								.append(", footerTop: ")
								.append(mFooterRefreshView.getTop()).toString());
				if (mIsCompleted.booleanValue() && isFooterVisible()
						&& hasItems()
						&& mLastMotionY - (float) i > (float) mTouchSlot) {
					Log.d("AutoRefreshListView", (new StringBuilder(
							"ACTION_UP, 底部往下回弹到合适的位置, ")).append(hasItems())
							.toString());
					fixFooterPadding();
					makeLastItemBottom();
				}
				break;

			case 2: // '\002'
				if (motionevent.getPointerCount() != 1
						|| Math.abs(mLastMotionY - (float) i) < (float) mTouchSlot)
					break;
				applyPadding(motionevent);
				if (getAdapter() == null)
					break;
				if (hasItems() || mRefreshState == 3 || mRefreshState == 4) {
					if (getFirstVisiblePosition() > getHeaderViewsCount())
						showNoMoreText();
					else
						hideFooterText();
				} else {
					showNoDataText();
				}
				break;
			}
			flag = super.onTouchEvent(motionevent);
		}
		return flag;
	}

	public void setAdapter(ListAdapter paramListAdapter) {
		super.setAdapter(paramListAdapter);
		hideHeader();
		if (paramListAdapter != null)
			paramListAdapter.registerDataSetObserver(this.mDataSetObserver);
	}

	public void setCompleted(boolean paramBoolean) {
		Log.d("AutoRefreshListView", "setCompleted: " + this.mIsCompleted
				+ " --> " + paramBoolean);
		this.mIsCompleted = Boolean.valueOf(paramBoolean);
	}

	public void setLayout(int paramInt1, int paramInt2) {
	}

	public void setNoDataFoundText(String paramString) {
		if (paramString != null)
			this.mNoDataFoundText = paramString;
	}

	public void setOnFooterRefreshListener(
			OnRefreshListener paramOnRefreshListener) {
		this.mOnFooterRefreshListener = paramOnRefreshListener;
	}

	public void setOnHeaderRefreshListener(
			OnRefreshListener paramOnRefreshListener) {
		this.mOnHeaderRefreshListener = paramOnRefreshListener;
	}

	public void setOnScrollListener(
			AbsListView.OnScrollListener paramOnScrollListener) {
		this.mOnScrollListener = paramOnScrollListener;
	}

	private class OnClickFooterRefreshListener implements View.OnClickListener {
		private OnClickFooterRefreshListener() {
		}

		public void onClick(View paramView) {
			if (!AutoRefreshListView.this.isLoadingData())
				AutoRefreshListView.this.onFooterRefresh();
		}
	}

	private class OnClickHeaderRefreshListener implements View.OnClickListener {
		private OnClickHeaderRefreshListener() {
		}

		public void onClick(View paramView) {
			if (!AutoRefreshListView.this.isLoadingData())
				AutoRefreshListView.this.onHeaderRefresh();
		}
	}

	public static abstract interface OnRefreshListener {
		public abstract void onRefresh();
	}
}