package cn.easy.android.sample.ui.drawer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import cn.easy.android.sample.R;
import cn.easy.android.sample.types.Category;

public class DrawerAdapter extends BaseAdapter {

	private Context mContext;
	private ListView mListView;

	public DrawerAdapter(Context context, ListView listView) {
		mContext = context;
		mListView = listView;
	}

	@Override
	public int getCount() {
		return Category.values().length;
	}

	@Override
	public Category getItem(int position) {
		return Category.values()[position];
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.drawer_list_item, null);
		}
		TextView textView = (TextView) convertView.findViewById(R.id.textView);
		textView.setText(getItem(position).getDisplayName());
		textView.setSelected(mListView.isItemChecked(position));
		return convertView;
	};
}
