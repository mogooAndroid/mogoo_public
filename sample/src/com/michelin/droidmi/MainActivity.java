package com.michelin.droidmi;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;

import com.loopj.android.http.XmlHttpResponseHandler;
import com.michelin.droid.parsers.GroupParser;
import com.michelin.droid.types.DroidType;
import com.michelin.droid.types.Group;
import com.michelin.droid.util.NotificationsUtil;
import com.michelin.droidmi.adapter.AppListAdapter;
import com.michelin.droidmi.app.Droid;
import com.michelin.droidmi.app.Droidmi;
import com.michelin.droidmi.parsers.ApkParser;
import com.michelin.droidmi.parsers.TopicParser;
import com.michelin.droidmi.types.Apk;
import com.michelin.droidmi.types.Topic;
import com.michelin.droidmi.widget.AutoRefreshListView;
import com.tsz.afinal.FinalBitmap;

public class MainActivity extends Activity {
	private AutoRefreshListView mListView;
	private AppListAdapter mAdapter;
	private FinalBitmap mFinalBitmap;
	private Group<Apk> mApks = new Group<Apk>();
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        init();
        ensureUi();
        // requestTopices();
        // asyncRequestTopices();
    }
    
	private void init() {
		mFinalBitmap = FinalBitmap.create(this);
		mFinalBitmap.configLoadfailImage(R.drawable.ic_launcher);
	}

	private void ensureUi() {
		mAdapter = new AppListAdapter(this, mFinalBitmap);
		mListView = (AutoRefreshListView) findViewById(R.id.app_list);
		mListView.setAdapter(mAdapter);
		mListView.setCompleted(true);
		mListView.setOnHeaderRefreshListener(new AutoRefreshListView.OnRefreshListener() {
			
			@Override
			public void onRefresh() {
				asyncRequestRecommend(true);
			}
		});
		mListView.setOnFooterRefreshListener(new AutoRefreshListView.OnRefreshListener() {
			
			@Override
			public void onRefresh() {
				 asyncRequestRecommend(false);
			}
		});
		
		mListView.setOnScrollListener(new OnScrollListener() {
			
			public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                	mFinalBitmap.pauseWork(false);
                } else {
                	mFinalBitmap.pauseWork(true);
                }
			}
			
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				
			}
		});
		asyncRequestRecommend(true);
	}
	
	private void requestTopices() {
		TopicTask topicTask = new TopicTask(this);
		topicTask.execute();
	}
	
    private void asyncRequestTopices() {
		Droidmi droidmi = (Droidmi) this.getApplication();
		Droid droid = droidmi.getDroid();
		droid.findTopic("1", "20", new XmlHttpResponseHandler(new GroupParser(
				new TopicParser())) {
			
			@Override
			public void onSuccess(DroidType response) {
				// TODO Auto-generated method stub
				Group<Topic> topics = (Group<Topic>) response;
				super.onSuccess(response);
			}

			@Override
			public void onFailure(Throwable error, String content) {
				NotificationsUtil.ToastReasonForFailure(MainActivity.this,
						error);
				super.onFailure(error, content);
			}
		});
	}
    
    private void asyncRequestRecommend(final boolean refreshHeader) {
    	if(refreshHeader) {
    		mApks.clear();
    		mAdapter.notifyDataSetChanged();
    	}
    	Droidmi droidmi = (Droidmi) this.getApplication();
		Droid droid = droidmi.getDroid();
		String page = String.valueOf(mAdapter.getNextPage());
		String pageSize = String.valueOf(mAdapter.getPageSize());
		droid.findRecommend(page, pageSize, new XmlHttpResponseHandler(new GroupParser(
				new ApkParser())) {
			
			@Override
			public void onSuccess(DroidType response) {
				// TODO Auto-generated method stub
				Group<Apk> apks = (Group<Apk>) response;
				for(Apk apk : apks) {
System.out.println(apk.toString());
				}
				mApks.addAll(apks);
				mAdapter.setGroup(mApks);
				if (refreshHeader) {
					mListView.onHeaderRefreshComplete();
				} else {
					mListView.onFooterRefreshComplete();
				}
				if (mAdapter.hasNextPage()) {
					mListView.setCompleted(false);
				} else {
					mListView.setCompleted(true);
				}
				super.onSuccess(response);
			}

			@Override
			public void onFailure(Throwable error, String content) {
				NotificationsUtil.ToastReasonForFailure(MainActivity.this,
						error);
				super.onFailure(error, content);
			}
		});
	}
    
	private void onTopicTaskComplete(Group<Topic> topics, Exception reason) {
    	if(topics != null) {
    	}
    }
    
    private static class TopicTask extends AsyncTask<String, Void, Group<Topic>> {

        private MainActivity mActivity;
        private Exception mReason;

        public TopicTask(MainActivity activity) {
            mActivity = activity;
        }
        
        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Group<Topic> doInBackground(String... params) {
            try {
                Droidmi droidmi = (Droidmi) mActivity.getApplication();
                Droid droid = droidmi.getDroid();
                return droid.findTopic("1", "1", "1", "1", "20");
            } catch (Exception e) {
                mReason = e;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Group<Topic> topics) {
            if (mActivity != null) {
                mActivity.onTopicTaskComplete(topics, mReason);
            }
        }

        @Override
        protected void onCancelled() {
            if (mActivity != null) {
                mActivity.onTopicTaskComplete(null, mReason);
            }
        }
    }
}