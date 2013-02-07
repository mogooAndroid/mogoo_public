package com.michelin.droidmi;

import android.app.Activity;
import android.os.Bundle;

import com.michelin.droidmi.adapter.AppLazyListAdapter;
import com.michelin.droidmi.app.DroidHttpApiV1;
import com.michelin.droidmi.parsers.AppParser;
import com.michelin.droidmi.widget.PullToRefreshListView;
import com.tsz.afinal.FinalBitmap;

public class MainActivity extends Activity {
	private PullToRefreshListView mListView;
	private AppLazyListAdapter mAdapter;
	private FinalBitmap mFinalBitmap;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_activity);
		init();
		ensureUi();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mFinalBitmap != null) {
			mFinalBitmap.onDestroy();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (mFinalBitmap != null) {
			mFinalBitmap.onResume();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (mFinalBitmap != null) {
			mFinalBitmap.onPause();
		}
	}

	private void init() {
		mFinalBitmap = FinalBitmap.create(this);
		mFinalBitmap.configLoadfailImage(R.drawable.ic_launcher);
	}

	private void ensureUi() {
		mListView = (PullToRefreshListView) findViewById(R.id.app_list);
		mAdapter = new AppLazyListAdapter(this, mListView, DroidHttpApiV1.URL_API_RECOMMEND);
		mListView.setAdapter(mAdapter);
		mAdapter.setParser(new AppParser());
		mAdapter.request();
	}
}