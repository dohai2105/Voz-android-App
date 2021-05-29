package com.fun.voznoad;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

import com.fun.voznoad.R;
import com.fun.voznoad.adapter.Page2ListAdp;
import com.fun.voznoad.common.Constant;
import com.fun.voznoad.fragment.QuoteFragment.GetDataTask;
import com.fun.voznoad.model.Page2Item;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;

public class SearchUserActivity extends FragmentActivity {
	private String locationRedirect=null;
	private ArrayList<Page2Item> threadList;
	private PullToRefreshListView mPullRefreshListView;
	private Page2ListAdp threadListAdp;
	private ListView lv;
	private String userId;
	private String url;
	private int currentpage;
	private SharedPreferences sharedPreferences;
	private Editor editor;
	private boolean isLoading;
	private View mLoadMoreFooter;
	private String totalPage;
	
	@Override
	protected void onCreate(Bundle bundle) {
		sharedPreferences = getSharedPreferences("voz_data", MODE_PRIVATE);
		editor = sharedPreferences.edit();
		if (sharedPreferences.getString("theme", "null").equals("null")) {
			editor.putString("theme", "normal");
			editor.commit();
		}
		if (sharedPreferences.getString("theme", "null").equals("black")) {
			setTheme(R.style.AppThemeDark);
		}
		super.onCreate(bundle);
		setContentView(R.layout.activity_searchuser);
		initUI();
		if (sharedPreferences.getString("theme", "null").equals("black")) {
			initTheme();
		}
		initCONTROL();
	}
	
	public void initTheme(){
		mLoadMoreFooter.setBackgroundColor(Color.parseColor("#191919"));
	}

	private void initUI() {
		userId = getIntent().getStringExtra("userid");
		if(getIntent().getStringExtra("searchtype").equalsIgnoreCase("post")){
			url = "http://vozforums.com/search.php?do=finduser&u="+userId;
			new GetDataTask().execute();
		}else {
			url = "http://vozforums.com/search.php?do=finduser&u="+userId+"&starteronly=1";
			new GetThreadDataTask().execute();
		}
		isLoading = true;
		currentpage=1;
		threadList = new ArrayList<Page2Item>();
		mPullRefreshListView = (PullToRefreshListView) findViewById(R.id.pull_refresh_list);
		threadListAdp = new Page2ListAdp(threadList, this);
		mPullRefreshListView.setAdapter(threadListAdp);
		lv = mPullRefreshListView.getRefreshableView();
		mLoadMoreFooter = getLayoutInflater().inflate(R.layout.footer_loadmore, null);
		lv.addFooterView(mLoadMoreFooter);
	}
	
