package com.fun.voznoad.adapter;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fun.voznoad.R;
import com.fun.voznoad.ViewPagerActivity;
import com.fun.voznoad.WebViewActivity;
import com.fun.voznoad.R.id;
import com.fun.voznoad.adapter.Page2ListAdp.ViewHolder;
import com.fun.voznoad.callback.OnNotifyViewPagerActivity;
import com.fun.voznoad.common.Constant;
import com.fun.voznoad.model.DialogItem;
import com.fun.voznoad.model.Page3Item;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;

public class Page3ListAdp extends BaseAdapter{
	public LayoutInflater layoutInflater;
	public ArrayList<Page3Item> threadItems;
	public Context mContext;
	public ViewHolder mHolder ;
	public boolean isdoubleClick ;
	private SharedPreferences sharedPreferences;
	private OnNotifyViewPagerActivity notifyOnNotifyViewPagerActivity;
	
	public Page3ListAdp(ArrayList<Page3Item> threadItems, Context mContext,OnNotifyViewPagerActivity notifyOnNotifyViewPagerActivity) {
		super();
		this.threadItems = threadItems;
		this.mContext = mContext;
		layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		isdoubleClick = false;
		sharedPreferences = mContext.getSharedPreferences("voz_data", mContext.MODE_PRIVATE);
		this.notifyOnNotifyViewPagerActivity = notifyOnNotifyViewPagerActivity;
	}
	
	@Override
	public int getCount() {
		return threadItems.size();
	}
	@Override
	public Object getItem(int arg0) {
		return null;
	}
	@Override
	public long getItemId(int arg0) {
		return arg0;
	}
	@SuppressLint("SetJavaScriptEnabled") @Override
	public View getView(int position, View convertview, ViewGroup viewGroup) {
		View view = convertview;
		if (view == null) {
			view = layoutInflater.inflate(R.layout.adapter_page3, viewGroup, false);
			mHolder = new ViewHolder(view);
			view.setTag(mHolder);
		} else {
			mHolder = (ViewHolder) view.getTag();
		}
		initUI(position);
		initCONTROL(position);
		return view;
	}
	
	public void initDialog(){
		final Dialog dialog = new Dialog(mContext);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		dialog.setContentView(R.layout.diaglog_menu);
		ArrayList<DialogItem> dialogItemLst = new ArrayList<DialogItem>();
		ListView dialogLv = (ListView) dialog.findViewById(R.id.dialogLv);
		AdapterDialog dialogAdp = new AdapterDialog(dialogItemLst, mContext);
		dialogLv.setAdapter(dialogAdp);
		dialog.show();
	}
	
