package com.michelin.droid.parsers;


import org.xmlpull.v1.XmlPullParser;

import com.michelin.droid.error.DroidError;
import com.michelin.droid.error.DroidParseException;
import com.michelin.droid.types.DroidType;

public interface Parser<T extends DroidType> {

    public abstract T parse(XmlPullParser parser) throws DroidError, DroidParseException;

}