	private void initCONTROL() {
		mPullRefreshListView.setOnRefreshListener(new OnRefreshListener<ListView>() {
			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				String label = DateUtils.formatDateTime(SearchUserActivity.this.getApplicationContext(), System.currentTimeMillis(),
						DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
				refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
				isLoading= true;
				currentpage=1;		
				threadList.clear();
				locationRedirect=null;
				if(getIntent().getStringExtra("searchtype").equalsIgnoreCase("post")){
					new GetDataTask().execute();
				}else {
					new GetThreadDataTask().execute();
				}
			}
		});
		
		mPullRefreshListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int position,long arg3) {
				if(getIntent().getStringExtra("searchtype").equalsIgnoreCase("post")){
					 Intent intent = new Intent(SearchUserActivity.this, ViewPagerActivity.class);
					 intent.putExtra("url",  threadList.get(position-1).getLink());
					 intent.putExtra("totalpage", threadList.get(position-1).getPage());
					 intent.putExtra("title", threadList.get(position-1).getTitle());
					 intent.putExtra("fromsearch", "fromsearch");
					 intent.putExtra("isquote","isquote");
					 String link = threadList.get(position-1).getLink();
					 intent.putExtra("quoteId", link.substring(link.indexOf("#post"), link.length()).replace("#post", ""));
					 intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
					 startActivity(intent);
				}else {	 
					 Intent intent = new Intent(SearchUserActivity.this, ViewPagerActivity.class);
					 intent.putExtra("url",  threadList.get(position-1).getLink());
					 intent.putExtra("totalpage", threadList.get(position-1).getPage());
					 intent.putExtra("title", threadList.get(position-1).getTitle());
					 intent.putExtra("fromsearch", "fromsearch");
					 intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
					 startActivity(intent);
				}
			}
		});
		
		mPullRefreshListView.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState)  {
			}
			
			@Override
			public void onScroll (AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				if (totalPage!= null) {
					if (Integer.parseInt(totalPage)<=currentpage) {
						lv.removeFooterView(mLoadMoreFooter);
						return;
					}
				}
				if (isLoading) {
					return;
				}
				int total = firstVisibleItem + visibleItemCount;
				if (totalItemCount == total && !isLoading) {
					isLoading=true;
					currentpage++;
					if(getIntent().getStringExtra("searchtype").equalsIgnoreCase("post")){
						new GetDataTask().execute();
					}else {
						new GetThreadDataTask().execute();
					}
				}
			}
		});
	}
	
	public Map<String, String> onGetCookie() {
		try{
			return Constant.getCookies(sharedPreferences.getString("cookie_data", ""));
		}catch(Exception e){
			return new HashMap<String, String>();
		}
	}
	
	public class GetThreadDataTask extends AsyncTask<Void, Void, Void>{

		private String page;

		@Override
		protected Void doInBackground(Void... params) {
			try{
				String html = "";
				if (locationRedirect==null) {
					Response response = Jsoup.connect(url).timeout(Constant.timeout).cookies(onGetCookie()).data("securitytoken", getIntent().getStringExtra("token")).method(org.jsoup.Connection.Method.GET).execute();
					html = response.body();
					locationRedirect = response.url().toString();
				}else {
					html =  Jsoup.connect(locationRedirect + "&pp=20&page="+ currentpage).timeout(Constant.timeout).method(org.jsoup.Connection.Method.GET).cookies(onGetCookie()).data("securitytoken", getIntent().getStringExtra("token")).get().html();
					Log.d("URL_________", locationRedirect + "&pp=20&page="+ currentpage);
				}
				Document doc = Jsoup.parse(html);
				if (totalPage==null) {
					try{
						String []panelPage = doc.select(".pagenav").first().select("td.vbmenu_control").get(0).text().split(" ");
						totalPage = panelPage[panelPage.length-1];
					}catch (Exception e) {
						// TODO: handle exception
						totalPage = "1";
					}
				}
				Element threadElement = doc.select("#threadslist").first();
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
					String pages = listthread.get(i).select("td:nth-child(4)").first().text();
					String page = Math.round(Double.parseDouble(pages.trim())/10.0 +0.5)+"" ;
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
				 
					Page2Item threadDetail = new Page2Item(title, user, reply, time, link, isSticky);
					threadDetail.setPage(page);
						threadList.add(threadDetail);
					}catch (Exception e){
						Log.e("error", "thread read catch exception");
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			threadListAdp.notifyDataSetChanged();
			mPullRefreshListView.onRefreshComplete();
			isLoading= false;
			//mLoadMoreFooter.setVisibility(View.GONE);
		}
	}
	
	
	public class GetDataTask extends AsyncTask<Void, Void, Void>{

		@Override
		protected Void doInBackground(Void... params) {
			try {
				String html = "";
				if (locationRedirect==null) {
					Response response = Jsoup.connect(url).timeout(Constant.timeout).cookies(onGetCookie()).data("securitytoken", getIntent().getStringExtra("token")).method(org.jsoup.Connection.Method.GET).execute();
					html = response.body();
					locationRedirect = response.url().toString();
				}else {
					html =  Jsoup.connect(locationRedirect + "&pp=20&page="+ currentpage).timeout(Constant.timeout).method(org.jsoup.Connection.Method.GET).cookies(onGetCookie()).data("securitytoken", getIntent().getStringExtra("token")).get().html();
				}
				Document doc = Jsoup.parse(html);
				Element containerElement = doc.select("#inlinemodform").first();
				Elements tableElements = containerElement.select("table.tborder");
				if (totalPage==null) {
					try{
						String []panelPage = doc.select(".pagenav").first().select("td.vbmenu_control").get(0).text().split(" ");
						totalPage = panelPage[panelPage.length-1];
					}catch (Exception e) {
						// TODO: handle exception
					}
				}
				for (int i = 0; i < tableElements.size(); i++) {
					try {
						Elements infos = tableElements.get(i).select("tr").get(1).select(".smallfont");
						String replys = infos.get(0).select("strong").text();
						String views = infos.get(1).select("strong").text();
						String postBy = infos.get(2).select("a").text();
						String link = infos.get(3).select("a").first().attr("href");
						String title = infos.get(3).select("a").first().text();
						Node linkNode = infos.get(3).select("a").first();
						infos.get(3).replaceWith(linkNode);
						String quote = infos.get(3).html();
						long page = Math.round(Double.parseDouble(replys.trim()) / 10.0 + 0.5);
						replys = "Trả l�?i : " + replys;
						Page2Item threadDetail = new Page2Item(title, quote,replys, postBy, link, false);
						threadDetail.setPage(page + "");
						
						boolean checkExist = true;
						for (int j = threadList.size()/2 ;j<threadList.size() ; j++ ){
							if (threadList.get(j).getLink().equalsIgnoreCase(threadDetail.getLink())){
								checkExist = false;
							}
						}
						if (checkExist) {
							threadList.add(threadDetail);
						}
						Log.d("dohai", link);
					} catch (Exception e) {

					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			return null;
		}
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			threadListAdp.notifyDataSetChanged();
			mPullRefreshListView.onRefreshComplete();
			isLoading= false;
			//mLoadMoreFooter.setVisibility(View.GONE);
		}
		
	}

}
