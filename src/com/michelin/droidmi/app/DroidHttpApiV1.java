package com.michelin.droidmi.app;

import java.io.IOException;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.XmlHttpResponseHandler;
import com.michelin.droid.error.DroidCredentialsException;
import com.michelin.droid.error.DroidError;
import com.michelin.droid.error.DroidException;
import com.michelin.droid.http.AbstractHttpApi;
import com.michelin.droid.http.HttpApi;
import com.michelin.droid.http.HttpApiWithBasicAuth;
import com.michelin.droid.http.HttpApiWithOAuth;
import com.michelin.droid.parsers.GroupParser;
import com.michelin.droid.types.Group;
import com.michelin.droid.util.EvtLog;
import com.michelin.droidmi.parsers.CredentialsParser;
import com.michelin.droidmi.parsers.TopicParser;
import com.michelin.droidmi.types.Credentials;
import com.michelin.droidmi.types.Topic;

class DroidHttpApiV1 {
	public static final String TAG = DroidHttpApiV1.class.getSimpleName();

    private static final String URL_API_AUTHEXCHANGE = "/authexchange";
    private static final String URL_API_RECOMMEND = "/Store/recommend.do";
    private static final String URL_API_TOPIC = "/Store/gettopic.do";
    
    private final DefaultHttpClient mHttpClient = AbstractHttpApi.createHttpClient();
    private HttpApi mHttpApi;

    private final String mApiBaseUrl;
    private final AuthScope mAuthScope;
    
    private AsyncHttpClient mAsyncHttpClient;

    public DroidHttpApiV1(String domain, String clientVersion, boolean useOAuth) {
        mApiBaseUrl = domain;
        mAuthScope = new AuthScope(domain, 80);

        if (useOAuth) {
            mHttpApi = new HttpApiWithOAuth(mHttpClient, clientVersion);
        } else {
            mHttpApi = new HttpApiWithBasicAuth(mHttpClient, clientVersion);
        }
        mAsyncHttpClient = new AsyncHttpClient();
    }

    void setCredentials(String phone, String password) {
        if (phone == null || phone.length() == 0 || password == null || password.length() == 0) {
            EvtLog.i(DroidHttpApiV1.class, TAG, "Clearing Credentials");
            mHttpClient.getCredentialsProvider().clear();
        } else {
            EvtLog.i(DroidHttpApiV1.class, TAG, "Setting Phone/Password: " + phone + "/******");
            mHttpClient.getCredentialsProvider().setCredentials(mAuthScope,
                    new UsernamePasswordCredentials(phone, password));
        }
    }

    public boolean hasCredentials() {
        return mHttpClient.getCredentialsProvider().getCredentials(mAuthScope) != null;
    }

    public void setOAuthConsumerCredentials(String oAuthConsumerKey, String oAuthConsumerSecret) {
		EvtLog.i(DroidHttpApiV1.class, TAG, "Setting consumer key/secret: "
				+ oAuthConsumerKey + " " + oAuthConsumerSecret);
        ((HttpApiWithOAuth) mHttpApi).setOAuthConsumerCredentials(oAuthConsumerKey,
                oAuthConsumerSecret);
    }

    public void setOAuthTokenWithSecret(String token, String secret) {
		EvtLog.i(DroidHttpApiV1.class, TAG, "Setting oauth token/secret: " + token + " " + secret);
        ((HttpApiWithOAuth) mHttpApi).setOAuthTokenWithSecret(token, secret);
    }

    public boolean hasOAuthTokenWithSecret() {
        return ((HttpApiWithOAuth) mHttpApi).hasOAuthTokenWithSecret();
    }

    /*
     * /authexchange?oauth_consumer_key=d123...a1bffb5&oauth_consumer_secret=fec...
     * 18
     */
    public Credentials authExchange(String phone, String password) throws DroidException,
            DroidCredentialsException, DroidError, IOException {
        if (((HttpApiWithOAuth) mHttpApi).hasOAuthTokenWithSecret()) {
            throw new IllegalStateException("Cannot do authExchange with OAuthToken already set");
        }
        HttpPost httpPost = mHttpApi.createHttpPost(fullUrl(URL_API_AUTHEXCHANGE), //
                new BasicNameValuePair("fs_username", phone), //
                new BasicNameValuePair("fs_password", password));
        return (Credentials) mHttpApi.doHttpRequest(httpPost, new CredentialsParser());
    }
    
    /**
     * http://192.168.0.177:9000/MAS/Store/getTopicAppList.do?akey=5252&uid=dfdf&aid=ddfadaf&page=1&pageSize=20
     * @throws IOException 
     * @throws DroidException 
     * @throws DroidError 
     * @throws DroidCredentialsException 
     */
    @SuppressWarnings("unchecked")
	public Group<Topic> topicRequests(String akey, String uid, String aid,
    		String page, String pageSize) throws DroidException, 
    		DroidCredentialsException, DroidError, IOException {
        HttpGet httpGet = mHttpApi.createHttpGet(fullUrl(URL_API_TOPIC), //
                new BasicNameValuePair("akey", akey), //
                new BasicNameValuePair("uid", uid), //
                new BasicNameValuePair("aid", aid), //
                new BasicNameValuePair("page", page), //
                new BasicNameValuePair("pageSize", pageSize) //
                );
        return (Group<Topic>) mHttpApi.doHttpRequest(httpGet, new GroupParser(new TopicParser()));
    }
    
	/**
	 * http://www.imogoo.cn/MAS/Store/gettopic.do?akey=999.999.999.999&uid=
	 * baf4370ab63a615163438ea5198042ad&aid=store@motone.net&page=1&pageSize=20
	 */
	public void topicRequests(String page, String pageSize,
			XmlHttpResponseHandler responseHandler) {
		RequestParams params = new RequestParams();
		params.put("akey", "999.999.999.999");
		params.put("uid", "baf4370ab63a615163438ea5198042ad");
		params.put("aid", "store@motone.net");
		params.put("page", page);
		params.put("pageSize", pageSize);
		EvtLog.i(Droidmi.class, TAG, "topic request url is: " 
				+ AsyncHttpClient.getUrlWithQueryString(fullUrl(URL_API_TOPIC), params));
		mAsyncHttpClient.get(fullUrl(URL_API_TOPIC), params, responseHandler);
	}
    
	/**
	 * http://www.imogoo.cn/MAS/Store/recommend.do?uid=baf4370ab63a615163438ea5198042ad
	 * &akey=c6fcdc393bd872504285426a77bfe0df&aid=store@motone.net&page=1&pagesize=20
	 */
	public void recommendRequests(String page, String pageSize,
			XmlHttpResponseHandler responseHandler) {
		RequestParams params = new RequestParams();
		params.put("akey", "c6fcdc393bd872504285426a77bfe0df");
		params.put("uid", "baf4370ab63a615163438ea5198042ad");
		params.put("aid", "store@motone.net");
		params.put("page", page);
		params.put("pageSize", pageSize);
		EvtLog.i(Droidmi.class, TAG, "recommend app request url is: " 
				+ AsyncHttpClient.getUrlWithQueryString(fullUrl(URL_API_RECOMMEND), params));
		mAsyncHttpClient.get(fullUrl(URL_API_RECOMMEND), params, responseHandler);
	}
	
    private String fullUrl(String url) {
        return mApiBaseUrl + url;
    }
}

