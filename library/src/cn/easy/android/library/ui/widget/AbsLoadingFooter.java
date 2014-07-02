package cn.easy.android.library.ui.widget;

import android.content.Context;
import android.view.View;

/**
 * Title: AbsLoadingFooter</p>
 * Description: 加载更多列表footerView抽象类</p>
 * @author lin.xr
 * @date 2014-7-1 下午4:12:12
 */
public abstract class AbsLoadingFooter {

	/**
	 * Title: AbsLoadingFooter</p>
	 * Description: footerView加载</p>
	 * @author lin.xr
	 * @date 2014-7-1 下午4:12:12
	 */
	public static enum State {
		IDLE, END, LOADING
	}
	
	static final String TAG = "AbsLoadingFooter";

	protected State mState = State.IDLE;

	protected Context mContext;

	public abstract View getView();

	public abstract void onIdle();

	public abstract void onEnd();

	public abstract void onLoading();

	public AbsLoadingFooter(Context context) {
		mContext = context;
	}

	public State getState() {
		return mState;
	}

	public void setState(State status) {
		if (mState == status) {
			return;
		}
		mState = status;

		switch (status) {
		case LOADING:
			onLoading();
			break;
		case END:
			onEnd();
			break;
		default:
			onIdle();
			break;
		}
	}
}
