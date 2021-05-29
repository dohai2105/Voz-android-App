package com.fun.voznoad.common;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;

public class AppController extends Application {

	Context mContext;

	// volley params
	private static AppController mInstance;
	private RequestQueue mRequestQueue;
    private com.android.volley.toolbox.ImageLoader mImageLoader;

	// voley methos
	public static synchronized AppController getInstance() {
		return mInstance;
	}

	public RequestQueue getRequestQueue() {
		if (mRequestQueue == null) {
			mRequestQueue = Volley.newRequestQueue(getApplicationContext());
		}

		return mRequestQueue;
	}

	public com.android.volley.toolbox.ImageLoader getImageLoader() {
		getRequestQueue();
		if (mImageLoader == null) {
			mImageLoader = new com.android.volley.toolbox.ImageLoader(this.mRequestQueue,new LruBitmapCache());
		}
		return this.mImageLoader;
	}

	public void onCreate() {
		super.onCreate();
		mInstance = this;
		mContext = getApplicationContext();
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				mContext).imageDownloader(new BaseImageDownloader(mContext))
				.build();
		ImageLoader.getInstance().init(config);
	}

	public boolean isNetworkConnect() {
		ConnectivityManager connMgr = (ConnectivityManager) mContext
				.getSystemService(Activity.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected())
			return true;
		else
			return false;
	}
 
}
