package com.michelin.droid.http;

import java.io.IOException;

import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;

import com.michelin.droid.error.DroidCredentialsException;
import com.michelin.droid.error.DroidException;
import com.michelin.droid.error.DroidParseException;
import com.michelin.droid.parsers.Parser;
import com.michelin.droid.types.DroidType;

public interface HttpApi {

    abstract public DroidType doHttpRequest(HttpRequestBase httpRequest,
            Parser<? extends DroidType> parser) throws DroidCredentialsException,
            DroidParseException, DroidException, IOException;

    abstract public String doHttpPost(String url, NameValuePair... nameValuePairs)
            throws DroidCredentialsException, DroidParseException, DroidException,
            IOException;

    abstract public HttpGet createHttpGet(String url, NameValuePair... nameValuePairs);

    abstract public HttpPost createHttpPost(String url, NameValuePair... nameValuePairs);
}
