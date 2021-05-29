package com.fun.voznoad.servicecontroller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class HttpUrlJSONParser {
	private String jsonContent;
	private String cookie ;
	private Boolean allowSaveCookie = true;
	private String quoteURL;
	
	public HttpUrlJSONParser() {
		super();
		cookie="";
	}
	public Boolean getAllowSaveCookie() {
		return allowSaveCookie;
	}



	public void setAllowSaveCookie(Boolean allowSaveCookie) {
		this.allowSaveCookie = allowSaveCookie;
	}



	public String makeHttpRequest(String urlPath , String method , List<NameValuePair> data ) throws IOException{
		URL url = null;
		String params = URLEncodedUtils.format(data, "utf-8");
		if (method.equalsIgnoreCase("get")) {
			url = new URL(urlPath+ "?" + params)  ;
		}else {
			url = new URL(urlPath) ;
		}
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setReadTimeout(10000);
		conn.setConnectTimeout(15000);
		conn.setDoInput(true);
		conn.setDoOutput(true);
		if (!cookie.equals("")) {
			conn.setRequestProperty("Cookie", cookie.substring(1, cookie.length()-1).trim());
		}
		if (method.equalsIgnoreCase("get")) {
			conn.setRequestMethod("GET");
		}else {
			conn.setRequestMethod("POST");
		}
		
		OutputStream out = conn.getOutputStream();
		out.write(params.getBytes());
 		if (conn.getResponseCode()==200) {
			InputStream is = conn.getInputStream();
			quoteURL = conn.getURL().toString();
			BufferedReader reader = new BufferedReader(new InputStreamReader(is, "utf-8"), 8);
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			is.close();
			jsonContent = sb.toString();
		}
 
//
//		HttpEntity httpEntity = null;
//		HttpResponse httpResp = null;
//		HttpParams httpParameters = new BasicHttpParams();
//		int timeout1 = 1000 * 8;
//		int timeout2 = 1000 * 8;
//		HttpConnectionParams.setConnectionTimeout(httpParameters, timeout1);
//		HttpConnectionParams.setSoTimeout(httpParameters, timeout2);
//
//		HttpClient httpClient = new DefaultHttpClient(httpParameters);
//		HttpPost httpPost = new HttpPost(urlPath);
//		httpPost.setEntity(new UrlEncodedFormEntity(data));
//
//		httpResp = httpClient.execute(httpPost);
//		Header[] headers = httpResp.getHeaders("Set-Cookie");
//		httpEntity = httpResp.getEntity();
//		jsonContent = EntityUtils.toString(httpEntity);
		return jsonContent;
	}
	
	public String getQuoteURL(){
		return quoteURL;
	}
	
	public String getCookie() {
		return cookie;
	}
	
	public void setCookie(String cookie){
		this.cookie = cookie;
	}
	
	public JSONArray getJsonArray(String json) throws JSONException, IOException{
		JSONArray jsonArray = new JSONArray(json);
		return jsonArray;
	}
	
	public JSONObject getJsonObject(String jsonContent) throws JSONException, IOException{
		JSONObject jsonObject =  new JSONObject(jsonContent);
		return jsonObject;
	}
	
	public String postForm(String url, String method,List<NameValuePair> params) throws JSONException, IOException{
		String response = makeHttpRequest(url, method, params);
		return response;
	}
}
