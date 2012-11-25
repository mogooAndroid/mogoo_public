package com.michelin.droid;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.loopj.android.http.XmlHttpResponseHandler;
import com.michelin.droid.error.DroidCredentialsException;
import com.michelin.droid.error.DroidError;
import com.michelin.droid.error.DroidException;
import com.michelin.droid.types.Credentials;
import com.michelin.droid.types.Group;
import com.michelin.droid.types.Topic;

public class Droid {
    private static final Logger LOG = Logger.getLogger("com.michelin.droid");
    public static final boolean DEBUG = true;
    public static final boolean PARSER_DEBUG = false;

    public static final String DROID_API_DOMAIN = "www.imogoo.cn/MAS/";

    public static final String MALE = "male";
    public static final String FEMALE = "female";

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
    public void setOAuthConsumerCredentials(String oAuthConsumerKey, String oAuthConsumerSecret) {
        mDroidV1.setOAuthConsumerCredentials(oAuthConsumerKey, oAuthConsumerSecret);
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
    		String page, String pageSize) 
    	throws DroidException, DroidError, IOException {
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
	
    public static final DroidHttpApiV1 createHttpApi(String domain, String clientVersion,
            boolean useOAuth) {
        LOG.log(Level.INFO, "Using foursquare.com for requests.");
        return new DroidHttpApiV1(domain, clientVersion, useOAuth);
    }

    public static final DroidHttpApiV1 createHttpApi(String clientVersion, boolean useOAuth) {
        return createHttpApi(DROID_API_DOMAIN, clientVersion, useOAuth);
    }

    /**
     * This api is supported in the V1 API documented at:
     * http://groups.google.com/group/foursquare-api/web/api-documentation
     */
    @interface V1 {
    }
}
