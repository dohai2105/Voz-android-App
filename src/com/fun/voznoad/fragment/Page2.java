package com.fun.voznoad.fragment;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
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

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.inputmethod.InputMethodManager;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.fun.voznoad.R;
import com.fun.voznoad.ViewPagerActivity;
import com.fun.voznoad.ViewPagerActivity.EditTask;
import com.fun.voznoad.ViewPagerActivity.QuickCommentTask;
import com.fun.voznoad.ViewPagerActivity.UploadImageTask;
import com.fun.voznoad.adapter.IconAdapter;
import com.fun.voznoad.adapter.Page2ListAdp;
import com.fun.voznoad.callback.OnNotifyMainListener;
import com.fun.voznoad.common.Constant;
import com.fun.voznoad.model.Icon;
import com.fun.voznoad.model.Page2Item;
import com.fun.voznoad.servicecontroller.HttpUrlJSONParser;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

public class Page2 extends Fragment{
	private OnNotifyMainListener notifyMainListener;
	private String url ;
	private String f ;
	private boolean searchSticky ;
	private ArrayList<Page2Item> threadList ;
	private PullToRefreshListView mPullRefreshListView;
	private View v;
	private Page2ListAdp threadListAdp;
	private boolean isLoading ;
	private int currentpage;
	private View mLoadMoreFooter;
	private LinearLayout quickPage;
	private TextView f17QuickTv;
	private TextView f33QuickTv;
	private TextView f47QuickTv;
	private TextView f31QuickTv;
	private LinearLayout newThreadContainer;
	private LinearLayout threadContainer;
	private Button postSubmit;
	private EditText commentText;
	private Button upImgSubmit;
	private Button backButton;
	private Button iconSelect;
	private LinearLayout commentContainer2;
	private LinearLayout iconContainer;
	private EditText titleText;
	private ArrayList<Icon> iconList;
	private SharedPreferences sharedPreferences;
	private Editor editor;
	private static final int PICK_IMAGE = 1;
	protected static final int VIEWPAGEROPEN = 10;
	
	private String prefix = "";
	 
	public void setNotifyMain (OnNotifyMainListener x){
		notifyMainListener = x;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		v = inflater.inflate(R.layout.fragment_page2, container, false);
		mLoadMoreFooter = inflater.inflate(R.layout.footer_loadmore, null);
		return v;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		url = getArguments().getString("url");
		f = url.replace("http://vozforums.com/forumdisplay.php?f=", "");
		initUI();
		initCONTROL();
		searchSticky = true;
		isLoading = true;
		currentpage=1;
		new GetDataTask().execute();
		setHasOptionsMenu(true);
		initQUICKMENU();
		if (sharedPreferences.getString("theme", "null").equals("black")) {
			initTheme();
		}
	}
	
	public void initTheme(){
		f17QuickTv.setTextColor(Color.parseColor("#bbbbbb"));
		f33QuickTv.setTextColor(Color.parseColor("#bbbbbb"));
		f47QuickTv.setTextColor(Color.parseColor("#bbbbbb"));
		f31QuickTv.setTextColor(Color.parseColor("#bbbbbb"));
		quickPage.setBackgroundColor(Color.parseColor("#000000"));
		mLoadMoreFooter.setBackgroundColor(Color.parseColor("#191919"));
		(v.findViewById(R.id.splitView)).setBackgroundColor(Color.parseColor("#ffffff"));
	}
	
	
	public void initUI(){
		threadList = new ArrayList<Page2Item>();
		mPullRefreshListView = (PullToRefreshListView) v.findViewById(R.id.pull_refresh_list);
		threadListAdp = new Page2ListAdp(threadList, getActivity());
		mPullRefreshListView.setAdapter(threadListAdp);
		lv = mPullRefreshListView.getRefreshableView();
		lv.addFooterView(mLoadMoreFooter);
		if (getArguments().getString("title")!=null) {
			notifyMainListener.onSetActionBarTitle(getArguments().getString("title") );
		}
		
		quickPage = (LinearLayout) v.findViewById(R.id.quickPage);
		f17QuickTv = (TextView) v.findViewById(R.id.f17QuickTv);
		f33QuickTv = (TextView) v.findViewById(R.id.f33QuickTv);
		f47QuickTv = (TextView) v.findViewById(R.id.f47QuickTv);
		f31QuickTv = (TextView) v.findViewById(R.id.f31QuickTv);
		newThreadContainer = (LinearLayout) v.findViewById(R.id.newThreadContainer);
		threadContainer = (LinearLayout) v.findViewById(R.id.threadContainer);
		
		// Post submit
		postSubmit = (Button) v.findViewById(R.id.postSubmit);
		// Title Edittext
		titleText = (EditText) v.findViewById(R.id.titleText);
		// Comment Edittext
		commentText = (EditText) v.findViewById(R.id.commentText);
		// Upload Image Button
		upImgSubmit = (Button) v.findViewById(R.id.upImgSubmit);

		backButton = (Button) v.findViewById(R.id.backButton);

		iconSelect = (Button) v.findViewById(R.id.iconSelect);

		commentContainer2 = (LinearLayout) v.findViewById(R.id.commentContainer2);

		iconContainer = (LinearLayout) v.findViewById(R.id.iconContainer);
		 
	}
	
