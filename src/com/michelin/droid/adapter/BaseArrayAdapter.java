package com.michelin.droid.adapter;

import android.content.Context;
import android.widget.ArrayAdapter;

import com.michelin.droid.data.ConstantSet;
import com.michelin.droid.types.DroidType;
import com.michelin.droid.types.Group;
import com.michelin.droid.widget.IPageOperator;

public abstract class BaseArrayAdapter<T extends DroidType> extends
		ArrayAdapter<T> implements IPageOperator {
	private Group<T> group = null;

	public BaseArrayAdapter(Context context, int textViewResourceId) {
		super(context, -1);
	}

	@Override
	public int getCount() {
		return (group == null) ? 0 : group.size();
	}

	@Override
	public T getItem(int position) {
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
		notifyDataSetInvalidated();
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
}
