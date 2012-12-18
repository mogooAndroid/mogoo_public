package com.michelin.droid.adapter;

import android.content.Context;
import android.database.Cursor;
import android.widget.ResourceCursorAdapter;

import com.michelin.droid.types.DroidType;
import com.michelin.droidmi.data.Constants;
import com.michelin.droidmi.widget.IPageOperator;

public abstract class BaseResourceCursorAdapter<T extends DroidType> extends
		ResourceCursorAdapter implements IPageOperator {

	public BaseResourceCursorAdapter(Context context, int layout, Cursor c) {
		super(context, layout, c);
		// TODO Auto-generated constructor stub
	}

	@Override
	public int getPageSize() {
		return Constants.PAGE_SIZE;
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
