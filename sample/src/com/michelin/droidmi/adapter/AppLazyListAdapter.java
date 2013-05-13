package com.michelin.droidmi.adapter;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.michelin.droidmi.R;
import com.michelin.droidmi.download.DownloadMgr;
import com.michelin.droidmi.download.DownloadTask;
import com.michelin.droidmi.download.DownloadTaskListener;
import com.michelin.droidmi.types.App;
import com.tsz.afinal.FinalBitmap;

public class AppLazyListAdapter extends AbsLazyListAdapter {

	private FinalBitmap mFinalBitmap;
	private Bitmap mLoadingBitmap = null;
	private Map<Integer, Integer> progresses = new HashMap<Integer, Integer>();
	
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			notifyDataSetChanged();
		};
	};
	
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
		final App app = (App) bean;
		if(!progresses.containsKey(i)) {
			progresses.put(i, 0);
		}
		((ViewHolder) viewHolder).name.setText(app.getName());
		mFinalBitmap.display(((ViewHolder) viewHolder).img, app.getIconUrl(),
				mLoadingBitmap);
		((ViewHolder) viewHolder).img.setOnClickListener(new IconOnClickListener(i, app));
		((ViewHolder) viewHolder).pb.setProgress(progresses.get(i));
	}
	
	@Override
	protected ViewHolder initHolder(View convertView) {
		ViewHolder holder = new ViewHolder();
		holder.img = (ImageView) convertView.findViewById(R.id.app_img);
		holder.name = (TextView) convertView.findViewById(R.id.app_name);
		holder.pb = (ProgressBar) convertView.findViewById(R.id.app_pb);
		return holder;
	}

	class IconOnClickListener implements OnClickListener {
		int position;
		App app;
		
		public IconOnClickListener(int position, App app) {
			this.position = position;
			this.app = app;
		}
		
		@Override
		public void onClick(View view) {
			if(app == null)
				return;
			
			final DownloadTask downloadTask = new DownloadTask();
			downloadTask.setDownloadUrl(app.getApkUrl());
			downloadTask.setName(app.getName());
			DownloadTaskListener downloadTaskListener = new DownloadTaskListener(downloadTask) {
				
				@Override
				public void onStateChange(int paramInt) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onProgressChange(int paramInt, long paramLong) {
					// TODO Auto-generated method stub
					System.out.println(String.format("lin.xr, task %s progressChange: ", downloadTask.getName()) + String.valueOf(paramInt));
					progresses.put(position, paramInt);
					mHandler.sendEmptyMessage(1);
				}
			};
			downloadTask.addDownloadListener(downloadTaskListener, position);
			DownloadMgr.addTask(downloadTask);
		}
	}
	
	class ViewHolder {
		ImageView img;
		TextView name;
		ProgressBar pb;
	}
}
