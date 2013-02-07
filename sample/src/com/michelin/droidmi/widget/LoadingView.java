package com.michelin.droidmi.widget;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.drawable.AnimationDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.michelin.droidmi.R;

public class LoadingView {
	private static final int S_LOADDING = 0;
	private static final int S_LOAD_ERROR = 1;
	private static final int S_LOAD_NOTICE = 2;
	private Context context;
	private ImageView mDyImage;
	private LayoutInflater mInflater;
	private View.OnClickListener mListener;
	private ImageView mLoadImage;
	private TextView mLoadText;
	private LinearLayout mLoadingPage;
	private TextView mNoticeText;
	private boolean mSmall = false;
	private int mStatus = S_LOADDING;
	private String mTip = "";

	public LoadingView(LayoutInflater layoutInflater) {
		mInflater = layoutInflater;
		context = layoutInflater.getContext().getApplicationContext();
		init();
	}

	private void init() {
		mLoadingPage = new LoadingContainer(context);
		mLoadingPage.setGravity(Gravity.CENTER);
		mLoadingPage.setLayoutParams(new ViewGroup.LayoutParams(
				LinearLayout.LayoutParams.FILL_PARENT,
				LinearLayout.LayoutParams.FILL_PARENT));
		mLoadingPage.addView(mInflater.inflate(R.layout.loading_page, null));
	}

	private void initContentView() {
		mLoadingPage.removeAllViews();
		View view = mInflater.inflate(R.layout.loading_page, null);
		mLoadingPage.addView(view);
		mLoadImage = (ImageView) view.findViewById(R.id.loading_image);
		mDyImage = (ImageView) view.findViewById(R.id.gf_loading);
		mLoadText = (TextView) view.findViewById(R.id.loading_text);
		mNoticeText = (TextView) view.findViewById(R.id.notic_text);
		if (mSmall) {
			mLoadText.setTextSize(12F);
			mNoticeText.setTextSize(12F);
		} else {
			mLoadText.setTextSize(15F);
			mNoticeText.setTextSize(15F);
		}
		show();
	}

	private void show() {
		switch (mStatus) {
		case S_LOADDING:
			mLoadImage.setVisibility(View.GONE);
			mDyImage.setVisibility(View.VISIBLE);
			mLoadText.setVisibility(View.VISIBLE);
			mNoticeText.setVisibility(View.GONE);
			mLoadText.setText(R.string.loading_text);
			mLoadingPage.setOnClickListener(null);
			mDyImage.post(new Runnable() {

				public void run() {
					if (mDyImage != null) {
						AnimationDrawable animationdrawable = (AnimationDrawable) mDyImage
								.getDrawable();
						animationdrawable.stop();
						animationdrawable.start();
					}
				}
			});
			break;
		case S_LOAD_ERROR:
			this.mLoadImage.setVisibility(View.VISIBLE);
			this.mLoadText.setVisibility(View.VISIBLE);
			this.mNoticeText.setVisibility(View.GONE);
			this.mDyImage.setVisibility(View.GONE);
			this.mLoadText.setText(R.string.loading_failed);
			this.mLoadingPage.setOnClickListener(mListener);
			break;
		case S_LOAD_NOTICE:
			this.mLoadImage.setVisibility(View.VISIBLE);
			this.mLoadText.setVisibility(View.VISIBLE);
			this.mNoticeText.setVisibility(View.GONE);
			this.mDyImage.setVisibility(View.GONE);
			this.mLoadImage.setImageResource(R.drawable.list_load_notice);
			this.mLoadText.setText(mTip);
			this.mLoadingPage.setOnClickListener(mListener);
			break;
		default:
			break;
		}
	}

	private void updateEmptyStatus(ListView listview) {
		ListAdapter listadapter = listview.getAdapter();
		View view = listview.getEmptyView();
		if (listadapter == null || listadapter.isEmpty()) {
			if (view != null) {
				view.setVisibility(View.VISIBLE);
				listview.setVisibility(View.GONE);
			} else {
				listview.setVisibility(View.VISIBLE);
			}
		} else {
			if (view != null)
				view.setVisibility(View.GONE);
			listview.setVisibility(View.VISIBLE);
		}
	}

	public void changeNormalMode() {
		mSmall = false;
		if (mLoadText != null) {
			mLoadText.setTextSize(15F);
			mNoticeText.setTextSize(15F);
		}
	}

	public void changeSmallMode() {
		mSmall = true;
		if (mLoadText != null) {
			mLoadText.setTextSize(12F);
			mNoticeText.setTextSize(12F);
		}
	}

	public View getView() {
		return mLoadingPage;
	}

	public void setEmptyView(final ListView listView) {
		mLoadingPage.setVisibility(View.GONE);
		((ViewGroup) listView.getParent()).addView(this.mLoadingPage);
		listView.setEmptyView(this.mLoadingPage);
		((ViewGroup) listView.getParent()).requestChildFocus(listView,
				this.mLoadingPage);
		listView.getAdapter().registerDataSetObserver(new DataSetObserver() {
			@Override
			public void onChanged() {
				updateEmptyStatus(listView);
				super.onChanged();
			}

			@Override
			public void onInvalidated() {
				updateEmptyStatus(listView);
				super.onInvalidated();
			}
		});
	}

	public void showLoadFailed(View.OnClickListener onclicklistener) {
		mStatus = S_LOAD_ERROR;
		mListener = onclicklistener;
		if (mLoadImage != null)
			show();
	}

	public void showNotice(String s, View.OnClickListener onclicklistener) {
		mStatus = S_LOAD_NOTICE;
		mListener = onclicklistener;
		mTip = s;
		if (mLoadImage != null)
			show();
	}

	public void showViewLoading() {
		mStatus = S_LOADDING;
		if (mLoadImage != null)
			show();
	}

	private class LoadingContainer extends LinearLayout {

		protected void onAttachedToWindow() {
			if (mLoadingPage != null)
				initContentView();
			super.onAttachedToWindow();
		}

		protected void onDetachedFromWindow() {
			if (mLoadingPage != null) {
				mLoadingPage.removeAllViews();
				mLoadImage = null;
				mDyImage = null;
				mLoadText = null;
				mNoticeText = null;
			}
			super.onDetachedFromWindow();
		}

		public LoadingContainer(Context context) {
			super(context);
		}
	}
}
