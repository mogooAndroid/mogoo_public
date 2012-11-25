package com.michelin.droid.parsers;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.michelin.droid.Droid;
import com.michelin.droid.error.DroidError;
import com.michelin.droid.error.DroidParseException;
import com.michelin.droid.types.DroidType;
import com.michelin.droid.types.Group;

@SuppressWarnings("rawtypes")
public class GroupParser extends AbstractParser<Group> {
    private static final Logger LOG = Logger.getLogger(GroupParser.class.getCanonicalName());
    private static final boolean DEBUG = Droid.PARSER_DEBUG;

    private Parser<? extends DroidType> mSubParser;

    public GroupParser(Parser<? extends DroidType> subParser) {
        this.mSubParser = subParser;
    }

    @Override
    public Group<DroidType> parseInner(XmlPullParser parser) throws XmlPullParserException, IOException,
            DroidParseException, DroidError {

        Group<DroidType> group = new Group<DroidType>();
        group.setType(parser.getAttributeValue(null, "type"));

        while (parser.nextTag() == XmlPullParser.START_TAG) {
            DroidType item = this.mSubParser.parse(parser);
            if(item != null) {
            	if (DEBUG) LOG.log(Level.FINE, "adding item: " + item);
                group.add(item);
            }
        }
        return group;
    }
}
