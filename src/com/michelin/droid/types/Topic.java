package com.michelin.droid.types;

import android.os.Parcel;
import android.os.Parcelable;

import com.michelin.droid.util.ParcelUtils;

public class Topic implements DroidType, Parcelable {

    private String mName;
    private String mP;

    public Topic() {
    }

    private Topic(Parcel in) {
    	mName = ParcelUtils.readStringFromParcel(in);
    	mName = ParcelUtils.readStringFromParcel(in);
    }
    
    public static final Topic.Creator<Topic> CREATOR = new Parcelable.Creator<Topic>() {
        public Topic createFromParcel(Parcel in) {
            return new Topic(in);
        }

        @Override
        public Topic[] newArray(int size) {
            return new Topic[size];
        }
    };

    public String getName() {
        return mName;
    }

    public void setName(String name) {
    	mName = name;
    }

    public String getP() {
        return mP;
    }

    public void setP(String p) {
        mP = p;
    }


    @Override
    public void writeToParcel(Parcel out, int flags) {
        ParcelUtils.writeStringToParcel(out, mName);
        ParcelUtils.writeStringToParcel(out, mP);
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
