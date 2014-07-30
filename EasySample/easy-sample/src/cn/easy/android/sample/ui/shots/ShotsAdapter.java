package cn.easy.android.sample.ui.shots;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import cn.easy.android.library.ui.adapter.SlowBaseAdapter;
import cn.easy.android.library.ui.listeners.OnSlowAdapterScrollListener;
import cn.easy.android.library.util.UiHelper;
import cn.easy.android.sample.R;
import cn.easy.android.sample.net.communication.types.Shot;

import com.nostra13.universalimageloader.core.ImageLoader;

public class ShotsAdapter extends SlowBaseAdapter {

	private Context mContext;
	private List<Shot> mShots;
	private Drawable mDefaultImageDrawable = new ColorDrawable(Color.argb(255,
			201, 201, 201));
	private ImageLoader mImageLoader;

	public ShotsAdapter(Context context, ImageLoader imageLoader) {
		mContext = context;
		mShots = new ArrayList<Shot>();
		mImageLoader = imageLoader;
	}

	public void setShots(List<Shot> shots) {
		mShots.clear();
		addShots(shots);
	}

	public void addShots(List<Shot> shots) {
		if (shots == null) {
			return;
		}
		mShots.addAll(shots);
	}

	@Override
	public void bindScrollIdleView(View convertView, Object itemObject) {
		Shot shot = (Shot) itemObject;
		TextView titleView = UiHelper.getViewFromHolder(convertView,
				R.id.tv_title);
		ImageView teaserView = UiHelper.getViewFromHolder(convertView,
				R.id.img_teaser);
		titleView.setText(shot.getTitle());
		mImageLoader.displayImage(shot.getImageUrl(), teaserView);
	}

	@Override
	public void bindScrollView(View convertView, Object itemObject) {
		Shot shot = (Shot) itemObject;
		TextView titleView = UiHelper.getViewFromHolder(convertView,
				R.id.tv_title);
		ImageView teaserView = UiHelper.getViewFromHolder(convertView,
				R.id.img_teaser);
		titleView.setText(shot.getTitle());
		mImageLoader.cancelDisplayTask(teaserView);
		teaserView.setImageDrawable(mDefaultImageDrawable);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mShots.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mShots.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.list_shots_item, parent, false);
		}
		Shot shot = (Shot) getItem(position);
		bindView(position, convertView, shot, mListBusy);
		return convertView;
	}

	private void bindView(int position, View convertView, Shot shot,
			boolean isListBusy) {
		if (isListBusy) {
			convertView.setTag(OnSlowAdapterScrollListener.BIND_DATA_TAG, shot);
			bindScrollView(convertView, shot);
		} else {
			convertView.setTag(OnSlowAdapterScrollListener.BIND_DATA_TAG, null);
			bindScrollIdleView(convertView, shot);
		}
	}
}
