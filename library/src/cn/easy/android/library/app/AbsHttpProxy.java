package cn.easy.android.library.app;import org.apache.http.Header;import org.apache.http.HttpEntity;import android.content.Context;import cn.easy.android.library.data.BaseConstantSet;import cn.easy.android.library.util.EvtLog;import com.loopj.android.http.AsyncHttpClient;import com.loopj.android.http.RequestHandle;import com.loopj.android.http.ResponseHandlerInterface;import com.loopj.android.http.SyncHttpClient;/** * Title: AbsHttpProxy</p> * Description: 应用http请求代理抽象类</p> * @author lin.xr * @date 2014-6-28 下午10:36:41 */public abstract class AbsHttpProxy {	static final String TAG = "AbsHttpProxy";		protected BaseHttpApi mHttpApi;		protected AsyncHttpClient mAsyncHttpClient;	protected SyncHttpClient mSyncHttpClient;		public AbsHttpProxy(BaseHttpApi httpApi) {		mHttpApi = httpApi;		mAsyncHttpClient = new AsyncHttpClient();		mSyncHttpClient = new SyncHttpClient();	}	/**	 * HTTP GET请求	 * 	 * @param context	 *            上下文	 * @param url	 *            请求地址	 * @param headers	 *            请求头	 * @param responseHandler	 *            请求响应回调	 * @param shouldUseAysnc	 *            是否线程异步	 * @return 请求回调	 */	public RequestHandle get(Context context, String url, Header[] headers,			ResponseHandlerInterface responseHandler, boolean shouldUseAysnc) {		if (BaseConstantSet.IS_DEVELOPING) {			final String headerStr = headers != null ? headers.toString() : "";			EvtLog.i(TAG, "Request: GET, url: " + url);			EvtLog.i(TAG, "Request: GET, headers: " + headerStr);		}			if (shouldUseAysnc) {			return mAsyncHttpClient.get(context, url, headers, null,					responseHandler);		} else {			return mSyncHttpClient.get(context, url, headers, null,					responseHandler);		}	}	/**	 * HTTP POST请求	 * 	 * @param context	 *            上下文	 * @param url	 *            请求地址	 * @param headers	 *            请求头	 * @param entity	 *            请求实体	 * @param contentType	 *            请求实体类型	 * @param responseHandler	 *            请求响应回调	 * @param shouldUseAysnc	 *            是否线程异步	 * @return 请求回调	 */	public RequestHandle post(Context context, String url, Header[] headers,			HttpEntity entity, String contentType,			ResponseHandlerInterface responseHandler, boolean shouldUseAysnc) {		if (BaseConstantSet.IS_DEVELOPING) {			final String headerStr = headers != null ? headers.toString() : "";			final String entityStr = entity != null ? entity.toString() : "";			EvtLog.i(TAG, "Request: POST, url: " + url);			EvtLog.i(TAG, "Request: POST, headers: " + headerStr);			EvtLog.i(TAG, "Request: POST, entity: " + entityStr);		}		if (shouldUseAysnc) {			return mAsyncHttpClient.post(context, url, headers, entity,					contentType, responseHandler);		} else {			return mSyncHttpClient.post(context, url, headers, entity,					contentType, responseHandler);		}	}}