	public void initQUICKMENU(){
		sharedPreferences = getActivity().getSharedPreferences("voz_data",getActivity().MODE_PRIVATE);
		editor = sharedPreferences.edit();
		String quick1 = sharedPreferences.getString("quick1", "17");
		String quick2 = sharedPreferences.getString("quick2", "33");
		String quick3 = sharedPreferences.getString("quick3", "47");
		String quick4 = sharedPreferences.getString("quick4", "31");
		
		f17QuickTv.setText("f"+quick1);
		f33QuickTv.setText("f"+quick2);
		f47QuickTv.setText("f"+quick3);
		f31QuickTv.setText("f"+quick4);
	}
	
	public void showIconGridView(final int currentPosition){
		iconContainer.setVisibility(View.VISIBLE);
		commentContainer2.setVisibility(View.GONE);
		GridView iconGridView = (GridView) v.findViewById(R.id.iconGridView);
		try{
			if (iconList==null) {
				iconList = Constant.getListIcon(getActivity());
			}
			IconAdapter iconAdapter = new IconAdapter(getActivity(), iconList);
			iconGridView.setAdapter(iconAdapter);
			iconGridView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> adapterView, View view, int position,long arg3) {
					commentText.requestFocus();
					String comment = commentText.getText().toString();
					if(currentPosition<comment.length()){
						comment = comment.substring(0, currentPosition) + iconList.get(position).getIconSymbol() + comment.substring(currentPosition,comment.length());
					}else {
						comment = comment+iconList.get(position).getIconSymbol();
					}
					commentText.setText(comment);
					commentText.setSelection(currentPosition+iconList.get(position).getIconSymbol().length());
					iconContainer.setVisibility(View.GONE);
					commentContainer2.setVisibility(View.VISIBLE);
				}
			});
		}catch(Exception e){
		}
	}
	
	public void initCONTROL(){
		
		// Post submit click
		postSubmit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				new NewPostTask().execute();
 			}
		});
		
		// Upload image button 
		upImgSubmit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
//				
				 Intent intent = new Intent();
			        intent.setType("image/*");
			        intent.setAction(Intent.ACTION_GET_CONTENT);
			        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE );
