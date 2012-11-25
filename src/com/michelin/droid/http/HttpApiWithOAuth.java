package com.michelin.droid.http;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.signature.SignatureMethod;

import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.DefaultHttpClient;

import com.michelin.droid.Droid;
import com.michelin.droid.error.DroidCredentialsException;
import com.michelin.droid.error.DroidError;
import com.michelin.droid.error.DroidException;
import com.michelin.droid.error.DroidParseException;
import com.michelin.droid.parsers.Parser;
import com.michelin.droid.types.DroidType;

public class HttpApiWithOAuth extends AbstractHttpApi {
    protected static final Logger LOG = Logger.getLogger(HttpApiWithOAuth.class.getCanonicalName());
    protected static final boolean DEBUG = Droid.DEBUG;

    private OAuthConsumer mConsumer;

    public HttpApiWithOAuth(DefaultHttpClient httpClient, String clientVersion) {
        super(httpClient, clientVersion);
    }

    public DroidType doHttpRequest(HttpRequestBase httpRequest,
            Parser<? extends DroidType> parser) throws DroidCredentialsException,
            DroidParseException, DroidException, IOException {
        if (DEBUG) LOG.log(Level.FINE, "doHttpRequest: " + httpRequest.getURI());
            try {
                if (DEBUG) LOG.log(Level.FINE, "Signing request: " + httpRequest.getURI());
                if (DEBUG) LOG.log(Level.FINE, "Consumer: " + mConsumer.getConsumerKey() + ", "
                        + mConsumer.getConsumerSecret());
                if (DEBUG) LOG.log(Level.FINE, "Token: " + mConsumer.getToken() + ", "
                        + mConsumer.getTokenSecret());
                mConsumer.sign(httpRequest);
            } catch (OAuthMessageSignerException e) {
                if (DEBUG) LOG.log(Level.FINE, "OAuthMessageSignerException", e);
                throw new RuntimeException(e);
            } catch (OAuthExpectationFailedException e) {
                if (DEBUG) LOG.log(Level.FINE, "OAuthExpectationFailedException", e);
                throw new RuntimeException(e);
            }
        return executeHttpRequest(httpRequest, parser);
    }

    public String doHttpPost(String url, NameValuePair... nameValuePairs) throws DroidError,
            DroidParseException, IOException, DroidCredentialsException {
        throw new RuntimeException("Haven't written this method yet.");
    }

    public void setOAuthConsumerCredentials(String key, String secret) {
        mConsumer = new CommonsHttpOAuthConsumer(key, secret, SignatureMethod.HMAC_SHA1);
    }

    public void setOAuthTokenWithSecret(String token, String tokenSecret) {
        verifyConsumer();
        if (token == null && tokenSecret == null) {
            if (DEBUG) LOG.log(Level.FINE, "Resetting consumer due to null token/secret.");
            String consumerKey = mConsumer.getConsumerKey();
            String consumerSecret = mConsumer.getConsumerSecret();
            mConsumer = new CommonsHttpOAuthConsumer(consumerKey, consumerSecret,
                    SignatureMethod.HMAC_SHA1);
        } else {
            mConsumer.setTokenWithSecret(token, tokenSecret);
        }
    }

    public boolean hasOAuthTokenWithSecret() {
        verifyConsumer();
        return (mConsumer.getToken() != null) && (mConsumer.getTokenSecret() != null);
    }

    private void verifyConsumer() {
        if (mConsumer == null) {
            throw new IllegalStateException(
                    "Cannot call method without setting consumer credentials.");
        }
    }
}
