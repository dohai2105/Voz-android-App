package com.fun.voznoad.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.fun.voznoad.model.Icon;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.WindowManager;

public class Constant {
	public static int screen_width;
	public static int screen_height;
	public static int timeout =10000;
	public static ArrayList<String> menuTitle = new ArrayList<String>(Arrays.asList("�?ăng nhập","Trang chủ","Yêu thích","Tới nhanh","Bài mới","Danh sách quote","Hòm thư","Cài đặt","Thoát" ));
	public static Map <String, String> getCookies(String _cookies){
		String []split = _cookies.substring(1, _cookies.length()-1).split(";");
		Map<String, String> loginCookies =new HashMap<String, String>();
		for(int i = 0 ;i<split.length;i++){
			String tmp_split = split[i];
			String []cookies = tmp_split.split("=");
			loginCookies.put(cookies[0], cookies[1]);
		}
		return loginCookies;
	}
	
	public static String quoteListURL="";
	
	public static String sign = "";
	
 
	
	public static ArrayList<String> mutilquoteCookieArr = new ArrayList<String>();
	
	public static final String convertToMd5(final String s) {
	    try {
	        // Create MD5 Hash
	        MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
	        digest.update(s.getBytes());
	        byte messageDigest[] = digest.digest();

	        // Create Hex String
	        StringBuffer hexString = new StringBuffer();
	        for (int i = 0; i < messageDigest.length; i++) {
	            String h = Integer.toHexString(0xFF & messageDigest[i]);
	            while (h.length() < 2)
	                h = "0" + h;
	            hexString.append(h);
	        }
	        return hexString.toString();

	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return "";
	}
	
	public static HashMap<String, String> MapIcon (){
		HashMap<String, String> MapIcon = new HashMap<String, String>();
		MapIcon.put("surrender", ":surrender:");
		return null;
	}
	

	 public static int convertDpToPx(Context context, int i)
	    {
	        Display display = ((WindowManager)context.getSystemService("window")).getDefaultDisplay();
	        DisplayMetrics displaymetrics = new DisplayMetrics();
	        display.getMetrics(displaymetrics);
	        return Math.round(TypedValue.applyDimension(1, i, displaymetrics));
	    }

	    public static int convertPxToDp(Context context, int i)
	    {
	        Display display = ((WindowManager)context.getSystemService("window")).getDefaultDisplay();
	        DisplayMetrics displaymetrics = new DisplayMetrics();
	        display.getMetrics(displaymetrics);
	        float f = displaymetrics.density;
	        return Math.round((float)i / f);
	    }

	    public static float pixelsToSp(Context context, Float float1)
	    {
	        float f = context.getResources().getDisplayMetrics().scaledDensity;
	        return float1.floatValue() / f;
	    }

	    public static float spTopixels(Context context, Float float1)
	    {
	        return TypedValue.applyDimension(2, float1.floatValue(), context.getResources().getDisplayMetrics());
	    }
	    
	    public static ArrayList<Icon> getListIcon(Context mContext) throws Exception{
	    	InputStream inputstream = mContext.getAssets().open("keymap.txt");
			BufferedReader reader = new BufferedReader(new InputStreamReader(inputstream, "utf-8"), 8);
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			inputstream.close();
			String data = sb.toString().substring(0, sb.toString().length()-1);
			ArrayList<Icon> iconList = new ArrayList<Icon>();
			String []dataArray = data.split("--");
			for(int i = 0 ; i<dataArray.length;i++){
				String []icon = dataArray[i].split(" ");
				Icon icon2 = new Icon(icon[0], icon[1]);
				iconList.add(icon2);
			}
			return iconList;
	    }
}
