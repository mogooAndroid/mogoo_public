package cn.easy.android.sample.types;

public enum Category {

	DEBUTS("debuts"), EVERYONE("everyone"), POPULAR("popular");
	
	private String mDisplayName;

	Category(String displayName) {
		mDisplayName = displayName;
	}

	public String getDisplayName() {
		return mDisplayName;
	}
}
