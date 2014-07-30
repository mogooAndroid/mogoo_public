package cn.easy.android.sample.app;import android.graphics.Bitmap;import com.nostra13.universalimageloader.cache.disc.naming.FileNameGenerator;import com.nostra13.universalimageloader.cache.memory.impl.LRULimitedMemoryCache;import com.nostra13.universalimageloader.core.DisplayImageOptions;import com.nostra13.universalimageloader.core.ImageLoader;import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;import com.nostra13.universalimageloader.core.assist.ImageScaleType;import cn.easy.android.library.app.AbsApplication;import cn.easy.android.library.app.BaseHttpProxy;import cn.easy.android.sample.data.ConstantSet;public class EasySampleApplication extends AbsApplication {	private BaseHttpProxy mHttpProxy;	private ImageLoader mImageLoader;	@Override	public BaseHttpProxy getHttpProxy() {		if (mHttpProxy == null) {			mHttpProxy = new EasySampleHttpProxy(new EasySampleHttpApi(this));		}		return mHttpProxy;	}	@Override	protected void init() {		super.init();		ConstantSet.init(mContext);		loadImageLoader();	}	public ImageLoader getImageLoader() {		return mImageLoader;	}	/**	 * 初始化ImageLoader	 */	private void loadImageLoader() {		DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()				.cacheInMemory(true).cacheOnDisc(true)				.imageScaleType(ImageScaleType.EXACTLY)				.bitmapConfig(Bitmap.Config.RGB_565).build();		ImageLoaderConfiguration.Builder builder = new ImageLoaderConfiguration.Builder(				getBaseContext()).defaultDisplayImageOptions(defaultOptions)				.denyCacheImageMultipleSizesInMemory()				.discCacheFileNameGenerator(new FileNameGenerator() {					@Override					public String generate(String imageUri) {						int start = imageUri.lastIndexOf('/');						return imageUri.substring(start + 1, imageUri.length());					}				}).memoryCache(new LRULimitedMemoryCache(200000));		ImageLoaderConfiguration config = builder.build();		mImageLoader = ImageLoader.getInstance();		mImageLoader.init(config);	}}