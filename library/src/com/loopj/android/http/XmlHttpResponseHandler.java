package com.loopj.android.http;

import java.io.ByteArrayInputStream;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.michelin.droid.error.DroidError;
import com.michelin.droid.error.DroidParseException;
import com.michelin.droid.parsers.AbstractParser;
import com.michelin.droid.parsers.Parser;
import com.michelin.droid.types.DroidType;

import android.os.Message;

public class XmlHttpResponseHandler extends AsyncHttpResponseHandler {
    protected static final int SUCCESS_XML_MESSAGE = 200;
    Parser<? extends DroidType> mParser = null;
    
    public XmlHttpResponseHandler(Parser<? extends DroidType> parser) {
    	mParser = parser;
    }

    public void onSuccess(DroidType response) {
    	
    }

    public void onSuccess(int statusCode, DroidType response) {
    	onSuccess(response);
    }
    
    public void onFailure(Throwable e, DroidType errorResponse) {}

    //
    // Pre-processing of messages (executes in background threadpool thread)
    //
    @Override
    protected void sendSuccessMessage(int statusCode, String responseBody) {
        try {
            Object jsonResponse = parseResponse(responseBody);
            sendMessage(obtainMessage(SUCCESS_XML_MESSAGE, new Object[]{statusCode, jsonResponse}));
        } catch(Exception e) {
            sendFailureMessage(e, responseBody);
        }
    }


    //
    // Pre-processing of messages (in original calling thread, typically the UI thread)
    //

    @Override
    protected void handleMessage(Message msg) {
        switch(msg.what){
            case SUCCESS_XML_MESSAGE:
                Object[] response = (Object[]) msg.obj;
                handleSuccessXmlMessage(((Integer) response[0]).intValue(), response[1]);
                break;
            default:
                super.handleMessage(msg);
        }
    }

    protected void handleSuccessXmlMessage(int statusCode, Object xmlResponse) {
        if(xmlResponse instanceof DroidType) {
            onSuccess(statusCode, (DroidType) xmlResponse);
        }else {
			onFailure(new DroidParseException("Unexpected type "
					+ xmlResponse.getClass().getName()), (DroidType) null);
        }
    }

    protected Object parseResponse(String responseBody) throws DroidError, DroidParseException {
    	if(mParser == null) {
    		throw new DroidParseException("Parser is null.");
    	}
    	ByteArrayInputStream is = new ByteArrayInputStream(responseBody.getBytes());
    	return mParser.parse(AbstractParser.createXmlPullParser(is));
    }

    @Override
    protected void handleFailureMessage(Throwable e, String responseBody) {
        if (responseBody != null) try {
            Object xmlResponse = parseResponse(responseBody);
            if(xmlResponse instanceof DroidType) {
                onFailure(e, (DroidType)xmlResponse);
            }
        }
        catch(DroidParseException ex) {
            onFailure(e, responseBody);
        } 
        catch (DroidError droidError) {
        	onFailure(droidError, "");
		}
        else {
            onFailure(e, "");
        }
    }
}
