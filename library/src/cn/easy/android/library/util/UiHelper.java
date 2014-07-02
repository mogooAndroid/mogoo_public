package cn.easy.android.library.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Build.VERSION;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

/**
 * Title: UiHelper</p>
 * Description: UI操作工具类</p>
 * @author lin.xr
 * @date 2014-6-28 下午8:01:19
 */
public class UiHelper {
	static final String TAG = "UiHelper";

	/**
	 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
	 * 
	 * @param context
	 *            上下文
	 * @param dpValue
	 *            dp单位
	 * @return px单位
	 */
	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	/**
	 * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
	 * 
	 * @param context
	 *            上下文
	 * @param pxValue
	 *            px单位
	 * @return dp单位
	 */
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	/**
	 * 创建带两个按钮对话框
	 * 
	 * @param message
	 *            ConfirmDialog的提示消息
	 * @param positive
	 *            ConfirmDialog确认按钮上面的文字
	 * @param negative
	 *            ConfirmDialog取消按钮上面的文字
	 * @param dialogListener
	 *            ConfirmDialog 的监听器
	 * @return 带两个按钮对话框
	 */
	public static Dialog createConfirmDialog(Context context, String message,
			String positive, String negative,
			final DialogInterface.OnClickListener dialogListener) {
		AlertDialog alert = null;
		AlertDialog.Builder builder = null;
		builder = new AlertDialog.Builder(context);
		builder.setMessage(message).setCancelable(true)
				.setPositiveButton(positive, dialogListener)
				.setNegativeButton(negative, dialogListener);
		alert = builder.create();
		return alert;
	}

	/**
	 * 创建带两个按钮对话框
	 * 
	 * @param message
	 *            ConfirmDialog的提示消息
	 * @param positive
	 *            ConfirmDialog确认按钮上面的文字
	 * @param negative
	 *            ConfirmDialog取消按钮上面的文字
	 * @param dialogListener
	 *            ConfirmDialog 的监听器
	 * @return 带两个按钮对话框
	 */
	public static AlertDialog createConfirmDialog(Context context, int message,
			int positive, int negative,
			final DialogInterface.OnClickListener dialogListener) {
		AlertDialog alert = null;
		AlertDialog.Builder builder = null;
		builder = new AlertDialog.Builder(context);
		builder.setMessage(message).setCancelable(true)
				.setPositiveButton(positive, dialogListener)
				.setNegativeButton(negative, dialogListener);
		alert = builder.create();
		return alert;
	}

	/**
	 * 创建单按钮对话框
	 * 
	 * @param context
	 *            上下文
	 * @param title
	 *            标题
	 * @param msg
	 *            信息
	 * @param positive
	 *            按钮文字
	 * @param positiveListener
	 *            按钮监听
	 * @return 单按钮对话框
	 */
	public static Dialog createDoneDialog(Context context, String title,
			String msg, String positive,
			DialogInterface.OnClickListener positiveListener) {
		Dialog dialog = new AlertDialog.Builder(context).setTitle(title)
				.setMessage(msg).setPositiveButton(positive, positiveListener)
				.create();
		return dialog;
	}

	/**
	 * 创建选择对话框
	 * 
	 * @param title
	 *            SelectDialog的提示消息
	 * @param positive
	 *            SelectDialog确认按钮上面的文字
	 * @param negative
	 *            SelectDialog取消按钮上面的文字
	 * @param dialogListener
	 *            SelectDialog 的监听器
	 * @return 选择对话框
	 */
	public static Dialog createSelectDialog(Context context, String title,
			String[] choiceItems, String positive, String negative,
			DialogInterface.OnClickListener choiceOnClickListener,
			final DialogInterface.OnClickListener dialogListener) {
		Dialog dialog = null;
		AlertDialog.Builder builder = null;
		builder = new AlertDialog.Builder(context);
		builder.setTitle(title)
				.setSingleChoiceItems(choiceItems, 1, choiceOnClickListener)
				.setPositiveButton(positive, dialogListener)
				.setNegativeButton(negative, dialogListener);
		dialog = builder.create();
		return dialog;
	}

	/**
	 * 创建带文字的进度框
	 * 
	 * @param msg
	 *            需要显示的文本信息
	 */
	public static ProgressDialog createProgressDialog(Context context,
			CharSequence msg) {
		ProgressDialog pDialog = ProgressDialog.show(context, null, msg);
		pDialog.setCancelable(true);
		pDialog.setCanceledOnTouchOutside(true);
		return pDialog;
	}

