package net.hcangus.http;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;


public class VolleyHandler {

	public static RequestQueue mRequestQueue;
//	private static ImageLoader mImageLoader;
//	public static int cacheSize;
	
	public static void intializeRequestQueue(Context context) {
		mRequestQueue = Volley.newRequestQueue(context.getApplicationContext());
//		 int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
//          cacheSize = maxMemory / 8;
//         mImageLoader = new ImageLoader(mRequestQueue, new BitmapLruCache(cacheSize));
	}
	
	static RequestQueue getRequestQueue(Context context) {
        if (mRequestQueue == null) {
			intializeRequestQueue(context);
        }
		return mRequestQueue;
	}
	
//	public static ImageLoader getImageLoader() {
//        if (mImageLoader != null) {
//            return mImageLoader;
//        } else {
//            throw new IllegalStateException("ImageLoader null");
//        }
//    }
	
}
