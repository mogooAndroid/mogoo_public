package com.michelin.droidmi;

import android.app.Activity;
import android.os.Bundle;

import com.michelin.droid.download.DownloadMgr;
import com.michelin.droid.download.DownloadTask;
import com.michelin.droidmi.adapter.AppLazyListAdapter;
import com.michelin.droidmi.app.DroidHttpApiV1;
import com.michelin.droidmi.parsers.AppParser;
import com.michelin.droidmi.widget.PullToRefreshListView;
import com.tsz.afinal.FinalBitmap;

public class MainActivity1 extends Activity {
	private PullToRefreshListView mListView;
	private AppLazyListAdapter mAdapter;
	private FinalBitmap mFinalBitmap;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.download);

		String url = "http://dl.sj.91.com/msoft/91assistant_3.2.9.6_295.apk";
		DownloadTask task = new DownloadTask(url);
		DownloadMgr.addTask(task);
		
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}
}