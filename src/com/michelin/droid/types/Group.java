package com.michelin.droid.types;

import java.util.ArrayList;

public class Group<T extends DroidType> extends ArrayList<T> implements DroidType {

    private static final long serialVersionUID = 1L;

    private String mType;

    public void setType(String type) {
        mType = type;
    }

    public String getType() {
        return mType;
    }
}
