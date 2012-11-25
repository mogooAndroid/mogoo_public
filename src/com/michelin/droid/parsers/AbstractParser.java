/**
 * Copyright 2009 Joe LaPenna
 */

package com.michelin.droid.parsers;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import com.michelin.droid.Droid;
import com.michelin.droid.error.DroidError;
import com.michelin.droid.error.DroidParseException;
import com.michelin.droid.types.DroidType;

public abstract class AbstractParser<T extends DroidType> implements Parser<T> {
    private static final Logger LOG = Logger.getLogger(AbstractParser.class.getCanonicalName());
    private static final boolean DEBUG = Droid.PARSER_DEBUG;

    private static XmlPullParserFactory sFactory;
    static {
        try {
            sFactory = XmlPullParserFactory.newInstance();
        } catch (XmlPullParserException e) {
            throw new IllegalStateException("Could not create a factory");
        }
    }

    abstract protected T parseInner(final XmlPullParser parser) throws IOException,
            XmlPullParserException,DroidError, DroidParseException;

    /*
     * (non-Javadoc)
     * @see com.joelapenna.foursquare.parsers.Parser#parse(java.io.InputStream)
     */
    public final T parse(XmlPullParser parser) throws DroidParseException, DroidError {
        try {
            if (parser.getEventType() == XmlPullParser.START_DOCUMENT) {
                parser.nextTag();
                if (parser.getName().equals("error")) {
                    throw new DroidError(parser.nextText());
                }
            }
            return parseInner(parser);
        } catch (IOException e) {
            if (DEBUG) LOG.log(Level.FINE, "IOException", e);
            throw new DroidParseException(e.getMessage());
        } catch (XmlPullParserException e) {
            if (DEBUG) LOG.log(Level.FINE, "XmlPullParserException", e);
            throw new DroidParseException(e.getMessage());
        }
    }

    public static final XmlPullParser createXmlPullParser(InputStream is) {
        XmlPullParser parser;
        try {
            parser = sFactory.newPullParser();
            if (DEBUG) {
                StringBuffer sb = new StringBuffer();
                if (DEBUG) {
                    while (true) {
                        final int ch = is.read();
                        if (ch < 0) {
                            break;
                        } else {
                            sb.append((char)ch);
                        }
                    }
                    is.close();
                    LOG.log(Level.FINE, sb.toString());
                }
                parser.setInput(new StringReader(sb.toString()));
            } else {
                parser.setInput(is, null);
            }
        } catch (XmlPullParserException e) {
            throw new IllegalArgumentException();
        } catch (IOException e) {
            throw new IllegalArgumentException();
        }
        return parser;
    }

    public static void skipSubTree(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, null, null);
        int level = 1;
        while (level > 0) {
            int eventType = parser.next();
            if (eventType == XmlPullParser.END_TAG) {
                --level;
            } else if (eventType == XmlPullParser.START_TAG) {
                ++level;
            }
        }
    }

}
