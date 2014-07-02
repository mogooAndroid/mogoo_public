package cn.easy.android.library.ui.adapter;

import android.view.View;

/**
 * Title: ISlowAdapter</p>
 * Description: 缓加载适配器接口类</p>
 * @author lin.xr
 * @date 2014-7-1 下午4:12:12
 */
public interface ISlowAdapter {

	static final String TAG = "ISlowAdapter";
	
	void bindScrollIdleView(View convertView, Object itemObject);

	void bindScrollView(View convertView, Object itemObject);

	void setListBusy(boolean isListBusy);
}
