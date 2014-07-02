package cn.easy.android.library.ui.adapter;

import android.content.Context;
import android.widget.CursorAdapter;

/**
 * Title: SlowCursorAdapter</p>
 * Description: 缓加载游标适配器接口类</p>
 * @author lin.xr
 * @date 2014-7-1 下午4:12:12
 */
public abstract class SlowCursorAdapter extends CursorAdapter implements
		ISlowAdapter {
	static final String TAG = "SlowCursorAdapter";

	protected boolean mListBusy;

	public SlowCursorAdapter(Context context) {
		super(context, null, false);
	}

	@Override
	public void setListBusy(boolean isListBusy) {
		mListBusy = isListBusy;
	}
}