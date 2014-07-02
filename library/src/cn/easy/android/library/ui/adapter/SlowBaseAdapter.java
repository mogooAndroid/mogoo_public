package cn.easy.android.library.ui.adapter;

import android.widget.BaseAdapter;

/**
 * Title: SlowBaseAdapter</p>
 * Description: 缓加载基础适配器类</p>
 * @author lin.xr
 * @date 2014-7-1 下午4:12:12
 */
public abstract class SlowBaseAdapter extends BaseAdapter implements
		ISlowAdapter {
	static final String TAG = "SlowBaseAdapter";

	protected boolean mListBusy;

	@Override
	public void setListBusy(boolean isListBusy) {
		mListBusy = isListBusy;
	}
}
