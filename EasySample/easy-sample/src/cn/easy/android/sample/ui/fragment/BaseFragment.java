package cn.easy.android.sample.ui.fragment;

import java.lang.ref.WeakReference;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;

public class BaseFragment extends Fragment {

	protected Activity mActivity;
	protected Handler mHandler;

	@Override
	public void onAttach(Activity activity) {
		mActivity = activity;
		mHandler = new CustomHandler(this);
		super.onAttach(activity);
	}

	protected void handleMessage(Message msg) {
	};

	protected void sendMsg(Message msg) {
		if (mHandler != null) {
			mHandler.sendMessage(msg);
		} else {
			handleMessage(msg);
		}
	}

	/**
	 * 静态的Handler对象
	 */
	private static class CustomHandler extends Handler {

		private final WeakReference<BaseFragment> mFragment;

		public CustomHandler(BaseFragment fragment) {
			mFragment = new WeakReference<BaseFragment>(fragment);
		}

		@Override
		public void handleMessage(Message msg) {
			BaseFragment fragment = mFragment.get();
			if (fragment != null) {
				fragment.handleMessage(msg);
			}
		}
	}
}
