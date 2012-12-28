package com.michelin.droid.widget;

public interface IPageOperator {
	
	public int getPageSize();

	public int getTotalCount();

	public int getNextPage();

	public int getCurrentPage();

	public boolean hasNextPage();
}
