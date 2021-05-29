package com.example.squareImage;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.text.Html.ImageGetter;
import android.util.Log;
import android.widget.TextView;

import com.fun.voznoad.common.Constant;

public class URLImageParser {
//implements ImageGetter {
//    Context c;
//    TextView container;
//
//    /***
//     * Construct the URLImageParser which will execute AsyncTask and refresh the container
//     * @param t
//     * @param c
//     */
//    public URLImageParser(TextView t, Context c) {
//        this.c = c;
//        this.container = t;
//    }
//    
//    public Drawable getDrawable(String source) {
//        URLDrawable urlDrawable = new URLDrawable();
//        if (!source.contains("http")) {
//        	Resources resources = c.getResources();
//	        int identifier = resources.getIdentifier("sweat", "drawable", c.getPackageName());
//	        Drawable res = resources.getDrawable(identifier);
//	        res.setBounds(0, 0, res.getIntrinsicWidth(), res.getIntrinsicHeight());
//	        return res;
//		}
//        // get the actual source
//        ImageGetterAsyncTask asyncTask = new ImageGetterAsyncTask( urlDrawable);
//
//        asyncTask.execute(source);
//
//        // return reference to URLDrawable where I will change with actual image from
//        // the src tag
//        return urlDrawable;
//    }
//
//    public class ImageGetterAsyncTask extends AsyncTask<String, Void, Drawable>  {
//        URLDrawable urlDrawable;
//        String url ;
//        public ImageGetterAsyncTask(URLDrawable d) {
//            this.urlDrawable = d;
//        }
//
//        @Override
//        protected Drawable doInBackground(String... params) {
//            String source = params[0];
//            url= source;
//            return fetchDrawable(source);
//        }
//
//        @Override
//        protected void onPostExecute(Drawable result) {
//            // set the correct bound according to the result from HTTP call
////            urlDrawable.setBounds(0, 0, 0 + result.getIntrinsicWidth(), 0 
////                    + result.getIntrinsicHeight()); 
//        	try{
//        		 urlDrawable.setBounds(0, 0, 0 + Constant.screen_width, 0 
//                         + result.getIntrinsicHeight()*(Constant.screen_width/result.getIntrinsicWidth())); 
//                 // change the reference of the current drawable to the result
//                 // from the HTTP call
//                 urlDrawable.drawable = result;
//
//                 // redraw the image by invalidating the container
//                 URLImageParser.this.container.invalidate();
//                 Log.d("hieght", result.getIntrinsicHeight()+"");
//                 // For ICS
//                 URLImageParser.this.container.setHeight((URLImageParser.this.container.getHeight() 
//                 +result.getIntrinsicHeight()*(Constant.screen_width/result.getIntrinsicWidth())));
//
//                 // Pre ICS
//                 URLImageParser.this.container.setEllipsize(null);
//        	}catch (Exception e){
//        		Log.d("onPostExecuteError", url);
//        	}
//            
//        }
//        /***
//         * Get the Drawable from URL
//         * @param urlString
//         * @return
//         */
//        public Drawable fetchDrawable(String urlString) {
//            try {
//                //InputStream is = fetch(urlString);
//                ImageLoader imageLoader = new ImageLoader(c);
//        		Bitmap loadedImage = imageLoader.getBitmap(urlString);
//                
//                //Drawable drawable = Drawable.createFromStream(is, "src");
//        		Drawable drawable= new BitmapDrawable(c.getResources(),loadedImage);
//                Log.d("width", Constant.screen_width+"");
//                drawable.setBounds(0, 0, 0 + Constant.screen_width, 0 
//                        + drawable.getIntrinsicHeight()*(Constant.screen_width/drawable.getIntrinsicWidth())); 
//                Log.d("hieght1", drawable.getIntrinsicHeight()+"");
//                return drawable;
//            } catch (Exception e) {
//                return null;
//            } 
//        }
//
////		private InputStream fetch(String urlString)throws MalformedURLException, IOException {
////			HttpURLConnection conn = (HttpURLConnection) new URL(urlString).openConnection();
////			conn.setReadTimeout(10000);
////			conn.setConnectTimeout(15000);
////			SharedPreferences sharedPreferences = c.getSharedPreferences("voz_data", c.MODE_PRIVATE);
////			String cookie = sharedPreferences.getString("cookie_data", "").toString();
////			conn.setRequestProperty("Cookie",cookie.toString().substring(1, cookie.length() - 1).trim());
////			if (conn.getResponseCode() == 200)
////				return conn.getInputStream();
////			else
////				return null;
////		}
//	}
}