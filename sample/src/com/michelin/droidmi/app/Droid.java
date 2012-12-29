package com.michelin.droidmi.app;

import java.io.IOException;

import com.loopj.android.http.XmlHttpResponseHandler;
import com.michelin.droid.error.DroidCredentialsException;
import com.michelin.droid.error.DroidError;
import com.michelin.droid.error.DroidException;
import com.michelin.droid.types.Group;
import com.michelin.droid.util.EvtLog;
import com.michelin.droid.util.PackageUtil;
import com.michelin.droidmi.data.Constants;
import com.michelin.droidmi.types.Credentials;
import com.michelin.droidmi.types.Topic;

public class Droid {
	public static final String TAG = Droid.class.getSimpleName();

	private String mPhone;
	private String mPassword;
	private DroidHttpApiV1 mDroidV1;

	@V1
	public Droid(DroidHttpApiV1 httpApi) {
		mDroidV1 = httpApi;
	}

	public void setCredentials(String phone, String password) {
		mPhone = phone;
		mPassword = password;
		mDroidV1.setCredentials(phone, password);
	}

	@V1
	public void setOAuthToken(String token, String secret) {
		mDroidV1.setOAuthTokenWithSecret(token, secret);
	}

	@V1
	public void setOAuthConsumerCredentials(String oAuthConsumerKey,
			String oAuthConsumerSecret) {
		mDroidV1.setOAuthConsumerCredentials(oAuthConsumerKey,
				oAuthConsumerSecret);
	}

	public void clearAllCredentials() {
		setCredentials(null, null);
		setOAuthToken(null, null);
	}

	@V1
	public boolean hasCredentials() {
		return mDroidV1.hasCredentials() && mDroidV1.hasOAuthTokenWithSecret();
	}

	@V1
	public boolean hasLoginAndPassword() {
		return mDroidV1.hasCredentials();
	}

	@V1
	public Credentials authExchange() throws DroidException, DroidError,
			DroidCredentialsException, IOException {
		if (mDroidV1 == null) {
			throw new NoSuchMethodError(
					"authExchange is unavailable without a consumer key/secret.");
		}
		return mDroidV1.authExchange(mPhone, mPassword);
	}

	@V1
	public Group<Topic> findTopic(String akey, String uid, String aid,
			String page, String pageSize) throws DroidException, DroidError,
			IOException {
		return mDroidV1.topicRequests(akey, uid, aid, page, pageSize);
	}

	@V1
	public void findTopic(String page, String pageSize,
			XmlHttpResponseHandler responseHandler) {
		mDroidV1.topicRequests(page, pageSize, responseHandler);
	}

	@V1
	public void findRecommend(String page, String pageSize,
			XmlHttpResponseHandler responseHandler) {
		mDroidV1.recommendRequests(page, pageSize, responseHandler);
	}

	public static final DroidHttpApiV1 createHttpApi(String domain,
			String clientVersion, boolean useOAuth) {
		EvtLog.i(Droid.class, TAG, "Using foursquare.com for requests.");
		return new DroidHttpApiV1(domain, clientVersion, useOAuth);
	}

	public static final DroidHttpApiV1 createHttpApi(String clientVersion,
			boolean useOAuth) {
		return createHttpApi(getApiRootUrl(), clientVersion, useOAuth);
	}

	private static String getApiRootUrl() {
        if (Constants.USE_DEBUG_SERVER) {
        	return PackageUtil.getConfigString("debug_root_url");
        } else {
        	return PackageUtil.getConfigString("api_root_url");
        }
	}
	
	/**
	 * This api is supported in the V1 API documented at:
	 * http://groups.google.com/group/foursquare-api/web/api-documentation
	 */
	@interface V1 {
	}
}
