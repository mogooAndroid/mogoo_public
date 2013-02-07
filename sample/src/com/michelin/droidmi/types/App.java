package com.michelin.droidmi.types;

import android.os.Parcel;
import android.os.Parcelable;

import com.michelin.droid.types.DroidType;
import com.michelin.droid.util.ParcelUtils;

public class App implements DroidType, Parcelable {

	private int mId = -1;
    private String mName = "";
    private String mVersionName = "";
    private int mVersionCode = -1;
    private int mSize = -1;
    private String mPackageName = "";
    private float mVirtualScore = -1.0f;
    private float mRealScore = -1.0f;
    private float mPrice = -1.0f;
    private String mAuthor = "";
    private String mApkUrl = "";
    private String mIconUrl = "";
    
    public App() {
    }

    private App(Parcel in) {
    	mName = ParcelUtils.readStringFromParcel(in);
    }
    
    public static final Topic.Creator<App> CREATOR = new Parcelable.Creator<App>() {
        public App createFromParcel(Parcel in) {
            return new App(in);
        }

        @Override
        public App[] newArray(int size) {
            return new App[size];
        }
    };

    public int getId() {
		return mId;
	}

	public void setId(String id) {
		try {
			this.mId = Integer.valueOf(id);
		} catch (Exception e) {
			this.mId = -1;
		}
	}

	public String getName() {
		return mName;
	}

	public void setName(String name) {
		this.mName = name;
	}

	public String getVersionName() {
		return mVersionName;
	}

	public void setVersionName(String versionName) {
		this.mVersionName = versionName;
	}

	public int getVersionCode() {
		return mVersionCode;
	}

	public void setVersionCode(String versionCode) {
		try {
			this.mVersionCode = Integer.valueOf(versionCode);
		} catch (Exception e) {
			this.mVersionCode = -1;
		}
	}

	public int getSize() {
		return mSize;
	}

	public void setSize(String size) {
		try {
			this.mSize = Integer.valueOf(size);
		} catch (Exception e) {
			this.mSize = 0;
		}
	}

	public String getPackageName() {
		return mPackageName;
	}

	public void setPackageName(String packageName) {
		this.mPackageName = packageName;
	}

	public float getVirtualScore() {
		return mVirtualScore;
	}

	public void setVirtualScore(String virtualScore) {
		try {
			this.mVirtualScore = Float.valueOf(virtualScore);
		} catch (Exception e) {
			this.mVirtualScore = 0.0f;
		}
	}

	public float getRealScore() {
		return mRealScore;
	}

	public void setRealScore(String realScore) {
		try {
			this.mRealScore = Float.valueOf(realScore);
		} catch (Exception e) {
			this.mRealScore = -1.0f;
		}
	}

	public float getPrice() {
		return mPrice;
	}

	public void setPrice(String price) {
		try {
			this.mPrice = Float.valueOf(price);
		} catch (Exception e) {
			this.mPrice = -1.0f;
		}
	}

	public String getAuthor() {
		return mAuthor;
	}

	public void setAuthor(String author) {
		this.mAuthor = author;
	}

	public String getApkUrl() {
		return mApkUrl;
	}

	public void setApkUrl(String apkUrl) {
		this.mApkUrl = apkUrl;
	}

	public String getIconUrl() {
		return mIconUrl;
	}

	public void setIconUrl(String iconUrl) {
		this.mIconUrl = iconUrl;
	}

	@Override
    public void writeToParcel(Parcel out, int flags) {
		ParcelUtils.writeStringToParcel(out, String.valueOf(mId));
		ParcelUtils.writeStringToParcel(out, mName);
        ParcelUtils.writeStringToParcel(out, mVersionName);
        ParcelUtils.writeStringToParcel(out, String.valueOf(mVersionCode));
        ParcelUtils.writeStringToParcel(out, String.valueOf(mSize));
        ParcelUtils.writeStringToParcel(out, mPackageName);
        ParcelUtils.writeStringToParcel(out, String.valueOf(mVirtualScore));
        ParcelUtils.writeStringToParcel(out, String.valueOf(mRealScore));
        ParcelUtils.writeStringToParcel(out, String.valueOf(mPrice));
        ParcelUtils.writeStringToParcel(out, mAuthor);
        ParcelUtils.writeStringToParcel(out, mApkUrl);
        ParcelUtils.writeStringToParcel(out, mIconUrl);
    }

    @Override
    public int describeContents() {
        return 0;
    }
    
    @Override
    public String toString() {
		return "Apk[id=" + String.valueOf(mId) + 
				", name=" + String.valueOf(mName) +
				", versionName=" + String.valueOf(mVersionName) +
				", versionCode=" + String.valueOf(mVersionCode) +
				", size=" + String.valueOf(mSize) +
				", packageName=" + String.valueOf(mPackageName) +
				", virtualScore=" + String.valueOf(mVirtualScore) +
				", realScore=" + String.valueOf(mRealScore) +
				", price=" + String.valueOf(mPrice) +
				", author=" + String.valueOf(mAuthor) +
				", apkUrl=" + String.valueOf(mApkUrl) +
				", iconUrl=" + String.valueOf(mIconUrl) +"]";
    }
}