	/**
	 * 设置对话框是否自动关闭
	 * 
	 * @param closable
	 *            是否可关闭
	 */
	public static void setDialogClosable(Dialog dialog, boolean closable) {
		try {
			Field field = dialog.getClass().getSuperclass()
					.getDeclaredField("mShowing");
			field.setAccessible(true);
			field.set(dialog, closable);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取对话框中message的父布局ScrollView, 必须在dialog.show()之后调用
	 * 
	 * @param context
	 *            上下文
	 * @param dialog
	 *            对话框
	 * @return 对话框中message的父布局ScrollView
	 */
	public static ScrollView findScrollView(Context context, Dialog dialog) {
		if (dialog == null || !dialog.isShowing()) {
			throw new IllegalStateException(
					"findScrollView only can called after dialog showed.");
		}
		ViewGroup parentView = (ViewGroup) dialog.getWindow().getDecorView();
		return findScrollView(context, parentView);
	}

	/**
	 * 递归遍历视图树获取scroolview
	 * 
	 * @param context
	 *            上下文
	 * @param parentView
	 *            父视图
	 * @return scroolview
	 */
	public static ScrollView findScrollView(Context context,
			ViewGroup parentView) {
		for (int i = 0; i < parentView.getChildCount(); i++) {
			View childView = parentView.getChildAt(i);
			if (childView instanceof ScrollView) {
				EvtLog.d(TAG, childView.toString());
				return (ScrollView) childView;
			}
			if (childView instanceof ViewGroup) {
				findScrollView(context, (ViewGroup) childView);
			}
		}
		return new ScrollView(context);
	}

	/**
	 * 创建文字颜色selector
	 * 
	 * @param normal
	 * @param pressed
	 * @param focused
	 * @param unable
	 * @return 文字颜色selector
	 */
	public static ColorStateList createColorStateList(int normal, int pressed,
			int focused, int unable) {
		int[] colors = new int[] { pressed, focused, normal, focused, unable,
				normal };
		int[][] states = new int[6][];
		states[0] = new int[] { android.R.attr.state_pressed,
				android.R.attr.state_enabled };
		states[1] = new int[] { android.R.attr.state_enabled,
				android.R.attr.state_focused };
		states[2] = new int[] { android.R.attr.state_enabled };
		states[3] = new int[] { android.R.attr.state_focused };
		states[4] = new int[] { android.R.attr.state_window_focused };
		states[5] = new int[] {};
		ColorStateList colorList = new ColorStateList(states, colors);
		return colorList;
	}

	/**
	 * 从视图根部递归设置TextView、Button、EditText 字体
	 * 
	 * @param context
	 *            上下文
	 * @param root
	 *            视图根部
	 * @param tf
	 *            字体
	 */
	public static void changeFonts(Context context, ViewGroup root, Typeface tf) {
		for (int i = 0; i < root.getChildCount(); i++) {
			View v = root.getChildAt(i);
			if (v instanceof TextView) {
				((TextView) v).setTypeface(tf);
			} else if (v instanceof Button) {
				((Button) v).setTypeface(tf);
			} else if (v instanceof EditText) {
				((EditText) v).setTypeface(tf);
			} else if (v instanceof ViewGroup) {
				changeFonts(context, (ViewGroup) v, tf);
			}
		}
	}

	/**
	 * 设置可滚动view取消超出内容区域滚动
	 * 
	 * @param view
	 */
	public static void disableOverscrollMode(View view) {
		if (view instanceof ListView) {
			try {
				ListView listView = (ListView) view;
				Method setEnableExcessScroll = ListView.class.getMethod(
						"setEnableExcessScroll", Boolean.TYPE);
				if (setEnableExcessScroll != null) {
					setEnableExcessScroll.invoke(listView,
							Boolean.valueOf(false));
				}
			} catch (Exception ignore) {
				// Silently ignore
			}
		}

		try {
			int OVER_SCROLL_NEVER = View.class.getField("OVER_SCROLL_NEVER")
					.getInt(null);
			Method setOverScrollMode = View.class.getMethod(
					"setOverScrollMode", new Class[] { Integer.TYPE });
			if (setOverScrollMode != null) {
				setOverScrollMode.invoke(view, OVER_SCROLL_NEVER);
			}
		} catch (Exception ignore) {
			// Silently ignore
		}
	}

	/**
	 * 从view获取bitmap
	 * 
	 * @param view
	 *            视图
	 * @return view生成的bitmap
	 */
	public static Bitmap getBitmapFromView(View view) {
		view.destroyDrawingCache();
		view.measure(View.MeasureSpec.makeMeasureSpec(0,
				View.MeasureSpec.UNSPECIFIED), View.MeasureSpec
				.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
		view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
		view.setDrawingCacheEnabled(true);
		Bitmap bitmap = view.getDrawingCache(true);
		return bitmap;
	}
	
	/**
	 * 从viewHolder中获取缓存的view
	 * @param parentView 父视图
	 * @param id 视图id
	 * @return 缓存的view
	 */
	@SuppressWarnings("unchecked")
	public static <T extends View> T getViewFromHolder(View parentView, int id) {
		SparseArray<View> viewHolder = (SparseArray<View>) parentView.getTag();
		if (viewHolder == null) {
			viewHolder = new SparseArray<View>();
			parentView.setTag(viewHolder);
		}
		View childView = viewHolder.get(id);
		if (childView == null) {
			childView = parentView.findViewById(id);
			viewHolder.put(id, childView);
		}
		return (T) childView;
	}
	
	/**
	 * 滚动列表到顶端
	 * 
	 * @param listView
	 *            目标列表
	 */
	public static void smoothScrollListViewToTop(final ListView listView) {
		if (listView == null) {
			return;
		}
		smoothScrollListView(listView, 0);
		Runnable runnable = new Runnable() {

			@Override
			public void run() {
				listView.setSelection(0);
			}
		};
		listView.postDelayed(runnable, 200);
	}

	/**
	 * 滚动列表到position
	 * 
	 * @param listView
	 *            目标列表
	 * @param position
	 *            目标位置
	 */
	@SuppressLint("NewApi")
	public static void smoothScrollListView(ListView listView, int position) {
		if (VERSION.SDK_INT > 7) {
			listView.smoothScrollToPositionFromTop(0, 0);
		} else {
			listView.setSelection(position);
		}
	}
}
