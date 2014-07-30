package cn.easy.android.sample.ui.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ProgressBar;
import android.widget.TextView;
import cn.easy.android.library.ui.widget.AbsLoadingFooter;
import cn.easy.android.sample.R;

public class LoadingFooter extends AbsLoadingFooter {

	static final String TAG = "LoadingFooter";
	
	protected View mLoadingFooter;

	protected TextView mLoadingText;

	private ProgressBar mProgress;

	private long mAnimationDuration;

	public LoadingFooter(Context context) {
		super(context);
		mLoadingFooter = LayoutInflater.from(context).inflate(
				R.layout.loading_footer, null);
		mLoadingFooter.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 屏蔽点击
			}
		});
		mProgress = (ProgressBar) mLoadingFooter.findViewById(R.id.progressBar);
		mLoadingText = (TextView) mLoadingFooter.findViewById(R.id.textView);
		mAnimationDuration = context.getResources().getInteger(
				android.R.integer.config_shortAnimTime);
		setState(State.IDLE);
	}

	@Override
	public View getView() {
		// TODO Auto-generated method stub
		return mLoadingFooter;
	}

	@Override
	public void onIdle() {
		mLoadingFooter.setVisibility(View.GONE);
	}

	@Override
	@SuppressLint("NewApi")
	public void onEnd() {
		mLoadingFooter.setVisibility(View.VISIBLE);
		mLoadingText.setVisibility(View.VISIBLE);
		mLoadingText.animate().withLayer().alpha(1)
				.setDuration(mAnimationDuration);
		mProgress.setVisibility(View.GONE);
	}

	@Override
	public void onLoading() {
		mLoadingFooter.setVisibility(View.VISIBLE);
		mLoadingText.setVisibility(View.GONE);
		mProgress.setVisibility(View.VISIBLE);
	}
}
