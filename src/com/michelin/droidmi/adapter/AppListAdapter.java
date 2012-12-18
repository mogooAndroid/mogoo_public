package com.michelin.droidmi.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.michelin.droid.adapter.BaseArrayAdapter;
import com.michelin.droidmi.R;
import com.michelin.droidmi.types.Apk;
import com.tsz.afinal.FinalBitmap;

public class AppListAdapter extends BaseArrayAdapter<Apk> {

	private LayoutInflater mInflater;
	private int mLayoutToInflate;
	private FinalBitmap mFinalBitmap;
	
	
	public AppListAdapter(Context context, FinalBitmap finalBitmap) {
		super(context, -1);
		mInflater = LayoutInflater.from(context);
		mLayoutToInflate = R.layout.app_list_item;
		mFinalBitmap = finalBitmap;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// A ViewHolder keeps references to children views to avoid unnecessary
		// calls to findViewById() on each row.
		ViewHolder holder;

		// When convertView is not null, we can reuse it directly, there is no
		// need to re-inflate it. We only inflate a new View when the
		// convertView supplied by ListView is null.
		if (convertView == null) {
			convertView = mInflater.inflate(mLayoutToInflate, null);

			// Creates a ViewHolder and store references to the two children
			// views we want to bind data to.
			holder = new ViewHolder();
			holder.img = (ImageView) convertView.findViewById(R.id.app_img);
			holder.name = (TextView) convertView.findViewById(R.id.app_name);

			convertView.setTag(holder);
		} else {
			// Get the ViewHolder back to get fast access to the TextView
			// and the ImageView.
			holder = (ViewHolder) convertView.getTag();
		}

		Apk apk = (Apk) getItem(position);

		holder.name.setText(apk.getName());
		mFinalBitmap.display(holder.img, apk.getIconUrl());
		return convertView;
	}

	static class ViewHolder {
		ImageView img;
		TextView name;
	}
}
