package com.michelin.droidmi.parsers;

import java.io.IOException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.michelin.droid.error.DroidError;
import com.michelin.droid.error.DroidParseException;
import com.michelin.droid.parsers.AbstractParser;
import com.michelin.droid.util.EvtLog;
import com.michelin.droidmi.types.Topic;

public class TopicParser extends AbstractParser<Topic> {
	private static final String TAG = TopicParser.class.getSimpleName();

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
				EvtLog.i(TopicParser.class, TAG,
						"Found tag that we don't recognize: " + name);
                skipSubTree(parser);
            }
            eventType = parser.nextTag();
        }
        return topic;
    }
}
