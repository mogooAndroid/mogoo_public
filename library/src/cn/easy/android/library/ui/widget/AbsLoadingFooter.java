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
	 * 
	 * @author lin.xr
	 * @date 2014-7-1 下午4:12:12
	 */
	public static enum State {
		IDLE, END, LOADING
	}

	static final String TAG = "AbsLoadingFooter";

	protected State mState = State.IDLE;

	protected Context mContext;

	/**
	 * 获取footer视图
	 * @return footer视图
	 */
	public abstract View getView();

	/**
	 * footer加载空闲回调
	 */
	public abstract void onIdle();

	/**
	 * footer加载空闲回调
	 */
	public abstract void onEnd();

	/**
	 * footer加载中回调
	 */
	public abstract void onLoading();

	public AbsLoadingFooter(Context context) {
		mContext = context;
	}

	/**
	 * 获取footer加载状态
	 * @return footer加载状态
	 */
	public State getState() {
		return mState;
	}

	/**
	 * 设置footer加载状态
	 * @param status footer加载状态
	 */
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
