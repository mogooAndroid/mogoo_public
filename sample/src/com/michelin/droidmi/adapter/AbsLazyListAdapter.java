package com.michelin.droidmi.adapter;

import android.content.Context;
import android.widget.ListView;

import com.loopj.android.http.XmlHttpResponseHandler;
import com.michelin.droid.parsers.GroupParser;
import com.michelin.droid.parsers.Parser;
import com.michelin.droid.types.DroidType;
import com.michelin.droid.types.Group;
import com.michelin.droid.util.EvtLog;
import com.michelin.droid.util.StringUtil;
import com.michelin.droidmi.app.Droid;
import com.michelin.droidmi.app.Droidmi;

public abstract class AbsLazyListAdapter extends AbsListViewAdapter {
	private static final String TAG = "AbsLazyListAdapter";
	
	private Parser<? extends DroidType> mParser = null;
	
	public AbsLazyListAdapter(Context context) {
		super(context);
	}

	public AbsLazyListAdapter(Context context, ListView listView,
			String url) {
		super(context, listView, url);
	}
	
	public void setParser(Parser<? extends DroidType> subParser) {
		mParser = subParser;
	}
	
	@Override
	protected void doRequest(String url) {
		if(mParser == null) {
			throw new NullPointerException("Parser is null, " +
					"call setParser before calling this .");
		}
		
		if (StringUtil.isNullOrEmpty(url)) {
			EvtLog.w(TAG, "AbsLazyListAdapter request url is null or empty.");
			return;
		}
		
		Droidmi droidmi = (Droidmi) mContext.getApplicationContext();
		Droid droid = droidmi.getDroid();
		droid.findLazyListData(url, getNextPage(), getPageSize(), new XmlHttpResponseHandler(
				new GroupParser(mParser)) {

			@Override
			public void onSuccess(DroidType response) {
				// TODO Auto-generated method stub
				appendData((Group<DroidType>) response, !hasNextPage(), getCurrentPage());
				super.onSuccess(response);
			}

			@Override
			public void onFailure(Throwable error, String content) {
				notifyRequestError();
				super.onFailure(error, content);
			}
		});
	}
}
