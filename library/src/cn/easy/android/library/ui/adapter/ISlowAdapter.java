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
	
	/**
	 * 绑定列表空闲视图
	 * @param convertView 列表项视图
	 * @param itemObject 列表项数据
	 */
	void bindScrollIdleView(View convertView, Object itemObject);

	/**
	 *  绑定列表滚动视图
	 * @param convertView 列表项视图
	 * @param itemObject 列表项数据
	 */
	void bindScrollView(View convertView, Object itemObject);

	/**
	 * 设置列表是否滚动状态
	 * @param isListBusy 是否滚动状态
	 */
	void setListBusy(boolean isListBusy);
}
