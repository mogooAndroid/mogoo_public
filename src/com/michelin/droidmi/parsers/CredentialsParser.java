package com.michelin.droidmi.parsers;

import java.io.IOException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.michelin.droid.error.DroidError;
import com.michelin.droid.error.DroidParseException;
import com.michelin.droid.parsers.AbstractParser;
import com.michelin.droid.util.EvtLog;
import com.michelin.droidmi.types.Credentials;

public class CredentialsParser extends AbstractParser<Credentials> {
	public static final String TAG = CredentialsParser.class.getSimpleName();

    @Override
    public Credentials parseInner(XmlPullParser parser) throws XmlPullParserException, IOException,
            DroidError, DroidParseException {
        parser.require(XmlPullParser.START_TAG, null, null);

        Credentials credentials = new Credentials();

        while (parser.nextTag() == XmlPullParser.START_TAG) {
            String name = parser.getName();
            if ("oauth_token".equals(name)) {
                credentials.setOauthToken(parser.nextText());

            } else if ("oauth_token_secret".equals(name)) {
                credentials.setOauthTokenSecret(parser.nextText());

            } else {
                // Consume something we don't understand.
				EvtLog.i(CredentialsParser.class, TAG,
						"Found tag that we don't recognize: " + name);
                skipSubTree(parser);
            }
        }
        return credentials;
    }
}