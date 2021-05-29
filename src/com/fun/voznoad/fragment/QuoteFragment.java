package com.fun.voznoad.fragment;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

import com.fun.voznoad.R;
import com.fun.voznoad.ViewPagerActivity;
import com.fun.voznoad.adapter.Page2ListAdp;
import com.fun.voznoad.callback.OnNotifyMainListener;
import com.fun.voznoad.common.Constant;
import com.fun.voznoad.model.Page2Item;
import com.fun.voznoad.servicecontroller.HttpUrlJSONParser;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;

public class QuoteFragment extends Fragment{
	private View view;
	private View mLoadMoreFooter;
	private boolean isLoading;
	private int currentpage;
	private String url;
	private ArrayList<Page2Item> threadList;
	private OnNotifyMainListener notifyMainListener;
	private PullToRefreshListView mPullRefreshListView;
	private Page2ListAdp threadListAdp;
	private SharedPreferences sharedPreferences;
	private String totalPage;
	
	public void setNotifyMain (OnNotifyMainListener x){
		notifyMainListener = x;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_page2, container, false);
		mLoadMoreFooter = inflater.inflate(R.layout.footer_loadmore, null);
		(view.findViewById(R.id.quickPage)).setVisibility(View.GONE);
		return view;
	}
	
	@Override
	public void onActivityCreated( Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		url = "http://vozforums.com/subscription.php?do=viewsubscription&daysprune=-1&folderid=all";
		initUI();
		initCONTROL();
		isLoading = true;
		currentpage=1;
		new GetDataTask().execute();
		sharedPreferences = getActivity().getSharedPreferences("voz_data",getActivity().MODE_PRIVATE);
		if (sharedPreferences.getString("theme", "null").equals("black")) {
			initTheme();
		}
	}
	
	public void initTheme(){
		mLoadMoreFooter.setBackgroundColor(Color.parseColor("#191919"));
	}

	private void initCONTROL() {
		mPullRefreshListView.setOnRefreshListener(new OnRefreshListener<ListView>() {
			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				String label = DateUtils.formatDateTime(getActivity().getApplicationContext(), System.currentTimeMillis(),
						DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
				refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
				isLoading= true;
				currentpage=1;		
				threadList.clear();
				locationRedirect=null;
				new GetDataTask().execute();
			}
		});
		
		mPullRefreshListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int position,long arg3) {
					 Intent intent = new Intent(getActivity(), ViewPagerActivity.class);
					 intent.putExtra("url",  threadList.get(position-1).getLink());
					 intent.putExtra("totalpage", threadList.get(position-1).getPage());
					 intent.putExtra("title", threadList.get(position-1).getTitle());
					 intent.putExtra("isquote","isquote");
					 String link = threadList.get(position-1).getLink();
					 intent.putExtra("quoteId", link.substring(link.indexOf("#post"), link.length()).replace("#post", ""));
					 intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
					 startActivity(intent);
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
					new GetDataTask().execute();
				}
			}
		});
	}

	private void initUI() {
		threadList = new ArrayList<Page2Item>();
		mPullRefreshListView = (PullToRefreshListView) view.findViewById(R.id.pull_refresh_list);
		threadListAdp = new Page2ListAdp(threadList, getActivity());
		mPullRefreshListView.setAdapter(threadListAdp);
		lv = mPullRefreshListView.getRefreshableView();
		lv.addFooterView(mLoadMoreFooter);
		notifyMainListener.onSetActionBarTitle("List Quote");
	}
	private String locationRedirect=null;
	private ListView lv;
	public class GetDataTask extends AsyncTask<Void, Void, Void>{
		 
 
		//http://vozforums.com/search.php?do=process
		@Override
		protected Void doInBackground(Void... params) {
			
			try {
				HttpUrlJSONParser jsonParser = new HttpUrlJSONParser();
				String html = "";
				if (locationRedirect == null){
					SharedPreferences sharedPreferences = getActivity().getSharedPreferences("voz_data", getActivity().MODE_PRIVATE);
					String user_data = sharedPreferences.getString("user_data", "");
					JSONObject jsonObj = new JSONObject(user_data);
					String name = jsonObj.getJSONObject("userinfo").getString("username");
					Document doc = Jsoup.connect("http://vozforums.com/search.php?do=process").timeout(Constant.timeout).cookies(notifyMainListener.onGetCookie()).get();
					String token = doc.select("input[name=securitytoken]").first().val();
					List<NameValuePair> data = new ArrayList<NameValuePair>();
					data.add(new BasicNameValuePair("securitytoken",token));
					data.add(new BasicNameValuePair("do","process"));
					data.add(new BasicNameValuePair("s",""));
					data.add(new BasicNameValuePair("searchthreadid","0"));
					data.add(new BasicNameValuePair("query",name));
					data.add(new BasicNameValuePair("titleonly","0"));
					data.add(new BasicNameValuePair("searchuser",""));
					data.add(new BasicNameValuePair("starteronly","0"));
					data.add(new BasicNameValuePair("exactname","1"));
					data.add(new BasicNameValuePair("prefixchoice[]",""));
					data.add(new BasicNameValuePair("replyless","0"));
					data.add(new BasicNameValuePair("replylimit","0"));
					data.add(new BasicNameValuePair("searchdate","0"));
					data.add(new BasicNameValuePair("beforeafter","after"));
					data.add(new BasicNameValuePair("sortby","lastpost"));
					data.add(new BasicNameValuePair("order","descending"));
					data.add(new BasicNameValuePair("forumchoice[]","0"));
					data.add(new BasicNameValuePair("childforums","1"));
					data.add(new BasicNameValuePair("dosearch","Search Now"));
					data.add(new BasicNameValuePair("saveprefs","1"));
					data.add(new BasicNameValuePair("showposts","1"));
					jsonParser.setCookie(sharedPreferences.getString("cookie_data", ""));
					jsonParser.setAllowSaveCookie(false);
					html = 	jsonParser.postForm("http://vozforums.com/search.php?do=process", "post", data);
					locationRedirect = jsonParser.getQuoteURL();
				}else { 
					Document doc = Jsoup.connect("http://vozforums.com/search.php?do=process").timeout(Constant.timeout).cookies(notifyMainListener.onGetCookie()).get();
					String token = doc.select("input[name=securitytoken]").first().val();
					List<NameValuePair> data = new ArrayList<NameValuePair>();
					data.add(new BasicNameValuePair("securitytoken",token));
					jsonParser.setCookie(sharedPreferences.getString("cookie_data", ""));
					jsonParser.setAllowSaveCookie(false);
					html = 	jsonParser.postForm(locationRedirect + "&pp=20&page="+ currentpage, "get", data);
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
				Element containerElement = doc.select("#inlinemodform").first();
				Elements tableElements = containerElement.select("table.tborder");
				
				for (int i = 0 ; i<tableElements.size();i++){
					try{
						Elements infos = tableElements.get(i).select("tr").get(1).select(".smallfont");
						String replys =   infos.get(0).select("strong").text();
						String views = infos.get(1).select("strong").text();
						String postBy = infos.get(2).select("a").text();
						String link =  infos.get(3).select("a").first().attr("href");
						String title = infos.get(3).select("a").first().text();
						Node linkNode = infos.get(3).select("a").first();
						infos.get(3).replaceWith(linkNode);
						String quote = infos.get(3).html();
						long page = Math.round(Double.parseDouble(replys.trim())/10.0 +0.5) ;
						replys = "Trả l�?i: " + replys;
						Page2Item threadDetail = new Page2Item(title, quote, replys, postBy, link, false);
						threadDetail.setPage(page+"");
						threadList.add(threadDetail);
						Log.d("dohai", link);
					}catch(Exception e){
						
					}
				}
				return null;
				
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
