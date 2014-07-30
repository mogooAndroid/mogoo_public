package cn.easy.android.sample.net.communication.types;

public class Shot {

	static final String TAG = "Shot";

	private String image_url = "";

	private String image_teaser_url = "";

	private String  title = "";
	
	public String getImageUrl() {
		return image_url;
	}

	public Shot setImageUrl(String imageUrl) {
		this.image_url = imageUrl;
		return this;
	}

	public String getImageTeaserUrl() {
		return image_teaser_url;
	}

	public Shot setImageTeaserUrl(String imageTeaserUrl) {
		this.image_teaser_url = imageTeaserUrl;
		return this;
	}
	
	public String getTitle() {
		return title;
	}

	public Shot setTitle(String title) {
		this.title = title;
		return this;
	}

	@Override
	public String toString() {
		return "Shot [image_url=" + image_url + ", image_teaser_url="
				+ image_teaser_url + ", title=" + title + "]";
	}
}
