package com.michelin.droid.types;

public class Credentials implements DroidType {

    private String mOauthToken;
    private String mOauthTokenSecret;

    public Credentials() {
    }

    public String getOauthToken() {
        return mOauthToken;
    }

    public void setOauthToken(String oauthToken) {
        mOauthToken = oauthToken;
    }

    public String getOauthTokenSecret() {
        return mOauthTokenSecret;
    }

    public void setOauthTokenSecret(String oauthTokenSecret) {
        mOauthTokenSecret = oauthTokenSecret;
    }

}
