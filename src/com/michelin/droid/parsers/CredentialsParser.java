package com.michelin.droid.parsers;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.michelin.droid.Droid;
import com.michelin.droid.error.DroidError;
import com.michelin.droid.error.DroidParseException;
import com.michelin.droid.types.Credentials;

public class CredentialsParser extends AbstractParser<Credentials> {
    private static final Logger LOG = Logger.getLogger(CredentialsParser.class.getCanonicalName());
    private static final boolean DEBUG = Droid.PARSER_DEBUG;

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
                if (DEBUG) LOG.log(Level.FINE, "Found tag that we don't recognize: " + name);
                skipSubTree(parser);
            }
        }
        return credentials;
    }
}
