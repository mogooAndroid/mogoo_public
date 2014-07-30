package cn.easy.android.sample.ui.drawer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import cn.easy.android.sample.R;
import cn.easy.android.sample.types.Category;
import cn.easy.android.sample.ui.MainActivity;
import cn.easy.android.sample.ui.fragment.BaseFragment;

public class DrawerFragment extends BaseFragment {
	private ListView mListView;

	private DrawerAdapter mAdapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View contentView = inflater.inflate(R.layout.fragment_drawer, null);
		mListView = (ListView) contentView.findViewById(R.id.listView);
		mAdapter = new DrawerAdapter(mActivity, mListView);
		mListView.setAdapter(mAdapter);
		mListView.setItemChecked(0, true);
		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				mListView.setItemChecked(position, true);
				((MainActivity) mActivity).setCategory(Category.values()[position]);
			}
		});
		return contentView;
	}
}
