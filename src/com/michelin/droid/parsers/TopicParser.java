package com.michelin.droid.parsers;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.michelin.droid.Droid;
import com.michelin.droid.error.DroidError;
import com.michelin.droid.error.DroidParseException;
import com.michelin.droid.types.Topic;

public class TopicParser extends AbstractParser<Topic> {
    private static final Logger LOG = Logger.getLogger(TopicParser.class.getCanonicalName());
    private static final boolean DEBUG = Droid.PARSER_DEBUG;

    @Override
    public Topic parseInner(XmlPullParser parser) throws XmlPullParserException, IOException,
            DroidError, DroidParseException {
//        parser.require(XmlPullParser.START_TAG, null, null);

        Topic topic = null;
        int eventType = parser.getEventType();
        while (eventType == XmlPullParser.START_TAG) {
            String name = parser.getName();
            if("rc".equals(name)) {
            	topic = new Topic();
            }else if ("n".equals(name)) {
                topic.setName(parser.nextText());
            } else if ("p".equals(name)) {
                topic.setP(parser.nextText());
            } else {
                // Consume something we don't understand.
                if (DEBUG) LOG.log(Level.FINE, "Found tag that we don't recognize: " + name);
                skipSubTree(parser);
            }
            eventType = parser.nextTag();
        }
        return topic;
    }
}
