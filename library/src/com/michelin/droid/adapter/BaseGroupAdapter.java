/**
 * Copyright 2008 Joe LaPenna
 */
package com.michelin.droid.adapter;

import com.michelin.droid.types.DroidType;
import com.michelin.droid.types.Group;

import android.content.Context;
import android.os.Handler;
import android.widget.BaseAdapter;

/**
 * @author Joe LaPenna (joe@joelapenna.com)
 */
abstract class BaseGroupAdapter<T extends DroidType> extends BaseAdapter {

	Group<T> group = null;

	public BaseGroupAdapter(Context context) {
	}

	@Override
	public int getCount() {
		return (group == null) ? 0 : group.size();
	}

	@Override
	public Object getItem(int position) {
		return group.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public boolean isEmpty() {
		return (group == null) ? true : group.isEmpty();
	}

	public void setGroup(Group<T> g) {
		group = g;
		mHandler.sendEmptyMessage(0);
	}
	
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			notifyDataSetInvalidated();
		};
	};
}
