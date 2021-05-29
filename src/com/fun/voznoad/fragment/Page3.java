package com.fun.voznoad.fragment;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.fun.voznoad.ImageActivity;
import com.fun.voznoad.R;
import com.fun.voznoad.SearchUserActivity;
import com.fun.voznoad.SendMessageActivity;
import com.fun.voznoad.ViewPagerActivity;
import com.fun.voznoad.WebViewActivity;
import com.fun.voznoad.adapter.AdapterDialog;
import com.fun.voznoad.callback.OnNotifyViewPagerActivity;
import com.fun.voznoad.common.Constant;
import com.fun.voznoad.model.DialogItem;
import com.fun.voznoad.model.Page3Item;
import com.fun.voznoad.servicecontroller.HttpUrlJSONParser;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class Page3 extends Fragment {
	private View view;
	private OnNotifyViewPagerActivity notifyOnNotifyViewPagerActivity;
	private String url;
	private ArrayList<Page3Item> threadItems;
	private int page;
	private ProgressBar page3Progres;
	private SharedPreferences sharedPreferences;
	private TextView notFoundTv;
	private LinearLayout listdetail;
	private ArrayList<ViewHolder> viewHoldersList;
	
	String quotePage ;
	
	public void setNotifyMain(OnNotifyViewPagerActivity x) {
		notifyOnNotifyViewPagerActivity = x;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_page3, container, false);
		return view;
	}
	
	 
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initUI();
		try {
			new GetDataTask().execute();
		} catch (Exception e) {
		}

	}

	public void initUI() {
		//listview = (ListView) view.findViewById(R.id.listdetail);
		threadItems = new ArrayList<Page3Item>();
		listdetail = (LinearLayout) view.findViewById(R.id.listdetail);
		url = "http://vozforums.com/" + getArguments().getString("url");
		page = getArguments().getInt("page");
		page3Progres = (ProgressBar) view.findViewById(R.id.page3Progress);
		sharedPreferences = getActivity().getSharedPreferences("voz_data",getActivity().MODE_PRIVATE);
		notFoundTv = (TextView) view.findViewById(R.id.notFound);
		viewHoldersList = new ArrayList<Page3.ViewHolder>();
		
		detailScrollView = (PullToRefreshScrollView) view.findViewById(R.id.detailScrollView);
		detailScrollView.setOnRefreshListener(new OnRefreshListener<ScrollView>() {

			@Override
			public void onRefresh(PullToRefreshBase<ScrollView> refreshView) {
				listdetail.removeAllViews();
				threadItems.clear();
				new GetDataTask().execute();
			}
		});
	 
		
		myScrollView = detailScrollView.getRefreshableView();
		myScrollView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				v.performClick();
				switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN: {
						downY = event.getY();
						// return true;
					}
					case MotionEvent.ACTION_UP: {
						upY = event.getY();
						float deltaY = downY - upY;
						// swipe vertical?
						if (Math.abs(deltaY) > MIN_DISTANCE) {
							// top or down
							if (deltaY < 0) {
								onTopToBottomSwipe();
							}
							if (deltaY >= 0 || downY==0) {
								onBottomToTopSwipe();
							}
						}
					}
				}
				return false;
			}
		});
	}
 
	public void onTopToBottomSwipe() {
		notifyOnNotifyViewPagerActivity.onNotifyTopToBottomSwipe();
	}

	public void onBottomToTopSwipe() {
		notifyOnNotifyViewPagerActivity.onNotifyBottomToTopSwipe();
	}

	static final int MIN_DISTANCE = 1;
	private float downX, downY, upX, upY;
	private PullToRefreshScrollView detailScrollView;
	private ScrollView myScrollView;
	
	public class ViewHolder {
		TextView numberTv;
		TextView typeTv;
		TextView timeTv;
		ImageView avartaTv;
		TextView nameTv;
		TextView postTv;
		WebView detailContainer;
		TextView quoteTv;
		TextView mutilQuoteTv;
		LinearLayout containerItem;
		TextView editTv;
		TextView joindateTv;
		
		RelativeLayout TopContainer;
		RelativeLayout middleContainer;

		public ViewHolder(View v) {
			numberTv = (TextView) v.findViewById(R.id.numberTv);
			typeTv = (TextView) v.findViewById(R.id.typeTv);
			timeTv = (TextView) v.findViewById(R.id.timeTv);
			avartaTv = (ImageView) v.findViewById(R.id.avartaImg);
			nameTv = (TextView) v.findViewById(R.id.nameTv);
			joindateTv = (TextView) v.findViewById(R.id.joindateTv);
			postTv = (TextView) v.findViewById(R.id.postTv);
			detailContainer = (WebView) v.findViewById(R.id.detailContainer);
			containerItem = (LinearLayout) v.findViewById(R.id.containerItem);
			quoteTv = (TextView) v.findViewById(R.id.quoteTv);
			mutilQuoteTv = (TextView) v.findViewById(R.id.mutilquoteTv);
			editTv = (TextView) v.findViewById(R.id.editTv);
			TopContainer = (RelativeLayout) v.findViewById(R.id.TopContainer);
			middleContainer = (RelativeLayout) v.findViewById(R.id.middleContainer);
		}
	}

	private String token="";
	private String postid="";
	private String userid="";
	
	@SuppressLint("NewApi") 
	public class GetDataTask extends AsyncTask<Void, Page3Item, Void> {
		String data;
		private String currentPage;
		private String threadID;
		private String totalPage;
		String number = sharedPreferences.getString(url + "&page=" + page, "-1");

		@Override
		protected void onPreExecute() {
			token = "";
			postid = "";
			userid ="";
			super.onPreExecute();
			page3Progres.setVisibility(View.VISIBLE);
		}

		@Override
		protected Void doInBackground(Void... params) {
			try {
				// Parse detail thread
				Map<String, String> cookie = null;
				try {
					cookie = Constant.getCookies(sharedPreferences.getString("cookie_data", ""));
				} catch (Exception e) {
					cookie = new HashMap<String, String>();
				}
				// Get Curren topic menu and save to Constant.quoteListURl
				if (!Constant.quoteListURL.equals("")&& getActivity().getIntent().getStringExtra("isquote") != null){
					url = Constant.quoteListURL;
				}
				
				
				Document homePage = Jsoup.connect(url + "&page=" + page).timeout(Constant.timeout).cookies(cookie).get();
				Element dataContainer = homePage.select("td.panelsurround").first();
				try{
					token = dataContainer.select("input[name=securitytoken]").first().val();
					postid = dataContainer.select("input#qr_threadid").first().val();
					userid = dataContainer.select("input[name=loggedinuser]").first().val();
				}catch (Exception e){
					
				}
				
				// Get current page and thread id when click quote List menu
				try{
					if (page == 1 && getActivity().getIntent().getStringExtra("isquote") != null&&Constant.quoteListURL.equals("")) {
						String []panelPage = homePage.select(".pagenav").first().select("td.vbmenu_control").get(0).text().split(" ");
						currentPage = panelPage[1];
						threadID = homePage.select("input#qr_threadid").val();
						totalPage = panelPage[panelPage.length-1];
						return null;
					}else {
						String []panelPage = homePage.select(".pagenav").first().select("td.vbmenu_control").get(0).text().split(" ");
						totalPage = panelPage[panelPage.length-1];
					}
				}catch (Exception e){
					
				}
				
				Elements details = homePage.select("div#posts > div[align=left]");
				for (int i = 0; i < details.size(); i++) {
					try {
						Element tableE = details.get(i).select("table.tborder").first();
						String id = tableE.attr("id").replace("post", "");
						String stt = tableE.select("#postcount"+id).first().text();
						String time = tableE.select("a[name=post" + id + "]").first().parent().text();
						Element name_link = tableE.select("a.bigusername").first();
						String userThreadId = name_link.attr("href").replace("member.php?u=", "");
						String name = name_link.text();
						String link = "";
						try {
							link = "http://vozforums.com/"+ tableE.select("tr>td>table td a img").first().attr("src");
						} catch (Exception e) {

						}
						Elements smallfontE = tableE.select("div.smallfont");
						String type = smallfontE.get(0).text();
						Elements date_post = smallfontE.get(1).select("> div");
						String joindate = date_post.get(0).text();
						String post = date_post.get(date_post.size()-2).text();
						String data = tableE.select("div#post_message_" + id).first().html();
						Element sibling =tableE.select("div#post_message_" + id).first().nextElementSibling();
 						try{
							if (sibling.attr("align").equals("")){
								data = data +"\r\n<br>"+ sibling.html();
							}
						}catch(Exception e){
						}
						boolean isEdit = false;
						if (userThreadId.equals(userid)) {
							isEdit = true;
						}
						Page3Item threadItem = new Page3Item(userThreadId,joindate,isEdit,time,stt , link, name, type, post,data, id);
						threadItems.add(threadItem);
						publishProgress(threadItem);
					} catch (Exception e) {
					}
				}
				// Get data to post comment [attr=value]
			 
			} catch (Exception e) {
			}
			return null;
		}
		
		@Override
		protected void onProgressUpdate(Page3Item... values) {
			super.onProgressUpdate(values);
			try {
				View itemview = getActivity().getLayoutInflater().inflate(R.layout.adapter_page3,listdetail,false);
				ViewHolder mHolder = new ViewHolder(itemview);
				initItemUI(values[0], mHolder);
				initItemCONTROL(values[0], mHolder);
				listdetail.addView(itemview);
				viewHoldersList.add(mHolder);
				if(values[0].getNumber().equals((Integer.parseInt(number)+1)+"")){
					Log.d("dohai", (Integer.parseInt(number)+1)+"");
					sharedPreferences.edit().remove(url + "&page=" + page);
					sharedPreferences.edit().commit();
 				} 
				int sdk = android.os.Build.VERSION.SDK_INT;
				if (Constant.mutilquoteCookieArr.contains(values[0].getPostid())) {
					if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
						mHolder.containerItem.setBackgroundDrawable(getResources().getDrawable(R.drawable.shape_mutilquote));
					}else {
						mHolder.containerItem.setBackground(getResources().getDrawable(R.drawable.shape_mutilquote));
					}
				}
				if (page3Progres.getVisibility() == View.VISIBLE){
					page3Progres.setVisibility(View.GONE);
				}
				 
			}catch(Exception e){
				
			}
		}

		@Override
		protected void onPostExecute(Void result) {
			if (page==1 && threadID!=null && currentPage!=null) {
				notifyOnNotifyViewPagerActivity.onNotifyQuoteList(threadID,currentPage);
				super.onPostExecute(result);
				return;
			}
			
			if (totalPage != null){
				notifyOnNotifyViewPagerActivity.onNotifyTotalPage(totalPage);
			}
			
			detailScrollView.onRefreshComplete();
 			notifyOnNotifyViewPagerActivity.onSetTokentDta(token, postid,userid);
			super.onPostExecute(result);
			if (threadItems.isEmpty()) {
				notFoundTv.setVisibility(View.VISIBLE);
			} else{
				notFoundTv.setVisibility(View.GONE);
			}
 
		}
	}
 
	@SuppressLint({ "SetJavaScriptEnabled", "NewApi" }) 
	public void initItemUI(Page3Item page3Item,ViewHolder mHolder ){
		mHolder.numberTv.setText("#"+page3Item.getNumber());
		mHolder.typeTv.setText(page3Item.getType());
		mHolder.timeTv.setText(page3Item.getTime());
		mHolder.nameTv.setText(page3Item.getName());
		mHolder.postTv.setText(page3Item.getJoindate()); 
		mHolder.joindateTv.setText(page3Item.getPost());
		if (page3Item.isEdit()) {
			mHolder.editTv.setVisibility(View.VISIBLE);
		}
	 
		String data = page3Item.getData();
		Document doc = Jsoup.parse(data);
		Elements imgElement = doc.select("img");
		for(int i = 0 ;i<imgElement.size();i++){
			Element img_element = imgElement.get(i);
			String url =  img_element.attr("src");
			if (url.contains("http")) {
				img_element.attr("onClick", "showAndroidToast(this.src)");
				img_element.attr("style"," width: 100%!important");
			}
		}
	    ImageLoader imgLoader = ImageLoader.getInstance();
        if (!page3Item.getAvarta().equals(""))  imgLoader.displayImage(page3Item.getAvarta(),mHolder.avartaTv,getObtion());
        else mHolder.avartaTv.setImageResource(R.drawable.brick);
		data ="<style>table tbody tr td {background: #E1E4F2}</style>"+doc.html()+"<script type=\"text/javascript\">\r\n function showAndroidToast(toast) {\r\n    Android.showToast(toast);\r\n }\r\n</script>";
		if (sharedPreferences.getString("theme", "null").equals("black")) {
			initTheme(mHolder, data, doc.html());
			data ="<style>table tbody tr td {background: #191919} body { background:#222222!important} body{color:#bbbbbb!important} a{color : #ffad26 !important }</style>"+doc.html()+"<script type=\"text/javascript\">\r\n function showAndroidToast(toast) {\r\n    Android.showToast(toast);\r\n }\r\n</script>";
		}
		int fontSizePlus = Integer.parseInt(sharedPreferences.getString("fontsize", "0"));
		
		mHolder.detailContainer.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        mHolder.detailContainer.getSettings().setJavaScriptEnabled(true);
        mHolder.detailContainer.getSettings().setDomStorageEnabled(true);
        mHolder.detailContainer.getSettings().setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN );
        mHolder.detailContainer.getSettings().setLayoutAlgorithm(LayoutAlgorithm.NARROW_COLUMNS );
        mHolder.detailContainer.getSettings().setDefaultFontSize(17+fontSizePlus);
        mHolder.detailContainer.addJavascriptInterface( new WebAppInterface(getActivity()), "Android");
 	    mHolder.detailContainer.loadDataWithBaseURL("http://vozforums.com/", data, "text/html", "UTF-8", null);
 	    try {
 	    	String quoteId = getActivity().getIntent().getStringExtra("quoteId");
 			if(page3Item.getPostid().equals(quoteId)){
 				Toast.makeText(getActivity(), "#"+page3Item.getNumber(), Toast.LENGTH_SHORT).show();
 				int sdk = android.os.Build.VERSION.SDK_INT;
 				if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
 					mHolder.containerItem.setBackgroundDrawable(getResources().getDrawable(R.drawable.shape_quote_list));
 				}else {
 					mHolder.containerItem.setBackground(getResources().getDrawable(R.drawable.shape_quote_list));
 				}
 			}
 	    }catch (Exception e){
 	    	
 	    }
 
	}
	
	public void initTheme(ViewHolder mHolder,String data,String html){
		notFoundTv.setTextColor(Color.parseColor("#bbbbbb"));
 		mHolder.numberTv.setTextColor(Color.parseColor("#bbbbbb"));
		mHolder.typeTv.setTextColor(Color.parseColor("#bbbbbb"));
		mHolder.timeTv.setTextColor(Color.parseColor("#bbbbbb"));
		mHolder.nameTv.setTextColor(Color.parseColor("#bbbbbb"));
		mHolder.quoteTv.setTextColor(Color.parseColor("#bbbbbb"));
		mHolder.mutilQuoteTv.setTextColor(Color.parseColor("#bbbbbb"));
		mHolder.editTv.setTextColor(Color.parseColor("#bbbbbb"));
		
		mHolder.quoteTv.setBackgroundColor(Color.parseColor("#3d224b"));
		mHolder.mutilQuoteTv.setBackgroundColor(Color.parseColor("#3d224b"));
		mHolder.editTv.setBackgroundColor(Color.parseColor("#3d224b"));
		
		mHolder.postTv.setTextColor(Color.parseColor("#bbbbbb"));
		mHolder.detailContainer.setBackgroundColor(Color.parseColor("#222222"));
		mHolder.TopContainer.setBackgroundColor(Color.parseColor("#111111"));
		mHolder.middleContainer.setBackgroundColor(Color.parseColor("#111111"));
		mHolder.containerItem.setBackgroundColor(Color.parseColor("#222222"));
	}
	
	@SuppressLint("NewApi")
	public void initItemCONTROL(final Page3Item page3Item,final ViewHolder mHolder){
		mHolder.detailContainer.setFocusable(false);
		mHolder.detailContainer.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				 if (url.contains(".jpg")||url.contains(".png")||url.contains(".jpeg")){
					return true;
				}
				
				if (url.contains("redirect")) {
					url = url.substring(url.indexOf("link=")+5,url.length());
				}else if (url.contains("vozforums")&&!url.contains("redirect")) {
					if (url.contains("showpost")) {
						 Intent intent = new Intent(getActivity(), WebViewActivity.class);
						 intent.putExtra("url", url);
						 intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
						 getActivity().startActivity(intent);
						 return true;
					}else if (url.contains("showthread")){
						 Intent intent = new Intent(getActivity(), ViewPagerActivity.class);
						 try {
							 intent.putExtra("url", url.split("&page=")[0].replace("http://vozforums.com/", ""));
							 intent.putExtra("totalpage", url.split("&page=")[1]);
						 }catch (Exception e){
							 intent.putExtra("url",url.replace("http://vozforums.com/", ""));
							 intent.putExtra("totalpage", "1");
						 }
	 
						 intent.putExtra("title", "");
						 intent.putExtra("isReadChap","isReadChap");
						 intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
						 getActivity().startActivity(intent);
						 return true;
					}
				}
				try {
					url = URLDecoder.decode(url, "UTF-8") ;
					Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
					getActivity().startActivity(intent);
					return true;
				} catch (Exception e) {
					e.printStackTrace();
				}
				return true;
			}
			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
			}
		});
		
		mHolder.quoteTv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				new GetQuoteCommentTask().execute(page3Item.getPostid()+"");
			}
		});
		
		mHolder.mutilQuoteTv.setOnClickListener( new OnClickListener() {		
			@Override
			public void onClick(View v) {
					int sdk = android.os.Build.VERSION.SDK_INT;
				if (!Constant.mutilquoteCookieArr.contains(page3Item.getPostid()+"")) {
					Constant.mutilquoteCookieArr.add(page3Item.getPostid()+"");
					if (!sharedPreferences.getString("theme", "null").equals("black")) {
						if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
							mHolder.containerItem.setBackgroundDrawable(getResources().getDrawable(R.drawable.shape_mutilquote));
						}else {
							mHolder.containerItem.setBackground(getResources().getDrawable(R.drawable.shape_mutilquote));
						}
					}else {
						if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
							mHolder.containerItem.setBackgroundDrawable(getResources().getDrawable(R.drawable.shape_mutilquote_black));
						}else {
							mHolder.containerItem.setBackground(getResources().getDrawable(R.drawable.shape_mutilquote_black));
						}
					}
				}else {
					Constant.mutilquoteCookieArr.remove(page3Item.getPostid()+"");
					if (!sharedPreferences.getString("theme", "null").equals("black")) {
	 					if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
	 						mHolder.containerItem.setBackgroundColor(Color.parseColor("#ffffff"));
						}else {
							mHolder.containerItem.setBackgroundColor(Color.parseColor("#ffffff"));
						}
					}else {
						if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
							mHolder.containerItem.setBackgroundColor(Color.parseColor("#222222"));
						}else {
							mHolder.containerItem.setBackgroundColor(Color.parseColor("#222222"));
						}
					}
				}
			}
		});
		
		mHolder.editTv.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				new GetEditCommentTask().execute(page3Item.getPostid()+"");
			}
		});
		
		mHolder.middleContainer.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				showUserDialog(page3Item);
			}
		});
	}
	
	public void showUserDialog(final Page3Item page3Item){
		final Dialog dialog = new Dialog(getActivity());
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		dialog.setContentView(R.layout.diaglog_menu);
		ArrayList<DialogItem> dialogItemLst = new ArrayList<DialogItem>();
		DialogItem dialogItem1 = new DialogItem("Gửi tin nhắn","http://vozforums.com/profile.php?do=doaddlist&list=&userid=");
		DialogItem dialogItem2 = new DialogItem("Danh sách đen","");
		DialogItem dialogItem3 = new DialogItem("Xem posts","");
		DialogItem dialogItem4 = new DialogItem("Xem threads","");
		dialogItemLst.add(dialogItem1);
		dialogItemLst.add(dialogItem2);
		dialogItemLst.add(dialogItem3);
		dialogItemLst.add(dialogItem4);
		ListView dialogLv = (ListView) dialog.findViewById(R.id.dialogLv);
		AdapterDialog dialogAdp = new AdapterDialog(dialogItemLst, getActivity());
		dialogLv.setAdapter(dialogAdp);
		//new GetRank().execute();
		dialog.show();
	 
		dialogLv.setOnItemClickListener(new OnItemClickListener() {


			public void onItemClick(AdapterView<?> arg0, View arg1, int position,long arg3) {
				if (position ==1) {
					new IgnoreUserTask().execute(page3Item.getUserThreadId());
					dialog.dismiss();
				}else if(position == 0){
					Intent intent = new Intent(getActivity(), SendMessageActivity.class);
					intent.putExtra("quickmessage", "quickmessage");
					intent.putExtra("token", token);
					intent.putExtra("username", page3Item.getName()); 
					intent.putExtra("url", "http://vozforums.com/private.php?do=insertpm&pmid=");
					dialog.dismiss();
					startActivity(intent);
				}else if (position ==2){
					Intent intent = new Intent(getActivity(), SearchUserActivity.class);
					intent.putExtra("userid", page3Item.getUserThreadId());
					intent.putExtra("searchtype", "post");
					intent.putExtra("token", token);
					dialog.dismiss();
					startActivity(intent);
				}else if (position==3){
					Intent intent = new Intent(getActivity(), SearchUserActivity.class);
					intent.putExtra("userid", page3Item.getUserThreadId());
					intent.putExtra("searchtype", "thread");
					intent.putExtra("token", token);
					dialog.dismiss();
					startActivity(intent);
				}
			}
		});
		ImageView closeBtn = (ImageView) dialog.findViewById(R.id.closeBtn);
		closeBtn.setOnClickListener(new OnClickListener() {
			
			public void onClick(View arg0) {
				dialog.dismiss();
			}
		});
	}
	

	private DisplayImageOptions getObtion() {
		DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisc(true).build();
		return options;
	}
	
	 
	public class WebAppInterface {
	    Context mContext;
	    /** Instantiate the interface and set the context */
	    WebAppInterface(Context c) {
	        mContext = c;
	    }
	    /** Show a toast from the web page */
	    @JavascriptInterface
		public void showToast(String toast) {
			if (!toast.contains("gif")) {
				Intent intent = new Intent(getActivity(), ImageActivity.class);
				intent.putExtra("url", toast);
				startActivity(intent);
			}
		}
	}
	
	public class GetQuoteCommentTask extends AsyncTask<String, Void, Void>{
		private boolean isGettedQuote;
		private ProgressDialog uploadDialog;
		private String quoteComment;

		@Override
		protected void onPreExecute() {
			uploadDialog = new ProgressDialog(getActivity());
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
					String mutilquote= Constant.mutilquoteCookieArr.toString();
					cookie = Constant.getCookies(sharedPreferences.getString("cookie_data", ""));
					cookie.put("vbulletin_multiquote", mutilquote.substring(1, mutilquote.length()-1));
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
					Toast.makeText(getActivity(), "Không lấy được comment", Toast.LENGTH_LONG).show();
				}
			}
		}
	}
	
	public class GetEditCommentTask extends AsyncTask<String, Void, Void>{
		private boolean isGettedQuote;
		private ProgressDialog uploadDialog;
		private String quoteComment;
		private String title;
		private String poststarttime;
		private String posthash;
		private String p;

		@Override
		protected void onPreExecute() {
			uploadDialog = new ProgressDialog(getActivity());
			uploadDialog.setMessage("Lấy comment .....");
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
					cookie.put("vbulletin_multiquote", "");
				}catch(Exception e){
					cookie = new HashMap<String, String>();
				}
				Document doc = Jsoup.connect("http://vozforums.com/editpost.php?do=editpost&p="+params[0]).timeout(Constant.timeout).cookies(cookie).get();
				Element textArea = doc.select("textarea ").first();
				title = doc.select("input[name=title]").val();
				poststarttime = doc.select("input[name=poststarttime]").val();
				posthash = doc.select("input[name=posthash]").val();
				p = params[0];
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
					notifyOnNotifyViewPagerActivity.onNotifyEdit(quoteComment,title,poststarttime,posthash,p);
				}else {
					Toast.makeText(getActivity(), "Không lấy được comment", Toast.LENGTH_LONG).show();
				}
			}
		}
	}
	
	
	
	@Override
	public void onDestroyView() {
		for(int i = 0 ;i<viewHoldersList.size();i++){
			WebView mWebView = viewHoldersList.get(i).detailContainer;
			listdetail.removeAllViews();
		    mWebView.loadUrl("about:blank");
		    mWebView = null;
		}
		super.onDestroyView();
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
	}
	
	public class IgnoreUserTask extends AsyncTask<String, Void, Void>{
		boolean isIgnore = true;
		
		@Override
		protected Void doInBackground(String... params) {
			List<NameValuePair> data = new ArrayList<NameValuePair>();
			data.add(new BasicNameValuePair("s", ""));
			data.add(new BasicNameValuePair("securitytoken", token));
			data.add(new BasicNameValuePair("userlist", "ignore"));
			data.add(new BasicNameValuePair("do", "doaddlist"));
			data.add(new BasicNameValuePair("userid", params[0]));
			data.add(new BasicNameValuePair("url", "http://vozforums.com/member.php?u="+params[0]));
			data.add(new BasicNameValuePair("confirm", "Yes"));
			HttpUrlJSONParser jsonParser = new HttpUrlJSONParser();
			SharedPreferences sharedPreferences = getActivity().getSharedPreferences("voz_data",getActivity().MODE_PRIVATE);
			jsonParser.setCookie(sharedPreferences.getString("cookie_data", ""));
			jsonParser.setAllowSaveCookie(false);
			try {
				String a= jsonParser.postForm("http://vozforums.com/profile.php?do=doaddlist&list=&userid="+ params[0], "post", data);
				String b = a;
			} catch (Exception e) {
				e.printStackTrace();
				isIgnore = false;
			}
			return null;
		}
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if (isIgnore) {
				Toast.makeText(getActivity(), "thành c ông", Toast.LENGTH_SHORT).show();
			}
		}
		
	}
}