	public void initUI(int postion){
		mHolder.numberTv.setText("#"+threadItems.get(postion).getNumber());
		mHolder.typeTv.setText(threadItems.get(postion).getType());
		mHolder.timeTv.setText(threadItems.get(postion).getTime());
		mHolder.nameTv.setText(threadItems.get(postion).getName());
		mHolder.postTv.setText(threadItems.get(postion).getPost());
		String data = threadItems.get(postion).getData();
		Document doc = Jsoup.parse(data);
		Elements imgElement = doc.select("img");
		for(int i = 0 ;i<imgElement.size();i++){
			Element img_element = imgElement.get(i);
			String url =  img_element.attr("src");
			if (!url.contains("images")) {
				img_element.attr("onClick", "showAndroidToast(this.src)");
				img_element.attr("style"," width: 100%!important");
			}
		}
		Log.d("dohai", postion+"");
	    ImageLoader imgLoader = ImageLoader.getInstance();
        if (!threadItems.get(postion).getAvarta().equals(""))  imgLoader.displayImage(threadItems.get(postion).getAvarta(),mHolder.avartaTv,getObtion());
        else mHolder.avartaTv.setImageResource(R.drawable.brick);
		data ="<style>table tbody tr td {background: #E1E4F2}</style>"+doc.html()+"<script type=\"text/javascript\">\r\n function showAndroidToast(toast) {\r\n    Android.showToast(toast);\r\n }\r\n</script>";
        mHolder.detailContainer.getSettings().setJavaScriptEnabled(true);
        mHolder.detailContainer.getSettings().setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN );
        mHolder.detailContainer.addJavascriptInterface( new WebAppInterface(mContext), "Android");
 	    mHolder.detailContainer.loadDataWithBaseURL("http://vozforums.com/", data, "text/html", "UTF-8", null);
	}
	
	public void initCONTROL(final int postion){
		mHolder.containerItem.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (isdoubleClick) {
					initDialog();
				}
				isdoubleClick = true;
			}
		});
		
		mHolder.detailContainer.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				Log.d("url", url);
				if (url.contains("redirect")) {
					url = url.substring(url.indexOf("link=")+5,url.length());
				}
				if (url.contains("vozforums")&&!url.contains("redirect")) {
					 Intent intent = new Intent(mContext, WebViewActivity.class);
					 intent.putExtra("url", url);
					 intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
					 mContext.startActivity(intent);
					 return true;
				}
				try {
					url = URLDecoder.decode(url, "UTF-8") ;
					Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
					mContext.startActivity(intent);
					return true;
				} catch (Exception e) {
					e.printStackTrace();
				}
				return true;
			}
		});

		mHolder.detailContainer.setOnTouchListener(new View.OnTouchListener() {
			// The definition of your actual onClick-type method
			public boolean onTouch(View v, MotionEvent event) {
				// Switching on what type of touch it was
				switch (event.getAction()) {
				// ACTION_UP and ACTION_DOWN together make up a click
				// We're handling both to make sure we grab it
				case MotionEvent.ACTION_DOWN: break;
				case MotionEvent.ACTION_UP: {
					Log.d("dohai", isdoubleClick + "run");

					if (isdoubleClick) {
						initDialog();
					}
					isdoubleClick = true;
				}
				 	break;
				}
				return false;
			}
		});
		
		mHolder.quoteTv.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				new GetQuoteCommentTask().execute(threadItems.get(postion).getPostid()+"");
			}
		});
	}
	
	public class WebAppInterface {
	    Context mContext;
	    /** Instantiate the interface and set the context */
	    WebAppInterface(Context c) {
	        mContext = c;
	    }
	    /** Show a toast from the web page */
	    public void showToast(String toast) {
	        Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show();
	    }
	}
	
	public class ViewHolder {
		TextView numberTv;
		TextView typeTv;
		TextView timeTv;
		ImageView avartaTv;
		TextView nameTv;
		TextView postTv;
		WebView detailContainer;
		TextView quoteTv;
		LinearLayout containerItem;
		public ViewHolder(View v) {
			numberTv = (TextView) v.findViewById(R.id.numberTv);
			typeTv = (TextView) v.findViewById(R.id.typeTv);
			timeTv = (TextView) v.findViewById(R.id.timeTv);
			avartaTv = (ImageView) v.findViewById(R.id.avartaImg);
			nameTv = (TextView) v.findViewById(R.id.nameTv);
			postTv = (TextView) v.findViewById(R.id.postTv);
			detailContainer = (WebView) v.findViewById(R.id.detailContainer);
			containerItem=(LinearLayout) v.findViewById(R.id.containerItem);
			quoteTv = (TextView) v.findViewById(R.id.quoteTv);
		}
	}
	
	private DisplayImageOptions getObtion() {
		DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisc(true).build();
		return options;
	}
	
	public class MyBaseImageDownloader extends BaseImageDownloader{
		
		public MyBaseImageDownloader(Context context) {
			super(context);
		}

		protected HttpURLConnection createConnection(String url, Object extra) throws IOException {
			String encodedUrl = Uri.encode(url, ALLOWED_URI_CHARS);
			HttpURLConnection conn = (HttpURLConnection) new URL(encodedUrl).openConnection();
			conn.setConnectTimeout(connectTimeout);
			conn.setReadTimeout(readTimeout);
			SharedPreferences sharedPreferences = mContext.getSharedPreferences("voz_data", mContext.MODE_PRIVATE);
			String cookie = sharedPreferences.getString("cookie_data", "").toString();
			conn.setRequestProperty("Cookie", cookie.toString().substring(1,cookie.length()-1).trim());
			return conn;
		}
	}
    
	public class GetQuoteCommentTask extends AsyncTask<String, Void, Void>{
		private boolean isGettedQuote;
		private ProgressDialog uploadDialog;
		private String quoteComment;

		@Override
		protected void onPreExecute() {
			uploadDialog = new ProgressDialog(mContext);
			uploadDialog.setMessage("Lấy quote .....");
			uploadDialog.show();
			isGettedQuote = true;
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(String... params) {
			try {
				
				Map<String, String> cookie = null;
				try{
					cookie = Constant.getCookies(sharedPreferences.getString("cookie_data", ""));
				}catch(Exception e){
					cookie = new HashMap<String, String>();
				}
				Document doc = Jsoup.connect("http://vozforums.com/newreply.php?do=newreply&p="+params[0]).timeout(Constant.timeout).cookies(cookie).get();
				Element textArea = doc.select("textarea ").first();
				quoteComment = textArea.text();
			} catch (Exception e) {
				e.printStackTrace();
				isGettedQuote = false;
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if (uploadDialog.isShowing()) {
				uploadDialog.dismiss();
				if (isGettedQuote) {
					notifyOnNotifyViewPagerActivity.onNotifyQuote(quoteComment);
				}else {
					Toast.makeText(mContext, "Không lấy được comment", Toast.LENGTH_LONG).show();
				}
			}
		}
	}
	
	
//	String []data2 = data.split("[<](/)?img[^>]*[>]",1000);
//	ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(mContext).imageDownloader(new MyBaseImageDownloader(mContext)).build();
//	ImageLoader.getInstance().init(config);
//	ImageLoader imgLoader = ImageLoader.getInstance();
//		TextView textView = new TextView(mContext);
//	URLImageParser p = new URLImageParser(textView, mContext);
//	textView.setText(Html.fromHtml(data,p,null));
//	textView.setMovementMethod(LinkMovementMethod.getInstance());
//	textView.setAutoLinkMask(Linkify.ALL);
//	textView.setTextSize(16);
//	LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
//	mHolder.detailContainer.removeAllViews();
//	mHolder.detailContainer.addView(textView, params);



//	for(int i = 0 ;i<data2.length;i++){
//		TextView textView = new TextView(mContext);
//		textView.setText(Html.fromHtml(data2[i]));
//		textView.setTextSize(16);
//		FlowLayout.LayoutParams params = new FlowLayout.LayoutParams(FlowLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
//		mHolder.detailContainer.addView(textView, params);
//		if (i<data2.length-1) {
//			ImageView imageView;
//				if (listimg.get(i).isIcon) {
//					TextView textViewIcon = new TextView(mContext);
//					URLImageParser p = new URLImageParser(textView, mContext);
//					textViewIcon.setText(Html.fromHtml("<img src = "+"\""+listimg.get(i)+"\""+"/>", new ImageGetter() {
//						@Override
//						public Drawable getDrawable(String source) {
//							Resources resources = mContext.getResources();
//					        int identifier = resources.getIdentifier("sweat", "drawable", mContext.getPackageName());
//					        Drawable res = resources.getDrawable(identifier);
//					        res.setBounds(0, 0, res.getIntrinsicWidth(), res.getIntrinsicHeight());
//					        return res;
//						}
//					},null));
//					textViewIcon.setTextSize(16);
//					FlowLayout.LayoutParams paramsIcon = new FlowLayout.LayoutParams(FlowLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
//					mHolder.detailContainer.addView(textViewIcon, paramsIcon);
//			}else{
//				imageView = new ResizableImageViewWidth(mContext, null);
//				imgLoader.displayImage(listimg.get(i).getLink(),imageView,getObtion());
//				RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(FlowLayout.LayoutParams.WRAP_CONTENT,FlowLayout.LayoutParams.WRAP_CONTENT);
//				mHolder.detailContainer.addView(imageView,params2);
//			}
//				 
//		}
//	}
}
