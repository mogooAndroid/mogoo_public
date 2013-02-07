package com.michelin.droidmi.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.michelin.droidmi.R;
import com.michelin.droidmi.types.App;
import com.tsz.afinal.FinalBitmap;

public class AppLazyListAdapter extends AbsLazyListAdapter {

	private FinalBitmap mFinalBitmap;
	private Bitmap mLoadingBitmap = null;

	public AppLazyListAdapter(Context context, ListView listView, String url) {
		super(context, listView, url);
		mLoadingBitmap = BitmapFactory.decodeResource(mContext.getResources(),
				R.drawable.ic_launcher);
		mFinalBitmap = FinalBitmap.create(mContext);
	}

	@Override
	protected View createItem() {
		return View.inflate(this.mContext, R.layout.app_list_item, null);
	}

	@Override
	protected void setViewContent(Object viewHolder, Object bean, int i) {
		App apk = (App) bean;
		((ViewHolder) viewHolder).name.setText(apk.getName());
		mFinalBitmap.display(((ViewHolder) viewHolder).img, apk.getIconUrl(),
				mLoadingBitmap);
	}

	@Override
	protected ViewHolder initHolder(View convertView) {
		ViewHolder holder = new ViewHolder();
		holder.img = (ImageView) convertView.findViewById(R.id.app_img);
		holder.name = (TextView) convertView.findViewById(R.id.app_name);
		return holder;
	}

	class ViewHolder {
		ImageView img;
		TextView name;
	}
}
