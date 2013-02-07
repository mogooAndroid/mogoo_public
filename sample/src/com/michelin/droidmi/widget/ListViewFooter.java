package com.michelin.droidmi.widget;

import com.michelin.droidmi.R;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

public class ListViewFooter {

	View mFooterView;

	public ListViewFooter(LayoutInflater layoutinflater) {
		mFooterView = layoutinflater.inflate(R.layout.footer_wait, null);
		showFooterViewWaiting();
	}

	public View getFooterView() {
		return mFooterView;
	}

	public void hideFooterView() {
		mFooterView.setVisibility(View.GONE);
	}

	public void showFooterViewError(
			android.view.View.OnClickListener onclicklistener) {
		mFooterView.setVisibility(View.VISIBLE);
		mFooterView.findViewById(R.id.wait).setVisibility(View.INVISIBLE);
		mFooterView.findViewById(R.id.refresh).setVisibility(View.VISIBLE);
		mFooterView.findViewById(R.id.refresh).setOnClickListener(
				onclicklistener);
	}

	public void showFooterViewError(String errorTip,
			android.view.View.OnClickListener onclicklistener) {
		mFooterView.setVisibility(View.VISIBLE);
		mFooterView.findViewById(R.id.wait).setVisibility(View.INVISIBLE);
		TextView textview = (TextView) mFooterView.findViewById(R.id.refresh);
		textview.setVisibility(View.VISIBLE);
		textview.setOnClickListener(onclicklistener);
		textview.setText(errorTip);
	}

	public void showFooterViewWaiting() {
		mFooterView.setVisibility(View.VISIBLE);
		mFooterView.findViewById(R.id.wait).setVisibility(View.VISIBLE);
		mFooterView.findViewById(R.id.refresh).setVisibility(View.INVISIBLE);
	}
}