//			        Intent intent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//			        startActivityForResult(intent, PICK_IMAGE);
			        
			}
		});
		
		backButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				newThreadContainer.setVisibility(View.GONE);
		    	threadContainer.setVisibility(View.VISIBLE);
				InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(commentText.getWindowToken(),0); 
			}
		});
		
		iconSelect.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				int currentPosition = commentText.getSelectionStart();
				showIconGridView(currentPosition);
			}
		});
		
		mPullRefreshListView.setOnRefreshListener(new OnRefreshListener<ListView>() {
			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				String label = DateUtils.formatDateTime(getActivity().getApplicationContext(), System.currentTimeMillis(),
						DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
				refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
				isLoading= true;
				searchSticky = true;
				currentpage=1;		
				threadList.clear();
				new GetDataTask().execute();
			}
		});
		
		mPullRefreshListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int position,long arg3) {
				if(f.equalsIgnoreCase("17")||f.equalsIgnoreCase("11")||f.equalsIgnoreCase("6")){
					if (((position == 1||position==2||position==3)&&f.equalsIgnoreCase("17"))||
							((position == 1||position==2)&&f.equalsIgnoreCase("11"))||
							((position == 1)&&f.equalsIgnoreCase("6"))) {
						String url = threadList.get(position-1).getLink();
						Bundle bundle = new Bundle();
						bundle.putString("url", url);
						Page2 ft = new Page2();
						ft.setArguments(bundle);
						ft.setNotifyMain(notifyMainListener);
						notifyMainListener.onSetActionBarTitle(threadList.get(position-1).getTitle());
						notifyMainListener.replaceFragment(ft);
					}else {
						 Intent intent = new Intent(getActivity(), ViewPagerActivity.class);
						 intent.putExtra("url",  threadList.get(position-1).getLink());
						 intent.putExtra("totalpage", threadList.get(position-1).getPage());
						 intent.putExtra("title", threadList.get(position-1).getTitle());
						 intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
						 startActivityForResult(intent,VIEWPAGEROPEN);
					}
				}else{
					 Intent intent = new Intent(getActivity(), ViewPagerActivity.class);
					 intent.putExtra("url",  threadList.get(position-1).getLink());
					 intent.putExtra("totalpage", threadList.get(position-1).getPage());
					 intent.putExtra("title", threadList.get(position-1).getTitle());
					 intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
					 startActivityForResult(intent,VIEWPAGEROPEN);
				}
			}
		});
		mPullRefreshListView.setOnScrollListener(new OnScrollListener() {
			private int mLastFirstVisibleItem;
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState)  {
			}
			
			@Override
			public void onScroll (AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				if (isLoading) {
					return;
				}
				int total = firstVisibleItem + visibleItemCount;
				if (totalItemCount == total && !isLoading) {
					isLoading=true;
					currentpage++;
					String isFromNewPost = getArguments().getString("fromNewPost");
					if(isFromNewPost==null){
						new GetDataTask().execute();
					}else {
						lv.removeFooterView(mLoadMoreFooter);
					}
				}
				
				if (mLastFirstVisibleItem < firstVisibleItem) {
					scrollDown();
				}
				if (mLastFirstVisibleItem > firstVisibleItem) {
					scrollUp();
				}
				mLastFirstVisibleItem = firstVisibleItem;
			}
		});
		
		f17QuickTv.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Bundle bundle = new Bundle();
				bundle.putString("url", "http://vozforums.com/forumdisplay.php?f="+f17QuickTv.getText().toString().replace("f", ""));
				bundle.putString("title", f17QuickTv.getText().toString());
				Page2 ft = new Page2();
				ft.setArguments(bundle);
				ft.setNotifyMain(notifyMainListener);
				notifyMainListener.replaceFragment(ft);
				notifyMainListener.onSetActionBarTitle(f17QuickTv.getText().toString());
			}
			
		});
		
		f17QuickTv.setOnLongClickListener(new OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				initQuickMenuCreate("quick1",f17QuickTv);
				return true;
			}
		});
		
		f33QuickTv.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Bundle bundle = new Bundle();
				bundle.putString("url", "http://vozforums.com/forumdisplay.php?f="+f33QuickTv.getText().toString().replace("f", ""));
				bundle.putString("title", f33QuickTv.getText().toString());
				Page2 ft = new Page2();
				ft.setArguments(bundle);
				ft.setNotifyMain(notifyMainListener);
				notifyMainListener.replaceFragment(ft);
				notifyMainListener.onSetActionBarTitle(f33QuickTv.getText().toString());
			}
		});
		
		f33QuickTv.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				initQuickMenuCreate("quick2",f33QuickTv);
				return true;
			}
		});
	
		f47QuickTv.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Bundle bundle = new Bundle();
				bundle.putString("url", "http://vozforums.com/forumdisplay.php?f="+f47QuickTv.getText().toString().replace("f", ""));
				bundle.putString("title", f47QuickTv.getText().toString());
				Page2 ft = new Page2();
				ft.setArguments(bundle);
				ft.setNotifyMain(notifyMainListener);
				notifyMainListener.replaceFragment(ft);
				notifyMainListener.onSetActionBarTitle(f47QuickTv.getText().toString());
			}
		});
		
		f47QuickTv.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				initQuickMenuCreate("quick3",f47QuickTv);
				return true;
			}
		});
		
		f31QuickTv.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Bundle bundle = new Bundle();
				bundle.putString("url", "http://vozforums.com/forumdisplay.php?f="+f31QuickTv.getText().toString().replace("f", ""));
				bundle.putString("title", f31QuickTv.getText().toString());
				Page2 ft = new Page2();
				ft.setArguments(bundle);
				ft.setNotifyMain(notifyMainListener);
				notifyMainListener.replaceFragment(ft);
				notifyMainListener.onSetActionBarTitle(f31QuickTv.getText().toString());
			}
		});
		
		f31QuickTv.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				initQuickMenuCreate("quick4",f31QuickTv);
				return true;
			}
		});
	}
	
	public void initQuickMenuCreate(final String quickName, final TextView quickTv){
		InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
		AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
		alert.setTitle("�?i�?n quick menu:");
		// Set an EditText view to get user input
		final EditText input = new EditText(getActivity());
		input.setInputType(InputType.TYPE_CLASS_NUMBER);
		alert.setView(input);

		alert.setPositiveButton("Ok",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int whichButton) {
						String value = input.getText().toString();
						quickTv.setText("f"+value);
						editor.putString(quickName, value);
						editor.commit();
						InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
						imm.hideSoftInputFromWindow(input.getWindowToken(), 0);
						dialog.cancel();
					}
				});
		alert.show();
	}
	
	public void scrollDown(){
		quickPage.setVisibility(View.GONE);
	}
	
	public void scrollUp(){
		quickPage.setVisibility(View.VISIBLE);
	}
	
	public class GetDataTask extends AsyncTask<Void, Void, Void>{
		private String page;
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}
		
		@Override
		protected Void doInBackground(Void... params) {
			try {
				String isFromNewPost = getArguments().getString("fromNewPost");
				Document homePage = null;
				Element threadElement = null;
				if (isFromNewPost!=null) {
					homePage = Jsoup.connect("http://vozforums.com/search.php?do=getnew&"+"page="+currentpage).timeout(Constant.timeout).cookies(notifyMainListener.onGetCookie()).get();
					threadElement = homePage.select("#threadslist").first();
				}else {
					homePage = Jsoup.connect("http://vozforums.com/forumdisplay.php?f="+f.trim()+prefix+"&order=desc&page="+currentpage).timeout(Constant.timeout).cookies(notifyMainListener.onGetCookie()).get();
					threadElement = homePage.select("#threadbits_forum_"+f.trim()).first();
					if(f.equalsIgnoreCase("17")&&currentpage==1){
						Page2Item threadDetail = new Page2Item("�?iểm báo", "�?iểm báo phong cách voz", "", "", "http://vozforums.com/forumdisplay.php?f=33", false );
						Page2Item threadDetail2 = new Page2Item("From f17 with Love", "Tình yêu tình báo voz", "", "", "http://vozforums.com/forumdisplay.php?f=145", false );
						Page2Item threadDetail3 = new Page2Item("Superthreads", "Các thread kinh điển", "", "", "http://vozforums.com/forumdisplay.php?f=34", false );
						threadList.add(threadDetail);
						threadList.add(threadDetail2);
						threadList.add(threadDetail3);
					}else if (f.equalsIgnoreCase("11")&&currentpage==1){
						Page2Item threadDetail = new Page2Item("MMO -- Game Online", "", "", "", "http://vozforums.com/forumdisplay.php?f=12", false );
						Page2Item threadDetail2 = new Page2Item("Liên Minh Huy�?n Thoại", "", "", "", "http://vozforums.com/forumdisplay.php?f=178", false );
						threadList.add(threadDetail);
						threadList.add(threadDetail2);
					}else if (f.equalsIgnoreCase("6")&&currentpage==1){
						Page2Item threadDetail = new Page2Item("Modding", "", "", "", "http://vozforums.com/forumdisplay.php?f=151", false );
						threadList.add(threadDetail);
					}
				}
				//threadbits_forum_26
				Elements listthread = threadElement.select("tr");
			 
				for(int i = 0 ;i<listthread.size();i++){
					try{
					Element dataContainer = listthread.get(i).select("td:nth-child(2)").first();
 					Elements divElement = dataContainer.select("div > a");
					String reply = "Trả l�?i : "+listthread.get(i).select("td:nth-child(4)").first().text();
					String time = listthread.get(i).select("td:nth-child(3)").first().text();
					if (divElement.isEmpty()) {
						dataContainer = listthread.get(i).select("td:nth-child(3)").first();
						divElement = dataContainer.select("div > a");
						reply = "Trả l�?i : "+listthread.get(i).select("td:nth-child(5)").first().text();
						time = listthread.get(i).select("td:nth-child(4)").first().text();
					}
					try{
						page = dataContainer.select("span.smallfont > a").last().attr("href").split("page=")[1];
					}catch(Exception e){
						page = "1";
					}
 					int indexA = 0;
					if (divElement.size()>1) {
						indexA=1;
					}
					String link = divElement.get(indexA).attr("href");
					String title = divElement.get(indexA).text().trim();
					String user = dataContainer.select("div").get(1).text();
					try{
					if(divElement.size()==3){
						link =divElement.get(2).attr("href");
						title = title+" - "+ divElement.get(2).text().trim();
					}
					}catch(Exception e){
						page = "1";
					}
					
					boolean isSticky = false;
					if (searchSticky==true) {
						if (dataContainer.select("div").get(0).text().trim().contains("Sticky")) {
							isSticky = true ;
						}else{
							searchSticky = false;
						}
					}
					Page2Item threadDetail = new Page2Item(title, user, reply, time, link, isSticky);
					threadDetail.setPage(page);
					boolean checkExist = true;
					for (int j = threadList.size()/2 ;j<threadList.size() ; j++ ){
						if (threadList.get(j).getLink().equalsIgnoreCase(threadDetail.getLink())){
							checkExist = false;
						}
					}
					if (checkExist) {
						threadList.add(threadDetail);
					}
					}catch (Exception e){
						Log.e("error", "thread read catch exception");
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			threadListAdp.notifyDataSetChanged();
			mPullRefreshListView.onRefreshComplete();
			isLoading= false;

		}
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
	    // TODO Add your menu entries here
 		if (f.equalsIgnoreCase("68")||f.equalsIgnoreCase("72")||f.equalsIgnoreCase("76")||f.equalsIgnoreCase("80")){
			getActivity().getMenuInflater().inflate(R.menu.page2_menu_custom, menu);
		}else {
			getActivity().getMenuInflater().inflate(R.menu.page2_menu, menu);
		}
	    super.onCreateOptionsMenu(menu, inflater);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    case R.id.newpost :
	        // Not implemented here
	    	new GetPostInfoTask().execute();
	    	newThreadContainer.setVisibility(View.VISIBLE);
	    	threadContainer.setVisibility(View.GONE);
	    	titleText.requestFocus();
	    	InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
	        return true;
	    case R.id.hnselect :
	        // Not implemented here
	    	prefix="&prefixid=hntq";
	    	isLoading= true;
			searchSticky = true;
			currentpage=1;		
			threadList.clear();
			new GetDataTask().execute();
	        return true;
	    case R.id.hcmselect :
	        // Not implemented here
	    	prefix="&prefixid=hcmtq";
	    	isLoading= true;
			searchSticky = true;
			currentpage=1;		
			threadList.clear();
			new GetDataTask().execute();
	        return true;
	    default:
	        break;
	    }
	    return false;
	}
	private String userid;
	private String securitytoken;
	private String poststarttime;
	private String posthash;
	private ListView lv;
	
	public class GetPostInfoTask extends AsyncTask<String, Void, Void>{
		private ProgressDialog uploadDialog;
		private SharedPreferences sharedPreferences;

		@Override
		protected void onPreExecute() {
			uploadDialog = new ProgressDialog(getActivity());
			uploadDialog.setMessage("Waiting .....");
			uploadDialog.show();
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(String... params) {
			try {
				Map<String, String> cookie = null;
				Document doc = Jsoup.connect("http://vozforums.com/newthread.php?do=newthread&f="+f).timeout(Constant.timeout).cookies(notifyMainListener.onGetCookie()).get();
				poststarttime = doc.select("input[name=poststarttime]").val();
				posthash = doc.select("input[name=posthash]").val();
				userid = doc.select("input[name=loggedinuser]").val();
				securitytoken = doc.select("input[name=securitytoken]").val();
				f = doc.select("input[name=f]").val();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if (uploadDialog.isShowing()) {
				uploadDialog.dismiss();
			}
		}
	}
	
	public class NewPostTask extends AsyncTask<Void, Void, Void>{
		public boolean issuccess = true;
		@Override
		protected Void doInBackground(Void... params) {
			try{
				List<NameValuePair> data = new ArrayList<NameValuePair>();
				data.add(new BasicNameValuePair("message", commentText.getText().toString()));
				data.add(new BasicNameValuePair("subject", titleText.getText().toString()));
				data.add(new BasicNameValuePair("wysiwyg","0"));
				data.add(new BasicNameValuePair("s",""));
				data.add(new BasicNameValuePair("do","postthread"));
				data.add(new BasicNameValuePair("sbutton","Submit New Thread"));
				data.add(new BasicNameValuePair("parseurl","1"));
				data.add(new BasicNameValuePair("emailupdate","9999"));
				data.add(new BasicNameValuePair("loggedinuser",userid));
				data.add(new BasicNameValuePair("poststarttime",poststarttime));
				data.add(new BasicNameValuePair("posthash",posthash));
				data.add(new BasicNameValuePair("securitytoken",securitytoken));
				data.add(new BasicNameValuePair("f",f));
				HttpUrlJSONParser jsonParser = new HttpUrlJSONParser();
				SharedPreferences sharedPreferences = getActivity().getSharedPreferences("voz_data", getActivity().MODE_PRIVATE);
				jsonParser.setCookie(sharedPreferences.getString("cookie_data", ""));
				jsonParser.setAllowSaveCookie(false);
				jsonParser.postForm(" http://vozforums.com/newthread.php?do=postthread&f="+f, "post", data);
			}catch(Exception e){
				issuccess= false;
			}
			return null;
		}
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if (issuccess) {
				Toast.makeText(getActivity(), "thành công", Toast.LENGTH_SHORT).show();
			}else {
				Toast.makeText(getActivity(), "thất bại", Toast.LENGTH_SHORT).show();
			}
			newThreadContainer.setVisibility(View.GONE);
	    	threadContainer.setVisibility(View.VISIBLE);
	    	InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(commentText.getWindowToken(),0); 
			isLoading= true;
			searchSticky = true;
			currentpage=1;		
			threadList.clear();
			threadListAdp.notifyDataSetChanged();
			new GetDataTask().execute();
		}
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == PICK_IMAGE && data != null && data.getData() != null) {
			Uri _uri = data.getData();
			String imageFilePath = "";
			try {
				// User had pick an image.
				Cursor cursor = getActivity().getContentResolver().query(_uri, null, null, null, null);
				   cursor.moveToFirst();
				   String document_id = cursor.getString(0);
				   document_id = document_id.substring(document_id.lastIndexOf(":")+1);
				   cursor.close();

				   cursor = getActivity().getContentResolver().query( 
				   android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
				   null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
				   cursor.moveToFirst();
				   String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
				   cursor.close();
				   imageFilePath = path;
				   
				cursor.close();
				// Call service to upload image
			} catch (Exception e) {
				imageFilePath = _uri.getPath();
			}
			new UploadImageTask().execute(imageFilePath);
		}else if (requestCode == VIEWPAGEROPEN){
			if(resultCode== 11){
				notifyMainListener.onMenuClick(data.getIntExtra("menu_back", 0));
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	// Upload Imager task
	public class UploadImageTask extends AsyncTask<String, Void, Void> {

		private String FILENAME = "dohai.png";
		String lineEnd = "\r\n";
		String twoHyphens = "--";
		String boundary = "*****";
		HttpURLConnection conn = null;
		int bytesRead, bytesAvailable, bufferSize;
		byte[] buffer;
        int maxBufferSize =5 * 1024 * 1024; // 5mb
        private ProgressDialog uploadDialog;
		private String resutlContent;
        
        @Override
		protected void onPreExecute() {
			uploadDialog = new ProgressDialog(getActivity());
			uploadDialog.setMessage("Upload .....");
			uploadDialog.show();
        	super.onPreExecute();
        }
    
		@Override
		protected Void doInBackground(String... params) {
			try {
				URL url = new URL("http://pik.vn/");
				// Open a HTTP connection to the URL
				conn = (HttpURLConnection) url.openConnection();
				conn.setReadTimeout(10000);
				conn.setConnectTimeout(15000);
				conn.setDoInput(true); // Allow Inputs
				conn.setDoOutput(true); // Allow Outputs
				conn.setUseCaches(false); // Don't use a Cached Copy
				conn.setRequestMethod("POST");
				conn.setRequestProperty("Connection", "Keep-Alive");
				conn.setRequestProperty("ENCTYPE", "multipart/form-data");
				conn.setRequestProperty("Cookie", "csrftoken=EEPCYB93WZmdQiDMDbji3KSjdcF6BxTY; _ga=GA1.2.1374335103.1416655565; _gat=1");
				conn.setRequestProperty("Content-Type","multipart/form-data;boundary=" + boundary);
//					conn.setRequestProperty("uploaded_file", FILENAME);
				

				DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
                dos.writeBytes(twoHyphens + boundary + lineEnd); 
                dos.writeBytes("Content-Disposition: form-data; name=\"csrfmiddlewaretoken\"" + lineEnd);
                dos.writeBytes("Content-Type: text/plain;charset=utf-8" + lineEnd);
                dos.writeBytes(lineEnd);
                dos.writeBytes("EEPCYB93WZmdQiDMDbji3KSjdcF6BxTY");
                dos.writeBytes(lineEnd);
                // Separate post param
				dos.writeBytes("--" + boundary+lineEnd);
            	
                dos.writeBytes("Content-Disposition: form-data; name=\"image\";filename=\""+ FILENAME + "\"" + lineEnd);
                dos.writeBytes("Content-Type:"+URLConnection.guessContentTypeFromName("icon.png") + lineEnd);
                dos.writeBytes("Content-Transfer-Encoding: binary"+lineEnd);
                dos.writeBytes(lineEnd);
                
                
                File file = new File(params[0]);
        		FileInputStream fileInputStream = new FileInputStream(file);
				bytesAvailable = fileInputStream.available();
				bufferSize = Math.min(bytesAvailable, maxBufferSize);
				buffer = new byte[bufferSize];
				// read file and write it into form...
				bytesRead = fileInputStream.read(buffer, 0, bufferSize);
				dos.write(buffer, 0, bufferSize);
				bytesAvailable = fileInputStream.available();
				bufferSize = Math.min(bytesAvailable, maxBufferSize);
				bytesRead = fileInputStream.read(buffer, 0, bufferSize);
				while (bytesRead > 0) {
					dos.write(buffer, 0, bufferSize);
				}
				// send multipart form data necesssary after file data...
				dos.writeBytes(lineEnd);
				dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
				fileInputStream.close();
				// Responses from the server (code and message)
				int serverResponseCode = conn.getResponseCode();
				resutlContent = "";
				if (serverResponseCode==200) {
					InputStream is = conn.getInputStream();
					BufferedReader reader = new BufferedReader(new InputStreamReader(is, "utf-8"), 8);
					StringBuilder sb = new StringBuilder();
					String line = null;
					while ((line = reader.readLine()) != null) {
						sb.append(line + "\n");
					}
					is.close();
					resutlContent = sb.toString();
				}
                
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if (uploadDialog.isShowing()) {
				Document doc = Jsoup.parse(resutlContent);
				try {
					String imgUrl = doc.select("img#img-result").first().attr("src");
					if (!imgUrl.equalsIgnoreCase("")) {
						commentText.requestFocus();
						commentText.setText(commentText.getText().toString() + "\n" +"[IMG]"+imgUrl+"[/IMG]"+"\n" );
						commentText.setSelection(commentText.getText().length());
					}
				} catch (Exception e) {

				}
				uploadDialog.dismiss();
			}
		}
	}
}
